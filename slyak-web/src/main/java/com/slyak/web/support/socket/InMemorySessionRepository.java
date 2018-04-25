package com.slyak.web.support.socket;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Map;

/**
 * .
 *
 * @author stormning 2018/4/25
 * @since 1.3.0
 */
public class InMemorySessionRepository implements CrudRepository<Session, String> {

    private Map<String, Session> sessionMap = Maps.newConcurrentMap();


    @Override
    public <S extends Session> S save(S session) {
        sessionMap.put(session.getId(), session);
        return session;
    }

    @Override
    public <S extends Session> Iterable<S> save(Iterable<S> iterable) {
        for (S s : iterable) {
            save(s);
        }
        return iterable;
    }

    @Override
    public Session findOne(String s) {
        return sessionMap.get(s);
    }

    @Override
    public boolean exists(String s) {
        return sessionMap.containsKey(s);
    }

    @Override
    public Iterable<Session> findAll() {
        return sessionMap.values();
    }

    @Override
    public Iterable<Session> findAll(Iterable<String> iterable) {
        List<Session> sessions = Lists.newArrayList();
        for (String s : iterable) {
            Session one = findOne(s);
            if (one != null) {
                sessions.add(one);
            }
        }
        return sessions;
    }

    @Override
    public long count() {
        return sessionMap.size();
    }

    @Override
    public void delete(String s) {
        sessionMap.remove(s);
    }

    @Override
    public void delete(Session session) {
        delete(session.getId());
    }

    @Override
    public void delete(Iterable<? extends Session> iterable) {
        for (Session session : iterable) {
            delete(session);
        }
    }

    @Override
    public void deleteAll() {
        sessionMap.clear();
    }
}
