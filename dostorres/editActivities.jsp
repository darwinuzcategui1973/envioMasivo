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
    ToolsHTML.clearSession(session,"application.actXUser");
    String styleTxt = "width: 300px; height: 19px";
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
        if (forma.name.value.length == 0) {
            alert('<%=rb.getString("E0111")%>');
            return;
        }
        forma.submit();
    }

    function cancelar() {
        location.href = "createAct.do";
    }

    function limpiar(forma) {
        forma.cmd.value = "<%=SuperActionForm.cmdInsert%>";
        forma.action = "createAct.do";
        forma.submit();
    }

    function loadActivity(number) {
    	var forma = document.getElementById("Selection");
        forma.action = "loadActivity.do";
        forma.number.value = number;
        forma.submit();
    }

    function loadActUsers(number,input,nameForm,value,type) {
        abrirVentana("loadActUsers.do?number="+number+"&input="+input+"&nameForma="+nameForm+"&value="+value+"&type="+type,600,400)
    }

    function paging_OnClick(pageFrom) {
        document.formPagingPage.from.value = pageFrom;
        document.formPagingPage.submit();
    }

    function youAreSure(form) {
        if (confirm("<%=rb.getString("areYouSure")%>")) {
            form.cmd.value = "<%=SuperActionForm.cmdDelete%>";
            form.submit();
        }
    }

</script>
</head>

<body class="bodyInternas" <%=onLoad.toString()%>>
<form name="Selection" id="Selection" action="">
    <input type="hidden" name="number" value=""/>
</form>
    <logic:present name="activitiesForm" scope="session" >
        <table cellSpacing=0 cellPadding=2 align=center border=0 width="100%">
          <tr>
            <td width="86%" valign="top">
                <bean:define id="act" name="activitiesForm" type="com.desige.webDocuments.activities.forms.Activities" scope="session"/>
                <html:form action="saveActivity">
                    <html:hidden property="number"/>
                    <html:hidden property="cmd"/>
                    <table width="100%" border="0">
                        <tr>
                            <td colspan="3" class="pagesTitle">
                                <%=rb.getString("act.title")%>
                            </td>
                        </tr>
                        <!-- Nombre -->
                        <tr>
                            <td valign="middle" class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" width="12%" height="26">
                                <%=rb.getString("act.name")%>:
                            </td>
                            <td valign="top" colspan="2">
                                <html:text property="name" size="30" styleClass="classText" style="<%=styleTxt%>"/>
                            </td>
                        </tr>
                        <!-- Descripción -->
                        <tr>
                            <td class="titleLeft" style="background: url(img/btn120x4.png); background-repeat: no-repeat" height="23" valign="middle" >
                                <%=rb.getString("act.desc")%>:
                            </td>
                            <td colspan="2">
                                <html:textarea property="description" cols="30" rows="5" styleClass="classText" style="width: 300px"/>
                            </td>
                        </tr>
                        <!-- Tipo de Documento -->
                        <tr>
                            <td class="titleLeft" height="26" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                                <%=rb.getString("doc.type")%>:
                            </td>
                            <td colspan="2">
                                <html:select property="idTypeDoc" styleClass="classText" style="width: 300px">
                                    <logic:present name="typesDocuments" scope="session">
                                        <logic:iterate id="bean" name="typesDocuments" scope="session" type="com.desige.webDocuments.utils.beans.Search">
                                            <html:option value="<%=bean.getId()%>">
                                                <%=bean.getDescript()%>
                                            </html:option>
                                        </logic:iterate>
                                    </logic:present>
                                    <logic:notPresent name="typesDocuments" scope="session">
                                        <%=rb.getString("E0006")%>
                                    </logic:notPresent>
                                </html:select>
                            </td>
                        </tr>
                        <!-- Sub Actividades -->
                        <%if (act.getNumber()!=0) {%>
                            <tr>
                                <td class="titleLeft" height="26" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                                    <%=rb.getString("subAct.title") %>
                                </td>
                                <td colspan="2">
                                        <a href="loadActivity.do?isSub=true&number=<%=act.getNumber()%>" class="ahref_b">
                                            <%=rb.getString("subAct.add") %>
                                        </a>

                                </td>
                            </tr>
                        <%}%>
                        <tr>
                            <td colspan="3" align="center">
                                <logic:equal value="<%=SuperActionForm.cmdEdit%>" property="cmd" name="activitiesForm" >
                                    <input type="button" class="boton" value="<%=rb.getString("btn.incluir")%>" onClick="javascript:limpiar(this.form);" />
                                    &nbsp;
                                </logic:equal>
                                <input type="button" class="boton" value="<%=rb.getString("btn.save")%>" onClick="javascript:salvar(this.form);" />
                                <logic:equal value="<%=SuperActionForm.cmdEdit%>" property="cmd" name="activitiesForm" >
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
    </logic:present>
</body>
</html>
