package gm.mobi.android.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.io.IOException;
import java.util.List;

import gm.mobi.android.db.objects.Follow;
import gm.mobi.android.db.objects.Shot;
import gm.mobi.android.db.objects.User;

public interface BagdadService {

    public User login(String id, String password) throws IOException;

    public List<Follow> getFollows(Integer idUser, Long lastModifiedDate, int typeFollow) throws IOException;

    public List<User> getUsersByUserIdList(List<Integer> userIds) throws IOException;

    public List<Shot> getNewShots(List<Integer> followingUserIds, Long newestShotDate) throws IOException;

    public List<Shot> getOlderShots(List<Integer> followingUserIds, Long oldestShotDate) throws IOException;

    public List<Shot> getShotsByUserIdList(List<Integer> followingUserIds, Long lastModifiedDate) throws IOException;

    public Shot postNewShot(Integer idUser, String comment) throws IOException;

    public User getUserByIdUser(Integer idUser) throws IOException;
}
