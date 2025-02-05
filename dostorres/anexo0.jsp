<HTML> 
<HEAD> 

<SCRIPT language="JavaScript">

function WriteFile() 
{
   var fso  = new ActiveXObject("Scripting.FileSystemObject"); 
   var fh = fso.CreateTextFile("D:/appProyectos/qweb/qweb452/appworkspace452/qwebds4/tmp/archivo.pdf", true); 
   fh.WriteLine("Some text goes here..."); 
   fh.Close(); 
}

</SCRIPT>
</HEAD>

<BODY>
<P>
<SCRIPT language="JavaScript">  WriteFile(); </SCRIPT>
</P>
</BODY>
</HTML>