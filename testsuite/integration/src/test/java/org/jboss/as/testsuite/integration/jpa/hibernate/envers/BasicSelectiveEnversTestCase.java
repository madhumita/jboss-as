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

import java.sql.Connection;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

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
public class BasicSelectiveEnversTestCase {
	private static final String ARCHIVE_NAME = "jpa_BasicSelectiveEnversTestCase";

	private static final String persistence_xml =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?> " +
					"<persistence xmlns=\"http://java.sun.com/xml/ns/persistence\" version=\"1.0\">" +
					"  <persistence-unit name=\"myOrg\">" +
					"    <description>Persistence Unit." +
					"    </description>" +
					"    <jta-data-source>java:jboss/datasources/ExampleDS</jta-data-source>" +
					"    <properties> " +
					"      <property name=\"hibernate.hbm2ddl.auto\" value=\"create-drop\"/>" +
					"    </properties>" +
					"  </persistence-unit>" +
					"</persistence>";
	/*private static final String persistence_xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?> " +
            "<persistence xmlns=\"http://java.sun.com/xml/ns/persistence\" version=\"1.0\">" +
            "  <persistence-unit name=\"myOrg\">" +
            "    <description>Persistence Unit." +
            "    </description>" +
            "  <jta-data-source>java:jboss/datasources/ExampleDS</jta-data-source>" +
            "<properties> <property name=\"hibernate.hbm2ddl.auto\" value=\"create-drop\"/>" +
            //"<property name=\"hibernate.show_sql\" value=\"true\"/>" +
            //"<property name=\"jboss.as.jpa.providerModule\" value=\"hibernate3-bundled\"/>" +
            //"<property name=\"hibernate.ejb.event.post-insert\" value=\"org.hibernate.ejb.event.EJB3PostInsertEventListener,org.hibernate.envers.event.AuditEventListener\"/>"+
            //"<property name=\"hibernate.ejb.event.post-update\" value=\"org.hibernate.ejb.event.EJB3PostUpdateEventListener,org.hibernate.envers.event.AuditEventListener\"/>"+
            //"<property name=\"hibernate.ejb.event.post-delete\" value=\"org.hibernate.ejb.event.EJB3PostDeleteEventListener,org.hibernate.envers.event.AuditEventListener\"/>"+
            "<property name=\"hibernate.ejb.event.pre-collection-update\" value=\"org.hibernate.envers.event.AuditEventListener\"/>"+
            "<property name=\"hibernate.ejb.event.pre-collection-remove\" value=\"org.hibernate.envers.event.AuditEventListener\"/>"+
            "<property name=\"hibernate.ejb.event.post-collection-recreate\" value=\"org.hibernate.envers.event.AuditEventListener\"/>"+
            "</properties>" +
            "  </persistence-unit>" +
            "</persistence>";*/

	
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
				Organization.class,
				SLSBOrg.class
		);
		jar.add( new StringAsset( persistence_xml ), "META-INF/persistence.xml" );
		return jar;
	}
	
	protected static <T> T lookup(String beanName, Class<T> interfaceType) throws NamingException {
        return interfaceType.cast(iniCtx.lookup("java:global/" + ARCHIVE_NAME + "/" + beanName + "!" + interfaceType.getName()));
    }

		
	@Test
	public void testSimpleSelectiveEnversOperation() throws Exception {
        SLSBOrg slsbOrg = lookup("SLSBOrg",SLSBOrg.class);
		Organization o1= slsbOrg.createOrg("REDHAT", "Software Co", "10/10/1994", "eternity","raleigh" );
		Organization o2= slsbOrg.createOrg("HALDIRAMS", "Food Co", "10/10/1974", "eternity","India" );
		
		o2.setStartDate("10/10/1924");
		
		
		slsbOrg.updateOrg( o2 );
		
		Organization ret1 = slsbOrg.retrieveOldOrgbyId( o2.getId() );
		Assert.assertNotNull(ret1);
                Organization ret2= slsbOrg.retrieveOldOrgbyName( "name",o2.getId() ); 
		Assert.assertNotNull(ret2); 

	}
	
	
	
	
}
