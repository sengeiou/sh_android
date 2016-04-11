package com.shootr.mobile.data.api.entity.mapper;

import com.shootr.mobile.data.api.entity.UserApiEntity;
import com.shootr.mobile.data.entity.UserEntity;
import javax.inject.Inject;

public class UserApiEntityMapper {

    @Inject public UserApiEntityMapper() {
    }

    public UserApiEntity transform(UserEntity userEntity) {
        UserApiEntity userApiEntity = new UserApiEntity();

        userApiEntity.setPhoto(userEntity.getPhoto());
        userApiEntity.setBio(userEntity.getBio());
        userApiEntity.setName(userEntity.getName());
        userApiEntity.setUserName(userEntity.getUserName());
        userApiEntity.setWebsite(userEntity.getWebsite());

        return userApiEntity;
    }
}
