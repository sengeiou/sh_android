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
        return localUserDataSource.getUser(Long.parseLong(user.getIdUser()));
    }

    @Override protected UserEntity updateValues(UserEntity currentEntity, User user) {
        UserEntity userEntityFromDomain = userEntityMapper.transform(user);
        userEntityFromDomain.setCsysModified(new Date());
        userEntityFromDomain.setCsysRevision(currentEntity.getCsysRevision() + 1);
        userEntityFromDomain.setCsysBirth(currentEntity.getCsysBirth());
        userEntityFromDomain.setCsysSynchronized(Synchronized.SYNC_UPDATED);
        userEntityFromDomain.setCsysDeleted(currentEntity.getCsysDeleted());
        return userEntityFromDomain;
    }

    @Override protected UserEntity createNewEntity(User user) {
        UserEntity newEntityFromDomain = userEntityMapper.transform(user);
        newEntityFromDomain.setCsysSynchronized(Synchronized.SYNC_NEW);
        Date birth = new Date();
        newEntityFromDomain.setCsysBirth(birth); //TODO dates from timeutils
        newEntityFromDomain.setCsysModified(birth);
        newEntityFromDomain.setCsysRevision(0);
        return newEntityFromDomain;
    }
}
