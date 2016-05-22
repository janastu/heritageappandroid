package org.janastu.heritageapp.geoheritagev2.client;

/**
 * Created by Graphics-User on 1/19/2016.
 */



import java.io.Serializable;
import java.util.Objects;


/**
 * A DTO for the ImageGeoTagHeritageEntity entity.
 */
public class ImageGeoTagHeritageEntityDTO implements Serializable {

    private Long id;


    private String title;

    private String description;

    private String address;


    private Double latitude;


    private Double longitude;

    private String consolidatedTags;


    private byte[] photo;

    private String photoContentType;

    private Long heritageCategoryId;

    private String heritageCategoryCategoryName;

    private Long heritageLanguageId;

    private String heritageLanguageHeritageLanguage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getConsolidatedTags() {
        return consolidatedTags;
    }

    public void setConsolidatedTags(String consolidatedTags) {
        this.consolidatedTags = consolidatedTags;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public String getPhotoContentType() {
        return photoContentType;
    }

    public void setPhotoContentType(String photoContentType) {
        this.photoContentType = photoContentType;
    }

    public Long getHeritageCategoryId() {
        return heritageCategoryId;
    }

    public void setHeritageCategoryId(Long heritageCategoryId) {
        this.heritageCategoryId = heritageCategoryId;
    }

    public String getHeritageCategoryCategoryName() {
        return heritageCategoryCategoryName;
    }

    public void setHeritageCategoryCategoryName(String heritageCategoryCategoryName) {
        this.heritageCategoryCategoryName = heritageCategoryCategoryName;
    }

    public Long getHeritageLanguageId() {
        return heritageLanguageId;
    }

    public void setHeritageLanguageId(Long heritageLanguageId) {
        this.heritageLanguageId = heritageLanguageId;
    }

    public String getHeritageLanguageHeritageLanguage() {
        return heritageLanguageHeritageLanguage;
    }

    public void setHeritageLanguageHeritageLanguage(String heritageLanguageHeritageLanguage) {
        this.heritageLanguageHeritageLanguage = heritageLanguageHeritageLanguage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ImageGeoTagHeritageEntityDTO imageGeoTagHeritageEntityDTO = (ImageGeoTagHeritageEntityDTO) o;

        if ( ! Objects.equals(id, imageGeoTagHeritageEntityDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ImageGeoTagHeritageEntityDTO{" +
                "id=" + id +
                ", title='" + title + "'" +
                ", description='" + description + "'" +
                ", address='" + address + "'" +
                ", latitude='" + latitude + "'" +
                ", longitude='" + longitude + "'" +
                ", consolidatedTags='" + consolidatedTags + "'" +
                ", photo='" +   "'" +
                '}';
    }
}
