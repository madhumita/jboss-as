package org.jboss.as.testsuite.integration.jpa.hibernate.envers;

import java.util.*;
import java.lang.*;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.PersistenceContext;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.envers.query.AuditQuery;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.DefaultRevisionEntity;

/**
 * @author Madhumita Sadhukhan
 */
@Stateless
public class SLSBAuditMO
{
	@PersistenceContext(unitName = "myCustPhone")
	EntityManager em;

	public CustomerMO createCustomer(String firstName, String surName, String type, String areacode,String phnumber) {
		PhoneMO phone1 = new PhoneMO();
		phone1.setNumber( phnumber );
		phone1.setAreacode( areacode );
                phone1.setType( type ); 

		PhoneMO phone2 = new PhoneMO();
		phone2.setNumber( "777222123" );
		phone2.setAreacode( "+420" );
                phone2.setType( "HOME" ); 

		CustomerMO cust = new CustomerMO();
		cust.setFirstname( firstName );
		cust.setSurname( surName );


                cust.getPhones().add(phone1);
                cust.getPhones().add(phone2);
		em.persist( cust );

		em.persist( phone1 );
		em.persist( phone2 );

		return cust;
	}

             

	public CustomerMO updateCustomer(CustomerMO c) {
		return em.merge( c );
	}

        public PhoneMO createPhone(String type, String areacode,String phnumber) {
		PhoneMO phone1 = new PhoneMO();
		phone1.setNumber( phnumber );
		phone1.setAreacode( areacode );
                phone1.setType( type ); 
		em.persist( phone1 );

		return phone1;
	}


	public PhoneMO updatePhone(PhoneMO p) {
		return em.merge( p );
	}

        public void deletePhone(PhoneMO p) {
		 em.remove( em.merge( p ) );
	}

	public int retrieveOldPhoneListSizeFromCustomer(int id,int revnumber) {
		AuditReader reader = AuditReaderFactory.get( em );
		CustomerMO cust_rev = reader.find( CustomerMO.class, id, revnumber );
		return cust_rev.getPhones().size();
	}

        public String retrieveOldPhoneListVersionFromCustomer(int id) {
		AuditReader reader = AuditReaderFactory.get( em );
		CustomerMO cust_rev = reader.find( CustomerMO.class, id, 1 );
		return cust_rev.getPhones().get(1).getType();
	}


	    

        /*public List<Object> verifyRevision() {

	AuditReader reader = AuditReaderFactory.get( em );
        boolean b;
        String queryString = "select a.originalId.REV from "+ CustomerMO.class.getName() + "_" + PhoneMO.class.getName() +"_AUD a";
        Query query = em.createQuery(queryString);
       
	List<Object> custHistory = query.getResultList();
        return custHistory;
		
	}

	
        public List<Object> verifyRevisionType() {

	AuditReader reader = AuditReaderFactory.get( em );
        boolean b;
        String queryString = "select a.REVTYPE_CUSTOM from CUSTOMER_PHONE_AUD a";

        Query query = em.createQuery(queryString);
       
	List<Object> custHistory = query.getResultList();
        return custHistory;
		
	}

        public List<Object> verifyOtherFields() {

	AuditReader reader = AuditReaderFactory.get( em );
        boolean b;
        String queryString = "select a.PHONE_ID from CUSTOMER_PHONE_BI_AUD a";
	//String queryString = "select a.originalId.REV from CUSTOMER_PHONE_AUD a";
        Query query = em.createQuery(queryString);
       
	List<Object> custHistory = query.getResultList();
        return custHistory;
		
	}*/

}
