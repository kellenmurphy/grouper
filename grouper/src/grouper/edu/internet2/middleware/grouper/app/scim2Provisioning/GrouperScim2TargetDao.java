package edu.internet2.middleware.grouper.app.scim2Provisioning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import edu.internet2.middleware.grouper.app.provisioning.ProvisioningEntity;
import edu.internet2.middleware.grouper.app.provisioning.ProvisioningGroup;
import edu.internet2.middleware.grouper.app.provisioning.ProvisioningMembership;
import edu.internet2.middleware.grouper.app.provisioning.ProvisioningObjectChange;
import edu.internet2.middleware.grouper.app.provisioning.ProvisioningObjectChangeAction;
import edu.internet2.middleware.grouper.app.provisioning.targetDao.GrouperProvisionerDaoCapabilities;
import edu.internet2.middleware.grouper.app.provisioning.targetDao.GrouperProvisionerTargetDaoBase;
import edu.internet2.middleware.grouper.app.provisioning.targetDao.TargetDaoDeleteEntityRequest;
import edu.internet2.middleware.grouper.app.provisioning.targetDao.TargetDaoDeleteEntityResponse;
import edu.internet2.middleware.grouper.app.provisioning.targetDao.TargetDaoDeleteGroupRequest;
import edu.internet2.middleware.grouper.app.provisioning.targetDao.TargetDaoDeleteGroupResponse;
import edu.internet2.middleware.grouper.app.provisioning.targetDao.TargetDaoDeleteMembershipsRequest;
import edu.internet2.middleware.grouper.app.provisioning.targetDao.TargetDaoDeleteMembershipsResponse;
import edu.internet2.middleware.grouper.app.provisioning.targetDao.TargetDaoInsertEntityRequest;
import edu.internet2.middleware.grouper.app.provisioning.targetDao.TargetDaoInsertEntityResponse;
import edu.internet2.middleware.grouper.app.provisioning.targetDao.TargetDaoInsertGroupRequest;
import edu.internet2.middleware.grouper.app.provisioning.targetDao.TargetDaoInsertGroupResponse;
import edu.internet2.middleware.grouper.app.provisioning.targetDao.TargetDaoInsertMembershipsRequest;
import edu.internet2.middleware.grouper.app.provisioning.targetDao.TargetDaoInsertMembershipsResponse;
import edu.internet2.middleware.grouper.app.provisioning.targetDao.TargetDaoReplaceGroupMembershipsRequest;
import edu.internet2.middleware.grouper.app.provisioning.targetDao.TargetDaoReplaceGroupMembershipsResponse;
import edu.internet2.middleware.grouper.app.provisioning.targetDao.TargetDaoRetrieveAllEntitiesRequest;
import edu.internet2.middleware.grouper.app.provisioning.targetDao.TargetDaoRetrieveAllEntitiesResponse;
import edu.internet2.middleware.grouper.app.provisioning.targetDao.TargetDaoRetrieveAllGroupsRequest;
import edu.internet2.middleware.grouper.app.provisioning.targetDao.TargetDaoRetrieveAllGroupsResponse;
import edu.internet2.middleware.grouper.app.provisioning.targetDao.TargetDaoRetrieveEntityRequest;
import edu.internet2.middleware.grouper.app.provisioning.targetDao.TargetDaoRetrieveEntityResponse;
import edu.internet2.middleware.grouper.app.provisioning.targetDao.TargetDaoRetrieveGroupRequest;
import edu.internet2.middleware.grouper.app.provisioning.targetDao.TargetDaoRetrieveGroupResponse;
import edu.internet2.middleware.grouper.app.provisioning.targetDao.TargetDaoTimingInfo;
import edu.internet2.middleware.grouper.app.provisioning.targetDao.TargetDaoUpdateEntityRequest;
import edu.internet2.middleware.grouper.app.provisioning.targetDao.TargetDaoUpdateEntityResponse;
import edu.internet2.middleware.grouper.app.provisioning.targetDao.TargetDaoUpdateGroupRequest;
import edu.internet2.middleware.grouper.app.provisioning.targetDao.TargetDaoUpdateGroupResponse;
import edu.internet2.middleware.grouper.util.GrouperUtil;
import edu.internet2.middleware.grouperClient.collections.MultiKey;

public class GrouperScim2TargetDao extends GrouperProvisionerTargetDaoBase {

  @Override
  public TargetDaoRetrieveAllGroupsResponse retrieveAllGroups(
      TargetDaoRetrieveAllGroupsRequest targetDaoRetrieveAllGroupsRequest) {

    long startNanos = System.nanoTime();

    try {

      GrouperScim2ProvisionerConfiguration scimConfiguration = (GrouperScim2ProvisionerConfiguration) this
          .getGrouperProvisioner().retrieveGrouperProvisioningConfiguration();

      List<ProvisioningGroup> results = new ArrayList<ProvisioningGroup>();

      List<GrouperScim2Group> grouperScim2Groups = GrouperScim2ApiCommands
          .retrieveScimGroups(scimConfiguration.getBearerTokenExternalSystemConfigId(), 
              scimConfiguration.getAcceptHeader());

      for (GrouperScim2Group grouperScim2Group : grouperScim2Groups) {
        
        if (scimConfiguration.isDisableGroupsInsteadOfDelete() && grouperScim2Group != null && !GrouperUtil.booleanValue(grouperScim2Group.getActive(), true)) {
          continue;
        }

        ProvisioningGroup targetGroup = grouperScim2Group.toProvisioningGroup();
        results.add(targetGroup);
      }

      return new TargetDaoRetrieveAllGroupsResponse(results);
    } finally {
      this.addTargetDaoTimingInfo(
          new TargetDaoTimingInfo("retrieveAllGroups", startNanos));
    }
  }

  @Override
  public TargetDaoRetrieveAllEntitiesResponse retrieveAllEntities(
      TargetDaoRetrieveAllEntitiesRequest targetDaoRetrieveAllEntitiesRequest) {

    long startNanos = System.nanoTime();

    try {

      GrouperScim2ProvisionerConfiguration scimConfiguration = (GrouperScim2ProvisionerConfiguration) this
          .getGrouperProvisioner().retrieveGrouperProvisioningConfiguration();

      List<ProvisioningEntity> results = new ArrayList<ProvisioningEntity>();

      List<GrouperScim2User> grouperScim2Users = GrouperScim2ApiCommands
          .retrieveScimUsers(scimConfiguration.getBearerTokenExternalSystemConfigId(), scimConfiguration.getAcceptHeader());

      for (GrouperScim2User grouperScim2User : grouperScim2Users) {
        
        if (scimConfiguration.isDisableEntitiesInsteadOfDelete() && grouperScim2User != null && !GrouperUtil.booleanValue(grouperScim2User.getActive(), true)) {
          continue;
        }

        ProvisioningEntity targetEntity = grouperScim2User.toProvisioningEntity();
        results.add(targetEntity);
      }

      return new TargetDaoRetrieveAllEntitiesResponse(results);
    } finally {
      this.addTargetDaoTimingInfo(
          new TargetDaoTimingInfo("retrieveAllEntities", startNanos));
    }
  }

  @Override
  public TargetDaoRetrieveEntityResponse retrieveEntity(
      TargetDaoRetrieveEntityRequest targetDaoRetrieveEntityRequest) {

    long startNanos = System.nanoTime();

    try {
      GrouperScim2ProvisionerConfiguration scimConfiguration = (GrouperScim2ProvisionerConfiguration) this
          .getGrouperProvisioner().retrieveGrouperProvisioningConfiguration();

      // we can retrieve by id or userPrincipalName, prefer id
      ProvisioningEntity grouperTargetEntity = targetDaoRetrieveEntityRequest
          .getTargetEntity();

      GrouperScim2User grouperScim2User = retrieveEntityHelper(
          scimConfiguration, grouperTargetEntity, scimConfiguration.isDisableEntitiesInsteadOfDelete());

      ProvisioningEntity targetEntity = grouperScim2User == null ? null
          : grouperScim2User.toProvisioningEntity();

      return new TargetDaoRetrieveEntityResponse(targetEntity);
    } finally {
      this.addTargetDaoTimingInfo(new TargetDaoTimingInfo("retrieveEntity", startNanos));
    }
  }

  public GrouperScim2User retrieveEntityHelper(
      GrouperScim2ProvisionerConfiguration scimConfiguration,
      ProvisioningEntity grouperTargetEntity, boolean filterInactive) {
    GrouperScim2User grouperScim2User = null;

    if (!StringUtils.isBlank(grouperTargetEntity.getId())) {
      grouperScim2User = GrouperScim2ApiCommands.retrieveScimUser(
          scimConfiguration.getBearerTokenExternalSystemConfigId(), 
          scimConfiguration.getAcceptHeader(),
          "id", 
          grouperTargetEntity.getId());
      if (filterInactive && grouperScim2User != null && !GrouperUtil.booleanValue(grouperScim2User.getActive(), true)) {
        grouperScim2User = null;
      }

    }

    String userName = grouperTargetEntity
        .retrieveAttributeValueString("userName");
    if (grouperScim2User == null && !StringUtils.isBlank(userName)) {
      grouperScim2User = GrouperScim2ApiCommands.retrieveScimUser(
          scimConfiguration.getBearerTokenExternalSystemConfigId(), scimConfiguration.getAcceptHeader(), "userName", userName);
      if (filterInactive && grouperScim2User != null && !GrouperUtil.booleanValue(grouperScim2User.getActive(), true)) {
        grouperScim2User = null;
      }
    }
    return grouperScim2User;
  }

  @Override
  public TargetDaoRetrieveGroupResponse retrieveGroup(
      TargetDaoRetrieveGroupRequest targetDaoRetrieveGroupRequest) {

    long startNanos = System.nanoTime();

    try {
      GrouperScim2ProvisionerConfiguration scimConfiguration = (GrouperScim2ProvisionerConfiguration) this
          .getGrouperProvisioner().retrieveGrouperProvisioningConfiguration();

      // we can retrieve by id or displayName

      ProvisioningGroup grouperTargetGroup = targetDaoRetrieveGroupRequest
          .getTargetGroup();

      GrouperScim2Group grouperScim2Group = retrieveGroupHelper(
          scimConfiguration, grouperTargetGroup, scimConfiguration.isDisableGroupsInsteadOfDelete());

      ProvisioningGroup targetGroup = grouperScim2Group == null ? null
          : grouperScim2Group.toProvisioningGroup();

      return new TargetDaoRetrieveGroupResponse(targetGroup);

    } finally {
      this.addTargetDaoTimingInfo(new TargetDaoTimingInfo("retrieveGroup", startNanos));
    }
  }

  public GrouperScim2Group retrieveGroupHelper(
      GrouperScim2ProvisionerConfiguration scimConfiguration,
      ProvisioningGroup grouperTargetGroup, boolean filterInactive) {
    
    GrouperScim2Group grouperScim2Group = null;

    if (!StringUtils.isBlank(grouperTargetGroup.getId())) {
      grouperScim2Group = GrouperScim2ApiCommands.retrieveScimGroup(
          scimConfiguration.getBearerTokenExternalSystemConfigId(),
          scimConfiguration.getAcceptHeader(),
          "id",
          grouperTargetGroup.getId());
      if (filterInactive && grouperScim2Group != null &&  !GrouperUtil.booleanValue(grouperScim2Group.getActive(), true)) {
        grouperScim2Group = null;
      }
    }

    String displayName = grouperTargetGroup.getDisplayName();
    if (grouperScim2Group == null && !StringUtils.isBlank(displayName)) {
      grouperScim2Group = GrouperScim2ApiCommands.retrieveScimGroup(
          scimConfiguration.getBearerTokenExternalSystemConfigId(), 
          scimConfiguration.getAcceptHeader(),
          "displayName", displayName);
      if (filterInactive && grouperScim2Group != null && !GrouperUtil.booleanValue(grouperScim2Group.getActive(), true)) {
        grouperScim2Group = null;
      }
    }
    return grouperScim2Group;
  }

  @Override
  public TargetDaoInsertGroupResponse insertGroup(TargetDaoInsertGroupRequest targetDaoInsertGroupRequest) {
    long startNanos = System.nanoTime();
    ProvisioningGroup targetGroup = targetDaoInsertGroupRequest.getTargetGroup();

    try {
      GrouperScim2ProvisionerConfiguration scimConfiguration = (GrouperScim2ProvisionerConfiguration) this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration();
      
      // lets make sure we are doing the right thing
      Set<String> fieldNamesToInsert = new HashSet<String>();
      for (ProvisioningObjectChange provisioningObjectChange : GrouperUtil.nonNull(targetGroup.getInternal_objectChanges())) {
        String fieldName = provisioningObjectChange.getAttributeName();
        if (provisioningObjectChange.getProvisioningObjectChangeAction() == ProvisioningObjectChangeAction.insert) {
          fieldNamesToInsert.add(fieldName);
        }
      }
      
      GrouperScim2Group grouperScim2Group = null;
      
      if (scimConfiguration.isDisableGroupsInsteadOfDelete()) {

        grouperScim2Group = retrieveGroupHelper(scimConfiguration, targetGroup, false);
        
        if (grouperScim2Group != null && !GrouperUtil.booleanValue(grouperScim2Group.getActive(), true)) {
          Map<String, ProvisioningObjectChangeAction> attributeToChange = new HashMap<>();
          grouperScim2Group.setActive(true);
          attributeToChange.put("active", ProvisioningObjectChangeAction.update);
          GrouperScim2ApiCommands.patchScimGroup(scimConfiguration.getBearerTokenExternalSystemConfigId(), 
              scimConfiguration.getAcceptHeader(), grouperScim2Group, attributeToChange);

        }
        
      }
      
      if (grouperScim2Group == null) {
        
        grouperScim2Group = GrouperScim2Group.fromProvisioningGroup(targetGroup, null);
        
        GrouperScim2ApiCommands.createScimGroup(scimConfiguration.getBearerTokenExternalSystemConfigId(), 
            scimConfiguration.getAcceptHeader(),
            grouperScim2Group, fieldNamesToInsert);
      }
      
      targetGroup.setProvisioned(true);

      for (ProvisioningObjectChange provisioningObjectChange : GrouperUtil.nonNull(targetGroup.getInternal_objectChanges())) {
        provisioningObjectChange.setProvisioned(true);
      }

      return new TargetDaoInsertGroupResponse();
    } catch (Exception e) {
      targetGroup.setProvisioned(false);
      for (ProvisioningObjectChange provisioningObjectChange : GrouperUtil.nonNull(targetGroup.getInternal_objectChanges())) {
        provisioningObjectChange.setProvisioned(false);
      }
      
      throw e;
    } finally {
      this.addTargetDaoTimingInfo(new TargetDaoTimingInfo("insertGroup", startNanos));
    }
  }

  @Override
  public TargetDaoInsertMembershipsResponse insertMemberships(TargetDaoInsertMembershipsRequest targetDaoInsertMembershipsRequest) {
    long startNanos = System.nanoTime();
    List<ProvisioningMembership> targetMemberships = targetDaoInsertMembershipsRequest.getTargetMemberships();

    try {
      
      GrouperScim2ProvisionerConfiguration scimConfiguration = (GrouperScim2ProvisionerConfiguration) this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration();

      // lets collate by group
      Map<String, List<String>> groupIdToUserIds = new LinkedHashMap<String, List<String>>();

      // keep track to mark as complete
      Map<MultiKey, ProvisioningMembership> groupIdUserIdToProvisioningMembership = new HashMap<MultiKey, ProvisioningMembership>();
      
      for (ProvisioningMembership targetMembership : targetMemberships) {

        groupIdUserIdToProvisioningMembership.put(new MultiKey(targetMembership.getProvisioningGroupId(), targetMembership.getProvisioningEntityId()), targetMembership);
        
        List<String> userIds = groupIdToUserIds.get(targetMembership.getProvisioningGroupId());
        if (userIds == null) {
          userIds = new ArrayList<String>();
          groupIdToUserIds.put(targetMembership.getProvisioningGroupId(), userIds);
        }
        userIds.add(targetMembership.getProvisioningEntityId());
      }

      // send batches by group
      for (String groupId : groupIdToUserIds.keySet()) {

        List<String> userIds = groupIdToUserIds.get(groupId);
        
        RuntimeException runtimeException = null;
        try {
          GrouperScim2ApiCommands.createScimMemberships(scimConfiguration.getBearerTokenExternalSystemConfigId(),
              scimConfiguration.getAcceptHeader(),
              groupId, new HashSet<String>(userIds));
        } catch (RuntimeException e) {
          runtimeException = e;
        }
        boolean success = runtimeException == null;
        for (String userId : userIds) {
          ProvisioningMembership targetMembership = groupIdUserIdToProvisioningMembership.get(new MultiKey(groupId, userId));
          
          targetMembership.setProvisioned(success);
          targetMembership.setException(runtimeException);
          for (ProvisioningObjectChange provisioningObjectChange : GrouperUtil.nonNull(targetMembership.getInternal_objectChanges())) {
            provisioningObjectChange.setProvisioned(success);
            
          }
        }
      }
      
      return new TargetDaoInsertMembershipsResponse();
    } finally {
      this.addTargetDaoTimingInfo(new TargetDaoTimingInfo("insertMemberships", startNanos));
    }
  }

  @Override
  public TargetDaoReplaceGroupMembershipsResponse replaceGroupMemberships(TargetDaoReplaceGroupMembershipsRequest targetDaoReplaceGroupMembershipsRequest) {

    long startNanos = System.nanoTime();
    List<ProvisioningMembership> targetMemberships = targetDaoReplaceGroupMembershipsRequest.getTargetMemberships();
    ProvisioningGroup targetGroup = targetDaoReplaceGroupMembershipsRequest.getTargetGroup();

    RuntimeException runtimeException = null;

    try {

      GrouperScim2ProvisionerConfiguration scimConfiguration = (GrouperScim2ProvisionerConfiguration) this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration();

      // lets collate by group
      Set<String> userIds = new HashSet<String>();

      for (ProvisioningMembership targetMembership : targetMemberships) {

        GrouperUtil.assertion(StringUtils.equals(targetGroup.getId(), targetMembership.getProvisioningGroupId()), 
            "Group id doesnt match: '" + targetGroup.getId() + "', '" + targetMembership.getProvisioningGroupId() + "'");

        userIds.add(targetMembership.getProvisioningEntityId());

      }

      try {
        GrouperScim2ApiCommands.replaceScimMemberships(scimConfiguration.getBearerTokenExternalSystemConfigId(), 
            scimConfiguration.getAcceptHeader(),
            targetGroup.getId(), new HashSet<String>(userIds));
      } catch (RuntimeException e) {
        runtimeException = e;
      }

      boolean success = runtimeException == null;

      for (ProvisioningMembership targetMembership : targetMemberships) {
        targetMembership.setProvisioned(success);
        targetMembership.setException(runtimeException);
        for (ProvisioningObjectChange provisioningObjectChange : GrouperUtil.nonNull(targetMembership.getInternal_objectChanges())) {
          provisioningObjectChange.setProvisioned(success);
        }
      }
      return new TargetDaoReplaceGroupMembershipsResponse();
    } finally {
      this.addTargetDaoTimingInfo(new TargetDaoTimingInfo("insertMemberships", startNanos));
    }
  }
  
  public TargetDaoDeleteMembershipsResponse deleteMemberships(TargetDaoDeleteMembershipsRequest targetDaoDeleteMembershipsRequest) {
    
    long startNanos = System.nanoTime();
    List<ProvisioningMembership> targetMemberships = targetDaoDeleteMembershipsRequest.getTargetMemberships();

    try {
      
      GrouperScim2ProvisionerConfiguration scimConfiguration = (GrouperScim2ProvisionerConfiguration) this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration();

      // lets collate by group
      Map<String, List<String>> groupIdToUserIds = new LinkedHashMap<String, List<String>>();

      // keep track to mark as complete
      Map<MultiKey, ProvisioningMembership> groupIdUserIdToProvisioningMembership = new HashMap<MultiKey, ProvisioningMembership>();
      
      for (ProvisioningMembership targetMembership : targetMemberships) {

        groupIdUserIdToProvisioningMembership.put(new MultiKey(targetMembership.getProvisioningGroupId(), targetMembership.getProvisioningEntityId()), targetMembership);
        
        List<String> userIds = groupIdToUserIds.get(targetMembership.getProvisioningGroupId());
        if (userIds == null) {
          userIds = new ArrayList<String>();
          groupIdToUserIds.put(targetMembership.getProvisioningGroupId(), userIds);
        }
        userIds.add(targetMembership.getProvisioningEntityId());
      }

      // send batches by group
      for (String groupId : groupIdToUserIds.keySet()) {

        List<String> userIds = groupIdToUserIds.get(groupId);
        
        RuntimeException runtimeException = null;
        try {
          GrouperScim2ApiCommands.deleteScimMemberships(scimConfiguration.getBearerTokenExternalSystemConfigId(),
              scimConfiguration.getAcceptHeader(),
              groupId, new HashSet<String>(userIds));
        } catch (RuntimeException e) {
          runtimeException = e;
        }
        boolean success = runtimeException == null;
        for (String userId : userIds) {
          ProvisioningMembership targetMembership = groupIdUserIdToProvisioningMembership.get(new MultiKey(groupId, userId));
          
          targetMembership.setProvisioned(success);
          targetMembership.setException(runtimeException);
          for (ProvisioningObjectChange provisioningObjectChange : GrouperUtil.nonNull(targetMembership.getInternal_objectChanges())) {
            provisioningObjectChange.setProvisioned(success);
            
          }
        }
      }
      
      return new TargetDaoDeleteMembershipsResponse();
    } finally {
      this.addTargetDaoTimingInfo(new TargetDaoTimingInfo("deleteMemberships", startNanos));
    }
  }
  @Override
  public TargetDaoDeleteGroupResponse deleteGroup(TargetDaoDeleteGroupRequest targetDaoDeleteGroupRequest) {
    
    long startNanos = System.nanoTime();
    ProvisioningGroup targetGroup = targetDaoDeleteGroupRequest.getTargetGroup();

    try {
      GrouperScim2ProvisionerConfiguration scimConfiguration = (GrouperScim2ProvisionerConfiguration) this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration();

      GrouperScim2Group grouperScim2Group = GrouperScim2Group.fromProvisioningGroup(targetGroup, null);

      if (scimConfiguration.isDisableGroupsInsteadOfDelete()) {
        
        Map<String, ProvisioningObjectChangeAction> attributeToChange = new HashMap<>();
        grouperScim2Group.setActive(false);
        attributeToChange.put("active", ProvisioningObjectChangeAction.update);
        GrouperScim2ApiCommands.patchScimGroup(scimConfiguration.getBearerTokenExternalSystemConfigId(), 
            scimConfiguration.getAcceptHeader(), grouperScim2Group, attributeToChange);
        
      } else {
        GrouperScim2ApiCommands.deleteScimGroup(scimConfiguration.getBearerTokenExternalSystemConfigId(), 
            scimConfiguration.getAcceptHeader(),
            grouperScim2Group.getId());
      }
      
      targetGroup.setProvisioned(true);

      for (ProvisioningObjectChange provisioningObjectChange : GrouperUtil.nonNull(targetGroup.getInternal_objectChanges())) {
        provisioningObjectChange.setProvisioned(true);
      }

      return new TargetDaoDeleteGroupResponse();
    } catch (Exception e) {
      targetGroup.setProvisioned(false);
      for (ProvisioningObjectChange provisioningObjectChange : GrouperUtil.nonNull(targetGroup.getInternal_objectChanges())) {
        provisioningObjectChange.setProvisioned(false);
      }

      throw e;
    } finally {
      this.addTargetDaoTimingInfo(new TargetDaoTimingInfo("deleteGroup", startNanos));
    }

  }

  @Override
  public void registerGrouperProvisionerDaoCapabilities(
      GrouperProvisionerDaoCapabilities grouperProvisionerDaoCapabilities) {
    grouperProvisionerDaoCapabilities.setDefaultBatchSize(1000);
    grouperProvisionerDaoCapabilities.setCanDeleteEntity(true);
    grouperProvisionerDaoCapabilities.setCanDeleteGroup(true);
    grouperProvisionerDaoCapabilities.setCanDeleteMemberships(true);
    grouperProvisionerDaoCapabilities.setCanInsertEntity(true);
    grouperProvisionerDaoCapabilities.setCanInsertGroup(true);
    grouperProvisionerDaoCapabilities.setCanInsertMemberships(true);
    grouperProvisionerDaoCapabilities.setCanReplaceGroupMemberships(true);
    grouperProvisionerDaoCapabilities.setCanRetrieveAllGroups(true);
    grouperProvisionerDaoCapabilities.setCanRetrieveAllEntities(true);
    grouperProvisionerDaoCapabilities.setCanRetrieveEntity(true);
    grouperProvisionerDaoCapabilities.setCanRetrieveGroup(true);
    grouperProvisionerDaoCapabilities.setCanUpdateEntity(true);
    grouperProvisionerDaoCapabilities.setCanUpdateGroup(true);
  }

  @Override
  public TargetDaoInsertEntityResponse insertEntity(TargetDaoInsertEntityRequest targetDaoInsertEntityRequest) {
    long startNanos = System.nanoTime();
    ProvisioningEntity targetEntity = targetDaoInsertEntityRequest.getTargetEntity();
  
    try {
      GrouperScim2ProvisionerConfiguration scimConfiguration = (GrouperScim2ProvisionerConfiguration) this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration();
      
      // lets make sure we are doing the right thing
      Set<String> fieldNamesToInsert = new HashSet<String>();
      for (ProvisioningObjectChange provisioningObjectChange : GrouperUtil.nonNull(targetEntity.getInternal_objectChanges())) {
        String fieldName = provisioningObjectChange.getAttributeName();
        if (provisioningObjectChange.getProvisioningObjectChangeAction() == ProvisioningObjectChangeAction.insert) {
          fieldNamesToInsert.add(fieldName);
        }
      }

      GrouperScim2User grouperScim2User = null;

      if (scimConfiguration.isDisableEntitiesInsteadOfDelete()) {

        grouperScim2User = retrieveEntityHelper(scimConfiguration, targetEntity, false);
        
        if (grouperScim2User != null && !GrouperUtil.booleanValue(grouperScim2User.getActive(), true)) {
          Map<String, ProvisioningObjectChangeAction> attributeToChange = new HashMap<>();
          grouperScim2User.setActive(true);
          attributeToChange.put("active", ProvisioningObjectChangeAction.update);
          GrouperScim2ApiCommands.patchScimUser(scimConfiguration.getBearerTokenExternalSystemConfigId(), 
              scimConfiguration.getAcceptHeader(), grouperScim2User, attributeToChange);

        }
        
      }
      
      if (grouperScim2User == null) {
        
        grouperScim2User = GrouperScim2User.fromProvisioningEntity(targetEntity, null);
        
        GrouperScim2ApiCommands.createScimUser(scimConfiguration.getBearerTokenExternalSystemConfigId(),
            scimConfiguration.getAcceptHeader(),
            grouperScim2User, fieldNamesToInsert);
      }

      targetEntity.setProvisioned(true);

      for (ProvisioningObjectChange provisioningObjectChange : GrouperUtil.nonNull(targetEntity.getInternal_objectChanges())) {
        provisioningObjectChange.setProvisioned(true);
      }
  
      return new TargetDaoInsertEntityResponse();
    } catch (Exception e) {
      targetEntity.setProvisioned(false);
      for (ProvisioningObjectChange provisioningObjectChange : GrouperUtil.nonNull(targetEntity.getInternal_objectChanges())) {
        provisioningObjectChange.setProvisioned(false);
      }
      
      throw e;
    } finally {
      this.addTargetDaoTimingInfo(new TargetDaoTimingInfo("insertEntity", startNanos));
    }
  }

  @Override
  public TargetDaoDeleteEntityResponse deleteEntity(TargetDaoDeleteEntityRequest targetDaoDeleteEntityRequest) {
    
    long startNanos = System.nanoTime();
    ProvisioningEntity targetEntity = targetDaoDeleteEntityRequest.getTargetEntity();
  
    try {
      GrouperScim2ProvisionerConfiguration scimConfiguration = (GrouperScim2ProvisionerConfiguration) this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration();
  
      GrouperScim2User grouperScim2User = GrouperScim2User.fromProvisioningEntity(targetEntity, null);
  
      if (scimConfiguration.isDisableEntitiesInsteadOfDelete()) {
        
        Map<String, ProvisioningObjectChangeAction> attributeToChange = new HashMap<>();
        grouperScim2User.setActive(false);
        attributeToChange.put("active", ProvisioningObjectChangeAction.update);
        GrouperScim2ApiCommands.patchScimUser(scimConfiguration.getBearerTokenExternalSystemConfigId(), 
            scimConfiguration.getAcceptHeader(), grouperScim2User, attributeToChange);
        
      } else {
        GrouperScim2ApiCommands.deleteScimUser(scimConfiguration.getBearerTokenExternalSystemConfigId(), 
            scimConfiguration.getAcceptHeader(),
            grouperScim2User.getId());
      }

  
      targetEntity.setProvisioned(true);
  
      for (ProvisioningObjectChange provisioningObjectChange : GrouperUtil.nonNull(targetEntity.getInternal_objectChanges())) {
        provisioningObjectChange.setProvisioned(true);
      }
  
      return new TargetDaoDeleteEntityResponse();
    } catch (Exception e) {
      targetEntity.setProvisioned(false);
      for (ProvisioningObjectChange provisioningObjectChange : GrouperUtil.nonNull(targetEntity.getInternal_objectChanges())) {
        provisioningObjectChange.setProvisioned(false);
      }
  
      throw e;
    } finally {
      this.addTargetDaoTimingInfo(new TargetDaoTimingInfo("deleteEntity", startNanos));
    }
  
  }

  @Override
  public TargetDaoUpdateEntityResponse updateEntity(TargetDaoUpdateEntityRequest targetDaoUpdateEntityRequest) {
    long startNanos = System.nanoTime();
    ProvisioningEntity targetEntity = targetDaoUpdateEntityRequest.getTargetEntity();
  
    try {
      GrouperScim2ProvisionerConfiguration scimConfiguration = (GrouperScim2ProvisionerConfiguration) 
          this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration();
      
      // lets make sure we are doing the right thing
      Map<String, ProvisioningObjectChangeAction> fieldNamesToProvisioningObjectChangeAction = new HashMap<String, ProvisioningObjectChangeAction>();
      
      for (ProvisioningObjectChange provisioningObjectChange : GrouperUtil.nonNull(targetEntity.getInternal_objectChanges())) {
        String fieldName = provisioningObjectChange.getAttributeName();
        fieldNamesToProvisioningObjectChangeAction.put(fieldName, provisioningObjectChange.getProvisioningObjectChangeAction());
      }
  
      GrouperScim2User grouperScim2User = GrouperScim2User.fromProvisioningEntity(targetEntity, null);
  
      GrouperScim2ApiCommands.patchScimUser(scimConfiguration.getBearerTokenExternalSystemConfigId(), 
          scimConfiguration.getAcceptHeader(),
          grouperScim2User, fieldNamesToProvisioningObjectChangeAction);
  
      targetEntity.setProvisioned(true);
  
      for (ProvisioningObjectChange provisioningObjectChange : GrouperUtil.nonNull(targetEntity.getInternal_objectChanges())) {
        provisioningObjectChange.setProvisioned(true);
      }
  
      return new TargetDaoUpdateEntityResponse();
    } catch (Exception e) {
      targetEntity.setProvisioned(false);
      for (ProvisioningObjectChange provisioningObjectChange : GrouperUtil.nonNull(targetEntity.getInternal_objectChanges())) {
        provisioningObjectChange.setProvisioned(false);
      }
      
      throw e;
    } finally {
      this.addTargetDaoTimingInfo(new TargetDaoTimingInfo("updateEntity", startNanos));
    }
  }

  @Override
  public TargetDaoUpdateGroupResponse updateGroup(TargetDaoUpdateGroupRequest targetDaoUpdateGroupRequest) {
    long startNanos = System.nanoTime();
    ProvisioningGroup targetGroup = targetDaoUpdateGroupRequest.getTargetGroup();
  
    try {
      GrouperScim2ProvisionerConfiguration scimConfiguration = (GrouperScim2ProvisionerConfiguration) 
          this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration();
      
      // lets make sure we are doing the right thing
      Map<String, ProvisioningObjectChangeAction> fieldNamesToProvisioningObjectChangeAction = new HashMap<String, ProvisioningObjectChangeAction>();
      
      for (ProvisioningObjectChange provisioningObjectChange : GrouperUtil.nonNull(targetGroup.getInternal_objectChanges())) {
        String fieldName = provisioningObjectChange.getAttributeName();
        fieldNamesToProvisioningObjectChangeAction.put(fieldName, provisioningObjectChange.getProvisioningObjectChangeAction());
      }
  
      GrouperScim2Group grouperScim2Group = GrouperScim2Group.fromProvisioningGroup(targetGroup, null);
  
      GrouperScim2ApiCommands.patchScimGroup(scimConfiguration.getBearerTokenExternalSystemConfigId(), 
          scimConfiguration.getAcceptHeader(),
          grouperScim2Group, fieldNamesToProvisioningObjectChangeAction);
  
      targetGroup.setProvisioned(true);
  
      for (ProvisioningObjectChange provisioningObjectChange : GrouperUtil.nonNull(targetGroup.getInternal_objectChanges())) {
        provisioningObjectChange.setProvisioned(true);
      }
  
      return new TargetDaoUpdateGroupResponse();
    } catch (Exception e) {
      targetGroup.setProvisioned(false);
      for (ProvisioningObjectChange provisioningObjectChange : GrouperUtil.nonNull(targetGroup.getInternal_objectChanges())) {
        provisioningObjectChange.setProvisioned(false);
      }
      
      throw e;
    } finally {
      this.addTargetDaoTimingInfo(new TargetDaoTimingInfo("updateGroup", startNanos));
    }
  }

}
