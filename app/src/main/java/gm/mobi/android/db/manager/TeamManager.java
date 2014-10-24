package gm.mobi.android.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import gm.mobi.android.db.GMContract.SyncColumns;
import gm.mobi.android.db.GMContract.TeamTable;
import gm.mobi.android.db.mappers.TeamMapper;
import gm.mobi.android.db.objects.TeamEntity;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class TeamManager extends AbstractManager{

    private static final String TEAM_TABLE = TeamTable.TABLE;

    @Inject TeamMapper teamMapper;

    @Inject
    public TeamManager(TeamMapper teamMapper){
        this.teamMapper = teamMapper;
    }

    /**
     * Insert in database a list of teams
     * */
    public void saveTeams( List<TeamEntity> teams) {
        //Save teams
        for (TeamEntity team : teams) {
            ContentValues contentValues = teamMapper.toContentValues(team);
            String args = TeamTable.ID_TEAM+"=?";
            String[] where = new String[]{String.valueOf(team.getIdTeam())};
            if (contentValues.getAsLong(SyncColumns.CSYS_DELETED) != null) {
                deleteTeam( team);
            } else {
                insertOrUpdate(TeamTable.TABLE,contentValues, TeamTable.PROJECTION, args,where);
                Timber.i("Team inserted ", team.getClubName());
            }
        }
        insertInSync();
    }

    /**
     * Retrieve a team by its identifier
     * */
    public TeamEntity getTeamByIdTeam( Long teamId){
        TeamEntity resTeam = null;
        String args = TeamTable.ID_TEAM+"=?";
        String[] argsString = new String[]{String.valueOf(teamId)};

        if(isTableEmpty(TeamTable.TABLE)){
            Timber.e("La tabla %s está vacia", TEAM_TABLE);
        }else{
            Cursor c = db.query(TEAM_TABLE, TeamTable.PROJECTION,args,argsString,null,null,null,null);
            if (c.getCount() > 0) {
                c.moveToFirst();
                resTeam = teamMapper.fromCursor(c);
            }
            c.close();
            return resTeam;
        }

        return null;
    }

    /**
     * Insert a single item in table
     * */
    public void insertOrUpdateTeam(TeamEntity team){
        String args = TeamTable.ID_TEAM+"=?";
        String[] where = new String[]{String.valueOf(team.getIdTeam())};
        ContentValues contentValues = teamMapper.toContentValues(team);
        insertOrUpdate(TEAM_TABLE,contentValues, TeamTable.PROJECTION, args,where);
    }

    /**
     * Delete one Follow
     */
    public long deleteTeam( TeamEntity team) {
        long res = 0;
        String args = TeamTable.ID_TEAM + "=?";
        String[] stringArgs = new String[]{String.valueOf(team.getIdTeam())};

        Cursor c = db.query(TEAM_TABLE, TeamTable.PROJECTION, args, stringArgs, null, null, null);
        if (c.getCount() > 0) {
            res = db.delete(TEAM_TABLE, TeamTable.ID_TEAM + "=?",
                    new String[]{String.valueOf(team.getIdTeam())});
        }
        c.close();
        return res;
    }


   public void insertInSync(){
        insertInTableSync(TEAM_TABLE,4,0,0);
   }

}
