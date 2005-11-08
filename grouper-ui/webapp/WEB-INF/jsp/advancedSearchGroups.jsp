<%-- @annotation@
		Tile which displays the advanced search form for groups
--%><%--
  @author Gary Brown.
  @version $Id: advancedSearchGroups.jsp,v 1.2 2005-11-08 15:39:36 isgwb Exp $
--%>
<%@include file="/WEB-INF/jsp/include.jsp"%>
<grouper:recordTile key="Not dynamic" tile="${requestScope['javax.servlet.include.servlet_path']}">
<a href="<c:out value="${pageUrl}"/>#endSearch" class="noCSSOnly"><fmt:message bundle="${nav}" key="page.skip.search"/></a>
<div class="advancedSearchGroups">
<h2 class="actionheader">
	<fmt:message bundle="${nav}" key="find.heading.groups-advanced-search"/>
</h2><p><a href="<c:out value="${pageUrlMinusQueryString}"/>?advancedSearch=false"><fmt:message bundle="${nav}" key="find.action.cancel-advanced-search"/></a></p>
<c:choose>
	<c:when test="${!empty findForNode}">
		<c:set var="submitAction" value="/searchNewMembers"/>
	</c:when>
	<c:otherwise>
		<c:set var="submitAction" value="/searchGroups${browseMode}"/>
	</c:otherwise>
</c:choose>
	<html:form styleId="SearchFormBean" action="${submitAction}">
	<input type="hidden" name="searchFor" value="groups"/>
	<input type="hidden" name="newSearch" value="Y"/>
	<fieldset>
		<div class="formRow">
			<div class="formLeft">
				<label for="searchTerm"><fmt:message bundle="${nav}" key="find.search-term"/></label>
			</div>
			<div class="formRight">
				<html:text property="searchTerm" size="25" styleId="searchTerm"/>
			</div>
			</div>
			<fieldset class="nested">
		<div class="formRow">
			<div class="formLeft">
				<label for="formSearchInDisplayExtension"><fmt:message bundle="${nav}" key="find.search-in-display-extension"/></label>
			</div>
			<div class="formRight">
				<html:radio property="searchInDisplayNameOrExtension" value="extension" styleId="formSearchInDisplayExtension"/>
			</div>
		</div>
		<div class="formRow">
			<div class="formLeft">
				<label for="formSearchInDisplayName"><fmt:message bundle="${nav}" key="find.search-in-display-name"/></label>
			</div>
			<div class="formRight">
				<html:radio property="searchInDisplayNameOrExtension" value="name" styleId="formSearchInDisplayName"/>
			</div>
		</div>
				<div class="formRow">
			<div class="formLeft">
				<label for="formSearchInDisplayNone"><fmt:message bundle="${nav}" key="find.search-in-display-none"/></label>
			</div>
			<div class="formRight">
				<html:radio property="searchInDisplayNameOrExtension" value="" styleId="formSearchInDisplayNone"/>
			</div>
		</div>
		</fieldset>
		<fieldset class="nested">
		<div class="formRow">
			<div class="formLeft">
				<label for="formSearchInExtension"><fmt:message bundle="${nav}" key="find.search-in-extension"/></label>
			</div>
			<div class="formRight">
				<html:radio property="searchInNameOrExtension" value="extension" styleId="formSearchInExtension"/>
			</div>
		</div>
		<div class="formRow">
			<div class="formLeft">
				<label for="formSearchInName"><fmt:message bundle="${nav}" key="find.search-in-name"/></label>
			</div>
			<div class="formRight">
				<html:radio property="searchInNameOrExtension" value="name" styleId="formSearchInName"/>
			</div>
		</div>
		<div class="formRow">
			<div class="formLeft">
				<label for="formSearchInNone"><fmt:message bundle="${nav}" key="find.search-in-none"/></label>
			</div>
			<div class="formRight">
				<html:radio property="searchInNameOrExtension" value="" styleId="formSearchInNone"/>
			</div>
		</div>
		</fieldset>
		<div class="formRow"><tiles:insert definition="searchFromDef"/></div><br/>
		<html:submit property="submit.search" value="${navMap['groups.action.search']}"/>
	</fieldset>
	</html:form>
</div>
<a name="endSearch" id="endSearch"></a>
</grouper:recordTile>