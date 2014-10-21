package gm.mobi.android.ui.model.mappers;

import gm.mobi.android.db.objects.FollowEntity;
import gm.mobi.android.db.objects.UserEntity;
import gm.mobi.android.ui.model.UserModel;
import java.util.Date;

public class UserModelMapper {

    public UserModel toUserModel(UserEntity user, FollowEntity follow, Long currentUserId){
        UserModel userVO = new UserModel();
        userVO.setIdUser(user.getIdUser());
        userVO.setBio(user.getBio());
        userVO.setUserName(user.getUserName());
        userVO.setFavoriteTeamName(user.getFavoriteTeamName());
        userVO.setName(user.getName());
        userVO.setNumFollowers(user.getNumFollowers());
        userVO.setNumFollowings(user.getNumFollowings());
        userVO.setPhoto(user.getPhoto());
        userVO.setRelationship(user.getIdUser().equals(currentUserId) ? FollowEntity.RELATIONSHIP_OWN : getRelationShip(follow));
        userVO.setWebsite(user.getWebsite());
        return userVO;
    }

    public UserEntity userByUserVO(UserModel userVO){
        UserEntity user = new UserEntity();
        user.setIdUser(userVO.getIdUser());
        user.setBio(userVO.getBio());
        user.setFavoriteTeamName(userVO.getFavoriteTeamName());
        user.setUserName(userVO.getUserName());
        user.setName(userVO.getName());
        user.setWebsite(userVO.getWebsite());
        user.setNumFollowers(userVO.getNumFollowers());
        user.setNumFollowings(userVO.getNumFollowings());
        user.setPhoto(userVO.getPhoto());
        user.setCsys_birth(new Date());
        user.setCsys_modified(new Date());
        user.setCsys_revision(0);
        return user;
    }

    private int getRelationShip(FollowEntity f){
        if(f!=null){
            return FollowEntity.RELATIONSHIP_FOLLOWING;
        }else{
            return FollowEntity.RELATIONSHIP_NONE;
        }
    }
}
