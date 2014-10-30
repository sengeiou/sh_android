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

        private async Task<List<int>> getMatchesUserFollowing()
        {
            List<int> matchList = new List<int>();
            try
            {
                Database db = await DataBaseHelper.GetDatabaseAsync();

                Statement st = await db.PrepareStatementAsync(SQLQuerys.getMatchesUserFollowing);

                while (await st.StepAsync())
                {
                    matchList.Add(st.GetIntAt(0));
                }

                DataBaseHelper.DBLoaded.Set();
            }
            catch (Exception e)
            {
                throw new Exception("E R R O R - Matches - getMatchesUserFollowing: " + e.Message);
            }
            return matchList;
        }

        public override async Task<string> ConstructFilter(string conditionDate)
        {
            StringBuilder sbFilterIdMatch = new StringBuilder();
            try
            {
                
                var matchList = await getMatchesUserFollowing();
                bool isFirst = true;
                foreach (int match in matchList)
                {
                    if (!isFirst)
                    {
                        sbFilterIdMatch.Append(",");
                    }
                    sbFilterIdMatch.Append("{\"comparator\":\"eq\",\"name\":\"idMatch\",\"value\":" + match + "}");
                    isFirst = false;
                }
            }
            catch (Exception e)
            {
                throw new Exception("E R R O R - Watch - constructFilter: " + e.Message);
            }
            return "\"filterItems\":[], \"filters\":[" + conditionDate + ",{\"filterItems\":[ " + sbFilterIdMatch.ToString() + "],\"filters\":[],\"nexus\":\"or\"},{\"filterItems\": [{\"comparator\": \"eq\",\"name\": \"status\",\"value\": 1},{\"comparator\": \"eq\",\"name\": \"status\",\"value\": 0}],\"filters\": [],\"nexus\": \"or\"}],\"nexus\":\"and\"";
        
        }
    }
}
