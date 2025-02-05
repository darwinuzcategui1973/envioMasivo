<%@ page import="java.util.ResourceBundle,com.desige.webDocuments.utils.ToolsHTML,
	com.desige.webDocuments.utils.beans.Search,
	com.desige.webDocuments.persistent.managers.HandlerDBUser,
	java.util.Collection" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
   	boolean isNombre = String.valueOf(request.getParameter("tipo")).equals("n");
   	
    Collection users = HandlerDBUser.getAllUsers();
    if(session.getAttribute("userSystem")==null){
    	session.setAttribute("userSystem",users);
    }
   	
%>
<html>
<head>
	<title>Qwebdocuments</title>
	<jsp:include page="meta.jsp" /> 
	<link href="estilo/estilo.css" rel="stylesheet" type="text/css" />
	<!-- Recuerda reemplazar richedit.docXHtml por richedit.value -->
	<link href="estilo/rte.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="./estilo/html2xhtml.js"></script>
	<script type="text/javascript" src="./estilo/richtext_compressed.js"></script>
	<style>.fondoEditor {	background-color: white; }</style> 
	<script type="text/javascript" src="./estilo/funciones.js"></script>
	<script type="text/javascript">
	    function setValue(id){
	        opener.document.<%=request.getParameter("nameForma")%>.<%=isNombre?"razonNameDocument":"razonOwner"%>.value=id;
	        window.close();
	    }
	    function setValueProperty(id){
	        opener.document.<%=request.getParameter("nameForma")%>.<%=isNombre?"nameDocument":"owner"%>.value=id;
	        <%if(isNombre){%>
		        opener.document.getElementById("lblNameDocument").innerHTML=id;
	        <%} else {%>
	        	var pos = document.forma.owner.selectedIndex;
		        opener.document.getElementById("lblOwner0").innerHTML=document.forma.owner.options[pos].text;
	        <%}%>
	    }
	
	
	    function closeWin() {
	  		  //alert('< %=request.getParameter("variable")%>');
			  updateRTEs();
			  var cad1="<P>";
			  var cad2="</P>";
			  var texto = document.forma.richedit.value.replace(/&nbsp;/g,"").replace(/<br>/g,"");
			  texto = texto.replace(/<P>/g,"");
			  texto = texto.replace(/<\/P>/g,"");
			  texto = texto.replace(/<p>/g,"");
			  texto = texto.replace(/<\/p>/g,"");
			  texto = texto.replace(/^\s*|\s*$/g,"");
	    	  if (texto.length == 0) {
			  	  alert("Por favor agregue algún comentario");
			  	  return false;
			  } else {
				  if(<%= isNombre %>){
					  //se desea cambiar el nombre, verificamos que no sea un simple texto vacio
					  if(document.forma.nameDocument.value.replace(/^\s*|\s*$/g,"").length == 0){
						  //el nombre modificado es vacio, debemos frenar la peticion de cambio y alertar 
						  alert("El nombre del documento no puede ser vacio");
						  return false;
					  }
				  }
				  setValueProperty(document.forma.<%=isNombre?"nameDocument":"owner"%>.value);
		          setValue(document.forma.richedit.value);
    			  //top.window.close();
			  }
	    }
	    
	   function cancelar() {
	        top.window.close();
	    }
	</script>
</head>

<body class="bodyInternas">
	<br/>
	<form name="forma">
	    <center>
	        <table align=center border=0 width="100%">
	            <tr>
	                <td colspan="2">
	                    <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
	                        <tbody>
	                            <tr>
	                                <td class="td_title_bc" height="21">
	                                    <%=request.getParameter("title")%>
	                                </td>
	                            </tr>
	                        </tbody>
	                    </table>
	                </td>
	            </tr>
	            
	           <tr>
	                <td colspan="2">
	                	<%if(isNombre) {%>
		                	<table border="0" width="100%" cellspacing="2" cellpadding="0">
					            <tr>
					                <td class="titleLeft" height="26" style="background: url(img/btn140.png); background-repeat: no-repeat" width="20%" valign="middle">
					                    <%=rb.getString("cbs.name")%>:
					                </td>
					                <td width="*" class="td_gris_l">
					                    <input type="text" id="nameDocument" name="nameDocument" size="100" value="<%=request.getParameter("value")%>" class="classText" />
										<textarea name="razonNameDocument" style="display:none"></textarea>
					                </td>
					            </tr>
		                	</table>
	                	<%} else {%>
		                	<table border="0" width="100%" cellspacing="2" cellpadding="0">
					            <tr>
					                <td class="titleLeft" height="26" style="background: url(img/btn140.png); background-repeat: no-repeat" width="20%" valign="middle">
					                    <%=rb.getString("cbs.owner")%>:
					                </td>
					                <td width="*" class="td_gris_l">
				                        <select name="owner" styleClass="classText" style="width: 400px">
				                            <logic:present name="userSystem" scope="session" >
				                                <logic:iterate id="bean" name="userSystem" scope="session" type="com.desige.webDocuments.utils.beans.Search">
				                                    <option value="<%=bean.getId()%>" ><%=bean.getDescript()%></option>
				                                </logic:iterate>
				                            </logic:present>
				                        </select>
				                        <script type="text/javascript">
										    <%if(!isNombre) {%>
										        document.forma.owner.value='<%=request.getParameter("value")%>';
										    <%}%>
										</script>
					                </td>
					            </tr>
		                	</table>
	                	<%}%>
	                </td>
	            </tr>

	           <tr>
	                <td colspan="2">
	                    &nbsp;
	                </td>
	            </tr>

			    <tr>
			        <td colspan="2" class="barraBlueTitle">
			        	<%=rb.getString("wf.razondelcambio")%>
			        </td>
			    </tr>
	            
	            <tr>
	                <td class="fondoEditor" align="center" width="100%" colspan="2">
						  <!--  acuerdate de colocar         updateRTEs(); antes de hacer el submit -->
	
							<script language="JavaScript" type="text/javascript">
							<!--
							//Usage: initRTE(imagesPath, includesPath, cssFile, genXHTML, encHTML)
							initRTE("./images/", "./", "./estilo/", false);
							//-->
							</script>
							<noscript><p><b>Javascript must be enabled to use this form.</b></p></noscript>
							
							<script language="JavaScript" type="text/javascript">
							<!--
							//build new richTextEditor http://www.kevinroth.com/rte/usage.htm init richedit
							var richedit = new richTextEditor('richedit');
							//richedit.html = 'here&#39;s the "\<em\>preloaded\<\/em\>&nbsp;\<b\>content\<\/b\>"';
							richedit.html='';
							//enable all commands for demo
							richedit.width="100%"
							
							richedit.toolbar1 = true;
							richedit.toolbar2 = true;
							richedit.readOnly = false;
							
							richedit.cmdFormatBlock = true;
							richedit.cmdFontName = true;
							richedit.cmdFontSize = true;
							richedit.cmdIncreaseFontSize = true;
							richedit.cmdDecreaseFontSize = true;
							
							richedit.cmdBold = true;
							richedit.cmdItalic = true;
							richedit.cmdUnderline = true;
							richedit.cmdStrikethrough = true;
							richedit.cmdSuperscript = true;
							richedit.cmdSubscript = true;
							
							richedit.cmdJustifyLeft = true;
							richedit.cmdJustifyCenter = true;
							richedit.cmdJustifyRight = true;
							richedit.cmdJustifyFull = true;
							
							richedit.cmdInsertHorizontalRule = true;
							richedit.cmdInsertOrderedList = true;
							richedit.cmdInsertUnorderedList = true;
							
							richedit.cmdOutdent = true;
							richedit.cmdIndent = true;
							richedit.cmdForeColor = true;
							richedit.cmdHiliteColor = true;
							richedit.cmdInsertLink = true;
							richedit.cmdInsertImage = false;
							richedit.cmdInsertSpecialChars = true;
							richedit.cmdInsertTable = true;
							richedit.cmdSpellcheck = false;
							
							richedit.cmdCut = true;
							richedit.cmdCopy = true;
							richedit.cmdPaste = true;
							richedit.cmdUndo = true;
							richedit.cmdRedo = true;
							richedit.cmdRemoveFormat = true;
							richedit.cmdUnlink = true;
							
							richedit.toggleSrc = false;
							
							
							//-->
							</script>                
			              <script type="text/javascript">
				                  document.forma.<%=isNombre?"nameDocument":"owner"%>.value = opener.document.<%=request.getParameter("nameForma")%>.<%=isNombre?"nameDocument":"owner"%>.value;

				                  richedit.html = opener.document.<%=request.getParameter("nameForma")%>.<%=isNombre?"razonNameDocument":"razonOwner"%>.value;
				                  richedit.build();
				          </script>
	                </td>
	            </tr>
	             <tr>
	                <td></td>
	            </tr>
	             <tr>
	                <td></td>
	            </tr>
	             <tr>
	                <td></td>
	            </tr>
	             <tr>
	                <td></td>
	            </tr>
	            <tr>
	                <td></td>
	            </tr>
	             <tr>
	                <td></td>
	            </tr>
	             <tr>
	                <td></td>
	            </tr>
	             <tr>
	                <td></td>
	            </tr>
	            <tr>
	                <td align="center" >
	                    <input type="button" class="boton" value="Aceptar" onclick="javascript:closeWin();"/>
	                     &nbsp;
	                    <input type="button" class="boton" value="Cancelar" onclick="javascript:cancelar();" name="btnCancel"/>
	                </td>
	            </tr>
	        </table>
	    </center>
	</form>
</body>
</html>
