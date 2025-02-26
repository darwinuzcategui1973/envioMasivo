<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<!--
// Cross-Browser Rich Text Editor  
// Written by Kevin Roth (http://www.kevinroth.com/rte/)
// Visit the support forums at http://www.kevinroth.com/forums/index.php?c=2
// This code is public domain. Redistribution and use of this code, with or without modification, is permitted.
//-->
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en"> 
<head>
	<title>Cross-Browser Rich Text Editor (RTE) xxxxxxxxx/title>
	<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
	<meta name="keywords" content="cross-browser rich text editor, rte, textarea, htmlarea, content management, cms, blog, internet explorer, firefox, safari, opera, netscape, konqueror" />
	<meta name="description" content="The cross-browser rich-text editor (RTE) is based on the designMode() functionality introduced in Internet Explorer 5, and implemented in Mozilla 1.3+ using the Mozilla Rich Text Editing API." />
	<meta name="author" content="Kevin Roth" />
	<meta name="ROBOTS" content="ALL" />
	<!-- html2xhtml.js written by Jacob Lee <letsgolee@lycos.co.kr> //-->
	<script language="JavaScript" type="text/javascript" src="./estilo/html2xhtml.js"></script>
	<script language="JavaScript" type="text/javascript" src="./estilo/richtext_compressed.js"></script>
	
	
<body bgcolor="#ff0000">

<h2>Cross-Browser Rich Text Editor (RTE) Demo xxxx</h2>
<p>For more information, visit the <a href="http://www.kevinroth.com/rte/">Cross-Browser Rich Text Editor (RTE) home page</a>.</p>

<!-- START Demo Code -->
<form name="RTEDemo" action="demo.htm" method="post" onsubmit="return submitForm();">
<script language="JavaScript" type="text/javascript">
<!--
function submitForm() {
	//make sure hidden and iframe values are in sync for all rtes before submitting form
	updateRTEs();
	
	//change the following line to true to submit form
	alert("rte1 = " + (document.RTEDemo.rte1.value));
	return false;
}

//Usage: initRTE(imagesPath, includesPath, cssFile, genXHTML, encHTML)
initRTE("./images/", "./", "", false);
//-->
</script>
<noscript><p><b>Javascript must be enabled to use this form.</b></p></noscript>

<script language="JavaScript" type="text/javascript">
<!--
//build new richTextEditor
var rte1 = new richTextEditor('rte1');
rte1.html = 'here&#39;s the "\<em\>preloaded\<\/em\>&nbsp;\<b\>content\<\/b\>"';

//enable all commands for demo
rte1.cmdFormatBlock = true;
rte1.cmdFontName = true;
rte1.cmdFontSize = true;
rte1.cmdIncreaseFontSize = true;
rte1.cmdDecreaseFontSize = true;

rte1.cmdBold = true;
rte1.cmdItalic = true;
rte1.cmdUnderline = true;
rte1.cmdStrikethrough = true;
rte1.cmdSuperscript = true;
rte1.cmdSubscript = true;

rte1.cmdJustifyLeft = true;
rte1.cmdJustifyCenter = true;
rte1.cmdJustifyRight = true;
rte1.cmdJustifyFull = true;

rte1.cmdInsertHorizontalRule = true;
rte1.cmdInsertOrderedList = true;
rte1.cmdInsertUnorderedList = true;

rte1.cmdOutdent = true;
rte1.cmdIndent = true;
rte1.cmdForeColor = true;
rte1.cmdHiliteColor = true;
rte1.cmdInsertLink = true;
rte1.cmdInsertImage = true;
rte1.cmdInsertSpecialChars = true;
rte1.cmdInsertTable = true;
rte1.cmdSpellcheck = true;

rte1.cmdCut = true;
rte1.cmdCopy = true;
rte1.cmdPaste = true;
rte1.cmdUndo = true;
rte1.cmdRedo = true;
rte1.cmdRemoveFormat = true;
rte1.cmdUnlink = true;

rte1.toggleSrc = true;

rte1.build();
//-->
</script>
<p>Click submit to show the value of the text box.</p>
<p><input type="submit" name="submit" value="Submit" /></p>
</form>
<!-- END Demo Code -->

</body>
</html>
