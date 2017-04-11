package com.shootr.mobile.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.mobile.data.entity.RecentSearchEntity;
import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.data.entity.UserEntity;
import com.shootr.mobile.db.DatabaseContract;
import com.shootr.mobile.db.mappers.RecentSearchEntityDBMapper;
import com.shootr.mobile.db.mappers.StreamEntityDBMapper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.inject.Inject;

public class RecentSearchManager extends AbstractManager {

  @Inject StreamEntityDBMapper streamEntityMapper;
  @Inject RecentSearchEntityDBMapper recentSearchEntityDBMapper;
  @Inject StreamManager streamManager;
  @Inject UserManager userManager;
  private final static String LIMIT_SEARCH = "50";

  @Inject
  public RecentSearchManager(SQLiteOpenHelper openHelper, StreamEntityDBMapper streamEntityDBMapper,
      RecentSearchEntityDBMapper recentSearchEntityDBMapper, StreamManager streamManager,
      UserManager userManager) {
    super(openHelper);
    this.streamEntityMapper = streamEntityDBMapper;
    this.recentSearchEntityDBMapper = recentSearchEntityDBMapper;
    this.streamManager = streamManager;
    this.userManager = userManager;
  }

  public List<RecentSearchEntity> readRecentSearches() {
    List<RecentSearchEntity> recentSearches = readRecentSearchesFromDB();
    List<String> streamsIds = new ArrayList<>();
    List<String> usersIds = new ArrayList<>();
    for (RecentSearchEntity recentSearch : recentSearches) {
      if (recentSearch.getStream() != null) {
        streamsIds.add(recentSearch.getStream().getIdStream());
      } else {
        usersIds.add(recentSearch.getUser().getIdUser());
      }
    }

    List<StreamEntity> streamList = streamManager.getStreamsByIds(streamsIds);
    List<UserEntity> userList = userManager.getUsersByIds(usersIds);

    Iterator<RecentSearchEntity> iteratorSearches = recentSearches.iterator();
    Iterator<StreamEntity> iteratorStream = streamList.iterator();
    Iterator<UserEntity> iteratorUser = userList.iterator();
    while (iteratorSearches.hasNext()) {
      RecentSearchEntity recentSearchEntity = iteratorSearches.next();
      if (recentSearchEntity.getStream() != null) {
        while (iteratorStream.hasNext()) {
          StreamEntity stream = iteratorStream.next();
          if (stream.getIdStream().equals(recentSearchEntity.getStream().getIdStream())) {
            recentSearchEntity.setStream(stream);
            iteratorStream.remove();
            break;
          }
        }
      } else {
        while (iteratorUser.hasNext()) {
          UserEntity user = iteratorUser.next();
          if (user.getIdUser().equals(recentSearchEntity.getUser().getIdUser())) {
            recentSearchEntity.setUser(user);
            iteratorUser.remove();
            break;
          }
        }
      }
    }
    return recentSearches;
  }

  private List<RecentSearchEntity> readRecentSearchesFromDB() {
    Cursor queryResult = getReadableDatabase().query(DatabaseContract.RecentSearchTable.TABLE,
        DatabaseContract.RecentSearchTable.PROJECTION, null, null, null, null,
        DatabaseContract.RecentSearchTable.VISIT_DATE + " DESC");

    List<RecentSearchEntity> recentSearches = new ArrayList<>(queryResult.getCount());
    RecentSearchEntity recentSearchEntity;
    if (queryResult.getCount() > 0) {
      queryResult.moveToFirst();
      do {
        recentSearchEntity = recentSearchEntityDBMapper.fromCursor(queryResult);
        recentSearches.add(recentSearchEntity);
      } while (queryResult.moveToNext());
    }
    queryResult.close();

    return recentSearches;
  }

  private List<RecentSearchEntity> readRecentByType(String type) {

    String whereSelection = DatabaseContract.RecentSearchTable.ITEM_TYPE + " = ?";

    String[] whereArguments = new String[] {
        (type == RecentSearchEntity.STREAM) ? RecentSearchEntity.STREAM : RecentSearchEntity.USER
    };

    Cursor queryResult = getReadableDatabase().query(DatabaseContract.RecentSearchTable.TABLE,
        DatabaseContract.RecentSearchTable.PROJECTION, whereSelection, whereArguments, null, null,
        DatabaseContract.RecentSearchTable.VISIT_DATE + " DESC", LIMIT_SEARCH);

    List<RecentSearchEntity> recentSearches = new ArrayList<>(queryResult.getCount());
    RecentSearchEntity recentSearchEntity;
    if (queryResult.getCount() > 0) {
      queryResult.moveToFirst();
      do {
        recentSearchEntity = recentSearchEntityDBMapper.fromCursor(queryResult);
        recentSearches.add(recentSearchEntity);
      } while (queryResult.moveToNext());
    }
    queryResult.close();

    return recentSearches;
  }

  public void saveRecentSearch(RecentSearchEntity recentSearchEntity) {
    ContentValues contentValues = recentSearchEntityDBMapper.toContentValues(recentSearchEntity);
    getWritableDatabase().insertWithOnConflict(DatabaseContract.RecentSearchTable.TABLE, null,
        contentValues, SQLiteDatabase.CONFLICT_REPLACE);
  }
}
