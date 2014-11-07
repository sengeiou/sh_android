package com.shootr.android.ui.model;


public class WatchingRequestModel {

    private Long matchId;
    private String title;
    private String subtitle;
    private Long matchDate;

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String matchTitle) {
        this.title = matchTitle;
    }

    public Long getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(Long matchDate) {
        this.matchDate = matchDate;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WatchingRequestModel that = (WatchingRequestModel) o;
        return matchId.equals(that.matchId);
    }

    @Override
    public int hashCode() {
        return matchId.hashCode();
    }
}
