package gm.mobi.android.db.objects;

import android.support.annotation.NonNull;
import java.io.Serializable;

public class UserEntity extends Synchronized implements Serializable, Comparable<UserEntity>{

    private Long idUser;
    private Long favoriteTeamId;
    private String favoriteTeamName;
    private String sessionToken;
    private String userName;
    private String email;
    private String name;
    private String photo;
    private Long points;
    private Long numFollowings;
    private Long numFollowers;
    private Long rank;
    private String website;
    private String bio;

    public UserEntity(){}

    public UserEntity(Long idUser, Long favoriteTeamId, String sessionToken, String userName, String email, String name,
      String photo, Long points, Long numFollowings, Long numFollowers, Long rank, String website, String bio) {
        this.idUser = idUser;
        this.favoriteTeamId = favoriteTeamId;
        this.sessionToken = sessionToken;
        this.userName = userName;
        this.email = email;
        this.name = name;
        this.photo = photo;
        this.points = points;
        this.numFollowings = numFollowings;
        this.numFollowers = numFollowers;
        this.rank = rank;
        this.website = website;
        this.bio = bio;
    }

    public UserEntity(Long idUser, Long favoriteTeamId, String favoriteTeamName, String userName, String name,
      String photo, Long points, Long numFollowings, Long numFollowers, Long rank, String website, String bio) {
        this.idUser = idUser;
        this.favoriteTeamId = favoriteTeamId;
        this.favoriteTeamName = favoriteTeamName;
        this.userName = userName;
        this.name = name;
        this.photo = photo;
        this.points = points;
        this.numFollowings = numFollowings;
        this.numFollowers = numFollowers;
        this.rank = rank;
        this.website = website;
        this.bio = bio;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public Long getFavoriteTeamId() {
        return favoriteTeamId;
    }

    public void setFavoriteTeamId(Long favoriteTeamId) {
        this.favoriteTeamId = favoriteTeamId;
    }

    public String getFavoriteTeamName() {
        return favoriteTeamName;
    }

    public void setFavoriteTeamName(String favoriteTeamName) {
        this.favoriteTeamName = favoriteTeamName;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
    }

    public Long getNumFollowings() {
        return numFollowings;
    }

    public void setNumFollowings(Long numFollowings) {
        this.numFollowings = numFollowings;
    }

    public Long getNumFollowers() {
        return numFollowers;
    }

    public void setNumFollowers(Long numFollowers) {
        this.numFollowers = numFollowers;
    }

    public Long getRank() {
        return rank;
    }

    public void setRank(Long rank) {
        this.rank = rank;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    @Override public int compareTo(@NonNull UserEntity another) {
        return this.getUserName().compareTo(another.getUserName());
    }
}
