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
                throw new Exception("Match - GetAnotherMatches: " + e.Message, e);
            }

            return newMatches;
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
                throw new Exception("Match - GetListOfUsersWatchingTheMatch: " + e.Message, e);
            }

            return users;
        }

        public async override Task<int> SaveData(List<BaseModelJsonConstructor> matches)
        {
            int done = 0;
            Database database;

            try
            {

                database = await DataBaseHelper.GetDatabaseAsync();
                using (var custstmt = await database.PrepareStatementAsync(SQLQuerys.InsertMatchesData))
                {

                    foreach (Match match in matches)
                    {
                        //idMatch, matchDate, status, idLocalTeam, idVisitorTeam, localTeamName, visitorTeamName
                        custstmt.Reset();

                        custstmt.BindIntParameterWithName("@idMatch", match.idMatch);
                        custstmt.BindTextParameterWithName("@matchDate", Util.FromUnixTime(match.matchDate.ToString()).ToString("s").Replace('T', ' '));
                        custstmt.BindIntParameterWithName("@status", match.status);
                        custstmt.BindIntParameterWithName("@idLocalTeam", match.idLocalTeam);
                        custstmt.BindIntParameterWithName("@idVisitorTeam", match.idVisitorTeam);
                        custstmt.BindTextParameterWithName("@localTeamName", match.localTeamName);
                        custstmt.BindTextParameterWithName("@visitorTeamName", match.visitorTeamName);
                        custstmt.BindTextParameterWithName("@csys_birth", Util.FromUnixTime(match.csys_birth.ToString()).ToString("s").Replace('T', ' '));
                        custstmt.BindTextParameterWithName("@csys_modified", Util.FromUnixTime(match.csys_modified.ToString()).ToString("s").Replace('T', ' '));
                        if (match.csys_deleted == 0)
                            custstmt.BindNullParameterWithName("@csys_deleted");
                        else
                            custstmt.BindTextParameterWithName("@csys_deleted", Util.FromUnixTime(match.csys_deleted.ToString()).ToString("s").Replace('T', ' '));
                        custstmt.BindIntParameterWithName("@csys_revision", match.csys_revision);
                        custstmt.BindTextParameterWithName("@csys_synchronized", "S");


                        await custstmt.StepAsync().AsTask().ConfigureAwait(false);
                        done++;
                    }
                }
                DataBaseHelper.DBLoaded.Set();
            }
            catch (Exception e)
            {
                string sError = Database.GetSqliteErrorCode(e.HResult).ToString();
                DataBaseHelper.DBLoaded.Set();
                throw new Exception("E R R O R - Match - SaveData: " + sError + " / " + e.Message);
            }
            return done;
        }
    }
}
