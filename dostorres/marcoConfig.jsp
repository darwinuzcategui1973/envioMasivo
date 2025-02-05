<!--
 * Title: marcoConfig.jsp <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @version WebDocuments v3.0
-->
<html>
<script>
	function hacer() {
		alert(1);
	}
</script>
<frameset rows="*" cols="250,*" frameborder="no" framespacing="0" > 
    <frame name="configEast" src="configEast.jsp" scrolling="NO" style="border:1px solid #efefef;">
	<frameset rows="50,*,50" cols="*" frameborder="no" framespacing="0" > 
	    <frame name="configNorth"  src="configNorth.jsp" scrolling="NO" >
	    <frame name="configCenter"  src="loadParameters.do">
	    <frame name="configSouth"  src="configSouth.jsp" scrolling="NO" >

	</frameset>
</frameset>

</html>
