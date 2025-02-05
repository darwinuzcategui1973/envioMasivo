 <%@ page import="java.util.Locale, 
                 java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,com.desige.webDocuments.util.Encryptor,
                 com.desige.webDocuments.persistent.managers.HandlerDBUser,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.focus.util.ModuloBean,
                 com.focus.util.NetworkInfo"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %> 
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>  

<%
	StringBuffer b = new StringBuffer();
	String serverName = request.getServerName();
	b.append("http://").append(request.getServerName()).append(":").append(request.getServerPort()).append(request.getContextPath());
%>
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    ModuloBean modulo = ToolsHTML.validarLicencia();
    StringBuffer onLoad = new StringBuffer("");
    String error = (String)ToolsHTML.getAttributeSession(session,"error",true);
    if ((error != null) && (error.compareTo("") != 0)) {
        onLoad.append("  onLoad=\"showMessage('").append(error).append("');linkActive();\"");
        session.removeAttribute("error");
    } else {
        onLoad.append("onLoad=\"linkActive();\"");
    }

%>
<html:html xhtml="true" locale="true">
<head>
<title><%=rb.getString("login.title")%></title>
<jsp:include page="meta.jsp" /> 
	<style>
		body{
		    background-image: url(img/fondoDelSur.jpg);
		    background-repeat: no-repeat;
		    background-position: top center;
		}
	</style>
</head>
<body <%=onLoad.toString()%> bgcolor="white">
    <script type="text/javascript">
        alert("La ejecución del Job En Demanda fue iniciada.\nPor favor confirme los resultados de la misma.");
    </script>
</body>
</html:html>

