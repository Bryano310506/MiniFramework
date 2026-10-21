package main.java.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.servlet.http.HttpSession;

public class MySession {
    private String sessionId;
    private long createdTime;
    private long limitedTime;
    private Map<String, Object> attributes;
    private boolean isValid = true;

    public MySession(String sessionId, long limitedTime, Map<String, Object> attributes) {
        this.sessionId = sessionId;
        this.createdTime = System.currentTimeMillis();
        this.limitedTime = limitedTime;
        this.attributes = attributes;
    }

    public MySession(String sessionId, Map<String, Object> attributes) {
        this.sessionId = sessionId;
        this.createdTime = System.currentTimeMillis();
        this.limitedTime = 1800;
        this.attributes = attributes;
    }

    public MySession(String sessionId) {
        this.sessionId = sessionId;
        this.createdTime = System.currentTimeMillis();
        this.limitedTime = 1800;
        this.attributes = new HashMap<>();
    }

    public MySession(HttpSession session) {
        this.sessionId = session.getId();
        this.createdTime = System.currentTimeMillis();
        this.limitedTime = 1800;
        this.attributes = new HashMap<>();
        getAttributFormHttpSession(session);
    }

    public String getSessionId() {
        return sessionId;
    }
    public long getCreatedTime() {
        return createdTime;
    }
    public long getLimitedTime() {
        return limitedTime;
    }
    public boolean isValid() {
        return isValid;
    }

    // public Map<String, Object> getAttributes() {
    //     return attributes;
    // }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }
    public void setLimitedTime(long limitedTime) {
        this.limitedTime = limitedTime;
    }
    // public void setAttributes(Map<String, Object> attributes) {
    //     this.attributes = attributes;
    // }

    public void destroy() {
        this.isValid = false;
        if (this.attributes != null) {
            this.attributes.clear(); 
        }
        this.sessionId = null;
    }

    public boolean isExpired() {
        long maintenant = System.currentTimeMillis();
        return (maintenant - createdTime) > limitedTime;
    }

    public void addAttribute(String key, Object value) {
        this.attributes.put(key, value);
    }

    public void removeAttribute(String key) {
        this.attributes.remove(key);
    }

    public Object getAttribute(String key) {
        return this.attributes.get(key);
    }

    public List<String> getAllAttributeNames() {
        List<String> list = new ArrayList<>();
        if(!this.isExpired()) {
            Set<String> keys = this.attributes.keySet();
            for(String s : keys) {
                list.add(s);
            }
        }
        return list;
    } 

    public void getAttributFormHttpSession(HttpSession session) {
        Enumeration<String> atttributeNames = session.getAttributeNames();
        List<String> list = Collections.list(atttributeNames);
        for(String attr : list) {
            Object value = session.getAttribute(attr);
            this.addAttribute(attr, value);
        }
    }

}
