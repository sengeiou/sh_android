package com.shootr.mobile.billing;


public interface BillingUpdatesListener {

  void onBillingSetupFinished();

  void verifyPurchase(String purchaseInfo, String purchaseToken);

  void onConsumeFinished(String purchaseToken);

  void onError();
}
