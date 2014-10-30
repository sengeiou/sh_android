package gm.mobi.android.ui.model.mappers;

import gm.mobi.android.db.objects.FollowEntity;
import gm.mobi.android.db.objects.UserEntity;
import gm.mobi.android.db.objects.WatchEntity;
import gm.mobi.android.ui.model.UserModel;
import gm.mobi.android.ui.model.UserWatchingModel;

public class UserWatchingModelMapper {

    public UserWatchingModel toUserWatchingModel(UserEntity user, boolean isWatching) {
        UserWatchingModel userModel = new UserWatchingModel();
        userModel.setIdUser(user.getIdUser());
        userModel.setFavoriteTeamId(user.getFavoriteTeamId());
        userModel.setPhoto(user.getPhoto());
        userModel.setStatus(isWatchingToString(isWatching)); //TODO guarro!
        userModel.setUserName(user.getUserName());
        return userModel;
    }

    public String isWatchingToString(boolean isWatching){
        if(isWatching){
            return "Is Watching";
        } else{
            return "Is out";
        }

    }


}
