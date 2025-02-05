<%@ page
	import="com.desige.webDocuments.utils.ToolsHTML, 
                 com.desige.webDocuments.persistent.managers.HandlerStruct,
                 java.util.Collection,
                 java.util.Hashtable"%>
<!-- structToSelect.jsp -->

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
<title>Tree</title>
<META http-equiv=Content-Type content="text/html; charset=windows-1252">
<META http-equiv=Cache-Control content="no-cache, must-revalidate">
<META http-equiv=Pragma content=no-cache>
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<%
	String nodeRoot = (String) session.getAttribute("nodeRoot");
	String idNodeRoot = (String) session.getAttribute("idNodeRoot");
	if (!ToolsHTML.checkValue(nodeRoot)) {
		nodeRoot = " ";
	}
	if (!ToolsHTML.checkValue(idNodeRoot)) {
		idNodeRoot = " ";
	}
	Hashtable tree = (Hashtable) session.getAttribute("tree");
%>
<script type="text/javascript">

function detenerError() {
    return true;
}
window.onerror=detenerError

/******************************************************************************
* Define the MenuItem object.                                                 *
******************************************************************************/
function MTMenuItem(text, url, target, icon,id) {
	MTMenuItem(text, url, target, icon, id, 0, 0);
}

function MTMenuItem(text, url, target, icon, id, mayorVer, minorVer) {
    this.text = text;
    this.url = url ? url : "";
    this.target =  target ? target : "";
    this.icon = icon ? icon : "";
    idObj = id?id:"";
    this.number = MTMSubNumber++;
    this.name = idObj;
    this.submenu     = null;
    this.expanded    = false;
    //aqui para validar que solo sea tipo carpeta
    //this.mayorVer = mayorVer;
    this.mayorVer = icon ? icon : "";
    this.minorVer = minorVer;
    //this.iconClose = iconOpen?iconOpen : "";
    this.MTMakeSubmenu = MTMakeSubmenu;
}

function MTMakeSubmenu(menu) {
    this.submenu = menu;
}

/******************************************************************************
* Define the Menu object.                                                     *
******************************************************************************/

function MTMenu() {
    this.items   = new Array();
    this.MTMAddItem = MTMAddItem;
    this.name = "";
}

function MTMAddItem(item) {
    this.items[this.items.length] = item;
}

/******************************************************************************
* Define the icon list, addIcon function and MTMIcon item.                    *
******************************************************************************/

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

/******************************************************************************
* Global variables.  Not to be altered unless you know what you're doing.     *
* User-configurable options are at the end of this document.                  *
******************************************************************************/

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

/******************************************************************************
* Code that picks up frame names of frames in the parent frameset.            *
******************************************************************************/

if(MTMBrowser) {
  var MTMFrameNames = new Array();
  for(i = 0; i < parent.frames.length; i++) {
    MTMFrameNames[i] = parent.frames[i].name;
  }
}

/******************************************************************************
* Dummy function for sub-menus without URLs                                   *
* Thanks to Michel Plungjan for the advice. :)                                *
******************************************************************************/

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
    if (!parent.frames['text'].active){
        if (parent.frames['text'].document.getElementById("Selection")) {
            if (parent.frames['text'].document.getElementById("Selection").movDocument) {
                isCopyPresent = parent.frames['text'].document.getElementById("Selection").movDocument.value == 1;
            } else if (parent.frames['text'].document.getElementById("Selection").isCopy) {
                isCopyPresent = parent.frames['text'].document.getElementById("Selection").isCopy.value == 1;
            }
        }
    }
    if (isCopyPresent) {
        MTMSubAction(itemActual,false);
    }
}

/******************************************************************************
* Functions to draw the menu.                                                 *
******************************************************************************/

function MTMSubAction(SubItem, ReturnValue) {
    var isCopyPresent = false;
    SubItem.expanded = (SubItem.expanded) ? false : true;
    if(SubItem.expanded) {
        MTMExpansion = true;
    }
    MTMClickedItem = SubItem.number;
    if(MTMTrackedItem && MTMTrackedItem != SubItem.number) {
        MTMTrackedItem = false;
    }
    if(!ReturnValue) {
        setTimeout("MTMDisplayMenu()", 10);
    }
    parent.frames['text'].setValues(SubItem.name,SubItem.text,SubItem.mayorVer,SubItem.minorVer);
    refreshTree(SubItem);
    return ReturnValue;
}

function MTMSubActionII(SubItem, ReturnValue) {
    expandir = (SubItem.expanded) ? false : true;
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
    parent.frames['text'].setValues(SubItem.name,SubItem.text,SubItem.mayorVer,SubItem.minorVer);
    refreshTree(SubItem);
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

function refreshText(newText,SubItem){
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
    MTMDoc = parent.frames[MTMenuFrame].document
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
      MTMOutputString += 'background="' + MTMPreHREF + MTMenuImageDirectory + MTMBackground + '" ';
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

        if(!MTMEmulateWE && !item.expanded && (item.url != "")) {
            MTMClickCmd = "return " + MTMfrm + ".MTMSubAction(" + MTMfrm + MTMref + ",true);";
        } else {
            MTMClickCmd = "return " + MTMfrm + ".MTMSubAction(" + MTMfrm + MTMref + ",false);";
            MTMSelect = "return " + MTMfrm + ".MTMSubActionII(" + MTMfrm + MTMref + ",false);";
        }
        if(item.url == "") {
            MTMouseOverText = (item.text.indexOf("'") != -1) ? MTMEscapeQuotes(item.text) : item.text;
        } else {
            MTMouseOverText = "Expand/Collapse";
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
        //MTMOutputString += "</a>\n";
        //MTMOutputString += MTMakeVoid(item, MTMSelect, MTMouseOverText);
        //MTMOutputString += MTMakeImage(img);
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
  alert(tempString + " " +thisItem.text );
return tempString;
}

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
var MTMBGColor = "white";
//var MTMBackground = "menuDer.gif";
var MTMBackground = "";
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

<%--<%=ToolsHTML.processNode("24",tree,session)%>--%>
<%=ToolsHTML.processNode("0", tree, session)%>
<%String dataNode = (String) ((Hashtable) session
					.getAttribute("arbol")).get(idNodeRoot);
			System.out.println("dataNode = " + dataNode);%>
refreshTree(<%=dataNode%>);

<%--test = "parent.frames['code'].menu.items[0].submenu.items[0].submenu.items[1]";--%>
<%--p = test.indexOf("].submenu");--%>
<%--inicio = 0;--%>
<%--len = test.length;--%>
<%--if (p > 0) {--%>
<%--    while (p > 0) {--%>
<%--        mensaje = test.substring(0,p+1);--%>
<%--        refreshTree(mensaje);--%>
<%--        if (p+2 < len) {--%>
<%--            p = test.indexOf("].sub",p+2);--%>
<%--        } else {--%>
<%--            p = 0;--%>
<%--        }--%>
<%--    }--%>
<%--    refreshTree(test);--%>
<%--} else {--%>
<%--    refreshTree(<%=dataNode%>);--%>
<%--}--%>
<%--refreshTree(parent.frames['code'].menu.items[0]);--%>
<%--refreshTree(parent.frames['code'].menu.items[0].submenu.items[0]);--%>
<%--refreshTree(parent.frames['code'].menu.items[0].submenu.items[0].submenu.items[1]);--%>
</script>

<meta content="MSHTML 6.00.2800.1106" name=GENERATOR>
</HEAD>
<body class="bodyInternas" onload=MTMStartMenu()>T
</body>
</html>
