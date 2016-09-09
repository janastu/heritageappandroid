package org.janastu.heritageapp.geoheritagev2.client.pojo;

/**
 * Created by Graphics-User on 1/22/2016.
 */
import android.util.Log;
import android.widget.ProgressBar;


public class DownloadInfo {
    private final static String TAG = DownloadInfo.class.getSimpleName();
        Integer id=0;


    public enum DownloadState {
        NOT_STARTED,
        QUEUED,
        DOWNLOADING,
        FAILED,
        COMPLETE
    }
    private volatile DownloadState mDownloadState = DownloadState.NOT_STARTED;

    private volatile Integer mProgress;

    private volatile ProgressBar mProgressBar;

    String title,  description,  address;
    String latitude, longitude;
    String consolidatedTags,   urlOrfileLink,
      heritageCategory,   heritageLanguage, heritageGroup;
    String mediaType;
    private   String mFilename;
    private   Integer mFileSize;

    public String getHeritageGroup() {
        return heritageGroup;
    }

    public void setHeritageGroup(String heritageGroup) {
        this.heritageGroup = heritageGroup;
    }

    Boolean fileUploadstatus;

    public Boolean getFileUploadstatus() {
        return fileUploadstatus;
    }

    public void setFileUploadstatus(Boolean fileUploadstatus) {
        this.fileUploadstatus = fileUploadstatus;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getConsolidatedTags() {
        return consolidatedTags;
    }

    public void setConsolidatedTags(String consolidatedTags) {
        this.consolidatedTags = consolidatedTags;
    }

    public String getUrlOrfileLink() {
        return urlOrfileLink;
    }

    public void setUrlOrfileLink(String urlOrfileLink) {
        this.urlOrfileLink = urlOrfileLink;
        mFilename = urlOrfileLink;
    }

    public String getHeritageCategory() {
        return heritageCategory;
    }

    public void setHeritageCategory(String heritageCategory) {
        this.heritageCategory = heritageCategory;
    }

    public String getHeritageLanguage() {
        return heritageLanguage;
    }

    public void setHeritageLanguage(String heritageLanguage) {
        this.heritageLanguage = heritageLanguage;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public DownloadInfo(){}
    public DownloadInfo(Integer id, String title, String description, String latitude, String longitude ,String urlOrfileLink , String mediaType , Boolean fileUploadstatus) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.consolidatedTags = consolidatedTags;
        this.urlOrfileLink = urlOrfileLink;
        this.heritageCategory = heritageCategory;
        this.heritageLanguage = heritageLanguage;
        this.mediaType = mediaType;
        this.mFilename = mFilename;
        this.fileUploadstatus = fileUploadstatus;
    }

    public ProgressBar getProgressBar() {
        return mProgressBar;
    }
    public void setProgressBar(ProgressBar progressBar) {
        Log.d(TAG, "setProgressBar " + mFilename + " to " + progressBar);
        mProgressBar = progressBar;
    }

    public void setDownloadState(DownloadState state) {
        mDownloadState = state;
    }
    public DownloadState getDownloadState() {
        return mDownloadState;
    }

    public Integer getProgress() {
        return mProgress;
    }

    public void setProgress(Integer progress) {
        this.mProgress = progress;
    }

    public Integer getFileSize() {
        return mFileSize;
    }

    public void setmFileSize(Integer mFileSize) {
        this.mFileSize = mFileSize;
    }

    public String getFilename() {
        return mFilename;
    }

    @Override
    public String toString() {
        return "DownloadInfo{" +
                "id=" + id +
                ", mDownloadState=" + mDownloadState +
                ", mProgress=" + mProgress +
                ", mProgressBar=" + mProgressBar +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", address='" + address + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", consolidatedTags='" + consolidatedTags + '\'' +
                ", urlOrfileLink='" + urlOrfileLink + '\'' +
                ", heritageCategory='" + heritageCategory + '\'' +
                ", heritageLanguage='" + heritageLanguage + '\'' +
                ", heritageGroup='" + heritageGroup + '\'' +
                ", mediaType='" + mediaType + '\'' +
                ", mFilename='" + mFilename + '\'' +
                ", mFileSize=" + mFileSize +
                ", fileUploadstatus=" + fileUploadstatus +
                '}';
    }
}
