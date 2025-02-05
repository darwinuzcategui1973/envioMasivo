<!-- /**
 * Title: searchDocumentUbicacion.jsp<br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @author Ing. Simon Rodríguez (SR)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/> 
 * <ul>
 *      <li> 25/05/2005 Se creo el parametro de busqueda TypesStatus,TypeStatuSession(SR)</li>
 *      <li> 01/06/2005 Se creo una session showCharge, para qaue validara por cargo
 *      <li> 19/04/2006 Add label to show count documents </li>
 *      <li> 27/04/2006 (NC) buggs en las búsquedas por cargos & Normas ISO corregido </li>
 *      <li> 30/05/2006 (NC) Cambios para Mostrar la Estructura
 </ul>
 */ -->
<%@ page import="java.util.ResourceBundle,
				 com.desige.webDocuments.files.forms.DocumentForm,
				 java.util.ArrayList,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.persistent.managers.HandlerDBUser,
                 com.desige.webDocuments.persistent.managers.HandlerNorms,
	             com.desige.webDocuments.utils.beans.Users,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 com.desige.webDocuments.utils.beans.PaginPage,
                 com.desige.webDocuments.utils.Constants,
                 com.desige.webDocuments.persistent.managers.HandlerBD,
				 com.desige.webDocuments.document.forms.BaseDocumentForm,
                 java.util.Collection"%>
                 
                 
                 
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>



<%@ page language="java" %> 
<%
	Users usuarioActual = session.getAttribute("user")!=null?(Users)session.getAttribute("user"):null;
	PaginPage Pagingbean = null;
	int lineas=Constants.LINEAS_POR_PAGINA;
	int nameWidth=60;
	int rootWidth=100;
	String ruta = "";
	int contador = 0;

	Collection users = null;
	Collection norms = null;

    ResourceBundle rb = ToolsHTML.getBundle(request);
    String onLoad = ToolsHTML.getMensajePages(request,rb);
    Users usuario = (Users)session.getAttribute("user");
    String ok = (String)session.getAttribute("usuario");
    
	ArrayList conf = (ArrayList)session.getAttribute("confDocument");
	DocumentForm obj;
	
	BaseDocumentForm doc = new BaseDocumentForm();
	request.setAttribute("doc",doc);
    

    if (onLoad==null){
        response.sendRedirect(rb.getString("href.logout"));
    }
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<style>

.ppalText {
	padding:0 20px 0 10px;
	font-size: 12px;
	margin: 0px;
	color: #000000;
	font-family: Sans-serif, Helvetica, Arial, Verdana ;
    text-decoration:none; 
    font-size:11px; 
}
.fondoOscuro{
	top:0px;
	left:0px;
	margin: 0px auto;
	background-color: #000000;
	position: absolute;
	opacity: 0.6;
	-moz-opacity: 0.6;
	-khtml-opacity: 0.6;
	filter: alpha(opacity=60);
	z-index:50;	
}
#caja2 {
	color:white;
	font-weight:bold;
	font-family: Sans-serif, Helvetica, Arial, Verdana ;
	font-size:12px;
	position: absolute;
	z-index:100;	
	border:1px solid white;
}
#caja3 {
	margin: 0px auto;
	background-color: #ffffff;
	position: absolute;
	opacity: 0.6;
	-moz-opacity: 0.6;
	-khtml-opacity: 0.6;
	filter: alpha(opacity=60);
	z-index:60;	
}
p.negro {color: #000;}
</style>
<script language=javascript src="./estilo/funciones.js"></script>
<script language="JavaScript">
String.prototype.trim = function() { return this.replace(/^\s*|\s*$/g,""); };
function getNumeroCampos() {
	var campos = document.getElementsByTagName("input");
	campos = campos + document.getElementsByTagName("select");
	campos = campos + document.getElementsByTagName("radio");
	campos = campos + document.getElementsByTagName("checkbox");
	alert(campos);
	return campos;
}
function cancelar() {
	window.parent.publicarCancelar();
}
function salvar() {
	if(document.searchLocation.comentarios.value.trim()=='') {
		alert('<%=rb.getString("msg.publ.masiva")%>');
		return false;
	}
	document.getElementById("botones").style.display="none";
	document.getElementById("barraProgreso").style.display="";
    frm = document.searchLocation;
    frm.target="_parent";
    frm.action = "publicEraserDocument.do";
    frm.submit();
}

</script>
</head>
<body class="bodyInternas" height="100%">
<form name="searchLocation">
<fieldset >
<legend><%=rb.getString("msg.publ.first.version")%></legend><br/>
<table height="100%" width='100%' border="0">
    <tr>
      <td class='pagesTitle' width='100%' height='22' style=''  >
        <legend><%=rb.getString("tit.publ.masiva")%></legend><br/>
      </td>
    </tr>
    <tr>
      <td class='titleCenter' width='100%' height='22'   >
        <textarea name="comentarios" rows="5" style="width:100%"></textarea>
      </td>
    </tr>
</table>

</fieldset>
<br>
<center>
<span id="botones">
<input type="button" class="boton" value="<%=rb.getString("btn.save")%>" name="btnsalvar" onClick="javascript:salvar();" />
&nbsp;&nbsp;&nbsp;&nbsp;
<input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>" name="btnsalvar" onClick="javascript:cancelar();" />
</span>
<span id="barraProgreso" style="display:none;">
<img src="images/barra_progreso.gif" border="0"/>
</span>
</center>
</form>
</body>
</html>
