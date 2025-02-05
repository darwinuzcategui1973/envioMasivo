<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title></title>
<jsp:include page="meta.jsp" /> 
<script>
    function sendForm() {
        document.showDocument.submit();
    }
</script>
</head>
<body onLoad="javascript:sendForm();">
    <form name="showDocument" action="editCheckOut.do" method="Post" >
        <input type="hidden" name="nameFile" value="<%=request.getParameter("nameFile")%>"/>
        <input type="hidden" name="idCheckOut" value="<%=request.getParameter("idCheckOut")%>"/>
        <input type="hidden" name="idDocument" value="<%=request.getParameter("idDocument")%>"/>
        <input type="hidden" name="numVersion" value="<%=request.getParameter("numVersion")%>"/>

    </form>
</body>
</html>
