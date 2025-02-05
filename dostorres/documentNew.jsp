<!-- Title: documentNew.jsp -->
<%@ page import="java.util.ResourceBundle,
				 com.desige.webDocuments.files.forms.DocumentForm,
                 com.desige.webDocuments.utils.beans.Users,
                 java.util.ArrayList,
                 com.desige.webDocuments.utils.ToolsHTML"%>
                 
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%
	ResourceBundle rb = ToolsHTML.getBundle(request);
    Users usuario = (Users)session.getAttribute("user");
    DocumentForm actual = (DocumentForm)request.getAttribute("campo");
    boolean editable=false;
    ArrayList listaTypeDocs = (ArrayList) request.getAttribute("listaTypeDocs");

    if(actual.getId()==null || actual.getId().trim().equals("")) {
    	actual.setId("d1");
    }
    
    StringBuffer onLoad = new StringBuffer("");
    String info = (String) request.getAttribute("info");
    if ((info == null) || (info.compareTo("") == 0)) {
        info = "";
    }
%>
<html>
    <head>    
        <meta content="MSHTML 6.00.2800.1106" name=generator/>
        <link href="estilo/estilo.css" rel="stylesheet" type="text/css"/>
		<script type="text/javascript" src="estilo/funciones.js"></script>
        <script type="text/javascript">
        	function ir(campo){
        		document.location.href="documentNew.do?id="+campo;
        	}
        	function salvar(){
        		if(document.documentNewSave.longitud.value=="0" || trim(document.documentNewSave.longitud.value)==""){
        			alert("<bean:message key="files.error.longitud"/>");
        			return false;
        		}
        		with(document.documentNewSave){
        			submit();
        		}
        	}
        	function eliminar(){
        		if(confirm('<bean:message key="files.delete"/>')){
	        		if(confirm('<bean:message key="files.delete2"/>')){
		        		with(document.documentNewSave){
		        			if(id.value!='<bean:write name='eliminable'/>' ){
		        				alert('<bean:message key="app.notDelete"/>');
		        				return false;
		        			}
		        			eliminar.value='true';
		        			submit();
		        		}
	        		}
        		}
        	}

		    function backStruct() {
		        document.location="administracionMain.do";
		    }
		    
		    if("" != "<%= info %>"){
		    	alert('<%=info%>');
		    }
        </script>
	</head>
<body class="bodyInternas">
<table border="0" height="100%" width="100%" cellspacing="0" cellpadding="10" align="center">
	<tr>
		<td class='ppalTextBoldBig'  height="30" style="border-bottom: 2px groove;">
			<bean:write name="campo" property="id"/>  - <bean:write name="campo" property="etiqueta02"/> <%= info %>
		</td>
		<td rowspan="2" valign="top" width="200" background="menu-images/backgd2.jpg" style="background-color:white;border-left: 0px groove;">
			<table border="0" cellspacing="2" cellpadding="2" style="left:0px;top:0px;background-color:#efefef;border: 1px solid #aaaaaa; ;" width="100%">
				<tr>
					<td>
					    <span ><img class="mano" src="icons/page_white.png" title="<%=rb.getString("btn.incluir")%>" onClick="javascript:ir('nuevo');"></span>&nbsp;
					</td>
					<td>
					    <span ><img class="mano" src="icons/disk.png" title="<%=rb.getString("btn.save")%>" onClick="javascript:salvar();"></span>&nbsp;
					</td>
					<td>
					    <span ><img class="mano" src="icons/cancel.png" title="<%=rb.getString("btn.delete")%>" onClick="javascript:eliminar();"></span>&nbsp;
					</td>
					<td>
					    <span ><img class="mano" src="icons/arrow_undo.png" title="<%=rb.getString("btn.back")%>" onClick="javascript:backStruct();"></span>&nbsp;
					</td>
					<td width="100%">
					    &nbsp;
					</td>
				</tr>
			</table>
			<div id="campos" style="overflow: auto;width:100%;height:93%">
			<ul class="ppalTextBold">
				<logic:iterate name="lista" id="obj">
					<li class="mano" onclick="ir('<bean:write name="obj" property="id"/>')">
						<a name="<bean:write name="obj" property="id"/>">&nbsp;</a>
						<bean:write name="obj" property="id"/>&nbsp;-&nbsp;<span style="color:blue">(<bean:write name="obj" property="orden"/>)</span>
						<%if(usuario.getLanguage().toLowerCase().equals("es")){%>
							<bean:write name="obj" property="etiqueta02"/>
						<%}else{%>
							<bean:write name="obj" property="etiqueta01"/>
						<%}%>
		                <logic:equal value='checkbox' name='obj' property='entrada'>
							<span class="mailTextColor"><bean:write name="obj" property="valores"/></span>
						</logic:equal>
		                <logic:equal value='1' name='obj' property='tipo'><span style="color:brown">[T]</span></logic:equal>
		                <logic:equal value='2' name='obj' property='tipo'><span style="color:brown">[N]</span></logic:equal>
		                <logic:equal value='3' name='obj' property='tipo'><span style="color:brown">[F]</span></logic:equal>
					</li>
				</logic:iterate>
			</ul>
			</div>
		</td>
	</tr>
	<tr>
		<td  valign="top">
			<html:form action="/documentNewSave">
				<input type="hidden" name='eliminar' value='false'>
			<table class="texto" border="0" width="100%">
				<tr>
					<td width="100">
						<bean:message key="files.field"/> :
					</td>
					<td>
						<html:text name="campo" property="id" size="4" readonly="true" />

						&nbsp;&nbsp;&nbsp;						
						<bean:message key="files.orden"/> :
						<html:text name="campo" property="orden" size="4" maxlength="2" readonly='<%=editable%>'  onkeyup='format(this)' onchange='format(this)' />
						
					</td>
				</tr>
				<tr>
					<td>
						<table class="texto" cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td><bean:message key="files.label"/> : </td>
								<td align="right"><bean:message key="files.label02"/> :-</td>
							</tr>
						</table>
					</td>
					<td>
						<html:text name="campo" property="etiqueta02" size="50" maxlength="100" readonly='<%=editable%>'/>
					</td>
				</tr>
				<tr>
					<td>
						<table class="texto" cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								<td align="right"><bean:message key="files.label01"/> :-</td>
							</tr>
						</table>
					</td>
					<td>
						<html:text name="campo" property="etiqueta01" size="50" maxlength="100" readonly="<%=editable%>"/>
					</td>
				</tr>
				<tr class="<%=editable?"none":""%>">
					<td>
						<bean:message key="files.type"/> :
					</td>
					<td>
						<html:select name="campo" property="tipo">
							<html:option value="1"><bean:message key="files.text"/></html:option>
							<html:option value="2"><bean:message key="files.number"/></html:option>
							<html:option value="3"><bean:message key="files.date"/></html:option>
						</html:select>
					</td>
				</tr>
				<tr>
					<td>
						<bean:message key="files.length"/> :
					</td>
					<td>
						<html:text name="campo" property="longitud" size="8" maxlength="4" readonly="<%=editable%>"   onkeyup='format(this)' onchange='format(this)' />
					</td>
				</tr>
				<tr>
					<td valign="top">
						<bean:message key="files.values"/> :
					</td>
					<td>
						<html:textarea name="campo" property="valores" rows="5" style="width:500px;" readonly="<%=editable%>"/>
					</td>
				</tr>
				<tr class="<%=editable?"none":""%>">
					<td>
						<bean:message key="files.in"/> :
					</td>
					<td>
						<html:select name="campo" property="entrada" >
							<html:option value="text">Text</html:option>
							<html:option value="radio">Radio</html:option>
							<html:option value="checkbox">Checkbox</html:option>
							<html:option value="select">Select</html:option>
							<html:option value="textarea">Textarea</html:option>
						</html:select>
					</td>
				</tr>
				<tr>
					<td>
						<bean:message key="files.condition"/> :
					</td>
					<td>
						<html:text name="campo" property="condicion" size="30" maxlength="500" readonly="<%=editable%>"/>
					</td>
				</tr>
				<tr>
					<td>
						<bean:message key="files.visible"/> :
					</td>
					<td>
						<html:select name="campo" property="visible" >
							<html:option value="1"><bean:message key="sistema.yes"/></html:option>
							<html:option value="0"><bean:message key="sistema.no"/></html:option>
						</html:select>

						<span class="<%=editable?"none":""%>">
						&nbsp;&nbsp;&nbsp;
						<bean:message key="files.edit"/> :
						<html:select name="campo" property="editable" >
							<html:option value="1"><bean:message key="sistema.yes"/></html:option>
							<html:option value="0"><bean:message key="sistema.no"/></html:option>
						</html:select>
						</span>
						
						<span class="<%=editable?"none":""%>">
						&nbsp;&nbsp;&nbsp;
						<bean:message key="files.auditable"/> :
						<html:select name="campo" property="auditable" >
							<html:option value="1"><bean:message key="sistema.yes"/></html:option>
							<html:option value="0"><bean:message key="sistema.no"/></html:option>
						</html:select>
						</span>

						&nbsp;&nbsp;&nbsp;
						<bean:message key="files.criterio"/> :
						<html:select name="campo" property="criterio">
							<html:option value="1"><bean:message key="sistema.yes"/></html:option>
							<html:option value="0"><bean:message key="sistema.no"/></html:option>
						</html:select>

						<div style="display:none">
						&nbsp;&nbsp;&nbsp;
						<bean:message key="files.print"/> :
						<html:select name="campo" property="imprimir" >
							<html:option value="1"><bean:message key="sistema.yes"/></html:option>
							<html:option value="0"><bean:message key="sistema.no"/></html:option>
						</html:select>
						</div>

						&nbsp;&nbsp;&nbsp;
						<bean:message key="files.location"/> :
						<html:select name="campo" property="location">
							<html:option value="1"><bean:message key="sistema.yes"/></html:option>
							<html:option value="0"><bean:message key="sistema.no"/></html:option>
						</html:select>
					</td>
				</tr>
				<tr>
					<td valign="top">
						<%=rb.getString("pb.typeDoc")%>
					</td>
					<td>
						<div style="overflow: auto;height:150px;width:500px;border:1px solid #afafaf;">
							<!--<div onmouseover="this.style.backgroundColor='#efefef';" onmouseout="this.style.backgroundColor='';"><input type="checkbox" /> General</div>-->
		                    <logic:present name="tiposDoc" scope="session">
	                            <logic:iterate id="bean" name="tiposDoc" scope="session" type="com.desige.webDocuments.utils.beans.Search">
	                            	<div onmouseover="this.style.backgroundColor='#efefef';" onmouseout="this.style.backgroundColor='';">
	                            		<input type="checkbox" name="tipos" value="<%=bean.getId()%>" <%=listaTypeDocs.contains(bean.getId())?"checked":""%>/>&nbsp;<%=bean.getDescript()%></div>
	                            </logic:iterate>
		                    </logic:present>
							
						</div>
					</td>
				</tr>
			</table>
			</html:form>
		</td>
	</tr>
</table>
</body>
</html>
<script type="text/javascript" event="onload" for="window">

	//document.getElementById("campos").className="clsParentDiv";
	//document.location.href="#<%=actual.getId()%>";
</script>
