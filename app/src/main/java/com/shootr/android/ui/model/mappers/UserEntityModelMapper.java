package com.shootr.android.ui.model.mappers;

import com.shootr.android.data.entity.FollowEntity;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.ui.model.UserModel;

@Deprecated
public class UserEntityModelMapper {

    public UserModel toUserModel(UserEntity user, FollowEntity follow, boolean isMe){
        UserModel userModel = new UserModel();
        userModel.setIdUser(user.getIdUser());
        userModel.setBio(user.getBio());
        userModel.setUsername(user.getUserName());
        userModel.setFavoriteTeamId(user.getFavoriteTeamId());
        userModel.setPoints(user.getPoints());
        userModel.setFavoriteTeamName(user.getFavoriteTeamName());
        userModel.setName(user.getName());
        userModel.setNumFollowers(user.getNumFollowers());
        userModel.setNumFollowings(user.getNumFollowings());
        userModel.setPhoto(user.getPhoto());
        userModel.setRelationship(isMe ? FollowEntity.RELATIONSHIP_OWN : getRelationShip(follow));
        userModel.setWebsite(user.getWebsite());
        return userModel;
    }

    private int getRelationShip(FollowEntity f){
        if(f!=null && f.getCsysBirth()!=null && f.getCsysDeleted() == null){
            return FollowEntity.RELATIONSHIP_FOLLOWING;
        }else{
            return FollowEntity.RELATIONSHIP_NONE;
        }
    }
}
