package com.desige.webDocuments.document.comparators;

import java.util.Comparator;

import com.desige.webDocuments.utils.beans.Search;

public class DescriptComparator implements Comparator {

	/**
	 * 
	 */
    public int compare(Object o1, Object o2) {
    	Search usr1 = (Search)o1;
    	Search usr2 = (Search)o2;
        return usr1.getDescript().compareToIgnoreCase(usr2.getDescript());  //To change body of implemented methods use File | Settings | File Templates.
    }

}