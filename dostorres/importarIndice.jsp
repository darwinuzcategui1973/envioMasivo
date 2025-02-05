<!--
 * Title: importarIndice.jsp <br/>
 * Copyright: (c) 2007 Focus Consulting <br/>
 * @version WebDocuments
-->
<%@ page import="java.util.ResourceBundle,
				 java.util.ArrayList,
				 com.focus.qweb.to.EquivalenciasTO,
				 com.focus.qweb.to.IndiceTO,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.DesigeConf"%>


<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ page language="java" %>
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    
    ArrayList equivalencias = (ArrayList)request.getAttribute("equivalencias");
    ArrayList lista = (ArrayList)request.getAttribute("lista");
    String numero = (String)(request.getAttribute("numero")!=null?request.getAttribute("numero"):"");
    
    EquivalenciasTO equi;
    IndiceTO ind;
%>
<html>
<head>
<title></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<link href="estilo/tabs.css" rel="stylesheet" type="text/css">

<script language="JavaScript">
	var http = null;
	var actual="";
	var actualValor="";
	var anterior="";

    String.prototype.endsWith = function(str){return (this.match(str+"$")==str)};
	String.prototype.trim = function() { return this.replace(/^\s*|\s*$/g,""); };
    
	function addEvent(obj, evType, fn, useCapture){
	  if (obj.addEventListener){
	    obj.addEventListener(evType, fn, useCapture);
	    return true;
	  } else if (obj.attachEvent){
	    var r = obj.attachEvent("on"+evType, fn);
	    return r;
	  }
	}
	
	function grabar(etiqueta) {
		http = null;
		if(navigator.appName == "Microsoft Internet Explorer") {
		  http = new ActiveXObject("Microsoft.XMLHTTP");
		} else {
		  http = new XMLHttpRequest();
		}
		http.open("POST", "importarIndice.do?cmd="+etiqueta+"&numero=<%=numero%>&clave="+document.registro.clave.value+"&valor="+document.registro.valor.value);
		http.onreadystatechange=function() {
		  //try {
			  if(http.readyState == 4) {
			  	if(http.responseText=="insert") {
			  		var tabla = document.getElementById("cuerpoTabla");
			  		var tr = document.createElement("tr");
			  		var td1 = document.createElement("td");
			  		var td2 = document.createElement("td");
			  		var div1 = document.createElement("div");
			  		var div2 = document.createElement("div");
			  		
			  		var v1 = document.registro.clave.value;
			  		var v2 = document.registro.valor.value;
	  				addEvent(tr,'dblclick',function() {
	  					seleccionar(v1,v2);
	  				});
	  				addEvent(tr,'mouseover',function() {
	  					tr.style.backgroundColor='#7196b8';
	  					tr.style.color='white';
	  				});
	  				addEvent(tr,'mouseout',function() {
	  					tr.style.backgroundColor='';
	  					tr.style.color='black';
	  				});
	  				
			  		div1.setAttribute("id","d1_"+document.registro.clave.value);
			  		div2.setAttribute("id","d2_"+document.registro.clave.value);

			  		tr.setAttribute("id","row_"+document.registro.clave.value);
			  		
			  		td1.setAttribute("id","data1_"+document.registro.clave.value);
			  		td2.setAttribute("id","data2_"+document.registro.clave.value);
			  		
			  		div1.appendChild(document.createTextNode(document.registro.clave.value));
			  		div2.appendChild(document.createTextNode(document.registro.valor.value));
			  		td1.appendChild(div1);
			  		td2.appendChild(div2);
			  		tr.appendChild(td1);
			  		tr.appendChild(td2);
			  		tabla.appendChild(tr);
	    			document.registro.clave.value="";
	    			document.registro.valor.value="";
	    			
	    			if(actual!=""){
			   			document.getElementById("d1_"+actual).style.display="";
   						document.getElementById("d2_"+actual).style.display="";
   					}

		   			document.getElementById("tdClave").appendChild(document.getElementById("clave"));
   					document.getElementById("tdValor").appendChild(document.getElementById("valor"));
   					
	    			anterior="";
	    			actual="";
	    			
			   		//document.location.href="#final";
			   		nuevo();
			  	} else if(http.responseText=="update") {
			   		document.getElementById("contenedor").style.display="none";
			    	document.getElementById("d1_"+document.registro.clave.value).innerHTML=document.registro.clave.value;
	    			document.getElementById("d2_"+document.registro.clave.value).innerHTML=document.registro.valor.value;

		   			document.getElementById("d1_"+actual).style.display="";
   					document.getElementById("d2_"+actual).style.display="";
    	
		   			document.getElementById("d1_"+actual).style.backgroundColor="";
					document.getElementById("d2_"+actual).style.backgroundColor="";
    	
		   			document.getElementById("tdClave").appendChild(document.getElementById("clave"));
   					document.getElementById("tdValor").appendChild(document.getElementById("valor"));

	    			document.registro.clave.value="";
	    			document.registro.valor.value="";
	    			
	    			anterior="";
	    			actual="";
			  	} else if(http.responseText=="delete") {
			   		document.getElementById("contenedor").style.display="none";
		   			document.getElementById("row_"+actual).style.display="none";
    	
		   			document.getElementById("tdClave").appendChild(document.getElementById("clave"));
   					document.getElementById("tdValor").appendChild(document.getElementById("valor"));

	    			document.registro.clave.value="";
	    			document.registro.valor.value="";
	    			
	    			anterior="";
	    			actual="";
			  	} else {
			  		alert("Ocurrio un error al intentar ejecutar la accion sobre el registro.");
			  	} 
			  	
			  }
		  //} catch(e){
		  //	alert(e);
		  //}
		}
		http.send(null);
	}
    

    function listar(forma){
    	forma.numero.value = forma.equivalencia.value; 
    	forma.submit();
    }
    
    function seleccionar(cla,val) {
    	anterior=actual;
    	actual=cla;
    	actualValor=val;
   		document.getElementById("d1_"+actual).style.backgroundColor="#99ccff";
		document.getElementById("d2_"+actual).style.backgroundColor="#99ccff";
    	if(anterior!=""){
	   		document.getElementById("d1_"+anterior).style.backgroundColor="";
			document.getElementById("d2_"+anterior).style.backgroundColor="";
		}
    }
    
    function modificar(cla,val) {
   		//document.getElementById("contenedor").style.display="none";
    	//anterior=actual;
    	//actual=cla;
    	if(actual.trim()=="") {
    		alert("Seleccione un item...");
    		return;
    	}
    	
    	if(anterior!=""){
    		document.getElementById("d1_"+anterior).style.display="";
    		document.getElementById("d2_"+anterior).style.display="";
    	}
    	if(actual!=""){
	   		document.getElementById("d1_"+actual).style.display="none";
   			document.getElementById("d2_"+actual).style.display="none";
    	
   			document.getElementById("data1_"+actual).appendChild(document.getElementById("clave"));
	   		document.getElementById("data2_"+actual).appendChild(document.getElementById("valor"));
   		}
   		
   		document.registro.clave.value=actual;
   		document.registro.valor.value=actualValor;

   		document.registro.clave.readOnly=true;
   		document.registro.valor.focus();
    }
    
    function nuevo() {
    	if(document.combo.equivalencia.value=="") {
    		alert("Seleccione una equivalencia");
    		return;
    	}
    	if(anterior!=""){
    		document.getElementById("d1_"+anterior).style.display="";
    		document.getElementById("d2_"+anterior).style.display="";
    	}
    	if(actual!=""){
    		document.getElementById("d1_"+actual).style.display="";
    		document.getElementById("d2_"+actual).style.display="";
    	}
		document.getElementById("tdClave").appendChild(document.getElementById("clave"));
		document.getElementById("tdValor").appendChild(document.getElementById("valor"));
   		
   		document.getElementById("contenedor").style.display="";
   		document.registro.clave.readOnly=false;
   		document.registro.clave.value="";
   		document.registro.valor.value="";
   		document.location.href="#final";
   		document.registro.clave.focus();
    }
    
    function guardar() {
   		if( document.registro.clave.value.trim()=="" || document.registro.valor.value.trim()=="" ) {
   			alert("Ingrese la Clave y el Valor");
	   		document.location.href="#final";
   		} else {
   			document.getElementById("contenedor").style.display="none";
			grabar("actualizar");
		}
    }

    function eliminar() {
   		if( actual.trim()=="") {
   			alert("Seleccione un item...");
	   		document.location.href="#";
   		} else {
   			document.getElementById("contenedor").style.display="none";
			grabar("eliminar");
		}
    }
    function help() {
    	alert("Para seleccionar un item haga doble click sobre el.");
    }
    
    
</script>

</head>

<body class="bodyInternas" style="margin:10;">
<fieldset style="padding:10;">
<legend style="font-size:13pt;font-family:Tahoma,Arial,Verdana;">Indice</legend>
        <table cellSpacing=0 cellPadding=0 align=center border=0  width="100%">
            <tr>
	            <td class="pagesTitle" align="center">
		        <table cellSpacing=3 cellPadding=5 align=center border="0" align="center" width="100%">
		            <tr>
		               <td valign="top" align="right" class="td_title_bc" style="text-align:left;" nowrap>
							<form method="POST" name="combo" action="importarIndice.do">
								<input type="hidden" name="cmd" value="listar">
								<input type="hidden" name="numero" value="">
			               		<fieldset>
			               			<legend>Campo :</legend>
			               		<select name="equivalencia" style="width:100%;" onchange="listar(this.form)" >
			               			<option value="">Seleccione . . . </option>
			               			<%if(equivalencias!=null){
			               			  for(int i=0; i<equivalencias.size();i++){
			               			    equi = (EquivalenciasTO)equivalencias.get(i);%>
			               			    <option value="<%=equi.getIndice()%>" <%=(equi.getIndice().equals(numero)?"selected":"")%>><%=equi.getCampo()%></option>
			               			<%}
			               			}%>
			               		</select>
			               		</fieldset>
						  </form>
		               </td>
		            </tr>
		            <tr>
		               <td valign="top" align="right" class="td_title_bc" style="text-align:left;" nowrap>
		               		<form name="registro" method="post" action="importarIndice.do">

					 <table align="center" border=0 width="100%" cellspacing="0" cellpadding="0">
                        <tr>
                            <td width="100%" valign="top">
                                

                                <table class="toolBar borderTopBlack" cellpadding="0" bgcolor="#efefef">
                                
                                <tr>
                                		 <td width="1px"><img src="menu-images/barraini.gif"></td><td class="toolBarMiddle"> <a class="toolBarMiddleText" href='javascript:nuevo();' target='_self'><img src="menu-images/nuevo.gif" style="border:1px solid #efefef" onmouseover="this.style.border='1px solid #afafaf'" onmouseout="this.style.border='1px solid #efefef'" title="Agregar"></a>
									</td><td width="1px"><img src="menu-images/barrasep.gif"></td><td class="toolBarMiddle"> <a class="toolBarMiddleText" href='javascript:modificar();' target='_self'><img src="images/lapiz.gif" style="border:1px solid #efefef" onmouseover="this.style.border='1px solid #afafaf'" onmouseout="this.style.border='1px solid #efefef'" title="Editar"></a>
									</td><td width="1px"><img src="menu-images/barrasep.gif"></td><td class="toolBarMiddle"> <a class="toolBarMiddleText" href='javascript:guardar();' target='_self'><img src="menu-images/floppy.gif" style="border:1px solid #efefef" onmouseover="this.style.border='1px solid #afafaf'" onmouseout="this.style.border='1px solid #efefef'" title="Guardar"></a>
									</td><td width="1px"><img src="menu-images/barrasep.gif"></td><td class="toolBarMiddle"> <a class="toolBarMiddleText" href='javascript:eliminar();' target='_self'><img src="menu-images/deleteRound.gif" style="border:1px solid #efefef" onmouseover="this.style.border='1px solid #afafaf'" onmouseout="this.style.border='1px solid #efefef'" title="Eliminar"></a>
									</td><td width='100%'>&nbsp;</td></tr></table>
                            </td>
                        </tr>
                    </table>
		               		
					        <table cellSpacing="0" cellPadding="1" align=center border="0" align="center" class="td_gris_l" style="border:1px solid #c2d2e0" width="100%">
		               			<tr bgcolor="#c2d2e0" >
		               				<td width="30%"><b>Clave</b></td>
		               				<td><b>&nbsp;Valor</b></td>
		               			</tr>
		               		</table>
		               		
		               		<div style="height:250;overflow:auto;border:1px solid #c2d2e0;vertical-align:top">
					        <table cellSpacing="2" cellPadding="2" align="center" border="0" align="center" class="td_gris_l" style="border:0px solid #c2d2e0" width="100%">
					        	<tbody id="cuerpoTabla">
		               			<%if(lista!=null){
		               			  for(int i=0; i<lista.size();i++){
		               			    ind = (IndiceTO)lista.get(i);%>
		               			<tr id="row_<%=ind.getClave()%>" onMouseover="this.bgColor='#7196b8';this.style.color='white'" onMouseout="this.bgColor='';this.style.color='black'" onclick="seleccionar('<%=ind.getClave()%>','<%=ind.getValor()%>')" style="cursor:pointer;" >	
		               				<td id="data1_<%=ind.getClave()%>" width="30%">
		               					<div id="d1_<%=ind.getClave()%>"><%=ind.getClave()%></div>
		               				</td>
		               				<td id="data2_<%=ind.getClave()%>">
		               					<div id="d2_<%=ind.getClave()%>"><%=ind.getValor()%></div>
		               				</td>
		               			</tr>
		               			  <%}%>
		               			<%}%>
		               			</tbody>
		               		</table>
					        <table id="contenedor" cellSpacing="1" cellPadding="1" align=center border="0" align="center" class="td_gris_l" style="margin-bottom:2px;border:0px solid #c2d2e0;display:none;" width="100%">
		               			<tr bgcolor="#c2d2e0">
		               				<td width="30%" id="tdClave">
				               			<input type="text" id="clave" name="clave" style="width:100%;height:16px;border:0;font-size:10px;background-color:#ffff99;font-family: Sans-serif, Helvetica, Arial, Verdana;">
		               				</td>
		               				<td id="tdValor">
				               			<input type="text" id="valor" name="valor" style="width:100%;height:16px;border:0;font-size:10px;background-color:#ffff99;font-family: Sans-serif, Helvetica, Arial, Verdana;" >
		               				</td>
		               			</tr>
		               		</table>
		               		<a name="final"></a>
		               		</div>
		               		</form>
		               </td>
		            </tr>
		            <tr style="display:none;">
		            	<td colspan="2" align="center">
		            		<input type="button" value="<%=rb.getString("btn.close")%>" name="btnAccept" onclick="javascript:window.close();">
		            	</td>
		            </tr>
		        </table>
                </td>
            </tr>
        </table>
</fieldset>
</body>
</html>
