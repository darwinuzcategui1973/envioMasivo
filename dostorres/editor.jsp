<!--/**
 * Title: editor.java<br>
 * Copyright: (c) 2004 Desige Servicios de Computación<br/>
 *
 * @author Ing. Nelson Crespo   (NC)
 * @author Ing. Simón Rodriguéz (SR)
 * @version WebDocuments v1.0
 * <br>
 *     Changes:<br>
 * <ul>
 
*      <li> 25/04/2006 (SR) Se sustituyo /WebDocuments/.. por ./..

 * <ul>
 */-->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>Documento sin t&iacute;tulo</title>
<jsp:include page="meta.jsp" /> 
</head>

<body>
<!-- < o b j e c t id="richedit" style="BACKGROUND-COLOR: buttonface" data="./Editor/rte/richedit.html"
	width="80%" height="100%" type="text/x-scriptlet" onBlur="save()" VIEWASTEXT onLoad="save()">
</ o b j e c t >
 -->
<script language="JavaScript" event="onload" for="window">
	richedit.docHtml = "<%=request.getParameter("mensaje")%>";//theForm.Texto.innerText;
</script>
</body>
</html>
