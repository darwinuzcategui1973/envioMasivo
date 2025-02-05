 <%@ page import="java.util.Locale,
                 java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,com.desige.webDocuments.util.Encryptor,
                 com.desige.webDocuments.persistent.managers.HandlerDBUser,
                 com.desige.webDocuments.utils.ToolsHTML"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %> 
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>  
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
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
	font-family:Helvetica,Arial,Verdana,Serif;
	width: 160px;
	background-color:transparent;
	border:1px solid #cfcfcf;
	font-size:14pt;
	padding:2px;
	color:white;
	/*background:url(images/login.gif);*/
	font-weight:normal;
}
</style>
<script language="JavaScript">
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
	    <%if(request.getAttribute("logout")!=null && String.valueOf(request.getAttribute("logout")).equals("true")){%>
	        entrar();
	    <%}%>
        document.login.user.focus();
    }
	function open_full(dir,name){
		var Ancho=screen.availWidth-10;
		var Alto=screen.availHeight-28;
		var features="toolbar=no,status=yes,location=no,directories=no,menubar=no,scrollbars=yes,top=0,left=0,width="+Ancho+",height="+Alto+'"';
		myWindow = window.open(dir,name,features);
	}    
	function cerrarSession() {
		document.location.href = "logout.do?pag=1";
	}
</script>
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<style>
.tableInic {
	background-image: url(img/fondo_fuller.jpg);
	background-repeat: no-repeat;
	background-position: top center;
}
</style>
</head>
<!--
<body class="bodyInic" <%=onLoad.toString()%>>
-->
<body <%=onLoad.toString()%> bgcolor="white">
<html:form action="/inicioSession.do" onsubmit="return false;">
<input type="hidden" name="url" value="<%=session.getAttribute("url")%>"/>
<input type="hidden" name="logout" value="false"/>
<table width="100%" height="100%" align="center" border="0" class="tableInic">
<tr>
	<td>
		<table width="1000" height="100%" align="center" border="0">
		<tr>
			<td colspan="3" height="500">&nbsp;
			</td>
		</tr>
		<tr>
			<td width="25%" height="130">
		        <table width="165" align=center border="0">
		            <tr>
		                <td align="left" class="txt" style="color:white;">
		                    <%=rb.getString("login.user")%>
		                </td>
		            </tr>
		            <tr>
		                <td align="left">
		                    <html:text property="user" styleClass="login"/>
		                </td>
		            </tr>
		            <tr>
		                <td align="left" class="txt"  style="color:white;">
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
			<td width="35%" align="center">&nbsp;
			</td>
			<td width="40%" align="center">
				<table border="0" align="center" cellspacing="1" cellpadding="0" >
				  <tr>
				    <td class="txtNormal" width="80" style="color:white;"><%=rb.getString("sch.version")%>:</td>
				    <td class="txtSmall" style="color:white;font-weight:bold;;">&nbsp;<%=rb.getString("lic.version")%></td>
				  </tr>
				  <tr>
				    <td class="txtNormal" styleClass="classText" style="color:white;"><%=rb.getString("lic.propietario0")%></td>
				    <td class="txtSmall" style="color:white;font-weight:bold;;">&nbsp;<%=com.desige.webDocuments.utils.ToolsHTML.getLicenceOwner()%></td>
				  </tr>
				  <tr>
				    <td class="txtNormal" style="color:white;"><%=rb.getString("lic.licencia0")%></td>
				    <%--<td class="txtSmall">&nbsp;<%=Encryptor.usuarios%>&nbsp;&nbsp;<%=rb.getString("lic.licencia1")%></td> --%>
				    <td class="txtSmall" style="color:white;font-weight:bold;;">&nbsp;<%=HandlerDBUser.getNumeroDeUsuarios()%>&nbsp;&nbsp;<%=rb.getString("lic.licencia1")%>
				    <% String viewer = HandlerDBUser.getNumeroDeUsuariosViewer();
				    if(viewer!=null && !viewer.trim().equals("") && Integer.parseInt(viewer)>0) {%>
					    &nbsp;&nbsp;(<%=Integer.parseInt(viewer)>=1000000?"Unlimited":viewer%> Viewers)</td>
					<%}%>
				  </tr>
				  <tr>
				    <td class="txtNormal" styleClass="classText" style="color:white;"><%=rb.getString("lic.tipo0")%></td>
				    <td class="txtSmall" style="color:white;font-weight:bold;">&nbsp;<%=rb.getString("lic.tipo1")%></td>
				  </tr>
				  <tr>
				    <td  class="txtNormal" styleClass="classText" style="color:brown;">&nbsp;</td>
				    <td class="txtSmall"  style="color:white;font-weight:bold;;">&nbsp;<%=rb.getString("lic.caduca")%><%=rb.getString("lic.caduca1")%></td>
				  </tr>
				</table>
			</td>
		</tr>
		<tr>
			<td colspan="3">&nbsp;
			</td>
		</tr>
		</table>
	</td>
</tr>
</table>
</html:form>
</body>
</html:html>
