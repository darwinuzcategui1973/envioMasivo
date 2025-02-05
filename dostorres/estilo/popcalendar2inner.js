/**
 * written	by Tan Ling	Wee	on 2 Dec 2001
 * last updated 23 June 2002
 * email :	fuushikaden@yahoo.com
 *
 */
	var	fixedX = -1; //0;			// x position (-1 if to appear below control)
	var	fixedY = -1; //0;			// y position (-1 if to appear below control)
	var startAt = 1;			// 0 - sunday ; 1 - monday
	var showWeekNumber = 1;	// 0 - don't show; 1 - show
	var showToday = 1;		// 0 - don't show; 1 - show
	var imgDir = "./estilo/images/";			// directory for images ... e.g. var imgDir="/img/"

	var gotoString = "Ir al Mes actual";
	var todayString = "Hoy es";
	var weekString = "Sem";
	var scrollLeftMessage = "Click to scroll to previous month. Hold mouse button to scroll automatically.";
	var scrollRightMessage = "Click to scroll to next month. Hold mouse button to scroll automatically.";
	var selectMonthMessage = "Click to select a month.";
	var selectYearMessage = "Click to select a year.";
	var selectDateMessage = "Select [date] as date."; // do not replace [date], it will be replaced by date.

	var	crossobj, crossMonthObj, crossYearObj, monthSelected, yearSelected, dateSelected, omonthSelected, oyearSelected, odateSelected, monthConstructed, yearConstructed, intervalID1, intervalID2, timeoutID1, timeoutID2, ctlToPlaceValue, ctlNow, dateFormat, nStartingYear, dayToPlaceValue, monthToPlaceValue, yearToPlaceValue;

	var	bPageLoaded=false;
	var	ie=document.all;
	var	dom=document.getElementById;

	var	ns4=document.layers;
	var	today =	new	Date();
	var	dateNow	 = today.getDate();
	var	monthNow = today.getMonth();
	var	yearNow	 = today.getFullYear();
	var	imgsrc = new Array("drop1.gif","drop2.gif","left1.gif","left2.gif","right1.gif","right2.gif")
	var	img	= new Array();

	var bShow = false;
	
	var final = null;

    /* hides <select> and <applet> objects (for IE only) */
    function hideElement( elmID, overDiv )
    {
      if( ie )
      {
        for( i = 0; i < document.all.tags( elmID ).length; i++ )
        {
          obj = document.all.tags( elmID )[i];
          if( !obj || !obj.offsetParent )
          {
            continue;
          }

          // Find the element's offsetTop and offsetLeft relative to the BODY tag.
          objLeft   = obj.offsetLeft;
          objTop    = obj.offsetTop;
          objParent = obj.offsetParent;

          while( objParent.tagName.toUpperCase() != "BODY" )
          {
            objLeft  += objParent.offsetLeft;
            objTop   += objParent.offsetTop;
            objParent = objParent.offsetParent;
          }

          objHeight = obj.offsetHeight;
          objWidth = obj.offsetWidth;

          if(( overDiv.offsetLeft + overDiv.offsetWidth ) <= objLeft );
          else if(( overDiv.offsetTop + overDiv.offsetHeight ) <= objTop );
          else if( overDiv.offsetTop >= ( objTop + objHeight ));
          else if( overDiv.offsetLeft >= ( objLeft + objWidth ));
          else
          {
          	// si el div no deja ver el calendario lo ocultamos display:none
            //obj.style.visibility = "hidden";
          }
        }
      }
    }

    /*
    * unhides <select> and <applet> objects (for IE only)
    */
    function showElement( elmID )
    {
      if( ie )
      {
        for( i = 0; i < document.all.tags( elmID ).length; i++ )
        {
          obj = document.all.tags( elmID )[i];

          if( !obj || !obj.offsetParent )
          {
            continue;
          }

          obj.style.visibility = "";
        }
      }
    }

	function HolidayRec (d, m, y,desc)
	{
		this.d = d
		this.m = m
		//Verication for holidays permanent (LL)
        if (y==0){
		   this.y = yearSelected
		}
		else this.y = y
		//end (LL)
		this.desc = desc

	}
   	var HolidaysCounter = 0
	var Holidays = new Array()
    //Holidays received as parameter (LL)
    var HolidaysCounterListPerm = 0
    var listaHolidaysPerm=new Array()
    //end (LL)

	function addHoliday (d, m, y, desc)
	{
		Holidays[HolidaysCounter++] = new HolidayRec ( d, m, y, desc )
	}

	if (dom)
	{
		for	(i=0;i<imgsrc.length;i++)
		{
			img[i] = new Image
			img[i].src = imgDir + imgsrc[i]
		}
		document.write ("<div onclick='bShow=true' id='calendar'	style='z-index:+999;position:absolute;visibility:hidden;'><table width="+((showWeekNumber==1)?220:190)+" style='font-family:arial;font-size:11px;border-width:1;border-style:solid;border-color:#a0a0a0;font-family:arial; font-size:11px}' bgcolor='#ffffff'><tr bgcolor='#0000aa'><td><table width='"+((showWeekNumber==1)?218:188)+"'><tr><td style='padding:2px;font-family:arial; font-size:11px;'><font color='#ffffff'><B><span id='caption'></span></B></font></td><td align=right><a href='javascript:hideCalendar(true)'><IMG SRC='"+imgDir+"close.gif' WIDTH='15' HEIGHT='13' BORDER='0' ALT='Close the Calendar' /></a></td></tr></table></td></tr><tr><td style='padding:5px' bgcolor=#ffffff><span id='content'></span></td></tr>")

		if (showToday==1)
		{
			document.write ("<tr bgcolor=#f0f0f0><td style='padding:0px' align=center><span id='lblToday'></span></td></tr>")
		}

		document.write ("</table></div><div id='selectMonth' style='z-index:+999;position:absolute;visibility:hidden;'></div><div id='selectYear' style='z-index:+999;position:absolute;visibility:hidden;'></div>");
	}

	var	monthName =	new	Array("Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiemb..","Octubre","Noviembre","Diciembre")
	if (startAt==0)
	{
		dayName = new Array	("Dom","Lun","Mar","Mie","Jue","Vie","Sab");
		dayNameLarge = new Array	("Domingo","Lunes","Martes","Miercoles","Jueves","Viernes","Sabado");
		//dayName = new Array ("D","L","M","M","J","V","S");
	}
	else
	{
		dayName = new Array	("Lun","Mar","Mie","Jue","Vie","Sab","Dom");
		dayNameLarge = new Array ("Lunes","Martes","Miercoles","Jueves","Viernes","Sabado","Domingo");
		//dayName = new Array	("L","M","M","J","V","S","D");
	}
	var	styleAnchor="text-decoration:none;color:black;padding:0px;"
	var	styleLightBorder="border-style:solid;border-width:1px;border-color:#a0a0a0;"

	function swapImage(srcImg, destImg){
		if (ie)	{ document.getElementById(srcImg).setAttribute("src",imgDir + destImg) }
	}

	function init()	{
		if (!ns4)
		{
			//if (!ie) { yearNow += 1900	}

			//crossobj=(dom)?document.getElementById("calendar").style : ie? document.all.calendar : document.calendar
			crossobj=document.getElementById("calendar").style;
			hideCalendar()

			crossMonthObj=(dom)?document.getElementById("selectMonth").style : ie? document.all.selectMonth	: document.selectMonth

			crossYearObj=(dom)?document.getElementById("selectYear").style : ie? document.all.selectYear : document.selectYear

			monthConstructed=false;
			yearConstructed=false;

			if (showToday==1)
			{
				document.getElementById("lblToday").innerHTML =	todayString + " <a onmousemove='window.status=\""+gotoString+"\"' onmouseout='window.status=\"\"' title='"+gotoString+"' style='"+styleAnchor+"' href='javascript:monthSelected=monthNow;yearSelected=yearNow;constructCalendar();'>"+dayNameLarge[(today.getDay()-startAt==-1)?6:(today.getDay()-startAt)]+", " + dateNow + " " + monthName[monthNow].substring(0,3)	+ "	" +	yearNow	+ "</a>"
			}
			sHTML1="<span id='spanLeft'	style='border-style:solid;border-width:1;border-color:#3366FF;cursor:pointer' onmouseover='swapImage(\"changeLeft\",\"left2.gif\");this.style.borderColor=\"#88AAFF\";window.status=\""+scrollLeftMessage+"\"' onclick='javascript:decMonth()' onmouseout='clearInterval(intervalID1);swapImage(\"changeLeft\",\"left1.gif\");this.style.borderColor=\"#3366FF\";window.status=\"\"' onmousedown='clearTimeout(timeoutID1);timeoutID1=setTimeout(\"StartDecMonth()\",500)'	onmouseup='clearTimeout(timeoutID1);clearInterval(intervalID1)'>&nbsp<IMG id='changeLeft' SRC='"+imgDir+"left1.gif' width=10 height=11 BORDER=0 />&nbsp</span>&nbsp;"
			sHTML1+="<span id='spanRight' style='border-style:solid;border-width:1;border-color:#3366FF;cursor:pointer'	onmouseover='swapImage(\"changeRight\",\"right2.gif\");this.style.borderColor=\"#88AAFF\";window.status=\""+scrollRightMessage+"\"' onmouseout='clearInterval(intervalID1);swapImage(\"changeRight\",\"right1.gif\");this.style.borderColor=\"#3366FF\";window.status=\"\"' onclick='incMonth()' onmousedown='clearTimeout(timeoutID1);timeoutID1=setTimeout(\"StartIncMonth()\",500)'	onmouseup='clearTimeout(timeoutID1);clearInterval(intervalID1)'>&nbsp<IMG id='changeRight' SRC='"+imgDir+"right1.gif'	width=10 height=11 BORDER=0 />&nbsp</span>&nbsp"
			sHTML1+="<span id='spanMonth' style='border-style:solid;border-width:1;border-color:#3366FF;cursor:pointer'	onmouseover='swapImage(\"changeMonth\",\"drop2.gif\");this.style.borderColor=\"#88AAFF\";window.status=\""+selectMonthMessage+"\"' onmouseout='swapImage(\"changeMonth\",\"drop1.gif\");this.style.borderColor=\"#3366FF\";window.status=\"\"' onclick='popUpMonth()'></span>&nbsp;"
			sHTML1+="<span id='spanYear' style='border-style:solid;border-width:1;border-color:#3366FF;cursor:pointer' onmouseover='swapImage(\"changeYear\",\"drop2.gif\");this.style.borderColor=\"#88AAFF\";window.status=\""+selectYearMessage+"\"'	onmouseout='swapImage(\"changeYear\",\"drop1.gif\");this.style.borderColor=\"#3366FF\";window.status=\"\"'	onclick='popUpYear()'></span>&nbsp;"

			document.getElementById("caption").innerHTML  =	sHTML1

			bPageLoaded=true
		}
	}

	function hideCalendar(cerrar)	{
		if (crossobj!=null) {crossobj.visibility="hidden";}
		if (crossMonthObj != null){crossMonthObj.visibility="hidden"}
		if (crossYearObj !=	null){crossYearObj.visibility="hidden"}

		if(cerrar==true) {
			window.close();
		}
	    showElement( 'SELECT' );
		showElement( 'APPLET' );
	}

	function padZero(num) {
		return (num	< 10)? '0' + num : num ;
	}

	function constructDate(d,m,y)
	{
		sTmp = dateFormat
		sTmp = sTmp.replace	("dd","<e>")
		sTmp = sTmp.replace	("d","<d>")
		sTmp = sTmp.replace	("<e>",padZero(d))
		sTmp = sTmp.replace	("<d>",d)
		sTmp = sTmp.replace	("mmm","<o>")
		sTmp = sTmp.replace	("mm","<n>")
		sTmp = sTmp.replace	("m","<m>")
		sTmp = sTmp.replace	("<m>",m+1)
		sTmp = sTmp.replace	("<n>",padZero(m+1))
		sTmp = sTmp.replace	("<o>",monthName[m])
		return sTmp.replace ("yyyy",y)
	}

	function closeCalendar() {
		var	sTmp

		hideCalendar();
		ctlToPlaceValue.value =	constructDate(dateSelected,monthSelected,yearSelected);
        dayToPlaceValue.value = padZero(dateSelected);
        monthToPlaceValue.value = padZero(monthSelected + 1);
        yearToPlaceValue.value = yearSelected;
        //
        if (ctlToPlaceValue.onchange) {
           ctlToPlaceValue.onchange();
        }
        //Aceptar(document.forms[0]);
        if(final!=null){
       		eval(final);
       	}
	}

	/*** Month Pulldown	***/

	function StartDecMonth()
	{
		intervalID1=setInterval("decMonth()",80)
	}

	function StartIncMonth()
	{
		intervalID1=setInterval("incMonth()",80)
	}

	function incMonth () {
		monthSelected++
		if (monthSelected>11) {
			monthSelected=0
			yearSelected++
		}
		constructCalendar()
	}

	function decMonth () {
		monthSelected--
		if (monthSelected<0) {
			monthSelected=11
			yearSelected--
		}
		constructCalendar()
	}

	function constructMonth() {
		popDownYear()
		if (!monthConstructed) {
			sHTML =	""
			for	(i=0; i<6;	i++) {
				sHTML += "<tr>";

				sName =	monthName[i];
				if (i==monthSelected){
					sName =	"<B>" +	sName +	"</B>"
				}

				sHTML += "<td id='m" + i + "' onmouseover='this.style.backgroundColor=\"#FFCC99\"' onmouseout='this.style.backgroundColor=\"\"' style='cursor:pointer' onclick='monthConstructed=false;monthSelected=" + i + ";constructCalendar();popDownMonth();event.cancelBubble=true'>";
				sHTML += "&nbsp;" + sName + "&nbsp;&nbsp;";
				sHTML += "</td>";

				i = i+6;
				sName =	monthName[i];
				if (i==monthSelected){
					sName =	"<B>" +	sName +	"</B>"
				}
				sHTML += "<td id='m" + i + "' onmouseover='this.style.backgroundColor=\"#FFCC99\"' onmouseout='this.style.backgroundColor=\"\"' style='cursor:pointer' onclick='monthConstructed=false;monthSelected=" + i + ";constructCalendar();popDownMonth();event.cancelBubble=true'>";
				sHTML += "&nbsp;" + sName + "&nbsp;&nbsp;";
				sHTML += "</td>";
				i = i-6;

				sHTML += "</tr>"
				
			}

			document.getElementById("selectMonth").innerHTML = "<table width=70 style='font-family:arial; font-size:11px; border-width:1; border-style:solid; border-color:#a0a0a0;' bgcolor='#FFFFDD' cellspacing=0 cellpadding=0 onmouseover='clearTimeout(timeoutID1)'	onmouseout='clearTimeout(timeoutID1);timeoutID1=setTimeout(\"popDownMonth()\",100);event.cancelBubble=true'>" +	sHTML +	"</table>"

			monthConstructed=true
		}
	}

	function popUpMonth() {
		constructMonth()
		crossMonthObj.zIndex=10000
		crossMonthObj.visibility = (dom||ie)? "visible"	: "show"
		crossMonthObj.left = parseInt(crossobj.left) + 50
		crossMonthObj.top =	parseInt(crossobj.top) + 26

		hideElement( 'SELECT', document.getElementById("selectMonth") );
		hideElement( 'APPLET', document.getElementById("selectMonth") );
	}

	function popDownMonth()	{
		crossMonthObj.visibility= "hidden"
	}

	/*** Year Pulldown ***/

	function incYear() {
		for	(i=0; i<7; i++){
			newYear	= (i+nStartingYear)+1
			if (newYear==yearSelected)
			{ txtYear =	"&nbsp;<B>"	+ newYear +	"</B>&nbsp;" }
			else
			{ txtYear =	"&nbsp;" + newYear + "&nbsp;" }
			document.getElementById("y"+i).innerHTML = txtYear
		}
		nStartingYear ++;
		bShow=true
	}

	function decYear() {
		for	(i=0; i<7; i++){
			newYear	= (i+nStartingYear)-1
			if (newYear==yearSelected)
			{ txtYear =	"&nbsp;<B>"	+ newYear +	"</B>&nbsp;" }
			else
			{ txtYear =	"&nbsp;" + newYear + "&nbsp;" }
			document.getElementById("y"+i).innerHTML = txtYear
		}
		nStartingYear --;
		bShow=true
	}

	function selectYear(nYear) {
		yearSelected=parseInt(nYear+nStartingYear);
		yearConstructed=false;
		constructCalendar();
		popDownYear();
	}

	function constructYear() {
		popDownMonth()
		sHTML =	""
		if (!yearConstructed) {

			sHTML =	"<tr><td align='center'	onmouseover='this.style.backgroundColor=\"#FFCC99\"' onmouseout='clearInterval(intervalID1);this.style.backgroundColor=\"\"' style='cursor:pointer'	onmousedown='clearInterval(intervalID1);intervalID1=setInterval(\"decYear()\",30)' onmouseup='clearInterval(intervalID1)'>-</td></tr>"

			j =	0
			nStartingYear =	yearSelected-3
			for	(i=(yearSelected-3); i<=(yearSelected+3); i++) {
				sName =	i;
				if (i==yearSelected){
					sName =	"<B>" +	sName +	"</B>"
				}

				sHTML += "<tr><td id='y" + j + "' onmouseover='this.style.backgroundColor=\"#FFCC99\"' onmouseout='this.style.backgroundColor=\"\"' style='cursor:pointer' onclick='selectYear("+j+");event.cancelBubble=true'>&nbsp;" + sName + "&nbsp;</td></tr>"
				j ++;
			}

			sHTML += "<tr><td align='center' onmouseover='this.style.backgroundColor=\"#FFCC99\"' onmouseout='clearInterval(intervalID2);this.style.backgroundColor=\"\"' style='cursor:pointer' onmousedown='clearInterval(intervalID2);intervalID2=setInterval(\"incYear()\",30)'	onmouseup='clearInterval(intervalID2)'>+</td></tr>"

			document.getElementById("selectYear").innerHTML	= "<table width=44 style='font-family:arial; font-size:11px; border-width:1; border-style:solid; border-color:#a0a0a0;'	bgcolor='#FFFFDD' onmouseover='clearTimeout(timeoutID2)' onmouseout='clearTimeout(timeoutID2);timeoutID2=setTimeout(\"popDownYear()\",100)' cellspacing=0>"	+ sHTML	+ "</table>"

			yearConstructed	= true
		}
	}

	function popDownYear() {
		clearInterval(intervalID1)
		clearTimeout(timeoutID1)
		clearInterval(intervalID2)
		clearTimeout(timeoutID2)
		crossYearObj.zIndex=10000
		crossYearObj.visibility= "hidden"
	}

	function popUpYear() {
		var	leftOffset

		constructYear()
		crossYearObj.zIndex=10000
		crossYearObj.visibility	= (dom||ie)? "visible" : "show"
		leftOffset = parseInt(crossobj.left) + document.getElementById("spanYear").offsetLeft
		if (ie)
		{
			leftOffset += 6
		}
		crossYearObj.left =	leftOffset
		crossYearObj.top = parseInt(crossobj.top) +	26
	}

	/*** calendar ***/
   function WeekNbr(n) {
      // Algorithm used:
      // From Klaus Tondering's Calendar document (The Authority/Guru)
      // hhtp://www.tondering.dk/claus/calendar.html
      // a = (14-month) / 12
      // y = year + 4800 - a
      // m = month + 12a - 3
      // J = day + (153m + 2) / 5 + 365y + y / 4 - y / 100 + y / 400 - 32045
      // d4 = (J + 31741 - (J mod 7)) mod 146097 mod 36524 mod 1461
      // L = d4 / 1460
      // d1 = ((d4 - L) mod 365) + L
      // WeekNumber = d1 / 7 + 1

      year = n.getFullYear();
      month = n.getMonth() + 1;
      if (startAt == 0) {
         day = n.getDate() + 1;
      }
      else {
         day = n.getDate();
      }

      a = Math.floor((14-month) / 12);
      y = year + 4800 - a;
      m = month + 12 * a - 3;
      b = Math.floor(y/4) - Math.floor(y/100) + Math.floor(y/400);
      J = day + Math.floor((153 * m + 2) / 5) + 365 * y + b - 32045;
      var d4 = (((J + 31741 - (J % 7)) % 146097) % 36524) % 1461;
      L = Math.floor(d4 / 1460);

      var d1 = ((d4 - L) % 365) + L;

      week = Math.floor(d1/7) + 1;

      return week;
   }

	function constructCalendar () {
		var aNumDays = Array (31,0,31,30,31,30,31,31,30,31,30,31)

		var dateMessage
		var	startDate =	new	Date (yearSelected,monthSelected,1)
		var endDate
      HolidaysCounter = 0
	   Holidays = new Array()

		if (monthSelected==1)
		{
			endDate	= new Date (yearSelected,monthSelected+1,1);
			endDate	= new Date (endDate	- (24*60*60*1000));
			numDaysInMonth = endDate.getDate()
		}
		else
		{
			numDaysInMonth = aNumDays[monthSelected];
		}

		datePointer	= 0
		dayPointer = startDate.getDay() - startAt

		if (dayPointer<0)
		{
			dayPointer = 6
		}

		sHTML =	"<table	 border=0 style='font-family:verdana;font-size:9px;'><tr>"

		if (showWeekNumber==1)
		{
			sHTML += "<td width=27><b>" + weekString + "</b></td><td width=1 rowspan=7 bgcolor='#d0d0d0' style='padding:0px'><img src='"+imgDir+"divider.gif' width=1/></td>"
		}

		for	(i=0; i<7; i++)	{
			sHTML += "<td width='20' align='right' ><B>"+ dayName[i]+"</B></td>"
		}
		sHTML +="</tr><tr>"

		if (showWeekNumber==1)
		{
			sHTML += "<td align=right bgcolor='#afafaf'>" + WeekNbr(startDate) + "&nbsp;</td>"
		}

		for	( var i=1; i<=dayPointer;i++ )
		{
			sHTML += "<td>&nbsp;</td>"
		}

      //Creation of Holidays with the received list of holidays  (LL)
      var elem,daux,maux,yaux,descaux
      for (i=0;i<listaHolidaysPerm.length;i++){
           elem=listaHolidaysPerm[i]

           daux=parseInt(elem.substring(0,2),10)
           maux=parseInt(elem.substring(3,5),10)
           yaux=parseInt(elem.substring(6,10),10)
           if (elem.substring(11,12)=="s"){   //verification if holiday is permanent
            if  (yaux<=yearSelected){         //verification if holiday is vigent
                yaux=0;
                descaux=elem.substring(13,elem.length)
                addHoliday(daux,maux,yaux,descaux)
            }
           }
           else {
               descaux=elem.substring(13,elem.length)
               addHoliday(daux,maux,yaux,descaux)
           }
      }
      //end (LL)
      for	( datePointer=1; datePointer<=numDaysInMonth; datePointer++ )
		{
			dayPointer++;
			sHTML += "<td align=right>"
			sStyle=styleAnchor
			if ((datePointer==odateSelected) &&	(monthSelected==omonthSelected)	&& (yearSelected==oyearSelected))
			{ sStyle+=styleLightBorder }

			sHint = ""
			for (k=0;k<HolidaysCounter;k++)
			{
				if ((parseInt(Holidays[k].d)==datePointer)&&(parseInt(Holidays[k].m)==(monthSelected+1)))
				{
					if ((parseInt(Holidays[k].y)==0)||((parseInt(Holidays[k].y)==yearSelected)&&(parseInt(Holidays[k].y)!=0)))
					{
						sStyle+="background-color:#FFDDDD;"
						sHint+=sHint==""?Holidays[k].desc:"\n"+Holidays[k].desc
					}
				}
			}

			var regexp= /\"/g
			sHint=sHint.replace(regexp,"&quot;")

			dateMessage = "onmousemove='window.status=\""+selectDateMessage.replace("[date]",constructDate(datePointer,monthSelected,yearSelected))+"\"' onmouseout='window.status=\"\"' "

			if ((datePointer==dateNow)&&(monthSelected==monthNow)&&(yearSelected==yearNow))
			{ sHTML += "<b><a "+dateMessage+" title=\"" + sHint + "\" style='"+sStyle+"' href='javascript:dateSelected="+datePointer+";closeCalendar();'><font color=#ff0000>&nbsp;" + datePointer + "</font>&nbsp;</a></b>"}
			else if	(dayPointer % 7 == (startAt * -1)+1)
			{ sHTML += "<a "+dateMessage+" title=\"" + sHint + "\" style='"+sStyle+"' href='javascript:dateSelected="+datePointer + ";closeCalendar();'>&nbsp;<font color=#909090>" + datePointer + "</font>&nbsp;</a>" }
			else
			{ sHTML += "<a "+dateMessage+" title=\"" + sHint + "\" style='"+sStyle+"' href='javascript:dateSelected="+datePointer + ";closeCalendar();'>&nbsp;" + datePointer + "&nbsp;</a>" }

			sHTML += ""
			if ((dayPointer+startAt) % 7 == startAt) {
				sHTML += "</tr><tr>"
				if ((showWeekNumber==1)&&(datePointer<numDaysInMonth))
				{
					sHTML += "<td align=right bgcolor='#afafaf'>" + (WeekNbr(new Date(yearSelected,monthSelected,datePointer+1))) + "&nbsp;</td>"
				}
			}
		}

		document.getElementById("content").innerHTML   = sHTML
		document.getElementById("spanMonth").innerHTML = "&nbsp; " +	monthName[monthSelected] + "&nbsp;<IMG id='changeMonth' SRC='"+imgDir+"drop1.gif' WIDTH='12' HEIGHT='10' BORDER=0 />"
		document.getElementById("spanYear").innerHTML =	"&nbsp;" + yearSelected	+ "&nbsp;<IMG id='changeYear' SRC='"+imgDir+"drop1.gif' WIDTH='12' HEIGHT='10' BORDER=0 />"
	}



	function showCalendar(ctl, contenedor, postCalendar) {
		var nada;
		popUpCalendar(ctl,nada,nada,nada,nada,nada,nada, contenedor, postCalendar);
	}

	function showCalendarDDMMYYYY(ctl, contenedor, postCalendar) {
		var nada;
		popUpCalendar(ctl,nada,nada,nada,nada,'dd/mm/yyyy',nada, contenedor, postCalendar);
	}
	
	function showCalendarFormato(ctl, contenedor,postCalendar) {
		var nada;
		var formato = "mm-yyyy";
		popUpCalendar(ctl,nada,nada,nada,nada,formato,nada, contenedor, postCalendar);
	}
	

	function popUpCalendar(ctl,	ctl2, day, month, year, format, listHolidays, contenedor, postCalendar) {
		day = (typeof ctl2=='undefined'?'':day);
		month = (typeof ctl2=='undefined'?'':month);
		year = (typeof ctl2=='undefined'?'':year);
		ctl2 = (typeof ctl2=='undefined'?ctl:ctl2);
		format = (typeof format=='undefined'?'yyyy-mm-dd':format);
		if(typeof postCalendar!='undefined') {
			final=postCalendar;    //funcion que se ejecuta al ocultarse el calendario
		}
		if(document.getElementById(contenedor)){
			document.getElementById(contenedor).appendChild(document.getElementById("calendar"));
			//document.getElementById("calendario").style.paddingTop="1pt";
		}
		var leftpos=0;
		var toppos=0;
		
		hideCalendar();
        //initialization of holidays list (LL)

        if (listHolidays!=null){listaHolidaysPerm=listHolidays};

		if (bPageLoaded)
		{
			if ( crossobj.visibility ==	"hidden" ) {
				ctlToPlaceValue	= ctl2;
                dayToPlaceValue = day;
                monthToPlaceValue = month;
                yearToPlaceValue = year;
				dateFormat=format;

				formatChar = " "
				aFormat	= dateFormat.split(formatChar)
				if (aFormat.length<3)
				{
					formatChar = "/"
					aFormat	= dateFormat.split(formatChar)
					if (aFormat.length<3)
					{
						formatChar = "."
						aFormat	= dateFormat.split(formatChar)
						if (aFormat.length<3)
						{
							formatChar = "-"
							aFormat	= dateFormat.split(formatChar)
							if (aFormat.length<3)
							{
								// invalid date	format
								formatChar=""
							}
						}
					}
				}

				tokensChanged =	0
				if ( formatChar	!= "" )
				{
					// use user's date
					aData =	ctl2.value.split(formatChar)

					for	(i=0;i<3;i++)
					{
						if ((aFormat[i]=="d") || (aFormat[i]=="dd"))
						{
							dateSelected = parseInt(aData[i], 10)
							tokensChanged ++
						}
						else if	((aFormat[i]=="m") || (aFormat[i]=="mm"))
						{
							monthSelected =	parseInt(aData[i], 10) - 1
							tokensChanged ++
						}
						else if	(aFormat[i]=="yyyy")
						{
							yearSelected = parseInt(aData[i], 10)
							tokensChanged ++
						}
						else if	(aFormat[i]=="mmm")
						{
							for	(j=0; j<12;	j++)
							{
								if (aData[i]==monthName[j])
								{
									monthSelected=j
									tokensChanged ++
								}
							}
						}
					}
				}

				if ((tokensChanged!=3)||isNaN(dateSelected)||isNaN(monthSelected)||isNaN(yearSelected))
				{
					dateSelected = dateNow
					monthSelected =	monthNow
					yearSelected = yearNow
				}

				odateSelected=dateSelected
				omonthSelected=monthSelected
				oyearSelected=yearSelected

				aTag = ctl
				do {
					aTag = aTag.offsetParent;
					leftpos	+= aTag.offsetLeft;
					toppos += aTag.offsetTop;
				} while(aTag.tagName!="BODY");
				crossobj.left =	fixedX==-1 ? ctl.offsetLeft	+ leftpos :	fixedX
				crossobj.top = fixedY==-1 ?	ctl.offsetTop +	toppos + ctl.offsetHeight +	2 :	fixedY
				constructCalendar (1, monthSelected, yearSelected);
				crossobj.visibility=(dom||ie)? "visible" : "show"

				hideElement( 'SELECT', document.getElementById("calendar") );
				hideElement( 'APPLET', document.getElementById("calendar") );

				bShow = true;
			}
			else
			{
				hideCalendar()
				if (ctlNow!=ctl) {popUpCalendar(ctl, ctl2, format)}
			}
			ctlNow = ctl
		}


	}

	document.onkeypress = function hidecal1 () {
		if (event.keyCode==27)
		{
			hideCalendar()
		}
	}
	document.onclick = function hidecal2 () {
		if (!bShow)
		{
			hideCalendar()
		}
		bShow = false
	}

	if(ie)
	{
		//init();
		addEvent(window,'load',init);
	}
	else
	{
		//window.onload=init
		addEvent(window,'load',init);
	}
	
	function addEvent(obj, evType, fn, useCapture){
	  if (obj.addEventListener){
	    obj.addEventListener(evType, fn, useCapture);
	    return true;
	  } else if (obj.attachEvent){
	    var r = obj.attachEvent("on"+evType, fn);
	    return r;
	  }
	}
	