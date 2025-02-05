package com.desige.webDocuments.utils.beans;

import java.io.Serializable;

/**
 * Title: UserRecord.java <br/>
 * Copyright: (c) 2007 Focus Consulting C.A.<br/>
 * @version WebDocuments v4.3
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li>26/12/2007 (YSA) Creation </li>
 </ul>
 */

public class UserRecord extends SuperBeans implements Serializable {
	//Número de documentos creados
	private int docsCreated = 0;
	
	//Número de documentos revisados
    private int docsReviewed = 0;
    
	//Número de documentos aprobados
    private int docsApproved = 0;
    
	//Número de documentos expirados
    private int docsExpired = 0;
    
	//Número de documentos obsoletos
    private int docsObsolete = 0;
    
	//Número de borradores expirados
    private int draftsExpired = 0;
    
	//Número de flujos de trabajo de aprobación emitidos
    private int workflowsApproval = 0;
    
	//Flujos de trabajo de aprobación pendientes
    private int workflowsApprovalPending = 0;
    
	//Flujos de trabajo de aprobación aprobados
    private int workflowsApprovalApproved = 0;
    
	//Flujos de trabajo de aprobación cancelados
    private int workflowsApprovalCanceled = 0;
    
	//Flujos de trabajo de aprobación expirados
    private int workflowsApprovalExpired = 0;
    
	//Número de flujos de trabajo de revisión emitidos
    private int workflowsReview = 0;
    
	//Flujos de trabajo de revisión pendientes
    private int workflowsReviewPending = 0;
    
	//Flujos de trabajo de revisión aprobados
    private int workflowsReviewApproved = 0;
    
	//Flujos de trabajo de revisión cancelados
    private int workflowsReviewCanceled = 0;
    
	//Flujos de trabajo de revisión expirados
    private int workflowsReviewExpired = 0;
    
	//Número de flujos de trabajo de paramétricos emitidos
    private int ftp = 0;
    
	//Flujos de trabajo paramétricos pendientes
    private int ftpPending = 0;
    
	//Flujos de trabajo paramétricos aprobados
    private int ftpApproved = 0;
    
	//Flujos de trabajo paramétricos cancelados
    private int ftpCanceled = 0;
    
	//Flujos de trabajo paramétricos expirados
    private int ftpExpired = 0;
    
	//Flujos de trabajo paramétricos completados
    private int ftpCompleted = 0;
    
	//Número de SACOPs creadas
    private int sacopsCreated = 0;
    
	//Número de SACOPs en las que es responsable
    private int sacopsIsResponsible = 0;
    
	//Número de SACOPs en las que paticipa
    private int sacopsParticipate = 0;
    
	//Número de SACOPs que cerró con éxito
    private int sacopsClosed = 0;
    
	//Número de solicitudes de Impresión realizadas
    private int printing = 0;
    
	//Número de solicitudes de Impresión pendientes para que le aprueben
    private int printingPendingTo = 0;
    
	//Número de solicitudes de Impresión que le aprobaron
    private int printingApprovedTo = 0;
    
	//Número de solicitudes de Impresión que le rechazaron
    private int printingRejectedTo = 0;
    
    //Número de solicitudes de Impresión aprobadas
    private int printingApproved = 0;
    
	//Número de solicitudes de Impresión rechazadas
    private int printingRejected = 0;
    
	//Número de solicitudes de Impresión pendientes por aprobar		
    private int printingPending = 0;
    
    
    public UserRecord(){
    	setDocsApproved(0);
    	setDocsExpired(0);
    	setDocsObsolete(0);
    	setDocsReviewed(0);
    	setWorkflowsApproval(0);
    	setWorkflowsApprovalApproved(0);
    	setWorkflowsApprovalCanceled(0);
    	setWorkflowsApprovalExpired(0);
    	setWorkflowsApprovalPending(0);
    	setWorkflowsReview(0);
    	setWorkflowsReviewApproved(0);
    	setWorkflowsReviewCanceled(0);
    	setWorkflowsReviewExpired(0);
    	setWorkflowsReviewPending(0);
    	setFtp(0);
    	setFtpApproved(0);
    	setFtpCanceled(0);
    	setFtpCompleted(0);
    	setFtpExpired(0);
    	setFtpPending(0);
    	setSacopsClosed(0);
    	setSacopsCreated(0);
    	setSacopsIsResponsible(0);
    	setSacopsParticipate(0);
    	setPrinting(0);
    	setPrintingApproved(0);
    	setPrintingApprovedTo(0);
    	setPrintingPending(0);
    	setPrintingPendingTo(0);
    	setPrintingRejected(0);
    	setPrintingRejectedTo(0);
    }

	public int getDocsApproved() {
		return docsApproved;
	}

	public void setDocsApproved(int docsApproved) {
		this.docsApproved = docsApproved;
	}

	public int getDocsExpired() {
		return docsExpired;
	}

	public void setDocsExpired(int docsExpired) {
		this.docsExpired = docsExpired;
	}

	public int getDocsObsolete() {
		return docsObsolete;
	}

	public void setDocsObsolete(int docsObsolete) {
		this.docsObsolete = docsObsolete;
	}

	public int getDocsCreated() {
		return docsCreated;
	}

	public void setDocsCreated(int docsCreated) {
		this.docsCreated = docsCreated;
	}
	
	public int getDocsReviewed() {
		return docsReviewed;
	}

	public void setDocsReviewed(int docsReviewed) {
		this.docsReviewed = docsReviewed;
	}

	public int getDraftsExpired() {
		return draftsExpired;
	}

	public void setDraftsExpired(int draftsExpired) {
		this.draftsExpired = draftsExpired;
	}

	public int getFtp() {
		return ftp;
	}

	public void setFtp(int ftp) {
		this.ftp = ftp;
	}

	public int getFtpApproved() {
		return ftpApproved;
	}

	public void setFtpApproved(int ftpApproved) {
		this.ftpApproved = ftpApproved;
	}

	public int getFtpCanceled() {
		return ftpCanceled;
	}

	public void setFtpCanceled(int ftpCanceled) {
		this.ftpCanceled = ftpCanceled;
	}

	public int getFtpCompleted() {
		return ftpCompleted;
	}

	public void setFtpCompleted(int ftpCompleted) {
		this.ftpCompleted = ftpCompleted;
	}

	public int getFtpExpired() {
		return ftpExpired;
	}

	public void setFtpExpired(int ftpExpired) {
		this.ftpExpired = ftpExpired;
	}

	public int getFtpPending() {
		return ftpPending;
	}

	public void setFtpPending(int ftpPending) {
		this.ftpPending = ftpPending;
	}

	public int getPrinting() {
		return printing;
	}

	public void setPrinting(int printing) {
		this.printing = printing;
	}

	public int getPrintingApproved() {
		return printingApproved;
	}

	public void setPrintingApproved(int printingApproved) {
		this.printingApproved = printingApproved;
	}

	public int getPrintingApprovedTo() {
		return printingApprovedTo;
	}

	public void setPrintingApprovedTo(int printingApprovedTo) {
		this.printingApprovedTo = printingApprovedTo;
	}

	public int getPrintingPending() {
		return printingPending;
	}

	public void setPrintingPending(int printingPending) {
		this.printingPending = printingPending;
	}

	public int getPrintingPendingTo() {
		return printingPendingTo;
	}

	public void setPrintingPendingTo(int printingPendingTo) {
		this.printingPendingTo = printingPendingTo;
	}

	public int getPrintingRejected() {
		return printingRejected;
	}

	public void setPrintingRejected(int printingRejected) {
		this.printingRejected = printingRejected;
	}

	public int getPrintingRejectedTo() {
		return printingRejectedTo;
	}

	public void setPrintingRejectedTo(int printingRejectedTo) {
		this.printingRejectedTo = printingRejectedTo;
	}

	public int getSacopsClosed() {
		return sacopsClosed;
	}

	public void setSacopsClosed(int sacopsClosed) {
		this.sacopsClosed = sacopsClosed;
	}

	public int getSacopsCreated() {
		return sacopsCreated;
	}

	public void setSacopsCreated(int sacopsCreated) {
		this.sacopsCreated = sacopsCreated;
	}

	public int getSacopsIsResponsible() {
		return sacopsIsResponsible;
	}

	public void setSacopsIsResponsible(int sacopsIsResponsible) {
		this.sacopsIsResponsible = sacopsIsResponsible;
	}

	public int getSacopsParticipate() {
		return sacopsParticipate;
	}

	public void setSacopsParticipate(int sacopsParticipate) {
		this.sacopsParticipate = sacopsParticipate;
	}

	public int getWorkflowsApprovalApproved() {
		return workflowsApprovalApproved;
	}

	public void setWorkflowsApprovalApproved(int workflowsApprovalApproved) {
		this.workflowsApprovalApproved = workflowsApprovalApproved;
	}

	public int getWorkflowsApprovalCanceled() {
		return workflowsApprovalCanceled;
	}

	public void setWorkflowsApprovalCanceled(int workflowsApprovalCanceled) {
		this.workflowsApprovalCanceled = workflowsApprovalCanceled;
	}

	public int getWorkflowsApprovalExpired() {
		return workflowsApprovalExpired;
	}

	public void setWorkflowsApprovalExpired(int workflowsApprovalExpired) {
		this.workflowsApprovalExpired = workflowsApprovalExpired;
	}

	public int getWorkflowsApprovalPending() {
		return workflowsApprovalPending;
	}

	public void setWorkflowsApprovalPending(int workflowsApprovalPending) {
		this.workflowsApprovalPending = workflowsApprovalPending;
	}

	public int getWorkflowsApproval() {
		return workflowsApproval;
	}

	public void setWorkflowsApproval(int workflowsApproval) {
		this.workflowsApproval = workflowsApproval;
	}

	public int getWorkflowsReview() {
		return workflowsReview;
	}

	public void setWorkflowsReview(int workflowsReview) {
		this.workflowsReview = workflowsReview;
	}

	public int getWorkflowsReviewApproved() {
		return workflowsReviewApproved;
	}

	public void setWorkflowsReviewApproved(int workflowsReviewApproved) {
		this.workflowsReviewApproved = workflowsReviewApproved;
	}

	public int getWorkflowsReviewCanceled() {
		return workflowsReviewCanceled;
	}

	public void setWorkflowsReviewCanceled(int workflowsReviewCanceled) {
		this.workflowsReviewCanceled = workflowsReviewCanceled;
	}

	public int getWorkflowsReviewExpired() {
		return workflowsReviewExpired;
	}

	public void setWorkflowsReviewExpired(int workflowsReviewExpired) {
		this.workflowsReviewExpired = workflowsReviewExpired;
	}

	public int getWorkflowsReviewPending() {
		return workflowsReviewPending;
	}

	public void setWorkflowsReviewPending(int workflowsReviewPending) {
		this.workflowsReviewPending = workflowsReviewPending;
	}

}
