package main.java.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import main.java.core.MySession;

public class SessionManager {
    private Map<String, MySession> activeSessions = new ConcurrentHashMap<>();

    public MySession getSession(String sessionId) {
        if (sessionId == null) return null;
        
        MySession session = activeSessions.get(sessionId);
        if (session != null) {
            if (session.isExpired()) {
                session.destroy();
                activeSessions.remove(sessionId);
                return null;
            }
        }
        return session;
    }

    public MySession createSession(String sessionId) {
        MySession newSession = new MySession(sessionId, new HashMap<>());
        activeSessions.put(sessionId, newSession);
        return newSession;
    }

    public void removeSession(String sessionId) {
        activeSessions.remove(sessionId);
    }
}
