
//<!-- U-Upload Cart script file.  Include this file as a script in your own personal page.
 //<script language="javascript" src="uuploadcart.js"></script> is the only code you will need
// to add to your page.  Just make sure that uuploadpro.js is in the same directory as you page.

// Also note that if you wan to populate the variables dynamically with JSP, ASP, PHP, or Cold Fusion
// it may be easier if you cut/paste the contents of this file into your page between <script>...</script> tags.

// Use these Javascript variables to control the applet parameters-->

var numkeys 			= 1; // The number of keys you have
var keys= new Array(numkeys);

// Enter you keys here in the following form, starting with key[0], key[1], key[2], etc...
 
keys[0]				= ""; 

// Un-comment the folowing lines to add additional keys

//keys[1]				= ""; 
//keys[2]				= "";

// FTP Connection related values


var server 			= "desige11";
var port 			= "";
var user 			= "admin";
var pass 			= "1234";
var passive 			= "";
var encrypt			= ""; 
var ek				= "";  
var connecttimeout		= "1000";
var sotimeout			= "";
var waitRetry 			= "";
var maxRetries 			= "";
var useSerfo			= "";
var SerfoLocation		= "";

// Proxy related settings for IE only

var autodetectproxy		= "true";
var socksproxy			= "";
var socksProxyHost		= "";
var socksProxyPort		= "";
var ftpproxy			= "";
var ftpProxyHost		= "";
var ftpProxyPort		= "";

// Functionallity related values

var remotedir 			= "";
var localdir 			= "";
var confirmoverwrite 		= "";
var createdirectoryonstartup 	= "";
var extensions			= "";
var callurlaftertransfer	= "";
var callurlaftertransfertarget	= "";
var transfererrorurl		= "";
var transfererrorurltarget	= "";
var transfercancelurl		= "";
var transfercancelurltarget	= "";
var othererrorurl		= "";
var othererrorurltarget		= "";
var site 			= "";
var mode 			= "";
var maxfiles			= "";
var maxsize			= "";
var maintainpathinfo		= "";
var sitecommandstor 		= "";
var delimiter			= "";
var sendLogsToURL		= "";
var postURL			= "";
var autoZip 			= "";
var zipFilename			= "";
var zipPathInfo 		= "";


// values that effect the color and interface layout of the client

var width			= "1";
var height			= "1";
var bgcolor 			= "";		
var language 			= "";
var showpreview			= "";
var showlocaladdressbar		= "";

var uploadButtonLocation 	= "";

// some customizable error pages

var rejectPermissionURL = "rejectPerms.html";
var errNavWin 		= "errNavWin.html";
var errIEWin 		= "errIEWin.html";
var errIEWinVM		= "errIEWinVM.html";
var errNavUnix		= "errNavUnix.html";
var errIEMac		= "errIEMac.html";
var errNavMac		= "errNavMac.html";
var errOperaWin		= "errOperaWin.html";

//*************************************************************************************** //
// ********** DO NOT EDIT BELOW THIS POINT UNLESS YOU KNOW WHAT YOU ARE DOING!  ********* //
//*************************************************************************************** //

	var n;

        var agt=navigator.userAgent.toLowerCase();

        // detect browser version
        // Note: On IE5, these return 4, so use is_ie5up to detect IE5.
        var is_major = parseInt(navigator.appVersion);
        var is_minor = parseFloat(navigator.appVersion);

        // *** BROWSER TYPE ***
        var is_nav  = ((agt.indexOf('mozilla')!=-1) && (agt.indexOf('spoofer')==-1)
                    && (agt.indexOf('compatible') == -1) && (agt.indexOf('opera')==-1)
                    && (agt.indexOf('webtv')==-1));

	var is_opera = (agt.indexOf('opera')!=-1);
	var is_safari = (agt.indexOf('safari')!=-1);
	var is_konqueror = (agt.indexOf('konqueror')!=-1);
	var is_opera6up = (is_opera && (is_major >= 6));
        var is_nav4up = (is_nav && (is_major >= 4));
	var is_nav6up = (is_nav && (is_major >= 6));
        var is_ie   = (agt.indexOf("msie") != -1);
        var is_ie3  = (is_ie && (is_major < 4));
        var is_ie4  = (is_ie && (is_major == 4) && (agt.indexOf("msie 5.0")==-1) );
        var is_ie5  = (is_ie && (is_major == 4) && (agt.indexOf("msie 5.0")!=-1) );
        var is_ie5up  = (is_ie  && !is_ie3 && !is_ie4);

	// *** PLATFORM ***
        var is_win   = ( (agt.indexOf("win")!=-1) || (agt.indexOf("16bit")!=-1) );
        var is_mac    = (agt.indexOf("mac")!=-1);
        var is_sun   = (agt.indexOf("sunos")!=-1);
        var is_irix  = (agt.indexOf("irix") !=-1);    
        var is_hpux  = (agt.indexOf("hp-ux")!=-1);
        var is_aix   = (agt.indexOf("aix") !=-1);     
        var is_linux = (agt.indexOf("inux")!=-1);
        var is_sco   = (agt.indexOf("sco")!=-1) || (agt.indexOf("unix_sv")!=-1);
        var is_unixware = (agt.indexOf("unix_system_v")!=-1);
        var is_mpras    = (agt.indexOf("ncr")!=-1);
        var is_reliant  = (agt.indexOf("reliantunix")!=-1);
        var is_dec   = ((agt.indexOf("dec")!=-1) || (agt.indexOf("osf1")!=-1) ||
               (agt.indexOf("dec_alpha")!=-1) || (agt.indexOf("alphaserver")!=-1) ||
               (agt.indexOf("ultrix")!=-1) || (agt.indexOf("alphastation")!=-1));
        var is_sinix = (agt.indexOf("sinix")!=-1);
        var is_freebsd = (agt.indexOf("freebsd")!=-1);
        var is_bsd = (agt.indexOf("bsd")!=-1);
        var is_unix  = ((agt.indexOf("x11")!=-1) || is_sun || is_irix || is_hpux ||
                     is_sco ||is_unixware || is_mpras || is_reliant ||
                     is_dec || is_sinix || is_aix || is_linux || is_bsd || is_freebsd);


	function isMacClassic() {
		return (is_mac && !isMacX());
	}

	function isMacX() {
		var OJI;
 		for (var i = 0; i < navigator.plugins.length; i++) {
  			if (navigator.plugins[i].name.indexOf("CFM") > -1 || navigator.plugins[i].name.indexOf("OJI") > -1) 
				OJI = true;
 		} 
		return (is_safari || agt.indexOf("omniweb") != -1) || (navigator.plugins["Default Plugin Carbon.cfm"]) || (OJI);
	}

	function isMacJaguar() {
		return (isMacX() && javaPlugin());
	}

	function appletWillRun() {
        	if (is_ie4 || is_ie5up || (is_nav4up && is_minor >= 4.08) || (is_opera6up) || is_safari)                  
			return true;           
        	return false;
	}
	

	function javaPlugin() {
		if (is_safari) return true;   // we know safari uses java plugin
 		for (var i = 0; i < navigator.plugins.length; i++) {
  			if (navigator.plugins[i].name.indexOf("Java Plug-in") > -1) return true;
 		} 
		return false;		
	}

	function mrj()  {
 		for (var i = 0; i < navigator.plugins.length; i++) {
  			if (navigator.plugins[i].name.indexOf("MRJ") > -1 || navigator.plugins[i].name.indexOf("OJI") > -1) return true;			
 		} 
		return false;
	}

	// The following script will detect weather MAC or PC version of the applet should be started 
	// and set up the apropriate jar file

	var jar = "uuploadcart.jar";
	if (is_mac && !isMacJaguar()) {
		var jar = "uuploadcartMac.jar";
	}


	var netmac=0;			// netmac = 0 means it is either Mac/IE or non-Mac
	if (is_nav && is_minor < 6 && !isMacJaguar()) {		
 		if (javaPlugin() || mrj()) {
 			var netmac=1;	// netmac = 1 means this is Mac/Netscape so use <embed> tag
			
		
		} else if (isMacClassic() && !mrj()) { 
			var netmac=2;	// this is Mac/Netscape w/o MRJ Plugin
		}	
	
	}
	
	if (netmac == 0) {
  	    if (appletWillRun()) {

  		document.write("<APPLET name=uupload code=com.desige.ftpUtil.uppload.class height="+height+" width="+width+" archive='"+jar+"' MAYSCRIPT VIEWASTEXT>");
  		document.write("<PARAM NAME='cabbase' VALUE='codeBase.jar'>");
  		document.write("<PARAM NAME='bgcolor' VALUE='"+bgcolor+"'>");
  		document.write("<PARAM NAME='server' VALUE='"+server+"'>");
  		document.write("<PARAM NAME='port' VALUE='"+port+"'>");
  		document.write("<PARAM NAME='pass' VALUE='"+pass+"'>");
  		document.write("<PARAM NAME='ascbin' VALUE='macbin'>");
  		document.write("<PARAM NAME='showascbin' VALUE='false'>");
  		document.write("<PARAM NAME='user' VALUE='"+user+"'>");
		document.write("<PARAM NAME='passive' VALUE='"+passive+"'>");
  		document.write("<PARAM NAME='remotedir' VALUE='"+remotedir+"'>");
  		document.write("<PARAM NAME='extensions' VALUE='"+extensions+"'>");
  		document.write("<PARAM NAME='localdir' VALUE='"+localdir+"'>");
  		document.write("<PARAM NAME='site' VALUE='"+site+"'>");
		document.write("<PARAM NAME='mode' VALUE='"+mode+"'>");
  		document.write("<PARAM NAME='language' VALUE='"+language+"'>");
  		document.write("<PARAM NAME='confirmoverwrite' VALUE='"+confirmoverwrite+"'>");
		document.write("<PARAM NAME='maxfiles' VALUE='"+maxfiles+"'>");
		document.write("<PARAM NAME='maxsize' VALUE='"+maxsize+"'>");
  		document.write("<PARAM NAME='createdirectoryonstartup' VALUE='"+createdirectoryonstartup+"'>");
  		document.write("<PARAM NAME='callurlaftertransfer' VALUE='"+callurlaftertransfer+"'>");
		document.write("<PARAM NAME='callurlaftertransfertarget' VALUE='"+callurlaftertransfertarget+"'>");
		document.write("<PARAM NAME='transfererrorurl' VALUE='"+transfererrorurl+"'>");
		document.write("<PARAM NAME='transfercancelurl' VALUE='"+transfercancelurl+"'>");
		document.write("<PARAM NAME='transfererrorurltarget' VALUE='"+transfererrorurltarget+"'>");
		document.write("<PARAM NAME='transfercancelurltarget' VALUE='"+transfercancelurltarget+"'>");
		document.write("<PARAM NAME='othererrorurl' VALUE='"+othererrorurl+"'>");
		document.write("<PARAM NAME='othererrorurltarget' VALUE='"+othererrorurltarget+"'>");
  		document.write("<PARAM NAME='errIEWinVM' VALUE='"+errIEWinVM+"'>");
		document.write("<PARAM NAME='encrypt' VALUE='"+encrypt+"'>");
		document.write("<PARAM NAME='autodetectproxy' VALUE='"+autodetectproxy+"'>");
		document.write("<PARAM NAME='socksproxy' VALUE='"+socksproxy+"'>");
		document.write("<PARAM NAME='socksProxyHost' VALUE='"+socksProxyHost+"'>");
		document.write("<PARAM NAME='socksProxyPort' VALUE='"+socksProxyPort+"'>");
		document.write("<PARAM NAME='ftpproxy' VALUE='"+ftpproxy+"'>");
		document.write("<PARAM NAME='ftpProxyHost' VALUE='"+ftpProxyHost+"'>");
		document.write("<PARAM NAME='ftpProxyPort' VALUE='"+ftpProxyPort+"'>");	
		document.write("<PARAM NAME='SerfoLocation' VALUE='"+SerfoLocation+"'>");	
		document.write("<PARAM NAME='useSerfo' VALUE='"+useSerfo+"'>");
		document.write("<PARAM NAME='sotimeout' VALUE='"+sotimeout+"'>");		
		document.write("<PARAM NAME='connecttimeout' VALUE='"+connecttimeout+"'>");
		document.write("<PARAM NAME='showpreview' VALUE='"+showpreview+"'>");
		document.write("<PARAM NAME='showlocaladdressbar' VALUE='"+showlocaladdressbar+"'>");
		document.write("<PARAM NAME='waitRetry' VALUE='"+waitRetry+"'>");
		document.write("<PARAM NAME='maxRetries' VALUE='"+maxRetries+"'>");
		document.write("<PARAM NAME='maintainpathinfo' VALUE='"+maintainpathinfo+"'>");
		document.write("<PARAM NAME='sitecommandstor' VALUE='"+sitecommandstor+"'>");
		document.write("<PARAM NAME='sendLogsToURL' VALUE='"+sendLogsToURL+"'>");
		document.write("<PARAM NAME='postURL' VALUE='"+postURL+"'>");
		document.write("<PARAM NAME='delimiter' VALUE='"+delimiter+"'>");
		document.write("<PARAM NAME='uploadButtonLocation' VALUE='"+uploadButtonLocation+"'>");
		document.write("<PARAM NAME='autoZip' VALUE='"+autoZip+"'>");
		document.write("<PARAM NAME='zipFilename' VALUE='"+zipFilename+"'>");
		document.write("<PARAM NAME='zipPathInfo' VALUE='"+zipPathInfo+"'>");
		document.write("<PARAM NAME='ek' VALUE='"+ek+"'>");
		for (n=0; n < numkeys; n++)
			document.write("<PARAM NAME='key"+(n+1)+"' VALUE='"+keys[n]+"'>");				
  		document.write("<h1>");
  		document.write("Error!!! Java is disabled.</h1>");
  		document.write("Please enable Java and reload this page.");
  		document.write("</APPLET>");	
	     } else if (!appletWillRun()) {
		if (is_nav && is_win)  {
			window.location.href=errNavWin;			
		}
		else if (is_ie && is_win) {
			window.location.href=errIEWin;		
		}
		else if (is_nav && is_unix) {
			window.location.href=errNavUnix;
		}
		else if (is_ie && is_mac) {
			window.location.href=errIEMac;
		}
		else if (is_opera) { 
			window.location.href=errOperaWin;
		}
					
	   }
	}
	else if (netmac==1){ //It's Netscape use the embed tag instead!
		if (is_mac)
	    		document.writeln("<EMBED  TYPE = 'application/x-java-vm' name='uuploadcart' PLUGINSPAGE = 'http://www.mozilla.org/oji/' BORDER='0'");
		else 
			document.writeln("<EMBED TYPE = 'application/x-java-applet;version=1.3' name='U-Upload Cart' PLUGINSPAGE = 'http://java.sun.com/getjava/' BORDER='0'  java_CODEBASE = .");

		document.writeln("code='unlimited.ftp.UUploadCart.class' height="+height+" width="+width+"  archive='"+jar+"' "); 
  		document.writeln("bgcolor='"+bgcolor+"'"); 
		document.writeln("server='"+server+"'");
		document.writeln("port='"+port+"'"); 
		document.writeln("pass='"+pass+"'"); 
		document.writeln("passive='"+passive+"'");
		document.writeln("user='"+user+"'"); 
  		document.writeln("ascbin='macbin'");
  		document.writeln("showascbin='false'");
		document.writeln("remotedir='"+remotedir+"'");
		document.writeln("localdir='"+localdir+"'");
		document.writeln("extensions='"+extensions+"'");
		document.writeln("site='"+site+"'"); 
		document.writeln("mode='"+mode+"'"); 
		document.writeln("maxfiles='"+maxfiles+"'");
		document.writeln("maxsize='"+maxsize+"'");
		document.writeln("language='"+language+"'"); 
		document.writeln("confirmoverwrite='"+confirmoverwrite+"'"); 
		document.writeln("createdirectoryonstartup='"+createdirectoryonstartup+"'"); 
		document.writeln("callurlaftertransfer='"+callurlaftertransfer+"'");
		document.writeln("callurlaftertransfertarget='"+callurlaftertransfertarget+"'");
		document.writeln("transfererrorurl='"+transfererrorurl+"'");
		document.writeln("transfercancelurl='"+transfercancelurl+"'");
		document.writeln("transfererrorurltarget='"+transfererrorurltarget+"'");
		document.writeln("transfercancelurltarget='"+transfercancelurltarget+"'");
		document.writeln("othererrorurl='"+othererrorurl+"'");
		document.writeln("othererrorurltarget='"+othererrorurltarget+"'");
		document.writeln("errIEWinVM='"+errIEWinVM+"'");
		document.writeln("encrypt='"+encrypt+"'");
		document.writeln("autodetectproxy='"+autodetectproxy+"'");
		document.writeln("socksproxy='"+socksproxy+"'");
		document.writeln("socksProxyHost='"+socksProxyHost+"'");
		document.writeln("socksProxyPort='"+socksProxyPort+"'");
		document.writeln("ftpproxy='"+ftpproxy+"'");
		document.writeln("ftpProxyHost='"+ftpProxyHost+"'");
		document.writeln("ftpProxyPort='"+ftpProxyPort+"'");	
		document.writeln("SerfoLocation='"+SerfoLocation+"'");
		document.writeln("sotimeout='"+sotimeout+"'");	
		document.writeln("connecttimeout='"+connecttimeout+"'");
		document.writeln("showpreview='"+showpreview+"'");	
		document.writeln("showlocaladdressbar='"+showlocaladdressbar+"'");
		document.writeln("waitRetry='"+waitRetry+"'");
		document.writeln("maxRetries='"+maxRetries+"'");
		document.writeln("useSerfo='"+useSerfo+"'");
		document.writeln("maintainpathinfo='"+maintainpathinfo+"'");
		document.writeln("sitecommandstor='"+sitecommandstor+"'");
		document.writeln("sendLogsToURL='"+sendLogsToURL+"'");
		document.writeln("postURL='"+postURL+"'");
		document.writeln("delimiter='"+delimiter+"'");
		document.writeln("uploadButtonLocation='"+uploadButtonLocation+"'");
		document.writeln("autoZip='"+autoZip+"'");
		document.writeln("zipFilename='"+zipFilename+"'");
		document.writeln("zipPathInfo='"+zipPathInfo+"'");
		document.writeln("ek='"+ek+"'");
		for (n=0; n < numkeys; n++)
			document.write("key"+(n+1)+"='"+keys[n]+"'");	
		document.writeln(">"); 
	}
	else if (netmac==2) {//Netscape doesn't have the MRJ Plugin.
		window.location.href=errNavMac;
	}


