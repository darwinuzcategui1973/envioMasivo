package com.desige.webDocuments.document.comparators;

import java.util.Comparator;

import com.desige.webDocuments.document.forms.BaseDocumentForm;

/**
 * Created by IntelliJ IDEA.
 * User: ydavila
 * Date: 26/12/2006
 * Time: 11:27:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class NumberComparator  implements Comparator {
	
	private boolean isDesc=false;
	
	public NumberComparator() {
	}

	public NumberComparator(boolean isDesc) {
		this.isDesc=isDesc;
	}

    public int compare(Object o1, Object o2) {
        BaseDocumentForm doc1 = (BaseDocumentForm)o1;
        BaseDocumentForm doc2 = (BaseDocumentForm)o2;

        int result = (!isDesc?doc1.getPrefix().compareToIgnoreCase(doc2.getPrefix()):doc2.getPrefix().compareToIgnoreCase(doc1.getPrefix()));

        if (result == 0){
           	result = (!isDesc?doc1.getNumber().compareToIgnoreCase(doc2.getNumber()):doc2.getNumber().compareToIgnoreCase(doc1.getNumber()));
        }
//        String numero1 = doc1.getPrefix()!=null?doc1.getPrefix().toUpperCase():"" +
//                         doc1.getNumber()!=null?doc1.getNumber().toUpperCase():"";
//        String numero2 = doc2.getPrefix()!=null?doc2.getPrefix().toUpperCase():"" +
//                         doc2.getNumber()!=null?doc2.getNumber().toUpperCase():"";
//        return numero1.compareTo(numero2);

        return result;
    }
}