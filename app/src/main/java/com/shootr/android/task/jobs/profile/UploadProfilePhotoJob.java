package com.shootr.android.task.jobs.profile;

import android.app.Application;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.data.api.entity.mapper.UserApiEntityMapper;
import com.shootr.android.data.api.exception.ApiException;
import com.shootr.android.data.api.service.UserApiService;
import com.shootr.android.data.bus.Main;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.data.mapper.UserEntityMapper;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.domain.repository.PhotoService;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.utils.ImageResizer;
import com.shootr.android.domain.utils.TimeUtils;
import com.shootr.android.task.events.profile.UploadProfilePhotoEvent;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.ui.model.mappers.UserEntityModelMapper;
import com.squareup.otto.Bus;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import javax.inject.Inject;
import org.json.JSONException;

public class UploadProfilePhotoJob extends ShootrBaseJob<UploadProfilePhotoEvent> {

    private static final int PRIORITY = 5;
    private final PhotoService photoService;
    private final UserManager userManager;
    private final SessionRepository sessionRepository;
    private final ImageResizer imageResizer;
    private final UserEntityModelMapper userModelMapper;
    private final TimeUtils timeUtils;
    private final UserEntityMapper userEntityMapper;
    private final UserApiService userApiService;
    private final UserApiEntityMapper userApiEntityMapper;

    private File photoFile;

    @Inject public UploadProfilePhotoJob(Application application, @Main Bus bus, NetworkUtil networkUtil, PhotoService photoService,
      UserManager userManager, SessionRepository sessionRepository, ImageResizer imageResizer, UserEntityModelMapper userModelMapper,
      TimeUtils timeUtils, UserEntityMapper userEntityMapper, UserApiService userApiService,
      UserApiEntityMapper userApiEntityMapper) {
        super(new Params(PRIORITY), application, bus, networkUtil);
        this.photoService = photoService;
        this.userManager = userManager;
        this.sessionRepository = sessionRepository;
        this.imageResizer = imageResizer;
        this.userModelMapper = userModelMapper;
        this.timeUtils = timeUtils;
        this.userEntityMapper = userEntityMapper;
        this.userApiService = userApiService;
        this.userApiEntityMapper = userApiEntityMapper;
    }

    public void init(File photoFile) {
        this.photoFile = photoFile;
    }

    @Override protected void run() throws SQLException, IOException, JSONException {
        File imageFile = getResizedImage(photoFile);
        String photoUrl = uploadPhoto(imageFile);
        UserEntity updatedUser = setCurrentUserPhoto(photoUrl);

        postSuccessfulEvent(new UploadProfilePhotoEvent(userModelMapper.toUserModel(updatedUser, null, true)));
    }

    private UserEntity setCurrentUserPhoto(String photoUrl) throws IOException {
        UserEntity currentUserEntity = userManager.getUserByIdUser(sessionRepository.getCurrentUserId());
        currentUserEntity.setPhoto(photoUrl);
        currentUserEntity.setModified(timeUtils.getCurrentDate());
        currentUserEntity.setRevision(currentUserEntity.getRevision() + 1);
        userManager.saveUser(currentUserEntity);
        try {
            currentUserEntity = userApiService.putUser(userApiEntityMapper.transform(currentUserEntity));
            userManager.saveUser(currentUserEntity);
            sessionRepository.setCurrentUser(userEntityMapper.transform(currentUserEntity, currentUserEntity.getIdUser()));
        } catch (ApiException e) {
            throw new IOException(e);
        }
        return currentUserEntity;
    }

    private String uploadPhoto(File imageFile) throws IOException, JSONException {
        return photoService.uploadProfilePhotoAndGetUrl(imageFile);
    }

    private File getResizedImage(File newPhotoFile) throws IOException {
        return imageResizer.getResizedCroppedImageFile(newPhotoFile);
    }

    @Override protected boolean isNetworkRequired() {
        return true;
    }
}
