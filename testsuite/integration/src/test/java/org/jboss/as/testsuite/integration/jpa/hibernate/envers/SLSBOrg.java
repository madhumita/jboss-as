package org.jboss.as.testsuite.integration.jpa.hibernate.envers;

import java.util.Set;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;

/**
 * @author Madhumita Sadhukhan
 */
@Stateless
public class SLSBOrg {
	
	@PersistenceContext(unitName = "myOrg")
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

	public Organization retrieveOldOrgbyId(int id) {
		
		AuditReader reader = AuditReaderFactory.get( em );
		Organization org1_rev = reader.find( Organization.class,id, 2 );
		return org1_rev;
	}
	
	public Organization retrieveDeletedOrgbyId(int id) {
		
		AuditReader reader = AuditReaderFactory.get( em );
		Organization org1_rev = reader.find( Organization.class,id, 8 );
		return org1_rev;
	}
	
	public Organization retrieveOldOrgbyEntityName(String name,int id) {
		
		AuditReader reader = AuditReaderFactory.get( em );
		Organization org1_rev = reader.find( Organization.class, name,id, 5 );
		return org1_rev;
	}

}
