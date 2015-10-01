package com.shootr.android.data.api.service;

import com.shootr.android.data.api.entity.UserApiEntity;
import com.shootr.android.data.api.exception.ApiException;
import com.shootr.android.data.entity.SuggestedPeopleEntity;
import com.shootr.android.data.entity.UserEntity;
import java.io.IOException;
import java.util.List;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.DELETE;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

public interface UserApiService {

    @GET("/user/id/{idUser}")
    UserEntity getUser(@Path("idUser") String idUser) throws IOException, ApiException;

    @GET("/user/{username}")
    UserEntity getUserByUsername(@Path("username") String username) throws IOException, ApiException;

    @GET("/user/{idUser}/following")
    List<UserEntity> getFollowing(@Path("idUser") String idUser) throws IOException, ApiException;

    @GET("/user/{idUser}/followers")
    List<UserEntity> getFollowers(@Path("idUser") String idUser) throws IOException, ApiException;

    @GET("/streams/{idStream}/participants")
    List<UserEntity> getAllParticipants(@Path("idStream") String idStream, @Query("maxJoinDate") Long maxJoinDate) throws IOException, ApiException;

    @GET("/streams/{idStream}/participants/search")
    List<UserEntity> findParticipants(@Path("idStream") String idStream, @Query("query") String query) throws IOException, ApiException;

    @PUT("/streams/{idStream}/watch")
    Response watch(@Path("idStream") String idStream) throws IOException, ApiException;

    @PUT("/user/unwatch")
    Response unwatch() throws IOException, ApiException;

    @PUT("/user/")
    UserEntity putUser(@Body UserApiEntity userApiEntity) throws IOException, ApiException;

    @GET("/user/suggested")
    List<SuggestedPeopleEntity> getSuggestedPeople() throws IOException, ApiException;

    @GET("/user/search/{query}?includeLinks=false&includeEmbed=false")
    List<UserEntity> search(@Path("query") String query, @Query("page") int pageOffset) throws IOException, ApiException;

    @POST("/user/{idUser}/follow")
    Response follow(@Path("idUser") String idUser) throws IOException, ApiException;

    @DELETE("/user/{idUser}/follow")
    Response unfollow(@Path("idUser") String idUser) throws IOException, ApiException;
}
