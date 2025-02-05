<%@ page import="com.desige.webDocuments.utils.beans.Users"%>
<%
    Users usuario = (Users)session.getAttribute("user");
%>
<html>
<head>
</head>
<body oncontextmenu="return false" onkeydown="return false" style="background: url(img/barraSup2.png); background-repeat: no-repeat">
<table border="0"  cellspacing="2" width="100%" cellpadding="2" style="position:absolute;left:0px;top:0px;" >
<tr><td align="right">&nbsp;
</td></tr>
</table>
<script language="javascript">
document.oncontextmenu = function(){return false;}
</script>
</body>
</html>
