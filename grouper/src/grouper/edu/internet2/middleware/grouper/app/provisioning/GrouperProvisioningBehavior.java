package edu.internet2.middleware.grouper.app.provisioning;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

import edu.internet2.middleware.grouper.util.GrouperUtil;
import edu.internet2.middleware.grouperClient.jdbc.tableSync.GcGrouperSyncGroup;
import edu.internet2.middleware.grouperClient.jdbc.tableSync.GcGrouperSyncMember;
import edu.internet2.middleware.grouperClient.jdbc.tableSync.GcGrouperSyncMembership;

/**
 * how this provisioner interacts with the target.
 * some of these things default to the common configuration
 * @author mchyzer-local
 *
 */
public class GrouperProvisioningBehavior {
  
  private boolean createGroupsAndEntitiesBeforeTranslatingMemberships = true;

  /**
   * if set then only provision users who are in this group
   */
  private String groupIdOfUsersToProvision;
  
  /**
   * if set then only provision users who are in this group
   * @return group id
   */
  public String getGroupIdOfUsersToProvision() {
    return groupIdOfUsersToProvision;
  }


  /**
   * if set then only provision users who are in this group
   * @param groupIdOfUsersToProvision
   */
  public void setGroupIdOfUsersToProvision(String groupIdOfUsersToProvision) {
    this.groupIdOfUsersToProvision = groupIdOfUsersToProvision;
  }


  /**
   * Only provision policy groups
   */
  private Boolean onlyProvisionPolicyGroups;
  
  /**
   * Only provision policy groups
   * @return
   */
  public boolean isOnlyProvisionPolicyGroups() {
    if (this.onlyProvisionPolicyGroups != null) {
      return this.onlyProvisionPolicyGroups;
    }
    return this.grouperProvisioner.retrieveGrouperProvisioningConfiguration().isOnlyProvisionPolicyGroups();
  }
  
  /**
   * If you want a metadata item on folders for specifying if provision only policy groups
   */
  private Boolean allowPolicyGroupOverride;
  
  /**
   * If you want a metadata item on folders for specifying if provision only policy groups
   * @return
   */
  public boolean isAllowPolicyGroupOverride() {
    if (this.allowPolicyGroupOverride != null) {
      return this.allowPolicyGroupOverride;
    }
    return this.grouperProvisioner.retrieveGrouperProvisioningConfiguration().isAllowPolicyGroupOverride();
  }
  
  /**
   * If you want to filter for groups in a provisionable folder by a regex on its name, specify here.  If the regex matches then the group in the folder is provisionable.  e.g. folderExtension matches ^.*_someExtension   folderName matches ^.*_someExtension   groupExtension matches ^.*_someExtension   groupName matches ^.*_someExtension$
   */
  private String provisionableRegex;

  /**
   * If you want to filter for groups in a provisionable folder by a regex on its name, specify here.  If the regex matches then the group in the folder is provisionable.  e.g. folderExtension matches ^.*_someExtension   folderName matches ^.*_someExtension   groupExtension matches ^.*_someExtension   groupName matches ^.*_someExtension$
   * @return
   */
  public String getProvisionableRegex() {
    if (this.provisionableRegex != null) {
      return this.provisionableRegex;
    }
    return this.grouperProvisioner.retrieveGrouperProvisioningConfiguration().getProvisionableRegex();
  }
  
  /**
   * If you want a metadata item on folders for specifying regex of names of objects to provision
   */
  private Boolean allowProvisionableRegexOverride;
  
  /**
   * If you want a metadata item on folders for specifying regex of names of objects to provision
   * @return
   */
  public boolean isAllowProvisionableRegexOverride() {
    if (this.allowProvisionableRegexOverride != null) {
      return this.allowProvisionableRegexOverride;
    }
    return this.grouperProvisioner.retrieveGrouperProvisioningConfiguration().isAllowProvisionableRegexOverride();
  }

  
  
//  # Only provision policy groups
//  # {valueType: "boolean", order: 86700, defaultValue: "false", subSection: "assigningProvisioning"}
//  # provisioner.genericProvisioner.onlyProvisionPolicyGroups =
//
//  # If you want a metadata item on folders for specifying if provision only policy groups
//  # {valueType: "boolean", order: 86750, defaultValue: "true", subSection: "assigningProvisioning"}
//  # provisioner.genericProvisioner.allowPolicyGroupOverride =
//
//  # If you want to filter for groups in a provisionable folder by a regex on its name, specify here.  If the regex matches then the group in the folder is provisionable.  e.g. folderExtension matches ^.*_someExtension   folderName matches ^.*_someExtension   groupExtension matches ^.*_someExtension   groupName matches ^.*_someExtension$
//  # {valueType: "boolean", order: 86775, subSection: "assigningProvisioning"}
//  # provisioner.genericProvisioner.provisionableRegex =
//
//  # If you want a metadata item on folders for specifying regex of names of objects to provision
//  # {valueType: "boolean", order: 86800, subSection: "assigningProvisioning"}
//  # provisioner.genericProvisioner. =

  
  /**
   * set to true if blank (empty) valued attributes on target should be treated differently than attribute not being assigned on target
   */
  private boolean deleteBlankAttributesFromTarget = false;

  /**
   * set to true if blank (empty) valued attributes on target should be treated differently than attribute not being assigned on target
   * @return
   */
  public boolean isDeleteBlankAttributesFromTarget() {
    return deleteBlankAttributesFromTarget;
  }

  /**
   * set to true if blank (empty) valued attributes on target should be treated differently than attribute not being assigned on target
   * @param deleteBlankAttributesFromTarget1
   */
  public void setDeleteBlankAttributesFromTarget(boolean deleteBlankAttributesFromTarget1) {
    this.deleteBlankAttributesFromTarget = deleteBlankAttributesFromTarget1;
  }

  /**
   * If the subject API is needed to resolve attribute on subject  required, drives requirements of other configurations. defaults to false.
   */
  private Boolean hasSubjectLink = null;
  
  /**
   * If groups need to be resolved in the target before provisioning
   */
  private Boolean hasTargetGroupLink = null;
  
  /**
   * If users need to be resolved in the target before provisioning
   */
  private Boolean hasTargetEntityLink = null;
  
  public boolean canInsertGroupAttribute(String name) {
    GrouperProvisioningConfigurationAttribute grouperProvisioningConfigurationAttribute = 
        this.grouperProvisioner.retrieveGrouperProvisioningConfiguration().getTargetGroupAttributeNameToConfig().get(name);
    if (grouperProvisioningConfigurationAttribute != null && grouperProvisioningConfigurationAttribute.isInsert()) {
      return true;
    }
    if (this.getGrouperProvisioningBehaviorMembershipType() == GrouperProvisioningBehaviorMembershipType.groupAttributes
        && StringUtils.equals(name, this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().getGroupAttributeNameForMemberships())) {
      return this.isInsertMemberships();
    }
    return false;
  }
  
  public boolean canUpdateGroupAttribute(String name) {
    GrouperProvisioningConfigurationAttribute grouperProvisioningConfigurationAttribute = 
        this.grouperProvisioner.retrieveGrouperProvisioningConfiguration().getTargetGroupAttributeNameToConfig().get(name);
    if (grouperProvisioningConfigurationAttribute != null && grouperProvisioningConfigurationAttribute.isUpdate()) {
      return true;
    }
    if (this.getGrouperProvisioningBehaviorMembershipType() == GrouperProvisioningBehaviorMembershipType.groupAttributes
        && StringUtils.equals(name, this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().getGroupAttributeNameForMemberships())) {
      return this.isUpdateMemberships();
    }
    return false;
  }

  public boolean canInsertEntityAttribute(String name) {
    
    GrouperProvisioningConfigurationAttribute grouperProvisioningConfigurationAttribute = 
        this.grouperProvisioner.retrieveGrouperProvisioningConfiguration().getTargetEntityAttributeNameToConfig().get(name);
    if (grouperProvisioningConfigurationAttribute != null && grouperProvisioningConfigurationAttribute.isInsert()) {
      return true;
    }
    
    if (this.getGrouperProvisioningBehaviorMembershipType() == GrouperProvisioningBehaviorMembershipType.entityAttributes
        && StringUtils.equals(name, this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().getEntityAttributeNameForMemberships())) {
      return this.isInsertMemberships();
    }
    return false;
    
  }
  public boolean canUpdateEntityAttribute(String name) {
    GrouperProvisioningConfigurationAttribute grouperProvisioningConfigurationAttribute = 
        this.grouperProvisioner.retrieveGrouperProvisioningConfiguration().getTargetEntityAttributeNameToConfig().get(name);
    if (grouperProvisioningConfigurationAttribute != null && grouperProvisioningConfigurationAttribute.isUpdate()) {
      return true;
    }
    if (this.getGrouperProvisioningBehaviorMembershipType() == GrouperProvisioningBehaviorMembershipType.entityAttributes
        && StringUtils.equals(name, this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().getEntityAttributeNameForMemberships())) {
      return this.isUpdateMemberships();
    }
    return false;
  }

  public boolean canInsertMembershipAttribute(String name) {
    GrouperProvisioningConfigurationAttribute grouperProvisioningConfigurationAttribute = 
        this.grouperProvisioner.retrieveGrouperProvisioningConfiguration().getTargetMembershipAttributeNameToConfig().get(name);
    return grouperProvisioningConfigurationAttribute != null && grouperProvisioningConfigurationAttribute.isInsert();
  }
  public boolean canUpdateMembershipAttribute(String name) {
    GrouperProvisioningConfigurationAttribute grouperProvisioningConfigurationAttribute = 
        this.grouperProvisioner.retrieveGrouperProvisioningConfiguration().getTargetMembershipAttributeNameToConfig().get(name);
    return grouperProvisioningConfigurationAttribute != null && grouperProvisioningConfigurationAttribute.isUpdate();
  }

  
  public boolean isHasTargetEntityLink() {
    if (hasTargetEntityLink != null) {
      return hasTargetEntityLink;
    }
    if (this.grouperProvisioner.retrieveGrouperProvisioningConfiguration().isHasTargetEntityLink()) {
      return true;
    }
    for (GrouperProvisioningConfigurationAttribute grouperProvisioningConfigurationAttribute : 
        this.grouperProvisioner.retrieveGrouperProvisioningConfiguration().getTargetEntityAttributeNameToConfig().values()) {
      if (!StringUtils.isBlank(grouperProvisioningConfigurationAttribute.getTranslateToMemberSyncField())) {
        return true;
      }
    }
    for (GrouperProvisioningConfigurationAttribute grouperProvisioningConfigurationAttribute : 
      this.grouperProvisioner.retrieveGrouperProvisioningConfiguration().getTargetEntityAttributeNameToConfig().values()) {
      if (!StringUtils.isBlank(grouperProvisioningConfigurationAttribute.getTranslateToMemberSyncField())) {
        return true;
      }
    }
    
    return false;
  }



  
  public void setHasTargetEntityLink(Boolean hasTargetEntityLink) {
    this.hasTargetEntityLink = hasTargetEntityLink;
  }



  public boolean isHasSubjectLink() {
    if (hasSubjectLink != null) {
      return hasSubjectLink;
    }
    return this.grouperProvisioner.retrieveGrouperProvisioningConfiguration().isHasSubjectLink();
  }


  
  public void setHasSubjectLink(Boolean hasSubjectLink) {
    this.hasSubjectLink = hasSubjectLink;
  }

  /**
   * 
   * @return true if has script or an attribute mapped
   */
  public boolean isHasGroupLinkGroupFromId2() {
    
    if (StringUtils.isNotBlank(this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().getGroupLinkGroupFromId2())) {
      return true;
    }

    if (null != getGroupLinkGroupFromId2Attribute()) {
      return true;
    }
    
    return false;
    
  }
  
  public GrouperProvisioningConfigurationAttribute getEntityLinkMemberToId2Attribute() {
    for (GrouperProvisioningConfigurationAttribute grouperProvisioningConfigurationAttribute : 
      this.grouperProvisioner.retrieveGrouperProvisioningConfiguration().getTargetEntityAttributeNameToConfig().values()) {
      if (StringUtils.equals("memberToId2", grouperProvisioningConfigurationAttribute.getTranslateToMemberSyncField())) {
        return grouperProvisioningConfigurationAttribute;
      }
    }
    for (GrouperProvisioningConfigurationAttribute grouperProvisioningConfigurationAttribute : 
      this.grouperProvisioner.retrieveGrouperProvisioningConfiguration().getTargetEntityAttributeNameToConfig().values()) {
      if (StringUtils.equals("memberToId2", grouperProvisioningConfigurationAttribute.getTranslateToMemberSyncField())) {
        return grouperProvisioningConfigurationAttribute;
      }
    }
    return null;
  }
  
  public GrouperProvisioningConfigurationAttribute getEntityLinkMemberFromId2Attribute() {
    for (GrouperProvisioningConfigurationAttribute grouperProvisioningConfigurationAttribute : 
      this.grouperProvisioner.retrieveGrouperProvisioningConfiguration().getTargetEntityAttributeNameToConfig().values()) {
      if (StringUtils.equals("memberFromId2", grouperProvisioningConfigurationAttribute.getTranslateToMemberSyncField())) {
        return grouperProvisioningConfigurationAttribute;
      }
    }
    for (GrouperProvisioningConfigurationAttribute grouperProvisioningConfigurationAttribute : 
      this.grouperProvisioner.retrieveGrouperProvisioningConfiguration().getTargetEntityAttributeNameToConfig().values()) {
      if (StringUtils.equals("memberFromId2", grouperProvisioningConfigurationAttribute.getTranslateToMemberSyncField())) {
        return grouperProvisioningConfigurationAttribute;
      }
    }
    return null;
  }
  
  public GrouperProvisioningConfigurationAttribute getEntityLinkMemberFromId3Attribute() {
    for (GrouperProvisioningConfigurationAttribute grouperProvisioningConfigurationAttribute : 
      this.grouperProvisioner.retrieveGrouperProvisioningConfiguration().getTargetEntityAttributeNameToConfig().values()) {
      if (StringUtils.equals("memberFromId3", grouperProvisioningConfigurationAttribute.getTranslateToMemberSyncField())) {
        return grouperProvisioningConfigurationAttribute;
      }
    }
    for (GrouperProvisioningConfigurationAttribute grouperProvisioningConfigurationAttribute : 
      this.grouperProvisioner.retrieveGrouperProvisioningConfiguration().getTargetEntityAttributeNameToConfig().values()) {
      if (StringUtils.equals("memberFromId3", grouperProvisioningConfigurationAttribute.getTranslateToMemberSyncField())) {
        return grouperProvisioningConfigurationAttribute;
      }
    }
    return null;
  }
  
  public GrouperProvisioningConfigurationAttribute getEntityLinkMemberToId3Attribute() {
    for (GrouperProvisioningConfigurationAttribute grouperProvisioningConfigurationAttribute : 
      this.grouperProvisioner.retrieveGrouperProvisioningConfiguration().getTargetEntityAttributeNameToConfig().values()) {
      if (StringUtils.equals("memberToId3", grouperProvisioningConfigurationAttribute.getTranslateToMemberSyncField())) {
        return grouperProvisioningConfigurationAttribute;
      }
    }
    for (GrouperProvisioningConfigurationAttribute grouperProvisioningConfigurationAttribute : 
      this.grouperProvisioner.retrieveGrouperProvisioningConfiguration().getTargetEntityAttributeNameToConfig().values()) {
      if (StringUtils.equals("memberToId3", grouperProvisioningConfigurationAttribute.getTranslateToMemberSyncField())) {
        return grouperProvisioningConfigurationAttribute;
      }
    }
    return null;
  }
  
  public GrouperProvisioningConfigurationAttribute getGroupLinkGroupToId2Attribute() {
    for (GrouperProvisioningConfigurationAttribute grouperProvisioningConfigurationAttribute : 
      this.grouperProvisioner.retrieveGrouperProvisioningConfiguration().getTargetGroupAttributeNameToConfig().values()) {
      if (StringUtils.equals("groupToId2", grouperProvisioningConfigurationAttribute.getTranslateToGroupSyncField())) {
        return grouperProvisioningConfigurationAttribute;
      }
    }
    for (GrouperProvisioningConfigurationAttribute grouperProvisioningConfigurationAttribute : 
      this.grouperProvisioner.retrieveGrouperProvisioningConfiguration().getTargetGroupAttributeNameToConfig().values()) {
      if (StringUtils.equals("groupToId2", grouperProvisioningConfigurationAttribute.getTranslateToGroupSyncField())) {
        return grouperProvisioningConfigurationAttribute;
      }
    }
    return null;
  }
  
  public GrouperProvisioningConfigurationAttribute getGroupLinkGroupToId3Attribute() {
    for (GrouperProvisioningConfigurationAttribute grouperProvisioningConfigurationAttribute : 
      this.grouperProvisioner.retrieveGrouperProvisioningConfiguration().getTargetGroupAttributeNameToConfig().values()) {
      if (StringUtils.equals("groupToId3", grouperProvisioningConfigurationAttribute.getTranslateToGroupSyncField())) {
        return grouperProvisioningConfigurationAttribute;
      }
    }
    for (GrouperProvisioningConfigurationAttribute grouperProvisioningConfigurationAttribute : 
      this.grouperProvisioner.retrieveGrouperProvisioningConfiguration().getTargetGroupAttributeNameToConfig().values()) {
      if (StringUtils.equals("groupToId3", grouperProvisioningConfigurationAttribute.getTranslateToGroupSyncField())) {
        return grouperProvisioningConfigurationAttribute;
      }
    }
    return null;
  }

  public GrouperProvisioningConfigurationAttribute getGroupLinkGroupFromId2Attribute() {
    for (GrouperProvisioningConfigurationAttribute grouperProvisioningConfigurationAttribute : 
      this.grouperProvisioner.retrieveGrouperProvisioningConfiguration().getTargetGroupAttributeNameToConfig().values()) {
      if (StringUtils.equals("groupFromId2", grouperProvisioningConfigurationAttribute.getTranslateToGroupSyncField())) {
        return grouperProvisioningConfigurationAttribute;
      }
    }
    for (GrouperProvisioningConfigurationAttribute grouperProvisioningConfigurationAttribute : 
      this.grouperProvisioner.retrieveGrouperProvisioningConfiguration().getTargetGroupAttributeNameToConfig().values()) {
      if (StringUtils.equals("groupFromId2", grouperProvisioningConfigurationAttribute.getTranslateToGroupSyncField())) {
        return grouperProvisioningConfigurationAttribute;
      }
    }
    return null;
  }

  public GrouperProvisioningConfigurationAttribute getGroupLinkGroupFromId3Attribute() {
    for (GrouperProvisioningConfigurationAttribute grouperProvisioningConfigurationAttribute : 
      this.grouperProvisioner.retrieveGrouperProvisioningConfiguration().getTargetGroupAttributeNameToConfig().values()) {
      if (StringUtils.equals("groupFromId3", grouperProvisioningConfigurationAttribute.getTranslateToGroupSyncField())) {
        return grouperProvisioningConfigurationAttribute;
      }
    }
    for (GrouperProvisioningConfigurationAttribute grouperProvisioningConfigurationAttribute : 
      this.grouperProvisioner.retrieveGrouperProvisioningConfiguration().getTargetGroupAttributeNameToConfig().values()) {
      if (StringUtils.equals("groupFromId3", grouperProvisioningConfigurationAttribute.getTranslateToGroupSyncField())) {
        return grouperProvisioningConfigurationAttribute;
      }
    }
    return null;
  }

  /**
   * 
   * @return true if has script or an attribute mapped
   */
  public boolean isHasGroupLinkGroupFromId3() {
    
    if (StringUtils.isNotBlank(this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().getGroupLinkGroupFromId3())) {
      return true;
    }

    if (null != getGroupLinkGroupFromId3Attribute()) {
      return true;
    }
    
    return false;
    
  }

  /**
   * 
   * @return true if has script or an attribute mapped
   */
  public boolean isHasGroupLinkGroupToId2() {
    
    if (StringUtils.isNotBlank(this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().getGroupLinkGroupToId2())) {
      return true;
    }

    if (null != getGroupLinkGroupToId2Attribute()) {
      return true;
    }
    
    return false;
    
  }
  

  /**
   * 
   * @return true if has script or an attribute mapped
   */
  public boolean isHasGroupLinkGroupToId3() {
    
    if (StringUtils.isNotBlank(this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().getGroupLinkGroupToId3())) {
      return true;
    }

    if (null != getGroupLinkGroupToId3Attribute()) {
      return true;
    }
    
    return false;
    
  }


  /**
   * 
   * @return true if has script or an attribute mapped
   */
  public boolean isHasEntityLinkMemberToId2() {
    
    if (StringUtils.isNotBlank(this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().getEntityLinkMemberToId2())) {
      return true;
    }

    if (null != getEntityLinkMemberToId2Attribute()) {
      return true;
    }
    
    return false;
    
  }

  /**
   * 
   * @return true if has script or an attribute mapped
   */
  public boolean isHasEntityLinkMemberFromId2() {
    
    if (StringUtils.isNotBlank(this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().getEntityLinkMemberFromId2())) {
      return true;
    }

    if (null != getEntityLinkMemberFromId2Attribute()) {
      return true;
    }
    
    return false;
    
  }

  /**
   * 
   * @return true if has script or an attribute mapped
   */
  public boolean isHasEntityLinkMemberFromId3() {
    
    if (StringUtils.isNotBlank(this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().getEntityLinkMemberFromId3())) {
      return true;
    }

    if (null != getEntityLinkMemberFromId3Attribute()) {
      return true;
    }
    
    return false;
    
  }


  /**
   * 
   * @return true if has script or an attribute mapped
   */
  public boolean isHasEntityLinkMemberToId3() {
    
    if (StringUtils.isNotBlank(this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().getEntityLinkMemberToId3())) {
      return true;
    }

    if (null != getEntityLinkMemberToId3Attribute()) {
      return true;
    }
    
    return false;
    
  }

  public boolean isHasTargetGroupLink() {
    if (hasTargetGroupLink != null) {
      return hasTargetGroupLink;
    }
    if (this.grouperProvisioner.retrieveGrouperProvisioningConfiguration().isHasTargetGroupLink()) {
      return true;
    }
    for (GrouperProvisioningConfigurationAttribute grouperProvisioningConfigurationAttribute : 
      this.grouperProvisioner.retrieveGrouperProvisioningConfiguration().getTargetGroupAttributeNameToConfig().values()) {
      if (!StringUtils.isBlank(grouperProvisioningConfigurationAttribute.getTranslateToGroupSyncField())) {
        return true;
      }
    }
    for (GrouperProvisioningConfigurationAttribute grouperProvisioningConfigurationAttribute : 
      this.grouperProvisioner.retrieveGrouperProvisioningConfiguration().getTargetGroupAttributeNameToConfig().values()) {
      if (!StringUtils.isBlank(grouperProvisioningConfigurationAttribute.getTranslateToGroupSyncField())) {
        return true;
      }
    }

    return false;
  }


  
  public void setHasTargetGroupLink(Boolean hasTargetGroupLink) {
    this.hasTargetGroupLink = hasTargetGroupLink;
  }

  /**
   * 
   */
  private Boolean selectGroupMissingIncremental;

  
  /**
   * 
   * @return
   */
  public boolean isSelectGroupMissingIncremental() {
    if (selectGroupMissingIncremental != null) {
      return selectGroupMissingIncremental;
    }
    if (!this.getGrouperProvisioningType().isIncrementalSync()) {
      return false;
    }
    if (this.isSelectGroups()) {
      return true;
    }
    return false;
  }


  /**
   * 
   * @param retrieveMissingGroupsIncremental
   */
  public void setSelectGroupMissingIncremental(
      Boolean retrieveMissingGroupsIncremental) {
    this.selectGroupMissingIncremental = retrieveMissingGroupsIncremental;
  }


  /**
   * 
   */
  private GrouperProvisioningType grouperProvisioningType;
  
  
  public GrouperProvisioningType getGrouperProvisioningType() {
    return grouperProvisioningType;
  }

  
  public void setGrouperProvisioningType(GrouperProvisioningType grouperProvisioningType) {
    this.grouperProvisioningType = grouperProvisioningType;
  }


  private GrouperProvisioner grouperProvisioner;
  
  public GrouperProvisioner getGrouperProvisioner() {
    return grouperProvisioner;
  }
  
  public GrouperProvisioningBehavior(GrouperProvisioner grouperProvisioner) {
    super();
    this.grouperProvisioner = grouperProvisioner;
  }

  public GrouperProvisioningBehavior() {
    super();
    // TODO Auto-generated constructor stub
  }

  public void setGrouperProvisioner(GrouperProvisioner grouperProvisioner) {
    this.grouperProvisioner = grouperProvisioner;
  }

  public GrouperProvisioningBehaviorMembershipType getGrouperProvisioningBehaviorMembershipType() {
    if (this.grouperProvisioningBehaviorMembershipType == null) {
      return this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().getGrouperProvisioningBehaviorMembershipType();
    }
    return grouperProvisioningBehaviorMembershipType;
  }
  
  public void setGrouperProvisioningBehaviorMembershipType(
      GrouperProvisioningBehaviorMembershipType grouperProvisioningBehaviorMembershipType) {
    this.grouperProvisioningBehaviorMembershipType = grouperProvisioningBehaviorMembershipType;
  }

  private GrouperProvisioningBehaviorMembershipType grouperProvisioningBehaviorMembershipType;

  private Boolean selectEntities;

  
  public boolean isSelectEntities() {
    if (this.selectEntities != null) {
      return this.selectEntities;
    }
    if (!GrouperUtil.booleanValue(this.getGrouperProvisioner().retrieveGrouperProvisioningTargetDaoAdapter().getGrouperProvisionerDaoCapabilities().getCanRetrieveEntities(), false)
        && !GrouperUtil.booleanValue(this.getGrouperProvisioner().retrieveGrouperProvisioningTargetDaoAdapter().getGrouperProvisionerDaoCapabilities().getCanRetrieveEntity(), false)) {
      return false;
    }
    return this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().isSelectEntities();
  }


  
  public void setSelectEntities(Boolean entitiesRetrieve) {
    this.selectEntities = entitiesRetrieve;
  }

  private Boolean selectMemberships;

  
  
  public boolean isSelectMemberships() {
    if (this.selectMemberships != null) {
      return selectMemberships;
    }
    
    if (!GrouperUtil.booleanValue(this.getGrouperProvisioner().retrieveGrouperProvisioningTargetDaoAdapter()
        .getGrouperProvisionerDaoCapabilities().getCanRetrieveAllMemberships(), false)
        && !GrouperUtil.booleanValue(this.getGrouperProvisioner().retrieveGrouperProvisioningTargetDaoAdapter()
            .getGrouperProvisionerDaoCapabilities().getCanRetrieveMemberships(), false)
        && !GrouperUtil.booleanValue(this.getGrouperProvisioner().retrieveGrouperProvisioningTargetDaoAdapter()
            .getGrouperProvisionerDaoCapabilities().getCanRetrieveMembershipsBulk(), false)
        && !GrouperUtil.booleanValue(this.getGrouperProvisioner().retrieveGrouperProvisioningTargetDaoAdapter()
            .getGrouperProvisionerDaoCapabilities().getCanRetrieveMembershipsByEntities(), false)
        && !GrouperUtil.booleanValue(this.getGrouperProvisioner().retrieveGrouperProvisioningTargetDaoAdapter()
            .getGrouperProvisionerDaoCapabilities().getCanRetrieveMembershipsByGroups(), false)) {
      return false;
    }

    return this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().isSelectMemberships();
  }
  
  private Boolean selectMembershipsForGroup;
  
  public void setSelectMembershipsForGroup(Boolean selectMembershipsForGroup) {
    this.selectMembershipsForGroup = selectMembershipsForGroup;
  }


  public boolean isSelectMembershipsForGroup() {
    if (this.selectMembershipsForGroup != null) {
      return selectMembershipsForGroup;
    }
    
    return GrouperUtil.booleanValue(this.getGrouperProvisioner().retrieveGrouperProvisioningTargetDaoAdapter()
        .getGrouperProvisionerDaoCapabilities().getCanRetrieveMembershipsByGroups(), false) ||
        GrouperUtil.booleanValue(this.getGrouperProvisioner().retrieveGrouperProvisioningTargetDaoAdapter()
            .getGrouperProvisionerDaoCapabilities().getCanRetrieveMembershipsByGroup(), false);
  }
  
  private Boolean selectMembershipsForEntity;
  
  
  public void setSelectMembershipsForEntity(Boolean selectMembershipsForEntity) {
    this.selectMembershipsForEntity = selectMembershipsForEntity;
  }


  public boolean isSelectMembershipsForEntity() {
    if (this.selectMembershipsForEntity != null) {
      return selectMembershipsForEntity;
    }
    
    return GrouperUtil.booleanValue(this.getGrouperProvisioner().retrieveGrouperProvisioningTargetDaoAdapter()
        .getGrouperProvisionerDaoCapabilities().getCanRetrieveMembershipsByEntities(), false) ||
        GrouperUtil.booleanValue(this.getGrouperProvisioner().retrieveGrouperProvisioningTargetDaoAdapter()
            .getGrouperProvisionerDaoCapabilities().getCanRetrieveMembershipsByEntity(), false) ;
  }
  
  private Boolean replaceMemberships;
  
  public boolean isReplaceMemberships() {
    
    if (replaceMemberships != null) {
      return replaceMemberships;
    }
    if (this.getGrouperProvisioningBehaviorMembershipType() == GrouperProvisioningBehaviorMembershipType.membershipObjects) {
      //can the provisioner even do this?
      if (GrouperUtil.booleanValue(this.getGrouperProvisioner().retrieveGrouperProvisioningTargetDaoAdapter().getGrouperProvisionerDaoCapabilities().getCanReplaceGroupMemberships(), false)) {
        return true;
      }
    }    
    return false;
  }

  
  public void setReplaceMemberships(Boolean replaceMemberships) {
    this.replaceMemberships = replaceMemberships;
  }


  public void setSelectMemberships(Boolean membershipsRetrieve) {
    this.selectMemberships = membershipsRetrieve;
  }

  private Boolean selectGroups;

  public boolean isSelectGroups() {
    
    if (this.selectGroups != null) {
      return selectGroups;
    }

    if (!GrouperUtil.booleanValue(this.getGrouperProvisioner().retrieveGrouperProvisioningTargetDaoAdapter().getGrouperProvisionerDaoCapabilities().getCanRetrieveGroups(), false)
        && !GrouperUtil.booleanValue(this.getGrouperProvisioner().retrieveGrouperProvisioningTargetDaoAdapter().getGrouperProvisionerDaoCapabilities().getCanRetrieveGroup(), false)) {
      return false;
    }
    
    return this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().isSelectGroups();
    
  }
  
  public void setSelectGroups(Boolean groupsRetrieve) {
    this.selectGroups = groupsRetrieve;
  }

  private Boolean selectGroupsAll;

  private Set<String> selectGroupsAttributes;

  private Boolean updateGroups;

  private Set<String> updateGroupAttributes;

  private Boolean insertGroups;

  private Set<String> insertGroupsAttributes;

  private Boolean deleteGroupsIfNotExistInGrouper;
  
  private Boolean deleteGroupsIfGrouperDeleted;

  private Boolean deleteEntitiesIfGrouperCreated;
  private Boolean deleteGroupsIfGrouperCreated;
  private Boolean deleteMembershipsIfGrouperCreated;


  public boolean isDeleteEntitiesIfGrouperCreated() {
    if (this.deleteEntitiesIfGrouperCreated != null) {
      return deleteEntitiesIfGrouperCreated;
    }

    //can the provisioner even do this?
    if (!isDeleteEntities()) {
      return false;
    }

    // is it configured to?
    return this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().isDeleteEntitiesIfGrouperCreated();
  }

  public void setDeleteEntitiesIfGrouperCreated(Boolean deleteEntitiesIfGrouperCreated) {
    this.deleteEntitiesIfGrouperCreated = deleteEntitiesIfGrouperCreated;
  }

  public boolean isDeleteGroupsIfGrouperCreated() {
    if (this.deleteGroupsIfGrouperCreated != null) {
      return deleteGroupsIfGrouperCreated;
    }

    //can the provisioner even do this?
    if (!isDeleteGroups()) {
      return false;
    }

    // is it configured to?
    return this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().isDeleteGroupsIfGrouperCreated();
  }
  
  public void setDeleteGroupsIfGrouperCreated(Boolean deleteGroupsIfGrouperCreated) {
    this.deleteGroupsIfGrouperCreated = deleteGroupsIfGrouperCreated;
  }
  
  public boolean isDeleteMembershipsIfGrouperCreated() {
    
    if (this.deleteMembershipsIfGrouperCreated != null) {
      return deleteMembershipsIfGrouperCreated;
    }

    //can the provisioner even do this?
    if (!this.isDeleteMemberships()) {
      return false;
    }
    
    // is it configured to?
    return this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().isDeleteMembershipsIfGrouperCreated();
  }

  public void setDeleteMembershipsIfGrouperCreated(
      Boolean deleteMembershipsIfGrouperCreated) {
    this.deleteMembershipsIfGrouperCreated = deleteMembershipsIfGrouperCreated;
  }
  
  private Boolean selectEntitiesAll;

  private Set<String> selectEntityAttributes;

  private Boolean updateEntities;

  private Set<String> updateEntityAttributes;

  private Boolean insertEntities;

  private Set<String> insertEntityAttributes;

  private Boolean deleteEntitiesIfNotExistInGrouper;

  private Boolean deleteGroups;
  
  private Boolean deleteEntities;
  
  private Boolean deleteMemberships;
  
  /**
   * 
   * @return
   */
  public boolean isDeleteGroups() {
    if (this.deleteGroups != null) {
      return deleteGroups;
    }

    //can the provisioner even do this?
    if (!GrouperUtil.booleanValue(this.getGrouperProvisioner().retrieveGrouperProvisioningTargetDaoAdapter().getGrouperProvisionerDaoCapabilities().getCanDeleteGroup(), false)
      &&  !GrouperUtil.booleanValue(this.getGrouperProvisioner().retrieveGrouperProvisioningTargetDaoAdapter().getGrouperProvisionerDaoCapabilities().getCanDeleteGroups(), false)
        ) {
      return false;
    }

    // is it configured to?
    return this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().isDeleteGroups();
  }

  /**
   * 
   * @param deleteGroups
   */
  public void setDeleteGroups(boolean deleteGroups) {
    this.deleteGroups = deleteGroups;
  }

  /**
   * 
   * @return
   */
  public boolean isDeleteEntities() {
    if (this.deleteEntities != null) {
      return deleteEntities;
    }

    //can the provisioner even do this?
    if (!GrouperUtil.booleanValue(this.getGrouperProvisioner().retrieveGrouperProvisioningTargetDaoAdapter().getGrouperProvisionerDaoCapabilities().getCanDeleteEntity(), false)
      &&  !GrouperUtil.booleanValue(this.getGrouperProvisioner().retrieveGrouperProvisioningTargetDaoAdapter().getGrouperProvisionerDaoCapabilities().getCanDeleteEntities(), false)
        ) {
      return false;
    }

    // is it configured to?
    return this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().isDeleteEntities();

  }
  
  public void setDeleteEntities(boolean deleteEntities) {
    this.deleteEntities = deleteEntities;
  }

  /**
   * @param gcGrouperSyncMembership
   * @return false
   */
  public boolean isDeleteMembership(GcGrouperSyncMembership gcGrouperSyncMembership) {
    
    if (this.isDeleteMembershipsIfNotExistInGrouper()) {
      return true;
    }
    
    if (gcGrouperSyncMembership == null) {
      return false;
    }
    
    // grouper deleted it
    if (this.isDeleteMembershipsIfGrouperDeleted()) {
      return true;
    }
    
    // delete if inserted and delete if grouper created
    return gcGrouperSyncMembership.isInTargetInsertOrExists() && this.isDeleteMembershipsIfGrouperCreated();
  }

  /**
   * @param gcGrouperSyncMember
   * @return false
   */
  public boolean isDeleteEntity(GcGrouperSyncMember gcGrouperSyncMember) {
    
    if (this.isDeleteEntitiesIfNotExistInGrouper()) {
      return true;
    }
    
    if (gcGrouperSyncMember == null) {
      return false;
    }
    
    // grouper deleted it
    if (this.isDeleteEntitiesIfGrouperDeleted()) {
      return true;
    }
    
    // delete if inserted and delete if grouper created
    return gcGrouperSyncMember.isInTargetInsertOrExists() && this.isDeleteEntitiesIfGrouperCreated();
  }

  /**
   * @param gcGrouperSyncGroup
   * @return false
   */
  public boolean isDeleteGroup(GcGrouperSyncGroup gcGrouperSyncGroup) {
    
    if (this.isDeleteGroupsIfNotExistInGrouper()) {
      return true;
    }
    
    if (gcGrouperSyncGroup == null) {
      return false;
    }
    
    // grouper deleted it
    if (this.isDeleteGroupsIfGrouperDeleted()) {
      return true;
    }
    
    // delete if inserted and delete if grouper created
    return gcGrouperSyncGroup.isInTargetInsertOrExists() && this.isDeleteGroupsIfGrouperCreated();
  }

  public boolean isDeleteMemberships() {
    if (this.deleteMemberships != null) {
      return deleteMemberships;
    }

    if (this.getGrouperProvisioningBehaviorMembershipType() == GrouperProvisioningBehaviorMembershipType.membershipObjects) {
      //can the provisioner even do this?
      if (!GrouperUtil.booleanValue(this.getGrouperProvisioner().retrieveGrouperProvisioningTargetDaoAdapter().getGrouperProvisionerDaoCapabilities().getCanDeleteMembership(), false)
        &&  !GrouperUtil.booleanValue(this.getGrouperProvisioner().retrieveGrouperProvisioningTargetDaoAdapter().getGrouperProvisionerDaoCapabilities().getCanDeleteMemberships(), false)
          ) {
        return false;
      }

    }

    // is it configured to?
    return this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().isDeleteMemberships();
  }
  
  public void setDeleteMemberships(boolean deleteMemberships) {
    this.deleteMemberships = deleteMemberships;
  }

  private Boolean deleteEntitiesIfGrouperDeleted;

  private Boolean selectMembershipsAll;

  private Set<String> selectMembershipAttributes;

  private Boolean updateMemberships;

  private Set<String> updateMembershipsAttributes;

  private Boolean insertMemberships;

  private Set<String> insertMembershipsAttributes;

  private Boolean deleteMembershipsIfNotExistInGrouper;
  
  private Boolean deleteMembershipsIfGrouperDeleted;
  
  public boolean isSelectGroupsAll() {
    if (this.selectGroupsAll != null) {
      return selectGroupsAll;
    }
    if (!this.isSelectGroups()) {
      return false;
    }
    return GrouperUtil.booleanValue(this.getGrouperProvisioner().retrieveGrouperProvisioningTargetDaoAdapter().getGrouperProvisionerDaoCapabilities().getCanRetrieveAllGroups(), false);
  }
  
  public void setSelectGroupsAll(Boolean groupsRetrieveAll) {
    this.selectGroupsAll = groupsRetrieveAll;
  }

  
  public Set<String> getSelectGroupsAttributes() {
    return selectGroupsAttributes;
  }

  
  public void setSelectGroupsAttributes(Set<String> groupsRetrieveAttributes) {
    this.selectGroupsAttributes = groupsRetrieveAttributes;
  }

  
  public boolean isUpdateGroups() {
    if (this.updateGroups != null) {
      return updateGroups;
    }

    //can the provisioner even do this?
    if (!GrouperUtil.booleanValue(this.getGrouperProvisioner().retrieveGrouperProvisioningTargetDaoAdapter().getGrouperProvisionerDaoCapabilities().getCanUpdateGroup(), false)
      &&  !GrouperUtil.booleanValue(this.getGrouperProvisioner().retrieveGrouperProvisioningTargetDaoAdapter().getGrouperProvisionerDaoCapabilities().getCanUpdateGroups(), false)
        ) {
      return false;
    }
    
    // is it configured to?
    return this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().isUpdateGroups();
  }
  
  public void setUpdateGroups(Boolean groupsUpdate) {
    this.updateGroups = groupsUpdate;
  }

  
  public Set<String> getUpdateGroupAttributes() {
    return updateGroupAttributes;
  }

  
  public void setUpdateGroupAttributes(Set<String> groupsUpdateAttributes) {
    this.updateGroupAttributes = groupsUpdateAttributes;
  }

  
  public boolean isInsertGroups() {
    if (this.insertGroups != null) {
      return insertGroups;
    }
    //can the provisioner even do this?
    if (!GrouperUtil.booleanValue(this.getGrouperProvisioner().retrieveGrouperProvisioningTargetDaoAdapter().getGrouperProvisionerDaoCapabilities().getCanInsertGroup(), false)
      &&  !GrouperUtil.booleanValue(this.getGrouperProvisioner().retrieveGrouperProvisioningTargetDaoAdapter().getGrouperProvisionerDaoCapabilities().getCanInsertGroups(), false)
        ) {
      return false;
    }
    
    // is it configured to?
    return this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().isInsertGroups();

  }

  
  public void setInsertGroups(Boolean groupsInsert) {
    this.insertGroups = groupsInsert;
  }

  
  public Set<String> getInsertGroupsAttributes() {
    return insertGroupsAttributes;
  }

  public void setInsertGroupsAttributes(Set<String> groupsInsertAttributes) {
    this.insertGroupsAttributes = groupsInsertAttributes;
  }

  
  public boolean isDeleteGroupsIfNotExistInGrouper() {
    if (this.deleteGroupsIfNotExistInGrouper != null) {
      return deleteGroupsIfNotExistInGrouper;
    }
    if (!this.isDeleteGroups()) {
      return false;
    }
    return this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().isDeleteGroupsIfNotExistInGrouper();
  }

  
  public void setDeleteGroupsIfNotExistInGrouper(Boolean groupsDeleteIfNotInGrouper) {
    this.deleteGroupsIfNotExistInGrouper = groupsDeleteIfNotInGrouper;
  }
  
  public boolean isDeleteGroupsIfGrouperDeleted() {
    
    if (this.deleteGroupsIfGrouperDeleted != null) {
      return deleteGroupsIfGrouperDeleted;
    }
    
    if (!this.isDeleteGroups()) {
      return false;
    }
    
    return this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().isDeleteGroupsIfGrouperDeleted();
  }

  
  public void setDeleteGroupsIfGrouperDeleted(
      Boolean groupsDeleteIfDeletedFromGrouper) {
    this.deleteGroupsIfGrouperDeleted = groupsDeleteIfDeletedFromGrouper;
  }

 
  public boolean isSelectEntitiesAll() {
    if (this.selectEntitiesAll != null) {
      return selectEntitiesAll;
    }

    //can the provisioner even do this?
    if (!this.isSelectEntities()) {
      return false;
    }
    if (!GrouperUtil.booleanValue(this.getGrouperProvisioner().retrieveGrouperProvisioningTargetDaoAdapter().getGrouperProvisionerDaoCapabilities().getCanRetrieveAllEntities(), false)) {
      return false;
    }
    return this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().isSelectAllEntities();
  }

  
  public void setSelectEntitiesAll(Boolean entitiesRetrieveAll) {
    this.selectEntitiesAll = entitiesRetrieveAll;
  }

  
  public Set<String> getSelectEntityAttributes() {
    return selectEntityAttributes;
  }

  
  public void setSelectEntityAttributes(Set<String> entitiesRetrieveAttributes) {
    this.selectEntityAttributes = entitiesRetrieveAttributes;
  }

  
  public boolean isUpdateEntities() {
    if (this.updateEntities != null) {
      return this.updateEntities;
    }
    //can the provisioner even do this?
    if (!GrouperUtil.booleanValue(this.getGrouperProvisioner().retrieveGrouperProvisioningTargetDaoAdapter().getGrouperProvisionerDaoCapabilities().getCanUpdateEntity(), false)
      &&  !GrouperUtil.booleanValue(this.getGrouperProvisioner().retrieveGrouperProvisioningTargetDaoAdapter().getGrouperProvisionerDaoCapabilities().getCanUpdateEntities(), false)
        ) {
      return false;
    }
    
    return this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().isUpdateEntities();
  }

  
  public void setUpdateEntities(Boolean entitiesUpdate) {
    this.updateEntities = entitiesUpdate;
  }

  
  public Set<String> getUpdateEntityAttributes() {
    return updateEntityAttributes;
  }

  
  public void setUpdateEntityAttributes(Set<String> entitiesUpdateAttributes) {
    this.updateEntityAttributes = entitiesUpdateAttributes;
  }

  
  public boolean isInsertEntities() {
    if (this.insertEntities != null) {
      return this.insertEntities;
    }
    
    //can the provisioner even do this?
    if (!GrouperUtil.booleanValue(this.getGrouperProvisioner().retrieveGrouperProvisioningTargetDaoAdapter().getGrouperProvisionerDaoCapabilities().getCanInsertEntity(), false)
      &&  !GrouperUtil.booleanValue(this.getGrouperProvisioner().retrieveGrouperProvisioningTargetDaoAdapter().getGrouperProvisionerDaoCapabilities().getCanInsertEntities(), false)
        ) {
      return false;
    }

    return this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().isInsertEntities();
  }
  
  public void setInsertEntities(Boolean entitiesInsert) {
    this.insertEntities = entitiesInsert;
  }

  
  public Set<String> getInsertEntityAttributes() {
    return insertEntityAttributes;
  }

  
  public void setInsertEntityAttributes(Set<String> entitiesInsertAttributes) {
    this.insertEntityAttributes = entitiesInsertAttributes;
  }

  
  public boolean isDeleteEntitiesIfNotExistInGrouper() {
    
    if (this.deleteEntitiesIfNotExistInGrouper != null) {
      return this.deleteEntitiesIfNotExistInGrouper;
    }

    //can the provisioner even do this?
    if (!GrouperUtil.booleanValue(this.getGrouperProvisioner().retrieveGrouperProvisioningTargetDaoAdapter().getGrouperProvisionerDaoCapabilities().getCanDeleteEntity(), false)
      &&  !GrouperUtil.booleanValue(this.getGrouperProvisioner().retrieveGrouperProvisioningTargetDaoAdapter().getGrouperProvisionerDaoCapabilities().getCanDeleteEntities(), false)
        ) {
      return false;
    }
    if (!isDeleteEntities()) {
      return false;
    }
    // is it configured to?
    return this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().isDeleteEntitiesIfNotExistInGrouper();
  }

  
  public void setDeleteEntitiesIfNotExistInGrouper(Boolean entitiesDeleteIfNotInGrouper) {
    this.deleteEntitiesIfNotExistInGrouper = entitiesDeleteIfNotInGrouper;
  }

  
  public boolean isDeleteEntitiesIfGrouperDeleted() {
    
    if (this.deleteEntitiesIfGrouperDeleted != null) {
      return this.deleteEntitiesIfGrouperDeleted;
    }
    
    //can the provisioner even do this?
    if (!GrouperUtil.booleanValue(this.getGrouperProvisioner().retrieveGrouperProvisioningTargetDaoAdapter().getGrouperProvisionerDaoCapabilities().getCanDeleteEntity(), false)
      &&  !GrouperUtil.booleanValue(this.getGrouperProvisioner().retrieveGrouperProvisioningTargetDaoAdapter().getGrouperProvisionerDaoCapabilities().getCanDeleteEntities(), false)
        ) {
      return false;
    }
    if (!isDeleteEntities()) {
      return false;
    }
    
    // is it configured to?
    return this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().isDeleteEntitiesIfGrouperDeleted();
  }

  public void setDeleteEntitiesIfGrouperDeleted(
      Boolean entitiesDeleteIfDeletedFromGrouper) {
    this.deleteEntitiesIfGrouperDeleted = entitiesDeleteIfDeletedFromGrouper;
  }

  public boolean isSelectMembershipsAll() {
    if (this.selectMembershipsAll != null) {
      return this.selectMembershipsAll;
    }
    if (!this.isSelectMemberships()) {
      return false;
    }
    if (!GrouperUtil.booleanValue(this.getGrouperProvisioner().retrieveGrouperProvisioningTargetDaoAdapter().getGrouperProvisionerDaoCapabilities().getCanRetrieveAllMemberships(), false)) {
      return false;
    }
    if (this.getGrouperProvisioningBehaviorMembershipType() == GrouperProvisioningBehaviorMembershipType.entityAttributes) {
      if (!this.isSelectEntitiesAll()) {
        return false;
      }
    }
    if (this.getGrouperProvisioningBehaviorMembershipType() == GrouperProvisioningBehaviorMembershipType.groupAttributes) {
      if (!this.isSelectGroupsAll()) {
        return false;
      }
    }
    
    if (this.getGrouperProvisioningBehaviorMembershipType() == GrouperProvisioningBehaviorMembershipType.membershipObjects) {
      return true;
    }
    
    return false;
  }

  public void setSelectMembershipsAll(Boolean membershipsRetrieveAll) {
    this.selectMembershipsAll = membershipsRetrieveAll;
  }
  
  public Set<String> getSelectMembershipAttributes() {
    return selectMembershipAttributes;
  }

  
  public void setSelectMembershipAttributes(Set<String> membershipsRetrieveAttributes) {
    this.selectMembershipAttributes = membershipsRetrieveAttributes;
  }

  
  public boolean isUpdateMemberships() {
    if (updateMemberships != null) {
      return updateMemberships;
    }
    if (this.getGrouperProvisioningBehaviorMembershipType() == GrouperProvisioningBehaviorMembershipType.membershipObjects) {
      //can the provisioner even do this?
      if (!GrouperUtil.booleanValue(this.getGrouperProvisioner().retrieveGrouperProvisioningTargetDaoAdapter().getGrouperProvisionerDaoCapabilities().getCanUpdateMembership(), false)
        &&  !GrouperUtil.booleanValue(this.getGrouperProvisioner().retrieveGrouperProvisioningTargetDaoAdapter().getGrouperProvisionerDaoCapabilities().getCanUpdateMemberships(), false)
          ) {
        return false;
      }
    }    
    // is it configured to?
    return this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().isUpdateMemberships();
  }

  
  public void setUpdateMemberships(Boolean membershipsUpdate) {
    this.updateMemberships = membershipsUpdate;
  }

  
  public Set<String> getUpdateMembershipsAttributes() {
    return updateMembershipsAttributes;
  }

  
  public void setUpdateMembershipsAttributes(Set<String> membershipsUpdateAttributes) {
    this.updateMembershipsAttributes = membershipsUpdateAttributes;
  }

  
  public boolean isInsertMemberships() {
    if (insertMemberships != null) {
      return insertMemberships;
    }
    if (this.getGrouperProvisioningBehaviorMembershipType() == GrouperProvisioningBehaviorMembershipType.membershipObjects) {
      //can the provisioner even do this?
      if (!GrouperUtil.booleanValue(this.getGrouperProvisioner().retrieveGrouperProvisioningTargetDaoAdapter().getGrouperProvisionerDaoCapabilities().getCanInsertMembership(), false)
        &&  !GrouperUtil.booleanValue(this.getGrouperProvisioner().retrieveGrouperProvisioningTargetDaoAdapter().getGrouperProvisionerDaoCapabilities().getCanInsertMemberships(), false)
          ) {
        return false;
      }
    }
    return this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().isInsertMemberships();
  }

  
  public void setInsertMemberships(Boolean membershipsInsert) {
    this.insertMemberships = membershipsInsert;
  }

  
  public Set<String> getInsertMembershipsAttributes() {
    return insertMembershipsAttributes;
  }

  
  public void setInsertMembershipsAttributes(Set<String> membershipsInsertAttributes) {
    this.insertMembershipsAttributes = membershipsInsertAttributes;
  }

  
  public boolean isDeleteMembershipsIfNotExistInGrouper() {
    if (deleteMembershipsIfNotExistInGrouper != null) {
      return deleteMembershipsIfNotExistInGrouper;
    }
    if (!this.isDeleteMemberships()) {
      return false;
    }
    return this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().isDeleteMembershipsIfNotExistInGrouper();
  }
  
  public void setDeleteMembershipsIfNotExistInGrouper(Boolean membershipsDeleteIfNotInGrouper) {
    this.deleteMembershipsIfNotExistInGrouper = membershipsDeleteIfNotInGrouper;
  }
  
  public boolean isDeleteMembershipsIfGrouperDeleted() {
    if (deleteMembershipsIfGrouperDeleted != null) {
      return deleteMembershipsIfGrouperDeleted;
    }
    if (!this.isDeleteMemberships()) {
      return false;
    }
    return this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().isDeleteMembershipsIfGrouperDeleted();
  }
  
  public void setDeleteMembershipsIfGrouperDeleted(
      Boolean membershipsDeleteIfDeletedFromGrouper) {
    this.deleteMembershipsIfGrouperDeleted = membershipsDeleteIfDeletedFromGrouper;
  }

  @Override
  public String toString() {
    
    StringBuilder result = new StringBuilder();
    
    Set<String> fieldNames = GrouperUtil.fieldNames(GrouperProvisioningBehavior.class, null, false);
        
    fieldNames = new TreeSet<String>(fieldNames);
    boolean firstField = true;
    for (String fieldName : fieldNames) {
      if ("grouperProvisioner".equals(fieldName)) {
        continue;
      }
      // call getter
      Object value = GrouperUtil.propertyValue(this, fieldName);
      if (!GrouperUtil.isBlank(value)) {
        
        if ((value instanceof Collection) && ((Collection)value).size() == 0) {
          continue;
        }
        if ((value instanceof Map) && ((Map)value).size() == 0) {
          continue;
        }
        if ((value.getClass().isArray()) && Array.getLength(value) == 0) {
          continue;
        }
        
        if (!firstField) {
          result.append(", ");
        }
        firstField = false;
        result.append(fieldName).append(" = '").append(GrouperUtil.toStringForLog(value, false)).append("'");
      }
    }
    for (String propertyName : new String[] {"hasEntityLinkMemberFromId2",
        "hasEntityLinkMemberFromId3", "hasEntityLinkMemberToId2", "hasEntityLinkMemberToId3",
        "hasGroupLinkGroupFromId2", "hasGroupLinkGroupFromId3", "hasGroupLinkGroupToId2", "hasGroupLinkGroupToId3",
        "groupLinkGroupFromId2Attribute", "groupLinkGroupFromId3Attribute", "groupLinkGroupToId2Attribute",
        "groupLinkGroupToId3Attribute", "entityLinkMemberFromId2Attribute", "entityLinkMemberFromId3Attribute",
        "entityLinkMemberToId2Attribute", "entityLinkMemberToId3Attribute"}) {

      Object value = GrouperUtil.propertyValue(this, propertyName);
      if (value != null) {
        if (!firstField) {
          result.append(", ");
        }
        firstField = false;
        result.append(propertyName).append(" = '").append(GrouperUtil.toStringForLog(value, false)).append("'");
        
      }
    }
      
    return result.toString();
  }
  
  public boolean canUpdateObjectAttribute(
      ProvisioningUpdatable grouperProvisioningUpdatable, String attributeName) {

    if (grouperProvisioningUpdatable instanceof ProvisioningGroup) {
      return this.canUpdateGroupAttribute(attributeName);
    }
    if (grouperProvisioningUpdatable instanceof ProvisioningEntity) {
      return this.canUpdateEntityAttribute(attributeName);
    }
    if (grouperProvisioningUpdatable instanceof ProvisioningMembership) {
      return this.canUpdateMembershipAttribute(attributeName);
    }
    throw new RuntimeException("Not expecting object type: " + (grouperProvisioningUpdatable == null ? "null" : grouperProvisioningUpdatable.getClass().getName()));
  }
  
  
  public boolean isCreateGroupsAndEntitiesBeforeTranslatingMemberships() {
    return createGroupsAndEntitiesBeforeTranslatingMemberships;
  }

  
  public void setCreateGroupsAndEntitiesBeforeTranslatingMemberships(boolean createGroupsAndEntitiesBeforeTranslatingMemberships) {
    this.createGroupsAndEntitiesBeforeTranslatingMemberships = createGroupsAndEntitiesBeforeTranslatingMemberships;
  }
  
  private String subjectIdentifierForMemberSyncTable;
  
  public String getSubjectIdentifierForMemberSyncTable() {
    
    if (this.subjectIdentifierForMemberSyncTable != null) {
      return this.subjectIdentifierForMemberSyncTable;
    }
    
    String currSubjectIdentifierForMemberSyncTable = this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().retrieveConfigString("subjectIdentifierForMemberSyncTable", false);

    // no override, try to compute it
    if (StringUtils.isBlank(currSubjectIdentifierForMemberSyncTable)) {
      List<GrouperProvisioningConfigurationAttribute> searchAttributes = this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().getEntitySearchAttributes();
      for (GrouperProvisioningConfigurationAttribute searchAttribute : searchAttributes) {
        String value = searchAttribute.getTranslateFromGrouperProvisioningEntityField();
        if (value != null && value.startsWith("subjectIdentifier")) {
          currSubjectIdentifierForMemberSyncTable = value;
        }
      }
    }
    
    if (StringUtils.isBlank(currSubjectIdentifierForMemberSyncTable)) {
      GrouperProvisioningConfigurationAttribute matchingAttribute = this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().retrieveEntityAttributeMatching();
      if (matchingAttribute != null) {
        String value = matchingAttribute.getTranslateFromGrouperProvisioningEntityField();
        if (value != null && value.startsWith("subjectIdentifier")) {
          currSubjectIdentifierForMemberSyncTable = value.substring(11);
        }
      }
    }
    
    if (StringUtils.isBlank(currSubjectIdentifierForMemberSyncTable)) {
      // default
      currSubjectIdentifierForMemberSyncTable = "subjectIdentifier0";
    }
    
    if (!currSubjectIdentifierForMemberSyncTable.equals("subjectIdentifier0") && 
        !currSubjectIdentifierForMemberSyncTable.equals("subjectIdentifier1") &&
        !currSubjectIdentifierForMemberSyncTable.equals("subjectIdentifier2")) {
      throw new RuntimeException("Not expecting subject identifier for member sync: " + currSubjectIdentifierForMemberSyncTable);
    }

    this.subjectIdentifierForMemberSyncTable = currSubjectIdentifierForMemberSyncTable;

    return this.subjectIdentifierForMemberSyncTable;
  }


  public boolean canSelectMembershipsForEntity() {
    
    if (GrouperUtil.booleanValue(this.getGrouperProvisioner().retrieveGrouperProvisioningTargetDaoAdapter().getGrouperProvisionerDaoCapabilities().getCanRetrieveMembershipsByEntities(), false)) {
      return true;
    }
    
    if (GrouperUtil.booleanValue(this.getGrouperProvisioner().retrieveGrouperProvisioningTargetDaoAdapter().getGrouperProvisionerDaoCapabilities().getCanRetrieveMembershipsByEntity(), false)) { 
      return true;
    }
    
    if (this.getGrouperProvisioner().retrieveGrouperProvisioningBehavior().getGrouperProvisioningBehaviorMembershipType() == GrouperProvisioningBehaviorMembershipType.entityAttributes) {
      return true;
    }
    
    return false;
    
  }
  
  public boolean canSelectMembershipsForGroup() {
    
    if (GrouperUtil.booleanValue(this.getGrouperProvisioner().retrieveGrouperProvisioningTargetDaoAdapter().getGrouperProvisionerDaoCapabilities().getCanRetrieveMembershipsByGroups(), false)) {
      return true;
    }
    
    if (GrouperUtil.booleanValue(this.getGrouperProvisioner().retrieveGrouperProvisioningTargetDaoAdapter().getGrouperProvisionerDaoCapabilities().getCanRetrieveMembershipsByGroup(), false)) { 
      return true;
    }
    
    if (this.getGrouperProvisioner().retrieveGrouperProvisioningBehavior().getGrouperProvisioningBehaviorMembershipType() == GrouperProvisioningBehaviorMembershipType.groupAttributes) {
      return true;
    }
    
    return false;
    
  }
  
  public boolean isLoadEntitiesToGrouperTable() {
    return this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().isLoadEntitiesToGrouperTable();
  }
  
}
