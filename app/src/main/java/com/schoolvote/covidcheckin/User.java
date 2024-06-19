package com.schoolvote.covidcheckin;

import java.io.Serializable;

public class User implements Serializable {

    private String id;
    private String telnum;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTelnum() {
        return telnum;
    }

    public void setTelnum(String telnum) {
        this.telnum = telnum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return this.id + ", " + this.telnum + ", " + this.name;
    }
}
