<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 sun.jdbc.rowset.CachedRowSet"
            	%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%
	ResourceBundle rb = ToolsHTML.getBundle(request);
	String color="";
	CachedRowSet listUser = (CachedRowSet) request.getAttribute("listUser");
	CachedRowSet listArea = (CachedRowSet) request.getAttribute("listArea");
%>
<html>
<head>
	<title>Lista de Usuarios Qweb</title>
	<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
	<script>
	function aceptar() {
		var cad="";
		var sep="";
		with(document.forms[0]) {
			for(var i=0;i<userName.length;i++) {
				if(userName[i].checked) {
					cad=cad+sep;
					cad=cad+userName[i].value
					sep=",";
				}
			}
		}
		try {
			window.opener.openUserReturn(cad);
			window.close();
		} catch(e) {
		}
	}
	function checkUser() {
		var listUser=window.opener.getListUser();
		var lista = listUser.split(',');
		with(document.forms[0]) {
			for(var i=0;i<userName.length;i++) {
				for(var k=0; k<lista.length; k++) {
					if(userName[i].value==lista[k]) {
						userName[i].checked=true;
					}
				}
			}
		}
	}
	
	function filtrar(id) {
		var obj = document.getElementsByTagName("tr");
		var color="";
		for(var i=1; i<obj.length-1; i++) {
			obj[i].style.display="none";
			if(id==-1 || obj[i].id==id){
				color=color=="#efefef"?"#d4d4d4":"#efefef";
				obj[i].style.display="";
				
				var tds = obj[i].getElementsByTagName("td");
				for(var k=0; k<tds.length; k++ ) {
					tds[k].style.backgroundColor=color;
				}
			}
		}
		document.forma.todo.checked=false;
	}
	function marcar(isActivo) {
		var id = document.forma.listaArea.value;
		var obj = document.getElementsByTagName("tr");
		var color="";
		for(var i=1; i<obj.length-1; i++) {
			if(id==-1 || obj[i].id==id){
				var chk = obj[i].getElementsByTagName("input");
				chk[0].checked=isActivo;
			}
		}
	}
	
	</script>
</head>
<body class="bodyInternas" style="margin:0;" onload="checkUser()">
<form name='forma' style="margin:0;">
	<div class="titleBlack">USUARIOS</div>
	<div class="titleBlack">Area : 
		<select name="listaArea" style="width:300;" onchange="filtrar(this.value)">
			<option value="-1">Todos</option>
        <%while(listArea.next()){%>
        	<option value="<%=listArea.getString("idArea")%>"><%=listArea.getString("area")%></option>
        <%}%>
		</select>
	</div>
<table width="100%">
	<tr id="0">
		<td class="td_titulo_C" style="border:1px solid #afafaf;" width="30%">
			<input name="todo" type="checkbox" value="0" onclick="marcar(this.checked);">
			Usuario
		</td>
		<td class="td_titulo_C"  style="border:1px solid #afafaf;">
			Nombre
		</td>
	</tr>
    <%if(listUser!=null){%>
        <%while(listUser.next()){
	        color=color.equals("#efefef")?"#d4d4d4":"#efefef";%>
			<tr id="<%=listUser.getString("idArea")%>">
				<td width="30%" class="td_gris_l" style="background-color:<%=color%>;" >
					<input id="<%=listUser.getString("nameUser")%>" name="userName" type="checkbox" value="<%=listUser.getString("nameUser")%>"><label for="<%=listUser.getString("nameUser")%>"><%=listUser.getString("nameUser")%></label>
				</td>
		
				<td width="70%" class="td_gris_l" style="background-color:<%=color%>">
					<label for="<%=listUser.getString("nameUser")%>"><%=listUser.getString("apellidos").concat(" ").concat(listUser.getString("nombres"))%></label>
					<%=listUser.getString("area")%>
				</td>
			</tr>
        <%}%>
    <%}%>
	<td align="center" colspan="2">
		<input type="button" class="boton" value="<%=rb.getString("btn.ok")%>" onClick="aceptar(this.form);"/>
		&nbsp;
		<input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>" onClick="window.close()"/>
	</td>

</table>
</form>
</body>
</html>