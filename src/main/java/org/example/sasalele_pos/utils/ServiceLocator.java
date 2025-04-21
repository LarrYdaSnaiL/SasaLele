package org.example.sasalele_pos.utils;

import java.util.HashMap;
import java.util.Map;

public class ServiceLocator {
    private static final Map<String, Object> services = new HashMap<>();

    public static void register(String name, Object service) {
        services.put(name, service);
    }

    public static <T> T get(String name) {
        return (T) services.get(name);
    }
}