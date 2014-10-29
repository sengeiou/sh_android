using Bagdad.Utils;
using Bagdad.ViewModels;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Bagdad.Models
{
    public partial class Watch : BaseModelJsonConstructor
    {
        private String ops_data = "\"idUser\": null,\"idFavoriteTeam\": null,\"favoriteTeamName\": null,\"userName\": null,\"name\": null,\"photo\": null,\"bio\": null,\"website\": null,\"points\": null,\"numFollowings\": null,\"numFollowers\": null,\"revision\": null,\"birth\": null,\"modified\": null,\"deleted\": null";
        protected override String GetOps() { return ops_data; }

        protected override string GetAlias(string operation)
        {
            return "\"GET_WATCH\",";
        }
        public override string ConstructAlias(string operation)
        {
            return base.ConstructAlias(operation);
        }
        public override Task<string> ConstructFilter(string conditionDate)
        {
            throw new NotImplementedException();
        }

        public override List<BaseModelJsonConstructor> ParseJson(JObject job)
        {
            throw new NotImplementedException();
        }
    }
}
