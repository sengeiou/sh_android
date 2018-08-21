package com.shootr.mobile.data.repository.remote.cache;

import com.shootr.mobile.data.repository.datasource.CachedDataSource;
import com.shootr.mobile.domain.model.DataItem;
import com.shootr.mobile.domain.model.ListType;
import com.shootr.mobile.domain.model.PrintableItem;
import com.shootr.mobile.domain.model.PrintableType;
import com.shootr.mobile.domain.model.StreamTimeline;
import com.shootr.mobile.domain.model.TimelineType;
import com.shootr.mobile.domain.model.poll.Poll;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.repository.Nicest;
import com.shootr.mobile.domain.repository.poll.InternalPollRepository;
import com.shootr.mobile.util.MergeUtils;
import com.vincentbrison.openlibraries.android.dualcache.DualCache;
import java.util.ArrayList;
import javax.inject.Inject;

public class TimelineCache implements CachedDataSource {

  private final DualCache<StreamTimeline> timelineLruCache;
  private final DualCache<StreamTimeline> nicestTimelineLruCache;
  private final TimelineRepositionCache timelineRepositionCache;
  private final InternalPollRepository internalPollRepository;

  @Inject public TimelineCache(DualCache<StreamTimeline> timelineLruCache,
      @Nicest DualCache<StreamTimeline> nicestTimelineLruCache, TimelineRepositionCache timelineRepositionCache,
      InternalPollRepository internalPollRepository) {
    this.timelineLruCache = timelineLruCache;
    this.nicestTimelineLruCache = nicestTimelineLruCache;
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

  public void updateStreamForTImeline(String idStream, String idFilter, Stream stream) {
    StreamTimeline timeline = getTimeline(idStream, idFilter);

    if (timeline != null) {
      timeline.setStream(stream);
      putTimeline(timeline);
    }
  }

  public void updateParticipantsForTImeline(String idStream, String idFilter, int totalParticipants,
      int followingParticipants) {
    StreamTimeline timeline = getTimeline(idStream, idFilter);

    if (timeline != null) {
      timeline.setParticipantsNumber(totalParticipants);
      timeline.setFollowingNumber(followingParticipants);
      putTimeline(timeline);
    }
  }

  public void putTimeline(StreamTimeline timeline) {
    if (timeline.getFilter().equals(TimelineType.NICEST)) {
      putNicestTimeline(timeline);
    } else {
      putStandarTimeline(timeline);
    }
  }

  private void putStandarTimeline(StreamTimeline timeline) {
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

  private void putNicestTimeline(StreamTimeline timeline) {
    nicestTimelineLruCache.delete(timeline.getStream().getId()
        + "-"
        + timeline.getFilter().toLowerCase()
        + "-"
        + timeline.getPeriod());
    nicestTimelineLruCache.put(timeline.getStream().getId()
        + "-"
        + timeline.getFilter().toLowerCase()
        + "-"
        + timeline.getPeriod(), timeline);
  }

  public StreamTimeline getNicestTimeline(String idStream, String idFilter, long period) {
    StreamTimeline timeline =
        nicestTimelineLruCache.get(idStream + "-" + idFilter.toLowerCase() + "-" + period);
    if (timeline != null) {
      timeline.setTimelineReposition(
          timelineRepositionCache.getTimelineReposition(idStream, idFilter));
    }
    return timeline;
  }

  private void storeTimeline(StreamTimeline timeline) {
    timelineLruCache.delete(timeline.getStream().getId() + "-" + timeline.getFilter().toLowerCase());
    timelineLruCache.put(timeline.getStream().getId() + "-" + timeline.getFilter().toLowerCase(), timeline);
  }

  public void putFixedItem(DataItem fixed, String idStream, String filter) {
    StreamTimeline timeline = this.getTimeline(idStream, filter.toLowerCase());
    if (timeline != null) {
      //timeline.setFixed(fixed);
      storeTimeline(timeline);
    }

  }

  public void putPinnedItem(DataItem pinned, String idStream, String filter) {
    StreamTimeline timeline = this.getTimeline(idStream, filter.toLowerCase());
    if (timeline != null) {
      //timeline.setPinned(pinned);
      storeTimeline(timeline);
      for (PrintableItem printableItem : pinned.getData()) {
        if (printableItem.getResultType().equals(PrintableType.POLL)) {
          internalPollRepository.putPoll((Poll) printableItem);
        }
      }

    }

  }

  public void putItemInTimeline(PrintableItem printableItem, String idStream, String filter,
      String list) {

    StreamTimeline timeline = this.getTimeline(idStream, filter.toLowerCase());

    if (timeline == null) {
      return;
    }

    switch (list) {
      case ListType.TIMELINE_ITEMS:
        timeline.getItems().getData().add(0, printableItem);
        break;

      case ListType.TIMELINE_VIDEOS:
        timeline.getVideos().getData().add(0, printableItem);
        break;

      case ListType.TIMELINE_POLLS:
        timeline.getPolls().getData().add(0, printableItem);
        break;

      case ListType.TIMELINE_HIGHLIGHTED_SHOTS:
        timeline.getHighlightedShots().getData().add(0, printableItem);
        break;
      case ListType.TIMELINE_PROMOTED_SHOTS:
        timeline.getPromotedShots().getData().add(0, printableItem);
        break;
      case ListType.TIMELINE_FOLLOWING_USERS:
        timeline.getFollowings().getData().add(0, printableItem);
        break;
      default:
        break;
    }

    storeTimeline(timeline);
  }

  public void updateItemInNicestTimeline(PrintableItem printableItem, String filter, long period) {
    if (printableItem.getResultType().equals(PrintableType.SHOT)) {
      String idStream = ((Shot) printableItem).getStreamInfo().getIdStream();
      StreamTimeline timeline = this.getNicestTimeline(idStream, filter.toLowerCase(), period);
      if (timeline != null) {
        updateItemInList(timeline.getItems().getData(), printableItem, true);
        putNicestTimeline(timeline);
      }

    }
  }

  public void updateItem(PrintableItem printableItem, String idStream, String filter, String list) {

    StreamTimeline timeline = this.getTimeline(idStream, filter.toLowerCase());

    if (timeline == null) {
      return;
    }

    switch (list) {
      case ListType.TIMELINE_ITEMS:
        updateItemInList(timeline.getItems().getData(), printableItem, false);
        break;

      case ListType.TIMELINE_VIDEOS:
        updateItemInList(timeline.getVideos().getData(), printableItem, false);
        break;

      case ListType.TIMELINE_POLLS:
        updateItemInList(timeline.getPolls().getData(), printableItem, false);
        break;

      case ListType.TIMELINE_HIGHLIGHTED_SHOTS:
        updateItemInList(timeline.getHighlightedShots().getData(), printableItem, false);

      case ListType.TIMELINE_PROMOTED_SHOTS:
        updateItemInList(timeline.getPromotedShots().getData(), printableItem, false);
        break;
      case ListType.TIMELINE_FOLLOWING_USERS:
        updateItemInList(timeline.getFollowings().getData(), printableItem, false);
        break;
      default:
        break;
    }

    storeTimeline(timeline);
  }

  public PrintableItem partialUpdateItem(PrintableItem printableItem, String idStream,
      String filter, String list) {

    PrintableItem printableItemResult = null;
    StreamTimeline timeline = this.getTimeline(idStream, filter.toLowerCase());

    if (timeline == null) {
      return printableItemResult;
    }

    switch (list) {
      case ListType.TIMELINE_ITEMS:
        printableItemResult = partialUpdateItemInList(timeline.getItems().getData(), printableItem);
        break;

      case ListType.TIMELINE_VIDEOS:
        printableItemResult =
            partialUpdateItemInList(timeline.getVideos().getData(), printableItem);
        break;

      case ListType.TIMELINE_POLLS:
        printableItemResult = partialUpdateItemInList(timeline.getPolls().getData(), printableItem);
        break;

      case ListType.TIMELINE_HIGHLIGHTED_SHOTS:
        printableItemResult =
            partialUpdateItemInList(timeline.getHighlightedShots().getData(), printableItem);
        break;
      case ListType.TIMELINE_PROMOTED_SHOTS:
        printableItemResult =
            partialUpdateItemInList(timeline.getPromotedShots().getData(), printableItem);
        break;
      case ListType.TIMELINE_FOLLOWING_USERS:
        printableItemResult =
            partialUpdateItemInList(timeline.getFollowings().getData(), printableItem);
        break;
      default:
        break;
    }
    storeTimeline(timeline);
    return printableItemResult;
  }


  private void updateItemInList(ArrayList<PrintableItem> items, PrintableItem printableItem,
      boolean insertIfNoExists) {
    int index = items.indexOf(printableItem);
    if (index != -1) {
      if (printableItem.getDeletedData() != null) {
        items.remove(index);
      } else {
        items.set(index, printableItem);
      }
    } else {
      if (insertIfNoExists) {
        items.add(0, printableItem);
      }
    }
  }

  private PrintableItem partialUpdateItemInList(ArrayList<PrintableItem> items,
      PrintableItem printableItem) {
    PrintableItem returnValue = null;
    int index = items.indexOf(printableItem);
    if (index != -1) {
      returnValue = (PrintableItem) MergeUtils.mergePrintableItem(printableItem, items.get(index));
      if (returnValue.getDeletedData() != null) {
        items.remove(index);
      } else {
        items.set(index, returnValue);
      }
    }
    return returnValue;
  }

  @Override public boolean isValid() {
    return false;
  }

  @Override public void invalidate() {
    timelineLruCache.invalidate();
  }
}
