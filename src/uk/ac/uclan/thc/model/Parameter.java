package uk.ac.uclan.thc.model;

import java.io.Serializable;

public class Parameter implements Serializable {

    private String uuid;
    private String key;
    private String value;

    public Parameter(String uuid, String key, String value) {
        this.uuid = uuid;
        this.key = key;
        this.value = value;
    }

    public String getUuid() {
        return uuid;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}