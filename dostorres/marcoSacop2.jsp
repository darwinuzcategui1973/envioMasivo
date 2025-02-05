<!--
 * Title: marcoSacop2.jsp <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @version WebDocuments v3.0
-->
<html>
<script>
	function hacer() {
		alert(1);
	}
</script>
<%
	String cadenaSrc = "CrearSacop.do";
	if(request.getParameter("id")!=null) {
		cadenaSrc = "CrearSacop.do?id=".concat((String)request.getParameter("id"));
	}
%>
<frameset rows="*" cols="250,*" frameborder="no" framespacing="0" > 
    <frame id="sacopEast" name="sacopEast" src="sacopEast.jsp" scrolling="NO" style="border:1px solid #efefef;">
	<frameset rows="50,*,50" cols="*" frameborder="no" framespacing="0" > 
	    <frame id="sacopNorth" name="sacopNorth"  src="sacopNorth.jsp" scrolling="NO" >
	    <frame id="sacopCenter" name="sacopCenter"  src="CrearSacop.do?idDocRelated=<%=request.getParameter("idDocRelated")%>">
	    <frame id="sacopSouth" name="sacopSouth"  src="sacopSouth.jsp" scrolling="NO" >
	</frameset>
</frameset>

</html>
