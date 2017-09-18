package com.shootr.mobile.ui.model.mappers;

import com.shootr.mobile.domain.model.stream.OnBoarding;
import com.shootr.mobile.ui.model.OnBoardingModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class OnBoardingStreamModelMapper {

  private final StreamModelMapper streamModelMapper;

  @Inject public OnBoardingStreamModelMapper(StreamModelMapper streamModelMapper) {
    this.streamModelMapper = streamModelMapper;
  }

  public OnBoardingModel transform(OnBoarding onBoardingStream) {
    if (onBoardingStream == null) {
      return null;
    }

    OnBoardingModel onBoardingStreamModel = new OnBoardingModel();

    onBoardingStreamModel.setFavorite(onBoardingStream.isFavorite());
    onBoardingStreamModel.setStreamModel(streamModelMapper.transform(onBoardingStream.getStream()));

    return onBoardingStreamModel;
  }

  public List<OnBoardingModel> transform(List<OnBoarding> onBoardingStreams) {
    ArrayList<OnBoardingModel> onBoardingStreamModels = new ArrayList<>();
    for (OnBoarding onBoardingStream : onBoardingStreams) {
      onBoardingStreamModels.add(transform(onBoardingStream));
    }
    return onBoardingStreamModels;
  }
}
