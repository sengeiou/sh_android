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
    public class Follow : BaseModelJsonConstructor
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
                            custstmt.Reset();

                            if (follow["idUser"] == null || String.IsNullOrEmpty(follow["idUser"].ToString()))
                                custstmt.BindNullParameterWithName("@idUser");
                            else
                                custstmt.BindIntParameterWithName("@idUser", int.Parse(follow["idUser"].ToString()));

                            if (follow["idFollowedUser"] == null || String.IsNullOrEmpty(follow["idFollowedUser"].ToString()))
                                custstmt.BindNullParameterWithName("@idUserFollowed");
                            else
                                custstmt.BindIntParameterWithName("@idUserFollowed", int.Parse(follow["idFollowedUser"].ToString()));

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
                throw new Exception("E R R O R - User - saveDataFollow: " + e.Message);
            }
            return done;
        }

        public async Task<List<int>> getidUserFollowing()
        {
            List<int> listOfidUserFollowing = new List<int>();
            try
            {

                Database database = await App.GetDatabaseAsync();

                string selectQuery = SQLQuerys.SelectIdUserFollowing;

                Statement selectStatement = await database.PrepareStatementAsync(selectQuery);

                selectStatement.BindIntParameterWithName("@idUser", App.ID_USER);

                while (await selectStatement.StepAsync())
                {
                    listOfidUserFollowing.Add(selectStatement.GetIntAt(0));
                }

                App.DBLoaded.Set();

                return listOfidUserFollowing;
            }
            catch (Exception e)
            {
                throw new Exception("Follow - getidUserFollowing: " + e.Message, e);
            }
        }

        protected override String GetEntityName() { return Constants.SERCOM_TB_FOLLOW; }

        public override async Task<string> ConstructFilter(string conditionDate)
        {
            return "\"filterItems\":[{\"comparator\":\"eq\",\"name\":\"idUser\",\"value\":" + App.ID_USER + "},{\"comparator\":\"ne\",\"name\":\"idFollowedUser\",\"value\":null}],\"filters\":[" + conditionDate + "],\"nexus\":\"and\"";
        }

    }
}
