package com.shootr.android.data.repository.sync;

import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.data.entity.Synchronized;
import com.shootr.android.data.mapper.UserEntityMapper;
import com.shootr.android.data.repository.datasource.user.UserDataSource;
import com.shootr.android.domain.User;
import com.shootr.android.domain.repository.Local;
import java.util.Date;
import javax.inject.Inject;

public class SyncableUserEntityFactory extends SyncableEntityFactory<User, UserEntity> {

    private UserEntityMapper userEntityMapper;
    private UserDataSource localUserDataSource;

    @Inject public SyncableUserEntityFactory(UserEntityMapper userEntityMapper,
      @Local UserDataSource localUserDataSource) {
        this.userEntityMapper = userEntityMapper;
        this.localUserDataSource = localUserDataSource;
    }

    @Override protected UserEntity currentEntity(User user) {
        return localUserDataSource.getUser(user.getIdUser());
    }

    @Override protected UserEntity updateValues(UserEntity currentEntity, User user) {
        UserEntity userEntityFromDomain = userEntityMapper.transform(user);
        userEntityFromDomain.setModified(new Date());
        userEntityFromDomain.setRevision(currentEntity.getRevision() + 1);
        userEntityFromDomain.setBirth(currentEntity.getBirth());
        userEntityFromDomain.setSynchronizedStatus(Synchronized.SYNC_UPDATED);
        userEntityFromDomain.setDeleted(currentEntity.getDeleted());
        return userEntityFromDomain;
    }

    @Override protected UserEntity createNewEntity(User user) {
        UserEntity newEntityFromDomain = userEntityMapper.transform(user);
        newEntityFromDomain.setSynchronizedStatus(Synchronized.SYNC_NEW);
        Date birth = new Date();
        newEntityFromDomain.setBirth(birth); //TODO dates from timeutils
        newEntityFromDomain.setModified(birth);
        newEntityFromDomain.setRevision(0);
        return newEntityFromDomain;
    }
}
