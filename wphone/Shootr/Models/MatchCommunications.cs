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
            List<BaseModelJsonConstructor> matches = new List<BaseModelJsonConstructor>();

            try
            {
                if (job["status"]["code"].ToString().Equals("OK") && !job["ops"][0]["metadata"]["items"].ToString().Equals("0"))
                {
                    foreach (JToken matchJson in job["ops"][0]["data"])
                    {
                        int idMatchParsed = 0, statusParsed = 0, idLocalTeamParsed = 0, idVisitorTeamParsed = 0;
                        int.TryParse(matchJson["idMatch"].ToString(), out idMatchParsed);
                        int.TryParse(matchJson["status"].ToString(), out statusParsed);
                        int.TryParse(matchJson["idLocalTeam"].ToString(), out idLocalTeamParsed);
                        int.TryParse(matchJson["idVisitorTeam"].ToString(), out idVisitorTeamParsed);

                        Match match = bagdadFactory.CreateMatch();

                        match.idMatch = idMatchParsed;
                        match.matchDate = Double.Parse(matchJson["matchDate"].ToString());
                        match.status = statusParsed;
                        match.idLocalTeam = idLocalTeamParsed;
                        match.idVisitorTeam = idLocalTeamParsed;
                        match.localTeamName = matchJson["localTeamName"].ToString();
                        match.visitorTeamName = matchJson["visitorTeamName"].ToString();

                        match.csys_birth = Double.Parse(matchJson["birth"].ToString());
                        match.csys_modified = Double.Parse(matchJson["modified"].ToString());
                        match.csys_deleted = ((!String.IsNullOrEmpty(matchJson["deleted"].ToString())) ? Double.Parse(matchJson["deleted"].ToString()) : 0);
                        match.csys_revision = int.Parse(matchJson["revision"].ToString());
                        match.csys_synchronized = 'S';
                        matches.Add(match);
                    }
                }
            }
            catch (Exception e)
            {

                throw new Exception("E R R O R - Match - ParseJson: " + e.Message);
            }
            return matches;
        }

        protected override String GetOps() { return ops_data; }

        protected override string GetAlias(string operation)
        {
            return "\"GET_MATCHES\",";
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
                conditionDate = "{\"filterItems\":[{\"comparator\":\"eq\",\"name\":\"deleted\",\"value\":null},{\"comparator\":\"ne\",\"name\":\"deleted\",\"value\":null}],\"filters\":[],\"nexus\":\"or\"}"; //
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
            return "\"filterItems\":[], \"filters\":[" + conditionDate + ",{\"filterItems\":[ " + sbFilterIdMatch.ToString() + "],\"filters\":[],\"nexus\":\"or\"},{\"filterItems\": [{\"comparator\": \"eq\",\"name\": \"status\",\"value\": 1},{\"comparator\": \"eq\",\"name\": \"status\",\"value\": 0}],\"filters\": [],\"nexus\": \"or\"},{\"filterItems\": [{\"comparator\": \"ne\",\"name\": \"matchDate\",\"value\": null}],\"filters\": [],\"nexus\": \"or\"}],\"nexus\":\"and\"";
        
        }
    }
}
