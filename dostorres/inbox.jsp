<!-- inbox.jsp -->
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.enlaces.forms.UrlsActionForm,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 com.desige.webDocuments.utils.beans.Users,
                 com.desige.webDocuments.utils.ToolsHTML"%>
                 
<%@ page import="com.desige.webDocuments.persistent.managers.HandlerBD" %>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<%
	if(request.getAttribute("visible")==null || !String.valueOf(request.getAttribute("visible")).equals("true") ) {
		//response.sendRedirect("loadPerfil.do");
	}

    ResourceBundle rb = ToolsHTML.getBundle(request);
    StringBuffer onLoad = new StringBuffer("");
    Users usuario = (Users)session.getAttribute("user");
    String ok = (String) ToolsHTML.getAttributeSession(session, "usuario", false);
    String info = (String) ToolsHTML.getAttribute(request, "info", true);
    
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script language="JavaScript">
function ver(etiqueta,valor) {
	returnMail(false);
	if(valor=="entrada"){
		nuevoMail(false);
	} else if(valor=="enviado"){
		nuevoMail(false);
	} else if(valor=="url"){
		abrirUrl();
	} else if(valor=="nuevoMail"){
		nuevoMail(true);
	}
	
	window.open(etiqueta,"bandeja");
}
function nuevoMail(valor) {
	document.getElementById("mailDelete").style.display=(valor?"none":"");
	document.getElementById("mailNuevo").style.display=(valor?"none":"");
	document.getElementById("mailEnviar").style.display=(valor?"":"none");
	document.getElementById("mailCancelar").style.display=(valor?"":"none");

	document.getElementById("urlAgregar").style.display="none";
	document.getElementById("urlCancelar").style.display="none";
	document.getElementById("urlGuardar").style.display="none";
	document.getElementById("urlEliminar").style.display="none";

	document.getElementById("buscar").style.display="none";
}
function returnMail(valor) {
	document.getElementById("mailResponse").style.display=(!valor?"none":"");
	document.getElementById("mailResend").style.display=(!valor?"none":"");
}
function response() {
	returnMail(false);
	window.bandeja.frames['detalle'].response();
}
function resend() {
	returnMail(false);
	window.bandeja.frames['detalle'].send();
}

function agregarNuevoUrl() {
	nuevoMail(false);
	document.getElementById("buscar").style.display="";
	document.getElementById("urlAgregar").style.display="";
	document.getElementById("urlEliminar").style.display="";
	document.getElementById("mailDelete").style.display="none";
}

function agregarUrl() {
	document.getElementById("buscar").style.display="none";

	document.getElementById("mailDelete").style.display="";
	document.getElementById("urlAgregar").style.display="none";
	document.getElementById("urlCancelar").style.display="";
	document.getElementById("urlGuardar").style.display="";
	document.getElementById("urlEliminar").style.display="";
	window.bandeja.incluir(window.bandeja.document.newUrls);
}

function modificarUrl() {
	document.getElementById("buscar").style.display="none";

	document.getElementById("urlAgregar").style.display="none";
	document.getElementById("urlCancelar").style.display="";
	document.getElementById("urlGuardar").style.display="";
	document.getElementById("urlEliminar").style.display="none";
}

function cancelarUrl() {
	document.getElementById("urlAgregar").style.display="";
	document.getElementById("urlCancelar").style.display="none";
	document.getElementById("urlGuardar").style.display="none";
	document.getElementById("urlEliminar").style.display="none";
	window.bandeja.cancelar(window.bandeja.document.newUrls,'loadUrls.do');
}
function guardarUrl() {
	window.bandeja.addContact();
}
function eliminarUrl(){
	window.bandeja.youAreSure();
}
function eliminarMails(){
	window.bandeja.lista.youAreSure();
}


function guardarDat() {
	window.bandeja.salvar();
}

function abrirUrl(){
	document.getElementById("buscar").style.display="";
	
	document.getElementById("datGuardar").style.display="none";
	document.getElementById("urlAgregar").style.display="";
	document.getElementById("urlCancelar").style.display="none";
	document.getElementById("urlGuardar").style.display="none";
	document.getElementById("urlEliminar").style.display="none";
	document.getElementById("mailDelete").style.display="none";
}

function buscarUrl() {
	window.bandeja.buscarContacto(document.getElementById("busqueda"));
}

function enviando(){
	window.frames[0].enviar()
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
									<%=rb.getString("enl.messages")%>
								</td>
								<td>
								    &nbsp;
								</td>
								<td id="mailResponse" style="display:none">
					            	<%if(!ToolsHTML.isNotifyEmail()){%>
									    <span ><img src="icons/email_back.png" title="<%=rb.getString("btn.reply")%>" onClick="javascript:alert('<%=rb.getString("E0131")%>')"></span>&nbsp;
					                <%}else{%>
									    <span ><img src="icons/email_back.png"  title="<%=rb.getString("btn.reply")%>" onClick="response(true)"></span>&nbsp;
					                <%}%>
								</td>
								<td id="mailResend" style="display:none">
					            	<%if(!ToolsHTML.isNotifyEmail()){%>
									    <span ><img src="icons/email_go.png" title="<%=rb.getString("btn.forward")%>" onClick="javascript:alert('<%=rb.getString("E0131")%>')"></span>&nbsp;
					                <%}else{%>
									    <span ><img src="icons/email_go.png"  title="<%=rb.getString("btn.forward")%>" onClick="resend()"></span>&nbsp;
					                <%}%>
								</td>
								<td id="mailDelete">
								    <span ><img src="icons/cancel.png" title="<%=rb.getString("mail.delete")%>" onClick="javascript:eliminarMails();"></span>&nbsp;
								</td>
								<td id="mailNuevo">
					            	<%if(!ToolsHTML.isNotifyEmail()){%>
									    <span ><img src="icons/email_add.png" title="<%=rb.getString("mail.new")%>" onClick="javascript:alert('<%=rb.getString("E0131")%>')"></span>&nbsp;
					                <%}else{%>
									    <span ><img src="icons/email_add.png" title="<%=rb.getString("mail.new")%>" onClick="ver('loadMail.do','nuevoMail')"></span>&nbsp;
					                <%}%>
								</td>
								<td id="mailEnviar" style="display:none">
								    <span ><img src="icons/email_go.png"  title="<%=rb.getString("btn.send")%>" onClick="enviando()"></span>&nbsp;
								</td>
								<td id="mailCancelar" style="display:none">
								    <span ><img src="icons/arrow_undo.png" title="<%=rb.getString("btn.cancel")%>" onClick="ver('inboxEntryFrame.jsp','entrada')"></span>&nbsp;
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
								<td id="datGuardar" style="display:none">
								    <span ><img src="images/floppy.gif" width="21" height="21" title="<%=rb.getString("btn.save")%>" onClick="guardarDat()"></span>&nbsp;
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td height="99%" bgcolor="white" valign="top" background="menu-images/backgd.png">
					
						<table class="ppalText" style="cursor:pointer;" cellspacing="3" border="0">
							<tr onclick="ver('inboxEntryFrame.jsp','entrada')">
								<td width="1%"><img src="icons/folder_in.png"/></td>
								<td>&nbsp;<%=rb.getString("mail.inbox")%></td>
							</tr>
							<tr onclick="ver('inboxSendedFrame.jsp','enviado')">
								<td><img src="icons/folder_out.png"/></td>
								<td>&nbsp;<%=rb.getString("mail.mailsSends")%></td>
							</tr>
							<tr onclick="ver('contacts.do','url')">
								<td><img src="icons/group.png"/></td>
								<td>&nbsp;<%=rb.getString("contacts.lista")%></td>
							</tr>
							<!-- comienzo DARWINUZCATEGUI DOS TORRES -->
							<tr onclick="ver('contacts.do','url')">
								<td><img src="icons/group.png"/></td>
								<td>&nbsp;lista de recibo</td>
							</tr>
							<!-- comienzo DARWINUZCATEGUI DOS TORRES -->
							<tr onclick="ver('contacts.do','url')">
								<td><img src="icons/group.png"/></td>
								<td>&nbsp;lista de OTRAS COSAS</td>
							</tr>
							<tr id="buscar" style="display:none">
								<td colspan="2"><br/>
					                <%=rb.getString("contacts.nombre")%>&nbsp;/&nbsp;<%=rb.getString("contacts.apellido")%><br/>
					                <input type="text" id="busqueda" name="busqueda" style="width:150px;height: 19px" value=""/>
					                <img src="icons/find.png" onclick="buscarUrl()" title="<%=rb.getString("btn.search")%>"/>
								</td>
							</tr>
						</table>
					
					</td>
				</tr>
						<%if (usuario!=null) {
                            out.println(ToolsHTML.printMenu(rb,usuario.getUser(),usuario.getIdGroup(),true,request));
                        }%>
			</table>
		</td>
		<td align="center">
			<table border="0" cellspacing="0" cellpadding="0" width="100%" height="100%">
				<tr>
					<td>
						<iframe name="bandeja" id="bandeja" width="100%" height="100%" src="inboxEntryFrame.jsp" border="0px" marginwidth="0px" marginheight="0px" frameborder="0"></iframe>
					</td>
				</tr>
			</table>
		</td>
    </tr>
</table>
</body>
</html>
<script LANGUAGE='JavaScript'>
function redimensionarIframe(tipo) {
	// Guardamos el iframe que vamos a redimensionar
	var iFrame = window.document.getElementById('bandeja');

	//alert(document.body.scrollHeight);
	//alert(document.body.offsetHeight);
	//alert(document.body.clientHeight);
	
	var alturaIframe = document.body.scrollHeight-12;
	// Redimensionamos la altura del iframe
	if(typeof tipo== "undefined"){
		iFrame.style.height = "100%";
		setTimeout("redimensionarIframe('0')",500);
	} else {
		iFrame.style.height = eval("'"+alturaIframe+"px'");
	}
	
}// Fin de la función

if(document.all) {
	window.onresize=redimensionarIframe;
}
</script>
<script language="javascript" event="onload" for="window">
if(document.all) {
	redimensionarIframe("0");
}
</script>
