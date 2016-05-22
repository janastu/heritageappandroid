package org.janastu.heritageapp.geoheritagev2.client.pojo;

/**
 * Created by HeritageRegionNameDTO;
 *
 */
public class HeritageRegionNameDTO {

    private Long id;
    private String name;
    private Double centerLatitude;
    private Double centerLongitude;
    private Double topLatitude;
    private Double topLongitude;
    private Double bottomLatitude;
    private Double bottomLongitude;

    public HeritageRegionNameDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getCenterLatitude() {
        return centerLatitude;
    }

    public void setCenterLatitude(Double centerLatitude) {
        this.centerLatitude = centerLatitude;
    }

    public Double getCenterLongitude() {
        return centerLongitude;
    }

    public void setCenterLongitude(Double centerLongitude) {
        this.centerLongitude = centerLongitude;
    }

    public Double getTopLatitude() {
        return topLatitude;
    }

    public void setTopLatitude(Double topLatitude) {
        this.topLatitude = topLatitude;
    }

    public Double getTopLongitude() {
        return topLongitude;
    }

    public void setTopLongitude(Double topLongitude) {
        this.topLongitude = topLongitude;
    }

    public Double getBottomLatitude() {
        return bottomLatitude;
    }

    public void setBottomLatitude(Double bottomLatitude) {
        this.bottomLatitude = bottomLatitude;
    }

    public Double getBottomLongitude() {
        return bottomLongitude;
    }

    public void setBottomLongitude(Double bottomLongitude) {
        this.bottomLongitude = bottomLongitude;
    }

    @Override
    public String toString() {
        return "HeritageRegionNameDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", centerLatitude=" + centerLatitude +
                ", centerLongitude=" + centerLongitude +
                ", topLatitude=" + topLatitude +
                ", topLongitude=" + topLongitude +
                ", bottomLatitude=" + bottomLatitude +
                ", bottomLongitude=" + bottomLongitude +
                '}';
    }
}
