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
    class Shot : BaseModelJsonConstructor
    {
        public int idShot { get; set; }
        public int idUser { get; set; }
        public string comment  { get; set; }
        public Double csys_birth { get; set; }
        public Double csys_modified { get; set; }
        public Double csys_deleted { get; set; }
        public int csys_revision { get; set; }
        public char csys_synchronized { get; set; }
        private String ops_data = "\"idShot\": null,\"idUser\": null,\"comment\": null,\"revision\": null,\"birth\": null,\"modified\": null,\"deleted\": null";



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

        public async Task<int> AddOlderShotsToTimeLine(JObject job)
        {
            int done = 0;
            
            bool add;
            int idShot = 0;
            int idUser = 0;
            string comment = "";
            string shotDate = "";
            User user = new User();
            List<String> userData = null;
            List<ShotViewModel> OldShots = new List<ShotViewModel>();

            try
            {
                if (job["status"]["code"].ToString().Equals("OK") && !job["ops"][0]["metadata"]["items"].ToString().Equals("0"))
                {
                    foreach (JToken shot in job["ops"][0]["data"])
                    {
                        add = true;

                        if (shot["idShot"] == null || String.IsNullOrEmpty(shot["idShot"].ToString()))
                            add = false;
                        else
                           idShot = int.Parse(shot["idShot"].ToString());

                        if (shot["idUser"] == null || String.IsNullOrEmpty(shot["idUser"].ToString()))
                            add = false;
                        else
                        {
                            idUser = int.Parse(shot["idUser"].ToString());
                            //get Name and URL By idUser
                            userData = await user.GetNameAndImageURL(idUser);
                            if(userData == null) add = false;
                        }

                        if (shot["comment"] == null || String.IsNullOrEmpty(shot["comment"].ToString()))
                            add = false;
                        else
                            comment = shot["comment"].ToString();

                        if (shot["birth"] == null || String.IsNullOrEmpty(shot["birth"].ToString()))
                            add = false;
                        else
                            shotDate = Util.FromUnixTime(shot["birth"].ToString()).ToString("s").Replace('T', ' ');


                        if (add)
                        {
                            OldShots.Add(new ShotViewModel() { shotId = idShot, shotMessage = comment, shotTime = shotDate, shotUserId = idUser, shotUserImageURL = userData[1], shotUserName = userData[0] });
                            done++;
                        }

                    }
                    //OldShots.Sort((x, y) => x.shotTime.CompareTo(y.shotTime));
                    //OldShots.Reverse();
                    App.ShotsVM.ParseShotsForPrinting(OldShots, true);

                }
            }
            catch (Exception e)
            {
                throw new Exception("E R R O R - Shot - AddOlderShotsToTimeLine: " + e.Message);
            }
            return done;
        }

        private async Task<int> Synchronized(int idShot)
        {
            try
            {
                int done = 0;

                Database database = await App.GetDatabaseAsync();

                string sQuery = sQuery = SQLQuerys.shotsSynchronized;

                Statement custstmt = await database.PrepareStatementAsync(sQuery);

                custstmt.BindTextParameterWithName("@csys_synchronized", 'S'.ToString());
                
                custstmt.BindIntParameterWithName("@idShot", idShot);
                await custstmt.StepAsync();

                App.DBLoaded.Set();

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
                database = await App.GetDatabaseAsync();
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
                App.DBLoaded.Set();
                Debug.WriteLine("\t\t\t\t\t" + GetEntityName() + " acabado con un total de: " + done + " registros añadidos\n");
            }
            catch (Exception e)
            {
                string sError = Database.GetSqliteErrorCode(e.HResult).ToString();
                App.DBLoaded.Set();
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
                database = await App.GetDatabaseAsync();
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
                App.DBLoaded.Set();
            }
            catch (Exception e)
            {
                string sError = Database.GetSqliteErrorCode(e.HResult).ToString();
                App.DBLoaded.Set();
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
                shotsToUpdate = new List<BaseModelJsonConstructor>();

                database = await App.GetDatabaseAsync();
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
                App.DBLoaded.Set();
                Debug.WriteLine("\t\t\t\t\t" + GetEntityName() + " acabado con un total de: " + done + " registros borrados\n");
            }
            catch (Exception e)
            {
                string sError = Database.GetSqliteErrorCode(e.HResult).ToString();
                App.DBLoaded.Set();
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
                database = await App.GetDatabaseAsync();
                using (var custstmt = await database.PrepareStatementAsync(SQLQuerys.getShotById))
                {
                    custstmt.Reset();

                    custstmt.BindIntParameterWithName("@idShot", idShot);

                    if (await custstmt.StepAsync())
                    {
                        done = custstmt.GetIntAt(0);
                    }
                }
                App.DBLoaded.Set();
            }
            catch (Exception e)
            {
                string sError = Database.GetSqliteErrorCode(e.HResult).ToString();
                App.DBLoaded.Set();
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
                database = await App.GetDatabaseAsync();
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
                App.DBLoaded.Set();
            }
            catch (Exception e)
            {
                string sError = Database.GetSqliteErrorCode(e.HResult).ToString();
                App.DBLoaded.Set();
                throw new Exception("E R R O R - Shot - DeleteData: " + e.Message);
            }
            return retorn;
        }

        public async Task<List<ShotViewModel>> getTimeLineShots()
        {
            try
            {
                List<ShotViewModel> shotList = new List<ShotViewModel>();
                Database database = await App.GetDatabaseAsync();

                Statement selectStatement = await database.PrepareStatementAsync(SQLQuerys.GetTimeLineShots);
                selectStatement.BindIntParameterWithName("@idUser", App.ID_USER);
                selectStatement.BindIntParameterWithName("@limit", Constants.SERCOM_PARAM_TIME_LINE_FIRST_CHARGE);

                while (await selectStatement.StepAsync())
                {
                    //s.idShot, s.idUser, s.comment, u.name, u.photo, s.csys_birth
                    shotList.Add(new ShotViewModel
                    {
                        shotId = selectStatement.GetIntAt(0),
                        shotUserId = selectStatement.GetIntAt(1),
                        shotMessage = selectStatement.GetTextAt(2),
                        shotUserName = selectStatement.GetTextAt(3),
                        shotUserImageURL = selectStatement.GetTextAt(4),
                        shotTime = selectStatement.GetTextAt(5)
                    });
                }
                return shotList;
            }
            catch (Exception e)
            {
                App.DBLoaded.Set();
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
                Database database = await App.GetDatabaseAsync();

                Statement selectStatement = await database.PrepareStatementAsync(SQLQuerys.GetTimeLineOtherShots);
                selectStatement.BindIntParameterWithName("@idUser", App.ID_USER);
                selectStatement.BindIntParameterWithName("@offset", offset);
                selectStatement.BindIntParameterWithName("@limit", Constants.SERCOM_PARAM_TIME_LINE_OFFSET_PAG);

                while (await selectStatement.StepAsync())
                {
                    DateTime.TryParseExact(selectStatement.GetTextAt(5), pattern, null, System.Globalization.DateTimeStyles.None, out parsedDate);
                    //s.idShot, s.idUser, s.comment, u.name, u.photo, s.csys_birth
                    shotList.Add(new Shot
                    {
                        idShot = selectStatement.GetIntAt(0),
                        idUser = selectStatement.GetIntAt(1),
                        comment = selectStatement.GetTextAt(2),
                        csys_birth = Util.DateToDouble(parsedDate)
                    });
                    count++;
                }
                App.ShotsVM.SetShotsOnScreenToUpdate(shotList, true);
                Debug.WriteLine("CARGADOS: " + count);
                return shotList;
            }
            catch (Exception e)
            {
                App.DBLoaded.Set();
                throw new Exception("E R R O R - Shot - getTimeLineShots: " + e.Message);
            }
        }

        protected override String GetEntityName() { return Constants.SERCOM_TB_SHOT; }

        protected override String GetOps() { return ops_data; }

        public override async Task<string> ConstructFilter(string conditionDate)
        {

            StringBuilder sbFilterIdUser = new StringBuilder();
            try
            {
                Follow follow = new Follow();
                var followList = await follow.getidUserFollowing();
                foreach (int idUser in followList)
                {
                    sbFilterIdUser.Append(",");
                    sbFilterIdUser.Append("{\"comparator\":\"eq\",\"name\":\"idUser\",\"value\":" + idUser + "}");
                }
            }
            catch (Exception e)
            {
                throw new Exception("E R R O R - User - constructFilterFollow: " + e.Message);
            }
            return "\"filterItems\":[], \"filters\":[" + conditionDate + ",{\"filterItems\":[ {\"comparator\":\"eq\",\"name\":\"idUser\",\"value\":" + App.ID_USER + "}"  + sbFilterIdUser.ToString() + "],\"filters\":[],\"nexus\":\"or\"}],\"nexus\":\"and\"";
        }

        public async Task<string> SynchronizeShot()
        {
            try
            {
                String json = "{\"status\": {\"message\": null,\"code\": null}," +
                            "\"req\": [@idDevice,@idUser,@idPlatform,@appVersion,@requestTime]," +
                            "\"ops\": [{@Data\"metadata\": {" +
                                "\"items\": null," +
                                "\"TotalItems\": null," +
                                "\"operation\": \"@Operation\"," +
                                "\"key\": {" +
                                    "\"idShot\": null" +
                                "}," +
                                "\"entity\": \"Shot\"" +
                            "}}]}";

                String Data = "\"data\": [{" +
                                "\"idShot\": null," +
                                "\"idUser\": @idUser," +
                                "\"comment\": \"@comment\"," +
                                "\"birth\": @birth," +
                                "\"revision\": @revision," +
                                "\"modified\": @modified," +
                                "\"deleted\": @deleted" +
                            "}],";

                TimeSpan t = DateTime.UtcNow - new DateTime(1970, 1, 1);
                double epochDate = t.TotalMilliseconds;

                //req
                json = json.Replace("@idDevice", "\"null\"");
                json = json.Replace("@idUser", this.idUser.ToString());
                json = json.Replace("@appVersion", App.appVersionInt().ToString());
                json = json.Replace("@idPlatform", App.PLATFORM_ID.ToString());
                json = json.Replace("@requestTime", Math.Round(epochDate, 0).ToString());

                //ops
                Data = Data.Replace("@idUser", this.idUser.ToString());
                Data = Data.Replace("@comment", this.comment);
                Data = Data.Replace("@birth", Math.Round(epochDate, 0).ToString());
                Data = Data.Replace("@modified", Math.Round(epochDate, 0).ToString());
                Data = Data.Replace("@revision", "0");
                Data = Data.Replace("@deleted", "null");



                json = json.Replace("@Operation", Constants.SERCOM_OP_UPDATECREATE);
                json = json.Replace("@Data", Data);

                ServiceCommunication serviceCom = new ServiceCommunication();
                await serviceCom.SendDataToServer(Constants.SERCOM_TB_SHOT, json);

                return json;
            }
            catch (TimeoutException timeEx)
            {
                throw timeEx;
            }
            catch (Exception e)
            {
                throw new Exception("Shot - SynchronizeSubscriptions: " + e.Message, e);
            }
        }

        public async Task<double> GetOlderShotDate()
        {
            try
            {

                Database database = await App.GetDatabaseAsync();
                double maxDate = 0;

                Statement selectStatement = await database.PrepareStatementAsync(SQLQuerys.getOlderShotDate);

                if (await selectStatement.StepAsync())
                {
                    TimeSpan t = DateTime.Parse(selectStatement.GetTextAt(0)) - new DateTime(1970, 1, 1);
                    maxDate = t.TotalMilliseconds;
                    System.Diagnostics.Debug.WriteLine("Shot - GetOlderShotDate. Older Shot Date: " + maxDate.ToString());
                }

                App.DBLoaded.Set();

                return maxDate;
            }
            catch (Exception e)
            {
                throw new Exception("Shot - GetOlderShotDate: " + e.Message, e);
            }
        }

        public override List<BaseModelJsonConstructor> ParseJson(JObject job)
        {
            List<BaseModelJsonConstructor> shots = new List<BaseModelJsonConstructor>();

            try
            {
                if (job["status"]["code"].ToString().Equals("OK") && !job["ops"][0]["metadata"]["items"].ToString().Equals("0"))
                {
                    foreach (JToken shot in job["ops"][0]["data"])
                    {
                        //idShot, idUser, comment, csys_birth, csys_modified, csys_revision, csys_deleted, csys_synchronized
                        Shot shotParse = new Shot();

                        shotParse.idShot = int.Parse(shot["idShot"].ToString());
                        shotParse.idUser = int.Parse(shot["idUser"].ToString());
                        shotParse.comment = (shot["comment"] != null) ? shot["comment"].ToString() : null;
                        shotParse.csys_birth = Double.Parse(shot["birth"].ToString());
                        shotParse.csys_modified = Double.Parse(shot["modified"].ToString());
                        Double deleted; if (Double.TryParse(shot["deleted"].ToString(), out deleted))
                            shotParse.csys_deleted = deleted;
                        shotParse.csys_revision = int.Parse(shot["revision"].ToString());
                        shotParse.csys_synchronized = 'S';
                        shots.Add(shotParse);
                    }
                }
            }
            catch (Exception e)
            {
                throw new Exception("E R R O R - Shot - ParseJson: " + e.Message);
            }
            return shots;
        }

    }
}
