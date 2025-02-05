<!--/**
 * Title: viewDocumentFirmas.jsp <br>
 * Copyright: (c) 2003 Focus Consulting<br>
 */-->
<% 
	StringBuffer file = new StringBuffer();
	file.append("http://").append(request.getServerName()).append(":").append(request.getServerPort()).append(request.getContextPath());
	file.append("/tmp/pag").append(request.getParameter("a")).append("_").append(request.getParameter("b")).append("_").append(request.getParameter("c")).append(".pdf");
%>
<body onkeydown="return false" onload="carga()">
<%if (request.getHeader("USER-AGENT").indexOf("MSIE")!=-1) { %>
	<iframe name="wndDocumentFirmas" src="<%=file.toString()%>" style="width:103%;height:101%;position:absolute;top:0px;left:0px"/></iframe>
	<iframe name="barrapdfFirmas" src="barrapdfFirmas.jsp" style="height:35px;position:absolute;top:0px;left:0px;width:103%" scrolling="no" frameborder="0"></iframe>
<%} else {%>
	<iframe name="wndDocumentFirmas" src="<%=file.toString()%>" style="width:99%;height:99%;position:absolute;top:0px;left:0px"/></iframe>
	<iframe name="barrapdfFirmas" src="barrapdfFirmas.jsp" style="height:35px;position:absolute;top:0px;left:0px;width:100%" scrolling="no" frameborder="0"></iframe>
<%}%>
</body>
</html>
<script language="javascript">
	function carga() {
		window.document.body.scroll = 'no';  
	}

document.oncontextmenu = function(){return false;}
</script>
