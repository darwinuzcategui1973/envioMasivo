<!DOCTYPE>
<html>
<head>
<script>
<% String path = request.getRequestURL().toString().replace(request.getRequestURI(), request.getContextPath()) + "/" + (String)session.getAttribute("MANUAL_AYUDA"); %>
document.location.href = "<%=path%>";
</script>
<title><%=session.getAttribute("MANUAL_AYUDA")%></title>
</head>
<body>
	<h6><%=path%></h6>
</body>
</html>