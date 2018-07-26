package com.shootr.mobile.data.repository.remote.cache;

import com.shootr.mobile.domain.model.PromotedReceipt;
import com.shootr.mobile.domain.model.user.PromotedTiers;
import com.vincentbrison.openlibraries.android.dualcache.DualCache;
import javax.inject.Inject;

public class PromotedTiersCache {
  private final static String EMPTY = "empty";
  private final static String PROMOTED = "promoted";
  private final DualCache<PromotedTiers> promotedTiersDualCache;

  @Inject public PromotedTiersCache(DualCache<PromotedTiers> newShotDetailDualCache) {
    this.promotedTiersDualCache = newShotDetailDualCache;
  }

  public void putPromotedTiers(PromotedTiers promotedTiers) {
    promotedTiersDualCache.delete(PROMOTED);
    promotedTiersDualCache.put(PROMOTED, promotedTiers);
  }

  public PromotedTiers getPromotedTiers() {
    return promotedTiersDualCache.get(PROMOTED);
  }

  public void addPromotedReceipt(PromotedReceipt promotedReceipt) {
    PromotedTiers promotedTiers = getPromotedTiers();
    promotedTiers.getPendingReceipts().add(promotedReceipt);
    putPromotedTiers(promotedTiers);
  }

  public void updatePromotedReceipt(PromotedReceipt promotedReceipt) {
    PromotedTiers promotedTiers = getPromotedTiers();
    int index = promotedTiers.getPendingReceipts().indexOf(promotedReceipt);
    if (index != -1) {
      if (promotedReceipt.getDeleted() != null) {
        promotedTiers.getPendingReceipts().remove(index);
      } else {
        promotedTiers.getPendingReceipts().set(index, promotedReceipt);
      }

      putPromotedTiers(promotedTiers);
    }
  }

}
