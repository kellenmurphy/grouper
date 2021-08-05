<%@ include file="../assetsJsp/commonTaglib.jsp"%>

                    <!-- start deprovisioning/deprovisioningAttributeDefMoreActionsButtonContents.jsp -->

                    <div class="btn-group btn-block">
                    
                      <a data-toggle="dropdown" href="#" aria-label="${textContainer.text['ariaLabelGuiMoreDeprovisioningActions']}" id="more-action-button" class="btn btn-medium btn-block dropdown-toggle" 
                        aria-haspopup="true" aria-expanded="false" role="menu" onclick="$('#deprovisioning-more-options').is(':visible') === true ? $(this).attr('aria-expanded','false') : $(this).attr('aria-expanded',function(index, currentValue) { $('#deprovisioning-more-options li').first().focus();return true;});">
                          ${textContainer.text['deprovisioningMoreActionsButton'] } <span class="caret"></span></a>

                      <ul class="dropdown-menu dropdown-menu-right" id="deprovisioning-more-options">

                        <c:if test="${grouperRequestContainer.deprovisioningContainer.canReadDeprovisioning}" >
                          <li><a href="#" onclick="return guiV2link('operation=UiV2Deprovisioning.deprovisioningOnAttributeDef&attributeDefId=${grouperRequestContainer.attributeDefContainer.guiAttributeDef.attributeDef.id}'); return false;"
                              >${textContainer.text['deprovisioningMoreActionsAttributeDefSettings'] }</a></li>
                        </c:if>

                        <c:if test="${grouperRequestContainer.deprovisioningContainer.canWriteDeprovisioning}" >
	                        <li><a href="#" onclick="return guiV2link('operation=UiV2Deprovisioning.deprovisioningOnAttributeDefEdit&attributeDefId=${grouperRequestContainer.attributeDefContainer.guiAttributeDef.attributeDef.id}'); return false;"
	                            >${textContainer.text['deprovisioningMoreActionsAttributeDefEditSettings'] }</a></li>
                        </c:if>

                        <li><a href="#" onclick="return guiV2link('operation=UiV2Deprovisioning.deprovisioningMain'); return false;"
                            >${textContainer.text['deprovisioningMoreActionsOverallDeprovision'] }</a></li>

                        <c:if test="${grouperRequestContainer.deprovisioningContainer.canWriteDeprovisioning}" >
                          <li><a href="#" onclick="return guiV2link('operation=UiV2Deprovisioning.deprovisioningOnAttributeDefReport&attributeDefId=${grouperRequestContainer.attributeDefContainer.guiAttributeDef.attributeDef.id}'); return false;"
                              >${textContainer.text['deprovisioningMoreActionsDeprovisioningReport'] }</a></li>
                        </c:if>

                        <c:if test="${grouperRequestContainer.deprovisioningContainer.canWriteDeprovisioning}" >
                          <li><a href="#" onclick="ajax('../app/UiV2Deprovisioning.updateAttributeDefLastCertifiedDateClear'); return false;"
                              >${textContainer.text['deprovisioningMoreActionsClearCertify'] }</a></li>
                        </c:if>

                      </ul>
                    </div>

                    <!-- end deprovisioning/deprovisioningAttributeDefMoreActionsButtonContents.jsp -->
