// precarga de imágenes
if(document.images){
var mas = new Image(9,9);
mas.src = "arbol_src/plus.gif";
var menos = new Image(9,9);
menos.src = "arbol_src/plus.gif";
var dir = new Image(12,11);
dir.src = "arbol_src/DIR.gif";
var izq = new Image(14,14);
izq.src = "arbol_src/iz.gif";
var der = new Image(14,14);
der.src = "arbol_src/de.gif";
var link = new Image(11,10);
izq.src = "arbol_src/link.gif";
}
//datos
var arbol = {
"rama1":{
"estado":false,
"titulo":"Artículos",
"enlaces":[
["Norma U.N.E.-E.N.-I.S.O. 9001","iso_9001.php"],
["JScript-CGI","jscript_cgi.php"],
["JavaScript Shell","jsshell.php"],
["Almacenando datos en JavaScript","almacenando_datos.php"],
["Ordenando tablas HTML usando DHTML","ordenando_tablas.php"],
["Pasar variables entre páginas HTML","pasar_variables.php"]
]
},
"rama2":{
"estado":false,
"titulo":"Programas",
"enlaces":[
["Zazou Mini Web Server Control","../software/zmws_control.php"],
["Sistema contador de descargas, sin MYSQL","../software/descargas.php"],
["Programa para realizar test de oposiciones","../software/test_oposiciones.php"]
]
},
"rama3":{
"estado":false,
"titulo":"Personal de JALL",
"enlaces":[
["Home","../index.html"],
["Mi currículum","../secciones.php?xml=curriculum.xml"],
["Mis servicios","../secciones.php?xml=servicios.xml"]
]
}
}
//funciones
function globalAction(tipo){
switch(tipo){
case("exp"):
for (obj in arbol){
arbol[obj].estado = true;
}
break;
case("cont"):
for (obj in arbol){
arbol[obj].estado = false;
}
break;
}
createArbol();
}

function createArbol(){
var textoArbol = "";
for (obj in arbol){

if (arbol[obj].estado == false)
{
textoArbol += "<a href='javascript:cambiaEstadoNodo(\"" + obj + "\")' title='Expande rama'>";
textoArbol += "<img src='menu-images/menu_tee_plus.gif' border='0'></a>&nbsp;-&nbsp";
textoArbol += "<img src='menu-images/menu_folder_closed.gif' border='0'>&nbsp;<b>" + arbol[obj].titulo + "</b><br>";
}
else
{
textoArbol += "<a href='javascript:cambiaEstadoNodo(\"" + obj + "\")' title='Contrae rama'>";
textoArbol += "<img src='menu-images/menu_corner_minus.gif' border='0'></a>&nbsp;-&nbsp;";
textoArbol += "<img src='menu-images/menu_folder_closed.gif' border='0'>&nbsp;<b>" + arbol[obj].titulo + "</b><br>";
for (i = 0 ; i < arbol[obj].enlaces.length ; i++){
textoArbol += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
textoArbol += "<a href='" + arbol[obj].enlaces[i] [1] + "' title='Visitar página' style='font-weight : normal;'>";
textoArbol += "<img src='menu-images/addDocument.jpg' width='15' height='15' border='0'>&nbsp;";
textoArbol += arbol[obj].enlaces[i][0] + "</a><br>";
}
}
}
document.getElementById("arbol").innerHTML = textoArbol;
}

function cambiaEstadoNodo(nodo){
(arbol[nodo].estado == true)? arbol[nodo].estado = false:arbol[nodo].estado = true;
createArbol();
}

/*
// CODIGO QUE VA EN LA PAGINA DONDE QUEREMOS EL ARBOL
<div align="center">
<a href='javascript:globalAction("exp")'>
<img src='arbol_src/iz.gif' width='14' height='14' alt="Expande todo" border="0">
<img src='arbol_src/de.gif' width='14' height='14' border="0" alt="Expande todo"></a>
&nbsp;&nbsp; &nbsp;&nbsp;
<a href='javascript:globalAction("cont")'>
<img src='arbol_src/de.gif' width='14' height='14' border="0" alt="Contrae todo">
<img src='arbol_src/iz.gif' width='14' height='14' alt="Contrae todo" border="0"></a>
</div>
<br>
<div align="center">
<div id="arbol" style="width : 400px; height:200px; border:1px solid black;overflow : auto;text-align:left;padding-left:30px; padding-top:10px;background-color : #E6E6FA;">
</div>
</div>
<script>createArbol()</script> 					
*/