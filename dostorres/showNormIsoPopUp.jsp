<!-- showNormIsoPopUp.jsp -->
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ page import="java.util.ResourceBundle"%>
<%@ page import="com.desige.webDocuments.utils.ToolsHTML"%>
<%
	ResourceBundle rb = ToolsHTML.getBundle(request);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<style type="text/css">
select {
	background-color: #ebebeb;
	height: 500px;
}
</style>
<script type="text/javascript" src="./script/sacopJS.js"></script>
<script type="text/javascript" src="./estilo/funciones.js"></script>

<script type="text/javascript">
        var parentHiddenField = "<%=request.getAttribute("hiddenField")%>";
        
            function closeWindow(){
        	if(window.opener != null){
        		//debemos regresarle a la ventana padre, los valores seleccionados de la lista de normas
        		alert(obtenerIdsSeleccionados(document.getElementById("normsisoSelected")));
        		window.opener.setNormas(parentHiddenField,obtenerIdsSeleccionados(document.getElementById("normsisoSelected")));
        		window.close();
        	}
        }
        
        function prepareSelectedNorms(idSelectedNorms){
        	//hacemos split por coma
        	var values = idSelectedNorms.split(",");
        	var normas = document.getElementById("normsiso");
        	
        	if(values.length > 0){
        		for (var i = 0; i < normas.length; i++) {
					if(values.indexOf(normas.options[i].value) > -1){
						normas.options[i].selected = true;
					}
				}
        		
        		mover(document.getElementById('normsiso'),
        				document.getElementById('normsisoSelected'));
        	}
        }
    </script>
</head>
<body>
	<table width="100%" cellSpacing="0" cellPadding="2" align="center" border="0">
		<tr>
			<td>
				<div style="overflow: auto; width: 600px;">
					<select name="normsiso" id="normsiso" multiple class="classText"
						onDblClick="javascript:vertexto(document.getElementById('normsiso'));">
						<logic:present name="allNorms" scope="request">
							<logic:iterate id="bean" name="allNorms" scope="request"
								type="com.desige.webDocuments.utils.beans.Search">
								<option value="<%=bean.getId()%>"><%=bean.getDescript()%></option>
							</logic:iterate>
						</logic:present>
					</select>
				</div>
			</td>
			<td width="100%"><input type="button" value=" &gt; "
				style="width: 28px;"
				onClick="javascript:mover(document.getElementById('normsiso'),document.getElementById('normsisoSelected'));" />
				<br /> <input type="button" value=" &lt; " style="width: 28px;"
				onClick="javascript:mover(document.getElementById('normsisoSelected'),document.getElementById('normsiso'));" />
			</td>
			<td>
				<div style="overflow: auto; width: 600px;">
					<select name="normsisoSelected" id="normsisoSelected" multiple
						class="classText"
						onDblClick="javascript:vertexto(document.getElementById('normsisoSelected'));">
						<logic:present name="norms1" scope="request">
							<logic:iterate id="bean" name="norms1" scope="request"
								type="com.desige.webDocuments.utils.beans.Search">
								<option value="<%=bean.getId()%>"><%=bean.getDescript()%></option>
							</logic:iterate>
						</logic:present>
					</select>
				</div>
			</td>
		</tr>
		<tr>&nbsp;</tr>
		<tr>
			<td colspan="3" align="center"><input type="button"
				name="guardar" value="<%=rb.getString("btn.save")%>"
				onclick="closeWindow();" /></td>
		</tr>
	</table>
	<script type="text/javascript">
                    prepareSelectedNorms('<%=((String) request.getAttribute("selected"))%>');
	</script>
</body>
</html>