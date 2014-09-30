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

    public List<Follow> getFollows(Long idUser, Long lastModifiedDate, int typeFollow) throws IOException;

    public List<User> getUsersByUserIdList(List<Long> userIds) throws IOException;

    public List<Shot> getNewShots(List<Long> followingUserIds, Long newestShotDate) throws IOException;

    public List<Shot> getOlderShots(List<Long> followingUserIds, Long oldestShotDate) throws IOException;

    public List<Shot> getShotsByUserIdList(List<Long> followingUserIds, Long lastModifiedDate) throws IOException;

    public Shot postNewShot(Long idUser, String comment) throws IOException;

    public User getUserByIdUser(Long idUser) throws IOException;
}
