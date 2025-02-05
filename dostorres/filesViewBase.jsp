<!-- Title: filesViewBase.jsp -->
<%@ page import="com.desige.webDocuments.files.forms.ExpedienteForm,
				 com.desige.webDocuments.dao.ExpedienteDetalleDAO,
	             com.desige.webDocuments.utils.beans.Users,
	             java.util.Collection,
                 com.desige.webDocuments.utils.ToolsHTML,
                 java.util.ResourceBundle,
	             com.desige.webDocuments.persistent.managers.HandlerDBUser"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%
Users usuario = (Users)session.getAttribute("user");
Collection users = HandlerDBUser.getAllUsers();
pageContext.setAttribute("usuarios",users);
boolean isDocument = ExpedienteDetalleDAO.isConstainDocumentEnabled((ExpedienteForm)request.getAttribute("files"));
boolean isOwnerFile = (boolean) request.getAttribute("isOwnerFile");

ResourceBundle rb = ToolsHTML.getBundle(request);
String onLoad = ToolsHTML.getAlertInfo(request,rb);

%>
<html>
    <head>
        <meta content="MSHTML 6.00.2800.1106" name=generator/>
        <link href="estilo/estilo.css" rel="stylesheet" type="text/css"/>
		<script language="JavaScript" src="estilo/funciones.js"></script>
		<script language="JavaScript" src="estilo/Calendario_2.js"></script>
		<script language="JavaScript">
			function isValido(obj,msg){
				if((typeof obj.length)=='undefined'){
					error=false;
					marcado=(obj.type=='radio' || obj.type=='checkbox');
					error = (marcado && obj.checked?true:error);
					error = (!marcado && trim(obj.value)==''?true:error);
					
					if(error){
						alert(msg+" es requerido ");
						obj.value='';
						return false;
					}
				} else {
					for(var x=0; x<obj.length; x++) {
						if(obj[x].checked){
							return true;
						}
					}
					alert(msg+" es requerido ");
					return false;
				}
				return true;
			}
			
			function isEmpty(obj){
				vacio=false;
				if((typeof obj.length)=='undefined'){
					vacio=false;
					marcado=(obj.type=='radio' || obj.type=='checkbox');
					vacio = (marcado && !obj.checked?true:vacio);
					vacio = (!marcado && trim(obj.value)!=''?true:vacio);

				} else {
					for(var x=0; x<obj.length; x++) {
						if(obj[x].checked){
							vacio=false;
						}
					}
				}
				return vacio;
			}
			

			function isItemValid(obj,item,objRequerido,msg){
				for(var x=0; x<obj.length; x++) {
					if(obj[x].value==item && (obj.type=="select-one"?obj[x].selected:obj[x].checked) ){
						if(!isValido(objRequerido,msg)) return false;
					}
				}
				return true;
			}

			function save(){
				<!--validacion-->
				
				document.forms[0].submit();
			}
			
			function cancelar() {
				window.parent.searchFiles();
			}
			
// 			function showFilesimprimir2(idDoc, idVersion, imprimir, nameFile, idLogoImp) {
// 				var hWnd = null;
// 				var nameFile = escape(nameFile);
// 				var toPrint = escape(nameFile);

// 				var params = "f1=" + idDoc;
// 				params += "&numVersion=" + idVersion;
// 				params += "&imprimir=" + imprimir;

// 				var url = "visorFilesPdfAjax.do?" + params;

// 				document.location.href = url;
// 			}
	
			
		</script>
	</head>
<body class="bodyInternas" onLoad="<%=onLoad%>">
<table cellspacing='2' cellpadding='3' border='0' class='texto' width='100%'>
  <tr> 
  <td width="80%" valign="top">
<!--codigo-->
      </td >
      <td id="menuFiles" valign="top" width="20%">
		<table bgColor="#003366" width="100%" border="0">
		           <tr>
		               <td class="td_title_bwc">
		                       <bean:message key="files.files" />
		               </td>
		           </tr>
				   <logic:equal name="securityFiles" property="toFilesEdit"  value="1">
		           <tr >
		               <td align="center" class="td_btn" onmouseover="this.className='menuOver'" onmouseout="this.className='td_btn'" >
		                   <a href="filesRegister.do?f1=<bean:write name="files" property="f1"/>" class="ahref_g" style="width:100%">
		                       <bean:message key="files.security.toFilesEdit" />
		                   </a>
		               </td>
		           </tr>
		           </logic:equal>
				   <logic:equal name="securityFiles" property="toFilesDelete"  value="1">
		           <tr >
		               <td align="center" class="td_btn" onmouseover="this.className='menuOver'" onmouseout="this.className='td_btn'" >
		                   <a target="info" href="javascript:if(confirm('<bean:message key="areYouSure" />')){ document.location.href='filesDelete.do?f1=<bean:write name="files" property="f1"/>';}" class="ahref_g" style="width:100%">
		                       <bean:message key="files.security.toFilesDelete" />
		                   </a>
		               </td>
		           </tr>
		           </logic:equal>
				   <logic:equal name="securityFiles" property="toFilesRelated"  value="1">
		           <tr >
		               <td align="center" class="td_btn" onmouseover="this.className='menuOver'" onmouseout="this.className='td_btn'" >
		                   <a href="filesRelation.do?f1=<bean:write name="files" property="f1"/>" class="ahref_g" style="width:100%">
		                       <bean:message key="files.security.toFilesRelated" />
		                   </a>
		               </td>
		           </tr>
		           </logic:equal>
				   <logic:equal name="securityFiles" property="toFilesView"  value="1">
				   <%if(isOwnerFile){%>
		           <tr >
		               <td align="center" class="td_btn" onmouseover="this.className='menuOver'" onmouseout="this.className='td_btn'" >
		                   <a <%if(isDocument){%>
								href="javascript:showFilesimprimir(<bean:write name="files" property="f1"/>,0,false,<bean:write name="files" property="f1"/>,0);"
								<%}else{%>
								href="javascript:alert('<bean:message key="files.notDocumentFound"/>');"
								<%}%> class="ahref_g" style="width: 100%"> <bean:message
										key="files.security.toFilesView" />
							</a>
							</td>
		           </tr>
		           <%}%>
		           </logic:equal>
				   <logic:equal name="securityFiles" property="toFilesPrint"  value="1">
				   <%if(isOwnerFile){%>
		           <tr >
		               <td align="center" class="td_btn" onmouseover="this.className='menuOver'" onmouseout="this.className='td_btn'" >
		                   <a 
		                       <%if(isDocument){%>
			                   	   href="javascript:showFilesimprimir(<bean:write name="files" property="f1"/>,0,true,<bean:write name="files" property="f1"/>,0);" 
		                   	   <%}else{%>
			                   	   href="javascript:alert('<bean:message key="files.notDocumentFound"/>');"
		                   	   <%}%>
			                   class="ahref_g" style="width:100%">
		                       <bean:message key="files.security.toFilesPrint" />
		                   </a>
		               </td>
		           </tr>
		           <%}%>
		           </logic:equal>
				   <logic:equal name="securityFiles" property="toFilesSave"  value="1">
				   <%if(isOwnerFile){%>
		           <tr >
		               <td align="center" class="td_btn" onmouseover="this.className='menuOver'" onmouseout="this.className='td_btn'" >
		                   <a href="javascript:if(confirm('<bean:message key="areYouSure" />')){ document.location.href='filesSaveVersion.do?f1=<bean:write name="files" property="f1"/>';}" class="ahref_g" style="width:100%">
		                       <bean:message key="files.security.toFilesSave" />
		                   </a>
		               </td>
		           </tr>
		           <%}%>
		           </logic:equal>
				   <logic:equal name="securityFiles" property="toFilesVersion"  value="1">
		           <tr >
		               <td align="center" class="td_btn" onmouseover="this.className='menuOver'" onmouseout="this.className='td_btn'" >
		                   <a href="filesVersion.do?f1=<bean:write name="files" property="f1"/>" class="ahref_g" style="width:100%">
		                       <bean:message key="files.security.toFilesVersion" />
		                   </a>
		               </td>
		           </tr>
		           </logic:equal>
				   <logic:equal name="securityFiles" property="toFilesHistory"  value="1">
		           <tr >
		               <td align="center" class="td_btn" onmouseover="this.className='menuOver'" onmouseout="this.className='td_btn'" >
		                   <a href="loadHistoryFiles.do?f1=<bean:write name="files" property="f1"/>&idWorkFlow=0" class="ahref_g" style="width:100%">
		                       <bean:message key="files.security.toFilesHistory" />
		                   </a>
		               </td>
		           </tr>
		           </logic:equal>
				   <logic:equal name="securityFiles" property="toFilesDownload"  value="1">
				   <%if(isOwnerFile){%>
		           <tr >
		               <td align="center" class="td_btn" onmouseover="this.className='menuOver'" onmouseout="this.className='td_btn'" >
		                   <a href="crearExpedienteZip.do?f1=<bean:write name="files" property="f1"/>&idWorkFlow=0" class="ahref_g" style="width:100%">
		                       <bean:message key="files.security.toFilesDownload" />
		                   </a>
		               </td>
		           </tr>
		           <%}%>
		           </logic:equal>
		</table>
      </td>
    </tr>
  </table>
<center>
<input type="button" class="boton" onclick="cancelar()" value="<bean:message key="btn.back"/>" >
</center>
</body>
</html>