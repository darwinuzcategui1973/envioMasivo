<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.Users"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
%>
<html>
<head>
<title></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script language="JavaScript">
    function searchDocument(forma) {
        if (forma.keys && forma.keys.value.length > 0) {
            forma.target = "info";
            forma.submit();
        } else {
            alert('<%=rb.getString("sch.notDesc")%>');
        }
    }
    function checkKey(evt,forma) {
        var charCode = (evt.which) ? ect.which : event.keyCode;
        if (charCode == 13){
            searchDocument(forma);
        }
    }
</script>
</head>
<%--<body background="img/menuDer.gif" bgcolor="#FFFFFF">--%>
<body style="background: url(img/menuDer.gif); background-repeat: no-repeat" bgcolor="#FFFFFF">
</body>
</html>
