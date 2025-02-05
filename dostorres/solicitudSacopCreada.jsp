<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
				 com.desige.webDocuments.utils.Constants,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 com.desige.webDocuments.utils.beans.Users,
                 com.desige.webDocuments.utils.beans.PaginPage,
                 com.desige.webDocuments.document.forms.BaseDocumentForm,
                 com.desige.webDocuments.persistent.managers.HandlerStruct,
                 com.focus.qweb.to.TitulosPlanillasSacopTO,
                 java.util.ArrayList,
                 java.util.TreeMap,
                 java.util.Iterator,
                 com.desige.webDocuments.enlaces.forms.UrlsActionForm"%>

<!--/**
 * Title: solicitudSacopCreada.jsp<br>
 * Copyright: (c) 2016 Focus Consulting<br/>
 *
 * @author Jairo Rivero (JR)
 * @version WebDocuments v5.0
 * <br>
 *     Changes:<br>
 * <ul>
 *      <li> 01-11-2016 (JR)  Creacion
 * <ul>
 */-->
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<%

    ResourceBundle rb = ToolsHTML.getBundle(request);
	Users user = (Users)session.getAttribute("user");
	
	BaseDocumentForm doc = (BaseDocumentForm)request.getAttribute("doc");
	ArrayList<TitulosPlanillasSacopTO> listaOrigen = (ArrayList<TitulosPlanillasSacopTO>) request.getAttribute("listaOrigen");
	TitulosPlanillasSacopTO titulo = null;
	TreeMap<String, Users> listaResponsable = (TreeMap<String, Users>) request.getAttribute("listaResponsable");  
%>
<!DOCTYPE>
<html>
	<head>
		<title><%=rb.getString("principal.title")%></title>
		<jsp:include page="meta.jsp" /> 
		<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
		<script type="text/javascript" src="./estilo/funciones.js"></script>
		<script language=javascript src="./estilo/fechas.js"></script>
		<script type="text/javascript" src="./estilo/popcalendar2inner.js"></script>
		<script type="text/javascript">
			function incluir(form) {
				//PENDIENTE: hacer las validaciones del formulario
				
				form.submit();
			}		
		</script>
	</head>
	
	<body class="bodyInternas" >
		<table width="100%" cellspacing="5" >
			<tr>
				<td colspan="2">
                    <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="2" align=center border=0>
                        <tbody>
                            <tr>
                                <td class="td_title_bc">
                                    <%=rb.getString("requestSacop.title")%>
                                </td>
                            </tr>
                        </tbody>
                    </table>
				</td>
			</tr>
			<tr>
				<td class="titleLeft" height="22" style="background: url(img/btn160.png); background-repeat: no-repeat;width:245px;" valign="middle">
					<%=rb.getString("scp.numSacop")%>
				</td>
				<td class="td_gris_l">
					<%=request.getAttribute("numSacop")%>
				</td>
			</tr>
			<tr>
				<td class="titleLeft" height="22" style="background: url(img/btn160.png); background-repeat: no-repeat" valign="middle">
					<%=rb.getString("scp.requestDate")%>
				</td>
				<td class="td_gris_l">
					<%=request.getAttribute("fechaSolicitud")%>
				</td>
			</tr>
			<tr>
				<td class="titleLeft" height="22" style="background: url(img/btn160.png); background-repeat: no-repeat" valign="middle">
					<%=rb.getString("scp.dateIssue")%>
				</td>
				<td class="td_gris_l">
					<%=request.getAttribute("fechaEmision")%>
				</td>
			</tr>
			<tr>
				<td class="titleLeft" height="22" style="background: url(img/btn160.png); background-repeat: no-repeat" valign="middle">
					<%=rb.getString("scp.dateOccurrence")%>
				</td>
				<td class="td_gris_l">
					<%=request.getAttribute("fechaOcurrencia")%>
				</td>
			</tr>
			<tr>
				<td class="titleLeft" height="22" style="background: url(img/btn160.png);" valign="top">
					<%=rb.getString("scp.descriptionSacop")%>
				</td>
				<td class="td_gris_l">
					<%=request.getAttribute("descripcion")%>
				</td>
			</tr>
			
			
		</table>
		<br>
		<center>
			<input type="button" class="boton" value="<%=rb.getString("scp.cerrar")%>" onClick="javascript:window.close()" />
		</center>
		
		<br><br>
		<table width="100%" cellspacing="5" >
			<tr>
				<td colspan="2">
                    <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="2" align=center border=0>
                        <tbody>
                            <tr>
                                <td class="td_title_bc">
                                    <%=rb.getString("requestSacop.historyAll")%>
                                </td>
                            </tr>
                        </tbody>
                    </table>
				</td>
			</tr>
			<tr>
				<td class="td_gris_l" colspan="2">
					<iframe src="loadSacopHistoryAll.do?idDocumentRelatedFilter=<%=request.getParameter("idDocument")%>" width="100%" height="800px" style="margin:0px;padding:0px;" frameborder="0"></iframe>
				</td>
			</tr>
		</table>
		
	</body>
</html>
