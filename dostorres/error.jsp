<%@ page import="com.desige.webDocuments.utils.ToolsHTML"%>
<%@ page language="java" %>
    <%
        String target = request.getAttribute("target")!=null?(String)request.getAttribute("target"):"_parent";
        String pageRedirect = ((String)request.getAttribute("redirect")!=null)?(String)request.getAttribute("redirect"):"index.jsp";
        if (session.getAttribute("error")!=null) {
            if (pageRedirect.indexOf("?") > 0) {
                pageRedirect += "&" + request.getQueryString();
            } else {
                if (request.getQueryString()!=null) {
                    pageRedirect += "?" + request.getQueryString();
                }
            }
        }
        String idNode = (String)(request.getAttribute("idNodeSelected")!=null?request.getAttribute("idNodeSelected"):"");
        //System.out.println("target = " + target);
        //System.out.println("pageRedirect = " + pageRedirect);
        //System.out.println("idNode  = " + idNode );
        if (ToolsHTML.isEmptyOrNull(idNode)) {
            idNode = request.getParameter("idNodeSelected");
        }
    %>
    <form name="text" id="text" action="<%=pageRedirect%>">
        <input type="hidden" name="target" value="<%=target%>">
        <input type="hidden" name="idNodeSelected" value="<%=idNode%>">
    </form>
<script>
   function redirect(newTarget) {
        forma = document.getElementById("text");
        forma.target.value = newTarget;
        forma.submit();
   }
</script>
<body onLoad="javascript:redirect('<%=target%>');">
</body>