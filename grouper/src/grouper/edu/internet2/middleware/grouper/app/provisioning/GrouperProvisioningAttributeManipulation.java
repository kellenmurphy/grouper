package edu.internet2.middleware.grouper.app.provisioning;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.internet2.middleware.grouper.util.GrouperUtil;
import edu.internet2.middleware.grouperClient.jdbc.tableSync.GcGrouperSync;

public class GrouperProvisioningAttributeManipulation {

  public GrouperProvisioningAttributeManipulation() {
  }
  
  private GrouperProvisioner grouperProvisioner = null;

  public GcGrouperSync getGcGrouperSync() {
    return this.getGrouperProvisioner().getGcGrouperSync();
  }
  
  public GrouperProvisioner getGrouperProvisioner() {
    return grouperProvisioner;
  }
  
  public void setGrouperProvisioner(GrouperProvisioner grouperProvisioner) {
    this.grouperProvisioner = grouperProvisioner;
  }

  public void manipulateAttributesGroups(List<ProvisioningGroup> provisioningGroups) {
    
    Map<String, GrouperProvisioningConfigurationAttribute> groupAttributeNameToConfig = this.grouperProvisioner.retrieveGrouperProvisioningConfiguration().getTargetGroupAttributeNameToConfig();
    
    for (ProvisioningGroup provisioningGroup : GrouperUtil.nonNull(provisioningGroups)) {
      for (GrouperProvisioningConfigurationAttribute grouperProvisioningConfigurationAttribute : groupAttributeNameToConfig.values() ) {
        manipulateValue(provisioningGroup, grouperProvisioningConfigurationAttribute);
      }
    }
  }

  public void manipulateAttributesEntities(List<ProvisioningEntity> provisioningEntities) {
    
    Map<String, GrouperProvisioningConfigurationAttribute> entityAttributeNameToConfig = this.grouperProvisioner.retrieveGrouperProvisioningConfiguration().getTargetEntityAttributeNameToConfig();
    
    for (ProvisioningEntity provisioningEntity : GrouperUtil.nonNull(provisioningEntities)) {
      
      for (GrouperProvisioningConfigurationAttribute grouperProvisioningConfigurationAttribute : entityAttributeNameToConfig.values() ) {
        manipulateValue(provisioningEntity, grouperProvisioningConfigurationAttribute);
      }
    }
  }

  public void manipulateAttributesMemberships(List<ProvisioningMembership> provisioningMemberships) {
    
    Map<String, GrouperProvisioningConfigurationAttribute> membershipAttributeNameToConfig = this.grouperProvisioner.retrieveGrouperProvisioningConfiguration().getTargetMembershipAttributeNameToConfig();
    
    for (ProvisioningMembership provisioningMembership : GrouperUtil.nonNull(provisioningMemberships)) {
      
      for (GrouperProvisioningConfigurationAttribute grouperProvisioningConfigurationAttribute : membershipAttributeNameToConfig.values() ) {
        manipulateValue(provisioningMembership, grouperProvisioningConfigurationAttribute);
      }
    }
  }

  public void manipulateValue(ProvisioningUpdatable provisioningUpdatable,
      GrouperProvisioningConfigurationAttribute grouperProvisioningConfigurationAttribute) {

    Object currentValue = provisioningUpdatable.retrieveAttributeValue(grouperProvisioningConfigurationAttribute.getName());
    
    if (grouperProvisioningConfigurationAttribute == null || currentValue == null) {
      return;
    }
    GrouperProvisioningConfigurationAttributeValueType valueType = grouperProvisioningConfigurationAttribute.getValueType();
    if (valueType == null) {
      return;
    }
    
    if (grouperProvisioningConfigurationAttribute.isMultiValued()) {
      if (currentValue instanceof Set) {
        // we good
      } else if (currentValue instanceof Collection) {
        currentValue = new HashSet((Collection)currentValue);
        
      } else if (currentValue.getClass().isArray()) {
        Set newValue = new HashSet();
        for (int i=0;i<Array.getLength(currentValue); i++) {
          newValue.add(Array.get(currentValue, i));
        }
        currentValue = newValue;
      } else {
        currentValue = GrouperUtil.toSet(currentValue);
      }
      if (!valueType.correctTypeForSet((Set)currentValue)) {
        Set newValue = new HashSet();
        for (Object value : (Set)currentValue) {
          newValue.add(valueType.convert(value));
        }
        currentValue = newValue;
      }
      provisioningUpdatable.assignAttributeValue(grouperProvisioningConfigurationAttribute.getName(), currentValue);
      return;
    }
    // unwrap collections?
    if (currentValue instanceof Collection) {
      Collection collection = (Collection)currentValue;
      if (collection.size() == 1) {
        currentValue = collection.iterator().next();
      } else if (collection.size() == 0) {
        return;
      } else {
        throw new RuntimeException("Attribute should not be a collection: " + grouperProvisioningConfigurationAttribute.getName());
      }
    } else if (currentValue.getClass().isArray()) {
      if (Array.getLength(currentValue) == 1) {
        currentValue = Array.get(currentValue, 0);
      } else if (Array.getLength(currentValue) == 0) {
        return;
      } else {
        throw new RuntimeException("Attribute should not be an array: " + grouperProvisioningConfigurationAttribute.getName());
      }
    }
    provisioningUpdatable.assignAttributeValue(grouperProvisioningConfigurationAttribute.getName(), valueType.convert(currentValue));
  }

  public void filterGroups(List<ProvisioningGroup> provisioningGroups, boolean filterSelect, boolean filterInsert, boolean filterUpdate) {
    
    Map<String, GrouperProvisioningConfigurationAttribute> groupAttributeNameToConfig = this.grouperProvisioner.retrieveGrouperProvisioningConfiguration().getTargetGroupAttributeNameToConfig();
    Map<String, GrouperProvisioningConfigurationAttribute> groupFieldNameToConfig = this.grouperProvisioner.retrieveGrouperProvisioningConfiguration().getTargetGroupFieldNameToConfig();
    
    for (ProvisioningGroup provisioningGroup : GrouperUtil.nonNull(provisioningGroups)) {
      
      provisioningGroup.setId((String)filterField(provisioningGroup.getId(), groupFieldNameToConfig.get("id"),  filterSelect,  filterInsert,  filterUpdate));
      provisioningGroup.setDisplayName((String)filterField(provisioningGroup.getDisplayName(), groupFieldNameToConfig.get("displayName"),  filterSelect,  filterInsert,  filterUpdate));
      provisioningGroup.setName((String)filterField(provisioningGroup.getName(), groupFieldNameToConfig.get("name"),  filterSelect,  filterInsert,  filterUpdate));
      provisioningGroup.setIdIndex((Long)filterField(provisioningGroup.getIdIndex(), groupFieldNameToConfig.get("idIndex"),  filterSelect,  filterInsert,  filterUpdate));

      for (GrouperProvisioningConfigurationAttribute grouperProvisioningConfigurationAttribute : groupAttributeNameToConfig.values() ) {
        
        String attributeName = grouperProvisioningConfigurationAttribute.getName();
        if ((filterSelect && !grouperProvisioningConfigurationAttribute.isSelect())
            || (filterInsert && !grouperProvisioningConfigurationAttribute.isInsert())
            || (filterUpdate && !grouperProvisioningConfigurationAttribute.isUpdate())){
          provisioningGroup.removeAttribute(attributeName);
        }
      }
    }
  }

  public void filterEntities(List<ProvisioningEntity> provisioningEntities, boolean filterSelect, boolean filterInsert, boolean filterUpdate) {
    
    Map<String, GrouperProvisioningConfigurationAttribute> entityAttributeNameToConfig = this.grouperProvisioner.retrieveGrouperProvisioningConfiguration().getTargetEntityAttributeNameToConfig();
    Map<String, GrouperProvisioningConfigurationAttribute> entityFieldNameToConfig = this.grouperProvisioner.retrieveGrouperProvisioningConfiguration().getTargetEntityFieldNameToConfig();
    
    for (ProvisioningEntity provisioningEntity : GrouperUtil.nonNull(provisioningEntities)) {
      
      provisioningEntity.setId((String)filterField(provisioningEntity.getId(), entityFieldNameToConfig.get("id"),  filterSelect,  filterInsert,  filterUpdate));
      provisioningEntity.setLoginId((String)filterField(provisioningEntity.getLoginId(), entityFieldNameToConfig.get("loginId"),  filterSelect,  filterInsert,  filterUpdate));
      provisioningEntity.setName((String)filterField(provisioningEntity.getName(), entityFieldNameToConfig.get("name"),  filterSelect,  filterInsert,  filterUpdate));
      provisioningEntity.setEmail((String)filterField(provisioningEntity.getEmail(), entityFieldNameToConfig.get("email"),  filterSelect,  filterInsert,  filterUpdate));

      for (GrouperProvisioningConfigurationAttribute grouperProvisioningConfigurationAttribute : entityAttributeNameToConfig.values() ) {
        
        String attributeName = grouperProvisioningConfigurationAttribute.getName();
        if ((filterSelect && !grouperProvisioningConfigurationAttribute.isSelect())
            || (filterInsert && !grouperProvisioningConfigurationAttribute.isInsert())
            || (filterUpdate && !grouperProvisioningConfigurationAttribute.isUpdate())){
          provisioningEntity.removeAttribute(attributeName);
        }
      }
    }
  }

  public void filterMemberships(List<ProvisioningMembership> provisioningMemberships, boolean filterSelect, boolean filterInsert, boolean filterUpdate) {
    
    Map<String, GrouperProvisioningConfigurationAttribute> membershipAttributeNameToConfig = this.grouperProvisioner.retrieveGrouperProvisioningConfiguration().getTargetMembershipAttributeNameToConfig();
    Map<String, GrouperProvisioningConfigurationAttribute> membershipFieldNameToConfig = this.grouperProvisioner.retrieveGrouperProvisioningConfiguration().getTargetMembershipFieldNameToConfig();
    
    for (ProvisioningMembership provisioningMembership : GrouperUtil.nonNull(provisioningMemberships)) {
      
      provisioningMembership.setId((String)filterField(provisioningMembership.getId(), membershipFieldNameToConfig.get("id"),  filterSelect,  filterInsert,  filterUpdate));
      provisioningMembership.setProvisioningEntityId((String)filterField(provisioningMembership.getProvisioningEntityId(), membershipFieldNameToConfig.get("provisioningEntityId"),  filterSelect,  filterInsert,  filterUpdate));
      provisioningMembership.setProvisioningGroupId((String)filterField(provisioningMembership.getProvisioningGroupId(), membershipFieldNameToConfig.get("provisioningGroupId"),  filterSelect,  filterInsert,  filterUpdate));

      for (GrouperProvisioningConfigurationAttribute grouperProvisioningConfigurationAttribute : membershipAttributeNameToConfig.values() ) {
        
        String attributeName = grouperProvisioningConfigurationAttribute.getName();
        if ((filterSelect && !grouperProvisioningConfigurationAttribute.isSelect())
            || (filterInsert && !grouperProvisioningConfigurationAttribute.isInsert())
            || (filterUpdate && !grouperProvisioningConfigurationAttribute.isUpdate())){
          provisioningMembership.removeAttribute(attributeName);
        }
      }
    }
  }

  private Object filterField(Object value,
      GrouperProvisioningConfigurationAttribute grouperProvisioningConfigurationAttribute, boolean filterSelect, boolean filterInsert, boolean filterUpdate) {
    if (value == null || grouperProvisioningConfigurationAttribute == null) {
      return value;
    }
    if ((filterSelect && !grouperProvisioningConfigurationAttribute.isSelect())
        || (filterInsert && !grouperProvisioningConfigurationAttribute.isInsert())
        || (filterUpdate && !grouperProvisioningConfigurationAttribute.isUpdate())){
      return null;
    }
    return value;
      
  }



}
