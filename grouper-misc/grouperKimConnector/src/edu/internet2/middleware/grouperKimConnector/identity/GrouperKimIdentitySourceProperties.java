/**
 * @author mchyzer
 * $Id$
 */
package edu.internet2.middleware.grouperKimConnector.identity;

import org.apache.commons.lang.StringUtils;

import edu.internet2.middleware.grouperClient.util.ExpirableCache;
import edu.internet2.middleware.grouperClient.util.GrouperClientUtils;


/**
 * properties about a source, cached
 */
public class GrouperKimIdentitySourceProperties {

  /**
   * cache of grouper source configs
   */
  private static ExpirableCache<String, GrouperKimIdentitySourceProperties> grouperKimIdentitySourcePropertiesCache 
    = new ExpirableCache<String, GrouperKimIdentitySourceProperties>(5);

  /**
   * get the source properties for an app name (current app name)
   * @param sourceId
   * @return properties for source and app name
   */
  public static GrouperKimIdentitySourceProperties grouperKimIdentitySourceProperties(String sourceId) {
    
    GrouperKimIdentitySourceProperties grouperKimIdentitySourceProperties = 
      grouperKimIdentitySourcePropertiesCache.get(sourceId);
    if (grouperKimIdentitySourceProperties == null) {
      grouperKimIdentitySourceProperties = new GrouperKimIdentitySourceProperties();
      grouperKimIdentitySourceProperties.setSourceId(sourceId);
      
      //loop through and find this config
      
      for (int i=0;i<100;i++) {
        
        String currentSourceId = GrouperClientUtils.propertiesValue("kuali.identity.source.id." + i, false);
        if (StringUtils.isBlank(currentSourceId)) {
          break;
        }
        if (StringUtils.equals(sourceId, currentSourceId)) {
          //we found it
          {
            String nameAttribute = GrouperClientUtils.propertiesValue("kuali.identity.source.nameAttribute." + i, false);
            grouperKimIdentitySourceProperties.setNameAttribute(nameAttribute);
          }
          
          {
            String identifierAttribute = GrouperClientUtils.propertiesValue("kuali.identity.source.identifierAttribute." + i, false);
            grouperKimIdentitySourceProperties.setIdentifierAttribute(identifierAttribute);
          }
          
          {
            String firstNameAttribute = GrouperClientUtils.propertiesValue("kuali.identity.source.firstNameAttribute." + i, false);
            grouperKimIdentitySourceProperties.setFirstNameAttribute(firstNameAttribute);
          }
          
          {
            String lastNameAttribute = GrouperClientUtils.propertiesValue("kuali.identity.source.lastNameAttribute." + i, false);
            grouperKimIdentitySourceProperties.setLastNameAttribute(lastNameAttribute);
          }
          
          {
            String middleNameAttribute = GrouperClientUtils.propertiesValue("kuali.identity.source.middleNameAttribute." + i, false);
            grouperKimIdentitySourceProperties.setMiddleNameAttribute(middleNameAttribute);
          }
          
          break;
        }
        
      }
      grouperKimIdentitySourcePropertiesCache.put(sourceId, grouperKimIdentitySourceProperties);
    }
    return grouperKimIdentitySourceProperties;
  }
  
  /**
   * 
   * @return first name attribute
   */
  public String getFirstNameAttribute() {
    return this.firstNameAttribute;
  }

  /**
   * 
   * @param firstNameAttribute1
   */
  public void setFirstNameAttribute(String firstNameAttribute1) {
    this.firstNameAttribute = firstNameAttribute1;
  }

  /**
   * 
   * @return last name attribute
   */
  public String getLastNameAttribute() {
    return this.lastNameAttribute;
  }

  
  /**
   * 
   * @param lastNameAttribute1
   */
  public void setLastNameAttribute(String lastNameAttribute1) {
    this.lastNameAttribute = lastNameAttribute1;
  }

  /**
   * 
   * @return attribute
   */
  public String getMiddleNameAttribute() {
    return this.middleNameAttribute;
  }

  /**
   * 
   * @param middleNameAttribute1
   */
  public void setMiddleNameAttribute(String middleNameAttribute1) {
    this.middleNameAttribute = middleNameAttribute1;
  }

  /**
   * source id
   */
  private String sourceId;
  
  /**
   * identifier attribute
   */
  private String identifierAttribute;
  
  /**
   * identifier attribute
   * @return identifier attribute
   */
  public String getIdentifierAttribute() {
    return this.identifierAttribute;
  }

  /**
   * identifier attribute
   * @param identifierAttribute1
   */
  public void setIdentifierAttribute(String identifierAttribute1) {
    this.identifierAttribute = identifierAttribute1;
  }

  /**
   * name attribute from a subject (attribute for subjects in this source which is the name of the subject)
   */
  private String nameAttribute;

  /**
   * first name attribute from a subject (attribute for subjects in this source which is the first name of the subject)
   */
  private String firstNameAttribute;
  
  /**
   * last name attribute from a subject (attribute for subjects in this source which is the last name of the subject)
   */
  private String lastNameAttribute;
  
  /**
   * middle name attribute from a subject (attribute for subjects in this source which is the middle name of the subject)
   */
  private String middleNameAttribute;
  
  /**
   * source id
   * @return source id
   */
  public String getSourceId() {
    return this.sourceId;
  }


  /**
   * source id
   * @param sourceId1
   */
  public void setSourceId(String sourceId1) {
    this.sourceId = sourceId1;
  }


  /**
   * name attribute from a subject (attribute for subjects in this source which is the name of the subject)
   * @return name attribute
   */
  public String getNameAttribute() {
    return this.nameAttribute;
  }


  /**
   * name attribute from a subject (attribute for subjects in this source which is the name of the subject)
   * @param nameAttribute1
   */
  public void setNameAttribute(String nameAttribute1) {
    this.nameAttribute = nameAttribute1;
  }

  
  
}
