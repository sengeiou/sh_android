package gm.mobi.android.db.objects;

public class User extends Synchronized {

    private Integer mIdUser;
    private Integer mFavouriteTeamId;
    private String mSessionToken;
    private String mUserName;
    private String mEmail;
    private String mName;
    private String mPhoto;
    private Integer mPoints;
    private Integer mNumFollowings;
    private Integer mNumFollowers;

    public Integer getIdUser() {
        return mIdUser;
    }

    public void setIdUser(Integer idUser) {
        mIdUser = idUser;
    }

    public Integer getFavouriteTeamId() {
        return mFavouriteTeamId;
    }

    public void setFavouriteTeamId(Integer favouriteTeamId) {
        mFavouriteTeamId = favouriteTeamId;
    }

    public String getSessionToken() {
        return mSessionToken;
    }

    public void setSessionToken(String sessionToken) {
        mSessionToken = sessionToken;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPhoto() {
        return mPhoto;
    }

    public void setPhoto(String photo) {
        mPhoto = photo;
    }

    public Integer getPoints() {
        return mPoints;
    }

    public void setPoints(Integer points) {
        mPoints = points;
    }

    public Integer getNumFollowings() {
        return mNumFollowings;
    }

    public void setNumFollowings(Integer numFollowings) {
        mNumFollowings = numFollowings;
    }

    public Integer getNumFollowers() {
        return mNumFollowers;
    }

    public void setNumFollowers(Integer numFollowers) {
        mNumFollowers = numFollowers;
    }
}
