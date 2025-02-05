<!-- /**
 * Title: seguridadLista.jsp<br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @author Ing. Simon Rodríguez (SR)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/>
 * <ul>
  *      <li> 30/06/2006 (NC) Se quitó espacio en Blanco en Vínculo </li>
  </ul>
 */ -->
<%@ page contentType="text/html; charset=iso-8859-1" language="java"
           import="java.util.ResourceBundle,
           com.desige.webDocuments.utils.ToolsHTML,
                   com.desige.webDocuments.utils.beans.SuperActionForm,
                   java.util.Collection,
                   com.desige.webDocuments.persistent.managers.HandlerGrupo" %>
<%@ page import="com.desige.webDocuments.persistent.managers.HandlerDBUser" %>
<%@ page import="java.util.Vector" %>
<%@ page import="com.desige.webDocuments.seguridad.forms.SeguridadUserForm" %>
<%@ page import="com.desige.webDocuments.persistent.managers.HandlerBD" %>
<%@ page import="com.desige.webDocuments.utils.DesigeConf" %>
<%@ page import="com.desige.webDocuments.utils.beans.Search3" %>
<%@ page import="com.desige.webDocuments.utils.beans.Users" %>
<%@ page import="java.util.*" %>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    String onLoad = ToolsHTML.getMensajePages(request, rb);
    String valorSeleccionado = (request.getAttribute("grupo")!=null?(String)request.getAttribute("grupo"):request.getParameter("grupo"));
    String toSearch = request.getParameter("toSearch");
    if (toSearch != null) {
        Vector users = (Vector) HandlerDBUser.getAllUsersForGrups(toSearch, true);
        if (users != null && users.size() > 0) {
            SeguridadUserForm frm = (SeguridadUserForm)users.get(0);
            valorSeleccionado = frm.getIdGrupo();
        }
    }
    String descGroup = "";
    String id = "";
    if (onLoad == null) {
        response.sendRedirect(rb.getString("href.logout"));
    }
    String cmd = (String) session.getAttribute("cmd");
    if (cmd == null) {
        cmd = SuperActionForm.cmdLoad;
    }
    pageContext.setAttribute("cmd", cmd);
%>
<html>
<head>
<title></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script language="JavaScript">
	function validar() {
        if (document.login.docSearch.value.length>0) {
            alert('<%=rb.getString("err.notDocumentSearch")%>');
        } else {
		    document.login.submit();
        }
	}

    function edit(num) {
        forma = document.user;
        forma.id.value = num;
        forma.cmd.value == '<%=SuperActionForm.cmdEdit%>';
        forma.submit();
    }

    function salvar(forma) {
        forma.submit();
    }

    function cancelar(form) {
        location.href = "administracionMain.do";
    }

    function editField(pages,input,value,forma){
        var hWnd = null;
        hWnd = window.open(pages+"?input="+input+"&value="+value+"&nameForma="+forma.name,"Catalogo","width=400,height=300,resizable=yes,scrollbars=yes,left=300,top=250");
        if ((document.window != null) && (!hWnd.opener)){
            hWnd.opener = document.window;
        }
    }

    function updateCheck(check,field) {
        if (check.checked){
            field.value = "0";
        } else{
            field.value = 1;
        }
    }

    function refreshButton(forma){
        forma.lengthDocNumber.disabled = !forma.docNumberGenerator[1].checked;
        forma.resetDocNumber.disabled = !forma.docNumberGenerator[1].checked;
        if (forma.docNumberGenerator[1].checked) {
            forma.lengthDocNumber.focus();
        }
    }
    function getDatos() {
        document.newUser.action = "seguridadLista.jsp";
        document.newUser.submit();
        document.newUser.grupo.value;
    }

    function buscar(forma) {
        forma.submit();
    }
   
    function checkKey(evt,forma) {
        var charCode = (evt.which) ? ect.which : event.keyCode;
        if (charCode == 13) {
            forma.submit();
        }
        return true;
    }

    

</script>
</head>

<body class="bodyInternas" <%=onLoad.toString()%>>

<logic:present name="desGrupos" scope="session" >
<form name="search" action="seguridadLista.jsp" method="post">
    <table cellSpacing=0 cellPadding=0 align=center border=0 width="80%">
        <tr>
	        <td class="td_title_bc" colspan="3">
	            <input type="text" name="toSearch" onkeypress="javascript:checkKey(event,this.form);" />
	            &nbsp;
	            <input type="button" class="boton" value="<%=rb.getString("btn.search")%>" onClick="javascript:buscar(this.form);"/>
	        </td>
	    </tr>
    </table>
</form>
<html:form action="/segUserGrupo.do">
<html:hidden property="cmd" value="<%=SuperActionForm.cmdInsert%>"/>
<table cellSpacing=0 cellPadding=2 width="100%" border="0" align="center">
    <tr>
        <td colspan="2" class="pagesTitle">
            <%=rb.getString("lista.title")%>
        </td>
    </tr>
    <tr>
        <td colspan="2">
            <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="0" align=center border=0>
                <tbody>
                    <tr>
                        <td class="td_title_bc" height="21">
                            <%=rb.getString("segur.title1")%>
                        </td>
                    </tr>
                </tbody>
            </table>
        </td>
    </tr>
    <tr>
        <td class="td_gris_l" width="40%">
            <%=rb.getString("segur.nombregrupo")%>
        </td>
        <td class="td_gris_l">
            <%=rb.getString("segur.option")%>
        </td>
    </tr>
    <tr>
        <td>
            <html:select onchange="getDatos();" property="grupo" >
                <logic:iterate id="idGrupos" name="desGrupos" scope="session" type="com.desige.webDocuments.utils.beans.Search3">
                    <% if (idGrupos.getId().equalsIgnoreCase(valorSeleccionado)) { %>
                        <option value="<%=idGrupos.getId()%>" selected><%=idGrupos.getName()%></option>
                    <%
                        descGroup = idGrupos.getDescript();
                        id = idGrupos.getId();
                    } else {
                        if (valorSeleccionado==null) {
                            valorSeleccionado = idGrupos.getId();
                            descGroup = idGrupos.getDescript();
                            id = idGrupos.getId();
                        }
                    %>
                        <option value="<%=idGrupos.getId()%>"><%=idGrupos.getName()%></option>
                    <% }%>
                </logic:iterate>
            </html:select>
        </td>
        <td>
           	<%if(valorSeleccionado.equals(com.desige.webDocuments.utils.Constants.ID_GROUP_VIEWER)){%>
                <%=rb.getString("lista.seguridad")%>
            <%} else {%>
            <a href="editSeguridadGrupo.do?cmd=<%=SuperActionForm.cmdLoad%>&idGrupo= <%=id%>">
                <%=rb.getString("lista.seguridad")%>
            </a>
            <%}%>
        </td>
    <tr>
        <td>
        </td>
    </tr>
    <tr>
        <td><div align="left"></div></td>
        <td><div align="left"></div></td>
    </tr>

    <tr>
        <td colspan="2">
            <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="0" align=center border=0>
                <tbody>
                    <tr>
                        <td class="td_title_bc" height="21">
                            <%=rb.getString("segur.title2")%>
                        </td>
                    </tr>
                </tbody>
            </table>
        </td>
    </tr>
    <tr>
        <td class="td_gris_l">
            <%=rb.getString("segur.nombreuser")%>
        </td>
        <td class="td_gris_l">
            <%=rb.getString("segur.option")%>
        </td>
    </tr>
    <tr>
        <td><div align="left"></div></td>
        <td><div align="left"></div></td>
    </tr>
         <%
            if (valorSeleccionado != null || toSearch != null) {
                Collection datos = HandlerGrupo.getUsersGrupos(valorSeleccionado);

                pageContext.setAttribute("usuariosGrupo",datos);
                String desc = rb.getString("security.edit");
        %>
                <logic:iterate id="userGroup" name="usuariosGrupo" indexId="ind"  type="com.desige.webDocuments.utils.beans.Search3">
                    <%  
                        int item = ind.intValue()+1;
                        int num = item%2;
                    %>
                    <tr>
                        <td class='td_<%=num%>'>
                            <%=userGroup.getName() + " " + userGroup.getApellido()%>
                        </td>
                        <td class='td_<%=num%>'>
                        	<%if(valorSeleccionado.equals(com.desige.webDocuments.utils.Constants.ID_GROUP_VIEWER)){%>
                                <%=desc%>
                            <%} else {%>
                            <a href="editSeguridad.do?cmd=<%=SuperActionForm.cmdLoad%>&user=<%=userGroup.getId()%>">
                                <%=desc%>
                            </a>
                            <%}%>
                        </td>
                    </tr>
                </logic:iterate>
           <%
            }
            %>
    <tr>
        <td colspan="2" align="center">
           <input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>" onClick="javascript:cancelar(this.form);"/>
        </td>
    </tr>
</table>
</html:form>
</logic:present>
<logic:notPresent scope="session" name="desGrupos">
    No se cargaron las Descripciones de los Grupos
</logic:notPresent>
</body>
</html>
