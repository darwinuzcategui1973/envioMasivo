<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.Users"%>
                 
<html>
<frameset id="principal" rows="*" cols="45%,*" frameborder="no" border="0" framespacing="0" > 
    <frame src="<%=ToolsHTML.getFileBasePathDigitalizados(request,request.getParameter("idDigital"))%>" id="digitalLeft" name="digitalLeft" scrolling="NO" style="width:100%" >
    <frame src="<%=ToolsHTML.getBasePath(request)%>newDocument.do?cmd=new&nexPage=upploadFile.jsp&nodeActive=1&idDigital=<%=(String)session.getAttribute("idDigital")%>" id="digitaRight" name="digitaRight" marginwidth="0px" marginheight="0px" >
</frameset>
</html>