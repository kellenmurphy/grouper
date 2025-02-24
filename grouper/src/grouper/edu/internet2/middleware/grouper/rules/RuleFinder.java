/**
 * Copyright 2018 Internet2
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

package edu.internet2.middleware.grouper.rules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import edu.internet2.middleware.grouper.Group;
import edu.internet2.middleware.grouper.GrouperSession;
import edu.internet2.middleware.grouper.Membership;
import edu.internet2.middleware.grouper.MembershipFinder;
import edu.internet2.middleware.grouper.Stem;
import edu.internet2.middleware.grouper.Stem.Scope;
import edu.internet2.middleware.grouper.StemFinder;
import edu.internet2.middleware.grouper.attr.AttributeDef;
import edu.internet2.middleware.grouper.attr.assign.AttributeAssign;
import edu.internet2.middleware.grouper.SubjectFinder;
import edu.internet2.middleware.grouper.internal.dao.QueryOptions;
import edu.internet2.middleware.grouper.misc.GrouperObject;
import edu.internet2.middleware.grouper.privs.AccessPrivilege;
import edu.internet2.middleware.grouper.privs.AttributeDefPrivilege;
import edu.internet2.middleware.grouper.privs.NamingPrivilege;
import edu.internet2.middleware.grouper.privs.PrivilegeHelper;
import edu.internet2.middleware.grouper.subj.SubjectHelper;
import edu.internet2.middleware.grouper.util.GrouperUtil;
import edu.internet2.middleware.subject.Subject;


/**
 *
 */
public class RuleFinder {

  /**
   * 
   */
  public RuleFinder() {
  }

  /**
   * find group inherit rules by stem name
   * @param stem
   * @return the rules
   */
  public static Set<RuleDefinition> findGroupPrivilegeInheritRules(Stem stem) {
    
    RuleEngine ruleEngine = RuleEngine.ruleEngine();
    
    //handle root stem... hmmm
    //we need to simulate a child object here
    String groupName = stem.isRootStem() ? "qwertyuiopasdfghjkl:b" : (stem.getName() + ":b");
    
    RuleCheck ruleCheck = new RuleCheck(RuleCheckType.groupCreate.name(), null, groupName, null, null, null);
    Set<RuleDefinition> ruleDefinitions = new HashSet<RuleDefinition>(GrouperUtil.nonNull(ruleEngine.ruleCheckIndexDefinitionsByNameOrIdInFolder(ruleCheck)));
    
    Iterator<RuleDefinition> iterator = ruleDefinitions.iterator();
    
    while (iterator.hasNext()) {
      
      RuleDefinition ruleDefinition = iterator.next();
      RuleThen ruleThen = ruleDefinition.getThen();
      RuleThenEnum ruleThenEnum = ruleThen.thenEnum();
      if (ruleThenEnum != RuleThenEnum.assignGroupPrivilegeToGroupId) {
        iterator.remove();
      }
    }
    
    return ruleDefinitions;

  }

  /**
   * find subject inherit rules by stem name.  Note, the calling subject must be able to see the rules
   * @param secure
   * @return the rules
   */
  public static Set<RuleDefinition> findPrivilegeInheritRules(boolean secure) {
    RuleEngine ruleEngine = RuleEngine.ruleEngine();

    Set<RuleDefinition> ruleDefinitions = new HashSet<RuleDefinition>();
    
    //go through rules and see which are about creating objects, and assigning privileges

    Subject grouperSessionSubject = GrouperSession.staticGrouperSession().getSubject();

    boolean wheelOrRoot = PrivilegeHelper.isWheelOrRoot(GrouperSession.staticGrouperSession().getSubject());

    for (RuleDefinition ruleDefinition : ruleEngine.getRuleDefinitions(true)) {
      if (ruleDefinition == null || ruleDefinition.getCheck() == null || ruleDefinition.getCheck().checkTypeEnum() == null) {
        continue;
      }
      switch (ruleDefinition.getCheck().checkTypeEnum()) {
        case attributeDefCreate:
        case groupCreate:
        case stemCreate:
          if (ruleDefinition.getThen() == null || ruleDefinition.getThen().thenEnum() == null) {
            continue;
          }
          switch (ruleDefinition.getThen().thenEnum()) {
            case assignAttributeDefPrivilegeToAttributeDefId:
            case assignGroupPrivilegeToGroupId:
            case assignStemPrivilegeToStemId:

              //get the stem
              Stem stem = ruleDefinition.getAttributeAssignType().getOwnerStemFailsafe();
              if (stem == null) {
                //why?
                continue;
              }

              boolean privilegeOk = false;
              if (wheelOrRoot || !secure) {
                privilegeOk = true;
              }
              if (!privilegeOk) {

                if (stem.canHavePrivilege(grouperSessionSubject, NamingPrivilege.STEM_ADMIN.getName(), false)) {
                  privilegeOk = true;
                }
              }
              if (!privilegeOk) {
                Scope scope = Scope.valueOfIgnoreCase(ruleDefinition.getCheck().getCheckStemScope(), false);
                
                if (scope == null) {
                  continue;
                }
                
                switch (ruleDefinition.getCheck().checkTypeEnum()) {
                  case attributeDefCreate:

                    //is there a membership for attribute def?
                    int membershipSize = new MembershipFinder().assignStem(stem).assignStemScope(scope)
                      .assignField(AttributeDefPrivilege.ATTR_ADMIN.getField())
                      .assignQueryOptionsForAttributeDef(new QueryOptions().paging(1, 1, false)).findMembershipsMembers().size();
                    
                    privilegeOk = membershipSize > 0;
                    
                    break;
                  case groupCreate:

                    //is there a membership for group?
                    membershipSize = new MembershipFinder().assignStem(stem).assignStemScope(scope)
                      .assignField(AccessPrivilege.ADMIN.getField())
                      .assignQueryOptionsForGroup(new QueryOptions().paging(1, 1, false)).findMembershipsMembers().size();
                    
                    privilegeOk = membershipSize > 0;

                    break;
                  case stemCreate:
                    
                    //is there a membership for stem?
                    membershipSize = new MembershipFinder().assignStem(stem).assignStemScope(scope)
                      .assignField(NamingPrivilege.STEM_ADMIN.getField())
                      .assignQueryOptionsForStem(new QueryOptions().paging(1, 1, false)).findMembershipsMembers().size();
                    
                    privilegeOk = membershipSize > 0;

                    break;
                  default:
                    
                }
              }
              if (!privilegeOk) {
                continue;
              }
              ruleDefinitions.add(ruleDefinition);
              break;
            default: 
                
          }
          default:
      }
    }
    
    return ruleDefinitions;

  }
    

  /**
   * find subject inherit rules by stem name.  Note, the calling subject must be able to see the rules
   * @param subject
   * @param secure
   * @return the rules
   */
  public static Set<RuleDefinition> findSubjectPrivilegeInheritRules(Subject subject, boolean secure) {
    
    RuleEngine ruleEngine = RuleEngine.ruleEngine();

    Set<RuleDefinition> ruleDefinitions = new HashSet<RuleDefinition>();
    
    //go through rules and see which are about creating objects, and assigning privileges

    Subject grouperSessionSubject = GrouperSession.staticGrouperSession().getSubject();

    //if checking security
    boolean wheelOrRoot = PrivilegeHelper.isWheelOrRoot(GrouperSession.staticGrouperSession().getSubject());

    for (RuleDefinition ruleDefinition : ruleEngine.getRuleDefinitions(true)) {
      if (ruleDefinition.getThen() == null || ruleDefinition.getThen().thenEnum() == null) {
        continue;
      }
      switch (ruleDefinition.getCheck().checkTypeEnum()) {
        case attributeDefCreate:
        case groupCreate:
        case stemCreate:
          switch (ruleDefinition.getThen().thenEnum()) {
            case assignAttributeDefPrivilegeToAttributeDefId:
            case assignGroupPrivilegeToGroupId:
            case assignStemPrivilegeToStemId:
              
              //see if the subject matches
              String subjectPackedString = ruleDefinition.getThen().getThenEnumArg0();
              Subject currentSubject = SubjectFinder.findByPackedSubjectString(subjectPackedString, false);
              if (subject != null && SubjectHelper.eq(currentSubject, subject)) {
                
                //get the stem
                Stem stem = ruleDefinition.getAttributeAssignType().getOwnerStemFailsafe();
                if (stem == null) {
                  //why?
                  continue;
                }

                boolean privilegeOk = false;
                if (wheelOrRoot || !secure) {
                  privilegeOk = true;
                }
                if (!privilegeOk) {

                  if (!stem.canHavePrivilege(grouperSessionSubject, NamingPrivilege.STEM_ADMIN.getName(), false)) {
                    privilegeOk = true;
                  }
                }
                if (!privilegeOk) {
                  Scope scope = Scope.valueOfIgnoreCase(ruleDefinition.getCheck().getCheckStemScope(), false);
                  
                  if (scope == null) {
                    continue;
                  }
                  
                  switch (ruleDefinition.getCheck().checkTypeEnum()) {
                    case attributeDefCreate:

                      //is there a membership for attribute def?
                      Membership membership = new MembershipFinder().assignScope(stem.getName()).assignStemScope(scope)
                        .assignField(AttributeDefPrivilege.ATTR_ADMIN.getField())
                        .assignQueryOptionsForAttributeDef(new QueryOptions().paging(1, 1, false)).findMembership(false);
                      
                      privilegeOk = membership != null;
                      
                      break;
                    case groupCreate:

                      //is there a membership for group?
                      membership = new MembershipFinder().assignScope(stem.getName()).assignStemScope(scope)
                        .assignField(AccessPrivilege.ADMIN.getField())
                        .assignQueryOptionsForGroup(new QueryOptions().paging(1, 1, false)).findMembership(false);
                      
                      privilegeOk = membership != null;

                      break;
                    case stemCreate:
                      
                      //is there a membership for stem?
                      membership = new MembershipFinder().assignScope(stem.getName()).assignStemScope(scope)
                        .assignField(NamingPrivilege.STEM_ADMIN.getField())
                        .assignQueryOptionsForStem(new QueryOptions().paging(1, 1, false)).findMembership(false);
                      
                      privilegeOk = membership != null;

                      break;
                    default:
                      
                  }
                  
                }
                if (!privilegeOk) {
                  continue;
                }
                ruleDefinitions.add(ruleDefinition);
                
              }
              default:
          }
          default:
      }
    }
    
    return ruleDefinitions;

  }
  
  /**
   * find folder inherit rules by stem name
   * @param stem
   * @return the rules
   */
  public static Set<RuleDefinition> findFolderPrivilegeInheritRules(Stem stem) {
    
    RuleEngine ruleEngine = RuleEngine.ruleEngine();

    //handle root stem... hmmm
    //we need to simulate a child object here
    String stemName = stem.isRootStem() ? "qwertyuiopasdfghjkl:b" : (stem.getName() + ":b");

    RuleCheck ruleCheck = new RuleCheck(RuleCheckType.stemCreate.name(), null, stemName, null, null, null);
    Set<RuleDefinition> ruleDefinitions = ruleEngine.ruleCheckIndexDefinitionsByNameOrIdInFolder(ruleCheck);

    Iterator<RuleDefinition> iterator = ruleDefinitions.iterator();
    
    while (iterator.hasNext()) {
      
      RuleDefinition ruleDefinition = iterator.next();
      RuleThen ruleThen = ruleDefinition.getThen();
      RuleThenEnum ruleThenEnum = ruleThen.thenEnum();
      if (ruleThenEnum != RuleThenEnum.assignStemPrivilegeToStemId) {
        iterator.remove();
      }
    }
    
    return ruleDefinitions;

  }
  
  
  /**
   * find attribute def inherit rules by stem name
   * @param stem
   * @return the rules
   */
  public static Set<RuleDefinition> findAttributeDefPrivilegeInheritRules(Stem stem) {
    
    RuleEngine ruleEngine = RuleEngine.ruleEngine();

    //handle root stem... hmmm
    //we need to simulate a child object here
    String attributeDefName = stem.isRootStem() ? "qwertyuiopasdfghjkl:b" : (stem.getName() + ":b");

    RuleCheck ruleCheck = new RuleCheck(RuleCheckType.attributeDefCreate.name(), null, attributeDefName, null, null, null);
    Set<RuleDefinition> ruleDefinitions = ruleEngine.ruleCheckIndexDefinitionsByNameOrIdInFolder(ruleCheck);
    
    Iterator<RuleDefinition> iterator = ruleDefinitions.iterator();
    
    while (iterator.hasNext()) {
      
      RuleDefinition ruleDefinition = iterator.next();
      RuleThen ruleThen = ruleDefinition.getThen();
      RuleThenEnum ruleThenEnum = ruleThen.thenEnum();
      if (ruleThenEnum != RuleThenEnum.assignAttributeDefPrivilegeToAttributeDefId) {
        iterator.remove();
      }
    }
    
    return ruleDefinitions;

  }
  
  
  public static Set<RuleDefinition> retrieveRuleDefinitionsForSubject(Subject subject) {
    
    Set<RuleDefinition> ruleDefinitions = new HashSet<>();
    
    RuleEngine ruleEngine = RuleEngine.ruleEngine();
    
    for (RuleDefinition ruleDefinition : ruleEngine.getRuleDefinitions(false)) {
      ruleDefinitions.add(ruleDefinition);
    }
    
    return ruleDefinitions;
    
  }
  
  /**
   * get all the rule definitions that are associated with the given grouper object. 
   * @param grouperObject
   * @return
   */
  public static Set<RuleDefinition> retrieveRuleDefinitionsForGrouperObject(GrouperObject grouperObject) {
    
    Set<RuleDefinition> ruleDefinitions = new HashSet<>();
    
    RuleEngine ruleEngine = RuleEngine.ruleEngine();
    
    Set<String> parentStemNames = GrouperUtil.findParentStemNames(grouperObject.getName());
    Set<Stem> parentStems = StemFinder.findByNames(parentStemNames, false);
    
    Set<String> parentIds = new HashSet<>();
    for (Stem stem: GrouperUtil.nonNull(parentStems)) {
      parentIds.add(stem.getUuid());
    }
    
    for (RuleDefinition ruleDefinition : ruleEngine.getRuleDefinitions(false)) {
      
      AttributeAssign attributeAssignType = ruleDefinition.getAttributeAssignType();
      RuleCheck ruleCheck = ruleDefinition.getCheck();
      
      {
        if (attributeAssignType != null) {
          if (StringUtils.equals(attributeAssignType.getOwnerGroupId(), grouperObject.getId())) {
            ruleDefinitions.add(ruleDefinition);
            continue;
          } else if (StringUtils.equals(attributeAssignType.getOwnerStemId(), grouperObject.getId())) {
            ruleDefinitions.add(ruleDefinition);
            continue;
          }
          
        }
      }
      
      {
        if (ruleCheck != null) {
          if (StringUtils.equals(ruleCheck.getCheckOwnerId(), grouperObject.getId())) {
            ruleDefinitions.add(ruleDefinition);
            continue;
          } else if (StringUtils.equals(ruleCheck.getCheckOwnerName(), grouperObject.getName())) {
            ruleDefinitions.add(ruleDefinition);
            continue;
          }
        }
      }
      
      {
        RuleIfCondition ifCondition = ruleDefinition.getIfCondition();
        if (ifCondition != null) {
          if (StringUtils.equals(ifCondition.getIfOwnerId(), grouperObject.getId())) {
            ruleDefinitions.add(ruleDefinition);
            continue;
          } else if (StringUtils.equals(ifCondition.getIfOwnerName(), grouperObject.getName())) {
            ruleDefinitions.add(ruleDefinition);
            continue;
          }
        }
      }
      
      {
        if (StringUtils.isNotBlank(attributeAssignType.getOwnerStemId()) && parentIds.contains(attributeAssignType.getOwnerStemId()) 
            && ruleCheck != null && StringUtils.equalsIgnoreCase(ruleCheck.getCheckStemScope(), "SUB")) {
          ruleDefinitions.add(ruleDefinition);
          continue;
        }
      }
      
    }
    
    return ruleDefinitions;
    
  }
  
  /**
   * finding rule definitions that refer to these objects but not defined in these objects
   * @param grouperObjects
   * @return
   */
  public static Set<RuleDefinition> retrieveRuleDefinitionsDeleteCountForGrouperObjects(Set<GrouperObject> grouperObjects) {
    
    Set<RuleDefinition> ruleDefinitions = new HashSet<>();
    
    RuleEngine ruleEngine = RuleEngine.ruleEngine();
    
    Set<String> grouperObjectIds = new HashSet<>();
    Set<String> grouperObjectNames = new HashSet<>();
    for (GrouperObject grouperObject: grouperObjects) {
      grouperObjectIds.add(grouperObject.getId());
      grouperObjectNames.add(grouperObject.getName());
    }
    
    for (RuleDefinition ruleDefinition : ruleEngine.getRuleDefinitions(false)) {
      
      AttributeAssign attributeAssignType = ruleDefinition.getAttributeAssignType();
      RuleCheck ruleCheck = ruleDefinition.getCheck();
      
      {
        if (attributeAssignType != null) {
          
          if (grouperObjectIds.contains(attributeAssignType.getOwnerGroupId())) {
            continue;
          } else if (grouperObjectIds.contains(attributeAssignType.getOwnerStemId())) {
            continue;
          } else if (grouperObjectIds.contains(attributeAssignType.getOwnerAttributeDefId())) {
            continue;
          }
        }
      }
      
      {
        if (ruleCheck != null) {
          if (grouperObjectIds.contains(ruleCheck.getCheckOwnerId())) {
            ruleDefinitions.add(ruleDefinition);
            continue;
          } else if (grouperObjectNames.contains(ruleCheck.getCheckOwnerName())) {
            ruleDefinitions.add(ruleDefinition);
            continue;
          }
        }
      }
      
      {
        RuleIfCondition ifCondition = ruleDefinition.getIfCondition();
        if (ifCondition != null) {
          if (grouperObjectIds.contains(ifCondition.getIfOwnerId())) {
            ruleDefinitions.add(ruleDefinition);
            continue;
          } else if (grouperObjectNames.contains(ifCondition.getIfOwnerName())) {
            ruleDefinitions.add(ruleDefinition);
            continue;
          }
        }
      }
      
    }
    
    return ruleDefinitions;
    
  }
  
//  /**
//   * get the count of rule definitions that can be deleted 
//   * @param stem
//   * @param includeGroups
//   * @param includeStems
//   * @param includeAttributeDefs
//   * @param scope
//   * @return
//   */
//  public static int retrieveRuleDefinitionsDeleteCountForStem(Stem stem, boolean includeGroups, boolean includeStems, boolean includeAttributeDefs, Scope scope) {
//    
//    List<GrouperObject> grouperObjects = new ArrayList<>();
//    grouperObjects.add(stem);
//    if (includeStems) {      
//      Set<Stem> childStems = stem.getChildStems(scope);
//      grouperObjects.addAll(childStems);
//    }
//    if (includeGroups) {
//      Set<Group> childGroups = stem.getChildGroups(scope);
//      grouperObjects.addAll(childGroups);
//    }
//    
//    if (includeAttributeDefs) {
//      Set<AttributeDef> attributeDefs = stem.deleteAttributeDefs(false, true, scope);
//      grouperObjects.addAll(attributeDefs);
//    }
//    
//    Set<RuleDefinition> ruleDefsToBeDeleted = retrieveRuleDefinitionsDeleteCountForGrouperObjects(grouperObjects);
//    
//    return ruleDefsToBeDeleted.size();
//    
//  }
  
  /**
   * get all the rule definitions that can be deleted when the given grouper object is deleted 
   * @param grouperObject
   * @return
   */
  public static Set<RuleDefinition> retrieveRuleDefinitionsToBeDeletedForGrouperObject(GrouperObject grouperObject) {
    
    Set<RuleDefinition> ruleDefinitions = new HashSet<>();
    
    RuleEngine ruleEngine = RuleEngine.ruleEngine();
    
    for (RuleDefinition ruleDefinition : ruleEngine.getRuleDefinitions(false)) {
      
      RuleCheck ruleCheck = ruleDefinition.getCheck();
      
      {
        if (ruleCheck != null) {
          if (StringUtils.equals(ruleCheck.getCheckOwnerId(), grouperObject.getId())) {
            ruleDefinitions.add(ruleDefinition);
            continue;
          } else if (StringUtils.equals(ruleCheck.getCheckOwnerName(), grouperObject.getName())) {
            ruleDefinitions.add(ruleDefinition);
            continue;
          }
        }
      }
      
      {
        RuleIfCondition ifCondition = ruleDefinition.getIfCondition();
        if (ifCondition != null) {
          if (StringUtils.equals(ifCondition.getIfOwnerId(), grouperObject.getId())) {
            ruleDefinitions.add(ruleDefinition);
            continue;
          } else if (StringUtils.equals(ifCondition.getIfOwnerName(), grouperObject.getName())) {
            ruleDefinitions.add(ruleDefinition);
            continue;
          }
        }
      }
      
    }
    
    return ruleDefinitions;
    
  }
  
  
  
}
