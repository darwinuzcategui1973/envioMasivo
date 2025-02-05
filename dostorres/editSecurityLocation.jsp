<%@ page import="java.util.ResourceBundle,com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 com.desige.webDocuments.utils.Constants"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    String onLoad = ToolsHTML.getMensajePages(request,rb);
    String cmd = (String)request.getAttribute("cmd");
    if (cmd==null){
        cmd = SuperActionForm.cmdLoad;
    }
    String txtExplorer = "seguridad.toRead";
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script language="JavaScript">
    function sendData(forma) {
       //alert(forma.action);
        forma.submit();
    }
    function updateCheck(check,field){
        if (check.checked){
            field.value = 1;
        } else{
            field.value = 0;
        }
    }

    function cancelar(forma) {
        <logic:notPresent name="toGroup" scope="session">
            forma.action = "loadAllUser.do";
        </logic:notPresent>
        <logic:present name="toGroup" scope="session">
            forma.action = "loadAllGroups.do";
        </logic:present>
        forma.submit();
    }

</script>
</head>

<body class="bodyInternas" <%=onLoad.toString()%>>
                        
<%
    String forma = "/updateSecurityUser.do"; 
%>
<logic:present name="toGroup" scope="session">
    <%
        forma = "/updateSecurityGroup.do";
    %>
</logic:present>
<html:form action="<%=forma%>">
    <logic:present name="securityUser" scope="session">
        <html:hidden property="command"/>
        <html:hidden property="idPerson"/>
        <html:hidden property="nodeType"/>

        <input type="hidden" name="idStruct" value="<%=session.getAttribute("idStruct")%>"/>
        <input type="hidden" name="idDocument" value="<%=session.getAttribute("idDocument")%>"/>
        <input type="hidden" name="idNodeSelected" value="<%=session.getAttribute("idStruct")%>"/>
        <table border="0" width="100%">
            <tr>
                <td colspan="3" class="pagesTitle">
                    <%=rb.getString("seguridad.title") + " " + rb.getString("security.toLocation")%>
                </td>
            </tr>
            <tr>
                <td>
                    <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="2" align=center border=0>
                        <tbody>
                            <tr>
                                <td class="td_title_bc" height="21">
                                    <%=rb.getString("seguridad.title") + " " + rb.getString("security.toStruct0")%>:
                                    <bean:write name="securityUser" property="rout"/>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </td>
            </tr>
        </table>
        <table align=center border=0 width="90%">
             <tr>
                <tr>
                    <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" width="25%" height="26" valign="middle">
                        <%=rb.getString("seguridad.idUser")%>:
                    </td>
                    <td width="*" class="td_gris_l">
                        <bean:write name="securityUser" property="idUser"/>
                    </td>
                </tr>
                <tr>
                    <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle" height="26">
                        <%=rb.getString("seguridad.nombre")%>:
                    </td>
                    <td class="td_gris_l">
                        <bean:write name="securityUser" property="nameUser"/>
                    </td>
                </tr>
                <!--<tr>-->
                    <!--<td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle" height="26">-->
                        <%--<%=rb.getString(txtExplorer)%>:--%>
                    <!--</td>-->
                    <!--<td class="td_gris_l">-->
                        <%--<logic:equal value="1" name="securityUser" property="toRead" >--%>
                            <%--<%=ToolsHTML.showCheckBox("toReadI","updateCheck(this.form.toReadI,this.form.toRead)","1","1")%>--%>
                        <%--</logic:equal>--%>
                        <%--<logic:notEqual value="1" name="securityUser" property="toRead" >--%>
                            <%--<%=ToolsHTML.showCheckBox("toReadI","updateCheck(this.form.toReadI,this.form.toRead)","0","1")%>--%>
                        <%--</logic:notEqual>--%>
                        <%--<html:hidden property="toRead" />--%>
                    <!--</td>-->
                <!--</tr>-->
                <tr>
                    <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle" height="26">
                        <%=rb.getString(txtExplorer)%>:
                    </td>
                    <td class="td_gris_l">
                        <logic:equal value="1" name="securityUser" property="toView" >
                            <%=ToolsHTML.showCheckBox("toReadI","updateCheck(this.form.toReadI,this.form.toView)","1","1")%>
                        </logic:equal>
                        <logic:notEqual value="1" name="securityUser" property="toView" >
                            <%=ToolsHTML.showCheckBox("toReadI","updateCheck(this.form.toReadI,this.form.toView)","0","1")%>
                        </logic:notEqual>
                        <html:hidden property="toView"/>
                    </td>
                </tr>
               <logic:present scope="session" name="CreaLocidad">
                <tr>
                    <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle" height="26">
                        <%=rb.getString("security.locToAdd")%>:
                    </td>
                    <td class="td_gris_l">
                        <logic:equal value="1" name="securityUser" property="toAddFolder" >
                            <%=ToolsHTML.showCheckBox("toAddFolderI","updateCheck(this.form.toAddFolderI,this.form.toAddFolder)","1","1")%>
                        </logic:equal>
                        <logic:notEqual value="1" name="securityUser" property="toAddFolder" >
                            <%=ToolsHTML.showCheckBox("toAddFolderI","updateCheck(this.form.toAddFolderI,this.form.toAddFolder)","0","1")%>
                        </logic:notEqual>
                        <html:hidden property="toAddFolder" />
                        <html:hidden property="toAddProcess" value="0"/>
                    </td>
                </tr>
               </logic:present>
                <tr>
                    <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle" height="26">
                        <%=rb.getString("seguridad.toAdmon")%>:
                    </td>
                    <td class="td_gris_l">
                        <logic:equal value="1" name="securityUser" property="toAdmon" >
                            <%=ToolsHTML.showCheckBox("toAdmonI","updateCheck(this.form.toAdmonI,this.form.toAdmon)","1","1")%>
                        </logic:equal>
                        <logic:notEqual value="1" name="securityUser" property="toAdmon" >
                            <%=ToolsHTML.showCheckBox("toAdmonI","updateCheck(this.form.toAdmonI,this.form.toAdmon)","0","1")%>
                        </logic:notEqual>
                        <html:hidden property="toAdmon" />
                    </td>
                </tr>
               <!--17 agosto 2005 inicio -->
                <%--<tr>
                    <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle" height="26">
                        <%=rb.getString("seguridad.toDelete")%>:
                    </td>
                    <td class="td_gris_l">
                        <logic:equal value="1" name="securityUser" property="toDelete" >
                            <%=ToolsHTML.showCheckBox("toDeleteI","updateCheck(this.form.toDeleteI,this.form.toDelete)","1","1")%>
                        </logic:equal>
                        <logic:notEqual value="1" name="securityUser" property="toDelete" >
                            <%=ToolsHTML.showCheckBox("toDeleteI","updateCheck(this.form.toDeleteI,this.form.toDelete)","0","1")%>
                        </logic:notEqual>
                        <html:hidden property="toDelete" />
                    </td>
                </tr>--%>
              <!--17 agosto 2005 fin -->
                <tr>
                    <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle" height="26">
                        <%=rb.getString("seguridad.toEdit")%>:
                    </td>
                    <td class="td_gris_l">
                        <logic:equal value="1" name="securityUser" property="toEdit" >
                            <%=ToolsHTML.showCheckBox("toEditI","updateCheck(this.form.toEditI,this.form.toEdit)","1","1")%>
                        </logic:equal>
                        <logic:notEqual value="1" name="securityUser" property="toEdit" >
                            <%=ToolsHTML.showCheckBox("toEditI","updateCheck(this.form.toEditI,this.form.toEdit)","0","1")%>
                        </logic:notEqual>
                        <html:hidden property="toEdit" />
                    </td>
                </tr>
                <html:hidden property="toMove" value="0" />
                <html:hidden property="toAddDocument" value="0" />
                <tr>
                    <td colspan="2" class="td_gris_l">
                        &nbsp;
                    </td>
                </tr>
                <tr>
                    <td colspan="2" align="center">
                        <center>
                            <input type="button" class="boton" value="<%=rb.getString("btn.ok")%>" onClick="javascript:sendData(this.form);"/>
                            &nbsp;
                            <input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>" onClick="javascript:cancelar(this.form);"/>
                        </center>
                    </td>
                </tr>
        </table>
    </logic:present>
</html:form>
</body>
</html>
