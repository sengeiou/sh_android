using Bagdad.Factories;
using Bagdad.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bagdad.ViewModels
{
    public class InfoWatchListOfMatchesViewModel
    {
        public List<WatchListMatchViewModel> listOfWatchingMatches { get; set; }

        public BagdadFactory bagdadFactory;

        public InfoWatchListOfMatchesViewModel()
        {
            bagdadFactory = new BagdadFactory();
        }

        public InfoWatchListOfMatchesViewModel(BagdadFactory _bagdadFactory)
        {
            bagdadFactory = _bagdadFactory;
        }

        public async Task GetCurrentWatchList()
        {
            User user = bagdadFactory.CreateUser();
            int idFavoriteTeam = await user.GetCurrentUserFavoriteTeamId();
            await GetNextTeamMatch(idFavoriteTeam);
        }

        public async Task GetNextTeamMatch(int _idFavoriteTeam)
        {
            WatchListMatchViewModel watchListMatch = bagdadFactory.CreateWatchListMatchViewModel();

            listOfWatchingMatches.Add(await watchListMatch.GetNextMatchOfFavoriteTeam(_idFavoriteTeam));
        }

        public async Task GetAnotherMatches(int _idFavoriteTeam)
        {
            WatchListMatchViewModel watchListMatch = bagdadFactory.CreateWatchListMatchViewModel();

            foreach (WatchListMatchViewModel matchInfo in await watchListMatch.GetAnotherMatches(_idFavoriteTeam))
            {
                listOfWatchingMatches.Add(matchInfo);
            }
        }

    }

}