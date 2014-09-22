package gm.mobi.android.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.io.IOException;
import java.util.List;

import gm.mobi.android.db.objects.Follow;
import gm.mobi.android.db.objects.User;

public interface BagdadService {

    public User login(String id, String password) throws IOException;
    public List<Follow> getFollows(Integer idUser, Context context, SQLiteDatabase db) throws IOException;

}
