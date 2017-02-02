package com.shootr.mobile.ui.model;

import java.io.Serializable;
import java.util.List;

public class ShotModel extends BaseMessageModel implements Comparable<ShotModel>, Serializable {

    private String idShot;
    private List<String> nicers;
    private String streamId;
    private String streamTitle;
    private String replyUsername;
    private String parentShotId;
    private Integer niceCount;
    private Boolean isMarkedAsNice;
    private Long hide;
    private Long replyCount;
    private Long views;
    private Long linkClickCount;
    private Long reshootCount;
    private String type;
    private String ctaCaption;
    private String ctaButtonLink;
    private String ctaButtonText;
    private Long promoted;
    private boolean canBePinned;
    private boolean titleEnabled;
    private boolean isHolderOrContributor;

    public String getIdShot() {
        return idShot;
    }

    public void setIdShot(String idShot) {
        this.idShot = idShot;
    }

    public String getStreamTitle() {
        return streamTitle;
    }

    public void setStreamTitle(String streamTitle) {
        this.streamTitle = streamTitle;
    }

    public String getReplyUsername() {
        return replyUsername;
    }

    public void setReplyUsername(String replyUsername) {
        this.replyUsername = replyUsername;
    }

    public boolean isReply() {
        return replyUsername != null;
    }

    public String getParentShotId() {
        return parentShotId;
    }

    public void setParentShotId(String parentShotId) {
        this.parentShotId = parentShotId;
    }

    public Integer getNiceCount() {
        return niceCount;
    }

    public void setNiceCount(Integer niceCount) {
        this.niceCount = niceCount;
    }

    public Boolean isMarkedAsNice() {
        return isMarkedAsNice;
    }

    public void setIsMarkedAsNice(Boolean isMarkedAsNice) {
        this.isMarkedAsNice = isMarkedAsNice;
    }

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    public Long getHide() {
        return hide;
    }

    public void setHide(Long hide) {
        this.hide = hide;
    }

    public List<String> getNicers() {
        return nicers;
    }

    public void setNicers(List<String> nicers) {
        this.nicers = nicers;
    }

    public Long getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(Long replyCount) {
        this.replyCount = replyCount;
    }

    public Long getViews() {
        return views;
    }

    public void setViews(Long views) {
        this.views = views;
    }

    public Long getLinkClickCount() {
        return linkClickCount;
    }

    public void setLinkClickCount(Long linkClickCount) {
        this.linkClickCount = linkClickCount;
    }

    public Long getReshootCount() {
        return reshootCount;
    }

    public void setReshootCount(Long reshootCount) {
        this.reshootCount = reshootCount;
    }

    public String getCtaCaption() {
        return ctaCaption;
    }

    public void setCtaCaption(String ctaCaption) {
        this.ctaCaption = ctaCaption;
    }

    public String getCtaButtonLink() {
        return ctaButtonLink;
    }

    public void setCtaButtonLink(String ctaButtonLink) {
        this.ctaButtonLink = ctaButtonLink;
    }

    public String getCtaButtonText() {
        return ctaButtonText;
    }

    public void setCtaButtonText(String ctaButtonText) {
        this.ctaButtonText = ctaButtonText;
    }

    public Long getPromoted() {
        return promoted;
    }

    public void setPromoted(Long promoted) {
        this.promoted = promoted;
    }

    public Boolean getCanBePinned() {
        return canBePinned;
    }

    public void setCanBePinned(Boolean canBePinned) {
        this.canBePinned = canBePinned;
    }

    public boolean isTitleEnabled() {
        return titleEnabled;
    }

    public void setTitleEnabled(boolean titleEnabled) {
        this.titleEnabled = titleEnabled;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isHolderOrContributor() {
        return isHolderOrContributor;
    }

    public void setHolderOrContributor(boolean holderOrContributor) {
        isHolderOrContributor = holderOrContributor;
    }

    @Override public int compareTo(ShotModel shotModel) {
        return this.getBirth().getTime() > shotModel.getBirth().getTime() ? 1 : 0;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShotModel)) return false;

        ShotModel shotModel = (ShotModel) o;

        if (idShot != null ? !idShot.equals(shotModel.idShot) : shotModel.idShot != null)
            return false;
        if (nicers != null ? !nicers.equals(shotModel.nicers) : shotModel.nicers != null)
            return false;
        if (streamTitle != null ? !streamTitle.equals(shotModel.streamTitle)
            : shotModel.streamTitle != null) {
            return false;
        }
        if (niceCount != null ? !niceCount.equals(shotModel.niceCount)
            : shotModel.niceCount != null) {
            return false;
        }
        if (isMarkedAsNice != null ? !isMarkedAsNice.equals(shotModel.isMarkedAsNice)
            : shotModel.isMarkedAsNice != null) {
            return false;
        }
        if (hide != null ? !hide.equals(shotModel.hide) : shotModel.hide != null) return false;
        return replyCount != null ? replyCount.equals(shotModel.replyCount)
            : shotModel.replyCount == null;
    }

    @Override public int hashCode() {
        int result = idShot != null ? idShot.hashCode() : 0;
        result = 31 * result + (nicers != null ? nicers.hashCode() : 0);
        result = 31 * result + (streamTitle != null ? streamTitle.hashCode() : 0);
        result = 31 * result + (niceCount != null ? niceCount.hashCode() : 0);
        result = 31 * result + (isMarkedAsNice != null ? isMarkedAsNice.hashCode() : 0);
        result = 31 * result + (hide != null ? hide.hashCode() : 0);
        result = 31 * result + (replyCount != null ? replyCount.hashCode() : 0);
        return result;
    }
}
