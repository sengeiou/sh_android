package gm.mobi.android.ui.model.mappers;

import gm.mobi.android.db.objects.Follow;
import gm.mobi.android.db.objects.Shot;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.ui.model.ShotVO;
import gm.mobi.android.ui.model.UserVO;

public class ShotVOMapper {


    public ShotVO toVO(User user, Follow follow,Shot shot, Long currentUserId){
        ShotVO shotVO = new ShotVO();
        shotVO.setName(user.getName());
        shotVO.setPoints(user.getPoints());
        shotVO.setFavoriteTeamId(user.getFavoriteTeamId());
        shotVO.setRank(user.getRank());
        shotVO.setPhoto(user.getPhoto());
        shotVO.setBio(user.getBio());
        shotVO.setFavoriteTeamName(user.getFavoriteTeamName());
        shotVO.setNumFollowers(user.getNumFollowers());
        shotVO.setNumFollowings(user.getNumFollowings());
        shotVO.setWebsite(user.getWebsite());
        shotVO.setRelationship(
          user.getIdUser().equals(currentUserId) ? Follow.RELATIONSHIP_OWN : getRelationShip(follow));
        shotVO.setIdUser(user.getIdUser());
        shotVO.setUserName(user.getUserName());
        shotVO.setIdShot(shot.getIdShot());
        shotVO.setComment(shot.getComment());
        shotVO.setCsys_birth(shot.getCsys_birth());
        shotVO.setCsys_modified(shot.getCsys_modified());
        shotVO.setCsys_deleted(shot.getCsys_deleted());
        shotVO.setCsys_revision(shot.getCsys_revision());
        return shotVO;
    }


    public UserVO userVOFromShotVO(ShotVO shotVO){
        UserVO user = new UserVO();
        user.setFavoriteTeamId(shotVO.getFavoriteTeamId());
        user.setRelationship(shotVO.getRelationship());
        user.setWebsite(shotVO.getWebsite());
        user.setPoints(shotVO.getPoints());
        user.setIdUser(shotVO.getIdUser());
        user.setBio(shotVO.getBio());
        user.setName(shotVO.getName());
        user.setUserName(shotVO.getUserName());
        user.setFavoriteTeamName(shotVO.getFavoriteTeamName());
        user.setRank(shotVO.getRank());
        user.setPhoto(shotVO.getPhoto());
        user.setNumFollowings(shotVO.getNumFollowings());
        user.setNumFollowers(shotVO.getNumFollowers());
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
