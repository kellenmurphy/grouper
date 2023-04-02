/**
 * @author mchyzer
 * $Id$
 */
package edu.internet2.middleware.grouperClient.jdbc.tableSync;

import java.sql.Timestamp;
import java.util.Set;

import edu.internet2.middleware.grouperClient.jdbc.GcDbAccess;
import edu.internet2.middleware.grouperClient.jdbc.GcDbVersionable;
import edu.internet2.middleware.grouperClient.jdbc.GcPersist;
import edu.internet2.middleware.grouperClient.jdbc.GcPersistableClass;
import edu.internet2.middleware.grouperClient.jdbc.GcPersistableField;
import edu.internet2.middleware.grouperClient.jdbc.GcPersistableHelper;
import edu.internet2.middleware.grouperClient.jdbc.GcSqlAssignPrimaryKey;
import edu.internet2.middleware.grouperClient.util.GrouperClientUtils;
import edu.internet2.middleware.grouperClientExt.org.apache.commons.lang3.builder.EqualsBuilder;


/**
 * if doing user level syncs, this is the metadata
 */
@GcPersistableClass(tableName="grouper_sync_member", defaultFieldPersist=GcPersist.doPersist)
public class GcGrouperSyncMember implements GcSqlAssignPrimaryKey, GcDbVersionable {

  //########## START GENERATED BY GcDbVersionableGenerate.java ###########
  /** save the state when retrieving from DB */
  @GcPersistableField(persist = GcPersist.dontPersist)
  private GcGrouperSyncMember dbVersion = null;

  /**
   * take a snapshot of the data since this is what is in the db
   */
  @Override
  public void dbVersionReset() {
    //lets get the state from the db so we know what has changed
    this.dbVersion = this.clone();
  }

  /**
   * if we need to update this object
   * @return if needs to update this object
   */
  @Override
  public boolean dbVersionDifferent() {
    return !this.equalsDeep(this.dbVersion);
  }

  /**
   * db version
   */
  @Override
  public void dbVersionDelete() {
    this.dbVersion = null;
  }

  /**
   * deep clone the fields in this object
   */
  @Override
  public GcGrouperSyncMember clone() {

    GcGrouperSyncMember gcGrouperSyncMember = new GcGrouperSyncMember();
    //connectionName  DONT CLONE

    gcGrouperSyncMember.errorCodeDb = this.errorCodeDb;
    gcGrouperSyncMember.errorMessage = this.errorMessage;
    gcGrouperSyncMember.errorTimestamp = this.errorTimestamp;
    //grouperSync  DONT CLONE

    gcGrouperSyncMember.grouperSyncId = this.grouperSyncId;
    gcGrouperSyncMember.id = this.id;
//    gcGrouperSyncMember.inGrouperDb = this.inGrouperDb;
//    gcGrouperSyncMember.inGrouperEnd = this.inGrouperEnd;
//    gcGrouperSyncMember.inGrouperInsertOrExistsDb = this.inGrouperInsertOrExistsDb;
//    gcGrouperSyncMember.inGrouperStart = this.inGrouperStart;
    gcGrouperSyncMember.inTargetDb = this.inTargetDb;
    gcGrouperSyncMember.inTargetEnd = this.inTargetEnd;
    gcGrouperSyncMember.inTargetInsertOrExistsDb = this.inTargetInsertOrExistsDb;
    gcGrouperSyncMember.inTargetStart = this.inTargetStart;
    gcGrouperSyncMember.lastTimeWorkWasDone = this.lastTimeWorkWasDone;
    //lastUpdated  DONT CLONE

    gcGrouperSyncMember.lastUserMetadataSyncStart = this.lastUserMetadataSyncStart;
    gcGrouperSyncMember.lastUserMetadataSync = this.lastUserMetadataSync;
    gcGrouperSyncMember.lastUserSyncStart = this.lastUserSyncStart;
    gcGrouperSyncMember.lastUserSync = this.lastUserSync;
    gcGrouperSyncMember.entityAttributeValueCache0 = this.entityAttributeValueCache0;
    gcGrouperSyncMember.entityAttributeValueCache1 = this.entityAttributeValueCache1;
    gcGrouperSyncMember.memberId = this.memberId;
    gcGrouperSyncMember.entityAttributeValueCache2 = this.entityAttributeValueCache2;
    gcGrouperSyncMember.entityAttributeValueCache3 = this.entityAttributeValueCache3;
    gcGrouperSyncMember.metadataUpdated = this.metadataUpdated;
    gcGrouperSyncMember.provisionableDb = this.provisionableDb;
    gcGrouperSyncMember.provisionableEnd = this.provisionableEnd;
    gcGrouperSyncMember.provisionableStart = this.provisionableStart;
    gcGrouperSyncMember.sourceId = this.sourceId;
    gcGrouperSyncMember.subjectId = this.subjectId;
    gcGrouperSyncMember.subjectIdentifier = this.subjectIdentifier;
    gcGrouperSyncMember.metadataJson = this.metadataJson;

    return gcGrouperSyncMember;
  }

  /**
   *
   */
  public boolean equalsDeep(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof GcGrouperSyncMember)) {
      return false;
    }
    GcGrouperSyncMember other = (GcGrouperSyncMember) obj;

    return new EqualsBuilder()

        //connectionName  DONT EQUALS

        .append(this.errorCodeDb, other.errorCodeDb)
        .append(this.errorMessage, other.errorMessage)
        .append(this.errorTimestamp, other.errorTimestamp)
        //grouperSync  DONT EQUALS

        .append(this.grouperSyncId, other.grouperSyncId)
        .append(this.id, other.id)
//        .append(this.inGrouperDb, other.inGrouperDb)
//        .append(this.inGrouperEnd, other.inGrouperEnd)
//        .append(this.inGrouperInsertOrExistsDb, other.inGrouperInsertOrExistsDb)
//        .append(this.inGrouperStart, other.inGrouperStart)
        .append(this.inTargetDb, other.inTargetDb)
        .append(this.inTargetEnd, other.inTargetEnd)
        .append(this.inTargetInsertOrExistsDb, other.inTargetInsertOrExistsDb)
        .append(this.inTargetStart, other.inTargetStart)
        .append(this.lastTimeWorkWasDone, other.lastTimeWorkWasDone)
        //lastUpdated  DONT EQUALS

        .append(this.lastUserMetadataSync, other.lastUserMetadataSync)
        .append(this.lastUserMetadataSyncStart, other.lastUserMetadataSyncStart)
        .append(this.lastUserSync, other.lastUserSync)
        .append(this.lastUserSyncStart, other.lastUserSyncStart)
        .append(this.entityAttributeValueCache0, other.entityAttributeValueCache0)
        .append(this.entityAttributeValueCache1, other.entityAttributeValueCache1)
        .append(this.memberId, other.memberId)
        .append(this.entityAttributeValueCache2, other.entityAttributeValueCache2)
        .append(this.entityAttributeValueCache3, other.entityAttributeValueCache3)
        .append(this.metadataUpdated, other.metadataUpdated)
        .append(this.provisionableDb, other.provisionableDb)
        .append(this.provisionableEnd, other.provisionableEnd)
        .append(this.provisionableStart, other.provisionableStart)
        .append(this.sourceId, other.sourceId)
        .append(this.subjectId, other.subjectId)
        .append(this.subjectIdentifier, other.subjectIdentifier)
        .isEquals();

  }
  //########## END GENERATED BY GcDbVersionableGenerate.java ###########


//  /**
//   * T if inserted on the in_grouper_start date, or F if it existed then and not sure when inserted
//   * @return true or false
//   */
//  public boolean isInGrouperInsertOrExists() {
//    return GrouperClientUtils.booleanValue(this.inGrouperInsertOrExistsDb, false);
//  }
//  
//  /**
//   * T if inserted on the in_grouper_start date, or F if it existed then and not sure when inserted
//   * @param inGrouperInsertOrExists
//   */
//  public void setInGrouperInsertOrExists(boolean inGrouperInsertOrExists) {
//    this.inGrouperInsertOrExistsDb = inGrouperInsertOrExists ? "T" : "F";
//  }
//
//  /**
//   * if in grouper
//   * @return if in grouper
//   */
//  public boolean isInGrouper() {
//    return GrouperClientUtils.booleanValue(this.inGrouperDb, false);
//  }
// 
//  /**
//   * if in grouper
//   * @param in grouper
//   */
//  public void setInGrouper(boolean inGrouper) {
//    this.inGrouperDb = inGrouper ? "T" : "F";
//  }
//  
//  /**
//   * if this group exists in grouper
//   */
//  @GcPersistableField(columnName="in_grouper")
//  private String inGrouperDb;
//
//  /**
//   * when this group was removed from grouper
//   */
//  private Timestamp inGrouperEnd;
//
//  /**
//   * when this group was added to grouper
//   */
//  private Timestamp inGrouperStart;
//
//  /**
//   * T if inserted on the in_grouper_start date, or F if it existed then and not sure when inserted
//   */
//  @GcPersistableField(columnName="in_grouper_insert_or_exists")
//  private String inGrouperInsertOrExistsDb;
//  
//  /**
//   * if this group exists in grouper
//   * @return if is target
//   */
//  public Boolean getInGrouper() {
//    return GrouperClientUtils.booleanObjectValue(this.inGrouperDb);
//  }
//
//  /**
//   * if this group exists in grouper T/F
//   * @return
//   */
//  public String getInGrouperDb() {
//    return inGrouperDb;
//  }
//
//  /**
//   * if this group exists in grouper T/F
//   * @param inGrouperDb
//   */
//  public void setInGrouperDb(String inGrouperDb) {
//    this.inGrouperDb = inGrouperDb;
//  }
//
//  /**
//   * when this group was removed from grouper
//   * @return
//   */
//  public Timestamp getInGrouperEnd() {
//    return inGrouperEnd;
//  }
//
//  /**
//   * when this group was removed from grouper
//   * @param inGrouperEnd
//   */
//  public void setInGrouperEnd(Timestamp inGrouperEnd) {
//    this.inGrouperEnd = inGrouperEnd;
//  }
//
//  /**
//   * when this group was added to grouper
//   * @return
//   */
//  public Timestamp getInGrouperStart() {
//    return inGrouperStart;
//  }
//
//  /**
//   * when this group was added to grouper
//   * @param inGrouperStart
//   */
//  public void setInGrouperStart(Timestamp inGrouperStart) {
//    this.inGrouperStart = inGrouperStart;
//  }
//
//  /**
//   * if the provisioner added to grouper or if it already existed
//   * @return
//   */
//  public String getInGrouperInsertOrExistsDb() {
//    return inGrouperInsertOrExistsDb;
//  }
//
//  /**
//   * if the provisioner added to grouper or if it already existed
//   * @param inGrouperInsertOrExistsDb
//   */
//  public void setInGrouperInsertOrExistsDb(String inGrouperInsertOrExistsDb) {
//    this.inGrouperInsertOrExistsDb = inGrouperInsertOrExistsDb;
//  }

  /**
   * Error code e.g. ERR error, INV invalid based on script, LEN attribute too large, REQ required attribute missing, DNE data in target does not exist
   */
  @GcPersistableField(columnName="error_code")
  private String errorCodeDb;

  /**
   * Error code e.g. ERR error, INV invalid based on script, LEN attribute too large, REQ required attribute missing, DNE data in target does not exist
   * @return
   */
  public String getErrorCodeDb() {
    return errorCodeDb;
  }

  /**
   * Error code e.g. ERR error, INV invalid based on script, LEN attribute too large, REQ required attribute missing, DNE data in target does not exist
   * @param errorCodeDb
   */
  public void setErrorCodeDb(String errorCodeDb) {
    this.errorCodeDb = errorCodeDb;
  }

  /**
   * Error code e.g. ERR error, INV invalid based on script, LEN attribute too large, REQ required attribute missing, DNE data in target does not exist
   * @return
   */
  public GcGrouperSyncErrorCode getErrorCode() {
    if (this.errorCodeDb == null) {
      return null;
    }
    return GrouperClientUtils.enumValueOfIgnoreCase(GcGrouperSyncErrorCode.class, this.errorCodeDb, false);
  }

  /**
   * Error code e.g. ERR error, INV invalid based on script, LEN attribute too large, REQ required attribute missing, DNE data in target does not exist
   * @param gcGrouperSyncErrorCode
   */
  public void setErrorCode(GcGrouperSyncErrorCode gcGrouperSyncErrorCode) {
    this.errorCodeDb = gcGrouperSyncErrorCode == null ? null : gcGrouperSyncErrorCode.name();
  }
  

  /**
   * when this user was last synced, includes metadata and memberships
   */
  private Timestamp lastUserSync;
  
  /**
   * when this user was last synced started, includes metadata and memberships
   */
  private Timestamp lastUserSyncStart;
  
  /**
   * when this user was last synced started, includes metadata and memberships
   * @return
   */
  public Timestamp getLastUserSyncStart() {
    return lastUserSyncStart;
  }

  /**
   * when this user was last synced started, includes metadata and memberships
   * @param lastUserSyncStart
   */
  public void setLastUserSyncStart(Timestamp lastUserSyncStart) {
    this.lastUserSyncStart = lastUserSyncStart;
  }

  /**
   * when this user was last synced, includes metadata and memberships
   * @return when
   */
  public Timestamp getLastUserSync() {
    return this.lastUserSync;
  }

  /**
   * when this user was last synced, includes metadata and memberships
   * @param lastUserSync1
   */
  public void setLastUserSync(Timestamp lastUserSync1) {
    this.lastUserSync = lastUserSync1;
  }

  /**
   * when this users name and description and metadata was synced
   */
  private Timestamp lastUserMetadataSync;
  
  /**
   * when this users name and description and metadata was synced (Started)
   */
  private Timestamp lastUserMetadataSyncStart;
  
  /**
   * when this users name and description and metadata was synced (Started)
   * @return
   */
  public Timestamp getLastUserMetadataSyncStart() {
    return lastUserMetadataSyncStart;
  }

  /**
   * when this users name and description and metadata was synced (Started)
   * @param lastUserMetadataSyncStart
   */
  public void setLastUserMetadataSyncStart(Timestamp lastUserMetadataSyncStart) {
    this.lastUserMetadataSyncStart = lastUserMetadataSyncStart;
  }

  /**
   * when this users name and description and metadata was synced
   * @return when
   */
  public Timestamp getLastUserMetadataSync() {
    return this.lastUserMetadataSync;
  }

  /**
   * when this users name and description and metadata was synced
   * @param lastUserMetadataSync1
   */
  public void setLastUserMetadataSync(Timestamp lastUserMetadataSync1) {
    this.lastUserMetadataSync = lastUserMetadataSync1;
  }

  /**
   * delete all data if table is here
   */
  public static void reset() {
    
    new GcDbAccess().connectionName("grouper").sql("delete from " + GcPersistableHelper.tableName(GcGrouperSyncMember.class)).executeSql();
  }

  /**
   * foreign key to the members sync table, though not a real foreign key
   */
  private String memberId;
  
  /**
   * foreign key to the members sync table, though not a real foreign key
   * @return member id
   */
  public String getMemberId() {
    return this.memberId;
  }

  /**
   * foreign key to the members sync table, though not a real foreign key
   * @param memberId1
   */
  public void setMemberId(String memberId1) {
    this.memberId = memberId1;
  }

  /**
   * subject identifier for this sync
   */
  private String subjectIdentifier;
  
  /**
   * subject identifier for this sync
   * @return subject identifier for this sync
   */
  public String getSubjectIdentifier() {
    return this.subjectIdentifier;
  }

  /**
   * subject identifier for this sync
   * @param subjectIdentifier1
   */
  public void setSubjectIdentifier(String subjectIdentifier1) {
    this.subjectIdentifier = subjectIdentifier1;
  }



  /**
   * subject source id
   */
  private String sourceId;
  
  /**
   * subject source id
   * @return subject source id
   */
  public String getSourceId() {
    return this.sourceId;
  }

  /**
   * subject source id
   * @param sourceId1
   */
  public void setSourceId(String sourceId1) {
    this.sourceId = sourceId1;
  }

  /**
   * when metadata was last updated
   */
  private Timestamp metadataUpdated;
  
  
  
  /**
   * when metadata was last updated
   * @return
   */
  public Timestamp getMetadataUpdated() {
    return this.metadataUpdated;
  }

  /**
   * when metadata was last updated
   * @param metadataUpdated1
   */
  public void setMetadataUpdated(Timestamp metadataUpdated1) {
    this.metadataUpdated = metadataUpdated1;
  }

  /**
   * subject id
   */
  private String subjectId;

  /**
   * subject id
   * @return subject id
   */
  public String getSubjectId() {
    return this.subjectId;
  }

  /**
   * subject id
   * @param subjectId1
   */
  public void setSubjectId(String subjectId1) {
    this.subjectId = subjectId1;
  }

  /**
   * call this before storing
   */
  public void storePrepare() {
    this.lastUpdated = new Timestamp(System.currentTimeMillis());
    this.connectionName = GcGrouperSync.defaultConnectionName(this.connectionName);
    this.errorMessage = GrouperClientUtils.abbreviate(this.errorMessage, 3700);
  }

  /**
   * 
   */
  @GcPersistableField(persist=GcPersist.dontPersist)
  private GcGrouperSync grouperSync;
  
  /**
   * 
   * @return gc grouper sync
   */
  public GcGrouperSync getGrouperSync() {
    return this.grouperSync;
  }
  
  /**
   * 
   * @param gcGrouperSync
   */
  public void setGrouperSync(GcGrouperSync gcGrouperSync) {
    this.grouperSync = gcGrouperSync;
    this.grouperSyncId = gcGrouperSync == null ? null : gcGrouperSync.getId();
  }
  
  /**
   * connection name or null for default
   */
  @GcPersistableField(persist=GcPersist.dontPersist)
  private String connectionName;

  /**
   * connection name or null for default
   * @return connection name
   */
  public String getConnectionName() {
    return this.connectionName;
  }

  /**
   * connection name or null for default
   * @param connectionName1
   */
  public void setConnectionName(String connectionName1) {
    this.connectionName = connectionName1;
  }

  /**
   * 
   * @param args
   */
  public static void main(String[] args) {
    
    System.out.println("none");
    
    for (GcGrouperSyncGroup theGcGrouperSyncGroup : new GcDbAccess().connectionName("grouper").selectList(GcGrouperSyncGroup.class)) {
      System.out.println(theGcGrouperSyncGroup.toString());
    }
    
    // foreign key
    GcGrouperSync gcGrouperSync = new GcGrouperSync();
    gcGrouperSync.setSyncEngine("temp");
    gcGrouperSync.setProvisionerName("myJob");
    gcGrouperSync.getGcGrouperSyncDao().store();
    
    GcGrouperSyncMember gcGrouperSyncMember = new GcGrouperSyncMember();
    gcGrouperSyncMember.setGrouperSync(gcGrouperSync);
    gcGrouperSyncMember.setLastTimeWorkWasDone(new Timestamp(System.currentTimeMillis() + 2000));
    gcGrouperSyncMember.memberId = "memId";
    gcGrouperSyncMember.sourceId = "sourceId";
    gcGrouperSyncMember.subjectId = "subjectId";
    gcGrouperSyncMember.subjectIdentifier = "subjectIdentifier";
    gcGrouperSyncMember.entityAttributeValueCache0 = "from2";
    gcGrouperSyncMember.entityAttributeValueCache1 = "from3";
    gcGrouperSyncMember.entityAttributeValueCache2 = "toId2";
    gcGrouperSyncMember.entityAttributeValueCache3 = "toId3";
    gcGrouperSyncMember.inTargetDb = "T";
    gcGrouperSyncMember.inTargetInsertOrExistsDb = "T";
    gcGrouperSyncMember.inTargetEnd = new Timestamp(123L);
    gcGrouperSyncMember.inTargetStart = new Timestamp(234L);
    gcGrouperSyncMember.lastTimeWorkWasDone = new Timestamp(345L);
    gcGrouperSyncMember.provisionableDb = "T";
    gcGrouperSyncMember.provisionableEnd = new Timestamp(456L);
    gcGrouperSyncMember.provisionableStart = new Timestamp(567L);
    gcGrouperSync.getGcGrouperSyncMemberDao().internal_memberStore(gcGrouperSyncMember);
    
    System.out.println("stored");
    
    gcGrouperSyncMember = gcGrouperSync.getGcGrouperSyncMemberDao().memberRetrieveByMemberId("memId");
    System.out.println(gcGrouperSyncMember);
    
    gcGrouperSyncMember.setEntityAttributeValueCache2("from2a");
    gcGrouperSync.getGcGrouperSyncMemberDao().internal_memberStore(gcGrouperSyncMember);

    System.out.println("updated");

    for (GcGrouperSyncMember theGcGrouperSyncMember : new GcDbAccess().connectionName("grouper").selectList(GcGrouperSyncMember.class)) {
      System.out.println(theGcGrouperSyncMember.toString());
    }

    gcGrouperSync.getGcGrouperSyncMemberDao().memberDelete(gcGrouperSyncMember, false, false);
    gcGrouperSync.getGcGrouperSyncDao().delete();
    
    System.out.println("deleted");

    for (GcGrouperSyncGroup theGcGrouperSyncStatus : new GcDbAccess().connectionName("grouper").selectList(GcGrouperSyncGroup.class)) {
      System.out.println(theGcGrouperSyncStatus.toString());
    }
  }
  
  private static Set<String> toStringFieldNamesToIgnore = GrouperClientUtils.toSet("connectionName", 
      "dbVersion", "errorMessage", "grouperSync", "grouperSyncId", "lastUpdated", "lastUserMetadataSync", "lastUserSync", "metadataUpdated");
  
  /**
   * 
   */
  @Override
  public String toString() {
    return GrouperClientUtils.toStringReflection(this, toStringFieldNamesToIgnore);
  }

  /**
   * last time a record was processed
   */
  private Timestamp lastTimeWorkWasDone;
  
  /**
   * last time a record was processe
   * @return last time a record was processed
   */
  public Timestamp getLastTimeWorkWasDone() {
    return this.lastTimeWorkWasDone;
  }

  /**
   * last time a record was processe
   * @param lastTimeWorkWasDone1
   */
  public void setLastTimeWorkWasDone(Timestamp lastTimeWorkWasDone1) {
    this.lastTimeWorkWasDone = lastTimeWorkWasDone1;
  }

  /**
   * if the last sync had an error, this is the error message
   */
  private String errorMessage; 

  /**
   * this the last sync had an error, this was the error timestamp
   */
  private Timestamp errorTimestamp;
  
  /**
   * if the last sync had an error, this is the error message
   * @return error message
   */
  public String getErrorMessage() {
    return this.errorMessage;
  }

  /**
   * if the last sync had an error, this is the error message
   * @param errorMessage1
   */
  public void setErrorMessage(String errorMessage1) {
    this.errorMessage = errorMessage1;
  }

  /**
   * this the last sync had an error, this was the error timestamp
   * @return error timestamp
   */
  public Timestamp getErrorTimestamp() {
    return this.errorTimestamp;
  }

  /**
   * this the last sync had an error, this was the error timestamp
   * @param errorTimestamp1
   */
  public void setErrorTimestamp(Timestamp errorTimestamp1) {
    this.errorTimestamp = errorTimestamp1;
  }


  /**
   * 
   */
  public GcGrouperSyncMember() {
  }
  
  /**
   * uuid of this record in this table
   */
  @GcPersistableField(primaryKey=true, primaryKeyManuallyAssigned=false)
  private String id;

  
  /**
   * uuid of this record in this table
   * @return the id
   */
  public String getId() {
    return this.id;
  }

  
  /**
   * uuid of this record in this table
   * @param id1 the id to set
   */
  public void setId(String id1) {
    this.id = id1;
  }

  /**
   * T if inserted on the in_target_start date, or F if it existed then and not sure when inserted
   */
  @GcPersistableField(columnName="in_target_insert_or_exists")
  private String inTargetInsertOrExistsDb;

  /**
   * T if inserted on the in_target_start date, or F if it existed then and not sure when inserted
   * @return true or false
   */
  public String getInTargetInsertOrExistsDb() {
    return this.inTargetInsertOrExistsDb;
  }

  /**
   * T if inserted on the in_target_start date, or F if it existed then and not sure when inserted
   * @param inTargetInsertOrExistsDb1
   */
  public void setInTargetInsertOrExistsDb(String inTargetInsertOrExistsDb1) {
    this.inTargetInsertOrExistsDb = inTargetInsertOrExistsDb1;
  }

  /**
   * T if inserted on the in_target_start date, or F if it existed then and not sure when inserted
   * @return true or false
   */
  public boolean isInTargetInsertOrExists() {
    return GrouperClientUtils.booleanValue(this.inTargetInsertOrExistsDb, false);
  }
  
  /**
   * T if inserted on the in_target_start date, or F if it existed then and not sure when inserted
   * @param inTargetInsertOrExists
   */
  public void setInTargetInsertOrExists(boolean inTargetInsertOrExists) {
    this.inTargetInsertOrExistsDb = inTargetInsertOrExists ? "T" : "F";
  }

  /**
   * if this group exists in the target/destination
   * T means in target, F means not in target, and null means not applicable (not selecting or inserting)
   */
  @GcPersistableField(columnName="in_target")
  private String inTargetDb;
  
  /**
   * if this group exists in the target/destination
   * T means in target, F means not in target, and null means not applicable (not selecting or inserting)
   * @return if in target
   */
  public String getInTargetDb() {
    return this.inTargetDb;
  }

  /**
   * if this group exists in the target/destination
   * T means in target, F means not in target, and null means not applicable (not selecting or inserting)
   * @param inTargetDb1
   */
  public void setInTargetDb(String inTargetDb1) {
    this.inTargetDb = inTargetDb1;
  }

  /**
   * if in target
   * true means in target, false means not in target, null means unsure (not selecting or inserting)
   * @param in target
   */
  public void setInTarget(Boolean inTarget) {
    this.inTargetDb = inTarget == null ? null : (inTarget ? "T" : "F");
  }
  

  /**
   * if this group exists in the target/destination
   * true means in target, false means not in target, null means unsure (not selecting or inserting)
   * @return if is target
   */
  public Boolean getInTarget() {
    return GrouperClientUtils.booleanObjectValue(this.inTargetDb);
  }

  /**
   * uuid of the job in grouper_sync
   */
  private String grouperSyncId;
  
  /**
   * uuid of the job in grouper_sync
   * @return uuid of the job in grouper_sync
   */ 
  public String getGrouperSyncId() {
    return this.grouperSyncId;
  }

  /**
   * uuid of the job in grouper_sync
   * @param grouperSyncId1
   */
  public void setGrouperSyncId(String grouperSyncId1) {
    this.grouperSyncId = grouperSyncId1;
    if (this.grouperSync == null || !GrouperClientUtils.equals(this.grouperSync.getId(), grouperSyncId1)) {
      this.grouperSync = null;
    }
  }

  /**
   * when this record was last updated
   */
  private Timestamp lastUpdated;
  
  /**
   * when this record was last updated
   * @return the lastUpdated
   */
  public Timestamp getLastUpdated() {
    return this.lastUpdated;
  }

  /**
   * when this record was last updated
   * @param lastUpdated1
   */
  public void setLastUpdated(Timestamp lastUpdated1) {
    this.lastUpdated = lastUpdated1;
  }

  /**
   * T if provisionable and F is not
   */
  @GcPersistableField(columnName="provisionable")
  private String provisionableDb;
  
  /**
   * T if provisionable and F is not
   * @return if provisionable
   */
  public String getProvisionableDb() {
    return this.provisionableDb;
  }

  /**
   * T if provisionable and F is not
   * @param provisionableDb1
   */
  public void setProvisionableDb(String provisionableDb1) {
    this.provisionableDb = provisionableDb1;
  }

  /**
   * if provisionable
   * @return if provisionable
   */
  public boolean isProvisionable() {
    return GrouperClientUtils.booleanValue(this.provisionableDb, false);
  }

  /**
   * if provisionable
   * @param provisionable
   */
  public void setProvisionable(boolean provisionable) {
    this.provisionableDb = provisionable ? "T" : "F";
  }
  
  /**
   * millis since 1970 that this group ended being provisionable
   */
  private Timestamp provisionableEnd;

  /**
   * millis since 1970 that this group ended being provisionable
   * @return millis
   */
  public Timestamp getProvisionableEnd() {
    return this.provisionableEnd;
  }

  /**
   * millis since 1970 that this group ended being provisionable
   * @param provisionableEndMillis1
   */
  public void setProvisionableEnd(Timestamp provisionableEndMillis1) {
    this.provisionableEnd = provisionableEndMillis1;
  }

  /**
   * millis since 1970 that this group started to be provisionable
   */
  private Timestamp provisionableStart;
    
  /**
   * millis since 1970 that this group started to be provisionable
   * @return millis
   */
  public Timestamp getProvisionableStart() {
    return this.provisionableStart;
  }

  /**
   * millis since 1970 that this group started to be provisionable
   * @param provisionableStartMillis1
   */
  public void setProvisionableStart(Timestamp provisionableStartMillis1) {
    this.provisionableStart = provisionableStartMillis1;
  }

  /**
   * for users this is the group idIndex
   */
  @GcPersistableField(columnName = "member_from_id2")
  private String entityAttributeValueCache0;

  /**
   * for users this is the group idIndex
   * @return group from id 2
   */
  public String getEntityAttributeValueCache0() {
    return this.entityAttributeValueCache0;
  }

  /**
   * for users this is the group idIndex
   * @param groupAttributeValueCache0_1
   */
  public void setEntityAttributeValueCache0(String groupAttributeValueCache0_1) {
    this.entityAttributeValueCache0 = groupAttributeValueCache0_1;
  }

  /**
   * other metadata on users
   */
  @GcPersistableField(columnName = "member_from_id3")
  private String entityAttributeValueCache1;

  /**
   * other metadata on users
   * @return id3
   */
  public String getEntityAttributeValueCache1() {
    return this.entityAttributeValueCache1;
  }

  /**
   * other metadata on users
   * @param groupAttributeValueCache1_1
   */
  public void setEntityAttributeValueCache1(String groupAttributeValueCache1_1) {
    this.entityAttributeValueCache1 = groupAttributeValueCache1_1;
  }

  /**
   * other metadata on users
   */
  @GcPersistableField(columnName = "member_to_id2")
  private String entityAttributeValueCache2;
  
  /**
   * other metadata on users
   * @return metadata
   */
  public String getEntityAttributeValueCache2() {
    return this.entityAttributeValueCache2;
  }

  /**
   * other metadata on users
   * @param groupAttributeValueCache2_1
   */
  public void setEntityAttributeValueCache2(String groupAttributeValueCache2_1) {
    this.entityAttributeValueCache2 = groupAttributeValueCache2_1;
  }

  /**
   * other metadata on users
   */
  @GcPersistableField(columnName = "member_to_id3")
  private String entityAttributeValueCache3;
  
  /**
   * when this group was removed from target
   */
  private Timestamp inTargetEnd;
  
  /**
   * when this group was provisioned to target
   */
  private Timestamp inTargetStart;

  /**
   * other metadata on users
   * @return group id
   */
  public String getEntityAttributeValueCache3() {
    return this.entityAttributeValueCache3;
  }

  /**
   * other metadata on users
   * @param groupAttributeValueCache3_1
   */
  public void setEntityAttributeValueCache3(String groupAttributeValueCache3_1) {
    this.entityAttributeValueCache3 = groupAttributeValueCache3_1;
  }

  /**
   * 
   */
  @Override
  public boolean gcSqlAssignNewPrimaryKeyForInsert() {
    if (this.id != null) {
      return false;
    }
    this.id = GrouperClientUtils.uuid();
    return true;
  }

  /**
   * when this group was provisioned to target
   * @return when
   */
  public Timestamp getInTargetEnd() {
    return this.inTargetEnd;
  }

  /**
   * when this group was provisioned to target
   * @return when
   */
  public Timestamp getInTargetStart() {
    return this.inTargetStart;
  }

  /**
   * when this group was provisioned to target
   * @param inTargetEnd1
   */
  public void setInTargetEnd(Timestamp inTargetEnd1) {
    this.inTargetEnd = inTargetEnd1;
  }

  /**
   * when this group was provisioned to target
   * @param inTargetStart1
   */
  public void setInTargetStart(Timestamp inTargetStart1) {
    this.inTargetStart = inTargetStart1;
  }

  /**
   * additional metadata for group
   */
  private String metadataJson;
  

  /**
   * additional metadata for group
   * @return metadataJson
   */
  public String getMetadataJson() {
    return metadataJson;
  }

  
  /**
   * additional metadata for group
   * @param metadataJson
   */
  public void setMetadataJson(String metadataJson) {
    this.metadataJson = metadataJson;
  }

  /**
   * 
   * @param translateGrouperToGroupSyncField
   * @param result
   */
  public void assignField(String syncField, Object result) {
    if (GrouperClientUtils.equals("entityAttributeValueCache0", syncField)) {
      this.setEntityAttributeValueCache0(GrouperClientUtils.stringValue(result));
    } else if (GrouperClientUtils.equals("entityAttributeValueCache1", syncField)) {
      this.setEntityAttributeValueCache1(GrouperClientUtils.stringValue(result));
    } else if (GrouperClientUtils.equals("entityAttributeValueCache2", syncField)) {
      this.setEntityAttributeValueCache2(GrouperClientUtils.stringValue(result));
    } else if (GrouperClientUtils.equals("entityAttributeValueCache3", syncField)) {
      this.setEntityAttributeValueCache3(GrouperClientUtils.stringValue(result));
    } else {
      throw new RuntimeException("Not expecting groupSyncField: '" + syncField + "'");
    }

  }

  /**
   * 
   * @param translateGrouperToGroupSyncField
   * @param result
   */
  public String retrieveField(String syncField) {
    if (GrouperClientUtils.equals("entityAttributeValueCache0", syncField)) {
      return this.getEntityAttributeValueCache0();
    } else if (GrouperClientUtils.equals("entityAttributeValueCache1", syncField)) {
      return this.getEntityAttributeValueCache1();
    } else if (GrouperClientUtils.equals("entityAttributeValueCache2", syncField)) {
      return this.getEntityAttributeValueCache2();
    } else if (GrouperClientUtils.equals("entityAttributeValueCache3", syncField)) {
      return this.getEntityAttributeValueCache3();
    } else {
      throw new RuntimeException("Not expecting memberSyncField: '" + syncField + "'");
    }

  }

}
