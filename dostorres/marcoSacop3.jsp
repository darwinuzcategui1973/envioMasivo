<!--
 * Title: marcoSacop3.jsp <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @version WebDocuments v3.0
-->
<html>
<script>
	function hacer() {
		alert("marcoSacop3.jsp");
	}
</script>
    <frameset rows="*" cols="*" frameborder="no" framespacing="0" > 
	    <frame id="sacopCenter" name="sacopCenter"  src="<%=request.getParameter("url").replaceAll("_","&")%>">
	</frameset>
</html>
