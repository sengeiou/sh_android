using Bagdad.Utils;
using Bagdad.ViewModels;
using Newtonsoft.Json.Linq;
using SQLiteWinRT;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bagdad.Models
{
    public partial class Match : BaseModelJsonConstructor
    {
        public override List<BaseModelJsonConstructor> ParseJson(JObject job)
        {
            throw new NotImplementedException();
        }

        protected override String GetOps() { return ops_data; }

        protected override string GetAlias(string operation)
        {
            return "";
        }

        public override async Task<string> ConstructFilter(string conditionDate)
        {
            return "";
        }
    }
}
