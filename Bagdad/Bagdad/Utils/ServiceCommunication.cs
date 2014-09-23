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
            FULL = 0
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
            }
        }


        /// <summary>
        /// Requiere que esté seteado el parametro SynchroType.
        /// </summary>
        public async void SynchronizeProcess(object sender, DoWorkEventArgs e)
        {
            try
            {
                if (App.isInternetAvailable)
                {
                    if (!App.isSynchroRunning())
                    {
                        if (await ExistAndSetServer())
                        {
                            listTablesSynchronized.Clear();
                            App.lockSynchro();

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
                                                string sParams = getParams(Table.Entity, date);
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

        private async Task<string> UpdateServer(String Entity)
        {
            try
            {
                String result = "\t No hay elementos pendientes de sincronizar\n";
                switch (Entity)
                {
                    default:
                        result = "\t NOT DONE YET: " + Entity + "\n";
                        break;
                }
                return result;
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

        public string getParams(string entity, double date)
        {
            string sFilterModifyDelete = "{\"filterItems\":[{\"comparator\":\"gt\",\"name\":\"modified\",\"value\":" + date + "},{\"comparator\":\"gt\",\"name\":\"deleted\",\"value\":" + date + "}],\"filters\":[],\"nexus\":\"or\"}";
            switch (entity)
            {
                case Constants.SERCOM_TB_USER:
                    User user = new User();
                    sFilterModifyDelete = "\"filter\":{" + user.constructFilterFollow(sFilterModifyDelete) + "}";
                    break;
                case Constants.SERCOM_TB_FOLLOW:
                    Follow follow = new Follow();
                    sFilterModifyDelete = "\"filter\":{" + follow.constructFilterFollow(sFilterModifyDelete) + "}";
                    break;
                case Constants.SERCOM_TB_SHOT:
                    Shot shot = new Shot();
                    sFilterModifyDelete = "\"filter\":{" + shot.constructFilterShot(sFilterModifyDelete) + "}";
                    break;
                default:
                    sFilterModifyDelete = "\"filter\":{\"filterItems\":[{\"comparator\":\"gt\",\"name\":\"modified\",\"value\":" + date + "},{\"comparator\":\"gt\",\"name\":\"deleted\",\"value\":" + date + "}],\"filters\":[],\"nexus\":\"or\"}";
                    break;
            }

            return sFilterModifyDelete;
        }

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
                        if (entity != Constants.SERCOM_TB_LOGIN)
                        {
                            GenericModel gm = new GenericModel();
                            await gm.updateModificationDateOf((double)job["req"].Last, entity);
                        }
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
                    case Constants.SERCOM_TB_USER:
                        ops = "\"ops\":[{\"data\":[{" + OPS_DATA_USER + "}],\"metadata\":{\"items\": 100,\"TotalItems\":null,\"operation\":\"" + operation + "\"," + searchParams + ",\"entity\":\"" + entity + "\"}}]";
                        break;
                    case Constants.SERCOM_TB_FOLLOW:
                        ops = "\"ops\":[{\"data\":[{" + OPS_DATA_FOLLOW + "}],\"metadata\":{\"items\": 100,\"TotalItems\":null,\"operation\":\"" + operation + "\"," + searchParams + ",\"entity\":\"" + entity + "\"}}]";
                        break;
                    case Constants.SERCOM_TB_SHOT:
                        ops = "\"ops\":[{\"data\":[{" + OPS_DATA_SHOT + "}],\"metadata\":{\"items\": 100,\"TotalItems\":null,\"operation\":\"" + operation + "\"," + searchParams + ",\"entity\":\"" + entity + "\"}}]";
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
                        changes = await shot.saveData(response);
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
