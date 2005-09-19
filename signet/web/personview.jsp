<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<!--
  $Id: personview.jsp,v 1.41 2005-09-19 06:37:04 acohen Exp $
  $Date: 2005-09-19 06:37:04 $
  
  Copyright 2004 Internet2 and Stanford University.  All Rights Reserved.
  Licensed under the Signet License, Version 1,
  see doc/license.txt in this distribution.
-->

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
  <meta name="robots" content="noindex, nofollow" />
  <title>
    Signet
  </title>
  <link href="styles/signet.css" rel="stylesheet" type="text/css" />
  <script language="JavaScript" type="text/javascript" src="scripts/signet.js"></script>
</head>

<body onload="javascript:initControls();">
  <script type="text/javascript">
    function initControls()
    {
      setStartButtonStatus();
      initRevokeAllCheckbox();
    }
    
    function setStartButtonStatus()
    {
      // The first entry in the list of grantable Subsystems is always
      // a place-holder that reads "select privilege type", to prompt the
      // user to go ahead and pick a Subsystem.
      //
      // As long as that first entry is selected, the "start" button, which
      // initiates the privilege-granting process, should be dimmed.
      
      var theGrantableSubsystemList = document.grantForm.grantableSubsystems;
      var theGrantButton = document.grantForm.grantButton;
      
      if (theGrantableSubsystemList.selectedIndex < 1)
      {
        theGrantButton.disabled = true;
      }
      else
      {
        theGrantButton.disabled = false;
      }
    }
    
    function initRevokeAllCheckbox()
    {
      // If there are no revocable Assignments on this page, then it makes
      // no sense to have the "revoke all" checkbox enabled.
      var theCheckAllBox = document.checkform.checkAll;
      
      if (selectableCount() > 0)
      {
        theCheckAllBox.disabled = false;
      }
      else
      {
        theCheckAllBox.disabled = true;
      }
    }
    
    function selectThis(isChecked)
    {
      var theCheckAllBox = document.checkform.checkAll;
      if (!isChecked)
      {
        theCheckAllBox.checked = false;
      }
      
      if (selectCount() > 0)
      {
        document.checkform.revokeButton.disabled = false;
      }
      else
      {
        document.checkform.revokeButton.disabled = true;
      }
    }
    
    function selectAll(isChecked)
    {
      var theForm = document.checkform;

      for (var i = 0; i < theForm.elements.length; i++)
      {
        if (theForm.elements[i].name != 'checkAll'
            && theForm.elements[i].type == 'checkbox'
            && theForm.elements[i].disabled == false)
        {
          theForm.elements[i].checked = isChecked;
        }
      }
      
      if (selectCount() > 0)
      {
        document.checkform.revokeButton.disabled = false;
      }
      else
      {
        document.checkform.revokeButton.disabled = true;
      }
    }
    
    function selectCount()
    {
      var theForm = document.checkform;
      var count = 0;
      
      for (var i = 0; i < theForm.elements.length; i++)
      {
        if ((theForm.elements[i].name != 'checkAll')
            && (theForm.elements[i].type == 'checkbox')
            && (theForm.elements[i].checked == true))
        {
          count++;
        }
      }
      
      return count;
    }
    
    function selectableCount()
    {
      var theForm = document.checkform;
      var count = 0;
      
      for (var i = 0; i < theForm.elements.length; i++)
      {
        if ((theForm.elements[i].name != 'checkAll')
            && (theForm.elements[i].type == 'checkbox')
            && (theForm.elements[i].disabled == false))
        {
          count++;
        }
      }
      
      return count;
    }
  </script>
  
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>

<%@ page import="java.text.DateFormat" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.SortedSet" %>
<%@ page import="java.util.TreeSet" %>
<%@ page import="java.util.HashSet" %>

<%@ page import="edu.internet2.middleware.signet.Signet" %>
<%@ page import="edu.internet2.middleware.signet.PrivilegedSubject" %>
<%@ page import="edu.internet2.middleware.signet.Subsystem" %>
<%@ page import="edu.internet2.middleware.signet.Category" %>
<%@ page import="edu.internet2.middleware.signet.Assignment" %>
<%@ page import="edu.internet2.middleware.signet.Function" %>
<%@ page import="edu.internet2.middleware.signet.Status" %>

<%@ page import="edu.internet2.middleware.signet.ui.Common" %>
<%@ page import="edu.internet2.middleware.signet.ui.UnusableStyle" %>
<%@ page import="edu.internet2.middleware.signet.ui.Constants" %>

<% 
  Signet signet
     = (Signet)
         (request.getSession().getAttribute("signet"));
         
  PrivilegedSubject loggedInPrivilegedSubject
    = (PrivilegedSubject)
        (request.getSession().getAttribute(Constants.LOGGEDINUSER_ATTRNAME));
   
  PrivilegedSubject currentGranteePrivilegedSubject
    = (PrivilegedSubject)
        (request.getSession().getAttribute(Constants.GRANTEE_ATTRNAME));
         
  Subsystem currentSubsystem
    = (Subsystem)
        (request.getSession().getAttribute(Constants.SUBSYSTEM_ATTRNAME));
         
  Set grantableSubsystems = loggedInPrivilegedSubject.getGrantableSubsystems();
         
  DateFormat dateFormat = DateFormat.getDateInstance();
%>
  <tiles:insert page="/tiles/header.jsp" flush="true" /> 
  <div id="Navbar">
    <span class="logout">
      <%=Common.displayLogoutHref(request)%>
    </span> <!-- logout -->
    <span class="select">
      <a href="Start.do">
        Home
      </a>
      &gt; <%=currentGranteePrivilegedSubject.getName()%>
    </span> <!-- select -->
  </div> <!-- Navbar -->
  
  <div id="Layout"> 
    <div id="Content">
      <div id="ViewHead">
	  	<span class="dropback">
        Privileges overview for </span> 
         <h1>
           <%=currentGranteePrivilegedSubject.getName()%>
         </h1>
         <span class="ident">
           <%=currentGranteePrivilegedSubject.getDescription()%><!--,	Technology Strategy and Support Operations-->
         </span> 
       </div> <!-- ViewHead -->
       <div class="tableheader">
	   
	    <a href="PrivilegesXML.do">
                <img
                	src="images/xml.gif"
                    alt="" />
                View XML
               </a>
			   <a
              href="javascript:;"
              onclick="alert('This will download the data shown in the table in an Excel-readable format.')">
             <img
                src="images/export.gif"
                alt="" />
             Export to Excel
           </a>
           <a
              href="PersonViewPrint.do">
             <img
                src="images/print.gif"
                alt="" />
             Printable version
           </a>
        <h2><%=(currentSubsystem == null ? "NO ASSIGNED" : currentSubsystem.getName())%> Privileges</h2> 
		</div> <!-- tableheader -->
		<div class="tablecontrols">
		<form name="pickSubsystem" action="PersonView.do">
		   <label for="subsystemId">Change the view to show </label>
           
           <select
              name="subsystemId"
              id="subsystem">
  
            <option
                onclick="javascript:document.pickSubsystem.showSubsystemPrivs.disabled=true">
               (assigned privilege types)
             </option>
                
<%
  Set assignmentsReceived
    = new TreeSet
        (currentGranteePrivilegedSubject
          .getAssignmentsReceived(Status.ACTIVE, null, null));
  Set subsystemsOfReceivedAssignments = new HashSet();
  Iterator assignmentsReceivedIterator
    = assignmentsReceived.iterator();
  while (assignmentsReceivedIterator.hasNext())
  {
    Assignment receivedAssignment
      = (Assignment)(assignmentsReceivedIterator.next());
    subsystemsOfReceivedAssignments.add
      (receivedAssignment.getFunction().getSubsystem());
  }

  Iterator subsystemsIterator
    = subsystemsOfReceivedAssignments.iterator();
  while (subsystemsIterator.hasNext())
  {
    Subsystem subsystem = (Subsystem)(subsystemsIterator.next());
%>
             <option
                value="<%=subsystem.getId()%>" selected="selected"
                onclick="javascript:document.pickSubsystem.showSubsystemPrivs.disabled=false">
               <%=subsystem.getName()%>
             </option>
                
<%
  }
%>
  
           </select>
           privileges           
          <input
              class="button1"
              disabled="true"
              type="submit"
              name="showSubsystemPrivs"
              value="Show"/>
         </form> <!-- pickSubsystem -->
		

		</div> <!-- tablecontrols -->              
      <form
          onsubmit
            ="return confirm
               ('Are you sure you want to revoke the'
                + (selectCount() == 1 ? '' : (' ' + selectCount()))
                + ' selected assignment'
                + (selectCount() > 1 ? 's' : '')
                + '?'
                + ' This action cannot be undone.'
                + ' Click OK to confirm.');"
          action="Revoke.do"
          method="post"
          name="checkform"
          id="checkform">
         <div class="tablecontent">
           <table>
             <tr class="columnhead">
               <th width="30%">
                 Privilege
               </th>
               <th width="20%">
                 Scope
               </th>
               <th>
                 Limits
               </th>
               <th>Status</th>
               <th width="10%">
                 All:
                 <input
                    name="checkAll"
                    type="checkbox"
                    id="checkAll"
                    onclick="selectAll(this.checked);"
                    value="Check All" />
               </th>
             </tr>
                  
<%
  if (currentSubsystem != null)
  {
    Set assignmentsReceivedForCurrentSubsystem
      = new TreeSet
          (currentGranteePrivilegedSubject
            .getAssignmentsReceived(Status.ACTIVE, currentSubsystem, null));
    Iterator assignmentsIterator
      = assignmentsReceivedForCurrentSubsystem.iterator();
    while (assignmentsIterator.hasNext())
    {
      Assignment assignment = (Assignment)(assignmentsIterator.next());
%>
  
             <tr>
               <td class="sorted"> <!-- privilege -->
                 <%=Common.assignmentPopupIcon(assignment)%>
                 <%=assignment.getFunction().getCategory().getName()%>
                 : 
                 <%=assignment.getFunction().getName()%>
               </td> <!-- privilege -->
               
               <td> <!-- scope -->
                 <%=assignment.getScope().getName()%>
               </td> <!-- scope -->
               
               <td> <!-- limits -->
                 <%=Common.editLink(loggedInPrivilegedSubject, assignment)%>
                 <%=Common.displayLimitValues(assignment)%>
               </td> <!-- limits -->
               
                 <td>&nbsp;</td>
                 <%=Common.revokeBox(loggedInPrivilegedSubject, assignment, UnusableStyle.DIM)%>
               </tr>
                  
<%
    }
  }
%>
  
              <tr >
                 <td>&nbsp;
                      
                </td>
                 <td>&nbsp;
                      
                </td>
                 <td >&nbsp;
                      
                </td>
                 <td>&nbsp;</td>
                 <td align="center" >
                   <input
                      name="revokeButton"
                      type="submit"
                      disabled="true"
                      class="button1"
                      value="Revoke" />
                 </td>
               </tr>
             </table>
         </div> <!-- tablecontent -->
       </form> <!-- checkform -->
     </div>
    <!-- Content -->

     <tiles:insert page="/tiles/footer.jsp" flush="true" />
      <div id="Sidebar">
        
<% 
  if (!(currentGranteePrivilegedSubject.equals(loggedInPrivilegedSubject)))
  {
%>

        <div class="grant">
          <h2>
            Grant a privilege
          </h2>
		   <p>to <%=currentGranteePrivilegedSubject.getName()%></p>
          <form name="grantForm" id="grantForm" action="Functions.do">
            <p>
              <select
                id="grantableSubsystems"
                name="grantableSubsystems"
                class="long" 
                onchange="setStartButtonStatus()">
                
                <option value="prompt">
                  select privilege type
                </option>

<%
    Iterator grantableSubsystemsIterator = grantableSubsystems.iterator();
    while (grantableSubsystemsIterator.hasNext())
    {
      Subsystem subsystem = (Subsystem)(grantableSubsystemsIterator.next());
%>
                <option value="<%=subsystem.getId()%>">
                  <%=subsystem.getName()%>
                </option>
<%
    }
%>
              </select>
       
              <input
                  type="submit"
                  name="grantButton"
                  id="grantButton"
                  class="button1"
                  <%=grantableSubsystems.size()==0 ? "disabled=\"disabled\"" : ""%>
                  value="Start &gt;&gt;" />
                <br />
            <label for="select">Select the type of privilege you want to grant, then click "Start."</label></p>
          </form> <!-- grantForm -->
        </div> <!-- grant -->
          
<%
  }
%>

    <form
      name="personSearchForm"
      method="post"
      action="" 
      onsubmit ="return checkForCursorInPersonSearch('personQuickSearch.jsp', 'words', 'PersonSearchResults')">
        <div class="findperson">
          <h2>
            find a Subject </h2>
          <p>
<input
                name="words"
                type="text"
                class="long"
                id="words"
                size="15"
                maxlength="500"
                onfocus="personSearchFieldHasFocus=true;"
                onblur="personSearchFieldHasFocus=false;" />              
<input
                name="searchbutton"
                type="submit"
                class="button1"
                value="Search"
                onclick="personSearchButtonHasFocus=true;"
                onfocus="personSearchButtonHasFocus=true;"
                onblur="personSearchButtonHasFocus=false;" />
            <br />
            <label for="words">
              Enter a subject's name, and click "Search."
            </label>
          </p>
          <div id="PersonSearchResults" style="display:none">
          </div> 
          <!-- PersonSearchResults -->
		    <p>
              <a href="PersonView.do?granteeSubjectTypeId=<%=loggedInPrivilegedSubject.getSubjectTypeId()%>&granteeSubjectId=<%=loggedInPrivilegedSubject.getSubjectId()%><%=currentSubsystem == null ? "" : ("&subsystemId=" + currentSubsystem.getId())%>">
                <img
                     src="images/arrow_right.gif"
                       alt="" />
            View <%= loggedInPrivilegedSubject.getName()%>'s privileges</a>
			</p>
        </div> <!-- findperson -->   
    </form> <!-- personSearchForm -->  
            
	<!-- stubbing this div out for now
	      <div class="views">
            <h2>
              View privileges...
            </h2>
            <p>
              <a href="Start.do">
                <img
                     src="images/arrow_right.gif"
                       alt="" />
                you have granted
              </a>
            </p>
            <p>
              <a href="PersonView.do?granteeSubjectTypeId=<%=loggedInPrivilegedSubject.getSubjectTypeId()%>&granteeSubjectId=<%=loggedInPrivilegedSubject.getSubjectId()%><%=currentSubsystem == null ? "" : ("&subsystemId=" + currentSubsystem.getId())%>">
                <img
                     src="images/arrow_right.gif"
                       alt="" />
                assigned to you
              </a>
            </p>
        </div>  
		
		end of stubbed-out views div -->
            
          

      </div> <!-- Sidebar -->
	  	  <div class="clear">&nbsp;</div>

</div> <!-- Layout -->
</body>
</html>
