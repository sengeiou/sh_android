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
    public partial class Login : BaseModelJsonConstructor
    {
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

                        if (!String.IsNullOrEmpty(login.favoriteTeamName)) custstmt.BindTextParameterWithName("@favoriteTeamName", login.favoriteTeamName);
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
    }
}
