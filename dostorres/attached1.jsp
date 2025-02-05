<%@ page import="java.util.ArrayList" %>
<% ArrayList lista = (ArrayList) request.getAttribute("lista"); %>
<!-- /**
 * Title: attached1.jsp<br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 */ -->
<body>
<%for(int i=0;i<lista.size();i++){%>
<img src="file:///<%=lista.get(i)%>"/>
<%}%>
</body>
</html>
