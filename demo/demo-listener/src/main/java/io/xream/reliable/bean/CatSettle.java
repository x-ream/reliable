package io.xream.reliable.bean;

import x7.core.repository.X;

public class CatSettle {

    @X.Key
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "CatSettle{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
