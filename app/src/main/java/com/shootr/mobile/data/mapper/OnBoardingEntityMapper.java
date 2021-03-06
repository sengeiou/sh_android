package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.entity.OnBoardingEntity;
import com.shootr.mobile.domain.model.stream.OnBoarding;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class OnBoardingEntityMapper {

  private final StreamEntityMapper streamEntityMapper;
  private final UserEntityMapper userEntityMapper;

  @Inject public OnBoardingEntityMapper(StreamEntityMapper streamEntityMapper,
      UserEntityMapper userEntityMapper) {
    this.streamEntityMapper = streamEntityMapper;
    this.userEntityMapper = userEntityMapper;
  }

  public OnBoarding map(OnBoardingEntity value, String currentUserId) {
    OnBoarding suggestedStream = new OnBoarding();
    suggestedStream.setStream(streamEntityMapper.transform(value.getStreamEntity()));
    suggestedStream.setUser(userEntityMapper.transform(value.getUser(), currentUserId));
    suggestedStream.setDefaultValue(value.isFavorite());
    return suggestedStream;
  }

  public List<OnBoarding> map(List<OnBoardingEntity> values, String currentIdUser) {
    List<OnBoarding> returnValues = new ArrayList<>(values.size());
    for (OnBoardingEntity value : values) {
      returnValues.add(map(value, currentIdUser));
    }
    return returnValues;
  }
}
