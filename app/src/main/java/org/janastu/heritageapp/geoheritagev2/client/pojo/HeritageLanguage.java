package org.janastu.heritageapp.geoheritagev2.client.pojo;


import java.io.Serializable;

/**
 * Created by Graphics-User on 1/25/2016.
 */
public class HeritageLanguage  implements Serializable{




    private Integer id;

    private String heritageLanguage;



    public HeritageLanguage() {
    }


    public Integer getId() {
        return id;
    }


    public void setId(Integer id) {
        this.id = id;
    }

    public String getHeritageLanguage() {
        return heritageLanguage;
    }

    public void setHeritageLanguage(String heritageLanguage) {
        this.heritageLanguage = heritageLanguage;
    }

    @Override
    public String toString() {
        return "HeritageLanguage{" +
                "id=" + id +
                ", heritageLanguage='" + heritageLanguage + '\'' +
                '}';
    }
}
