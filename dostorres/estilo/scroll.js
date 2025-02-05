
scrollList=new Array();

 

function registraScroll(idAbajo, idArriba, div, velAbajo, velArriba)
{
    if(scrollList[idAbajo]==null) scrollList[idAbajo]=new Array();
    if(scrollList[idArriba]==null) scrollList[idArriba]=new Array();
    scrollList[idAbajo].push(new Array(div, velAbajo));
    scrollList[idArriba].push(new Array(div, velArriba));
}


window.onload=inicializar;
 
function getEl(elementId)
{
    return document.getElementById(elementId);
}
 
function inicializar()
{
    for(key in scrollList)
    {
        var elemento=getEl(key);
        elemento.onmouseover=iniciaScroll;
        elemento.onmouseout=detieneScroll;
    }
}
 
function iniciaScroll()
{
    scrollDivs=new Array();
    velDivs=new Array();
    for(key in scrollList[this.id])
    {
        scrollDivs.push(getEl(scrollList[this.id][key][0]));
        velDivs.push(scrollList[this.id][key][1]);
    }
    identificador=setInterval('scrollNow()', 50);
}
 
function detieneScroll()
{
    clearInterval(identificador);
}
 
function scrollNow()
{
    for(key in scrollDivs)
    {
    var desplazamientoActual=scrollDivs[key].scrollTop;
    var nuevoDesplazamiento=desplazamientoActual+velDivs[key];
    scrollDivs[key].scrollTop=nuevoDesplazamiento;
    }
}

