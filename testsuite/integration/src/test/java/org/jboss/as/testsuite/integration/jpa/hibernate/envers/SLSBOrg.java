package org.jboss.as.testsuite.integration.jpa.hibernate.selectiveEnvers;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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

		em.persist( org );
		return org;
	}

	public Organization updateOrg(Organization o) {
		return em.merge( o );
	}

	public Organization deleteOrg(Organization o) {
		return em.merge( o );
	}

	public Organization retrieveOldOrgbyId(int id) {
		AuditReader reader = AuditReaderFactory.get( em );
		Organization org1_rev = reader.find( Organization.class,id, 1 );
		//System.out.println("org1_rev:" + org1_rev);
		return org1_rev;
	}
	
	public Organization retrieveOldOrgbyName(String name,int id) {
		AuditReader reader = AuditReaderFactory.get( em );
		Organization org1_rev = reader.find( Organization.class, name,id, 1 );
		return org1_rev;
	}

}
