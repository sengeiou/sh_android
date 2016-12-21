package com.shootr.mobile.data.api.entity;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ShotApiEntity extends BaseMessageApiEntity {

    private String idShot;
    private String idStream;
    private String streamTitle;

    private Integer niceCount;

    private String type;

    private String idShotParent;
    private String idUserParent;
    private String userNameParent;

    private Long linkClicks;
    private Long views;

    private EmbedUserApiEntity user;
    private List<ShotApiEntity> replies;
    private ShotApiEntity parent;

    private List<ShotApiEntity> thread;
    private Long birth;
    private Long modified;
    private Integer revision;
    private Long profileHidden;
    private Long replyCount;
    private Long reshootCount;
    private Long promoted;
    @SerializedName("CTAButtonLink")
    private String ctaButtonLink;
    @SerializedName("CTAButtonText")
    private String ctaButtonText;
    @SerializedName("CTACaption")
    private String ctaCaption;
    private Long verifiedUser;

    public String getIdShot() {
        return idShot;
    }

    public void setIdShot(String idShot) {
        this.idShot = idShot;
    }

    public String getIdStream() {
        return idStream;
    }

    public void setIdStream(String idStream) {
        this.idStream = idStream;
    }

    public String getStreamTitle() {
        return streamTitle;
    }

    public void setStreamTitle(String streamTitle) {
        this.streamTitle = streamTitle;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIdShotParent() {
        return idShotParent;
    }

    public void setIdShotParent(String idShotParent) {
        this.idShotParent = idShotParent;
    }

    public String getIdUserParent() {
        return idUserParent;
    }

    public void setIdUserParent(String idUserParent) {
        this.idUserParent = idUserParent;
    }

    public String getUserNameParent() {
        return userNameParent;
    }

    public void setUserNameParent(String userNameParent) {
        this.userNameParent = userNameParent;
    }

    public EmbedUserApiEntity getUser() {
        return user;
    }

    public void setUser(EmbedUserApiEntity user) {
        this.user = user;
    }

    public Long getBirth() {
        return birth;
    }

    public void setBirth(Long birth) {
        this.birth = birth;
    }

    public Long getModified() {
        return modified;
    }

    public void setModified(Long modified) {
        this.modified = modified;
    }

    public Integer getRevision() {
        return revision;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    public List<ShotApiEntity> getReplies() {
        return replies;
    }

    public void setReplies(List<ShotApiEntity> replies) {
        this.replies = replies;
    }

    public Integer getNiceCount() {
        return niceCount;
    }

    public void setNiceCount(Integer niceCount) {
        this.niceCount = niceCount;
    }

    public ShotApiEntity getParent() {
        return parent;
    }

    public void setParent(ShotApiEntity parent) {
        this.parent = parent;
    }

    public Long getProfileHidden() {
        return profileHidden;
    }

    public void setProfileHidden(Long profileHidden) {
        this.profileHidden = profileHidden;
    }

    public List<ShotApiEntity> getThread() {
        return thread;
    }

    public void setThread(List<ShotApiEntity> thread) {
        this.thread = thread;
    }

    public Long getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(Long replyCount) {
        this.replyCount = replyCount;
    }

    public Long getLinkClicks() {
        return linkClicks;
    }

    public void setLinkClicks(Long linkClicks) {
        this.linkClicks = linkClicks;
    }

    public Long getViews() {
        return views;
    }

    public void setViews(Long views) {
        this.views = views;
    }

    public Long getReshootCount() {
        return reshootCount;
    }

    public void setReshootCount(Long reshootCount) {
        this.reshootCount = reshootCount;
    }

    public Long getPromoted() {
        return promoted;
    }

    public void setPromoted(Long promoted) {
        this.promoted = promoted;
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

    public String getCtaCaption() {
        return ctaCaption;
    }

    public void setCtaCaption(String ctaCaption) {
        this.ctaCaption = ctaCaption;
    }

    public Long getVerifiedUser() {
        return verifiedUser;
    }

    public void setVerifiedUser(Long verifiedUser) {
        this.verifiedUser = verifiedUser;
    }
}
