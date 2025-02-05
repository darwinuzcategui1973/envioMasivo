<html>
<head>
<%@ page import="java.util.ResourceBundle,
				 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.Users" %>
<%
Users usuario = (Users)session.getAttribute("user");

int modo = (request.getParameter("modo")!=null?Integer.parseInt(request.getParameter("modo")):usuario.getModo());
int lado = (request.getParameter("lado")!=null?Integer.parseInt(request.getParameter("lado")):usuario.getLado());
int ppp = (request.getParameter("ppp")!=null?Integer.parseInt(request.getParameter("ppp")):usuario.getPpp());
int panel = (request.getParameter("panel")!=null?Integer.parseInt(request.getParameter("panel")):usuario.getPanel());
int separar = (request.getParameter("separar")!=null?ToolsHTML.parseInt(request.getParameter("separar")):usuario.getSeparar());
int pagina = (request.getParameter("pagina")!=null?Integer.parseInt(request.getParameter("pagina")):usuario.getPagina());
int minimo = (request.getParameter("minimo")!=null?Integer.parseInt(request.getParameter("minimo")):usuario.getMinimo());
int typeDocuments = usuario.getTypeDocuments();

%>
<script type="text/javascript">
function success() {
	//window.parent.document.forms[0].isFileScanned.value=true;
}
function failure() {
	//window.parent.document.forms[0].isFileScanned.value=false;
}
</script>
</head>
<body style="margin:0;">
<applet code="DigitalizarLote" archive="digitalizarLote.jar" width="400" height="380" style="position:absolute;left:0px;top:0px">
	<param name="java_arguments" value="-Xms16M -Xmx256M">
	<param name="user" value="<%=usuario.getIdPerson()%>">
	<param name="modo" value="<%=modo%>">
	<param name="lado" value="<%=lado%>">
	<param name="panel" value="<%=panel%>">
	<param name="ppp" value="<%=ppp%>">
	<param name="separar" value="<%=separar%>">
	<param name="pagina" value="<%=pagina%>">
	<param name="minimo" value="<%=minimo%>">
	<param name="typeDocuments" value="<%=typeDocuments%>">
</applet>
</body>
</html>
