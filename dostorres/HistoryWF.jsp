<%@ page language="java" %>
<html>
    <%
        String pagesInfo = (String)request.getAttribute("pages");
    //System.out.println(session.getAttribute("documentOfRejection"));
    %>
    <head>
		<title>History</title>
		<jsp:include page="meta.jsp" /> 
		<meta content="MSHTML 6.00.2800.1106" name=GENERATOR>
	</head>
    <frameset border="10" frameSpacing="2" rows="*" frameBorder="2" cols="20%,*" bordeColor="#FF0000">
        <frame name="opciones" marginWidth=0 marginHeight=5 src="HistoryWFIzq.jsp" frameBorder=0>
        <!--<frame name="history" marginWidth=5 marginHeight=5 src="showDataWorkFlow.jsp"/>-->
        <frame name="history" marginWidth=5 marginHeight=5 src="<%=pagesInfo%>"/>
    </frameset>
    <noFrames>
    </noFrames>
</html>
