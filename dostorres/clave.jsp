<%@ page contentType="text/html; charset=iso-8859-1" language="java" import="java.sql.*,
                                                                             java.util.ResourceBundle,
                                                                             com.desige.webDocuments.utils.ToolsHTML,
                                                                             com.desige.webDocuments.utils.beans.Users,
                                                                             com.desige.webDocuments.users.forms.LoginUser,
                                                                             com.desige.webDocuments.utils.beans.SuperActionForm" errorPage="" %>
<!-- clave.jsp -->                                                                             

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    Users user = (Users)request.getSession().getAttribute("user");
    StringBuffer onLoad = new StringBuffer("");
    String ok = (String)session.getAttribute("usuario");
    String info = (String)request.getAttribute("info");
    if (ToolsHTML.checkValue(ok)) {
        onLoad.append(" onLoad=\"self.status='").append(rb.getString("pagin.welcome")).append(" ");
        onLoad.append(ok).append("'");
    } else{// El Usuario No se ha Logeado.... => Lo reboto...
        response.sendRedirect(rb.getString("href.logout"));
    }
    if (ToolsHTML.checkValue(info)){
        onLoad.append(";alert('").append(info).append("')");
    }
    if (onLoad.length()>0){
        onLoad.append("\"");
    }
    ToolsHTML.clearSession(session,"application.profile");
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script language="JavaScript">
    function salvar(){
   	String.prototype.trim = function() { return this.replace(/^\s*|\s*$/g,""); };
    document.perfil.clave.value=document.perfil.clave.value.trim();
    document.perfil.clavenueva.value=document.perfil.clavenueva.value.trim();
    if (validar(document.perfil)) { //1
        if  (document.perfil.clave.value==document.perfil.clavenueva.value) {//2
            if ((document.perfil.lengthPass.value!=null)||(document.perfil.lengthPass.value!='null')) { //3
                if (document.perfil.clave.value.length >= parseInt(document.perfil.lengthPass.value)){//4
                    document.perfil.submit();
                    //alert ("<%=rb.getString("clave.claveact")%>");
                    //location.href = "administracionMain.do";
                } else { //4
                    alert ("<%=rb.getString("clave.lenght")%> " + document.perfil.lengthPass.value);
                    location.href = "clave.do";
                }
            } else {//3
                //document.perfil.submit();
                alert ("<%=rb.getString("clave.claveact")%>");
                location.href = "administracionMain.do";
            }
        } else { //2
             alert ("<%=rb.getString("clave.clavedif")%>");
             location.href = "clave.do";
        }
    } else {//1
        alert ("<%=rb.getString("clave.clavevacia")%>");
        location.href = "clave.do";
        }
    }
    function validar(forma){
        if (forma.clave.value.length==0){
            return false;
        }
        return true;
    }
    function cancelar(){
        location.href = "administracionMain.do";
    }
    
    function isAllowedInPassword(event){
        var key = (window.Event) ? event.which : event.keyCode;
        var allowed = true;
        
        //solo aceptamos, letras numeros y espacios en blanco
        //alert(key);
        //32 espacio en blanco
        //34 comilla doble "
        //39 comilla simple '
        if(key == 32){
            allowed = false;
            //alert("<%= rb.getString("user.character.not.allowed")%>");
        }
        
        return allowed;
    }
</script>
</head>

<body class="bodyInternas" <%=onLoad.toString()%>>
<html:form action="editClave">
<html:hidden property="id"/>
<html:hidden property="lengthPass" />
<table width="100%" border="0">
    <tr>
        <td colspan="2" class="pagesTitle">
            <%=rb.getString("clave.title")%>
        </td>
    </tr>
    <tr>
        <td colspan="2">
            <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="0" align=center border=0>
                <tbody>
                    <tr>
                        <td class="td_title_bc" height="21">
                            <%=rb.getString("clave.title")%>
                        </td>
                    </tr>
                </tbody>
            </table>
        </td>
    </tr>
    <tr>
        <td height="22" class="titleLeft" style="background: url(img/btn140.png); background-repeat: no-repeat" width="16%" valign="middle">
            <%=rb.getString("clave.nombre")%>
        </td>
        <td class="td_gris_l">
            <bean:write name="perfil" property="user"/>
        </td>
    </tr>
    <tr>
        <td height="22" class="titleLeft" style="background: url(img/btn140.png); background-repeat: no-repeat" valign="middle">
            <%=rb.getString("clave.nueva")%>
        </td>
        <td>
            <html:password property="clave" size="20" styleClass="classText" onkeypress="return isAllowedInPassword(event);"/>
        </td>
    </tr>
    <tr>
        <td height="22" class="titleLeft" style="background: url(img/btn140.png); background-repeat: no-repeat" valign="middle">
            <%=rb.getString("clave.rep_nueva")%>
        </td>
        <td>
            <html:password property="clavenueva" size="20" styleClass="classText" onkeypress="return isAllowedInPassword(event);"/>
        </td>
    </tr>
    <tr>
        <td colspan="2" align="center">
            <input type="button" class="boton" value="<%=rb.getString("btn.save")%>" onClick="javascript:salvar();" />
            &nbsp;
            <input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>" onClick="javascript:cancelar();" />
        </td>
    </tr>
</table>
</html:form>
</body>
</html>
