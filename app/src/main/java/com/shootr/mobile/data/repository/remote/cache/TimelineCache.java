package com.shootr.mobile.data.repository.remote.cache;

import com.shootr.mobile.data.repository.datasource.CachedDataSource;
import com.shootr.mobile.domain.model.DataItem;
import com.shootr.mobile.domain.model.PrintableItem;
import com.shootr.mobile.domain.model.PrintableType;
import com.shootr.mobile.domain.model.StreamTimeline;
import com.shootr.mobile.domain.model.poll.Poll;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.repository.poll.InternalPollRepository;
import com.vincentbrison.openlibraries.android.dualcache.DualCache;
import java.util.ArrayList;
import javax.inject.Inject;

public class TimelineCache implements CachedDataSource {

  private final DualCache<StreamTimeline> timelineLruCache;
  private final TimelineRepositionCache timelineRepositionCache;
  private final InternalPollRepository internalPollRepository;

  @Inject public TimelineCache(DualCache<StreamTimeline> timelineLruCache,
      TimelineRepositionCache timelineRepositionCache, InternalPollRepository internalPollRepository) {
    this.timelineLruCache = timelineLruCache;
    this.timelineRepositionCache = timelineRepositionCache;
    this.internalPollRepository = internalPollRepository;
  }

  public StreamTimeline getTimeline(String idStream, String idFilter) {
    StreamTimeline timeline = timelineLruCache.get(idStream + "-" + idFilter.toLowerCase());
    if (timeline != null) {
      timeline.setTimelineReposition(
          timelineRepositionCache.getTimelineReposition(idStream, idFilter));
    }
    return timeline;
  }

  public void putTimeline(StreamTimeline timeline) {

    StreamTimeline cachedTimeline = getTimeline(timeline.getStream().getId(), timeline.getFilter());

    if (cachedTimeline != null) {
      if (timeline.getItems().getMaxTimestamp() >= cachedTimeline.getItems().getMaxTimestamp()) {
        storeTimeline(timeline);
      } else {
        cachedTimeline.getItems().setMaxTimestamp(timeline.getItems().getMaxTimestamp());
        cachedTimeline.getItems().getData().addAll(timeline.getItems().getData());
        storeTimeline(cachedTimeline);
      }
    } else {
      storeTimeline(timeline);
    }
  }

  private void storeTimeline(StreamTimeline timeline) {
    timelineLruCache.delete(timeline.getStream().getId() + "-" + timeline.getFilter().toLowerCase());
    timelineLruCache.put(timeline.getStream().getId() + "-" + timeline.getFilter().toLowerCase(), timeline);
  }

  public void putFixedItem(DataItem fixed, String idStream, String filter) {
    StreamTimeline timeline = this.getTimeline(idStream, filter.toLowerCase());
    if (timeline != null) {
      timeline.setFixed(fixed);
      storeTimeline(timeline);
    }

  }

  public void putPinnedItem(DataItem pinned, String idStream, String filter) {
    StreamTimeline timeline = this.getTimeline(idStream, filter.toLowerCase());
    if (timeline != null) {
      timeline.setPinned(pinned);
      storeTimeline(timeline);
      for (PrintableItem printableItem : pinned.getData()) {
        if (printableItem.getResultType().equals(PrintableType.POLL)) {
          internalPollRepository.putPoll((Poll) printableItem);
        }
      }

    }

  }

  public void putItemInTimeline(PrintableItem printableItem, String filter) {
    if (printableItem.getResultType().equals(PrintableType.SHOT)) {
      String idStream = ((Shot) printableItem).getStreamInfo().getIdStream();
      StreamTimeline timeline = this.getTimeline(idStream, filter.toLowerCase());
      if (timeline != null) {
        timeline.getItems().getData().add(0, printableItem);
        storeTimeline(timeline);
      }
    }
  }

  public void updateItem(PrintableItem printableItem, String filter) {
    if (printableItem.getResultType().equals(PrintableType.SHOT)) {
      String idStream = ((Shot) printableItem).getStreamInfo().getIdStream();
      StreamTimeline timeline = this.getTimeline(idStream, filter.toLowerCase());
      if (timeline != null) {

        updateItemInList(timeline.getItems().getData(), printableItem);
        if (timeline.getFixed() != null) {
          updateItemInList(timeline.getFixed().getData(), printableItem);
        }
        storeTimeline(timeline);
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

  @Override public boolean isValid() {
    return false;
  }

  @Override public void invalidate() {
    timelineLruCache.invalidate();
  }
}
