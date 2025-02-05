package com.desige.webDocuments.document.comparators;

import java.util.Comparator;

import com.desige.webDocuments.document.forms.BaseDocumentForm;

/**
 * Created by IntelliJ IDEA.
 * User: ydavila
 * Date: 26/12/2006
 * Time: 11:27:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class NormISOComparator   implements Comparator {

	private boolean isDesc=false;
	
	public NormISOComparator() {
	}

	public NormISOComparator(boolean isDesc) {
		this.isDesc=isDesc;
	}

	public int compare(Object o1, Object o2) {
        BaseDocumentForm doc1 = (BaseDocumentForm)o1;
        BaseDocumentForm doc2 = (BaseDocumentForm)o2;
        return (!isDesc?doc1.getNormISODescript().compareToIgnoreCase(doc2.getNormISODescript()):doc2.getNormISODescript().compareToIgnoreCase(doc1.getNormISODescript()));
    }
}