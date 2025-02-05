<!-- Title: digitalRegister.jsp -->
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
				cad += "&aplicar=true";
				cad += "&lote=true";
				window.open('<%=basePath%>digitalizarLoad.do?'+cad,'scanner');
			}
		</script>
	</head>
<body class="bodyInternas" style="padding:20px;">
<form name="newDocument" style="margin:0;">
<table  border='0' width='100%' class="ventana" style="background-color:#b9c3cf;" cellpadding="0">
	<tr>
		<td style="background-color:#8f8f8f;color:white;padding:1px;font-family:Helvetica,Arial,Verdana;font-weight:bold;font-size:13;" colspan="2">
			Panel de ajuste para la digitalizaci&oacute;n de archivos
		</td> 
	</tr>
	<tr>
		<td width="60%"> 
			<table cellspacing='2' cellpadding='8' border='0' class='texto' width='100%'>
				<%--
				<tr>
					<td valign="middle">
						<b>Tipo de documento</b><br/>
		                <select name="typeDocuments" style="width:350px">
		                	<%for(int i=0;i<tipos.size();i++){
		                		tipo = (Search)tipos.get(i);%>
		                		<option value="<%=tipo.getId()%>"><%=tipo.getDescript()%></option>
		                	<%}%>
						</select>
					</td>
				</tr>
				--%>
				<tr>
					<td width="50%"> 
						<b>Modo</b><br/>
						<input type="radio" name="modo" value="0" <%=(modo==0?"Checked":"")%>>Blanco y Negro
						&nbsp;&nbsp;&nbsp;
						<input type="radio" name="modo" value="1" <%=(modo==1?"Checked":"")%>>Escala de Grises
						&nbsp;&nbsp;&nbsp;
						<input type="radio" name="modo" value="2" <%=(modo==2?"Checked":"")%>>Color
					</td>
				</tr>
				<tr>
					<td>
						<b>Puntos por Pulgada</b><br/>
						<select style="width:350px;" name="ppp"  value="<%=ppp%>">
							<option value="75" <%=ppp==75?"selected":""%>>75 ppp</option>
							<option value="100" <%=ppp==100?"selected":""%>>100 ppp</option>
							<option value="150" <%=ppp==150?"selected":""%>>150 ppp</option>
							<option value="200" <%=ppp==200?"selected":""%>>200 ppp</option>
							<option value="250" <%=ppp==250?"selected":""%>>250 ppp</option>
							<option value="300" <%=ppp==300?"selected":""%>>300 ppp</option>
							<option value="400" <%=ppp==400?"selected":""%>>400 ppp</option>
							<option value="600" <%=ppp==600?"selected":""%>>600 ppp</option>
						</select>
					</td>
				</tr>
				<tr>
					<td width="50%">
						<b>Lado de escaneado</b><br/>
						<input type="radio" name="lado" value="0" <%=(lado==0?"Checked":"")%>>Una cara
						&nbsp;&nbsp;&nbsp;
						<input type="radio" name="lado" value="1" <%=(lado==1?"Checked":"")%>>Doble cara
					</td>
				</tr>
				<tr>
					<td width="50%">
						<b>Separador de archivos</b><br/>
						<input type="radio" name="separar" value="1" <%=(separar==1?"Checked":"")%>>No separar
						&nbsp;&nbsp;&nbsp;
						<input type="radio" name="separar" value="2" <%=(separar==2?"Checked":"")%>>Por pagina
						&nbsp;&nbsp;&nbsp;
						<input type="radio" name="separar" value="3" <%=(separar==3?"Checked":"")%>>Por hoja
						&nbsp;&nbsp;&nbsp;
						<input type="radio" name="separar" value="4" <%=(separar==4?"Checked":"")%>>Pagina en blanco
					</td>
				</tr>
				<tr>
					<td width="50%">
						<b>Pagina en blanco</b><br/>
						<input type="radio" name="pagina" value="0" <%=(pagina==0?"Checked":"")%>>Conservar
						&nbsp;&nbsp;&nbsp;
						<input type="radio" name="pagina" value="1" <%=(pagina==1?"Checked":"")%>>Eliminar
					</td>
				</tr>
				<tr>
					<td width="50%">
						<b>Tama&ntilde;o de p&aacute;gina en blanco (aprox.)</b><br/>
						<input name="minimo" style="width:150px;" value="<%=minimo%>" onkeyup="this.value=this.value.replace(/[^0-9]/g,'')" maxlength="8">
						<span style="font-size:8pt;font-family:Helvetica,Arial,Verdana;">(770 sugerido)</span>
					</td>
				</tr>
				<tr>
					<td>
						<b>Mostrar panel del escaner</b><br/>
						<select style="width:350px;" name="panel" value="<%=panel%>">
							<option value="0" <%=panel==0?"selected":""%>>No</option>
							<option value="1" <%=panel==1?"selected":""%>>Si</option>
						</select>
					</td>
				</tr>
				<tr>
					<td valign="middle">
						&nbsp;
					</td>
				</tr>
				<tr>
					<td valign="middle" align="left">
						<input type="button" value="Aplicar configuraci&oacute;n" onclick="reloadScanner()"/>
					</td>
				</tr>
				<tr>
					<td valign="middle">
						&nbsp;
					</td>
				</tr>
				
			</table>
		</td>
		<td  width="40%" style="border:1px solid #efefef;" valign="top">
			<iframe name="scanner" id="scanner" src="digitalizarLote.jsp" style="margin:20;width:400;height:380;" FRAMEBORDER="0" SCROLLING="NO"></iframe>
		</td>
	</tr>
</table>
</form>
</body>
</html>
