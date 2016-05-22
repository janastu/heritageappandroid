package org.janastu.heritageapp.geoheritagev2.client.pojo;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by DESKTOP on 4/9/2016.
 */
public class HeritageAppDTO {

    int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String name;



    private String contact;


    private Set<HeritageRegionNameDTO> regions = new HashSet<HeritageRegionNameDTO>();

    private Set<HeritageGroup > groups = new HashSet<HeritageGroup>();

    private Set<HeritageLanguage > languages = new HashSet<HeritageLanguage>();

    private Set<HeritageCategory > categorys = new HashSet<HeritageCategory>();

    public HeritageAppDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public Set<HeritageRegionNameDTO> getRegions() {
        return regions;
    }

    public void setRegions(Set<HeritageRegionNameDTO> regions) {
        this.regions = regions;
    }

    public Set<HeritageGroup> getGroups() {
        return groups;
    }

    public void setGroups(Set<HeritageGroup> groups) {
        this.groups = groups;
    }

    public Set<HeritageLanguage> getLanguages() {
        return languages;
    }

    public void setLanguages(Set<HeritageLanguage> languages) {
        this.languages = languages;
    }

    public Set<HeritageCategory> getCategorys() {
        return categorys;
    }

    public void setCategorys(Set<HeritageCategory> categorys) {
        this.categorys = categorys;
    }

    @Override
    public String toString() {
        return "HeritageAppDTO{" +
                "name='" + name + '\'' +
                ", contact='" + contact + '\'' +
                ", regions=" + regions +
                ", groups=" + groups +
                ", languages=" + languages +
                ", categorys=" + categorys +
                '}';
    }
}
