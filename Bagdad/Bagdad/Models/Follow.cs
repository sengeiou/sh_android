using Bagdad.Utils;
using Bagdad.ViewModels;
using Newtonsoft.Json.Linq;
using SQLiteWinRT;
using System;
using System.Collections.Generic;
using System.Diagnostics;
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

                database = await DataBaseHelper.GetDatabaseAsync();
                using (var custstmt = await database.PrepareStatementAsync(SQLQuerys.InsertFollowData))
                {

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
                }
                DataBaseHelper.DBLoaded.Set();
            }
            catch (Exception e)
            {
                string sError = Database.GetSqliteErrorCode(e.HResult).ToString();
                DataBaseHelper.DBLoaded.Set();
                throw new Exception("E R R O R - Follow - SaveData: " + sError + " / " + e.Message);
            }
            return done;
        }

        public virtual async Task<List<int>> getidUserFollowing()
        {
            List<int> listOfidUserFollowing = new List<int>();
            try
            {

                Database database = await DataBaseHelper.GetDatabaseAsync();

                string selectQuery = SQLQuerys.SelectIdUserFollowing;

                Statement selectStatement = await database.PrepareStatementAsync(selectQuery);

                selectStatement.BindIntParameterWithName("@idUser", App.ID_USER);

                while (await selectStatement.StepAsync())
                {
                    listOfidUserFollowing.Add(selectStatement.GetIntAt(0));
                }

                DataBaseHelper.DBLoaded.Set();

                return listOfidUserFollowing;
            }
            catch (Exception e)
            {
                throw new Exception("Follow - getidUserFollowing: " + e.Message, e);
            }
        }

        protected override String GetEntityName() { return Constants.SERCOM_TB_FOLLOW; }

        protected override String GetOps() { return ops_data; }

        protected override string GetAlias(string operation)
        {
            return "";
        }

        public override async Task<string> ConstructFilter(string conditionDate)
        {
            return "\"filterItems\":[],\"filters\":[{\"filterItems\":[{\"comparator\":\"eq\",\"name\":\"idUser\",\"value\":" + App.ID_USER + "},{\"comparator\":\"ne\",\"name\":\"idFollowedUser\",\"value\":null}],\"filters\":[],\"nexus\":\"or\"}," + conditionDate + "],\"nexus\":\"and\"";
        }

        public override List<BaseModelJsonConstructor> ParseJson(JObject job)
        {
            List<BaseModelJsonConstructor> follows = new List<BaseModelJsonConstructor>();

            try
            {
                if (job["status"]["code"].ToString().Equals("OK") && !job["ops"][0]["metadata"]["items"].ToString().Equals("0"))
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

                Database database = await DataBaseHelper.GetDatabaseAsync();

                Statement selectStatement = await database.PrepareStatementAsync(SQLQuerys.GetFollowByIdUserAndIdUserFollowed);

                selectStatement.BindIntParameterWithName("@idUser", App.ID_USER);
                selectStatement.BindIntParameterWithName("@idUserFollowed", idUser);

                if (await selectStatement.StepAsync() && selectStatement.GetIntAt(0) == App.ID_USER && selectStatement.GetIntAt(1) == idUser)
                {
                    imFollowing = true;
                }

                DataBaseHelper.DBLoaded.Set();

            }
            catch (Exception e)
            {
                throw new Exception("Follow - ImFollowing: " + e.Message, e);
            }

            return imFollowing;
        }

        public async Task<List<User>> GetUserFollowingLocalData(int idUser, String type)
        {
            String query;
            if (type.Equals(Constants.CONST_FOLLOWING)) query = SQLQuerys.GetAllInfoFromFollowings;
            else query = SQLQuerys.GetAllInfoFromPeople;

            List<User> followings = new List<User>();
            try
            {
                Database db = await DataBaseHelper.GetDatabaseAsync();
                Statement st = await db.PrepareStatementAsync(query);
                st.BindIntParameterWithName("@idUser", idUser);

                while (await st.StepAsync())
                {
                    followings.Add(new User() { idUser = st.GetIntAt(0), userName = st.GetTextAt(1), name = st.GetTextAt(2), photo = st.GetTextAt(3), favoriteTeamName = st.GetTextAt(4)});
                }
            }
            catch (Exception e)
            {
                throw new Exception("Follow - GetUserFollowingLocalData: " + e.Message, e);
            }
            return followings;
        }

        public async Task<List<User>> GetUserFollowingFromServer(int idUser, int offset)
        {
            List<User> followings = new List<User>();
            try
            {
                User user = new User(); 
                ServiceCommunication sc = new ServiceCommunication();

                String jsonFollow = "{\"status\": {\"message\": null,\"code\": null}," + await sc.GetREQ() + ",\"ops\": [{\"data\": [{" + user.GetUserOps() + "}],\"metadata\": {\"items\": " + Constants.SERCOM_PARAM_TIME_LINE_OFFSET_PAG + ",\"TotalItems\": null,\"offset\": " + offset + ",\"operation\": \"retrieve\",\"filter\": {\"filterItems\": [],\"filters\": [{\"filterItems\": [{\"comparator\": \"ne\",\"name\": \"modified\",\"value\": null},{\"comparator\": \"eq\",\"name\": \"deleted\",\"value\": null}],\"filters\": [],\"nexus\": \"or\"},{\"filterItems\": [{\"comparator\": \"eq\",\"name\": \"idUserFollowing\",\"value\": " + idUser + "}],\"filters\": [],\"nexus\": \"and\"}],\"nexus\": \"and\"},\"entity\": \"Following\"}}]}";

                JObject responseFollow = JObject.Parse(await sc.MakeRequestToMemory(jsonFollow));

                if (responseFollow["status"]["code"].ToString().Equals("OK") && !responseFollow["ops"][0]["metadata"]["items"].ToString().Equals("0"))
                {
                    foreach (JToken follow in responseFollow["ops"][0]["data"])
                    {
                        followings.Add(new User() { idUser = int.Parse(follow["idUser"].ToString()), userName = follow["userName"].ToString(), name = follow["name"].ToString(), photo = follow["photo"].ToString(), numFollowers = int.Parse(follow["numFollowers"].ToString()), numFollowing = int.Parse(follow["numFollowings"].ToString()), points = int.Parse(follow["points"].ToString()), bio = follow["bio"].ToString(), website = follow["website"].ToString(), favoriteTeamName = follow["favoriteTeamName"].ToString(), idFavoriteTeam = int.Parse(follow["idFavoriteTeam"].ToString()), csys_revision = int.Parse(follow["revision"].ToString()), csys_birth = double.Parse(follow["birth"].ToString()), csys_modified = double.Parse(follow["modified"].ToString()) });
                    }

                }
            }
            catch (Exception e)
            {
                throw new Exception("Follow - GetUserFollowingFromServer: " + e.Message, e);
            }
            return followings;
        }

        public async Task<List<User>> GetUserFollowersFromServer(int idUser, int offset)
        {
            List<User> followings = new List<User>();
            try
            {
                ServiceCommunication sc = new ServiceCommunication();

                String jsonFollow = "{\"status\": {\"message\": null,\"code\": null}," + await sc.GetREQ() + ",\"ops\": [{\"data\": [{\"idUser\": null,\"idFavoriteTeam\": null,\"favoriteTeamName\": null,\"userName\": null,\"name\": null,\"photo\": null,\"bio\": null,\"website\": null,\"points\": null,\"numFollowings\": null,\"numFollowers\": null,\"revision\": null,\"birth\": null,\"modified\": null,\"deleted\": null}],\"metadata\": {\"items\": " + Constants.SERCOM_PARAM_TIME_LINE_OFFSET_PAG + ",\"TotalItems\": null,\"offset\": " + offset + ",\"operation\": \"retrieve\",\"filter\": {\"filterItems\": [],\"filters\": [{\"filterItems\": [{\"comparator\": \"ne\",\"name\": \"modified\",\"value\": null},{\"comparator\": \"eq\",\"name\": \"deleted\",\"value\": null}],\"filters\": [],\"nexus\": \"or\"},{\"filterItems\": [{\"comparator\": \"eq\",\"name\": \"idUserFollowed\",\"value\": " + idUser + "}],\"filters\": [],\"nexus\": \"and\"}],\"nexus\": \"and\"},\"entity\": \"Followers\"}}]}";

                JObject responseFollow = JObject.Parse(await sc.MakeRequestToMemory(jsonFollow));


                if (responseFollow["status"]["code"].ToString().Equals("OK") && !responseFollow["ops"][0]["metadata"]["items"].ToString().Equals("0"))
                {
                    foreach (JToken follow in responseFollow["ops"][0]["data"])
                    {
                        followings.Add(new User() { idUser = int.Parse(follow["idUser"].ToString()), userName = follow["userName"].ToString(), name = follow["name"].ToString(), photo = follow["photo"].ToString(), numFollowers = int.Parse(follow["numFollowers"].ToString()), numFollowing = int.Parse(follow["numFollowings"].ToString()), points = int.Parse(follow["points"].ToString()), bio = follow["bio"].ToString(), website = follow["website"].ToString(), favoriteTeamName = follow["favoriteTeamName"].ToString(), idFavoriteTeam = int.Parse(follow["idFavoriteTeam"].ToString()), csys_revision = int.Parse(follow["revision"].ToString()), csys_birth = double.Parse(follow["birth"].ToString()), csys_modified = double.Parse(follow["modified"].ToString()) });
                    }

                }
            }
            catch (Exception e)
            {
                throw new Exception("Follow - GetUserFollowingLocalData: " + e.Message, e);
            }
            return followings;
        }

        #region FOLLOW/UNFOLLOW

        public async Task<bool> AddFollowing(User user)
        {
            bool _return = false;
            ServiceCommunication sc = new ServiceCommunication();
            try
            {
                Database db = await DataBaseHelper.GetDatabaseAsync();
                Statement st = await db.PrepareStatementAsync(SQLQuerys.InsertOrReplaceFollowData);
                
                st.BindIntParameterWithName("@idUser", App.ID_USER);
                st.BindIntParameterWithName("@idUserFollowed", user.idUser);
                st.BindTextParameterWithName("@csys_birth", DateTime.UtcNow.ToString("s").Replace('T', ' '));
                st.BindTextParameterWithName("@csys_modified", DateTime.UtcNow.ToString("s").Replace('T', ' '));
                st.BindIntParameterWithName("@csys_revision", 0);
                st.BindNullParameterWithName("@csys_deleted");
                st.BindTextParameterWithName("@csys_synchronized", "N");

                await st.StepAsync();
                DataBaseHelper.DBLoaded.Set();

                _return = await UpdateNumOfFollowings(1, user);
            }
            catch (Exception e)
            {
                string sError = Database.GetSqliteErrorCode(e.HResult).ToString();
                DataBaseHelper.DBLoaded.Set();
                Debug.WriteLine("E R R O R - Follow - AddFollowing: " + sError + " / " + e.Message);
            }
            return _return;
        }

        public async Task<bool> DelFollowing(User user)
        {
            //TODO: Split unfollow sending to server
            bool _return = false;
            ServiceCommunication sc = new ServiceCommunication();

            try
            {
                int _revision = await GetNewRevisionForFollowing(user.idUser);

                Database db = await DataBaseHelper.GetDatabaseAsync();
                Statement st = await db.PrepareStatementAsync(SQLQuerys.LogicDeleteFollowData);

                st.BindIntParameterWithName("@idUser", App.ID_USER);
                st.BindIntParameterWithName("@idUserFollowed", user.idUser);

                st.BindTextParameterWithName("@csys_modified", DateTime.UtcNow.ToString("s").Replace('T', ' '));
                st.BindTextParameterWithName("@csys_deleted", DateTime.UtcNow.ToString("s").Replace('T', ' '));
                st.BindIntParameterWithName("@csys_revision", _revision);
                st.BindTextParameterWithName("@csys_synchronized", "D");

                await st.StepAsync();
                DataBaseHelper.DBLoaded.Set();

                _return = await UpdateNumOfFollowings(-1, user);
            }
            catch (Exception e)
            {
                string sError = Database.GetSqliteErrorCode(e.HResult).ToString();
                DataBaseHelper.DBLoaded.Set();
                Debug.WriteLine("E R R O R - Follow - DelFollowing: " + sError + " / " + e.Message);
            }
            return _return;
        }

        private async Task<int> GetNewRevisionForFollowing(int _idUser)
        {
            int _return = 0;
            try
            {
                Database db = await DataBaseHelper.GetDatabaseAsync();
                Statement st = await db.PrepareStatementAsync(SQLQuerys.GetFollowingRevision);

                st.BindIntParameterWithName("@idUser", App.ID_USER);
                st.BindIntParameterWithName("@idUserFollowed", _idUser);

                if(await st.StepAsync())
                {
                    _return = st.GetIntAt(0) + 1;
                }

                DataBaseHelper.DBLoaded.Set();
            }
            catch (Exception e)
            {
                throw new Exception("Follow - GetNewRevisionForFollowing: " + e.Message, e);
            }
            return _return;
        }

        private async Task<bool> UpdateNumOfFollowings(int _change, User user)
        {
            bool _result = false;

            int actualNumOfFollowings = await GetActualNumOfFollowings() + _change;
            
            if (_change > 0) _result = await user.AddCurrentUserToLocalDB();
            else _result = await user.RemoveCurrentUserFromLocalDB();

            if(_result) _result = await EditNumOfFollowings(actualNumOfFollowings);

            return _result;
        }

        private async Task<int> GetActualNumOfFollowings()
        {
            int _return = 0;
            try
            {
                Database db = await DataBaseHelper.GetDatabaseAsync();
                Statement st = await db.PrepareStatementAsync(SQLQuerys.GetActualNumOfFollowings);

                st.BindIntParameterWithName("@idUser", App.ID_USER);

                if (await st.StepAsync())
                {
                    _return = st.GetIntAt(0);
                }

                DataBaseHelper.DBLoaded.Set();
            }
            catch (Exception e)
            {
                throw new Exception("Follow - GetActualNumOfFollowings: " + e.Message, e);
            }
            return _return;
        }

        private async Task<bool> EditNumOfFollowings(int _newNum)
        {
            bool _return = false;
            try
            {
                Database db = await DataBaseHelper.GetDatabaseAsync();
                Statement st = await db.PrepareStatementAsync(SQLQuerys.EditNumOfFollowings);

                st.BindIntParameterWithName("@idUser", App.ID_USER);
                st.BindIntParameterWithName("@numFollowings", _newNum);

                await st.StepAsync();
                _return = true;

                DataBaseHelper.DBLoaded.Set();
            }
            catch (Exception e)
            {
                throw new Exception("Follow - GetActualNumOfFollowings: " + e.Message, e);
            }
            return _return;
        }


        public async Task<string> SynchronizeFollows()
        {
            try
            {
                List<Follow> follows = await GetFollowsToUpdate();

                if (follows.Count() > 0)
                {
                    String json = "{\"status\": {\"message\": null,\"code\": null}," +
                                "\"req\": [@idDevice,@idUser,@idPlatform,@appVersion,@requestTime]," +
                                "\"ops\": [{@Data\"metadata\": {" +
                                    "\"items\": null," +
                                    "\"TotalItems\": null," +
                                    "\"operation\": \"@Operation\"," +
                                    "\"key\": {" +
                                        "\"idUser\": null," +
                                        "\"idFollowedUser\": null" +
                                    "}," +
                                    "\"entity\": \"Follow\"" +
                                "}}]}";

                    String singleData = "{" +
                                    "\"idUser\": @idUser," +
                                    "\"idFollowedUser\": @idFollowedUser," +
                                    "\"birth\": @birth," +
                                    "\"revision\": @revision," +
                                    "\"modified\": @modified," +
                                    "\"deleted\": @deleted" +
                                "}";

                    StringBuilder builderData = new StringBuilder();

                    TimeSpan t = DateTime.UtcNow - new DateTime(1970, 1, 1);
                    double epochDate = t.TotalMilliseconds;

                    //req
                    json = json.Replace("@idDevice", "\"null\"");
                    json = json.Replace("@idUser", this.idUser.ToString());
                    json = json.Replace("@appVersion", App.appVersionInt().ToString());
                    json = json.Replace("@idPlatform", App.PLATFORM_ID.ToString());
                    json = json.Replace("@requestTime", Math.Round(epochDate, 0).ToString());
                    json = json.Replace("@Operation", Constants.SERCOM_OP_CREATE);

                    bool isFirst = true;
                    foreach (Follow follow in follows)
                    {
                        String data = singleData;
                        if (!isFirst) data += ",";
                        else
                        {
                            data = "\"data\": [" + data;
                            isFirst = false;
                        }
                        

                        //ops
                        data = data.Replace("@idUser", follow.idUser.ToString());
                        data = data.Replace("@idFollowedUser", follow.idUserFollowed.ToString());
                        data = data.Replace("@birth", Math.Round(epochDate, 0).ToString());
                        data = data.Replace("@modified", Math.Round(epochDate, 0).ToString());
                        data = data.Replace("@revision", follow.csys_revision.ToString());
                        data = data.Replace("@deleted", "null");

                        builderData.Append(data);
                    }

                    builderData.Append("],");
                    json = json.Replace("@Data", builderData.ToString());

                    ServiceCommunication serviceCom = new ServiceCommunication();
                    await serviceCom.SendDataToServer(Constants.SERCOM_TB_FOLLOW, json);

                    await UpdateFollowSynchro(true);

                    json = follows.Count().ToString();
                    return json;
                }
                else return "0";
            }
            catch (TimeoutException timeEx)
            {
                throw timeEx;
            }
            catch (Exception e)
            {
                throw new Exception("Follow - SynchronizeFollows: " + e.Message, e);
            }
        }

        public async Task<string> SynchronizeUnFollows()
        {
            try
            {
                List<Follow> unFollows = await GetUnFollowsToUpdate();

                if (unFollows.Count() > 0)
                {
                    String json = "{\"status\": {\"message\": null,\"code\": null}," +
                                "\"req\": [@idDevice,@idUser,@idPlatform,@appVersion,@requestTime]," +
                                "\"ops\": [{@Data\"metadata\": {" +
                                    "\"items\": 1," +
                                    "\"TotalItems\": null," +
                                    "\"operation\": \"@Operation\"," +
                                    "\"key\": {" +
                                        "\"idUser\": @idUser," +
                                        "\"idFollowedUser\": @idFollowedUser" +
                                    "}," +
                                    "\"entity\": \"Follow\"" +
                                "}}]}";

                    String data = "\"data\": [{" +
                                    "\"idUser\": @idUser," +
                                    "\"idFollowedUser\": @idFollowedUser," +
                                    "\"birth\": @birth," +
                                    "\"revision\": @revision," +
                                    "\"modified\": @modified," +
                                    "\"deleted\": @deleted" +
                                "}],";

                    StringBuilder builderData = new StringBuilder();

                    TimeSpan t = DateTime.UtcNow - new DateTime(1970, 1, 1);
                    double epochDate = t.TotalMilliseconds;

                    //req
                    json = json.Replace("@idDevice", App.ID_DEVICE.ToString());
                    json = json.Replace("@appVersion", App.appVersionInt().ToString());
                    json = json.Replace("@idPlatform", App.PLATFORM_ID.ToString());
                    json = json.Replace("@requestTime", Math.Round(epochDate, 0).ToString());
                    json = json.Replace("@Operation", Constants.SERCOM_OP_DELETE);

                    foreach (Follow follow in unFollows)
                    {
                        json = json.Replace("@idUser", follow.idUser.ToString());
                        json = json.Replace("@idFollowedUser", follow.idUserFollowed.ToString());
                        //ops
                        data = data.Replace("@idUser", follow.idUser.ToString());
                        data = data.Replace("@idFollowedUser", follow.idUserFollowed.ToString());
                        data = data.Replace("@birth", Math.Round(epochDate, 0).ToString());
                        data = data.Replace("@modified", Math.Round(epochDate, 0).ToString());
                        data = data.Replace("@revision", follow.csys_revision.ToString());
                        data = data.Replace("@deleted", follow.csys_deleted.ToString());

                        builderData.Append(data);
                        json = json.Replace("@Data", builderData.ToString());
                        ServiceCommunication serviceCom = new ServiceCommunication();
                        await serviceCom.SendDataToServer(Constants.SERCOM_TB_FOLLOW, json);
                    }

                    

                    

                    await UpdateFollowSynchro(false);

                    json = unFollows.Count().ToString();
                    return json;
                }
                else return "0";
            }
            catch (TimeoutException timeEx)
            {
                throw timeEx;
            }
            catch (Exception e)
            {
                throw new Exception("Follow - SynchronizeFollows: " + e.Message, e);
            }
        }
        private async Task<bool> UpdateFollowSynchro(bool isFollow)
        {
            bool _result = false;

            try {
                String sqlQuery = "";
                if (isFollow) sqlQuery = SQLQuerys.UpdateFollowSynchro;
                else sqlQuery = SQLQuerys.UpdateUnFollowSynchro;

                Database db = await DataBaseHelper.GetDatabaseAsync();
                Statement st = await db.PrepareStatementAsync(sqlQuery);

                await st.StepAsync();

                _result = true;
                DataBaseHelper.DBLoaded.Set();
            }
            catch (Exception e)
            {
                throw new Exception("Follow - GetFollowsToUpdate: " + e.Message, e);
            }

            return _result;
        }

        private async Task<List<Follow>> GetFollowsToUpdate()
        {
            List<Follow> follows = new List<Follow>();
            try
            {
                Database db = await DataBaseHelper.GetDatabaseAsync();
                Statement st = await db.PrepareStatementAsync(SQLQuerys.GetFollowsToUpdate);

                while (await st.StepAsync())
                {
                    string synchro = st.GetTextAt(6);
                    char synchroChar = 'N'; 
                    if(synchro.Length > 0) synchroChar = synchro.ToCharArray(0,1)[0];
                    follows.Add(new Follow { idUser = st.GetIntAt(0), idUserFollowed = st.GetIntAt(1), csys_birth = Util.DateToDouble(DateTime.Parse(st.GetTextAt(2))), csys_modified = Util.DateToDouble(DateTime.Parse(st.GetTextAt(3))), csys_deleted = (String.IsNullOrEmpty(st.GetTextAt(4))) ? 0d : Util.DateToDouble(DateTime.Parse(st.GetTextAt(4))), csys_synchronized = synchroChar, csys_revision = st.GetIntAt(5) });
                }

                DataBaseHelper.DBLoaded.Set();
            }
            catch (Exception e)
            {
                throw new Exception("Follow - GetFollowsToUpdate: " + e.Message, e);
            }
            return follows;
        }

        private async Task<List<Follow>> GetUnFollowsToUpdate()
        {
            List<Follow> follows = new List<Follow>();
            try
            {
                Database db = await DataBaseHelper.GetDatabaseAsync();
                Statement st = await db.PrepareStatementAsync(SQLQuerys.GetUnFollowsToUpdate);

                while (await st.StepAsync())
                {
                    string synchro = st.GetTextAt(6);
                    char synchroChar = 'D';
                    if (synchro.Length > 0) synchroChar = synchro.ToCharArray(0, 1)[0];
                    follows.Add(new Follow { idUser = st.GetIntAt(0), idUserFollowed = st.GetIntAt(1), csys_birth = Util.DateToDouble(DateTime.Parse(st.GetTextAt(2))), csys_modified = Util.DateToDouble(DateTime.Parse(st.GetTextAt(3))), csys_deleted = (String.IsNullOrEmpty(st.GetTextAt(4))) ? 0d : Util.DateToDouble(DateTime.Parse(st.GetTextAt(4))), csys_synchronized = synchroChar, csys_revision = st.GetIntAt(5) });
                }

                DataBaseHelper.DBLoaded.Set();
            }
            catch (Exception e)
            {
                throw new Exception("Follow - GetUnFollowsToUpdate: " + e.Message, e);
            }
            return follows;
        }
        
        #endregion
    }
}
