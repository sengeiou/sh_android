using Bagdad.Models;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Collections.ObjectModel;
using System.ComponentModel;

namespace Bagdad.Utils
{
    public class ServiceCommunication
    {

        public enum enumSynchroTables
        {
            FULL = 0, SHOTS = 1
        }

        public int nChanges = 0;    //Número de cambios provocador por la sincro

        public const string mainURL = "http://tst.shootermessenger.com/data-services/rest/generic/";
        public const string mainServerURL = "http://tst.shootermessenger.com/data-services/rest/system/time";

        public const string secondaryServerURL = "http://tst.shootermessenger.com/data-services/rest/generic/";
        public const string secondaryURL = "http://tst.shootermessenger.com/data-services/rest/system/time";

        public const string thirdURL = "http://tst.shootermessenger.com/data-services/rest/generic/";
        public const string thirdServerURL = "http://tst.shootermessenger.com/data-services/rest/system/time";

        private string urlToConnect = mainURL;

        private string serverDateTimeURL = "http://tst.shootermessenger.com/data-services/rest/system/time";

        private ObservableCollection<String> listTables;
        private ObservableCollection<String> listTablesSynchronized;

        #region OPS
        private String OPS_DATA_USER = "\"idUser\": null,\"idFavouriteTeam\": null,\"userName\": null,\"name\": null,\"photo\": null,\"revision\": null,\"birth\": null,\"modified\": null,\"deleted\": null";
        private String OPS_DATA_LOGIN = "\"idUser\": null,\"idFavouriteTeam\": null,\"sessionToken\": null,\"userName\": null,\"email\": null,\"name\": null,\"photo\": null,\"revision\": null,\"birth\": null,\"modified\": null,\"deleted\": null";
        private String OPS_DATA_SHOT = "\"idShot\": null,\"idUser\": null,\"comment\": null,\"revision\": null,\"birth\": null,\"modified\": null,\"deleted\": null";
        private String OPS_DATA_FOLLOW = "\"idUser\": null,\"idFollowedUser\": null,\"revision\": null,\"birth\": null,\"modified\": null,\"deleted\": null";
        #endregion

        public ServiceCommunication()
        {
            // Constructor
            //SSystem.Threading.Thread.Sleep(5000);
            listTables = new ObservableCollection<string>();
            listTablesSynchronized = new ObservableCollection<string>();
            initTablesToSynchro(enumSynchroTables.FULL);
        }

        /// <summary>
        /// Inicializamos las tablas a sincronizar, para una sincronización total
        /// </summary>
        public void initTablesToSynchro(enumSynchroTables typeSynchroTables)
        {
            listTables.Clear();
            switch (typeSynchroTables)
            {
                case enumSynchroTables.FULL:
                    listTables.Add(Constants.SERCOM_TB_USER);
                    listTables.Add(Constants.SERCOM_TB_FOLLOW);
                    listTables.Add(Constants.SERCOM_TB_SHOT);
                    break;
                case enumSynchroTables.SHOTS:
                    listTables.Add(Constants.SERCOM_TB_SHOT);
                    break;
            }
        }


        /// <summary>
        /// Requiere que esté seteado el parametro SynchroType.
        /// </summary>
        public async void SynchronizeProcess(object sender, DoWorkEventArgs e)
        {
            try
            {
                if (App.isInternetAvailable && !App.isSynchroRunning())
                {
                    if (await ExistAndSetServer())
                    {
                        listTablesSynchronized.Clear();
                        App.lockSynchro();
                            App.changesOnSynchro = 0;

                        GenericModel gm = new GenericModel();
                        List<SynchroTableInfo> Tables = await gm.GetSynchronizationTables();

                        int total = 0;

                        foreach (SynchroTableInfo Table in Tables)
                        {
                            try
                            {

                                listTablesSynchronized.Add(Table.Entity);



                                total = 0;
                                if (listTables.Contains(Table.Entity))
                                {
                                    if (Table.Direction.Equals("Both") && (SynchroType == Constants.ST_UPLOAD_ONLY || SynchroType == Constants.ST_FULL_SYNCHRO))
                                    {
                                        Debug.WriteLine("SUBIENDO: " + Table.Entity);
                                        string sText = await UpdateServer(Table.Entity);
                                        Debug.WriteLine(sText);
                                    }

                                    if (SynchroType == Constants.ST_DOWNLOAD_ONLY || SynchroType == Constants.ST_FULL_SYNCHRO)
                                    {
                                        Debug.WriteLine("DESCARGANDO: " + Table.Entity);
                                        double date = await gm.getMaxModificationDateOf(Table.Entity);
                                        string sParams = await getParamsForSync(Table.Entity, date);
                                        total = await doRequest(Constants.SERCOM_OP_RETRIEVE, Table.Entity, sParams, 0);
                                        nChanges += total;      //Solo tiene en cuenta la sincro estandard
                                        Debug.WriteLine("\t" + Table.Entity + " acabado con un total de: " + total + "\n");
                                    }
                                }
                            }
                            catch (Exception ex)
                            {
                                Debug.WriteLine("ServiceCommunication - SynchronizeProcess - " + Table.Entity + ": " + ex.Message);
                            }
                        }
                    }
                    App.releaseSynchro();
                    App.changesOnSynchro = nChanges;
                    Debug.WriteLine("Cambios totales en la sincronización: " + nChanges.ToString());
                    Debug.WriteLine("______________________________________________________________________________________ \n");

                }
                else
                {
                    Debug.WriteLine("Sin internet no es posible la sincronización general");
                }
            }
            catch (Exception ex)
            {
                Debug.WriteLine("E R R O R : ServiceCommunication - SynchronizeProcess: " + ex.Message);
            }
        }

        public async Task<int> GetOlderShots(int offset)
        {
            Debug.WriteLine("DESCARGANDO: OLD " + Constants.SERCOM_TB_SHOT);
            Shot shot = new Shot();
            double date = await shot.GetOlderShotDate();
            string sParams = await getParamsForPaging(Constants.SERCOM_TB_SHOT, date);
            int total = await doRequest(Constants.SERCOM_OP_RETRIEVE_NO_AUTO_OFFSET, Constants.SERCOM_TB_OLD_SHOTS, sParams, offset);
            Debug.WriteLine("\tOLD " + Constants.SERCOM_TB_SHOT + " acabado con un total de: " + total + "\n");
            return total;
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="entity"></param>
        /// <returns></returns>
        private async Task<string> UpdateServer(String entity)
        {
            try
            {
                String result = "\t No hay elementos pendientes de sincronizar\n";
                string json = string.Empty;
                switch (entity)
                {
                    case Constants.SERCOM_TB_SHOT:
                        Shot shot = new Shot();
                        json = await shot.SynchronizeShot();
                        result = "\t Sincronizado: " + entity + "\n";
                        break;
                    default:
                        result = "\t NOT DONE YET: " + entity + "\n";
                        break;
                }

                if(!String.IsNullOrEmpty(json)) sendDataToServer(entity, json);
                return result;
            }
            catch (Exception ex)
            {
                throw new Exception("ServiceCommunication - UpdateServer: " + ex.Message, ex);
            }
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="entity">Entity to synchro</param>
        /// <param name="json">json to send to the server</param>
        public async void sendDataToServer(String entity, String json)
        {
            int tryCount = 0, done = 0;
            try
            {
                while (done == 0)
                {
                    TimeSpan t = DateTime.UtcNow - new DateTime(1970, 1, 1);
                    double epochDate = t.TotalMilliseconds;

                    done = await doRequest(Constants.SERCOM_OP_MANUAL_JSON_REQUEST, entity, json, 0);
                    tryCount++;
                    if (tryCount == 5) break;

                    //New Date (evitar caché)
                    t = DateTime.UtcNow - new DateTime(1970, 1, 1);
                    epochDate = t.TotalMilliseconds;
                    json = json.Replace("@requestTime", Math.Round(epochDate,0).ToString());
                }
            }
            catch (Exception ex)
            {
                throw new Exception("ServiceCommunication - UpdateServer: " + ex.Message, ex);
            }
        }

        /// <summary>
        /// 0: Update
        /// 1: Download
        /// 2: Both
        /// </summary>
        private int? synchroType;

        private int SynchroType
        {
            get
            {
                if (synchroType == null) return 2;
                else return (int)synchroType;
            }
        }

        public void SetSynchroType(int Type)
        {
            if (Type >= Constants.ST_UPLOAD_ONLY && Type <= Constants.ST_FULL_SYNCHRO)
            {
                synchroType = Type;
            }
            else
            {
                Debug.WriteLine("\n\n No se ha podido establecer el valor para SynchroType.\n Valores admitidos: \n\t - 0: Update \n\t - 1: Download \n\t - 2: Both \n\n Nota: Si este parametro no se establece, SynchronizeProcess actuará como si SynchroType fuera Both.\n\n");
            }
        }

        public async Task<string> getParamsForSync(string entity, double date)
        {
            return await getParams(entity, date, false);
        }

        public async Task<string> getParamsForPaging(string entity, double date)
        {
            return await getParams(entity, date, true);
        }

        private static async Task<string> getParams(string entity, double date, Boolean getOlder)
        {
            string sFilterModifyDelete = ConstructFilterModifyDelete(date, getOlder);
            BaseModelJsonConstructor model = CreateModelJsonConstructor(entity);
            sFilterModifyDelete = "\"filter\":{" + await model.ConstructFilter(sFilterModifyDelete) + "}";

            return sFilterModifyDelete;

        }

        private static BaseModelJsonConstructor CreateModelJsonConstructor(string entity)
        {
            BaseModelJsonConstructor model = null;
            switch (entity)
            {
                case Constants.SERCOM_TB_USER:
                    model = new User();
                    break;
                case Constants.SERCOM_TB_FOLLOW:
                    model = new Follow();
                    break;
                case Constants.SERCOM_TB_SHOT:
                    model = new Shot();
                    break;
                default:
                    break;
            }
            return model;
        }

        private static string ConstructFilterModifyDelete(double date, Boolean getOlder)
        {
            string sFilterModifyDelete = "{\"filterItems\":[{\"comparator\":\"" + (getOlder?"lt":"gt") + "\",\"name\":\"modified\",\"value\":" + date + "},{\"comparator\":\"gt\",\"name\":\"deleted\",\"value\":" + date + "}],\"filters\":[],\"nexus\":\"or\"}";
            return sFilterModifyDelete;
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="operation">Operation (retrieve, CreateUpdate)</param>
        /// <param name="entity">Entity to synchronize</param>
        /// <param name="searchParams"></param>
        /// <param name="offset">offset of the page</param>
        /// <returns></returns>
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
                    String ops = GetOPS(translate(operation), entity, searchParams, offset);

                    json = "{" + status + "," + req + "," + ops + "}";
                }

                String response = await makeRequest(json);
                JObject job = JObject.Parse(response);
               
                //Llamamos al retrieve
                if (operation.Equals(Constants.SERCOM_OP_RETRIEVE) || operation.Equals(Constants.SERCOM_OP_RETRIEVE_NO_AUTO_OFFSET))
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
                        if (int.Parse(job["ops"][0]["metadata"]["items"].ToString()) == Constants.SERCOM_PARAM_OFFSET_PAG && operation.Equals(Constants.SERCOM_OP_RETRIEVE))
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
                        if (entity != Constants.SERCOM_TB_LOGIN)
                        {
                            GenericModel gm = new GenericModel();
                            await gm.updateModificationDateOf((double)job["req"].Last, entity);
                        }
                    }
                }
                else
                {
                    //if (entity.Equals(Constants.SERCOM_TB_SHOT)) totalDone += await saveData(entity, job);
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

        private string translate(string text)
        {
            switch (text)
            {
                case Constants.SERCOM_OP_RETRIEVE_NO_AUTO_OFFSET:
                    return Constants.SERCOM_OP_RETRIEVE;
                case Constants.SERCOM_TB_OLD_SHOTS:
                    return Constants.SERCOM_TB_SHOT;
                default:
                    return text;
            }
        }

        private async Task<String> GetREQ()
        {
            String req = "\"req\":[" + App.ID_DEVICE + "," + App.ID_USER + "," + App.PLATFORM_ID + "," + App.appVersionInt() + "," + await getServerTime() + "]";
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
                        ops = ConstructOPS(OPS_DATA_LOGIN, operation, entity, searchParams, offset, 1);
                        break;
                    case Constants.SERCOM_TB_USER:
                        User user = new User();
                        ops = user.ConstructOperation(OPS_DATA_USER, operation, searchParams, offset, Constants.SERCOM_PARAM_OFFSET_PAG);
                        break;
                    case Constants.SERCOM_TB_FOLLOW:
                        Follow follow = new Follow();
                        ops = follow.ConstructOperation(OPS_DATA_FOLLOW, operation, searchParams, offset, Constants.SERCOM_PARAM_OFFSET_PAG);
                        break;
                    case Constants.SERCOM_TB_SHOT:
                        Shot shot = new Shot();
                        ops = shot.ConstructOperation(OPS_DATA_SHOT, operation, searchParams, offset, Constants.SERCOM_PARAM_OFFSET_PAG);
                        break;
                    case Constants.SERCOM_TB_OLD_SHOTS:
                        Shot oldShot = new Shot();
                        ops = oldShot.ConstructOperation(OPS_DATA_SHOT, operation, searchParams, offset, Constants.SERCOM_PARAM_TIME_LINE_OFFSET_PAG);
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

        private String ConstructOPS(String opsData, String operation, String entity, String searchParams, int offset, int nItems)
        {

            return "\"ops\":[{\"data\":[{" + opsData + "}],\"metadata\":{\"items\": " + nItems + ((offset != 0)?",\"offset\":" + offset : "") + ",\"TotalItems\":null,\"operation\":\"" + operation + "\"," + searchParams + ",\"entity\":\"" + entity + "\"}}]";
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
                    case Constants.SERCOM_TB_USER:
                        User people = new User();
                        changes = await people.saveDataPeople(response);
                        break;
                    case Constants.SERCOM_TB_FOLLOW:
                        Follow follow = new Follow();
                        changes = await follow.saveData(response);
                        break;
                    case Constants.SERCOM_TB_SHOT:
                        Shot shot = new Shot();
                        changes = await shot.SaveData(response);
                        break;
                    case Constants.SERCOM_TB_OLD_SHOTS:
                        Shot oldShots = new Shot();
                        changes = await oldShots.AddOlderShotsToTimeLine(response);
                        break;
                    default:

                        break;
                }
            }
            catch (Exception e)
            {
                throw new Exception("ServiceCommunication - SaveData: " + entity.ToString() + " " + e.Message, e);
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

        public async Task<String> getServerTime()
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
            catch
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
