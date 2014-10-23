using Bagdad.Utils;
using Bagdad.ViewModels;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Bagdad.Models
{
    public partial class User : BaseModelJsonConstructor
    {
        public override List<BaseModelJsonConstructor> ParseJson(JObject job)
        {
            List<BaseModelJsonConstructor> users = new List<BaseModelJsonConstructor>();
            UserImageManager userImageManager = bagdadFactory.CreateUserImageManager();

            try
            {
                if (job["status"]["code"].ToString().Equals("OK") && !job["ops"][0]["metadata"]["items"].ToString().Equals("0"))
                {
                    foreach (JToken user in job["ops"][0]["data"])
                    {
                        users.Add(
                            bagdadFactory.CreateFilledUserWithDeleteAndSynchronizedInfo(
                                int.Parse(user["idUser"].ToString()),
                                user["userName"].ToString(),
                                user["name"].ToString(),
                                user["photo"].ToString(),
                                int.Parse(user["numFollowers"].ToString()),
                                int.Parse(user["numFollowings"].ToString()),
                                int.Parse(user["points"].ToString()),
                                user["bio"].ToString(),
                                user["website"].ToString(),
                                favoriteTeamName = user["favoriteTeamName"].ToString(),
                                int.Parse(user["idFavoriteTeam"].ToString()),
                                Double.Parse(user["birth"].ToString()),
                                Double.Parse(user["modified"].ToString()),
                                ((!String.IsNullOrEmpty(user["deleted"].ToString())) ? Double.Parse(user["deleted"].ToString()) : 0),
                                int.Parse(user["revision"].ToString()),
                                'S'
                            )
                        );

                        userImageManager.Enqueue(int.Parse(user["idUser"].ToString()) + "♠" + user["photo"].ToString());
                    }
                }
                userImageManager.SaveMultipleImages();
            }
            catch (Exception e)
            {

                throw new Exception("E R R O R - Shot - ParseJson: " + e.Message);
            }
            return users;
        }

        public async Task<UserViewModel> GetProfilInfoFromServer(int idUser)
        {
            UserViewModel uvm = bagdadFactory.CreateUserViewModel();
            try
            {
                ServiceCommunication sc = bagdadFactory.CreateServiceCommunication();

                String jsonFollow = "{\"status\": {\"message\": null,\"code\": null}," + await sc.GetREQ() + ",\"ops\": [{\"data\": [{" + ops_data + "}],\"metadata\": {\"items\": 1,\"TotalItems\": null,\"operation\": \"retrieve\",\"filter\": {\"filterItems\": [],\"filters\": [{\"filterItems\": [{\"comparator\": \"ne\",\"name\": \"modified\",\"value\": null},{\"comparator\": \"eq\",\"name\": \"deleted\",\"value\": null}],\"filters\": [],\"nexus\": \"or\"},{\"filterItems\": [{\"comparator\": \"eq\",\"name\": \"idUser\",\"value\": " + idUser + "}],\"filters\": [],\"nexus\": \"and\"}],\"nexus\": \"and\"},\"entity\": \"User\"}}]}";

                JObject responseFollow = JObject.Parse(await sc.MakeRequestToMemory(jsonFollow));

                if (responseFollow["status"]["code"].ToString().Equals("OK") && !responseFollow["ops"][0]["metadata"]["items"].ToString().Equals("0"))
                {
                    JToken userProfileInfo = responseFollow["ops"][0]["data"][0];

                    uvm.idUser = int.Parse(userProfileInfo["idUser"].ToString());
                    uvm.userNickName = (userProfileInfo["userName"] != null) ? userProfileInfo["userName"].ToString() : null;
                    uvm.userName = (userProfileInfo["name"] != null) ? userProfileInfo["name"].ToString() : null;
                    uvm.userURLImage = (userProfileInfo["photo"] != null) ? userProfileInfo["photo"].ToString() : null;
                    uvm.userBio = (userProfileInfo["bio"] != null) ? userProfileInfo["bio"].ToString() : null;
                    uvm.points = int.Parse(userProfileInfo["points"].ToString());
                    uvm.following = int.Parse(userProfileInfo["numFollowings"].ToString());
                    uvm.followers = int.Parse(userProfileInfo["numFollowers"].ToString());
                    uvm.userWebsite = (userProfileInfo["website"] != null) ? userProfileInfo["website"].ToString() : null;
                    uvm.favoriteTeamName = (userProfileInfo["favoriteTeamName"] != null) ? userProfileInfo["favoriteTeamName"].ToString() : null;
                    uvm.idFavoriteTeam = int.Parse(userProfileInfo["idFavoriteTeam"].ToString());
                    uvm.birth = (!String.IsNullOrEmpty(userProfileInfo["birth"].ToString()) ? Double.Parse(userProfileInfo["birth"].ToString()) : 0);
                    uvm.modified = (!String.IsNullOrEmpty(userProfileInfo["modified"].ToString()) ? Double.Parse(userProfileInfo["modified"].ToString()) : 0);
                    uvm.revision = int.Parse(userProfileInfo["revision"].ToString());
                }
            }
            catch (Exception e)
            {
                throw new Exception("E R R O R - User - GetProfileInfo: " + e.Message);
            }
            return uvm;
        }

        public async Task<List<User>> FindUsersInServer(String searchString, int offset)
        {
            Follow follow = bagdadFactory.CreateFollow();

            List<User> users = new List<User>();
            try
            {
                ServiceCommunication sc = new ServiceCommunication();

                String json = "{\"alias\":\"FINDFRIENDS\"," + await sc.GetREQ() + ",\"status\":{\"code\":null,\"message\":null},\"ops\":[{\"data\":[{" + ops_data + "}],\"metadata\":{\"entity\":\"User\",\"filter\":{\"filterItems\":[],\"filters\":[{\"filterItems\":[{\"comparator\":\"eq\",\"name\":\"deleted\",\"value\": null},{\"comparator\":\"ne\",\"name\":\"modified\",\"value\": null}],\"filters\":[],\"nexus\":\"or\"},{\"filterItems\":[{\"comparator\":\"ct\",\"name\":\"name\",\"value\":\"" + searchString + "\"},{\"comparator\":\"ct\",\"name\":\"userName\",\"value\":\"" + searchString + "\"}],\"filters\":[],\"nexus\":\"or\"}],\"nexus\":\"and\"},\"includeDeleted\":false,\"items\": " + Constants.SERCOM_PARAM_TIME_LINE_OFFSET_PAG + ",\"key\":null,\"offset\": " + offset + ",\"operation\":\"retrieve\",\"totalItems\":null}}]}";

                JObject response = JObject.Parse(await sc.MakeRequestToMemory(json));

                if (response["status"]["code"].ToString().Equals("OK") && !response["ops"][0]["metadata"]["items"].ToString().Equals("0"))
                {
                    foreach (JToken user in response["ops"][0]["data"])
                    {
                        users.Add(
                            bagdadFactory.CreateFilledUserWithOutDeleteAndSynchronizedInfo(
                                int.Parse(user["idUser"].ToString()),
                                user["userName"].ToString(),
                                user["name"].ToString(),
                                user["photo"].ToString(),
                                int.Parse(user["numFollowers"].ToString()),
                                int.Parse(user["numFollowings"].ToString()),
                                int.Parse(user["points"].ToString()),
                                user["bio"].ToString(),
                                user["website"].ToString(),
                                user["favoriteTeamName"].ToString(),
                                int.Parse(user["idFavoriteTeam"].ToString()),
                                int.Parse(user["revision"].ToString()),
                                (!String.IsNullOrEmpty(user["birth"].ToString()) ? double.Parse(user["birth"].ToString()) : 0),
                                (!String.IsNullOrEmpty(user["modified"].ToString()) ? double.Parse(user["modified"].ToString()) : Util.DateToDouble(DateTime.Now))
                            )
                        );
                    }

                }
            }
            catch (Exception e)
            {
                throw new Exception("User - FindUsersInServer: " + e.Message, e);
            }
            return users;
        }
    }
}
