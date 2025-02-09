<!-- principal.jsp 13 junio 2017-->
<%@page import="com.focus.util.PerfilAdministrador"%>
<%@ page
	import="java.util.ResourceBundle,
				 java.util.Collection,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.Users,
                 com.desige.webDocuments.persistent.managers.HandlerDocuments"%>
<%@ page import="org.apache.struts.taglib.html.Constants"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>

<%@ page language="java"%>

<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    StringBuffer onLoad = new StringBuffer("");
    Users usuario = (Users)session.getAttribute("user");
    String ok = (String) session.getAttribute("usuario");
    
     int MAXIMO = 2;

    //Luis Cisneros 12/03/07 Para mostrar informaci'on de info en la pestana ppal
    String info = (String) ToolsHTML.getAttribute(request, "info", true);
    //Fin 12/03/07

    if ((ok != null) && (ok.compareTo("") != 0)) {
        onLoad.append(" onLoad=\"self.status='").append(rb.getString("pagin.welcome")).append(" ");
        onLoad.append(ok);
        onLoad.append("'");

        //Luis Cisneros 12/03/07 Para mostrar informaci'on de info en la pestana ppal
        if (ToolsHTML.checkValue(info)) {
            onLoad.append(";alert('").append(info).append("');");
        }
        //Fin 12/03/07
        
        onLoad.append("\"");
    } else{
        response.sendRedirect(rb.getString("href.logout"));
    }

    ToolsHTML.clearSession(session,"application.ppal");
    String tipoImpresion=HandlerDocuments.TypeDocumentsImpresion;
    
    int docCheckOutsSize = (session.getAttribute("docCheckOuts")!=null?((Collection)session.getAttribute("docCheckOuts")).size():0);
    int docExpiresSize = (session.getAttribute("docExpires")!=null?((Collection)session.getAttribute("docExpires")).size():0);
    int wfPendingsSize = (session.getAttribute("wfPendings")!=null?((Collection)session.getAttribute("wfPendings")).size():0);
    int wfExpiresSize = (session.getAttribute("wfExpires")!=null?((Collection) session.getAttribute("wfExpires")).size():0);
    int wfExpiresOwnerSize = (session.getAttribute("wfExpiresOwnerSize")!=null?((Integer)session.getAttribute("wfExpiresOwnerSize")):0);
    int wfCanceledSize = (session.getAttribute("wfCanceled")!=null?((Collection)session.getAttribute("wfCanceled")).size():0);
    int wfPrintApprovedSize = (session.getAttribute("wfPrintApproved")!=null?((Collection)session.getAttribute("wfPrintApproved")).size():0);
    int wfPrintCanceledSize = (session.getAttribute("wfPrintCanceled")!=null?((Collection)session.getAttribute("wfPrintCanceled")).size():0);
    int docVersionApprovedSize = (session.getAttribute("docVersionApproved")!=null?((Collection)session.getAttribute("docVersionApproved")).size():0);
%>
<! DOCTYPE html>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" />
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script type="text/javascript">
function ver(etiqueta) {
	/*
	window.open("principal"+etiqueta+"Tit.jsp","bandejaTit");
	window.open("principal"+etiqueta+".jsp","bandeja");
	*/
	alert("Proximas Versiones -->"+etiqueta);
}

<!-- variables globales -->
	function showDocument(idDoc){
		var forma = document.getElementById("selection");
		forma.idDocument.value = idDoc;
		forma.action = "showDataDocument.do";
		forma.submit();
	}
	function validar(){
        if (document.login.docSearch.value.length>0){
            alert('<%=rb.getString("err.notDocumentSearch")%>');
        }else{
		    document.login.submit();
        }
	}

    function showWF(idWorkFlow,row,owner,isFlexFlow,isPrintWF){
        var forma = document.getElementById("selection");
        forma.idWorkFlow.value = idWorkFlow;
        forma.row.value = row;
        forma.owner.value = owner;
        forma.isFlexFlow.value = isFlexFlow;
        forma.isPrintWF.value = (typeof isPrintWF == 'undefined'?"false":isPrintWF);
        forma.showStruct.value = 'false';
        forma.submit();
    }
    
    function reporteDocumentosFlujos(){
    	document.flowsReport.submit();
    }
</script>

<style>
.ppalText {
	padding: 0 20px 0 10px;
	font-size: 12px;
	margin: 0px;
	color: #000000;
	font-family: Sans-serif, Helvetica, Arial, Verdana;
	text-decoration: none;
	font-size: 11px;
}
</style>
</head>

<body class="bodyInternas" <%=onLoad.toString()%>>
	<form name="selection" id="selection" action="loadWF.do">
		<input type="hidden" name="idWorkFlow" value="" /> <input
			type="hidden" name="row" value="" /> <input type="hidden"
			name="idMovement" value="" /> <input type="hidden" name="idDocument"
			value="" /> <input type="hidden" name="owner" value="" /> <input
			type="hidden" name="showStruct" value="true" /> <input type="hidden"
			name="isFlexFlow" value="" /> <input type="hidden" name="isPrintWF"
			value="false" /> <input type="hidden" name="origen"
			value="principal" />
	</form>
	<table cellSpacing="1" cellPadding="0" align="center" border="0"
		width="100%" height="100%">
		<tr>
			<td width="<%=ToolsHTML.ANCHO_MENU%>px" valign="top" align="left">
				<table cellSpacing="0" cellPadding="2" border="0"
					width="<%=ToolsHTML.ANCHO_MENU%>px" height="100%"
					style="border-color: #ofofof; border: 1px solid #efefef">
					<tr>
						<td height="30px" class="ppalBoton"
							onmouseover="this.className='ppalBoton ppalBoton2'"
							onmouseout="this.className='ppalBoton'">
							<table height="30px" border="0" cellspacing="0" cellpadding="0"
								width="100%">
								<tr>
									<td class="ppalBoton"
										onmouseover="this.className='ppalBoton ppalBoton2'"
										onmouseout="this.className='ppalBoton'"><%=rb.getString("enl.home")%>
									</td>
									<td class="ppalBoton" align="right">
										<%
                                  if (PerfilAdministrador.userIsInAdminGroup(usuario)) {
                              %>
										<form name="flowsReport" method="post"
											action="CrearReportePrincipalAction.do"></form> <img
										src="icons/page_excel.png"
										title="<%= rb.getString("downloadReport.htmlTitle") %>"
										onClick="javascript:reporteDocumentosFlujos();"> <%
                                  } else {
                              %> &nbsp; <%
                                  }
                              %>

									</td>
								</tr>
							</table>
						</td>
					</tr>
			
					<tr>
						<td height="99%" bgcolor="white" valign="top"
							background="menu-images/backgd.png">

							<table class="ppalText" style="cursor: pointer;" cellspacing="5">
								<tr onclick="ver('DocPen')">
									<td><img src="icons/time.png" /></td>
									<td>Para Nueva Version <span
										style="display:<%=docCheckOutsSize==0?"none":""%>"
										class="cantidad">(<%=docCheckOutsSize%>)
									</span></td>
								</tr>
								<tr onclick="ver('DocExp')">
									<td><img src="icons/date_error_blue.png" /></td>
									<td>Proximas Version <span
										style="display:<%=docExpiresSize==0?"none":""%>"
										class="cantidad">(<%=docExpiresSize%>)<span></td>
								</tr>
								<%if(ToolsHTML.showFlujo()){%>
								<tr onclick="ver('FluPen')">
									<td><img src="icons/arrow_divide.png" /></td>
									<td><%=rb.getString("doc.workFlowReq")%> <span
										style="display:<%=wfPendingsSize==0?"none":""%>"
										class="cantidad">(<%=wfPendingsSize%>)<span></td>
								</tr>
								<tr onclick="ver('FluExp')">
									<td><img src="icons/date_back.png" /></td>
									<td><%=rb.getString("doc.workFlowExpired").replaceAll(" - ","<br>(").concat(")")%>
										<span style="display:<%=wfExpiresSize==0?"none":""%>"
										class="cantidad">(<%=wfExpiresSize%>)<span></td>
								</tr>
								<tr onclick="ver('FluExpOri')">
									<td><img src="icons/date_go.png" /></td>
									<td><%=rb.getString("doc.workFlowExpiredOwner").replaceAll(" - ","<br>(").concat(")")%>
										<span style="display:<%=wfExpiresOwnerSize==0?"none":""%>"
										class="cantidad">(<%=wfExpiresOwnerSize%>)<span></td>
								</tr>
								<tr onclick="ver('FluCan')">
									<td><img src="icons/arrow_undo.png" /></td>
									<td><%=rb.getString("doc.worFlowCanceled")%> <span
										style="display:<%=wfCanceledSize==0?"none":""%>"
										class="cantidad">(<%=wfCanceledSize%>)<span></td>
								</tr>
								<tr onclick="ver('Print')">
									<td><img src="icons/printer.png" /></td>
									<td><%=rb.getString("doc.printApproved")%> <span
										style="display:<%=wfPrintApprovedSize==0?"none":""%>"
										class="cantidad">(<%=wfPrintApprovedSize%>)
									</span></td>
								</tr>
								<tr onclick="ver('PrintCancel')">
									<td><img src="icons/printer.png" /></td>
									<td><%=rb.getString("doc.printCanceled")%> <span
										style="display:<%=wfPrintCanceledSize==0?"none":""%>"
										class="cantidad">(<%=wfPrintCanceledSize%>)
									</span></td>
								</tr>
								<tr onclick="ver('ListDist')">
									<td><img src="icons/page_white_star.png" /></td>
									<td><%=rb.getString("doc.listDist")%> <span
										style="display:<%=docVersionApprovedSize==0?"none":""%>"
										class="cantidad">(<%=docVersionApprovedSize%>)
									</span></td>
								</tr>
								<%}%>
							</table>
						</td>
					</tr>
					<%if (usuario!=null) {
                            out.println(ToolsHTML.printMenu(rb,usuario.getUser(),usuario.getIdGroup(),true,request));
                        }%>
				</table>
			</td>
		
			<td align="center">
				<table border="0" cellspacing="0" cellpadding="0" width="100%"
					height="100%">
					<tr>
						<td height="40px"><iframe name="bandejaTit" width="100%"
								height="40px" src="principalEntradaTit.jsp" border="0px"
								marginwidth="0px" marginheight="0px" frameborder="0"
								scrolling="no"></iframe></td>
					</tr>
					<tr>
						<td><iframe name="bandeja" width="100%" height="100%"
								src="principalEntrada.jsp" border="0px" marginwidth="0px"
								marginheight="0px" frameborder="0"></iframe></td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</body>
</html>
