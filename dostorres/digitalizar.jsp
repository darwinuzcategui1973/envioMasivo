<!-- digitalizar.jsp -->
<html>
<head>
<%@ page import="java.util.ResourceBundle,
				com.desige.webDocuments.utils.Constants,
                 com.desige.webDocuments.utils.beans.Users" %>
<%
Users usuario = (Users)session.getAttribute("user");

int modo = (request.getParameter("modo")!=null?Integer.parseInt(request.getParameter("modo")):usuario.getModo());
int lado = (request.getParameter("lado")!=null?Integer.parseInt(request.getParameter("lado")):usuario.getLado());
int ppp = (request.getParameter("ppp")!=null?Integer.parseInt(request.getParameter("ppp")):usuario.getPpp());
int panel = (request.getParameter("panel")!=null?Integer.parseInt(request.getParameter("panel")):usuario.getPanel());
String idDigital = session.getAttribute("idDigital")!=null?String.valueOf(session.getAttribute("idDigital")):"";
%>
<script type="text/javascript">
function success() {
	//alert("success");
	window.parent.document.forms[0].isFileScanned.value=true;
}
function failure() {
	//alert("failure");
	window.parent.document.forms[0].isFileScanned.value=false;
}
function recargarPDF() {
	try {
	window.parent.reloadPDF(); 
	} catch(e){}
}

</script>
</head>
<body style="margin:0;">
<applet code="Digitalizar" archive="digitalizar.jar" width="255" height="24" style="position:absolute;left:0px;top:0px">
	<param name="java_arguments" value="-Xms16M -Xmx378M">
	<param name="user" value="<%=usuario.getIdPerson()%>">
	<param name="modo" value="<%=modo%>">
	<param name="lado" value="<%=lado%>">
	<param name="panel" value="<%=panel%>">
	<param name="ppp" value="<%=ppp%>">
	<param name="modulo" value="<%=session.getAttribute(Constants.MODULO_ACTIVO)%>">
	<param name="idDigital" value="<%= idDigital %>">
</applet>
</body>
</html>
