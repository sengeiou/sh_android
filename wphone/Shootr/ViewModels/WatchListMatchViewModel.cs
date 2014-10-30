using Bagdad.Factories;
using Bagdad.Models;
using Bagdad.Resources;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Media;

namespace Bagdad.ViewModels
{
    public class WatchListMatchViewModel
    {
        public int idMatch { get; set; }
        public String matchName { get; set; }
        public String matchDate { get; set; }
        public Boolean isLive { get; set; }
        public List<WatchListOfMatchUserInfoViewModel> usersViewingMatch { get; set; }
        public BagdadFactory bagdadFactory { private get; set; }

        public WatchListMatchViewModel()
        {
            bagdadFactory = new BagdadFactory();
        }

        public WatchListMatchViewModel(BagdadFactory _bagdadFactory)
        {
            bagdadFactory = _bagdadFactory;
        }

        public async Task<WatchListMatchViewModel> GetNextMatchOfFavoriteTeam(int _idTeam)
        {
            
            Match match = bagdadFactory.CreateMatch();
            try
            {
                match = await match.GetNextTeamMatch(_idTeam);

                idMatch = match.idMatch;
                matchName = match.localTeamName + "-" + match.visitorTeamName;
                matchDate = Utils.Util.FromUnixTime(match.matchDate.ToString()).ToString();
                isLive = match.status == 1 ? true : false;

                usersViewingMatch = await GetMatchViewerUsersInfo(idMatch, isLive);
            }
            catch (Exception e)
            {
                System.Diagnostics.Debug.WriteLine("E R R O R:  WatchListMatchViewModel - GetNextMatchOfFavoriteTeam :" + e.Message);
            }
            return this;
        }

        public async Task<List<WatchListMatchViewModel>> GetAnotherMatches(int _idTeam)
        {
            List<WatchListMatchViewModel> matchList = bagdadFactory.CreateListOfWatchListMatchViewModel();
            Match matches = bagdadFactory.CreateMatch();
            try
            {
                foreach (Match match in await matches.GetAnotherMatches(_idTeam))
                {
                    matchList.Add(
                        bagdadFactory.CreateFilledWatchListMatchViewModel(
                            match.idMatch,
                            match.localTeamName + "-" + match.visitorTeamName,
                            Utils.Util.FromUnixTime(match.matchDate.ToString()).ToString(),
                            (match.status == 1 ? true : false),
                            await GetMatchViewerUsersInfo(idMatch, isLive)
                        )
                    );
                }
            }
            catch (Exception e)
            {
                System.Diagnostics.Debug.WriteLine("E R R O R:  WatchListMatchViewModel - GetAnotherMatches :" + e.Message);
            }
            return matchList;
        }

        public async Task<List<WatchListOfMatchUserInfoViewModel>> GetMatchViewerUsersInfo(int _idMatch, bool _isLive)
        {
            Match match = bagdadFactory.CreateMatch();
            List<WatchListOfMatchUserInfoViewModel> users = bagdadFactory.CreateListOfWatchListOfMatchUserInfoViewModel();
            try
            {
                List<UserViewModel> usersInfo = await match.GetListOfUsersWatchingTheMatch(_idMatch);

                UserViewModel currentUserInfo = bagdadFactory.CreateUserViewModel();
                await currentUserInfo.GetUserProfileInfo(App.ID_USER);

                if (usersInfo.Contains(currentUserInfo))
                {
                    users.Add(
                        bagdadFactory.CreateWatchListOfMatchUserInfoViewModel(
                            currentUserInfo,
                            AppResources.Watching,
                            (_isLive ? Application.Current.Resources["PhoneAccentBrush"] as SolidColorBrush : Application.Current.Resources["PhoneDisabledBrush"] as SolidColorBrush),
                            Visibility.Visible
                        )
                    );
                }
                else
                {
                    users.Add(
                        bagdadFactory.CreateWatchListOfMatchUserInfoViewModel(
                            currentUserInfo,
                            AppResources.NotWatching,
                            Application.Current.Resources["PhoneDisabledBrush"] as SolidColorBrush,
                            Visibility.Visible
                        )
                    );
                }

                foreach (UserViewModel user in usersInfo)
                {
                    if (user.idUser != App.ID_USER)
                    {
                        users.Add(
                            bagdadFactory.CreateWatchListOfMatchUserInfoViewModel(
                                user,
                                AppResources.Watching,
                                (_isLive ? Application.Current.Resources["PhoneAccentBrush"] as SolidColorBrush : Application.Current.Resources["PhoneDisabledBrush"] as SolidColorBrush),
                                Visibility.Collapsed
                            )
                        );
                    }
                }
            }
            catch (Exception e)
            {
                System.Diagnostics.Debug.WriteLine("E R R O R:  WatchListMatchViewModel - GetMatchViewerUsersInfo :" + e.Message);
            }
            return users;
        }
    }
}
