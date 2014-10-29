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
    public partial class Device : BaseModelJsonConstructor
    {
        public override List<BaseModelJsonConstructor> ParseJson(JObject job)
        {
            throw new NotImplementedException();
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

            if (this.idDevice != 0) _idDevice = this.idDevice.ToString();
            if (this.csys_birth != 0) _birth = this.csys_birth.ToString();
            else _birth = Util.DateToDouble(DateTime.UtcNow).ToString();
            if (this.csys_modified != 0) _modified = this.csys_modified.ToString();
            else _modified = Util.DateToDouble(DateTime.UtcNow).ToString();

            ServiceCommunication sc = bagdadFactory.CreateServiceCommunication();

            String json = "{\"alias\":" + GetAlias(Constants.SERCOM_OP_UPDATECREATE) + "\"status\": {\"message\": null,\"code\": null}," + await sc.GetREQ() + ",\"ops\": [{\"data\": [{\"idDevice\": " + _idDevice + ",\"idUser\": " + App.ID_USER + ",\"token\": \"" + App.pushToken + "\",\"uniqueDeviceID\": \"" + App.UDID() + "\",\"platform\": " + App.PLATFORM_ID + ",\"model\": \"" + App.modelVersion() + "\",\"osVer\": \"" + App.osVersion() + "\",\"locale\": \"" + App.locale() + "\",\"revision\": " + _revision + ",\"birth\": " + _birth + ",\"modified\": " + _modified + ",\"deleted\": null}],\"metadata\": {\"items\": 1,\"TotalItems\": null,\"operation\": \"UpdateCreate\",\"key\": {\"uniqueDeviceID\": \"" + App.UDID() + "\"},\"entity\": \"Device\"}}]}";
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
    }
}
