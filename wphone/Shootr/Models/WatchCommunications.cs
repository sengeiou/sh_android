using Bagdad.Utils;
using Bagdad.ViewModels;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;

namespace Bagdad.Models
{
    public partial class Watch : BaseModelJsonConstructor
    {
        private String ops_data = "\"idUser\": null,\"idMatch\": null,\"status\": null,\"revision\": null,\"birth\": null,\"modified\": null,\"deleted\": null";
        protected override String GetOps() { return ops_data; }

        protected override string GetAlias(string operation)
        {
            return "\"GET_WATCH\",";
        }
        public override string ConstructAlias(string operation)
        {
            return base.ConstructAlias(operation);
        }
        public override async Task<string> ConstructFilter(string conditionDate)
        {
            StringBuilder sbFilterIdUser = new StringBuilder();
            try
            {
                Follow follow = bagdadFactory.CreateFollow();
                var followList = await follow.getidUserFollowing();
                bool isFirst = true;
                foreach (int idUser in followList)
                {
                    if (!isFirst)
                    {
                        sbFilterIdUser.Append(",");
                    }
                    sbFilterIdUser.Append("{\"comparator\":\"eq\",\"name\":\"idUser\",\"value\":" + idUser + "}");
                    isFirst = false;
                }
                sbFilterIdUser.Append(",{\"comparator\":\"eq\",\"name\":\"idUser\",\"value\":" + App.ID_USER + "}");
            }
            catch (Exception e)
            {
                throw new Exception("E R R O R - Watch - constructFilter: " + e.Message);
            }
            return "\"filterItems\":[], \"filters\":[" + conditionDate + ",{\"filterItems\":[ " + sbFilterIdUser.ToString() + "],\"filters\":[],\"nexus\":\"or\"},{\"filterItems\": [{\"comparator\": \"eq\",\"name\": \"status\",\"value\": 1}],\"filters\": [],\"nexus\": \"or\"}],\"nexus\":\"and\"";
        }

        public override List<BaseModelJsonConstructor> ParseJson(JObject job)
        {
            throw new NotImplementedException();
        }
    }
}
