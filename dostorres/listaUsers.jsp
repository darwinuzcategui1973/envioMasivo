<!-- listaUsers.jsp -->
<%@ page contentType="text/html; charset=iso-8859-1" language="java" import="java.util.ResourceBundle,
                                                                             com.desige.webDocuments.utils.ToolsHTML,
                                                                             com.desige.webDocuments.utils.beans.SuperActionForm,
                                                                             java.util.Collection,
                                                                             com.desige.webDocuments.persistent.managers.HandlerGrupo" %>
<%@ page import="com.desige.webDocuments.seguridad.forms.SeguridadUserForm" %>
<%@ page import="java.util.Vector" %>
<%@ page import="com.desige.webDocuments.persistent.managers.HandlerDBUser" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    String onLoad = ToolsHTML.getMensajePages(request, rb);
    String valorSeleccionado = request.getParameter("grupo");
    String toSearch = request.getParameter("toSearch");
    String tituloAlert = rb.getString("showMSG.title");
    if (toSearch != null) {
        Vector users = (Vector) HandlerDBUser.getAllUsersForGrups(toSearch, true);
        //Luis Cisneros 28/02/07
        //Identifica si la busqueda no retorno nada, coloca el listado de usuario  en el
        //Page Scope, para luego preuntar si esta vacio o no.
        //si esta vacio, no muestra la lista sino una página vacía.
        if (users != null && users.size() > 0) {            
            SeguridadUserForm frm = (SeguridadUserForm) users.get(0);
            valorSeleccionado = frm.getIdGrupo();
        }else {
            pageContext.setAttribute("emptySearch", "true");
        }
    }
    String id = "";
    String descGroup = "";
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
<link rel="stylesheet" href="alertify/css/alertify.css">
<script src="alertify/alertify.js"></script>

<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script language="JavaScript">

	function validar(){
        if (document.login.docSearch.value.length>0){
            alert('<%=rb.getString("err.notDocumentSearch")%>');
        }else{
		    document.login.submit();
        }
	}
    function edit(num){
        forma = document.edit;
        forma.user.value = num;
        forma.submit();
    }
    function salvar(forma){
        forma.submit();
    }
    function cancelar(form){
        location.href = "administracionMain.do";
    }
    function editField(pages,input,value,forma){
        var hWnd = null;
        hWnd = window.open(pages+"?input="+input+"&value="+value+"&nameForma="+forma.name,"Catalogo","width=400,height=300,resizable=yes,scrollbars=yes,left=300,top=250");
        if ((document.window != null) && (!hWnd.opener)){
            hWnd.opener = document.window;
        }
    }
    function updateCheck(check,field){
        if (check.checked){
            field.value = "0";
        } else{
            field.value = 1;
        }
    }
    function refreshButton(forma){
        forma.lengthDocNumber.disabled = !forma.docNumberGenerator[1].checked;
        forma.resetDocNumber.disabled = !forma.docNumberGenerator[1].checked;

        if (forma.docNumberGenerator[1].checked){
            forma.lengthDocNumber.focus();
        }
    }
    function getDatos(){
        document.newUser.action = "listaUsers.jsp"
        document.newUser.submit();
        document.newUser.grupo.value;
    }

    function checkKey(evt,forma) {
        var charCode = (evt.which) ? ect.which : event.keyCode;
        if (charCode == 13) {
            forma.submit();
        }
        return true;
    }

    function buscar(forma) {
        forma.submit();
    }

    function backStruct(forma) {
        forma.action = "administracionMain.do";
        forma.submit();
    }

</script>
</head>

<body class="bodyInternas" <%=onLoad.toString().replaceAll("alert\\(","alertify.alert('"+tituloAlert+"',").replaceAll("\\.",".<br/>").replaceAll("self.<br/>status","self.status").replaceAll("alertify.<br/>","alertify.")%>>
<logic:present name="desGrupos" scope="session" >

<form name="edit" action="loadUserEdit.do">
    <html:hidden property="cmd" value="<%=SuperActionForm.cmdLoad%>"/>
    <html:hidden property="user" value=""/>
</form>
<table cellSpacing=0 cellPadding=2 width="100%" border="0" align="center">
    <tr>
        <td colspan="2">
            <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="0" align=center border=0>
                <tbody>
                    <tr>
                        <td class="td_title_bc" height="21">
                            <%=rb.getString("lista.title")%>
                        </td>
                    </tr>
                </tbody>
            </table>
        </td>
    </tr>
</table>

<table cellSpacing=0 cellPadding=0 align=center border=0 width="80%">
    <form name="search" action="listaUsers.jsp" method="post">
        <tr>
            <td class="td_title_bc" colspan="3">
                <input type="text" name="toSearch" onkeypress="javascript:checkKey(event,this.form);" />
                &nbsp;
                <input type="button" class="boton" value="<%=rb.getString("btn.search")%>" onClick="javascript:buscar(this.form);" />
                 &nbsp;
               <input type="button" class="boton" value="<%=rb.getString("btn.back")%>" onClick="javascript:backStruct(this.form);" />
            </td>
        </tr>
    </form>
</table>

<logic:notPresent scope="page" name="emptySearch">
    <html:form action="/newUserGrupo.do">
        <html:hidden property="cmd" value="<%=SuperActionForm.cmdInsert%>"/>
        <table cellSpacing=0 cellPadding=2 width="100%" border="0" align="center">
            <tr>
                <td colspan="2" class="pagesTitle">
                    <!--<%=rb.getString("lista.title")%>-->&nbsp;
                </td>
            </tr>

            <tr>
                <td colspan="2">
                    <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="0" align=center border=0>
                        <tbody>
                            <tr>
                                <td class="td_title_bc" height="21">
                                    <%=rb.getString("lista.title1")%>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </td>
            </tr>
            <tr>
                <td class="td_gris_l" width="40%">
                    <%=rb.getString("lista.nombregrupo")%>
                </td>
                <td class="td_gris_l">
                    <%=rb.getString("lista.descripciongrupo")%>
                </td>
            </tr>
            <tr>
                <td>
                    <html:select onchange="getDatos();" property="grupo" >
                        <logic:iterate id="idGrupos" name="desGrupos" scope="session" type="com.desige.webDocuments.utils.beans.Search3">
                            <%
                                if (idGrupos.getId().equalsIgnoreCase(valorSeleccionado)) {
                            %>
                            <option value="<%=idGrupos.getId()%>" selected>
                                <%=idGrupos.getName()%>
                            </option>
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
                            <option value="<%=idGrupos.getId()%>">
                                <%=idGrupos.getName()%>
                            </option>
                            <% } %>
                        </logic:iterate>
                    </html:select>
                </td>
                <td>
                    <a href="loadGrupoEdit.do?cmd=<%=SuperActionForm.cmdLoad%>&idGrupo=<%=id%>" <%=descGroup%>>
                        <%=descGroup%>
                    </a>
                </td>
            <tr>
                <td>
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    &nbsp;
                </td>
            </tr>

            <tr>
                <td colspan="2">
                    <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="0" align=center border=0>
                        <tbody>
                            <tr>
                                <td class="td_title_bc" height="21">
                                    <%=rb.getString("lista.title2")%>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </td>
            </tr>

            <tr>
                <td class="td_gris_l">
                    <%=rb.getString("lista.nombreuser")%>
                </td>
                <td class="td_gris_l">
                    <%=rb.getString("lista.emailuser")%>
                </td>
            </tr>
             <%
                if (valorSeleccionado!=null) {
                    Collection datos = HandlerGrupo.getUsersGrupos(valorSeleccionado);
                    pageContext.setAttribute("usuariosGrupo",datos);%>
                    <logic:iterate id="userGroup" name="usuariosGrupo" indexId="ind" type="com.desige.webDocuments.utils.beans.Search3">
                        <%
                            int item = ind.intValue() + 1;
                            int num = item%2;
                        %>
                        <tr>
                            <td class='td_<%=num%>'>
                                    <a href="loadUserEdit.do?nextPage=showAllAcounts&cmd=<%=SuperActionForm.cmdLoad%>&user=<%=userGroup.getId()%>">
                                        <%=userGroup.getName()%> <%=userGroup.getApellido()%>
                                    </a>
                            </td>
                            <td class='td_<%=num%>'>
                                    <a href="loadMail.do?adm=true&to=<%=userGroup.getDescript()%>">
                                        <%=userGroup.getDescript()%>
                                    </a>
                            </td>
                        </tr>
                    </logic:iterate>
            <%
                }
            %>
            <tr>
                <td colspan="2" align="center">
                    <input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>" onClick="javascript:cancelar(this.form);" />
                </td>
            </tr>
        </table>
    </html:form>
</logic:notPresent>
<logic:present scope="page" name="emptySearch">
      <br/>
      <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="2" align=center border=0>
        <tbody>
            <tr>
                <td class="td_title_bc">
                    <%=rb.getString("sch.searchFail")%>
                </td>
            </tr>
        </tbody>
    </table>
</logic:present>
</logic:present>
<logic:notPresent scope="session" name="desGrupos">
    No se cargaron las Descripciones de los Grupos
</logic:notPresent>

</body>
</html>
