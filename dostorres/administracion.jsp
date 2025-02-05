<!-- administracion.jsp -->
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
		//response.sendRedirect("administracionMain.do");
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
			<table cellSpacing="0" cellPadding="2"  border="0" width="<%=ToolsHTML.ANCHO_MENU%>px"  height="100%" style="border-collapse:collapse;border-color:#ofofof;;border:1px solid #efefef">
				<tr>
					<td height="30px" class="ppalBoton" onmouseover="this.className='ppalBoton ppalBoton2'" onmouseout="this.className='ppalBoton'">
						<table border="0" cellspacing="0" cellpadding="2" height="100%">
							<tr>
								<td class="ppalTextBold" width="100%">
									<%=rb.getString("enl.Admin")%>
								</td>
								<td>
								    &nbsp;
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td height="99%" bgcolor="white" valign="top" background="menu-images/backgd.png">
						<div   style="height:100%" class="scroll" >
							<table class="ppalText" style="cursor:pointer;" cellspacing="5">
								<tr >
									<td style="text-align:justify">
										&nbsp;&nbsp;<img src="images/exclamacion.gif"/>
										&nbsp;&nbsp;
										<%=rb.getString("admin.message1")%> 
										<br/><br/>
										<%=rb.getString("admin.message2")%>
									</td>
								<tr>
								</tr>
									<td>
									<div id="cuadro" style="width:100px;" class="scroll">
									</div>
									<div id="opcion" style="border:1px solid #000000;padding:5px;height:10px;background-color:white;display:none;text-align:center;">
									<div>
									</td>
								</tr>
							</table>
						</div>
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
						<iframe name="bandeja" id="bandeja" width="100%" height="100%" src="administracionMain.do" border="0px" marginwidth="0px" marginheight="0px" frameborder="0"></iframe>
					</td>
				</tr>
			</table>
		</td>
    </tr>
</table>
</body>
<div id="opc_1" style="display:none">
    <img src="img/user33.jpg">
    <span class="ppalTextBold">
        <%=rb.getString("admin.cuentas")%>
    </span>
</div>
<div id="opc_2" style="display:none">
    <img src="img/parameter33.jpg"> 
    <span class="ppalTextBold">
        <%=rb.getString("admin.config")%>
    </a>
</div>
<div id="opc_3" style="display:none">
	<img src="img/addUser33.jpg">
    <span class="ppalTextBold">
        <%=rb.getString("admin.usuario")%>
    </span>
</div>
<div id="opc_4" style="display:none">
    <img src="img/sendMail33.jpg">
    <span class="ppalTextBold">
	    <%=rb.getString("admin.mensaje")%>
    </span>
</div>
<div id="opc_5" style="display:none">
    <img src="img/addGroup33.jpg">
    <span class="ppalTextBold">
    	<%=rb.getString("admin.grupo")%>
    </span>
</div>
<div id="opc_6" style="display:none">
	<img src="img/normas33.jpg">
    <span class="ppalTextBold">
    	<%=rb.getString("norms.update")%>
    </span>
</div>
<div id="opc_7" style="display:none">
	<img src="img/candado33.jpg">
    <span class="ppalTextBold">
    	<%=rb.getString("admin.seguridad")%>
    </span>
</div>
<div id="opc_8" style="display:none">
	<img src="img/typeDoc33.jpg">
    <span class="ppalTextBold">
    	<%=rb.getString("typeDoc.update")%>
    </span>
</div>
<div id="opc_9" style="display:none">
	<img src="img/passUser33.jpg">
    <span class="ppalTextBold">
    	<%=rb.getString("admin.clave")%>
    </span>
</div>
<div id="opc_10" style="display:none">
	<img src="img/areas33.jpg">
    <span class="ppalTextBold">
    	<%=rb.getString("admin.areas")%>
    </span>
</div>
<div id="opc_11" style="display:none">
	<img src="img/editUser33.jpg">
    <span class="ppalTextBold">
    	<%=rb.getString("admin.changeUserGroup")%>
    </span>
</div>
<div id="opc_12" style="display:none">
	<img src="img/cargos33.jpg">
    <span class="ppalTextBold">
    	<%=rb.getString("admin.cargos")%>
    </span>
</div>
<div id="opc_13" style="display:none">
	<img src="img/editGroup33.jpg">
    <span class="ppalTextBold">
    	<%=rb.getString("admin.changeGroup")%>
    </span>
</div>
<div id="opc_14" style="display:none">
	<img src="img/sacop33.jpg">
    <span class="ppalTextBold">
    	<%=rb.getString("admin.scpplanillas")%>
    </span>
</div>
<div id="opc_15" style="display:none">
	<img src="img/editSession33.jpg">
    <span class="ppalTextBold">
    	<%=rb.getString("session.admin")%>
    </span>
</div>
<div id="opc_16" style="display:none">
	<img src="img/parametrico33.jpg">
    <span class="ppalTextBold">
    <%-- ydavila Ticket 001-00-003023 --%>
	  <%--  <%=rb.getString("admin.activities")%> --%>
	    <%=rb.getString("enl.workFlow")%>
    </span>
</div>
<div id="opc_17" style="display:none">
	<img src="img/excel.jpg">
    <span class="ppalTextBold">
	    <%=rb.getString("migr.title")%>
    </span>
</div>
<div id="opc_18" style="display:none">
	<img src="img/excel.jpg">
    <span class="ppalTextBold">
	    <%=rb.getString("migr.title")%>
    </span>
</div>
<div id="opc_19" style="display:none">
	<img src="img/normas.gif">
    <span class="ppalTextBold">
	    <%=rb.getString("files.configuration")%>
    </span>
</div>
<div id="opc_20" style="display:none">
	<img src="img/Filecab.jpg">
    <span class="ppalTextBold">
	    <%=rb.getString("document.configuration")%>
    </span>
</div>
<div id="opc_21" style="display:none">
    <img src="img/sacop34.jpg">
    <span class="ppalTextBold">
        <%=rb.getString("scp.clasificacionsacop")%>
    </span>
</div>
<div id="opc_22" style="display:none">
    <img src="img/causas.jpg">
    <span class="ppalTextBold">
        <%=rb.getString("admin.possibleCause")%>
    </span>
</div>
<div id="opc_23" style="display:none">
    <img src="img/recommended.jpg">
    <span class="ppalTextBold">
        <%=rb.getString("admin.actionRecommended")%>
    </span>
</div>
<div id="opc_24" style="display:none">
    <img src="img/programAudit.jpg">
    <span class="ppalTextBold">
        <%=rb.getString("admin.programAudit")%>
    </span>
</div>
<!-- aqu ocultar auditoria
<div id="opc_25" style="display:none">
    <img src="img/detective33.jpg">
    <span class="ppalTextBold">
        <%=rb.getString("admin.transactionHistory")%>
    </span>
</div>
 -->

</html>
<script>
	var pixel=10;
	var timer=null;
	
	function clear() {
		ocultar("true");
	}
	function llenar(id) {
		document.getElementById("cuadro").style.height="200px";
		document.getElementById("opcion").innerHTML=document.getElementById(id).innerHTML;
	}
	function mostrar(){
		document.getElementById("opcion").style.display="";
		var alto=document.getElementById("cuadro").style.height;
		if(parseInt(alto)>pixel) {
			document.getElementById("cuadro").style.height=parseInt(alto)-pixel;
			pixel++;
			timer=setTimeout("mostrar()",2);
		} else {
			document.getElementById("cuadro").style.height=1;
			pixel=10;
		}
	}
	function ocultar(limpiar){
		var alto=document.getElementById("cuadro").style.height;
		if(parseInt(alto)<200) {
			document.getElementById("cuadro").style.height=parseInt(alto)+10;
			timer=setTimeout("ocultar('cuadro')",2);
		} else {
			if(limpiar==='true') {
				document.getElementById("opcion").innerHTML="";
			}
			document.getElementById("cuadro").style.height="200px";
			document.getElementById("opcion").style.height="200px";
			document.getElementById("opcion").style.display="none";
			//document.getElementById("opcion").style.backgroundColor="white";
		}
	}
	
	try {
	if(document.all){
		clear();
		document.getElementById("opcion").styel.width="180px";
	} else {
		document.getElementById("opcion").styel.width="140px";
	}
	}catch(e){}
	function hacer(id) {
		clearTimeout(timer);
		llenar(id);
		ocultar();
		mostrar();
	}
</script>
