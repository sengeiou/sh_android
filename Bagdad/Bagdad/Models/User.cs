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
    class User
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
                    using (var custstmt = await database.PrepareStatementAsync(SQLQuerys.InsertLoginData))
                    {
                        await database.ExecuteStatementAsync("BEGIN TRANSACTION");

                        foreach (JToken userLogin in job["ops"][0]["data"])
                        {
                            //idUser, idFavouriteTeam, sessionToken, userName, email, name, photo, csys_birth, csys_modified, csys_revision, csys_deleted, csys_synchronized

                            if (userLogin["idUser"] == null || String.IsNullOrEmpty(userLogin["idUser"].ToString()))
                                custstmt.BindNullParameterWithName("@idUser");
                            else
                                custstmt.BindIntParameterWithName("@idUser", int.Parse(userLogin["idUser"].ToString()));

                            if (userLogin["idFavouriteTeam"] == null || String.IsNullOrEmpty(userLogin["idFavouriteTeam"].ToString()))
                                custstmt.BindNullParameterWithName("@idFavouriteTeam");
                            else
                                custstmt.BindIntParameterWithName("@idFavouriteTeam", int.Parse(userLogin["idFavouriteTeam"].ToString()));

                            if (userLogin["sessionToken"] == null || String.IsNullOrEmpty(userLogin["sessionToken"].ToString()))
                                custstmt.BindNullParameterWithName("@sessionToken");
                            else
                                custstmt.BindTextParameterWithName("@sessionToken", userLogin["sessionToken"].ToString());

                            if (userLogin["userName"] == null || String.IsNullOrEmpty(userLogin["userName"].ToString()))
                                custstmt.BindNullParameterWithName("@userName");
                            else
                                custstmt.BindTextParameterWithName("@userName", userLogin["userName"].ToString());

                            if (userLogin["email"] == null || String.IsNullOrEmpty(userLogin["email"].ToString()))
                                custstmt.BindNullParameterWithName("@email");
                            else
                                custstmt.BindTextParameterWithName("@email", userLogin["email"].ToString());

                            if (userLogin["name"] == null || String.IsNullOrEmpty(userLogin["name"].ToString()))
                                custstmt.BindNullParameterWithName("@name");
                            else
                                custstmt.BindTextParameterWithName("@name", userLogin["name"].ToString());

                            if (userLogin["photo"] == null || String.IsNullOrEmpty(userLogin["photo"].ToString()))
                                custstmt.BindNullParameterWithName("@photo");
                            else
                                custstmt.BindTextParameterWithName("@photo", userLogin["photo"].ToString());

                            if (userLogin["birth"] == null || String.IsNullOrEmpty(userLogin["birth"].ToString()))
                                custstmt.BindNullParameterWithName("@csys_birth");
                            else
                                custstmt.BindTextParameterWithName("@csys_birth", Util.FromUnixTime(userLogin["birth"].ToString()).ToString("s").Replace('T', ' '));

                            if (userLogin["modified"] == null || String.IsNullOrEmpty(userLogin["modified"].ToString()))
                                custstmt.BindNullParameterWithName("@csys_modified");
                            else
                                custstmt.BindTextParameterWithName("@csys_modified", Util.FromUnixTime(userLogin["modified"].ToString()).ToString("s").Replace('T', ' '));

                            if (userLogin["deleted"] == null || String.IsNullOrEmpty(userLogin["deleted"].ToString()))
                                custstmt.BindNullParameterWithName("@csys_deleted");
                            else
                                custstmt.BindTextParameterWithName("@csys_deleted", Util.FromUnixTime(userLogin["deleted"].ToString()).ToString("s").Replace('T', ' '));

                            if (userLogin["revision"] == null || String.IsNullOrEmpty(userLogin["revision"].ToString()))
                                custstmt.BindNullParameterWithName("@csys_revision");
                            else
                                custstmt.BindIntParameterWithName("@csys_revision", int.Parse(userLogin["revision"].ToString()));

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
                throw new Exception("E R R O R - User - saveData: " + e.Message);
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
                }

                App.DBLoaded.Set();
            }
            catch (Exception e)
            {
                throw new Exception("E R R O R - User - getSessionToken: " + e.Message);
            }
            return sessionToken;
        }
    }
}
