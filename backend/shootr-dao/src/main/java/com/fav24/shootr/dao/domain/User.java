package com.fav24.shootr.dao.domain;

public class User extends Synchronized {

	private Long idUser;
	private Long idFavoriteTeam;
	private String favoriteTeamName;
	private String sessionToken;
	private String userName;
	private Long numFollowings;
	private Long numFollowers;
	private String email;
	private String name;
	private String password;
	private String photo;
	private String bio;
	private String website;
	private Long points;
	private Long rank;

	public Long getIdUser() {
		return idUser;
	}

	public void setIdUser(Long idUser) {
		this.idUser = idUser;
	}

	public Long getIdFavoriteTeam() {
		return idFavoriteTeam;
	}

	public void setIdFavoriteTeam(Long idFavoriteTeam) {
		this.idFavoriteTeam = idFavoriteTeam;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public Long getPoints() {
		return points;
	}

	public void setPoints(Long points) {
		this.points = points;
	}

	public Long getRank() {
		return rank;
	}

	public void setRank(Long rank) {
		this.rank = rank;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("User [idUser=").append(idUser).append(", userName=").append(userName).append(", numFollowings=").append(numFollowings).append(", numFollowers=").append(numFollowers)
				.append(", name=").append(name).append("]");
		return builder.toString();
	}
}
