<%@ page import="com.desige.webDocuments.utils.DesigeConf"%><html>
<html>
<head>
<%
    String nameFile = request.getAttribute("nameFile")!=null?(String)request.getAttribute("nameFile"):"";
%>
<%--<link rel=alternate media=print href="tmp/protegido.doc">--%>
</head>
<body onLoad="alert('Llego');">
<script>
	alert(1);
</script>
    <iframe src="<%=com.desige.webDocuments.utils.ToolsHTML.getFolderTmp()%>/<%=nameFile%>" width="50%" height="50%">
    </iframe>
</body>
</html>