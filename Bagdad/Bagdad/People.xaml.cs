using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Navigation;
using Microsoft.Phone.Controls;
using Microsoft.Phone.Shell;
using System.Threading.Tasks;
using Bagdad.ViewModels;
using Bagdad.Utils;
using System.Windows.Automation.Peers;
using System.Windows.Automation.Provider;
using Bagdad.Resources;
using System.Diagnostics;
using Bagdad.Models;

namespace Bagdad
{
    public partial class People : PhoneApplicationPage
    {
        int idUser = 0;
        int offset = 0;
        private int scrollToChargue = 0;
        private bool endOfList = false;
        private int charge = 0;
        FollowsViewModel followings;
        public ProgressIndicator progress;

        public People()
        {
            InitializeComponent();
            followings = new FollowsViewModel();
            DataContext = followings;
            BuildLocalizedApplicationBar();
            progress = new ProgressIndicator()
            {
                Text = AppResources.Synchroning,
                IsIndeterminate = true,
                IsVisible = false

            };
            SystemTray.SetProgressIndicator(this, progress);
        }

        protected async override void OnNavigatedTo(NavigationEventArgs e)
        {
            idUser = App.ID_USER;

            if(followings.Followings.Count == 0) await LoadFollowingsData();
            progress.IsVisible = false;
        }

        private async Task<int> LoadFollowingsData()
        {
            try
            {

                int returned = await followings.LoadData(idUser, offset, Constants.CONST_PEOPLE);
                offset += Constants.SERCOM_PARAM_TIME_LINE_OFFSET_PAG;
                return returned;

            }
            catch (Exception e)
            {
                Debug.WriteLine("E R R O R - Following.xaml.cs - LoadFollowingsData: " + e.Message);
                MessageBox.Show(AppResources.ServerError);
                return -1;
            }
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
            User user = ((FollowViewModel)followingList.SelectedItem).userInfo;
            int userId = user.idUser;

            PhoneApplicationService.Current.State["user"] = user;
            
            NavigationService.Navigate(new Uri("/Me.xaml?idUser=" + userId, UriKind.Relative));
        }

        private async void followingList_MouseEnter(object sender, System.Windows.Input.MouseEventArgs e)
        {
            progress.IsVisible = true;

            ListBoxAutomationPeer svAutomation = (ListBoxAutomationPeer)ScrollViewerAutomationPeer.CreatePeerForElement(followingList);
            // not feeling creative with my var names today...
            IScrollProvider scrollInterface = (IScrollProvider)svAutomation.GetPattern(PatternInterface.Scroll);

            if (followingList.Items.Count() != 0)
            {
                scrollToChargue = 100 - (15 * 100 / followingList.Items.Count());
            }

            if (scrollInterface.VerticalScrollPercent >= scrollToChargue)
            {
                if (!endOfList)
                {
                    charge = await LoadFollowingsData();

                    //if there is no more shots, don't need to charge it again
                    if (charge == 0)
                    {
                        endOfList = true;
                    }
                }

            }
            progress.IsVisible = false;
        }

        private void PhoneApplicationPage_OrientationChanged(object sender, OrientationChangedEventArgs e)
        {
            followingList.ItemsSource = null;
            followingList.ItemsSource = followings.Followings;
        }
    }
}