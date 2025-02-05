<%@ page import="com.desige.webDocuments.utils.ToolsHTML"%>
<%
    String parameters = ToolsHTML.parseParameters(request);
    String file = "viewFile.jsp" + parameters;
    String pie = "copyRight.jsp" + parameters;
%>
<html>
<head>
<script>
    var doc = null;
</script>
<title>
</title>
<jsp:include page="meta.jsp" /> 
</head>
<frameset rows="*,140" cols="*" frameborder="NO" border="0" framespacing="0">
    <frame src="<%=file%>" name="doc" scrolling="YES" noresize/>
    <frame src="<%=pie%>" name="info2">
</frameset>
<noframes>
</noframes>

</html>
