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

    function enviar() {
    	send(document.sendMail);
    }

    function send(forma) {
        updateRTEs();
    
        forma.mensaje.value = forma.richedit.value;
        if (validar(forma)) {
            forma.submit();
        }
    }

    function sendallusers(forma) {
    	updateRTEs();
        forma.mensaje.value = forma.richedit.value;
        var sw=true;
         //el titulo de lo que escribe
        if (forma.subject.value.length==0) {
            alert ("<%=rb.getString("error.notSubject")%>");
            sw = false;
            return false;
        }
        if (forma.mensaje.value.length==0){
            alert ("<%=rb.getString("error.notMessage")%>");
            sw=false;
            return false;
        }
        if (sw){
           forma.submit();
        }
    }
    function cancelar(forma){
    	<%if(request.getParameter("to")!=null) {%>
    		forma.target="bandeja";
            forma.action = "newUserGrupo.do";
            forma.submit();
    	<%} else {%>
        <logic:present name="width" scope="session">
            forma.action = "showDataDocument.do";
            forma.submit();
        </logic:present>
        <logic:notPresent name="width" scope="session">
	        forma.action = "loadInbox.do";
	        forma.submit();
        </logic:notPresent>
    	<%}%>
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
    <html:form action="/sendMail.do" target="info">
        <html:textarea property="mensaje" style="display:none" />
        <logic:present name="width" scope="session">
            <input type="hidden" name="goTo" value="showDoc"/>
            <input type="hidden" name="idDocument" value="<%=session.getAttribute("idDocument")%>"/>
        </logic:present>
        <table align=center border="0" width="100%">
            <tr class="<%=request.getParameter("adm")!=null?"":"none"%>">
                <td class="pagesTitle" colspan="2" >
                   <table cellspacing="0" cellpadding="0" border="0">
                   	<tr></td>
	                    <table class="toolBar borderTopBlack" cellpadding="0" bgcolor="#efefef">
	                    	<tr>
	                    		<td width="1px"><img src="menu-images/barraini.gif"></td>
	                    		<td class="toolBarMiddle"> 
	                    			<a class="toolBarMiddleText" href='#' onClick="<%=(request.getAttribute("allUsers")!=null?"javascript:sendallusers(document.sendMail);":"javascript:send(document.sendMail);")%>" target='_self'>
	                    				<img src="menu-images/mailSend.jpg" style="border:1px solid #efefef" onmouseover="this.style.border='1px solid #afafaf'" onmouseout="this.style.border='1px solid #efefef'" title="<%=rb.getString("btn.send")%>">
	                    			</a>
	                    		</td>
	                    		<td width="1px"><img src="menu-images/barrasep.gif"></td>
	                    		<td class="toolBarMiddle"> 
	                    			<a class="toolBarMiddleText" href='#' onClick="javascript:cancelar(document.sendMail);" target='_self'>
	                    				<img src="menu-images/cancel.jpg" style="border:1px solid #efefef" onmouseover="this.style.border='1px solid #afafaf'" onmouseout="this.style.border='1px solid #efefef'" title="<%=rb.getString("btn.cancel")%>">
	                    			</a>
	                    		</td>
                   	</td></tr>
                   </table>
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
                <td height="22" class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" width="12%" valign="middle">

                    <%=rb.getString("mail.from")%>:
                </td>
                <td class="td_gris_l">
                    <bean:write name="sendMail" property="nameFrom"/>
                    <html:hidden name="sendMail" property="from"/>
                </td>
            </tr>
            <logic:notPresent name="allUsers" scope="request">
                <tr>
                    <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat;border:2px outset #efefef;" valign="middle">
                        <a class="ahref_w" href="javascript:loadAddressBook('to','names','idTo',document.sendMail.idTo.value)">
                            <img src="icons/book_open.png" border="0">&nbsp;
                            <%=rb.getString("mail.to")%>:
                        </a>
                    </td>
                    <td class="td_gris_l">
                        <html:hidden name="sendMail" property="idTo"/>
                        <html:hidden name="sendMail" property="to"/>
                        <html:text property="names" name="sendMail" disabled='true' size="120" styleClass="classText" style="width:99%"/>
                    </td>
                </tr>
                <tr>
                    <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat;border:2px outset #efefef;" valign="middle">
                        <a class="ahref_w" href="javascript:loadAddressBook('cc','copyTo','idCC',document.sendMail.idCC.value)">
                            <img src="icons/book_open.png" border="0">&nbsp;
                            <%=rb.getString("mail.cc")%>:
                        </a>
                    </td>
                    <td class="td_gris_l">
                        <html:hidden name="sendMail" property="idCC"/>
                        <html:hidden name="sendMail" property="cc"/>
                        <html:text property="copyTo" name="sendMail" disabled='true' size="120" styleClass="classText" style="width:99%"/>
                    </td>
                </tr>
            </logic:notPresent>
            <logic:present name="allUsers" scope="request">
                <html:hidden name="sendMail" property="to"/>
                <input type="hidden" name="allUsers" value="true"/>
            </logic:present>
            <tr>
                <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                    <%=rb.getString("mail.subject")%>:
                </td>
                <td class="td_gris_l">
                    <html:text property="subject"  size="120" styleClass="classText" style="width:99%"/>
                </td>
            </tr>
            <tr>
                <td class="fondoEditor" colspan="2" >
                    <jsp:include page="richedit.jsp" > 
                    	<jsp:param name="richedit" value="richedit"/>
                    	<jsp:param name="build" value="false"/>
                    </jsp:include>
					<script language="javascript">
						richedit.height=window.parent.document.body.scrollHeight-205-<%=request.getParameter("to")!=null?"60":"0"%>;
						richedit.build();
					</script>
                </td>
            </tr>
            <logic:present name="allUsers" scope="request">
            <tr>
                <td colspan="2" align="center">
                <logic:present name="allUsers" scope="request">
					<a  class="boton botonLinkBack" id="lnkEditar" name="lnkEditar"  title="Haga click en abrir cuando el navegador se lo pregunte."  onClick="javascript:sendallusers(document.sendMail);" >
                        <%=rb.getString("btn.send")%>
					</a>
                 </logic:present>
                 <logic:notPresent name="allUsers" scope="request">
						<a  class="boton botonLinkBack" id="lnkEditar" name="lnkEditar"  title="Haga click en abrir cuando el navegador se lo pregunte."  onClick="javascript:send(document.sendMail);" >
	                        <%=rb.getString("btn.send")%>
						</a>
                  </logic:notPresent>
                    &nbsp;
					<a  class="boton botonLinkBack" id="lnkEditar" name="lnkEditar"  title="Haga click en abrir cuando el navegador se lo pregunte."  onClick="javascript:history.back();" >
                        <%=rb.getString("btn.cancel")%>
					</a>
                </td>
                </logic:present>
            </tr>
        </table>
    </html:form>
    <script language="JavaScript" event="onload" for="window">
        <%  String mensaje = (String)request.getSession().getAttribute("info");
            if (mensaje!=null) {
                out.println("alert('"+mensaje+"');");
            }
            request.getSession().removeAttribute("info");
        %>
    </script>
</body>
</html>
