<!-- /**
 * Title: seguridadUser.jsp<br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @author Ing. Simon Rodríguez (SR)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li>21/04/2006 (NC) Add field search in the security profile </li>
 </ul>
 */ -->
<%@ page contentType="text/html; charset=iso-8859-1" language="java"
 import="java.sql.*,
         java.util.ResourceBundle,
         com.desige.webDocuments.utils.ToolsHTML,
         com.desige.webDocuments.utils.beans.SuperActionForm,
         com.desige.webDocuments.seguridad.forms.SeguridadUserForm" errorPage="" %>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    String onLoad = ToolsHTML.getMensajePages(request,rb);
    if (onLoad==null){
        response.sendRedirect(rb.getString("href.logout"));
    }
    String cmd = (String)session.getAttribute("cmd");
    if (cmd==null){
        cmd = SuperActionForm.cmdLoad;
    }
    pageContext.setAttribute("cmd",cmd);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css"/>
<script language="JavaScript">
function validar(forma){
<%--      if (forma.nombreGrupo.value.length==0 || forma.descripcionGrupo.value.length==0){--%>
<%--         return false;--%>
<%--        }else{--%>
		    return true;
<%--        }--%>
	}
    function salvar(forma){
        if (validar(forma)){
           forma.submit();
           //alert("<%=rb.getString("app.editOk")%>");
        }
        else{
        alert("<%=rb.getString("user.mensaje")%>");
        }
    }
    function cancelar(form){
        location.href = "seguridadLista.jsp";
    }
    function editField(pages,input,value,forma){
        var hWnd = null;
        hWnd = window.open(pages+"?input="+input+"&value="+value+"&nameForma="+forma.name,"Catalogo","width=400,height=300,resizable=yes,scrollbars=yes,left=300,top=250");
        if ((document.window != null) && (!hWnd.opener)){
            hWnd.opener = document.window;
        }
    }
    function youAreSure(form){
      if (confirm("<%=rb.getString("areYouSure")%>")) {
         form.cmd.value = "<%=SuperActionForm.cmdDelete%>";
         form.submit();
      }
    }
</script>
</head>

<body class="bodyInternas">
<html:form action="/editSeguridad.do">
<html:hidden property="cmd" />
<table width="100%" border="0">
    <tr>
        <td colspan="2" class="pagesTitle">
            <%=rb.getString("seguridad.title")%>
        </td>
    </tr>
    <tr>
        <td colspan="2">
            <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="0" align=center border=0>
                <tbody>
                    <tr>
                        <td class="td_title_bc" height="21">
                            <%=rb.getString("seguridad.nombre")%>
                        </td>
                    </tr>
                </tbody>
            </table>
        </td>
    </tr>
    <tr>
        <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" width="18%" height="26" valign="middle">
            <%=rb.getString("seguridad.nombre")%>
        </td>
        <td width="*" class="td_gris_l">
            <bean:define id="data" name="SeguridadForm" type="com.desige.webDocuments.seguridad.forms.SeguridadUserForm" scope="session" />
            <bean:write name="SeguridadForm" property="nameUser"/>
        </td>
    </tr>
    <tr>
        <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" height="26" valign="middle">
            <%=rb.getString("seguridad.email")%>
        </td>
        <td class="td_gris_l">
            <%//System.out.println(data.getMail());%>
            <bean:write name="SeguridadForm" property="mail"/>
        </td>
    </tr>
    <tr>
        <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" height="26" valign="middle">
            <%=rb.getString("seguridad.grupo")%>
        </td>
        <td class="td_gris_l">
            <bean:write name="SeguridadForm" property="nombreGrupo"/>
        </td>
    </tr>
</table>
<table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="0" align=center border=0>
    <tbody>
        <tr>
            <td class="td_title_bc" height="21">
                <%=rb.getString("seguridad.title")%>
            </td>
        </tr>
    </tbody>
</table>
<table width="75%" border="0">
    <tr class="td_vino">
        <td width="22%" align="center" class="txtTitleTable">
            <%=rb.getString("seguridad.permiso")%>
        </td>
        <td width="25%" align="center" class="txtTitleTable">
            <%=rb.getString("seguridad.permitir")%>
        </td>
        <td width="25%" class="txtTitleTable">
            <%=rb.getString("seguridad.negar")%>
        </td>
        <td width="*" align="center" class="txtTitleTable">
            <%=rb.getString("seguridad.heredar")%>
        </td>
    </tr>
    <tr>
        <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" height="26" valign="middle">
            <%=rb.getString("seguridad.estructura")%>
        </td>
        <td align= "center">
            <%=ToolsHTML.getRadioButton("estructura",data.getEstructura(),0,null)%>
        </td>
        <td align= "center">
            <%=ToolsHTML.getRadioButton("estructura",data.getEstructura(),1,null)%>
        </td>
        <td align= "center">
            <%=ToolsHTML.getRadioButton("estructura",data.getEstructura(),2,null)%>
        </td>
    </tr>
    <%if (ToolsHTML.showFlujo()){ %>
    <tr>
        <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" height="26" valign="middle">
            <%=rb.getString("seguridad.flujo")%>
        </td>
        <td align= "center">
            <%=ToolsHTML.getRadioButton("flujos",data.getFlujos(),0,null)%>
        </td>
        <td align= "center">
            <%=ToolsHTML.getRadioButton("flujos",data.getFlujos(),1,null)%>
        </td>
        <td align= "center">
            <%=ToolsHTML.getRadioButton("flujos",data.getFlujos(),2,null)%>
        </td>
    </tr>
    <%}%>
    <%if (ToolsHTML.showFiles()){ %>
    <tr>
        <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" height="26" valign="middle">
            <%=rb.getString("seguridad.files")%>
        </td>
        <td align= "center">
            <%=ToolsHTML.getRadioButton("files",data.getFiles(),0,null)%>
        </td>
        <td align= "center">
            <%=ToolsHTML.getRadioButton("files",data.getFiles(),1,null)%>
        </td>
        <td align= "center">
            <%=ToolsHTML.getRadioButton("files",data.getFiles(),2,null)%>
        </td>
    </tr>
    <%} else {%>
        <input type="hidden" name="files" id="files" value="0" />
    <%}%>
    
    <tr>
        <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" height="26" valign="middle">
            <%=rb.getString("seguridad.mensaje")%>
        </td>
        <td align= "center">
            <%=ToolsHTML.getRadioButton("mensajes",data.getMensajes(),0,null)%>
        </td>
        <td align= "center">
            <%=ToolsHTML.getRadioButton("mensajes",data.getMensajes(),1,null)%>
        </td>
        <td align= "center">
            <%=ToolsHTML.getRadioButton("mensajes",data.getMensajes(),2,null)%>
        </td>
    </tr>
    <tr>
        <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" height="26" valign="middle">
            <%=rb.getString("seguridad.administracion")%>
        </td>
        <td align= "center">
            <%=ToolsHTML.getRadioButton("administracion",data.getAdministracion(),0,null)%>
        </td>
        <td align= "center">
            <%=ToolsHTML.getRadioButton("administracion",data.getAdministracion(),1,null)%>
        </td>
        <td align= "center">
            <%=ToolsHTML.getRadioButton("administracion",data.getAdministracion(),2,null)%>
        </td>
    </tr>
    <tr>
        <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" height="26" valign="middle">
            <%=rb.getString("seguridad.search")%>
        </td>
        <td align= "center">
            <%=ToolsHTML.getRadioButton("search",data.getSearch(),0,null)%>
        </td>
        <td align= "center">
            <%=ToolsHTML.getRadioButton("search",data.getSearch(),1,null)%>
        </td>
        <td align= "center">
            <%=ToolsHTML.getRadioButton("search",data.getSearch(),2,null)%>
        </td>
    </tr>
    <tr>
        <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" height="26" valign="middle">
            <%=rb.getString("seguridad.perfil")%>
        </td>
        <td align= "center">
            <%=ToolsHTML.getRadioButton("perfil",data.getPerfil(),0,null)%>
        </td>
        <td align= "center">
            <%=ToolsHTML.getRadioButton("perfil",data.getPerfil(),1,null)%>
        </td>
        <td align= "center">
            <%=ToolsHTML.getRadioButton("perfil",data.getPerfil(),2,null)%>
                    </td>
                </tr>

                <tr>
                    <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" height="26" valign="middle">
                        <%=rb.getString("seguridad.impresion")%> 
                    </td>
                    <td align= "center">
                        <%=ToolsHTML.getRadioButton("toImpresion",data.getToImpresion(),0,null)%>
                    </td>
                    <td align= "center">
                        <%=ToolsHTML.getRadioButton("toImpresion",data.getToImpresion(),1,null)%>
                    </td>
                    <td align= "center">
                        <%=ToolsHTML.getRadioButton("toImpresion",data.getToImpresion(),2,null)%>
                    </td>
                </tr>
    <%if (ToolsHTML.showSACOP()){ %>
        <tr>
           <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" height="26" valign="middle">
                        <%=rb.getString("scp.sacop")%>
           </td>
           <td align= "center">
                        <%=ToolsHTML.getRadioButton("sacop",data.getSacop(),0,null)%>
           </td>
           <td align= "center">
                        <%=ToolsHTML.getRadioButton("sacop",data.getSacop(),1,null)%>
           </td>
           <td align= "center">
                         <%=ToolsHTML.getRadioButton("sacop",data.getSacop(),2,null)%>
           </td>
        </tr>
    <%} else {%>
        <input type="hidden" name="sacop" id="sacop" value="0" />
    <%}%>

    <%if (ToolsHTML.showRecord()){ %>
        <tr>
           <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" height="26" valign="middle">
                        <%=rb.getString("seguridad.record")%>
           </td>
           <td align= "center">
                        <%=ToolsHTML.getRadioButton("record",data.getRecord(),0,null)%>
           </td>
           <td align= "center">
                        <%=ToolsHTML.getRadioButton("record",data.getRecord(),1,null)%>
           </td>
           <td align= "center">
                         <%=ToolsHTML.getRadioButton("record",data.getRecord(),2,null)%>
           </td>
        </tr>
    <%} else {%>
        <input type="hidden" name="record" id="record" value="0" />
    <%}%>
    
    <%if (ToolsHTML.showDigital()){ %>
    <tr>
        <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" height="26" valign="middle">
            <%=rb.getString("seguridad.digital")%>
        </td>
        <td align= "center">
            <%=ToolsHTML.getRadioButton("digital",data.getDigital(),0,null)%>
        </td>
        <td align= "center">
            <%=ToolsHTML.getRadioButton("digital",data.getDigital(),1,null)%>
        </td>
        <td align= "center">
            <%=ToolsHTML.getRadioButton("digital",data.getDigital(),2,null)%>
        </td>
    </tr>
    <%} else {%>
        <input type="hidden" name="digital" id="digital" value="0" />
    <%}%>

   <tr>
        <td colspan="4" align="center">
            <input type="button" class="boton" value="<%=rb.getString("btn.save")%>" onClick="javascript:salvar(this.form);" />
            <input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>" onClick="javascript:cancelar(this.form);"/>
        </td>
    </tr>
</table>
</html:form>
</body>
</html>
