
<!--  acuerdate de colocar         updateRTEs(); antes de hacer el submit -->

<script language="JavaScript" type="text/javascript">
<!--
//Usage: initRTE(imagesPath, includesPath, cssFile, genXHTML, encHTML)
initRTE("./images/", "./", "./estilo/", false);
//-->
</script>
<noscript><p><b>Javascript must be enabled to use this form.</b></p></noscript>

<%
String defaultValue = null;
if(request.getParameter("deserialize")!=null) {
	defaultValue = request.getParameter("defaultValue");
	if(defaultValue!=null) {
		defaultValue = com.desige.webDocuments.utils.StringUtil.deserialize(defaultValue);
		defaultValue = defaultValue.replaceAll("\\n","").replaceAll("\\r","").replaceAll("'","\""); 		
	} else {
		defaultValue = "";
	}
} else {
		defaultValue = request.getParameter("defaultValue")!=null && !String.valueOf(request.getParameter("defaultValue")).equalsIgnoreCase("null") ? String.valueOf(request.getParameter("defaultValue")).replaceAll("\\n","").replaceAll("\\r","").replaceAll("'","\"") : "";	
}
%>

<script language="JavaScript" type="text/javascript">
<!--
//build new richTextEditor http://www.kevinroth.com/rte/usage.htm init richedit
var <%= request.getParameter("richedit") %> = new richTextEditor('<%= request.getParameter("richedit") %>');
//richedit.html = 'here&#39;s the "\<em\>preloaded\<\/em\>&nbsp;\<b\>content\<\/b\>"';
<%= request.getParameter("richedit") %>.html='<%= defaultValue %>';
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

<%= request.getParameter("richedit") %>.cmdInsertHorizontalRule = true;
<%= request.getParameter("richedit") %>.cmdInsertOrderedList = true;
<%= request.getParameter("richedit") %>.cmdInsertUnorderedList = true;

<%= request.getParameter("richedit") %>.cmdOutdent = true;
<%= request.getParameter("richedit") %>.cmdIndent = true;
<%= request.getParameter("richedit") %>.cmdForeColor = true;
<%= request.getParameter("richedit") %>.cmdHiliteColor = true;
<%= request.getParameter("richedit") %>.cmdInsertLink = true;
<%= request.getParameter("richedit") %>.cmdInsertImage = false;
<%= request.getParameter("richedit") %>.cmdInsertSpecialChars = true;
<%= request.getParameter("richedit") %>.cmdInsertTable = true;
<%= request.getParameter("richedit") %>.cmdSpellcheck = false;

<%= request.getParameter("richedit") %>.cmdCut = true;
<%= request.getParameter("richedit") %>.cmdCopy = true;
<%= request.getParameter("richedit") %>.cmdPaste = true;
<%= request.getParameter("richedit") %>.cmdUndo = true;
<%= request.getParameter("richedit") %>.cmdRedo = true;
<%= request.getParameter("richedit") %>.cmdRemoveFormat = true;
<%= request.getParameter("richedit") %>.cmdUnlink = true;

<%= request.getParameter("richedit") %>.toggleSrc = false;

<%if (request.getParameter("height")!=null) {%> 
	<%= request.getParameter("richedit") %>.height=<%=request.getParameter("height")%>;
<%}%>

<%if (request.getParameter("build")==null || String.valueOf(request.getParameter("build")).equalsIgnoreCase("true") ) {%> 
	<%= request.getParameter("richedit") %>.build(); // esto debe ir antes de cerrar la etiqueta form
<%}%>
//-->
</script>                
