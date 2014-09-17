using Bagdad.Models;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bagdad.Utils
{
    class ServiceCommunication
    {

        public const string mainURL = "http://tst.shootermessenger.com/data-services/rest/generic/";
        public const string mainServerURL = "http://tst.shootermessenger.com/data-services/rest/system/time";

        public const string secondaryServerURL = "http://tst.shootermessenger.com/data-services/rest/generic/";
        public const string secondaryURL = "http://tst.shootermessenger.com/data-services/rest/system/time";

        public const string thirdURL = "http://tst.shootermessenger.com/data-services/rest/generic/";
        public const string thirdServerURL = "http://tst.shootermessenger.com/data-services/rest/system/time";

        private string urlToConnect = mainURL;

        private string serverDateTimeURL = "http://tst.shootermessenger.com/data-services/rest/system/time";


        #region OPS_DATA

        private String OPS_DATA_LOGIN = "\"idUser\": null,\"idFavouriteTeam\": null,\"sessionToken\": null,\"userName\": null,\"email\": null,\"name\": null,\"photo\": null,\"revision\": null,\"birth\": null,\"modified\": null,\"deleted\": null";

        #endregion

        public async Task<int> doRequest(String operation, String entity, String searchParams, int offset)
        {
            String sErrorJSON = "";
            try
            {
                int totalDone = 0;

                String json = "";

                if (operation.Equals(Constants.SERCOM_OP_MANUAL_JSON_REQUEST))
                {
                    //This make a direct request to the services
                    json = searchParams;
                }
                else
                {
                    String status = "\"status\":{\"message\":null,\"code\":null}";
                    String req = await GetREQ();
                    String ops = GetOPS(operation, entity, searchParams, offset);

                    json = "{" + status + "," + req + "," + ops + "}";
                }

                String response = await makeRequest(json);
                JObject job = JObject.Parse(response);
               
                //Llamamos al retrieve
                if (operation.Equals(Constants.SERCOM_OP_RETRIEVE))
                {
                    //Any ERRORs?
                    if (job["status"].ToString().Equals("No Server Available"))
                    {
                        sErrorJSON = "No Server Available";
                        throw new Exception();
                    }
                    else if (job["status"]["code"].ToString().Equals("OK"))
                    {
                        //Save data on DB
                        totalDone += await saveData(entity, job);
                        
                        //If there is offset
                        if (int.Parse(job["ops"][0]["metadata"]["items"].ToString()) == Constants.SERCOM_PARAM_OFFSET_PAG)
                        {
                            //Recursively get all the items
                            totalDone += await doRequest(operation, entity, searchParams, offset + Constants.SERCOM_PARAM_OFFSET_PAG);
                        }
                    }
                    else
                    {
                        //Show the ERRORs
                        sErrorJSON = job["status"].ToString();
                        throw new Exception();
                    }

                    if (totalDone > 0 && operation.Equals(Constants.SERCOM_OP_RETRIEVE))
                    {
                        //TODO: Save the last Synchro Date for the Entity.
                    }
                }
                else
                {
                    //IF It's an UPLOAD we return 1 for SUCCESS and 0 for ERROR.
                    if (job.ToString().Contains("status") && job.ToString().Contains("code") && job["status"]["code"].ToString().Equals("OK"))
                        totalDone = 1;
                    else totalDone = 0;
                }

                return totalDone;
            }
            catch (Exception e)
            {
                throw new Exception("ServiceCommunication - doRequest: " + entity.ToString() + " - " + sErrorJSON + " - " + e.Message, e);
            }
        }

        private async Task<String> GetREQ()
        {
            String req = "\"req\":[" + App.ID_DEVICE + "," + App.ID_PLAYER + "," + App.PLATFORM_ID + "," + App.appVersionInt() + "," + await getServerTime() + "]";
            return req;
        }

        private String GetOPS(String operation, String entity, String searchParams, int offset)
        {
            String ops;
            try
            {
                switch (entity)
                {
                    case Constants.SERCOM_TB_LOGIN:
                        ops = "\"ops\":[{\"data\":[{" + OPS_DATA_LOGIN + "}],\"metadata\":{\"items\": 1,\"TotalItems\":null,\"operation\":\"" + operation + "\"," + searchParams + ",\"entity\":\"" + entity + "\"}}]";
                        break;
                    default:
                        ops = "";
                        break;
                }
            }
            catch (Exception e)
            {
                throw new Exception("ServiceCommunication - GetOPS: " + entity.ToString() + " " + e.Message, e);
            }
            return ops;

        }

        private async Task<int> saveData(String entity, JObject response)
        {
            int changes = 0;
            try
            {
                switch (entity)
                {
                    case Constants.SERCOM_TB_LOGIN:
                        User user = new User();
                        changes = await user.saveData(response);
                        break;
                    default:

                        break;
                }
            }
            catch (Exception e)
            {
                throw new Exception("ServiceCommunication - saveData: " + entity.ToString() + " " + e.Message, e);
            }
            return changes;
        }


        private async Task<String> makeRequest(String json)
        {
            try
            {
                if (await ExistAndSetServer())
                {
                    byte[] dataStream = System.Text.Encoding.UTF8.GetBytes(json);


                    System.Net.WebRequest webRequest = System.Net.WebRequest.Create(urlToConnect);
                    webRequest.Method = "POST";
                    webRequest.ContentType = "application/json";
                    webRequest.ContentLength = dataStream.Length;
                    Stream newStream = await webRequest.GetRequestStreamAsync();
                    // Send the data.
                    newStream.Write(dataStream, 0, dataStream.Length);
                    newStream.Close();
                    var webResponse = await webRequest.GetResponseAsync();
                    using (Stream stream = webResponse.GetResponseStream())
                    {

                        StreamReader reader = new StreamReader(stream, Encoding.UTF8);
                        String responseString = reader.ReadToEnd();
                        return responseString;
                    }
                }
                else return "{\"e\":\"No Server Available\"}";
            }
            catch (Exception e)
            {
                return e.Message;
            }
        }

        private async Task<String> getServerTime()
        {
            try
            {
                System.Net.WebRequest webRequest = System.Net.WebRequest.Create(serverDateTimeURL);
                webRequest.Method = "POST";
                // Send the data.
                var webResponse = await webRequest.GetResponseAsync();
                using (Stream stream = webResponse.GetResponseStream())
                {

                    StreamReader reader = new StreamReader(stream, Encoding.UTF8);
                    String responseString = reader.ReadToEnd();
                    return responseString;
                }
            }
            catch (Exception e)
            {
                return "[\"" + e.Message + "\"]";
            }
        }

        #region CHECK_SERVER_STATUS

        private async Task<bool> ExistAndSetServer()
        {
            try
            {
                urlToConnect = mainURL;

                if (await Exist(mainServerURL)) urlToConnect = mainURL;
                else if (await Exist(secondaryServerURL)) urlToConnect = secondaryURL;
                else if (await Exist(thirdServerURL)) urlToConnect = thirdURL;
                else return false;
                return true;
            }
            catch (Exception e)
            {
                throw new Exception("{\"status\":\"No Server Available\"}");
            }
        }

        private async Task<bool> Exist(string urlServer)
        {
            bool retorn = false;
            try
            {
                System.Net.WebRequest webRequest = System.Net.WebRequest.Create(urlServer);
                webRequest.Method = "POST";
                // Send the data.
                var webResponse = await webRequest.GetResponseAsync();
                using (Stream stream = webResponse.GetResponseStream())
                {
                    StreamReader reader = new StreamReader(stream, Encoding.UTF8);
                    String responseString = reader.ReadToEnd();
                    if (!String.IsNullOrEmpty(responseString)) retorn = true;
                }
                return retorn;
            }
            catch (Exception e)
            {
                Debug.WriteLine("E R R O R :  ServiceCommunication - Exist :" + urlServer + " - " + e.Message);
                //Any exception will returns false.
                return false;
            }
        }

        #endregion
    }
}
