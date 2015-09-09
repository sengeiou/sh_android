package com.shootr.android.task.jobs.follows;

import android.app.Application;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.data.bus.Main;
import com.shootr.android.data.entity.FollowEntity;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.utils.TimeUtils;
import com.shootr.android.service.dataservice.dto.UserDtoFactory;
import com.shootr.android.task.events.follows.FollowUnFollowResultEvent;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.squareup.otto.Bus;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import javax.inject.Inject;
import timber.log.Timber;

public class GetFollowUnFollowUserOfflineJob  extends ShootrBaseJob<FollowUnFollowResultEvent> {

    private static final Boolean NOT_FOLLOWING = false;
    private static final Boolean FOLLOWING = true;

    private static final int PRIORITY = 9; //TODO Define next values for our queue

    private final UserManager userManager;
    private final FollowManager followManager;
    private final SessionRepository sessionRepository;
    private final TimeUtils timeUtils;

    private String idUser;
    private int followUnfollowType;

    @Inject
    public GetFollowUnFollowUserOfflineJob(Application application, NetworkUtil networkUtil, @Main Bus bus, UserManager userManager, FollowManager followManager,
      SessionRepository sessionRepository, TimeUtils timeUtils) {
        super(new Params(PRIORITY), application, bus, networkUtil);
        this.userManager = userManager;
        this.followManager = followManager;
        this.sessionRepository = sessionRepository;
        this.timeUtils = timeUtils;
    }

    public void init(String idUser, int followUnfollowType){
        this.idUser = idUser;
        this.followUnfollowType = followUnfollowType;
    }

    @Override protected void run() throws SQLException, IOException, Exception {
        switch (followUnfollowType){
            case UserDtoFactory.FOLLOW_TYPE:
                followUser(idUser);
                break;
            case UserDtoFactory.UNFOLLOW_TYPE:
                unfollowUser(idUser);
                break;
        }
    }

    private void followUser(String idUserToFollow) {
        if (!isFollowing(idUserToFollow)) {
            FollowEntity followWithUser = performFollow(idUserToFollow);

            saveFollowRelationship(followWithUser);

            storeUserFollowed(idUserToFollow);
            success(idUserToFollow, FOLLOWING);
        } else {
            Timber.w("Can't follow, already following");
            success(idUserToFollow, FOLLOWING);
        }
    }

    private void unfollowUser(String idUserToUnfollow) {
        if (isFollowing(idUserToUnfollow)) {
            FollowEntity followNotFollowing = performUnfollow(idUserToUnfollow);
            saveFollowRelationship(followNotFollowing);

            success(idUserToUnfollow, NOT_FOLLOWING);
        } else {
            Timber.w("Can't unfollow, not following already");
            success(idUserToUnfollow, NOT_FOLLOWING);
        }
    }

    private FollowEntity performUnfollow(String idUserToUnfollow) {
        FollowEntity followWithUser = getExistingFollow(idUserToUnfollow);
        followWithUser.setDeleted(timeUtils.getCurrentDate());
        followWithUser.setSynchronizedStatus("D");
        return followWithUser;
    }

    private void success(String followingUserId, Boolean following) {
        postSuccessfulEvent(new FollowUnFollowResultEvent(followingUserId, following));
    }

    private UserEntity storeUserFollowed(String idUserToFollow) {
        //TODO este método no tiene mucho sentido, la idea es que si el usuario seguido no existe en local lo guarde, pero
        //TODO dado que el job es offline sólo lo saca de local. O sea, no hará nada útil
        UserEntity userToFollow = getUser(idUserToFollow);
        if (userToFollow != null) {
            userManager.saveUser(userToFollow);
        } else {
            Timber.w("Woops, trying to store a null user. This is a big problem, bro.");
        }
        return userToFollow;
    }

    private void saveFollowRelationship(FollowEntity followRelationshipWithUser) {
        followManager.saveFollow(followRelationshipWithUser);
    }

    private FollowEntity performFollow(String idUserToFollow) {
        Date currentDate = timeUtils.getCurrentDate();
        FollowEntity follow = getExistingFollow(idUserToFollow);
        if (follow == null) {
            follow = new FollowEntity();
            follow.setSynchronizedStatus("N");
            follow.setIdUser(currentUserId());
            follow.setFollowedUser(idUserToFollow);
            follow.setRevision(0);
            follow.setBirth(currentDate);
        } else {
            follow.setSynchronizedStatus("U");
            follow.setRevision(follow.getRevision() + 1);
            follow.setDeleted(null);
        }
        follow.setModified(currentDate);
        return follow;

    }

    private FollowEntity getExistingFollow(String idUserFollowing) {
        return followManager.getFollowByUserIds(currentUserId(), idUserFollowing);
    }

    private UserEntity getUser(String idUserToFollow) {
        return userManager.getUserByIdUser(idUserToFollow);
        //TODO y qué pasa si no lo tengo y estoy offline? Eh? EH!!?
    }

    private boolean isFollowing(String idUserFollowing) {
        int followRelationshipState = followManager.getFollowRelationshipState(currentUserId(), idUserFollowing);
        return followRelationshipState == FollowEntity.RELATIONSHIP_FOLLOWING || followRelationshipState == FollowEntity.RELATIONSHIP_BOTH;
    }

    private String currentUserId() {
        return sessionRepository.getCurrentUserId();
    }

    @Override protected boolean isNetworkRequired() {
        return false;
    }

}
