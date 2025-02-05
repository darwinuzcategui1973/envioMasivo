<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<bean:define id="data" name="loadParameters" type="com.desige.webDocuments.parameters.forms.BaseParametersForm" scope="session" />	
<%String valor = "";
int n = Integer.parseInt(request.getParameter("numero"));
switch(n) {
	case 1:
		valor = String.valueOf(data.getMsgDocExpirado());
		break;
	case 2:
		valor = String.valueOf(data.getMsgWFBorrador());
		break;
	case 3:
		valor = String.valueOf(data.getMsgWFExpires());
		break;
	case 4:
		valor = String.valueOf(data.getMsgWFRevision());
		break;
	case 5:
		valor = String.valueOf(data.getMsgWFAprobados());
		break;
	case 6:
		valor = String.valueOf(data.getMsgWFCancelados());
		break;
	case 7:
        valor = String.valueOf(data.getMsgSacopAccionesPorVencer());
        break;
	case 8:
        valor = String.valueOf(data.getMsgSacopAccionesVencidas());
        break;
}
%>
<html>
<head>
	<jsp:include page="richeditHead.jsp" /> 
	<script type="text/javascript">
		function getValor(){
            updateRTEs();
            return document.forma.richedit.value;
		}
		
	</script>
</head>
<body>
<form name="forma">
  	  <jsp:include page="richeditMessage.jsp">
  	  	<jsp:param name="richedit" value="richedit"/>
  	  	<jsp:param name="defaultValue" value='<%=valor%>' />
  	  </jsp:include>
</form>	
</body>
</html>