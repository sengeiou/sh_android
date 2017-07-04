package com.shootr.mobile.data.api.service;

import com.shootr.mobile.data.api.entity.FollowingsEntity;
import com.shootr.mobile.data.api.entity.UserApiEntity;
import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.entity.BlockEntity;
import com.shootr.mobile.data.entity.FollowEntity;
import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.data.entity.SuggestedPeopleEntity;
import com.shootr.mobile.data.entity.UserEntity;
import java.io.IOException;
import java.util.List;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

public interface UserApiService {

    @GET("/user/{idUser}") UserEntity getUser(@Path("idUser") String idUser) throws IOException, ApiException;

    @GET("/user?includeLinks=false&includeEmbed=false") UserEntity getUser() throws IOException, ApiException;

    @GET("/user?includeLinks=false&includeEmbed=false") UserEntity getUserByUsername(
      @Query("userName") String username) throws IOException, ApiException;

    @GET("/user/{idUser}/following?includeLinks=false&includeEmbed=false") List<UserEntity> getFollowing(
      @Path("idUser") String idUser, @Query("page") Integer page, @Query("pageSize") Integer pageSize)
      throws IOException, ApiException;

    @GET("/user/{idUser}/followers?includeLinks=false&includeEmbed=false") List<UserEntity> getFollowers(
      @Path("idUser") String idUser, @Query("page") Integer page, @Query("pageSize") Integer pageSize)
      throws IOException, ApiException;

    @GET("/user/streamParticipants/{idStream}") List<UserEntity> getAllParticipants(
      @Path("idStream") String idStream, @Query("maxJoinDate") Long maxJoinDate) throws IOException, ApiException;

    @GET("/user/streamParticipants/{idStream}/search") List<UserEntity> findParticipants(
      @Path("idStream") String idStream, @Query("query") String query) throws IOException, ApiException;

    @PUT("/user/watch/{idStream}") StreamEntity watch(@Path("idStream") String idStream)
      throws IOException, ApiException;

    @PUT("/user/unwatch") Response unwatch() throws IOException, ApiException;

    @PUT("/user/") UserEntity putUser(@Body UserApiEntity userApiEntity) throws IOException, ApiException;

    @GET("/user/suggested") List<SuggestedPeopleEntity> getSuggestedPeople(@Query("locale") String locale)
      throws IOException, ApiException;

    @GET("/user/search/{query}?includeLinks=false&includeEmbed=false") List<UserEntity> search(
      @Path("query") String query, @Query("page") int pageOffset, @Query("locale") String locale)
      throws IOException, ApiException;

    @POST("/follow/{idUser}") Response follow(@Path("idUser") String idUser) throws IOException, ApiException;

    @DELETE("/follow/{idUser}") Response unfollow(@Path("idUser") String idUser) throws IOException, ApiException;

    @POST("/block/{idUser}") Response block(@Path("idUser") String idUser) throws IOException, ApiException;

    @DELETE("/block/{idUser}") Response unblock(@Path("idUser") String idUser) throws IOException, ApiException;

    @GET("/block/") List<BlockEntity> getBlockedIdUsers() throws IOException, ApiException;

    @GET("/follow") List<FollowEntity> getFollows(@Query("idUser") String idUser,
        @Query("page") Integer page, @Query("modifiedTimestamp") Long timestamp)
        throws IOException, ApiException;

    @GET("/user/related?includeLinks=false&includeEmbed=true&"
        + "following=true&followers=false&me=false&streamHolder=false")
    List<UserEntity> getRelatedUsers(@Query("idUser") String idUser,
        @Query("modifiedTimestamp") Long timestamp) throws IOException, ApiException;

    @GET("/followable/following") FollowingsEntity getFollowing(@Query("idUser") String idUser,
        @Query("resultType") String[] type, @Query("sinceTimestamp") Long timestamp)
        throws IOException, ApiException;
}
