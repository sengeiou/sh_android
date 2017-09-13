package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.entity.OnBoardingEntity;
import com.shootr.mobile.domain.model.stream.OnBoarding;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class OnBoardingStreamEntityMapper {

  private final StreamEntityMapper streamEntityMapper;
  private final UserEntityMapper userEntityMapper;

  @Inject public OnBoardingStreamEntityMapper(StreamEntityMapper streamEntityMapper,
      UserEntityMapper userEntityMapper) {
    this.streamEntityMapper = streamEntityMapper;
    this.userEntityMapper = userEntityMapper;
  }

  public OnBoarding map(OnBoardingEntity value) {
    OnBoarding suggestedStream = new OnBoarding();
    suggestedStream.setStream(streamEntityMapper.transform(value.getStreamEntity()));
    suggestedStream.setUser(userEntityMapper.transform(value.getUserEntity()));
    suggestedStream.setDefaultValue(value.isFavorite());
    return suggestedStream;
  }

  public List<OnBoarding> map(List<OnBoardingEntity> values) {
    List<OnBoarding> returnValues = new ArrayList<>(values.size());
    for (OnBoardingEntity value : values) {
      returnValues.add(map(value));
    }
    return returnValues;
  }
}
