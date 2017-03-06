package com.shootr.mobile.ui.model.mappers;

import com.shootr.mobile.domain.model.stream.OnBoardingStream;
import com.shootr.mobile.ui.model.OnBoardingStreamModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class OnBoardingStreamModelMapper {

  private final StreamModelMapper streamModelMapper;

  @Inject public OnBoardingStreamModelMapper(StreamModelMapper streamModelMapper) {
    this.streamModelMapper = streamModelMapper;
  }

  public OnBoardingStreamModel transform(OnBoardingStream onBoardingStream) {
    if (onBoardingStream == null) {
      return null;
    }

    OnBoardingStreamModel onBoardingStreamModel = new OnBoardingStreamModel();

    onBoardingStreamModel.setFavorite(onBoardingStream.isFavorite());
    onBoardingStreamModel.setStreamModel(streamModelMapper.transform(onBoardingStream.getStream()));

    return onBoardingStreamModel;
  }

  public List<OnBoardingStreamModel> transform(List<OnBoardingStream> onBoardingStreams) {
    ArrayList<OnBoardingStreamModel> onBoardingStreamModels = new ArrayList<>();
    for (OnBoardingStream onBoardingStream : onBoardingStreams) {
      onBoardingStreamModels.add(transform(onBoardingStream));
    }
    return onBoardingStreamModels;
  }
}
