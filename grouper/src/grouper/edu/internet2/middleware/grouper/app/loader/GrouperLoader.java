/**
 * Copyright 2014 Internet2
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * @author mchyzer
 * $Id: GrouperLoader.java,v 1.15 2009-11-02 03:50:50 mchyzer Exp $
 */
package edu.internet2.middleware.grouper.app.loader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import edu.internet2.middleware.grouper.ext.org.apache.ddlutils.PlatformFactory;
import org.hibernate.type.StringType;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

import edu.internet2.middleware.grouper.Group;
import edu.internet2.middleware.grouper.GroupFinder;
import edu.internet2.middleware.grouper.GroupTypeFinder;
import edu.internet2.middleware.grouper.GrouperSession;
import edu.internet2.middleware.grouper.app.loader.db.Hib3GrouperLoaderLog;
import edu.internet2.middleware.grouper.app.loader.ldap.LoaderLdapUtils;
import edu.internet2.middleware.grouper.attr.AttributeDef;
import edu.internet2.middleware.grouper.attr.AttributeDefName;
import edu.internet2.middleware.grouper.attr.assign.AttributeAssign;
import edu.internet2.middleware.grouper.attr.finder.AttributeDefFinder;
import edu.internet2.middleware.grouper.attr.finder.AttributeDefNameFinder;
import edu.internet2.middleware.grouper.audit.GrouperEngineBuiltin;
import edu.internet2.middleware.grouper.cfg.GrouperConfig;
import edu.internet2.middleware.grouper.changeLog.ChangeLogConsumer;
import edu.internet2.middleware.grouper.changeLog.ChangeLogConsumerBase;
import edu.internet2.middleware.grouper.changeLog.ChangeLogEntry;
import edu.internet2.middleware.grouper.client.ClientConfig;
import edu.internet2.middleware.grouper.client.ClientConfig.ClientGroupConfigBean;
import edu.internet2.middleware.grouper.ddl.GrouperDdlUtils;
import edu.internet2.middleware.grouper.hibernate.GrouperContext;
import edu.internet2.middleware.grouper.hibernate.GrouperTransaction;
import edu.internet2.middleware.grouper.hibernate.GrouperTransactionHandler;
import edu.internet2.middleware.grouper.hibernate.GrouperTransactionType;
import edu.internet2.middleware.grouper.hibernate.HibUtils;
import edu.internet2.middleware.grouper.hibernate.HibernateSession;
import edu.internet2.middleware.grouper.instrumentation.InstrumentationThread;
import edu.internet2.middleware.grouper.internal.dao.GrouperDAOException;
import edu.internet2.middleware.grouper.internal.dao.QueryOptions;
import edu.internet2.middleware.grouper.messaging.MessagingListenerBase;
import edu.internet2.middleware.grouper.misc.GrouperCheckConfig;
import edu.internet2.middleware.grouper.misc.GrouperDAOFactory;
import edu.internet2.middleware.grouper.misc.GrouperStartup;
import edu.internet2.middleware.grouper.util.GrouperUtil;
import edu.internet2.middleware.grouperClient.jdbc.GcDbAccess;



/**
 * main class to start the grouper loader
 */
public class GrouperLoader {

  /**
   * call this when exiting grouper if not the daemon which should stay running
   */
  public static void shutdownIfStarted() {
    //stop quartz
    try {
      for (Scheduler scheduler : GrouperUtil.nonNull(GrouperLoader.schedulerFactory().getAllSchedulers())) {
        scheduler.shutdown(false);
      }
    } catch (Exception e) {
      throw new RuntimeException("error", e);
    }

  }
  
  /**
   * logger 
   */
  private static final Log LOG = GrouperUtil.getLog(GrouperLoader.class);

  private static ThreadLocal<Boolean> isRunningJobOnceLocally = new ThreadLocal<Boolean>();

  /**
   * @param args
   */
  public static void main(String[] args) {
    
    //set this and leave it...
    GrouperContext.createNewDefaultContext(GrouperEngineBuiltin.LOADER, false, true);

    //printAllSupportDdlUtilsPlatforms();
    GrouperStartup.startup();
    GrouperStartup.waitForGrouperStartup();

    //make sure properties file is there
    GrouperCheckConfig.checkResource("grouper-loader.properties");
    
    //make sure properties are there
//    GrouperCheckConfig.checkConfigProperties("grouper-loader.properties", 
//        "grouper-loader.example.properties");
    
    GrouperCheckConfig.checkGrouperLoaderConfigDbs();
    GrouperCheckConfig.checkGrouperLoaderConsumers();
    GrouperCheckConfig.checkGrouperLoaderOtherJobs();
    
    scheduleJobs();
    
    InstrumentationThread.startThread(GrouperContext.retrieveDefaultContext().getGrouperEngine(), null);
    
    GrouperDaemonSchedulerCheck.startDaemonSchedulerCheckThreadIfNeeded();
    
    GrouperDaemonUtils.startDaemonPausedJobsCheckThread();
    
    // delay starting the scheduler until the end to make sure things that need to be unscheduled are taken care of first?
    try {
      schedulerFactory.getScheduler().start();
    } catch (SchedulerException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * 
   * @return the number of changes made
   */
  public static int scheduleJobs() {
    
    int changesMade = 0;
    
    //this will find all schedulable groups, and schedule them
    changesMade += GrouperLoaderType.scheduleLoads();
    
    changesMade += GrouperLoaderType.scheduleAttributeLoads();
    
    changesMade += GrouperLoaderType.scheduleLdapLoads();

    changesMade += scheduleChangeLogJobs();
    changesMade += scheduleMessagingListeners();
    
    changesMade += scheduleOtherJobs();
    
    //this will schedule ESB listener jobs if enabled
    changesMade += scheduleEsbListenerJobs();
    
    if (schedulePspFullSyncJob()) {
      changesMade++;
    }
    
    return changesMade;
  }

  /**
   * print out all ddlutils platforms
   */
  public static void printAllSupportDdlUtilsPlatforms() {
    String[] platforms = PlatformFactory.getSupportedPlatforms();
    Arrays.sort(platforms);
    for (String platform : platforms) {
      System.out.print(platform + ", ");
    }
  }
  
  /**
   * group attribute name of type of the loader, must match one of the enums in GrouperLoaderType.
   * If there is a query, and it has "group_name" before "from", then defaults to SQL_GROUP_LIST
   * else defaults to SQL_SIMPLE
   */
  public static final String GROUPER_LOADER_TYPE = "grouperLoaderType";
  
  /**
   * grouper loader display name sync type. BASE_FOLDER_NAME or LEVELS
   */
  public static final String GROUPER_LOADER_DISPLAY_NAME_SYNC_TYPE = "grouperLoaderDisplayNameSyncType";
  
  /**
   * folder name after which display names need to be synced between source and grouper
   */
  public static final String GROUPER_LOADER_DISPLAY_NAME_SYNC_BASE_FOLDER_NAME = "grouperLoaderDisplayNameSyncBaseFolderName";
  
  /**
   * levels starting from the group after which display names need to be synced between source and grouper
   */
  public static final String GROUPER_LOADER_DISPLAY_NAME_SYNC_LEVELS = "grouperLoaderDisplayNameSyncLevels";

  /**
   * groups to and with to restrict members (e.g. "and" with activeEmployees)
   */
  public static final String GROUPER_LOADER_AND_GROUPS = "grouperLoaderAndGroups";

  /**
   * If you want the group (if not used from anywhere) or members deleted when 
   * no longer in loader sql results, list the sql like name, e.g. stem1:stem2:%:%org
   */
  public static final String GROUPER_LOADER_GROUPS_LIKE = "grouperLoaderGroupsLike";

  /**
   * optional group information for a group list query: e.g. to specify the display name of the
   * group/stem when it is created
   */
  public static final String GROUPER_LOADER_GROUP_QUERY = "grouperLoaderGroupQuery";

  /**
   * types to add to loaded groups
   */
  public static final String GROUPER_LOADER_GROUP_TYPES = "grouperLoaderGroupTypes";

  /**
   * group attribute name of type of schedule, must match one of the enums in GrouperLoaderScheduleType.
   * defaults to START_TO_START_INTERVAL if grouperLoaderQuartzCron is blank, else defaults to
   * CRON
   */
  public static final String GROUPER_LOADER_SCHEDULE_TYPE = "grouperLoaderScheduleType";

  /**
   * group attribute name of query, must have the required columns for the grouperLoaderType
   */
  public static final String GROUPER_LOADER_QUERY = "grouperLoaderQuery";

  /**
   * group attribute name of quartz cron-like string to describe when the job should run
   */
  public static final String GROUPER_LOADER_QUARTZ_CRON = "grouperLoaderQuartzCron";

  /**
   * group attribute name of the interval in seconds for a schedule type like START_TO_START_INTERVAL.
   * defaults to 86400 (1 day)
   */
  public static final String GROUPER_LOADER_INTERVAL_SECONDS = "grouperLoaderIntervalSeconds";

  /**
   * group attribute name of priority of job, optional, if not there, will be 5.  More is better.
   * if the threadpool is full, then this priority will help the schedule pick which job should go next
   */
  public static final String GROUPER_LOADER_PRIORITY = "grouperLoaderPriority";

  
  /**
   * T or F if using failsafe.  If blank use the global defaults
   */
  public static final String GROUPER_LOADER_FAILSAFE_USE = "grouperLoaderFailsafeUse";

  /**
   * integer from 0 to 100 which specifies the maximum percent of a group which can be removed in a loader run.
   * If not specified will use the global default grouper-loader.properties config setting:
   * loader.failsafe.maxPercentRemove = 30
   */
  public static final String GROUPER_LOADER_MAX_GROUP_PERCENT_REMOVE = "grouperLoaderMaxGroupPercentRemove";

  /**
   * If the group list meets the criteria above and the percentage of memberships that are managed by
   * the loader (i.e. match the groupLikeString) that currently have members in Grouper but 
   * wouldn't after the job runs is greater than this percentage, then don't remove members,
   * log it as an error and fail the job.  An admin would need to approve the failsafe or change this param in the config,
   * and run the job manually, then change this config back.
   * {valueType: "integer", required: true}
   * loader.failsafe.groupList.managedGroups.maxPercentMembershipsRemove = 30
   */
  public static final String GROUPER_LOADER_MAX_OVERALL_PERCENT_GROUPS_REMOVE = "grouperLoaderMaxOverallPercentGroupsRemove";
  
  /**
   * integer from 0 to 100 which specifies the maximum percent of all loaded groups in the job
   * which can be removed in a loader run.
   * If not specified will use the global default grouper-loader.properties config setting:
   * loader.failsafe.groupList.managedGroups.maxPercentGroupsRemove = 30
   */
  public static final String GROUPER_LOADER_MAX_OVERALL_PERCENT_MEMBERSHIPS_REMOVE = "grouperLoaderMaxOverallPercentMembershipsRemove";
  
  /**
   * minimum number of members for the group to be tracked by failsafe
   * defaults to grouper-loader.base.properties: loader.failsafe.minGroupSize
   */
  public static final String GROUPER_LOADER_MIN_GROUP_SIZE = "grouperLoaderMinGroupSize";
  
  /**
   * The minimum number of managed groups for this loader job for the list of groups job to be applicable
   */
  public static final String GROUPER_LOADER_MIN_MANAGED_GROUPS = "grouperLoaderMinManagedGroups";
  
  /**
   * The minimum group number of members for this group, a failsafe alert will trigger if the group is smaller than this amount
   */
  public static final String GROUPER_LOADER_MIN_GROUP_NUMBER_OF_MEMBERS = "grouperLoaderMinGroupNumberOfMembers";
  
  /**
   * The minimum overall number of members for this job across all managed groups, 
   * a failsafe alert will trigger if the job's overall membership count is smaller than this amount
   */
  public static final String GROUPER_LOADER_MIN_OVERALL_NUMBER_OF_MEMBERS = "grouperLoaderMinOverallNumberOfMembers";
  
  /**
   * If an email should be sent out when a failsafe alert happens.
   * The email will be sent to the list or group configured in grouper-loader.properties:
   * loader.failsafe.sendEmailToAddresses, or loader.failsafe.sendEmailToGroup 
   */
  public static final String GROUPER_LOADER_FAILSAFE_SEND_EMAIL = "grouperLoaderFailsafeSendEmail";

  /**
   * group attribute name of the db connection where this query comes from.
   * if the name is "grouper", then it will be the group db name.  defaults to "grouper" for sql type
   * loaders
   */
  public static final String GROUPER_LOADER_DB_NAME = "grouperLoaderDbName";
  
  /**
   * Type of loader, e.g. ATTR_SQL_SIMPLE
   */
  public static final String ATTRIBUTE_LOADER_TYPE = "attributeLoaderType";
  
  /**
   * DB name in grouper-loader.properties or default grouper db if blank
   */
  public static final String ATTRIBUTE_LOADER_DB_NAME = "attributeLoaderDbName";
  
  /**
   * Type of schedule.  Defaults to CRON if a cron schedule is entered, or START_TO_START_INTERVAL if an interval is entered
   */
  public static final String ATTRIBUTE_LOADER_SCHEDULE_TYPE = "attributeLoaderScheduleType";
  
  /**
   * If a CRON schedule type, this is the cron setting string from the quartz product to run a job daily, hourly, weekly, etc.  e.g. daily at 7am: 0 0 7 * * ?
   */
  public static final String ATTRIBUTE_LOADER_QUARTZ_CRON = "attributeLoaderQuartzCron";

  /**
   * If a START_TO_START_INTERVAL schedule type, this is the number of seconds between runs
   */
  public static final String ATTRIBUTE_LOADER_INTERVAL_SECONDS = "attributeLoaderIntervalSeconds";
  
  /**
   * Quartz has a fixed threadpool (max configured in the grouper-loader.properties), and when the max is reached, then jobs are prioritized by this integer.  The higher the better, and the default if not set is 5.
   */
  public static final String ATTRIBUTE_LOADER_PRIORITY = "attributeLoaderPriority";

  /**
   * If empty, then orphans will be left alone (for attributeDefName and attributeDefNameSets).  If %, then all orphans deleted.  If a SQL like string, then only ones in that like string not in loader will be deleted
   */
  public static final String ATTRIBUTE_LOADER_ATTRS_LIKE = "attributeLoaderAttrsLike";
  
  /**
   * SQL query with at least some of the following columns: attr_name, attr_display_name, attr_description
   */
  public static final String ATTRIBUTE_LOADER_ATTR_QUERY = "attributeLoaderAttrQuery";
  
  /**
   * SQL query with at least the following columns: if_has_attr_name, then_has_attr_name
   */
  public static final String ATTRIBUTE_LOADER_ATTR_SET_QUERY = "attributeLoaderAttrSetQuery";
  
  /**
   * SQL query with at least the following column: action_name
   */
  public static final String ATTRIBUTE_LOADER_ACTION_QUERY = "attributeLoaderActionQuery";
  
  /**
   * SQL query with at least the following columns: if_has_action_name, then_has_action_name
   */
  public static final String ATTRIBUTE_LOADER_ACTION_SET_QUERY = "attributeLoaderActionSetQuery";
  
  /**
   * True means the group was loaded from loader
   */
  public static final String ATTRIBUTE_GROUPER_LOADER_METADATA_LOADED = "grouperLoaderMetadataLoaded";
  
  /**
   * True means the group was loaded from loader
   * TODO remove in 2.4
   */
  @Deprecated
  public static final String ATTRIBUTE_GROUPER_LOADER_METADATA_LAODED = ATTRIBUTE_GROUPER_LOADER_METADATA_LOADED;

  /**
   * Group id which is being populated from the loader
   */
  public static final String ATTRIBUTE_GROUPER_LOADER_METADATA_GROUP_ID = "grouperLoaderMetadataGroupId";
  
  /**
   * Millis since 1970 that this group was fully processed
   */
  public static final String ATTRIBUTE_GROUPER_LOADER_METADATA_LAST_FULL_MILLIS = "grouperLoaderMetadataLastFullMillisSince1970";
  
  /**
   * Millis since 1970 that this group was incrementally processed
   */
  public static final String ATTRIBUTE_GROUPER_LOADER_METADATA_LAST_INCREMENTAL_MILLIS = "grouperLoaderMetadataLastIncrementalMillisSince1970";

  /**
   * summary like count of additions, updates and removals
   */
  public static final String ATTRIBUTE_GROUPER_LOADER_METADATA_LAST_SUMMARY = "grouperLoaderMetadataLastSummary";
  
  /**
   * name of the loader metadata definition
   */
  public static final String LOADER_METADATA_VALUE_DEF = "loaderMetadata";

  /**
   * scheduler factory singleton
   */
  private static SchedulerFactory schedulerFactory = null;

  /**
   * lazy load (and start the scheduler) the scheduler factory
   * @return the scheduler factory
   */
  public static SchedulerFactory schedulerFactory() {
    if (schedulerFactory == null) {
      
      Properties props = new Properties();
      for (String key : GrouperLoaderConfig.retrieveConfig().propertyNames()) {
        if (key.startsWith("org.quartz.")) {
          String value = GrouperLoaderConfig.retrieveConfig().propertyValueString(key);
          if (key.startsWith("org.quartz.dataSource.myDS") 
              && !(key.equals("org.quartz.dataSource.myDS.connectionProvider.class") && StringUtils.equals(value, GrouperQuartzConnectionProvider.class.getName()))) {
            LOG.error("Quartz property filtered since uses Grouper datastore now! '" + key + "', value: '" + value + "'");
          } else {
            if (value == null) {
              value = "";
            }
            props.put(key, value);
          }
        }
      }
      if (!StringUtils.equals("myDS", props.getProperty("org.quartz.jobStore.dataSource"))) {
        LOG.error("Quartz datasource should be myDS! '" + props.getProperty("org.quartz.jobStore.dataSource") + "'");
      }
      if (StringUtils.isBlank(props.getProperty("org.quartz.jobStore.driverDelegateClass"))) {
        String driverDelegate = GrouperDdlUtils.convertUrlToQuartzDriverDelegateClass();
        if (!StringUtils.isBlank(driverDelegate)) {
          props.put("org.quartz.jobStore.driverDelegateClass", driverDelegate);
        }
      }
      try {
        schedulerFactory = new StdSchedulerFactory(props);
      } catch (SchedulerException se) {
        throw new RuntimeException(se);
      }
    }
    return schedulerFactory;
  }

  /**
   * schedule change log jobs
   */
  public static int scheduleChangeLogJobs() {
    int changesMade = 0;
    if (scheduleChangeLogTempToChangeLogJob()) {
      changesMade++;
    }
    changesMade += scheduleChangeLogConsumers();
    return changesMade;
  }
  
  /**
   * schedule maintenance job for moving records from change log to change log temp
   */
  public static boolean scheduleChangeLogTempToChangeLogJob() {

    String cronString = null;

    //this is a medium priority job
    int priority = 5;

    //schedule the log delete job
    try {
      Scheduler scheduler = GrouperLoader.schedulerFactory().getScheduler();
      String triggerName = "triggerChangeLog_grouperChangeLogTempToChangeLog";
      
      if (!GrouperLoaderConfig.retrieveConfig().propertyValueBoolean("changeLog.changeLogTempToChangeLog.enable", false)) {
        LOG.warn("grouper-loader.properties key: changeLog.changeLogTempToChangeLog.enable is not " +
          "filled in or false so the change log temp to change log daemon will not run");
        return scheduler.unscheduleJob(TriggerKey.triggerKey(triggerName));
      }
      
      cronString = GrouperLoaderConfig.retrieveConfig().propertyValueString("changeLog.changeLogTempToChangeLog.quartz.cron");

      if (StringUtils.isBlank(cronString)) {
        cronString = "50 * * * * ?";
        
      }
      
      //at this point we have all the attributes and we know the required ones are there, and logged when 
      //forbidden ones are there

      //the name of the job must be unique, so use the group name since one job per group (at this point)
      JobDetail jobDetail = JobBuilder.newJob(GrouperLoaderJob.class)
        .withIdentity(GrouperLoaderType.GROUPER_CHANGE_LOG_TEMP_TO_CHANGE_LOG)
        .build();

      //schedule this job
      GrouperLoaderScheduleType grouperLoaderScheduleType = GrouperLoaderScheduleType.CRON;

      Trigger trigger = grouperLoaderScheduleType.createTrigger(triggerName, priority, cronString, null);

      return scheduleJobIfNeeded(jobDetail, trigger);


    } catch (Exception e) {
      String errorMessage = "Could not schedule job: '" + GrouperLoaderType.GROUPER_CHANGE_LOG_TEMP_TO_CHANGE_LOG + "'";
      LOG.error(errorMessage, e);
      errorMessage += "\n" + ExceptionUtils.getFullStackTrace(e);
      try {
        //lets enter a log entry so it shows up as error in the db
        Hib3GrouperLoaderLog hib3GrouploaderLog = new Hib3GrouperLoaderLog();
        hib3GrouploaderLog.setHost(GrouperUtil.hostname());
        hib3GrouploaderLog.setJobMessage(errorMessage);
        hib3GrouploaderLog.setJobName(GrouperLoaderType.GROUPER_CHANGE_LOG_TEMP_TO_CHANGE_LOG);
        hib3GrouploaderLog.setJobSchedulePriority(priority);
        hib3GrouploaderLog.setJobScheduleQuartzCron(cronString);
        hib3GrouploaderLog.setJobScheduleType(GrouperLoaderScheduleType.CRON.name());
        hib3GrouploaderLog.setJobType(GrouperLoaderType.CHANGE_LOG.name());
        hib3GrouploaderLog.setStatus(GrouperLoaderStatus.CONFIG_ERROR.name());
        hib3GrouploaderLog.store();
        
      } catch (Exception e2) {
        LOG.error("Problem logging to loader db log", e2);
      }
    }
    return false;
  }

  /**
   * schedule change log consumer jobs
   */
  public static int scheduleChangeLogConsumers() {

    int changesMade = 0;
    
    //changeLog.consumer.ldappc.class = 
    //changeLog.consumer.ldappc.quartz.cron
    
    //make sure sequences are ok
    Map<String, String> consumerMap = GrouperLoaderConfig.retrieveConfig().propertiesMap( 
        GrouperCheckConfig.grouperLoaderConsumerPattern);
    
    Set<String> changeLogJobNames = new HashSet<String>();
    
    int index = 0;
    
    while (consumerMap.size() > 0) {
      
      //get one
      String consumerKey = consumerMap.keySet().iterator().next();
      //get the database name
      Matcher matcher = GrouperCheckConfig.grouperLoaderConsumerPattern.matcher(consumerKey);
      matcher.matches();
      String consumerName = matcher.group(1);
      boolean missingOne = false;
      //now find all 4 required keys
      String classKey = "changeLog.consumer." + consumerName + ".class";
      if (!consumerMap.containsKey(classKey)) {
        String error = "cannot find grouper-loader.properties key: " + classKey; 
        System.out.println("Grouper error: " + error);
        LOG.error(error);
        missingOne = true;
      }
      String cronKey = "changeLog.consumer." + consumerName + ".quartzCron";

      //check the classname
      Class<?> theClass = null;
      String className = consumerMap.get(classKey);
      String cronString = consumerMap.get(cronKey);
      
      String jobName = GrouperLoaderType.GROUPER_CHANGE_LOG_CONSUMER_PREFIX + consumerName;
      changeLogJobNames.add(jobName);
      
      //this is a medium priority job
      int priority = 5;

      try {
        if (missingOne) {
          throw new RuntimeException("Cant find config param" );
        }
        
        theClass = GrouperUtil.forName(className);
        if (!ChangeLogConsumerBase.class.isAssignableFrom(theClass)) {
          throw new RuntimeException("not a subclass of ChangeLogConsumerBase");
        }

        //default to every minute on the minute, though add a couple of seconds for each one
        //        if (StringUtils.isBlank(cronString) || StringUtils.equals("0 * * * * ?", cronString)) {
        //        dont change the crons that are explicitly set... only blank ones
        if (StringUtils.isBlank(cronString)) {
          cronString = ((index * 2) % 60) + " * * * * ?";
        }
        //at this point we have all the attributes and we know the required ones are there, and logged when 
        //forbidden ones are there

        //the name of the job must be unique, so use the group name since one job per group (at this point)
        JobDetail jobDetail = JobBuilder.newJob(GrouperLoaderJob.class)
          .withIdentity(jobName)
          .build();
        
        //schedule this job
        GrouperLoaderScheduleType grouperLoaderScheduleType = GrouperLoaderScheduleType.CRON;

        Trigger trigger = grouperLoaderScheduleType.createTrigger("triggerChangeLog_" + jobName, priority, cronString, null);

        if (scheduleJobIfNeeded(jobDetail, trigger)) {
          changesMade++;
        }

      } catch (Exception e) {

        String errorMessage = "Could not schedule job: '" + jobName + "'";
        LOG.error(errorMessage, e);
        errorMessage += "\n" + ExceptionUtils.getFullStackTrace(e);
        try {
          //lets enter a log entry so it shows up as error in the db
          Hib3GrouperLoaderLog hib3GrouploaderLog = new Hib3GrouperLoaderLog();
          hib3GrouploaderLog.setHost(GrouperUtil.hostname());
          hib3GrouploaderLog.setJobMessage(errorMessage);
          hib3GrouploaderLog.setJobName(jobName);
          hib3GrouploaderLog.setJobSchedulePriority(priority);
          hib3GrouploaderLog.setJobScheduleQuartzCron(cronString);
          hib3GrouploaderLog.setJobScheduleType(GrouperLoaderScheduleType.CRON.name());
          hib3GrouploaderLog.setJobType(GrouperLoaderType.CHANGE_LOG.name());
          hib3GrouploaderLog.setStatus(GrouperLoaderStatus.CONFIG_ERROR.name());
          hib3GrouploaderLog.store();
          
        } catch (Exception e2) {
          LOG.error("Problem logging to loader db log", e2);
        }

      }
      
      consumerMap.remove(classKey);
      consumerMap.remove(cronKey);
      index++;
    }
    
    // check to see if anything should be unscheduled.
    try {
      Scheduler scheduler = GrouperLoader.schedulerFactory().getScheduler();

      for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals("DEFAULT"))) {
        
        String jobName = jobKey.getName();
        
        if (jobName.startsWith(GrouperLoaderType.GROUPER_CHANGE_LOG_CONSUMER_PREFIX) && !changeLogJobNames.contains(jobName)) {
          try {
            String triggerName = "triggerChangeLog_" + jobName;
            if (scheduler.unscheduleJob(TriggerKey.triggerKey(triggerName))) {
              changesMade++;
            }
          } catch (Exception e) {
            String errorMessage = "Could not unschedule job: '" + jobName + "'";
            LOG.error(errorMessage, e);
            errorMessage += "\n" + ExceptionUtils.getFullStackTrace(e);
            try {
              //lets enter a log entry so it shows up as error in the db
              Hib3GrouperLoaderLog hib3GrouploaderLog = new Hib3GrouperLoaderLog();
              hib3GrouploaderLog.setHost(GrouperUtil.hostname());
              hib3GrouploaderLog.setJobMessage(errorMessage);
              hib3GrouploaderLog.setJobName(jobName);
              hib3GrouploaderLog.setJobType(GrouperLoaderType.CHANGE_LOG.name());
              hib3GrouploaderLog.setStatus(GrouperLoaderStatus.CONFIG_ERROR.name());
              hib3GrouploaderLog.store();
              
            } catch (Exception e2) {
              LOG.error("Problem logging to loader db log", e2);
            }
          }
        }
      }
    } catch (Exception e) {
      
      String errorMessage = "Could not query change log jobs to see if any should be unscheduled.";
      LOG.error(errorMessage, e);
      errorMessage += "\n" + ExceptionUtils.getFullStackTrace(e);
      try {
        //lets enter a log entry so it shows up as error in the db
        Hib3GrouperLoaderLog hib3GrouploaderLog = new Hib3GrouperLoaderLog();
        hib3GrouploaderLog.setHost(GrouperUtil.hostname());
        hib3GrouploaderLog.setJobMessage(errorMessage);
        hib3GrouploaderLog.setJobType(GrouperLoaderType.CHANGE_LOG.name());
        hib3GrouploaderLog.setStatus(GrouperLoaderStatus.CONFIG_ERROR.name());
        hib3GrouploaderLog.store();
        
      } catch (Exception e2) {
        LOG.error("Problem logging to loader db log", e2);
      }
    }
    return changesMade;
  }
  
  /**
   * schedule messaging listener jobs
   */
  public static int scheduleMessagingListeners() {

    int changesMade = 0;
    
    //#messaging.listener.messagingListener.class = edu.internet2.middleware.grouper.messaging.MessagingListener
    //#messaging.listener.messagingListener.quartzCron = 0 * * * * ?

    //make sure sequences are ok
    Map<String, String> listenerMap = GrouperLoaderConfig.retrieveConfig().propertiesMap( 
        GrouperCheckConfig.messagingListenerConsumerPattern);

    Set<String> messagingListenerJobNames = new HashSet<String>();
    
    int index = 0;
    
    for (String listenerKey : listenerMap.keySet()) {
      
      //get the database name
      Matcher matcher = GrouperCheckConfig.messagingListenerConsumerPattern.matcher(listenerKey);
      matcher.matches();
      String listenerName = matcher.group(1);
      boolean missingOne = false;

      //now find required keys
      String classKey = "messaging.listener." + listenerName + ".class";
            
      if (!GrouperLoaderConfig.retrieveConfig().containsKey(classKey)) {
        String error = "cannot find grouper-loader.properties key: " + classKey; 
        System.out.println("Grouper error: " + error);
        LOG.error(error);
        missingOne = true;
      }
      String cronKey = "messaging.listener." + listenerName + ".quartzCron";

      //check the classname
      Class<?> theClass = null;
      String className = GrouperLoaderConfig.retrieveConfig().propertyValueString(classKey);
      String cronString = GrouperLoaderConfig.retrieveConfig().propertyValueString(cronKey);
      
      String jobName = GrouperLoaderType.GROUPER_MESSAGING_LISTENER_PREFIX + listenerName;
      
      //could be dupes here
      if (messagingListenerJobNames.contains(jobName)) {
        continue;
      }

      messagingListenerJobNames.add(jobName);
      
      //this is a medium priority job
      int priority = 5;

      try {
        if (missingOne) {
          throw new RuntimeException("Cant find config param" );
        }
        
        theClass = GrouperUtil.forName(className);
        if (!MessagingListenerBase.class.isAssignableFrom(theClass)) {
          throw new RuntimeException("not a subclass of MessagingListenerBase");
        }

        //default to every minute on the minute, though add a couple of seconds for each one
        //        if (StringUtils.isBlank(cronString) || StringUtils.equals("0 * * * * ?", cronString)) {
        //        dont change the crons that are explicitly set... only blank ones
        if (StringUtils.isBlank(cronString)) {
          cronString = ((index * 2) % 60) + " * * * * ?";
        }
        //at this point we have all the attributes and we know the required ones are there, and logged when 
        //forbidden ones are there

        //the name of the job must be unique, so use the group name since one job per group (at this point)
        JobDetail jobDetail = JobBuilder.newJob(GrouperLoaderJob.class)
          .withIdentity(jobName)
          .build();
        
        //schedule this job
        GrouperLoaderScheduleType grouperLoaderScheduleType = GrouperLoaderScheduleType.CRON;

        Trigger trigger = grouperLoaderScheduleType.createTrigger("triggerMessaging_" + jobName, priority, cronString, null);

        if (scheduleJobIfNeeded(jobDetail, trigger)) {
          changesMade++;
        }

      } catch (Exception e) {

        String errorMessage = "Could not schedule job: '" + jobName + "'";
        LOG.error(errorMessage, e);
        errorMessage += "\n" + ExceptionUtils.getFullStackTrace(e);
        try {
          //lets enter a log entry so it shows up as error in the db
          Hib3GrouperLoaderLog hib3GrouploaderLog = new Hib3GrouperLoaderLog();
          hib3GrouploaderLog.setHost(GrouperUtil.hostname());
          hib3GrouploaderLog.setJobMessage(errorMessage);
          hib3GrouploaderLog.setJobName(jobName);
          hib3GrouploaderLog.setJobSchedulePriority(priority);
          hib3GrouploaderLog.setJobScheduleQuartzCron(cronString);
          hib3GrouploaderLog.setJobScheduleType(GrouperLoaderScheduleType.CRON.name());
          hib3GrouploaderLog.setJobType(GrouperLoaderType.MESSAGE_LISTENER.name());
          hib3GrouploaderLog.setStatus(GrouperLoaderStatus.CONFIG_ERROR.name());
          hib3GrouploaderLog.store();
          
        } catch (Exception e2) {
          LOG.error("Problem logging to loader db log", e2);
        }

      }
      
      index++;
    }
    
    // check to see if anything should be unscheduled.
    try {
      Scheduler scheduler = GrouperLoader.schedulerFactory().getScheduler();

      for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals("DEFAULT"))) {
        
        String jobName = jobKey.getName();
        
        if (jobName.startsWith(GrouperLoaderType.GROUPER_MESSAGING_LISTENER_PREFIX) && !messagingListenerJobNames.contains(jobName)) {
          try {
            String triggerName = "triggerMessaging_" + jobName;
            if (scheduler.unscheduleJob(TriggerKey.triggerKey(triggerName))) {
              changesMade++;
            }
          } catch (Exception e) {
            String errorMessage = "Could not unschedule job: '" + jobName + "'";
            LOG.error(errorMessage, e);
            errorMessage += "\n" + ExceptionUtils.getFullStackTrace(e);
            try {
              //lets enter a log entry so it shows up as error in the db
              Hib3GrouperLoaderLog hib3GrouploaderLog = new Hib3GrouperLoaderLog();
              hib3GrouploaderLog.setHost(GrouperUtil.hostname());
              hib3GrouploaderLog.setJobMessage(errorMessage);
              hib3GrouploaderLog.setJobName(jobName);
              hib3GrouploaderLog.setJobType(GrouperLoaderType.MESSAGE_LISTENER.name());
              hib3GrouploaderLog.setStatus(GrouperLoaderStatus.CONFIG_ERROR.name());
              hib3GrouploaderLog.store();
              
            } catch (Exception e2) {
              LOG.error("Problem logging to loader db log", e2);
            }
          }
        }
      }
    } catch (Exception e) {
      
      String errorMessage = "Could not query change log jobs to see if any should be unscheduled.";
      LOG.error(errorMessage, e);
      errorMessage += "\n" + ExceptionUtils.getFullStackTrace(e);
      try {
        //lets enter a log entry so it shows up as error in the db
        Hib3GrouperLoaderLog hib3GrouploaderLog = new Hib3GrouperLoaderLog();
        hib3GrouploaderLog.setHost(GrouperUtil.hostname());
        hib3GrouploaderLog.setJobMessage(errorMessage);
        hib3GrouploaderLog.setJobType(GrouperLoaderType.MESSAGE_LISTENER.name());
        hib3GrouploaderLog.setStatus(GrouperLoaderStatus.CONFIG_ERROR.name());
        hib3GrouploaderLog.store();
        
      } catch (Exception e2) {
        LOG.error("Problem logging to loader db log", e2);
      }
    }
    return changesMade;
  }
  
  /**
   * schedule other jobs
   */
  public static int scheduleOtherJobs() {
    
    //otherJob.duo.class = 
    //otherJob.duo.quartzCron = 
    //otherJob.duo.priority = 

    //make sure sequences are ok
    Map<String, String> otherJobMap = GrouperLoaderConfig.retrieveConfig().propertiesMap( 
        GrouperCheckConfig.grouperLoaderOtherJobPattern);
    
    Set<String> otherJobNames = new HashSet<String>();
        
    int changesMade = 0;
    
    while (otherJobMap.size() > 0) {
            
      //get one
      String consumerKey = otherJobMap.keySet().iterator().next();

      Matcher matcher = GrouperCheckConfig.grouperLoaderOtherJobPattern.matcher(consumerKey);
      matcher.matches();
      String otherJobName = matcher.group(1);
      boolean missingOne = false;
      //now find all required keys
      String classKey = "otherJob." + otherJobName + ".class";
      if (!otherJobMap.containsKey(classKey)) {
        String error = "cannot find grouper-loader.properties key: " + classKey; 
        System.out.println("Grouper error: " + error);
        LOG.error(error);
        missingOne = true;
      }
      
      String cronKey = "otherJob." + otherJobName + ".quartzCron";
      if (!otherJobMap.containsKey(cronKey)) {
        String error = "cannot find grouper-loader.properties key: " + cronKey; 
        System.out.println("Grouper error: " + error);
        LOG.error(error);
        missingOne = true;
      }
      
      String priorityKey = "otherJob." + otherJobName + ".priority";
      
      //check the classname
      String className = otherJobMap.get(classKey);
      String cronString = otherJobMap.get(cronKey);
      int priority = GrouperUtil.intValue(otherJobMap.get(priorityKey), 5);
      
      if (StringUtils.isBlank(cronString)) {
        LOG.warn("grouper-loader.properties key: " + cronKey + " is blank so disabling job " + otherJobName + ".");
        otherJobMap.remove(classKey);
        otherJobMap.remove(cronKey);
        otherJobMap.remove(priorityKey);
        continue;
      }
      
      String jobName = GrouperLoaderType.GROUPER_OTHER_JOB_PREFIX + otherJobName;
      otherJobNames.add(jobName);

      try {
        if (missingOne) {
          throw new RuntimeException("Cant find config param" );
        }
        
        GrouperUtil.forName(className); // just confirm that it resolves

        //at this point we have all the attributes and we know the required ones are there, and logged when 
        //forbidden ones are there

        JobDetail jobDetail = JobBuilder.newJob(GrouperDaemonJob.class)
          .withIdentity(jobName)
          .build();
        
        //schedule this job
        GrouperLoaderScheduleType grouperLoaderScheduleType = GrouperLoaderScheduleType.CRON;

        Trigger trigger = grouperLoaderScheduleType.createTrigger("triggerOtherJob_" + jobName, priority, cronString, null);

        if (scheduleJobIfNeeded(jobDetail, trigger)) {
          changesMade++;
        }
      } catch (Exception e) {

        String errorMessage = "Could not schedule job: '" + jobName + "'";
        LOG.error(errorMessage, e);
        errorMessage += "\n" + ExceptionUtils.getFullStackTrace(e);
        try {
          //lets enter a log entry so it shows up as error in the db
          Hib3GrouperLoaderLog hib3GrouploaderLog = new Hib3GrouperLoaderLog();
          hib3GrouploaderLog.setHost(GrouperUtil.hostname());
          hib3GrouploaderLog.setJobMessage(errorMessage);
          hib3GrouploaderLog.setJobName(jobName);
          hib3GrouploaderLog.setJobSchedulePriority(priority);
          hib3GrouploaderLog.setJobScheduleQuartzCron(cronString);
          hib3GrouploaderLog.setJobScheduleType(GrouperLoaderScheduleType.CRON.name());
          hib3GrouploaderLog.setJobType("OTHER_JOB");
          hib3GrouploaderLog.setStatus(GrouperLoaderStatus.CONFIG_ERROR.name());
          hib3GrouploaderLog.store();
          
        } catch (Exception e2) {
          LOG.error("Problem logging to loader db log", e2);
        }

      }
      
      otherJobMap.remove(classKey);
      otherJobMap.remove(cronKey);
      otherJobMap.remove(priorityKey);
    }
    
    // check to see if anything should be unscheduled.
    try {
      Scheduler scheduler = GrouperLoader.schedulerFactory().getScheduler();

      for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals("DEFAULT"))) {
        
        String jobName = jobKey.getName();
        
        if (jobName.startsWith(GrouperLoaderType.GROUPER_OTHER_JOB_PREFIX) && !otherJobNames.contains(jobName)) {
          try {
            String triggerName = "triggerOtherJob_" + jobName;
            if (scheduler.unscheduleJob(TriggerKey.triggerKey(triggerName))) {
              changesMade++;
            }
          } catch (Exception e) {
            String errorMessage = "Could not unschedule job: '" + jobName + "'";
            LOG.error(errorMessage, e);
            errorMessage += "\n" + ExceptionUtils.getFullStackTrace(e);
            try {
              //lets enter a log entry so it shows up as error in the db
              Hib3GrouperLoaderLog hib3GrouploaderLog = new Hib3GrouperLoaderLog();
              hib3GrouploaderLog.setHost(GrouperUtil.hostname());
              hib3GrouploaderLog.setJobMessage(errorMessage);
              hib3GrouploaderLog.setJobName(jobName);
              hib3GrouploaderLog.setJobType("OTHER_JOB");
              hib3GrouploaderLog.setStatus(GrouperLoaderStatus.CONFIG_ERROR.name());
              hib3GrouploaderLog.store();
              
            } catch (Exception e2) {
              LOG.error("Problem logging to loader db log", e2);
            }
          }
        }
      }
    } catch (Exception e) {
      
      String errorMessage = "Could not query other jobs to see if any should be unscheduled.";
      LOG.error(errorMessage, e);
      errorMessage += "\n" + ExceptionUtils.getFullStackTrace(e);
      try {
        //lets enter a log entry so it shows up as error in the db
        Hib3GrouperLoaderLog hib3GrouploaderLog = new Hib3GrouperLoaderLog();
        hib3GrouploaderLog.setHost(GrouperUtil.hostname());
        hib3GrouploaderLog.setJobMessage(errorMessage);
        hib3GrouploaderLog.setJobType("OTHER_JOB");
        hib3GrouploaderLog.setStatus(GrouperLoaderStatus.CONFIG_ERROR.name());
        hib3GrouploaderLog.store();
        
      } catch (Exception e2) {
        LOG.error("Problem logging to loader db log", e2);
      }
    }
    return changesMade;
  }

  /**
   * 
   */
  public static int scheduleEsbListenerJobs() {

    int changesMade = 0;
    
    int priority = 1;
    GregorianCalendar cal = new GregorianCalendar();
    cal.add(GregorianCalendar.SECOND, 5);
    Date runTime = cal.getTime();
    
    String triggerNameHttpListener = GrouperLoaderType.GROUPER_ESB_HTTP_LISTENER + "_trigger";
    String triggerNameXmmpListener = GrouperLoaderType.GROUPER_ESB_XMMP_LISTENER + "_trigger";

    // String cronString = "15 55 13 * * ?";
    //cronString = cal.getTime().getSeconds() + " " + cal.getTime().getMinutes() + " " + cal.getTime().getHours() + " * * ?"; 
    //System.out.println(cronString);
    boolean runEsbHttpListener = GrouperLoaderConfig.retrieveConfig().propertyValueBoolean(
        "esb.listeners.http.enable", false);

    try {
      Scheduler scheduler = GrouperLoader.schedulerFactory().getScheduler();

      if (runEsbHttpListener) {
        LOG.info("Starting experimental HTTP(S) listener");
        String port = GrouperLoaderConfig.retrieveConfig().propertyValueString("esb.listeners.http.port",
            "8080");
        String bindAddress = GrouperLoaderConfig.retrieveConfig().propertyValueString(
            "esb.listeners.http.bindaddress", "127.0.0.1");
        String authConfigFile = GrouperLoaderConfig.retrieveConfig().propertyValueString(
            "esb.listeners.http.authConfigFile", "");
        String sslKeystore = GrouperLoaderConfig.retrieveConfig().propertyValueString(
            "esb.listeners.http.ssl.keystore", "");
        String sslKeyPassword = GrouperLoaderConfig.retrieveConfig().propertyValueString(
            "esb.listeners.http.ssl.keyPassword", "");
        String sslTrustStore = GrouperLoaderConfig.retrieveConfig().propertyValueString(
            "esb.listeners.http.ssl.trustStore", "");
        String sslTrustPassword = GrouperLoaderConfig.retrieveConfig().propertyValueString(
            "esb.listeners.http.ssl.trustPassword", "");
        String sslPassword = GrouperLoaderConfig.retrieveConfig().propertyValueString(
            "esb.listeners.http.ssl.password", "");
        //at this point we have all the attributes and we know the required ones are there, and logged when 
        //forbidden ones are there

        //the name of the job must be unique, so use the group name since one job per group (at this point)
        JobDetail jobDetail = JobBuilder.newJob(GrouperUtil.forName("edu.internet2.middleware.grouper.esb.listener.EsbHttpServer"))
          .withIdentity(GrouperLoaderType.GROUPER_ESB_HTTP_LISTENER)
          .usingJobData("port", port)
          .usingJobData("bindAddress", bindAddress)
          .usingJobData("authConfigFile", authConfigFile)
          .usingJobData("keystore", sslKeystore)
          .usingJobData("keyPassword", sslKeyPassword)
          .usingJobData("trustStore", sslTrustStore)
          .usingJobData("trustPassword", sslTrustPassword)
          .usingJobData("keystore", sslKeystore)
          .usingJobData("password", sslPassword)
          .build();

        //schedule this job to run in 5 seconds
        Trigger trg = TriggerBuilder.newTrigger()
          .withIdentity(triggerNameHttpListener)
          .startAt(runTime)
          .withPriority(priority)
          .build();

        if (scheduleJobIfNeeded(jobDetail, trg)) {
          changesMade++;
        }
      } else {
        LOG.info("Not starting experimental HTTP(S) listener");
       
        scheduler.unscheduleJob(TriggerKey.triggerKey(triggerNameHttpListener));
      }
    } catch (Exception e) {
      String errorMessage = "Could not schedule job: '"
          + GrouperLoaderType.GROUPER_ESB_HTTP_LISTENER + "'";
      LOG.error(errorMessage, e);
      errorMessage += "\n" + ExceptionUtils.getFullStackTrace(e);
      try {
        //lets enter a log entry so it shows up as error in the db
        Hib3GrouperLoaderLog hib3GrouploaderLog = new Hib3GrouperLoaderLog();
        hib3GrouploaderLog.setHost(GrouperUtil.hostname());
        hib3GrouploaderLog.setJobMessage(errorMessage);
        hib3GrouploaderLog.setJobName(GrouperLoaderType.GROUPER_ESB_HTTP_LISTENER);
        hib3GrouploaderLog.setJobSchedulePriority(priority);
        hib3GrouploaderLog.setJobScheduleQuartzCron("5 seconds from now");
        hib3GrouploaderLog.setJobScheduleType(GrouperLoaderScheduleType.CRON.name());
        hib3GrouploaderLog.setJobType(GrouperLoaderType.GROUPER_ESB_HTTP_LISTENER);
        hib3GrouploaderLog.setStatus(GrouperLoaderStatus.CONFIG_ERROR.name());
        hib3GrouploaderLog.store();

      } catch (Exception e2) {
        LOG.error("Problem logging to loader db log", e2);
      }
    }

    boolean runEsbHXmppListener = GrouperLoaderConfig.retrieveConfig().propertyValueBoolean(
        "esb.listeners.xmpp.enable", false);
    try {
      Scheduler scheduler = GrouperLoader.schedulerFactory().getScheduler();
      boolean unschedule = false;
      if (runEsbHXmppListener) {
        LOG.info("Starting experimental XMPP listener");

        String server = GrouperLoaderConfig.retrieveConfig().propertyValueString(
            "esb.listeners.xmpp.server", "");
        if (server.equals("")) {
          LOG.warn("XMPP server must be configured in grouper-loader.properties");
        }
        String port = GrouperLoaderConfig.retrieveConfig().propertyValueString("esb.listeners.xmpp.port",
            "5222");
        String username = GrouperLoaderConfig.retrieveConfig().propertyValueString(
            "esb.listeners.xmpp.username", "");
        if (username.equals("")) {
          LOG.warn("XMPP username must be configured in grouper-loader.properties");
        }
        String password = GrouperLoaderConfig.retrieveConfig().propertyValueString(
            "esb.listeners.xmpp.password", "");
        if (password.equals("")) {
          LOG.warn("XMPP password must be configured in grouper-loader.properties");
        }
        String sendername = GrouperLoaderConfig.retrieveConfig().propertyValueString(
            "esb.listeners.xmpp.sendername", "");
        String resource = GrouperLoaderConfig.retrieveConfig().propertyValueString(
            "esb.listeners.xmpp.resource", "GrouperListener");
        if (server.equals("")) {
          LOG.warn("XMPP sendername must be configured in grouper-loader.properties");
        }
        if (!(server.equals("")) & !(username.equals("")) && !(password.equals(""))
            && !(sendername.equals(""))) {

          //the name of the job must be unique, so use the group name since one job per group (at this point)
          JobDetail jobDetail = JobBuilder.newJob(GrouperUtil.forName("edu.internet2.middleware.grouper.esb.listener.EsbXmppListener"))
            .withIdentity(GrouperLoaderType.GROUPER_ESB_XMMP_LISTENER)
            .usingJobData("port", port)
            .usingJobData("server", server)
            .usingJobData("username", username)
            .usingJobData("password", password)
            .usingJobData("sendername", sendername)
            .usingJobData("resource", resource)
            .build();
          
          Trigger trg = TriggerBuilder.newTrigger()
            .withIdentity(triggerNameXmmpListener)
            .startAt(runTime)
            .withPriority(priority)
            .build();

          if (scheduleJobIfNeeded(jobDetail, trg)) {
            changesMade++;
          }
        } else {
          unschedule = true;
        }
      } else {
        LOG.info("Not starting experimental XMPP listener");
        unschedule = true;
      }
      
      if (unschedule) {
        scheduler.unscheduleJob(TriggerKey.triggerKey(triggerNameXmmpListener));
      }
      
    } catch (Exception e) {
      String errorMessage = "Could not schedule job: '"
          + GrouperLoaderType.GROUPER_ESB_XMMP_LISTENER + "'";
      LOG.error(errorMessage, e);
      errorMessage += "\n" + ExceptionUtils.getFullStackTrace(e);
      try {
        //lets enter a log entry so it shows up as error in the db
        Hib3GrouperLoaderLog hib3GrouploaderLog = new Hib3GrouperLoaderLog();
        hib3GrouploaderLog.setHost(GrouperUtil.hostname());
        hib3GrouploaderLog.setJobMessage(errorMessage);
        hib3GrouploaderLog.setJobName(GrouperLoaderType.GROUPER_ESB_XMMP_LISTENER);
        hib3GrouploaderLog.setJobSchedulePriority(priority);
        hib3GrouploaderLog.setJobScheduleQuartzCron("5 seconds from now");
        hib3GrouploaderLog.setJobScheduleType(GrouperLoaderScheduleType.CRON.name());
        hib3GrouploaderLog.setJobType(GrouperLoaderType.GROUPER_ESB_XMMP_LISTENER);
        hib3GrouploaderLog.setStatus(GrouperLoaderStatus.CONFIG_ERROR.name());
        hib3GrouploaderLog.store();

      } catch (Exception e2) {
        LOG.error("Problem logging to loader db log", e2);
      }
    }
    return changesMade;
  }
  
  /**
   * @param group
   * @param grouperSession
   * @return status
   */
  public static String runJobOnceForGroup(GrouperSession grouperSession, Group group) {
    return runJobOnceForGroup(grouperSession, group, false);
  }

  /**
   * @param group
   * @param grouperSession
   * @param runOnDaemon
   * @return status
   */
  public static String runJobOnceForGroup(GrouperSession grouperSession, Group group, boolean runOnDaemon) {
    
    boolean loggerInitted = GrouperLoaderLogger.initializeThreadLocalMap("overallLog");

    try {
  
      @SuppressWarnings("deprecation")
      boolean isSqlLoader = group.hasType(GroupTypeFinder.find("grouperLoader", false));
      boolean isLdapLoader = false;
      
      String grouperLoaderTypeString = null;
        
      if (!isSqlLoader) {
        AttributeDefName grouperLoaderLdapTypeAttributeDefName = AttributeDefNameFinder.findByName(LoaderLdapUtils.grouperLoaderLdapName(), false);
        AttributeAssign attributeAssign = grouperLoaderLdapTypeAttributeDefName == null ? null : 
          group.getAttributeDelegate().retrieveAssignment(
            null, grouperLoaderLdapTypeAttributeDefName, false, false);
        if (attributeAssign != null) {
          grouperLoaderTypeString = attributeAssign.getAttributeValueDelegate().retrieveValueString(LoaderLdapUtils.grouperLoaderLdapTypeName());
          isLdapLoader = true;
        }
      } else {
        grouperLoaderTypeString = GrouperLoaderType.attributeValueOrDefaultOrNull(group, GROUPER_LOADER_TYPE);
        if (!StringUtils.isBlank(grouperLoaderTypeString)) {
          isSqlLoader = true;
        }
      }
      
      if (StringUtils.isBlank(grouperLoaderTypeString)) {
        
        throw new RuntimeException("Cant find grouper loader type of group: " + group.getName());
      }
      
      GrouperLoaderType grouperLoaderType = GrouperLoaderType.valueOfIgnoreCase(grouperLoaderTypeString, true);
      String jobName = grouperLoaderType.name() + "__" + group.getName() + "__" + group.getUuid();
      
      if (runOnDaemon) {
        return runOnceByJobName(grouperSession, jobName, true);
      }
      
      isRunningJobOnceLocally.set(true);
      
      Hib3GrouperLoaderLog hib3GrouperLoaderLog = new Hib3GrouperLoaderLog();
      hib3GrouperLoaderLog.setJobScheduleType("MANUAL_FROM_GSH");
      hib3GrouperLoaderLog.setJobName(jobName);
      hib3GrouperLoaderLog.setJobType(grouperLoaderTypeString);
      
      if (isSqlLoader) {
        GrouperLoaderJob.runJob(hib3GrouperLoaderLog, group, grouperSession);
      }
      
      if (isLdapLoader) {
        GrouperLoaderJob.runJobLdap(hib3GrouperLoaderLog, group, grouperSession);
      }
      
      String status = "SUBJECT_PROBLEMS".equals(hib3GrouperLoaderLog.getStatus()) ? "with subject problems" :
        hib3GrouperLoaderLog.getStatus();
      
      return "loader " + (isDryRun() ? "dry " : "") + "ran " + status + ", " + (isDryRun() ? "would have " : "") + "inserted " + hib3GrouperLoaderLog.getInsertCount()
        + " memberships, " + (isDryRun() ? "would have " : "") + "deleted " + hib3GrouperLoaderLog.getDeleteCount() + " memberships, total membership count: "
        + hib3GrouperLoaderLog.getTotalCount() + ", unresolvable subjects: " + hib3GrouperLoaderLog.getUnresolvableSubjectCount();
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      if (loggerInitted) {
        GrouperLoaderLogger.doTheLogging("overallLog");
      }
      
      isRunningJobOnceLocally.remove();
    }      
  }
  
  /**
   * @param grouperSession
   * @param jobName
   * @return status
   */
  public static String runOnceByJobName(GrouperSession grouperSession, String jobName) {
    return runOnceByJobName(grouperSession, jobName, false);
  }
  
  
  /**
   * @param grouperSession
   * @param jobName
   * @param runOnDaemon
   * @return status
   */
  public static String runOnceByJobName(GrouperSession grouperSession, String jobName, boolean runOnDaemon) {
    try {
      
      if (runOnDaemon) {
        if (isDryRun()) {
          throw new RuntimeException("Dry run not supported if running on daemon.");
        }
        
        if (!isJobEnabled(jobName)) {
          throw new RuntimeException("Job " + jobName + " is not enabled.");
        }
  
        try {
          Scheduler scheduler = GrouperLoader.schedulerFactory().getScheduler();
          JobKey jobKey = new JobKey(jobName);
          scheduler.triggerJob(jobKey);
        } catch (SchedulerException e) {
          throw new RuntimeException(e);
        }
        
        return "Job successfully scheduled on daemon";
      }
  
      isRunningJobOnceLocally.set(true);
      
      GrouperLoaderType grouperLoaderType = GrouperLoaderType.typeForThisName(jobName);
      if (grouperLoaderType.equals(GrouperLoaderType.SQL_SIMPLE) || grouperLoaderType.equals(GrouperLoaderType.SQL_GROUP_LIST)) {
        
        int uuidIndexStart = jobName.lastIndexOf("__");
      
        String grouperLoaderGroupUuid = jobName.substring(uuidIndexStart+2, jobName.length());
        Group group = GroupFinder.findByUuid(grouperSession, grouperLoaderGroupUuid, true);
        return runJobOnceForGroup(grouperSession, group);
      } else if (grouperLoaderType.equals(GrouperLoaderType.ATTR_SQL_SIMPLE)) {
        int uuidIndexStart = jobName.lastIndexOf("__");
        
        String grouperLoaderAttributeDefUuid = jobName.substring(uuidIndexStart+2, jobName.length());
        AttributeDef attributeDef = AttributeDefFinder.findById(grouperLoaderAttributeDefUuid, true);
        return runJobOnceForAttributeDef(grouperSession, attributeDef);
        
      }
      Hib3GrouperLoaderLog hib3GrouperLoaderLog = new Hib3GrouperLoaderLog();
      hib3GrouperLoaderLog.setJobScheduleType("MANUAL_FROM_GSH");
      hib3GrouperLoaderLog.setJobName(jobName);
      GrouperLoaderJob.runJob(hib3GrouperLoaderLog, (Group)null, grouperSession);
      
      return "loader ran successfully: " + hib3GrouperLoaderLog.getJobMessage();
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      isRunningJobOnceLocally.remove();
    }
  }
  
  /**
   * @param attributeDef
   * @param grouperSession
   * @return status
   */
  public static Hib3GrouperLoaderLog _internal_runJobOnceForAttributeDef(GrouperSession grouperSession, AttributeDef attributeDef) {
    try {
      Hib3GrouperLoaderLog hib3GrouperLoaderLog = new Hib3GrouperLoaderLog();
      hib3GrouperLoaderLog.setJobScheduleType("MANUAL_FROM_GSH");
      
      if (!attributeDef.getAttributeDelegate().hasAttributeByName(GrouperCheckConfig.attributeLoaderStemName() + ":attributeLoader")) {
        throw new RuntimeException("Cant find attributeLoader type of attributeDef: " + attributeDef.getName());
      }
      String grouperLoaderTypeString = attributeDef.getAttributeValueDelegate()
        .retrieveValueString(GrouperCheckConfig.attributeLoaderStemName() + ":attributeLoaderType");

      GrouperLoaderType grouperLoaderType = GrouperLoaderType.valueOfIgnoreCase(grouperLoaderTypeString, true);
      
      hib3GrouperLoaderLog.setJobName(grouperLoaderType.name() + "__" + attributeDef.getName() + "__" + attributeDef.getUuid());
      hib3GrouperLoaderLog.setJobType(grouperLoaderTypeString);
  
      GrouperLoaderJob.runJobAttrDef(hib3GrouperLoaderLog, attributeDef, grouperSession);
      
      return hib3GrouperLoaderLog;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }
  /**
   * @param attributeDef
   * @param grouperSession
   * @return status
   */
  public static String runJobOnceForAttributeDef(GrouperSession grouperSession, AttributeDef attributeDef) {

    Hib3GrouperLoaderLog hib3GrouperLoaderLog = _internal_runJobOnceForAttributeDef(grouperSession, attributeDef);
    
    return "loader " + (isDryRun() ? "dry " : "") + "ran successfully, " + (isDryRun() ? "would have " : "") + "inserted " + hib3GrouperLoaderLog.getInsertCount()
      + " attrDefNames, " + (isDryRun() ? "would have " : "") + "deleted " + hib3GrouperLoaderLog.getDeleteCount() + " records, total record count: "
      + hib3GrouperLoaderLog.getTotalCount();
  }

  /**
   * schedule psp full sync job
   */
  public static boolean schedulePspFullSyncJob() {
  
    String cronString = null;
    
    //this is a medium priority job
    int priority = 5;
  
    //schedule the job
    try {
      Scheduler scheduler = GrouperLoader.schedulerFactory().getScheduler();

      cronString = GrouperLoaderConfig.retrieveConfig().propertyValueString("changeLog.psp.fullSync.quartzCron");
     
      String triggerName = "trigger_" + GrouperLoaderType.PSP_FULL_SYNC.name();
      
      boolean unscheduleAndReturn = false;
      
      if (StringUtils.isEmpty(cronString)) {
        LOG.info("Full synchronization provisioning jobs are not scheduled. To schedule full synchronization jobs, " +
                 "set grouper-loader.properties key 'changeLog.psp.fullSync.quartzCron' to a cron expression.");
        unscheduleAndReturn = true;
      } else if (StringUtils.isEmpty(GrouperLoaderConfig.retrieveConfig().propertyValueString("changeLog.psp.fullSync.class"))) {
        LOG.info("Unable to run a full synchronization provisioning job. " +
            "Set grouper-loader.properties key 'changeLog.psp.fullSync.class' to the name of the class providing a fullSync() method.");
        unscheduleAndReturn = true;
      }
      
      if (unscheduleAndReturn) {
        return scheduler.unscheduleJob(TriggerKey.triggerKey(triggerName));
      }
      
      //LOG.info("Scheduling " + GrouperLoaderType.PSP_FULL_SYNC.name());
        
      //at this point we have all the attributes and we know the required ones are there, and logged when 
      //forbidden ones are there

      //the name of the job must be unique
      JobDetail jobDetail = JobBuilder.newJob(GrouperLoaderJob.class)
        .withIdentity(GrouperLoaderType.PSP_FULL_SYNC.name())
        .build();
  
      //schedule this job
      GrouperLoaderScheduleType grouperLoaderScheduleType = GrouperLoaderScheduleType.CRON;
  
      Trigger trigger = grouperLoaderScheduleType.createTrigger(triggerName, priority, cronString, null);
  
      // 2023/12/16 do not schedule the psp job
      //return scheduleJobIfNeeded(jobDetail, trigger);
      return false;
    } catch (Exception e) {
      String errorMessage = "Could not schedule job: '" + GrouperLoaderType.PSP_FULL_SYNC.name() + "'";
      LOG.error(errorMessage, e);
      errorMessage += "\n" + ExceptionUtils.getFullStackTrace(e);
      try {
        //lets enter a log entry so it shows up as error in the db
        Hib3GrouperLoaderLog hib3GrouploaderLog = new Hib3GrouperLoaderLog();
        hib3GrouploaderLog.setHost(GrouperUtil.hostname());
        hib3GrouploaderLog.setJobMessage(errorMessage);
        hib3GrouploaderLog.setJobName(GrouperLoaderType.PSP_FULL_SYNC.name());
        hib3GrouploaderLog.setJobSchedulePriority(priority);
        hib3GrouploaderLog.setJobScheduleQuartzCron(cronString);
        hib3GrouploaderLog.setJobScheduleType(GrouperLoaderScheduleType.CRON.name());
        hib3GrouploaderLog.setJobType(GrouperLoaderType.PSP_FULL_SYNC.name());
        hib3GrouploaderLog.setStatus(GrouperLoaderStatus.CONFIG_ERROR.name());
        hib3GrouploaderLog.store();
        
      } catch (Exception e2) {
        LOG.error("Problem logging to loader db log", e2);
      }
    }
    return false;
  }
  
   /**
   * if there is a threadlocal, then we are in dry run mode
   */

  private static ThreadLocal<WeakReference<GrouperLoaderDryRunBean>> threadLocalGrouperLoaderDryRun = new ThreadLocal<WeakReference<GrouperLoaderDryRunBean>>();
  
  
  
  
  /**
   * @return the threadLocalGrouperLoaderDryRun
   */
  public static GrouperLoaderDryRunBean internal_retrieveThreadLocalGrouperLoaderDryRun() {
    WeakReference<GrouperLoaderDryRunBean> weakReference = threadLocalGrouperLoaderDryRun.get();
    if (weakReference == null) {
      return null;
    }
    return weakReference.get();
  }

  
  /**
   * @param theThreadLocalGrouperLoaderDryRun the threadLocalGrouperLoaderDryRun to set
   */
  public static void internal_assignThreadLocalGrouperLoaderDryRun(
      GrouperLoaderDryRunBean theThreadLocalGrouperLoaderDryRun) {
    if (theThreadLocalGrouperLoaderDryRun == null) {
      threadLocalGrouperLoaderDryRun.remove();
    } else {
      threadLocalGrouperLoaderDryRun.set(new WeakReference<GrouperLoader.GrouperLoaderDryRunBean>(theThreadLocalGrouperLoaderDryRun));
    }
  }

  /**
   * bean holds where the logging goes, and if there, then it means we are in dry run mode
   *
   */
  public static class GrouperLoaderDryRunBean {
    
    /**
     * filewriter for output
     */
    private FileWriter fileWriter;
    
    /**
     * file
     */
    private File file;

    /**
     * construct
     * @param fileName
     */
    public GrouperLoaderDryRunBean(String fileName) {
      if (!StringUtils.isBlank(fileName)) {
        this.file = new File(fileName); 
        try {
          this.fileWriter = new FileWriter(this.file);
        } catch (IOException ioe) {
          throw new RuntimeException("Cant open file: " + fileName, ioe);
        }
      }
    }
    
    /**
     * finish everything up
     * @param success
     */
    public void finish(boolean success) {
      if (this.fileWriter != null) {
        try {
          this.fileWriter.close();
        } catch (IOException ioe) {
          throw new RuntimeException("Problem closing file: " + this.file.getAbsolutePath(), ioe);
        }
        System.out.println("Wrote dry run to file: " + this.file.getAbsolutePath() + ", succcess? " + success);
      }
    }
    
    /**
     * write a line, it shouldnt end in newline
     * @param line
     */
    public void writeLine(String line) {
      if (this.fileWriter != null) {
        try {
          this.fileWriter.write(line);
          this.fileWriter.write("\n");
        } catch (IOException ioe) {
          throw new RuntimeException("Problem writing to file: " + this.file.getAbsolutePath(), ioe);
        }
      } else {
        System.out.println(line);
      }
    }
    
  }
  
  /**
   * @param group
   * @param grouperSession
   * @param fileName is the file where output should go
   * @return status
   */
  public static String dryRunJobOnceForGroup(final GrouperSession grouperSession, final Group group, String fileName) {
    
    //put grouepr in readonly mode
    HibernateSession.threadLocalReadonlyAssign();
    GrouperLoaderDryRunBean grouperLoaderDryRunBean = new GrouperLoaderDryRunBean(fileName);
    try {
      internal_assignThreadLocalGrouperLoaderDryRun(grouperLoaderDryRunBean);
      
      boolean success = false;
      try {
      
        String result = (String)GrouperTransaction.callbackGrouperTransaction(GrouperTransactionType.READONLY_NEW, new GrouperTransactionHandler() {
          
          /**
           * 
           */
          @Override
          public Object callback(GrouperTransaction grouperTransaction)
              throws GrouperDAOException {
            
            return runJobOnceForGroup(grouperSession, group);
            
          }
        });
        
        success = true;
        return result;
        
      } finally {
        grouperLoaderDryRunBean = internal_retrieveThreadLocalGrouperLoaderDryRun();
        if (grouperLoaderDryRunBean != null) {
          try {
            grouperLoaderDryRunBean.finish(success);
          } finally {
            internal_assignThreadLocalGrouperLoaderDryRun(null);
          }
        }
      }
    } finally {
      //no longer in readonly mode
      HibernateSession.threadLocalReadonlyClear();
    }
  }
  
  /**
   * 
   * @return true if dry run
   */
  public static boolean isDryRun() {
    return internal_retrieveThreadLocalGrouperLoaderDryRun() != null;
  }
  
  /**
   * 
   * @param line
   */
  public static void dryRunWriteLine(String line) {
    GrouperLoaderDryRunBean grouperLoaderDryRunBean = internal_retrieveThreadLocalGrouperLoaderDryRun();
    if (grouperLoaderDryRunBean != null) {
      grouperLoaderDryRunBean.writeLine(line);
    }
  }
  
  /**
   * Rename a job and it's associated triggers where the name replaces the oldSubstring with the newSubstring
   * @param oldJobName
   * @param oldSubstring
   * @param newSubstring
   * @throws SchedulerException
   */
  public static void renameJobAndTriggerSubstring(String oldJobName, String oldSubstring, String newSubstring) throws SchedulerException {
    Scheduler scheduler = GrouperLoader.schedulerFactory().getScheduler();

    String newJobName = oldJobName.replace(oldSubstring, newSubstring);
    
    String grouperLoaderLogSqlUpdate = "update grouper_loader_log set job_name = ? where job_name = ?";
    HibernateSession.bySqlStatic().executeSql(grouperLoaderLogSqlUpdate, 
        GrouperUtil.toListObject(newJobName, oldJobName), 
        HibUtils.listType(StringType.INSTANCE, StringType.INSTANCE));
    
    JobKey oldJobKey = JobKey.jobKey(oldJobName);
    JobDetail oldJobDetail = scheduler.getJobDetail(oldJobKey);
    List<? extends Trigger> oldTriggers = scheduler.getTriggersOfJob(oldJobKey);
    
    JobDetail jobDetail = JobBuilder.newJob(oldJobDetail.getJobClass())
        .withIdentity(newJobName)
        .withDescription(oldJobDetail.getDescription())
        .setJobData(oldJobDetail.getJobDataMap())
        .build();
    
    Set<Trigger> triggers = new LinkedHashSet<Trigger>();
    Set<Trigger> triggersToPause = new LinkedHashSet<Trigger>();
    for (Trigger oldTrigger : oldTriggers) {
      String newTriggerName = oldTrigger.getKey().getName().replace(oldSubstring, newSubstring);
      
      Trigger trigger = TriggerBuilder.newTrigger()
          .withIdentity(newTriggerName)
          .withPriority(oldTrigger.getPriority())
          .withSchedule(oldTrigger.getScheduleBuilder())
          .withDescription(oldTrigger.getDescription())
          .build();
      
      triggers.add(trigger);
      
      if (scheduler.getTriggerState(oldTrigger.getKey()) == Trigger.TriggerState.PAUSED) {
        triggersToPause.add(trigger);
      }
    }
    
    scheduler.deleteJob(oldJobKey);
    scheduler.scheduleJob(jobDetail, triggers, true);
    
    for (Trigger trigger : triggersToPause) {
      scheduler.pauseTrigger(trigger.getKey());
    }
  }
  
  /**
   * Schedule job if new or something has changed
   * @param jobDetail
   * @param trigger
   * @return true if needed an update
   * @throws SchedulerException 
   */
  public static boolean scheduleJobIfNeeded(JobDetail jobDetail, Trigger trigger) throws SchedulerException {
    Scheduler scheduler = GrouperLoader.schedulerFactory().getScheduler();
    
    boolean scheduleJob = false;
    boolean triggerTypeChanging = false;
    
    JobDetail oldJobDetail = scheduler.getJobDetail(jobDetail.getKey());
    Trigger oldTrigger = scheduler.getTrigger(trigger.getKey());
    Trigger.TriggerState oldTriggerState = scheduler.getTriggerState(trigger.getKey());

    if (oldJobDetail == null || oldTrigger == null) {
      scheduleJob = true;
    }
    
    if (!scheduleJob) {

      if (oldTrigger instanceof SimpleTrigger && trigger instanceof SimpleTrigger) {
        if (((SimpleTrigger)oldTrigger).getRepeatInterval() != ((SimpleTrigger)trigger).getRepeatInterval()) {
          scheduleJob = true;
        }
      } else if (oldTrigger instanceof CronTrigger && trigger instanceof CronTrigger) {
        if (!((CronTrigger)oldTrigger).getCronExpression().equals(((CronTrigger)trigger).getCronExpression())) {
          scheduleJob = true;
        }
      } else {
        triggerTypeChanging = true;
        scheduleJob = true;
      }
    }
    
    if (!scheduleJob && oldJobDetail.getJobClass() != jobDetail.getJobClass()) {
      scheduleJob = true;
    }
    
    if (!scheduleJob && oldTrigger.getPriority() != trigger.getPriority()) {
      scheduleJob = true;
    }
    
    if (!scheduleJob) {
      if (oldJobDetail.getJobDataMap() == null && jobDetail.getJobDataMap() == null) {
        // ok
      } else if (oldJobDetail.getJobDataMap() == null || jobDetail.getJobDataMap() == null) {
        scheduleJob = true;
      } else if (!oldJobDetail.getJobDataMap().equals(jobDetail.getJobDataMap())) {
        scheduleJob = true;
      }
    }
    
    if (scheduleJob) {
      // apparently running this for a job that already exists when bringing the daemon up on a different host may fire some
      // jobs concurrently with jobs on another host.  so only updating if there's something to update.
      
      if (triggerTypeChanging) {
        // if this isn't done, then the schedule in quartz gets messed up..
        scheduler.unscheduleJob(oldTrigger.getKey());
      }
      
      scheduler.scheduleJob(jobDetail, GrouperUtil.toSet(trigger), true);
      if (oldTriggerState == Trigger.TriggerState.PAUSED) {
        // old trigger was disabled so disable this one too.  need a better way..
        scheduler.pauseTrigger(trigger.getKey());
      }
      
      LOG.info("Scheduled quartz job: " + jobDetail.getKey().getName());
      return true;
    }
    
    return false;
  }
  
  /**
   * see if the job is running as a run now: 
   * @param jobName
   * @return true if run now, e.g. OTHER_JOB_sleep, OTHER_JOB_schedulerCheckDaemon
   */
  public static boolean isJobRunningAsRunNow(String jobName) {
    
    int count = new GcDbAccess().sql("select count(1) from grouper_QZ_TRIGGERS where job_name = ? "
        + "and trigger_type = 'SIMPLE' and trigger_state = 'COMPLETE' and start_time > ?")
        .addBindVar(jobName)
        .addBindVar(System.currentTimeMillis() - 2000).select(int.class);
    
    return count > 0;
    
  }
  
  /**
   * @param jobName
   * @param true if checking within this job (in which case checking to see if it is running elsewhere?)
   * @return true if the job appears to currently be running
   */
  public static boolean isJobRunning(String jobName, boolean checkingAtStartOfJobBeforeInsertingGrouperLoaderLog) {

    // old logic
    if (GrouperLoaderConfig.retrieveConfig().propertyValueBoolean("daemon.legacyIsJobRunning", false)) {
      long assumeJobKilledIfNoUpdateInMillis = 1000L * GrouperConfig.retrieveConfig().propertyValueInt("loader.assumeJobKilledIfNoUpdateInSeconds", 300);
      Long count = HibernateSession.byHqlStatic()
          .createQuery("select count(*) from Hib3GrouperLoaderLog where jobName = :jobName and status = 'STARTED' and lastUpdated > :lastUpdated")
          .setString("jobName", jobName)
          .setTimestamp("lastUpdated", new Date(System.currentTimeMillis() - assumeJobKilledIfNoUpdateInMillis))
          .uniqueResult(Long.class);
      
      return count > 0;

    }
    
    //  has status of STARTED or RUNNING 
    //  AND
    //    - last_updated is in the last 30 seconds
    //    OR
    //        - theres a fired triggers with job name that matches
    //        OR 
    //         - theres a fired triggers with trigger name that matches and the triggers table has a job name that matches
    //      - AND the triggers instance name matches a scheduler that is alive
    //  then the job is running

    long assumeJobKilledIfNoUpdateInMillis = 1000L * GrouperConfig.retrieveConfig().propertyValueInt("loader.assumeJobKilledIfNoUpdateInSecondsV2", 50);
    // last checkin in last 50 seconds
    long lastCheckinTime = System.currentTimeMillis() - 50000;
    List<Long> counts = new GcDbAccess().sql("select count(*) from grouper_loader_log where job_name = ? and status in ('STARTED', 'RUNNING') and last_updated > ? "
        + " union all "
        + " select count(*) from grouper_QZ_FIRED_TRIGGERS gqft, grouper_QZ_SCHEDULER_STATE gqss "
        + " where gqft.job_name = ? and gqft.instance_name = gqss.instance_name and gqss.last_checkin_time > ? "
        + " union all "
        + " select count(*) from grouper_QZ_FIRED_TRIGGERS gqft, grouper_QZ_TRIGGERS gqt, grouper_QZ_SCHEDULER_STATE gqss "
        + " where gqft.trigger_name = gqt.trigger_name and gqt.job_name = ? "
        + " and gqft.instance_name = gqss.instance_name and gqss.last_checkin_time > ? ")
        .addBindVar(jobName)
        .addBindVar(new Date(System.currentTimeMillis() - assumeJobKilledIfNoUpdateInMillis))
        .addBindVar(jobName).addBindVar(lastCheckinTime).addBindVar(jobName).addBindVar(lastCheckinTime)
        .selectList(Long.class);

    int rowsToFind = checkingAtStartOfJobBeforeInsertingGrouperLoaderLog ? 1 : 0;
    return counts.get(0) > 0 && (counts.get(1) + counts.get(2) > rowsToFind);
  }
  
  /**
   * @param jobName 
   * @return date or null if not running
   */
  public static Date internal_getJobStartTimeIfRunning(String jobName) {
    
    // all of our jobs should have the DisallowConcurrentExecution annotation so this should only return 1 at most
    List<String> firedTimes = HibernateSession.bySqlStatic().listSelect(String.class, "select fired_time from grouper_QZ_FIRED_TRIGGERS where state='EXECUTING' and job_name = ? and fired_time is not null", 
        GrouperUtil.toListObject(jobName), HibUtils.listType(StringType.INSTANCE));
    if (firedTimes != null && firedTimes.size() > 0 && firedTimes.get(0) != null) {
      return new Date(Long.parseLong(firedTimes.get(0)));
    }
    
    return null;
  }
  
  /**
   * @param jobName
   * @return true is job is enabled
   */
  public static boolean isJobEnabled(String jobName) {

    try {
      Scheduler scheduler = GrouperLoader.schedulerFactory().getScheduler();
      
      List<? extends Trigger> triggers = scheduler.getTriggersOfJob(new JobKey(jobName));
  
      for (Trigger trigger : triggers) {
        
        Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
        if (triggerState == Trigger.TriggerState.COMPLETE) {
          // looks like this trigger is done so skip it
          continue;
        }
        
        if (triggerState != Trigger.TriggerState.PAUSED) {
          return true;
        }
      }
     
      return false;
    } catch (SchedulerException e) {
      throw new RuntimeException(e);
    }
  }
  
  /**
   * If the threadlocal indicates that the current job should be running once
   * @return boolean
   */
  public static boolean isRunningJobOnceLocally() {
    if (isRunningJobOnceLocally.get() != null && isRunningJobOnceLocally.get()) {
      return true;
    }
    
    return false;
  }
  
  /**
   * Retrieves the most recent ChangeLogTempToEntity entry and waits for it to be processed.
   * @param exceptionIfNotProcessedAfterSeconds throw an exception if still not done after this amount of seconds pass.
   *                                            -1 to continue indefinitely
   */
  public static void waitForChangeLogTempToEntityProcess(int exceptionIfNotProcessedAfterSeconds) {
    long exceptionIfNotProcessedAfterMillis = exceptionIfNotProcessedAfterSeconds * 1000;
    long startTime = System.currentTimeMillis();
    
    ChangeLogEntry tempChangeLogEntry = HibernateSession.byHqlStatic().createQuery("from ChangeLogEntryTemp order by createdOnDb desc")
        .options(new QueryOptions().paging(1, 1, false)).uniqueResult(ChangeLogEntry.class);
   
    if (tempChangeLogEntry == null) {
      return;
    }
    
    LOG.debug("Waiting for change " + tempChangeLogEntry.getId() + " to be processed");
    GrouperUtil.sleep(500);
    
    int iterations = 0;
    
    while (true) {
      int count = new GcDbAccess().sql("select count(1) from grouper_change_log_entry_temp where created_on = ?")
          .addBindVar(tempChangeLogEntry.getCreatedOnDb()).select(int.class);
      
      if (count == 0) {
        return;
      }
      
      if (exceptionIfNotProcessedAfterMillis > 0) {
        long processedTimeMillis = System.currentTimeMillis() - startTime;
        if (processedTimeMillis > exceptionIfNotProcessedAfterMillis) {
          throw new RuntimeException("grouper_change_log_entry_temp still not processed after " + processedTimeMillis + " milliseconds");
        }
      }
      
      iterations++;
      
      long sleepTime = iterations * 2000;
      if (sleepTime > 20000) {
        sleepTime = 20000;
      }

      LOG.debug("Sleeping for " + sleepTime);
      GrouperUtil.sleep(sleepTime);
    }
  }
  
  /**
   * Retrieves the most recent change log entry and waits for it to be processed by this change log consumer
   * @param jobName name of the job, e.g. CHANGE_LOG_consumer_compositeMemberships
   * @param waitForChangeLogTempToEntityProcess wait for change log temp to clear first
   * @param exceptionIfNotProcessedAfterSeconds throw an exception if still not done after this amount of seconds pass.
   *                                            -1 to continue indefinitely
   */
  public static void waitForChangeLogJobProcess(String jobName, boolean waitForChangeLogTempToEntityProcess, int exceptionIfNotProcessedAfterSeconds) {
    long exceptionIfNotProcessedAfterMillis = exceptionIfNotProcessedAfterSeconds * 1000;
    long startTime = System.currentTimeMillis();
    
    if (!jobName.startsWith(GrouperLoaderType.GROUPER_CHANGE_LOG_CONSUMER_PREFIX)) {
      throw new RuntimeException("jobName doesn't start with " + GrouperLoaderType.GROUPER_CHANGE_LOG_CONSUMER_PREFIX);
    }
    
    String consumerName = jobName.substring(GrouperLoaderType.GROUPER_CHANGE_LOG_CONSUMER_PREFIX.length());
    
    if (waitForChangeLogTempToEntityProcess) {
      waitForChangeLogTempToEntityProcess(exceptionIfNotProcessedAfterSeconds);
    }
    
    ChangeLogEntry changeLogEntry = HibernateSession.byHqlStatic().createQuery("from ChangeLogEntryEntity order by sequenceNumber desc")
        .options(new QueryOptions().paging(1, 1, false)).uniqueResult(ChangeLogEntry.class);
   
    if (changeLogEntry == null) {
      return;
    }
    
    LOG.debug("Waiting for change " + changeLogEntry.getSequenceNumber() + " to be processed");
    
    int iterations = 0;
    
    while (true) {
      ChangeLogConsumer changeLogConsumer = GrouperDAOFactory.getFactory().getChangeLogConsumer().findByName(consumerName, true);

      if (changeLogConsumer.getLastSequenceProcessed() >= changeLogEntry.getSequenceNumber()) {
        return;
      }
      
      if (exceptionIfNotProcessedAfterMillis > 0) {
        long processedTimeMillis = System.currentTimeMillis() - startTime;
        if (processedTimeMillis > exceptionIfNotProcessedAfterMillis) {
          throw new RuntimeException("change log still not processed after " + processedTimeMillis + " milliseconds");
        }
      }
      
      iterations++;
      
      long sleepTime = iterations * 2000;
      if (sleepTime > 20000) {
        sleepTime = 20000;
      }

      LOG.debug("Sleeping for " + sleepTime);
      GrouperUtil.sleep(sleepTime);
    }
  }
}
