<%@ page import="com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.persistent.managers.HandlerStruct,
                 java.util.Collection,
                 java.util.Hashtable,
                 com.desige.webDocuments.utils.beans.Users,
                 java.util.ResourceBundle"%>
<!-- code-tree.jsp --> 
                 
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML><HEAD><title>Estructura</title>
<META http-equiv=Content-Type content="text/html; charset=windows-1252">
<META http-equiv=Cache-Control content="no-cache, must-revalidate">
<META http-equiv=Pragma content=no-cache>
<META Http-Equiv="Expires" Content="0">
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    String nodeRoot = session.getAttribute("nodeRoot")!=null?(String)session.getAttribute("nodeRoot"):"";
    String idNodeRoot = session.getAttribute("idNodeRoot")!=null?(String)session.getAttribute("idNodeRoot"):"";
    if (!ToolsHTML.checkValue(nodeRoot)) {
        nodeRoot = " ";
    }
    if (!ToolsHTML.checkValue(idNodeRoot)) {
        idNodeRoot = " ";
    }
    Hashtable tree = session.getAttribute("tree")!=null?(Hashtable)session.getAttribute("tree"):null;
    String ok = (String)ToolsHTML.getAttributeSession(session,"usuario",false);
    String info = (String) ToolsHTML.getAttribute(request,"info",true);
    Users usuario = (Users)session.getAttribute("user");
    StringBuffer onLoad = new StringBuffer("");

    if (ToolsHTML.checkValue(ok)) {
        onLoad.append(" onLoad=\"self.status='").append(rb.getString("pagin.welcome")).append(" ");
        onLoad.append(usuario.getNamePerson()).append("'");
    } else {
        String link = rb.getString("href.logout");
        response.sendRedirect(link+"?target=parent");
    }

    if (ToolsHTML.checkValue(info)) {
        onLoad.append(";alert('").append(info).append("')");
    }

    if (onLoad.length()>0) {
        onLoad.append(";MTMStartMenu();\"");
    }
%>
<script type="text/javascript">
var http=null;
function detenerError() {
    return true;
}
window.onerror=detenerError
function MTMenuItem(text, url, target, icon,id,indice) {
	MTMenuItem(text, url, target, icon, id, 0, 0,indice);
}

function MTMenuItem(text, url, target, icon, id, mayorVer, minorVer, indice) {
    this.text = text;
    this.url = url ? url : "";
    this.target =  target ? target : "";
    this.icon = icon ? icon : "";
    idObj = id?id:"";
    this.number = MTMSubNumber++;
    this.name = idObj;
    this.submenu     = null;
    this.expanded    = false;
    this.MTMakeSubmenu = MTMakeSubmenu;
    this.indice=indice;
}
function MTMakeSubmenu(menu) {
    this.submenu = menu;
}
function MTMenu() {
    this.items   = new Array();
    this.MTMAddItem = MTMAddItem;
    this.name = "";
}
function MTMAddItem(item) {
    this.items[this.items.length] = item;
}
function IconList() {
    this.items = new Array();
    this.addIcon = addIcon;
}
function addIcon(item) {
    this.items[this.items.length] = item;
}
function MTMIcon(iconfile, match, type) {
    this.file = iconfile;
    this.match = match;
    this.type = type;
}
var MTMLevel;
var MTMBar = new Array();
var MTMIndices = new Array();
var MTMBrowser = null;
var MTMNN3 = false;
var MTMNN4 = false;
var MTMIE4 = false;
var MTMUseStyle = true;
var itemActual = null;
if(navigator.appName == "Netscape") {
    if(parseInt(navigator.appVersion) == 3 && (navigator.userAgent.indexOf("Opera") == -1)) {
        MTMBrowser = true;
        MTMNN3 = true;
        MTMUseStyle = false;
    } else if(parseInt(navigator.appVersion) >= 4) {
        MTMBrowser = true;
        MTMNN4 = true;
    }
    } else if (navigator.appName == "Microsoft Internet Explorer" && parseInt(navigator.appVersion) >= 4) {
      MTMBrowser = true;
      MTMIE4 = true;
}
var MTMClickedItem = false;
var MTMExpansion = false;
var MTMSubNumber = 1;
var MTMTrackedItem = false;
var MTMTrack = false;
var MTMPreHREF = "";
if(MTMIE4 || MTMNN3) {
  MTMPreHREF += document.location.href.substring(0, document.location.href.lastIndexOf("/") +1);
}
var MTMFirstRun = true;
var MTMCurrentTime = 0; // for checking timeout.
var MTMUpdating = false;
var MTMSubName = "";
var MTMWinSize, MTMyval;
var MTMOutputString = "";
if(MTMBrowser) {
  var MTMFrameNames = new Array();
  for(var i = 0; i < parent.frames.length; i++) {
    MTMFrameNames[i] = parent.frames[i].name;
  }
}
//document.getElementById('menuTop').src 
function myVoid() {
	if(parent.frames['text'] != null){
        var trContent = parent.frames['text'].document.getElementById("subNodesTable");
        var trContentWhenLoad = parent.frames['text'].document.getElementById("subNodesTableWhenLoad");
        if(trContent != null){
            trContent.style.display = "none";
            
            if(trContentWhenLoad != null){
                trContentWhenLoad.style.display = "";
            }
        }
    }
    var isCopyPresent = false;
    var objFrameDocument = ( MTMIE4 ? parent.frames['text'].document : parent.document.getElementById('text').contentDocument );
    if (!parent.frames['text'].active){
        if (objFrameDocument.Selection) {
            if (objFrameDocument.Selection.movDocument) {
                isCopyPresent = objFrameDocument.Selection.movDocument.value == 1;
            } else if (objFrameDocument.Selection.isCopy) {
                isCopyPresent = objFrameDocument.Selection.isCopy.value == 1;
            }
        }
    }

    if (isCopyPresent) {
        MTMSubAction(itemActual,false);
    }
}

function MTMSubAction(SubItem, ReturnValue) {
    var isCopyPresent = false;
    expandir = (SubItem.expanded) ? false : true;
    var objFrameDocument = ( MTMIE4 ? parent.frames['text'].document : parent.document.getElementById('text').contentDocument );
    if (!parent.frames['text'].active) {
        if (objFrameDocument.Selection) {
            if (objFrameDocument.Selection.movDocument) {
                isCopyPresent = objFrameDocument.Selection.movDocument.value == 1;
                itemActual = SubItem;
            } else if (objFrameDocument.Selection.isCopy) {
                isCopyPresent = objFrameDocument.Selection.isCopy.value == 1;
                itemActual = SubItem;
            } else {
                itemActual = null;
            }
        }
    }
    if (parent.frames['text'].active||isCopyPresent) {
        SubItem.expanded = (SubItem.expanded) ? false : true;
        if(SubItem.expanded) {
            MTMExpansion = true;
        }
        if(expandir) {
            MTMExpansion = true;
        }
        MTMClickedItem = SubItem.number;
        if(MTMTrackedItem && MTMTrackedItem != SubItem.number) {
            MTMTrackedItem = false;
        }
        if(!ReturnValue||MTMExpansion) {
            setTimeout("MTMDisplayMenu()", 10);
        }

        if(SubItem.submenu==null) {
	        // recargaremos el nodo desde aqui
	    	if(navigator.appName == "Microsoft Internet Explorer") {
	    	  http = new ActiveXObject("Microsoft.XMLHTTP");
	    	} else {
	    	  http = new XMLHttpRequest();
	    	}
	        //alert("REVISAR LA ESTRUCTURA PRINCIPAL QUE CARGA ERRONEAMENTE 'CENTRAL PRINCIPALMENTE'");
	        //alert("loadSubNode.do?idNode="+SubItem.name+"&indice="+SubItem.indice);
	    	http.open("POST", "loadSubNode.do?idNode="+SubItem.name+"&indice="+SubItem.indice);
	    	http.onreadystatechange=function() {
	    	  if(http.readyState == 4) {
	    		  	var arr = http.responseText.split(";");
	    		  	if(arr.length>3) {
		    			for(var x=0; x<arr.length-1; x++) {
		    				//console.log(arr[x]);
		    				eval(arr[x].valueOf());
		    			}
	    			}
	    			refreshTree(SubItem);
	    			parent.frames['text'].setValueSelected(SubItem.name);
	    	  }
	    	}
	    	http.send(null);
        } else {
        	if(SubItem.expanded==true) {
        		parent.frames['text'].setValueSelected(SubItem.name);
        	}
        }        
        
    } else {
        alert("Debe cancelar la operación previamente seleccionada");
    }
    return ReturnValue;
}
function MTMSubActionII(SubItem, ReturnValue) {
    expandir = (SubItem.expanded) ? false : true;
    if (expandir) {
        if (parent.frames['text'].active) {
            SubItem.expanded = expandir;
            if(expandir) {
                MTMExpansion = true;
            }
            MTMClickedItem = SubItem.number;
            if(MTMTrackedItem && MTMTrackedItem != SubItem.number) {
                MTMTrackedItem = false;
            }
            if(!ReturnValue) {
                setTimeout("MTMDisplayMenu()", 10);
            }
            if (SubItem.expanded) {
                parent.frames['text'].setValueSelected(SubItem.name,expandir);
            } else {
                parent.frames['text'].setValueSelected(SubItem.url,expandir);
            }
        } else {
            parent.frames['text'].setValueSelected(SubItem.url,expandir);
        }
<%--    } else {--%>
<%--        alert("Debe cancelar la operación previamente seleccionada");--%> 
    }
    return ReturnValue;
}
function refreshTree(SubItem){
    if (SubItem!=null){
        expandir = (SubItem.expanded) ? false : true;
        SubItem.expanded = true;
        if(expandir) {
            MTMExpansion = true;
        }
        MTMClickedItem = SubItem.number;
        if(MTMTrackedItem && MTMTrackedItem != SubItem.number) {
            MTMTrackedItem = false;
        }
        setTimeout("MTMDisplayMenu()", 10);
    }
}
function refreshText(newText,SubItem) {
    SubItem.text = newText;
    setTimeout("MTMDisplayMenu()", 10);
}
function MTMStartMenu() {
  if(MTMFirstRun) {
    MTMCurrentTime++;
    if(MTMCurrentTime == MTMTimeOut) { // call MTMDisplayMenu
      setTimeout("MTMDisplayMenu()",10);
    } else {
      setTimeout("MTMStartMenu()",100);
    }
  }
}
function MTMDisplayMenu() {
  if(MTMBrowser && !MTMUpdating) {
    MTMUpdating = true;
    MTMFirstRun = false;

    if(MTMTrack) {
      MTMTrackedItem = MTMTrackExpand(menu);
      if(MTMExpansion && MTMSubsAutoClose) {
        MTMCloseSubs(menu);
      }
    } else if(MTMExpansion && MTMSubsAutoClose) {
      MTMCloseSubs(menu);
    }

    MTMLevel = 0;
    //MTMDoc = parent.frames[MTMenuFrame].document
    MTMDoc = ( MTMIE4 ? parent.frames[MTMenuFrame].document : parent.document.getElementById(MTMenuFrame).contentDocument );
    MTMDoc.open("text/html", "replace");
    MTMOutputString = '<html><head>';
    if(MTMLinkedSS) {
      MTMOutputString += '<link rel="stylesheet" type="text/css" href="' + MTMPreHREF + MTMSSHREF + '">';
    } else if(MTMUseStyle) {
      MTMOutputString += '<style type="text/css">body {color:' + MTMTextColor + ';background:';
      MTMOutputString += (MTMBackground == "") ? MTMBGColor : MTMakeBackImage(MTMBackground);
      MTMOutputString += ';} #root {color:' + MTMRootColor + ';background:' + ((MTMBackground == "") ? MTMBGColor : 'transparent') + ';font-family:' + MTMRootFont + ';font-size:' + MTMRootCSSize + ';} ';
      MTMOutputString += 'a {font-family:' + MTMenuFont + ';font-size:' + MTMenuCSSize + ';text-decoration:none;color:' + MTMLinkColor + ';background:' + MTMakeBackground() + ';} ';
      MTMOutputString += MTMakeA('pseudo', 'hover', MTMAhoverColor);
      MTMOutputString += MTMakeA('class', 'tracked', MTMTrackColor);
      MTMOutputString += MTMakeA('class', 'subexpanded', MTMSubExpandColor);
      MTMOutputString += MTMakeA('class', 'subclosed', MTMSubClosedColor) + '</style>';
    }

    MTMOutputString += '</head><body ';
    if(MTMBackground != "") {
      MTMOutputString += 'style="background-repeat: no-repeat;background-position: left top;height:97%" background="' + MTMPreHREF + MTMenuImageDirectory + MTMBackground + '" ';
      //MTMOutputString += ' background="' + MTMPreHREF + MTMenuImageDirectory + MTMBackground + '" ';
    }
    MTMOutputString += 'bgcolor="' + MTMBGColor + '" text="' + MTMTextColor + '" link="' + MTMLinkColor + '" vlink="' + MTMLinkColor + '" alink="' + MTMLinkColor + '">';
    MTMOutputString += '<table border="0" cellpadding="0" cellspacing="0" width="' + MTMTableWidth + '">';
    MTMOutputString += '<tr valign="top">\n     <td nowrap>';
    if(MTMUseStyle) {
      MTMOutputString += '<span id="root">&nbsp;' + MTMenuText + '</span>';
    } else {
      MTMOutputString += '<font size="' + MTMRootFontSize + '" face="' + MTMRootFont + '" color="' + MTMRootColor + '">' + MTMenuText + '</font>';
    }
    MTMDoc.writeln(MTMOutputString + '</td></tr>');

    MTMListItems(menu);
    MTMDoc.writeln('</table></body></html>');
    MTMDoc.close();

    if((MTMClickedItem || MTMTrackedItem) && (MTMNN4 || MTMIE4) && !MTMFirstRun) {
      MTMItemName = "sub" + (MTMClickedItem ? MTMClickedItem : MTMTrackedItem);
      if(document.layers && parent.frames[MTMenuFrame].scrollbars) {
        MTMDoc = ( MTMIE4 ? parent.frames[MTMenuFrame].document : parent.document.getElementById(MTMenuFrame).contentDocument );
        //MTMyval = parent.frames[MTMenuFrame].document.anchors[MTMItemName].y;
        MTMyval = MTMDoc.anchors[MTMItemName].y; 
        MTMWinSize = parent.frames[MTMenuFrame].innerHeight;
      } else {
        //MTMyval = MTMGetPos(parent.frames[MTMenuFrame].document.getElementById(MTMItemName));//[MTMItemName]);
        
        //alert("MTMItemName="+MTMItemName);
        // INFO: jairo
        try{
        	MTMyval = MTMGetPos(MTMDoc.getElementById(MTMItemName));
        }catch(e) {
        	// INFO: este error es lanzado en el navegador firefox
        }
        
        //MTMWinSize = parent.frames[MTMenuFrame].document.body.offsetHeight;
        MTMWinSize = MTMDoc.body.offsetHeight;
      }
      if(MTMyval > (MTMWinSize - 60)) {
        parent.frames[MTMenuFrame].scrollBy(0, parseInt(MTMyval - (MTMWinSize * 1/3)));
      }
    }

    MTMClickedItem = false;
    MTMExpansion = false;
    MTMTrack = false;
  }
    MTMUpdating = false;
}
function MTMListItems(menu) {
  var i, isLast;
  // INFO: aqui se van listando los items del menu
  for (i = 0; i < menu.items.length; i++) {
    MTMIndices[MTMLevel] = i;
    isLast = (i == menu.items.length -1);
    MTMDisplayItem(menu.items[i], isLast);

    if (menu.items[i].submenu && menu.items[i].expanded) {
      MTMBar[MTMLevel] = (isLast) ? false : true;
      MTMLevel++;
      MTMListItems(menu.items[i].submenu);
      MTMLevel--;
    } else {
      MTMBar[MTMLevel] = false;
    }
  }
}
function MTMDisplayItem(item, last) {
    var i, img, more;
    if(item.submenu) {
        var MTMouseOverText;
        var MTMClickCmd;
        var MTMDblClickCmd = false;
        var MTMfrm = "parent.frames['code']";
        var MTMref = '.menu.items[' + MTMIndices[0] + ']';
        var MTMSelect;
        if(MTMLevel > 0) {
            for(i = 1; i <= MTMLevel; i++) {
                MTMref += ".submenu.items[" + MTMIndices[i] + "]";
            }
        }
// jairo
        if(!MTMEmulateWE && !item.expanded && (item.url != "")) {
            MTMClickCmd = "return " + MTMfrm + ".MTMSubAction(" + MTMfrm + MTMref + ",true);";
        } else {
            MTMClickCmd = "return " + MTMfrm + ".MTMSubAction(" + MTMfrm + MTMref + ",false);";
            MTMSelect = "return " + MTMfrm + ".MTMSubActionII(" + MTMfrm + MTMref + ",false);";
        }
        if(item.url == "") {
            MTMouseOverText = (item.text.indexOf("'") != -1) ? MTMEscapeQuotes(item.text) : item.text;
        } else {
            MTMouseOverText = "Expandir/Contraer";
        }
    }
    MTMOutputString = '<tr valign="top">\n     <td nowrap>';
    if(MTMLevel > 0) {
        for (i = 0; i < MTMLevel; i++) {
            MTMOutputString += (MTMBar[i]) ? MTMakeImage("menu_bar.gif") : MTMakeImage("menu_pixel.gif");
        }
    }

    more = false;
    if(item.submenu) {
        if(MTMSubsGetPlus || MTMEmulateWE) {
            more = true;
        } else {
            for (i = 0; i < item.submenu.items.length; i++) {
                if (item.submenu.items[i].submenu) {
                    more = true;
                }
            }
        }
    }

    if(!more) {
        img = (last) ? "menu_corner.gif" : "menu_tee.gif";
    } else {
        if(item.expanded) {
            img = (last) ? "menu_corner_minus.gif" : "menu_tee_minus.gif";
        } else {
            img = (last) ? "menu_corner_plus.gif" : "menu_tee_plus.gif";
        }
<%--        alert('MTMOutputString I: '+MTMOutputString);--%>
        MTMOutputString += MTMakeVoid(item, MTMClickCmd, MTMouseOverText);
<%--        alert('MTMOutputString II: '+MTMOutputString);--%>
    }
    MTMOutputString += MTMakeImage(img);

    if(item.submenu) {
        if(MTMEmulateWE && item.url != "") {
            MTMOutputString += '</a>/n' + MTMakeLink(item, false) + '>';
        }
<%--    img = (item.expanded) ? "menu_folder_open.gif" : "menu_folder_closed.gif";--%>
        img = (item.expanded) ? item.icon : item.icon;
        if(!more) {
<%--      if(item.url == "" || item.expanded) {--%>
            MTMOutputString += MTMakeVoid(item, MTMClickCmd, MTMouseOverText);
<%--      } else {--%>
<%--        MTMOutputString += MTMakeLink(item, true) + ' onclick="' + MTMClickCmd + '">';--%>
<%--      }--%>
        }
        // jairo: ini estado original
          //MTMOutputString += "<span id=\"img\"></span></a>\n"; //1
          // jairo: aqui empieza el link del texto del nodo
          //MTMOutputString += MTMakeVoid(item, MTMSelect, MTMouseOverText); //2
          //MTMOutputString += MTMakeImage(img);  //3
        // jairo: fin estado original

        // jairo: ini nuevo estado para que se pueda hacer link desde la imagen y el nombre
        MTMOutputString += MTMakeImage(img);  //3
        // jairo: fin nuevo estado
      } else {
        var MTMClickCmd;
        var MTMDblClickCmd = false;
        var MTMfrm = "parent.frames['code']";
        var MTMref = '.menu.items[' + MTMIndices[0] + ']';
        if(MTMLevel > 0) {
            for(i = 1; i <= MTMLevel; i++) {
                MTMref += ".submenu.items[" + MTMIndices[i] + "]";
            }
        }

        if(!MTMEmulateWE && !item.expanded && (item.url != "")) {
            MTMClickCmd = "return " + MTMfrm + ".MTMSubAction(" + MTMfrm + MTMref + ",true);";
        } else {
            MTMClickCmd = "return " + MTMfrm + ".MTMSubAction(" + MTMfrm + MTMref + ",false);";
        }
<%--    MTMOutputString += MTMakeLink(item, true) + '>';--%>
        MTMOutputString += MTMakeVoid(item, MTMClickCmd, MTMouseOverText);
        img = (item.icon != "") ? item.icon : MTMFetchIcon(item.url);
        MTMOutputString += MTMakeImage(img);
    }
<%--    if(item.submenu && (item.url != "") && (item.expanded && !MTMEmulateWE)) {--%>
<%--        MTMOutputString += '</a>' + MTMakeLink(item, false) + '>';--%>
<%--    }--%>

    if(MTMNN3 && !MTMLinkedSS) {
        var stringColor;
        if(item.submenu && (item.url == "") && (item.number == MTMClickedItem)) {
            stringColor = (item.expanded) ? MTMSubExpandColor : MTMSubClosedColor;
        } else if(MTMTrackedItem && MTMTrackedItem == item.number) {
            stringColor = MTMTrackColor;
        } else {
            stringColor = MTMLinkColor;
        }
        MTMOutputString += '<font color="' + stringColor + '" size="' + MTMenuFontSize + '" face="' + MTMenuFont + '">';
    }
    // jairo: aqui va el texto del link
    MTMOutputString += '&nbsp;' + item.text + ((MTMNN3 && !MTMLinkedSS) ? '</font>' : '') + '</a>\n' ;
    MTMDoc.writeln(MTMOutputString + '     </td>\n</tr>\n');
}

function MTMEscapeQuotes(myString) {
  var newString = "";
  var cur_pos = myString.indexOf("'");
  var prev_pos = 0;
  while (cur_pos != -1) {
    if(cur_pos == 0) {
      newString += "\\";
    } else if(myString.charAt(cur_pos-1) != "\\") {
      newString += myString.substring(prev_pos, cur_pos) + "\\";
    } else if(myString.charAt(cur_pos-1) == "\\") {
      newString += myString.substring(prev_pos, cur_pos);
    }
    prev_pos = cur_pos++;
    cur_pos = myString.indexOf("'", cur_pos);
  }
  return(newString + myString.substring(prev_pos, myString.length));
}

function MTMTrackExpand(thisMenu) {
  var i, targetPath;
  var foundNumber = false;
  for(i = 0; i < thisMenu.items.length; i++) {
    if(thisMenu.items[i].url != "" && MTMTrackTarget(thisMenu.items[i].target)) {
      targetPath = parent.frames[thisMenu.items[i].target].location.pathname;
      if(targetPath.lastIndexOf(thisMenu.items[i].url) != -1 && (targetPath.lastIndexOf(thisMenu.items[i].url) + thisMenu.items[i].url.length) == targetPath.length && !foundNumber) {
        return(thisMenu.items[i].number);
      }
    }
    if(thisMenu.items[i].submenu && !foundNumber) {
      if(thisMenu.items[i].expanded) {
        foundNumber = MTMTrackExpand(thisMenu.items[i].submenu);
        if(foundNumber) {
          return(foundNumber);
        }
      } else {
        thisMenu.items[i].expanded = true;
        foundNumber = MTMTrackExpand(thisMenu.items[i].submenu);
        if(foundNumber) {
          MTMClickedItem = thisMenu.items[i].number;
          MTMExpansion = true;
          return(foundNumber);
        } else {
          thisMenu.items[i].expanded = false;
        }
      }
    }
  }
return(foundNumber);
}

function MTMCloseSubs(thisMenu) {
  var i, j;
  var foundMatch = false;
  for(i = 0; i < thisMenu.items.length; i++) {
    if(thisMenu.items[i].submenu && thisMenu.items[i].expanded) {
      if(thisMenu.items[i].number == MTMClickedItem) {
        foundMatch = true;
        for(j = 0; j < thisMenu.items[i].submenu.items.length; j++) {
          if(thisMenu.items[i].submenu.items[j].submenu && thisMenu.items[i].submenu.items[j].expanded) {
            thisMenu.items[i].submenu.items[j].expanded = false;
          }
        }
      } else {
        if(foundMatch) {
          thisMenu.items[i].expanded = false;
        } else {
          foundMatch = MTMCloseSubs(thisMenu.items[i].submenu);
          if(!foundMatch) {
            thisMenu.items[i].expanded = false;
          }
        }
      }
    }
  }
return(foundMatch);
}

function MTMFetchIcon(testString) {
  var i;
  for(i = 0; i < MTMIconList.items.length; i++) {
    if((MTMIconList.items[i].type == 'any') && (testString.indexOf(MTMIconList.items[i].match) != -1)) {
      return(MTMIconList.items[i].file);
    } else if((MTMIconList.items[i].type == 'pre') && (testString.indexOf(MTMIconList.items[i].match) == 0)) {
      return(MTMIconList.items[i].file);
    } else if((MTMIconList.items[i].type == 'post') && (testString.indexOf(MTMIconList.items[i].match) != -1)) {
      if((testString.lastIndexOf(MTMIconList.items[i].match) + MTMIconList.items[i].match.length) == testString.length) {
        return(MTMIconList.items[i].file);
      }
    }
  }
return("menu_link_default.gif");
}

function MTMGetPos(myObj) {
	// INFO: esta funcion da error en firefox
  return(myObj.offsetTop + ((myObj.offsetParent) ? MTMGetPos(myObj.offsetParent) : 0));
}

function MTMCheckURL(myURL) {
  var tempString = "";
  if((myURL.indexOf("http://") == 0) || (myURL.indexOf("https://") == 0) || (myURL.indexOf("mailto:") == 0) || (myURL.indexOf("ftp://") == 0) || (myURL.indexOf("telnet:") == 0) || (myURL.indexOf("news:") == 0) || (myURL.indexOf("gopher:") == 0) || (myURL.indexOf("nntp:") == 0) || (myURL.indexOf("javascript:") == 0)) {
    tempString += myURL;
  } else {
    tempString += MTMPreHREF + myURL;
  }
return(tempString);
}

function MTMakeVoid(thisItem, thisCmd, thisText) {
	// INFO: item de menu
    var tempString = "";
    tempString +=  '\n          <a name="sub' + thisItem.number + '" href="javascript:parent.frames[\'code\'].myVoid();" onclick="' + thisCmd + '" onmouseover="window.status=\'' + thisText + '\';return true;" onmouseout="window.status=\'' + window.defaultStatus + '\';return true;"';
    if(thisItem.number == MTMClickedItem) {
        var tempClass;
        tempClass = thisItem.expanded ? "subexpanded" : "subclosed";
        tempString += ' class="' + tempClass + '"';
    }
    return(tempString + '>\n');
}

function MTMakeVoidII(thisItem, thisCmd, thisText) {
    var tempString = "";
    tempString +=  '\n          <a name="sub' + thisItem.number + '" href="javascript:parent.frames[\'code\'].myVoid();" onclick="' + thisCmd + '" onmouseover="window.status=\'' + thisText + '\';return true;" onmouseout="window.status=\'' + window.defaultStatus + '\';return true;"';
    if(thisItem.number == MTMClickedItem) {
        var tempClass;
        tempClass = thisItem.expanded ? "subexpanded" : "subclosed";
        tempString += ' class="' + tempClass + '"';
    }
    return(tempString + '>\n');
}

function MTMakeLink(thisItem, addName) {
  var tempString = '<a';

  if(MTMTrackedItem && MTMTrackedItem == thisItem.number) {
    tempString += ' class="tracked"'
  }
  if(addName) {
    tempString += ' name="sub' + thisItem.number + '"';
  }
  tempString += ' href="' + MTMCheckURL(thisItem.url) + '"';
  if(thisItem.target != "") {
    tempString += ' target="' + thisItem.target + '"';
  }
return tempString;
}

// jairo
function MTMakeImage(thisImage) {
  return('<img src="' + MTMPreHREF + MTMenuImageDirectory + thisImage + '" align="left" border="0" vspace="0" hspace="0" width="18" height="18">');
}

function MTMakeBackImage(thisImage) {
  var tempString = 'transparent url("' + ((MTMPreHREF == "") ? "" : MTMPreHREF);
  tempString += MTMenuImageDirectory + thisImage + '")'
  return(tempString);
}

function MTMakeA(thisType, thisText, thisColor) {
  var tempString = "";
  tempString += 'a' + ((thisType == "pseudo") ? ':' : '.');
  return(tempString + thisText + '{color:' + thisColor + ';background:' + MTMakeBackground() + ';}');
}

function MTMakeBackground() {
  return((MTMBackground == "") ? MTMBGColor : 'transparent');
}

function MTMTrackTarget(thisTarget) {
    if(thisTarget.charAt(0) == "_") {
        return false;
    } else {
        for(i = 0; i < MTMFrameNames.length; i++) {
            if(thisTarget == MTMFrameNames[i]) {
                return true;
            }
        }
    }
    return false;
}

/*
    Esta función permite actualizar la estructura de Arbol cuando se incluye o se elimina un nodo de la misma
*/
function updateArbol(itemPpal,subItem){
<%--    subItem = parent.frames['code'].items[nodeSelect];--%>
    if (subItem!=null){
        subItem.MTMakeSubmenu(itemPpal);
        setTimeout("MTMDisplayMenu()", 10);
    }
}

/******************************************************************************
* User-configurable options.                                                  *
******************************************************************************/

// Menu table width, either a pixel-value (number) or a percentage value.
var MTMTableWidth = "80%";

// Name of the frame where the menu is to appear.
var MTMenuFrame = "menu";

// variable for determining whether a sub-menu always gets a plus-sign
// regardless of whether it holds another sub-menu or not
var MTMSubsGetPlus = true;

// variable that defines whether the menu emulates the behaviour of
// Windows Explorer
var MTMEmulateWE = false;

// Directory of menu images/icons
var MTMenuImageDirectory = "menu-images/";

// Variables for controlling colors in the menu document.
// Regular BODY atttributes as in HTML documents.
var MTMBGColor = "#ffffff";
var MTMBackground = "menuDer.png";
//var MTMBackground = "";
var MTMTextColor = "blue";

// color for all menu items
var MTMLinkColor = "#996600";
//var MTMLinkColor = "#FFFFFF";

// Hover color, when the mouse is over a menu link
var MTMAhoverColor = "red";

// Foreground color for the tracking & clicked submenu item
var MTMTrackColor ="#FFCC99";
var MTMSubExpandColor = "#000099";
var MTMSubClosedColor = "#0066FF";

// All options regarding the root text and it's icon
var MTMRootIcon = "menu_new_root.gif";
<%--var MTMenuText = ":";--%>
var MTMenuText = "<%=nodeRoot%>";
var MTMRootColor = "#000066";
var MTMRootFont = "Arial, Helvetica, sans-serif";
var MTMRootCSSize = "84%";
var MTMRootFontSize = "-1";

// Font for menu items.
var MTMenuFont = "Arial, Helvetica, sans-serif";
var MTMenuCSSize = "84%";
var MTMenuFontSize = "-1";

// Variables for style sheet usage
// 'true' means use a linked style sheet.
var MTMLinkedSS = false;
var MTMSSHREF = "style/menu.css";

// Whether you want an open sub-menu to close automagically
// when another sub-menu is opened.  'true' means auto-close
var MTMSubsAutoClose = false;

// This variable controls how long it will take for the menu
// to appear if the tracking code in the content frame has
// failed to display the menu. Number if in tenths of a second
// (1/10) so 10 means "wait 1 second".
var MTMTimeOut = 35;

/******************************************************************************
* User-configurable list of icons.                                            *
******************************************************************************/

var MTMIconList = null;
MTMIconList = new IconList();
MTMIconList.addIcon(new MTMIcon("menu_link_external.gif", "http://", "pre"));
MTMIconList.addIcon(new MTMIcon("menu_link_pdf.gif", ".pdf", "post"));

/******************************************************************************
* User-configurable menu.                                                     *
******************************************************************************/

// Main menu.
var menu = null;
menu = new MTMenu();
<%=ToolsHTML.processNode("0",tree,session)%>
<%
    String dataNode = (String)((Hashtable)session.getAttribute("arbol")).get(idNodeRoot);
%>
    test = "<%=dataNode%>";
    p = test.indexOf("].submenu");
    inicio = 0;
    len = test.length;
    if (p > 0) {
        while (p > 0) {
            mensaje = test.substring(0,p+1);
            refreshTree(eval(mensaje));
            if (p+2 < len) {
                p = test.indexOf("].sub",p+2);
            } else {
                p = 0;
            }
        }
        refreshTree(test);
    } else {
        refreshTree(<%=dataNode%>);
    }

</script>

<script language="javascript">
function minimizar(){
	window.parent.document.getElementById("marcoEOC").rows="32,*,0";
	document.getElementById("min").style.display="none";
	document.getElementById("max").style.display="";
}
function maximizar(){
	<%ToolsHTML.printMenu(rb,usuario.getUser(),usuario.getIdGroup(),true,request);
	int item = Integer.parseInt(ToolsHTML.isEmptyOrNull(String.valueOf(session.getAttribute("numeroDeModulos")),"0"));%>
	window.parent.document.getElementById("marcoEOC").rows="32,*,<%=(item*20)+4%>";
	document.getElementById("min").style.display="";
	document.getElementById("max").style.display="none";
}

</script>
</HEAD>
    <body class="bodyInternas" "<%=onLoad.toString()%>">
		<table cellSpacing="1" cellPadding="0" align="center" border="0" width="100%"  height="100%" >
			<tr>
				<td width="100%"  valign="top" align="left" >
					<table cellSpacing="0" cellPadding="2"  border="0" width="100%"  height="100%" style="border-collapse:collapse;border-color:#ofofof;border:1px solid #efefef">
						<tr>
							<td height="30px" class="ppalBoton" onmouseover="this.className='ppalBoton ppalBoton2'" onmouseout="this.className='ppalBoton'">
								<table border="0" cellspacing="0" cellpadding="2" height="100%">
									<tr>
										<td class="ppalTextBold" width="100%">
											<%=rb.getString("enl.cbs")%>
										</td>
										<td>
										    &nbsp;
										</td>
										<td id="max" style="display:none">
										    <span ><img src="images/maximizar.gif" onClick="maximizar()"></span>&nbsp;
										</td>
										<td id="min">
										    <span ><img src="images/minimizar.gif" onClick="minimizar()"></span>&nbsp;
										</td>
									</tr>
								</table>
								
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
    </body>
</html>

