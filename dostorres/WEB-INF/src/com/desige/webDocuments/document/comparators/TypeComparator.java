package com.desige.webDocuments.document.comparators;

import java.util.Comparator;

import com.desige.webDocuments.document.forms.BaseDocumentForm;

/**
 * Created by IntelliJ IDEA.
 * User: ydavila
 * Date: 26/12/2006
 * Time: 11:27:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class TypeComparator  implements Comparator {

	private boolean isDesc=false;
	
	public TypeComparator() {
	}

	public TypeComparator(boolean isDesc) {
		this.isDesc=isDesc;
	}

	public int compare(Object o1, Object o2) {
        BaseDocumentForm doc1 = (BaseDocumentForm)o1;
        BaseDocumentForm doc2 = (BaseDocumentForm)o2;
        return (!isDesc?doc1.getDescriptTypeDoc().compareToIgnoreCase(doc2.getDescriptTypeDoc()):doc2.getDescriptTypeDoc().compareToIgnoreCase(doc1.getDescriptTypeDoc()));  
    }
}