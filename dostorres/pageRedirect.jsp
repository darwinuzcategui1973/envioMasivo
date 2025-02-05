<!-- pageRedirect.jsp -->
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML"%>
<%@ page language="java" %>
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
%>
<html>
    <head>
    <title></title>
	<jsp:include page="meta.jsp" /> 
    </head>
    <body onLoad="sendRedirect();" class="bodyInternas">
        <script language="javascript">
            function sendRedirect () {
                forma = document.redirectPage;
                forma.target = "_parent";
                forma.action = "errorSession.jsp";
                forma.submit();
            }
        </script>
        <form name="redirectPage" action="">
        </form>
    </body>
</html>
