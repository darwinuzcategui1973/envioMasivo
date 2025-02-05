<%@ page language="java" %>
<%@ page import="com.desige.webDocuments.utils.ToolsHTML,
                 java.util.ResourceBundle"%>
<html>
<%
	if(request.getAttribute("visible")==null || !String.valueOf(request.getAttribute("visible")).equals("true") ) {
		//response.sendRedirect("loadAllStruct.do"); 
	}

    ResourceBundle rb = ToolsHTML.getBundle(request);
    String ok = (String)session.getAttribute("usuario");
    if ((ok == null) || (ok.length() == 0)) {
        response.sendRedirect(rb.getString("href.logout"));
    }
    String pagesInfo = request.getAttribute("pagesInfo")!=null?(String)request.getAttribute("pagesInfo"):"main-tree.jsp";
%>
	<head>

        <title>Struct</title>
		<meta http-equiv=Content-Type content="text/html; charset=windows-1252">
		<meta content="MSHTML 6.00.2800.1106" name=GENERATOR>
	</head>
    <frameset border="0" frameSpacing="2" frameBorder="0" cols="<%=ToolsHTML.ANCHO_MENU%>,*" bordeColor="#FF0000">
        <frameset id="marcoEOC" border="0" frameSpacing="0" rows="32,*,300" frameBorder="0" bordeColor="#FF0000">
            <frame id="code" name="code" marginWidth=0 marginHeight=0 src="code-tree.jsp" frameBorder="0" scrolling="no">
            <frame id="menu" name="menu" marginWidth=0 marginHeight=0 src="vacio.jsp" frameBorder="0">
            <frame id="modulos" name="modulos" marginWidth=0 marginHeight=0 src="modulos.jsp" frameBorder="0" scrolling="NO">
        </frameset>
    		    <frame id="text" frameBorder="0" name="text" marginWidth="0" marginHeight="0" src="<%=pagesInfo%>" />
    		    
    	<!-- esto es por si hay que activar :hove underline en los estilos
        <frameset border="0" frameSpacing="0" rows="0" cols="5,*" frameBorder="0"  bordeColor="#FF0000">
    	    <frame id="text1" frameBorder="0" name="text1" marginWidth="0" marginHeight="0" src="vacio.jsp"/>
	        <frameset border="0" frameSpacing="0" rows="3,*" cols="0" frameBorder="0"  bordeColor="#FF0000">
    		    <frame id="text2" frameBorder="0" name="text2" marginWidth="0" marginHeight="0" src="vacio.jsp"/>
    		    <frame id="text" frameBorder="0" name="text" marginWidth="0" marginHeight="0" src="<%=pagesInfo%>" />
		    </frameset>
	    </frameset>
    	 -->
    </frameset>
    <noFrames>
    </noFrames>
</html>
