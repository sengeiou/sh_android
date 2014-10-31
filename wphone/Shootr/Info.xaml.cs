using System;
using System.Windows.Navigation;
using Microsoft.Phone.Controls;
using Microsoft.Phone.Shell;
using Bagdad.ViewModels;
using Bagdad.Resources;
using Bagdad.Factories;
using System.Windows.Threading;

namespace Bagdad
{
    public partial class Info : PhoneApplicationPage
    {
        int idUser = 0;
        int offset = 0;
        private int scrollToChargue = 0;
        private bool endOfList = false;
        private int charge = 0;
        InfoWatchListOfMatchesViewModel infoViewModel;
        public ProgressIndicator progress;
        BagdadFactory bagdadFactory;
        DispatcherTimer timer;

        public Info()
        {
            bagdadFactory = new BagdadFactory();
            InitializeComponent();
            infoViewModel = bagdadFactory.CreateInfoWatchListOfMatchesViewModel();
            //DataContext = infoViewModel;
            timer = new DispatcherTimer()
            {
                Interval = new TimeSpan(0, 0, 0, 2)
            };
            timer.Tick+=timer_Tick;
            timer.Start();
            System.Diagnostics.Debug.WriteLine("-----------------------------------------\nTimer Start on Info to Refresh Data\n-----------------------------------------");
        }

        protected async override void OnNavigatedTo(NavigationEventArgs e)
        {
            App.UpdateServices(Utils.ServiceCommunication.enumTypeSynchro.ST_DOWNLOAD_ONLY, Utils.ServiceCommunication.enumSynchroTables.WATCH);
            await infoViewModel.GetCurrentWatchList();
            DataContext = infoViewModel;
        }
        
        private void BuildLocalizedApplicationBar()
        {

            // Set the page's ApplicationBar to a new instance of ApplicationBar.
            ApplicationBar = new ApplicationBar();

            ApplicationBarIconButton appBarButtonSearch =
                new ApplicationBarIconButton(new
                Uri("/Resources/icons/appbar.magnify.png", UriKind.Relative));
            appBarButtonSearch.Text = AppResources.FindFriends;
            appBarButtonSearch.Click += appBarButtonSearch_Click;
            ApplicationBar.Buttons.Add(appBarButtonSearch);
            
            ApplicationBarMenuItem appBarMenuItemPeople =
                new ApplicationBarMenuItem(AppResources.People);
            appBarMenuItemPeople.IsEnabled = false;
            ApplicationBar.MenuItems.Add(appBarMenuItemPeople);

            ApplicationBarMenuItem appBarMenuItemTimeLine =
                new ApplicationBarMenuItem(AppResources.TimeLine);
            appBarMenuItemTimeLine.Click += appBarMenuItemTimeLine_Click;
            ApplicationBar.MenuItems.Add(appBarMenuItemTimeLine);

            ApplicationBarMenuItem appBarMenuItemMe =
                new ApplicationBarMenuItem(AppResources.Me);
            appBarMenuItemMe.Click += appBarMenuItemMe_Click;
            ApplicationBar.MenuItems.Add(appBarMenuItemMe);
        }

        private void appBarButtonSearch_Click(object sender, EventArgs e)
        {
            NavigationService.Navigate(new Uri("/FindFriends.xaml", UriKind.Relative));
        }

        private void appBarMenuItemMe_Click(object sender, EventArgs e)
        {
            NavigationService.Navigate(new Uri("/Me.xaml?idUser=" + App.ID_USER, UriKind.Relative));
        }

        private void appBarMenuItemTimeLine_Click(object sender, EventArgs e)
        {
            NavigationService.Navigate(new Uri("/TimeLine.xaml", UriKind.Relative));
        }

        private void goToProfile_Tap(object sender, System.Windows.Input.GestureEventArgs e)
        {
            
        }

        private async void followingList_MouseEnter(object sender, System.Windows.Input.MouseEventArgs e)
        {
        }

        private void PhoneApplicationPage_OrientationChanged(object sender, OrientationChangedEventArgs e)
        {
         
        }
        async void timer_Tick(object sender, EventArgs e)
        {
            if (!App.isSynchroRunning())
            {
                System.Diagnostics.Debug.WriteLine("-----------------------------------------\nTimer Stop on Info to Refresh Data\n-----------------------------------------");
                timer.Stop();
                await infoViewModel.GetCurrentWatchList();
                MatchList.ItemsSource = null;
                MatchList.ItemsSource = infoViewModel.listOfWatchingMatches;
            }
        }
    }
}