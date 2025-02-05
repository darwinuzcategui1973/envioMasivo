<%@ page import="java.util.ResourceBundle,com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 com.desige.webDocuments.persistent.managers.HandlerParameters,
                 com.desige.webDocuments.persistent.managers.HandlerBD"%>
<!-- insertContacts.jsp -->

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    StringBuffer onLoad = new StringBuffer("");
    String ok = (String)session.getAttribute("usuario");
    String info = (String)request.getAttribute("info");
    if (ToolsHTML.checkValue(ok)) {
        onLoad.append(" onLoad=\"self.status='").append(rb.getString("pagin.welcome")).append(" ");
        onLoad.append(ok).append("'");
    } else{// El Usuario No se ha Logeado.... => Lo reboto...
        response.sendRedirect(rb.getString("href.logout"));
    }
    if (ToolsHTML.checkValue(info)){
        onLoad.append(";alert('").append(info).append("')");
    }
    if (onLoad.length()>0){
        onLoad.append("\"");
    }
    String cmd = (String)request.getAttribute("cmd");
    if (cmd==null){
        cmd = SuperActionForm.cmdInsert;
    }
    pageContext.setAttribute("cmd",cmd);
    String inputReturn = (String)session.getAttribute("input");
    if (inputReturn==null){
        inputReturn = "";
    }
    String valueReturn = (String)session.getAttribute("value");
    if (valueReturn==null){
        valueReturn = "";
    }
    String forma = "/insertContacts.do";
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script language="JavaScript">
    function cancelar() {
        history.back();
    }

	function addContact() {
		adicionar(document.addressMail);
	}
	
    function adicionar(forma) {
         if (forma.nombre && forma.nombre.value.length == 0) {
            alert("<%=rb.getString("err.notName")%>");
            return false;
        }
        if (forma.apellido && forma.apellido.value.length == 0) {
            alert("<%=rb.getString("err.notApe")%>");
            return false;
        }       
        if (forma.email && forma.email.value.length == 0) {
            alert("<%=rb.getString("err.notMail")%>");
            return false;
        }
        //Validando el Formato del Correo
        <% if(String.valueOf(HandlerParameters.PARAMETROS.getValidateEmail()).equals("0")) {%>

	        if (!(/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(forma.email.value))) {
	            alert ("<%=rb.getString("E0099")%>");
            	return false;
	        }
        	var dominioCorreo = forma.email.value.substring(forma.email.value.indexOf("@")+1);
        	//Validando el dominio de correo
        	<% if(HandlerParameters.PARAMETROS.getDomainEmail()!=null && !HandlerParameters.PARAMETROS.getDomainEmail().trim().equals("")) {
	        	String[]  dominios = HandlerParameters.PARAMETROS.getDomainEmail().trim().split(",");
	        	StringBuffer sb = new StringBuffer();
	        	String sep = "";
        		for(int item=0; item < dominios.length; item++) {
        			if(!dominios[item].trim().equals("")) {
        				sb.append(sep);
        				sb.append("dominioCorreo=='");
        				sb.append(dominios[item].trim());
        				sb.append("'");
        				sep = " || ";
					}        		
        		}
        		if(sb.toString().length()>0) {%>
        			if(!(<%=sb.toString()%>)) {
        				alert("<%=rb.getString("err.NotValidDomain")%>");
        				return false;
        			}
        		<%}
        	}%>
        
        <%}%>
        
        forma.submit();
    }

</script>
</head>

<body class="bodyInternas" <%=onLoad.toString()%>>
<logic:equal value="<%=SuperActionForm.cmdEdit%>" name="cmd" >
    <%
        forma = "/insertContacts.do";
    %>
</logic:equal>
<%--<html:form action="/insertContacts.do">--%>
<html:form action="<%=forma%>">
    <table align=center border=0 width="100%">
        <html:hidden property="cmd" value="<%=cmd%>"/>
        <table align=center border=0 width="100%">
            <tr>
                <td colspan="2">
                    <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                        <tbody>
                            <tr>
                                <td class="td_title_bc" height="23 px" >
                                    <%=rb.getString("contacts.new")%>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </td>
            </tr>
            <tr>
<%--                <td class="td_orange_l_b" width="30%">--%>
                <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" width="16%" valign="middle">
                    <%=rb.getString("contacts.nombre")%>:
                </td>
                <td width="*" >
                    <html:text property="nombre" size="30" styleClass="classText"/>
                </td>
            </tr>
            <tr>
<%--                <td class="td_orange_l_b" width="40%">--%>
                <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                    <%=rb.getString("contacts.apellido")%>:
                </td>
                <td>
                    <html:text property="apellido" size="30" styleClass="classText"/>
                </td>
            </tr>
            <tr>
<%--                <td class="td_orange_l_b" width="25%">--%>
                <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                    <%=rb.getString("contacts.email")%>:
                </td>
                <td>
                    <html:text property="email" size="30" styleClass="classText"/>
                </td>
            </tr>
            <tr class="none">
                <td colspan="2" >
                    <center>
                       <input type="button" class="boton" value="<%=rb.getString("btn.save")%>" onClick="javascript:adicionar(this.form);"/>
                        &nbsp;
                        <input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>" onClick="javascript:cancelar();"/>
                    </center>
                </td>
            </tr>
        </table>
    </table>
</html:form>
</body>
</html>
<script language="javascript" event="onload" for="window">
	try{
		window.parent.modificarUrl();
		document.getElementById("buscarTexto").className="none";
	}catch(e){}
</script>
