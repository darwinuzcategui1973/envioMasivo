<!--
 * Title: marco.jsp <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Simón Rodriguez.
 * @author Ing. Nelson Crespo.
 * @version WebDocuments v3.0
 * <br/>
 *       Changes:<br/>
 *           14/05/2005 (NC) Cambios en manejo de Registros
 *           03/08/2005 (SR) Se valida la variable primeravez, en caso de que sea la primeravez que entra el usuario
 *                           para informarle que cambie su password por seguridad.
 * </ul>
-->
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.focus.util.ModuloBean,
                 com.desige.webDocuments.utils.beans.Users"%>
<%

    ResourceBundle rb = ToolsHTML.getBundle(request);
    ModuloBean modulo = ToolsHTML.validarLicencia();
    StringBuffer onLoad = new StringBuffer("");
    String ok = (String)session.getAttribute("usuario");
    Users usuario = (Users) session.getAttribute("user");
    
    String urlToGoAfterLogin = usuario.getActionToInvokeAfterLogin();
    usuario.setActionToInvokeAfterLogin(null);
    if((urlToGoAfterLogin != null) && (! urlToGoAfterLogin.startsWith(request.getContextPath()))){
    	urlToGoAfterLogin = request.getContextPath().concat(urlToGoAfterLogin);
    }
    
    if ((ok != null) && (ok.compareTo("") != 0)) {
        onLoad.append(" onLoad=\"self.status='").append(rb.getString("pagin.welcome")).append(" ");
        onLoad.append(ok).append("'");
        
        if(urlToGoAfterLogin != null){
        	onLoad.append(", open_full('").append(urlToGoAfterLogin).append("')\"");
        }else{
        	onLoad.append("\"");
        }
    } else{// El Usuario No se ha Logeado.... => Lo reboto...
        response.sendRedirect(rb.getString("href.logout"));
    }
    
    String pagesInit = "principal.jsp";
    String superior="superior.jsp";
    if (usuario.getEmail()!=null && usuario.getEmail().equalsIgnoreCase("")) {
        pagesInit = "loadPerfil.do";
    }
    String primeravez = (String)(session.getAttribute("primeravez")!=null?session.getAttribute("primeravez"):"false");

    if (primeravez.equalsIgnoreCase("true")) {
        pagesInit = "loadPerfil.do";
    }
%>
<html>
<head>
<title><%=rb.getString("principal.title")%> versi&oacute;n <%=rb.getString("lic.version")%> - <%=modulo.getEdicionFull()%> - <%=modulo.getEmpresa()%></title>
<jsp:include page="meta.jsp" /> 
</head>

<script type="text/javascript">
    function closeSession() {
    	window.opener.cerrarSession();
    }
    
    function open_full(dir){
    	if(dir != "null"){
    		alert("<%= rb.getString("afterLogin.newWindowMsg") %>");
    		var Ancho=screen.availWidth-10;
        	var Alto=screen.availHeight-28;
        	var features="toolbar=no,status=yes,location=no,directories=no,resizable=yes,menubar=no,scrollbars=no,top=0,left=0,width="+Ancho+",height="+Alto+'"';
        	myWindow = window.open(dir,'_blank',features);
    	}
	}
    
    
</script>

<!--            <frameset id="principal" rows="93,*" cols="*" frameborder="no" border="0" framespacing="0" onUnload="closeSession();" <%=onLoad.toString()%>> -->
            <frameset id="principal" rows="93,*" cols="*" frameborder="no" border="0" framespacing="0" <%=onLoad.toString()%> onUnload="closeSession();"> 
                <frame src="<%=superior%>" id="superior" name="superior" scrolling="NO" style="width:100%" >
                <frame src="<%=pagesInit%>" id="info" name="info" marginwidth="0px" marginheight="0px" >
            </frameset>
<noframes>
<%--    <body <%=onLoad.toString()%>>--%>
<%--    </body >--%>
</noframes>
</html>
 