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
    public class Shot : BaseModelJsonConstructor
    {
        public int idShot { get; set; }
        public int idUser { get; set; }
        public string comment  { get; set; }
        public Double csys_birth { get; set; }
        public Double csys_modified { get; set; }
        public Double csys_deleted { get; set; }
        public int csys_revision { get; set; }
        public char csys_synchronized { get; set; }
        public Factories.BagdadFactory bagdadFactory { private get; set; }
        private String ops_data = "\"idShot\": null,\"idUser\": null,\"comment\": null,\"revision\": null,\"birth\": null,\"modified\": null,\"deleted\": null";

        public Shot(Factories.BagdadFactory _bagdadFactory)
        {
            bagdadFactory = _bagdadFactory; 
        }
        public Shot()
        {
            bagdadFactory = new Factories.BagdadFactory();
        }

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
                            OldShots.Add(bagdadFactory.CreateShotViewModel(idShot, comment, shotDate, idUser, userData[1], userData[0]));
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

        protected override String GetEntityName() { return Constants.SERCOM_TB_SHOT; }

        protected override String GetOps() { return ops_data; }

        public override async Task<string> ConstructFilter(string conditionDate)
        {

            StringBuilder sbFilterIdUser = new StringBuilder();
            try
            {
                Follow follow = bagdadFactory.CreateFollow();
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

                String data = "\"data\": [{" +
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
                data = data.Replace("@idUser", this.idUser.ToString());
                data = data.Replace("@comment", this.comment);
                data = data.Replace("@birth", Math.Round(epochDate, 0).ToString());
                data = data.Replace("@modified", Math.Round(epochDate, 0).ToString());
                data = data.Replace("@revision", "0");
                data = data.Replace("@deleted", "null");



                json = json.Replace("@Operation", Constants.SERCOM_OP_CREATE);
                json = json.Replace("@Data", data);

                ServiceCommunication serviceCom = bagdadFactory.CreateServiceCommunication();
                await serviceCom.SendDataToServer(Constants.SERCOM_TB_SHOT, json);

                return json;
            }
            catch (TimeoutException timeEx)
            {
                throw timeEx;
            }
            catch (Exception e)
            {
                throw new Exception("Shot - SynchronizeShot: " + e.Message, e);
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

        public override List<BaseModelJsonConstructor> ParseJson(JObject job)
        {
            List<BaseModelJsonConstructor> shots = new List<BaseModelJsonConstructor>();

            try
            {
                if (job["status"]["code"].ToString().Equals("OK") && !job["ops"][0]["metadata"]["items"].ToString().Equals("0"))
                {
                    foreach (JToken shot in job["ops"][0]["data"])
                    {
                        shots.Add(
                            bagdadFactory.CreateShotForParseJson(
                                int.Parse(shot["idShot"].ToString()),
                                int.Parse(shot["idUser"].ToString()),
                                ((shot["comment"] != null) ? shot["comment"].ToString() : null),
                                Double.Parse(shot["birth"].ToString()),
                                Double.Parse(shot["modified"].ToString()),
                                ((!String.IsNullOrEmpty(shot["deleted"].ToString())) ? Double.Parse(shot["deleted"].ToString()) : 0),
                                int.Parse(shot["revision"].ToString()),
                                'S'
                            )    
                        );
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
