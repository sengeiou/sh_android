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
        public Factories.BagdadFactory bagdadFactory { private get; set; }

        private String ops_data = "\"idDevice\": null,\"idUser\": null,\"token\": null,\"idPushEngine\": null,\"uniqueDeviceID\": null,\"platform\": null,\"model\": null,\"osVer\": null,\"locale\": null,\"revision\": null,\"birth\": null,\"modified\": null,\"deleted\": null";
        
        public Device(Factories.BagdadFactory _bagdadFactory)
        {
            bagdadFactory = _bagdadFactory; 
        }
        public Device()
        {
            bagdadFactory = new Factories.BagdadFactory();
        }

        protected override String GetEntityName() { return Constants.SERCOM_TB_DEVICE; }

        protected override String GetOps() { return ops_data; }

        protected override string GetAlias(string operation)
        {
            return "\"UPDATE_DEVICE\",";
        }

        public override Task<string> ConstructFilter(string conditionDate)
        {
            throw new NotImplementedException();
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

    }
}
