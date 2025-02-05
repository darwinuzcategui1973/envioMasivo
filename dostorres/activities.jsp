<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.PaginPage,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 com.desige.webDocuments.utils.beans.Users"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    String onLoad = ToolsHTML.getMensajePages(request,rb);
    if (onLoad==null) {
        response.sendRedirect(rb.getString("href.logout").concat("?target=parent"));
    }
//    StringBuffer onLoad = new StringBuffer("");
//    String ok = (String)ToolsHTML.getAttributeSession(session,"usuario",false);
//    String info = (String) ToolsHTML.getAttribute(request,"info",true);
//    if (ToolsHTML.checkValue(ok)) {
//        onLoad.append(" onLoad=\"self.status='").append(rb.getString("pagin.welcome")).append(" ");
//        onLoad.append(ok).append("'");
//    } else {
//        String link = rb.getString("href.logout");
//        response.sendRedirect(link+"?target=parent");
//    }
//    if (ToolsHTML.checkValue(info)){
//        onLoad.append(";alert('").append(info).append("')");
//    }
//    if (onLoad.length()>0){
//        onLoad.append("\"");
//    }
    ToolsHTML.clearSession(session,"application.actXUser");
    String styleTxt = "width: 300px; height: 19px";
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script language=javascript src="./estilo/funciones.js"></script>
<script language="JavaScript">

    setDimensionScreen();

<%--    function salvar(forma) {--%>
<%--        forma.submit();--%>
<%--    }--%>

    function cancelar() {
        location.href = "administracionMain.do";
    }

    function incluir() {
        forma = document.getElementById("Selection");
        forma.cmd.value = "<%=SuperActionForm.cmdInsert%>"
        forma.action = "createAct.do";
        forma.submit();
    }

    function loadActivity(number) {
        forma = document.getElementById("Selection");
        forma.action = "loadActivity.do";
        forma.number.value = number;
        forma.submit();
    }

    function loadActUsers(number,input,nameForm,value,type) {
        abrirVentana("loadActUsers.do?number="+number+"&input="+input+"&nameForma="+nameForm+"&value="+value+"&type="+type,600,400)
    }

    function paging_OnClick(pageFrom){
        document.formPagingPage.from.value = pageFrom;
        document.formPagingPage.submit();
    }

    function youAreSure(form) {
<%--        form = document.editarStruct;--%>
        if (confirm("<%=rb.getString("areYouSure")%>")) {
            form.cmd.value = "<%=SuperActionForm.cmdDelete%>";
            form.submit();
        }
    }

</script>
</head>

<body class="bodyInternas" <%=onLoad.toString()%>>
<center>
<logic:present name="activities" scope="session">
    <table cellSpacing=0 cellPadding=0 align=center border=0 width="70%">
        <tr>
            <td width="100%" valign="top" colspan="2">
                <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                    <tbody>
                        <tr>
                            <td class="td_title_bc" height="21 px">
                                <%=rb.getString("act.title")%>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </td>
        </tr>
        <tr>
            <td width="40%" class="td_titulo_C">
                <%=rb.getString("act.name")%>
                <%--<%=rb.getString("act.num")%>--%>
            </td>
            <td width="*" class="td_titulo_C">
                <%=rb.getString("act.desc")%>
            </td>
        </tr>
        <!-- Paginación -->
        <%
            String from = request.getParameter("from");
            String size = (String)session.getAttribute("size");
            Users users = session.getAttribute("user")!=null?(Users)session.getAttribute("user"):null;
            PaginPage Pagingbean =  ToolsHTML.getBeanPaginPage(from,Integer.parseInt(size),String.valueOf(users.getNumRecord()));
        %>
        <logic:iterate id="act" name="activities" indexId="ind" offset="<%=Pagingbean.getDesde()%>" length="<%=Pagingbean.getCuantos()%>"
                                        type="com.desige.webDocuments.activities.forms.Activities" scope="session">
            <%
                int item = ind.intValue()+1;
                int num = item%2;
            %>
            <tr>
                <td class='td_<%=num%>'>
                    <a href="javascript:loadActivity('<%=act.getNumber()%>')" class="ahref_b">
                        <%--<%=act.getNumber()%>--%>
                        <%=act.getName()%>
                    </a>
                </td>
                <td class='td_<%=num%>'>
                    <%=act.getDescription()%>
                </td>
            </tr>
        </logic:iterate>
        <tr>
            <td align="center" colspan="2">
                <!-- Inicio de Paginación -->
                <table class="paginador">
                    <tr>
                        <td align="center" colspan="4">
                            <br/>
                                <font size="1" color="#000000">
                                    <%=rb.getString("pagin.title")+ " "%>
                                    <%=Integer.parseInt(Pagingbean.getPages())+1%>
                                    <%=rb.getString("pagin.of")%>
                                    <%=Pagingbean.getNumPages()%>
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
                           onclick="paging_OnClick(<%=(Integer.parseInt(Pagingbean.getPages())-1)%>)">
                        </td>
                        <td align="center"> <img src="img/right.gif" width="24" height="24"
                           class="GraphicButton" title="<%=rb.getString("pagin.next")%>"
                           onclick="paging_OnClick(<%=(Integer.parseInt(Pagingbean.getPages())+1)%>)">
                        </td>
                        <td align="center"> <img src="img/End.gif" width="24" height="24"
                           class="GraphicButton" title="<%=rb.getString("pagin.Last")%>"
                           onclick="paging_OnClick(<%=Pagingbean.getNumPages()%>)">
                        </td>
                    </tr>
                </table>
                <form name="formPagingPage" method="post" action="activities.jsp">
                  <input type="hidden" name="from"  value="">
                </form>
                <!-- Fin de Paginación -->
            </td>
        </tr>
    </table>
</logic:present>
</center>
<form name="Selection" id="Selection" action="">
    <input type="hidden" name="number" value=""/>
    <input type="hidden" name="cmd" value=""/>
</form>
<table cellSpacing=0 cellPadding=2 align=center border=0 width="100%">
    <tr>
        <td width="86%" valign="top" align="center">
            <input type="button" class="boton" value="<%=rb.getString("btn.incluir")%>" onClick="javascript:incluir();" />
            &nbsp;
            &nbsp;
            <input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>" onClick="javascript:cancelar();" />
        </td>
    </tr>
</table>

</body>
</html>
