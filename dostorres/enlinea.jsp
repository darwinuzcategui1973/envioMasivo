<jsp:include page="richeditDocType.jsp" /> 
<!--**
 * Title: enlinea.jsp <br/>
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
    	send();
    }

    function send() {
    	forma = document.sendMail
        updateRTEs();
        forma.mensaje.value = forma.richedit.value;
    }
    function getMensaje(){
    	return document.sendMail.mensaje.value;
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
    }

    function validar(forma){
        //si to interno esta vacio, colocamos lo que esta escribiendo en el texto
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
    <html:form action="/sendMail.do">
	    <span id="contenido" style="display:none"><%=request.getAttribute("documentoEnLinea")%></span>
        <html:textarea property="mensaje" style="display:none" />
        <table>
            <tr>
                <td class="fondoEditor" colspan="2" >
                    <jsp:include page="richedit.jsp" > 
                    	<jsp:param name="richedit" value="richedit"/>
                    	<jsp:param name="build" value="false"/>
                    </jsp:include>
                </td>
            </tr>
        </table>
		<script type="text/javascript">
			richedit.height=window.parent.document.body.scrollHeight-220;
			richedit.html=document.getElementById("contenido").innerHTML;
			richedit.build();
		        <%  String mensaje = (String)request.getSession().getAttribute("info");
		            if (mensaje!=null) {
		                out.println("alert('"+mensaje+"');");
		            }
		            request.getSession().removeAttribute("info");
		        %>
		</script>
    </html:form>
</body>
</html>


