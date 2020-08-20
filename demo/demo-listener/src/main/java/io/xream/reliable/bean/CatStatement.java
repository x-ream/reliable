package io.xream.reliable.bean;


import io.xream.sqli.annotation.X;

public class CatStatement {

    @X.Key
    private String id;
    private String test;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }
}
