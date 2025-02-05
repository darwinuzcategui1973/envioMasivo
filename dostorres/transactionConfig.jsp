<!--
 * Title: marcoConfig.jsp <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @version WebDocuments v3.0
-->
<html>
<head>

</head>
<script>
	function hacer() {
		alert(1);
	}
	
	function ocultar() {
		window.parent.parent.parent.document.getElementById("info").src = "administracion.do";
	}

	function informe() {
		window.parent.parent.parent.document.getElementById("info").src = "listTransactionConsulta.do";
	}

</script>
<!-- 
<frameset rows="*" cols="250,*" frameborder="no" framespacing="0" > 
    <frame name="configEast" src="traConfigEast.jsp" scrolling="NO" style="border:1px solid #efefef;">
	<frameset rows="50,*,50" cols="*" frameborder="no" framespacing="0" > 
	    <frame name="configNorth"  src="traConfigNorth.jsp" scrolling="NO" >
	    <frame name="configCenter"  src="traConfigCenter.jsp">
	    <frame name="configSouth"  src="traConfigSouth.jsp" scrolling="NO" >

	</frameset>
</frameset>
 -->
<frameset rows="50,*,50" cols="*" frameborder="no" framespacing="0" > 
    <frame name="configNorth"  src="traConfigNorth.jsp" scrolling="NO" >
    <frame name="configCenter"  src="traConfigCenter.jsp">
    <frame name="configSouth"  src="traConfigSouth.jsp" scrolling="NO" >

</frameset>
 -->
</html>
