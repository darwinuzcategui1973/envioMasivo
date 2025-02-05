<!-- /**
 * Title: digitalizarLoteFile.jsp<br/>
 */ -->
 <%@page import="com.desige.webDocuments.parameters.forms.BaseParametersForm"%>
<%@page import="com.desige.webDocuments.persistent.managers.HandlerParameters"%>
<html>
<head>
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.beans.Users" %>
<%
Users usuario = (Users)session.getAttribute("user");

int modo = (request.getParameter("modo")!=null?Integer.parseInt(request.getParameter("modo")):usuario.getModo());
int lado = (request.getParameter("lado")!=null?Integer.parseInt(request.getParameter("lado")):usuario.getLado());
int ppp = (request.getParameter("ppp")!=null?Integer.parseInt(request.getParameter("ppp")):usuario.getPpp());
int panel = (request.getParameter("panel")!=null?Integer.parseInt(request.getParameter("panel")):usuario.getPanel());
int separar = (request.getParameter("separar")!=null?Integer.parseInt(request.getParameter("separar")):usuario.getSeparar());
int pagina = (request.getParameter("pagina")!=null?Integer.parseInt(request.getParameter("pagina")):usuario.getPagina());
int minimo = (request.getParameter("minimo")!=null?Integer.parseInt(request.getParameter("minimo")):usuario.getMinimo());
int typeDocuments = (request.getParameter("typeDocuments")!=null?Integer.parseInt(request.getParameter("typeDocuments")):usuario.getTypeDocuments());

BaseParametersForm forma = new BaseParametersForm();
HandlerParameters.load(forma);
byte viewPdfTool = forma.getViewpdftool();
String isDocPrint = (viewPdfTool == (byte)0 ? "true" : "false");
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
<applet code="DigitalizarLoteFile" archive="digitalizarLoteFile.jar" width="1000" height="500" style="position:absolute;left:0px;top:0px">
	<param name="java_arguments" value="-Xms16M -Xmx256M">
	<param name="user" value="<%=usuario.getIdPerson()%>">
	<param name="modo" value="<%=modo%>">
	<param name="lado" value="<%=lado%>">
	<param name="panel" value="<%=panel%>">
	<param name="ppp" value="<%=ppp%>">
	<param name="separar" value="<%=separar%>">
	<param name="pagina" value="<%=pagina%>">
	<param name="minimo" value="<%=minimo%>">
	<param name="isDocPrint" value="<%=isDocPrint%>">
	<param name="typeDocuments" value="<%=typeDocuments%>">
</applet>
</body>
</html>
