package gm.mobi.android.ui.model.mappers;

import gm.mobi.android.db.objects.Follow;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.ui.model.UserVO;

public class UserVOMapper {

    public UserVO toVO(User user, Follow follow, Long currentUserId){
        UserVO userVO = new UserVO();
        userVO.setIdUser(user.getIdUser());
        userVO.setBio(user.getBio());
        userVO.setUserName(user.getUserName());
        userVO.setFavoriteTeamName(user.getFavoriteTeamName());
        userVO.setName(user.getName());
        userVO.setNumFollowers(user.getNumFollowers());
        userVO.setNumFollowings(user.getNumFollowings());
        userVO.setPhoto(user.getPhoto());
        userVO.setPoints(user.getPoints());
        userVO.setRank(user.getRank());
        userVO.setRelationship(user.getIdUser().equals(currentUserId) ? Follow.RELATIONSHIP_OWN : getRelationShip(follow));
        userVO.setWebsite(user.getWebsite());
        return userVO;
    }


    private int getRelationShip(Follow f){
        if(f!=null){
            return Follow.RELATIONSHIP_FOLLOWING;
        }else{
            return Follow.RELATIONSHIP_NONE;
        }
    }
}
