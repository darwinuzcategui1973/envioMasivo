<!-- /**
 * Title: sacopSouth3.jsp<br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @author Ing. Simon Rodríguez (SR)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li>Nothing</li>
 </ul>
 */ -->
 <%@ page language="java" %>
<%
String bandejaToReload = "";
if(request.getSession().getAttribute("bandejaToReload") != null){
    bandejaToReload = (String) request.getSession().getAttribute("bandejaToReload"); 
}
%>
<html>
    <head>
        <meta content="MSHTML 6.00.2800.1106" name=generator/>
        <link href="estilo/estilo.css" rel="stylesheet" type="text/css"/>
        <script>
        	function anterior() {
        		window.parent.frames['sacopCenter'].anterior();
        	}
        	function siguiente() {
        		window.parent.frames['sacopCenter'].siguiente();
        		
        	}
        	function guardar() {
        		if(typeof window.parent.frames['sacopCenter'].guardar != 'undefined') {
	        		//cancelar();
    	    		window.parent.frames['sacopCenter'].guardar();
    	    	}
        	}
        	function emitir() {
        		if(typeof window.parent.frames['sacopCenter'].emitir != 'undefined') {
	        		//cancelar();
    	    		window.parent.frames['sacopCenter'].emitir();
    	    	}
        	}
        	function cancelar() {
        		window.parent.parent.administrarCancelar();
        	}
        	
        	function showAnteriorBtn(showBtn){
        		var btn = document.getElementById('btnAnterior');
        		if(showBtn){
        			btn.disabled = false;
        		} else{
        			btn.disabled = true;
        		}
        	}
        	
        	function showSiguienteBtn(showBtn){
                var btn = document.getElementById('btnSiguiente');
                if(showBtn){
                    btn.disabled = false;
                } else{
                    btn.disabled = true;
                }
            }
        	
        	function showEmitirBtn(showBtn){
                var btn = document.getElementById('btnEmitir');
                if(showBtn){
                    btn.disabled = false;
                } else{
                    btn.disabled = true;
                }
            }
        	
        	function showGuardarBtn(showBtn){
                var btn = document.getElementById('btnGuardar');
                if(showBtn){
                    btn.disabled = false;
                } else{
                    btn.disabled = true;
                }
            }
        </script>
	</head>
<body class="bodyInternas" style="margin:0px;">
<table cellspacing="0" cellpadding="0"  style="border:1px solid #afafaf;" width="100%" height="100%" bgcolor="#c2d2e0">
<tr>
	<td align="right">
	   <input type="button" id="btnAnterior" value="Anterior" onclick="anterior()" style="width:100px;" disabled />
	   &nbsp;&nbsp;
	   <input type="button" id="btnSiguiente" value="Siguiente" onclick="siguiente()" style="width:100px;"/>
	   &nbsp;&nbsp;
	   <span id="botones">
	   <input type="button" id="btnEmitir" value="Emitir" onclick="emitir()" style="width:100px;" disabled />
	   &nbsp;&nbsp;
	   <input type="button" id="btnGuardar" value="Guardar" onclick="guardar()" style="width:100px;" disabled />
		&nbsp;&nbsp;
		</span>
		<input type="button" id="btnCancelar" value="Cancelar" onclick="cancelar()" style="width:100px;"/>
		&nbsp;&nbsp;&nbsp;&nbsp;
	</td>
</tr>
</table>
</body>
</html>
