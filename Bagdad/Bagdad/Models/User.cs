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
    public class User : BaseModelJsonConstructor
    {
        public int idUser { get; set; }
        public int idFavouriteTeam{ get; set; }
        public string userName{ get; set; }
        public string name{ get; set; }
        public string photo{ get; set; }
        public string bio { get; set; }
        public string website { get; set; }
        public int numFollowers { get; set; }
        public int numFollowing { get; set; }
        public int points { get; set; }
        public Double csys_birth { get; set; }
        public Double csys_modified { get; set; }
        public Double csys_deleted { get; set; }
        public int csys_revision { get; set; }
        public char csys_synchronized { get; set; }

        private String ops_data = "\"idUser\": null,\"idFavouriteTeam\": null,\"userName\": null,\"name\": null,\"photo\": null,\"bio\": null,\"website\": null,\"points\": null,\"numFollowings\": null,\"numFollowers\": null,\"revision\": null,\"birth\": null,\"modified\": null,\"deleted\": null";

        public override async Task<int> SaveData(List<BaseModelJsonConstructor> users)
        {
            int done = 0;
            Database database;

            try
            {

                database = await App.GetDatabaseAsync();
                using (var custstmt = await database.PrepareStatementAsync(SQLQuerys.InsertUserData))
                {
                    await database.ExecuteStatementAsync("BEGIN TRANSACTION");

                    foreach (User user in users)
                    {
                        //idUser, idFavouriteTeam, userName, name, photo, csys_birth, csys_modified, csys_revision, csys_deleted, csys_synchronized
                        custstmt.Reset();

                        custstmt.BindIntParameterWithName("@idUser", user.idUser);

                        if(user.idFavouriteTeam == 0)
                            custstmt.BindNullParameterWithName("@idFavouriteTeam");
                        else
                            custstmt.BindIntParameterWithName("@idFavouriteTeam", user.idFavouriteTeam);

                        custstmt.BindTextParameterWithName("@userName", user.userName);
                        custstmt.BindTextParameterWithName("@name", user.name);
                        custstmt.BindTextParameterWithName("@photo", user.photo);

                        if (String.IsNullOrEmpty(user.bio))
                            custstmt.BindNullParameterWithName("@bio");
                        else
                            custstmt.BindTextParameterWithName("@bio", user.bio);

                        if (String.IsNullOrEmpty(user.website))
                            custstmt.BindNullParameterWithName("@website");
                        else
                            custstmt.BindTextParameterWithName("@website", user.website);

                        if(user.numFollowing == 0)
                            custstmt.BindNullParameterWithName("@numFollowings");
                        else
                            custstmt.BindInt64ParameterWithName("@numFollowings", user.numFollowing);

                        if (user.numFollowers == 0)
                            custstmt.BindNullParameterWithName("@numFollowers");
                        else
                            custstmt.BindInt64ParameterWithName("@numFollowers", user.numFollowers);

                        custstmt.BindTextParameterWithName("@csys_birth", Util.FromUnixTime(user.csys_birth.ToString()).ToString("s").Replace('T', ' '));
                        custstmt.BindTextParameterWithName("@csys_modified", Util.FromUnixTime(user.csys_modified.ToString()).ToString("s").Replace('T', ' '));
                        if (user.csys_deleted == 0)
                            custstmt.BindNullParameterWithName("@csys_deleted");
                        else
                            custstmt.BindTextParameterWithName("@csys_deleted", Util.FromUnixTime(user.csys_deleted.ToString()).ToString("s").Replace('T', ' '));
                        custstmt.BindIntParameterWithName("@csys_revision", user.csys_revision);
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
                throw new Exception("E R R O R - User - SaveData: " + e.Message);
            }
            return done;
        }

        public async Task<String> getSessionToken()
        {
            String sessionToken = "";
            try
            {
                Database db = await App.GetDatabaseAsync();

                Statement st = await db.PrepareStatementAsync(SQLQuerys.GetSessionToken);

                if (await st.StepAsync())
                {
                    sessionToken = st.GetTextAt(0);
                    int idPlayer = st.GetIntAt(1);
                    if (idPlayer > 0) App.ID_USER = idPlayer;
                }

                App.DBLoaded.Set();
            }
            catch (Exception e)
            {
                throw new Exception("E R R O R - User - getSessionToken: " + e.Message);
            }
            return sessionToken;
        }

        protected override String GetEntityName() { return Constants.SERCOM_TB_USER; }
        
        protected override String GetOps() { return ops_data; }

        /// <summary>
        /// Construimos el filtro 
        /// </summary>
        /// <param name="conditionDate"></param>
        /// <returns></returns>
        public override async Task<string> ConstructFilter(string conditionDate)
        {
            StringBuilder sbFilterIdUser = new StringBuilder();
            try
            {
                Follow follow = new Follow();
                 var followList = await follow.getidUserFollowing();
                 bool isFirst = true;
                 foreach (int idUser in followList)
                 {
                     if (!isFirst)
                     {
                         sbFilterIdUser.Append(",");
                     }
                     sbFilterIdUser.Append("{\"comparator\":\"eq\",\"name\":\"idUser\",\"value\":" + idUser + "}");
                     isFirst = false;
                 }
            }
            catch (Exception e)
            {
                throw new Exception("E R R O R - User - constructFilterFollow: " + e.Message);
            }
            return "\"filterItems\":[], \"filters\":[" + conditionDate + ",{\"filterItems\":[ " + sbFilterIdUser.ToString() + "],\"filters\":[],\"nexus\":\"or\"}],\"nexus\":\"and\"";
        }

        public async Task<List<String>> GetNameAndImageURL(int idUser)
        {
            List<String> userInfo = new List<string>();
            try
            {
                Database db = await App.GetDatabaseAsync();

                Statement st = await db.PrepareStatementAsync(SQLQuerys.GetNameAndURL);
                st.BindIntParameterWithName("@idUser", idUser);

                if (await st.StepAsync())
                {
                    userInfo.Add(st.GetTextAt(0)); //Name
                    userInfo.Add(st.GetTextAt(1)); //URL
                }

                App.DBLoaded.Set();
            }
            catch (Exception e)
            {
                throw new Exception("E R R O R - User - GetNameAndImageURL: " + e.Message);
            }
            return userInfo;
        }

        public override List<BaseModelJsonConstructor> ParseJson(JObject job)
        {
            List<BaseModelJsonConstructor> users = new List<BaseModelJsonConstructor>();
            UserImageManager userImageManager = new UserImageManager();

            try
            {
                if (job["status"]["code"].ToString().Equals("OK") && !job["ops"][0]["metadata"]["items"].ToString().Equals("0"))
                {
                    foreach (JToken user in job["ops"][0]["data"])
                    {
                        //idShot, idUser, comment, csys_birth, csys_modified, csys_revision, csys_deleted, csys_synchronized
                        User userParse = new User();

                        userParse.idUser = int.Parse(user["idUser"].ToString());
                        userParse.idFavouriteTeam = int.Parse(user["idFavouriteTeam"].ToString());
                        userParse.userName = user["userName"].ToString();
                        userParse.name = user["name"].ToString();
                        userParse.photo = user["photo"].ToString();
                        userParse.bio = user["bio"].ToString();
                        userParse.website = user["website"].ToString();
                        userParse.points = int.Parse(user["points"].ToString());
                        userParse.numFollowing = int.Parse(user["numFollowings"].ToString());
                        userParse.numFollowers = int.Parse(user["numFollowers"].ToString());
                        userParse.csys_birth = Double.Parse(user["birth"].ToString());
                        userParse.csys_modified = Double.Parse(user["modified"].ToString());
                        Double deleted; if (Double.TryParse(user["deleted"].ToString(), out deleted))
                            userParse.csys_deleted = deleted;
                        userParse.csys_revision = int.Parse(user["revision"].ToString());
                        userParse.csys_synchronized = 'S';

                        userImageManager.Enqueue(userParse.idUser.ToString() + "♠" + userParse.photo);

                        users.Add(userParse);
                    }
                }
                userImageManager.SaveMultipleImages();
            }
            catch (Exception e)
            {

                throw new Exception("E R R O R - Shot - ParseJson: " + e.Message);
            }
            return users;
        }

        public async Task<UserViewModel> GetProfileInfo(int idUser)
        {
            UserViewModel uvm = new UserViewModel();
            try
            {
                Database db = await App.GetDatabaseAsync();

                Statement st = await db.PrepareStatementAsync(SQLQuerys.GetUserProfileInfo);
                st.BindIntParameterWithName("@idUser", idUser);

                if (await st.StepAsync())
                {
                    uvm.idUser = st.GetIntAt(0);
                    uvm.userNickName = st.GetTextAt(1);
                    uvm.userName = st.GetTextAt(2);
                    uvm.userURLImage = st.GetTextAt(3);
                    uvm.userBio = st.GetTextAt(4);
                    uvm.points = st.GetIntAt(5);
                    uvm.following = st.GetIntAt(6);
                    uvm.followers = st.GetIntAt(7);
                    uvm.userWebsite = st.GetTextAt(8);
                }
            }
            catch (Exception e)
            {
                throw new Exception("E R R O R - User - GetProfileInfo: " + e.Message);
            }
            return uvm;
        }

        public async Task<UserViewModel> GetProfilInfoFromServer(int idUser)
        {
            UserViewModel uvm = new UserViewModel();
            try
            {
                ServiceCommunication sc = new ServiceCommunication();

                String jsonFollow = "{\"status\": {\"message\": null,\"code\": null}," + await sc.GetREQ() + ",\"ops\": [{\"data\": [{\"idUser\": null,\"userName\": null,\"name\": null,\"photo\": null,\"bio\": null,\"website\": null,\"points\": null,\"numFollowings\": null,\"numFollowers\": null}],\"metadata\": {\"items\": 1,\"TotalItems\": null,\"operation\": \"retrieve\",\"filter\": {\"filterItems\": [],\"filters\": [{\"filterItems\": [{\"comparator\": \"ne\",\"name\": \"modified\",\"value\": null},{\"comparator\": \"eq\",\"name\": \"deleted\",\"value\": null}],\"filters\": [],\"nexus\": \"or\"},{\"filterItems\": [{\"comparator\": \"eq\",\"name\": \"idUser\",\"value\": " + idUser + "}],\"filters\": [],\"nexus\": \"and\"}],\"nexus\": \"and\"},\"entity\": \"User\"}}]}";

                JObject responseFollow = JObject.Parse(await sc.MakeRequestToMemory(jsonFollow));

                if (responseFollow["status"]["code"].ToString().Equals("OK") && !responseFollow["ops"][0]["metadata"]["items"].ToString().Equals("0"))
                {
                    JToken userProfileInfo = responseFollow["ops"][0]["data"][0];

                    uvm.idUser = int.Parse(userProfileInfo["idUser"].ToString());
                    uvm.userNickName = userProfileInfo["userName"].ToString();
                    uvm.userName = userProfileInfo["name"].ToString();
                    uvm.userURLImage = userProfileInfo["photo"].ToString();
                    uvm.userBio = userProfileInfo["bio"].ToString();
                    uvm.points = int.Parse(userProfileInfo["points"].ToString());
                    uvm.following = int.Parse(userProfileInfo["numFollowings"].ToString());
                    uvm.followers = int.Parse(userProfileInfo["numFollowers"].ToString());
                    uvm.userWebsite = userProfileInfo["website"].ToString();
                }
            }
            catch (Exception e)
            {
                throw new Exception("E R R O R - User - GetProfileInfo: " + e.Message);
            }
            return uvm;
        }

        public async Task<List<FollowViewModel>> FindUsersInServer(String searchString, int offset)
        {
            Follow follow = new Follow();
            List<int> myFollowings = await follow.getidUserFollowing();

            List<FollowViewModel> users = new List<FollowViewModel>();
            try
            {
                ServiceCommunication sc = new ServiceCommunication();

                String json = "{\"alias\":\"FINDFRIENDS\"," + await sc.GetREQ() + ",\"status\":{\"code\":null,\"message\":null},\"ops\":[{\"data\":[{" + ops_data + "}],\"metadata\":{\"entity\":\"User\",\"filter\":{\"filterItems\":[],\"filters\":[{\"filterItems\":[{\"comparator\":\"eq\",\"name\":\"deleted\",\"value\": null},{\"comparator\":\"ne\",\"name\":\"modified\",\"value\": null}],\"filters\":[],\"nexus\":\"or\"},{\"filterItems\":[{\"comparator\":\"ct\",\"name\":\"name\",\"value\":\"" + searchString + "\"},{\"comparator\":\"ct\",\"name\":\"userName\",\"value\":\"" + searchString + "\"}],\"filters\":[],\"nexus\":\"or\"}],\"nexus\":\"and\"},\"includeDeleted\":false,\"items\": " + Constants.SERCOM_PARAM_TIME_LINE_OFFSET_PAG + ",\"key\":null,\"offset\": " + offset + ",\"operation\":\"retrieve\",\"totalItems\":null}}]}";

                JObject response = JObject.Parse(await sc.MakeRequestToMemory(json));

                bool iFollow;

                if (response["status"]["code"].ToString().Equals("OK") && !response["ops"][0]["metadata"]["items"].ToString().Equals("0"))
                {
                    foreach (JToken user in response["ops"][0]["data"])
                    {
                        iFollow = false;

                        if (myFollowings.Contains(int.Parse(user["idUser"].ToString()))) iFollow = true;

                        users.Add(new FollowViewModel() { userInfo = new UserViewModel() { idUser = int.Parse(user["idUser"].ToString()), userNickName = user["userName"].ToString(), userName = user["name"].ToString(), userURLImage = user["photo"].ToString(), isFollowed = iFollow, followers = int.Parse(user["numFollowers"].ToString()), following = int.Parse(user["numFollowings"].ToString()), points = int.Parse(user["points"].ToString()), userBio = user["bio"].ToString(), userWebsite = user["website"].ToString() } });
                    }

                }
            }
            catch (Exception e)
            {
                throw new Exception("Follow - GetUserFollowingLocalData: " + e.Message, e);
            }
            return users;
        }
    }
}
