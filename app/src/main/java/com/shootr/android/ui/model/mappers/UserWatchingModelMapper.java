package gm.mobi.android.ui.model.mappers;

import gm.mobi.android.db.objects.UserEntity;
import gm.mobi.android.ui.model.UserWatchingModel;

public class UserWatchingModelMapper {

    public UserWatchingModel toUserWatchingModel(UserEntity user, boolean isWatching) {
        UserWatchingModel userModel = new UserWatchingModel();
        userModel.setIdUser(user.getIdUser());
        userModel.setFavoriteTeamId(user.getFavoriteTeamId());
        userModel.setPhoto(user.getPhoto());
        userModel.setStatus(getStatusString(isWatching)); //TODO guarro!
        userModel.setUserName(user.getUserName());
        return userModel;
    }

    public String getStatusString(boolean isWatching){
        if(isWatching){
            return "Watching";
        } else{
            return "Not watching";
        }

    }


}
