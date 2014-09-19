using Bagdad.Utils;
using Newtonsoft.Json.Linq;
using SQLiteWinRT;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bagdad.Models
{
    class Follow
    {

        public async Task<int> saveData(JObject job)
        {
            int done = 0;
            Database database;

            try
            {
                if (job["status"]["code"].ToString().Equals("OK") && !job["ops"][0]["metadata"]["totalItems"].ToString().Equals("0"))
                {
                    database = await App.GetDatabaseAsync();
                    using (var custstmt = await database.PrepareStatementAsync(SQLQuerys.InsertFollowData))
                    {
                        await database.ExecuteStatementAsync("BEGIN TRANSACTION");

                        foreach (JToken follow in job["ops"][0]["data"])
                        {
                            //idUser, idUserFollowed, csys_birth, csys_modified, csys_revision, csys_deleted, csys_synchronized


                            if (follow["idUser"] == null || String.IsNullOrEmpty(follow["idUser"].ToString()))
                                custstmt.BindNullParameterWithName("@idUser");
                            else
                                custstmt.BindIntParameterWithName("@idUser", int.Parse(follow["idUser"].ToString()));

                            if (follow["idUserFollowed"] == null || String.IsNullOrEmpty(follow["idUserFollowed"].ToString()))
                                custstmt.BindNullParameterWithName("@idUserFollowed");
                            else
                                custstmt.BindIntParameterWithName("@idUserFollowed", int.Parse(follow["idUserFollowed"].ToString()));

                            if (follow["birth"] == null || String.IsNullOrEmpty(follow["birth"].ToString()))
                                custstmt.BindNullParameterWithName("@csys_birth");
                            else
                                custstmt.BindTextParameterWithName("@csys_birth", Util.FromUnixTime(follow["birth"].ToString()).ToString("s").Replace('T', ' '));

                            if (follow["modified"] == null || String.IsNullOrEmpty(follow["modified"].ToString()))
                                custstmt.BindNullParameterWithName("@csys_modified");
                            else
                                custstmt.BindTextParameterWithName("@csys_modified", Util.FromUnixTime(follow["modified"].ToString()).ToString("s").Replace('T', ' '));

                            if (follow["deleted"] == null || String.IsNullOrEmpty(follow["deleted"].ToString()))
                                custstmt.BindNullParameterWithName("@csys_deleted");
                            else
                                custstmt.BindTextParameterWithName("@csys_deleted", Util.FromUnixTime(follow["deleted"].ToString()).ToString("s").Replace('T', ' '));

                            if (follow["revision"] == null || String.IsNullOrEmpty(follow["revision"].ToString()))
                                custstmt.BindNullParameterWithName("@csys_revision");
                            else
                                custstmt.BindIntParameterWithName("@csys_revision", int.Parse(follow["revision"].ToString()));

                            custstmt.BindNullParameterWithName("@csys_deleted");
                            custstmt.BindTextParameterWithName("@csys_synchronized", "S");

                            await custstmt.StepAsync().AsTask().ConfigureAwait(false);
                        }
                        await database.ExecuteStatementAsync("COMMIT TRANSACTION");
                    }
                }
                App.DBLoaded.Set();
                done = 1;
            }
            catch (Exception e)
            {
                throw new Exception("E R R O R - User - saveDataPeople: " + e.Message);
            }
            return done;
        }
    }
}
