<!--
 * Title: superior.jsp <br/>
 * Copyright: (c) 2004 Desige Servicios de Computación<br/>
 * @author Ing. Simón Rodriguez.
 * @author Ing. Nelson Crespo.
 * @version WebDocuments v1.0
 * <br/>
 *       Changes:<br/>
 *           20/04/2006 (SR) Se agrego un rb.getString("");
 * </ul>
-->
<%@page import="com.desige.webDocuments.utils.DesigeConf"%>
<%@ page import="java.util.ResourceBundle,
				 com.desige.webDocuments.persistent.managers.HandlerBD,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.persistent.managers.HandlerMessages,
				 com.desige.webDocuments.utils.Constants,
                 com.desige.webDocuments.utils.beans.Users"%>
<%
    ResourceBundle rb = ToolsHTML.getBundle(request); 

    Users usuario = (Users)session.getAttribute("user");
	int mails = 0;
	if(usuario!=null){
		mails = HandlerMessages.getTotalInbox(usuario.getEmail());
	}

 	String opt = HandlerBD.getOptionMenuHead((int)((Users)session.getAttribute("user")).getIdPerson());

 	String path = request.getRequestURL().toString().replace(request.getRequestURI(), request.getContextPath()) + "/";
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<style>
.superior1 {
	background: url(img/bannerIzquierda1.png); background-repeat: no-repeat;
	background-color:#799fc6; 
}
.superior2 {
	background: url(img/qwebbannersuperior.png); background-repeat: no-repeat;
	background-color:#c2d2e0;
}
.superior3 {
width: 100%;
  height: 100px;
  background-image:  url(img/bannerIzquierda1.png), url(img/bannerDerecha1.png), linear-gradient(to right,#84cafe, #69b5ff);
  background-repeat: no-repeat, no-repeat, no-repeat;
  background-position:top left,top right,top center;
  background:
    -moz-linear-gradient(
      to right,
      rgba(130, 202, 253, 1),
      rgba(255, 255, 255, 0)
    ),
    -webkit-gradient(to right, rgba(130, 202, 253, 1), rgba(255, 255, 255, 0)),
    -ms-linear-gradient(to right, rgba(130, 202, 253, 1), rgba(255, 255, 255, 0)),
    linear-gradient(to right, rgba(130, 202, 253, 1), rgba(255, 255, 255, 0));
}

</style>
<script type="text/javascript">
    //esta funcion se ejecutara cuando el elemento superior "superior.jsp"
    //reciba un evenco de clic
    //allí verificaremos si estamos ingresando en la sección de busqueda
    //para automaticamente colocar el foco en ese contenedor ("info")
    document.onclick=function(e){
    	frameObj = null;
        objElementToSetFocus = null;
        
        isIE = (navigator.appName == "Microsoft Internet Explorer");
        if(isIE) {
    		frameObj = window.parent.document.frames["info"];
    	}else{
    		frameObj = window.parent.document.getElementById("info");
    	}
    	
    	//alert(frameObj);
    	if(frameObj != null){
    		if(isIE) {
                //is IE
                objElementToSetFocus = frameObj.document.getElementById("keys")
            } else {
                //no IE
                objElementToSetFocus = frameObj.contentDocument.getElementById("keys")
            }
            
            //alert(objElementToSetFocus);
            
            if(objElementToSetFocus != null) {
                objElementToSetFocus.focus();
            }
        }
    }
</script>
<script language="JavaScript">

    var winWidth  = 600;
    var winHeight = 800;

    function setDimensionScreen() {
        if (screen.availWidth) {
            winWidth = screen.availWidth;
        }
        if (screen.availHeight){
            winHeight = screen.availHeight - 100;
        }
    }
    function cambieContrasenia() {
        alert('<%=rb.getString("enl.perfilseg")%>');
    }
    function getPosition(totalValue,value){
        var calculo = (totalValue - value) / 2;
        if (calculo < 0){
            return 0;
        }
        return calculo;
    }
    function abrirVentana(pagina,width,height,nameWin) {
        //alert('Mostrando a: ' + pagina);
        var hWnd = null;
        var left = getPosition(winWidth,width);
        var top = getPosition(winHeight,height);
        hWnd = window.open("<%=path%>manual.do", nameWin, "resizable=yes,scrollbars=yes,statusbar=yes,width="+width+",height="+height+",left="+left+",top="+top);
    }

    setDimensionScreen();
    
	var http = false;
    
	function sele(obj){
		if(navigator.appName == "Microsoft Internet Explorer") {
		  http = new ActiveXObject("Microsoft.XMLHTTP");
		} else {
		  http = new XMLHttpRequest();
		}
		http.open("POST", "markOptionAjax.do?campo=<%=Constants.COLUMN_MENUHEAD_NAME%>&columna="+obj);
		http.onreadystatechange=function() {
		  if(http.readyState == 4) {
		  	//nada
		  }
		}
		http.send(null);
    }
    
    function moduloActivo(nameModule,target) {
    	window.parent.document.getElementById(target).src=nameModule;
    }
    

</script>

</head>
<body class="superior3">
<span class="preload1"></span>
<span class="preload2"></span>
    <div class="posMenu" style="width:100%">
         <table cellSpacing=0 cellPadding=0 align=center border="0" width="100%">
            <tr>
                
                <td width="44%" class="titleLeft">
                	<img src="icons/user.png" border="2">
                    <%=session.getAttribute("usuario")%>
                </td>
                <td width="26%" class="titleLeft">
                </td>
                <td width="15%" class="titleLeft">
		        	<a href="<%=rb.getString("href.messages")%>" class="ahrefMail" target="info">
                	<img src="icons/email.png" border="0">
		            	<span id="mailwithoutreader"><%=mails%></span> <%=rb.getString("cbs.pendingMails")%>	
		            </a>                    
                    -
                	[
                    <a href="logout.do" class="ahrefMail" target="_parent">
                       <%=rb.getString("enl.logout")%>
                    </a>
                    ]
                    &nbsp;
                   	<img src="icons/help.png" class="helpBTN" alt='<%=rb.getString("show.help")%>' onclick="abrirVentana('download/manual.pdf',800,800,'help')">
                </td>
            </tr>
        </table>
        <br/>
        <table id="opciones" cellSpacing=0 cellPadding=0 align=center border="0" width="100%">
            <tr>
            	<td width="200%">
					<ul class="prodrop4" >
						<%if (usuario!=null) {
                            out.println(ToolsHTML.printMenu(rb,usuario.getUser(),usuario.getIdGroup(),false,request));
                        }%>
					</ul>
                </td>
            </tr>
        </table>
    </div>
    <div style="position:absolute;left:5px;top:40px;">
    	<img id="up" onclick="hideMenu()" src="images/up2.gif" title="<%=rb.getString("sup.hide")%>">
    	<img id="down" onclick="showMenu()" src="images/down2.gif" style="display:none" title="<%=rb.getString("sup.show")%>">
    </div>
</body>
</html>
<script language="javascript">
function hideMenu(){
	document.getElementById("up").style.display="none";
	document.getElementById("down").style.display="";
	sele("N");
	mover(93,58);
}
function showMenu(){
	document.getElementById("up").style.display="";
	document.getElementById("down").style.display="none";
	document.getElementById("opciones").style.display="";
	window.parent.document.getElementById('principal').rows='93,*';
	document.body.className="superior3";
	sele("S");
	mover(58,93);
}
function mover(valor,limite){
	var mostrar=limite>valor;
	var cont=parseInt(valor);
	var tamano;
	cont=(mostrar?cont+1:cont-1);
	tamano=""+cont+",*"
	window.parent.document.getElementById('principal').rows=tamano;
	if(mostrar && cont<limite || !mostrar && cont>limite){
		var cad="mover("+cont+","+limite+")";
		setTimeout(cad,10);
	} else if(!mostrar) {
		document.getElementById("opciones").style.display="none";
		document.body.className="superior3";
	}
}
if('<%=opt%>'==='N'){
	document.getElementById("up").style.display="none";
	document.getElementById("down").style.display="";
	window.parent.document.getElementById('principal').rows="58,*";
}
</script>
