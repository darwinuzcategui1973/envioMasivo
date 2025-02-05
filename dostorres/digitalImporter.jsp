<!-- Title: digitalImporter.jsp -->
<%@ page import="com.desige.webDocuments.files.forms.ExpedienteForm,
	             com.desige.webDocuments.utils.beans.Users,
	             java.util.Collection,
	             java.util.ArrayList,
	             com.desige.webDocuments.utils.beans.Search,
	             com.desige.webDocuments.persistent.managers.HandlerDBUser"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%
	Users usuario = (Users)session.getAttribute("user");
	Collection users = HandlerDBUser.getAllUsers();
	pageContext.setAttribute("usuarios",users);

	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	
	ArrayList tipos = (ArrayList)request.getAttribute("typeDoc");
	Search tipo = new Search();	

    // datos del scanner
    int modo = usuario.getModo();
    int lado = usuario.getLado();
    int ppp = usuario.getPpp();
    int panel = usuario.getPanel();
    int separar = usuario.getSeparar();
    int pagina = usuario.getPagina();
    int minimo = usuario.getMinimo();
    int typeDocuments = usuario.getTypeDocuments();

%>
<html>
    <head>
        <link href="estilo/estilo.css" rel="stylesheet" type="text/css"/>
		<script language="JavaScript" src="estilo/funciones.js"></script>
		<script language="JavaScript" src="estilo/Calendario_2.js"></script>
		<style>
			.ventana{
				border-top:1px solid #efefef;
				border-left:1px solid #efefef;
				border-bottom:1px solid #0f0f0f;
				border-right:1px solid #0f0f0f;
			}
		</style>
		<script language="JavaScript">
			function getCheckedRadioValue(obj) {
				for(var x=0; x<obj.length; x++) {
					if(obj[x].checked) {
						return obj[x].value;
					}
				}	
			}
			function reloadScanner() {
				var cad = "modo="+getCheckedRadioValue(document.newDocument.modo);
				cad += "&lado="+getCheckedRadioValue(document.newDocument.lado);
				cad += "&ppp="+document.newDocument.ppp.value;
				cad += "&panel="+document.newDocument.panel.value;
				cad += "&separar="+getCheckedRadioValue(document.newDocument.separar);
				cad += "&pagina="+getCheckedRadioValue(document.newDocument.pagina);
				cad += "&minimo="+document.newDocument.minimo.value;
				cad += "&typeDocuments="+document.newDocument.typeDocuments.value;
				cad += "&aplicar=true";
				cad += "&lote=true";
				window.open('<%=basePath%>digitalizarLoad.do?'+cad,'scanner');
			}
		</script>
	</head>
<body class="bodyInternas" style="padding:20px;">
<form name="newDocument" style="margin:0;">
<table  border='0' width='100%' class="ventana" style="background-color:#b9c3cf;height:300px;" cellpadding="0">
	<tr>
		<td style="background-color:#8f8f8f;color:white;padding:1px;height:20px;font-family:Helvetica,Arial,Verdana;font-weight:bold;font-size:13;" colspan="2">
			Importar archivos electronicos desde una ubicaci&oacute;n fisica
		</td> 
	</tr>
	<tr>
		<td width="100%"> 
			<table cellspacing='0' cellpadding='0' border='0' class='texto' width='100%' height="500px">
				<tr>
					<td valign="top">
						<iframe name="scanner" id="scanner" src="digitalizarLoteFile.jsp" style="margin:0px;width:1000;height:500;" FRAMEBORDER="0" SCROLLING="NO"></iframe>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
</form>
</body>
</html>
