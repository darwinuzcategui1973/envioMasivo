package com.desige.webDocuments.document.comparators;

import java.util.Comparator;

import com.desige.webDocuments.document.forms.BaseDocumentForm;

/**
 * Created by IntelliJ IDEA.
 * User: ydavila
 * Date: 26/12/2006
 * Time: 10:35:43 AM
 * To change this template use File | Settings | File Templates.
 */
public class StatusComparator implements Comparator {

	private boolean isDesc=false;
	
	public StatusComparator() {
	}

	public StatusComparator(boolean isDesc) {
		this.isDesc=isDesc;
	}

	public int compare(Object o1, Object o2) {
        BaseDocumentForm doc1 = (BaseDocumentForm)o1;
        BaseDocumentForm doc2 = (BaseDocumentForm)o2;
        String nameI = doc1.getStatuDoc();
        String nameII = doc2.getStatuDoc();
//        return doc1.getNameDocument().compareTo(doc2.getNameDocument());
        return (!isDesc?nameI.compareToIgnoreCase(nameII):nameII.compareToIgnoreCase(nameI));
    }

}
