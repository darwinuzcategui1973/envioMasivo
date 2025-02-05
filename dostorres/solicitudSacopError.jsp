<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
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
 * Title: solicitudSacopError.jsp<br>
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
	
%>
<!DOCTYPE>
<html>
	<head>
		<title><%=rb.getString("principal.title")%></title>
		<jsp:include page="meta.jsp" /> 
		<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
		<script type="text/javascript" src="./estilo/funciones.js"></script>
		<script type="text/javascript">
		
		</script>
	</head>
	
	<body class="bodyInternas" >
		<center>
		<center class="titleCenter" style="color:#ff0000">
			<h1><%=rb.getString("requestSacop.msgError1")%></h1>
			<h3><%=rb.getString("requestSacop.msgError2")%></h3>
			<input type="button" class="boton" value="<%=rb.getString("scp.cerrar")%>" onClick="javascript:window.close()" />
		</center>
		</center>
	</body>
</html>
