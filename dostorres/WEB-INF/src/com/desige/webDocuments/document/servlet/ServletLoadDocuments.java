package com.desige.webDocuments.document.servlet;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desige.webDocuments.dao.ConfDocumentoDAO;
import com.desige.webDocuments.dao.DigitalDAO;
import com.desige.webDocuments.document.actions.EditDocumentAction;
import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.files.facade.DigitalFacade;
import com.desige.webDocuments.files.forms.DocumentForm;
import com.desige.webDocuments.persistent.managers.HandlerBD;
import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.to.DigitalTO;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.CheckConditions;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.StringUtil;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.CheckDocvencThread;
import com.desige.webDocuments.utils.beans.CheckUsersThread;
import com.desige.webDocuments.utils.beans.SacopActionsThreadManager;
import com.desige.webDocuments.utils.beans.Users;
import com.focus.util.Archivo;

/**
 * Title: ServletLoadDocuments.java.java <br/> Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * 
 * @author Ing. Nelson Crespo (NC) *
 * @author Ing. Sim�n Rodrigu�z (SR)
 * @version WebDocuments v3.0 <br/> Changes:<br/>
 *          <ul>
 *          <li>30/06/2004 (NC) Creation </li>
 *          <li>27/07/2005 (SR) Se valido que el documento sea publico para agregar el numero correlativo forma.getDocPublic </li>
 *          <li>16/08/2005 (SR) Se agrego la session de info2</li>
 *          <li>23/06/2006 (NC) Se agreg� el uso del Log y se sincronizaron los Hilos </li>
 *          </ul>
 */
public class ServletLoadDocuments extends HttpServlet {

}
