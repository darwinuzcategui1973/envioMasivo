<%@ page language="java" %>
<%@ page import="com.desige.webDocuments.utils.ToolsHTML,
                 java.util.Locale,
                 java.util.ResourceBundle"%>
<html>
	<head>
		<title>Struct</title>
		<jsp:include page="meta.jsp" /> 
		<meta content="MSHTML 6.00.2800.1106" name=GENERATOR>
	</head>
    <frameset border="10" frameSpacing="2" rows="*" frameBorder="2" cols="20%,*" bordeColor="#FF0000">
        <frame name="opciones" marginWidth=0 marginHeight=5 src="HistoryStructIzq.jsp" frameBorder=0>
        <%if(request.getAttribute("historyViews")!=null){%>
        <frame name="history" marginWidth=5 marginHeight=5 src="showDataStructViews.jsp"/>
        <%} else {%>
        <frame name="history" marginWidth=5 marginHeight=5 src="showDataStruct.jsp"/>
        <%}%>
    </frameset>
    <noFrames>
    </noFrames>
</html>
