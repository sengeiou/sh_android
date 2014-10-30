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

        public async Task<List<Match>> GetAnotherMatches(int _idTeam)
        {
            List<Match> newMatches = bagdadFactory.CreateListOfMatches();

            try
            {
                Database db = await DataBaseHelper.GetDatabaseAsync();
                Statement st = await db.PrepareStatementAsync(SQLQuerys.GetAnotherMatches);
                st.BindIntParameterWithName("@idLocalTeam", _idTeam);
                st.BindIntParameterWithName("@idVisitorTeam", _idTeam);

                while (await st.StepAsync())
                {
                    newMatches.Add(
                        bagdadFactory.CreateNextTeamMatch(
                            st.GetIntAt(0),                                         //_idMatch
                            st.GetTextAt(1),                                        //_localTeamName
                            st.GetTextAt(2),                                        //_visitorTeamName
                            Util.DateToDouble(DateTime.Parse(st.GetTextAt(3))),     //_matchDate
                            st.GetIntAt(4)                                          //_status
                        )
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

        public async Task<List<UserViewModel>> GetListOfUsersWatchingTheMatch(int _idMatch)
        {
            List<UserViewModel> users = bagdadFactory.CreateListOfUserViewModel();

            try
            {
                Database db = await DataBaseHelper.GetDatabaseAsync();
                Statement st = await db.PrepareStatementAsync(SQLQuerys.GetListOfUsersWatchingTheMatch);
                st.BindIntParameterWithName("@idMatch", idMatch);

                if (await st.StepAsync())
                {
                    users.Add(
                        bagdadFactory.CreateFillUserViewModelForInfoView(
                            st.GetIntAt(0),
                            st.GetTextAt(1),
                            st.GetTextAt(2),
                            st.GetTextAt(3),
                            st.GetIntAt(7),
                            st.GetIntAt(6),
                            st.GetIntAt(5),
                            st.GetTextAt(4),
                            st.GetTextAt(8),
                            st.GetTextAt(9),
                            st.GetIntAt(10),
                            st.GetIntAt(13),
                            Util.DateToDouble(DateTime.Parse(st.GetTextAt(11))),
                            Util.DateToDouble(DateTime.Parse(st.GetTextAt(12)))
                        )
                    );
                }

                DataBaseHelper.DBLoaded.Set();
            }
            catch (Exception e)
            {
                throw new Exception("Match - GetNextTeamMatch: " + e.Message, e);
            }

            return users;
        }

        public override Task<int> SaveData(List<BaseModelJsonConstructor> models)
        {
            throw new NotImplementedException();
        }
    }
}
