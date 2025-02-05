<!-- /**
 * Title: sacopEast.jsp<br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 */ -->
<%@page import="com.desige.webDocuments.persistent.managers.HandlerProcesosSacop"%>
<%@page import="com.desige.webDocuments.persistent.managers.HandlerDBUser"%>
<%@page import="java.util.Collection"%>
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.DesigeConf,
                 com.desige.webDocuments.utils.Constants,
                 com.desige.webDocuments.utils.beans.Users,
                 com.desige.webDocuments.utils.beans.Search"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %> 
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%
 
    ResourceBundle rb = ToolsHTML.getBundle(request);
    
    Collection allUsers = HandlerDBUser.getAllUsersFilter(null, false);
    request.setAttribute("todosUsuarios", allUsers);

    Collection allAreas = HandlerProcesosSacop.loadAllAreas();
    request.setAttribute("todasAreas", allAreas);
    
	// todos lo processos
	Collection procesosSacop = HandlerProcesosSacop.getAllProcesosSacop();
    request.setAttribute("procesosSacop", procesosSacop);
    
	// request.setAttribute("processAll", (Collection)HandlerProcesosSacop.getAllProcesosSacop());
					
    
%>
 
<html>
    <head>
        <meta content="MSHTML 6.00.2800.1106" name=generator/>
        <link href="estilo/estilo.css" rel="stylesheet" type="text/css"/>
        <link rel="StyleSheet" href="css/dtree.css" type="text/css" />
		<style>
			<%for(int i=0; i<Constants.ACTION_TYPE.length;i++){%>
			.actionBig<%=i%> {
				border:0px solid #afafaf;padding:5px;background: url(images/actionBig<%=i%>.gif) no-repeat bottom center;		
			}
			<%}%>
			
		</style>
        <script type="text/javascript" src="script/dtree.js"></script>
        <script>
			var d = '';  // tree
			var preTitulo = "";
			var isCorrectiva = true;
			var nCorrectiva = 0;
			var titulos=new Array();
			var indTit=0;
			titulos[indTit++] =['Registro             ','Registro             '];
			titulos[indTit++] =['Referencias          ','Referencias          '];
			titulos[indTit++] =['Procesos             ','Procesos             '];
			titulos[indTit++] =['Responsable del Proceso ','Responsable del Proceso '];
			titulos[indTit++] =['Requisitos Aplicables','Requisitos Aplicables'];
			titulos[indTit++] =['Informar             ','Informar             '];
			titulos[indTit++] =['Causas               ','Causas               '];
			titulos[indTit++] =['Acciones Recomendadas','Acciones Recomendadas'];
			titulos[indTit++]=['Archivo Relacionado  ','Archivo Relacionado  '];
			titulos[indTit++]=['Sacop Relacionados   ','Sacop Relacionados   '];
			
        	function ir(nodo,tipoAccion) { //alert(nodo);
        		
        		try {
        			if(tipoAccion) {
        				fondo(tipoAccion);
        			}

	        		window.parent.frames['sacopCenter'].ir(nodo);
    	    		//window.parent.frames['sacopNorth'].setTitulo(preTitulo+" - "+titulos[nodo][nCorrectiva]);
    	    		window.parent.frames['sacopNorth'].setTitulo(titulos[nodo][nCorrectiva]);
    	    		for(var k=1;k<=titulos.length;k++){
    	    			if(document.getElementById("sd"+k)!=null) {
    	    				document.getElementById("sd"+k).className="node";
    	    			}
    	    		}
    	    		nodoActual=nodo;
    	    		//alert(nodo+"-sd"+(nodo+1));
    	    		document.getElementById("sd"+(nodo+1)).className="nodesel";
        		} catch(e) {
    	    		for(var k=1;k<=titulos.length;k++){
    	    			if(document.getElementById("sd"+k)!=null) {
	    	    			document.getElementById("sd"+k).className="node";
	    	    		}
    	    		}
    	    		//document.getElementById("sd1").className="nodesel";
        		}
        		
        		if(nodo == 9){
        			showRelatedSacopForm(true);
        		}else {
        			showRelatedSacopForm(false);
        		}
        	}
        	
        	function fondo(tipo) {
       			document.getElementById("menuSacop").className="actionBig"+tipo;
				
				var listaPreTitulos = new Array();
				<%for(int i=0; i<Constants.ACTION_TYPE.length;i++){%>
					listaPreTitulos[<%=i%>] = "<%=rb.getString("scp.actionType".concat(String.valueOf(i)))%>";
				<%}%>

				preTitulo=listaPreTitulos[tipo];

    	    	//window.parent.frames['sacopNorth'].setTitulo(preTitulo+" - "+titulos[0][0]);
    	    	window.parent.frames['sacopNorth'].setTitulo(titulos[0][0]);
        	}
        	
			var nodoActual=0;
			function anterior() {
				if(nodoActual>0) {
					nodoActual--;
					ir(nodoActual);
				}
			}
			function siguiente() {
				if(nodoActual<9) {
					nodoActual++;
					ir(nodoActual);
				}
			}
        </script>
	</head>
<body class="bodyInternas" style="margin:0px;">
<table id="menuSacop" cellspacing="0" cellpadding="0"  class="corregir" width="100%" height="100%" >
    <tr>
        <td valign="top" style="textDecoration:none;">
            <div class="item">
                <script type="text/javascript">
                    function showRelatedSacopForm(show){
                    	if(show){
                    		document.getElementById("relatedSacopForm").style.display = "";
                    	} else {
                    		document.getElementById("relatedSacopForm").style.display = "none";
                    	}
                    }
                    
	                <!--
	                var ind = 0;
					d = new dTree('d');
					d.add( ind++,-1,'<%=rb.getString("requestSacop.registerTitle0")%>');   //1
					d.add( ind++,0,'<%=rb.getString("requestSacop.registerTitle1")%>',"javascript:ir( 0)"); //0
					d.add( ind++,0,'<%=rb.getString("requestSacop.registerTitle2")%>',"javascript:ir( 1)"); //1
					d.add( ind++,0,'<%=rb.getString("requestSacop.registerTitle3")%>',"javascript:ir( 2)"); //3
					d.add( ind++,0,'<%=rb.getString("requestSacop.registerTitle4")%>',"javascript:ir( 3)"); //2
					d.add( ind++,0,'<%=rb.getString("requestSacop.registerTitle5")%>',"javascript:ir( 4)"); //4
					d.add( ind++,0,'<%=rb.getString("requestSacop.registerTitle6")%>',"javascript:ir( 5)"); //5
					d.add( ind++,0,'<%=rb.getString("requestSacop.registerTitle7")%>',"javascript:ir( 6)"); //6
					d.add( ind++,0,'<%=rb.getString("requestSacop.registerTitle8")%>',"javascript:ir( 7)"); //7
					d.add( ind++,0,'<%=rb.getString("requestSacop.registerTitle9")%>',"javascript:ir( 8)"); //8
					d.add( ind++,0,'<%=rb.getString("requestSacop.registerTitle10")%>',"javascript:showRelatedSacopForm(true); ir(9,9);"); //9
					document.write(d);
										
		    		for(var k=1;k<=titulos.length;k++){
		    			if(document.getElementById("sd"+k)!=null) {
		    				document.getElementById("sd"+k).className="node";
		    			}
		    		}
	    			document.getElementById("sd1").className="nodeSel";
					//-->
				</script>
			</div>
			<fieldset id="relatedSacopForm" style="display:none;padding:10px;">
                <legend>B&uacute;squeda</legend>
                <form id="nodo9Form" action="" onsubmit="ir(9); return false;">
                    <input type="hidden" name="buscar" value="buscar"/>
                        <table border="0" cellspacing="0" cellpadding="0" class="ppalText" style="width:100%;padding:0">
                            <tr>
                                <td style="padding-top:5px;">
                                    <%=rb.getString("scp.registerList")%>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <select id="listRegisterUnique" name="listRegisterUnique" style="width:100%;">
                                        <option value="1"><%=rb.getString("scp.registerUnique")%> </option>
                                        <option value=""><%=rb.getString("scp.registerAll")%></option>
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <td style="padding-top:5px;">
                                    <%=rb.getString("scpintouch.numero")%>&nbsp;
                                    <%=rb.getString("scpintouch.sacop")%>
                                </td>
                            </tr>
                            <tr>
                                <td nowrap>
                                    <input type="text" id="idSacop" name="idSacop" style="width:85%;height: 19px" value=""/>
                                    &nbsp;
                                    <a href="javascript: ir(9);">
                                        <img src="icons/find.png" border="0" title="<%=rb.getString("btn.search")%>"/>
                                    </a>
                                </td>
                            </tr>
                            <tr>
                                <td style="padding-top:5px;">
                                    <%=rb.getString("scp.emisor")%>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <select id="emisorSacop" name="emisorSacop" style="width:100%;">
                                        <option value=""><%=rb.getString("lista.todos")%></option>
                                        <logic:present name="todosUsuarios" scope="request">
                                            <logic:iterate id="bean" name="todosUsuarios" scope="request" type="com.desige.webDocuments.utils.beans.Search">
                                                <option value="<%=bean.getId()%>"><%=bean.getDescript()%>(<%=bean.getAditionalInfo()%>)</option>
                                            </logic:iterate>
                                        </logic:present>
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <td style="padding-top:5px;">
                                    <%=rb.getString("scp.responsable")%>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <select id="responsableSacop" name="responsableSacop" style="width:100%;">
                                        <option value=""><%=rb.getString("lista.todos")%></option>
                                        <logic:present name="todosUsuarios" scope="request">
                                            <logic:iterate id="bean" name="todosUsuarios" scope="request" type="com.desige.webDocuments.utils.beans.Search">
                                                <option value="<%=bean.getId()%>"><%=bean.getDescript()%>(<%=bean.getAditionalInfo()%>)</option>
                                            </logic:iterate>
                                        </logic:present>
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <td style="padding-top:5px;">
                                    <%=rb.getString("scp.receivingArea")%>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <select id="areaResponsableSacop" name="areaResponsableSacop" style="width:100%;">
                                        <option value=""><%=rb.getString("lista.todas")%></option>
                                        <logic:present name="todasAreas" scope="request">
                                            <logic:iterate id="bean" name="todasAreas" scope="request" type="com.desige.webDocuments.area.forms.Area">
                                                <option value="<%=bean.getIdarea()%>"><%=bean.getArea()%></option>
                                            </logic:iterate>
                                        </logic:present>
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <td style="padding-top:5px;">
                                    <%=rb.getString("scp.fueefectiva")%>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <select id="efectivaSacop" name="efectivaSacop" style="width:100%;">
                                        <option value="">--</option>
                                        <option value="0"><%=rb.getString("scp.si")%></option>
                                        <option value="1"><%=rb.getString("scp.no")%></option>
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <td style="padding-top:5px;">
                                    <%=rb.getString("scp.process")%>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <select name="idProceso" style="width:100%;">
                                        <option value=""><%=rb.getString("lista.todos")%></option>
                                        <logic:present name="procesosSacop" scope="request">
                                            <logic:iterate id="bean" name="procesosSacop" scope="request" type="com.desige.webDocuments.utils.beans.Search">
                                                <option value="<%=bean.getId()%>"><%=bean.getDescript()%> (<%=bean.getAditionalInfo()%>)</option>
                                            </logic:iterate>
                                        </logic:present>
                                    </select>
                                </td>
                            </tr>
                        </table>
                    </form>
            </fieldset>
        </td>
    </tr>
</table>
</body>
</html>
