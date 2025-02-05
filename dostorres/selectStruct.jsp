<%@ page language="java" %>
<%@ page import="com.desige.webDocuments.utils.ToolsHTML,
                 java.util.Locale,
                 java.util.ResourceBundle"%>
<html>
<%
	if(request.getParameter("toSelectValue")==null) {
		if(request.getAttribute("visible")==null || !String.valueOf(request.getAttribute("visible")).equals("true") ) {
			response.sendRedirect("loadAllStruct.do");
		}
	}

    ResourceBundle rb = ToolsHTML.getBundle(request);
    String ok = (String)session.getAttribute("usuario");
    if ((ok == null) || (ok.length() == 0)) {
        response.sendRedirect(rb.getString("href.logout"));
    }
%>
	<head>
	   <!-- selectStruct.jsp -->
		<title><%=rb.getString("enl.cbs")%></title>
		<meta http-equiv=Content-Type content="text/html; charset=windows-1252">
		<meta content="MSHTML 6.00.2800.1106" name=GENERATOR>
	</head>
    <frameset border="10" frameSpacing="0" rows="*" frameBorder="0" cols="70%,*" bordeColor="#FF0000">
        <frameset border="0" frameSpacing="0" rows="0,*" frameBorder="0" cols="0" bordeColor="#FF0000">
            <frame name="code" marginWidth=0 marginHeight=0 src="structToSelect.jsp" frameBorder=0 scrolling=auto>
            <frame name="menu" marginWidth=0 marginHeight=5 src="" frameBorder=0>
        </frameset>
        <frame name="text" marginWidth=5 marginHeight=5 src="blank.jsp?nameForma=<%=request.getParameter("nameForma")%>"/>
    </frameset>
	<--
    <frameset border="10" frameSpacing="0" rows="*" frameBorder="1" cols="70%,*" bordeColor="#FF0000">
        <frameset border="0" frameSpacing="0" rows="0px,*" frameBorder="1" cols="0" bordeColor="#FF0000">
            <frame name="code" marginWidth=0 marginHeight=0 src="structToSelect.jsp" frameBorder=0 scrolling=no>
            <frame name="menu" marginWidth=0 marginHeight=5 src="structToSelect.jsp" frameBorder=0>
        </frameset>
        <frame name="text" marginWidth=5 marginHeight=5 src="blank.jsp?nameForma=<%=request.getParameter("nameForma")%>"/>
    </frameset>
    -->

    <noFrames>
    </noFrames>
</html>
