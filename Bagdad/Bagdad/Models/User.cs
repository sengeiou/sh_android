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
        public int idFavoriteTeam{ get; set; }
        public string favoriteTeamName { get; set; }
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
        public Factories.BagdadFactory bagdadFactory { private get; set; }


        private String ops_data = "\"idUser\": null,\"idFavoriteTeam\": null,\"favoriteTeamName\": null,\"userName\": null,\"name\": null,\"photo\": null,\"bio\": null,\"website\": null,\"points\": null,\"numFollowings\": null,\"numFollowers\": null,\"revision\": null,\"birth\": null,\"modified\": null,\"deleted\": null";

        public User(Factories.BagdadFactory _bagdadFactory)
        {
            bagdadFactory = _bagdadFactory; 
        }
        public User()
        {
            bagdadFactory = new Factories.BagdadFactory();
        }

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
                        //idUser, idFavoriteTeam, userName, name, photo, csys_birth, csys_modified, csys_revision, csys_deleted, csys_synchronized
                        custstmt.Reset();

                        custstmt.BindIntParameterWithName("@idUser", user.idUser);

                        if(user.idFavoriteTeam == 0)
                            custstmt.BindNullParameterWithName("@idFavoriteTeam");
                        else
                            custstmt.BindIntParameterWithName("@idFavoriteTeam", user.idFavoriteTeam);

                        custstmt.BindTextParameterWithName("@favoriteTeamName", user.favoriteTeamName);
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

        public String GetUserOps() { return ops_data; }

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
            UserImageManager userImageManager = bagdadFactory.CreateUserImageManager();

            try
            {
                if (job["status"]["code"].ToString().Equals("OK") && !job["ops"][0]["metadata"]["items"].ToString().Equals("0"))
                {
                    foreach (JToken user in job["ops"][0]["data"])
                    {
                        users.Add(
                            bagdadFactory.CreateFilledUserWithDeleteAndSynchronizedInfo(
                                int.Parse(user["idUser"].ToString()),
                                user["userName"].ToString(),
                                user["name"].ToString(),
                                user["photo"].ToString(),
                                int.Parse(user["numFollowers"].ToString()),
                                int.Parse(user["numFollowings"].ToString()),
                                int.Parse(user["points"].ToString()),
                                user["bio"].ToString(),
                                user["website"].ToString(),
                                favoriteTeamName = user["favoriteTeamName"].ToString(),
                                int.Parse(user["idFavoriteTeam"].ToString()),
                                Double.Parse(user["birth"].ToString()),
                                Double.Parse(user["modified"].ToString()),
                                ((!String.IsNullOrEmpty(user["deleted"].ToString())) ? Double.Parse(user["deleted"].ToString()) : 0),
                                int.Parse(user["revision"].ToString()),
                                'S'
                            )
                        );

                        userImageManager.Enqueue(int.Parse(user["idUser"].ToString()) + "♠" + user["photo"].ToString());
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
            UserViewModel uvm = bagdadFactory.CreateUserViewModel();
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
                    uvm.favoriteTeamName = st.GetTextAt(9);
                    uvm.idFavoriteTeam = st.GetIntAt(10);
                    uvm.birth = Util.DateToDouble(DateTime.Parse(st.GetTextAt(11)));
                    uvm.modified = Util.DateToDouble(DateTime.Parse(st.GetTextAt(12)));
                    uvm.revision = st.GetIntAt(13);

                }
                App.DBLoaded.Set();
            }
            catch (Exception e)
            {
                throw new Exception("E R R O R - User - GetProfileInfo: " + e.Message);
            }
            return uvm;
        }

        public async Task<UserViewModel> GetProfilInfoFromServer(int idUser)
        {
            UserViewModel uvm = bagdadFactory.CreateUserViewModel();
            try
            {
                ServiceCommunication sc = bagdadFactory.CreateServiceCommunication();

                String jsonFollow = "{\"status\": {\"message\": null,\"code\": null}," + await sc.GetREQ() + ",\"ops\": [{\"data\": [{" + ops_data + "}],\"metadata\": {\"items\": 1,\"TotalItems\": null,\"operation\": \"retrieve\",\"filter\": {\"filterItems\": [],\"filters\": [{\"filterItems\": [{\"comparator\": \"ne\",\"name\": \"modified\",\"value\": null},{\"comparator\": \"eq\",\"name\": \"deleted\",\"value\": null}],\"filters\": [],\"nexus\": \"or\"},{\"filterItems\": [{\"comparator\": \"eq\",\"name\": \"idUser\",\"value\": " + idUser + "}],\"filters\": [],\"nexus\": \"and\"}],\"nexus\": \"and\"},\"entity\": \"User\"}}]}";

                JObject responseFollow = JObject.Parse(await sc.MakeRequestToMemory(jsonFollow));

                if (responseFollow["status"]["code"].ToString().Equals("OK") && !responseFollow["ops"][0]["metadata"]["items"].ToString().Equals("0"))
                {
                    JToken userProfileInfo = responseFollow["ops"][0]["data"][0];

                    uvm.idUser = int.Parse(userProfileInfo["idUser"].ToString());
                    uvm.userNickName = (userProfileInfo["userName"] != null) ? userProfileInfo["userName"].ToString() : null;
                    uvm.userName = (userProfileInfo["name"] != null) ? userProfileInfo["name"].ToString() : null;
                    uvm.userURLImage = (userProfileInfo["photo"] != null) ? userProfileInfo["photo"].ToString() : null;
                    uvm.userBio = (userProfileInfo["bio"] != null) ? userProfileInfo["bio"].ToString() : null; 
                    uvm.points = int.Parse(userProfileInfo["points"].ToString());
                    uvm.following = int.Parse(userProfileInfo["numFollowings"].ToString());
                    uvm.followers = int.Parse(userProfileInfo["numFollowers"].ToString());
                    uvm.userWebsite = (userProfileInfo["website"] != null) ? userProfileInfo["website"].ToString() : null;
                    uvm.favoriteTeamName = (userProfileInfo["favoriteTeamName"] != null) ? userProfileInfo["favoriteTeamName"].ToString() : null;
                    uvm.idFavoriteTeam = int.Parse(userProfileInfo["idFavoriteTeam"].ToString());
                    uvm.birth = (!String.IsNullOrEmpty(userProfileInfo["birth"].ToString()) ? Double.Parse(userProfileInfo["birth"].ToString()) : 0);
                    uvm.modified = (!String.IsNullOrEmpty(userProfileInfo["modified"].ToString()) ? Double.Parse(userProfileInfo["modified"].ToString()) : 0);
                    uvm.revision = int.Parse(userProfileInfo["revision"].ToString());
                }
            }
            catch (Exception e)
            {
                throw new Exception("E R R O R - User - GetProfileInfo: " + e.Message);
            }
            return uvm;
        }

        public async Task<List<User>> FindUsersInServer(String searchString, int offset)
        {
            Follow follow = bagdadFactory.CreateFollow();

            List<User> users = new List<User>();
            try
            {
                ServiceCommunication sc = new ServiceCommunication();

                String json = "{\"alias\":\"FINDFRIENDS\"," + await sc.GetREQ() + ",\"status\":{\"code\":null,\"message\":null},\"ops\":[{\"data\":[{" + ops_data + "}],\"metadata\":{\"entity\":\"User\",\"filter\":{\"filterItems\":[],\"filters\":[{\"filterItems\":[{\"comparator\":\"eq\",\"name\":\"deleted\",\"value\": null},{\"comparator\":\"ne\",\"name\":\"modified\",\"value\": null}],\"filters\":[],\"nexus\":\"or\"},{\"filterItems\":[{\"comparator\":\"ct\",\"name\":\"name\",\"value\":\"" + searchString + "\"},{\"comparator\":\"ct\",\"name\":\"userName\",\"value\":\"" + searchString + "\"}],\"filters\":[],\"nexus\":\"or\"}],\"nexus\":\"and\"},\"includeDeleted\":false,\"items\": " + Constants.SERCOM_PARAM_TIME_LINE_OFFSET_PAG + ",\"key\":null,\"offset\": " + offset + ",\"operation\":\"retrieve\",\"totalItems\":null}}]}";

                JObject response = JObject.Parse(await sc.MakeRequestToMemory(json));

                if (response["status"]["code"].ToString().Equals("OK") && !response["ops"][0]["metadata"]["items"].ToString().Equals("0"))
                {
                    foreach (JToken user in response["ops"][0]["data"])
                    {
                        users.Add(
                            bagdadFactory.CreateFilledUserWithOutDeleteAndSynchronizedInfo(
                                int.Parse(user["idUser"].ToString()),
                                user["userName"].ToString(),
                                user["name"].ToString(),
                                user["photo"].ToString(),
                                int.Parse(user["numFollowers"].ToString()),
                                int.Parse(user["numFollowings"].ToString()),
                                int.Parse(user["points"].ToString()),
                                user["bio"].ToString(),
                                user["website"].ToString(),
                                user["favoriteTeamName"].ToString(),
                                int.Parse(user["idFavoriteTeam"].ToString()),
                                int.Parse(user["revision"].ToString()),
                                (!String.IsNullOrEmpty(user["birth"].ToString()) ? double.Parse(user["birth"].ToString()) : 0),
                                (!String.IsNullOrEmpty(user["modified"].ToString()) ? double.Parse(user["modified"].ToString()) : Util.DateToDouble(DateTime.Now))
                            )
                        );
                    }

                }
            }
            catch (Exception e)
            {
                throw new Exception("User - FindUsersInServer: " + e.Message, e);
            }
            return users;
        }

        public async Task<List<User>> FindUsersInDB(String searchString)
        {
            List<User> usersResult = new List<User>();
            try
            {
             Database db = await App.GetDatabaseAsync();

             Statement st = await db.PrepareStatementAsync(SQLQuerys.GetUsersByUserAndNick);
             st.BindTextParameterWithName("@name", "%" + searchString + "%");
             st.BindTextParameterWithName("@userName", "%" + searchString + "%");

                while (await st.StepAsync())
                {
                    usersResult.Add(
                        bagdadFactory.CreateFilledUserWithOutDeleteAndSynchronizedInfo(
                            st.GetIntAt(0),
                            st.GetTextAt(1),
                            st.GetTextAt(2),
                            st.GetTextAt(3),
                            st.GetIntAt(7),
                            st.GetIntAt(6),
                            st.GetIntAt(5),
                            st.GetTextAt(4),
                            st.GetTextAt(8),
                            st.GetTextAt(9),
                            st.GetIntAt(10),
                            st.GetIntAt(13),
                            Util.DateToDouble(DateTime.Parse(st.GetTextAt(11))),
                            Util.DateToDouble(DateTime.Parse(st.GetTextAt(12)))
                        )
                    );
                }
                App.DBLoaded.Set();
            }
            catch (Exception e)
            {
                throw new Exception("User - FindUsersInServer: " + e.Message, e);
            }
            return usersResult;
        }

        public async Task<bool> AddCurrentUserToLocalDB()
        {
            bool _return = false;
            UserImageManager userImageManager = bagdadFactory.CreateUserImageManager();
            try
            {
                Database db = await App.GetDatabaseAsync();

                Statement st = await db.PrepareStatementAsync(SQLQuerys.InsertUserData);
                
                st.BindIntParameterWithName("@idUser", this.idUser);
                st.BindIntParameterWithName("@idFavoriteTeam", this.idFavoriteTeam);
                st.BindTextParameterWithName("@favoriteTeamName", this.favoriteTeamName);
                st.BindTextParameterWithName("@userName", this.userName);
                st.BindTextParameterWithName("@name", this.name);
                st.BindTextParameterWithName("@photo", this.photo);
                st.BindTextParameterWithName("@bio", this.bio);
                st.BindTextParameterWithName("@website", this.website);
                st.BindIntParameterWithName("@points", this.points);
                st.BindIntParameterWithName("@numFollowings", this.numFollowing);
                st.BindIntParameterWithName("@numFollowers", this.numFollowers);
                st.BindTextParameterWithName("@csys_birth", Util.FromUnixTime(this.csys_birth.ToString()).ToString("s").Replace('T', ' '));
                st.BindTextParameterWithName("@csys_modified", Util.FromUnixTime(this.csys_modified.ToString()).ToString("s").Replace('T', ' '));
                st.BindIntParameterWithName("@csys_revision", this.csys_revision);
                st.BindNullParameterWithName("@csys_deleted");
                st.BindTextParameterWithName("@csys_synchronized", "S");

                await st.StepAsync();
                App.DBLoaded.Set();
                
                userImageManager.SaveImageFromURL(this.photo, this.idUser);
                
                _return = true;
            }
            catch (Exception e)
            {
                throw new Exception("User - AddCurrentUserToLocalDB: " + e.Message, e);
            }
            return _return;
        }

        public async Task<bool> RemoveCurrentUserFromLocalDB()
        {
            bool _return = false;
            UserImageManager userImageManager = bagdadFactory.CreateUserImageManager();
            try
            {
                Database db = await App.GetDatabaseAsync();

                Statement st = await db.PrepareStatementAsync(SQLQuerys.DeleteUserData);

                st.BindIntParameterWithName("@idUser", this.idUser);
                
                await st.StepAsync();
                App.DBLoaded.Set();

                userImageManager.RemoveImageById(this.idUser);

                _return = true;
            }
            catch (Exception e)
            {
                throw new Exception("User - AddCurrentUserToLocalDB: " + e.Message, e);
            }
            return _return;
        }

    }
}
