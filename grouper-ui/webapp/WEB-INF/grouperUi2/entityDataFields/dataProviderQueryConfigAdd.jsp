<%@ include file="../assetsJsp/commonTaglib.jsp"%>

   <div class="bread-header-container">
       <ul class="breadcrumb">
          <li><a href="#" onclick="return guiV2link('operation=UiV2Main.indexMain');">${textContainer.text['myServicesHomeBreadcrumb'] }</a><span
            class="divider"><i class='fa fa-angle-right'></i></span></li>
          <li><a href="#" onclick="return guiV2link('operation=UiV2Main.miscellaneous');">${textContainer.text['miscellaneousBreadcrumb'] }</a><span
            class="divider"><i class='fa fa-angle-right'></i></span></li>
            
            <li><a href="#" onclick="return guiV2link('operation=UiV2EntityDataFields.viewEntityDataFieldsSummary');">${textContainer.text['miscellaneousEntityDataFieldsBreadcrumb'] }</a><span
      class="divider"><i class='fa fa-angle-right'></i></span></li>
            
            <li><a href="#" onclick="return guiV2link('operation=UiV2EntityDataFields.viewEntityDataProviderQueries');">${textContainer.text['miscellaneousDataProviderQueriesBreadcrumb'] }</a><span class="divider"><i class='fa fa-angle-right'></i></span></li>
            
            <li class="active">${textContainer.text['miscellaneousDataProviderQueryAddBreadcrumb'] }</li> 
        </ul>
       
       <div class="page-header blue-gradient">
       
         <div class="row-fluid">
           <div class="lead span9 pull-left"><h4>${textContainer.text['miscellaneousDataProviderQueriesMainDescription'] }</h4></div>
           <div class="span3 pull-right">
             <%@ include file="dataProviderQueryConfigsMoreActionsButtonContents.jsp"%>
           </div>
         </div>
       </div>
     </div>
     
     
     <div class="row-fluid">
    <div class="span12">
     <div id="messages"></div>
         
         <form class="form-inline form-small form-filter" id="dataProviderQueryConfigDetails">
            <table class="table table-condensed table-striped">
              <tbody>
                <%@ include file="dataProviderQueryConfigAddHelper.jsp" %>
                <tr>
                  <td>
                    <input type="hidden" name="mode" value="add">
                  </td>
                  <td></td>
                  <td
                    style="white-space: nowrap; padding-top: 2em; padding-bottom: 2em;">
                    <input type="submit" class="btn btn-primary"
                    aria-controls="dataProviderQueryConfigDetails" id="submitId"
                    value="${textContainer.text['dataFieldConfigAddFormSubmitButton'] }"
                    onclick="ajax('../app/UiV2EntityDataFields.addDataProviderQueryConfigSubmit', {formIds: 'dataProviderQueryConfigDetails'}); return false;">
                    &nbsp;
                  <a class="btn btn-cancel" role="button"
                          onclick="return guiV2link('operation=UiV2EntityDataFields.viewEntityDataProviderQueries'); return false;"
                          >${textContainer.text['dataFieldConfigAddFormCancelButton'] }</a>
                  </td>
                </tr>

              </tbody>
            </table>
            
          </form>
      
    </div>
  </div>
