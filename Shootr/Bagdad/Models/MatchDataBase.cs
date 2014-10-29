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
        public async Task<Match> GetNextTeamMatch(int _idTeam)
        {
            Match newMatch = bagdadFactory.CreateMatch();

            try
            {
                Database db = await DataBaseHelper.GetDatabaseAsync();
                Statement st = await db.PrepareStatementAsync(SQLQuerys.GetNextTeamMatch);
                st.BindIntParameterWithName("@idLocalTeam", _idTeam);
                st.BindIntParameterWithName("@idVisitorTeam", _idTeam);

                if (await st.StepAsync())
                {
                    newMatch = bagdadFactory.CreateNextTeamMatch(
                            st.GetIntAt(0),                                         //_idMatch
                            st.GetTextAt(1),                                        //_localTeamName
                            st.GetTextAt(2),                                        //_visitorTeamName
                            Util.DateToDouble(DateTime.Parse(st.GetTextAt(3))),     //_matchDate
                            st.GetIntAt(4)                                          //_status
                        );
                }

                DataBaseHelper.DBLoaded.Set();
            }
            catch (Exception e)
            {
                throw new Exception("Match - GetNextTeamMatch: " + e.Message, e);
            }

            return newMatch;
        }

        public override Task<int> SaveData(List<BaseModelJsonConstructor> models)
        {
            throw new NotImplementedException();
        }
    }
}
