<!-- perfil.jsp -->
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.enlaces.forms.UrlsActionForm,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 com.desige.webDocuments.utils.beans.Users,
                 com.desige.webDocuments.utils.Constants,
                 com.desige.webDocuments.utils.ToolsHTML"%>
                 
<%@ page import="com.desige.webDocuments.persistent.managers.HandlerBD" %>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<%
    // seteamos el modulo activo
	//request.getSession().setAttribute(Constants.MODULO_ACTIVO, Constants.MODULO_PERFIL);

	if(request.getAttribute("visible")==null || !String.valueOf(request.getAttribute("visible")).equals("true") ) {
		//response.sendRedirect("loadPerfil.do");
	}

    ResourceBundle rb = ToolsHTML.getBundle(request);
    StringBuffer onLoad = new StringBuffer("");
    Users usuario = (Users)session.getAttribute("user");
    String ok = (String) ToolsHTML.getAttributeSession(session, "usuario", false);
    String info = (String) ToolsHTML.getAttribute(request, "info", true);
    boolean mustChange = Boolean.parseBoolean((String) request.getSession().getAttribute("mustChange"));
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script type="text/javascript">
function ver(etiqueta,valor) {
	if(valor=="dat"){
		abrirDat();
	} else if(valor=="url"){
		abrirUrl();
	}
	
	window.open(etiqueta,"bandeja");
}
function agregarNuevoUrl() {
	agregarUrl();
	document.getElementById("urlAgregar").style.display="";
	document.getElementById("urlEliminar").style.display="";
}
function agregarUrl() {
	document.getElementById("urlAgregar").style.display="none";
	document.getElementById("urlCancelar").style.display="";
	document.getElementById("urlGuardar").style.display="";
	document.getElementById("urlEliminar").style.display="none";
	if((typeof window.bandeja.document.newUrls)!="undefined") {
		window.bandeja.incluir(window.bandeja.document.newUrls);
	} else {
		window.bandeja.incluir(window.bandeja.document.editarUrls);
	}		
}
function cancelarUrl() {
	document.getElementById("urlAgregar").style.display="";
	document.getElementById("urlCancelar").style.display="none";
	document.getElementById("urlGuardar").style.display="none";
	document.getElementById("urlEliminar").style.display="none";
	if((typeof window.bandeja.document.newUrls)!="undefined") {
		window.bandeja.cancelar(window.bandeja.document.newUrls,'loadUrls.do');
	} else {
		window.bandeja.cancelar(window.bandeja.document.editarUrls,'loadUrls.do');
	}
}
function guardarUrl() {
	if((typeof window.bandeja.document.newUrls)!="undefined") {
		window.bandeja.salvar(window.bandeja.document.newUrls);
	} else {
		window.bandeja.salvar(window.bandeja.document.editarUrls);
	}
}
function eliminarUrl(){
	window.bandeja.youAreSure(window.bandeja.document.editarUrls);
}

function guardarDat() {
	window.bandeja.salvar();
}

function abrirDat(){
	document.getElementById("datGuardar").style.display="";
	document.getElementById("urlAgregar").style.display="none";
	document.getElementById("urlCancelar").style.display="none";
	document.getElementById("urlGuardar").style.display="none";
	document.getElementById("urlEliminar").style.display="none";
}
function abrirUrl(){
	document.getElementById("datGuardar").style.display="none";
	document.getElementById("urlAgregar").style.display="";
	document.getElementById("urlCancelar").style.display="none";
	document.getElementById("urlGuardar").style.display="none";
	document.getElementById("urlEliminar").style.display="none";
}
</script>
</head>
<body class="bodyInternas" <%=onLoad.toString()%>>
<table cellSpacing="1" cellPadding="0" align="center" border="0" width="100%"  height="100%" >
	<tr>
		<td width="<%=ToolsHTML.ANCHO_MENU%>px" valign="top" align="left" >
			<table cellSpacing="0" cellPadding="2"  border="0" width="<%=ToolsHTML.ANCHO_MENU%>px"  height="100%" style="/*border-collapse:collapse;*/border-color:#ofofof;border:1px solid #efefef">
				<tr>
					<td height="30px" class="ppalBoton" onmouseover="this.className='ppalBoton ppalBoton2'" onmouseout="this.className='ppalBoton'">
						<table border="0" cellspacing="0" cellpadding="2" height="100%">
							<tr>
								<td class="ppalTextBold" width="100%">
									<%=rb.getString("enl.perfil")%>
								</td>
								<td>
								    &nbsp;
								</td>
								<td id="urlAgregar" style="display:none">
								    <span ><img src="images/lapiz.gif" title="<%=rb.getString("btn.incluir")%>" onClick="agregarUrl()"></span>&nbsp;
								</td>
								<td id="urlGuardar" style="display:none">
								    <span ><img src="icons/disk.png" title="<%=rb.getString("btn.save")%>" onClick="guardarUrl()"></span>&nbsp;
								</td>
								<td id="urlEliminar" style="display:none">
								    <span ><img src="images/deleteRound.gif" title="<%=rb.getString("btn.delete")%>" onClick="eliminarUrl()"></span>&nbsp;
								</td>
								<td id="urlCancelar" style="display:none">
								    <span ><img src="images/leftRound.gif" title="<%=rb.getString("btn.cancel")%>" onClick="cancelarUrl()"></span>&nbsp;
								</td>
								<td id="datGuardar">
								    <span ><img src="icons/disk.png" title="<%=rb.getString("btn.save")%>" onClick="guardarDat()"></span>&nbsp;
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td height="99%" bgcolor="white" valign="top" background="menu-images/backgd.png">
					
						<table class="ppalText" style="cursor:pointer;" cellspacing="5">
							<tr onclick="ver('loadPerfilFicha.do','dat')">
								<td><img src="icons/folder_user.png"/></td>
								<td>&nbsp;<%=rb.getString("user.ficha")%></td>
							</tr>
							<tr onclick="ver('loadUrls.do','url')">
								<td><img src="icons/world_link.png"/></td>
								<td>&nbsp;<%=rb.getString("urls.myLinks")%></td>
							</tr>
						</table>
					
					</td>
				</tr>
						<%if (usuario!=null && !mustChange) {
                            out.println(ToolsHTML.printMenu(rb,usuario.getUser(),usuario.getIdGroup(),true,request));
                        }%>
			</table>
		</td>
		<td align="center">
			<table border="0" cellspacing="0" cellpadding="0" width="100%" height="100%">
				<tr>
					<td>
						<iframe name="bandeja" id="bandeja" width="100%" height="100%" src="loadPerfilFicha.do" border="0px" marginwidth="0px" marginheight="0px" frameborder="0"></iframe>
					</td>
				</tr>
			</table>
		</td>
    </tr>
</table>
</body>
</html>
