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
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.DefaultRevisionEntity;

/**
 * @author Madhumita Sadhukhan
 */
@Stateless
public class SLSBAuditInheritance {
	
	@PersistenceContext(unitName = "myPlayer")
	EntityManager em;

	public SoccerPlayer createSoccerPlayer(String firstName, String lastName, String game,String clubName) {
		
		

		SoccerPlayer socplayer = new SoccerPlayer();
		socplayer.setFirstName(firstName);
		socplayer.setLastName(lastName);
		socplayer.setGame(game);
		socplayer.setClubName(clubName);
        	em.persist( socplayer );
		return socplayer;
	}


        

	public SoccerPlayer updateSoccerPlayer(SoccerPlayer p,String clubName) {
		
		
               
		p.setClubName(clubName);
        	
                em.merge( p );
		return p;
	}
	     
	       
	public List retrieveSoccerPlayerbyId(int id) {

		AuditReader reader = AuditReaderFactory.get( em );
		List val = reader.getRevisions( SoccerPlayer.class,id);
		return val;
	}
	
	

}
