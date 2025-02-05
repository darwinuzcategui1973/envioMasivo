<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<style>
.clsMessageView
{
  background: white;
  color: black;
  border: 1px solid #FFFFFF;
  padding: 2;
  font-size: 12px;
  font-family: Geneva, Verdana, Arial, Helvetica;
  font-weight: normal;
  overflow: scroll;
}
</style>
<script language="JavaScript">
    parent.frames['superior'].location = "superior.jsp";

    function disabledBtns(forma) {
        forma.reSend.disabled = true;
        forma.reply.disabled = true;
        forma.cancel.disabled = true;
    }

    function cancelar(forma){
<%--        location.href = "loadInbox.do";--%>
        forma.action = "loadInbox.do";
        disabledBtns(forma);
        forma.submit();
    }
    function validar(forma){
        if (forma.to.value.length==0){
            alert ("<%=rb.getString("error.notTo")%>");
            return false;
        }
        if (forma.subject.value.length==0){
            alert ("<%=rb.getString("error.notSubject")%>");
            return false;
        }
        if (forma.mensaje.value.length==0){
            alert ("<%=rb.getString("error.notMessage")%>");
            return false;
        }
        return true;
    }
    function save() {
    }

    function send(forma) {
        forma.subject.value = "RV: " + forma.subOrg.value;
        forma.to.value = "";
        forma.action = "loadMail.do";
        disabledBtns(forma);
        forma.submit();
    }

    function response(forma) {
        forma.to.value = forma.from.value;
        forma.subject.value = "RE: " + forma.subOrg.value;
        //forma.mensaje.value = "";
        forma.action = "loadMail.do";
        disabledBtns(forma);
        forma.submit();
    }

</script>
<style>
.viewMessage
{
  background: white;
  color: black;
  border: 1px solid #FFFFFF;
  padding: 2;
  font-size: 12px;
  font-family: Arial, Helvetica, sans-serif;
  font-weight: normal;
  overflow: scroll;
}
</style>
</head>

<body class="bodyInternas">

    <logic:present name="sendMail" scope="session">
    <bean:define id ="mail" name="sendMail" type="com.desige.webDocuments.mail.forms.MailForm" scope="session"/>
        <form name="mails" action="" method="post">
            <table align=center border=0 width="100%">
                <tr>
                    <td colspan="2" class="pagesTitle">
                        <%=rb.getString("mail.viewMail")%>
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                            <tbody>
                                <tr>
                                    <td class="td_title_bc">
                                        <%=rb.getString("mail.viewMail")%>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </td>
                </tr>
                <tr>
                    <td class="td_gris_l" colspan="2">&nbsp;

                    </td>
                </tr>
                <tr>
<%--                    <td class="td_orange_l_b" width="18%">--%>
                    <td class="titleLeft" height="22" style="background: url(img/btn120.png); background-repeat: no-repeat" width="14%" valign="middle">
                        <%=rb.getString("mail.from")%>:
                    </td>
                    <td class="td_gris_l">
                        <bean:write name="sendMail" property="nameFrom"/>
                        <html:hidden name="sendMail" property="from"/>
                    </td>
                </tr>
                <tr>
                    <td class="titleLeft" height="22" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                        <%=rb.getString("mail.to")%>:
                    </td>
                    <td class="td_gris_l">
                        <font class="classText">
                            <bean:write name="sendMail" property="to"/>
                            <html:hidden name="sendMail" property="to"/>
                        </font>
                    </td>
                </tr>
                <tr>
                    <td class="titleLeft" height="22" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                        <%=rb.getString("mail.cc")%>:
                    </td>
                    <td class="td_gris_l">
                        <font class="classText">
                            <bean:write name="sendMail" property="cc"/>
                        </font>
                    </td>
                </tr>
                <tr>
                    <td class="titleLeft" height="22" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                        <%=rb.getString("mail.subject")%>:
                    </td>
                    <td class="td_gris_l">
                        <font class="classText">
                            <bean:write name="sendMail" property="subject"/>
                            <html:hidden name="sendMail" property="subject"/>
                            <input type="hidden" name="subOrg" value ="<bean:write name='sendMail' property='subject'/>"/>
                        </font>
                    </td>
                </tr>
                <tr>
                    <td colspan="4" align="left" class="txt_title">
                        &nbsp;
                    </td>
                </tr>
                <tr>
                    <td colspan="4" align="center">
                        <input type="button" class="boton" value="<%=rb.getString("btn.forward")%>" name="reSend" onClick="javascript:send(this.form);" style="width:100px" />
                        &nbsp;
                        <input type="button" class="boton" value="<%=rb.getString("btn.reply")%>" name="reply" onClick="javascript:response(this.form);" style="width:100px" />
                        &nbsp;
                        <input type="button" class="boton" value="<%=rb.getString("btn.back")%>" name="cancel" onClick="javascript:cancelar(this.form);" style="width:100px" />
                    </td>
                </tr>
                <tr>
                    <td colspan="4" align="left" class="txt_title">
                        &nbsp;
                    </td>
                </tr>
               <tr>
                    <td colspan="4" align="left" style="padding: 0px" class="td_gris_l">
                        <div align="left" class="clsMessageView" id='mensaje' style="width: 100%; height: 160px">
                            <%=mail.getMensaje()%>
                        </div>
                    </td>
                    <html:hidden name="sendMail" property="mensaje"/>
               </tr>
            </table>
        </form>
    </logic:present>
</body>
</html>
