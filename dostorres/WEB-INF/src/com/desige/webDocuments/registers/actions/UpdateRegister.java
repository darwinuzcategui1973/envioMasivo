package com.desige.webDocuments.registers.actions;

import java.sql.Connection;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.document.actions.FileUtil;
import com.desige.webDocuments.document.forms.CheckOutDocForm;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: UpdateRegister.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0
 * <br/>
 *      Changes:<br/>
 * <ul>
 *      <li> 19/02/2005 (NC) Creation </li>
 * </ul>
 */
public class UpdateRegister extends SuperAction {
    public ActionForward execute(ActionMapping actionMapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(actionMapping,form,request,response);
        Connection con = null;
        try {
        	Users usuario = getUserSession();
            
            Properties campos = null;
            
			// TODO: CAMBIO PARA DOBLE VERSION
            FileUtil fileUtil = new FileUtil();
            campos = fileUtil.procesaFicheros(request);
            
            //System.out.println(campos.get("numVer"));
            int idVersion = Integer.parseInt(String.valueOf(campos.get("numVer")));
            //System.out.println("idVersion = " + idVersion);
            String path = ToolsHTML.getPath().concat("tmp"); // \\tmp
            //System.out.println("path = " + path);
            try {
                con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
                HandlerStruct.saveBD(con,String.valueOf(campos.get("fileName")),path,idVersion);

				//Eliminamos el archivo del cache
				ToolsHTML.deleteVersionCache(String.valueOf(idVersion));
                
                // marcamos que el usuario edito el registro en el flujo
                if(campos.get("idWF")!=null && !String.valueOf(campos.get("idWF")).equals("null")) {
                	try{
                		int idWF = Integer.parseInt(String.valueOf(campos.get("idWF")));
                        HandlerStruct.saveUserEditorBD(con,usuario.getUser(),idWF);
                	} catch(Exception ne){
                		ne.printStackTrace();
                	}
                }
                
                CheckOutDocForm forma = new CheckOutDocForm();
                forma.setNameFileParalelo(String.valueOf(campos.get("nameFileParalelo")));
                forma.setNumVer(idVersion);
                forma.setPath(path);
				HandlerStruct.saveDocView(con, forma);
				
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            String closeWindow = String.valueOf(campos.get("closeWindow"));
            if (ToolsHTML.isEmptyOrNull(closeWindow)) {
            	if ( !ToolsHTML.isEmptyOrNull(String.valueOf(getSessionObject("rejectDocument"))) ){
            		HandlerDocuments.approvedDocumentRejection(String.valueOf(idVersion), String.valueOf(getSessionObject("rejectDocument")),usuario, String.valueOf(getSessionObject("rejectFlexFlow")));
                    putAtributte("closeWindow","true");
            		return goTo("closeAndReject");
            	}
            }
            
            //System.out.println("closeWindow = " + closeWindow);
            if (!ToolsHTML.isEmptyOrNull(closeWindow)) {
                putAtributte("closeWindow","true");
                return goSucces();
            }
            ActionForward toStruct = null;
            if(ToolsHTML.isEmptyOrNull(String.valueOf(campos.get("idDocument")))) {
            	toStruct = new ActionForward("/loadStructMain.do?idNodeSelected="+getParameter("idNodeSelected"),false);
            } else {
            	toStruct = new ActionForward("/showDataDocument.do?idDocument="+campos.get("idDocument"),false);
            }
            return toStruct;
        } catch (ApplicationExceptionChecked ae) {
            ae.printStackTrace();
            return goError(ae.getKeyError());
        } finally {
            JDBCUtil.closeConnection(con,null);
        }
    }
}

