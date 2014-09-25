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
    class Shot
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
                    using (var custstmt = await database.PrepareStatementAsync(SQLQuerys.InsertShotData))
                    {
                        await database.ExecuteStatementAsync("BEGIN TRANSACTION");

                        foreach (JToken shot in job["ops"][0]["data"])
                        {
                            //idShot, idUser, comment, csys_birth, csys_modified, csys_revision, csys_deleted, csys_synchronized
                            custstmt.Reset();

                            if (shot["idShot"] == null || String.IsNullOrEmpty(shot["idShot"].ToString()))
                                custstmt.BindNullParameterWithName("@idShot");
                            else
                                custstmt.BindIntParameterWithName("@idShot", int.Parse(shot["idShot"].ToString()));

                            if (shot["idUser"] == null || String.IsNullOrEmpty(shot["idUser"].ToString()))
                                custstmt.BindNullParameterWithName("@idUser");
                            else
                                custstmt.BindIntParameterWithName("@idUser", int.Parse(shot["idUser"].ToString()));

                            if (shot["comment"] == null || String.IsNullOrEmpty(shot["comment"].ToString()))
                                custstmt.BindNullParameterWithName("@comment");
                            else
                                custstmt.BindTextParameterWithName("@comment", shot["comment"].ToString());

                            if (shot["birth"] == null || String.IsNullOrEmpty(shot["birth"].ToString()))
                                custstmt.BindNullParameterWithName("@csys_birth");
                            else
                                custstmt.BindTextParameterWithName("@csys_birth", Util.FromUnixTime(shot["birth"].ToString()).ToString("s").Replace('T', ' '));

                            if (shot["modified"] == null || String.IsNullOrEmpty(shot["modified"].ToString()))
                                custstmt.BindNullParameterWithName("@csys_modified");
                            else
                                custstmt.BindTextParameterWithName("@csys_modified", Util.FromUnixTime(shot["modified"].ToString()).ToString("s").Replace('T', ' '));

                            if (shot["deleted"] == null || String.IsNullOrEmpty(shot["deleted"].ToString()))
                                custstmt.BindNullParameterWithName("@csys_deleted");
                            else
                                custstmt.BindTextParameterWithName("@csys_deleted", Util.FromUnixTime(shot["deleted"].ToString()).ToString("s").Replace('T', ' '));

                            if (shot["revision"] == null || String.IsNullOrEmpty(shot["revision"].ToString()))
                                custstmt.BindNullParameterWithName("@csys_revision");
                            else
                                custstmt.BindIntParameterWithName("@csys_revision", int.Parse(shot["revision"].ToString()));

                            custstmt.BindTextParameterWithName("@csys_synchronized", "S");

                            await custstmt.StepAsync().AsTask().ConfigureAwait(false);
                            done++;
                        }
                        await database.ExecuteStatementAsync("COMMIT TRANSACTION");
                    }
                }
                App.DBLoaded.Set();
            }
            catch (Exception e)
            {
                App.DBLoaded.Set();
                throw new Exception("E R R O R - Shot - saveData: " + e.Message);
            }
            return done;
        }

        

        public string constructFilterShot(string conditionDate)
        {
            return "\"filterItems\":[{\"comparator\":\"eq\",\"name\":\"idUser\",\"value\":" + App.ID_PLAYER + "}],\"filters\":[" + conditionDate + "],\"nexus\":\"and\"";
        }

    }
}
