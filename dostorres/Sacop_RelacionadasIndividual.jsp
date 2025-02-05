<!-- Sacop_RelacionadasIndividual.jsp -->
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML"%>
<%@ page import="com.desige.webDocuments.sacop.actions.LoadSacop"%>
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);

    String id = (String)request.getParameter("id");
    String edo = (String)request.getParameter("edo");


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

  function showPlanilla(forma,id,edo){
           forma.Idplanillasacop1.value=='';
           forma.sacoprelacionada.value='1';
           if ((edo=='<%=LoadSacop.edoBorrador%>')) {    //edoBorrador
              forma.goTo.value="planillauno";
              forma.edodelsacop.value=edo;
              forma.Idplanillasacop1.value=id;
              forma.submit();
          } else if ((edo=='<%=LoadSacop.edoEmitida%>')||(edo=='<%=LoadSacop.edoRechazado%>')) {    //edoEmitida o edoRechazado
              forma.edodelsacop.value=edo;
              forma.goTo.value="planillados";
              forma.Idplanillasacop1.value=id;
              forma.submit();
          } else if((edo=='<%=LoadSacop.edoAprobado%>')) {  //edoAprobado
              forma.goTo.value="planilla3_0";
              forma.Idplanillasacop1.value=id;
              forma.submit();

          } else if((edo=='<%=LoadSacop.edoPendienteVerifSeg%>')||(edo=='<%=LoadSacop.edoEnEjecucion%>')) {
              forma.goTo.value="planilla4";
              forma.Idplanillasacop1.value=id;
              forma.submit();

          } else if((edo=='<%=LoadSacop.edoVerificacion%>')) {
              forma.goTo.value="planilla5";
              forma.Idplanillasacop1.value=id;
              forma.submit();

          } else if((edo=='<%=LoadSacop.edoCerrado%>')) {
              forma.goTo.value="planilla6";
              forma.Idplanillasacop1.value=id;
              forma.submit();
          }

      }

</script>

<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
</head>

<body class="bodyInternas" onLoad="showPlanilla(document.solicitudsacopplanilla,<%=id%>,<%=edo%>);">
  <form name=solicitudsacopplanilla action='loadSACOPMain.do'>
                              <!--Variables para usar en eliminar planillas-->
                                <input type='hidden' name=userdlt>
                                <input type='hidden' name=idpdlt>
                                <input type='hidden' name=goTo>
                                <input type='hidden' name="Idplanillasacop1">
                                <input type='hidden' name="edodelsacop">
                                <input type='hidden' name="borrarAtodos">
                                <input type='hidden' name="sacoprelacionada" value="1">
    </form>
</body>
</html>
