package com.focus.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desige.webDocuments.document.forms.DocumentsCheckOutsBean;
import com.desige.webDocuments.utils.beans.Users;
import com.desige.webDocuments.utils.mail.SendMail;
import com.desige.webDocuments.workFlows.forms.DataUserWorkFlowForm;

/**
 * 
 * Title: SendMassiveMailThread.java <br>
 * Copyright: (c) 2012 Focus Consulting C.A.<br/>
 * @author Ing. Felipe Rojas (FJR)
 * 
 * @version WebDocuments v4.5.2
 * <br>
 *     Changes:<br>
 * <ul>
 *     <li> 26/03/2012 (FJR) Creation </li>
 * <ul>
 */
public class SendMassiveMailThread implements Runnable{
	private static Logger log = LoggerFactory.getLogger(SendMassiveMailThread.class.getName());
	
	private Collection<DocumentsCheckOutsBean> docCheckOuts;
	private Collection<DocumentsCheckOutsBean> docExpires;
	private Collection<DataUserWorkFlowForm> wfPendings;
	private Collection<DataUserWorkFlowForm> wfExpires;
	private Collection<DataUserWorkFlowForm> wfExpiresOwner;
	private Collection<DataUserWorkFlowForm> wfCancelled;
	private Collection<DocumentsCheckOutsBean> wfPrintApproved;

	// ticket darwin
	private Collection<DocumentsCheckOutsBean> wfPrintCanceled;
	private Collection<DocumentsCheckOutsBean> distList;
	
	private Map<String, MailReportBean> valuesToSend;
	private ResourceBundle rb;
	private Users usuario;
	
	/**
	 * 
	 * @param session
	 * @param rb
	 * @param usuario
	 */
	@SuppressWarnings("unchecked")
	public SendMassiveMailThread(HttpSession session, ResourceBundle rb, Users usuario) {
		// TODO Auto-generated constructor stub
		this.docCheckOuts = (Collection<DocumentsCheckOutsBean>) session.getAttribute("docCheckOuts");
		this.docExpires = (Collection<DocumentsCheckOutsBean>) session.getAttribute("docExpires");
		this.wfPendings = (Collection<DataUserWorkFlowForm>) session.getAttribute("wfPendings");
		this.wfExpires = (Collection<DataUserWorkFlowForm>) session.getAttribute("wfExpires");
		this.wfExpiresOwner = (Collection<DataUserWorkFlowForm>) session.getAttribute("wfExpiresOwner");
		this.wfCancelled = (Collection<DataUserWorkFlowForm>) session.getAttribute("wfCanceled");
		this.wfPrintApproved = (Collection<DocumentsCheckOutsBean>) session.getAttribute("wfPrintApproved");
		this.wfPrintCanceled = (Collection<DocumentsCheckOutsBean>) session.getAttribute("wfPrintCanceled");
		this.distList = (Collection<DocumentsCheckOutsBean>) session.getAttribute("docVersionApproved");
		
		this.rb = rb;
		this.usuario = usuario;
		valuesToSend = new HashMap<String, MailReportBean>();
	}
	
	public void run() {
		// TODO Auto-generated method stub
		//debemos obtener las metricas de cada usuario
		try {
			log.info("Iniciamos parseo de colecciones java, para obtener metricas de los usuarios y sus flujos");
			
			iterateOverBeanCollection(docCheckOuts, "setDocCheckOutsCount");
			iterateOverBeanCollection(docExpires, "setDocExpiresCount");
			iterateOverBeanCollection(wfPendings, "setWfPendingsCount");
			iterateOverBeanCollection(wfExpires, "setWfExpiresCount");
			iterateOverBeanCollection(wfExpiresOwner, "setWfExpiresOwnerCount");
			iterateOverBeanCollection(wfCancelled, "setWfCancelledCount");
			iterateOverBeanCollection(wfPrintApproved, "setWfPrintApprovedCount");
			iterateOverBeanCollection(wfPrintCanceled, "setWfPrintCanceledCount");
			iterateOverBeanCollection(distList, "setDistListCount");
			
			log.info("Fin de parseo de colecciones java, para obtener metricas de los usuarios y sus flujos");
			
			log.info("Iniciamos envio de correo a los involucrados en el parseo previo");
			
			ArrayList<String> tmpList = new ArrayList<String>();
			for (String key : valuesToSend.keySet()) {
				tmpList.add(key);
				
				try {
					SendMail.send(tmpList,
							usuario,
							rb.getString("sendEmail.mailSubject"),
							valuesToSend.get(key).getEmailMessageBody());
				} catch (Exception e) {
					// TODO: handle exception
					//si falla el envio de este correo particular, continuamos con los restantes,
					//de igual manera el mensaje quedara almacenado en base de datos
				}
				
				tmpList.clear();
			}
			
			log.info("Finalizado envio de correo a los involucrados en el parseo previo");
		} catch (Exception e) {
			// TODO: handle exception
			log.error("Error llenando las metricas de los usuarios y sus flujos para envio de correo masivo."
					+ " Error fue: " + e.getMessage(), e);
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param colToIterate
	 * @param setMethodToInvoke
	 * 
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private void iterateOverBeanCollection(Collection<? extends Object> colToIterate,
			String setMethodToInvoke) 
	throws SecurityException, NoSuchMethodException, IllegalArgumentException, 
	IllegalAccessException, InvocationTargetException{
		
		if(colToIterate != null){
			String previousValue = "";
			String currValue = "";
			int counter = 0;
			MailReportBean tmp = null;
			
			for (Object objBean : colToIterate) {
				counter ++;
				
				if(objBean instanceof DocumentsCheckOutsBean){
					currValue = ((DocumentsCheckOutsBean) objBean).getPersonBean().getEmail();
				} else if(objBean instanceof DataUserWorkFlowForm){
					currValue = ((DataUserWorkFlowForm) objBean).getPersonBean().getEmail();
				}
				if(! previousValue.equals(currValue)){
					//estamos iniciando un nuevo valor
					//ajustamos la cuenta del anterior
					//siempre y cuando exista un valor anterior
					if(! "".equals(previousValue)){
						updateMap(previousValue,
								--counter,
								setMethodToInvoke);
						counter = 1;
					}
					
					previousValue = currValue;
				}
			}
			
			//colocamos el ultimo registro ya que el no tendra un previo
			updateMap(previousValue,
					counter,
					setMethodToInvoke);
		}
	}
	
	/**
	 * 
	 * @param key
	 * @param counter
	 * 
	 * @param setMethodToInvoke
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private void updateMap(String key, int counter, String setMethodToInvoke) 
			throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, 
			InvocationTargetException{
		
		MailReportBean tmp = valuesToSend.get(key);
		
		if (tmp == null) {
			tmp = new MailReportBean(rb);
		}
		
		Class<MailReportBean> clazz = MailReportBean.class;
		Method m = clazz.getMethod(setMethodToInvoke, Integer.class);
		m.invoke(tmp, counter);
		
		valuesToSend.put(key, tmp);
	}
}

/**
 * Clase de tipo inner para colocar los valores asociados a los usuarios y sus contadores en el correo
 * con la informacion de sus metricas a nivel de flujos y documentos. <br>
 * 
 * Title: MailReportBean.java <br>
 * Copyright: (c) 2012 Focus Consulting C.A.<br>
 * @author Ing. Felipe Rojas (FJR)
 * 
 * @version WebDocuments v4.5.2
 * <br>
 *     Changes:<br>
 * <ul>
 *     <li> 26/03/2012 (FJR) Creation </li>
 * <ul>
 */
class MailReportBean{
	private Integer docCheckOutsCount;
	private Integer docExpiresCount;
	private Integer wfPendingsCount;
	private Integer wfExpiresCount;
	private Integer wfExpiresOwnerCount;
	private Integer wfCancelledCount;
	private Integer wfPrintApprovedCount;
	private Integer wfPrintCanceledCount ; // Contador de Fujo de impresnsion canceladas
	private Integer distListCount; //Contador de Lista Divulgacion
	
	private ResourceBundle rb;
	
	public MailReportBean(ResourceBundle rb) {
		// TODO Auto-generated constructor stub
		this.rb = rb;
	}

	public int getDocCheckOutsCount() {
		return docCheckOutsCount;
	}

	public void setDocCheckOutsCount(Integer docCheckOutsCount) {
		this.docCheckOutsCount = docCheckOutsCount;
	}

	public int getDocExpiresCount() {
		return docExpiresCount;
	}

	public void setDocExpiresCount(Integer docExpiresCount) {
		this.docExpiresCount = docExpiresCount;
	}

	public int getWfPendingsCount() {
		return wfPendingsCount;
	}

	public void setWfPendingsCount(Integer wfPendingsCount) {
		this.wfPendingsCount = wfPendingsCount;
	}

	public int getWfExpiresCount() {
		return wfExpiresCount;
	}

	public void setWfExpiresCount(Integer wfExpiresCount) {
		this.wfExpiresCount = wfExpiresCount;
	}

	public int getWfExpiresOwnerCount() {
		return wfExpiresOwnerCount;
	}

	public void setWfExpiresOwnerCount(Integer wfExpiresOwnerCount) {
		this.wfExpiresOwnerCount = wfExpiresOwnerCount;
	}

	public int getWfCancelledCount() {
		return wfCancelledCount;
	}

	public void setWfCancelledCount(Integer wfCancelledCount) {
		this.wfCancelledCount = wfCancelledCount;
	}

	public int getWfPrintApprovedCount() {
		return wfPrintApprovedCount;
	}

	public void setWfPrintApprovedCount(Integer wfPrintApprovedCount) {
		this.wfPrintApprovedCount = wfPrintApprovedCount;
	}
	          
	public int getWfPrintCancelledCount() {
		return wfPrintCanceledCount;
	}

	public void setWfPrintCancelledCount(Integer wfPrintCancelledCount) {
		this.wfPrintCanceledCount =wfPrintCancelledCount;
	}
	
	public int getDistListCount() {
		return distListCount;
	}

	public void setDistListCount(Integer distListCount) {
		this.distListCount =distListCount;
	}
	
	public String getEmailMessageBody(){
		final String br = "<br />";
		StringBuilder mailBody = new StringBuilder();
		
		mailBody.append(rb.getString("sendEmail.labelPendingDocs")).append(" ");
		mailBody.append((docCheckOutsCount == null ? "0" : docCheckOutsCount.toString())).append(br);
		
		mailBody.append(rb.getString("sendEmail.labelExpiredDocs")).append(" ");
		mailBody.append((docExpiresCount == null ? "0" : docExpiresCount.toString())).append(br);
		
		mailBody.append(rb.getString("sendEmail.labelPendingWF")).append(" ");
		mailBody.append((wfPendingsCount == null ? "0" : wfPendingsCount.toString())).append(br);
		
		mailBody.append(rb.getString("sendEmail.labelExpiredWFReceived")).append(" ");
		mailBody.append((wfExpiresCount == null ? "0" : wfExpiresCount.toString())).append(br);
		
		mailBody.append(rb.getString("sendEmail.labelExpiredWFSent")).append(" ");
		mailBody.append((wfExpiresOwnerCount == null ? "0" : wfExpiresOwnerCount.toString())).append(br);
		
		mailBody.append(rb.getString("sendEmail.labelCancelledWF")).append(" ");
		mailBody.append((wfCancelledCount == null ? "0" : wfCancelledCount.toString())).append(br);
		
		mailBody.append(rb.getString("sendEmail.labelPrintWF")).append(" ");
		mailBody.append((wfPrintApprovedCount == null ? "0" : wfPrintApprovedCount.toString())).append(br);
		
		mailBody.append(rb.getString("sendEmail.labelPrintCancelledWF")).append(" ");
		mailBody.append((wfPrintCanceledCount == null ? "0" : wfPrintCanceledCount.toString())).append(br);
	
		mailBody.append(rb.getString("sendEmail.labelDistList")).append(" ");
		mailBody.append((distListCount == null ? "0" : distListCount.toString())).append(br);
		
		return mailBody.toString();
	}
	
	@Override
	public String toString() {
		return "MailReportBean [docCheckOutsCount=" + docCheckOutsCount + ", docExpiresCount=" + docExpiresCount 
				+ ", wfPendingsCount=" + wfPendingsCount + ", wfExpiresCount=" + wfExpiresCount 
				+ ", wfExpiresOwnerCount=" + wfExpiresOwnerCount
				+ ", wfCancelledCount=" + wfCancelledCount 
				+ ", wfPrintApprovedCount=" + wfPrintApprovedCount
				+ ", wfPrintCanceledCount=" + wfPrintCanceledCount
				+ ", distListCount=" + distListCount + "]";
	}
}
