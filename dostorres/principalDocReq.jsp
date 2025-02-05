<!-- principalDocReq.jsp --> 
<%@page import="com.focus.util.PerfilAdministrador"%>
<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.Users,
                 com.focus.qweb.to.ProgramAuditTO,
                 com.focus.qweb.to.PlanAuditTO,
                 com.desige.webDocuments.persistent.managers.HandlerDocuments"%>
<%@ page import="org.apache.struts.taglib.html.Constants"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>

<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    StringBuffer onLoad = new StringBuffer("");
    String ok = (String)session.getAttribute("usuario");
    Users usuario = (Users)session.getAttribute("user");

    int docTotal = 0;
    int docComplete = 0;
    int docMissing = 0;

	boolean isProgram = request.getAttribute("programAuditTO")!=null;
	boolean isPlan = request.getAttribute("planAuditTO")!=null;
	ProgramAuditTO programAuditTO = null;
	PlanAuditTO planAuditTO = null;
	
	if(isProgram) {
		programAuditTO = (ProgramAuditTO) request.getAttribute("programAuditTO");
	}
	if(isPlan){
		planAuditTO = (PlanAuditTO) request.getAttribute("planAuditTO");
	} else {
		planAuditTO = new PlanAuditTO();
	}
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<style>
body {
	margin:10px;
	margin-right:30px;
	margin-left:30px;
	background-color:#5f5f5f;
}
.tabla {
}
.hoja {
	padding:20px;
	background-color:#ffffff;
	font-family:Arial;
	font-size:11px;
}
.tdLabel {
	background-color:#efefef;
	font-size:11px;
	padding-left:10px;
}
.tdText {
	color:#2f2f2f;
	font-weight:bold;
	font-size:11px;
}
.tdTitle {
	border:1px solid #afafaf;
	text-align:center;
	font-size:11px;
	font-weight:bold;
	text-transform:upperCase;
	background-color:#5f5f5f;
	color:#ffffff;
}
.titleReport {
	text-transform:upperCase;
	text-align:center;
	font-weight:bold;
	border-bottom:2px solid #afafaf;
	background-color:#5f5f5f;
	color:#ffffff;
	padding:3px;
}
.fondo_header {
	font-size:11px;
	font-family:Arial;
	font-weight:normal;
}
.fondo_text {
	font-size:11px;
	font-family:Arial;
	font-weight:normal;
	background-color:#efefef;
}
.fondo_text div {
	font-size:11px;
	font-family:Arial;
	font-weight:normal;
	background-color:#efefef;
}
</style>
<body >
<table class="hoja" cellSpacing="0" cellPadding="0" align=center border=0 width="100%">
	<tr>
	<td width="100%" valign="top">
		<%if(isProgram){%>
		<table class="tabla" border="0" width="100%" style="border:1px solid #cdcdcd;">
			<tr>
				<td class="tdTitle" colspan="6"><%=rb.getString("admin.required.programData")%></td>
			</tr>
			<tr>
				<td class="tdLabel" width="20%" ><%=rb.getString("admin.planAudit.descript")%></td>
				<td class="tdText" width="80%" colspan="5"><%=programAuditTO.getNameProgram()%></td>
			</tr>
			<tr>
				<td class="tdLabel" width="20%"><%=rb.getString("admin.planAudit.dateFrom")%></td>
				<td class="tdText" width="16%"><%=ToolsHTML.formatDateShow(programAuditTO.getDateFrom(),false)%></td>
				<td class="tdLabel" width="16%"><%=rb.getString("admin.planAudit.dateUntil")%></td>
				<td class="tdText" width="16%"><%=ToolsHTML.formatDateShow(programAuditTO.getDateUntil(),false)%></td>
				<td class="tdLabel" width="16%"><%=rb.getString("admin.planAudit.status")%></td>
				<td class="tdText" width="16%"><%=rb.getString("admin.planAudit.status".concat(programAuditTO.getStatus()))%></td>
			</tr>
		</table>
		<br>
		<%}%>
		<%if(isPlan){%>
		<table border="0" width="100%" style="border:1px solid #cdcdcd;">
			<tr>
				<td class="tdTitle" colspan="6"><%=rb.getString("admin.required.planData")%></td>
			</tr>
			<tr>
				<td class="tdLabel" width="20%" ><%=rb.getString("admin.planAudit.descript")%></td>
				<td class="tdText" width="80%" colspan="5"><%=planAuditTO.getNamePlan()%></td>
			</tr>
			<tr>
				<td class="tdLabel" width="20%"><%=rb.getString("admin.planAudit.dateFrom")%></td>
				<td class="tdText" width="16%"><%=ToolsHTML.formatDateShow(planAuditTO.getDateFromPlan(),false)%></td>
				<td class="tdLabel" width="16%"><%=rb.getString("admin.planAudit.dateUntil")%></td>
				<td class="tdText" width="16%"><%=ToolsHTML.formatDateShow(planAuditTO.getDateUntilPlan(),false)%></td>
				<td class="tdLabel" width="16%"><%=rb.getString("admin.planAudit.status")%></td>
				<td class="tdText" width="16%"><%=rb.getString("admin.planAudit.status".concat(planAuditTO.getStatusPlan()))%></td>
			</tr>
		</table>
		<br>
		<%}%>
		<div class="titleReport"><%=rb.getString("admin.planAudit.ManagementSystemAudit")%></div>
		<table width="100%" border="0" cellSpacing="0" cellPadding="0">
            <tr>
                <td>
                    <!-- Documentos Pendientes -->
                    <table width="100%" border="0" cellspacing="1" class="borderWindowPrincipal" >
                    
                        <logic:present name="docRequired" scope="request" >
                            <% 
                                int cont=0; 
                                Integer previousValue = 0;
                            %>
                            
                            <logic:iterate id="docCheck" name="docRequired" type="com.desige.webDocuments.document.forms.DocumentsCheckOutsBean" scope="request" >
                                <%
                                        if (PerfilAdministrador.userIsInAdminGroup(usuario)
                                                && !previousValue.equals(docCheck.getPersonBean().getIdPerson())) {
                                            previousValue = docCheck.getPersonBean().getIdPerson();
                                    %>
                                    <tr class='fondo_header'>
                                    <td colspan="4">
                                        <%= docCheck.getPersonBean().getApellido().concat(" ").concat(docCheck.getPersonBean().getEmail()).concat(" ").concat(docCheck.getPersonBean().getNombre()) %> <%docTotal++;%>
                                    </td>
                                    </tr>
                                    <%
                                        }
                                    %>
                                <tr class='fondo_text'>
                                    <%if(docCheck.getNameDocument().equals("")){
                                    	docMissing++;%>
                                    <td colspan="4">
                                    </td>
                                    <%} else {
                                    	docComplete++;%>
                                    <td width="3%">
                                        <img src="images/nuevo.gif" width="15px" height="15px" style="margin-left:30px;padding-right:10px;"/> 
                                    </td>
                                    <td width="5%">
                                        <%=docCheck.getMayorVer()+"."+docCheck.getMinorVer()%>
                                    </td>
                                    <td width="73%">
                                        <%=docCheck.getNameDocument()+" " + docCheck.getPrefix()+docCheck.getNumber()%>
                                    </td>
                                    <td width="23%" align="center">
                                        <%=docCheck.getDateCheckOut()%>
                                    </td>
                                	<%}%>
                                </tr>
                            </logic:iterate>
                        </logic:present>
                    </table>
                    <hr>
                    <div class="fondo_text"> <%=rb.getString("admin.required.assignedRequirements")%> = <%=docTotal-docMissing%> - <%=rb.getString("admin.required.missingRequirements")%> = <%=docMissing%></div>
                </td>
            </tr>
        </table>
	</td>
  </tr>
 </table>
</body>
</html>
