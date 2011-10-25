/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.testsuite.integration.jpa.hibernate.envers;

import javax.ejb.EJB;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertTrue;
import java.util.*;
import java.util.Set;
import java.sql.Connection;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionType;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.hibernate.Session;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * @author Madhumita Sadhukhan
 */
@RunWith(Arquillian.class)
public class AuditJoinTableoverBidirectionalTest {
	private static final String ARCHIVE_NAME = "jpa_AuditJoinTableoverBidirectionalTest";

	private static final String persistence_xml =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?> " +
					"<persistence xmlns=\"http://java.sun.com/xml/ns/persistence\" version=\"1.0\">" +
					"  <persistence-unit name=\"myCustPhone\">" +
					"    <description>Persistence Unit." +
					"    </description>" +
					"    <jta-data-source>java:jboss/datasources/ExampleDS</jta-data-source>" +
					"    <properties> " +
					"      <property name=\"hibernate.hbm2ddl.auto\" value=\"create-drop\"/>" +
					"    </properties>" +
					"    <properties> " +
					"      <property name=\"org.hibernate.envers.audit_strategy\" " + 
                                        "      value=\"org.hibernate.envers.strategy.ValidityAuditStrategy\"/>" +
					"      <property name=\"org.hibernate.envers.revision_type_field_name\" " + 
                                        "      value=\"REVTYPE_CUSTOM\"/>" +
					"    </properties>" +
					"  </persistence-unit>" +
					"</persistence>";


	
    @ArquillianResource
    private static InitialContext iniCtx;
    
    @BeforeClass
    public static void beforeClass() throws NamingException {
        iniCtx = new InitialContext();
    }
	@Deployment
	public static Archive<?> deploy() {
		JavaArchive jar = ShrinkWrap.create( JavaArchive.class, ARCHIVE_NAME + ".jar" );
		jar.addClasses(
				CustomerMO.class,
				PhoneMO.class,
				SLSBAuditMO.class
		);
		jar.add( new StringAsset( persistence_xml ), "META-INF/persistence.xml" );
		return jar;
	}
	
	protected static <T> T lookup(String beanName, Class<T> interfaceType) throws NamingException {
        return interfaceType.cast(iniCtx.lookup("java:global/" + ARCHIVE_NAME + "/" + beanName + "!" + interfaceType.getName()));
    }



        	@Test
		public void testRevisionsforValidityStrategyoverManytoOne() throws Exception {
                
		SLSBAuditMO slsbAudit = lookup("SLSBAuditMO",SLSBAuditMO.class);
        
		CustomerMO c1= slsbAudit.createCustomer("MADHUMITA", "SADHUKHAN", "WORK", "+420","543789654" );
		PhoneMO p1 = c1.getPhones().get(0);
                p1.setType("Emergency");
                p1.setCustomer(c1);
                slsbAudit.updatePhone(p1);
                c1.setFirstname("Madhu");  
                slsbAudit.updateCustomer(c1);
			
		int c = slsbAudit.retrieveOldPhoneListSizeFromCustomer( c1.getId(),1 );
		
		Assert.assertEquals(2, c);
                
		String phoneType= slsbAudit.retrieveOldPhoneListVersionFromCustomer( c1.getId() );

                //check that updating Phone updates audit information fetched from Customer
		Assert.assertEquals("HOME", phoneType);
		

                PhoneMO p3 = slsbAudit.createPhone("WORK", "+420","543789654");
                p3.setCustomer(c1);
                slsbAudit.updatePhone(p3);
                c1.getPhones().add(p3);
                slsbAudit.updateCustomer(c1);

		

                int check = slsbAudit.retrieveOldPhoneListSizeFromCustomer( c1.getId(), 7 );
		Assert.assertEquals(3, check);
                 

                  

					
		}



		/*@Test
		public void testRevisionsfromAuditJoinTable() throws Exception {
                
		SLSBAuditMO slsbAudit = lookup("SLSBAuditMO",SLSBAuditMO.class);
        
		CustomerMO c1= slsbAudit.createCustomer("MADHUMITA", "SADHUKHAN", "WORK", "+420","543789654"  );
                PhoneMO p1 = c1.getPhones().get(1);
                p1.setType("Emergency");
                slsbAudit.updatePhone(p1);
                c1.setFirstname("Madhu");  
                slsbAudit.updateCustomer(c1);
                

                //delete phone
                
                c1.getPhones().remove(p1);
                slsbAudit.updateCustomer(c1);
                slsbAudit.deletePhone(p1);
		Assert.assertEquals(1,c1.getPhones().size());

                //fetch REV	
		List<Object> custHistory = slsbAudit.verifyRevision();

                                                                                 
		Assert.assertNotNull(custHistory);


                //verify size
                Assert.assertEquals(3, custHistory.size());
                

                for ( Object revisionEntity : custHistory) {
			
			Assert.assertNotNull(revisionEntity);
			DefaultRevisionEntity rev = (DefaultRevisionEntity) revisionEntity;
			Assert.assertNotNull(rev);
                        Date revTimestamp = rev.getRevisionDate();
                        Assert.assertNotNull(revTimestamp);
                        System.out.println(revTimestamp);

		}	

              
               
		
      

	}
	
	

	
		@Test
		public void testRevisionTypeandOtherFieldsfromAuditJoinTable() throws Exception {
                
		SLSBAuditMO slsbAudit = lookup("SLSBAuditMO",SLSBAuditMO.class);
        
		CustomerMO c1= slsbAudit.createCustomer("MADHUMITA", "SADHUKHAN", "WORK", "+420","543789654"  );
                PhoneMO p1 = c1.getPhones().get(1);
                p1.setType("Emergency");
                slsbAudit.updatePhone(p1);
                c1.setFirstname("Madhu");  
                slsbAudit.updateCustomer(c1);
                

                //delete phone
                
                c1.getPhones().remove(p1);
                slsbAudit.updateCustomer(c1);
                slsbAudit.deletePhone(p1);
		Assert.assertEquals(1,c1.getPhones().size());


                //fetch REVType
                List<Object> custRevisionType = slsbAudit.verifyRevisionType();
                                                                  
		Assert.assertNotNull(custRevisionType);

               

	
                for ( Object revisionTypeEntity : custRevisionType) {
			
			Assert.assertNotNull(revisionTypeEntity);
			RevisionType revType = (RevisionType) revisionTypeEntity;
			Assert.assertNotNull(revType);
                        System.out.println(revType);
			
			
		}

                List<Object> custRevFields = slsbAudit.verifyOtherFields();
		Assert.assertNotNull(custRevFields);

                
               
		
      

	}*/
	
	
		
}
