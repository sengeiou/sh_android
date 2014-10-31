using Bagdad.Factories;
using Bagdad.Models;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bagdad.ViewModels
{
    public class InfoWatchListOfMatchesViewModel : INotifyPropertyChanged
    {
        public List<WatchListMatchViewModel> listOfWatchingMatches { get; set; }

        private BagdadFactory bagdadFactory;

        public InfoWatchListOfMatchesViewModel()
        {
            bagdadFactory = new BagdadFactory();
            listOfWatchingMatches = bagdadFactory.CreateListOfWatchListMatchViewModel();
        }

        public InfoWatchListOfMatchesViewModel(BagdadFactory _bagdadFactory)
        {
            bagdadFactory = _bagdadFactory;
            listOfWatchingMatches = bagdadFactory.CreateListOfWatchListMatchViewModel();
        }

        public async Task GetCurrentWatchList()
        {
            listOfWatchingMatches.Clear();
            User user = bagdadFactory.CreateUser();
            int idFavoriteTeam = await user.GetCurrentUserFavoriteTeamId();
            await GetNextTeamMatch(idFavoriteTeam);
            await GetAnotherMatches(idFavoriteTeam);
        }

        private async Task GetNextTeamMatch(int _idFavoriteTeam)
        {
            WatchListMatchViewModel watchListMatch = bagdadFactory.CreateWatchListMatchViewModel();
            var a = await watchListMatch.GetNextMatchOfFavoriteTeam(_idFavoriteTeam);
            if(a.idMatch != 0) listOfWatchingMatches.Add(a);
        }

        private async Task GetAnotherMatches(int _idFavoriteTeam)
        {
            WatchListMatchViewModel watchListMatch = bagdadFactory.CreateWatchListMatchViewModel();

            foreach (WatchListMatchViewModel matchInfo in await watchListMatch.GetAnotherMatches(_idFavoriteTeam))
            {
                listOfWatchingMatches.Add(matchInfo);
            }
        }

        public event PropertyChangedEventHandler PropertyChanged;

        private void NotifyPropertyChanged(String propertyName)
        {
            PropertyChangedEventHandler handler = PropertyChanged;
            if (null != handler)
            {
                handler(this, new PropertyChangedEventArgs(propertyName));
            }
        }
    }

}