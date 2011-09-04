package org.jboss.as.testsuite.integration.jpa.hibernate.envers;

import java.util.*;
import java.lang.*;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.PersistenceContext;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.DefaultRevisionEntity;

/**
 * @author Madhumita Sadhukhan
 */
@Stateless
public class SLSBValidityStrategyOrg {
	
	@PersistenceContext(unitName = "myValidOrg")
	EntityManager em;

	public Organization createOrg(String name, String type, String startDate, String endDate,String location) {
		
		Organization org = new Organization();
		org.setName( name );
		org.setType( type );
		org.setStartDate( startDate );
		org.setEndDate( endDate );
		org.setLocation( location );
       // org.setPhoneNumbers( phones );
		em.persist( org );
		return org;
	}

	public Organization updateOrg(Organization o) {
		return em.merge( o );
	}

	public void deleteOrg(Organization o) {
		 em.remove( em.merge( o ) );
	}

	public List<Map<String,Object>> verifyEndRevision(Integer id) {

		AuditReader reader = AuditReaderFactory.get( em );
        boolean b;
        String queryString = "select a from " + Organization.class.getName() + "_AUD a where a.originalId.id=:id";
		//where a.originalId.id=:id
        Query query = em.createQuery(queryString);
        query.setParameter("id", id);
		List<Map<String,Object>> orgHistory = query.getResultList();
                
		 //List<Number> p1RevList = reader.getRevisions(Organization.class,id);
		 
		 return orgHistory;
		//Organization org1_rev = reader.find( Organization.class,id, 2 );
		//return revendTimestamp;
	}


	public Organization retrieveOldOrgbyId(int id) {
		
		AuditReader reader = AuditReaderFactory.get( em );
		Organization org1_rev = reader.find( Organization.class,id, 2 );
		return org1_rev;
	}
	
	

}
