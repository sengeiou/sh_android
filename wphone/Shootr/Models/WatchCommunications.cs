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
            List<BaseModelJsonConstructor> watches = new List<BaseModelJsonConstructor>();

            try
            {
                if (job["status"]["code"].ToString().Equals("OK") && !job["ops"][0]["metadata"]["items"].ToString().Equals("0"))
                {
                    foreach (JToken watchJson in job["ops"][0]["data"])
                    {
                        int idMatchParsed = 0, statusParsed = 0, idUserParsed = 0;
                        int.TryParse(watchJson["idMatch"].ToString(), out idMatchParsed);
                        int.TryParse(watchJson["status"].ToString(), out statusParsed);
                        int.TryParse(watchJson["idUser"].ToString(), out idUserParsed);

                        Watch watch = bagdadFactory.CreateWatch();

                        watch.idMatch = idMatchParsed;
                        watch.idUser = idUserParsed;
                        watch.status = statusParsed;

                        watch.csys_birth = Double.Parse(watchJson["birth"].ToString());
                        watch.csys_modified = Double.Parse(watchJson["modified"].ToString());
                        watch.csys_deleted = ((!String.IsNullOrEmpty(watchJson["deleted"].ToString())) ? Double.Parse(watchJson["deleted"].ToString()) : 0);
                        watch.csys_revision = int.Parse(watchJson["revision"].ToString());
                        watch.csys_synchronized = 'S';
                        watches.Add(watch);
                    }
                }
            }
            catch (Exception e)
            {

                throw new Exception("E R R O R - Match - ParseJson: " + e.Message);
            }
            return watches;
        }
    }
}
