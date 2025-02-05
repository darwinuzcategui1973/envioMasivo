package com.desige.webDocuments.document.comparators;

import java.util.Comparator;

import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.utils.ToolsHTML;

/**
 * Title: ApprovedComparator.java <br/>
 * Copyright: (c) 2007 Focus Consulting C.A. <br/>
 * @author Ing. Nelson Crespo (NC)
 * @version QwebDocuments v4.0
 *
 * <br/>
 *      Changes:
 * <ul>
 *      <li> 2007-01-24 (NC) Creation </li>
 * </ul>
 */
public class ApprovedComparator implements Comparator {

	private boolean isDesc=false;
	
	public ApprovedComparator() {
	}

	public ApprovedComparator(boolean isDesc) {
		this.isDesc=isDesc;
	}

	public int compare(Object o1, Object o2) {
        BaseDocumentForm doc1 = (BaseDocumentForm)o1;
        BaseDocumentForm doc2 = (BaseDocumentForm)o2;
        String fechaI = null;
        String fechaII = null;
        try {
            fechaI = ToolsHTML.sdf.format(ToolsHTML.sdfShowWithoutHour.parse(doc1.getDateApproved()));
            fechaII = ToolsHTML.sdf.format(ToolsHTML.sdfShowWithoutHour.parse(doc2.getDateApproved()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
//        return doc1.getDateApproved().compareTo(doc2.getDateApproved());
        return (!isDesc?fechaI.compareTo(fechaII):fechaII.compareTo(fechaI));
    }
}
