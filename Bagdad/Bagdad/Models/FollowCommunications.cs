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
        public override List<BaseModelJsonConstructor> ParseJson(JObject job)
        {
            List<BaseModelJsonConstructor> follows = new List<BaseModelJsonConstructor>();

            try
            {
                if (job["status"]["code"].ToString().Equals("OK") && !job["ops"][0]["metadata"]["items"].ToString().Equals("0"))
                {
                    foreach (JToken follow in job["ops"][0]["data"])
                    {
                        follows.Add(
                            bagdadFactory.CreateFollowParseJson(
                                int.Parse(follow["idUser"].ToString()),
                                int.Parse(follow["idFollowedUser"].ToString()),
                                Double.Parse(follow["birth"].ToString()),
                                Double.Parse(follow["modified"].ToString()),
                                ((!String.IsNullOrEmpty(follow["deleted"].ToString())) ? Double.Parse(follow["deleted"].ToString()) : 0),
                                int.Parse(follow["revision"].ToString()),
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
            return follows;
        }

        public async Task<List<User>> GetUserFollowingFromServer(int idUser, int offset)
        {
            List<User> followings = new List<User>();
            try
            {
                User user = new User(); 
                ServiceCommunication sc = bagdadFactory.CreateServiceCommunication();

                String jsonFollow = "{\"status\": {\"message\": null,\"code\": null}," + await sc.GetREQ() + ",\"ops\": [{\"data\": [{" + user.GetUserOps() + "}],\"metadata\": {\"items\": " + Constants.SERCOM_PARAM_TIME_LINE_OFFSET_PAG + ",\"TotalItems\": null,\"offset\": " + offset + ",\"operation\": \"retrieve\",\"filter\": {\"filterItems\": [],\"filters\": [{\"filterItems\": [{\"comparator\": \"ne\",\"name\": \"modified\",\"value\": null},{\"comparator\": \"eq\",\"name\": \"deleted\",\"value\": null}],\"filters\": [],\"nexus\": \"or\"},{\"filterItems\": [{\"comparator\": \"eq\",\"name\": \"idUserFollowing\",\"value\": " + idUser + "}],\"filters\": [],\"nexus\": \"and\"}],\"nexus\": \"and\"},\"entity\": \"Following\"}}]}";

                JObject responseFollow = JObject.Parse(await sc.MakeRequestToMemory(jsonFollow));

                if (responseFollow["status"]["code"].ToString().Equals("OK") && !responseFollow["ops"][0]["metadata"]["items"].ToString().Equals("0"))
                {
                    foreach (JToken follow in responseFollow["ops"][0]["data"])
                    {
                        followings.Add(
                            bagdadFactory.CreateFilledUserWithOutDeleteAndSynchronizedInfo(
                                int.Parse(follow["idUser"].ToString()),
                                follow["userName"].ToString(),
                                follow["name"].ToString(),
                                follow["photo"].ToString(),
                                int.Parse(follow["numFollowers"].ToString()),
                                int.Parse(follow["numFollowings"].ToString()),
                                int.Parse(follow["points"].ToString()),
                                follow["bio"].ToString(),
                                follow["website"].ToString(),
                                follow["favoriteTeamName"].ToString(),
                                int.Parse(follow["idFavoriteTeam"].ToString()),
                                int.Parse(follow["revision"].ToString()),
                                double.Parse(follow["birth"].ToString()),
                                double.Parse(follow["modified"].ToString())
                            )
                        );
                    }
                }
            }
            catch (Exception e)
            {
                throw new Exception("Follow - GetUserFollowingFromServer: " + e.Message, e);
            }
            return followings;
        }

        public async Task<List<User>> GetUserFollowersFromServer(int idUser, int offset)
        {
            List<User> followings = new List<User>();
            try
            {
                ServiceCommunication sc = bagdadFactory.CreateServiceCommunication();

                String jsonFollow = "{\"status\": {\"message\": null,\"code\": null}," + await sc.GetREQ() + ",\"ops\": [{\"data\": [{\"idUser\": null,\"idFavoriteTeam\": null,\"favoriteTeamName\": null,\"userName\": null,\"name\": null,\"photo\": null,\"bio\": null,\"website\": null,\"points\": null,\"numFollowings\": null,\"numFollowers\": null,\"revision\": null,\"birth\": null,\"modified\": null,\"deleted\": null}],\"metadata\": {\"items\": " + Constants.SERCOM_PARAM_TIME_LINE_OFFSET_PAG + ",\"TotalItems\": null,\"offset\": " + offset + ",\"operation\": \"retrieve\",\"filter\": {\"filterItems\": [],\"filters\": [{\"filterItems\": [{\"comparator\": \"ne\",\"name\": \"modified\",\"value\": null},{\"comparator\": \"eq\",\"name\": \"deleted\",\"value\": null}],\"filters\": [],\"nexus\": \"or\"},{\"filterItems\": [{\"comparator\": \"eq\",\"name\": \"idUserFollowed\",\"value\": " + idUser + "}],\"filters\": [],\"nexus\": \"and\"}],\"nexus\": \"and\"},\"entity\": \"Followers\"}}]}";

                JObject responseFollow = JObject.Parse(await sc.MakeRequestToMemory(jsonFollow));


                if (responseFollow["status"]["code"].ToString().Equals("OK") && !responseFollow["ops"][0]["metadata"]["items"].ToString().Equals("0"))
                {
                    foreach (JToken follow in responseFollow["ops"][0]["data"])
                    {
                        followings.Add(
                            bagdadFactory.CreateFilledUserWithOutDeleteAndSynchronizedInfo(
                                int.Parse(follow["idUser"].ToString()),
                                follow["userName"].ToString(),
                                follow["name"].ToString(),
                                follow["photo"].ToString(),
                                int.Parse(follow["numFollowers"].ToString()),
                                int.Parse(follow["numFollowings"].ToString()),
                                int.Parse(follow["points"].ToString()),
                                follow["bio"].ToString(),
                                follow["website"].ToString(),
                                follow["favoriteTeamName"].ToString(),
                                int.Parse(follow["idFavoriteTeam"].ToString()),
                                int.Parse(follow["revision"].ToString()),
                                double.Parse(follow["birth"].ToString()),
                                double.Parse(follow["modified"].ToString())
                            )
                        );
                    }

                }
            }
            catch (Exception e)
            {
                throw new Exception("Follow - GetUserFollowingLocalData: " + e.Message, e);
            }
            return followings;
        }
        
        public async Task<string> SynchronizeFollows()
        {
            try
            {
                List<Follow> follows = await GetFollowsToUpdate();

                if (follows.Count() > 0)
                {
                    String json = "{\"alias\": @alias" +
                                    "\"status\": {\"message\": null,\"code\": null}," +
                                "\"req\": [@idDevice,@idUser,@idPlatform,@appVersion,@requestTime]," +
                                "\"ops\": [{@Data\"metadata\": {" +
                                    "\"items\": null," +
                                    "\"TotalItems\": null," +
                                    "\"operation\": \"@Operation\"," +
                                    "\"key\": {" +
                                        "\"idUser\": null," +
                                        "\"idFollowedUser\": null" +
                                    "}," +
                                    "\"entity\": \"Follow\"" +
                                "}}]}";

                    String singleData = "{" +
                                    "\"idUser\": @idUser," +
                                    "\"idFollowedUser\": @idFollowedUser," +
                                    "\"birth\": @birth," +
                                    "\"revision\": @revision," +
                                    "\"modified\": @modified," +
                                    "\"deleted\": @deleted" +
                                "}";

                    StringBuilder builderData = new StringBuilder();

                    TimeSpan t = DateTime.UtcNow - new DateTime(1970, 1, 1);
                    double epochDate = t.TotalMilliseconds;

                    //req
                    json = json.Replace("@idDevice", "\"null\"");
                    json = json.Replace("@idUser", this.idUser.ToString());
                    json = json.Replace("@appVersion", App.appVersionInt().ToString());
                    json = json.Replace("@idPlatform", App.PLATFORM_ID.ToString());
                    json = json.Replace("@requestTime", Math.Round(epochDate, 0).ToString());
                    json = json.Replace("@Operation", Constants.SERCOM_OP_CREATE);
                    json = json.Replace("@alias", GetAlias(Constants.SERCOM_OP_CREATE));

                    bool isFirst = true;
                    foreach (Follow follow in follows)
                    {
                        String data = singleData;
                        if (!isFirst) data = "," + data;
                        else
                        {
                            data = "\"data\": [" + data;
                            isFirst = false;
                        }


                        //ops
                        data = data.Replace("@idUser", follow.idUser.ToString());
                        data = data.Replace("@idFollowedUser", follow.idUserFollowed.ToString());
                        data = data.Replace("@birth", Math.Round(epochDate, 0).ToString());
                        data = data.Replace("@modified", Math.Round(epochDate, 0).ToString());
                        data = data.Replace("@revision", follow.csys_revision.ToString());
                        data = data.Replace("@deleted", "null");

                        builderData.Append(data);
                    }

                    builderData.Append("],");
                    json = json.Replace("@Data", builderData.ToString());

                    ServiceCommunication serviceCom = new ServiceCommunication();
                    await serviceCom.SendDataToServer(Constants.SERCOM_TB_FOLLOW, json);

                    await UpdateFollowSynchro(true);

                    json = follows.Count().ToString();
                    return json;
                }
                else return "0";
            }
            catch (TimeoutException timeEx)
            {
                throw timeEx;
            }
            catch (Exception e)
            {
                throw new Exception("Follow - SynchronizeFollows: " + e.Message, e);
            }
        }

        public async Task<string> SynchronizeUnFollows()
        {
            try
            {
                List<Follow> unFollows = await GetUnFollowsToUpdate();

                if (unFollows.Count() > 0)
                {
                    String jsonOriginal = "{\"alias\": @alias" +
                                        "\"status\": {\"message\": null,\"code\": null}," +
                                "\"req\": [@idDevice,@idUser,@idPlatform,@appVersion,@requestTime]," +
                                "\"ops\": [{@Data\"metadata\": {" +
                                    "\"items\": 1," +
                                    "\"TotalItems\": null," +
                                    "\"operation\": \"@Operation\"," +
                                    "\"key\": {" +
                                        "\"idUser\": @idUser," +
                                        "\"idFollowedUser\": @idFollowedUser" +
                                    "}," +
                                    "\"entity\": \"Follow\"" +
                                "}}]}";

                    String dataOriginal = "\"data\": [{" +
                                    "\"idUser\": @idUser," +
                                    "\"idFollowedUser\": @idFollowedUser," +
                                    "\"birth\": @birth," +
                                    "\"revision\": @revision," +
                                    "\"modified\": @modified," +
                                    "\"deleted\": @deleted" +
                                "}],";

                    TimeSpan t = DateTime.UtcNow - new DateTime(1970, 1, 1);
                    double epochDate = t.TotalMilliseconds;

                    //req
                    jsonOriginal = jsonOriginal.Replace("@idDevice", App.ID_DEVICE.ToString());
                    jsonOriginal = jsonOriginal.Replace("@appVersion", App.appVersionInt().ToString());
                    jsonOriginal = jsonOriginal.Replace("@idPlatform", App.PLATFORM_ID.ToString());
                    jsonOriginal = jsonOriginal.Replace("@requestTime", Math.Round(epochDate, 0).ToString());
                    jsonOriginal = jsonOriginal.Replace("@Operation", Constants.SERCOM_OP_DELETE);
                    jsonOriginal = jsonOriginal.Replace("@alias", GetAlias(Constants.SERCOM_OP_DELETE));

                    foreach (Follow follow in unFollows)
                    {
                        String json = jsonOriginal;
                        String data = dataOriginal;
                        json = json.Replace("@idUser", follow.idUser.ToString());
                        json = json.Replace("@idFollowedUser", follow.idUserFollowed.ToString());

                        //ops
                        data = data.Replace("@idUser", follow.idUser.ToString());
                        data = data.Replace("@idFollowedUser", follow.idUserFollowed.ToString());
                        data = data.Replace("@birth", Math.Round(epochDate, 0).ToString());
                        data = data.Replace("@modified", Math.Round(epochDate, 0).ToString());
                        data = data.Replace("@revision", follow.csys_revision.ToString());
                        data = data.Replace("@deleted", follow.csys_deleted.ToString());

                        json = json.Replace("@Data", data);
                        ServiceCommunication serviceCom = new ServiceCommunication();
                        await serviceCom.SendDataToServer(Constants.SERCOM_TB_FOLLOW, json);
                    }

                    await UpdateFollowSynchro(false);

                    jsonOriginal = unFollows.Count().ToString();
                    return jsonOriginal;
                }
                else return "0";
            }
            catch (TimeoutException timeEx)
            {
                throw timeEx;
            }
            catch (Exception e)
            {
                throw new Exception("Follow - SynchronizeFollows: " + e.Message, e);
            }
        }

    }
}
