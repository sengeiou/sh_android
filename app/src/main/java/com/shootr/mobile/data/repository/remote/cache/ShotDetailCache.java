package com.shootr.mobile.data.repository.remote.cache;

import com.shootr.mobile.domain.model.ListType;
import com.shootr.mobile.domain.model.PrintableItem;
import com.shootr.mobile.domain.model.PrintableType;
import com.shootr.mobile.domain.model.shot.NewShotDetail;
import com.shootr.mobile.domain.model.shot.Shot;
import com.vincentbrison.openlibraries.android.dualcache.DualCache;
import java.util.ArrayList;
import javax.inject.Inject;

public class ShotDetailCache {

  private final static String EMPTY = "empty";
  private final DualCache<NewShotDetail> newShotDetailDualCache;

  @Inject public ShotDetailCache(DualCache<NewShotDetail> newShotDetailDualCache) {
    this.newShotDetailDualCache = newShotDetailDualCache;
  }

  public void putShot(NewShotDetail shotDetail) {
    newShotDetailDualCache.delete(((Shot) shotDetail.getShot()).getIdShot());
    newShotDetailDualCache.put(((Shot) shotDetail.getShot()).getIdShot(), shotDetail);
  }

  public NewShotDetail getShot(String idShot) {
    return newShotDetailDualCache.get(idShot);
  }

  public void updateItem(PrintableItem printableItem, String idMainShot, String list) {
    if (printableItem.getResultType().equals(PrintableType.SHOT)) {
      NewShotDetail shotDetail = getShot(idMainShot);
      if (shotDetail != null) {
        switch (list) {
          case ListType.ITEM_DETAIL:
            shotDetail.setShot(printableItem);
            break;
          case ListType.PROMOTED_REPLIES:
            updateItemInList(shotDetail.getReplies().getPromoted().getData(), printableItem);
            break;

          case ListType.SUBSCRIBERS_REPLIES:
            updateItemInList(shotDetail.getReplies().getSubscribers().getData(), printableItem);
            break;
          case ListType.OTHER_REPLIES:
            updateItemInList(shotDetail.getReplies().getBasic().getData(), printableItem);
            break;
          case ListType.PARENTS_DETAIL:
            updateItemInList(shotDetail.getParents().getData(), printableItem);
            break;

          default:
            break;
        }
        putShot(shotDetail);
      }
    }
  }

  private void updateItemInList(ArrayList<PrintableItem> items, PrintableItem printableItem) {
    int index = items.indexOf(printableItem);
    if (index != -1) {
      if (((Shot) printableItem).getMetadata().getDeleted() != null) {
        items.remove(index);
      } else {
        items.set(index, printableItem);
      }
    }
  }

  public void addItemInShotDetail(PrintableItem item, String list) {
    if (item.getResultType().equals(PrintableType.SHOT)) {
      String idShot = ((Shot) item).getParentShotId() == null ? EMPTY : ((Shot) item).getParentShotId();
      NewShotDetail shotDetail = getShot(idShot);
      if (shotDetail != null) {
        switch (list) {
          case ListType.PROMOTED_REPLIES:
            shotDetail.getReplies().getPromoted().getData().add(0, item);
            break;
          case ListType.SUBSCRIBERS_REPLIES:
            shotDetail.getReplies().getSubscribers().getData().add(0, item);
            break;
          case ListType.OTHER_REPLIES:
            shotDetail.getReplies().getBasic().getData().add(0, item);
            break;
          default:
            break;
        }
        putShot(shotDetail);
      }
    }

  }
}
