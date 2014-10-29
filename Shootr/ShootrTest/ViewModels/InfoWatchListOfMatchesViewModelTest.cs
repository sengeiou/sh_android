using Bagdad.ViewModels;
using Microsoft.VisualStudio.TestPlatform.UnitTestFramework;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BagdadTest.ViewModels
{
    [TestClass]
    public class InfoWatchListOfMatchesViewModelTest
    {
        [TestMethod]
        public void GetMatchListReturnSomething()
        {
            InfoWatchListOfMatchesViewModel infoWatchList = new InfoWatchListOfMatchesViewModel();
            infoWatchList.GetCurrentWatchList().Wait();
            Assert.AreNotEqual(infoWatchList.listOfWatchingMatches.Count, 0);
        }


    }
}
