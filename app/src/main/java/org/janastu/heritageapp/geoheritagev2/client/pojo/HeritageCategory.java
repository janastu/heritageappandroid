package org.janastu.heritageapp.geoheritagev2.client.pojo;


import java.io.Serializable;

public class HeritageCategory  implements Serializable{

    private Integer id;

    private String categoryName;

    private String categoryIcon;

    private String categoryIconContentType;

    private String categoryDecription;

    public HeritageCategory() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryIcon() {
        return categoryIcon;
    }

    public void setCategoryIcon(String categoryIcon) {
        this.categoryIcon = categoryIcon;
    }

    public String getCategoryIconContentType() {
        return categoryIconContentType;
    }

    public void setCategoryIconContentType(String categoryIconContentType) {
        this.categoryIconContentType = categoryIconContentType;
    }

    public String getCategoryDecription() {
        return categoryDecription;
    }

    public void setCategoryDecription(String categoryDecription) {
        this.categoryDecription = categoryDecription;
    }

    @Override
    public String toString() {
        return "HeritageCategory{" +
                "id=" + id +
                ", categoryName='" + categoryName + '\'' +
                ", categoryIcon='" + categoryIcon + '\'' +
                ", categoryIconContentType='" + categoryIconContentType + '\'' +
                ", categoryDecription='" + categoryDecription + '\'' +
                '}';
    }
}
