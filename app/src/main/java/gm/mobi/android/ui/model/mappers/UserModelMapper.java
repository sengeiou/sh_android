package gm.mobi.android.ui.model.mappers;

import gm.mobi.android.db.objects.FollowEntity;
import gm.mobi.android.db.objects.UserEntity;
import gm.mobi.android.ui.model.UserModel;
import java.util.Date;

public class UserModelMapper {

    public UserModel toUserModel(UserEntity user, FollowEntity follow, Long currentUserId){
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
        userModel.setRelationship(user.getIdUser().equals(currentUserId) ? FollowEntity.RELATIONSHIP_OWN : getRelationShip(follow));
        userModel.setWebsite(user.getWebsite());
        return userModel;
    }

    public UserEntity userByUserVO(UserModel userModel){
        UserEntity user = new UserEntity();
        user.setIdUser(userModel.getIdUser());
        user.setBio(userModel.getBio());
        user.setFavoriteTeamId(userModel.getFavoriteTeamId());
        user.setFavoriteTeamName(userModel.getFavoriteTeamName());
        user.setUserName(userModel.getUserName());
        user.setName(userModel.getName());
        user.setWebsite(userModel.getWebsite());
        user.setPoints(userModel.getPoints());
        user.setNumFollowers(userModel.getNumFollowers());
        user.setNumFollowings(userModel.getNumFollowings());
        user.setPhoto(userModel.getPhoto());
        user.setCsys_birth(new Date());
        user.setCsys_modified(new Date());
        user.setCsys_revision(0);
        return user;
    }

    private int getRelationShip(FollowEntity f){
        if(f!=null && f.getCsys_deleted() == null){
            return FollowEntity.RELATIONSHIP_FOLLOWING;
        }else{
            return FollowEntity.RELATIONSHIP_NONE;
        }
    }
}
