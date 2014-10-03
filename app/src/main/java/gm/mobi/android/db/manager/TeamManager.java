package gm.mobi.android.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

import javax.inject.Inject;

import gm.mobi.android.db.GMContract.TeamTable;
import gm.mobi.android.db.GMContract.SyncColumns;
import gm.mobi.android.db.mappers.TeamMapper;
import gm.mobi.android.db.objects.TableSync;
import gm.mobi.android.db.objects.Team;
import timber.log.Timber;

public class TeamManager extends AbstractManager{

    @Inject
    public TeamManager(){

    }

    /**
     * Insert in database a list of teams
     * */
    public void saveTeams( List<Team> teams) {
        //Save teams
        for (Team team : teams) {
            ContentValues contentValues = TeamMapper.toContentValues(team);
            String args = TeamTable.ID_TEAM+"=?";
            String[] where = new String[]{String.valueOf(team.getIdTeam())};
            if (contentValues.getAsLong(SyncColumns.CSYS_DELETED) != null) {
                deleteTeam( team);
            } else {
                insertOrUpdate(TeamTable.TABLE,contentValues, TeamTable.PROJECTION, args,where);
                Timber.i("Team inserted ", team.getClubName());
            }
        }
        insertTeamInTableSync();
    }

    /**
     * Retrieve a team by its identifier
     * */
    public Team getTeamByIdTeam( Long teamId){
        Team resTeam = new Team();
        String args = TeamTable.ID_TEAM+"=?";
        String[] argsString = new String[]{String.valueOf(teamId)};

        if(isTableEmpty(TeamTable.TABLE)){
            Timber.e("La tabla %s está vacia", TeamTable.TABLE);
        }
        Cursor c = db.query(TeamTable.TABLE, TeamTable.PROJECTION,args,argsString,null,null,null,null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            resTeam = TeamMapper.fromCursor(c);
            }
        c.close();
        return resTeam;
    }

    /**
     * Insert a single item in table
     * */
    public void insertOrUpdateTeam(Team team){
        String args = TeamTable.ID_TEAM+"=?";
        String[] where = new String[]{String.valueOf(team.getIdTeam())};
        ContentValues contentValues = TeamMapper.toContentValues(team);
        insertOrUpdate(TeamTable.TABLE,contentValues, TeamTable.PROJECTION, args,where);
    }

    /**
     * Delete one Follow
     */
    public long deleteTeam( Team team) {
        long res = 0;
        String args = TeamTable.ID_TEAM + "=?";
        String[] stringArgs = new String[]{String.valueOf(team.getIdTeam())};

        Cursor c = db.query(TeamTable.TABLE, TeamTable.PROJECTION, args, stringArgs, null, null, null);
        if (c.getCount() > 0) {
            res = db.delete(TeamTable.TABLE, TeamTable.ID_TEAM + "=?",
                    new String[]{String.valueOf(team.getIdTeam())});
        }
        c.close();
        return res;
    }

    /**
     * Insert team in syncTable
     * */
    private long insertTeamInTableSync(){
        TableSync tablesSync = new TableSync();
        tablesSync.setOrder(4); // It's the second data type the application insert in database
        tablesSync.setDirection("BOTH");
        tablesSync.setEntity(TeamTable.TABLE);
        tablesSync.setMax_timestamp(System.currentTimeMillis());

        if(isTableEmpty( TeamTable.TABLE)){
            tablesSync.setMin_timestamp(System.currentTimeMillis());
        }

        return insertOrUpdateSyncTable(tablesSync);
    }
}
