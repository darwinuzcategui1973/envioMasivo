<!--
 * Title: marcoSacop4.jsp <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @version WebDocuments v3.0
-->
<html>
<script>
	function hacer() {
		alert("marcoSacop4.jsp");
	}
</script>
<frameset rows="*" cols="250,*" frameborder="no" framespacing="0" > 
    <frame name="sacopEast" src="sacopEast.jsp" scrolling="NO" style="border:1px solid #efefef;">
	<frameset rows="50,*,50" cols="*" frameborder="no" framespacing="0" > 
	    <frame name="sacopNorth"  src="sacopNorth.jsp" scrolling="NO" >
	    <frame id="sacopCenter" name="sacopCenter"  src="<%=request.getParameter("url").replaceAll("_","&")%>">
	    <frame name="sacopSouth"  src="sacopSouth.jsp" scrolling="NO" >
	</frameset>
</frameset>

</html>
