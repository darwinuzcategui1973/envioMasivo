<%@ page import="java.util.Locale,java.util.ResourceBundle,com.desige.webDocuments.utils.ToolsHTML,com.desige.webDocuments.util.Encryptor,com.desige.webDocuments.persistent.managers.HandlerDBUser"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ page language="java"%>
<%
ResourceBundle rb = ToolsHTML.getBundle(request); 
StringBuffer onLoad = new StringBuffer(""); 
String error =(String)ToolsHTML.getAttributeSession(session,"error",true); 
if ((error!= null) && (error.compareTo("") != 0)) { 
	onLoad.append("onLoad=\"showMessage('").append(error).append("');linkActive();\"");
	session.removeAttribute("error"); 
} else {
	onLoad.append("onLoad=\"linkActive();\""); 
}
%>
<html:html xhtml="true" locale="true">
<head>
	<title><%=rb.getString("login.title")%>
	</title>
	<jsp:include page="meta.jsp" /> 
	<script language="JavaScript">
    function Aceptar(forma) {
	    if(forma.user.value!='' && forma.clave.value!='' && forma.user.value.length>0 && forma.clave.value.length>0){
	        if (forma.btnOk) {
	            forma.btnOk.disabled = true;
	        }
	        forma.submit();
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
        document.login.user.focus();
    }

</script>
	<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
	<style type="text/css">
<!--
.style3 {
	color: #efefef;
	font-size: 45;
	font-style: italic;
	font-weight: bold;
}
.style4 {
	font-size: 50px;
	color: #efefef;
	font-style: italic;
	font-weight: bold;
}
.style5 {
		color: #efefef;
		font-weight: bold;
}
.style7 {
	color: #efefef;
	font-size: 14px;
	font-weight: bold;
}
.botonFama{
	cursor: pointer;
	font-family: Verdana, Arial, Helvetica, sans-serif;
	font-weight:bold;
	font-size:9px;
	width: 102px;
	color: #000000;
	height: 20px;
	border: 0px outset #334F75;
	background-color: #FFFF00;
}
.style8 {
	color: #5E0E19;
	font-style: italic;
	font-weight: bold;
	font-size: 18;
}

.bodyInicFama {
	font-size: 12px;
	margin: 0px;
	color: #ffffff;
	font-family: Arial, Helvetica;
	background-color: #FFFFFF;
	background-image: url(img/fondo_vargas.jpg);
	background-repeat: no-repeat;
}
-->   
</style>
</head>
<body class="bodyInicFama" <%=onLoad.toString()%>>
	<table width="1097" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td height="40">
				&nbsp;
			</td>
		</tr>
		<tr>
			<td>
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td width="80px">
							&nbsp;
						</td>
						<td width="700px">
							<font face="Book Antiqua" style="font-size: 45px" color="#efefef">Q</font>
							<font face="Century Gothic" style="font-size: 40px" color="#dfdfdf">web</font>
							<font face="Century Gothic" style="font-size: 40px;font-weight: bold;color:#cfcfcf" >documents</font>
						</td>
						<td align="left">
							<img src="img/logoqweb2.gif">
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td align="right" height="5px">
				<br />
				<br />
				<br />
			</td>
		</tr>
		<tr>
			<td height="160">
				&nbsp;
			</td>
		</tr>
		<tr>
			<td>
				<table width="100%" border="0" >
				<tr>
				<td>
				<html:form action="/inicioSession.do">
					<input type="hidden" name="url"
						value="<%=session.getAttribute("url")%>" />
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td width="50">
								&nbsp;
							</td>
							<td>
								<span class="style7"> <%=rb.getString("login.user")%>
								</span>
							</td>
						</tr>
						<tr>
							<td>
								&nbsp;
							</td>
							<td>
								<html:text property="user" style="width: 160px"
									styleClass="classText"
									onkeypress="javascript:checkKey(event,this.form);" />
							</td>
						</tr>
						<tr>
							<td>
								&nbsp;
							</td>
							<td>
								<span class="style7"> <%=rb.getString("login.pass")%>
								</span>
							</td>
						</tr>
						<tr>
							<td>
								&nbsp;
							</td>
							<td>
								<html:password property="clave" style="width: 160px"
									styleClass="classText"
									onkeypress="javascript:checkKey(event,this.form);" />
							</td>
						</tr>
						<tr>
							<td>
								&nbsp;
							</td>
							<td height="30" valign="middle">
								<div align="center">
									<input type="button" class="botonFama" value="<%=rb.getString("btn.ok")%>" onClick="javascript:Aceptar(document.getElementById('login'));" name="btnOk" id="btnOk" />
								</div>
							</td>
						</tr>
					</table>
					
					</td>
					<td align="right">
					
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td colspan="3">
								<span class="style4"><!-- QwebDocuments -->&nbsp;</span>
							</td>
							<td width="200">
								&nbsp;
							</td>
						</tr>
						<tr>
							<td width="100">
								&nbsp;
							</td>
							<td>
								<span class="style5"><%=rb.getString("sch.version")%>:</span>
							</td>
							<td>
								&nbsp;
								<span class="style5">&nbsp;<%=rb.getString("lic.version")%></span> 
							</td>
							<td>
								&nbsp;
							</td>
						</tr>
						<tr>
							<td>
								&nbsp;
							</td>
							<td>
								<span class="style5"><%=rb.getString("lic.propietario0")%>
								</span>
							</td>
							<td>
								<span class="style5">&nbsp;<%=com.desige.webDocuments.utils.ToolsHTML.getLicenceOwner()%>
								</span>
							</td>
							<td>
								&nbsp;
							</td>
						</tr>
						<tr>
							<td>
								&nbsp;
							</td>
							<td>
								<span class="style5"><%=rb.getString("lic.licencia0")%>
								</span>
							</td>
							<td>
								<span class="style5">&nbsp;<%=HandlerDBUser.getNumeroDeUsuarios()%>&nbsp<%=rb.getString("lic.licencia1")%>
								</span>
							</td>
							<td>
								&nbsp;
							</td>
						</tr>
						<tr>
							<td>
								&nbsp;
							</td>
							<td>
								<span class="style5"><%=rb.getString("lic.tipo0")%>
								</span>
							</td>
							<td>
								<span class="style5">&nbsp;<%=rb.getString("lic.tipo1")%>
								</span>
							</td>
							<td>
								&nbsp;
							</td>
						</tr>
  						<tr>
							<td>
								&nbsp;
							</td>
							<td>
								<span class="style5"><%=rb.getString("lic.caduca")%>
								</span>
							</td>
							<td>
								<span class="style5">&nbsp;<%=rb.getString("lic.caduca1")%>
								</span>
							</td>
							<td>
								&nbsp;
							</td>  
  						</tr>						
					</table>

				</html:form>
				</td>
				</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td>
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td width="300">
							&nbsp;
						</td>
						<td>
							<span class="style8">&nbsp;</span>
						</td>
					</tr>
				</table>

			</td>
		</tr>
	</table>

</body>
</html:html>
