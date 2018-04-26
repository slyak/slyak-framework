package com.slyak.web.support.socket;

import lombok.Setter;
import org.springframework.context.ApplicationListener;
import org.springframework.data.repository.CrudRepository;
import org.springframework.messaging.simp.SimpAttributes;
import org.springframework.messaging.simp.SimpAttributesContextHolder;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

/**
 * .
 *
 * @author stormning 2018/4/25
 * @since 1.3.0
 */
public class SessionConnectedEventListener implements ApplicationListener<SessionConnectedEvent> {

    @Setter
    private CrudRepository<Session, String> sessionRepository = new InMemorySessionRepository();

    @Override
    public void onApplicationEvent(SessionConnectedEvent event) {
        Session session = new Session();
        SimpAttributes attributes = SimpAttributesContextHolder.getAttributes();
        session.setId(attributes.getSessionId());
        session.setUser(event.getUser());
        sessionRepository.save(session);
    }
}