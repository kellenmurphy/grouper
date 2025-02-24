package edu.internet2.middleware.grouper.app.scim2Provisioning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.internet2.middleware.grouper.app.azure.GrouperAzureLog;
import edu.internet2.middleware.grouper.app.externalSystem.WsBearerTokenExternalSystem;
import edu.internet2.middleware.grouper.app.loader.GrouperLoaderConfig;
import edu.internet2.middleware.grouper.app.provisioning.ProvisioningObjectChangeAction;
import edu.internet2.middleware.grouper.misc.GrouperStartup;
import edu.internet2.middleware.grouper.util.GrouperHttpClient;
import edu.internet2.middleware.grouper.util.GrouperHttpMethod;
import edu.internet2.middleware.grouper.util.GrouperUtil;
import edu.internet2.middleware.grouperClient.util.GrouperClientUtils;

/**
 * This class interacts with the Microsoft Graph API.
 */
public class GrouperScim2ApiCommands {
  
  public static void main(String[] args) {
    
    GrouperStartup.startup();
    
//    for (int i=10;i<60;i++) {
//      GrouperScim2User grouperScimUser = new GrouperScim2User();
//      grouperScimUser.setActive(true);
//      grouperScimUser.setDisplayName("dispName" + i);
//      grouperScimUser.setEmailType("emailTy" + i);
//      grouperScimUser.setEmailValue("emailVal" + i+"@test.com");
//      grouperScimUser.setExternalId("extId" + i);
//      grouperScimUser.setFamilyName("famName" + i);
//      grouperScimUser.setFormattedName("formName" + i);
//      grouperScimUser.setGivenName("givName" + i);
//      grouperScimUser.setUserName("userNam" + i);
//      grouperScimUser.setUserType("userTyp" + i);
//    
//      createScimUser("githubLocal", grouperScimUser, null);
//    }
    
//      List<GrouperScim2User> grouperScim2Users = retrieveScimUsers("githubLocal");
//      System.out.println("Total users are "+grouperScim2Users.size());
//      for (GrouperScim2User grouperScim2User : grouperScim2Users) {
//        System.out.println(grouperScim2User);
//      }
    
      GrouperScim2User grouperScimUser = retrieveScimUser("atlassianCloudSso", null, "id", "eec55825-add9-4bc8-8dce-def0eb4422b3");
      System.out.println(grouperScimUser);
    
//      deleteScimUser("githubLocal", "49dbdc83e0744a68bd565ee9e2780e38");
    
//      Map<String, ProvisioningObjectChangeAction> fieldToAction = new HashMap<String, ProvisioningObjectChangeAction>();
//      fieldToAction.put("displayName", ProvisioningObjectChangeAction.update);
//      grouperScimUser.setDisplayName("newDisplayName");
//      fieldToAction.put("active", ProvisioningObjectChangeAction.update);
//      grouperScimUser.setActive(false);
//      patchScimUser("githubLocal", null, grouperScimUser, fieldToAction);
//    
//      grouperScimUser = retrieveScimUser("githubLocal",null, "id", "4a16500367134ad0954d0612d8203b56");
//      System.out.println(grouperScimUser);

    
  }

  public static void main1(String[] args) {

    GrouperStartup.startup();
//
    for (int i=3;i<10;i++) {
      GrouperScim2User grouperScimUser = new GrouperScim2User();
//      grouperScimUser.setActive(true);
//      grouperScimUser.setCostCenter("costCent" + i);
      grouperScimUser.setDisplayName("dispName" + i);
//      grouperScimUser.setEmailType("emailTy" + i);
//      grouperScimUser.setEmailValue("emailVal" + i);
//      grouperScimUser.setEmployeeNumber("123456" + i);
//      grouperScimUser.setExternalId("extId" + i);
      grouperScimUser.setFamilyName("famName" + i);
//      grouperScimUser.setFormattedName("formName" + i);
      grouperScimUser.setGivenName("givName" + i);
//      grouperScimUser.setMiddleName("midName" + i);
      grouperScimUser.setUserName("userNam" + i);
//      grouperScimUser.setUserType("userTyp" + i);
  
      createScimUser("awsConfigId", null, grouperScimUser, null);
    }
//
    for (int i=3;i<10;i++) {
      GrouperScim2Group grouperScimGroup = new GrouperScim2Group();
      grouperScimGroup.setDisplayName("dispName" + i);
    
      createScimGroup("awsConfigId", null, grouperScimGroup, null);
    }

//    List<GrouperScim2User> grouperScim2Users = retrieveScimUsers("awsConfigId");
//    for (GrouperScim2User grouperScim2User : grouperScim2Users) {
//      System.out.println(grouperScim2User);
//    }

//    GrouperScim2Group grouperScimGroup = retrieveScimGroup("awsConfigId", "id", "90677e12fd-bc261a14-c4a2-4d3f-b6a4-e22b72aa39cb");
//    GrouperScim2Group grouperScimGroup = retrieveScimGroup("awsConfigId", "displayName", "dispName4");
//    System.out.println(grouperScimGroup);

//        GrouperScim2User grouperScimUser = retrieveScimUser("awsConfigId", "userName", "userNam3");
//        System.out.println(grouperScimUser);
    
//        deleteScimUser("awsConfigId", "90677e12fd-97190f13-5312-42ca-aa11-76655a517453");
//        deleteScimGroup("awsConfigId", "90677e12fd-bc261a14-c4a2-4d3f-b6a4-e22b72aa39cb");
    
//    GrouperScim2User grouperScim2User = HibernateSession.byHqlStatic()
//      .createQuery("from GrouperScim2User where id = :theId")
//      .setString("theId", "2c3b311291b340a0b1cf52d899d69998")
//      .uniqueResult(GrouperScim2User.class);

//      String scimConfigId = "awsConfigId";
//      GrouperScim2User grouperScim2User = retrieveScimUser(scimConfigId, "userName", "userNam5");
//      System.out.println(grouperScim2User);
//
//      Map<String, ProvisioningObjectChangeAction> fieldToAction = new HashMap<String, ProvisioningObjectChangeAction>();
//      fieldToAction.put("active", ProvisioningObjectChangeAction.update);
//      grouperScim2User.setActive(false);
//      patchScimUser(scimConfigId, grouperScim2User, fieldToAction);
    //
    //  grouperScim2User = retrieveScimUser(scimConfigId, "userName", "userNam5");
    //  System.out.println(grouperScim2User);

//    String scimConfigId = "awsConfigId";
//    GrouperScim2User grouperScim2User = retrieveScimUser(scimConfigId, "userName", "userNam5");
//    System.out.println(grouperScim2User);
//    
//    Map<String, ProvisioningObjectChangeAction> fieldToAction = new HashMap<String, ProvisioningObjectChangeAction>();
//    fieldToAction.put("middleName", ProvisioningObjectChangeAction.update);
//    grouperScim2User.setMiddleName("myMid5");
//    patchScimUser(scimConfigId, grouperScim2User, fieldToAction);
//
//    grouperScim2User = retrieveScimUser(scimConfigId, "userName", "userNam5");
//    System.out.println(grouperScim2User);

    List<GrouperScim2User> grouperScim2Users = retrieveScimUsers("awsConfigId", null);
    for (GrouperScim2User grouperScim2User : grouperScim2Users) {
      System.out.println(grouperScim2User);
    }

//    createScimMemberships("awsLocal", "39eacb03852349aa90e740353f076dbb", GrouperUtil.toSet(
//        "442490540946446988319e21065e69d8", "5728def917c3496a8d6b83501fbb7939",
//        "650b48cc80fe46929b4911f20ac6cde1"));

//    createScimMemberships("awsConfigId", "90677e12fd-c68ad069-b25c-4913-b7f2-d07fe0f9a24c",
//        GrouperUtil.toSet("90677e12fd-5d3836ea-44d8-41b6-8f3c-2ae2a90404f7", 
//            "90677e12fd-2640018a-dffa-4206-9a6c-d2dd7aa70f79", "90677e12fd-fe48a053-9821-4e43-8a33-58c54ecded36"));

//    deleteScimMemberships("awsConfigId", "90677e12fd-c68ad069-b25c-4913-b7f2-d07fe0f9a24c",
//        GrouperUtil.toSet("90677e12fd-5d3836ea-44d8-41b6-8f3c-2ae2a90404f7",
//            "90677e12fd-2640018a-dffa-4206-9a6c-d2dd7aa70f79", "90677e12fd-fe48a053-9821-4e43-8a33-58c54ecded36"));

//    replaceScimMemberships("awsConfigId", "90677e12fd-c68ad069-b25c-4913-b7f2-d07fe0f9a24c", GrouperUtil.toSet(
//      //"1bc585968f974664be6144bbbecfc061",
//        "90677e12fd-2640018a-dffa-4206-9a6c-d2dd7aa70f79", "90677e12fd-fe48a053-9821-4e43-8a33-58c54ecded36"));

//    List<GrouperScim2Group> grouperScim2Groups = retrieveScimGroups("awsLocal");
//    for (GrouperScim2Group grouperScim2Group : grouperScim2Groups) {
//      System.out.println(grouperScim2Group);
//    }

//    deleteScimMemberships("awsReal", "906765e4ce-532dd5cb-bdc4-4046-923e-16dec5fe4b9a",
//        GrouperUtil.toSet("906765e4ce-cf77e628-89ed-41ac-93cd-cfa0a1bc965a", 
//            "906765e4ce-64270e1f-d1f7-4922-a487-69ac67c218b9", "906765e4ce-70466854-1917-403f-a193-c99cb07505dc"));

//    replaceScimMemberships("awsReal", "906765e4ce-532dd5cb-bdc4-4046-923e-16dec5fe4b9a",
//    GrouperUtil.toSet(//"906765e4ce-cf77e628-89ed-41ac-93cd-cfa0a1bc965a", 
//        "906765e4ce-64270e1f-d1f7-4922-a487-69ac67c218b9", "906765e4ce-70466854-1917-403f-a193-c99cb07505dc"));

  }

  
  /**
   * create a user
   * @param grouperScim2User
   * @return the result
   */
  public static void patchScimUser(String configId, String acceptHeader,
      GrouperScim2User grouperScim2User, Map<String, ProvisioningObjectChangeAction> fieldsToUpdate) {

    Map<String, Object> debugMap = new LinkedHashMap<String, Object>();

    debugMap.put("method", "patchScimUser");

    long startTime = System.nanoTime();

    try {

      //  {
      //    "schemas": [
      //        "urn:ietf:params:scim:api:messages:2.0:PatchOp"
      //    ],
      //    "Operations": [
      //        {
      //            "op": "replace",
      //            "path": "active",
      //            "value": false
      //        }
      //    ]
      // }

      ObjectNode jsonToSend = GrouperUtil.jsonJacksonNode();

      {
        ArrayNode schemasNode = GrouperUtil.jsonJacksonArrayNode();
        schemasNode.add("urn:ietf:params:scim:api:messages:2.0:PatchOp");
        jsonToSend.set("schemas", schemasNode);
      }

      ArrayNode operationsNode = GrouperUtil.jsonJacksonArrayNode();
      
//      if (fieldsToUpdate.containsKey("active")) {
//        throw new UnsupportedOperationException("active field cannnot be modified");
//      }

      if (fieldsToUpdate.containsKey("emailValue") || fieldsToUpdate.containsKey("emailType") || fieldsToUpdate.containsKey("emailValue2") || fieldsToUpdate.containsKey("emailType2")) {
        
        ProvisioningObjectChangeAction emailValueChangeAction = fieldsToUpdate.get("emailValue");
        ProvisioningObjectChangeAction emailValue2ChangeAction = fieldsToUpdate.get("emailValue2");

        // "value", "type" 
        //  {
        //    "op": "replace",
        //    "path": "active",
        //    "value": "false"
        //  }

        ProvisioningObjectChangeAction resultingAction = null;
        if (emailValueChangeAction == ProvisioningObjectChangeAction.delete && emailValue2ChangeAction == ProvisioningObjectChangeAction.delete) {
          resultingAction = ProvisioningObjectChangeAction.delete;
        } else if (emailValueChangeAction == ProvisioningObjectChangeAction.insert && emailValue2ChangeAction == ProvisioningObjectChangeAction.insert) {
          resultingAction = ProvisioningObjectChangeAction.insert;
        } else {
          resultingAction = ProvisioningObjectChangeAction.update;
        }

        ObjectNode operationNode = GrouperUtil.jsonJacksonNode();
        operationNode.put("path", "emails");

        if (resultingAction == ProvisioningObjectChangeAction.delete) {
          
          operationNode.put("op", "remove");
          
        } else {
          
          boolean hasEmail = !StringUtils.isBlank(grouperScim2User.getEmailValue());
          boolean hasEmail2 = !StringUtils.isBlank(grouperScim2User.getEmailValue2());
          
          operationNode.put("op", "replace");
          ArrayNode emailsNode = GrouperUtil.jsonJacksonArrayNode();
          
          if (hasEmail) {
            ObjectNode emailNode = GrouperUtil.jsonJacksonNode();
            emailNode.put("primary", true);
            emailNode.put("value", grouperScim2User.getEmailValue());
            if (!StringUtils.isBlank(grouperScim2User.getEmailType())) {
              emailNode.put("type", grouperScim2User.getEmailType());
            }
            emailsNode.add(emailNode);
          }
          if (hasEmail2) {
            ObjectNode emailNode = GrouperUtil.jsonJacksonNode();
            if (!hasEmail) {
              emailNode.put("primary", true);
            }
            emailNode.put("value", grouperScim2User.getEmailValue2());
            if (!StringUtils.isBlank(grouperScim2User.getEmailType2())) {
              emailNode.put("type", grouperScim2User.getEmailType2());
            }
            emailsNode.add(emailNode);
          }
          
          operationNode.set("value", emailsNode);
        }
        operationsNode.add(operationNode);
        
      }

      for (String fieldToUpdate : fieldsToUpdate.keySet()) {
        
        if (StringUtils.equals(fieldToUpdate, "emailValue") || StringUtils.equals(fieldToUpdate, "emailType") || StringUtils.equals(fieldToUpdate, "emailValue2") || StringUtils.equals(fieldToUpdate, "emailType2")) {
          continue;
        }
        
        ProvisioningObjectChangeAction provisioningObjectChangeAction = fieldsToUpdate.get(fieldToUpdate);
        
        ObjectNode operationNode = GrouperUtil.jsonJacksonNode();
        
        switch (provisioningObjectChangeAction) {
          case insert:
            operationNode.put("op", "add");
            operationNode.put("value", GrouperUtil.stringValue(GrouperUtil.fieldValue(grouperScim2User, fieldToUpdate)));
            break;
          case update:
            operationNode.put("op", "replace");
            Object resolvedObject = GrouperUtil.fieldValue(grouperScim2User, fieldToUpdate);
            if (resolvedObject != null && resolvedObject instanceof Boolean) {
              operationNode.put("value", GrouperUtil.booleanValue(resolvedObject));
            } else {
              operationNode.put("value", GrouperUtil.stringValue(GrouperUtil.fieldValue(grouperScim2User, fieldToUpdate)));
            }
            break;
          case delete:
            operationNode.put("op", "remove");
            break;
          default:
            throw new RuntimeException("Not expecting object change: " + provisioningObjectChangeAction);
        }
        
        if ("id".equals(fieldToUpdate)) {
          throw new RuntimeException("Cannot patch id field");
        } else if ("costCenter".equals(fieldToUpdate)) {
          fieldToUpdate = "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User.costCenter";
        } else if ("employeeNumber".equals(fieldToUpdate)) {
          fieldToUpdate = "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User.employeeNumber";
        } else if ("familyName".equals(fieldToUpdate)) {
          fieldToUpdate = "name.familyName";
        } else if ("formattedName".equals(fieldToUpdate)) {
          fieldToUpdate = "name.formatted";
        } else if ("middleName".equals(fieldToUpdate)) {
          fieldToUpdate = "name.middleName";
        } else if ("givenName".equals(fieldToUpdate)) {
          fieldToUpdate = "name.givenName";
        }
        operationNode.put("path", fieldToUpdate);
        operationsNode.add(operationNode);
        
      }
      
      jsonToSend.set("Operations", operationsNode);
      
      String jsonStringToSend = GrouperUtil.jsonJacksonToString(jsonToSend);

      executeMethod(debugMap, GrouperHttpMethod.patch, configId, "/Users/" + GrouperUtil.escapeUrlEncode(grouperScim2User.getId()),
          GrouperUtil.toSet(200, 204), new int[] { -1 }, jsonStringToSend, acceptHeader);

    } catch (RuntimeException re) {
      debugMap.put("exception", GrouperClientUtils.getFullStackTrace(re));
      throw re;
    } finally {
      GrouperScim2Log.scimLog(debugMap, startTime);
    }

  }

  /**
   * update a group
   * @param grouperScim2Group
   * @return the result
   */
  public static void patchScimGroup(String configId, String acceptHeader,
      GrouperScim2Group grouperScim2Group, Map<String, ProvisioningObjectChangeAction> fieldsToUpdate) {

    Map<String, Object> debugMap = new LinkedHashMap<String, Object>();

    debugMap.put("method", "patchScimGroup");

    long startTime = System.nanoTime();

    try {

      //  {
      //    "schemas": [
      //        "urn:ietf:params:scim:api:messages:2.0:PatchOp"
      //    ],
      //    "Operations": [
      //        {
      //            "op": "replace",
      //            "path": "active",
      //            "value": false
      //        }
      //    ]
      // }

      ObjectNode jsonToSend = GrouperUtil.jsonJacksonNode();

      {
        ArrayNode schemasNode = GrouperUtil.jsonJacksonArrayNode();
        schemasNode.add("urn:ietf:params:scim:api:messages:2.0:PatchOp");
        jsonToSend.set("schemas", schemasNode);
      }

      ArrayNode operationsNode = GrouperUtil.jsonJacksonArrayNode();
      
      for (String fieldToUpdate : fieldsToUpdate.keySet()) {
        
        ProvisioningObjectChangeAction provisioningObjectChangeAction = fieldsToUpdate.get(fieldToUpdate);
        
        ObjectNode operationNode = GrouperUtil.jsonJacksonNode();
        
        switch (provisioningObjectChangeAction) {
          case insert:
            operationNode.put("op", "add");
            operationNode.put("value", GrouperUtil.stringValue(GrouperUtil.fieldValue(grouperScim2Group, fieldToUpdate)));
            break;
          case update:
            operationNode.put("op", "replace");
            Object resolvedObject = GrouperUtil.fieldValue(grouperScim2Group, fieldToUpdate);
            if (resolvedObject != null && resolvedObject instanceof Boolean) {
              operationNode.put("value", GrouperUtil.booleanValue(resolvedObject));
            } else {
              operationNode.put("value", GrouperUtil.stringValue(GrouperUtil.fieldValue(grouperScim2Group, fieldToUpdate)));
            }
            break;
          case delete:
            operationNode.put("op", "remove");
            break;
          default:
            throw new RuntimeException("Not expecting object change: " + provisioningObjectChangeAction);
        }
        
        if ("id".equals(fieldToUpdate)) {
          throw new RuntimeException("Cannot patch id field");
        }
        operationNode.put("path", fieldToUpdate);
        operationsNode.add(operationNode);
        
      }
      
      jsonToSend.set("Operations", operationsNode);
      
      String jsonStringToSend = GrouperUtil.jsonJacksonToString(jsonToSend);

      executeMethod(debugMap, GrouperHttpMethod.patch, configId, "/Groups/" + GrouperUtil.escapeUrlEncode(grouperScim2Group.getId()),
          GrouperUtil.toSet(200, 204), new int[] { -1 }, jsonStringToSend, acceptHeader);

    } catch (RuntimeException re) {
      debugMap.put("exception", GrouperClientUtils.getFullStackTrace(re));
      throw re;
    } finally {
      GrouperScim2Log.scimLog(debugMap, startTime);
    }

  }

  /**
   * 
   * @param debugMap
   * @param configId
   * @param urlSuffix
   * @param grouperHttpMethod is GET, POST, DELETE, PUT
   * @return
   */
  private static GrouperHttpClient httpMethod(Map<String, Object> debugMap, String configId,
      String urlSuffix, GrouperHttpMethod grouperHttpMethod, String acceptHeader) {
    String endpoint = GrouperLoaderConfig.retrieveConfig()
        .propertyValueStringRequired(
            "grouper.wsBearerToken." + configId + ".endpoint");
    String url = GrouperUtil.stripLastSlashIfExists(endpoint);

    // in a nextLink, url is specified, so it might not have a prefix of the resourceEndpoint
    if (!urlSuffix.startsWith("http")) {
      url += (urlSuffix.startsWith("/") ? "" : "/") + urlSuffix;
    } else {
      url = urlSuffix;
    }
    debugMap.put("url", url);

    GrouperHttpClient grouperHttpClient = new GrouperHttpClient();
    grouperHttpClient.assignUrl(url);
    grouperHttpClient.assignGrouperHttpMethod(grouperHttpMethod);

    WsBearerTokenExternalSystem.attachAuthenticationToHttpClient(grouperHttpClient, configId);
    
    if (StringUtils.isNotBlank(acceptHeader)) {
      grouperHttpClient.addHeader("Accept", acceptHeader);
    }
    
    return grouperHttpClient;
  }

  private static JsonNode executeGetMethod(Map<String, Object> debugMap, String configId,
      String urlSuffix, String acceptHeader) {

    int[] returnCode = new int[] { -1 };
    
    JsonNode jsonNode = executeMethod(debugMap, GrouperHttpMethod.get, configId, urlSuffix,
        GrouperUtil.toSet(200, 404), returnCode, null, acceptHeader);
    
    if (returnCode[0] == 404) {
      return null;
    }
    
    return jsonNode;
  }

  private static JsonNode executeMethod(Map<String, Object> debugMap,
      GrouperHttpMethod httpMethodName, String configId,
      String urlSuffix, Set<Integer> allowedReturnCodes, int[] returnCode, String body, String acceptHeader) {

    int code = -1;
    String json = null;

    GrouperHttpClient grouperHttpClient = httpMethod(debugMap, configId, urlSuffix, httpMethodName, acceptHeader);

    if (!StringUtils.isBlank(body)) {
      if (httpMethodName.supportsRequestBody()) {
        grouperHttpClient.addHeader("Content-type", "application/json");
        grouperHttpClient.assignBody(body);
      } else {
        throw new RuntimeException("Cant attach a body if in method: " + httpMethodName);
      }
    }

    try {
      grouperHttpClient.executeRequest();
      
      code = grouperHttpClient.getResponseCode();
      returnCode[0] = code;
      json = grouperHttpClient.getResponseBody();

      if (!allowedReturnCodes.contains(code)) {
        throw new RuntimeException(
            "Invalid return code '" + code + "', expecting: " + GrouperUtil.setToString(allowedReturnCodes));
      }
      if (StringUtils.isBlank(json)) {
        return null;
      }
      try {
        JsonNode rootNode = GrouperUtil.jsonJacksonNode(json);
        return rootNode;
      } catch (Exception e) {
        throw new RuntimeException("Error parsing response: '" + json + "'", e);
      }
    } catch (Exception e) {
      throw new RuntimeException("Error connecting to '" 
          + httpMethodName + "' '" + debugMap.get("url") + ", body: '" +
          body + "' returnCode: " + code + ", response: '" + json + "'", e);
    }

  }

  /**
   * create a user
   * @param grouperScimUser
   * @return the result
   */
  public static GrouperScim2User createScimUser(String configId, String acceptHeader,
      GrouperScim2User grouperScimUser, Set<String> fieldsToUpdate) {

    Map<String, Object> debugMap = new LinkedHashMap<String, Object>();

    debugMap.put("method", "createScimUser");

    long startTime = System.nanoTime();

    try {

      ObjectNode jsonToSend = grouperScimUser.toJson(fieldsToUpdate);
      
      {
        ArrayNode schemasNode = GrouperUtil.jsonJacksonArrayNode();
        schemasNode.add("urn:ietf:params:scim:schemas:core:2.0:User");
        jsonToSend.set("schemas", schemasNode);
      }

      String jsonStringToSend = GrouperUtil.jsonJacksonToString(jsonToSend);

      JsonNode jsonNode = executeMethod(debugMap, GrouperHttpMethod.post, configId, "/Users",
          GrouperUtil.toSet(201), new int[] { -1 }, jsonStringToSend, acceptHeader);

      GrouperScim2User grouperScimUserResult = GrouperScim2User.fromJson(jsonNode);

      return grouperScimUserResult;
    } catch (RuntimeException re) {
      debugMap.put("exception", GrouperClientUtils.getFullStackTrace(re));
      throw re;
    } finally {
      GrouperScim2Log.scimLog(debugMap, startTime);
    }

  }

  /**
   * @param configId
   * @param fieldName id or userPrincipalName
   * @param fieldValue is value of id or userPrincipalName
   * @return
   */
  public static GrouperScim2User retrieveScimUser(String configId, String acceptHeader, String fieldName,
      String fieldValue) {

    Map<String, Object> debugMap = new LinkedHashMap<String, Object>();

    debugMap.put("method", "retrieveScimUser");

    long startTime = System.nanoTime();

    try {

      String urlSuffix = null;
      if (StringUtils.equals(fieldName, "id")) {
        urlSuffix = "/Users/" + GrouperUtil.escapeUrlEncode(fieldValue);
      } else {
        urlSuffix = "/Users?filter=" + GrouperUtil.escapeUrlEncode(fieldName)
            + "%20eq%20" + GrouperUtil.escapeUrlEncode("\"" + StringEscapeUtils.escapeJson(fieldValue) + "\"");
      }
      
      JsonNode jsonNode = executeGetMethod(debugMap, configId, urlSuffix, acceptHeader);
      
      if (jsonNode == null) {
        debugMap.put("found", false);
        return null;
      }

      if (StringUtils.equals(fieldName, "id")) {
        GrouperScim2User grouperScimUser = GrouperScim2User.fromJson(jsonNode);
        debugMap.put("found", grouperScimUser != null);
        return grouperScimUser;
      }

      if (!jsonNode.has("Resources")) {
        debugMap.put("found", false);
        return null;
      }
      
      ArrayNode resourcesNode = (ArrayNode)jsonNode.get("Resources");

      if (resourcesNode.size() == 0) {
        debugMap.put("found", false);
        return null;
      }
      
      if (resourcesNode.size() != 1) {
        throw new RuntimeException("Why is resourcesNode size " + resourcesNode.size() + " and not 1???? " + fieldName + ", " +  fieldValue);
      }
      
      JsonNode userNode = resourcesNode.get(0);
      GrouperScim2User grouperScimUser = GrouperScim2User.fromJson(userNode);
      debugMap.put("found", grouperScimUser != null);
      return grouperScimUser;

    } catch (RuntimeException re) {
      debugMap.put("exception", GrouperClientUtils.getFullStackTrace(re));
      throw re;
    } finally {
      GrouperScim2Log.scimLog(debugMap, startTime);
    }

  }


  /**
   * retrieve all users
   * @return the results
   */
  public static List<GrouperScim2User> retrieveScimUsers(String configId, String acceptHeader) {

    Map<String, Object> debugMap = new LinkedHashMap<String, Object>();

    debugMap.put("method", "retrieveScimUsers");

    long startTime = System.nanoTime();

    List<GrouperScim2User> results = new ArrayList<GrouperScim2User>();
    
    int pageSize = GrouperLoaderConfig.retrieveConfig()
        .propertyValueInt(
            "grouper.wsBearerToken." + configId + ".pageSize", 50);
    pageSize = GrouperLoaderConfig.retrieveConfig()
        .propertyValueInt(
            "grouper.wsBearerToken." + configId + ".userPageSize", pageSize);

    Set<String> idsRetreved = new HashSet<String>();
    
    try {
      
      int startIndex = 1;
      int maxCalls = Math.max(10000000/pageSize, 1);
      int previousStartIndex = 0;
      do {

        JsonNode jsonNode = null;
        if (pageSize == -1) {
          jsonNode = executeMethod(debugMap, GrouperHttpMethod.get, configId, "/Users",
              GrouperUtil.toSet(200), new int[] { -1 }, null, acceptHeader);
        } else {
          jsonNode = executeMethod(debugMap, GrouperHttpMethod.get, configId, "/Users?startIndex="+startIndex+"&count="+pageSize,
              GrouperUtil.toSet(200), new int[] { -1 }, null, acceptHeader);
        }

        int totalResults = GrouperUtil.jsonJacksonGetInteger(jsonNode, "totalResults");
        int itemsPerPage = GrouperUtil.jsonJacksonGetInteger(jsonNode, "itemsPerPage");
        int returnedStartIndex = GrouperUtil.jsonJacksonGetInteger(jsonNode, "startIndex");
        if (previousStartIndex == returnedStartIndex) {
          // the server returned the previous page so we're done. It happens in AWS.
          return results;
        }
        
        if (maxCalls-- < 0) {
          throw new RuntimeException("Endless loop detected! total: " + totalResults 
              + ", itemsPerPage: " + itemsPerPage + ", startIndex: " + startIndex + ", resultsRetrieved: " + results.size());
        }

        if (totalResults == 0) {
          return results;
        }
        
        ArrayNode resourcesNode = (ArrayNode)jsonNode.get("Resources");

        if (resourcesNode.size() == 0) {
          return results;
        }
   
        for (int i=0;i<resourcesNode.size();i++) {
          JsonNode userNode = resourcesNode.get(i);
          GrouperScim2User grouperScimUser = GrouperScim2User.fromJson(userNode);
          if (!idsRetreved.contains(grouperScimUser.getId())) {
            results.add(grouperScimUser);
            idsRetreved.add(grouperScimUser.getId());
          }
        }
        
        previousStartIndex = startIndex;
        
        // this doesnt increase by pageSize since the server might not support it
        startIndex = startIndex + resourcesNode.size();
        
        if (pageSize == -1) {
          return results;
        }

      } while (true);
        
    } catch (RuntimeException re) {
      debugMap.put("exception", GrouperClientUtils.getFullStackTrace(re));
      throw re;
    } finally {
      GrouperScim2Log.scimLog(debugMap, startTime);
    }

  }



  public static void deleteScimUser(String configId, String acceptHeader,
      String userId) {
    Map<String, Object> debugMap = new LinkedHashMap<String, Object>();
  
    debugMap.put("method", "deleteScimUser");
  
    long startTime = System.nanoTime();
  
    try {
    
      if (StringUtils.isBlank(userId)) {
        throw new RuntimeException("id is null");
      }
    
      executeMethod(debugMap, GrouperHttpMethod.delete, configId, "/Users/" + GrouperUtil.escapeUrlEncode(userId),
          GrouperUtil.toSet(204, 404), new int[] { -1 }, null, acceptHeader);
  
    } catch (RuntimeException re) {
      debugMap.put("exception", GrouperClientUtils.getFullStackTrace(re));
      throw re;
    } finally {
      GrouperScim2Log.scimLog(debugMap, startTime);
    }
  }


  /**
   * create a group
   * @param grouperScimGroup
   * @return the result
   */
  public static GrouperScim2Group createScimGroup(String configId, String acceptHeader,
      GrouperScim2Group grouperScimGroup, Set<String> fieldsToUpdate) {

    Map<String, Object> debugMap = new LinkedHashMap<String, Object>();

    debugMap.put("method", "createScimGroup");

    long startTime = System.nanoTime();

    try {

      ObjectNode jsonToSend = grouperScimGroup.toJson(fieldsToUpdate);
      
      {
        ArrayNode schemasNode = GrouperUtil.jsonJacksonArrayNode();
        schemasNode.add("urn:ietf:params:scim:schemas:core:2.0:Group");
        jsonToSend.set("schemas", schemasNode);
      }

      String jsonStringToSend = GrouperUtil.jsonJacksonToString(jsonToSend);

      JsonNode jsonNode = executeMethod(debugMap, GrouperHttpMethod.post, configId, "/Groups",
          GrouperUtil.toSet(201), new int[] { -1 }, jsonStringToSend, acceptHeader);
  
      GrouperScim2Group grouperScimGroupResult = GrouperScim2Group.fromJson(jsonNode);

      return grouperScimGroupResult;
    } catch (RuntimeException re) {
      debugMap.put("exception", GrouperClientUtils.getFullStackTrace(re));
      throw re;
    } finally {
      GrouperScim2Log.scimLog(debugMap, startTime);
    }
  
  }


  public static void deleteScimGroup(String configId, String acceptHeader,
      String groupId) {
    Map<String, Object> debugMap = new LinkedHashMap<String, Object>();
  
    debugMap.put("method", "deleteScimGroup");
  
    long startTime = System.nanoTime();
  
    try {
    
      if (StringUtils.isBlank(groupId)) {
        throw new RuntimeException("id is null");
      }
    
      executeMethod(debugMap, GrouperHttpMethod.delete, configId, "/Groups/" + GrouperUtil.escapeUrlEncode(groupId),
          GrouperUtil.toSet(204, 404), new int[] { -1 }, null, acceptHeader);
  
    } catch (RuntimeException re) {
      debugMap.put("exception", GrouperClientUtils.getFullStackTrace(re));
      throw re;
    } finally {
      GrouperScim2Log.scimLog(debugMap, startTime);
    }
  }


  /**
   * @param configId
   * @param fieldName id or userPrincipalName
   * @param fieldValue is value of id or userPrincipalName
   * @return
   */
  public static GrouperScim2Group retrieveScimGroup(String configId, String acceptHeader, String fieldName,
      String fieldValue) {
  
    Map<String, Object> debugMap = new LinkedHashMap<String, Object>();
  
    debugMap.put("method", "retrieveScimGroup");
  
    long startTime = System.nanoTime();
  
    try {
  
      String urlSuffix = null;
      if (StringUtils.equals(fieldName, "id")) {
        urlSuffix = "/Groups/" + GrouperUtil.escapeUrlEncode(fieldValue);
      } else {
        urlSuffix = "/Groups?filter=" + GrouperUtil.escapeUrlEncode(fieldName)
            + "%20eq%20" + GrouperUtil.escapeUrlEncode("\"" + StringEscapeUtils.escapeJson(fieldValue) + "\"");
      }
      
      JsonNode jsonNode = executeGetMethod(debugMap, configId, urlSuffix, acceptHeader);
      
      if (jsonNode == null) {
        debugMap.put("found", false);
        return null;
      }
  
      if (StringUtils.equals(fieldName, "id")) {
        GrouperScim2Group grouperScimGroup = GrouperScim2Group.fromJson(jsonNode);
        debugMap.put("found", grouperScimGroup != null);
        return grouperScimGroup;
      }
  
      if (!jsonNode.has("Resources")) {
        debugMap.put("found", false);
        return null;
      }
      
      ArrayNode resourcesNode = (ArrayNode)jsonNode.get("Resources");
  
      if (resourcesNode.size() == 0) {
        debugMap.put("found", false);
        return null;
      }
      
      if (resourcesNode.size() != 1) {
        throw new RuntimeException("Why is resourcesNode size " + resourcesNode.size() + " and not 1???? " + fieldName + ", " +  fieldValue);
      }
      
      JsonNode groupNode = resourcesNode.get(0);
      GrouperScim2Group grouperScimGroup = GrouperScim2Group.fromJson(groupNode);
      debugMap.put("found", grouperScimGroup != null);
      return grouperScimGroup;
  
    } catch (RuntimeException re) {
      debugMap.put("exception", GrouperClientUtils.getFullStackTrace(re));
      throw re;
    } finally {
      GrouperScim2Log.scimLog(debugMap, startTime);
    }
  
  }


  /**
   * create membership
   * @param groupId
   * @param userIds
   * @return the result
   */
  public static void createScimMemberships(String configId, String acceptHeader,
      String groupId, Set<String> userIds) {

    Map<String, Object> debugMap = new LinkedHashMap<String, Object>();

    debugMap.put("method", "createScimMemberships");

    long startTime = System.nanoTime();

    try {

      //  {
      //    "schemas": [
      //        "urn:ietf:params:scim:api:messages:2.0:PatchOp"
      //    ],
      //    "Operations": [
      //        {
      //            "op": "add",
      //            "path": "members",
      //            "value": [{
      //                "value": "user_id_2"
      //            }]
      //        }
      //    ]
      // }

      ObjectNode jsonToSend = GrouperUtil.jsonJacksonNode();
      
      {
        ArrayNode schemasNode = GrouperUtil.jsonJacksonArrayNode();
        schemasNode.add("urn:ietf:params:scim:api:messages:2.0:PatchOp");
        jsonToSend.set("schemas", schemasNode);
      }
  
      ArrayNode operationsNode = GrouperUtil.jsonJacksonArrayNode();

      ObjectNode operationNode = GrouperUtil.jsonJacksonNode();
      
      operationNode.put("op", "add");
      operationNode.put("path", "members");

      ArrayNode valuesNode = GrouperUtil.jsonJacksonArrayNode();
      
      for (String userId: userIds) {
        
        ObjectNode valueNode = GrouperUtil.jsonJacksonNode();
        valueNode.put("value", userId);
        valuesNode.add(valueNode);
        
      }
      operationNode.set("value", valuesNode);
      
      operationsNode.add(operationNode);

      jsonToSend.set("Operations", operationsNode);
      
      String jsonStringToSend = GrouperUtil.jsonJacksonToString(jsonToSend);
  
      executeMethod(debugMap, GrouperHttpMethod.patch, configId, "/Groups/" + GrouperUtil.escapeUrlEncode(groupId),
          GrouperUtil.toSet(200, 204), new int[] { -1 }, jsonStringToSend, acceptHeader);
    
    } catch (RuntimeException re) {
      debugMap.put("exception", GrouperClientUtils.getFullStackTrace(re));
      throw re;
    } finally {
      GrouperAzureLog.azureLog(debugMap, startTime);
    }

  }


  /**
   * delete membership
   * @param groupId
   * @param userIds
   * @return the result
   */
  public static void deleteScimMemberships(String configId, String acceptHeader,
      String groupId, Set<String> userIds) {
    Map<String, Object> debugMap = new LinkedHashMap<String, Object>();
    
    debugMap.put("method", "deleteScimMemberships");
  
    long startTime = System.nanoTime();
  
    try {
  
      List<String> userIdsList = new ArrayList<String>(userIds);
      
      int numberOfBatches = GrouperUtil.batchNumberOfBatches(userIdsList, 100);

      debugMap.put("numberOfBatches", numberOfBatches);

      for (int batchIndex=0;batchIndex<numberOfBatches;batchIndex++) {
        
        List<String> currentBatch = GrouperUtil.batchList(userIdsList, 100, batchIndex);
        
        deleteScimMembershipsHelper(configId, acceptHeader, groupId, currentBatch);
        
      }
      
    } catch (RuntimeException re) {
      debugMap.put("exception", GrouperClientUtils.getFullStackTrace(re));
      throw re;
    } finally {
      GrouperAzureLog.azureLog(debugMap, startTime);
    }
    
  }

  /**
   * delete membership
   * @param groupId
   * @param userIds
   * @return the result
   */
  private static void deleteScimMembershipsHelper(String configId, String acceptHeader,
      String groupId, List<String> userIds) {
  
    Map<String, Object> debugMap = new LinkedHashMap<String, Object>();
  
    debugMap.put("method", "deleteScimMembershipsHelper");
  
    long startTime = System.nanoTime();
  
    try {
  
      //  {
      //    "schemas": [
      //        "urn:ietf:params:scim:api:messages:2.0:PatchOp"
      //    ],
      //    "Operations": [
      //        {
      //            "op": "remove",
      //            "path": "members",
      //            "value": [{
      //                "value": "user_id_2"
      //            }]
      //        }
      //    ]
      // }
  
      ObjectNode jsonToSend = GrouperUtil.jsonJacksonNode();
      
      {
        ArrayNode schemasNode = GrouperUtil.jsonJacksonArrayNode();
        schemasNode.add("urn:ietf:params:scim:api:messages:2.0:PatchOp");
        jsonToSend.set("schemas", schemasNode);
      }
  
      ArrayNode operationsNode = GrouperUtil.jsonJacksonArrayNode();

      for (String userId: userIds) {

        ObjectNode operationNode = GrouperUtil.jsonJacksonNode();
        
        operationNode.put("op", "remove");
        //  members[value eq "89bb1940-b905-4575-9e7f-6f887cfb368e"]
        operationNode.put("path", "members[value eq \"" + StringEscapeUtils.escapeJson(userId) + "\"]");
        
        operationsNode.add(operationNode);
      }
      
      jsonToSend.set("Operations", operationsNode);
      
      String jsonStringToSend = GrouperUtil.jsonJacksonToString(jsonToSend);
  
      executeMethod(debugMap, GrouperHttpMethod.patch, configId, "/Groups/" + GrouperUtil.escapeUrlEncode(groupId),
          GrouperUtil.toSet(200, 204), new int[] { -1 }, jsonStringToSend, acceptHeader);
    
    } catch (RuntimeException re) {
      debugMap.put("exception", GrouperClientUtils.getFullStackTrace(re));
      throw re;
    } finally {
      GrouperAzureLog.azureLog(debugMap, startTime);
    }
  
  }


  /**
   * replace memberships
   * @param groupId
   * @param userIds
   * @return the result
   */
  public static void replaceScimMemberships(String configId, String acceptHeader,
      String groupId, Set<String> userIds) {
  
    Map<String, Object> debugMap = new LinkedHashMap<String, Object>();
  
    debugMap.put("method", "replaceScimMemberships");
  
    long startTime = System.nanoTime();
  
    try {
  
      //  {
      //    "schemas": [
      //        "urn:ietf:params:scim:api:messages:2.0:PatchOp"
      //    ],
      //    "Operations": [
      //        {
      //            "op": "replace",
      //            "path": "members",
      //            "value": [{
      //                "value": "user_id_2"
      //            }]
      //        }
      //    ]
      // }
  
      ObjectNode jsonToSend = GrouperUtil.jsonJacksonNode();
      
      {
        ArrayNode schemasNode = GrouperUtil.jsonJacksonArrayNode();
        schemasNode.add("urn:ietf:params:scim:api:messages:2.0:PatchOp");
        jsonToSend.set("schemas", schemasNode);
      }
  
      ArrayNode operationsNode = GrouperUtil.jsonJacksonArrayNode();
  
      ObjectNode operationNode = GrouperUtil.jsonJacksonNode();
      
      operationNode.put("op", "replace");
      operationNode.put("path", "members");
  
      ArrayNode valuesNode = GrouperUtil.jsonJacksonArrayNode();
      
      for (String userId: userIds) {
        
        ObjectNode valueNode = GrouperUtil.jsonJacksonNode();
        valueNode.put("value", userId);
        valuesNode.add(valueNode);
        
      }
      operationNode.set("value", valuesNode);
      
      operationsNode.add(operationNode);
  
      jsonToSend.set("Operations", operationsNode);
      
      String jsonStringToSend = GrouperUtil.jsonJacksonToString(jsonToSend);
  
      executeMethod(debugMap, GrouperHttpMethod.patch, configId, "/Groups/" + GrouperUtil.escapeUrlEncode(groupId),
          GrouperUtil.toSet(200, 204), new int[] { -1 }, jsonStringToSend, acceptHeader);
    
    } catch (RuntimeException re) {
      debugMap.put("exception", GrouperClientUtils.getFullStackTrace(re));
      throw re;
    } finally {
      GrouperAzureLog.azureLog(debugMap, startTime);
    }
  
  }


  /**
   * retrieve all groups
   * @return the results
   */
  public static List<GrouperScim2Group> retrieveScimGroups(String configId, String acceptHeader) {
  
    Map<String, Object> debugMap = new LinkedHashMap<String, Object>();

    debugMap.put("method", "retrieveScimGroups");

    long startTime = System.nanoTime();

    List<GrouperScim2Group> results = new ArrayList<GrouperScim2Group>();
    
    int pageSize = GrouperLoaderConfig.retrieveConfig()
        .propertyValueInt(
            "grouper.wsBearerToken." + configId + ".pageSize", 50);
    pageSize = GrouperLoaderConfig.retrieveConfig()
        .propertyValueInt(
            "grouper.wsBearerToken." + configId + ".groupPageSize", pageSize);

    Set<String> idsRetreved = new HashSet<String>();

    try {
      
      int startIndex = 1;
      int maxCalls = Math.max(5000000/pageSize, 1);
      int previousStartIndex = 0;
      do {

        JsonNode jsonNode = null;
        
        if (pageSize == -1) {
          jsonNode = executeMethod(debugMap, GrouperHttpMethod.get, configId, "/Groups",
              GrouperUtil.toSet(200), new int[] { -1 }, null, acceptHeader);
        } else {
          jsonNode = executeMethod(debugMap, GrouperHttpMethod.get, configId, "/Groups?startIndex="+startIndex+"&count="+pageSize,
              GrouperUtil.toSet(200), new int[] { -1 }, null, acceptHeader);
        }
        int totalResults = GrouperUtil.jsonJacksonGetInteger(jsonNode, "totalResults");
        int itemsPerPage = GrouperUtil.jsonJacksonGetInteger(jsonNode, "itemsPerPage");
        int returnedStartIndex = GrouperUtil.jsonJacksonGetInteger(jsonNode, "startIndex");
        if (previousStartIndex == returnedStartIndex) {
          // the server returned the previous page so we're done. It happens in AWS.
          return results;
        }
        
        
        if (maxCalls-- < 0) {
          throw new RuntimeException("Endless loop detected! total: " + totalResults 
              + ", itemsPerPage: " + itemsPerPage + ", startIndex: " + startIndex + ", resultsRetrieved: " + results.size());
        }

        if (totalResults == 0) {
          return results;
        }
        
        ArrayNode resourcesNode = (ArrayNode)jsonNode.get("Resources");

        if (resourcesNode.size() == 0) {
          return results;
        }
   
        for (int i=0;i<resourcesNode.size();i++) {
          JsonNode userNode = resourcesNode.get(i);
          GrouperScim2Group grouperScimGroup = GrouperScim2Group.fromJson(userNode);
          if (!idsRetreved.contains(grouperScimGroup.getId())) {
            results.add(grouperScimGroup);
            idsRetreved.add(grouperScimGroup.getId());
          }
        }
        
        previousStartIndex = startIndex;
        
        // this doesnt increase by pageSize since the server might not support it
        startIndex = startIndex + resourcesNode.size();
        
        if (pageSize == -1) {
          return results;
        }

        
      } while (true);
        
    } catch (RuntimeException re) {
      debugMap.put("exception", GrouperClientUtils.getFullStackTrace(re));
      throw re;
    } finally {
      GrouperScim2Log.scimLog(debugMap, startTime);
    }

  }


//  public void updateScimUser(String configId,
//      GrouperScim2User grouperScimUser, Set<String> fieldsToUpdate) {
//    
//    Map<String, Object> debugMap = new LinkedHashMap<String, Object>();
//
//    debugMap.put("method", "updateScimUser");
//
//    long startTime = System.nanoTime();
//
//    try {
//
//      
//      
//      JsonNode jsonToSend = grouperScimUser.toJson(fieldsToUpdate);
//      String jsonStringToSend = GrouperUtil.jsonJacksonToString(jsonToSend);
//
//      JsonNode jsonNode = executeMethod(debugMap, "POST", configId, "/Users",
//          GrouperUtil.toSet(201), new int[] { -1 }, jsonStringToSend);
//
//      GrouperScim2User grouperScimUserResult = GrouperScim2User.fromJson(jsonNode);
//
//      return grouperScimUserResult;
//    } catch (RuntimeException re) {
//      debugMap.put("exception", GrouperClientUtils.getFullStackTrace(re));
//      throw re;
//    } finally {
//      GrouperScim2Log.scimLog(debugMap, startTime);
//    }
//
//
//    if (!checkAuthorization(mockServiceRequest, mockServiceResponse)) {
//      return;
//    }
//    
//    String id = mockServiceRequest.getPostMockNamePaths()[1];
//    
//    GrouperUtil.assertion(GrouperUtil.length(id) > 0, "id is required");
//  
//    GrouperScim2User grouperScimUser = HibernateSession.byHqlStatic()
//        .createQuery("from GrouperScimUser where id = :theValue").setString("theValue", id)
//        .uniqueResult(GrouperScim2User.class);
//
//    if (grouperScimUser == null) {
//      mockServiceResponse.setResponseCode(404);
//      mockServiceRequest.getDebugMap().put("foundUser", false);
//      return;
//    }
//        
//    mockServiceResponse.setContentType("application/json");
//    
//    //  {
//    //    "schemas": [
//    //        "urn:ietf:params:scim:api:messages:2.0:PatchOp"
//    //    ],
//    //    "Operations": [
//    //        {
//    //            "op": "replace",
//    //            "path": "active",
//    //            "value": "false"
//    //        }
//    //    ]
//    //  }
//    
//    String requestBodyString = mockServiceRequest.getRequestBody();
//    JsonNode requestNode = GrouperUtil.jsonJacksonNode(requestBodyString);
//
//    ArrayNode schemasNode = (ArrayNode)requestNode.get("schemas");
//
//    GrouperUtil.assertion(schemasNode.size() == 1, "schema is required");
//    GrouperUtil.assertion("urn:ietf:params:scim:api:messages:2.0:PatchOp".equals(schemasNode.get(0).asText()), "schema is required");
//
//    ArrayNode operationsNode = (ArrayNode)requestNode.get("Operations");
//
//    GrouperUtil.assertion(operationsNode.size() > 0, "must send operations");
//
//    for (int i=0;i<operationsNode.size();i++) {
//      
//      JsonNode operation = operationsNode.get(i);
//      
//      //            "op": "replace",
//      //            "path": "active",
//      //            "value": "false"
//
//      // replace, add, remove
//      String op = GrouperUtil.jsonJacksonGetString(operation, "op");
//      boolean opAdd = "add".equals(op);
//      boolean opReplace = "replace".equals(op);
//      boolean opRemove = "remove".equals(op);
//      if (!opAdd && !opRemove && !opReplace) {
//        throw new RuntimeException("Invalid op, expecting add, replace, remove, but received: '" + op + "'");
//      }
//      String path = GrouperUtil.jsonJacksonGetString(operation, "path");
//      
//      //  {
//      //    "active":true,
//      //    "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User":{
//      //       "employeeNumber":"12345",
//      //       "costCenter":"costCent"   e.g. urn:ietf:params:scim:schemas:extension:enterprise:2.0:User.costCenter
//      //    },
//      //    "id":"i",
//      //    "displayName":"dispName",
//      //    "emails":[
//      //       {
//      //          "value":"emailVal", emails.value eq "emailVal" or emails[value eq "emailVal"]
//      //          "primary":true,
//      //          "type":"emailTy"  emails.type eq "work" or emails[type eq "work"]
//      //       }
//      //    ],
//      //    "name":{
//      //       "formatted":"formName",    e.g. name.formatted
//      //       "familyName":"famName",
//      //       "givenName":"givName",
//      //       "middleName":"midName"
//      //    },
//      //    "externalId":"extId",
//      //    "userName":"userNam",
//      //    "userType":"userTyp"
//      // }
//      
//      GrouperUtil.assertion(!"id".equals(path), "cannot patch id");
//
//      //  costCenter : String
//      if ("urn:ietf:params:scim:schemas:extension:enterprise:2.0:User.costCenter".equals(path)) {
//        path = "costCenter";
//      }
//      //  employeeNumber : String
//      if ("urn:ietf:params:scim:schemas:extension:enterprise:2.0:User.employeeNumber".equals(path)) {
//        path = "employeeNumber";
//      }
//
//      //  familyName : String
//      if ("name.familyName".equals(path)) {
//        path = "familyName";
//      }
//      //  formattedName : String
//      if ("name.formattedName".equals(path)) {
//        path = "formattedName";
//      }
//      //  givenName : String
//      if ("name.givenName".equals(path)) {
//        path = "givenName";
//      }
//      //  middleName : String
//      if ("name.middleName".equals(path)) {
//        path = "middleName";
//      }
//      
//      if (path.startsWith("emails")) {
//        // emailType : String
//        // emailValue : String
//        // emails[0]['value'] or emails.value eq "emailVal" or emails[value eq "emailVal"]
//        
//        JsonNode newEmailNode = operation.get("value");
//        
//        // validate the email
//        if (opAdd) {
//          
//          // if theres an existing, thats bad
//          if (!StringUtils.isBlank(grouperScimUser.getEmailValue()) || !StringUtils.isBlank(grouperScimUser.getEmailType())) {
//            
//            throw new RuntimeException("Adding email but already exists! " + grouperScimUser);
//            
//          }
//
//          if (newEmailNode.has("type")) {
//            grouperScimUser.setEmailType(GrouperUtil.jsonJacksonGetString(newEmailNode, "type"));
//          }
//          if (newEmailNode.has("value")) {
//            grouperScimUser.setEmailType(GrouperUtil.jsonJacksonGetString(newEmailNode, "value"));
//          }
//          
//        } else {
//          grouperScimUser.validateEmail(path);
//
//          if (StringUtils.isBlank(grouperScimUser.getEmailValue()) && StringUtils.isBlank(grouperScimUser.getEmailType())) {
//            
//            throw new RuntimeException(op + " email but not there! " + grouperScimUser);
//            
//          }
//
//          if (opRemove) {
//            
//            grouperScimUser.setEmailType(null);
//            grouperScimUser.setEmailValue(null);
//            
//          } else {
//            
//            //replace
//            GrouperUtil.assertion(opReplace, "expecting replace");
//
//            if (newEmailNode.isArray()) {
//              GrouperUtil.assertion(newEmailNode.size() == 1, "expecting size 1 but was " + newEmailNode.size());
//              newEmailNode = ((ArrayNode)newEmailNode).get(0);
//            }
//            if (newEmailNode.has("type")) {
//              grouperScimUser.setEmailType(GrouperUtil.jsonJacksonGetString(newEmailNode, "type"));
//            }
//            if (newEmailNode.has("value")) {
//              grouperScimUser.setEmailType(GrouperUtil.jsonJacksonGetString(newEmailNode, "value"));
//            }
//            
//            
//          }
//          
//        }
//        
//      } else {
//        
//        Object newValue = "active".equals(path) ? GrouperUtil.jsonJacksonGetBoolean(operation, "value") : GrouperUtil.jsonJacksonGetString(operation, "value");
//        Object oldValue = GrouperUtil.fieldValue(grouperScimUser, path);
//        
//        // validate the email
//        if (opAdd) {
//          
//          GrouperUtil.assertion(GrouperUtil.isBlank(oldValue), "add op already has value! " + path + ", '" + oldValue + "' " + grouperScimUser);
//          
//          GrouperUtil.assignField(grouperScimUser, path, newValue);
//          
//        } else {
//
//          GrouperUtil.assertion(!GrouperUtil.isBlank(oldValue), "add op doesnt have value! " + path + ", '" + oldValue + "' " + grouperScimUser);
//
//          if (opRemove) {
//            
//            GrouperUtil.assertion(newValue == null, "remove op should not have a value! " + path + ", '" + newValue + "' " + grouperScimUser);
//          }
//
//          GrouperUtil.assignField(grouperScimUser, path, newValue);
//        }
//        
//      }
//      
//    }
//    HibernateSession.byObjectStatic().saveOrUpdate(grouperScimUser);
//    
//    ObjectNode objectNode = grouperScimUser.toJson(null);
//    mockServiceResponse.setResponseCode(204);
//    mockServiceResponse.setContentType("application/json");
//    mockServiceResponse.setResponseBody(GrouperUtil.jsonJacksonToString(objectNode));
//    
//    
//  }

}
