package gm.mobi.android.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.mappers.TeamMapper;
import gm.mobi.android.db.objects.Follow;
import gm.mobi.android.db.objects.TableSync;
import gm.mobi.android.db.objects.Team;
import timber.log.Timber;

public class TeamManager {

    /**
     * Insert in database a list of teams
     * */
    public static void saveTeams(SQLiteDatabase db, List<Team> teams) {
        //Save teams

        for (Team team : teams) {
            ContentValues contentValues = TeamMapper.toContentValues(team);

            if (contentValues.getAsLong(GMContract.SyncColumns.CSYS_DELETED) != null) {
                deleteTeam(db, team);
            } else {
                GeneralManager.insertOrUpdate(db, GMContract.TeamTable.TABLE,contentValues, GMContract.TeamTable.PROJECTION, GMContract.TeamTable.ID_TEAM+"=?",new String[]{String.valueOf(team.getIdTeam())});
                Timber.i("Team inserted ", team.getClubName());
            }
        }
        insertTeamInTableSync(db);
    }

    /**
     * Retrieve a team by its identifier
     * */
    public static Team getTeamByIdTeam(SQLiteDatabase db, Long teamId){
        Team resTeam = new Team();
        String args = GMContract.TeamTable.ID_TEAM+"=?";
        String[] argsString = new String[]{String.valueOf(teamId)};
        if(GeneralManager.isTableEmpty(db, GMContract.TeamTable.TABLE)){
            Timber.e("La tabla %s está vacia", GMContract.TeamTable.TABLE);
        }
        Cursor c = db.query(GMContract.TeamTable.TABLE, GMContract.TeamTable.PROJECTION,args,argsString,null,null,null,null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            resTeam = TeamMapper.fromCursor(c);
            }
        c.close();
        return resTeam;
    }

    /**
     * Delete one Follow
     */
    public static long deleteTeam(SQLiteDatabase db, Team team) {
        long res = 0;
        String args = GMContract.TeamTable.ID_TEAM + "=?";
        String[] stringArgs = new String[]{String.valueOf(team.getIdTeam())};
        Cursor c = db.query(GMContract.TeamTable.TABLE, GMContract.TeamTable.PROJECTION, args, stringArgs, null, null, null);
        if (c.getCount() > 0) {
            res = db.delete(GMContract.TeamTable.TABLE, GMContract.TeamTable.ID_TEAM + "=?",
                    new String[]{String.valueOf(team.getIdTeam())});
        }
        c.close();
        return res;
    }

    /**
     * Insert team in syncTable
     * */
    public static long insertTeamInTableSync(SQLiteDatabase db){
        TableSync tablesSync = new TableSync();
        tablesSync.setOrder(4); // It's the second data type the application insert in database
        tablesSync.setDirection("BOTH");
        tablesSync.setEntity(GMContract.TeamTable.TABLE);
        tablesSync.setMax_timestamp(System.currentTimeMillis());
        if(GeneralManager.isTableEmpty(db, GMContract.TeamTable.TABLE)){
            tablesSync.setMin_timestamp(System.currentTimeMillis());
        }
        //We don't have this information already
//        tablesSync.setMaxRows();
//        tablesSync.setMinRows();

        return SyncTableManager.insertOrUpdateSyncTable(db,tablesSync);
    }
}
