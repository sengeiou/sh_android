package com.shootr.mobile.ui.model.mappers;

import com.shootr.mobile.domain.model.PrintableItem;
import com.shootr.mobile.domain.model.Topic;
import com.shootr.mobile.domain.model.poll.Poll;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.ui.model.PollModel;
import com.shootr.mobile.ui.model.PrintableModel;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.model.TopicModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class PrintableModelMapper {

  private final ShotModelMapper shotModelMapper;
  private final TopicModelMapper topicModelMapper;
  private final PollModelMapper pollModelMapper;

  @Inject public PrintableModelMapper(ShotModelMapper shotModelMapper,
      TopicModelMapper topicModelMapper, PollModelMapper pollModelMapper) {
    this.shotModelMapper = shotModelMapper;
    this.topicModelMapper = topicModelMapper;
    this.pollModelMapper = pollModelMapper;
  }

  public List<PrintableModel> mapPrintableModel(List<PrintableItem> printableItems) {
    ArrayList<PrintableModel> printableModels = new ArrayList<>();
    for (PrintableItem printableItem : printableItems) {
      if (printableItem instanceof Shot) {
        mapShotModel(printableModels, (Shot) printableItem, PrintableModel.ITEMS_GROUP);
      } else if (printableItem instanceof Topic) {
        mapTopicModel(printableModels, (Topic) printableItem);
      }
    }

    return  printableModels;
  }

  public List<PrintableModel> mapFixableModel(List<PrintableItem> printableItems) {
    ArrayList<PrintableModel> printableModels = new ArrayList<>();
    for (PrintableItem printableItem : printableItems) {
      if (printableItem instanceof Shot) {
        mapShotModel(printableModels, (Shot) printableItem, PrintableModel.FIXED_GROUP);
      }
    }

    return  printableModels;
  }

  public List<PrintableModel> mapResponseModel(List<PrintableItem> printableItems) {
    ArrayList<PrintableModel> printableModels = new ArrayList<>();
    for (PrintableItem printableItem : printableItems) {
      if (printableItem instanceof Shot) {
        mapShotModel(printableModels, (Shot) printableItem, PrintableModel.REPLY);
      } else if (printableItem instanceof Topic) {
        mapTopicModel(printableModels, (Topic) printableItem);
      }
    }

    return  printableModels;
  }

  public List<PrintableModel> mapMainShot(List<PrintableItem> printableItems) {
    ArrayList<PrintableModel> printableModels = new ArrayList<>();
    for (PrintableItem printableItem : printableItems) {
      if (printableItem instanceof Shot) {
        mapShotModel(printableModels, (Shot) printableItem, PrintableModel.MAIN_SHOT);
      } else if (printableItem instanceof Topic) {
        mapTopicModel(printableModels, (Topic) printableItem);
      }
    }

    return  printableModels;
  }


  public List<PrintableModel> mapPinnableModel(List<PrintableItem> printableItems) {
    ArrayList<PrintableModel> printableModels = new ArrayList<>();
    for (PrintableItem printableItem : printableItems) {
      if (printableItem instanceof Topic) {
        mapTopicModel(printableModels, (Topic) printableItem);
      } else if (printableItem instanceof Poll) {
        PollModel pollModel = pollModelMapper.transform((Poll) printableItem);
        pollModel.setTimelineGroup(PrintableModel.PINNED_GROUP);
        printableModels.add(pollModel);
      }
    }

    return  printableModels;
  }


  private void mapShotModel(ArrayList<PrintableModel> printableModels, Shot printableItem,
      String itemsGroup) {
    ShotModel shotModel = shotModelMapper.transform(printableItem);
    shotModel.setTimelineGroup(itemsGroup);
    printableModels.add(shotModel);
  }

  private void mapTopicModel(ArrayList<PrintableModel> printableModels, Topic printableItem) {
    TopicModel topicModel = topicModelMapper.map(printableItem);
    topicModel.setTimelineGroup(PrintableModel.PINNED_GROUP);
    printableModels.add(topicModel);
  }

}
