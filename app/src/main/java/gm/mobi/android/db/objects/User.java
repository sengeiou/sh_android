package gm.mobi.android.db.objects;

public class User extends Synchronized {

    private Integer id;
    private Integer favouriteTeamId;
    private String sessionToken;
    private String userName;
    private String email;
    private String name;
    private String photo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFavouriteTeamId() {
        return favouriteTeamId;
    }

    public void setFavouriteTeamId(Integer favouriteTeamId) {
        this.favouriteTeamId = favouriteTeamId;
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
}
