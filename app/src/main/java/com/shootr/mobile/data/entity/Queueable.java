package com.shootr.mobile.data.entity;

public interface Queueable {

  Long getIdQueue();

  void setIdQueue(Long idQueue);

  Integer getFailed();

  void setFailed(Integer failed);

  String getImageFile();

  void setImageFile(String imageFile);
}
