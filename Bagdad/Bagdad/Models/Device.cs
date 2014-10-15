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
    public class Device : BaseModelJsonConstructor
    {

        
        public int idDevice { get; set; }
        public int idUser { get; set; }
        public String token { get; set; }
        public String uniqueDeviceID { get; set; }
        public String model { get; set; }
        public String osVer { get; set; }
        public Double csys_birth { get; set; }
        public Double csys_modified { get; set; }
        public Double csys_deleted { get; set; }
        public int csys_revision { get; set; }
        public char csys_synchronized { get; set; }

        private String ops_data = "\"idDevice\": null,\"idUser\": null,\"token\": null,\"idPushEngine\": null,\"uniqueDeviceID\": null,\"platform\": null,\"model\": null,\"osVer\": null,\"locale\": null,\"revision\": null,\"birth\": null,\"modified\": null,\"deleted\": null";
        
        protected override String GetEntityName() { return Constants.SERCOM_TB_DEVICE; }

        protected override String GetOps() { return ops_data; }

        public override Task<int> SaveData(List<BaseModelJsonConstructor> models)
        {
            throw new NotImplementedException();
        }

        public override List<BaseModelJsonConstructor> ParseJson(JObject job)
        {
            throw new NotImplementedException();
        }

        public override Task<string> ConstructFilter(string conditionDate)
        {
            throw new NotImplementedException();
        }

        /// <summary>
        /// Update Object with the current information on the Local DB
        /// </summary>
        /// <returns>true if device exists, false if not</returns>
        public async Task<bool> GetCurrentDeviceInfo()
        {
            bool _return = false;
            try
            {
                Database db = await App.GetDatabaseAsync();
                Statement st = await db.PrepareStatementAsync(SQLQuerys.GetCurrentDevice);

                if (await st.StepAsync())
                {

                    idDevice = st.GetIntAt(0);
                    idUser = st.GetIntAt(1);
                    token = st.GetTextAt(2);
                    uniqueDeviceID = st.GetTextAt(3);
                    model = st.GetTextAt(4);
                    osVer = st.GetTextAt(5);
                    csys_birth = Util.DateToDouble(DateTime.Parse(st.GetTextAt(6)));
                    csys_modified = Util.DateToDouble(DateTime.Parse(st.GetTextAt(7)));
                    csys_revision = st.GetIntAt(8);

                    App.ID_DEVICE = st.GetIntAt(0);
                    _return = true;
                }
                App.DBLoaded.Set();
            }
            catch (Exception e)
            {
                throw new Exception("Device - GetCurrentDeviceID: " + e.Message, e);
            }
            return _return;
        }

        /// <summary>
        /// Returns true if the param token and the one stored in local DB are equals, false if not
        /// </summary>
        /// <param name="_token"></param>
        /// <returns>true or false</returns>
        public async Task<bool> IsTheSameToken()
        {
            if (await GetCurrentDeviceInfo() && token.Equals(App.pushToken)) return true;
            else return false;
        }

        /// <summary>
        /// If token changed, updates info in server and then in local DB
        /// </summary>
        /// <param name="_token"></param>
        /// <returns>true if there are changes, false if not</returns>
        public async Task<bool> UpdateDeviceToken()
        {
            if (!await IsTheSameToken() && App.ID_USER != 0) //Looking for the idUser we can prevent a device registration on the server side before a Login or a registration in the App
            {
                //update in server and update local with server response
                return await UpdateCreateDeviceAtServer();
            }
            return false;
        }

        /// <summary>
        /// Updates the data in Server (create a new Device if no exist) and take this data to the current Device Object
        /// </summary>
        /// <returns>true if process finalizy correctly, false if not</returns>
        private async Task<bool> UpdateCreateDeviceAtServer()
        {

            String _idDevice = "null";
            String _birth = "null";
            String _modified = "null";
            int _revision = csys_revision++;

            if (this.idDevice != null && this.idDevice != 0) _idDevice = this.idDevice.ToString();
            if (this.csys_birth != null && this.csys_birth != 0) _birth = this.csys_birth.ToString();
            if (this.csys_modified != null && this.csys_modified != 0) _modified = this.csys_modified.ToString();

            ServiceCommunication sc = new ServiceCommunication();
            
            String json = "{\"status\": {\"message\": null,\"code\": null}," + await sc.GetREQ() + ",\"ops\": [{\"data\": [{\"idDevice\": " + _idDevice + ",\"idUser\": " + App.ID_USER + ",\"token\": \"" + App.pushToken + "\",\"uniqueDeviceID\": \"" + App.UDID() + "\",\"platform\": " + App.PLATFORM_ID + ",\"model\": \"" + App.modelVersion() + "\",\"osVer\": \"" + App.osVersion() + "\",\"locale\": \"" + App.locale() + "\",\"revision\": " + _revision + ",\"birth\": " + _birth + ",\"modified\": " + _modified + ",\"deleted\": null}],\"metadata\": {\"items\": 1,\"TotalItems\": null,\"operation\": \"UpdateCreate\",\"key\": {\"uniqueDeviceID\": \"" + App.UDID() + "\"},\"entity\": \"Device\"}}]}";
            JObject response = JObject.Parse(await sc.MakeRequestToMemory(json));


            if (response["status"]["code"].ToString().Equals("OK") && !response["ops"][0]["metadata"]["items"].ToString().Equals("0"))
            {
                foreach (JToken device in response["ops"][0]["data"])
                {
                    this.idDevice = int.Parse(device["idDevice"].ToString());
                    this.idUser = int.Parse(device["idUser"].ToString());
                    this.token = device["token"].ToString();
                    this.uniqueDeviceID = device["uniqueDeviceID"].ToString();
                    this.model = device["model"].ToString();
                    this.osVer = device["osVer"].ToString();
                    this.csys_birth = double.Parse(device["birth"].ToString());
                    this.csys_modified = double.Parse(device["modified"].ToString());
                    this.csys_revision = int.Parse(device["revision"].ToString());
                }
                if (await SaveOrUpdateLocalData()) return true;
            }

            return false;
        }

        /// <summary>
        /// Put the current object data in the Local DB
        /// </summary>
        /// <returns>true if works, false if not</returns>
        private async Task<bool> SaveOrUpdateLocalData()
        {
            bool _return = false;
            try
            {
                Database db = await App.GetDatabaseAsync();
                Statement st = await db.PrepareStatementAsync(SQLQuerys.SaveOrCreateDevice);

                st.BindIntParameterWithName("@idDevice", this.idDevice);
                st.BindIntParameterWithName("@idUser", this.idUser);
                st.BindTextParameterWithName("@token", this.token);
                st.BindTextParameterWithName("@uniqueDeviceID", this.uniqueDeviceID);
                st.BindTextParameterWithName("@model", this.model);
                st.BindTextParameterWithName("@osVer", this.osVer);
                st.BindTextParameterWithName("@csys_birth", Util.FromUnixTime(this.csys_birth.ToString()).ToString("s").Replace('T', ' '));
                st.BindTextParameterWithName("@csys_modified", Util.FromUnixTime(this.csys_modified.ToString()).ToString("s").Replace('T', ' '));
                st.BindTextParameterWithName("@csys_revision", Util.FromUnixTime(this.csys_revision.ToString()).ToString("s").Replace('T', ' '));

                await st.StepAsync();
                _return = true;

                App.DBLoaded.Set();
            }
            catch (Exception e)
            {
                throw new Exception("Device - GetCurrentDeviceID: " + e.Message, e);
            }
            return _return;
        }
    }
}
