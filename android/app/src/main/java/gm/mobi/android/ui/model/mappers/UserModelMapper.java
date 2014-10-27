package gm.mobi.android.ui.model.mappers;

import gm.mobi.android.db.objects.FollowEntity;
import gm.mobi.android.db.objects.UserEntity;
import gm.mobi.android.ui.model.UserModel;

public class UserModelMapper {

    public UserModel toUserModel(UserEntity user, FollowEntity follow, boolean isMe){
        UserModel userModel = new UserModel();
        userModel.setIdUser(user.getIdUser());
        userModel.setBio(user.getBio());
        userModel.setUserName(user.getUserName());
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
        if(f!=null && f.getCsys_deleted() == null){
            return FollowEntity.RELATIONSHIP_FOLLOWING;
        }else{
            return FollowEntity.RELATIONSHIP_NONE;
        }
    }
}
