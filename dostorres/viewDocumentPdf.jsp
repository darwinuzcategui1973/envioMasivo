<%@ page import="com.desige.webDocuments.utils.ToolsHTML" %><%  
	String parameters = ToolsHTML.parseParameters(request);
    String file = "srShowDoc";
    boolean isExpediente = false;
    boolean isFirstPage = Boolean.parseBoolean(request.getParameter("firstPage")!=null?String.valueOf(request.getParameter("firstPage")):"false");
    
    /*
    if(request.getParameter("showFile")!=null){
    	file = request.getParameter("showFile");
    	if("showExpediente.jsp".equals(file)) {
    		isExpediente = true;
    		isFirstPage = true;
    	}
    }
    */
    file = file + parameters;
%>
<script>
	document.location.href="<%=file%>"
</script>
