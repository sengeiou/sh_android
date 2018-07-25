package com.shootr.mobile.data.entity.socket;

import com.shootr.mobile.data.entity.NewShotParameters;

public class NewShotContentApiEntity {

  private NewShotParameters shot;
  private ShotReceiptApiEntity receipt;

  public NewShotParameters getShot() {
    return shot;
  }

  public void setShot(NewShotParameters shot) {
    this.shot = shot;
  }

  public ShotReceiptApiEntity getReceipt() {
    return receipt;
  }

  public void setReceipt(ShotReceiptApiEntity receipt) {
    this.receipt = receipt;
  }
}
