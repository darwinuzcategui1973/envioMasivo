<jsp:include page="richeditDocType.jsp" /> 
<!--**
 * Title: sendMail.jsp <br/>
 * Copyright: (c) 2004 Desige Servicios de Computación<br/>
 * @author Ing. Nelson Crespo
 * @author Ing. Simón Rodriguéz. (SR)
 * @version WebDocuments v1.0
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li> 27-08-2004 (NC) Creation </li>
 *      <li> 11-10-2006 (SR)ruta de librerias,history.back()</li>
 *      <li> 25/04/2006 (SR) Se sustituyo /WebDocuments/.. por ./..</li>
 *      <li> 25/05/2006 (SR) Se corrgio error en envar mails a todos l usuarios</li>

 </ul>

 */-->

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
<jsp:include page="richeditHead.jsp" /> 

<script language="JavaScript">

    setDimensionScreen();

    function send(forma) {
        updateRTEs();
    
        forma.mensaje.value = forma.richedit.value;
        if (validar(forma)) {
            forma.submit();
        }
    }

    function cancelar(forma){
        history.back();
    }

    function validar(forma){
        //si to interno esta vacio, colocamos lo que esta escribiendo en el texto
        if (forma.to.value.length==0){
            forma.to.value=forma.names.value;
            forma.idTo.value=forma.names.value;
        }
        if (forma.names.value.length==0){
            alert ("<%=rb.getString("error.notTo")%>");
            return false;
        }
        //el titulo de lo que escribe
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

    function loadAddressBook(input,values,ids,selecteds) {
        abrirVentana("<%=rb.getString("href.contacts")%>?showLinkSystem=true&nameForm=sendMail&input="+input+"&value="+values+"&ids="+ids+"&selected="+selecteds,500,400);
    }

    function save(){
<%--        document.sendMail.richedit.docHtml = document.sendMail.mensaje.innerText;--%>
    }
</script>
</head>

<body class="bodyInternas">
    <html:form action="/sendMailRecord.do">
        <table align=center border=0 width="100%">
            <tr>
                <td class="pagesTitle" colspan="2">
                    <%=rb.getString("mail.title")%>
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                        <tbody>
                            <tr>
                                <td class="td_title_bc" height="21">
                                    <%=rb.getString("mail.title")%>
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
                <td height="22" class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" width="12%" valign="middle">

                    <%=rb.getString("mail.from")%>:
                </td>
                <td class="td_gris_l">
                    <bean:write name="sendMail" property="nameFrom"/>
                    <html:hidden name="sendMail" property="from"/>
                </td>
            </tr>

                <tr>
                    <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                        <a class="ahref_w" href="javascript:loadAddressBook('to','names','idTo',document.sendMail.idTo.value)">
                            <%=rb.getString("mail.to")%>:
                        </a>
                    </td>
                    <td class="td_gris_l">
                        <html:hidden name="sendMail" property="idTo"/>
                        <html:hidden name="sendMail" property="to"/>
                        <html:text property="names" name="sendMail" disabled='true' size="120" styleClass="classText"  />
                    </td>
                </tr>
                <tr>
                    <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                        <a class="ahref_w" href="javascript:loadAddressBook('cc','copyTo','idCC',document.sendMail.idCC.value)">
                            <%=rb.getString("mail.cc")%>:
                        </a>
                    </td>
                    <td class="td_gris_l">
                        <html:hidden name="sendMail" property="idCC"/>
                        <html:hidden name="sendMail" property="cc"/>
                        <html:text property="copyTo" name="sendMail" disabled='true' size="120" styleClass="classText"  />
                    </td>
                </tr>

            <tr>
                <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                    <%=rb.getString("mail.subject")%>:
                </td>
                <td class="td_gris_l">
                    <html:text property="subject"  size="120" styleClass="classText"/>
                </td>
            </tr>
            <html:textarea property="mensaje" style="display:none" />
            <tr>
                <td class="fondoEditor" colspan="2" >
                    <jsp:include page="richedit.jsp" > 
                    	<jsp:param name="richedit" value="richedit"/>
                    </jsp:include>
                </td>
            </tr>
            <tr>
                <td colspan="2" align="center">
                	<a  class="boton botonLinkBack" id="lnkEditar" name="lnkEditar"  title="Haga click en abrir cuando el navegador se lo pregunte."  onClick="javascript:send(document.sendMail);" >
	                	<%=rb.getString("btn.send")%>
					</a>
	                &nbsp;
					<a  class="boton botonLinkBack" id="lnkEditar" name="lnkEditar"  title="Haga click en abrir cuando el navegador se lo pregunte."  onClick="javascript:cancelar(document.sendMail);" >
                        <%=rb.getString("btn.cancel")%>
					</a>
                </td>
            </tr>
        </table>
    </html:form>
    <script language="JavaScript" event="onload" for="window">
        <%  String mensaje = (String)request.getSession().getAttribute("info");
            if (mensaje!=null) {
                out.println("alert('"+mensaje+"');");
                request.getSession().removeAttribute("info");
            }
        %>
    </script>
</body>
</html>
