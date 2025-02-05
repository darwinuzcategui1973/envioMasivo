package com.desige.webDocuments.document.comparators;

import java.util.Comparator;

import com.desige.webDocuments.document.forms.BaseDocumentForm;

/**
 * Created by IntelliJ IDEA.
 * User: ydavila
 * Date: 26/12/2006
 * Time: 11:28:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class OwnerComparator implements Comparator {

	private boolean isDesc=false;
	
	public OwnerComparator() {
	}

	public OwnerComparator(boolean isDesc) {
		this.isDesc=isDesc;
	}
	
    public int compare(Object o1, Object o2) {
        BaseDocumentForm doc1 = (BaseDocumentForm)o1;
        BaseDocumentForm doc2 = (BaseDocumentForm)o2;
        return (!isDesc?doc1.getOwner().compareToIgnoreCase(doc2.getOwner()):doc2.getOwner().compareToIgnoreCase(doc1.getOwner()));
    }
}