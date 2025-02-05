<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML"%>
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    String nameFile = (String)request.getAttribute("nameFileTest");
    String nameDoc = (String)request.getAttribute("nameDoc");
    //System.out.println("nameFile = " + nameFile);
    //System.out.println("nameDoc = " + nameDoc);
    //System.out.println("new java.util.Date() = " + new java.util.Date());
%>
<html>
<head>
<title></title>
<jsp:include page="meta.jsp" /> 

<script languaje="javascript">

  function saveFile(){
     fichero = "<%=nameFile%>";
     fichero = "\\desige11\tmp$\\Forecast Form - 2005.xls";
     wdApp = new ActiveXObject("Word.Application");
     var doc = wdApp.Documents.Open(fichero,false,false,true);
     wdApp.Application.Visible = false;
     doc.saveAs("C:\\doc.doc");
     //doc.close();
     wdApp.Application.Visible = true;
  }

    function startExcel() {
        var myApp = new ActiveXObject("Excel.Application");
        fichero = "\\\\desige11\\tmp$\\Forecast Form - 2005.xls";
<%--        location.href = fichero;--%>
        if (myApp != null) {
            myApp.Visible = false;
            parent.doc = myApp.Workbooks.Open(fichero,0,true);
            alert("abriendo el documento III....");
            myApp.UserControl = true;
<%--            myApp.Workbooks(1).Protect("cvfgb");--%>
<%--            this.window = window.open(myApp);--%>
            myApp.Visible = true;

        } else {
            alert("No se pudo cargar la aplicación");
        }
    }

    function loadworddoc() {
        var doc = new ActiveXObject("Excel.Application");
        doc.Visible = false;
        doc.Documents.Open("C:\\My Documents\\file.doc");
        var txt;
        txt = doc.Documents("C:\\My Documents\\file.doc").Content;
        document.all.myarea.value = txt;
        doc.quit(0);
    }


</script>

<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
</head>
<body class="bodyInternas" onLoad="startExcel();">

</body>
</html>
