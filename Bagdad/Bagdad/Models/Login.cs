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

        private String ops_data = "\"idUser\": null,\"idFavoriteTeam\": null,\"favoriteTeamName\": null,\"sessionToken\": null,\"userName\": null,\"email\": null,\"name\": null,\"photo\": null,\"bio\": null,\"website\": null,\"points\": null,\"numFollowings\": null,\"numFollowers\": null,\"revision\": null,\"birth\": null,\"modified\": null,\"deleted\": null";
        
        public override async Task<int> SaveData(List<BaseModelJsonConstructor> logins)
        {
            int done = 0;
            Database database;

            try
            {

                database = await App.GetDatabaseAsync();
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
                App.DBLoaded.Set();
            }
            catch (Exception e)
            {
                string sError = Database.GetSqliteErrorCode(e.HResult).ToString();
                App.DBLoaded.Set();
                throw new Exception("E R R O R - Login - SaveData: " + e.Message);
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

        protected override String GetEntityName() { return Constants.SERCOM_TB_LOGIN; }

        protected override String GetOps() { return ops_data; }

        public override string ConstructOperation(String operation, String searchParams, int offset, int nItems)
        {
            nItems = 1;
            return "\"ops\":[{\"data\":[{" + GetOps() + "}],\"metadata\":{\"items\": " + nItems + ((offset != 0) ? ",\"offset\":" + offset : "") + ",\"TotalItems\":null,\"operation\":\"" + operation + "\"," + searchParams + ",\"entity\":\"" + GetEntityName() + "\"}}]";
        }

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
                    foreach (JToken login in job["ops"][0]["data"])
                    {
                        //idShot, idUser, comment, csys_birth, csys_modified, csys_revision, csys_deleted, csys_synchronized
                        Login loginParse = new Login();

                        loginParse.idUser = int.Parse(login["idUser"].ToString());
                        loginParse.sessionToken = login["sessionToken"].ToString();
                        loginParse.email = login["email"].ToString();
                        loginParse.idFavoriteTeam = int.Parse(login["idFavoriteTeam"].ToString());
                        loginParse.favoriteTeamName = login["favoriteTeamName"].ToString();
                        loginParse.userName = login["userName"].ToString();
                        loginParse.name = login["name"].ToString();
                        loginParse.photo = login["photo"].ToString();
                        loginParse.bio = login["bio"].ToString();
                        loginParse.website = login["website"].ToString();
                        loginParse.points = int.Parse(login["points"].ToString());
                        loginParse.numFollowing = int.Parse(login["numFollowings"].ToString());
                        loginParse.numFollowers = int.Parse(login["numFollowers"].ToString());
                        loginParse.csys_birth = Double.Parse(login["birth"].ToString());
                        loginParse.csys_modified = Double.Parse(login["modified"].ToString());
                        Double deleted; if (Double.TryParse(login["deleted"].ToString(), out deleted))
                            loginParse.csys_deleted = deleted;
                        loginParse.csys_revision = int.Parse(login["revision"].ToString());
                        loginParse.csys_synchronized = 'S';

                        App.ID_USER = loginParse.idUser;

                        userImageManager.SaveImageFromURL(loginParse.photo, loginParse.idUser);

                        users.Add(loginParse);
                    }
                }
            }
            catch (Exception e)
            {

                throw new Exception("E R R O R - Shot - ParseJson: " + e.Message);
            }
            return users;
        }
    }
}
