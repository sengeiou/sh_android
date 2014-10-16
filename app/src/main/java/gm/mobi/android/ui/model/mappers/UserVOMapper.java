package gm.mobi.android.ui.model.mappers;

import gm.mobi.android.db.objects.Follow;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.ui.model.UserVO;
import java.util.Date;

public class UserVOMapper {

    public UserVO toVO(User user, Follow follow, Long currentUserId){
        UserVO userVO = new UserVO();
        userVO.setIdUser(user.getIdUser());
        userVO.setBio(user.getBio());
        userVO.setUserName(user.getUserName());
        userVO.setFavoriteTeamName(user.getFavoriteTeamName());
        userVO.setFavoriteTeamId(user.getFavoriteTeamId());
        userVO.setName(user.getName());
        userVO.setNumFollowers(user.getNumFollowers());
        userVO.setNumFollowings(user.getNumFollowings());
        userVO.setPhoto(user.getPhoto());
        userVO.setPoints(user.getPoints());
        userVO.setRank(user.getRank());
        userVO.setRelationship(user.getIdUser().equals(currentUserId) ? Follow.RELATIONSHIP_OWN : getRelationShip(follow));
        userVO.setWebsite(user.getWebsite());
        userVO.setCsys_deleted(user.getCsys_deleted());
        userVO.setCsys_revision(user.getCsys_revision());
        userVO.setCsys_birth(user.getCsys_birth());
        userVO.setCsys_modified(user.getCsys_modified());
        return userVO;
    }

    public User userByUserVO(UserVO userVO){
        User user = new User();
        user.setIdUser(userVO.getIdUser());
        user.setBio(userVO.getBio());
        user.setFavoriteTeamName(userVO.getFavoriteTeamName());
        user.setUserName(userVO.getUserName());
        user.setName(userVO.getName());
        user.setWebsite(userVO.getWebsite());
        user.setFavoriteTeamId(userVO.getFavoriteTeamId());
        user.setNumFollowers(userVO.getNumFollowers());
        user.setNumFollowings(userVO.getNumFollowings());
        user.setPhoto(userVO.getPhoto());
        user.setRank(userVO.getRank());
        user.setPoints(userVO.getPoints());
        user.setCsys_birth(new Date());
        user.setCsys_modified(new Date());
        user.setCsys_revision(0);
        return user;
    }

    private int getRelationShip(Follow f){
        if(f!=null){
            return Follow.RELATIONSHIP_FOLLOWING;
        }else{
            return Follow.RELATIONSHIP_NONE;
        }
    }
}
