<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.PaginPage,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 com.desige.webDocuments.utils.beans.Users"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<!-- subActivities.jsp -->
<%@ page language="java" %>
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    String onLoad = ToolsHTML.getMensajePages(request,rb);
    if (onLoad==null) {
        response.sendRedirect(rb.getString("href.logout").concat("?target=parent"));
    }
//    ToolsHTML.clearSession(session,"application.actXUser");
    String styleTxt = "width: 300px; height: 19px";
    String activityID = "0";
    boolean isEmptyList = true;
    int item = 0;
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="./estilo/funciones.js"></script>
<script type="text/javascript">

    setDimensionScreen();

    function salvar(forma) {
        if (forma.nameAct && forma.nameAct.value.length == 0) {
            alert("Debe indicar el Nombre de la Actividad a crear");
            return false;
        }        
        forma.submit();
    }

    function cancelar() {
        location.href = "createAct.do";
    }

    function limpiar(forma) {
        location.href="loadActivity.do?isSub=true&number="+forma.activityID.value;
<%--        forma.action = "createAct.do";--%>
<%--        forma.submit();--%>
    }

    function loadActivity(number) {
    	var forma = document.getElementById("Selection");
        forma.action = "loadSubActivity.do";
        forma.number.value = number;
        forma.submit();
    }

    function loadActUsers(number,input,nameForm,value,type) {
        abrirVentana("loadActUsers.do?number="+number+"&input="+input+"&nameForma="+nameForm+"&value="+value+"&type="+type,600,400)
    }

    function paging_OnClick(pageFrom){
	    document.formPagingPage.action = "loadActivity.do";
	    document.formPagingPage.number.value = document.getElementById("Selection").activityID.value;
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
<logic:present name="activitiesForm" scope="session">
    <bean:define id="act" name="activitiesForm" type="com.desige.webDocuments.activities.forms.Activities" scope="session"/>
    <%
        activityID = String.valueOf(act.getNumber());
    %>
    <table cellSpacing=2 cellPadding=2 align=center border="0" width="90%">
        <tr>
            <td width="100%" valign="top" colspan="2">
                <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                    <tbody>
                        <tr>
                            <td class="td_title_bc" height="21 px">
                                <%=rb.getString("act.titleI")%>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </td>
        </tr>
        <tr>
            <td valign="middle" class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" width="12%" height="26">
                <%=rb.getString("act.name")%>:
            </td>
            <td valign="top" class="classText">
                <bean:write name="activitiesForm" property="name" />
            </td>
        </tr>
        <!-- Descripción -->
        <tr>
            <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" height="26" valign="middle" >
                <%=rb.getString("act.desc")%>:
            </td>
            <td class="classText">
<%--                <textarea name="description" cols="30" rows="5" style="width: 300px" class="classText">--%>
                    <bean:write name="activitiesForm" property="description" />
<%--                </textarea>--%>
            </td>
        </tr>
                <!-- Tipo de Documento -->
        <tr>
            <td class="titleLeft" height="26" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                <%=rb.getString("doc.type")%>:
            </td>
            <td>
                <select name="idTypeDoc" class="classText" style="width: 300px" disabled>
                    <logic:present name="typesDocuments" scope="session">
                        <logic:iterate id="bean" name="typesDocuments" scope="session" type="com.desige.webDocuments.utils.beans.Search">
                            <logic:equal value="<%=bean.getId()%>" name="activitiesForm" property="idTypeDoc">
                                <option value="<%=bean.getId()%>" selected>
                                    <%=bean.getDescript()%>
                                </option>
                            </logic:equal>
                            <logic:notEqual value="<%=bean.getId()%>" name="activitiesForm" property="idTypeDoc">
                                <option value="<%=bean.getId()%>">
                                    <%=bean.getDescript()%>
                                </option>
                            </logic:notEqual>
                        </logic:iterate>
                    </logic:present>
                    <logic:notPresent name="typesDocuments" scope="session">
                        <%=rb.getString("E0006")%>
                    </logic:notPresent>
                </select>
            </td>
        </tr>
    </table>
    <br/>
</logic:present>
<logic:present name="subActivities" scope="session">
    <table cellSpacing=1 cellPadding=1 align=center border=0 width="90%">
        <tr>
            <td width="100%" valign="top" colspan="2">
                <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=0 align=center border=0>
                    <tbody>
                        <tr>
                            <td class="td_title_bc" height="21 px">
                                <%=rb.getString("subAct.title")%>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </td>
        </tr>
        <tr>
            <td width="40%" class="td_titulo_C">
                <%=rb.getString("act.name")%>
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
        <logic:iterate id="sAct" name="subActivities" indexId="ind" offset="<%=Pagingbean.getDesde()%>" length="<%=Pagingbean.getCuantos()%>"
                                        type="com.desige.webDocuments.activities.forms.SubActivities" scope="session">
            <%
                item = ind.intValue()+1;
                int num = item%2;
                isEmptyList = false;
            %>
            <tr>
                <td class='td_<%=num%>'>
                    <a href="javascript:loadActivity('<%=sAct.getNumber()%>')" class="ahref_b">
                        <center>
<%--                            <%=sAct.getNumber()%>--%>
                            <%=sAct.getNameAct()%>
                        </center>
                    </a>
                </td>
                <td class='td_<%=num%>'>
                    <%=sAct.getDescription()%>
<%--                    <%=sAct.getName()%>--%>
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
                        <td align="center"><img src="img/First.gif" width="24" height="24"
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
                <form name="formPagingPage" method="post" action="main-tree.jsp">
                  <input type="hidden" name="from"  value="">
                  <input type="hidden" name="number"  value="">
                  <input type="hidden" name="isSub" value="true"/>
                </form>
                <!-- Fin de Paginación -->
            </td>
        </tr>
    </table>
</logic:present>
</center>
<form name="Selection" id="Selection" action="">
    <input type="hidden" name="number" value=""/>
    <input type="hidden" name="activityID" value="<%=activityID%>"/>
</form>
<table cellSpacing=0 cellPadding=2 align=center border=0 width="90%">
  <tr>
    <td width="86%" valign="top">
        <html:form action="saveSubActivity">
            <html:hidden property="number"/>
            <html:hidden property="activityID" value="<%=activityID%>" />
            <html:hidden property="cmd"/>
            <table width="100%" border="0">
                <tr>
                    <td colspan="3" class="pagesTitle">
                        <%=rb.getString("subAct.titleI")%>
                    </td>
                </tr>
                <!-- Nombre -->
                <tr>
                    <td valign="middle" class="titleLeft" style="background: url(img/btn120x2.png); background-repeat: no-repeat" width="16%" height="26">
                        <%=rb.getString("act.name")%>:
                    </td>
                    <td valign="top" colspan="2">
                        <html:text property="nameAct" size="30" styleClass="classText" style="<%=styleTxt%>"/>
                    </td>
                </tr>
                <!-- Descripción -->
                <tr>
                    <td class="titleLeft" style="background: url(img/btn200x4.png); background-repeat: no-repeat" height="23" valign="middle" >
                        <%=rb.getString("act.desc")%>:
                    </td>
                    <td colspan="2">
                        <html:textarea property="description" cols="30" rows="5" styleClass="classText" style="width: 300px"/>
                    </td>
                </tr>
                <!-- Ejecutante -->
                <tr>
                    <td class="titleLeft" style="background: url(img/btn200x4.png); background-repeat: no-repeat" height="23" valign="middle">
                        <%=rb.getString("act.eject")%>:
                     </td>
                    <td width="40%">
                        <input type="hidden" name="exec" value=""/>
                        <html:textarea property="executant" cols="30" rows="5" styleClass="classText" style="width: 300px" readonly="true" />
                    </td>
                    <td width="*">
                        <input type="button" class="boton" value="<%=rb.getString("btn.modify")%>" onClick="javascript:loadActUsers('<bean:write name="subActivity" property="number"/>','exec','subActivity','executant',0);" />
                    </td>
                </tr>
                <!-- Orden de Ejecución -->
                <tr>
                    <td class="titleLeft" style="background: url(img/btn120x2.png); background-repeat: no-repeat" height="23" valign="middle" >
                        <%=rb.getString("act.orden")%>:
                    </td>
                    <td colspan="2">
                        <html:select property="orden" style="width:50px">
                            <%
                                for (int pos = 1; pos < 100; pos++ ) {
                            %>
                                    <html:option value="<%=String.valueOf(pos)%>"><%=pos%></html:option>
                            <%   }
                            %>
                        </html:select>
                        <input type="hidden" name="actual" value='<bean:write name="subActivity" property="orden"/>'/>
                        <%--<html:text property="orden" cols="30" rows="5" styleClass="classText" style="width: 300px"/>--%>
                    </td>
                </tr>
                <tr>
                    <td colspan="3" align="center">
                    	<%if(!isEmptyList){%>
    	                    <input type="button" class="boton" value="<%=rb.getString("btn.clear")%>" onClick="javascript:limpiar(this.form);" />
                    	<%} else {%>
	                        <input type="reset" class="boton" value="<%=rb.getString("btn.clear")%>" />
                    	<%}%>
                        &nbsp;
                        <input type="button" class="boton" value="<%=rb.getString("btn.save")%>" onClick="javascript:salvar(this.form);" />
                        <logic:equal value="<%=SuperActionForm.cmdEdit%>" property="cmd" name="subActivity" >
                            &nbsp;
                            <input type="button" class="boton" value="<%=rb.getString("btn.delAct")%>" onClick="javascript:youAreSure(this.form);" />
                        </logic:equal>
                        &nbsp;
                        <input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>" onClick="javascript:cancelar();" />
                    </td>
                </tr>
                <tr>
                </tr>
            </table>
        </html:form>
        </td>
    </tr>
</table>
</body>
</html>
<script>
   <logic:notEqual value="<%=SuperActionForm.cmdEdit%>" property="cmd" name="subActivity" >
		document.subActivity.orden.value=<%=item+1%>;
   </logic:notEqual>
</script>