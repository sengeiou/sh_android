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
        private String ops_data = "\"idShot\": null,\"idUser\": null,\"comment\": null,\"revision\": null,\"birth\": null,\"modified\": null,\"deleted\": null";
        protected override String GetOps() { return ops_data; }

        protected override string GetAlias(string operation)
        {
            if (operation.Equals(Constants.SERCOM_OP_RETRIEVE) || operation.Equals(Constants.SERCOM_OP_RETRIEVE_NO_AUTO_OFFSET))
                return "\"GET_NEWER_SHOTS\",";
            else if (operation.Equals(Constants.SERCOM_OP_UPDATECREATE) || operation.Equals(Constants.SERCOM_OP_CREATE))
                return "\"CREATE_SHOT\",";
            else return "\"GET_NEWER_SHOTS\",";
        }
        public async Task<string> SynchronizeShot()
        {
            try
            {
                String json = "{\"alias\": @alias" +
                                "\"status\": {\"message\": null,\"code\": null}," +
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
                json = json.Replace("@alias", GetAlias(Constants.SERCOM_OP_CREATE));

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
            return "\"filterItems\":[], \"filters\":[" + conditionDate + ",{\"filterItems\":[ {\"comparator\":\"eq\",\"name\":\"idUser\",\"value\":" + App.ID_USER + "}" + sbFilterIdUser.ToString() + "],\"filters\":[],\"nexus\":\"or\"}],\"nexus\":\"and\"";
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
