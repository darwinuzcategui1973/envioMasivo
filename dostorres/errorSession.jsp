<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML"%>
<%@ page language="java" %>
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
%>
<html>
    <head>
    <title></title>
	<jsp:include page="meta.jsp" /> 
    <link href="estilo/estilo.css" rel="stylesheet" type="text/css">
    <style>
    	body{
    		height:100%;
    	}
    </style>
    </head>
    <body height="100%" onLoad="begintimer()" class="bodyInternas" style="background:url(images/reloj.jpg) no-repeat bottom right;">
        <script language="javascript">
            function sendRedirect () {
                forma = document.redirectPage;
                forma.target = "_parent";
                forma.action = "logout.do";
                forma.submit();
            }
        </script>

        <script>
        <!--
            var limit="0:05"
            var parselimit = limit.split(":")
            parselimit=parselimit[0]*60+parselimit[1]*1

            function begintimer() { 
                if (!document.images) {
                    return
                }

                if (parselimit==1) {
                    sendRedirect();
                } else {
                    parselimit-=1
                    curmin=Math.floor(parselimit/60)
                    cursec=parselimit%60
                    if (curmin!=0) {
                        curtime = curmin + "<%=rb.getString("sec.minutes")%>" + cursec + " <%=rb.getString("sec.segundes")%>";
                    } else {
                        curtime=cursec + " <%=rb.getString("sec.segundes")%>";
                    }
                    window.status =  "<%=rb.getString("sec.redirect")%> " + curtime
                    setTimeout("begintimer()",1000)
                }
            }
        //-->
        </script>
        <form name="redirectPage" action="">
        </form>

        <table width="100%" border="0" align="center">
            <tr>
                <td class="pagesTitle">
                    <%=rb.getString("application.info")%>
                </td>
            </tr>
            <tr>
                <td>
                    <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="0" align=center border=0>
                        <tbody>
                            <tr>
                                <td class="td_title_bc" height="21">
                                    <%=rb.getString("application.info")%>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </td>
            </tr>
            <logic:present name="error" scope="session">
                <tr>
                    <td class="td_gris_c" style="font-size:20pt;">
                        <br>
                        <br>
                        <%=rb.getString("E0035")%>
                        <%session.removeAttribute("error");%>
                    </td>
                </tr>
            </logic:present>
        </table>
    </body>
</html>
