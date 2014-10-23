using Bagdad.Utils;
using Bagdad.ViewModels;
using SQLiteWinRT;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Bagdad.Models
{
    public partial class User : BaseModelJsonConstructor
    {
        public override async Task<int> SaveData(List<BaseModelJsonConstructor> users)
        {
            int done = 0;
            Database database;
            User meUser = bagdadFactory.CreateUser();

            try
            {

                database = await DataBaseHelper.GetDatabaseAsync();
                using (var custstmt = await database.PrepareStatementAsync(SQLQuerys.InsertUserData))
                {
                    await database.ExecuteStatementAsync("BEGIN TRANSACTION");

                    foreach (User user in users)
                    {
                        if (user.idUser == App.ID_USER) meUser = user;
                        else
                        {
                            //idUser, idFavoriteTeam, userName, name, photo, csys_birth, csys_modified, csys_revision, csys_deleted, csys_synchronized
                            custstmt.Reset();


                            custstmt.BindIntParameterWithName("@idUser", user.idUser);

                            if (user.idFavoriteTeam == 0)
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

                            if (user.numFollowing == 0)
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
                    }
                    await database.ExecuteStatementAsync("COMMIT TRANSACTION");
                }
                DataBaseHelper.DBLoaded.Set();
                if (meUser.idUser != 0) await updateUserMe(meUser);
            }
            catch (Exception e)
            {
                string sError = Database.GetSqliteErrorCode(e.HResult).ToString();
                DataBaseHelper.DBLoaded.Set();
                throw new Exception("E R R O R - User - SaveData: " + e.Message);
            }
            return done;
        }
        public async Task<int> updateUserMe(User user)
        {
            Database database;

            try
            {
                database = await DataBaseHelper.GetDatabaseAsync();
                Statement custstmt = await database.PrepareStatementAsync(SQLQuerys.UpdateUserData);

                // idFavoriteTeam , favoriteTeamName , userName, name , photo , bio , website, points, numFollowings, numFollowers, csys_modified , csys_revision, csys_synchronized  -- WHERE idUser = @idUser

                custstmt.BindIntParameterWithName("@idUser", user.idUser);

                if (user.idFavoriteTeam == 0)
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

                if (user.points == 0)
                    custstmt.BindNullParameterWithName("@points");
                else
                    custstmt.BindInt64ParameterWithName("@points", user.points);

                if (user.numFollowing == 0)
                    custstmt.BindNullParameterWithName("@numFollowings");
                else
                    custstmt.BindInt64ParameterWithName("@numFollowings", user.numFollowing);

                if (user.numFollowers == 0)
                    custstmt.BindNullParameterWithName("@numFollowers");
                else
                    custstmt.BindInt64ParameterWithName("@numFollowers", user.numFollowers);

                custstmt.BindTextParameterWithName("@csys_modified", Util.FromUnixTime(user.csys_modified.ToString()).ToString("s").Replace('T', ' '));
                if (user.csys_deleted == 0)
                    custstmt.BindNullParameterWithName("@csys_deleted");
                else
                    custstmt.BindTextParameterWithName("@csys_deleted", Util.FromUnixTime(user.csys_deleted.ToString()).ToString("s").Replace('T', ' '));
                custstmt.BindIntParameterWithName("@csys_revision", user.csys_revision);
                custstmt.BindTextParameterWithName("@csys_synchronized", "S");

                await custstmt.StepAsync();

                DataBaseHelper.DBLoaded.Set();
            }
            catch (Exception e)
            {
                string sError = Database.GetSqliteErrorCode(e.HResult).ToString();
                DataBaseHelper.DBLoaded.Set();
                throw new Exception("E R R O R - User - updateUserMe: " + e.Message);
            }
            return 1;
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
        public async Task<UserViewModel> GetProfileInfo(int idUser)
        {
            UserViewModel uvm = bagdadFactory.CreateUserViewModel();
            try
            {
                Database db = await DataBaseHelper.GetDatabaseAsync();

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
                DataBaseHelper.DBLoaded.Set();
            }
            catch (Exception e)
            {
                throw new Exception("E R R O R - User - GetProfileInfo: " + e.Message);
            }
            return uvm;
        }
        public async Task<List<User>> FindUsersInDB(String searchString)
        {
            List<User> usersResult = new List<User>();
            try
            {
                Database db = await DataBaseHelper.GetDatabaseAsync();

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
                DataBaseHelper.DBLoaded.Set();
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
                Database db = await DataBaseHelper.GetDatabaseAsync();

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
                DataBaseHelper.DBLoaded.Set();

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
                Database db = await DataBaseHelper.GetDatabaseAsync();

                Statement st = await db.PrepareStatementAsync(SQLQuerys.DeleteUserData);

                st.BindIntParameterWithName("@idUser", this.idUser);

                await st.StepAsync();
                DataBaseHelper.DBLoaded.Set();

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
