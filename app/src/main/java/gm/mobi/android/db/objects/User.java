package gm.mobi.android.db.objects;

import java.io.Serializable;

public class User extends Synchronized implements Serializable{

    private Long mIdUser;
    private Long mFavouriteTeamId;
    private String mSessionToken;
    private String mUserName;
    private String mEmail;
    private String mName;
    private String mPhoto;
    private Long mPoints;
    private Long mNumFollowings;
    private Long mNumFollowers;
    private Long mRank;
    private String mWebsite;
    private String mBio;

    public Long getIdUser() {
        return mIdUser;
    }

    public void setIdUser(Long idUser) {
        mIdUser = idUser;
    }

    public Long getFavouriteTeamId() {
        return mFavouriteTeamId;
    }

    public void setFavouriteTeamId(Long favouriteTeamId) {
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

    public Long getPoints() {
        return mPoints;
    }

    public void setPoints(Long points) {
        mPoints = points;
    }

    public Long getNumFollowings() {
        return mNumFollowings;
    }

    public void setNumFollowings(Long numFollowings) {
        mNumFollowings = numFollowings;
    }

    public Long getNumFollowers() {
        return mNumFollowers;
    }

    public void setNumFollowers(Long numFollowers) {
        mNumFollowers = numFollowers;
    }

    public Long getRank() {
        return mRank;
    }

    public void setRank(Long rank) {
        mRank = rank;
    }

    public String getWebsite() {
        return mWebsite;
    }

    public void setWebsite(String website) {
        mWebsite = website;
    }

    public String getBio() {
        return mBio;
    }

    public void setBio(String bio) {
        mBio = bio;
    }

    @Override
    public String toString() {
        return "User{" +
                "mIdUser=" + mIdUser +
                ", mFavouriteTeamId=" + mFavouriteTeamId +
                ", mSessionToken='" + mSessionToken + '\'' +
                ", mUserName='" + mUserName + '\'' +
                ", mEmail='" + mEmail + '\'' +
                ", mName='" + mName + '\'' +
                ", mPhoto='" + mPhoto + '\'' +
                ", mPoints=" + mPoints +
                ", mNumFollowings=" + mNumFollowings +
                ", mNumFollowers=" + mNumFollowers +
                ", mRank=" + mRank +
                ", mWebsite='" + mWebsite + '\'' +
                ", mBio='" + mBio + '\'' +
                '}';
    }
}
