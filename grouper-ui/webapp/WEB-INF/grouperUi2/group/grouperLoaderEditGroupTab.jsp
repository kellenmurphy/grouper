<%@ include file="../assetsJsp/commonTaglib.jsp"%>

            <%-- for the new group or new stem button --%>
            <input type="hidden" name="objectStemId" value="${grouperRequestContainer.groupContainer.guiGroup.group.parentUuid}" />

            <%-- show the add member button for privileges --%>
            <c:set target="${grouperRequestContainer.groupContainer}" property="showAddMember" value="false" />
            <%@ include file="groupHeader.jsp" %>

            <div class="row-fluid">
              <div class="span12">
              
                <ul class="nav nav-tabs">
                  <li><a role="tab" href="#" onclick="return guiV2link('operation=UiV2Group.viewGroup&groupId=${grouperRequestContainer.groupContainer.guiGroup.group.id}', {dontScrollTop: true});" >${textContainer.text['groupMembersTab'] }</a></li>
                  <c:if test="${grouperRequestContainer.groupContainer.canAdmin}">
                    <li><a role="tab" href="#" onclick="return guiV2link('operation=UiV2Group.groupPrivileges&groupId=${grouperRequestContainer.groupContainer.guiGroup.group.id}', {dontScrollTop: true});" >${textContainer.text['groupPrivilegesTab'] }</a></li>
                  </c:if>
                  <%@ include file="groupMoreTab.jsp" %>
                </ul>
                <div class="row-fluid">
                  <div class="lead span10">${textContainer.text['grouperLoaderEditGroupDecription'] }</div>
                  <div class="span2" id="grouperLoaderMoreActionsButtonContentsDivId">
                    <%@ include file="grouperLoaderMoreActionsButtonContents.jsp"%>
                  </div>
                </div>
                
                <div class="row-fluid">
                  <c:if test="${fn:length(grouperRequestContainer.grouperLoaderContainer.grouperJexlScriptAnalysis.grouperJexlScriptParts) > 0}">
                    
                    <div id="analyze-member-search" tabindex="-1" role="dialog" aria-labelledby="member-search-label" aria-hidden="true" class="modal hide fade lead span12">
                      <div class="modal-header"><a href="#" data-dismiss="modal" aria-hidden="true" class="close">x</a>
                        <h3 id="member-search-label">${textContainer.text['groupSearchForEntityButton'] }</h3>
                      </div>
                      <div class="modal-body">
                        <form class="form form-inline" id="addAnalyzeMemberSearchFormId">
                          <input name="addMemberSubjectSearch" type="text" placeholder=""/>
                          <button class="btn" onclick="ajax('../app/UiV2GrouperLoader.addMemberSearch?groupId=${grouperRequestContainer.groupContainer.guiGroup.group.id}', {formIds: 'addAnalyzeMemberSearchFormId'}); return false;" >${textContainer.text['groupSearchButton'] }</button>
                          <br />
                          <span style="white-space: nowrap;"><input type="checkbox" name="matchExactId" value="true"/> ${textContainer.text['groupLabelExactIdMatch'] }</span>
                          <br />
                          <span style="white-space: nowrap;">${textContainer.text['find.search-source'] } 
                          <select name="sourceId">
                            <option value="all">${textContainer.textEscapeXml['find.search-all-sources'] }</option>
                            <c:forEach items="${grouperRequestContainer.subjectContainer.sources}" var="source" >
                              <option value="${grouper:escapeHtml(source.id)}">
                                ${grouper:escapeHtml(source.name) } (
                                  <c:forEach var="subjectType" items="${source.subjectTypes}" varStatus="typeStatus">
                                    <c:if test="${typeStatus.count>1}">, </c:if>
                                    ${grouper:escapeHtml(subjectType)}
                                  </c:forEach>
                                )                               
                              </option>
                            </c:forEach>
                          </select></span>
                        </form>
                        <div id="addAnalyzeMemberResults">
                        </div>
                      </div>
                      <div class="modal-footer">
                        <button data-dismiss="modal" aria-hidden="true" class="btn">${textContainer.text['groupSearchCloseButton']}</button>
                      </div>
                    </div>
                    
                     <form id="editLoaderJexlSubjectAnalyzeFormId" class="form-horizontal form-highlight">
                      <div class="control-group" id="add-member-control-group" aria-live="polite" aria-expanded="false">
                        <label for="analyzeAddMemberComboID" class="control-label">${textContainer.text['groupSearchMemberOrId'] }</label>
                        <div class="controls">
                          <div id="add-members-container">
                           
                            <%-- placeholder: Enter the name of a person, group, or other entity --%>
                            <grouper:combobox2 idBase="analyzeAddMemberCombo" style="width: 30em"
                              filterOperation="../app/UiV2Group.addMemberFilter?groupId=${grouperRequestContainer.groupContainer.guiGroup.group.id}"/>
                            ${textContainer.text['groupSearchLabelPreComboLink']} <a href="#analyze-member-search" onclick="$('#addAnalyzeMemberResults').empty();" role="button" data-toggle="modal" style="text-decoration: underline !important;">${textContainer.text['groupSearchForEntityLink']}</a>
                           
                          </div>
                        </div>
                      </div>
                     </form>
                    <div class="span10" style="margin-left: 180px; margin-bottom: 20px;">
                      <input type="submit" class="btn btn-primary" aria-controls="groupFilterResultsId" id="analyzeSubjectId" 
                          value="${textContainer.text['grouperLoaderEditButtonAnalyze'] }"
                          onclick="ajax('../app/UiV2GrouperLoader.editGrouperLoaderAnalyze', {formIds: 'editLoaderFormId, editLoaderJexlSubjectAnalyzeFormId'}); return false;">
                    </div>
                    
                    <div class="span10">The overall JEXL analysis is the first row.</div>
                    
                    <div class="row-fluid">
                      
                      <table class="table table-hover table-bordered table-striped table-condensed data-table">
                      <thead>        
                        <tr>
                          <th style="white-space: nowrap; text-align: center; width: 5em; vertical-align: top;">Population count</th>
                          <c:if test="${grouperRequestContainer.grouperLoaderContainer.guiSubject != null}">
                            <th style="white-space: nowrap; text-align: center; width: 5em; vertical-align: top;">Contains
                            <br/>
                             ${grouperRequestContainer.grouperLoaderContainer.guiSubject.shortLinkWithIcon}</th>
                          </c:if>
                          
                          <th style="vertical-align: top;">ABAC script description</th>
                         </tr>
                        </thead>
                    <tbody>
                    <c:forEach items="${grouperRequestContainer.grouperLoaderContainer.grouperJexlScriptAnalysis.grouperJexlScriptParts}" var="scriptPart">
                    
                      <tr>
                         <td style="white-space: nowrap; text-align: center; width: 5em;">
                          ${scriptPart.populationCount}
                         </td>
                         
                         <c:if test="${grouperRequestContainer.grouperLoaderContainer.guiSubject != null}">
                            <td style="white-space: nowrap; text-align: center; width: 5em;">${scriptPart.containsSubject}</td>
                          </c:if>
                         
                         <td>
                          ${scriptPart.displayDescription}
                         </td>
                       </tr>

                     </c:forEach>
                      </tbody>
                      </table>
                    
                    </div>
                    <div class="lead span12">
                    
                    <c:if test="${grouper:isBlank(grouperRequestContainer.grouperLoaderContainer.grouperJexlScriptAnalysis.warningMessage)}">
                      <input type="submit" class="btn btn-primary" aria-controls="groupFilterResultsId" id="filterSubmitId" 
                            value="${textContainer.text['grouperLoaderEditButtonSave'] }" 
                            onclick="ajax('../app/UiV2GrouperLoader.editGrouperLoaderSave', {formIds: 'editLoaderFormId'}); return false;">
                    </c:if>
                      &nbsp; 
                      <a class="btn btn-cancel" role="button" 
                        onclick="return guiV2link('operation=UiV2GrouperLoader.loader?groupId=${grouperRequestContainer.groupContainer.guiGroup.group.id}'); return false;"
                        >${textContainer.text['grouperLoaderEditButtonCancel'] }</a>
                    </div>
                    
                  </c:if>
                
                </div>
                
                <form class="form-inline form-small form-filter" id="editLoaderFormId">
                  <input type="hidden" name="groupId" value="${grouperRequestContainer.groupContainer.guiGroup.group.id}" />
                  <table class="table table-condensed table-striped">
                    <tbody>
                      <tr>
                        <td style="vertical-align: top; white-space: nowrap;"><strong><label for="grouperLoaderHasLoaderId">${textContainer.text['grouperLoaderHasLoaderLabel']}</label></strong></td>
                        <td>
                          <select name="grouperLoaderHasLoaderName" id="grouperLoaderHasLoaderId" style="width: 25em"
                            onchange="ajax('../app/UiV2GrouperLoader.editGrouperLoader', {formIds: 'editLoaderFormId'}); return false;">
                            <option value="false" ${grouperRequestContainer.grouperLoaderContainer.editLoaderIsLoader ? '' : 'selected="selected"' } >${textContainer.textEscapeXml['grouperLoaderNoDoesNotHaveLoaderLabel']}</option>
                            <option value="true" ${grouperRequestContainer.grouperLoaderContainer.editLoaderIsLoader ? 'selected="selected"'  : '' }>${textContainer.textEscapeXml['grouperLoaderYesHasLoaderLabel']}</option>
                          </select>
                          <br />
                          <span class="description">${textContainer.text['grouperLoaderHasLoaderDescription']}</span>
                        </td>
                      </tr>
                      <c:if test="${grouperRequestContainer.grouperLoaderContainer.editLoaderShowLoaderType}">
                        <tr>
                          <td style="vertical-align: top; white-space: nowrap;"><strong><label for="grouperLoaderTypeId">${textContainer.text['grouperLoaderSourceType']}</label></strong></td>
                          <td>
                            <span style="white-space: nowrap">
                              <select name="grouperLoaderTypeName" id="grouperLoaderTypeId" style="width: 15em"
                                onchange="ajax('../app/UiV2GrouperLoader.editGrouperLoader', {formIds: 'editLoaderFormId'}); return false;">
                                <option value="" ></option>
                                <option value="JEXL_SCRIPT" ${grouperRequestContainer.grouperLoaderContainer.editLoaderType == 'JEXL_SCRIPT' ? 'selected="selected"'  : '' }>${textContainer.textEscapeXml['grouperLoaderJexlScript']}</option>
                                <option value="LDAP" ${grouperRequestContainer.grouperLoaderContainer.editLoaderType == 'LDAP' ? 'selected="selected"'  : '' }>${textContainer.textEscapeXml['grouperLoaderLdap']}</option>
                                <option value="SQL" ${grouperRequestContainer.grouperLoaderContainer.editLoaderType == 'SQL' ? 'selected="selected"' : '' } >${textContainer.textEscapeXml['grouperLoaderSql']}</option>
                                <option value="RECENT_MEMBERSHIPS" ${grouperRequestContainer.grouperLoaderContainer.editLoaderType == 'RECENT_MEMBERSHIPS' ? 'selected="selected"'  : '' }>${textContainer.textEscapeXml['grouperLoaderRecentMemberships']}</option>
                              </select>
                              <span class="requiredField" rel="tooltip" data-html="true" data-delay-show="200" data-placement="right" 
                                data-original-title="${textContainer.textEscapeDouble['grouperRequiredTooltip']}">*</span>
                            </span>
                            <br />
                            <span class="description">${textContainer.text[grouper:concat2('grouperLoaderSourceType__',grouperRequestContainer.grouperLoaderContainer.editLoaderType)]}</span>
                          </td>
                        </tr>
                        <c:if test="${grouperRequestContainer.grouperLoaderContainer.editLoaderType == 'RECENT_MEMBERSHIPS'}">
                          <c:if test="${grouperRequestContainer.grouperLoaderContainer.editLoaderShowRecentMemberships}">
                            <tr>
                              <td style="vertical-align: top; white-space: nowrap;"><strong><label for="grouperLoaderSqlTypeId">${textContainer.text['grouperLoaderRecentFromGroup']}</label></strong></td>
                              <td>
                                <span style="white-space: nowrap">
                                  <table border="0" style="border:none;padding:0em; margin: 0em">
                                    <tr style="border:none;padding:0em; margin: 0em" >
                                    <td style="border:none;padding:0em; margin: 0em;border-top: none !important;"><grouper:combobox2 idBase="recentMembershipsFromGroupCombo" style="width: 30em" 
                                    value="${grouperRequestContainer.grouperLoaderContainer.editLoaderRecentGroupUuidFrom}"
                                    filterOperation="../app/UiV2GrouperLoader.recentMembershipsGroupFromFilter"/></td>
                                    <td style="border:none;padding:0em; margin: 0em">
                                    <span class="requiredField" rel="tooltip" data-html="true" data-delay-show="200" data-placement="right" 
                                    data-original-title="${textContainer.textEscapeDouble['grouperRequiredTooltip']}">*</span>
                                    </td></tr></table>
                                </span>
                                <br />
                                <span class="description">${textContainer.text['grouperLoaderRecentFromGroupDescription']}</span>
                              </td>
                            </tr>
                            <tr>
                              <td style="vertical-align: top; white-space: nowrap;"><strong><label for="grouperLoaderRecentDaysId">${textContainer.text['grouperLoaderRecentDays']}</label></strong></td>
                              <td>
                                <span style="white-space: nowrap">
                                  <input type="text" style="width: 5em" value="${grouper:escapeHtml(grouperRequestContainer.grouperLoaderContainer.editLoaderRecentDays)}"
                                     name="grouperLoaderRecentDaysName" id="grouperLoaderRecentDaysId" />
                                  <span class="requiredField" rel="tooltip" data-html="true" data-delay-show="200" data-placement="right" 
                                    data-original-title="${textContainer.textEscapeDouble['grouperRequiredTooltip']}">*</span>
                                </span>
                                <br />
                                <span class="description">
                                ${textContainer.text["grouperLoaderRecentDaysDescription"]}</span>
                              </td>
                            </tr>
                            <tr>
                              <td style="vertical-align: top; white-space: nowrap;"><strong><label for="grouperLoaderRecentIncludeCurrentId">${textContainer.text['grouperLoaderRecentIncludeCurrent']}</label></strong></td>
                              <td>
                                <span style="white-space: nowrap">
                                  <select name="grouperLoaderRecentIncludeCurrentName" id="grouperLoaderRecentIncludeCurrentId" style="width: 40em">
                                    <option value="" ></option>
                                    <option value="true" ${grouperRequestContainer.grouperLoaderContainer.editLoaderRecentIncludeCurrent ? 'selected="selected"' : '' } 
                                      >${textContainer.textEscapeXml['grouperLoaderRecentIncludeCurrentTrue']}</option>
                                    <option value="false" ${grouperRequestContainer.grouperLoaderContainer.editLoaderRecentIncludeCurrent ? '' : 'selected="selected"' }
                                      >${textContainer.textEscapeXml['grouperLoaderRecentIncludeCurrentFalse']}</option>
                                  </select>
                                  <span class="requiredField" rel="tooltip" data-html="true" data-delay-show="200" data-placement="right" 
                                    data-original-title="${textContainer.textEscapeDouble['grouperRequiredTooltip']}">*</span>
                                </span>
                                <br />
                                <span class="description">${textContainer.text["grouperLoaderRecentIncludeCurrentDescription"]}</span>
                              </td>
                            </tr>
                          </c:if>
                        </c:if>
                        <c:if test="${grouperRequestContainer.grouperLoaderContainer.editLoaderType == 'JEXL_SCRIPT'}"> 
                        
                          <tr>
                            <td style="vertical-align: top; white-space: nowrap;"><strong><label for="constructScriptId">${textContainer.text['grouperLoaderConstructScript']}</label></strong></td>
                            <td>
                              <span style="white-space: nowrap">
                                <select name="constructScript" id="constructScriptId" style="width: 40em" onchange="ajax('../app/UiV2GrouperLoader.editGrouperLoader', {formIds: 'editLoaderFormId'}); return false;">
                                  <option value=""></option>
                                  <option value="inputScript" ${grouperRequestContainer.grouperLoaderContainer.editLoaderConstructScript == 'inputScript' ? 'selected="selected"' : '' } 
                                    >${textContainer.textEscapeXml['grouperLoaderConstructScriptInputScript']}</option>
                                  <option value="pattern" ${grouperRequestContainer.grouperLoaderContainer.editLoaderConstructScript == 'pattern' ? 'selected="selected"' : '' }
                                    >${textContainer.textEscapeXml['grouperLoaderConstructScriptPattern']}</option>
                                </select>
                                <span class="requiredField" rel="tooltip" data-html="true" data-delay-show="200" data-placement="right" 
                                  data-original-title="${textContainer.textEscapeDouble['grouperRequiredTooltip']}">*</span>
                              </span>
                              <br />
                              <span class="description">${textContainer.text["grouperLoaderConstructScriptDescription"]}</span>
                            </td>
                          </tr>
                          
                          <c:if test="${grouperRequestContainer.grouperLoaderContainer.editLoaderConstructScript == 'pattern'}">
                            <tr>
                              <td style="vertical-align: top; white-space: nowrap;"><strong><label for="grouperLoaderAbacPatternId">${textContainer.text['grouperLoaderAbacPatterns']}</label></strong></td>
                              <td>
                                <span style="white-space: nowrap">
                                  <select name="templateType" id="grouperLoaderAbacPatternId" style="width: 40em"
                                    onchange="ajax('../app/UiV2GrouperLoader.editGrouperLoader', {formIds: 'editLoaderFormId'}); return false;">
                                    <option value=""></option>
                                    <c:forEach items="${grouperRequestContainer.groupStemTemplateContainer.customAbacTemplates}" var="abacTemplate">
                                      <option value="${abacTemplate.key}" ${grouperRequestContainer.grouperLoaderContainer.editLoaderAbacPattern == abacTemplate.key ? 'selected="selected"' : '' } 
                                        >${abacTemplate.value}</option>
                                    </c:forEach>
                                  </select>
                                  <span class="requiredField" rel="tooltip" data-html="true" data-delay-show="200" data-placement="right" 
                                    data-original-title="${textContainer.textEscapeDouble['grouperRequiredTooltip']}">*</span>
                                </span>
                                <br />
                                <span class="description">
                                ${textContainer.text['grouperLoaderAbacPatternsDescription']}</span> 
                              </td>
                            </tr>
                            
                            <c:forEach items="${grouperRequestContainer.groupStemTemplateContainer.guiGshTemplateConfig.gshTemplateInputConfigAndValues}" var="gshTemplateInputConfigAndValueMap">
            
                              <c:set var="guiGshTemplateInputConfigName" value="${gshTemplateInputConfigAndValueMap.key}"></c:set>             
                              <c:set var="guiGshTemplateInputConfig" value="${gshTemplateInputConfigAndValueMap.value}"></c:set>             
                                                  
                              <grouper:configFormElement 
                                formElementType="${guiGshTemplateInputConfig.gshTemplateInputConfig.configItemFormElement}"
                                configId="${guiGshTemplateInputConfig.gshTemplateInputConfig.name}" 
                                label="${guiGshTemplateInputConfig.gshTemplateInputConfig.labelForUi}"
                                readOnly="false"
                                helperText="${guiGshTemplateInputConfig.gshTemplateInputConfig.descriptionForUi}"
                                helperTextDefaultValue="${guiGshTemplateInputConfig.gshTemplateInputConfig.defaultValue}"
                                required="${guiGshTemplateInputConfig.gshTemplateInputConfig.required}"
                                shouldShow="true"
                                shouldShowElCheckbox="false"
                                value="${guiGshTemplateInputConfig.value}"
                                hasExpressionLanguage="false"
                                ajaxCallback="ajax('../app/UiV2GrouperLoader.editGrouperLoader', {formIds: 'editLoaderFormId'}); return false;"
                                valuesAndLabels="${guiGshTemplateInputConfig.gshTemplateInputConfig.dropdownKeysAndLabels}"
                                indent="${attribute.configItemMetadata.indent}"
                              />
                                  
                            </c:forEach>
                            
                          </c:if>
                          
                           <c:if test="${grouperRequestContainer.grouperLoaderContainer.editLoaderConstructScript == 'inputScript'}">
                            <tr>
                              <td style="vertical-align: top; white-space: nowrap;"><strong><label for="grouperLoaderJexlScriptId">${textContainer.text['grouperLoaderEntityJexlScript']}</label></strong></td>
                              <td>
                                <span style="white-space: nowrap">
                                  <textarea cols="100" rows="12" name="grouperLoaderJexlScriptName" id="grouperLoaderJexlScriptId" style="width: 40em">${grouper:escapeHtml(grouperRequestContainer.grouperLoaderContainer.editLoaderJexlScriptJexlScript)}</textarea>
                                  <span class="requiredField" rel="tooltip" data-html="true" data-delay-show="200" data-placement="right" style="vertical-align: top;"
                                    data-original-title="${textContainer.textEscapeDouble['grouperRequiredTooltip']}">*</span>
                                </span>
                                <br />
                                <span class="description">${textContainer.text["grouperLoaderEntityJexlScriptDescription"]}</span>
                              </td>
                            </tr>
                            <tr>
                              <td style="vertical-align: top; white-space: nowrap;"><strong><label for="grouperLoaderIncludeInternalSourcesId">${textContainer.text['grouperLoaderIncludeInternalSources']}</label></strong></td>
                              <td>
                                <span style="white-space: nowrap">
                                  <select name="grouperLoaderIncludeInternalSourcesName" id="grouperLoaderIncludeInternalSourcesId" style="width: 40em">
                                    <option value="" ></option>
                                    <option value="false" ${grouperRequestContainer.grouperLoaderContainer.editLoaderJexlScriptIncludeInternalSources == 'false' ? 'selected="selected"' : '' }
                                      >${textContainer.textEscapeXml['grouperLoaderIncludeInternalSourcesFalse']}</option>
                                    <option value="true" ${grouperRequestContainer.grouperLoaderContainer.editLoaderJexlScriptIncludeInternalSources == 'true' ? 'selected="selected"' : '' } 
                                      >${textContainer.textEscapeXml['grouperLoaderIncludeInternalSourcesTrue']}</option>
                                                                          
                                  </select>
                                </span>
                                <br />
                                <span class="description">${textContainer.text["grouperLoaderIncludeInternalSourcesDescription"]}</span>
                              </td>
                            </tr>
                          </c:if>
                         
                        </c:if>
                        
                        <c:if test="${grouperRequestContainer.grouperLoaderContainer.editLoaderType == 'SQL'}">
                          <c:if test="${grouperRequestContainer.grouperLoaderContainer.editLoaderShowSqlLoaderType}">
                            <tr>
                              <td style="vertical-align: top; white-space: nowrap;"><strong><label for="grouperLoaderSqlTypeId">${textContainer.text['grouperLoaderSqlLoaderType']}</label></strong></td>
                              <td>
                                <span style="white-space: nowrap">
                                  <select name="grouperLoaderSqlTypeName" id="grouperLoaderSqlTypeId" style="width: 40em"
                                    onchange="ajax('../app/UiV2GrouperLoader.editGrouperLoader', {formIds: 'editLoaderFormId'}); return false;">
                                    <option value="" ></option>
                                    <option value="SQL_SIMPLE" ${grouperRequestContainer.grouperLoaderContainer.editLoaderSqlType == 'SQL_SIMPLE' ? 'selected="selected"' : '' } 
                                      >${textContainer.textEscapeXml['grouperLoaderSqlLoaderTypeOption__SQL_SIMPLE']}</option>
                                    <option value="SQL_GROUP_LIST" ${grouperRequestContainer.grouperLoaderContainer.editLoaderSqlType == 'SQL_GROUP_LIST' ? 'selected="selected"'  : '' }
                                      >${textContainer.textEscapeXml['grouperLoaderSqlLoaderTypeOption__SQL_GROUP_LIST']}</option>
                                  </select>
                                  <span class="requiredField" rel="tooltip" data-html="true" data-delay-show="200" data-placement="right" 
                                    data-original-title="${textContainer.textEscapeDouble['grouperRequiredTooltip']}">*</span>
                                </span>
                                <br />
                                <span class="description">${textContainer.text[grouper:concat2('grouperLoaderSqlLoaderType__',grouperRequestContainer.grouperLoaderContainer.editLoaderSqlType)]}</span>
                              </td>
                            </tr>
                          </c:if>
                          <c:if test="${grouperRequestContainer.grouperLoaderContainer.editLoaderShowSqlDatabaseName}">
                            <tr>
                              <td style="vertical-align: top; white-space: nowrap;"><strong><label for="grouperLoaderSqlDatabaseNameId">${textContainer.text['grouperLoaderDatabaseName']}</label></strong></td>
                              <td>
                                <span style="white-space: nowrap">
                                  <select name="grouperLoaderSqlDatabaseNameName" id="grouperLoaderSqlDatabaseNameId" style="width: 40em"
                                    onchange="ajax('../app/UiV2GrouperLoader.editGrouperLoader', {formIds: 'editLoaderFormId'}); return false;">
                                    <option value="" ></option>
                                    <c:forEach items="${grouperRequestContainer.grouperLoaderContainer.sqlDatabaseNames}" var="sqlDatabaseName">
                                      <option value="${grouper:escapeHtml(sqlDatabaseName.id)}" ${grouperRequestContainer.grouperLoaderContainer.editLoaderSqlDatabaseName == sqlDatabaseName.id ? 'selected="selected"' : '' } 
                                        >${grouper:escapeHtml(sqlDatabaseName.name)}</option>
                                    </c:forEach>
                                  </select>
                                  <span class="requiredField" rel="tooltip" data-html="true" data-delay-show="200" data-placement="right" 
                                    data-original-title="${textContainer.textEscapeDouble['grouperRequiredTooltip']}">*</span>
                                </span>
                                <br />
                                <span class="description">
                                ${grouper:escapeHtml(grouperRequestContainer.grouperLoaderContainer.editLoaderSqlDatabaseNameText)}<br />
                                ${textContainer.text['grouperLoaderDatabaseNameDescription']}</span>
                              </td>
                            </tr>
                          </c:if>
                          <c:if test="${grouperRequestContainer.grouperLoaderContainer.editLoaderShowSqlQuery}">
                            <tr>
                              <td style="vertical-align: top; white-space: nowrap;"><strong><label for="grouperLoaderSqlQueryId">${textContainer.text['grouperLoaderSqlQuery']}</label></strong></td>
                              <td>
                                <span style="white-space: nowrap">
                                     
                                 <textarea id="grouperLoaderSqlQueryId" name="grouperLoaderSqlQueryName" rows="4" cols="40" class="input-block-level">${grouper:escapeHtml(grouperRequestContainer.grouperLoaderContainer.editLoaderSqlQuery)}</textarea>    
                                     
                                  <span class="requiredField" rel="tooltip" data-html="true" data-delay-show="200" data-placement="right" 
                                    data-original-title="${textContainer.textEscapeDouble['grouperRequiredTooltip']}">*</span>
                                </span>
                                <br />
                                <span class="description">
                                ${textContainer.text[grouper:concat2('grouperLoaderSqlQueryDescription__',grouperRequestContainer.grouperLoaderContainer.editLoaderSqlType)]}</span>
                              </td>
                            </tr>
                          </c:if>
                          <c:if test="${grouperRequestContainer.grouperLoaderContainer.editLoaderShowFields}">
                            <tr>
                              <td style="vertical-align: top; white-space: nowrap;"><strong><label for="editLoaderScheduleTypeId">${textContainer.text['grouperLoaderSqlScheduleType']}</label></strong></td>
                              <td>
                                <span style="white-space: nowrap">
                                  <select name="editLoaderScheduleTypeName" id="editLoaderScheduleTypeId" style="width: 40em"
                                    onchange="ajax('../app/UiV2GrouperLoader.editGrouperLoader', {formIds: 'editLoaderFormId'}); return false;">
                                    <option value="CRON" ${grouperRequestContainer.grouperLoaderContainer.editLoaderScheduleType == 'CRON' ? 'selected="selected"' : '' } 
                                      >${textContainer.textEscapeXml['grouperLoaderSqlScheduleTypeOption__CRON']}</option>
                                    <option value="START_TO_START_INTERVAL" ${grouperRequestContainer.grouperLoaderContainer.editLoaderScheduleType == 'START_TO_START_INTERVAL' ? 'selected="selected"'  : '' }
                                      >${textContainer.textEscapeXml['grouperLoaderSqlScheduleTypeOption__START_TO_START_INTERVAL']}</option>
                                  </select>
                                  <span class="requiredField" rel="tooltip" data-html="true" data-delay-show="200" data-placement="right" 
                                    data-original-title="${textContainer.textEscapeDouble['grouperRequiredTooltip']}">*</span>
                                </span>
                                <br />
                                <span class="description">${textContainer.text[grouper:concat2('grouperLoaderSqlScheduleType__',grouperRequestContainer.grouperLoaderContainer.editLoaderScheduleType)]}</span>
                              </td>
                            </tr>
    
                            <c:choose>
                              <c:when test="${grouperRequestContainer.grouperLoaderContainer.editLoaderScheduleType == 'CRON' || grouperRequestContainer.grouperLoaderContainer.editLoaderScheduleType == null}">
                                <tr>
                                  <td style="vertical-align: top; white-space: nowrap;"><strong><label for="editLoaderCronId">${textContainer.text['grouperLoaderSqlCron']}</label></strong></td>
                                  <td>
                                    <span style="white-space: nowrap">
                                      <input type="text" style="width: 20em" value="${grouper:escapeHtml(grouperRequestContainer.grouperLoaderContainer.editLoaderCron)}"
                                         name="editLoaderCronName" id="editLoaderCronId" />
                                      <span class="requiredField" rel="tooltip" data-html="true" data-delay-show="200" data-placement="right" 
                                        data-original-title="${textContainer.textEscapeDouble['grouperRequiredTooltip']}">*</span>
                                    </span>
                                    <c:if test="${grouperRequestContainer.grouperLoaderContainer.editLoaderCronDescription != null && grouperRequestContainer.grouperLoaderContainer.editLoaderCronDescription != ''}">
                                      <br /><span class="description">${grouper:escapeHtml(grouperRequestContainer.grouperLoaderContainer.editLoaderCronDescription)}</span>
                                    </c:if>
                                    <br /><span class="description">${textContainer.text['grouperLoaderSqlCronDescription']}</span>
                                  </td>
                                </tr>
    
                              </c:when>
                              <c:when test="${grouperRequestContainer.grouperLoaderContainer.editLoaderScheduleType == 'START_TO_START_INTERVAL'}">
                                <tr>
                                  <td style="vertical-align: top; white-space: nowrap;"><strong><label for="editLoaderScheduleIntervalId">${textContainer.text['grouperLoaderSqlScheduleInterval']}</label></strong></td>
                                  <td>
                                    <span style="white-space: nowrap">
                                      <input type="text" style="width: 20em" value="${grouper:escapeHtml(grouperRequestContainer.grouperLoaderContainer.editLoaderScheduleInterval)}"
                                         name="editLoaderScheduleIntervalName" id="editLoaderScheduleIntervalId" />
                                      <span class="requiredField" rel="tooltip" data-html="true" data-delay-show="200" data-placement="right" 
                                        data-original-title="${textContainer.textEscapeDouble['grouperRequiredTooltip']}">*</span>
                                    </span>
                                    <br /><span class="description">${grouper:escapeHtml(grouperRequestContainer.grouperLoaderContainer.editLoaderScheduleIntervalHumanReadable)}</span>
                                  </td>
                                </tr>
                              </c:when>
                            </c:choose>
                            <tr>
                              <td style="vertical-align: top; white-space: nowrap;"><strong><label for="editLoaderPriorityId">${textContainer.text['grouperLoaderSqlPriority']}</label></strong></td>
                              <td>
                                <span style="white-space: nowrap">
                                  <input type="text" style="width: 20em" value="${grouper:escapeHtml(grouperRequestContainer.grouperLoaderContainer.editLoaderPriority)}"
                                     name="editLoaderPriorityName" id="editLoaderPriorityId" />
                                  <br />
                                  <span class="description">
                                    <c:choose>
                                      <c:when test="${grouperRequestContainer.grouperLoaderContainer.editLoaderPriorityInt == -200}">
                                        ${textContainer.text['grouperLoaderSqlPriorityInvalid']}
                                      </c:when>
                                      <c:when test="${grouperRequestContainer.grouperLoaderContainer.editLoaderPriorityInt < 5}">
                                        ${textContainer.text['grouperLoaderSqlPriorityLow']}
                                      </c:when>
                                      <c:when test="${grouperRequestContainer.grouperLoaderContainer.editLoaderPriorityInt == 5}">
                                        ${textContainer.text['grouperLoaderSqlPriorityAverage']}
                                      </c:when>
                                      <c:when test="${grouperRequestContainer.grouperLoaderContainer.editLoaderPriorityInt > 5}">
                                        ${textContainer.text['grouperLoaderSqlPriorityHigh']}
                                      </c:when>
                                      
                                    </c:choose>
                                  </span>
                                </span>
                                <br /><span class="description"></span>
                              </td>
                            </tr>
                            <tr>
                              <td style="vertical-align: top; white-space: nowrap;"><strong><label for="editLoaderAndGroupsId">${textContainer.text['grouperLoaderSqlAndGroups']}</label></strong></td>
                              <td>
                                <input type="text" style="width: 20em" value="${grouper:escapeHtml(grouperRequestContainer.grouperLoaderContainer.editLoaderAndGroups)}"
                                   name="editLoaderAndGroupsName" id="editLoaderAndGroupsId" />
                                <br />
                                <c:forEach var="guiGroup" items="${grouperRequestContainer.grouperLoaderContainer.editLoaderAndGuiGroups}">
                                
                                  ${guiGroup.shortLinkWithIcon} &nbsp; 
                                
                                </c:forEach>
                                
                              </td>
                            </tr>
                                         
                            <c:if test="${grouperRequestContainer.grouperLoaderContainer.editLoaderSqlType == 'SQL_GROUP_LIST'}">
                              <tr>
                                <td style="vertical-align: top; white-space: nowrap;"><strong><label for="grouperLoaderSqlGroupQueryId">${textContainer.text['grouperLoaderSqlGroupQuery']}</label></strong></td>
                                <td>
                                     
                                  <textarea id="grouperLoaderSqlGroupQueryId" name="grouperLoaderSqlGroupQueryName" rows="4" cols="40" class="input-block-level">${grouper:escapeHtml(grouperRequestContainer.grouperLoaderContainer.editLoaderSqlGroupQuery)}</textarea>
                                     
                                  <br />
                                  <span class="description">
                                  ${textContainer.text['grouperLoaderSqlGroupQueryDescription']}</span>
                                </td>
                              </tr>
                              
                              <tr>
                                <td style="vertical-align: top; white-space: nowrap;"><strong><label for="grouperLoaderSqlGroupsLikeId">${textContainer.text['grouperLoaderSqlGroupsLike']}</label></strong></td>
                                <td>
                                  <input type="text" style="width: 40em" value="${grouper:escapeHtml(grouperRequestContainer.grouperLoaderContainer.editLoaderGroupsLike)}"
                                     name="grouperLoaderSqlGroupsLikeName" id="grouperLoaderSqlGroupsLikeId" />
                                  <br />
                                  <span class="description">
                                  ${textContainer.text['grouperLoaderSqlGroupsLikeDescription']}</span>
                                </td>
                              </tr>
                              
                              <tr>
                                <td style="vertical-align: top; white-space: nowrap;"><strong><label for="grouperLoaderSyncDisplayNameId">${textContainer.text['grouperLoaderSqlGroupsSyncDisplayNameConfig']}</label></strong></td>
                                <td>
                                
  	                              <select name="grouperLoaderSyncDisplayName" id="grouperLoaderSyncDisplayNameId" style="width: 25em"
  			                            onchange="ajax('../app/UiV2GrouperLoader.editGrouperLoader', {formIds: 'editLoaderFormId'}); return false;">
  			                            <option value=""></option>
  			                            
  			                            <option value="BASE_FOLDER_NAME" ${grouperRequestContainer.grouperLoaderContainer.editLoaderDisplayNameSyncType == 'BASE_FOLDER_NAME' ? 'selected="selected"' : '' } 
                                      >${textContainer.textEscapeXml['grouperLoaderSyncDisplayNameOption__BASE_FOLDER_NAME']}</option>
                                    <option value="LEVELS" ${grouperRequestContainer.grouperLoaderContainer.editLoaderDisplayNameSyncType == 'LEVELS' ? 'selected="selected"'  : '' }
                                      >${textContainer.textEscapeXml['grouperLoaderSyncDisplayNameOption__LEVELS']}</option>
  			                            
  			                          </select>
  	                              <br />
                                  <span class="description">
                                  ${textContainer.text['grouperLoaderSqlGroupsSyncDisplayNameDescription']}</span>
                                </td>
                              </tr>
                              
                              <c:choose>
                              <c:when test="${grouperRequestContainer.grouperLoaderContainer.editLoaderDisplayNameSyncType == 'BASE_FOLDER_NAME'}">
                                <tr>
                                  <td style="vertical-align: top; white-space: nowrap;"><strong><label for="editLoaderBaseFolderNameId">${textContainer.text['grouperLoaderSyncDisplayNameBaseFolderName']}</label></strong></td>
                                  <td>
                                    <span style="white-space: nowrap">
                                      <input type="text" style="width: 20em" value="${grouper:escapeHtml(grouperRequestContainer.grouperLoaderContainer.editLoaderDisplayNameSyncBaseFolderName)}"
                                         name="editLoaderBaseFolderName" id="editLoaderBaseFolderNameId" />
                                      <%-- <span class="requiredField" rel="tooltip" data-html="true" data-delay-show="200" data-placement="right" 
                                        data-original-title="${textContainer.textEscapeDouble['grouperRequiredTooltip']}">*</span> --%>
                                    </span>
                                    <br /><span class="description">${textContainer.text['grouperLoaderSyncDisplayNameBaseFolderNameDescription']}</span>
                                  </td>
                                </tr>
    
                              </c:when>
                              <c:when test="${grouperRequestContainer.grouperLoaderContainer.editLoaderDisplayNameSyncType == 'LEVELS'}">
                                <tr>
                                  <td style="vertical-align: top; white-space: nowrap;"><strong><label for="editLoaderLevelsId">${textContainer.text['grouperLoaderSyncDisplayNameLevels']}</label></strong></td>
                                  <td>
                                    <span style="white-space: nowrap">
                                      <input type="text" style="width: 20em" value="${grouper:escapeHtml(grouperRequestContainer.grouperLoaderContainer.editLoaderDisplayNameSyncLevels)}"
                                         name="editLoaderLevels" id="editLoaderLevelsId" />
                                      <%-- <span class="requiredField" rel="tooltip" data-html="true" data-delay-show="200" data-placement="right" 
                                        data-original-title="${textContainer.textEscapeDouble['grouperRequiredTooltip']}">*</span> --%>
                                    </span>
                                    <br /><span class="description">${textContainer.text['grouperLoaderSyncDisplayNameLevelsDescription']}</span>
                                  </td>
                                </tr>
                              </c:when>
                            </c:choose>
                              
                              <tr>
                                <td style="vertical-align: top; white-space: nowrap;"><strong><label for="grouperLoaderSqlGroupTypesId">${textContainer.text['grouperLoaderSqlGroupTypes']}</label></strong></td>
                                <td>
                                  <input type="text" style="width: 40em" value="${grouper:escapeHtml(grouperRequestContainer.grouperLoaderContainer.editLoaderGroupTypes)}"
                                     name="grouperLoaderSqlGroupTypesName" id="grouperLoaderSqlGroupTypesId" />
                                  <br />
                                  <span class="description">
                                  ${textContainer.text['grouperLoaderSqlGroupTypesDescription']}</span>
                                </td>
                              </tr>
                                                        
                            </c:if>
                          </c:if>
                        </c:if>
  
                        <c:if test="${grouperRequestContainer.grouperLoaderContainer.editLoaderType == 'LDAP'}">
                          <c:if test="${grouperRequestContainer.grouperLoaderContainer.editLoaderShowLdapLoaderType}">
                            <tr>
                              <td style="vertical-align: top; white-space: nowrap;"><strong><label for="grouperLoaderLdapTypeId">${textContainer.text['grouperLoaderLdapLoaderType']}</label></strong></td>
                              <td>
                                <span style="white-space: nowrap">
                                  <select name="grouperLoaderLdapTypeName" id="grouperLoaderLdapTypeId" style="width: 40em"
                                    onchange="ajax('../app/UiV2GrouperLoader.editGrouperLoader', {formIds: 'editLoaderFormId'}); return false;">
                                    <option value="" ></option>
                                    <option value="LDAP_SIMPLE" ${grouperRequestContainer.grouperLoaderContainer.editLoaderLdapType == 'LDAP_SIMPLE' ? 'selected="selected"' : '' } 
                                      >${textContainer.textEscapeXml['grouperLoaderLdapLoaderTypeOption__LDAP_SIMPLE']}</option>
                                    <option value="LDAP_GROUP_LIST" ${grouperRequestContainer.grouperLoaderContainer.editLoaderLdapType == 'LDAP_GROUP_LIST' ? 'selected="selected"'  : '' }
                                      >${textContainer.textEscapeXml['grouperLoaderLdapLoaderTypeOption__LDAP_GROUP_LIST']}</option>
                                    <option value="LDAP_GROUPS_FROM_ATTRIBUTES" ${grouperRequestContainer.grouperLoaderContainer.editLoaderLdapType == 'LDAP_GROUPS_FROM_ATTRIBUTES' ? 'selected="selected"'  : '' }
                                      >${textContainer.textEscapeXml['grouperLoaderLdapLoaderTypeOption__LDAP_GROUPS_FROM_ATTRIBUTES']}</option>
                                  </select>
                                  <span class="requiredField" rel="tooltip" data-html="true" data-delay-show="200" data-placement="right" 
                                    data-original-title="${textContainer.textEscapeDouble['grouperRequiredTooltip']}">*</span>
                                </span>
                                <br />
                                <span class="description">${textContainer.text[grouper:concat2('grouperLoaderLdapLoaderType__',grouperRequestContainer.grouperLoaderContainer.editLoaderLdapType)]}</span>
                              </td>
                            </tr>
                          </c:if>
                          <c:if test="${grouperRequestContainer.grouperLoaderContainer.editLoaderShowLdapServerId}">
                            <tr>
                              <td style="vertical-align: top; white-space: nowrap;"><strong><label for="grouperLoaderLdapServerIdId">${textContainer.text['grouperLoaderLdapServerId']}</label></strong></td>
                              <td>
                                <span style="white-space: nowrap">
                                  <select name="grouperLoaderLdapServerIdName" id="grouperLoaderLdapServerIdId" style="width: 40em"
                                    onchange="ajax('../app/UiV2GrouperLoader.editGrouperLoader', {formIds: 'editLoaderFormId'}); return false;">
                                    <option value="" ></option>
                                    <c:forEach items="${grouperRequestContainer.grouperLoaderContainer.ldapServerIds}" var="ldapServerId">
                                      <option value="${grouper:escapeHtml(ldapServerId.id)}" ${grouperRequestContainer.grouperLoaderContainer.editLoaderLdapServerId == ldapServerId.id ? 'selected="selected"' : '' } 
                                        >${grouper:escapeHtml(ldapServerId.name)}</option>
                                    </c:forEach>
                                  </select>
                                  <span class="requiredField" rel="tooltip" data-html="true" data-delay-show="200" data-placement="right" 
                                    data-original-title="${textContainer.textEscapeDouble['grouperRequiredTooltip']}">*</span>
                                </span>
                                <br />
                                <span class="description">
                                ${grouper:escapeHtml(grouperRequestContainer.grouperLoaderContainer.editLoaderLdapServerIdUrlText)}<br />
                                ${textContainer.text['grouperLoaderLdapServerIdDescription']}</span>
                              </td>
                            </tr>
                          </c:if>
                          <c:if test="${grouperRequestContainer.grouperLoaderContainer.editLoaderShowLdapFilter}">
                            <tr>
                              <td style="vertical-align: top; white-space: nowrap;"><strong><label for="grouperLoaderLdapFilterId">${textContainer.text['grouperLoaderLdapFilter']}</label></strong></td>
                              <td>
                                <span style="white-space: nowrap">
                                     
                                  <textarea id="grouperLoaderLdapFilterId" name="grouperLoaderLdapFilterName" rows="4" cols="40" class="input-block-level">${grouper:escapeHtml(grouperRequestContainer.grouperLoaderContainer.editLoaderLdapFilter)}</textarea>
                                  
                                  <span class="requiredField" rel="tooltip" data-html="true" data-delay-show="200" data-placement="right" 
                                    data-original-title="${textContainer.textEscapeDouble['grouperRequiredTooltip']}">*</span>
                                </span>
                                <br />
                                <span class="description">
                                  ${textContainer.text[grouper:concat2('grouperLoaderLdapFilterDescription__',grouperRequestContainer.grouperLoaderContainer.editLoaderLdapType)]}</span>
                              </td>
                            </tr>
                          </c:if>
                          <c:if test="${grouperRequestContainer.grouperLoaderContainer.editLoaderShowFields}">
  
  	                        <tr>
  	                          <td style="vertical-align: top; white-space: nowrap;"><strong><label for="editLoaderLdapSubjectAttributeId">${textContainer.text['grouperLoaderLdapSubjectAttributeName']}</label></strong></td>
  	                          <td>
  	                            <span style="white-space: nowrap">
  		                          <input type="text" style="width: 20em" value="${grouper:escapeHtml(grouperRequestContainer.grouperLoaderContainer.editLoaderLdapSubjectAttributeName)}"
  		                              name="editLoaderLdapSubjectAttributeName" id="editLoaderLdapSubjectAttributeId" />
  	                                <c:if test="${grouperRequestContainer.grouperLoaderContainer.editLoaderLdapType == 'LDAP_GROUP_LIST'
  	                                    || grouperRequestContainer.grouperLoaderContainer.editLoaderLdapType == 'LDAP_SIMPLE'}">
  	                                  <span class="requiredField" rel="tooltip" data-html="true" data-delay-show="200" data-placement="right" 
  	                                    data-original-title="${textContainer.textEscapeDouble['grouperRequiredTooltip']}">*</span>
  	                                </c:if>
  	                            </span>
  	                               
  	                            <br />
  	                            <span class="description">${textContainer.text[grouper:concat2('grouperLoaderLdapSubjectAttributeNameDescription__',grouperRequestContainer.grouperLoaderContainer.editLoaderLdapType)]}</span>
  	                            
  	                          </td>
  	                        </tr>
  
  	                        <tr>
  	                          <td style="vertical-align: top; white-space: nowrap;"><strong><label for="editLoaderLdapSearchDnId">${textContainer.text['grouperLoaderLdapSearchDn']}</label></strong></td>
  	                          <td>
  	                            <span style="white-space: nowrap">
  		                          <input type="text" style="width: 20em" value="${grouper:escapeHtml(grouperRequestContainer.grouperLoaderContainer.editLoaderLdapSearchDn)}"
  		                              name="editLoaderLdapSearchDnName" id="editLoaderLdapSearchDnId" />
  	                            </span>
  	                               
  	                            <br />
  	                            <span class="description">${textContainer.text['grouperLoaderLdapSearchDnDescription']}</span>
  	                            
  	                          </td>
  	                        </tr>
  
                            <tr>
                              <td style="vertical-align: top; white-space: nowrap;"><strong><label for="editLoaderCronId">${textContainer.text['grouperLoaderLdapQuartzCron']}</label></strong></td>
                              <td>
                                <span style="white-space: nowrap">
                                  <input type="text" style="width: 20em" value="${grouper:escapeHtml(grouperRequestContainer.grouperLoaderContainer.editLoaderCron)}"
                                     name="editLoaderCronName" id="editLoaderCronId" />
                                  <span class="requiredField" rel="tooltip" data-html="true" data-delay-show="200" data-placement="right" 
                                    data-original-title="${textContainer.textEscapeDouble['grouperRequiredTooltip']}">*</span>
                                </span>
                                <c:if test="${grouperRequestContainer.grouperLoaderContainer.editLoaderCronDescription != null && grouperRequestContainer.grouperLoaderContainer.editLoaderCronDescription != ''}">
                                  <br /><span class="description">${grouper:escapeHtml(grouperRequestContainer.grouperLoaderContainer.editLoaderCronDescription)}</span>
                                </c:if>
                                <br /><span class="description">${textContainer.text['grouperLoaderSqlCronDescription']}</span>
                              </td>
                            </tr>
  	                        <tr>
  	                          <td style="vertical-align: top; white-space: nowrap;"><strong><label for="editLoaderLdapSourceId">${textContainer.text['grouperLoaderLdapSourceId']}</label></strong></td>
  	                          <td>
  	                            <span style="white-space: nowrap">
                                  <select name="editLoaderLdapSourceName" id="editLoaderLdapSourceId" style="width: 40em">
                                    <option value="" ></option>
                                    <c:forEach items="${grouperRequestContainer.grouperLoaderContainer.sources}" var="source">
                                      <option value="${grouper:escapeHtml(source.id)}" ${grouperRequestContainer.grouperLoaderContainer.editLoaderLdapSourceId == source.id ? 'selected="selected"' : '' } 
                                        >${grouper:escapeHtml(source.name)}</option>
                                    </c:forEach>
                                  </select>
  	                            </span>
  	                               
  	                            <br />
  	                            <span class="description">${textContainer.text['grouperLoaderLdapSourceIdDescription']}</span>
  	                            
  	                          </td>
  	                        </tr>
  
  	                        <tr>
  	                          <td style="vertical-align: top; white-space: nowrap;"><strong><label for="editLoaderLdapSubjectLookupTypeId">${textContainer.text['grouperLoaderLdapSubjectLookupType']}</label></strong></td>
  	                          <td>
  	                            <span style="white-space: nowrap">
  
                                  <select name="editLoaderLdapSubjectLookupTypeName" id="editLoaderLdapSubjectLookupTypeId" style="width: 20em">
                                    <option value="subjectId" 
  	                                  ${grouperRequestContainer.grouperLoaderContainer.editLoaderLdapSubjectLookupType == 'subjectId' ? 'selected="selected"' : '' }
                                    >subjectId</option>
                                    <option value="subjectIdentifier" 
  	                                  ${grouperRequestContainer.grouperLoaderContainer.editLoaderLdapSubjectLookupType == 'subjectIdentifier' ? 'selected="selected"' : '' }
                                    >subjectIdentifier</option>
                                    <option value="subjectIdOrIdentifier" 
  	                                  ${grouperRequestContainer.grouperLoaderContainer.editLoaderLdapSubjectLookupType == 'subjectIdOrIdentifier' ? 'selected="selected"' : '' }
                                    >subjectIdOrIdentifier</option>
                                  </select>
  			                              
  	                            </span>
  	                               
  	                            <br />
  	                            <span class="description">${textContainer.text['grouperLoaderLdapSubjectLookupTypeDescription']}</span>
  	                            
  	                          </td>
  	                        </tr>
  
  	                        <tr>
  	                          <td style="vertical-align: top; white-space: nowrap;"><strong><label for="editLoaderLdapSearchScopeId">${textContainer.text['grouperLoaderLdapSearchScope']}</label></strong></td>
  	                          <td>
  	                            <span style="white-space: nowrap">
  	                               
  	                              <select name="editLoaderLdapSearchScopeName" id="editLoaderLdapSearchScopeId" style="width: 20em">
  	                                <option value="OBJECT_SCOPE" 
  	                                 ${grouperRequestContainer.grouperLoaderContainer.editLoaderLdapSearchScope == 'OBJECT_SCOPE' ? 'selected="selected"' : '' }
  	                                >OBJECT_SCOPE</option>
  	                                <option value="ONELEVEL_SCOPE" 
  	                                 ${grouperRequestContainer.grouperLoaderContainer.editLoaderLdapSearchScope == 'ONELEVEL_SCOPE' ? 'selected="selected"' : '' }
  	                                >ONELEVEL_SCOPE</option>
  	                                <option value="SUBTREE_SCOPE" 
  	                                 ${(grouperRequestContainer.grouperLoaderContainer.editLoaderLdapSearchScope == 'SUBTREE_SCOPE' 
  	                                   || grouperRequestContainer.grouperLoaderContainer.editLoaderLdapSearchScope == null) ? 'selected="selected"' : '' }
  	                                >SUBTREE_SCOPE</option>
  	                              </select>
  	                            </span>
  	                              
  	                            <br />
  	                            <span class="description">${textContainer.text['grouperLoaderLdapSearchScopeDescription']}</span>
  	                            
  	                          </td>
  	                        </tr>
  
                            <tr>
                              <td style="vertical-align: top; white-space: nowrap;"><strong><label for="editLoaderPriorityId">${textContainer.text['grouperLoaderSqlPriority']}</label></strong></td>
                              <td>
                                <span style="white-space: nowrap">
                                  <input type="text" style="width: 20em" value="${grouper:escapeHtml(grouperRequestContainer.grouperLoaderContainer.editLoaderPriority)}"
                                     name="editLoaderPriorityName" id="editLoaderPriorityId" />
                                  <br />
                                  <span class="description">
                                    <c:choose>
                                      <c:when test="${grouperRequestContainer.grouperLoaderContainer.editLoaderPriorityInt == -200}">
                                        ${textContainer.text['grouperLoaderSqlPriorityInvalid']}
                                      </c:when>
                                      <c:when test="${grouperRequestContainer.grouperLoaderContainer.editLoaderPriorityInt < 5}">
                                        ${textContainer.text['grouperLoaderSqlPriorityLow']}
                                      </c:when>
                                      <c:when test="${grouperRequestContainer.grouperLoaderContainer.editLoaderPriorityInt == 5}">
                                        ${textContainer.text['grouperLoaderSqlPriorityAverage']}
                                      </c:when>
                                      <c:when test="${grouperRequestContainer.grouperLoaderContainer.editLoaderPriorityInt > 5}">
                                        ${textContainer.text['grouperLoaderSqlPriorityHigh']}
                                      </c:when>
                                      
                                    </c:choose>
                                  </span>
                                </span>
                              </td>
                            </tr>
                            <c:if test="${grouperRequestContainer.grouperLoaderContainer.editLoaderLdapType == 'LDAP_GROUPS_FROM_ATTRIBUTES'}">
                              <tr>
                                <td style="vertical-align: top; white-space: nowrap;"><strong><label for="editLoaderLdapAttributeFilterExpressionId">${textContainer.text['grouperLoaderLdapAttributeFilterExpression']}</label></strong></td>
                                <td>
                                  <input type="text" style="width: 20em" value="${grouper:escapeHtml(grouperRequestContainer.grouperLoaderContainer.editLoaderLdapAttributeFilterExpression)}"
                                     name="editLoaderLdapAttributeFilterExpressionName" id="editLoaderLdapAttributeFilterExpressionId" />
                                  <br /><span class="description">${textContainer.text['grouperLoaderLdapAttributeFilterExpressionDescription']}</span>
                                  
                                </td>
                              </tr>
                              <tr>
                                <td style="vertical-align: top; white-space: nowrap;"><strong><label for="editLoaderLdapResultsTransformationClassId">${textContainer.text['grouperLoaderLdapResultsTransformationClass']}</label></strong></td>
                                <td>
                                  <input type="text" style="width: 20em" value="${grouper:escapeHtml(grouperRequestContainer.grouperLoaderContainer.editLoaderLdapResultsTransformationClass)}"
                                     name="editLoaderLdapResultsTransformationClassName" id="editLoaderLdapResultsTransformationClassId" />
                                  <br /><span class="description">${textContainer.text['grouperLoaderLdapResultsTransformationClassDescription']}</span>
  
                                </td>
                              </tr>
                            
                            </c:if>
                            <tr>
                              <td style="vertical-align: top; white-space: nowrap;"><strong><label for="editLoaderLdapSubjectExpressionId">${textContainer.text['grouperLoaderLdapSubjectExpression']}</label></strong></td>
                              <td>
                                <input type="text" style="width: 20em" value="${grouper:escapeHtml(grouperRequestContainer.grouperLoaderContainer.editLoaderLdapSubjectExpression)}"
                                   name="editLoaderLdapSubjectExpressionName" id="editLoaderLdapSubjectExpressionId" />
                                <br /><span class="description">${textContainer.text['grouperLoaderLdapSubjectExpressionDescription']}</span>
                              </td>
                            </tr>
  	                        <c:if test="${grouperRequestContainer.grouperLoaderContainer.editLoaderLdapType == 'LDAP_GROUPS_FROM_ATTRIBUTES' }">
  	                            
  	                          <tr>
  	                            <td style="vertical-align: top; white-space: nowrap;"><strong><label for="editLoaderLdapGroupAttributeId">${textContainer.text['grouperLoaderLdapGroupAttributeName']}</label></strong></td>
  	                            <td>
                                  <span style="white-space: nowrap">
    	                              <input type="text" style="width: 20em" value="${grouper:escapeHtml(grouperRequestContainer.grouperLoaderContainer.editLoaderLdapGroupAttributeName)}"
    	                                 name="editLoaderLdapGroupAttributeName" id="editLoaderLdapGroupAttributeId" />
                                    <span class="requiredField" rel="tooltip" data-html="true" data-delay-show="200" data-placement="right" 
                                      data-original-title="${textContainer.textEscapeDouble['grouperRequiredTooltip']}">*</span>
                                   </span>
                                     
  	                              <br /><span class="description">${textContainer.text['grouperLoaderLdapGroupAttributeNameDescription']}</span>
  	                            </td>
  	                          </tr>
  	                        </c:if>
                            <tr>
                              <td style="vertical-align: top; white-space: nowrap;"><strong><label for="editLoaderAndGroupsId">${textContainer.text['grouperLoaderSqlAndGroups']}</label></strong></td>
                              <td>
                                <input type="text" style="width: 20em" value="${grouper:escapeHtml(grouperRequestContainer.grouperLoaderContainer.editLoaderAndGroups)}"
                                   name="editLoaderAndGroupsName" id="editLoaderAndGroupsId" />
                                <br />
                                <c:forEach var="guiGroup" items="${grouperRequestContainer.grouperLoaderContainer.editLoaderAndGuiGroups}">
                                
                                  ${guiGroup.shortLinkWithIcon} &nbsp; 
                                
                                </c:forEach>
                                
                              </td>
                            </tr>
  
  	                        <c:if test="${grouperRequestContainer.grouperLoaderContainer.editLoaderLdapType == 'LDAP_GROUP_LIST'
  	                            || grouperRequestContainer.grouperLoaderContainer.editLoaderLdapType == 'LDAP_GROUPS_FROM_ATTRIBUTES' }">
  	                          <tr>
  	                            <td style="vertical-align: top; white-space: nowrap;"><strong><label for="editLoaderLdapExtraAttributesId">${textContainer.text['grouperLoaderLdapExtraAttributes']}</label></strong></td>
  	                            <td>
  	                              <input type="text" style="width: 20em" value="${grouper:escapeHtml(grouperRequestContainer.grouperLoaderContainer.editLoaderLdapExtraAttributes)}"
  	                                 name="editLoaderLdapExtraAttributesName" id="editLoaderLdapExtraAttributesId" />
  	                              <br /><span class="description">${textContainer.text['grouperLoaderLdapExtraAttributesDescription']}</span>
  	                            </td>
  	                          </tr>
  	                          <tr>
  	                            <td style="vertical-align: top; white-space: nowrap;"><strong><label for="grouperLoaderSqlGroupsLikeId">${textContainer.text['grouperLoaderLdapGroupsLike']}</label></strong></td>
  	                            <td>
  	                              <input type="text" style="width: 40em" value="${grouper:escapeHtml(grouperRequestContainer.grouperLoaderContainer.editLoaderGroupsLike)}"
  	                                 name="grouperLoaderSqlGroupsLikeName" id="grouperLoaderSqlGroupsLikeId" />
  	                              <br />
  	                              <span class="description">
  	                              ${textContainer.text['grouperLoaderLdapGroupsLikeDescription']}</span>
  	                            </td>
  	                          </tr>
  	                          <tr>
  	                            <td style="vertical-align: top; white-space: nowrap;"><strong><label for="grouperLoaderGroupNameExpressionId">${textContainer.text['grouperLoaderLdapGroupNameExpression']}</label></strong></td>
  	                            <td>
  	                              <input type="text" style="width: 40em" value="${grouper:escapeHtml(grouperRequestContainer.grouperLoaderContainer.editLoaderLdapGroupNameExpression)}"
  	                                 name="grouperLoaderGroupNameExpressionName" id="grouperLoaderGroupNameExpressionId" />
  	                              <br />
  	                              <span class="description">
  	                              ${textContainer.text['grouperLoaderLdapGroupNameExpressionDescription']}</span>
  	                            </td>
  	                          </tr>
  	                          
  	                          <tr>
  	                            <td style="vertical-align: top; white-space: nowrap;"><strong><label for="grouperLoaderLdapGroupDisplayNameId">${textContainer.text['grouperLoaderLdapGroupDisplayNameExpression']}</label></strong></td>
  	                            <td>
  	                              <input type="text" style="width: 40em" value="${grouper:escapeHtml(grouperRequestContainer.grouperLoaderContainer.editLoaderLdapGroupDisplayNameExpression)}"
  	                                 name="grouperLoaderLdapGroupDisplayNameName" id="grouperLoaderLdapGroupDisplayNameId" />
  	                              <br />
  	                              <span class="description">
  	                              ${textContainer.text['grouperLoaderLdapGroupDisplayNameExpressionDescription']}</span>
  	                            </td>
  	                          </tr>
  	                          <tr>
  	                            <td style="vertical-align: top; white-space: nowrap;"><strong><label for="grouperLoaderLdapGroupDescriptionId">${textContainer.text['grouperLoaderLdapGroupDescriptionExpression']}</label></strong></td>
  	                            <td>
  	                              <input type="text" style="width: 40em" value="${grouper:escapeHtml(grouperRequestContainer.grouperLoaderContainer.editLoaderLdapGroupDescriptionExpression)}"
  	                                 name="grouperLoaderLdapGroupDescriptionName" id="grouperLoaderLdapGroupDescriptionId" />
  	                              <br />
  	                              <span class="description">
  	                              ${textContainer.text['grouperLoaderLdapGroupDescriptionExpressionDescription']}</span>
  	                            </td>
  	                          </tr>
                            
  	                          <tr>
  	                            <td style="vertical-align: top; white-space: nowrap;"><strong><label for="grouperLoaderSqlGroupTypesId">${textContainer.text['grouperLoaderLdapGroupTypes']}</label></strong></td>
  	                            <td>
  	                              <input type="text" style="width: 40em" value="${grouper:escapeHtml(grouperRequestContainer.grouperLoaderContainer.editLoaderGroupTypes)}"
  	                                 name="grouperLoaderSqlGroupTypesName" id="grouperLoaderSqlGroupTypesId" />
  	                              <br />
  	                              <span class="description">
  	                              ${textContainer.text['grouperLoaderLdapGroupTypesDescription']}</span>
  	                            </td>
  	                          </tr>
  	                          <tr>
  	                            <td style="vertical-align: top; white-space: nowrap;"><strong><label for="grouperLoaderLdapReadersId">${textContainer.text['grouperLoaderLdapReaders']}</label></strong></td>
  	                            <td>
  	                              <input type="text" style="width: 40em" value="${grouper:escapeHtml(grouperRequestContainer.grouperLoaderContainer.editLoaderLdapReaders)}"
  	                                 name="grouperLoaderLdapReadersName" id="grouperLoaderLdapReadersId" />
  	                              <br />
  	                              <span class="description">
  	                              ${textContainer.text['grouperLoaderLdapReadersDescription']}</span>
  	                            </td>
  	                          </tr>
                            
  	                          <tr>
  	                            <td style="vertical-align: top; white-space: nowrap;"><strong><label for="grouperLoaderLdapViewersId">${textContainer.text['grouperLoaderLdapViewers']}</label></strong></td>
  	                            <td>
  	                              <input type="text" style="width: 40em" value="${grouper:escapeHtml(grouperRequestContainer.grouperLoaderContainer.editLoaderLdapViewers)}"
  	                                 name="grouperLoaderLdapViewersName" id="grouperLoaderLdapViewersId" />
  	                              <br />
  	                              <span class="description">
  	                              ${textContainer.text['grouperLoaderLdapViewersDescription']}</span>
  	                            </td>
  	                          </tr>
                            
  	                          <tr>
  	                            <td style="vertical-align: top; white-space: nowrap;"><strong><label for="grouperLoaderLdapAdminsId">${textContainer.text['grouperLoaderLdapAdmins']}</label></strong></td>
  	                            <td>
  	                              <input type="text" style="width: 40em" value="${grouper:escapeHtml(grouperRequestContainer.grouperLoaderContainer.editLoaderLdapAdmins)}"
  	                                 name="grouperLoaderLdapAdminsName" id="grouperLoaderLdapAdminsId" />
  	                              <br />
  	                              <span class="description">
  	                              ${textContainer.text['grouperLoaderLdapAdminsDescription']}</span>
  	                            </td>
  	                          </tr>
                          
  	                          <tr>
  	                            <td style="vertical-align: top; white-space: nowrap;"><strong><label for="grouperLoaderLdapUpdatersId">${textContainer.text['grouperLoaderLdapUpdaters']}</label></strong></td>
  	                            <td>
  	                              <input type="text" style="width: 40em" value="${grouper:escapeHtml(grouperRequestContainer.grouperLoaderContainer.editLoaderLdapUpdaters)}"
  	                                 name="grouperLoaderLdapUpdatersName" id="grouperLoaderLdapUpdatersId" />
  	                              <br />
  	                              <span class="description">
  	                              ${textContainer.text['grouperLoaderLdapUpdatersDescription']}</span>
  	                            </td>
  	                          </tr>
                          
  	                          <tr>
  	                            <td style="vertical-align: top; white-space: nowrap;"><strong><label for="grouperLoaderLdapOptinsId">${textContainer.text['grouperLoaderLdapOptins']}</label></strong></td>
  	                            <td>
  	                              <input type="text" style="width: 40em" value="${grouper:escapeHtml(grouperRequestContainer.grouperLoaderContainer.editLoaderLdapOptins)}"
  	                                 name="grouperLoaderLdapOptinsName" id="grouperLoaderLdapOptinsId" />
  	                              <br />
  	                              <span class="description">
  	                              ${textContainer.text['grouperLoaderLdapOptinsDescription']}</span>
  	                            </td>
  	                          </tr>
                          
  	                          <tr>
  	                            <td style="vertical-align: top; white-space: nowrap;"><strong><label for="grouperLoaderLdapOptoutsId">${textContainer.text['grouperLoaderLdapOptouts']}</label></strong></td>
  	                            <td>
  	                              <input type="text" style="width: 40em" value="${grouper:escapeHtml(grouperRequestContainer.grouperLoaderContainer.editLoaderLdapOptouts)}"
  	                                 name="grouperLoaderLdapOptoutsName" id="grouperLoaderLdapOptoutsId" />
  	                              <br />
  	                              <span class="description">
  	                              ${textContainer.text['grouperLoaderLdapOptoutsDescription']}</span>
  	                            </td>
  	                          </tr>
  
  	                          <tr>
  	                            <td style="vertical-align: top; white-space: nowrap;"><strong><label for="grouperLoaderLdapAttrReadersId">${textContainer.text['grouperLoaderLdapAttrReaders']}</label></strong></td>
  	                            <td>
  	                              <input type="text" style="width: 40em" value="${grouper:escapeHtml(grouperRequestContainer.grouperLoaderContainer.editLoaderLdapAttrReaders)}"
  	                                 name="grouperLoaderLdapAttrReadersName" id="grouperLoaderLdapAttrReadersId" />
  	                              <br />
  	                              <span class="description">
  	                              ${textContainer.text['grouperLoaderLdapAttrReadersDescription']}</span>
  	                            </td>
  	                          </tr>
                            
  	                          <tr>
  	                            <td style="vertical-align: top; white-space: nowrap;"><strong><label for="grouperLoaderLdapAttrUpdatersId">${textContainer.text['grouperLoaderLdapAttrUpdaters']}</label></strong></td>
  	                            <td>
  	                              <input type="text" style="width: 40em" value="${grouper:escapeHtml(grouperRequestContainer.grouperLoaderContainer.editLoaderLdapAttrUpdaters)}"
  	                                 name="grouperLoaderLdapAttrUpdatersName" id="grouperLoaderLdapAttrUpdatersId" />
  	                              <br />
  	                              <span class="description">
  	                              ${textContainer.text['grouperLoaderLdapAttrUpdatersDescription']}</span>
  	                            </td>
  	                          </tr>
  	                          
  	                        </c:if>
  
                          </c:if>
                        </c:if>
  
                        <c:if test="${grouperRequestContainer.grouperLoaderContainer.editLoaderShowFields
                             && (grouperRequestContainer.grouperLoaderContainer.editLoaderType == 'SQL'
                             && (grouperRequestContainer.grouperLoaderContainer.editLoaderSqlType == 'SQL_GROUP_LIST'
                                || grouperRequestContainer.grouperLoaderContainer.editLoaderSqlType == 'SQL_SIMPLE'))
                                || (grouperRequestContainer.grouperLoaderContainer.editLoaderType == 'LDAP'
                             && (grouperRequestContainer.grouperLoaderContainer.editLoaderLdapType == 'LDAP_GROUP_LIST'
                                        || grouperRequestContainer.grouperLoaderContainer.editLoaderLdapType == 'LDAP_SIMPLE'
                                        || grouperRequestContainer.grouperLoaderContainer.editLoaderLdapType == 'LDAP_GROUPS_FROM_ATTRIBUTES'))}">
                          <tr>
                            <td style="vertical-align: top; white-space: nowrap;"><strong><label for="editFailsafeId">${textContainer.text['grouperLoaderFailsafeLabel']}</label></strong></td>
                            <td>
                              <span style="white-space: nowrap">
                                <select name="editFailsafeName" id="editFailsafeId" style="width: 30em"
                                onchange="ajax('../app/UiV2GrouperLoader.editGrouperLoader', {formIds: 'editLoaderFormId'}); return false;">
                                  <option value="false"
                                   ${grouperRequestContainer.grouperLoaderContainer.customizeFailsafeTrue ? '' : 'selected="selected"' }
                                  >${textContainer.textEscapeXml['grouperLoaderNoFailsafeLabel']}</option>
                                  <option value="true"
                                   ${grouperRequestContainer.grouperLoaderContainer.customizeFailsafeTrue ? 'selected="selected"' : '' }
                                  >${textContainer.textEscapeXml['grouperLoaderYesFailsafeLabel']}</option>
                                </select>
                              </span>
                              <br />
                              <span class="description">${textContainer.text['grouperLoaderFailsafeDescription']}</span>
                            </td>
                          </tr>
                          <c:if test="${grouperRequestContainer.grouperLoaderContainer.customizeFailsafeTrue}">
                            <tr>
                              <td style="vertical-align: top; white-space: nowrap;"><strong><label for="editLoaderFailsafeUseId">${textContainer.text['grouperLoaderFailsafeUseLabel']}</label></strong></td>
                              <td>
                                <span style="white-space: nowrap">
                                  <select name="editFailsafeUseName" id="editFailsafeUseId" style="width: 30em">
                                    <option value=""
                                    >${textContainer.textEscapeXml['grouperLoaderDefaultUseFailsafeLabel']}</option>
                                    <option value="false"
                                    ${grouperRequestContainer.grouperLoaderContainer.editLoaderFailsafeUseOrDefault == 'false' ? 'selected="selected"' : '' }
                                    >${textContainer.textEscapeXml['grouperLoaderNoDoNotUseFailsafeLabel']}</option>
                                    <option value="true"
                                    ${grouperRequestContainer.grouperLoaderContainer.editLoaderFailsafeUseOrDefault == 'true' ? 'selected="selected"' : '' }
                                    >${textContainer.textEscapeXml['grouperLoaderYesUseFailsafeLabel']}</option>
                                  </select>
                                </span>
                                <br />
                                <span class="description">${textContainer.text['grouperLoaderFailsafeUseDescription']}</span>
                              </td>
                            </tr>
                            <tr>
                              <td style="vertical-align: top; white-space: nowrap;"><strong><label for="editLoaderFailsafeSendEmailId">${textContainer.text['grouperLoaderFailsafeSendEmailLabel']}</label></strong></td>
                              <td>
                                <span style="white-space: nowrap">
                                  <select name="editFailsafeSendEmailName" id="editFailsafeSendEmailId" style="width: 30em">
                                  
                                    <option value=""
                                    >${textContainer.textEscapeXml['grouperLoaderDefaultSendEmailLabel']}</option>
                                    <option value="false"
                                    ${grouperRequestContainer.grouperLoaderContainer.editLoaderFailsafeSendEmailOrDefault == 'false' ? 'selected="selected"' : '' }
                                    >${textContainer.textEscapeXml['grouperLoaderNoDoNotSendEmailFailsafeLabel']}</option>
                                    <option value="true"
                                    ${grouperRequestContainer.grouperLoaderContainer.editLoaderFailsafeSendEmailOrDefault == 'true' ? 'selected="selected"' : '' }
                                    >${textContainer.textEscapeXml['grouperLoaderYesSendEmailFailsafeLabel']}</option>
                                  </select>
                                </span>
                                <br />
                                <span class="description">${textContainer.text['grouperLoaderFailsafeSendEmailDescription']}</span>
                              </td>
                            </tr>
                            <tr>
                              <td style="vertical-align: top; white-space: nowrap;"><strong><label for="editLoaderMinGroupSizeId">${textContainer.text['grouperLoaderMinGroupSizeLabel']}</label></strong></td>
                              <td>
                              <input type="text" style="width: 40em" value="${grouper:escapeHtml(grouperRequestContainer.grouperLoaderContainer.editLoaderMinGroupSize)}"
                                 name="editLoaderMinGroupSizeName" id="editLoaderMinGroupSizeId" />
                              <br />
                              <span class="description">
                              ${textContainer.text['grouperLoaderMinGroupSizeDescription']}</span>
                              </td>
                            </tr>
                            <tr>
                              <td style="vertical-align: top; white-space: nowrap;"><strong><label for="editLoaderMaxGroupPercentRemoveId">${textContainer.text['grouperLoaderMaxGroupPercentRemoveLabel']}</label></strong></td>
                              <td>
                              <input type="text" style="width: 40em" value="${grouper:escapeHtml(grouperRequestContainer.grouperLoaderContainer.editLoaderMaxGroupPercentRemove)}"
                                 name="editLoaderMaxGroupPercentRemoveName" id="editLoaderMaxGroupPercentRemoveId" />
                              <br />
                              <span class="description">
                              ${textContainer.text['grouperLoaderMaxGroupPercentRemoveDescription']}</span>
                              </td>
                            </tr>
                            <c:if test="${(grouperRequestContainer.grouperLoaderContainer.editLoaderType == 'SQL' && grouperRequestContainer.grouperLoaderContainer.editLoaderSqlType == 'SQL_SIMPLE')
                                || (grouperRequestContainer.grouperLoaderContainer.editLoaderType == 'LDAP' && grouperRequestContainer.grouperLoaderContainer.editLoaderLdapType == 'LDAP_SIMPLE')}">
                              <tr>
                                <td style="vertical-align: top; white-space: nowrap;"><strong><label for="editLoaderMinGroupNumberOfMembersId">${textContainer.text['grouperLoaderMinGroupNumberOfMembersLabel']}</label></strong></td>
                                <td>
                                <input type="text" style="width: 40em" value="${grouper:escapeHtml(grouperRequestContainer.grouperLoaderContainer.editLoaderMinGroupNumberOfMembers)}"
                                   name="editLoaderMinGroupNumberOfMembersName" id="editLoaderMinGroupNumberOfMembersId" />
                                <br />
                                <span class="description">
                                ${textContainer.text['grouperLoaderMinGroupNumberOfMembersDescription']}</span>
                                </td>
                              </tr>
                            </c:if>
                            <c:if test="${(grouperRequestContainer.grouperLoaderContainer.editLoaderType == 'SQL' && grouperRequestContainer.grouperLoaderContainer.editLoaderSqlType == 'SQL_GROUP_LIST')
                                || (grouperRequestContainer.grouperLoaderContainer.editLoaderType == 'LDAP' && (grouperRequestContainer.grouperLoaderContainer.editLoaderLdapType == 'LDAP_GROUP_LIST'
                                        || grouperRequestContainer.grouperLoaderContainer.editLoaderLdapType == 'LDAP_GROUPS_FROM_ATTRIBUTES' ))}">
                              <tr>
                                <td style="vertical-align: top; white-space: nowrap;"><strong><label for="editLoaderMinManagedGroupsId">${textContainer.text['grouperLoaderMinManagedGroupsLabel']}</label></strong></td>
                                <td>
                                <input type="text" style="width: 40em" value="${grouper:escapeHtml(grouperRequestContainer.grouperLoaderContainer.editLoaderMinManagedGroups)}"
                                   name="editLoaderMinManagedGroupsName" id="editLoaderMinManagedGroupsId" />
                                <br />
                                <span class="description">
                                ${textContainer.text['grouperLoaderMinManagedGroupsDescription']}</span>
                                </td>
                              </tr>
                              <tr>
                                <td style="vertical-align: top; white-space: nowrap;"><strong><label for="editLoaderMaxOverallPercentGroupsRemoveId">${textContainer.text['grouperLoaderMaxOverallPercentGroupsRemoveLabel']}</label></strong></td>
                                <td>
                                <input type="text" style="width: 40em" value="${grouper:escapeHtml(grouperRequestContainer.grouperLoaderContainer.editLoaderMaxOverallPercentGroupsRemove)}"
                                   name="editLoaderMaxOverallPercentGroupsRemoveName" id="editLoaderMaxOverallPercentGroupsRemoveId" />
                                <br />
                                <span class="description">
                                ${textContainer.text['grouperLoaderMaxOverallPercentGroupsRemoveDescription']}</span>
                                </td>
                              </tr>
                              <tr>
                                <td style="vertical-align: top; white-space: nowrap;"><strong><label for="editLoaderMaxOverallPercentMembershipsRemoveId">${textContainer.text['grouperLoaderMaxOverallPercentMembershipsRemoveLabel']}</label></strong></td>
                                <td>
                                <input type="text" style="width: 40em" value="${grouper:escapeHtml(grouperRequestContainer.grouperLoaderContainer.editLoaderMaxOverallPercentMembershipsRemove)}"
                                   name="editLoaderMaxOverallPercentMembershipsRemoveName" id="editLoaderMaxOverallPercentMembershipsRemoveId" />
                                <br />
                                <span class="description">
                                ${textContainer.text['grouperLoaderMaxOverallPercentMembershipsRemoveDescription']}</span>
                                </td>
                              </tr>
                              <tr>
                                <td style="vertical-align: top; white-space: nowrap;"><strong><label for="editLoaderMinOverallNumberOfMembersId">${textContainer.text['grouperLoaderMinOverallNumberOfMembersLabel']}</label></strong></td>
                                <td>
                                <input type="text" style="width: 40em" value="${grouper:escapeHtml(grouperRequestContainer.grouperLoaderContainer.editLoaderMinOverallNumberOfMembers)}"
                                   name="editLoaderMinOverallNumberOfMembersName" id="editLoaderMinOverallNumberOfMembersId" />
                                <br />
                                <span class="description">
                                ${textContainer.text['grouperLoaderMinOverallNumberOfMembersDescription']}</span>
                                </td>
                              </tr>
                            
                            </c:if>
                          </c:if>
                        </c:if>
  
                        <c:if test="${grouperRequestContainer.grouperLoaderContainer.editLoaderShowFields}">
                          <c:if test="${grouperRequestContainer.grouperLoaderContainer.isLoaderGroup() == true}">
                            <tr>
                              <td style="vertical-align: top; white-space: nowrap;"><strong><label for="editLoaderScheduleJobId">${textContainer.text['grouperLoaderScheduleJob']}</label></strong></td>
                              <td>
                                <span style="white-space: nowrap">
                                  <select name="editLoaderScheduleJobName" id="editLoaderScheduleJobId" style="width: 30em">
                                    <option value="true">${textContainer.textEscapeXml['grouperLoaderYesScheduleJobLabel']}</option>
                                    <option value="false">${textContainer.textEscapeXml['grouperLoaderNoDoNotScheduleJobLabel']}</option>
                                  </select>
                                </span>
                                <br />
                                <span class="description">${textContainer.text['grouperLoaderScheduleJobDescription']}</span>
                              </td>
                            </tr>
                            
                            
                          </c:if>
                        </c:if>
                      </c:if>
                      
                      <tr>
                        <td></td>
                        <c:if test="${grouperRequestContainer.grouperLoaderContainer.editLoaderConstructScript == 'pattern'}">
                          <td style="white-space: nowrap; padding-top: 2em; padding-bottom: 2em;"><input type="submit" class="btn btn-primary" aria-controls="groupFilterResultsId" id="filterSubmitId" 
                            value="${textContainer.text['grouperLoaderEditButtonContinue'] }" 
                            onclick="ajax('../app/UiV2GrouperLoader.editGrouperLoaderSave', {formIds: 'editLoaderFormId'}); return false;"> 
                            &nbsp; 
                            <a class="btn btn-cancel" role="button" 
                              onclick="return guiV2link('operation=UiV2GrouperLoader.loader?groupId=${grouperRequestContainer.groupContainer.guiGroup.group.id}'); return false;"
                              >${textContainer.text['grouperLoaderEditButtonCancel'] }</a>
                          </td>
                        </c:if>
                        <c:if test="${grouperRequestContainer.grouperLoaderContainer.editLoaderConstructScript != 'pattern'}">
                          
                          <c:if test="${grouperRequestContainer.grouperLoaderContainer.editLoaderType == 'JEXL_SCRIPT'}">
                            
                            <td style="white-space: nowrap; padding-top: 2em; padding-bottom: 2em;"><input type="submit" class="btn btn-primary" aria-controls="groupFilterResultsId" id="filterSubmitId" 
                              value="${textContainer.text['grouperLoaderEditButtonAnalyze'] }" 
                              onclick="ajax('../app/UiV2GrouperLoader.editGrouperLoaderAnalyze', {formIds: 'editLoaderFormId'}); return false;"> 
                              &nbsp; 
                              <a class="btn btn-cancel" role="button"
                                onclick="return guiV2link('operation=UiV2GrouperLoader.loader?groupId=${grouperRequestContainer.groupContainer.guiGroup.group.id}'); return false;"
                                >${textContainer.text['grouperLoaderEditButtonCancel'] }</a>
                            </td>
                            
                          </c:if>
                          <c:if test="${grouperRequestContainer.grouperLoaderContainer.editLoaderType != 'JEXL_SCRIPT'}">
                            <td style="white-space: nowrap; padding-top: 2em; padding-bottom: 2em;"><input type="submit" class="btn btn-primary" aria-controls="groupFilterResultsId" id="filterSubmitId" 
                              value="${textContainer.text['grouperLoaderEditButtonSave'] }" 
                              onclick="ajax('../app/UiV2GrouperLoader.editGrouperLoaderSave', {formIds: 'editLoaderFormId'}); return false;"> 
                              &nbsp; 
                              <a class="btn btn-cancel" role="button" 
                                onclick="return guiV2link('operation=UiV2GrouperLoader.loader?groupId=${grouperRequestContainer.groupContainer.guiGroup.group.id}'); return false;"
                                >${textContainer.text['grouperLoaderEditButtonCancel'] }</a>
                            </td>
                          </c:if>
                        </c:if>
                      </tr>
                    </tbody>
                  </table>
                </form>
              </div>
            </div>
