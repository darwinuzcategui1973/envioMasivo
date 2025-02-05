<%@page import="com.desige.webDocuments.persistent.managers.HandlerParameters"%>
<%@page import="com.desige.webDocuments.persistent.managers.HandlerDBUser"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="com.focus.jndi.*"%>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<!-- 
Códigos de Error
525    user not found
52e    invalid credentials
530    not permitted to logon at this time
531    not permitted to logon at this workstation
532    password expired
533    account disabled
701    account expired
773    user must reset password
775    user account locked
-->
<%
	String resultado = "";
    String ldapURL = "";
    String dominio = "";
    String usuario = "";
    
if(request.getParameter("envio") != null){
	usuario = request.getParameter("usuario");
	ldapURL = request.getParameter("ldapUrl");
    dominio = request.getParameter("dominio");
    
	TestLoginActiveDirectory test = new TestLoginActiveDirectory(
			   request.getParameter("ldapUrl"),
			   request.getParameter("dominio"),
			   request.getParameter("usuario"),
			   request.getParameter("clave"));
	
    resultado = test.executeConnnection();
} else {
	ldapURL = HandlerParameters.getParameter("ldapurl");
    dominio = HandlerParameters.getParameter("domain");
}
%>
<body>
    <form method="post" action="testActiveDirectory.jsp">
        <input type="hidden" id="envio" name="envio" value="1" />
        
        <table align="center">
            <tr>
	           <td>URL Active Directory</td>
	           <td><input type="text" name="ldapUrl" id="ldapUrl" value="<%=ldapURL%>"/></td>
	        </tr>
	        <tr>
               <td>Dominio Active Directory</td>
               <td><input type="text" name="dominio" id="dominio" value="<%=dominio%>"/></td>
            </tr>
            <tr>
               <td>Usuario Active Directory</td>
               <td><input type="text" name="usuario" id="usuario" value="<%=usuario%>"/></td>
            </tr>
            <tr>
               <td>Clave</td>
               <td><input type="password" name="clave" id="clave" value=""/></td>
            </tr>
            <tr>
               <td colspan="2">
                   <input type="submit" name="enviar" value="Procesar"/>
               </td>
            </tr>
            <tr>
               <td colspan="2">
                   <%if(!"".equals(resultado.trim())){
                	   out.print("<h1>Resultado:" + resultado + "</h1>");
                   }
                   %>
               </td>
            </tr>
	    </table>
    </form>
</body>
</html>