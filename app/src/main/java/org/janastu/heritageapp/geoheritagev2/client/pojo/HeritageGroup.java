package org.janastu.heritageapp.geoheritagev2.client.pojo;


import java.io.Serializable;

public class HeritageGroup implements Serializable{

    private Integer id;

    private String name;

    private String icon;

    private String iconContentType;

    private String details;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIconContentType() {
        return iconContentType;
    }

    public void setIconContentType(String iconContentType) {
        this.iconContentType = iconContentType;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "HeritageGroup{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", icon='" + icon + '\'' +
                ", iconContentType='" + iconContentType + '\'' +
                ", details='" + details + '\'' +
                '}';
    }
}
