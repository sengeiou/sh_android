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
    class Shot
    {
        public int idShot { get; set; }
        public int idUser { get; set; }
        public string comment  { get; set; }
        public DateTime csys_birth { get; set; }
        public DateTime csys_modified { get; set; }
        public DateTime csys_deleted { get; set; }
        public int csys_revision { get; set; }
        public char csys_synchronized { get; set; }

        public async Task<int> saveData(JObject job)
        {
            int done = 0;
            Database database;

            try
            {
                if (job["status"]["code"].ToString().Equals("OK") && !job["ops"][0]["metadata"]["totalItems"].ToString().Equals("0"))
                {
                    database = await App.GetDatabaseAsync();
                    using (var custstmt = await database.PrepareStatementAsync(SQLQuerys.InsertShotData))
                    {
                        await database.ExecuteStatementAsync("BEGIN TRANSACTION");

                        foreach (JToken shot in job["ops"][0]["data"])
                        {
                            //idShot, idUser, comment, csys_birth, csys_modified, csys_revision, csys_deleted, csys_synchronized
                            custstmt.Reset();

                            if (shot["idShot"] == null || String.IsNullOrEmpty(shot["idShot"].ToString()))
                                custstmt.BindNullParameterWithName("@idShot");
                            else
                                custstmt.BindIntParameterWithName("@idShot", int.Parse(shot["idShot"].ToString()));

                            if (shot["idUser"] == null || String.IsNullOrEmpty(shot["idUser"].ToString()))
                                custstmt.BindNullParameterWithName("@idUser");
                            else
                                custstmt.BindIntParameterWithName("@idUser", int.Parse(shot["idUser"].ToString()));

                            if (shot["comment"] == null || String.IsNullOrEmpty(shot["comment"].ToString()))
                                custstmt.BindNullParameterWithName("@comment");
                            else
                                custstmt.BindTextParameterWithName("@comment", shot["comment"].ToString());

                            if (shot["birth"] == null || String.IsNullOrEmpty(shot["birth"].ToString()))
                                custstmt.BindNullParameterWithName("@csys_birth");
                            else
                                custstmt.BindTextParameterWithName("@csys_birth", Util.FromUnixTime(shot["birth"].ToString()).ToString("s").Replace('T', ' '));

                            if (shot["modified"] == null || String.IsNullOrEmpty(shot["modified"].ToString()))
                                custstmt.BindNullParameterWithName("@csys_modified");
                            else
                                custstmt.BindTextParameterWithName("@csys_modified", Util.FromUnixTime(shot["modified"].ToString()).ToString("s").Replace('T', ' '));

                            if (shot["deleted"] == null || String.IsNullOrEmpty(shot["deleted"].ToString()))
                                custstmt.BindNullParameterWithName("@csys_deleted");
                            else
                                custstmt.BindTextParameterWithName("@csys_deleted", Util.FromUnixTime(shot["deleted"].ToString()).ToString("s").Replace('T', ' '));

                            if (shot["revision"] == null || String.IsNullOrEmpty(shot["revision"].ToString()))
                                custstmt.BindNullParameterWithName("@csys_revision");
                            else
                                custstmt.BindIntParameterWithName("@csys_revision", int.Parse(shot["revision"].ToString()));

                            custstmt.BindTextParameterWithName("@csys_synchronized", "S");

                            await custstmt.StepAsync().AsTask().ConfigureAwait(false);
                            done++;
                        }
                        await database.ExecuteStatementAsync("COMMIT TRANSACTION");
                    }
                }
                App.DBLoaded.Set();
            }
            catch (Exception e)
            {
                App.DBLoaded.Set();
                throw new Exception("E R R O R - Shot - saveData: " + e.Message);
            }
            return done;
        }


        private async Task<int> synchronized(int idShot)
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
                throw new Exception("Shot - synchronized: " + e.Message, e);
            }
        }


         public async Task<List<ShotModel>> getTimeLineShots()
        {
            try
            {
                List<ShotModel> shotList = new List<ShotModel>();
                Database database = await App.GetDatabaseAsync();

                Statement selectStatement = await database.PrepareStatementAsync(SQLQuerys.GetTimeLineShots);
                selectStatement.BindIntParameterWithName("@idUser", App.ID_USER);
                while (await selectStatement.StepAsync())
                {
                    //s.idShot, s.idUser, s.comment, u.name, u.photo, s.csys_birth
                    shotList.Add(new ShotModel {
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
                throw new Exception("E R R O R - Shot - getTimeLineShots: " + e.Message);
            }
        }

        public async Task<string> constructFilterShot(string conditionDate)
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



        public async Task<string> synchronizeShot()
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


                if (this.csys_synchronized.Equals('D'))
                {
                    //TODO: NOT IMPLEMENTED YET DELETE SHOT
                }
                else
                {
                    ServiceCommunication serviceCom = new ServiceCommunication();
                    serviceCom.sendDataToServer(Constants.SERCOM_TB_SHOT, json);
                    //await synchronized();
                }

                return json;
            }
            catch (Exception e)
            {
                throw new Exception("SubscriptionModel - SynchronizeSubscriptions: " + e.Message, e);
            }
        }

    }
}
