package com.shootr.mobile.ui.model.mappers;

import com.shootr.mobile.domain.model.ExternalVideo;
import com.shootr.mobile.domain.model.PrintableItem;
import com.shootr.mobile.domain.model.Topic;
import com.shootr.mobile.domain.model.poll.Poll;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.ui.model.ExternalVideoModel;
import com.shootr.mobile.ui.model.ExternalVideoModelMapper;
import com.shootr.mobile.ui.model.PollModel;
import com.shootr.mobile.ui.model.PrintableModel;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.model.TopicModel;
import com.shootr.mobile.ui.model.UserModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class PrintableModelMapper {

  private final ShotModelMapper shotModelMapper;
  private final TopicModelMapper topicModelMapper;
  private final PollModelMapper pollModelMapper;
  private final UserModelMapper userModelMapper;
  private final ExternalVideoModelMapper externalVideoModelMapper;

  @Inject public PrintableModelMapper(ShotModelMapper shotModelMapper,
      TopicModelMapper topicModelMapper, PollModelMapper pollModelMapper,
      UserModelMapper userModelMapper, ExternalVideoModelMapper externalVideoModelMapper) {
    this.shotModelMapper = shotModelMapper;
    this.topicModelMapper = topicModelMapper;
    this.pollModelMapper = pollModelMapper;
    this.userModelMapper = userModelMapper;
    this.externalVideoModelMapper = externalVideoModelMapper;
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

  public List<PrintableModel> mapPrintableModel(List<PrintableItem> printableItems, String group) {
    ArrayList<PrintableModel> printableModels = new ArrayList<>();
    for (PrintableItem printableItem : printableItems) {
      if (printableItem instanceof Shot) {
        mapShotModel(printableModels, (Shot) printableItem, group);
      } else if (printableItem instanceof Poll) {
        PollModel pollModel = pollModelMapper.transform((Poll) printableItem);
        pollModel.setTimelineGroup(group);
        printableModels.add(pollModel);
      } else if (printableItem instanceof User) {
        UserModel userModel = userModelMapper.transform((User) printableItem);
        userModel.setTimelineGroup(group);
        printableModels.add(userModel);
      } else if (printableItem instanceof ExternalVideo) {
        ExternalVideoModel externalVideoModel = externalVideoModelMapper.map((ExternalVideo) printableItem);
        externalVideoModel.setTimelineGroup(group);
        printableModels.add(externalVideoModel);
      }
    }

    return  printableModels;
  }

  public PrintableModel mapPrintableModel(PrintableItem printableItem, String group) {

    if (printableItem instanceof Shot) {
      ShotModel shotModel = shotModelMapper.transform((Shot) printableItem);
      shotModel.setTimelineGroup(group);
      return shotModel;
    } else if (printableItem instanceof Poll) {
      PollModel pollModel = pollModelMapper.transform((Poll) printableItem);
      pollModel.setTimelineGroup(group);
      return pollModel;
    } else if (printableItem instanceof User) {
      UserModel userModel = userModelMapper.transform((User) printableItem);
      userModel.setTimelineGroup(group);
      return userModel;
    } else if (printableItem instanceof ExternalVideo) {
      ExternalVideoModel externalVideoModel = externalVideoModelMapper.map((ExternalVideo) printableItem);
      externalVideoModel.setTimelineGroup(group);
      return externalVideoModel;
    }

    return null;
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
