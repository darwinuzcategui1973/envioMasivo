<%
StringBuffer ruta = new StringBuffer();
ruta.append("accion=").append(request.getParameter("accion"));
ruta.append("&nameFile=").append(request.getParameter("nameFile"));
ruta.append("&idDocument=").append(request.getParameter("idDocument"));
ruta.append("&idVersion=").append(request.getParameter("idVersion"));
ruta.append("&imprimir=").append(request.getParameter("imprimir"));
ruta.append("&nameFileToPrint=").append(request.getParameter("nameFileToPrint"));
ruta.append("&idLogoImp=").append(request.getParameter("idLogoImp"));
%>
<html>
<head>
<!-- loadFrame.jsp -->
<script>
//alert(window.opener.getListaAnexo());
function abre(){
//window.open("file://///192.168.10.42/opt/temp/reportes/0001.doc","anexo1");
window.open("showAttached.do?prefijo=<%=request.getParameter("prefijo")%>&numero=1","anexo2");
window.open("showAttached.do?prefijo=<%=request.getParameter("prefijo")%>&numero=0","anexo1");
window.open("loadDataDoc.do?<%=ruta.toString()%>","documento");
}
setTimeout("abre()",100);
</script></head>
<FRAMESET rows="100%, 50" border="0">
  <FRAMESET id="frmDocumentos" cols="37%, 37%, 26%" border="0">
      <FRAME name="documento" src="" id="documentoPDF"  scrolling="NO">
      <FRAME name="anexo1" src="">
      <FRAME name="anexo2" src="">
  </FRAMESET>
  <FRAME src="anexoNavegacion.jsp?item=<%=request.getParameter("item")%>" scrolling="no">
</FRAMESET>
</html>
