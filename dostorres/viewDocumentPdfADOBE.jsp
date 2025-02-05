<!--/**
 * Title: viewDocument.jsp <br>
 * Copyright: (c) 2003 Focus Consulting<br>
 * Company: Consis International (CON)<br>
 * @author Nelson Crespo (NC)
 * @author Simón Rodriguez (SR)
 * @version Acsel-e v2.2
 * <br>
 * Changes:<br>
 * <ul>
 *      <li> 01/06/2005 (NC) Cambios para agregar algún comentario al hacer roll Back </li>
 *      <li> 06/06/2005 (SR) Cambios frameset rows="170,*,70" </li>
 *      <li> 24/08/2005 (NC) Se eliminó el frame con el pie de página, dicha información fué movida
 *                           al tope del documento</li>
 *      <li> 27/04/2006 (NC) Bugg al imprimir documentos corregido </li>
 *      <li> 09/05/2006 (SR) Si el documento no tiene permisologia, sale un documento impreso diciendo que
                             este documento se encuentra protegido.</li>
 *      <li> 30/06/2006 (NC) Cambios para correcto formato de los documentos a Mostrar </li>
 *      <li> 13/07/2006 (NC) Cambios para heredar o no los Prefijos de las Carpetas </li>
 *      <li> 14/06/2007 (YS) Si no es un FTP se cargan en sesión revisores de un documento. Se agregó función javascript recargarPublicados()</li>
 *      <li> 04/08/2008 (YS) Se carga en sesión elaborador de un documento</li>
 * <ul>
 */-->
<%@ page import="com.desige.webDocuments.utils.ToolsHTML,
                 java.util.Collection,
                 com.desige.webDocuments.persistent.managers.HandlerDocuments,
                 com.desige.webDocuments.persistent.managers.HandlerStruct,
                 com.desige.webDocuments.persistent.managers.HandlerBD,
                 com.desige.webDocuments.document.forms.BaseDocumentForm,
                 com.desige.webDocuments.document.forms.BeanFirmsDoc,
                 com.desige.webDocuments.utils.DesigeConf,
                 com.desige.webDocuments.utils.Constants"%>
<%@ page import="com.desige.webDocuments.document.actions.loadsolicitudImpresion"%>
<%@ page import="java.util.Hashtable"%>
<%@ page import="com.desige.webDocuments.utils.beans.Users"%>
<%@ page import="java.util.ResourceBundle"%>
<%@ page import="java.util.Locale"%>
<%
    String parameters = ToolsHTML.parseParameters(request);
    String topFile = "topDocument.jsp" + parameters;
    String file = "showFile.jsp";
    boolean isExpediente = false;
    boolean isFirstPage = Boolean.parseBoolean(request.getParameter("firstPage")!=null?String.valueOf(request.getParameter("firstPage")):"false");
    
    if(request.getParameter("showFile")!=null){
    	file = request.getParameter("showFile");
    	if(file.equals("showExpediente.jsp")) {
    		isExpediente = true;
    		isFirstPage = true;
    	}
    }
    file = file + parameters;
%>
<html>
<head>
	<title> Qwebdocuments </title>
	<jsp:include page="meta.jsp" /> 
</head>
<body onkeydown="return false" >
	<script language="javascript">
	document.oncontextmenu = function(){return false;}
	</script>
	<%if (request.getHeader("USER-AGENT").indexOf("MSIE")!=-1) { %>
		<iframe name="wndDocument" src="<%=file%>" style="width:103%;height:101%;position:absolute;top:0px;left:0px"/></iframe>
		<iframe id="barrapdf" name="barrapdf" src="barrapdf.jsp?idDocument=<%=request.getParameter("idDocument")%>&idVersion=<%=request.getParameter("idVersion")%>&isExpediente=<%=isExpediente%>&firstPage=<%=isFirstPage%>" style="height:39px;position:absolute;top:0px;left:0px;width:103%;z-index:1000;" scrolling="no" frameborder="0"></iframe>
	<%} else {%>
		<iframe name="wndDocument" src="<%=file%>" style="width:99%;height:99%;position:absolute;top:0px;left:0px"/></iframe>
		<iframe id="barrapdf" name="barrapdf" src="barrapdf.jsp?idDocument=<%=request.getParameter("idDocument")%>&idVersion=<%=request.getParameter("idVersion")%>&isExpediente=<%=isExpediente%>&firstPage=<%=isFirstPage%>" style="height:39px;position:absolute;top:0px;left:0px;width:100%;z-index:10000;" scrolling="no" frameborder="0"></iframe>
	<%}%>
</body>
</html>