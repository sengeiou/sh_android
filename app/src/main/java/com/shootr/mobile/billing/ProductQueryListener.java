package com.shootr.mobile.billing;

import com.android.billingclient.api.SkuDetails;
import java.util.List;


public interface ProductQueryListener {

  void onQueryCompleted(List<SkuDetails> skuDetailsList);

  void onError();
}
