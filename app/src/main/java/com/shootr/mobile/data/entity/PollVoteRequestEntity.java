package com.shootr.mobile.data.entity;

public class PollVoteRequestEntity {

  private static final String PUBLIC = "PUBLIC";
  private static final String PRIVATE = "PRIVATE";

  private String votePrivacy;
  private boolean includeEmbed;

  public PollVoteRequestEntity() {
    votePrivacy = PRIVATE;
    includeEmbed = true;
  }

  public String getVotePrivacy() {
    return votePrivacy;
  }

  public void setVotePrivacy(boolean isPrivateVote) {
    votePrivacy = isPrivateVote ? PRIVATE : PUBLIC;
  }

  public boolean isIncludeEmbed() {
    return includeEmbed;
  }

  public void setIncludeEmbed(boolean includeEmbed) {
    this.includeEmbed = includeEmbed;
  }
}
