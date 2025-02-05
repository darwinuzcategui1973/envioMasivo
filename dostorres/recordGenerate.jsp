<!--**
 * Title: recordGenerate.jsp <br/>
 * Copyright: (c) 2007 Focus Consulting<br/>
 * @version WebDocuments v4.3
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li> 19-12-2007 (YSA) Creation </li>
 </ul>

 */-->


<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.SuperActionForm"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>

<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    String onLoad = ToolsHTML.getMensajePages(request,rb,"initPage();");
    if (onLoad==null) {
        response.sendRedirect(rb.getString("href.logout"));
    }
    Integer numUsers = 0;
    numUsers = (Integer) session.getAttribute("numUsers");
    //if(numUsers<=0) numUsers = 0;
    
    Integer cols = 4;
    cols = (Integer) session.getAttribute("cols");
    //if(cols<=0) cols = 4;

    String sAreas = (String) session.getAttribute("sAreas");
    if(ToolsHTML.isEmptyOrNull(sAreas)) sAreas = "";
    
    String sCharges = (String) session.getAttribute("sCharges");
    if(ToolsHTML.isEmptyOrNull(sCharges)) sCharges = "";
    
    String nameFile = (String) session.getAttribute("nameFile");
    if(ToolsHTML.isEmptyOrNull(nameFile)) nameFile = "";
    
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script language=javascript src="./estilo/funciones.js"></script>
<script language=javascript src="./estilo/fechas.js"></script><%-- Luis Cisneros 23-02-07 --%>
<script language="JavaScript">

    function initPage() {
        forma = document.forms[0];
        if ((forma)&&(forma.nameDocument)) {
            forma.nameDocument.focus();
        }
    }
    
    function showReport(){
    	window.open("<%=nameFile%>");
    }

    function printReport(){
    	this.print();
    }
    
    function sendReport(){
    	document.location.href="loadMailRecord.do";
    }

</script>
</head>
<body class="bodyInternas" <%=onLoad%>>

<table align=center border=0 width="100%">
	<tr>
		<td valign="top" class="pagesTitle14">
			<%=rb.getString("rcd.titleresult")%>
		</td>
	</tr>

	<tr>
		<td width="100%">
			<table width="100%" cellSpacing=2 cellPadding=2 align=left border=0>
				<tr>
					<td width="60" class="txt_b" valign="top" align=left>
						<b><%=rb.getString("rcd.areas")%>:</b> 
					<td width="*" class="txt_b" valign="top" align=left>
						<%=sAreas%>
			 		</td>
             	</tr>
				<tr>
					<td width="60" class="txt_b" valign="top" align=left>
						<b><%=rb.getString("rcd.charges")%>:</b> 
					<td width="*" class="txt_b" valign="top" align=left>
						<%=sCharges%>
			 		</td>
    		</table>
 		 </td>
	</tr>   
	<%if(!ToolsHTML.isEmptyOrNull(cols+"")){%>
	<tr>
		<td width="100%">
			<table width="700" cellSpacing=0 cellPadding=2 align="left" border=0>
				<tr>
					<td width="*" valign="top" align="right">
					<logic:present name="securityRecord">
						<logic:equal name="securityRecord" property="toSend"  value="1">
							<input type="button" name="btnSend" onclick="javascript:sendReport();" value='<%=rb.getString("rcd.sendReport")%>' class="boton">              
							<img src="img/vacio.gif" border="0" width="30" height="1">
						</logic:equal>
						
						<logic:equal name="securityRecord" property="toExport"  value="1">
							<input type="button" name="btnExport" onclick="javascript:showReport();" value='<%=rb.getString("rcd.exportReport")%>' class="boton">              
						</logic:equal>
						
						<logic:equal name="securityRecord" property="toPrint"  value="1">
							<!-- <img src="img/vacio.gif" border="0" width="30" height="1">
							<input type="button" name="btnPrint" onclick="javascript:printReport();" value='<%=rb.getString("rcd.printReport")%>' class="boton">              
							 -->
						</logic:equal>
					</logic:present>
			 		</td>
             	</tr>
				<tr>
					<td width="*" height="3"></td>
             	</tr>
    		</table>
 		 </td>
	</tr>  

	<tr>
    	<td valign="top">
        	<table cellSpacing=1 cellPadding=1 align=center border=0 width="100%">
         		<tr>
        			<td class="td_title_bc" colspan="<%=cols%>" background="img/barraTitleBck.gif">
						<%=rb.getString("rcd.result1")%> <%=numUsers%> <%=rb.getString("rcd.result2")%>
					</td>
        		</tr>
         		<tr>
        			<td class="td_titulo_C2" width="100" valign="top"><%=rb.getString("rcd.usr")%></td>
        			<td class="td_titulo_C2" width="150" valign="top"><%=rb.getString("rcd.name")%></td>
        			<td class="td_titulo_C2" width="100" valign="top"><%=rb.getString("rcd.area")%></td>
        			<td class="td_titulo_C2" width="150" valign="top"><%=rb.getString("rcd.charge")%></td>
        			<logic:equal value="1" name="docsCreatedSel" scope="session"><td class="td_titulo_C2" width="100" valign="top"><%=rb.getString("rcd.titDoc6")%></td></logic:equal>
        			<logic:equal value="1" name="docsExpiredSel" scope="session"><td class="td_titulo_C2" width="100" valign="top"><%=rb.getString("rcd.titDoc3")%></td></logic:equal>
        			<logic:equal value="1" name="docsObsoleteSel" scope="session"><td class="td_titulo_C2" width="100" valign="top"><%=rb.getString("rcd.titDoc4")%></td></logic:equal>
        			<logic:equal value="1" name="draftsExpiredSel" scope="session"><td class="td_titulo_C2" width="100" valign="top"><%=rb.getString("rcd.titDoc5")%></td></logic:equal>
        			<logic:equal value="1" name="workflowsApprovalSel" scope="session"><td class="td_titulo_C2" width="150" valign="top"><%=rb.getString("rcd.titFlow1")%></td></logic:equal>
        			<logic:equal value="1" name="workflowsApprovalPendingSel" scope="session"><td class="td_titulo_C2" width="150" valign="top"><%=rb.getString("rcd.titFlow2")%></td></logic:equal>
        			<logic:equal value="1" name="workflowsApprovalApprovedSel" scope="session"><td class="td_titulo_C2" width="150" valign="top"><%=rb.getString("rcd.titFlow3")%></td></logic:equal>
        			<logic:equal value="1" name="workflowsApprovalCanceledSel" scope="session"><td class="td_titulo_C2" width="150" valign="top"><%=rb.getString("rcd.titFlow4")%></td></logic:equal>
        			<logic:equal value="1" name="workflowsApprovalExpiredSel" scope="session"><td class="td_titulo_C2" width="150" valign="top"><%=rb.getString("rcd.titFlow5")%></td></logic:equal>
        			<logic:equal value="1" name="workflowsReviewSel" scope="session"><td class="td_titulo_C2" width="150" valign="top"><%=rb.getString("rcd.titFlow6")%></td></logic:equal>
        			<logic:equal value="1" name="workflowsReviewPendingSel" scope="session"><td class="td_titulo_C2" width="150" valign="top"><%=rb.getString("rcd.titFlow7")%></td></logic:equal>
        			<logic:equal value="1" name="workflowsReviewApprovedSel" scope="session"><td class="td_titulo_C2" width="150" valign="top"><%=rb.getString("rcd.titFlow8")%></td></logic:equal>
        			<logic:equal value="1" name="workflowsReviewCanceledSel" scope="session"><td class="td_titulo_C2" width="150" valign="top"><%=rb.getString("rcd.titFlow9")%></td></logic:equal>
        			<logic:equal value="1" name="workflowsReviewExpiredSel" scope="session"><td class="td_titulo_C2" width="150" valign="top"><%=rb.getString("rcd.titFlow10")%></td></logic:equal>
        			<%if (ToolsHTML.showFTP()) {%>
        			<logic:equal value="1" name="ftpSel" scope="session"><td class="td_titulo_C2" width="100" valign="top"><%=rb.getString("rcd.titFTP1")%></td></logic:equal>
        			<logic:equal value="1" name="ftpPendingSel" scope="session"><td class="td_titulo_C2" width="100" valign="top"><%=rb.getString("rcd.titFTP2")%></td></logic:equal>
        			<logic:equal value="1" name="ftpApprovedSel" scope="session"><td class="td_titulo_C2" width="100" valign="top"><%=rb.getString("rcd.titFTP3")%></td></logic:equal>
        			<logic:equal value="1" name="ftpCanceledSel" scope="session"><td class="td_titulo_C2" width="100" valign="top"><%=rb.getString("rcd.titFTP4")%></td></logic:equal>
        			<logic:equal value="1" name="ftpExpiredSel" scope="session"><td class="td_titulo_C2" width="100" valign="top"><%=rb.getString("rcd.titFTP5")%></td></logic:equal>
        			<logic:equal value="1" name="ftpCompletedSel" scope="session"><td class="td_titulo_C2" width="100" valign="top"><%=rb.getString("rcd.titFTP6")%></td></logic:equal>
        			<%} %>
        			<%if (ToolsHTML.showSACOP()){ %>
        			<logic:equal value="1" name="sacopsCreatedSel" scope="session"><td class="td_titulo_C2" width="100" valign="top"><%=rb.getString("rcd.titSacop1")%></td></logic:equal>
        			<logic:equal value="1" name="sacopsIsResponsibleSel" scope="session"><td class="td_titulo_C2" width="100" valign="top"><%=rb.getString("rcd.titSacop2")%></td></logic:equal>
        			<logic:equal value="1" name="sacopsParticipateSel" scope="session"><td class="td_titulo_C2" width="100" valign="top"><%=rb.getString("rcd.titSacop3")%></td></logic:equal>
        			<logic:equal value="1" name="sacopsClosedSel" scope="session"><td class="td_titulo_C2" width="100" valign="top"><%=rb.getString("rcd.titSacop4")%></td></logic:equal>
        			<%} %>
        			<logic:equal value="1" name="printingSel" scope="session"><td class="td_titulo_C2" width="100" valign="top"><%=rb.getString("rcd.titPrint1")%></td></logic:equal>
        			<logic:equal value="1" name="printingApprovedSel" scope="session"><td class="td_titulo_C2" width="100" valign="top"><%=rb.getString("rcd.titPrint5")%></td></logic:equal>
        			<logic:equal value="1" name="printingRejectedSel" scope="session"><td class="td_titulo_C2" width="100" valign="top"><%=rb.getString("rcd.titPrint6")%></td></logic:equal>
        			<logic:equal value="1" name="printingPendingSel" scope="session"><td class="td_titulo_C2" width="100" valign="top"><%=rb.getString("rcd.titPrint7")%></td></logic:equal>
             	</tr>
             	
				<logic:present name="recordPorUsuario" scope="session">
				<logic:equal value="1" name="recordPorUsuario" scope="session">
				
				<logic:present name="usersRecord" scope="session">
				<logic:iterate id="usr" name="usersRecord" scope="session" type="com.desige.webDocuments.utils.beans.Users" indexId="ind">
	            	<%
                    	int item = ind.intValue()+1;
                        int num = item%2;
                    %>
             	<tr>
					<td class='td_<%=num%>_nb' valign="top"><%=usr.getNameUser()%></td>
					<td class='td_<%=num%>_nb' valign="top"><%=usr.getNamePerson()%></td>
					<td class='td_<%=num%>_nb' valign="top"><%if (usr.getArea()!=null){%><%=usr.getArea().getArea()%><%}%></td>
					<td class='td_<%=num%>_nb' valign="top"><%if (usr.getCargo()!=null){%><%=usr.getCargo().getCargo()%><%}%></td>
					<%if (usr.getUserRecord()!=null){%>
					<logic:equal value="1" name="docsCreatedSel" scope="session"><td class='td_<%=num%>_nb_c' valign="top" align="center"><%=usr.getUserRecord().getDocsCreated()%></td></logic:equal>
					<logic:equal value="1" name="docsExpiredSel" scope="session"><td class='td_<%=num%>_nb_c' valign="top" align="center"><%=usr.getUserRecord().getDocsExpired()%></td></logic:equal>
					<logic:equal value="1" name="docsObsoleteSel" scope="session"><td class='td_<%=num%>_nb_c' valign="top" align="center"><%=usr.getUserRecord().getDocsObsolete()%></td></logic:equal>
					<logic:equal value="1" name="draftsExpiredSel" scope="session"><td class='td_<%=num%>_nb_c' valign="top" align="center"><%=usr.getUserRecord().getDraftsExpired()%></td></logic:equal>
					<logic:equal value="1" name="workflowsApprovalSel" scope="session"><td class='td_<%=num%>_nb_c' valign="top" align="center"><%=usr.getUserRecord().getWorkflowsApproval()%></td></logic:equal>
					<logic:equal value="1" name="workflowsApprovalPendingSel" scope="session"><td class='td_<%=num%>_nb_c' valign="top" align="center"><%=usr.getUserRecord().getWorkflowsApprovalPending()%></td></logic:equal>
					<logic:equal value="1" name="workflowsApprovalApprovedSel" scope="session"><td class='td_<%=num%>_nb_c' valign="top" align="center"><%=usr.getUserRecord().getWorkflowsApprovalApproved()%></td></logic:equal>
					<logic:equal value="1" name="workflowsApprovalCanceledSel" scope="session"><td class='td_<%=num%>_nb_c' valign="top" align="center"><%=usr.getUserRecord().getWorkflowsApprovalCanceled()%></td></logic:equal>
					<logic:equal value="1" name="workflowsApprovalExpiredSel" scope="session"><td class='td_<%=num%>_nb_c' valign="top" align="center"><%=usr.getUserRecord().getWorkflowsApprovalExpired()%></td></logic:equal>
					<logic:equal value="1" name="workflowsReviewSel" scope="session"><td class='td_<%=num%>_nb_c' valign="top" align="center"><%=usr.getUserRecord().getWorkflowsReview()%></td></logic:equal>
					<logic:equal value="1" name="workflowsReviewPendingSel" scope="session"><td class='td_<%=num%>_nb_c' valign="top" align="center"><%=usr.getUserRecord().getWorkflowsReviewPending()%></td></logic:equal>
					<logic:equal value="1" name="workflowsReviewApprovedSel" scope="session"><td class='td_<%=num%>_nb_c' valign="top" align="center"><%=usr.getUserRecord().getWorkflowsReviewApproved()%></td></logic:equal>
					<logic:equal value="1" name="workflowsReviewCanceledSel" scope="session"><td class='td_<%=num%>_nb_c' valign="top" align="center"><%=usr.getUserRecord().getWorkflowsReviewCanceled()%></td></logic:equal>
					<logic:equal value="1" name="workflowsReviewExpiredSel" scope="session"><td class='td_<%=num%>_nb_c' valign="top" align="center"><%=usr.getUserRecord().getWorkflowsReviewExpired()%></td></logic:equal>
					<%if (ToolsHTML.showFTP()) {%>
					<logic:equal value="1" name="ftpSel" scope="session"><td class='td_<%=num%>_nb_c' valign="top" align="center"><%=usr.getUserRecord().getFtp()%></td></logic:equal>
					<logic:equal value="1" name="ftpPendingSel" scope="session"><td class='td_<%=num%>_nb_c' valign="top" align="center"><%=usr.getUserRecord().getFtpPending()%></td></logic:equal>
					<logic:equal value="1" name="ftpApprovedSel" scope="session"><td class='td_<%=num%>_nb_c' valign="top" align="center"><%=usr.getUserRecord().getFtpApproved()%></td></logic:equal>
					<logic:equal value="1" name="ftpCanceledSel" scope="session"><td class='td_<%=num%>_nb_c' valign="top" align="center"><%=usr.getUserRecord().getFtpCanceled()%></td></logic:equal>
					<logic:equal value="1" name="ftpExpiredSel" scope="session"><td class='td_<%=num%>_nb_c' valign="top" align="center"><%=usr.getUserRecord().getFtpExpired()%></td></logic:equal>
					<logic:equal value="1" name="ftpCompletedSel" scope="session"><td class='td_<%=num%>_nb_c' valign="top" align="center"><%=usr.getUserRecord().getFtpCompleted()%></td></logic:equal>
					<%}%>
					<%if (ToolsHTML.showSACOP()) {%>
					<logic:equal value="1" name="sacopsCreatedSel" scope="session"><td class='td_<%=num%>_nb_c' valign="top" align="center"><%=usr.getUserRecord().getSacopsCreated()%></td></logic:equal>
					<logic:equal value="1" name="sacopsIsResponsibleSel" scope="session"><td class='td_<%=num%>_nb_c' valign="top" align="center"><%=usr.getUserRecord().getSacopsIsResponsible()%></td></logic:equal>
					<logic:equal value="1" name="sacopsParticipateSel" scope="session"><td class='td_<%=num%>_nb_c' valign="top" align="center"><%=usr.getUserRecord().getSacopsParticipate()%></td></logic:equal>
					<logic:equal value="1" name="sacopsClosedSel" scope="session"><td class='td_<%=num%>_nb_c' valign="top" align="center"><%=usr.getUserRecord().getSacopsClosed()%></td></logic:equal>
					<%}%>
					<logic:equal value="1" name="printingSel" scope="session"><td class='td_<%=num%>_nb_c' valign="top" align="center"><%=usr.getUserRecord().getPrinting()%></td></logic:equal>
					<logic:equal value="1" name="printingApprovedSel" scope="session"><td class='td_<%=num%>_nb_c' valign="top" align="center"><%=usr.getUserRecord().getPrintingApproved()%></td></logic:equal>
					<logic:equal value="1" name="printingRejectedSel" scope="session"><td class='td_<%=num%>_nb_c' valign="top" align="center"><%=usr.getUserRecord().getPrintingRejected()%></td></logic:equal>
					<logic:equal value="1" name="printingPendingSel" scope="session"><td class='td_<%=num%>_nb_c' valign="top" align="center"><%=usr.getUserRecord().getPrintingPending()%></td></logic:equal>
					<%}else{%>
					<td class='td_<%=num%>_nb_c' valign="top"></td>
					<%}%>
             	</tr>
				</logic:iterate>
    			</logic:present>

				</logic:equal>
				</logic:present>

				<logic:present name="recordPromedio" scope="session">
				<logic:equal value="1" name="recordPromedio" scope="session">
				<tr>
					<td class='td_0_r' valign="top" colspan="4"><%=rb.getString("rcd.option1")%></td>
					<logic:equal value="1" name="docsCreatedSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("docsCreatedRP")!=null?session.getAttribute("docsCreatedRP"):"0"%></td></logic:equal>
					<logic:equal value="1" name="docsExpiredSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("docsExpiredRP")!=null?session.getAttribute("docsExpiredRP"):"0"%></td></logic:equal>
					<logic:equal value="1" name="docsObsoleteSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("docsObsoleteRP")!=null?session.getAttribute("docsObsoleteRP"):"0"%></td></logic:equal>
					<logic:equal value="1" name="draftsExpiredSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("draftsExpiredRP")!=null?session.getAttribute("draftsExpiredRP"):"0"%></td></logic:equal>
					<logic:equal value="1" name="workflowsApprovalSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("workflowsApprovalRP")!=null?session.getAttribute("workflowsApprovalRP"):"0"%></td></logic:equal>
					<logic:equal value="1" name="workflowsApprovalPendingSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("workflowsApprovalPendingRP")!=null?session.getAttribute("workflowsApprovalPendingRP"):"0"%></td></logic:equal>
					<logic:equal value="1" name="workflowsApprovalApprovedSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("workflowsApprovalApprovedRP")!=null?session.getAttribute("workflowsApprovalApprovedRP"):"0"%></td></logic:equal>
					<logic:equal value="1" name="workflowsApprovalCanceledSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("workflowsApprovalCanceledRP")!=null?session.getAttribute("workflowsApprovalCanceledRP"):"0"%></td></logic:equal>
					<logic:equal value="1" name="workflowsApprovalExpiredSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("workflowsApprovalExpiredRP")!=null?session.getAttribute("workflowsApprovalExpiredRP"):"0"%></td></logic:equal>
					<logic:equal value="1" name="workflowsReviewSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("workflowsReviewRP")!=null?session.getAttribute("workflowsReviewRP"):"0"%></td></logic:equal>
					<logic:equal value="1" name="workflowsReviewPendingSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("workflowsReviewPendingRP")!=null?session.getAttribute("workflowsReviewPendingRP"):"0"%></td></logic:equal>
					<logic:equal value="1" name="workflowsReviewApprovedSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("workflowsReviewApprovedRP")!=null?session.getAttribute("workflowsReviewApprovedRP"):"0"%></td></logic:equal>
					<logic:equal value="1" name="workflowsReviewCanceledSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("workflowsReviewCanceledRP")!=null?session.getAttribute("workflowsReviewCanceledRP"):"0"%></td></logic:equal>
					<logic:equal value="1" name="workflowsReviewExpiredSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("workflowsReviewExpiredRP")!=null?session.getAttribute("workflowsReviewExpiredRP"):"0"%></td></logic:equal>
					<%if (ToolsHTML.showFTP()) {%>
					<logic:equal value="1" name="ftpSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("ftpRP")!=null?session.getAttribute("ftpRP"):"0"%></td></logic:equal>
					<logic:equal value="1" name="ftpPendingSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("ftpPendingRP")!=null?session.getAttribute("ftpPendingRP"):"0"%></td></logic:equal>
					<logic:equal value="1" name="ftpApprovedSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("ftpApprovedRP")!=null?session.getAttribute("ftpApprovedRP"):"0"%></td></logic:equal>
					<logic:equal value="1" name="ftpCanceledSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("ftpCanceledRP")!=null?session.getAttribute("ftpCanceledRP"):"0"%></td></logic:equal>
					<logic:equal value="1" name="ftpExpiredSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("ftpExpiredRP")!=null?session.getAttribute("ftpExpiredRP"):"0"%></td></logic:equal>
					<logic:equal value="1" name="ftpCompletedSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("ftpCompletedRP")!=null?session.getAttribute("ftpCompletedRP"):"0"%></td></logic:equal>
					<%}%>
					<%if (ToolsHTML.showSACOP()) {%>
					<logic:equal value="1" name="sacopsCreatedSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("sacopsCreatedRP")!=null?session.getAttribute("sacopsCreatedRP"):"0"%></td></logic:equal>
					<logic:equal value="1" name="sacopsIsResponsibleSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("sacopsIsResponsibleRP")!=null?session.getAttribute("sacopsIsResponsibleRP"):"0"%></td></logic:equal>
					<logic:equal value="1" name="sacopsParticipateSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("sacopsParticipateRP")!=null?session.getAttribute("sacopsParticipateRP"):"0"%></td></logic:equal>
					<logic:equal value="1" name="sacopsClosedSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("sacopsClosedRP")!=null?session.getAttribute("sacopsClosedRP"):"0"%></td></logic:equal>
					<%}%>
					<logic:equal value="1" name="printingSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("printingRP")!=null?session.getAttribute("printingRP"):"0"%></td></logic:equal>
					<logic:equal value="1" name="printingApprovedSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("printingApprovedRP")!=null?session.getAttribute("printingApprovedRP"):"0"%></td></logic:equal>
					<logic:equal value="1" name="printingRejectedSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("printingRejectedRP")!=null?session.getAttribute("printingRejectedRP"):"0"%></td></logic:equal>
					<logic:equal value="1" name="printingPendingSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("printingPendingRP")!=null?session.getAttribute("printingPendingRP"):"0"%></td></logic:equal>
				</tr>
				</logic:equal>
				</logic:present>

				<logic:present name="medianaGeneral" scope="session">
				<logic:equal value="1" name="medianaGeneral" scope="session">
				<tr>
					<td class='td_0_r' valign="top" colspan="4"><%=rb.getString("rcd.option3")%></td>
					<logic:equal value="1" name="docsCreatedSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("docsCreatedMG")!=null?session.getAttribute("docsCreatedMG"):"0"%></td></logic:equal>
					<logic:equal value="1" name="docsExpiredSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("docsExpiredMG")!=null?session.getAttribute("docsExpiredMG"):"0"%></td></logic:equal>
					<logic:equal value="1" name="docsObsoleteSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("docsObsoleteMG")!=null?session.getAttribute("docsObsoleteMG"):"0"%></td></logic:equal>
					<logic:equal value="1" name="draftsExpiredSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("draftsExpiredMG")!=null?session.getAttribute("draftsExpiredMG"):"0"%></td></logic:equal>
					<logic:equal value="1" name="workflowsApprovalSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("workflowsApprovalMG")!=null?session.getAttribute("workflowsApprovalMG"):"0"%></td></logic:equal>
					<logic:equal value="1" name="workflowsApprovalPendingSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("workflowsApprovalPendingMG")!=null?session.getAttribute("workflowsApprovalPendingMG"):"0"%></td></logic:equal>
					<logic:equal value="1" name="workflowsApprovalApprovedSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("workflowsApprovalApprovedMG")!=null?session.getAttribute("workflowsApprovalApprovedMG"):"0"%></td></logic:equal>
					<logic:equal value="1" name="workflowsApprovalCanceledSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("workflowsApprovalCanceledMG")!=null?session.getAttribute("workflowsApprovalCanceledMG"):"0"%></td></logic:equal>
					<logic:equal value="1" name="workflowsApprovalExpiredSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("workflowsApprovalExpiredMG")!=null?session.getAttribute("workflowsApprovalExpiredMG"):"0"%></td></logic:equal>
					<logic:equal value="1" name="workflowsReviewSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("workflowsReviewMG")!=null?session.getAttribute("workflowsReviewMG"):"0"%></td></logic:equal>
					<logic:equal value="1" name="workflowsReviewPendingSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("workflowsReviewPendingMG")!=null?session.getAttribute("workflowsReviewPendingMG"):"0"%></td></logic:equal>
					<logic:equal value="1" name="workflowsReviewApprovedSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("workflowsReviewApprovedMG")!=null?session.getAttribute("workflowsReviewApprovedMG"):"0"%></td></logic:equal>
					<logic:equal value="1" name="workflowsReviewCanceledSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("workflowsReviewCanceledMG")!=null?session.getAttribute("workflowsReviewCanceledMG"):"0"%></td></logic:equal>
					<logic:equal value="1" name="workflowsReviewExpiredSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("workflowsReviewExpiredMG")!=null?session.getAttribute("workflowsReviewExpiredMG"):"0"%></td></logic:equal>
					<%if (ToolsHTML.showFTP()) {%>
					<logic:equal value="1" name="ftpSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("ftpMG")!=null?session.getAttribute("ftpMG"):"0"%></td></logic:equal>
					<logic:equal value="1" name="ftpPendingSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("ftpPendingMG")!=null?session.getAttribute("ftpPendingMG"):"0"%></td></logic:equal>
					<logic:equal value="1" name="ftpApprovedSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("ftpApprovedMG")!=null?session.getAttribute("ftpApprovedMG"):"0"%></td></logic:equal>
					<logic:equal value="1" name="ftpCanceledSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("ftpCanceledMG")!=null?session.getAttribute("ftpCanceledMG"):"0"%></td></logic:equal>
					<logic:equal value="1" name="ftpExpiredSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("ftpExpiredMG")!=null?session.getAttribute("ftpExpiredMG"):"0"%></td></logic:equal>
					<logic:equal value="1" name="ftpCompletedSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("ftpCompletedMG")!=null?session.getAttribute("ftpCompletedMG"):"0"%></td></logic:equal>
					<%}%>
					<%if (ToolsHTML.showSACOP()) {%>
					<logic:equal value="1" name="sacopsCreatedSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("sacopsCreatedMG")!=null?session.getAttribute("sacopsCreatedMG"):"0"%></td></logic:equal>
					<logic:equal value="1" name="sacopsIsResponsibleSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("sacopsIsResponsibleMG")!=null?session.getAttribute("sacopsIsResponsibleMG"):"0"%></td></logic:equal>
					<logic:equal value="1" name="sacopsParticipateSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("sacopsParticipateMG")!=null?session.getAttribute("sacopsParticipateMG"):"0"%></td></logic:equal>
					<logic:equal value="1" name="sacopsClosedSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("sacopsClosedMG")!=null?session.getAttribute("sacopsClosedMG"):"0"%></td></logic:equal>
					<%}%>
					<logic:equal value="1" name="printingSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("printingMG")!=null?session.getAttribute("printingMG"):"0"%></td></logic:equal>
					<logic:equal value="1" name="printingApprovedSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("printingApprovedMG")!=null?session.getAttribute("printingApprovedMG"):"0"%></td></logic:equal>
					<logic:equal value="1" name="printingRejectedSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("printingRejectedMG")!=null?session.getAttribute("printingRejectedMG"):"0"%></td></logic:equal>
					<logic:equal value="1" name="printingPendingSel" scope="session"><td class='td_0_c' valign="top"><%=session.getAttribute("printingPendingMG")!=null?session.getAttribute("printingPendingMG"):"0"%></td></logic:equal>
				</tr>
				</logic:equal>
				</logic:present>

    		</table>

 		 </td>
	</tr>   
	<%}%>
</table>

</body>
</html>
<script language="javascript">
	window.parent.document.getElementById("frameBase").cols="100%,0%";
</script>