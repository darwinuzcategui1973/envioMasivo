<%@ page
	import="java.util.ResourceBundle,com.desige.webDocuments.utils.ToolsHTML,com.desige.webDocuments.utils.DesigeConf"%>
<%
	String parameters = ToolsHTML.parseParameters(request);

	/*String topFile = "topDocument.jsp" + parameters;*/
	String file = "showFileSacop.jsp" + parameters;
	String pie = "notePages.jsp" + parameters;
	//System.out.println("pie = " + pie);
	String nameFileToPrint = request.getParameter("nameFileToPrint") != null ? request
			.getParameter("nameFileToPrint") : "protegido.doc";
	//System.out.println("[nameFileToPrint] = " + nameFileToPrint);
%>
<!--
 * Title: viewDocumentSacop.jsp
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @author Ing. Nelson Crespo (SR)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li> 01/06/2005 (NC) Cambios para agregar algún comentario al hacer roll Back </li>
 *      <li> 06/06/2005 (SR) Cambios frameset rows="170,*,70" </li>
 *      <li> 24/08/2005 (NC) Se eliminó el frame con el pie de página, dicha información fué movida
 *                           al tope del documento</li> 
 </ul>
-->
<html>
<head>
<title></title>
<jsp:include page="meta.jsp" />
<%
	if ("protegido.doc".equalsIgnoreCase(nameFileToPrint)) {
%>
<link rel=alternate media=print
	href="<%=com.desige.webDocuments.utils.ToolsHTML
						.getFolderTmp()%>/<%=nameFileToPrint%>">
<%
	}
%>
</head>

<%--<frameset rows="240,*" cols="*" frameborder="NO" border="0" framespacing="0" >--%>
<frameset rows="0,*" cols="*" frameborder="NO" border="0"
	framespacing="0">
	<%--<frame src="<%=topFile%>" name="cabezera" scrolling="YES"/>--%>
	<frame src="" name="cabezera" scrolling="YES" />
	<frame src="<%=file%>" name="documento" scrolling="YES" noresize />
</frameset>
<noframes>
</noframes>
</html>

