package org.janastu.heritageapp.geoheritagev2.client.pojo;


import org.geojson.LngLatAlt;

public class OSMMarkerInfo {

    LngLatAlt latLngOSM;

    String title;
    String description;
    String url;
    String markerColor;
    String markerSize;
    String mediaType;
    String markerSymbol;

    String category;
    String languauge;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLanguauge() {
        return languauge;
    }

    public void setLanguauge(String languauge) {
        this.languauge = languauge;
    }

    public OSMMarkerInfo() {
    }

    public LngLatAlt getLatLngOSM() {
        return latLngOSM;
    }

    public void setLatLngOSM(LngLatAlt latLngOSM) {
        this.latLngOSM = latLngOSM;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMarkerColor() {
        return markerColor;
    }

    public void setMarkerColor(String markerColor) {
        this.markerColor = markerColor;
    }

    public String getMarkerSize() {
        return markerSize;
    }

    public void setMarkerSize(String markerSize) {
        this.markerSize = markerSize;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getMarkerSymbol() {
        return markerSymbol;
    }

    public void setMarkerSymbol(String markerSymbol) {
        this.markerSymbol = markerSymbol;
    }

    @Override
    public String toString() {
        return "OSMMarkerInfo{" +
                "latLngOSM=" + latLngOSM +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                ", markerColor='" + markerColor + '\'' +
                ", markerSize='" + markerSize + '\'' +
                ", mediaType='" + mediaType + '\'' +
                ", markerSymbol='" + markerSymbol + '\'' +
                ", category='" + category + '\'' +
                ", languauge='" + languauge + '\'' +
                '}';
    }
}
