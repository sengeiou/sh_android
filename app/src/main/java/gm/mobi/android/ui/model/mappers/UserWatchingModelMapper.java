package gm.mobi.android.ui.model.mappers;

import android.content.Context;
import gm.mobi.android.R;
import gm.mobi.android.db.objects.UserEntity;
import gm.mobi.android.ui.model.UserWatchingModel;

public class UserWatchingModelMapper {

    private String watchingText;
    private String notWatchingText;

    public UserWatchingModelMapper(Context context) {
        watchingText = context.getString(R.string.watching_text);
        notWatchingText = context.getString(R.string.watching_not_text);
    }

    public UserWatchingModel toUserWatchingModel(UserEntity user, boolean isWatching, boolean isLive) {
        UserWatchingModel userModel = new UserWatchingModel();
        userModel.setIdUser(user.getIdUser());
        userModel.setFavoriteTeamId(user.getFavoriteTeamId());
        userModel.setPhoto(user.getPhoto());
        userModel.setStatus(getStatusString(isWatching));
        userModel.setUserName(user.getUserName());
        userModel.setLive(isLive && isWatching); //TODO esto es lógica de negocio, no debería estar en un mapper
        return userModel;
    }

    public String getStatusString(boolean isWatching){
        if(isWatching){
            return watchingText;
        } else{
            return notWatchingText;
        }

    }


}
