package main.java.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import main.java.core.MySession;

public class SessionManager {
    private final Map<String, MySession> activeSessions = new ConcurrentHashMap<>();

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
        if (sessionId == null) return null;

        return activeSessions.compute(sessionId, (id, currentSession) -> {
            if (currentSession != null && !currentSession.isExpired()) {
                return currentSession;
            }

            if (currentSession != null) {
                currentSession.destroy();
            }
            return new MySession(id);
        });
    }

    public void removeSession(String sessionId) {
        activeSessions.remove(sessionId);
    }
}
