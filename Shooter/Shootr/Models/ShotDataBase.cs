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
    public partial class Shot : BaseModelJsonConstructor
    {
        public override async Task<int> SaveData(List<BaseModelJsonConstructor> shots)
        {
            int done = 0;

            List<BaseModelJsonConstructor> shotsToUpdate;
            List<BaseModelJsonConstructor> shotsToInsert;
            List<BaseModelJsonConstructor> shotsToDelete;
            try
            {
                shotsToUpdate = new List<BaseModelJsonConstructor>();
                shotsToInsert = new List<BaseModelJsonConstructor>();
                shotsToDelete = new List<BaseModelJsonConstructor>();

                foreach (Shot shot in shots)
                {
                    if (shot.csys_deleted != 0) shotsToDelete.Add(shot);
                    else if (await ExistShot(shot.idShot) != 0)
                        shotsToUpdate.Add(shot);
                    else
                        shotsToInsert.Add(shot);
                }
                if (shotsToInsert.Count > 0)
                {                    
                    done = await InsertData(shotsToInsert);
                    App.ShotsVM.SetShotsOnScreenToUpdate(shotsToInsert, false);
                }
                if (shotsToUpdate.Count > 0) done += await UpdateData(shotsToUpdate);
                if (shotsToDelete.Count > 0) done += await DeleteData(shotsToDelete);
                
                
            }
            catch (Exception e)
            {
                throw new Exception("E R R O R - Shot - SaveData: " + e.Message);
            }
            return done;
        }

        private async Task<int> Synchronized(int idShot)
        {
            try
            {
                int done = 0;

                Database database = await DataBaseHelper.GetDatabaseAsync();

                string sQuery = sQuery = SQLQuerys.shotsSynchronized;

                Statement custstmt = await database.PrepareStatementAsync(sQuery);

                custstmt.BindTextParameterWithName("@csys_synchronized", 'S'.ToString());
                
                custstmt.BindIntParameterWithName("@idShot", idShot);
                await custstmt.StepAsync();

                DataBaseHelper.DBLoaded.Set();

                return done;
            }
            catch (Exception e)
            {
                throw new Exception("Shot - Synchronized: " + e.Message, e);
            }
        }

        public async Task<int> InsertData(List<BaseModelJsonConstructor> shots)
        {
            int done = 0;
            Database database;

            try
            {
                database = await DataBaseHelper.GetDatabaseAsync();
                using (var custstmt = await database.PrepareStatementAsync(SQLQuerys.InsertShotData))
                {
                    await database.ExecuteStatementAsync("BEGIN TRANSACTION");

                    foreach (Shot shot in shots)
                    {
                        //idShot, idUser, comment, csys_birth, csys_modified, csys_revision, csys_deleted, csys_synchronized
                        custstmt.Reset();

                        custstmt.BindIntParameterWithName("@idShot", shot.idShot);
                        custstmt.BindIntParameterWithName("@idUser", shot.idUser);
                        custstmt.BindTextParameterWithName("@comment", shot.comment);
                        custstmt.BindTextParameterWithName("@csys_birth", Util.FromUnixTime(shot.csys_birth.ToString()).ToString("s").Replace('T', ' '));
                        custstmt.BindTextParameterWithName("@csys_modified", Util.FromUnixTime(shot.csys_modified.ToString()).ToString("s").Replace('T', ' '));
                        if (shot.csys_deleted == 0)
                            custstmt.BindNullParameterWithName("@csys_deleted");
                        else
                            custstmt.BindTextParameterWithName("@csys_deleted", Util.FromUnixTime(shot.csys_deleted.ToString()).ToString("s").Replace('T', ' '));
                        custstmt.BindIntParameterWithName("@csys_revision", shot.csys_revision);
                        custstmt.BindTextParameterWithName("@csys_synchronized", "S");

                        await custstmt.StepAsync().AsTask().ConfigureAwait(false);
                        done++;
                    }
                    await database.ExecuteStatementAsync("COMMIT TRANSACTION");
                }
                DataBaseHelper.DBLoaded.Set();
                Debug.WriteLine("\t\t\t\t\t" + GetEntityName() + " acabado con un total de: " + done + " registros añadidos\n");
            }
            catch (Exception e)
            {
                string sError = Database.GetSqliteErrorCode(e.HResult).ToString();
                DataBaseHelper.DBLoaded.Set();
                throw new Exception("E R R O R - Shot - InsertData: " + e.Message);
            }
            return done;
        }

        private async Task<int> UpdateData(List<BaseModelJsonConstructor> shots)
        {
            int done = 0;
            Database database;

            try
            {
                database = await DataBaseHelper.GetDatabaseAsync();
                using (var custstmt = await database.PrepareStatementAsync(SQLQuerys.UpdateShotData))
                {
                    foreach (Shot shot in shots)
                    {
                        custstmt.Reset();

                        custstmt.BindIntParameterWithName("@idShot", shot.idShot);
                        custstmt.BindTextParameterWithName("@comment", shot.comment);
                        custstmt.BindTextParameterWithName("@csys_birth", Util.FromUnixTime(shot.csys_birth.ToString()).ToString("s").Replace('T', ' '));
                        custstmt.BindTextParameterWithName("@csys_modified", Util.FromUnixTime(shot.csys_modified.ToString()).ToString("s").Replace('T', ' '));
                        custstmt.BindIntParameterWithName("@csys_revision", shot.csys_revision);
                        custstmt.BindTextParameterWithName("@csys_synchronized", "S");

                        await custstmt.StepAsync().AsTask().ConfigureAwait(false);
                        done++;
                    }
                    Debug.WriteLine("\t\t\t\t\t" + GetEntityName() + " acabado con un total de: " + done + " registros actualizados\n");
                }
                DataBaseHelper.DBLoaded.Set();
            }
            catch (Exception e)
            {
                string sError = Database.GetSqliteErrorCode(e.HResult).ToString();
                DataBaseHelper.DBLoaded.Set();
                throw new Exception("E R R O R - Shot - SaveData: " + e.Message);
            }
            return done;
        }

        public async Task<int> DeleteData(List<BaseModelJsonConstructor> shots)
        {
            int done = 0;
            Database database;
            List<BaseModelJsonConstructor> shotsToUpdate;
            try
            {
                shotsToUpdate = new List<BaseModelJsonConstructor>();  //TODO:

                database = await DataBaseHelper.GetDatabaseAsync();
                using (var custstmt = await database.PrepareStatementAsync(SQLQuerys.DeleteShotData))
                {
                    foreach (Shot shot in shots)
                    {
                        //idShot, idUser, comment, csys_birth, csys_modified, csys_revision, csys_deleted, csys_synchronized
                        custstmt.Reset();

                        custstmt.BindIntParameterWithName("@idShot", shot.idShot);

                        await custstmt.StepAsync().AsTask().ConfigureAwait(false);
                        done++;
                    }
                }
                DataBaseHelper.DBLoaded.Set();
                Debug.WriteLine("\t\t\t\t\t" + GetEntityName() + " acabado con un total de: " + done + " registros borrados\n");
            }
            catch (Exception e)
            {
                string sError = Database.GetSqliteErrorCode(e.HResult).ToString();
                DataBaseHelper.DBLoaded.Set();
                throw new Exception("E R R O R - Shot - DeleteData: " + e.Message);
            }
            return done;
        }

        public async Task<int> ExistShot(int idShot)
        {
            int done = 0;
            Database database;

            try
            {
                database = await DataBaseHelper.GetDatabaseAsync();
                using (var custstmt = await database.PrepareStatementAsync(SQLQuerys.getShotById))
                {
                    custstmt.Reset();

                    custstmt.BindIntParameterWithName("@idShot", idShot);

                    if (await custstmt.StepAsync())
                    {
                        done = custstmt.GetIntAt(0);
                    }
                }
                DataBaseHelper.DBLoaded.Set();
            }
            catch (Exception e)
            {
                string sError = Database.GetSqliteErrorCode(e.HResult).ToString();
                DataBaseHelper.DBLoaded.Set();
                throw new Exception("E R R O R - Shot - DeleteData: " + e.Message);
            }
            return done;
        }

        public async Task<bool> isShotRepeatedIn24h()
        {
            Database database;
            bool retorn = false;

            try
            {
                database = await DataBaseHelper.GetDatabaseAsync();
                using (var custstmt = await database.PrepareStatementAsync(SQLQuerys.getShotByComment24Hours))
                {
                    custstmt.Reset();

                    DateTime yesterday = DateTime.Now.AddHours(-24d);
                    custstmt.BindTextParameterWithName("@comment", this.comment);
                    custstmt.BindIntParameterWithName("@idUser", this.idUser);
                    custstmt.BindTextParameterWithName("@yesterday", yesterday.ToString("s").Replace('T', ' '));

                    if (await custstmt.StepAsync())
                    {
                        retorn = true;
                    }
                }
                DataBaseHelper.DBLoaded.Set();
            }
            catch (Exception e)
            {
                string sError = Database.GetSqliteErrorCode(e.HResult).ToString();
                DataBaseHelper.DBLoaded.Set();
                throw new Exception("E R R O R - Shot - DeleteData: " + e.Message);
            }
            return retorn;
        }

        public async Task<List<ShotViewModel>> getTimeLineShots()
        {
            try
            {
                List<ShotViewModel> shotList = new List<ShotViewModel>();
                Database database = await DataBaseHelper.GetDatabaseAsync();

                Statement selectStatement = await database.PrepareStatementAsync(SQLQuerys.GetTimeLineShots);
                selectStatement.BindIntParameterWithName("@idUser", App.ID_USER);
                selectStatement.BindIntParameterWithName("@limit", Constants.SERCOM_PARAM_TIME_LINE_FIRST_CHARGE);

                while (await selectStatement.StepAsync())
                {
                    //s.idShot, s.idUser, s.comment, u.name, u.photo, s.csys_birth
                    shotList.Add(
                        bagdadFactory.CreateShotViewModel(
                            selectStatement.GetIntAt(0),
                            selectStatement.GetTextAt(2),
                            selectStatement.GetTextAt(5),
                            selectStatement.GetIntAt(1),
                            selectStatement.GetTextAt(4),
                            selectStatement.GetTextAt(3)
                        )
                    );
                }
                return shotList;
            }
            catch (Exception e)
            {
                DataBaseHelper.DBLoaded.Set();
                throw new Exception("E R R O R - Shot - GetTimeLineShots: " + e.Message);
            }
        }

        public async Task<List<BaseModelJsonConstructor>> getTimeLineOtherShots(int offset)
        {
            string pattern = "yyyy-MM-dd HH':'mm':'ss";
            DateTime parsedDate;
            try
            {
                int count = 0;
                List<BaseModelJsonConstructor> shotList = new List<BaseModelJsonConstructor>();
                Database database = await DataBaseHelper.GetDatabaseAsync();

                Statement selectStatement = await database.PrepareStatementAsync(SQLQuerys.GetTimeLineOtherShots);
                selectStatement.BindIntParameterWithName("@idUser", App.ID_USER);
                selectStatement.BindIntParameterWithName("@offset", offset);
                selectStatement.BindIntParameterWithName("@limit", Constants.SERCOM_PARAM_TIME_LINE_OFFSET_PAG);

                while (await selectStatement.StepAsync())
                {
                    DateTime.TryParseExact(selectStatement.GetTextAt(5), pattern, null, System.Globalization.DateTimeStyles.None, out parsedDate);
                    //s.idShot, s.idUser, s.comment, u.name, u.photo, s.csys_birth
                    shotList.Add(
                        bagdadFactory.CreateShotForTimeLineOtherShots(
                            selectStatement.GetIntAt(0),
                            selectStatement.GetIntAt(1),
                            selectStatement.GetTextAt(2),
                            Util.DateToDouble(parsedDate)
                        )
                    );                    
                    count++;
                }
                App.ShotsVM.SetShotsOnScreenToUpdate(shotList, true);
                Debug.WriteLine("CARGADOS: " + count);
                return shotList;
            }
            catch (Exception e)
            {
                DataBaseHelper.DBLoaded.Set();
                throw new Exception("E R R O R - Shot - getTimeLineShots: " + e.Message);
            }
        }
        
        public async Task<double> GetOlderShotDate()
        {
            try
            {

                Database database = await DataBaseHelper.GetDatabaseAsync();
                double maxDate = 0;

                Statement selectStatement = await database.PrepareStatementAsync(SQLQuerys.getOlderShotDate);

                if (await selectStatement.StepAsync())
                {
                    TimeSpan t = DateTime.Parse(selectStatement.GetTextAt(0)) - new DateTime(1970, 1, 1);
                    maxDate = t.TotalMilliseconds;
                    System.Diagnostics.Debug.WriteLine("Shot - GetOlderShotDate. Older Shot Date: " + maxDate.ToString());
                }

                DataBaseHelper.DBLoaded.Set();

                return maxDate;
            }
            catch (Exception e)
            {
                throw new Exception("Shot - GetOlderShotDate: " + e.Message, e);
            }
        }
    }
}
