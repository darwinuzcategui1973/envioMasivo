<!--/**
 * Title: viewDocument.jsp <br>
 * Copyright: (c) 2003 Focus Consulting<br>
 * Company: Consis International (CON)<br>
 * @author Nelson Crespo (NC)
 * @author Simón Rodriguez (SR)
 * @version Acsel-e v2.2
 * <br>
 * Changes:<br>
 * <ul>
 *      <li> 01/06/2005 (NC) Cambios para agregar algún comentario al hacer roll Back </li>
 *      <li> 06/06/2005 (SR) Cambios frameset rows="170,*,70" </li>
 *      <li> 24/08/2005 (NC) Se eliminó el frame con el pie de página, dicha información fué movida
 *                           al tope del documento</li>
 *      <li> 27/04/2006 (NC) Bugg al imprimir documentos corregido </li>
 *      <li> 09/05/2006 (SR) Si el documento no tiene permisologia, sale un documento impreso diciendo que
                             este documento se encuentra protegido.</li>
 *      <li> 30/06/2006 (NC) Cambios para correcto formato de los documentos a Mostrar </li>
 *      <li> 13/07/2006 (NC) Cambios para heredar o no los Prefijos de las Carpetas </li>
 *      <li> 14/06/2007 (YS) Si no es un FTP se cargan en sesión revisores de un documento. Se agregó función javascript recargarPublicados()</li>
 *      <li> 04/08/2008 (YS) Se carga en sesión elaborador de un documento</li>
 * <ul>
 */-->
<%@ page import="com.desige.webDocuments.utils.ToolsHTML,
                 java.util.Collection,
                 com.desige.webDocuments.persistent.managers.HandlerDocuments,
                 com.desige.webDocuments.persistent.managers.HandlerStruct,
                 com.desige.webDocuments.persistent.managers.HandlerBD,
                 com.desige.webDocuments.document.forms.BaseDocumentForm,
                 com.desige.webDocuments.document.forms.BeanFirmsDoc,
                 com.desige.webDocuments.persistent.managers.HandlerParameters,
                 com.desige.webDocuments.utils.DesigeConf,
                 com.desige.webDocuments.utils.Constants"%>
<%@ page import="com.desige.webDocuments.document.actions.loadsolicitudImpresion"%>
<%@ page import="java.util.Hashtable"%>
<%@ page import="com.desige.webDocuments.utils.beans.Users"%>
<%@ page import="java.util.ResourceBundle"%>
<%@ page import="java.util.Locale"%>
<%
    String parameters = ToolsHTML.parseParameters(request);
    String topFile = "topDocument.jsp" + parameters;
    String file = "showFile.jsp";
    boolean isExpediente = false;
    boolean isFirstPage = Boolean.parseBoolean(request.getParameter("firstPage")!=null?String.valueOf(request.getParameter("firstPage")):"false");
    
    if(request.getParameter("showFile")!=null){
    	file = request.getParameter("showFile");
    	if(file.equals("showExpediente.jsp")) {
    		isExpediente = true;
    		isFirstPage = true;
    	}
    }
    file = file + parameters;
//    String pie = "notePages.jsp" + parameters;
    //en caso que se apruebe si desea que el admninistrador imprima sin usar ningun flujo de aprobacion de impresion
    //todavia falta actualizar topDocument.jsp
    ///*if ((!swAdmin)&&(!loadsolicitudImpresion.existepermisoImpresion(forma.getIdDocument(),idUserptr))) {*/
//    boolean swAdmin = request.getParameter("swAdmin")!=null?request.getParameter("swAdmin").equalsIgnoreCase("true"):false;
    String nameFileToPrint = request.getParameter("nameFileToPrint")!=null?request.getParameter("nameFileToPrint"):"protegido.doc";
    String showAllInfo = DesigeConf.getProperty("showAllInfo");
    //String alto = "60";
    //String scroll = "NO";
    
    //YSA 01/08/2007
    String alto = "160";
    String scroll = "YES";
    
    Users usuario = (Users)request.getSession(false).getAttribute("user");

    //Si el Usuario no desea ver toda la información asociada al Documento se evita cargar data Innecesaria
    if (Constants.permissionSt.equalsIgnoreCase(showAllInfo)) {
        alto = "160";
        scroll = "YES";
    }
    BaseDocumentForm forma = new BaseDocumentForm();
    forma.setIdDocument(request.getParameter("idDocument"));
    forma.setNumberGen(request.getParameter("idDocument"));
    if(!ToolsHTML.isEmptyOrNull(request.getParameter("idVersion")) && ToolsHTML.isNumeric(request.getParameter("idVersion")))
	    forma.setNumVer(Integer.parseInt(request.getParameter("idVersion")));
	else
	    forma.setNumVer(0);
	    
    if (forma.getNumVer()!=0) {
        Hashtable tree = (Hashtable)session.getAttribute("tree");
        try {
            if (usuario!=null) {
                tree = ToolsHTML.checkTree(tree,usuario);
                HandlerStruct.loadDocument(forma,request.getParameter("downFile")!=null,false,tree,request);
                session.setAttribute("showDocumentI",forma);
            } else {
                String ipClient = request.getRemoteAddr();
                String ipServe  = request.getLocalAddr();
                int posPoint = ipClient.indexOf(".");
                //Si el Cliente y el Servidor tienen la Misma Dirección IP se procede a Mostrar el Documento
                if (ipClient.substring(0,posPoint).equalsIgnoreCase(ipServe.substring(0,posPoint))) {
                    HandlerStruct.loadDocument(forma,request.getParameter("downFile")!=null,false,null,request);
                    session.setAttribute("showDocumentI",forma);
                } else {
                    String url = request.getServletPath()+parameters;
                    session.setAttribute("url",url);
                    ResourceBundle rb = ToolsHTML.getBundle(request);
                    session.setAttribute("error",rb.getString("err.userValid"));
                    response.sendRedirect("index.jsp");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            session.setAttribute("info","Error:");
        }
    }
    Collection firmas = null;
    Collection firmasRevisores = null;
    Collection firmaPropietario = null;
    BeanFirmsDoc firmaElaborador = null;
    Collection firmasColaboradores = null;
    
            //HandlerDocuments.loadFirmsToVersionDoc(request.getParameter("idDocument"),
                        //                                       request.getParameter("idVersion"),String.valueOf(forma.getStatu()));

	boolean viewReaders = String.valueOf(HandlerParameters.PARAMETROS.getViewReaders()).equals("1");
	boolean viewCreator = String.valueOf(HandlerParameters.PARAMETROS.getViewCreator()).equals("1");
	boolean viewCollaborator = String.valueOf(HandlerParameters.PARAMETROS.getViewCollaborator()).equals("1");

    if (forma!=null&& forma.getTypeWF() == Constants.permission) {
        firmas = HandlerDocuments.loadFirmsToVersionDoc(request.getParameter("idDocument"),
                                                        request.getParameter("idVersion"),
                                                        String.valueOf(forma.getStatu()),true);
    } else {
        firmas = HandlerDocuments.loadFirmsToVersionDoc(request.getParameter("idDocument"),
                                                        request.getParameter("idVersion"),
                                                        String.valueOf(forma.getStatu()),false);
        if(viewReaders){
	        firmasRevisores = HandlerDocuments.loadFirmsReviewsToVersionDoc(request.getParameter("idDocument"),
	                                                        request.getParameter("idVersion"),
	                                                        String.valueOf(forma.getStatu()));                                                
        }
    }
    if ((firmas!=null)&&(!firmas.isEmpty())) {
        session.setAttribute("firmas",firmas);
    } else {
        session.removeAttribute("firmas");
		firmaPropietario = HandlerDocuments.getSignatureOwnerDocument(request.getParameter("idDocument"),
                                                       request.getParameter("idVersion"),
	                                                   String.valueOf(forma.getStatu()));
	    if ((firmaPropietario!=null)&&(!firmaPropietario.isEmpty())) {
        	session.setAttribute("firmaPropietario",firmaPropietario);
        }else{
          	session.removeAttribute("firmaPropietario");  
        }
    }
    
    if(viewCreator){
    	firmaElaborador = HandlerDocuments.getCreatorDocument(request.getParameter("idDocument"),
                                                       request.getParameter("idVersion"));
	    if (firmaElaborador!=null) {
        	session.setAttribute("firmaElaborador",firmaElaborador);
        }else{
          	session.removeAttribute("firmaElaborador");  
        }
    }
        
    if(viewCollaborator){
        firmasColaboradores = HandlerDocuments.loadFirmsCollaboratorToVersionDoc(request.getParameter("idDocument"),
                                                        request.getParameter("idVersion"),
                                                        String.valueOf(forma.getStatu()));                                                
        
    }
        
    if(viewCreator){
    	session.setAttribute("viewCreator",viewCreator);
   	}else{
   		session.removeAttribute("viewCreator");
   	}
   	
   	if ((firmasRevisores!=null)&&(!firmasRevisores.isEmpty())) {
        session.setAttribute("firmasRevisores",firmasRevisores);
    } else {
        session.removeAttribute("firmasRevisores");
    }
    
    if(viewCollaborator){
    	session.setAttribute("viewCollaborator",viewCollaborator);
	   	if ((firmasColaboradores!=null)&&(!firmasColaboradores.isEmpty())) {
	        session.setAttribute("firmasColaboradores",firmasColaboradores);
	    } else {
	        session.removeAttribute("firmasColaboradores");
	    }
   	}else{
   		session.removeAttribute("viewCollaborator");
  	}
    
    if(viewReaders){
    	session.setAttribute("viewReaders",viewReaders);
   	}else{
   		session.removeAttribute("viewReaders");
   	}
   	
    
    String idUserptr = (String)session.getAttribute("idUser");
    boolean printFree = false;
    if (session.getAttribute("printFree")!=null&&"true".compareTo((String)session.getAttribute("printFree"))==0) {
        printFree = true;
    }
    if (!printFree && !loadsolicitudImpresion.existepermisoImpresion(forma.getIdDocument(),idUserptr)) {
        nameFileToPrint = "protegido.doc";
    }
    
	String sImpNameCharge = String.valueOf(HandlerParameters.PARAMETROS.getImpNameCharge());    
	int impNameCharge = 0;
	if(!ToolsHTML.isEmptyOrNull(sImpNameCharge) && ToolsHTML.isNumeric(sImpNameCharge)){
		impNameCharge = Integer.parseInt(sImpNameCharge);
	}
	session.setAttribute("nameCharge",impNameCharge);
%>
<html>
<head>
	<title> Qwebdocuments </title>
	<jsp:include page="meta.jsp" /> 
	<%if ("protegido.doc".equalsIgnoreCase(nameFileToPrint)) {%>
	    <link rel=alternate media=print href="<%=nameFileToPrint%>">
	<%}%>

<script language="Javascript">
	var key = new Array();
	key['p'] = "No imprimirás.";
	key['c'] = "No copiarás.";
	key['f'] = "No buscarás.";
	
	function getKey(keyStroke) {
		if(isIE7()){
			return;			
		}
		var isNetscape=(window.clipboardData?false:true);
		var hacer = (isNetscape? keyStroke.ctrlKey : window.event.ctrlKey);
		if (hacer) {
			eventChooser = (isNetscape) ? keyStroke.which : event.keyCode;
			which = String.fromCharCode(eventChooser).toLowerCase();
			alert("Opción no valida");
		}
	}
	document.onkeydown = getKey;

	function recargarPublicados(){
		if (window.opener!=null && window.opener.location!=null){
			nombre = window.opener.location;
			nombre = nombre+'';
			if(nombre.indexOf('showPublished.do')>1 || nombre.indexOf('orderDocument.do')>1){
				window.opener.searchItem(window.opener.document.search,'showPublished.do',1);
			}
		}
	}

	<%
	boolean validNavigator = false;
	try {
		// validar o no el navegador - para este caso ie7
		validNavigator = String.valueOf(HandlerParameters.PARAMETROS.getValidNavigator()).equals("1");
	} catch (Exception e) {
		e.printStackTrace();
	}
    if(validNavigator) {%>
	if (navigator.appVersion.indexOf("MSIE 6")==-1) {
		<%    ResourceBundle rb2 = ToolsHTML.getBundle(request);%>
		alert("<%=rb2.getString("doc.printVersionValid")%>");
		window.close();
	}
	<%}%>
	
	function hideBtnImp(){
		try{
			//window.window.opener.ocultarImpresora(<%=request.getParameter("idLogoImp")%>);
			window.opener.ocultarImpresora(<%=request.getParameter("idLogoImp")%>);
		} catch(e){}
	}
	//alert("<%=request.getParameter("idDocument")%>");
	//alert("<%=topFile%>");
	//alert("<%=file%>");

function getScrollXY() {
	var winW = 630, winH = 460;
	
	if (parseInt(navigator.appVersion)>3) {
	 if (navigator.appName=="Netscape") {
	  winW = window.innerWidth;
	  winH = window.innerHeight;
	 }
	 if (navigator.appName.indexOf("Microsoft")!=-1) {
	  winW = document.body.offsetWidth;
	  winH = document.body.offsetHeight;
	 }
	}
	return [ winW, winH ];
}
	function listo() {
		//window.parent.document.title="Qwebdocuments";
		//window.resizeTo(812,638);
		//window.document.body.scroll = 'no';  
		
	}
	
	function test() {
		//pagina = document.location;
		//abrirVentanaSinScroll(pagina,800,600,"nueva")
	}

function getWindowXY() {
  var myWidth = 0, myHeight = 0;
  if( typeof( window.parent.innerWidth ) == 'number' ) {
    //Non-IE
    myWidth = window.innerWidth;
    myHeight = window.innerHeight;
  } else if( document.documentElement && ( document.documentElement.clientWidth || document.documentElement.clientHeight ) ) {
    //IE 6+ in 'standards compliant mode'
    myWidth = document.documentElement.clientWidth;
    myHeight = document.documentElement.clientHeight;
  } else if( document.body && ( document.body.clientWidth || document.body.clientHeight ) ) {
    //IE 4 compatible
    myWidth = document.body.clientWidth;
    myHeight = document.body.clientHeight;
  }
  return [ myWidth , myHeight ];
}

	document.oncontextmenu = function(){return false;}
	//alert('<%=file%>');
</script>
</head>
<%if(Constants.PRINTER_PDF || request.getAttribute("filesPreview")!=null ) {%>
	<body onkeydown="return false" onload="listo()">
	<script language="javascript">
	document.oncontextmenu = function(){return false;}
	</script>
	<%if (request.getHeader("USER-AGENT").indexOf("MSIE")!=-1) { %>
		<iframe name="wndDocument" src="<%=file%>" style="width:103%;height:101%;position:absolute;top:0px;left:0px"/></iframe>
		<iframe id="barrapdf" name="barrapdf" src="barrapdf.jsp?idDocument=<%=request.getParameter("idDocument")%>&idVersion=<%=request.getParameter("idVersion")%>&isExpediente=<%=isExpediente%>&firstPage=<%=isFirstPage%>" style="height:39px;position:absolute;top:0px;left:0px;width:103%;z-index:1000;" scrolling="no" frameborder="0"></iframe>
		<!--<iframe name="messagepdf" src="messagepdf.jsp?idDocument=<%=request.getParameter("idDocument")%>&idVersion=<%=request.getParameter("idVersion")%>" style="height:300px;position:absolute;top:300px;left:0px;width:103%" scrolling="no" frameborder="0"></iframe>-->
	<%} else {%>
		<iframe name="wndDocument" src="<%=file%>" style="width:99%;height:99%;position:absolute;top:0px;left:0px"/></iframe>
		<iframe id="barrapdf" name="barrapdf" src="barrapdf.jsp?idDocument=<%=request.getParameter("idDocument")%>&idVersion=<%=request.getParameter("idVersion")%>&isExpediente=<%=isExpediente%>&firstPage=<%=isFirstPage%>" style="height:39px;position:absolute;top:0px;left:0px;width:100%;z-index:10000;" scrolling="no" frameborder="0"></iframe>
		<!--<iframe name="messagepdf" src="messagepdf.jsp?idDocument=<%=request.getParameter("idDocument")%>&idVersion=<%=request.getParameter("idVersion")%>" style="height:300px;position:absolute;top:300px;left:0px;width:103%" scrolling="no" frameborder="0"></iframe>-->
	<%}%>
	</body>
<%} else {%>
	<frameset id="ventana" rows="<%=alto%>,*" border="0" frameborder="0" framespacing="0" style="border:0px #ffffff;z-index:1000;" onunload="javascript:recargarPublicados();" >
	    <frame id="cabezera" src="<%=topFile%>" name="cabezera" scrolling="auto" style=""/>
	    <frame id="documento" src="<%=file%>" name="documento" scrolling="YES" noresize/>
	</frameset>
<%}%>
</html>