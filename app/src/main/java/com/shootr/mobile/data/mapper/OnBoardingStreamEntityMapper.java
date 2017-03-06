package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.entity.OnBoardingStreamEntity;
import com.shootr.mobile.domain.model.stream.OnBoardingStream;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class OnBoardingStreamEntityMapper {

  private final StreamEntityMapper streamEntityMapper;

  @Inject public OnBoardingStreamEntityMapper(StreamEntityMapper streamEntityMapper) {
    this.streamEntityMapper = streamEntityMapper;
  }

  public OnBoardingStream map(OnBoardingStreamEntity value) {
    OnBoardingStream suggestedStream = new OnBoardingStream();
    suggestedStream.setStream(streamEntityMapper.transform(value.getStreamEntity()));
    suggestedStream.setDefaultValue(value.isFavorite());
    return suggestedStream;
  }

  public List<OnBoardingStream> map(List<OnBoardingStreamEntity> values) {
    List<OnBoardingStream> returnValues = new ArrayList<>(values.size());
    for (OnBoardingStreamEntity value : values) {
      returnValues.add(map(value));
    }
    return returnValues;
  }
}
