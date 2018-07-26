package com.shootr.mobile.ui.model;

import java.io.Serializable;
import java.util.ArrayList;

public class EntitiesModel implements Serializable {

  private ArrayList<UrlModel> urls;
  private ArrayList<BaseMessagePollModel> polls;
  private ArrayList<StreamIndexModel> streams;
  private ArrayList<ImageMediaModel> images;
  private ArrayList<MentionModel> mentions;
  private ArrayList<CardModel> cards;
  private PromotedModel promoted;

  public ArrayList<UrlModel> getUrls() {
    return urls;
  }

  public void setUrls(ArrayList<UrlModel> urls) {
    this.urls = urls;
  }

  public ArrayList<BaseMessagePollModel> getPolls() {
    return polls;
  }

  public void setPolls(ArrayList<BaseMessagePollModel> polls) {
    this.polls = polls;
  }

  public ArrayList<StreamIndexModel> getStreams() {
    return streams;
  }

  public void setStreams(ArrayList<StreamIndexModel> streams) {
    this.streams = streams;
  }

  public ArrayList<ImageMediaModel> getImages() {
    return images;
  }

  public void setImages(ArrayList<ImageMediaModel> images) {
    this.images = images;
  }

  public ArrayList<MentionModel> getMentions() {
    return mentions;
  }

  public void setMentions(ArrayList<MentionModel> mentions) {
    this.mentions = mentions;
  }

  public ArrayList<CardModel> getCards() {
    return cards;
  }

  public void setCards(ArrayList<CardModel> cards) {
    this.cards = cards;
  }

  public PromotedModel getPromoted() {
    return promoted;
  }

  public void setPromoted(PromotedModel promoted) {
    this.promoted = promoted;
  }
}
