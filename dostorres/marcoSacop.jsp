<!--
 * Title: marcoSacop.jsp <br/>
 * Copyright: (c) 2004 Desige Servicios de Computación<br/>
 * @author Ing. Simón Rodriguez.
 * @author Ing. Nelson Crespo.
 * @version WebDocuments v1.0
 * <br/>
 *       Changes:<br/>
 *           14/05/2005 (NC) Cambios en manejo de Registros
 *           03/08/2005 (SR) Se valida la variable primeravez, en caso de que sea la primeravez que entra el usuario
 *                           para informarle que cambie su password por seguridad.
 * </ul>
-->
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.Users"%>
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    StringBuffer onLoad = new StringBuffer("");
    String ok = (String)session.getAttribute("usuario");
    if ((ok != null) && (ok.compareTo("") != 0)) {
        onLoad.append(" onLoad=\"self.status='").append(rb.getString("pagin.welcome")).append(" ");
        onLoad.append(ok).append("'\"");
    } else{// El Usuario No se ha Logeado.... => Lo reboto...
        response.sendRedirect(rb.getString("href.logout"));
    }
    Users usuario = (Users)session.getAttribute("user");


    String pagesInit = "principalSacop.jsp";
 //   String superior="superior.jsp";
/*
    if (usuario.getEmail()== "") {
        pagesInit = "loadPerfil.do";
    }
*/

   // String primeravez = (String)(session.getAttribute("primeravez")!=null?session.getAttribute("primeravez"):"false");

  /*  if (primeravez.equalsIgnoreCase("true")) {
        pagesInit = "loadPerfil.do";
    } */
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
</head>
<script language="JavaScript">
    function closeSession() {
        location.href = "logout.do";
    }
</script>
            <frameset rows="98,*" cols="*" frameborder="no" border="0" framespacing="0" onUnload="closeSession();" <%=onLoad.toString()%>>
                <%--<frame src="<%=superior%>" name="superior" scrolling="NO" noresize >--%>
                <frame src="<%=pagesInit%>" name="info">
            </frameset>
<noframes>
<%--    <body <%=onLoad.toString()%>>--%>
<%--    </body >--%>
</noframes>
</html>
