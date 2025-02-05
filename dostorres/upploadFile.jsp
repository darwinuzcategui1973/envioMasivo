<!-- * Title: upploadFile.jsp <br> -->
<%@ page import="java.util.ResourceBundle,
				 com.desige.webDocuments.to.DigitalTO,
				 com.desige.webDocuments.typeDocuments.forms.TypeDocumentsForm,
				 com.desige.webDocuments.utils.Constants,
                 java.util.Collection,
                 java.util.Iterator,
                 java.io.File,
                 com.desige.webDocuments.utils.beans.Search,
			     com.desige.webDocuments.utils.ToolsHTML" %>
<%@ page language="java" %>
<link href="estilo/tabs.css" rel="stylesheet" type="text/css" />


<% boolean isModuloDigital = session.getAttribute(Constants.MODULO_ACTIVO).equals(Constants.MODULO_DIGITALIZAR);%>
<%if(isModuloDigital){%>
<jsp:include page="upploadFileDigital.jsp" /> 
<%} else {%>
<jsp:include page="upploadFileDocument.jsp" /> 
<%}%>
