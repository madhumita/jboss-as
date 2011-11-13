/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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
package org.jboss.as.testsuite.integration.beanvalidation.hibernate.weblayerValidation;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * Tests that hibernate validator works correctly for WAR(Web Applications)
 * 
 * 
 * @author Madhumita Sadhukhan
 */
@RunWith(Arquillian.class)
public class GroupandGroupSequenceValidationTestCase {

    @Deployment
    public static Archive<?> deploy() {
        final WebArchive war = ShrinkWrap.create(WebArchive.class, "testgroupvalidation.war");
        war.addPackage(GroupandGroupSequenceValidationTestCase.class.getPackage());
        return war;

    }

    @Test
    public void testGroupValidation() throws NamingException, SQLException {
        Validator validator = (Validator) new InitialContext().lookup("java:comp/Validator");

        // create first passenger
        UserBean user1 = new UserBean("MADHUMITA", "SADHUKHAN");
        user1.setEmail("madhu@gmail.com");
        user1.setAddress("REDHAT Brno");

        // create second passenger
        UserBean user2 = new UserBean("Mickey", "Mouse");
        user2.setEmail("mickey@gmail.com");
        user2.setAddress("DISNEY CA USA");

        List<UserBean> passengers = new ArrayList<UserBean>();
        passengers.add(user1);
        passengers.add(user2);

        // Create a Car
        Car car = new Car("CET5678", passengers);
        car.setModel("SKODA Octavia");

        Set<ConstraintViolation<Car>> result = validator.validate(car);
        // Assert.assertEquals(0, result.size());

        // result = validator.validate(car , CarChecks.class);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("The Car has to pass the fuel test and inspection test before being driven", result.iterator()
                .next().getMessage());

        Driver driver = new Driver("Sebastian", "Vettel");
        driver.setAge(15);
        driver.setAddress("ABC");

        car.setDriver(driver);
        result = validator.validate(car);
        // Assert.assertEquals( 2, result.size() );
        Assert.assertEquals("The Car has to pass the fuel test and inspection test before being driven", result.iterator()
                .next().getMessage());
        System.out.println("Assertion failure message before vehicle inspection:--" + result.iterator().next().getMessage());
        car.setPassedVehicleInspection(true);
        // Assert.assertEquals( 2, validator.validate(car).size() );
        // driver.hasValidDrivingLicense(true);
        // car.setDriver(driver);
        result = validator.validate(car);
        Assert.assertEquals(2, result.size());

        // System.out.println("Assertion failure message:--" + result.iterator().next().getMessage());

        Iterator it1 = result.iterator();
        while (it1.hasNext()) {
            ConstraintViolation<Car> cts = (ConstraintViolation<Car>) it1.next();
            System.out.println("Testing :--" + cts.getMessage());

        }

    }
}
