package com.desige.webDocuments.activities.actions;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.activities.forms.Activities;
import com.desige.webDocuments.persistent.managers.ActivityBD;
import com.desige.webDocuments.persistent.managers.HandlerTypeDoc;
import com.desige.webDocuments.persistent.utils.HibernateUtil;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;

public class CreateActivitiesAction extends SuperAction {
    static Logger log = LoggerFactory.getLogger(CreateActivitiesAction.class.getName());
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(mapping,form,request,response);
        try {
        	Activities forma = (Activities)form;
            if (forma==null) {
                forma = new Activities();
            }
            if (!SuperActionForm.cmdInsert.equalsIgnoreCase(getParameter("cmd"))) {
                forma.setCmd(SuperActionForm.cmdInsert);
                Collection acti = ActivityBD.getAllActivities(null);
                putObjectSession("activitiesForm",forma);
                putObjectSession("activities",acti);
                if(acti!=null) {
                	putObjectSession("size",String.valueOf(acti.size()));
                } else {
                	putObjectSession("size","0");
                }
                Collection typesDocuments = HandlerTypeDoc.getAllTypeDocs(null,true);
                if (typesDocuments!=null) {
                    putObjectSession("typesDocuments",typesDocuments);
                }
                return goSucces();
            } else {
                forma.setCmd(SuperActionForm.cmdInsert);
                putObjectSession("activitiesForm",forma);
                if (getSessionObject("typesDocuments")==null) {
                    Collection typesDocuments = HandlerTypeDoc.getAllTypeDocs(null,false);
                    if (typesDocuments!=null) {
                        putObjectSession("typesDocuments",typesDocuments);
                    }
                }
                return goTo("newFlexFlow");
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
            ex.printStackTrace();
        }
        return goError("E0100");
    }
//        Session session = null;
//            session = HibernateUtil.getSession();
//            forma = (Activities) HibernateUtil.loadObject(Activities.class,new Long(2));
//            log.debug("Number: " + forma.getNumber());
//            log.debug("Name: " + forma.getName());
//            log.debug("Description: " + forma.getDescription());
//            log.debug("Executant: " + forma.getExecutant());

//            Collection hCity = getAct();
//            for (Iterator iterator = hCity.iterator(); iterator.hasNext();) {
//                activities search = (Activities) iterator.next();
//                log.debug("" + search.getNumber());
//                log.debug("" + search.getName());
//            }
            //Prueba de Actualizaci�n
//            forma.setExecutant("14");
//            Transaction tx = HibernateUtil.createTransaction();
//            HibernateUtil.saveObject(forma);
//            tx.commit();
            //Prueba de Insertar de la Informaci�n
//            activities formaII = new Activities();
//            formaII.setNumber(3);
//            formaII.setName("Aprobaci�n");
//            formaII.setDescription("Aprobaci�n de Documentos de la Aplicaci�n");
//            formaII.setExecutant("1");
//            formaII.setNextExecutant("2");
//            Transaction tx = HibernateUtil.createTransaction();
//            session.save(formaII);
//            tx.commit();


//    public synchronized static Collection getAct() {
//        Vector result = new Vector();
////        Query hql1 = HibernateUtil.getQuery("AllActivitys");
////        List lista = hql1.list();
////        for (int i = 0; i < lista.size(); i++) {
////            activities activities = (Activities) lista.get(i);
////            result.add(activities);
////        }
//        Query q = HibernateUtil.getSQLQuery(" FROM activities WHERE executant = 1 ");
////        List l = q.list()
//        for (Iterator iter = q.list().iterator(); iter.hasNext();) {
//            result.add((Activities)iter.next());
//        }
	// return result;
	//    }

}
