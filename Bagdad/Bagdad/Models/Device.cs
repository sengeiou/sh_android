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
    public class Device //: BaseModelJsonConstructor
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
        /*
        protected override String GetEntityName() { return Constants.SERCOM_TB_DEVICE; }

        protected override String GetOps() { return ops_data; }

        */
    }
}
