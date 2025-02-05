<!--
 * Title: showDataStruct.jsp
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li> 01/06/2005 (NC) Cambios para mostrar los comentario </li>
 *      <li> 23/06/2005 (SR) Cambios para mostrar los comentario, se agrego (HandlerDocuments.docCheckIn) </li>
 *      <li> 07/06/2005 (NC) Cambios para mostrar los comentarios </li>   
 </ul>
-->
<%@ page
	import="java.util.ResourceBundle,com.desige.webDocuments.utils.ToolsHTML,com.desige.webDocuments.utils.beans.SuperActionForm,com.desige.webDocuments.utils.Constants"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>

<%@ page language="java"%>
<%
	ResourceBundle rb = ToolsHTML.getBundle(request);
	String onLoad = ToolsHTML.getMensajePages(request, rb);
	if (onLoad == null) {
		response.sendRedirect(rb.getString("href.logout"));
	}
	String cmd = (String) session.getAttribute("cmd");
	String historicoImpreso = (String) session
			.getAttribute("historicoImpreso");
	if (cmd == null) {
		cmd = SuperActionForm.cmdLoad;
	}
	pageContext.setAttribute("cmd", cmd);
	String nodeActive = (String) session.getAttribute("nodeActive");
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" />
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<style>
.clsMessageViewI {
	background: white;
	color: black;
	border: 1px solid #afafaf;
	padding: 2;
	font-size: 12px;
	font-family: Geneva, Verdana, Arial, Helvetica;
	font-weight: normal;
	overflow: auto;
}
</style>
<script type="text/javascript">

    function cancelar() {
    	var form = document.getElementById("Selection");
        
        <%if (session.getAttribute("idDocument") != null
					&& !String.valueOf(session.getAttribute("idDocument"))
							.equals("")) {%>
        	form.idDocument.value = <%=session.getAttribute("idDocument")%>;
	        form.action="showDataDocument.do";
        <%} else {%>
	        form.action="loadStructMain.do";
        <%}%>
        form.target = "_parent";
<%--        form.cmd.value="<%=SuperActionForm.cmdLoad%>";--%>
        form.submit();
    }

    function edit() {
        document.showDocument.submit();
    }

    function updateCheck(check,field) {
        if (check.checked){
            field.value = "0";
        } else{
            field.value = 1;
        }
    }

    function createWF(item) {
        document.newWF.typeWF.value = item;
        document.newWF.target = "_parent";
        document.newWF.submit();
    }

    function showDocument(item){
        var hWnd = null;
        hWnd = window.open("showDocument.do?idDocument="+item,"Catalogo","width=800,height=600,resizable=yes,scrollbars=yes,left=100,top=150");
        if ((document.window != null) && (!hWnd.opener)) {
            hWnd.opener = document.window;
        }
    }

    function showDataDocument(id){
    	var formaSelection = document.getElementById("Selection");
        formaSelection.idDocument.value = id;
        formaSelection.submit();
    }
</script>
</head>

<body class="bodyInternas" <%=onLoad%>>

	<!-- Formulario para Obtener el nodo Seleccionado por el usuario -->
	<form name="Selection" id="Selection" action="showDataDocument.do">
		<input type="hidden" name="idDocument" value="" /> <input
			type="hidden" name="idNodeSelected" value="<%=nodeActive%>" />
	</form>
	<!-- END Form -->

	<logic:present name="dataHistoryStruct" scope="session">
		<bean:define id="struct" name="dataHistoryStruct"
			type="com.desige.webDocuments.structured.forms.DataHistoryStructForm"
			scope="session" />
		<table align=center border=0 width="100%">
			<logic:notPresent name="historyimpresion">
				<tr>
					<td colspan="2" class="pagesTitle"><%=rb.getString("btn.wfHistory")%>
					</td>
				</tr>

				<tr>
					<td valign="top" colspan="2">
						<table class="clsTableTitle" width="100%" cellSpacing=0
							cellPadding=2 align=center border=0>
							<tbody>
								<tr>
									<td class="td_title_bc" height="21">
										<%
											String tempString = String
													.valueOf(struct.getTypeMovement()).trim();
											if ("null".equalsIgnoreCase(tempString)) {
												out.print("No se ha hecho ninguna visualización de este expediente.");
											} else {
												StringBuilder sb = new StringBuilder(1024);
												String tag = "cbs.historyType" + tempString;
												sb.append(rb.getString(tag));
												sb.append(" ");
	
												tempString = struct.getDateChange();
												if (tempString == null) {
													tempString = "";
												} else {
													tempString = struct.getDateCreation();
												}
												sb.append(tempString);
												out.print(sb.toString());
											}
										%> <bean:write
											name="dataHistoryStruct" property="name" />
									</td>
								</tr>
							</tbody>
						</table>
					</td>
				</tr>
			</logic:notPresent>

			<tr>
				<td height="22" class="titleLeft"
					style="background: url(img/btn120.png); background-repeat: no-repeat"
					width="18%" valign="middle"><%=rb.getString("cbs.owner")%>:</td>
				<td class="td_gris_l" width="*"><bean:write
						name="dataHistoryStruct" property="nameOwner" /></td>
			</tr>

			<tr>
				<td class="titleLeft" height="22"
					style="background: url(img/btn120.png); background-repeat: no-repeat"
					valign="middle"><%=rb.getString("cbs.dateCreation")%>:</td>
				<td class="td_gris_l" width="*"><bean:write
						name="dataHistoryStruct" property="dateCreation" /></td>
			</tr>

			<tr>
				<td class="titleLeft" height="22"
					style="background: url(img/btn120.png); background-repeat: no-repeat"
					valign="middle"><%=rb.getString("cbs.icon")%>:</td>
				<td class="td_gris_l" width="*">
					<%
						String nameIcon = struct.getNameIcon();
							if (nameIcon != null) {
								StringBuilder sb = new StringBuilder(1024)
										.append("<img src='menu-images/").append(nameIcon)
										.append("' border='0' width='18' height='18'>");
								out.print(sb.toString());
							}
					%>
				</td>
			</tr>
			<%
				if (!ToolsHTML.isEmptyOrNull(struct.getMayorVer())
							|| !ToolsHTML.isEmptyOrNull(struct.getMinorVer())) {
			%>
			<tr>
				<td class="titleLeft" height="22"
					style="background: url(img/btn120.png); background-repeat: no-repeat"
					valign="middle"><%=rb.getString("sch.version")%>:</td>
				<td class="td_gris_l" width="*"><bean:write
						name="dataHistoryStruct" property="mayorVer" />.<bean:write
						name="dataHistoryStruct" property="minorVer" /></td>
			</tr>
			<%
				}
			%>

			<tr>
				<td colspan="2">&nbsp;</td>
			</tr>

			<tr>
				<td class="titleLeft" height="22"
					style="background: url(img/btn120.png); background-repeat: no-repeat"
					valign="middle"><%=rb.getString("cbs.historyUser")%>:</td>
				<td class="td_gris_l" width="*"><bean:write
						name="dataHistoryStruct" property="nameUser" /></td>
			</tr>

			<tr>
				<td class="titleLeft" height="22"
					style="background: url(img/btn120.png); background-repeat: no-repeat"
					valign="middle"><%=rb.getString("cbs.historyDate")%>:</td>
				<td class="td_gris_l" width="*"><bean:write
						name="dataHistoryStruct" property="dateChange" /></td>
			</tr>
			<%
				String typeMovement = struct.getTypeMovement();
			%>
			<%
				if ("2".equalsIgnoreCase(typeMovement)) {
			%>
			<tr>
				<td class="titleLeft" height="22"
					style="background: url(img/btn120.png); background-repeat: no-repeat"
					valign="middle"><%=rb.getString("cbs.historyAnt")%>:</td>
				<td class="td_gris_l" width="*"><bean:write
						name="dataHistoryStruct" property="nameNodeFatherAnt" /></td>
			</tr>

			<tr>
				<td class="titleLeft" height="22"
					style="background: url(img/btn120.png); background-repeat: no-repeat"
					valign="middle"><%=rb.getString("cbs.historyNew")%>:</td>
				<td class="td_gris_l" width="*"><bean:write
						name="dataHistoryStruct" property="nameNodeFatherNew" /></td>
			</tr>
			<%
				}
			%>
			<logic:notEqual value="" name="dataHistoryStruct" property="comments">
				<logic:notPresent name="historyimpresion">
					<tr>
						<td colspan="2" class="titleLeft" height="22"
							style="background: url(img/BgComments.png); background-repeat: no-repeat"
							valign="middle"><%=rb.getString("wf.razondelcambio")%></td>
					</tr>
					<tr>
						<td colspan="2" align="left" style="padding: 0px"
							class="td_gris_l">
							<div align="left" class="clsMessageViewI"
								style="width: 100%; height: 260px">
								<%=struct.getComments()%>
							</div>
						</td>
					</tr>
				</logic:notPresent>
			</logic:notEqual>
			<logic:present name="historyimpresion">
				<tr>
					<td colspan="2" class="titleLeft" height="22"
						style="background: url(img/BgComments.png); background-repeat: no-repeat"
						valign="middle"><%=rb.getString("imp.historicoimp")%></td>
				</tr>
				<tr>
					<td colspan="2" align="left" style="padding: 0px" class="td_gris_l">
						<div align="left" class="clsMessageViewI"
							style="width: 100%; height: 260px">
							<%=historicoImpreso%>
						</div>
					</td>
				</tr>
				<!--</tr>-->
			</logic:present>
			<tr>
				<td colspan="2">&nbsp;</td>
			</tr>
			<tr>
				<td colspan="2">
					<center>
						<%
							if (session.getAttribute("f1") != null) {
						%>
						<input type="button" class="boton"
							value="<%=rb.getString("btn.cancel")%>"
							onClick="javascript:window.parent.document.location='filesView.do?f1=<%=session.getAttribute("f1")%>';" />
						<%
							} else {
						%>
						<input type="button" class="boton"
							value="<%=rb.getString("btn.cancel")%>"
							onClick="javascript:cancelar();" />
						<%
							}
						%>
					</center>
				</td>
			</tr>
		</table>
	</logic:present>
	<logic:notPresent name="dataHistoryStruct" scope="session">
		<table cellSpacing=0 cellPadding=0 align=center border=0 width="100%">
			<tr>
				<td class="td_orange_l_b" width="85%"><%=rb.getString("wf.selecItem")%>
				</td>
				<td width="*">
					<center>
						<%
							if (session.getAttribute("f1") != null) {
						%>
						<input type="button" class="boton"
							value="<%=rb.getString("btn.cancel")%>"
							onClick="javascript:window.parent.document.location='filesView.do?f1=<%=session.getAttribute("f1")%>';" />
						<%
							} else {
						%>
						<input type="button" class="boton"
							value="<%=rb.getString("btn.cancel")%>"
							onClick="javascript:cancelar();" />
						<%
							}
						%>
					</center>
				</td>
			</tr>
		</table>
	</logic:notPresent>
</body>
</html>
