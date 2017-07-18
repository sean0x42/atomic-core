package net.atomichive.core.entity;

import java.util.Map;

/**
 * Entity Config
 * Keeps track of various configuration options
 * for entities.
 * TODO Generalise this?
 */
public class EntityAttributes {

    private Map<String, Object> attributes;

    public EntityAttributes (Map<String, Object> attributes) {
        this.attributes = attributes;
    }


    /**
     * Get boolean
     * @param key Config key.
     * @param defaultValue Default value, if not defined or an error occurs.
     * @return Boolean from map.
     */
    public boolean getBoolean (String key, boolean defaultValue) {

        if (attributes == null) return defaultValue;

        Object obj = attributes.getOrDefault(key, defaultValue);

        if (Boolean.class.isInstance(obj))
            return (boolean) obj;

        return defaultValue;

    }


    /**
     * Get string
     * @param key Config key.
     * @param defaultValue Default value, if not defined or an error occurs.
     * @return String from map.
     */
    public String getString (String key, String defaultValue) {

        if (attributes == null) return defaultValue;

        Object obj = attributes.getOrDefault(key, defaultValue);

        if (obj instanceof String)
            return (String) obj;

        return defaultValue;

    }


    /**
     * Get int
     * @param key Config key
     * @param defaultValue Default value, if not defined or an error occurs.
     * @return Int from map
     */
    public int getInt (String key, int defaultValue) {

        if (attributes == null) return defaultValue;

        Object obj = attributes.getOrDefault(key, defaultValue);

        if (Integer.class.isInstance(obj))
            return (int) obj;

        return defaultValue;

    }


    /**
     * Get double
     * @param key Config key.
     * @param defaultValue Default value, if not defined or an error occurs.
     * @return Double from map.
     */
    public double getDouble (String key, double defaultValue) {

        if (attributes == null) return defaultValue;

        Object obj = attributes.getOrDefault(key, defaultValue);

        if (Double.class.isInstance(obj))
            return (double) obj;

        return defaultValue;

    }

}