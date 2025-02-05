
function reloadBandeja(){
	alert(window.frames['bandeja']);
	
	if(window.frames['bandeja'] != null){
		window.frames['bandeja'].document.location.href = window.frames['bandeja'].document.location.href;
	}
}

/**
 * 
 * @param etiqueta
 */
function ver(etiqueta) {
	var extraParameters = "";
	
	//colocamos los datos respectivos en el formulario del reporte
	var reporteForm = document.getElementById("getSacopReport");
	reporteForm.bandejaReporte.value = etiqueta.toLowerCase();
	
	//abrimos el ancabezado
	window.open("principalSacop1"+etiqueta+"Tit.jsp","bandejaTit");
	
	if(etiqueta.toLowerCase() == "buscar"){
		//se desea buscar SACOPs, entonces colocamos en el request
		//los parametros respectivos
		extraParameters += "&buscar=buscar";
		extraParameters += "&idSacop=" + document.getElementById("idSacop").value;
		extraParameters += "&emisorSacop=" + document.getElementById("emisorSacop").value;
		extraParameters += "&responsableSacop=" + document.getElementById("responsableSacop").value;
		extraParameters += "&areaResponsableSacop=" + document.getElementById("areaResponsableSacop").value;
		extraParameters += "&efectivaSacop=" + document.getElementById("efectivaSacop").value;
		extraParameters += "&estadoSacop=" + document.getElementById("estadoSacop").value;
		extraParameters += "&fechaEmisionSacop=" + document.getElementById("fechaEmisionSacop").value;
		
		reporteForm.idSacopReporte.value = document.getElementById("idSacop").value;
		reporteForm.emisorSacopReporte.value = document.getElementById("emisorSacop").value;
		reporteForm.responsableSacopReporte.value = document.getElementById("responsableSacop").value;
		reporteForm.areaResponsableSacopReporte.value = document.getElementById("areaResponsableSacop").value;
		reporteForm.efectivaSacopReporte.value = document.getElementById("efectivaSacop").value;
		reporteForm.estadoSacopReporte.value = document.getElementById("estadoSacop").value;
		reporteForm.fechaEmisionSacopReporte.value = document.getElementById("fechaEmisionSacop").value;
		
	}
	
	//buscamos el detalle
	window.open("loadSACOPMain.do?getList=true&goTo=" + etiqueta.toLowerCase() + extraParameters, "bandeja");
}

/**
 * Dependiendo de la etiqueta, se abrira el url
 * en contenedores aparte o en la misma pagina.
 * 
 * @param etiqueta
 * @param url
 */
function ir(etiqueta, url, idDocRelated){
	if(etiqueta == 'CrearSacop.do') {
		window.open("marcoSacop2.jsp?idDocRelated="+idDocRelated,"frmConfig");
		administrar();
		return;
	}
	
	if(etiqueta == 'loadBorrador'){
		window.open("marcoSacop4.jsp?url="+url.replace(/&/g,'_'),"frmConfig");
		administrar();
		return;
	}
	
	if(etiqueta == 'loadSACOPMain.do') {
		window.open("marcoSacop3.jsp?url="+url.replace(/&/g,'_'),"frmConfig");
		administrar();
		return;
	}
	
	window.open("principalSacop1Tit.jsp","bandejaTit");
	window.open(etiqueta,"bandeja");
}

/**
 * 
 */
function administrarCancelar(){
    alert("administrarCancelar");
	document.getElementById("caja").style.display="none";
	document.getElementById("caja2").style.display="none";
	document.getElementById("caja3").style.display="none";
}

/**
 * 
 */
function administrar(){

	var medidas = getWindowXY();
	var ancho=medidas[0]-150;
	var largo=medidas[1]-70;
	var reduccionDeCaja = 200;
	var margen=10;
	
	var frm = document.getElementById("frmConfig");
	frm.style.width='';
	frm.style.height='';

	var obj = document.getElementById("caja");
	obj.style.display="";
	obj.style.width=medidas[0]+"px";
	obj.style.height=medidas[1]+"px";
	
	var obj2 = document.getElementById("caja2");
	obj2.style.display="";
	obj2.style.left=((medidas[0]-(ancho - reduccionDeCaja))/2)+"px";
	obj2.style.top=((medidas[1] - largo) / 2)+"px";
	obj2.style.width = (ancho - reduccionDeCaja) + "px";
	
	obj2.style.height = (largo - (margen*2)) + "px";
	obj2.className="bodyInternas";
	//obj2.style.backgroundColor="gray";
	//obj2.style.backgroundImage.src=url("img/fondoGris2.jpg");
	
	var marco = document.getElementById("caja3");
	
	marco.style.display="";
	marco.style.left=((medidas[0]-(ancho - reduccionDeCaja))/2)-margen+"px";
	//marco.style.top=((medidas[1]-largo)/2)-margen+"px";
	marco.style.top="10px";
	marco.style.width=(ancho - reduccionDeCaja)+(margen*2)+"px";
	marco.style.height=largo+(margen*2)+"px";
	
	var obj3 = document.getElementById("frmConfig");
	obj3.style.left="0px";
	obj3.style.top="0px";
	//obj3.width=obj2.style.width;
	obj3.width="100%";
	obj3.height="100%";
}

/**
 * 
 * @param listDer
 * @param listIzq
 */
function mover(listDer, listIzq) {

    console.log(listDer);
    console.log(listIzq);
	
	var items = listDer.length;
	var selecteds = contarItemsSelecteds(listDer);
	
	console.log("*******************************");
    console.log(items);
	console.log(selecteds);
	
	if (selecteds > 0){
		var	newItem;
		for (var row = 0; row < items; row++){
			var valor = listDer[row];
			if (valor.selected) {
                newItem = createElement(listDer[row].value,listDer[row].text);
				listIzq.options[listIzq.length] = newItem;
			}
		}
		
		removerDeLista(listDer,items);
	}
}

/**
 * 
 * @param listDer
 * @param listIzq
  */
function moverMultiple(listDer, listIzq,origenFinal) {
    console.log("****************moverMultiple****************");
	var items = listDer.length;
	var items2 = origenFinal.length;
	var selecteds = listIzq.value;
	var array = selecteds.split(",");
	var items1= array.length;
	
 if (items1 > 0){
	var	newItem;
	var valorOrigen;
		
	for (var row0=0;row0<items1;row0++) {
			
		for (var row = 0; row < items; row++){
			var valor = listDer[row].value;
			 if (row < items2){
			   valorOrigen=origenFinal[row].value
			}
			if (valor==array[row0]) {
			 
			    if (valorOrigen!= valor){
			      	newItem = createElement(listDer[row].value,listDer[row].text);
                 	origenFinal.options[origenFinal.length] = newItem;
                 	removerDeLista(listDer,items);
			    }
			    
			}
		}
		
	}
		
		removerDeLista(listDer,items);
 }
	
}


/**
 * 
 * @param lista
 * @returns {Number}
 */
function contarItemsSelecteds(lista){
    if (lista){
        var items = lista.length;
        var cont = 0;
        var row = 0;
        for (row = 0; row < items; row++){
            var valor = lista[row];
            if (valor.selected){
                cont++;
            }
        }
        
        return cont;
    }
    
    return 0;
}

/**
 * 
 * @param lista
 * @returns
 */
function contSelectInList(lista) {
    var items = lista.length;
    if (items==0) {
        return 0;
    }
    var row = 0;
    for (row = 0; row < items ; row++) {
        var valor = lista[row];
        valor.selected = true;
    }
    return items;
}

/**
 * 
 * @param lista
 * @param items
 */
function removerDeLista(lista,items) {
	if (lista){
        var row = 0;
		for (row = items - 1; row >= 0; row--) {
			var valor = lista[row];
			if (valor.selected){
				lista.remove(row);
			}
		}
	}
}

/**
 * 
 * @param lista
 * @param items
 */
function obtenerIdsSeleccionados(listaDeNormas) {
	var idsSeleccionados = "";
	
	if (listaDeNormas != null){
		for (var i = 0; i < listaDeNormas.length; i++) {
		    //  Aca haces referencia al "option" actual
		   if(idsSeleccionados != ""){
			   idsSeleccionados += ",";
		   }
		   
		   idsSeleccionados += listaDeNormas[i].value;
		}
	}
	
	if(idsSeleccionados == ""){
		idsSeleccionados = "0";
	}
	return idsSeleccionados;
}

/**
 * 
 * @param normsiso
 */
function vertexto(normsiso){
	var items = normsiso.length;
    for (var row = 0; row < items; row++){
    	var valor = normsiso[row];
        if (valor.selected){
        	alert(normsiso[row].text);
            row +=items+1;
        }
    }
}

/**
 * 
 * @param value
 * @param texto
 * @returns {___anonymous5612_5618}
 */
function createElement(value,texto){
	var newItem = document.createElement("OPTION");
	newItem.value = value;
	newItem.text = texto;
	return newItem;
}