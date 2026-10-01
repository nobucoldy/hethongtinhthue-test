package com.pit;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionBindingListener;

import java.util.*;

public class MockHttpSession implements HttpSession {

    private final Map<String, Object> attributes = new HashMap<>();
    private boolean invalid = false;
    private final String id = UUID.randomUUID().toString();
    private final long creationTime = System.currentTimeMillis();

    @Override
    public long getCreationTime() { return creationTime; }

    @Override
    public String getId() { return id; }

    @Override
    public long getLastAccessedTime() { return creationTime; }

    @Override
    public ServletContext getServletContext() { return null; }

    @Override
    public void setMaxInactiveInterval(int interval) {}

    @Override
    public int getMaxInactiveInterval() { return 1800; }

    @Override
    public Object getAttribute(String name) {
        if (invalid) throw new IllegalStateException("Session is invalidated");
        return attributes.get(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(attributes.keySet());
    }

    @Override
    public void setAttribute(String name, Object value) {
        if (invalid) throw new IllegalStateException("Session is invalidated");
        if (value == null) {
            removeAttribute(name);
        } else {
            attributes.put(name, value);
        }
    }

    @Override
    public void removeAttribute(String name) {
        if (invalid) throw new IllegalStateException("Session is invalidated");
        attributes.remove(name);
    }

    @Override
    public void invalidate() {
        this.invalid = true;
        this.attributes.clear();
    }

    @Override
    public boolean isNew() { return false; }

    public boolean isInvalid() { return invalid; }
}
