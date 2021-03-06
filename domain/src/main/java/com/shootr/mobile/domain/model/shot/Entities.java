package com.shootr.mobile.domain.model.shot;

import com.shootr.mobile.domain.model.ImageMedia;
import java.util.ArrayList;

public class Entities {

  private ArrayList<Url> urls;
  private ArrayList<Poll> polls;
  private ArrayList<StreamIndex> streams;
  private ArrayList<ImageMedia> images;
  private ArrayList<Mention> mentions;
  private ArrayList<Card> cards;
  private Promoted promoted;

  public ArrayList<Url> getUrls() {
    return urls;
  }

  public void setUrls(ArrayList<Url> urls) {
    this.urls = urls;
  }

  public ArrayList<Poll> getPolls() {
    return polls;
  }

  public void setPolls(ArrayList<Poll> polls) {
    this.polls = polls;
  }

  public ArrayList<StreamIndex> getStreams() {
    return streams;
  }

  public void setStreams(ArrayList<StreamIndex> streams) {
    this.streams = streams;
  }

  public ArrayList<ImageMedia> getImages() {
    return images;
  }

  public void setImages(ArrayList<ImageMedia> images) {
    this.images = images;
  }

  public ArrayList<Mention> getMentions() {
    return mentions;
  }

  public void setMentions(ArrayList<Mention> mentions) {
    this.mentions = mentions;
  }

  public ArrayList<Card> getCards() {
    return cards;
  }

  public void setCards(ArrayList<Card> cards) {
    this.cards = cards;
  }

  public Promoted getPromoted() {
    return promoted;
  }

  public void setPromoted(Promoted promoted) {
    this.promoted = promoted;
  }
}
