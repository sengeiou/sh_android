package com.shootr.mobile.ui.widgets;

/**
 * Created by miniserver on 18/4/18.
 */

public class YTParams {
  private int autoplay = 0;
  private int autohide = 1;
  private int rel = 0;
  private int showinfo = 0;
  private int enablejsapi = 1;
  private int disablekb = 1;
  private String ccLangPref = "en";
  private int controls = 1;
  private int volume = 100;
  private PlayBackQuality playbackQuality;

  public int getAutoplay() {
    return autoplay;
  }

  public void setAutoplay(int autoplay) {
    this.autoplay = autoplay;
  }

  public int getAutohide() {
    return autohide;
  }

  public void setAutohide(int autohide) {
    this.autohide = autohide;
  }

  public int getRel() {
    return rel;
  }

  public void setRel(int rel) {
    this.rel = rel;
  }

  public int getShowinfo() {
    return showinfo;
  }

  public void setShowinfo(int showinfo) {
    this.showinfo = showinfo;
  }

  public int getEnablejsapi() {
    return enablejsapi;
  }

  public void setEnablejsapi(int enablejsapi) {
    this.enablejsapi = enablejsapi;
  }

  public int getDisablekb() {
    return disablekb;
  }

  public void setDisablekb(int disablekb) {
    this.disablekb = disablekb;
  }

  public String getgetCcLangPref() {
    return ccLangPref;
  }

  public void setCcLangPref(String ccLangPref) {
    this.ccLangPref = ccLangPref;
  }

  public int getControls() {
    return controls;
  }

  public void setControls(int controls) {
    this.controls = controls;
  }

  public int getVolume() {
    return volume;
  }

  public YTParams setVolume(int volume) {
    this.volume = volume;
    return this;
  }

  public PlayBackQuality getPlaybackQuality() {
    return playbackQuality;
  }

  public YTParams setPlaybackQuality(PlayBackQuality playbackQuality) {
    this.playbackQuality = playbackQuality;
    return this;
  }


  @Override
  public String toString() {
    return "?autoplay=" + autoplay +
        "&autohide=" + autohide +
        "&rel=" + rel +
        "&showinfo=" + showinfo +
        "&enablejsapi=" + enablejsapi +
        "&disablekb=" + disablekb +
        "&cc_lang_pref=" + ccLangPref +
        "&controls=" + controls +
        "&volume=" + volume +
        "&playbackQuality=" + playbackQuality.name();
  }
}
