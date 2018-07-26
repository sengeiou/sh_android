package com.shootr.mobile.billing;

import android.app.Activity;
import android.support.annotation.Nullable;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BillingManager implements PurchasesUpdatedListener, ConsumeResponseListener {

  private static String TAG = "store";

  private BillingClient billingClient;
  private Activity activity;
  private BillingUpdatesListener billingUpdatesListener;

  private boolean isConnectedToBillingService;
  private int billingResponseCode;
  private Set<String> itemsToConsume;
  private Purchase purchase;
  private List<Purchase> currentPurchases;

  public BillingManager(Activity activity, final BillingUpdatesListener billingUpdatesListener) {
    this.activity = activity;
    this.billingUpdatesListener = billingUpdatesListener;

    billingClient = BillingClient.newBuilder(activity).setListener(this).build();

    startServiceConnection(new Runnable() {
      @Override public void run() {
        billingUpdatesListener.onBillingSetupFinished();
      }
    });
  }

  public void getProductsDetails(final List<String> skuList, final ProductQueryListener listener) {

    Runnable queryRequest = new Runnable() {
      @Override public void run() {
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList);
        params.setType(BillingClient.SkuType.INAPP);

        billingClient.querySkuDetailsAsync(params.build(), new SkuDetailsResponseListener() {
          @Override
          public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {
            if (responseCode == BillingClient.BillingResponse.OK) {
              listener.onQueryCompleted(skuDetailsList);
            } else {
              listener.onError();
            }
          }
        });
      }
    };

    executeServiceRequest(queryRequest);
  }

  public void initiatePurchaseFlow(final String skuId) {
    Runnable purchaseRequest = new Runnable() {
      @Override public void run() {
        BillingFlowParams purchaseParams = BillingFlowParams.newBuilder().setSku(skuId).setType(
            BillingClient.SkuType.INAPP).build();

        for (Purchase currentPurchase : currentPurchases) {
          if (currentPurchase.getSku().equals(skuId)) {
            handlePurchase(currentPurchase);
            return;
          }
        }

        billingClient.launchBillingFlow(activity, purchaseParams);
      }
    };
    executeServiceRequest(purchaseRequest);
  }

  private void executeServiceRequest(Runnable runnable) {
    if (isConnectedToBillingService) {
      runnable.run();
    } else {
      startServiceConnection(runnable);
    }
  }

  @Override public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {
    if (responseCode == BillingClient.BillingResponse.OK) {
      if (purchases != null) {
        for (Purchase purchase : purchases) {
          handlePurchase(purchase);
        }
      }
    } else if (responseCode == BillingClient.BillingResponse.USER_CANCELED) {
      /* no-op */
    } else if (responseCode == BillingClient.BillingResponse.ITEM_ALREADY_OWNED) {
      /* no-op */
    } else {
      /* no-op */
    }
  }

  private void handlePurchase(Purchase purchase) {
    billingUpdatesListener.verifyPurchase(purchase.getOriginalJson(), purchase.getPurchaseToken());
  }

  public void onPurchaseVerified(String purchaseToken) {
    consumeProduct(purchaseToken);
  }

  public void consumeProduct(final String purchaseToken) {
    if (itemsToConsume == null) {
      itemsToConsume = new HashSet<>();
    } else if (itemsToConsume.contains(purchaseToken)) {
      return;
    }
    itemsToConsume.add(purchaseToken);

    Runnable consumeRequest = new Runnable() {
      @Override
      public void run() {
        billingClient.consumeAsync(purchaseToken, BillingManager.this);
      }
    };

    executeServiceRequest(consumeRequest);
  }

  public void queryPurchases() {
    Runnable queryToExecute = new Runnable() {
      @Override
      public void run() {
        Purchase.PurchasesResult purchasesResult = billingClient.queryPurchases(
            BillingClient.SkuType.INAPP);
        onQueryPurchasesFinished(purchasesResult);
      }
    };

    executeServiceRequest(queryToExecute);
  }

  private void onQueryPurchasesFinished(Purchase.PurchasesResult result) {
    if (billingClient == null || result.getResponseCode() != BillingClient.BillingResponse.OK) {
      return;
    }
    currentPurchases = result.getPurchasesList();
  }

  private void startServiceConnection(final Runnable executeOnSuccess) {
    billingClient.startConnection(new BillingClientStateListener() {
      @Override public void onBillingSetupFinished(int billingResponse) {
        if (billingResponseCode == BillingClient.BillingResponse.OK) {
          isConnectedToBillingService = true;
          if (executeOnSuccess != null) {
            executeOnSuccess.run();
          }
        }
        billingResponseCode = billingResponse;
        queryPurchases();
      }

      @Override public void onBillingServiceDisconnected() {
        isConnectedToBillingService = false;
      }
    });
  }

  @Override public void onConsumeResponse(int responseCode, String purchaseToken) {
    itemsToConsume.remove(purchaseToken);
    if (responseCode == BillingClient.BillingResponse.OK) {
      billingUpdatesListener.onConsumeFinished(purchaseToken);
    } else {
      billingUpdatesListener.onError();
    }
  }

  public void destroy() {
    if (billingClient != null && billingClient.isReady()) {
      billingClient.endConnection();
      billingClient = null;
    }
  }
}
