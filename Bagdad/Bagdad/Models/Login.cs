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
    public class Login : BaseModelJsonConstructor
    {
        public int idUser { get; set; }
        public int idFavoriteTeam { get; set; }
        public string favoriteTeamName { get; set; }
        public string userName { get; set; }
        public string name { get; set; }
        public string sessionToken { get; set; }
        public string email { get; set; }
        public string photo { get; set; }
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

        private String ops_data = "\"idUser\": null,\"idFavoriteTeam\": null,\"favoriteTeamName\": null,\"sessionToken\": null,\"userName\": null,\"email\": null,\"name\": null,\"photo\": null,\"bio\": null,\"website\": null,\"points\": null,\"numFollowings\": null,\"numFollowers\": null,\"revision\": null,\"birth\": null,\"modified\": null,\"deleted\": null";

        public Login(Factories.BagdadFactory _bagdadFactory)
        {
            bagdadFactory = _bagdadFactory; 
        }
        public Login()
        {
            bagdadFactory = new Factories.BagdadFactory();
        }
        public override async Task<int> SaveData(List<BaseModelJsonConstructor> logins)
        {
            int done = 0;
            Database database;

            try
            {

                database = await DataBaseHelper.GetDatabaseAsync();
                using (var custstmt = await database.PrepareStatementAsync(SQLQuerys.InsertLoginData))
                {
                    await database.ExecuteStatementAsync("BEGIN TRANSACTION");

                    foreach (Login login in logins)
                    {
                        //idUser, idFavoriteTeam, userName, name, photo, csys_birth, csys_modified, csys_revision, csys_deleted, csys_synchronized
                        custstmt.Reset();

                        custstmt.BindIntParameterWithName("@idUser", login.idUser);
                        if (login.idFavoriteTeam == 0)
                            custstmt.BindNullParameterWithName("@idFavoriteTeam");
                        else
                            custstmt.BindIntParameterWithName("@idFavoriteTeam", login.idFavoriteTeam);

                        if(!String.IsNullOrEmpty(login.favoriteTeamName)) custstmt.BindTextParameterWithName("@favoriteTeamName", login.favoriteTeamName);
                        else custstmt.BindNullParameterWithName("@favoriteTeamName");

                        custstmt.BindTextParameterWithName("@sessionToken", login.sessionToken);
                        custstmt.BindTextParameterWithName("@email", login.email);
                        custstmt.BindTextParameterWithName("@userName", login.userName);
                        custstmt.BindTextParameterWithName("@name", login.name);
                        custstmt.BindTextParameterWithName("@photo", login.photo);
                        
                        if (String.IsNullOrEmpty(login.bio))
                            custstmt.BindNullParameterWithName("@bio");
                        else
                            custstmt.BindTextParameterWithName("@bio", login.bio);

                        if (String.IsNullOrEmpty(login.website))
                            custstmt.BindNullParameterWithName("@website");
                        else
                            custstmt.BindTextParameterWithName("@website", login.website);

                        if (login.numFollowing == 0)
                            custstmt.BindNullParameterWithName("@numFollowings");
                        else
                            custstmt.BindInt64ParameterWithName("@numFollowings", login.numFollowing);

                        if (login.numFollowers == 0)
                            custstmt.BindNullParameterWithName("@numFollowers");
                        else
                            custstmt.BindInt64ParameterWithName("@numFollowers", login.numFollowers);

                        custstmt.BindTextParameterWithName("@csys_birth", Util.FromUnixTime(login.csys_birth.ToString()).ToString("s").Replace('T', ' '));
                        custstmt.BindTextParameterWithName("@csys_modified", Util.FromUnixTime(login.csys_modified.ToString()).ToString("s").Replace('T', ' '));
                        if (login.csys_deleted == 0)
                            custstmt.BindNullParameterWithName("@csys_deleted");
                        else
                            custstmt.BindTextParameterWithName("@csys_deleted", Util.FromUnixTime(login.csys_deleted.ToString()).ToString("s").Replace('T', ' '));
                        custstmt.BindIntParameterWithName("@csys_revision", login.csys_revision);
                        custstmt.BindTextParameterWithName("@csys_synchronized", "S");

                        await custstmt.StepAsync().AsTask().ConfigureAwait(false);
                        done++;
                    }
                    await database.ExecuteStatementAsync("COMMIT TRANSACTION");
                }
                DataBaseHelper.DBLoaded.Set();

                Device device = new Device();
                await device.UpdateDeviceToken();
            }
            catch (Exception e)
            {
                string sError = Database.GetSqliteErrorCode(e.HResult).ToString();
                DataBaseHelper.DBLoaded.Set();
                throw new Exception("E R R O R - Login - SaveData: " + e.Message);
            }
            return done;
        }

        public async Task<String> getSessionToken()
        {
            String sessionToken = "";
            try
            {
                Database db = await DataBaseHelper.GetDatabaseAsync();

                Statement st = await db.PrepareStatementAsync(SQLQuerys.GetSessionToken);

                if (await st.StepAsync())
                {
                    sessionToken = st.GetTextAt(0);
                    int idPlayer = st.GetIntAt(1);
                    if (idPlayer > 0) App.ID_USER = idPlayer;
                }

                DataBaseHelper.DBLoaded.Set();
            }
            catch (Exception e)
            {
                throw new Exception("E R R O R - User - getSessionToken: " + e.Message);
            }
            return sessionToken;
        }

        protected override String GetEntityName() { return Constants.SERCOM_TB_LOGIN; }

        protected override String GetOps() { return ops_data; }

        protected override string GetAlias(string operation)
        {
            return "";
        }

        public override string ConstructOperation(String operation, String searchParams, int offset, int nItems)
        {
            nItems = 1;
            return "\"ops\":[{\"data\":[{" + GetOps() + "}],\"metadata\":{\"items\": " + nItems + ((offset != 0) ? ",\"offset\":" + offset : "") + ",\"TotalItems\":null,\"operation\":\"" + operation + "\"," + searchParams + ",\"entity\":\"" + GetEntityName() + "\"}}]";
        }

        /// <summary>
        /// Construct the filter
        /// </summary>
        /// <param name="conditionDate"></param>
        /// <returns></returns>
        public override async Task<string> ConstructFilter(string conditionDate)
        {
            StringBuilder sbFilterIdUser = new StringBuilder();
            try
            {
                Follow follow = bagdadFactory.CreateFollow();
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
                throw new Exception("E R R O R - Login - constructFilterFollow: " + e.Message);
            }
            return "\"filterItems\":[], \"filters\":[" + conditionDate + ",{\"filterItems\":[ " + sbFilterIdUser.ToString() + "],\"filters\":[],\"nexus\":\"or\"}],\"nexus\":\"and\"";
        }

        public async Task<List<String>> GetNameAndImageURL(int idUser)
        {
            List<String> userInfo = new List<string>();
            try
            {
                Database db = await DataBaseHelper.GetDatabaseAsync();

                Statement st = await db.PrepareStatementAsync(SQLQuerys.GetNameAndURL);
                st.BindIntParameterWithName("@idUser", idUser);

                if (await st.StepAsync())
                {
                    userInfo.Add(st.GetTextAt(0)); //Name
                    userInfo.Add(st.GetTextAt(1)); //URL
                }

                DataBaseHelper.DBLoaded.Set();
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
                    foreach (JToken login in job["ops"][0]["data"])
                    {
                        users.Add(
                            bagdadFactory.CreateFullFilledLogin(
                                int.Parse(login["idUser"].ToString()),
                                ((login["sessionToken"] != null) ? login["sessionToken"].ToString() : ""),
                                ((login["email"] != null) ? login["email"].ToString() : ""),
                                int.Parse(login["idFavoriteTeam"].ToString()),
                                ((login["favoriteTeamName"] != null) ? login["favoriteTeamName"].ToString() : ""),
                                ((login["userName"] != null) ? login["userName"].ToString() : ""),
                                ((login["name"] != null) ? login["name"].ToString() : ""),
                                ((login["photo"] != null) ? login["photo"].ToString() : ""),
                                ((login["bio"] != null) ? login["bio"].ToString() : ""),
                                ((login["website"] != null) ? login["website"].ToString() : ""),
                                int.Parse(login["points"].ToString()),
                                int.Parse(login["numFollowings"].ToString()),
                                int.Parse(login["numFollowers"].ToString()),
                                Double.Parse(login["birth"].ToString()),
                                Double.Parse(login["modified"].ToString()),
                                ((!String.IsNullOrEmpty(login["deleted"].ToString())) ? Double.Parse(login["deleted"].ToString()) : 0),
                                int.Parse(login["revision"].ToString()),
                                'S'
                            ) 
                        );

                        App.ID_USER = int.Parse(login["idUser"].ToString());
                        if (login["photo"] != null) userImageManager.SaveImageFromURL(login["photo"].ToString(), int.Parse(login["idUser"].ToString()));
                    }
                }
            }
            catch (Exception e)
            {

                throw new Exception("E R R O R - Login - ParseJson: " + e.Message);
            }
            return users;
        }

        public async Task<bool> isUserAlreadyLoged()
        {
            try
            {
                User u = new User();
                String sessionToken = await u.getSessionToken();

                if (sessionToken.Equals("")) return false;
                else return true;
            }
            catch (System.Security.SecurityException e)
            {
                System.Diagnostics.Debug.WriteLine("E R R O R :  isUserAlreadyLoged: " + e.Message);
                throw e;
            }
        }

        public async Task<bool> LogInByEmail(String email, String password)
        {
            try
            {
                ServiceCommunication sercom = new ServiceCommunication();
                await sercom.DoRequest(Constants.SERCOM_OP_RETRIEVE, Constants.SERCOM_TB_LOGIN, "\"key\":{\"email\": \"" + email + "\",\"password\" : \"" + Util.encryptPassword(password) + "\"}", 0);
                return true;
            }
            catch (Exception e)
            {
                System.Diagnostics.Debug.WriteLine("E R R O R :  LogInByEmail: " + e.Message);
                throw e;
            }
        }

        public async Task<bool> LogInByUserName(String userName, String password)
        {
            try
            {
                ServiceCommunication sercom = new ServiceCommunication();
                await sercom.DoRequest(Constants.SERCOM_OP_RETRIEVE, Constants.SERCOM_TB_LOGIN, "\"key\":{\"userName\": \"" + userName + "\",\"password\" : \"" + Util.encryptPassword(password) + "\"}", 0);
                return true;
            }
            catch (Exception e)
            {
                System.Diagnostics.Debug.WriteLine("E R R O R :  LogInByUserName: " + e.Message);
                throw e;
            }
        }
    }
}
