<html>
<head>
<!-- Creacion de branch 29 03 2018-->
<script>
function getPathFromLocation() {
	var cad = document.location.href;
	cad = cad.replace("http://","");
	var arr = cad.split("/");
	return "http://"+arr[0]+"/"+arr[1]+"/";
}
</script>
</head>
<body>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<h1><%=basePath%></h1>
<h2>
<script>
document.write(document.location);
</script>
</h2>
<h2>
<script>
document.write(getPathFromLocation());
</script>
</h2>
<h2>
<%=getServletContext().getRealPath("/")%>
</h2>


<%

out.println("<br/>request.getAuthType() = ");out.println(request.getAuthType());
out.println("<br/>request.getContentLength() = ");out.println(String.valueOf(request.getContentLength()));
out.println("<br/>request.getContentType() = ");out.println(request.getContentType());
out.println("<br/>getServletContext().getRealPath(\"/\") = ");out.println(getServletContext().getRealPath("/"));
out.println("<br/>request.getPathInfo() = ");out.println(request.getPathInfo());
out.println("<br/>request.getPathTranslated() = ");out.println(request.getPathTranslated());
out.println("<br/>request.getQueryString() = ");out.println(request.getQueryString());
out.println("<br/>request.getRemoteAddr() = ");out.println(request.getRemoteAddr());
out.println("<br/>request.getRemoteHost() = ");out.println(request.getRemoteHost());
out.println("<br/>request.getRemoteUser() = ");out.println(request.getRemoteUser());
out.println("<br/>request.getMethod() = ");out.println(request.getMethod());
out.println("<br/>request.getMethod() = ");out.println(request.getServletPath());
out.println("<br/>request.getServerName() = ");out.println(request.getServerName());
out.println("<br/>request.getServerPort() = ");out.println(String.valueOf(request.getServerPort()));
out.println("<br/>request.getProtocol() = ");out.println(request.getProtocol());
out.println("<br/>getServletContext() = ");out.println(getServletContext().getServerInfo());

%>

</body>
</html>