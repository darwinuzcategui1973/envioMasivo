<%@ page import="com.focus.util.ContextBean" %>
<% ContextBean context = (ContextBean)request.getAttribute("context"); %>
<html>
<head>
<style>
.bodyInic {
	background-image: url(./img/fondoDosTorres.jpg);
	background-repeat: no-repeat;
	background-position: top center
}
.caja {
    width:600px;
    height:460px;
    position:absolute;
    left:50%;
    top:50%;
    margin:-220px 0 0 -290px;
    background:url(images/wizard.png) no-repeat;;
    font-family:Tahoma;
    font-size:13px;
}
.text {
    font-family:Tahoma;
    font-size:13px;
}
</style>
<script>
var pasoActual = 0;
var ultimoPaso = 0;
function pagina(paso) {
	for(var i=0; i<20; i++) {
		//alert('paso'+i));
		if(document.getElementById('paso'+i)) {
			document.getElementById('paso'+i).style.display="none";
			ultimoPaso = i;
		} else {
			break;
		}
	}
	with(document.forma){
		servidor.value = _servidor.value;
		database.value = _database.value;
		if(_manejador.value=='1'){
			driver.value='net.sourceforge.jtds.jdbc.Driver';
		} else {
			driver.value='org.postgresql.Driver';
		}
		puerto.value=_puerto.value;
		usuarioAdmin.value=_usuarioAdmin.value;
		passwordAdmin.value=_passwordAdmin.value;
	}

	if(paso==(ultimoPaso+1)){
		document.getElementById('paso'+ultimoPaso).style.display="";
		alert('Reinicie el servidor de aplicaciones tomcat y luego presione aceptar para finalizar la configuracion.');
		document.location.href='./inicio.do';
		return;
	} else {
		document.getElementById('paso'+paso).style.display="";
	}
}
function changePort(valor){
	with(document.forma){
		if(valor=='1'){
			driver.value='net.sourceforge.jtds.jdbc.Driver';
			_puerto.value=1433;
		} else {
			driver.value='org.postgresql.Driver';
			_puerto.value=5432;
		}
	}
}


function main() {
	
	with(document.forma) {
		_servidor.value = '<%=context.getServidor()%>';
		// <%=context.getDriverClassName() %>
		_manejador.value = <%=context.getDriverClassName().equals("org.postgresql.Driver")?"2":"1"%>
		_database.value = '<%=context.getDatabase()%>';
		usuario.value = '<%=context.getUsername()%>';
		password.value = '<%=context.getPassword()%>';
		puerto.value = '<%=context.getPort()%>';
		_puerto.value = '<%=context.getPort()%>';
	}
	
}

var http = null;
function probar() {
	if(navigator.appName == "Microsoft Internet Explorer") {
	  http = new ActiveXObject("Microsoft.XMLHTTP");
	} else {
	  http = new XMLHttpRequest();
	}

	with(document.forma){
		var cad = "?servidor="+servidor.value;
		cad += "&puerto="+puerto.value;
		cad += "&database="+database.value;
		cad += "&usuario="+usuario.value;
		cad += "&password="+password.value;
		cad += "&driver="+driver.value;
		cad += "&usuarioAdmin="+usuarioAdmin.value;
		cad += "&passwordAdmin="+passwordAdmin.value;
	}
	//alert("nuevaConexionAjax.do"+cad);
	http.open("POST", "nuevaConexionAjax.do"+cad);
	http.onreadystatechange=function() {
	  if(http.readyState == 4) {
	  	var resp = eval("("+http.responseText+")");
  		alert(resp.message);
	  }
	};
	
	http.send(null);    
}


</script>
</head>
<body class="bodyInic" onload="main()">
<form name="forma" style="margin:0px;padding:0px;">
<div class="caja" id="paso0" >
	<div style="border:0px solid blue;height:50px;">
	</div>
	<div style="border:0px solid blue;height:340px;padding-top:5px;padding-left:180px;padding-right:80px;">
		<b>No hay conexi&oacute;n con la base de datos</b>
		<br><br>
		<div style="text-align:justify;">
		Qwebdocuments no ha podido establecer una conexi&oacute;n con la base de datos, este asistente le
		permitira configurar facilmente el enlace entre la aplicaci&oacute;n y su fuente de datos.
		</div>
		<br>
		<div style="text-align:justify;">
		Antes de proseguir asegurese de tener el acceso al servidor
		asi como el usuario y la clave de administrador de la base de datos
		</div>
		<br>
		Al finalizar debera reiniciar el contenedor Tomcat para que el sistema tome los cambios.
	</div>
	<div style="border:0px solid blue;height:35px;text-align:right;padding-right:80px;padding-top:5px;">
		<input type="button" value="Siguiente" onclick="pagina(++pasoActual)"/>
	</div>
</div>
<div class="caja" id="paso1" style="display:none;">
	<div style="border:0px solid blue;height:50px;">
	</div>
	<div style="border:0px solid blue;height:340px;padding-top:5px;padding-left:180px;padding-right:80px;">
		<b>Asistente para una nueva conexi&oacute;n</b>
		<br>
		<br>
		<div style="text-align:justify;">
		Este asistente le guiara paso a paso para configurar una conexion a base de datos.
		Asegurese de tener instalado un manejador (Postgresql / MSSqlServer) de base de 
		datos compatible con la aplicaci&oacute;n
		</div><br><br>
		<b>Servidor:</b> <input type="text" name="_servidor" value="localhost" size="30" maxlength="30" />
		<div style="text-align:justify;">
		Ingrese el nombre del servidor o la direccion IP donde esta instalada la base de 
		datos. Esta es la maquina que contendra toda la informaci&oacute;n y sera utilizado
		para conectarse a trav&eacute;s de la aplicaci&oacute;n
		</div>
	</div>
	<div style="border:0px solid blue;height:35px;text-align:right;padding-right:80px;padding-top:5px;">
		<input type="button" value="Anterior" onclick="pagina(--pasoActual)"/>
		<input type="button" value="Siguiente" onclick="pagina(++pasoActual)"/>
	</div>
</div>
<div class="caja" id="paso2" style="display:none;">
	<div style="border:0px solid blue;height:50px;">
	</div>
	<div style="border:0px solid blue;height:340px;padding-top:5px;padding-left:180px;padding-right:80px;">
		<b>Seleccionar el driver de conexi&oacute;n</b>
		<br>
		<br>
		<div style="text-align:justify;">
		Qwebdocuments puede conectarse a diferentes bases de datos, asegurese de tener el manejador instalado y
		funcionando en su servidor.
		</div><br><br>
		<b>Tipo de Conexi&oacute;n:</b> 
			<select name="_manejador" style="width:200px;" onchange="changePort(this.value)" >
				<option value="1">SqlServer</option>
				<option value="2">Postgresql</option>
				<option value="3">MySql</option>
			</select><br>
		Seleccione el tipo de conexi&oacute;n que desea para el almacenamiento de datos.
	</div>
	<div style="border:0px solid blue;height:35px;text-align:right;padding-right:80px;padding-top:5px;">
		<input type="button" value="Anterior" onclick="pagina(--pasoActual)"/>
		<input type="button" value="Siguiente" onclick="pagina(++pasoActual)"/>
	</div>
</div>

<div class="caja" id="paso3" style="display:none;">
	<div style="border:0px solid blue;height:50px;">
	</div>
	<div style="border:0px solid blue;height:340px;padding-top:5px;padding-left:180px;padding-right:80px;">
		<b>Seleccionar el nombre de la base de datos</b>
		<br>
		<br>
		<div style="text-align:justify;">
		Consulte con el administrador de sistemas el nombre de la base de datos actual de qwebdocuments,
		en caso de no poseer base de datos, proporcione los datos del usuario administrador para crear una nueva.
		</div>
		<br>
		<b>Nombre de la base de datos:</b> 
			<input type="text" name="_database" value="qwebdocuments" style="width:250px;" />
		<div style="text-align:justify;">
		Ingrese el nombre que le desea dar a la base de datos donde se almacenar&aacute; la informacion documental.
		</div>
		<br>
		
	</div>
	<div style="border:0px solid blue;height:35px;text-align:right;padding-right:80px;padding-top:5px;">
		<input type="button" value="Anterior" onclick="pagina(--pasoActual)"/>
		<input type="button" value="Siguiente" onclick="pagina(++pasoActual)"/>
	</div>
</div>

<div class="caja" id="paso4" style="display:none;">
	<div style="border:0px solid blue;height:50px;">
	</div>
	<div style="border:0px solid blue;height:340px;padding-top:5px;padding-left:180px;padding-right:80px;">
		<b>Usuario administrador de base de datos</b>
		<br>
		<br>
		<div style="text-align:justify;">
		En caso de ser una base de datos nueva, debe inidicar  la clave del usuario administrador de la base de datos,
		comuniquese con el administrador del sistemas para que le suministre esta informaci&oacute;n.
		</div>
		<br>
		<br>
		<div>
		<b style="width:80px">Usuario :</b> 
			<input type="text" name="_usuarioAdmin" value="" style="width:150px;" /><br>
		</div>
		<div>
		<b style="width:80px">Password :</b> 
			<input type="password" name="_passwordAdmin" value="" style="width:150px;" /><br>
		</div>
		
	</div>
	<div style="border:0px solid blue;height:35px;text-align:right;padding-right:80px;padding-top:5px;">
		<input type="button" value="Anterior" onclick="pagina(--pasoActual)"/>
		<input type="button" value="Siguiente" onclick="pagina(++pasoActual)"/>
	</div>
</div>

<div class="caja" id="paso5" style="display:none;">
	<div style="border:0px solid blue;height:50px;">
	</div>
	<div style="border:0px solid blue;height:340px;padding-top:5px;padding-left:180px;padding-right:80px;">
		<b>Puerto de escucha de base de datos</b>
		<br>
		<br>
		<div style="text-align:justify;">
		El sistema se comunica con la base de datos ubicada en el servidor a traves de un puerto especifico.
		El puerto es un numero y debe ser especificado correctamente.
		</div><br>
		<b>Puerto de base de datos:</b> 
			<input type="text" name="_puerto" value="" style="width:250px;" /><br>
		Ingrese el puerto exacto por donde escucha la base de datos. Pregunte a su administrador de sistemas.
		<br>
		<br>
		
	</div>
	<div style="border:0px solid blue;height:35px;text-align:right;padding-right:80px;padding-top:5px;">
		<input type="button" value="Anterior" onclick="pagina(--pasoActual)"/>
		<input type="button" value="Siguiente" onclick="pagina(++pasoActual)"/>
	</div>
</div>

<div class="caja" id="paso6" style="display:none;">
	<div style="border:0px solid blue;height:50px;">
	</div>
	<div style="border:0px solid blue;height:340px;padding-top:5px;padding-left:180px;padding-right:80px;">
		<div style="padding-bottom:5px;"><b>Probar la conexi&oacute;n</b></div>
		Ingrese en puerto de escucha de la base de datos, en caso de no ser el puerto por defecto.
		<br><br>
		<table border="0" width="350">
			<tr>
				<td class="text" width="100px;">Servidor:</td>
				<td>
					<input type="text" name="servidor" value="localhost" size="13" />
					&nbsp;&nbsp;&nbsp;<span class="text">Puerto:<input type="text" name="puerto" size="5" />
				</td>
			</tr>
			<tr>
				<td class="text" width="100px;">Base de datos:</td><td><input type="text" name="database" size="35" /></td>
			</tr>
			<tr>
				<td class="text" width="100px;">Usuario:</td><td><input type="text" name="usuario" size="35"  /></td>
			</tr>
			<tr>
				<td class="text" width="100px;">Clave:</td><td><input type="password" name="password" size="35"  /></td>
			</tr>
			<tr>
				<td class="text" width="100px;">Driver:</td><td><input type="text" name="driver" size="35"  /></td>
			</tr>
			<tr>
				<td class="text" colspan="2" valign="bottom" align="center" style="padding-top:15px;"><b>Usuario administrador de base de datos</b></td>
			</tr>
			<tr>
				<td class="text" width="100px;">Usuario (admin):</td><td><input type="text" name="usuarioAdmin" size="35"  /></td>
			</tr>
			<tr>
				<td class="text" width="100px;">Clave (admin):</td><td><input type="password" name="passwordAdmin" size="35"  /></td>
			</tr>
			<tr>
				<td align="center" colspan="2">
					<input type="button" value="Probar la conexi&oacute;n" onclick="probar()"/>
				</td>
			</tr>
		</table>
	</div>
	<div style="border:0px solid blue;height:35px;text-align:right;padding-right:80px;padding-top:5px;">
		<input type="button" value="Anterior" onclick="pagina(--pasoActual)"/>
		<input type="button" value="Finalizar" onclick="pagina(++pasoActual)"/>
	</div>
</div>
</form>
</body>
</html>