<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML"%>
<%
    String parameters = ToolsHTML.parseParameters(request);
    String file = "viewFile.jsp" + parameters;
    String pie = "copyRight.jsp" + parameters;
    String nameDoc = "file:C:\\"+ request.getParameter("nameDoc");
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

<frameset rows="*,80" cols="*" frameborder="NO" border="0" framespacing="0">
    <frame src="<%=nameDoc%>" name="doc" scrolling="YES" noresize/>
    <frame src="<%=pie%>" name="info">
</frameset>
<noframes>
</noframes>
</html>
