<!--
/**
 * Title: allUsersRecord.jsp <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @author Ing. Simon Rodriguez (SR)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li>21/05/2004 (NC) Creation </li>
 *      <li> logic  present name = "usuarios" scope="session">(SR) </li>
 *      <li>09/06/2005 (SR) Se agrego en usersWithSec las barras de retroceder y adelantar.</li>
 *      <li>09/06/2005 (SR) Se corrio el present usuarios para poder visualizar la busqueda.</li>
 *      <li>11/08/2005 (SR) Se crea deleteSecurityDocs para borar la seguridad de los documentos por usuario.</li>

 </ul>
 */

-->
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 com.desige.webDocuments.utils.beans.PaginPage,
                 com.desige.webDocuments.utils.beans.Users,
                 java.util.Collection,
                 com.desige.webDocuments.utils.Constants"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
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
    String txt = "";
    txt = rb.getString("security.toRecord");

    //Luis Cisneros, Para que no mustre la palabra null, le colocamos un string vacio. 
    if (txt == null){
        txt="";
    }
    
    String from = null;
    String size = null;
    Users users = null;
    PaginPage bean = null;
%>
<html>
<head>
<title><%=rb.getString("seguridad.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script type="text/javascript">
    function cancelar() {
    	var form = document.getElementById("Selection");
        form.action="loadStructMain.do";
        form.cmd.value="<%=SuperActionForm.cmdLoad%>";
        form.submit();
    }
   function updateCheck(check,field) {
        if (check.checked){
            field.value = "0";
        } else{
            field.value = 1;
        }
    }

    function deleteSecurity(idPerson,idUser) {
        forma = document.securityUser;
        forma.idPerson.value = idPerson;
        forma.idUser.value = idUser;
        forma.action = "deleteSecRecordUser.do";
        forma.submit();
    }



<%--
    function deleteSecurityDocs(idPerson,document) {

  alert("hola mun do");

        forma = document.securityUser;
        forma.idPerson.value = idPerson;
        if ((document!=null)&&(document.length>0)){
            forma.action = "deleteSecDocUser.do";
            alert("forma.action :"+forma.action );
         }else{
            forma.action = "deleteSecUser.do";
            alert("forma.action :"+idPerson +" document:"+document );

         }

  forma.submit();
    }
--%>
    function paging_OnClick(pageFrom){
        document.formPagingPage.from.value = pageFrom;
        document.formPagingPage.submit();
    }

   function paging_OnClick1(pageFrom){
        formPagingPage1.from.value = pageFrom;
        formPagingPage1.submit();
    }
    function setPermissionUser(idPerson,idUser,nameUser,idGroup) {
        forma = document.securityUser;
        forma.idPerson.value = idPerson;
        forma.idUser.value = idUser;
        forma.nameUser.value = nameUser;
        forma.idGroup.value = idGroup;

         forma.submit();
    }

    function toSearch(value){
        forma = document.securityUser;
        forma.toSearch.value = value;
        forma.action = "loadAllUserRecord.do";
        forma.submit();
    }

    function backStruct(forma) {
	    forma.action = "recordFiltersFrame.jsp";
        forma.submit();
    }

</script>
</head>

<body class="bodyInternas" <%=onLoad%>>

    <!-- Formulario para Obtener el nodo Seleccionado por el usuario -->
            <html:form action="/loadSecurityUserRecord.do">
                <input type="hidden" name="idPerson" value=''/>
                <input type="hidden" name="idUser" value=''/>
                <input type="hidden" name="idGroup" value=''/>
                <input type="hidden" name="idStruct" value='<%=session.getAttribute("idStruct")%>'/>
                <input type="hidden" name="idDocument" value='<%=session.getAttribute("idDocument")%>'/>
                <input type="hidden" name="nameUser" value=''/>
                <input type="hidden" name="nameDocument" value='<%=request.getAttribute("nameDocument")%>'/>
                <input type="hidden" name="idNodeSelected" value='<%=session.getAttribute("idStruct")%>'/>
                <input type="hidden" name="toSearch" value=''/>
                <input type="hidden" name="command" value='<%=session.getAttribute("command")%>'/>
                <input type="hidden" name="rout" value='<%=session.getAttribute("routClean")%>'/>
                <input type="hidden" name="nexPage" value='loadStructMain.do'/>
                <input type="hidden" name="nodeType" value='<%=session.getAttribute("nodeType")%>'/>
                <input type="hidden" name="securityPorStructura" value='<%=session.getAttribute("securityPorStructura")%>'/>
            </html:form>
    <!-- END Form -->
    
<!-- SIMON 09 JUNIO 2005 INICIO -->
    <!-- < logic : present name="usuarios" scope= "session" > -->
<!-- SIMON 09 JUNIO 2005 FIN -->
    <table border="0" width="100%">
        <tr>
            <td colspan="3" class="pagesTitle">
                <%=rb.getString("seguridad.title") + " " + txt%>
            </td>
        </tr>
        <tr>
            <td>
                <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="2" align=center border=0>
                    <tbody>
                        <tr>
                            <td class="td_title_bc" height="21">
                                <%=rb.getString("seguridad.title") + " " + txt %>
                                <bean:write name="securityUser" property="rout"/>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </td>
        </tr>
    </table>

    <table cellSpacing=0 cellPadding=0 align=center border=0 width="80%">
        <tr>
            <td valign="top">
                <table cellSpacing=0 cellPadding=0 align=center border=0 width="100%">
                    <tr>
                        <td colspan="3">
                            &nbsp;
                        </td>
                    </tr>
                    <form name="busqueda" action="">
                        <input type="hidden" name="idNodeSelected" value='<%=session.getAttribute("idStruct")%>'/>
                        <tr>
                            <td class="td_title_bc" colspan="3">
                                <input type="text" name="buscar" />
                                &nbsp;
                                <input type="button" class="boton" value="<%=rb.getString("btn.search")%>" onClick="javascript:toSearch(this.form.buscar.value);" />
                                &nbsp;
                                <input type="button" class="boton" value="<%=rb.getString("btn.back")%>" onClick="javascript:backStruct(this.form);" />
                            </td>
                        </tr>
                    </form>
                    <tr>
                        <td colspan="3">
                            &nbsp;
                        </td>
                    </tr>
                    <tr>
                        <td class="td_orange_l_b" width="40%">
                            <%=rb.getString("seguridad.idUser")%>
                        </td>
                        <td class="td_orange_l_b" width="30%">
                            <%=rb.getString("seguridad.nombre")%>
                        </td>
                        <td class="td_orange_l_b" width="*">
                            <%=rb.getString("seguridad.grupo")%>
                        </td>
                    </tr>

                   <!-- SIMON 09 JUNIO 2005 INICIO -->
                   <logic:present name="usuarios" scope="session">
                   <!-- SIMON 09 JUNIO 2005 FIN -->
                    <%
                        from = request.getParameter("from");
                        size = String.valueOf(((Collection)session.getAttribute("usuarios")).size());
                        users = (Users)session.getAttribute("user");
                        bean = ToolsHTML.getBeanPaginPage(from,Integer.parseInt(size),String.valueOf(users.getNumRecord()));
                    %>

                    <logic:iterate id="user" name="usuarios" type="com.desige.webDocuments.seguridad.forms.SeguridadUserForm" scope="session"
                    indexId="ind" offset="<%=bean.getDesde()%>" length="<%=bean.getCuantos()%>">
                        <%
                            int item = ind.intValue()+1;
                            int num = item%2;
                        %>
                        <tr>
                            <td class='td_<%=num%>'>
                               <a class="ahref_b" href="javascript:setPermissionUser('<%=user.getIdPerson()%>','<%=user.getNameUser()%>','<%=user.getNombres()%>',<%=user.getIdGrupo()%>)">
                                    <%=user.getNameUser()%>
                                </a>
                            </td>
                            <td class='td_<%=num%>'>
                                <%=user.getNombres()%>
                            </td>
                            <td class='td_<%=num%>'>
                                <%=user.getNombreGrupo()%>
                            </td>
                        </tr>
                    </logic:iterate>
                        <tr>
                            <td colspan="3" align="center">
		                        <!-- Inicio de Paginación -->
		                        <table class="paginador">
                                    <tr>
                                        <td align="center" colspan="4">
                                            <br>
                                            <font size="1" color="#000000">
                                                <%=rb.getString("pagin.title")+ " "%>
                                                <%=Integer.parseInt(bean.getPages())+1%>
                                                <%=rb.getString("pagin.of")%>
                                                <%=bean.getNumPages()%>
                                            </font>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td align="center"> <img src="img/First.gif" width="24" height="24"
                                            class="GraphicButton" title="<%=rb.getString("pagin.First")%>"
                                            onclick="paging_OnClick(0)">
                                        </td>
                                        <td align="center"> <img src="img/left.gif" width="24" height="24"
                                           class="GraphicButton" title="<%=rb.getString("pagin.Previous")%>"
                                           onclick="paging_OnClick(<%=(Integer.parseInt(bean.getPages())-1)%>)">
                                        </td>
                                        <td align="center"> <img src="img/right.gif" width="24" height="24"
                                           class="GraphicButton" title="<%=rb.getString("pagin.next")%>"
                                           onclick="paging_OnClick(<%=(Integer.parseInt(bean.getPages())+1)%>)">
                                        </td>
                                        <td align="center"> <img src="img/End.gif" width="24" height="24"
                                           class="GraphicButton" title="<%=rb.getString("pagin.Last")%>"
                                           onclick="paging_OnClick(<%=bean.getNumPages()%>)">
                                        </td>
                                    </tr>
                                </table>
                                <form name="formPagingPage" method="post" action="allUsersRecord.jsp">
                                    <input type="hidden" name="from"  value="">
                                    <input type="hidden" name="cmd"  value="<%=SuperActionForm.cmdLoad%>">
                                </form>
                            </td>
                      </tr>
                </table>
            </td>
        </tr>
    </table>
</logic:present>
<br/>
<logic:present name="usersWithSec" scope="session">
    <table border="0" width="100%">
        <tr>
            <td>
                <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="2" align=center border=0>
                    <tbody>
                        <tr>
                            <td class="td_title_bc" height="21">
                                <%=rb.getString("seguridad.usersWithSec")%>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </td>
        </tr>
    </table>

    <table cellSpacing=0 cellPadding=0 align=center border=0 width="80%">
        <tr>
            <td valign="top">
                <table cellSpacing=0 cellPadding=0 align=center border=0 width="100%">
                    <tr>
                        <td class="td_orange_l_b" width="25%">
                            <%=rb.getString("seguridad.idUser")%>
                        </td>
                        <td class="td_orange_l_b" width="30%">
                            <%=rb.getString("seguridad.nombre")%>
                        </td>
                        <td class="td_orange_l_b" width="25%">
                            <%=rb.getString("seguridad.grupo")%>
                        </td>
                        <td class="td_orange_l_b" width="*">
                            <%=rb.getString("segur.option")%>
                        </td>
                    </tr>

                    <%
                        from = request.getParameter("from");
                        size = String.valueOf(((Collection)session.getAttribute("usersWithSec")).size());
                        users = (Users)session.getAttribute("user");
                        bean = ToolsHTML.getBeanPaginPage(from,Integer.parseInt(size),String.valueOf(users.getNumRecord()));
                    %>

                    <logic:iterate id="user" name="usersWithSec" type="com.desige.webDocuments.seguridad.forms.SeguridadUserForm" scope="session"
                    indexId="ind" offset="<%=bean.getDesde()%>" length="<%=bean.getCuantos()%>">
                        <%
                            int item = ind.intValue()+1;
                            int num = item%2;
                        %>
                        <tr>
                            <td class='td_<%=num%>'>
                               <a class="ahref_b" href="javascript:setPermissionUser('<%=user.getIdPerson()%>','<%=user.getNameUser()%>','<%=user.getNombres()%>',<%=user.getIdGrupo()%>)">
                                    <%=user.getNameUser()%>
                                </a>
                            </td>
                            <td class='td_<%=num%>'>
                                <%=user.getNombres()%>
                            </td>
                            <td class='td_<%=num%>'>
                                <%=user.getNombreGrupo()%>
                            </td>
                            <td class='td_<%=num%>'>
                            <a class="ahref_b" href="javascript:deleteSecurity('<%=user.getIdPerson()%>','<%=user.getNameUser()%>')">
                                    <%=rb.getString("segur.delete")%>
                                </a>
                            </td>
                        </tr>


                    </logic:iterate>
                    <!-- SIMON 9 DE JUNIO 2005 INICIO -->
                                       <tr>
                                            <td colspan="4" align="center">
						                        <!-- Inicio de Paginación -->
						                        <table class="paginador">
                                                    <tr>
                                                        <td align="center" colspan="4">
                                                            <br>
                                                            <font size="1" color="#000000">
                                                                <%=rb.getString("pagin.title")+ " "%>
                                                                <%=Integer.parseInt(bean.getPages())+1%>
                                                                <%=rb.getString("pagin.of")%>
                                                                <%=bean.getNumPages()%>
                                                            </font>
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td align="center"> <img src="img/First.gif" width="24" height="24"
                                                            class="GraphicButton" title="<%=rb.getString("pagin.First")%>"
                                                            onclick="paging_OnClick1(0)">
                                                        </td>
                                                        <td align="center"> <img src="img/left.gif" width="24" height="24"
                                                           class="GraphicButton" title="<%=rb.getString("pagin.Previous")%>"
                                                           onclick="paging_OnClick1(<%=(Integer.parseInt(bean.getPages())-1)%>)">
                                                        </td>
                                                        <td align="center"> <img src="img/right.gif" width="24" height="24"
                                                           class="GraphicButton" title="<%=rb.getString("pagin.next")%>"
                                                           onclick="paging_OnClick1(<%=(Integer.parseInt(bean.getPages())+1)%>)">
                                                        </td>
                                                        <td align="center"> <img src="img/End.gif" width="24" height="24"
                                                           class="GraphicButton" title="<%=rb.getString("pagin.Last")%>"
                                                           onclick="paging_OnClick1(<%=bean.getNumPages()%>)">
                                                        </td>
                                                    </tr>
                                                </table>
                                                <form name="formPagingPage1" method="post" action="allUsersRecord.jsp">
                                                    <input type="hidden" name="from"  value="">
                                                    <input type="hidden" name="cmd"  value="<%=SuperActionForm.cmdLoad%>">
                                                </form>
                                            </td>
                                      </tr>
                                    <!-- SIMON 9 DE JUNIO 2005 FIN -->

                </table>
            </td>
        </tr>
    </table>
</logic:present>
</body>
</html>
