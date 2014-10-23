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
    public partial class Login : BaseModelJsonConstructor
    {
        public override List<BaseModelJsonConstructor> ParseJson(JObject job)
        {
            List<BaseModelJsonConstructor> users = new List<BaseModelJsonConstructor>();
            UserImageManager userImageManager = bagdadFactory.CreateUserImageManager();
            try
            {
                if (job["status"]["code"].ToString().Equals("OK") && !job["ops"][0]["metadata"]["items"].ToString().Equals("0"))
                {
                    foreach (JToken login in job["ops"][0]["data"])
                    {
                        users.Add(
                            bagdadFactory.CreateFullFilledLogin(
                                int.Parse(login["idUser"].ToString()),
                                ((login["sessionToken"] != null) ? login["sessionToken"].ToString() : ""),
                                ((login["email"] != null) ? login["email"].ToString() : ""),
                                int.Parse(login["idFavoriteTeam"].ToString()),
                                ((login["favoriteTeamName"] != null) ? login["favoriteTeamName"].ToString() : ""),
                                ((login["userName"] != null) ? login["userName"].ToString() : ""),
                                ((login["name"] != null) ? login["name"].ToString() : ""),
                                ((login["photo"] != null) ? login["photo"].ToString() : ""),
                                ((login["bio"] != null) ? login["bio"].ToString() : ""),
                                ((login["website"] != null) ? login["website"].ToString() : ""),
                                int.Parse(login["points"].ToString()),
                                int.Parse(login["numFollowings"].ToString()),
                                int.Parse(login["numFollowers"].ToString()),
                                Double.Parse(login["birth"].ToString()),
                                Double.Parse(login["modified"].ToString()),
                                ((!String.IsNullOrEmpty(login["deleted"].ToString())) ? Double.Parse(login["deleted"].ToString()) : 0),
                                int.Parse(login["revision"].ToString()),
                                'S'
                            )
                        );

                        App.ID_USER = int.Parse(login["idUser"].ToString());
                        if (login["photo"] != null) userImageManager.SaveImageFromURL(login["photo"].ToString(), int.Parse(login["idUser"].ToString()));
                    }
                }
            }
            catch (Exception e)
            {

                throw new Exception("E R R O R - Login - ParseJson: " + e.Message);
            }
            return users;
        }

        public async Task<bool> LogInByEmail(String email, String password)
        {
            try
            {
                ServiceCommunication sercom = new ServiceCommunication();
                await sercom.DoRequest(Constants.SERCOM_OP_RETRIEVE, Constants.SERCOM_TB_LOGIN, "\"key\":{\"email\": \"" + email + "\",\"password\" : \"" + Util.encryptPassword(password) + "\"}", 0);
                return true;
            }
            catch (Exception e)
            {
                System.Diagnostics.Debug.WriteLine("E R R O R :  LogInByEmail: " + e.Message);
                throw e;
            }
        }

        public async Task<bool> LogInByUserName(String userName, String password)
        {
            try
            {
                ServiceCommunication sercom = new ServiceCommunication();
                await sercom.DoRequest(Constants.SERCOM_OP_RETRIEVE, Constants.SERCOM_TB_LOGIN, "\"key\":{\"userName\": \"" + userName + "\",\"password\" : \"" + Util.encryptPassword(password) + "\"}", 0);
                return true;
            }
            catch (Exception e)
            {
                System.Diagnostics.Debug.WriteLine("E R R O R :  LogInByUserName: " + e.Message);
                throw e;
            }
        }
    }
}
