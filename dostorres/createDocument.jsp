
<!--
 * Title: createDocument.jsp <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Simón Rodriguez.
 * @author Ing. Nelson Crespo.
 * @version WebDocuments v3.0
 * <br/>
 *       Changes:<br/>

 *      <li> 25/04/2006 (SR) Se sustituyo /WebDocuments/.. por ./..
 * </ul>
-->
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    String width = "12%";
    if (request.getAttribute("width")!=null) {
        width = "25%";
    }
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script language=javascript src="./estilo/funciones.js"></script>
<script language="JavaScript">

    setDimensionScreen();

    function send(forma){
        if (validar(forma)) {
           forma.submit();
        }
    }

    <logic:present name="info" scope="session">
        showInfo('<%=session.getAttribute("info")%>');
        <%
            session.removeAttribute("info");
        %>
    </logic:present>

    <logic:present name="error" scope="session">
        showInfo('<%=session.getAttribute("error")%>');
        <%
            session.removeAttribute("error");
        %>
    </logic:present>

    function editField(pages,input,value,forma){
        var hWnd = null;
        hWnd = window.open(pages+"?input="+input+"&value="+value+"&nameForma="+forma.name,"Catalogo","width=600,height=450,resizable=yes,statubar=yes,scrollbars=yes,left=200,top=150");
        if ((document.window != null) && (!hWnd.opener)){
            hWnd.opener = document.window;
        }
    }

    function cancelar() {
        history.back();
    }

    function validar(forma){
        if (forma.nameDocument.value.length==0) {
            alert('<%=rb.getString("err.notNameFile")%>');
            return false;
        }
        if (forma.idRout.value.length==0){
            alert ("<%=rb.getString("err.notIDRout")%>");
            return false;
        }
        return true;
    }

    function save(){
    }
</script>
</head>

<body class="bodyInternas">

    <html:form action="/saveRegister.do">
        <html:hidden property="idDocument"/>
        <table align=center border=0 width="100%">
            <tr>
                <td class="pagesTitle" colspan="2">
                    <%=rb.getString("btn.createDocument")%>
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                        <tbody>
                            <tr>
                                <td class="td_title_bc">
                                    <%=rb.getString("btn.createDocument")%>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </td>
            </tr>
            <tr>
                <td class="td_gris_l" colspan="2">
                    &nbsp;
                </td>
            </tr>
            <tr>
                <td class="titleLeft" height="26" style="background: url(img/btn120.png); background-repeat: no-repeat" width="20%" valign="middle">
                    <%=rb.getString("cbs.name")%>:
                </td>
                <td class="td_gris_l" width="*">
                    <html:text property="nameDocument" size="60" maxlength="1000"  styleClass="classText"/>
                </td>
            </tr>
            <tr>
                <td height="26" class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                    <%=rb.getString("doc.Ver")%>:
                </td>
                <td class="td_gris_l">
                    <%--<logic:equal value="" name="newDocument" property="minorVer">--%>
                        <html:text property="mayorVer" size="4" styleClass="classText"/>.<html:text property="minorVer" size="4" styleClass="classText"/>
                    <%--</logic:equal>--%>
                    <%--<logic:notEqual value="" name="newDocument" property="minorVer">--%>
                        <%--<html:text property="mayorVer" size="4" styleClass="classText"/>.<html:text property="minorVer" size="4" styleClass="classText"/>--%>
                    <%--</logic:notEqual>--%>
                </td>
            </tr>
            <tr>
                <td class="titleLeft" height="26" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                    <%=rb.getString("reg.new.folder")%>:
                </td>
                <td class="td_gris_l">
                    <html:hidden property="idRout"/>
                    <html:text property="nameRout" size="60" maxlength="1000"  styleClass="classText" readonly="true"/>
                    <input type="button" class="boton" value=" ... " onClick="javascript:editField('loadAllStruct.do?toSelectValue=true','estado','nameState',this.form);" style="width:20px;" />
                </td>
            </tr>
            <tr>
                <td class="titleLeft" height="26" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                    <%=rb.getString("doc.type")%>:
                </td>
                <td>
                    <html:select property="typeDocument" styleClass="classText" style="width: 250px">
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
            <tr>
                <td colspan="2" align="center">
                    <input type="button" class="boton" value="<%=rb.getString("btn.create")%>" onClick="javascript:send(this.form);" />
                    &nbsp;
                    <input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>" onClick="javascript:cancelar();" />
                </td>
            </tr>
        </table>
    </html:form>
</body>
</html>
