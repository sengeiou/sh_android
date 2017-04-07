package com.shootr.mobile.domain.model.discover;

import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.stream.Stream;
import java.util.ArrayList;

public class DiscoverStream {

  private Stream stream;
  private ArrayList<Shot> shots;
  private boolean isFavorite;

  public Stream getStream() {
    return stream;
  }

  public void setStream(Stream stream) {
    this.stream = stream;
  }

  public ArrayList<Shot> getShots() {
    return shots;
  }

  public void setShots(ArrayList<Shot> shots) {
    this.shots = shots;
  }

  public boolean isFavorite() {
    return isFavorite;
  }

  public void setFavorite(boolean favorite) {
    isFavorite = favorite;
  }
}
