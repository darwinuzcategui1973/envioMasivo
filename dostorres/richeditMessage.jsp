<!--  acuerdate de colocar         updateRTEs(); antes de hacer el submit -->

<script language="JavaScript" type="text/javascript">
<!--
//Usage: initRTE(imagesPath, includesPath, cssFile, genXHTML, encHTML)
initRTE("./images/", "./", "./estilo/", false);
//-->
</script>
<noscript><p><b>Javascript must be enabled to use this form.</b></p></noscript>

<script language="JavaScript" type="text/javascript">
<!--
//build new richTextEditor http://www.kevinroth.com/rte/usage.htm init richedit
var <%= request.getParameter("richedit") %> = new richTextEditor('<%= request.getParameter("richedit") %>');
//richedit.html = 'here&#39;s the "\<em\>preloaded\<\/em\>&nbsp;\<b\>content\<\/b\>"';
<%= request.getParameter("richedit") %>.html='<%= request.getParameter("defaultValue")!=null && !String.valueOf(request.getParameter("defaultValue")).equalsIgnoreCase("null") ? String.valueOf(request.getParameter("defaultValue")).replaceAll("\\n","").replaceAll("\\r","").replaceAll("'","\"") : "" %>';
//enable all commands for demo
<%= request.getParameter("richedit") %>.width="100%"

<%= request.getParameter("richedit") %>.toolbar1 = true;
<%= request.getParameter("richedit") %>.toolbar2 = true;
<%= request.getParameter("richedit") %>.readOnly = false;

<%= request.getParameter("richedit") %>.cmdFormatBlock = true;
<%= request.getParameter("richedit") %>.cmdFontName = true;
<%= request.getParameter("richedit") %>.cmdFontSize = true;
<%= request.getParameter("richedit") %>.cmdIncreaseFontSize = true;
<%= request.getParameter("richedit") %>.cmdDecreaseFontSize = true;

<%= request.getParameter("richedit") %>.cmdBold = true;
<%= request.getParameter("richedit") %>.cmdItalic = true;
<%= request.getParameter("richedit") %>.cmdUnderline = true;
<%= request.getParameter("richedit") %>.cmdStrikethrough = true;
<%= request.getParameter("richedit") %>.cmdSuperscript = true;
<%= request.getParameter("richedit") %>.cmdSubscript = true;

<%= request.getParameter("richedit") %>.cmdJustifyLeft = true;
<%= request.getParameter("richedit") %>.cmdJustifyCenter = true;
<%= request.getParameter("richedit") %>.cmdJustifyRight = true;
<%= request.getParameter("richedit") %>.cmdJustifyFull = true;

<%= request.getParameter("richedit") %>.cmdInsertHorizontalRule = false;
<%= request.getParameter("richedit") %>.cmdInsertOrderedList = false;
<%= request.getParameter("richedit") %>.cmdInsertUnorderedList = false;

<%= request.getParameter("richedit") %>.cmdOutdent = false;
<%= request.getParameter("richedit") %>.cmdIndent = false;
<%= request.getParameter("richedit") %>.cmdForeColor = true;
<%= request.getParameter("richedit") %>.cmdHiliteColor = true;
<%= request.getParameter("richedit") %>.cmdInsertLink = false;
<%= request.getParameter("richedit") %>.cmdInsertImage = false;
<%= request.getParameter("richedit") %>.cmdInsertSpecialChars = false;
<%= request.getParameter("richedit") %>.cmdInsertTable = false;
<%= request.getParameter("richedit") %>.cmdSpellcheck = false;

<%= request.getParameter("richedit") %>.cmdCut = true;
<%= request.getParameter("richedit") %>.cmdCopy = true;
<%= request.getParameter("richedit") %>.cmdPaste = true;
<%= request.getParameter("richedit") %>.cmdUndo = true;
<%= request.getParameter("richedit") %>.cmdRedo = true;
<%= request.getParameter("richedit") %>.cmdRemoveFormat = true;
<%= request.getParameter("richedit") %>.cmdUnlink = false;

<%= request.getParameter("richedit") %>.toggleSrc = false;

<%if (request.getParameter("height")!=null) {%> 
	<%= request.getParameter("richedit") %>.height=<%=request.getParameter("height")%>;
<%}%>

<%if (request.getParameter("build")==null || String.valueOf(request.getParameter("build")).equalsIgnoreCase("true") ) {%> 
	try {
	<%= request.getParameter("richedit") %>.build(); // esto debe ir antes de cerrar la etiqueta form
	} catch(e) {}
<%}%>
<%if (request.getParameter("execute")!=null) {%> 
	try {
		eval('<%= request.getParameter("execute") %>');; // esto debe ir antes de cerrar la etiqueta form
	} catch(e) {}
<%}%>

//-->
</script>                
