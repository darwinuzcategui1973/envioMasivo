package com.desige.webDocuments.files.comparators;

import java.util.Comparator;

import com.desige.webDocuments.document.forms.BaseDocumentForm;

/**
 * Created by IntelliJ IDEA.
 * User: ydavila
 * Date: 26/12/2006
 * Time: 10:35:43 AM
 * To change this template use File | Settings | File Templates.
 */
public class FieldComparator implements Comparator {
	
	private String fa="";
	private String fb="";
	
	public FieldComparator(){
		
	}
	public FieldComparator(String fa, String fb){
		this.fa=fa;
		this.fb=fb;
	}

    public int compare(Object o1, Object o2) {
        BaseDocumentForm doc1 = (BaseDocumentForm)o1;
        BaseDocumentForm doc2 = (BaseDocumentForm)o2;
        String nameI = doc1.getNameDocument().trim();
        String nameII = doc2.getNameDocument().trim();
//        return doc1.getNameDocument().compareTo(doc2.getNameDocument());
        return nameI.compareToIgnoreCase(nameII);
    }

}
