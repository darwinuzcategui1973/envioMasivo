var winWidth = 600;
var winHeight = 800;

String.prototype.trim = function() {
	return this.replace(/^\s*|\s*$/g, "");
};
String.prototype.endsWith = function(str) {
	return (this.match(str + "$") == str)
};

function setDimensionScreen() {
	if (screen.availWidth) {
		winWidth = screen.availWidth;
	}
	if (screen.availHeight) {
		winHeight = screen.availHeight - 100;
	}
}
setDimensionScreen();

function showReporteListaMaestra(sql_query) {
	window
			.open(
					"reportelistamaestra.jsp?sql_query=" + sql_query,
					"WebDocuments",
					"resizable=no,scrollbars=yes,statusbar=yes,width=800,height=600,left=115,top=50");
}

function MsgInEditor(text, editor) {
	editor.docHtml = text.innerText;
}

function showDocRelations(pages, title, idDoc, value, width, height) {
	abrirVentana(pages + "?read=true&value=" + value + "&idDoc=" + idDoc
			+ "&title=" + title, width, height);
}

function getPosition(totalValue, value) {
	var calculo = (totalValue - value) / 2;
	if (calculo < 0) {
		return 0;
	}
	return calculo;
}

function abrirVentanaAccionSacop(pagina, width, height) {
	var hWnd = null;
	var left = 200;
	var top = 200;
	hWnd = window.open(pagina, "WebDocuments",
			"resizable=no,scrollbars=yes,statusbar=yes,width=" + width
					+ ",height=" + height + ",left=" + left + ",top=" + top);
}

function abrirVentanaSinScroll(pagina, width, height, targeta) {
	var hWnd = null;
	var left = getPosition(winWidth, width);
	var top = getPosition(winHeight, height);
	hWnd = window.open(pagina, (typeof targeta == 'undefined') ? "WebDocuments"
			: targeta, "resizable=yes,scrollbars=no,statusbar=yes,width="
			+ width + ",height=" + height + ",left=" + left + ",top=" + top);
}

function abrirVentanaSinResize(pagina, width, height, targeta) {
	var hWnd = null;
	var left = getPosition(winWidth, width);
	var top = getPosition(winHeight, height);
	hWnd = window.open(pagina, (typeof targeta == 'undefined') ? "WebDocuments"
			: targeta, "resizable=no,scrollbars=yes,statusbar=yes,width="
			+ width + ",height=" + height + ",left=" + left + ",top=" + top);
	return hWnd;
}

function abrirVentana(pagina, width, height) {
	var hWnd = null;
	var left = getPosition(winWidth, width);
	var top = getPosition(winHeight, height);
	hWnd = window.open(pagina, "WebDocuments",
			"resizable=yes,scrollbars=yes,statusbar=yes,width=" + width
					+ ",height=" + height + ",left=" + left + ",top=" + top);
}

function abrirVentanaFirmas(pagina, width, height, nameWin) {
	var hWnd = null;
	var left = getPosition(winWidth, width) + 30;
	var top = getPosition(winHeight, height) + 50;
	hWnd = window.open(pagina, nameWin,
			"resizable=yes,scrollbars=yes,statusbar=yes,width=" + width
					+ ",height=" + height + ",left=" + left + ",top=" + top);
}

function abrirVentana(pagina, width, height, nameWin) {
	var hWnd = null;
	setDimensionScreen();
	var left = getPosition(winWidth, width);
	var top = getPosition(winHeight, height);
	hWnd = window.open(pagina, nameWin,
			"resizable=yes,scrollbars=yes,statusbar=yes,width=" + width
					+ ",height=" + height + ",left=" + left + ",top=" + top);
}

function abrirVentanaFull(pagina, nameWin) {
	var hWnd = null;
	setDimensionScreen();
	var left = 0;
	var top = 0;
	hWnd = window.open(pagina, nameWin,
			"resizable=yes,scrollbars=yes,statusbar=yes,width=" + winWidth
					+ ",height=" + winHeight + ",left=" + left + ",top=" + top);
}

function abrirVentananorms(pagina, width, height) {
	var hWnd = null;
	var left = getPosition(winWidth, width);
	var top = getPosition(winHeight, height);
	hWnd = window.open(pagina, "WebDocuments",
			"resizable=yes,scrollbars=yes,statusbar=yes,width=" + width
					+ ",height=" + height + ",left=" + left + ",top=" + top);
}

function abrirCentrado(url) {
	var nWidth = screen.availWidth - (100);
	var nHeight = screen.availHeight - (150);
	fullscreen = window
			.open(
					url,
					"fullscreen",
					'top=50,left=50,width='
							+ (nWidth)
							+ ',height ='
							+ (nHeight)
							+ ',fullscreen=no,toolbar=0 ,location=0,directories=0,status=0,menubar=0,resizable=1,scrolling=1,scrollbars=1');
}

function openFullScreen(url) {
	fullscreen = window
			.open(
					url,
					"fullscreen",
					'top=0,left=0,width='
							+ (screen.availWidth)
							+ ',height ='
							+ (screen.availHeight)
							+ ',fullscreen=no,toolbar=0 ,location=0,directories=0,status=0,menubar=0,resizable=1,scrolling=0,scrollbars=0');
}

function showDocument(idDoc, idVersion, nameFile) {
	var hWnd = null;
	var nameFile = escape(nameFile);
	abrirVentana("viewDocument.jsp?nameFile=" + nameFile + "&idDocument="
			+ idDoc + "&idVersion=" + idVersion, 800, 600);
}

function showPaginaFirmantes(a, b, c) {
	var hWnd = null;
	abrirVentanaFirmas("viewDocumentFirmas.jsp?a=" + a + "&b=" + b + "&c=" + c,
			812, 638, "firmas");
}

function showDocumentimprimir(idDoc, idVersion, nameFile, imprimir, nameWin, isVisorJava) {
	//isVisorJava = true;
	console.log("isVisorJava="+isVisorJava);
	debugger;
    if (isVisorJava) {
    //    alert("aqui java");
		showDocumentimprimirJAVA(idDoc, idVersion, nameFile, imprimir, nameWin);
	} else {
		//showDocumentimprimirJAVA(idDoc, idVersion, nameFile, imprimir, nameWin);
		showDocumentimprimirADOBE(idDoc, idVersion, nameFile, imprimir, nameWin);
	}
}

function showDocumentimprimirADOBE(idDoc, idVersion, nameFile, imprimir,
		nameWin) {
	var hWnd = null;
	var nameFile = escape(nameFile);
	var toPrint = escape(nameFile);
	if (imprimir == '0') {
		toPrint = "protegido.doc";
	}
	if ((typeof nameWin) == 'undefined') {
		nameWin = 'QWebDocuments';
	}

	var url = "loadDataDoc.do?nameFile=" + nameFile + "&idDocument=" + idDoc
						+ "&idVersion=" + idVersion + "&imprimir=" + imprimir
						+ "&nameFileToPrint=" + toPrint + "&copyContents=false";

				document.location.href = url;
				

			// showVisor(url);
}

function showDocumentimprimirJAVA(idDoc, idVersion, nameFile, imprimir, nameWin) {
	console.log("showDocumentImprimirJAVA");
	var nameFile = escape(nameFile);
	var toPrint = escape(nameFile);
 
	respuesta = "false";
	if (navigator.appName == "Microsoft Internet Explorer") {
		http = new ActiveXObject("Microsoft.XMLHTTP");
	} else {
		http = new XMLHttpRequest();
	}

      //alert("aqui java2");
      //console.log("aqui loadDataDocTarget");

	http.open("POST", "loadDataDocTarget.do?idDocument="+idDoc+"&idVersion="+idVersion);
	//http.open("POST", "VisorVideoTarget.do?idDocument="+idDoc+"&idVersion="+idVersion);  
	http.onreadystatechange=function() {
		if(http.readyState == 4) { 
			 var respuesta = http.responseText;
			if(respuesta=='true') {
			// showFile.jsp /load.jsp
				abrirVentanaSinScroll("showFile.jsp?accion=loadDataDoc.do&nameFile=" + nameFile
							+ "&idDocument=" + idDoc + "&idVersion=" + idVersion + "&imprimir="
							+ imprimir + "&nameFileToPrint=" + toPrint + "&copyContents=false",
							800, 600, nameWin);
			} else {
			
			
				var url = "loadDataDoc.do?nameFile=" + nameFile + "&idDocument=" + idDoc
						+ "&idVersion=" + idVersion + "&imprimir=" + imprimir
						+ "&nameFileToPrint=" + toPrint + "&copyContents=false";

				// alert(url);
				// abrirVentanaSinResize // abrirVentanaSinScroll //abrirCentrado
				
				document.location.href = url;
				
			}
		} 
	} 
	http.send(null);
	
	/*
	*/
	
	// window.open(url);
	
}

function showVisor(docUrl) {
	respuesta = "false";

	if (navigator.appName == "Microsoft Internet Explorer") {
		http = new ActiveXObject("Microsoft.XMLHTTP");
	} else {
		http = new XMLHttpRequest();
	}

	docUrl = docUrl.replace(/\?/g, '-SIGNO-');
	docUrl = docUrl.replace(/=/g, '-IGUAL-');
	docUrl = docUrl.replace(/&/g, '-AMPER-');

	var params = "urlDocumento=" + docUrl;
	// alert("visorPdfAjax.do?"+params);
	http.open("POST", "visorPdfAjax.do?" + params);

	http.onreadystatechange = function() {
		if (http.readyState == 4) {
			// alert(getPathFromLocation() + http.responseText);
			// document.location.href=getPathFromLocation()+"editorPorInternet/qpdf4<%=
			// users.getIdPerson()%>.jnlp";
			document.location.href = getPathFromLocation() + http.responseText;
		}
	};
	http.send();

}

function getPathFromLocation() {
	var cad = document.location.href;
	cad = cad.replace("http://", "");
	var arr = cad.split("/");
	cad = "http://" + arr[0] + "/";
	if (!arr[1].endsWith(".do")) {
		cad = cad + arr[1] + "/";
	}
	return cad;
}

function getServerLocation() {
	var cad = document.location.href;
	cad = cad.replace("http://", "");
	var arr = "";
	if (cad.indexOf(":") != -1) {
		arr = cad.split(":");
	} else {
		var arr = cad.split("/");
	}
	return arr[0];
}

function showFilesimprimir(idDoc, idVersion, imprimir, nameFile, idLogoImp) {
	var hWnd = null;
	var nameFile = escape(nameFile);
	var toPrint = escape(nameFile);

	// abrirVentanaSinScroll("FilesLoadData.do?idDocument=" + idDoc +
	// "&idVersion=" + idVersion + "&imprimir=" + imprimir +
	// "&nameFileToPrint="+toPrint+"&idLogoImp="+idLogoImp,800,600);
	// abrirVentanaSinScroll("load.jsp?accion=FilesLoadData.do&idDocument=" +
	// idDoc + "&idVersion=" + idVersion + "&imprimir=" + imprimir,800,600);

	var params = "f1=" + idDoc;
	params += "&numVersion=" + idVersion;
	params += "&imprimir=" + imprimir;

	var url = "visorFilesPdfAjax.do?" + params;

	document.location.href = url;

	// pedimos por ajax el .jnlp asociado a la visualizacion de expedientes
	/*
	 * if (typeof XMLHttpRequest == "undefined" ){ http = new
	 * ActiveXObject("Microsoft.XMLHTTP"); } else { http = new XMLHttpRequest(); }
	 * 
	 * 
	 * http.onreadystatechange=function() { if(http.readyState == 4) {
	 * alert(getPathFromLocation()+http.responseText);
	 * document.location.href=getPathFromLocation()+http.responseText; } };
	 * 
	 * http.open("POST", "visorFilesPdfAjax.do");
	 * http.setRequestHeader("Content-type",
	 * "application/x-www-form-urlencoded"); http.send(params);
	 */
}

function showDocumentPublishImp(idDoc, idVersion, nameFile, imprimir, idLogoImp) {
    showDocumentimprimir(idDoc, idVersion, nameFile, imprimir, idLogoImp, true);
	/*
	 * var hWnd = null; var nameFile = escape(nameFile); var toPrint =
	 * escape(nameFile); if (imprimir == '0') { toPrint = "protegido.doc"; }
	 * // alert("loadDataDoc.do?nameFile=" + nameFile + "&idDocument=" + idDoc +
	 * "&idVersion=" + idVersion + "&imprimir=" + imprimir +
	 * "&nameFileToPrint="+toPrint);
	 * //abrirVentanaSinScroll("loadDataDoc.do?nameFile=" + nameFile +
	 * "&idDocument=" + idDoc + "&idVersion=" + idVersion + "&imprimir=" +
	 * imprimir + "&nameFileToPrint="+toPrint,800,600);
	 * abrirVentanaSinScroll("load.jsp?accion=loadDataDoc.do&nameFile=" +
	 * nameFile + "&idDocument=" + idDoc + "&idVersion=" + idVersion +
	 * "&imprimir=" + imprimir +
	 * "&nameFileToPrint="+toPrint+"&idLogoImp="+idLogoImp,800,600);
	 */
}

function showDocumentAttached(item, idDoc, idVersion, nameFile, imprimir,
		idLogoImp, prefijo) {
	var hWnd = null;
	var nameFile = escape(nameFile);
	var toPrint = escape(nameFile);
	if (imprimir == '0') {
		toPrint = "protegido.doc";
	}
	// alert("loadFrame.jsp?accion=loadDataDoc.do&nameFile=" + nameFile +
	// "&idDocument=" + idDoc + "&idVersion=" + idVersion + "&imprimir=" +
	// imprimir +
	// "&nameFileToPrint="+toPrint+"&idLogoImp="+idLogoImp+"&prefijo="+prefijo+"&item="+item);
	abrirVentanaSinScroll("loadFrame.jsp?accion=loadDataDoc.do&nameFile="
			+ nameFile + "&idDocument=" + idDoc + "&idVersion=" + idVersion
			+ "&imprimir=" + imprimir + "&nameFileToPrint=" + toPrint
			+ "&idLogoImp=" + idLogoImp + "&prefijo=" + prefijo + "&item="
			+ item, winWidth, winHeight + 50);
}

function showDocumentPublish(idDoc, idVersion, nameFile) {
	var hWnd = null;
	var nameFile = escape(nameFile);
	abrirVentana("loadDataDoc.do?nameFile=" + nameFile + "&idDocument=" + idDoc
			+ "&idVersion=" + idVersion, 800, 600);
}
// 20 de JULIO 2005 INICIO
function showFirmantes(pagina, width, height) {
	return window.open(pagina, "WebDocuments",
			"resizable=no,scrollbars=yes,statusbar=yes,width=" + width
					+ ",height=" + height + ",left=115,top=50");
}
function showImpresion(pagina, width, height) {
	// window.open(pagina, "WebDocuments",
	// "resizable=no,menubar=yes,scrollbars=yes,statusbar=yes,width="+width+",height="+height+",left=115,top=50");
	window.open(pagina, "WebDocuments",
			"resizable=no,scrollbars=yes,statusbar=yes,width=" + width
					+ ",height=" + height + ",left=165,top=200");
}
function showDocumentFirmantes(idDoc, idVersion, namePropietario) {
	var hWnd = null;
	var nameFile = escape(nameFile);
	showFirmantes("loadDataFirmantes.do?namePropietario=" + namePropietario
			+ "&idDocument=" + idDoc + "&idVersion=" + idVersion, 200, 50);
			//800 600
}
function showSolicitudImpresion(idDoc, idVersion, namePropietario, nameResponsable,isMenuDocuments) {
	var hWnd = null;
	var nameFile = escape(nameFile);
	var cmdValor ="nuevo";
	if (isMenuDocuments) {
	showImpresion("loadsolicitudImpresion.do?namePropietario=" + namePropietario 
				+"&idDocument=" + idDoc + "&idVersion=" + idVersion 
				+"&nameResponsable=" + nameResponsable + "&cmd="+cmdValor , 600, 580);
				} else {
				showImpresion("loadsolicitudImpresion.do?namePropietario=" + namePropietario 
				+"&idDocument=" + idDoc + "&idVersion=" + idVersion 
				+"&nameResponsable=" + nameResponsable + "&cmd=" , 600, 580);
				}
}
// 20 DE JULIO 2005 FIN

function searchItem(forma, action, value) {
	if (forma.toSearch) {
		forma.toSearch.value = value;
	   alect (forma.action)
		forma.action = action;
		forma.submit();
	}
}

function showInfo(mensaje) {
	alert(mensaje);
}

function execApp(prog) {
	var theShell = new ActiveXObject("WScript.Shell");
	theShell.run(prog, 1, true);
}

function showDocumentInFrame(idDoc, idVersion, nameFile) {
	var hWnd = null;
	var nameFile = escape(nameFile);
	var link = "viewDocument.jsp?nameFile=" + nameFile + "&idDocument=" + idDoc
			+ "&idVersion=" + idVersion;
	location.href = link;
}

function showWindow(pages, input, nameForm, value, title, width, height, read) {
	var hWnd = null;
	hWnd = window
			.open(
					pages + "?input=" + input + "&nameForma=" + nameForm
							+ "&value=" + value + "&title=" + title + "&read="
							+ read,
					"",
					"width="
							+ width
							+ ",height="
							+ height
							+ ",resizable=no,scrollbars=yes,statusbar=yes,left=100,top=150");
	if ((document.window != null) && (!hWnd.opener)) {
		hWnd.opener = document.window;
	}
}

function showIFrame(pages, input, nameForm, value, title, read) {
	var hWnd = null;
	hWnd = window.open(pages + "?input=" + input + "&nameForma=" + nameForm
			+ "&value=" + value + "&title=" + title + "&read=" + read, input);
	if ((document.window != null) && (!hWnd.opener)) {
		hWnd.opener = document.window;
	}
}

function Trim(TRIM_VALUE) {
	if (TRIM_VALUE.length < 1) {
		return "";
	}
	TRIM_VALUE = RTrim(TRIM_VALUE);
	TRIM_VALUE = LTrim(TRIM_VALUE);
	if (TRIM_VALUE == "") {
		return "";
	} else {
		return TRIM_VALUE;
	}
} // End Function

function RTrim(VALUE) {
	var w_space = String.fromCharCode(32);
	var v_length = VALUE.length;
	var strTemp = "";
	if (v_length < 0) {
		return "";
	}
	var iTemp = v_length - 1;

	while (iTemp > -1) {
		if (VALUE.charAt(iTemp) == w_space) {
		} else {
			strTemp = VALUE.substring(0, iTemp + 1);
			break;
		}
		iTemp = iTemp - 1;

	} // End While
	return strTemp;

} // End Function

function LTrim(VALUE) {
	var w_space = String.fromCharCode(32);
	if (v_length < 1) {
		return "";
	}
	var v_length = VALUE.length;
	var strTemp = "";

	var iTemp = 0;

	while (iTemp < v_length) {
		if (VALUE.charAt(iTemp) == w_space) {
		} else {
			strTemp = VALUE.substring(iTemp, v_length);
			break;
		}
		iTemp = iTemp + 1;
	} // End While
	return strTemp;
} // End Function

function stringVacio(VALUE) {
	var v_length = VALUE.length;
	var w_space = String.fromCharCode(32);
	var iTemp = v_length - 1;
	var sw = true;
	while (iTemp > -1) {
		if (VALUE.charAt(iTemp) == w_space) {
		} else {
			sw = false;
		}
		iTemp = iTemp - 1;
	}
	return sw;
}

function hayComillas(VALUE) {
	var w_space = String.fromCharCode(32);
	var w_comillita1 = "'";
	var w_comillita2 = "`";
	var w_comillita3 = "�";

	var v_length = VALUE.length;
	var strTemp = "";
	if (v_length < 0) {
		return false;
	}
	var iTemp = v_length - 1;

	while (iTemp > -1) {
		if (VALUE.charAt(iTemp) == w_comillita1) {
			return true;
		}
		if (VALUE.charAt(iTemp) == w_comillita2) {
			return true;
		}
		if (VALUE.charAt(iTemp) == w_comillita3) {
			return true;
		}
		iTemp = iTemp - 1;
	} // End While
	return false;

} // End Function

function anyoBisiesto(anyo) {
	/**
	 * si el a�o introducido es de dos cifras lo pasamos al periodo de 1900.
	 * Ejemplo: 25 > 1925
	 */
	if (anyo < 100)
		var fin = anyo + 1900;
	else
		var fin = anyo;

	/*
	 * primera condicion: si el resto de dividir el a�o entre 4 no es cero > el
	 * a�o no es bisiesto es decir, obtenemos a�o modulo 4, teniendo que
	 * cumplirse anyo mod(4)=0 para bisiesto
	 */
	if (fin % 4 != 0)
		return false;
	else {
		if (fin % 100 == 0) {
			/**
			 * si el a�o es divisible por 4 y por 100 y divisible por 400 > es
			 * bisiesto
			 */
			if (fin % 400 == 0) {
				return true;
			}
			/**
			 * si es divisible por 4 y por 100 pero no lo es por 400 > no es
			 * bisiesto
			 */
			else {
				return false;
			}
		}
		/**
		 * si es divisible por 4 y no es divisible por 100 > el a�o es bisiesto
		 */
		else {
			return true;
		}
	}
}
function loadNoconformidadesRef(number, input, nameForm, value, type) {
	abrirVentana("loadNoconformidadesRef.do?number=" + number + "&input="
			+ input + "&nameForma=" + nameForm + "&value=" + value + "&type="
			+ type + "&userAssociate="
			+ document.sacoplantilla.noconformidadesref.value, 800, 600)
}
function loadNoconformidadesRefFrame(number, input, nameForm, value, type) {
	abrirVentana("loadNoconformidadesRef.do?number=" + number + "&input="
			+ input + "&nameForma=" + nameForm + "&value=" + value + "&type="
			+ type + "&userAssociate="
			+ document.sacoplantilla.noconformidadesref.value, 800, 600, value);
}

function fechavalbisiesto(dia, mes, anyo) {
	var febrero;
	if (anyoBisiesto(anyo))
		febrero = 29;
	else
		febrero = 28;
	/**
	 * si el mes introducido es febrero y el dia es mayor que el correspondiente
	 * al a�o introducido > alertamos y detenemos ejecucion
	 */
	if ((mes == 2) && ((dia < 1) || (dia > febrero))) {
		return '1';
	}
	/**
	 * si el mes introducido es de 31 dias y el dia introducido es mayor de 31 >
	 * alertamos y detenemos ejecucion
	 */
	if (((mes == 1) || (mes == 3) || (mes == 5) || (mes == 7) || (mes == 8)
			|| (mes == 10) || (mes == 12))
			&& ((dia < 1) || (dia > 31))) {
		return '2';
	}
	/**
	 * si el mes introducido es de 30 dias y el dia introducido es mayor de 31 >
	 * alertamos y detenemos ejecucion
	 */
	if (((mes == 4) || (mes == 6) || (mes == 9) || (mes == 11))
			&& ((dia < 1) || (dia > 30))) {
		return '3';
	}
	return '4';

}

/** **************************************************************** */
/** * ** */
/** * Tokenizer.js - JavaScript String Tokenizer Function ** */
/** * ** */
/** * Version : 0.2 ** */
/** * Date : 01.05.2005 ** */
/** * Copyright : 2005 Adrian Zentner ** */
/** * Website : http://www.adrian.zentner.name/ ** */
/** * ** */
/** * This library is free software. It can be freely used as ** */
/** * long as this this copyright notice is not removed. ** */
/** * ** */
/** **************************************************************** */

String.prototype.tokenize = tokenize;

function tokenize() {
	var input = "";
	var separator = " ";
	var trim = "";
	var ignoreEmptyTokens = true;

	try {
		String(this.toLowerCase());
	} catch (e) {
		// window.alert("Tokenizer Usage: string myTokens[] =
		// myString.tokenize(string separator, string trim, boolean
		// ignoreEmptyTokens);");
		return;
	}

	if (typeof (this) != "undefined") {
		input = String(this);
	}

	if (typeof (tokenize.arguments[0]) != "undefined") {
		separator = String(tokenize.arguments[0]);
	}

	if (typeof (tokenize.arguments[1]) != "undefined") {
		trim = String(tokenize.arguments[1]);
	}

	if (typeof (tokenize.arguments[2]) != "undefined") {
		if (!tokenize.arguments[2])
			ignoreEmptyTokens = false;
	}

	var array = input.split(separator);

	if (trim)
		for ( var i = 0; i < array.length; i++) {
			while (array[i].slice(0, trim.length) == trim)
				array[i] = array[i].slice(trim.length);
			while (array[i].slice(array[i].length - trim.length) == trim)
				array[i] = array[i].slice(0, array[i].length - trim.length);
		}

	var token = new Array();
	if (ignoreEmptyTokens) {
		for ( var i = 0; i < array.length; i++)
			if (array[i] != "")
				token.push(array[i]);
	} else {
		token = array;
	}

	return token;
}

function format(input) {
	var num = input.value.replace(/\./g, '');
	if (!isNaN(num)) {
		// num =
		// num.toString().split('').reverse().join('').replace(/(?=\d*\.?)(\d{3})/g,'$1.');
		// num = num.split('').reverse().join('').replace(/^[\.]/,'');
		input.value = num;
	} else {
		alert('Solo se permiten numeros');
		input.value = input.value.replace(/[^\d\.]*/g, '');
	}
}
function formatWithDecimal(input) {
	var num = input.value.replace(/\./g, '');
	if (!isNaN(num)) {
		num = num.toString().split('').reverse().join('').replace(
				/(?=\d*\.?)(\d{3})/g, '$1.');
		// coloca el punto decimal
		num = num.split('').reverse().join('').replace(/^[\.]/, '');
		input.value = num;
	} else {
		alert('Solo se permiten numeros');
		input.value = input.value.replace(/[^\d\.]*/g, '');
	}
}

function trim(s) {
	if (typeof s != "undefined") {
		s = "" + s;
		return s.replace(/^\s*/, "").replace(/\s*$/, "");
	} else {
		return "";
	}
}

// valida la fecha de un campo de texto

function DateFormat(vDateName, vDateValue, e, dateCheck, dateType) {
	// try {
	vDateType = dateType;
	while (vDateName.value != ""
			&& "0123456789/".indexOf(vDateName.value
					.substr((vDateValue.length - 1))) == -1) {
		vDateName.value = vDateName.value.substr(0, (vDateValue.length - 1));
	}
	if (vDateName.value != ""
			&& "0123456789/".indexOf(vDateName.value
					.substr((vDateValue.length - 1))) == -1) {
		return false;
	}
	sep1 = "";
	sep2 = "";
	dd = "";
	mm = "";
	yy = "";
	temp = "";
	valor = vDateName.value;
	cont = 0;
	isAno = 0;
	if (valor.length >= 5) {
		cad = valor.substring(valor.length - 5);
		isAno = (cad.substring(0, 1) == "/" ? 1 : 0);
	}
	// alert(isAno);
	while (valor != "" && valor.indexOf("/") != -1) {
		valor = valor.replace("/", "");
		cont = cont + 1;
	}
	var key = (window.Event) ? e.which : e.keyCode;
	if (",8,36,37,39,46,".indexOf("," + key + ",") == -1
			&& (((cont < 2 && !isAno) || valor.length == 8) || dateCheck)) {
		for ( var xx = 0; xx < valor.length; xx = xx + 1) {
			letra = valor.charAt(xx);
			temp += letra + (xx == 1 || xx == 3 ? "/" : "");
			if (xx == 0 || xx == 1) {
				dd += letra;
			} else if (xx == 2 || xx == 3) {
				mm += letra;
			} else {
				yy += letra;
			}
		}
		if (eval(yy) && yy != "" && eval(yy) < 1900 && yy.length <= 3
				&& dateCheck) {
			n = eval(yy);
			if (n >= 100) {
				yy = n + 1000;
			} else {
				yy = (n <= 35 ? 2000 + n : 1900 + n);
			}
			temp = dd + "/" + mm + "/" + yy;
		}
		vDateName.value = temp;
	}
	if (dateCheck) {
		if (!dateValid(vDateName.value)) {
			// alert("Fecha no v�lida\nIntroduzcala de nuevo "+err);
			vDateName.value = "";
			alert("Fecha no v�lida\nIntroduzcala nuevamente ");
			vDateName.focus();
			vDateName.select();
			return false;
		}
		return true;
	}
	// } catch(e) {
	//	
	// alert("El formato de fecha es dd/mm/yyyy ejemplo: 01/02/2008");
	// vDateName.value = "";
	// }

}

function dateValid(objName) {
	var strDate;
	var strDateArray;
	var strDay;
	var strMonth;
	var strYear;
	var intday;
	var intMonth;
	var intYear;
	var booFound = false;
	var datefield = objName;
	var strSeparatorArray = new Array("-", " ", "/", ".");
	var intElementNr;
	// var err = 0;
	var strMonthArray = new Array(12);
	strMonthArray[0] = "Jan";
	strMonthArray[1] = "Feb";
	strMonthArray[2] = "Mar";
	strMonthArray[3] = "Apr";
	strMonthArray[4] = "May";
	strMonthArray[5] = "Jun";
	strMonthArray[6] = "Jul";
	strMonthArray[7] = "Aug";
	strMonthArray[8] = "Sep";
	strMonthArray[9] = "Oct";
	strMonthArray[10] = "Nov";
	strMonthArray[11] = "Dec";

	// strDate = datefield.value;
	strDate = objName;

	if (strDate.length < 1) {
		return true;
	}
	for (intElementNr = 0; intElementNr < strSeparatorArray.length; intElementNr++) {
		if (strDate.indexOf(strSeparatorArray[intElementNr]) != -1) {
			strDateArray = strDate.split(strSeparatorArray[intElementNr]);
			if (strDateArray.length != 3) {
				err = 1;
				return false;
			} else {
				strDay = strDateArray[0];
				strMonth = strDateArray[1];
				strYear = strDateArray[2];
			}
			booFound = true;
		}
	}
	if (booFound == false) {
		if (strDate.length > 5) {
			strDay = strDate.substr(0, 2);
			strMonth = strDate.substr(2, 2);
			strYear = strDate.substr(4);
		}
	}
	// Adjustment for short years entered
	if (strYear.length == 2) {
		strYear = '20' + strYear;
	}
	// strTemp = strDay;
	// strDay = strMonth;
	// strMonth = strTemp;
	intday = parseInt(strDay, 10);
	if (isNaN(intday)) {
		err = 2;
		return false;
	}

	intMonth = parseInt(strMonth, 10);
	if (isNaN(intMonth)) {
		for (i = 0; i < 12; i = i + 1) {
			if (strMonth.toUpperCase() == strMonthArray[i].toUpperCase()) {
				intMonth = i + 1;
				strMonth = strMonthArray[i];
				i = 12;
			}
		}
		if (isNaN(intMonth)) {
			err = 3;
			return false;
		}
	}
	intYear = parseInt(strYear, 10);
	if (isNaN(intYear)) {
		err = 4;
		return false;
	}
	if (intMonth > 12 || intMonth < 1) {
		err = 5;
		return false;
	}
	if ((intMonth == 1 || intMonth == 3 || intMonth == 5 || intMonth == 7
			|| intMonth == 8 || intMonth == 10 || intMonth == 12)
			&& (intday > 31 || intday < 1)) {
		err = 6;
		return false;
	}
	if ((intMonth == 4 || intMonth == 6 || intMonth == 9 || intMonth == 11)
			&& (intday > 30 || intday < 1)) {
		err = 7;
		return false;
	}
	if (intMonth == 2) {
		if (intday < 1) {
			err = 8;
			return false;
		}
		if (LeapYear(intYear) == true) {
			;
			if (intday > 29) {
				err = 9;
				return false;
			}
		} else {
			if (intday > 28) {
				err = 10;
				return false;
			}
		}
	}
	return true;
}
function LeapYear(intYear) {
	if (intYear % 100 == 0) {
		if (intYear % 400 == 0) {
			return true;
		}
	} else {
		if ((intYear % 4) == 0) {
			return true;
		}
	}
	return false;
}

function moduloActivo(nameModule, target) {
	window.parent.document.getElementById(target).src = nameModule;
}
function limitar(id) {
	//if (document.all) {
		document.getElementById(id).className = 'nueva';
	//}
}
function isIE7() {
	return (navigator.appVersion.indexOf("MSIE 7") > 0 ? true : false);
}

function getWindowXY() {
	var myWidth = 0, myHeight = 0;
	if (typeof (window.parent.innerWidth) == 'number') {
		// Non-IE
		myWidth = window.innerWidth;
		myHeight = window.innerHeight;
	} else if (document.documentElement
			&& (document.documentElement.clientWidth || document.documentElement.clientHeight)) {
		// IE 6+ in 'standards compliant mode'
		myWidth = document.documentElement.clientWidth;
		myHeight = document.documentElement.clientHeight;
	} else if (document.body
			&& (document.body.clientWidth || document.body.clientHeight)) {
		// IE 4 compatible
		myWidth = document.body.clientWidth;
		myHeight = document.body.clientHeight;
	}
	// window.alert( 'Width = ' + myWidth );
	// window.alert( 'Height = ' + myHeight );
	return [ myWidth, myHeight ];
}

function cero(num, length) {
	num = String(num);
	length = parseInt(length) || 2;
	while (num.length < length)
		num = "0" + num;
	return num;
};

function getDate() {
	var d = new Date();
	var cad = Rigth(d.getDate(), 2) + "/" + Rigth("00" + d.getMonth(), 2) + "/"
			+ getFullYear();
}

function Left(str, n) {
	if (n <= 0)
		return "";
	else if (n > String(str).length)
		return str;
	else
		return String(str).substring(0, n);
}
function Right(str, n) {
	if (n <= 0)
		return "";
	else if (n > String(str).length)
		return str;
	else {
		var iLen = String(str).length;
		return String(str).substring(iLen, iLen - n);
	}
}

function addEvent(elm, evType, fn, useCapture) {
	if (elm.addEventListener) {
		elm.addEventListener(evType, fn, useCapture);
		return true;
	} else if (elm.attachEvent) {
		var r = elm.attachEvent('on' + evType, fn);
		return r;
	} else {
		elm['on' + evType] = fn;
	}
}

function setCookie(nombre, valor, tiempo) {
	if (!tiempo) {
		var d = new Date();
		d.setYear(d.getYear() + 10);
		document.cookie = nombre + "=" + valor + "; expires=" + d.toGMTString();
	} else {
		document.cookie = nombre + "=" + valor + "; expires=" + tiempo;
	}
}

function getCookie(nombre) {
	var a = "";
	if (document.cookie.indexOf(nombre) != -1) {
		a = document.cookie.substring(document.cookie.indexOf(nombre + '=')
				+ nombre.length + 1, document.cookie.length);
		if (a.indexOf(';') != -1)
			a = a.substring(0, a.indexOf(';'));
	}
	return a;
}

function allowCharacter(obj, messageIfNotAllowed) {
	var before = obj.value;
	var after = obj.value.replace(/[^a-zA-Z0-9��\-_\r\n; ]/g, '');

	if (before != after) {
		alert(messageIfNotAllowed);
		obj.value = after;
	}
}

/**
 * 
 * @param hiddenFieldId
 * @param normasValue
 */
function setNormas(hiddenFieldId, normasValue) {
	var hiddenField = document.getElementById(hiddenFieldId);
	if (hiddenField != null) {
		// tengo el campo oculto para almacenar las normas que vienen del popup
		// respectivo
		hiddenField.value = normasValue;
		// alert(normasValue);
		try {
			setNormasAfterChange(); 
		} catch(e) {}
	}
} 
function setNormaLabel(hiddenFieldId, normaValue, normaLabel) {
	console.log("setNormas()");
	var hiddenField = document.getElementById(hiddenFieldId);
	if (hiddenField != null) {
		hiddenField.value = normaValue;
	}
	console.log(hiddenFieldId+'Label');
	var hiddenFieldLabel = document.getElementById(hiddenFieldId+'Label');
	if (hiddenFieldLabel != null) {
		console.log('seteando label '+normaLabel);
		hiddenFieldLabel.value = normaLabel;
	}
} 

function showNormsPopUp(hiddenNormsFieldId) {
	//var w = window.innerWidth - 250;
	//var h = window.innerHeight - 10;
	var w = 1000;
	var h = 600;
	
	abrirVentanaSinResize('showNormIsoPopUp.do?selected='
			+ document.getElementById(hiddenNormsFieldId).value + "&field="
			+ hiddenNormsFieldId, w, h, null);
}

function showNormsPopUpFromDocument(hiddenNormsFieldId) {
	var w = 1000;
	var h = 600;
	abrirVentanaSinResize('loadNormsFromDocument.do?selected='
			+ document.getElementById(hiddenNormsFieldId).value + "&field="
			+ hiddenNormsFieldId, w, h, null);
}
function showNormsPopUpFromMasterList(hiddenNormsFieldId) {
	var w = 1000;
	var h = 600;
	abrirVentanaSinResize('loadNormsFromMasterList.do?selected='
			+ document.getElementById(hiddenNormsFieldId).value + "&field="
			+ hiddenNormsFieldId, w, h, null);
}

function formatearNumero(nStr) {
    nStr += '';
    x = nStr.split('.');
    x1 = x[0];
    x2 = x.length > 1 ? ',' + x[1] : '';
    var rgx = /(\d+)(\d{3})/;
    while (rgx.test(x1)) {
            x1 = x1.replace(rgx, '$1' + '.' + '$2');
    }
    return x1 + x2;
}

function showStatus(ind) {
    if (ind==0){
        self.status = '';
    }
}