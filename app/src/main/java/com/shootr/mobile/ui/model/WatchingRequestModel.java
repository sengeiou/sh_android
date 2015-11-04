package com.shootr.mobile.ui.model;


public class WatchingRequestModel {

    private String streamId;
    private String title;
    private String subtitle;
    private Long streamDate;

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String streamTitle) {
        this.title = streamTitle;
    }

    public Long getStreamDate() {
        return streamDate;
    }

    public void setStreamDate(Long streamDate) {
        this.streamDate = streamDate;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }

        if (o == null || getClass() != o.getClass()){
            return false;
        }
        WatchingRequestModel that = (WatchingRequestModel) o;
        return streamId.equals(that.streamId);
    }

    @Override
    public int hashCode() {
        return streamId.hashCode();
    }
}
