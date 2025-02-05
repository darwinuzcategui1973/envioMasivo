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
	//b.append("http://").append(request.getServerName()).append(":").append(request.getServerPort()).append(request.getContextPath());
	String path = request.getRequestURL().toString().replace(request.getRequestURI(), request.getContextPath()) + "/";
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

<!-- include the RTL css files-->
<link rel="stylesheet" href="css/alertify.rtl.css">
<link rel="stylesheet" href="css/themes/semantic.css">

<!-- include alertify script -->
<script src="alertify.js"></script>

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
    	<%if(ToolsHTML.LICENCIA_GRACIA_VENCIDA){%>
    	if(forma.user.value!='admin') {
    		alert("Usuario no Autorizado.");
    		document.location.href="./index.jsp";
    		return;
    	}
    	<%}%>
	    if(forma.user.value!='' && forma.clave.value!='' && forma.user.value.length>0 && forma.clave.value.length>0){
	        if (forma.btnOk) {
	            forma.btnOk.disabled = true;
	            setTimeout("document.login.btnOk.disabled=false",3000);
	        }
	        forma.user.focus();
	        open_full("",'main_qweb');
	        forma.target='main_qweb';
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
		var AnchoTotal=screen.availWidth-10;
		var AltoTotal=screen.availHeight-28;
		
		var Ancho = AnchoTotal;
		var Alto = AltoTotal;
		var Top = 0;
		var Left = 0;
		
		if(document.location.href.indexOf('localhost')!=-1) {
			console.log(document.location.href);
			Ancho = Math.min(1440,AnchoTotal);
			Alto = Math.min(900,AltoTotal);
			Top = Math.max(Math.min(AltoTotal-Alto,10),10);
			Left = Math.max(AnchoTotal-Ancho,10);
			console.log("ancho="+Ancho + " , alto=" + Alto);
		}
		var features="toolbar=no,status=yes,resizable=yes,location=no,directories=no,menubar=no,scrollbars=yes,top="+Top+",left="+Left+",width="+Ancho+",height="+Alto+'"';
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
	   	document.getElementById('fondo').style.display='none';
	   	document.getElementById('fondo2').style.display='none';
   	}
   	function continuarAdmin() {
   		document.login.lic.value="";
   		//document.login.demo.value="true";
   		window.valid=true;
	   	document.getElementById('fondo').style.display='none';
	   	document.getElementById('fondo2').style.display='none';
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
   	
   	function verCuadroLicencia() {
	   	document.getElementById('fondo').style.display='';
	   	document.getElementById('fondo2').style.display='';
   	}


            var winWidth  = 600;
            var winHeight = 800;
            function setDimensionScreen() {
                if (screen.availWidth) {
                    winWidth = screen.availWidth;
                }
                if (screen.availHeight){
                    winHeight = screen.availHeight - 100;
                }
            }

            function abrirVentana(pagina,width,height,nameWin) {
            	setDimensionScreen();
                //alert('Mostrando a: ' + pagina);
                var hWnd = null;
                var left = getPosition(winWidth,width);
                var top = getPosition(winHeight,height);
                hWnd = window.open("<%=path%>"+pagina, nameWin, "resizable=yes,scrollbars=yes,statusbar=yes,width="+width+",height="+height+",left="+left+",top="+top);
            }
            function getPosition(totalValue, value) {
            	var calculo = (totalValue - value) / 2;
            	if (calculo < 0) {
            		return 0;
            	}
            	return calculo;
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
   margin-left:-356px;
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
	background: url(img/vacio.gif);
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
<table width="100%" height="100%" align="center" border="0" class="tableInic">
<tr>
	<td>
		<table width="800" height="100%" align="center" border="0">
		<tr>
			<td colspan="2" height="410">&nbsp;
			</td>
		</tr>
		<tr>
			<td width="35%" height="130" border="0">
		        <table width="135" align=center border="0">
		            <tr>
		                <td align="left" class="txtNegro">
		                    <%=rb.getString("login.user")%> <img src="icons/help.png" class="helpBTN" alt='Mostrar Ayuda' onclick="abrirVentana('download/Manual_de_Usuarios_Finales_QwebDocuments_V5_Introduccion.pdf',800,800,'help')">
		                </td>
		            </tr>
		            <tr>
		                <td align="left">
		                    <html:text property="user" styleClass="login" onkeyup="this.value=this.value.toLowerCase()"/>
		                </td>
		            </tr>
		            <tr>
		                <td align="left" class="txtNegro">
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
			<td width="65%" align="center">
				<table border="0" align="left" cellspacing="1" cellpadding="0" >
				  <tr>
				    <td class="txtNormalNegro" width="80"><%=rb.getString("sch.version")%>:</td>
				    <td class="txtSmallNegro">&nbsp;<%=rb.getString("lic.version")%></td>
   				  <!--   <td class="txtSmallNegro">&nbsp;<%=rb.getString("lic.version") + rb.getString("lic.hotfixcode")%></td> -->
				  </tr>
				  <tr>
				    <td class="txtNormalNegro" styleClass="classText"><%=rb.getString("lic.propietario0")%></td>
				    <td class="txtSmallNegro">&nbsp;<%=modulo.getEmpresa()%></td>
				  </tr>
				  <tr>
				    <td class="txtNormalNegro"><%=rb.getString("lic.licencia0")%></td>
				    <%--<td class="txtSmallNegro">&nbsp;<%=Encryptor.usuarios%>&nbsp;&nbsp;<%=rb.getString("lic.licencia1")%></td> --%>
				    <td class="txtSmallNegro">&nbsp;<%=modulo.getUsuarios()%>&nbsp;&nbsp;<%=rb.getString("lic.licencia1")%>
				   <%-- 
				    <% String viewer = modulo.getViewers();
				    if(viewer!=null && !viewer.trim().equals("") && Integer.parseInt(viewer)>0) {%>
					    &nbsp;&nbsp;(<%=Integer.parseInt(viewer)>=1000000?"Unlimited":viewer%> Viewers)</td>
					<%}%>
					--%>
					
				  </tr>
				  <tr>
				    <td class="txtNormalNegro" styleClass="classText"><%=rb.getString("lic.tipo0")%></td>
				    <td class="txtSmallNegro">&nbsp;<%=rb.getString("lic.tipo1")%></td>
				    <!--  <td class="txtSmallNegro">&nbsp;<%=rb.getString("lic.tipo1")%> - <%=modulo.getEdicionFull()%></td>-->
				  </tr>
				  <tr>
				    <td class="txtNormalNegro" styleClass="classText">Activaci&oacute;n:</td>
				    <td class="txtSmallNegro">&nbsp;<%=modulo.getInicio()%></td>
				  </tr>
				  <tr>
				    <td class="txtNormalNegro" styleClass="classText">Caduca:</td>
				    <td class="txtSmallNegro">
				    	&nbsp;<%=modulo.getCaduca()%> <a href="#" onclick="verCuadroLicencia()" style="color:yellow;">Registrar Nueva Licencia</a>
				    </td>
				  </tr>
				  <tr>
				  	<td colspan="2">&nbsp;</td>
				  </tr>
				  <!-- 
				  <tr>
				    <td  class="txtNormalNegro" styleClass="classText">&nbsp;</td>
				    <td class="txtSmallNegro">&nbsp;<%=rb.getString("lic.caduca")%><%=rb.getString("lic.caduca1")%></td>
				  </tr>
				  
				  <tr>
				  	<td colspan="2">
						<div style="border:0px solid red;font-size:10px;color:#afafaf;text-align:center;"> 
							<%= application.getServerInfo() %> / jdk <%=System.getProperty("java.version")%> / vm <%=System.getProperty("java.vm.version")%>
							<br>
							<%=com.desige.webDocuments.persistent.managers.HandlerBD.getVersionSql()%>
						</div>
				  	</td>
				  </tr>
				   -->
				</table> 
			</td>
		</tr>
		<tr>
			<td colspan="2">&nbsp;
			</td>
		</tr>
		</table>
		<div id="fondo" style="position:absolute;display:none;">
		</div>
			<div id="fondo2" class="centerbox" style="display:none;width:712px;" >
				<div style="position:abolute;margin-top:-12px;height:23px;border:0px solid white;width:712px;text-align:left;background-color:white;">
					<table border="0" cellspacing="0" cellpadding="0"  style="padding-top:3px;">
						<tr>
							<td aling="left"><b style="font-family:Tahoma;font-size:10pt;">&nbsp;CONFORMACION DE LICENCIA - GESTION@Envio - versi&oacute;n <%=rb.getString("lic.version")%></b></td>
							<td align="right" style="display:none;"><a style="cursor:pointer;" onclick="javascript:document.getElementById('fondo').style.display='none';document.getElementById('fondo2').style.display='none';"><img src="./icons/cancel.png"></a>&nbsp;&nbsp;</td>
						</tr>
					</table>
				</div>
				<div id="error" style="padding:10px;background-color:#045FB4;">
					<table border="0" width="100%" >
						<tr>
							<td class="titleLeft left" nowrap>Nombre de la Empresa : </td>
							<td><input type="text" name="emp" size="70" value="<%=modulo.getEmpresa()%>" style="width:400px;"></td>
						</tr>
						<tr>
							<td class="titleLeft left">C&oacute;digo Identificaci&oacute;n Tributario : </td>
							<td><input type="text" name="rif" size="70" value="<%=modulo.getRif()%>" style="width:400px;"></td> 
						</tr>
						<tr>
							<td class="titleLeft left">C&oacute;digo de Instalaci&oacute;n: </td>
							<td><input type="text" name="mac" size="60" value="<%=NetworkInfo.getSerialMac()%>" style="background:transparent;border:1px solid white;color:white;font-weight:bold;width:400px;height:25px;" readonly></td>
						</tr>
						<tr>
							<td class="titleLeft left">N&uacute;mero de Licencia : </td>
							<td><input type="text" name="lic" size="70" value="<%=modulo.getLicencia()%>"  style="width:400px;"></td>
						</tr>
						<tr>
							<td class="titleLeft" colspan="2" style="padding-top:10;">
								<input type="button" value="Aceptar y Validar" onclick="conformar(true)">&nbsp;&nbsp;&nbsp;
								<input type="button" value="Modo de Prueba" onclick="continuar()">
								<input type="button" value="Modo Administrador" onclick="continuarAdmin()">
							</td>
						</tr>
						<!-- 
						<tr>
							<td class="titleLeft" colspan="2" style="padding-top:10;">
								Solicite el c&oacute;digo de licenciamiento a trav&eacute;s de<br>
								<a href="http://www.qwebcloud.com/index.php/licenciamiento" target="_blank" style="color:#ffffff;">
									http://www.qwebcloud.com/index.php/licenciamiento
								</a>
							</td>
						</tr>
						 -->
					</table>
				</div>
			</div>
	</td>
</tr>
</table>
</html:form>
<script>
	<%if(ToolsHTML.LICENCIA_GRACIA_VENCIDA){%>
		alertify.alert("LICENCIAMIENTO","El tiempo para solicitar la renovaci&oacute;n de licenciamiento para usuarios finales ha caducado.<br><br>Solo esta disponible el usuario administrador para operar el sistema");
	<%} else if(ToolsHTML.LICENCIA_VENCIDA) {%>
		alertify.alert("LICENCIAMIENTO","Es tiempo de solicitar la renovaci&oacuten de licenciamiento para usuarios finales.");
	<%}%>
	//conformar(false);
</script>
</body>
</html:html>
