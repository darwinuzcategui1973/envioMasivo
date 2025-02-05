<!-- usersToEdit.jsp -->
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 com.desige.webDocuments.utils.beans.Users,
                 java.util.Collection,
                 com.desige.webDocuments.utils.beans.PaginPage"%>

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
%>
<html>
<head>
<title><%=rb.getString("seguridad.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script type="text/javascript">

    function paging_OnClick(pageFrom) {
        document.formPagingPage.from.value = pageFrom;
        document.formPagingPage.submit();
    }

    function buscar(forma) {
        forma.action = "loadUsersToEdit.do?loadUser=true";
        forma.submit();
    }            

    function backStruct(forma){
        forma.action = "administracionMain.do";
        forma.submit();
    }

//    function checkKey(evt,value,forma) {
//        var charCode = (evt.which) ? ect.which : event.keyCode;
//        if (charCode == 13){
//            toSearch(value,forma);
//            toSearch(document.search.buscar.value,document.getElementById("selection"));
//        }
//        return true;
//    }

    function checkKey(evt,forma) {
        var charCode = (evt.which) ? ect.which : event.keyCode;
        if (charCode == 13) {
            forma.submit();
        }
        return true;
    }

</script>
</head>

<body class="bodyInternas" <%=onLoad%>>
    <table cellSpacing=0 cellPadding=0 align=center border=0 width="80%">
        <form name="search" action="loadUsersToEdit.do?loadUser=true" method="post">
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
    <form name="selection" id="selection" action="" method="post">
        <input type="hidden" name="toSearch" value=""/>
    </form>
<logic:present name="usersToEdit" scope="session">
    <table cellSpacing=0 cellPadding=0 align=center border=0 width="80%">
        <tr>
            <td valign="top">
                <table cellSpacing=0 cellPadding=0 align=center border=0 width="100%">
                    <tr>
                        <td valign="top" colspan="3">
                            <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="2" align=center border=0>
                                <tbody>
                                    <tr>
                                        <td class="td_title_bc">
                                            <%=rb.getString("admin.changeUserGroup")%>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3">
                            &nbsp;
                        </td>
                    </tr>
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

                    <%
                        String from = request.getParameter("from");
                        String size = String.valueOf(((Collection)session.getAttribute("usersToEdit")).size());
                        Users users = (Users)session.getAttribute("user");
                        PaginPage bean = ToolsHTML.getBeanPaginPage(from,Integer.parseInt(size),String.valueOf(users.getNumRecord()));
                    %>

                    <logic:iterate id="user" name="usersToEdit" type="com.desige.webDocuments.seguridad.forms.SeguridadUserForm" scope="session"
                    indexId="ind" offset="<%=bean.getDesde()%>" length="<%=bean.getCuantos()%>">
                        <%
                            int item = ind.intValue()+1;
                            int num = item%2;
                        %>
                        <tr>
                            <td class='td_<%=num%>' >
                                <a class="ahref_b" href="loadUserEdit.do?nextPage=loadToEdit&cmd=<%=SuperActionForm.cmdLoad%>&user=<%=user.getNameUser()%>">
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
                                <form name="formPagingPage" method="post" action="loadUsersToEdit.do?loadUser=true">
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
<logic:notPresent name="usersToEdit" scope="session">
    <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="2" align=center border=0>
        <tbody>
            <tr>
                <td class="td_title_bc">
                    <%=rb.getString("sch.searchFail")%>
                </td>
            </tr>
        </tbody>
    </table>
</logic:notPresent>
</body>
</html>
