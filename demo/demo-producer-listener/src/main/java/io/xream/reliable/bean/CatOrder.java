package io.xream.reliable.bean;


import io.xream.sqli.annotation.X;

/**
 * @Author Sim
 */
public class CatOrder {

    @X.Key
    private String id;
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "CatOrder{" +
                "id='" + id + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
