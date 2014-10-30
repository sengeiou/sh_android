using Bagdad.Utils;
using Bagdad.ViewModels;
using SQLiteWinRT;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Bagdad.Models
{
    public partial class Watch : BaseModelJsonConstructor
    {
        public async override Task<int> SaveData(List<BaseModelJsonConstructor> watches)
        {
            int done = 0;
            Database database;

            try
            {

                database = await DataBaseHelper.GetDatabaseAsync();
                using (var custstmt = await database.PrepareStatementAsync(SQLQuerys.InsertWatchesData))
                {

                    foreach (Watch watch in watches)
                    {
                        //idMatch, matchDate, status, idLocalTeam, idVisitorTeam, localTeamName, visitorTeamName
                        custstmt.Reset();

                        custstmt.BindIntParameterWithName("@idUser", watch.idUser);
                        custstmt.BindIntParameterWithName("@idMatch", watch.idMatch);
                        custstmt.BindIntParameterWithName("@status", watch.status);
                        
                        custstmt.BindTextParameterWithName("@csys_birth", Util.FromUnixTime(watch.csys_birth.ToString()).ToString("s").Replace('T', ' '));
                        custstmt.BindTextParameterWithName("@csys_modified", Util.FromUnixTime(watch.csys_modified.ToString()).ToString("s").Replace('T', ' '));
                        if (watch.csys_deleted == 0)
                            custstmt.BindNullParameterWithName("@csys_deleted");
                        else
                            custstmt.BindTextParameterWithName("@csys_deleted", Util.FromUnixTime(watch.csys_deleted.ToString()).ToString("s").Replace('T', ' '));
                        custstmt.BindIntParameterWithName("@csys_revision", watch.csys_revision);
                        custstmt.BindTextParameterWithName("@csys_synchronized", "S");


                        await custstmt.StepAsync().AsTask().ConfigureAwait(false);
                        done++;
                    }
                }
                DataBaseHelper.DBLoaded.Set();
            }
            catch (Exception e)
            {
                string sError = Database.GetSqliteErrorCode(e.HResult).ToString();
                DataBaseHelper.DBLoaded.Set();
                throw new Exception("E R R O R - Watch - SaveData: " + sError + " / " + e.Message);
            }
            return done;
        }
    }
}
