using Bagdad.Utils;
using Bagdad.ViewModels;
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

        public int idUser { get; set; }
        public int idUserFollowed { get; set; }
        public Double csys_birth { get; set; }
        public Double csys_modified { get; set; }
        public Double csys_deleted { get; set; }
        public int csys_revision { get; set; }
        public char csys_synchronized { get; set; }
        private String ops_data = "\"idUser\": null,\"idFollowedUser\": null,\"revision\": null,\"birth\": null,\"modified\": null,\"deleted\": null";

        public override async Task<int> SaveData(List<BaseModelJsonConstructor> follows)
        {
            int done = 0;
            Database database;

            try
            {

                database = await App.GetDatabaseAsync();
                using (var custstmt = await database.PrepareStatementAsync(SQLQuerys.InsertFollowData))
                {
                    await database.ExecuteStatementAsync("BEGIN TRANSACTION");

                    foreach (Follow follow in follows)
                    {
                        //idShot, idUser, comment, csys_birth, csys_modified, csys_revision, csys_deleted, csys_synchronized
                        custstmt.Reset();

                        custstmt.BindIntParameterWithName("@idUser", follow.idUser);
                        custstmt.BindIntParameterWithName("@idUserFollowed", follow.idUserFollowed);
                        custstmt.BindTextParameterWithName("@csys_birth", Util.FromUnixTime(follow.csys_birth.ToString()).ToString("s").Replace('T', ' '));
                        custstmt.BindTextParameterWithName("@csys_modified", Util.FromUnixTime(follow.csys_modified.ToString()).ToString("s").Replace('T', ' '));
                        if (follow.csys_deleted == 0)
                            custstmt.BindNullParameterWithName("@csys_deleted");
                        else
                            custstmt.BindTextParameterWithName("@csys_deleted", Util.FromUnixTime(follow.csys_deleted.ToString()).ToString("s").Replace('T', ' '));
                        custstmt.BindIntParameterWithName("@csys_revision", follow.csys_revision);
                        custstmt.BindTextParameterWithName("@csys_synchronized", "S");


                        await custstmt.StepAsync().AsTask().ConfigureAwait(false);
                        done++;
                    }
                    await database.ExecuteStatementAsync("COMMIT TRANSACTION");
                }
                App.DBLoaded.Set();
            }
            catch (Exception e)
            {
                string sError = Database.GetSqliteErrorCode(e.HResult).ToString();
                App.DBLoaded.Set();
                throw new Exception("E R R O R - Follow - SaveData: " + sError + " / " + e.Message);
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

        protected override String GetOps() { return ops_data; }

        public override async Task<string> ConstructFilter(string conditionDate)
        {
            return "\"filterItems\":[{\"comparator\":\"eq\",\"name\":\"idUser\",\"value\":" + App.ID_USER + "},{\"comparator\":\"ne\",\"name\":\"idFollowedUser\",\"value\":null}],\"filters\":[" + conditionDate + "],\"nexus\":\"and\"";
        }

        public override List<BaseModelJsonConstructor> ParseJson(JObject job)
        {
            List<BaseModelJsonConstructor> follows = new List<BaseModelJsonConstructor>();

            try
            {
                if (job["status"]["code"].ToString().Equals("OK") && !job["ops"][0]["metadata"]["totalItems"].ToString().Equals("0"))
                {
                    foreach (JToken follow in job["ops"][0]["data"])
                    {
                        //idShot, idUser, comment, csys_birth, csys_modified, csys_revision, csys_deleted, csys_synchronized
                        Follow followParse = new Follow();

                        followParse.idUser = int.Parse(follow["idUser"].ToString());
                        followParse.idUserFollowed = int.Parse(follow["idFollowedUser"].ToString());
                        followParse.csys_birth = Double.Parse(follow["birth"].ToString());
                        followParse.csys_modified = Double.Parse(follow["modified"].ToString());
                        Double deleted; if (Double.TryParse(follow["deleted"].ToString(), out deleted))
                            followParse.csys_deleted = deleted;
                        followParse.csys_revision = int.Parse(follow["revision"].ToString());
                        followParse.csys_synchronized = 'S';
                        follows.Add(followParse);
                    }
                }
            }
            catch (Exception e)
            {

                throw new Exception("E R R O R - Shot - ParseJson: " + e.Message);
            }
            return follows;
        }

        public async Task<bool> ImFollowing(int idUser)
        {
            bool imFollowing = false;
            try
            {

                Database database = await App.GetDatabaseAsync();

                Statement selectStatement = await database.PrepareStatementAsync(SQLQuerys.GetFollowByIdUserAndIdUserFollowed);

                selectStatement.BindIntParameterWithName("@idUser", App.ID_USER);
                selectStatement.BindIntParameterWithName("@idUserFollowed", idUser);

                if (await selectStatement.StepAsync() && selectStatement.GetIntAt(0) == App.ID_USER && selectStatement.GetIntAt(1) == idUser)
                {
                    imFollowing = true;
                }

                App.DBLoaded.Set();

            }
            catch (Exception e)
            {
                throw new Exception("Follow - ImFollowing: " + e.Message, e);
            }

            return imFollowing;
        }

        public async Task<List<FollowingViewModel>> GetUserFollowingLocalData(int idUser)
        {
            List<FollowingViewModel> followings = new List<FollowingViewModel>();
            try
            {
                Database db = await App.GetDatabaseAsync();
                Statement st = await db.PrepareStatementAsync(SQLQuerys.GetAllInfoFromFollowings);
                st.BindIntParameterWithName("@idUser", idUser);

                while (await st.StepAsync())
                {
                    followings.Add(new FollowingViewModel() { idUser = st.GetIntAt(0), userNickName = st.GetTextAt(1), userName = st.GetTextAt(2), userImageURL = st.GetTextAt(3), isFollowed = true });
                }
            }
            catch (Exception e)
            {
                throw new Exception("Follow - GetUserFollowingLocalData: " + e.Message, e);
            }
            return followings;
        }

        public async Task<List<FollowingViewModel>> GetUserFollowingFromServer(int idUser, int offset)
        {
            List<int> myFollowings = await getidUserFollowing();

            List<FollowingViewModel> followings = new List<FollowingViewModel>();
            try
            {
                ServiceCommunication sc = new ServiceCommunication();

                String jsonFollow = "{\"status\": {\"message\": null,\"code\": null}," + await sc.GetREQ() + ",\"ops\": [{\"data\": [{\"idFollowedUser\": null}],\"metadata\": {\"items\": " + Constants.SERCOM_PARAM_TIME_LINE_OFFSET_PAG + ",\"TotalItems\": null,\"offset\": " + offset + ",\"operation\": \"retrieve\",\"filter\": {\"filterItems\": [],\"filters\": [{\"filterItems\": [{\"comparator\": \"ne\",\"name\": \"modified\",\"value\": null},{\"comparator\": \"eq\",\"name\": \"deleted\",\"value\": null}],\"filters\": [],\"nexus\": \"or\"},{\"filterItems\": [{\"comparator\": \"eq\",\"name\": \"idUser\",\"value\": " + idUser + "},{\"comparator\": \"ne\",\"name\": \"idFollowedUser\",\"value\": null}],\"filters\": [],\"nexus\": \"and\"}],\"nexus\": \"and\"},\"entity\": \"Follow\"}}]}";

                String jsonUser = "{\"status\": {\"message\": null,\"code\": null}," + await sc.GetREQ() + ",\"ops\": [{\"data\": [{\"idUser\": null,\"userName\": null,\"name\": null,\"photo\": null}],\"metadata\": {\"items\": " + Constants.SERCOM_PARAM_TIME_LINE_OFFSET_PAG + ",\"TotalItems\": null,\"operation\": \"retrieve\",\"filter\": {\"filterItems\": [],\"filters\": [{\"filterItems\": [{\"comparator\": \"ne\",\"name\": \"modified\",\"value\": null},{\"comparator\": \"eq\",\"name\": \"deleted\",\"value\": null}],\"filters\": [],\"nexus\": \"or\"},{\"filterItems\": [";

                JObject responseFollow = JObject.Parse(await sc.doRequest(Constants.SERCOM_OP_MANUAL_JSON_REQUEST, "", jsonFollow, 0));

                int count = 0;

                if (responseFollow["status"]["code"].ToString().Equals("OK") && !responseFollow["ops"][0]["metadata"]["totalItems"].ToString().Equals("0"))
                {
                    foreach (JToken follow in responseFollow["ops"][0]["data"])
                    {
                        if (count != 0) jsonUser += ",";

                        jsonUser += "{\"comparator\": \"eq\",\"name\": \"idUser\",\"value\": " + follow["idFollowedUser"].ToString() + "}";

                        count++;
                    }

                    jsonUser += "],\"filters\": [],\"nexus\": \"or\"}],\"nexus\": \"and\"},\"entity\": \"User\"}}]}";

                    JObject responseUser = JObject.Parse(await sc.doRequest(Constants.SERCOM_OP_MANUAL_JSON_REQUEST, "", jsonUser, 0));


                    if (responseUser["status"]["code"].ToString().Equals("OK") && !responseUser["ops"][0]["metadata"]["totalItems"].ToString().Equals("0"))
                    {
                        foreach (JToken user in responseUser["ops"][0]["data"])
                        {
                            bool iFollow = false;

                            if (myFollowings.Contains(int.Parse(user["idUser"].ToString()))) iFollow = true;

                            followings.Add(new FollowingViewModel() { idUser = int.Parse(user["idUser"].ToString()), userNickName = user["userName"].ToString(), userName = user["name"].ToString(), userImageURL = user["photo"].ToString(), isFollowed = iFollow });
                        }
                    }
                }
            }
            catch (Exception e)
            {
                throw new Exception("Follow - GetUserFollowingLocalData: " + e.Message, e);
            }
            return followings;
        }
    }
}
