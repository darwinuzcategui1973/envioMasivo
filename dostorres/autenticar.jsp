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
.login {
	width: 160px;
	background-color:transparent;
	border:1px solid #cfcfcf;
	font-size:11pt;
	padding:2px;
	color:black;
	background:url(images/login.gif);
	font-weight:normal;
}
</style>

<script type="text/javascript">
	function entrar(){
		if(confirm("<%=rb.getString("err.userCloseSession")%>")){
	        if (document.login.btnOk) {
    	        document.login.btnOk.disabled = true;
        	}
	        document.login.logout.value="true";
    	    document.login.submit();
        }
	}

    function Aceptar(forma) {
    	if(!conformacion()){
    		return;
    	}
	    if(forma.user.value!='' && forma.clave.value!='' && forma.user.value.length>0 && forma.clave.value.length>0){
	        if (forma.btnOk) {
	            forma.btnOk.disabled = true;
	            setTimeout("document.login.btnOk.disabled=false",3000);
	        }
	        forma.user.focus();
	        open_full("",'');
	        forma.target='';
			//escondeme();
	        forma.submit();
	        forma.user.value="";
	        forma.clave.value="";
	        forma.url.value="null";
        }else if(forma.user.value=='' && forma.clave.value!=''){
        	alert('<%=rb.getString("notUser")%>');
        	forma.user.focus();
        }else if(forma.clave.value=='' && forma.user.value!=''){
        	alert('<%=rb.getString("notPass")%>');
        	forma.clave.focus();
      	}
	}
	
 	function showMessage(mensaje,op){
        alert(mensaje);
    }
	function checkKey(evt,forma) {
        var charCode = (evt.which) ? ect.which : event.keyCode;
        if (charCode == 13) {
            Aceptar(forma);
        }
    }
    function linkActive() {
    	conformar(false);
	    <%if(request.getAttribute("logout")!=null && String.valueOf(request.getAttribute("logout")).equals("true")){%>
	        entrar();
	    <%}%>
        //document.login.user.focus();
    }
	function open_full(dir,name){
		var Ancho=screen.availWidth-10;
		var Alto=screen.availHeight-28;
		var features="toolbar=no,status=yes,resizable=yes,location=no,directories=no,menubar=no,scrollbars=yes,top=0,left=0,width="+Ancho+",height="+Alto+'"';
		myWindow = window.open(dir,name,features);
	}    
	function cerrarSession() {
		document.location.href = "logout.do?pag=1";
	}
	
    function conformacion() {
   		return window.valid;
   	}
   	function continuar() {
   		document.login.lic.value="";
   		document.login.demo.value="true";
   		var nDoc = <%=session.getAttribute("documentosRegistrados")%>;
   		var nMax = 100
   		if(nDoc>=nMax) {
   			alert("El periodo de prueba ha expirado. por favor comuniquese con Focus Consulting para tramitar su licencia.");
   			return;
   		} else {
   			alert("El periodo de prueba le permite gestionar hasta un maximo de "+nMax+" documentos. actualmente registrados "+nDoc);
   		}
   		window.valid=true;

   	}
   	function conformar(revisar) {
   		window.valida=revisar;
   		window.http = false;
		if(navigator.appName == "Microsoft Internet Explorer") {
		  window.http = new ActiveXObject("Microsoft.XMLHTTP");
		} else {
		  window.http = new XMLHttpRequest();
		}
		var cad = "";
		cad += "emp="+document.login.emp.value.replace(/'/g,'').replace(/&/g,'.ampersand.');
		cad += "&rif="+document.login.rif.value;
		cad += "&mac="+document.login.mac.value;
		cad += "&lic="+document.login.lic.value;
		window.http.open("POST", "updateLicenciaAjax.do?"+cad);
		window.http.onreadystatechange=function(resp,txt) {
		  try {
			  if(window.http.readyState == 4) {
				if(window.http.responseText=='true'){
					//alert("conforme");
				   	document.getElementById('fondo').style.display='none';
				   	document.getElementById('fondo2').style.display='none';
				   	window.valid=true;
				   	if(window.valida){
						document.location.href="./index.jsp";
					}
				} else {
				   	window.valid=false;
				   	if(window.valida){
						alert("Licencia invalida");
					}
				   	document.getElementById('fondo').style.display='';
				   	document.getElementById('fondo2').style.display='';
				}
			  }
		  } catch(e){
		  }
		}
		http.send(null);
   	}
   	
   	function solicitar() {
   		window.http = false;
		if(navigator.appName == "Microsoft Internet Explorer") {
		  window.http = new ActiveXObject("Microsoft.XMLHTTP");
		} else {
		  window.http = new XMLHttpRequest();
		}
		var cad = "";
		cad += "emp="+document.login.emp.value.replace(/'/g,'').replace(/&/g,'.ampersand.');
		cad += "&rif="+document.login.rif.value;
		cad += "&mac="+document.login.mac.value;
		cad += "&lic="+document.login.lic.value;
		window.http.open("POST", "solicitarLicenciaAjax.do?"+cad);
		window.http.onreadystatechange=function(resp,txt) {
		  try {
			  if(window.http.readyState == 4) {
				if(window.http.responseText=='true'){
				   	document.getElementById('fondo').style.display='none';
				   	document.getElementById('fondo2').style.display='none';
				   	alert("Se ha enviado un correo a soporte@focus.com.ve, su requerimiento sera procesado a la brevedad posible.");
				} else {
					alert("No se ha podido establecer la comunicacion por correo");
				}
			  }
		  } catch(e){
		  }
		}
		http.send(null);
   	}
   	
</script>
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<style>
.tableInic {
	background-image: url(img/fondoDosTorres.jpg);
	background-repeat: no-repeat;
	background-position: top center;
}
.centerbox
{
   position:absolute;
   top:50%;
   left:50%;
   margin-top:-40px;
   margin-left:-250px;
   height:210px;
   width:500px;
   text-align:center;
   padding-top:10px;
   border:5px solid lightsteelblue;
   background-color: #045FB4; 
   font-family:Verdana;
   filter:alpha(opacity=90);
   -moz-opacity: 0.9;
   opacity: 0.9;
}

#fondo {
	top:0px;
	left:0px;
	position:absolute;
	width: 100%;
	height: 100%;
	background: url();
}	
	
#fondoxx{
	top:0px;
	left:0px;
	width:100%;
	height:115%;
	background-color: #000;
  	filter:alpha(opacity=50);
  	-moz-opacity: 0.5;
  	opacity: 0.5;
}
	
</style>
</head>
<body <%=onLoad.toString()%> bgcolor="white">
<html:form action="/inicioSession.do" onsubmit="return false;">
<input type="hidden" name="url" value="<%=session.getAttribute("url")%>"/>
<input type="hidden" name="logout" value="false"/>
<input type="hidden" name="demo" value="false"/>
<table width="10%" height="10%" align="center" border="0" class="tableInic">
<tr>
	<td>
		<table width="30" height="10%" align="center" border="0">
		<tr>
			<td colspan="2" height="100">&nbsp;
			</td>
		</tr>
		<tr>  
			<td width="5%" height="50">
		        <table width="165" align=center border="0">
		            <tr>
		                <td align="left" class="txt">
		                <%=rb.getString("autenticar.user")%>
		                    <%=rb.getString("login.user")%>
		                </td>
		            </tr>
		            <tr>
		                <td align="left">
		                    <html:text property="user" styleClass="login"/>
		                </td>
		            </tr>
		            <tr>
		                <td align="left" class="txt">
		                    <%=rb.getString("login.pass")%>
		                </td>
		            </tr>
		            <tr>
		                <td align="left">
		                    <html:password property="clave" styleClass="login"/>
		                </td>
		            </tr>
		            <tr>
		                <td align="center" valign="middle" >
		                    <input type="submit" class="boton" value="<%=rb.getString("btn.ok")%>" onClick="javascript:Aceptar(this.form);" name="btnOk" id="btnOk"/>
		                </td>
		            </tr>
		        </table>
			</td>
			
		</tr>
		<tr>
			<td colspan="2">&nbsp;
			</td>
		</tr>
		</table>
		<div id="" style="position:absolute;display:none;">
		</div>
			<div id="" class="centerbox" style="display:none;" >
				<div style="position:abolute;margin-top:-12px;height:23px;border:0px solid white;width:612px;text-align:left;background-color:white;">
					<table border="0" cellspacing="0" cellpadding="0" width="100%" style="padding-top:3px;">
						<tr>
							<td aling="left"><b style="font-family:Tahoma;font-size:10pt;">&nbsp;CONFORMACION DE LICENCIA - Qwebdocuments - versi&oacute;n <%=rb.getString("lic.version")%></b></td>
							<td align="right" style="display:none;"><a style="cursor:pointer;" onclick="javascript:document.getElementById('fondo').style.display='none';document.getElementById('fondo2').style.display='none';"><img src="./icons/cancel.png"></a>&nbsp;&nbsp;</td>
						</tr>
					</table>
				</div>
				<div id="error" style="padding:10px;background-color:#045FB4;">
					<table border="0" width="100%" >
						<tr>
							<td class="titleLeft left" nowrap>Nombre de la Empresa : </td>
							<td><input type="text" name="emp" size="70" value="<%=modulo.getEmpresa()%>"></td>
						</tr>
						<tr>
							<td class="titleLeft left">C&oacute;digo Identificaci&oacute;n Tributario : </td>
							<td><input type="text" name="rif" size="70" value="<%=modulo.getRif()%>"></td> 
						</tr>
						<tr>
							<td class="titleLeft left">C&oacute;digo de Instalaci&oacute;n: </td>
							<td><input type="text" name="mac" size="60" value="<%=NetworkInfo.getSerialMac()%>" style="background:transparent;border:1px solid white;color:white;font-weight:bold;" readonly></td>
						</tr>
						<tr>
							<td class="titleLeft left">N&uacute;mero de Licencia : </td>
							<td><input type="text" name="lic" size="70" value="<%=modulo.getLicencia()%>"></td>
						</tr>
						<tr>
							<td class="titleLeft" colspan="2" style="padding-top:10;">
								<input type="button" value="Aceptar y Validar" onclick="conformar(true)">&nbsp;&nbsp;&nbsp;
								<input type="button" value="Solicitar Clave por Correo" onclick="solicitar()">&nbsp;&nbsp;&nbsp;
								<input type="button" value="Modo de Prueba" onclick="continuar()">
							</td>
						</tr>
						<tr>
							<td class="titleLeft" colspan="2" style="padding-top:10;">
								Solicite la cotizaci&oacute;n de licenciamiento 
								<a href="http://www.qwebcloud.com/index.php/licenciamiento" target="_blank" style="text-decoration: none;color:#ffffff;">
									http://www.qwebcloud.com/index.php/licenciamiento
								</a>
							</td>
						</tr>
					</table>
				</div>
			</div>
	</td>
</tr>
</table>
</html:form>
</body>
</html:html>
<script>
	//conformar(false);
</script>
