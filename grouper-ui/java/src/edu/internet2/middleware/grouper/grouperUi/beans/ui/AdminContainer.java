/**
 * 
 */
package edu.internet2.middleware.grouper.grouperUi.beans.ui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import edu.emory.mathcs.backport.java.util.Collections;
import edu.internet2.middleware.grouper.grouperUi.beans.api.GuiDaemonJob;
import edu.internet2.middleware.grouper.grouperUi.beans.api.GuiHib3GrouperLoaderLog;
import edu.internet2.middleware.grouper.grouperUi.beans.api.GuiInstrumentationDataInstance;
import edu.internet2.middleware.grouper.grouperUi.beans.json.GuiOption;
import edu.internet2.middleware.grouper.grouperUi.beans.json.GuiPaging;
import edu.internet2.middleware.grouper.privs.PrivilegeHelper;
import edu.internet2.middleware.grouper.ui.GrouperUiFilter;
import edu.internet2.middleware.grouper.ui.util.GrouperUiConfig;
import edu.internet2.middleware.grouper.util.GrouperUtil;
import edu.internet2.middleware.subject.Subject;


/**
 * admin container
 * @author mchyzer
 *
 */
public class AdminContainer {

  private List<GuiInstrumentationDataInstance> guiInstrumentationDataInstances;
  
  private Map<String, Map<String, Long>> guiInstrumentationGraphResults;
  
  private Set<String> guiInstrumentationDaysWithData;
  
  private String guiInstrumentationFilterDate;
  
  private boolean daemonJobsShowExtendedResults = false;
  
  private boolean daemonJobsShowOnlyErrors = false;
  
  private List<GuiHib3GrouperLoaderLog> guiHib3GrouperLoaderLogs;
  
  /**
   * paging for daemon jobs
   */
  private GuiPaging daemonJobsGuiPaging = null;

  /**
   * if import from group
   * @return if from group
   */
  public boolean isSubjectApiDiagnosticsShow() {
    
    if (!GrouperUiConfig.retrieveConfig().propertyValueBoolean("uiV2.admin.subjectApiDiagnostics.show", true)) {
      return false;
    }
    
    Subject loggedInSubject = GrouperUiFilter.retrieveSubjectLoggedIn();
    if (PrivilegeHelper.isWheelOrRoot(loggedInSubject)) {
      return true;
    }
    
    String error = GrouperUiFilter.requireUiGroup("uiV2.admin.subjectApiDiagnostics.must.be.in.group", loggedInSubject, false);

    if (StringUtils.isBlank(error)) {
      return true;
    }
    
    return false;
  }
  
  /**
   * if show instrumentation
   * @return if show instrumentation
   */
  public boolean isInstrumentationShow() {
    
    if (!GrouperUiConfig.retrieveConfig().propertyValueBoolean("uiV2.admin.instrumentation.show", true)) {
      return false;
    }
    
    Subject loggedInSubject = GrouperUiFilter.retrieveSubjectLoggedIn();
    if (PrivilegeHelper.isWheelOrRoot(loggedInSubject)) {
      return true;
    }
    
    String error = GrouperUiFilter.requireUiGroup("uiV2.admin.instrumentation.must.be.in.group", loggedInSubject, false);

    if (StringUtils.isBlank(error)) {
      return true;
    }
    
    return false;
  }
  
  /**
   * if show daemon jobs
   * @return if show daemon jobs
   */
  public boolean isDaemonJobsShow() {
    
    if (!GrouperUiConfig.retrieveConfig().propertyValueBoolean("uiV2.admin.daemonJobs.show", true)) {
      return false;
    }
    
    Subject loggedInSubject = GrouperUiFilter.retrieveSubjectLoggedIn();
    if (PrivilegeHelper.isWheelOrRoot(loggedInSubject)) {
      return true;
    }
    
    String error = GrouperUiFilter.requireUiGroup("uiV2.admin.daemonJobs.must.be.in.group", loggedInSubject, false);

    if (StringUtils.isBlank(error)) {
      return true;
    }
    
    return false;
  }

  
  /**
   * @return the guiInstrumentationDataInstances
   */
  public List<GuiInstrumentationDataInstance> getGuiInstrumentationDataInstances() {
    return guiInstrumentationDataInstances;
  }

  
  /**
   * @param guiInstrumentationDataInstances the guiInstrumentationDataInstances to set
   */
  public void setGuiInstrumentationDataInstances(
      List<GuiInstrumentationDataInstance> guiInstrumentationDataInstances) {
    this.guiInstrumentationDataInstances = guiInstrumentationDataInstances;
  }

  
  /**
   * @return the guiInstrumentationGraphResults
   */
  public Map<String, Map<String, Long>> getGuiInstrumentationGraphResults() {
    return guiInstrumentationGraphResults;
  }

  
  /**
   * @param guiInstrumentationGraphResults the guiInstrumentationGraphResults to set
   */
  public void setGuiInstrumentationGraphResults(
      Map<String, Map<String, Long>> guiInstrumentationGraphResults) {
    this.guiInstrumentationGraphResults = guiInstrumentationGraphResults;
  }

  
  /**
   * @return the guiInstrumentationDaysWithData
   */
  public Set<String> getGuiInstrumentationDaysWithData() {
    return guiInstrumentationDaysWithData;
  }

  
  /**
   * @param guiInstrumentationDaysWithData the guiInstrumentationDaysWithData to set
   */
  public void setGuiInstrumentationDaysWithData(Set<String> guiInstrumentationDaysWithData) {
    this.guiInstrumentationDaysWithData = guiInstrumentationDaysWithData;
  }

  
  /**
   * @return the guiInstrumentationFilterDate
   */
  public String getGuiInstrumentationFilterDate() {
    return guiInstrumentationFilterDate;
  }

  
  /**
   * @param guiInstrumentationFilterDate the guiInstrumentationFilterDate to set
   */
  public void setGuiInstrumentationFilterDate(String guiInstrumentationFilterDate) {
    this.guiInstrumentationFilterDate = guiInstrumentationFilterDate;
  }
  
  /**
   * set of jobs to show on screen
   */
  private List<GuiDaemonJob> guiDaemonJobs;
  
  /**
   * set of jobs to show on screen
   * @return set of jobs
   */
  public List<GuiDaemonJob> getGuiDaemonJobs() {
    return this.guiDaemonJobs;
  }

  /**
   * set of jobs to show on screen
   * @param guiDaemonJobs1
   */
  public void setGuiDaemonJobs(List<GuiDaemonJob> guiDaemonJobs1) {
    this.guiDaemonJobs = guiDaemonJobs1;
  }

  
  /**
   * @return the daemonJobsGuiPaging
   */
  public GuiPaging getDaemonJobsGuiPaging() {
    if (daemonJobsGuiPaging == null) {
      daemonJobsGuiPaging = new GuiPaging();
    }
    return daemonJobsGuiPaging;
  }

  
  /**
   * @param daemonJobsGuiPaging the daemonJobsGuiPaging to set
   */
  public void setDaemonJobsGuiPaging(GuiPaging daemonJobsGuiPaging) {
    this.daemonJobsGuiPaging = daemonJobsGuiPaging;
  }
  
  /**
   * @return refresh interval in seconds
   */
  public int getDaemonJobsRefreshInterval() {
    return GrouperUiConfig.retrieveConfig().propertyValueInt("uiV2.admin.daemonJobs.refreshInterval", 30);
  }
  
  /**
   * @return refresh count
   */
  public int getDaemonJobsRefreshCount() {
    return GrouperUiConfig.retrieveConfig().propertyValueInt("uiV2.admin.daemonJobs.refreshCount", 30);
  }

  
  
  /**
   * @return the daemonJobsShowOnlyErrors
   */
  public boolean isDaemonJobsShowOnlyErrors() {
    return this.daemonJobsShowOnlyErrors;
  }

  
  /**
   * @param daemonJobsShowOnlyErrors1 the daemonJobsShowOnlyErrors to set
   */
  public void setDaemonJobsShowOnlyErrors(boolean daemonJobsShowOnlyErrors1) {
    this.daemonJobsShowOnlyErrors = daemonJobsShowOnlyErrors1;
  }

  /**
   * @return the daemonJobsShowExtendedResults
   */
  public boolean isDaemonJobsShowExtendedResults() {
    return daemonJobsShowExtendedResults;
  }

  
  /**
   * @param daemonJobsShowExtendedResults the daemonJobsShowExtendedResults to set
   */
  public void setDaemonJobsShowExtendedResults(boolean daemonJobsShowExtendedResults) {
    this.daemonJobsShowExtendedResults = daemonJobsShowExtendedResults;
  }

  /**
   * selected item for common filter
   */
  private String daemonJobsCommonFilter;
  
  
  /**
   * selected item for common filter
   * @return the daemonJobsCommonFilter
   */
  public String getDaemonJobsCommonFilter() {
    return this.daemonJobsCommonFilter;
  }

  
  /**
   * selected item for common filter
   * @param daemonJobsCommonFilter1 the daemonJobsCommonFilter to set
   */
  public void setDaemonJobsCommonFilter(String daemonJobsCommonFilter1) {
    this.daemonJobsCommonFilter = daemonJobsCommonFilter1;
  }

  /**
   * 
   */
  private List<GuiOption> daemonJobsCommonFilters;
  
  /**
   * @return the daemonJobsCommonFilters
   */
  public List<GuiOption> getDaemonJobsCommonFilters() {
    
    if (this.daemonJobsCommonFilters == null) {
      
      this.daemonJobsCommonFilters = new ArrayList<GuiOption>();

      this.daemonJobsCommonFilters.add(new GuiOption("Loader", "INTERNAL_LOADER", null));
      this.daemonJobsCommonFilters.add(new GuiOption("Other job", "OTHER_JOB_", null));
      this.daemonJobsCommonFilters.add(new GuiOption("Reports", "grouper_report_", null));
      this.daemonJobsCommonFilters.add(new GuiOption("Change log", "CHANGE_LOG_", null));
      this.daemonJobsCommonFilters.add(new GuiOption("Maintenance", "MAINTENANCE_", null));
      this.daemonJobsCommonFilters.add(new GuiOption("Loader - SQL simple", "SQL_SIMPLE_", null));
      this.daemonJobsCommonFilters.add(new GuiOption("Loader - SQL group list", "SQL_GROUP_LIST_", null));
      this.daemonJobsCommonFilters.add(new GuiOption("Loader - LDAP group list", "LDAP_GROUP_LIST_", null));
      this.daemonJobsCommonFilters.add(new GuiOption("Loader - LDAP groups from attributes", "LDAP_GROUPS_FROM_ATTRIBUTES_", null));
      this.daemonJobsCommonFilters.add(new GuiOption("Loader - LDAP simple", "LDAP_SIMPLE_", null));
      
      String customCommonFilterString = GrouperUiConfig.retrieveConfig().propertyValueString("uiV2.admin.daemonJob.commonFilterAdditions");
      if (!StringUtils.isBlank(customCommonFilterString)) {
        for (String commonFilterAddition : GrouperUtil.splitTrim(customCommonFilterString, ",")) {
          this.daemonJobsCommonFilters.add(new GuiOption(commonFilterAddition, commonFilterAddition, null));
        }
      }
      
      Collections.sort(this.daemonJobsCommonFilters, new Comparator<GuiOption>() {

        public int compare(GuiOption o1, GuiOption o2) {
          if (o1 == o2) {
            return 0;
          }
          if (o1==null) {
            return -1;
          }
          if (o2==null) {
            return 1;
          }
          return o1.getName().compareTo(o2.getName());
        }
      });
    }
    
    return this.daemonJobsCommonFilters;
  }
  
  /**
   * 
   */
  private String daemonJobsFilter;
  
  
  /**
   * @return the daemonJobsFilter
   */
  public String getDaemonJobsFilter() {
    return this.daemonJobsFilter;
  }

  
  /**
   * @param daemonJobsFilter1 the daemonJobsFilter to set
   */
  public void setDaemonJobsFilter(String daemonJobsFilter1) {
    this.daemonJobsFilter = daemonJobsFilter1;
  }

  /**
   * @return the guiHib3GrouperLoaderLogs
   */
  public List<GuiHib3GrouperLoaderLog> getGuiHib3GrouperLoaderLogs() {
    return guiHib3GrouperLoaderLogs;
  }

  
  /**
   * @param guiHib3GrouperLoaderLogs the guiHib3GrouperLoaderLogs to set
   */
  public void setGuiHib3GrouperLoaderLogs(
      List<GuiHib3GrouperLoaderLog> guiHib3GrouperLoaderLogs) {
    this.guiHib3GrouperLoaderLogs = guiHib3GrouperLoaderLogs;
  }
  
  /**
   * 
   * @return number of rows
   */
  public int getDaemonJobsViewLogsNumberOfRows() {
    return GrouperUiConfig.retrieveConfig().propertyValueInt("uiV2.loader.logs.maxSize", 400);
  }
}
