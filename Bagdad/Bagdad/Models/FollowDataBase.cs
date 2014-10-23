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
    public partial class Follow : BaseModelJsonConstructor
    {
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
                    followings.Add(bagdadFactory.CreateFollowingUserBasicInfo(st.GetIntAt(0), st.GetTextAt(1), st.GetTextAt(2), st.GetTextAt(3), st.GetTextAt(4)));
                }
            }
            catch (Exception e)
            {
                throw new Exception("Follow - GetUserFollowingLocalData: " + e.Message, e);
            }
            return followings;
        }

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
        
    }
}
