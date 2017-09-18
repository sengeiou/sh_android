package com.shootr.mobile.ui.model.mappers;

import com.shootr.mobile.domain.model.stream.OnBoarding;
import com.shootr.mobile.ui.model.OnBoardingModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class OnBoardingModelMapper {

  private final StreamModelMapper streamModelMapper;
  private final UserModelMapper userModelMapper;

  @Inject public OnBoardingModelMapper(StreamModelMapper streamModelMapper,
      UserModelMapper userModelMapper) {
    this.streamModelMapper = streamModelMapper;
    this.userModelMapper = userModelMapper;
  }

  public OnBoardingModel transform(OnBoarding onBoarding) {
    if (onBoarding == null) {
      return null;
    }

    OnBoardingModel onBoardingModel = new OnBoardingModel();

    onBoardingModel.setFavorite(onBoarding.isFavorite());
    onBoardingModel.setStreamModel(streamModelMapper.transform(onBoarding.getStream()));
    onBoardingModel.setUserModel(userModelMapper.transform(onBoarding.getUser()));

    return onBoardingModel;
  }

  public List<OnBoardingModel> transform(List<OnBoarding> onBoardings) {
    ArrayList<OnBoardingModel> onBoardingModels = new ArrayList<>();
    for (OnBoarding onBoarding : onBoardings) {
      onBoardingModels.add(transform(onBoarding));
    }
    return onBoardingModels;
  }
}
