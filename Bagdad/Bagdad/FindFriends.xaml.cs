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
using System.Windows.Input;
using System.Windows.Media;

namespace Bagdad
{
    public partial class FindFriends : PhoneApplicationPage
    {
        int idUser = 0;
        int offset = 0;
        private int scrollToChargue = 0;
        private bool endOfList = false;
        private int charge = 0;
        bool loadedFromServer = false;
        FollowsViewModel searchedFriends;
        UserViewModel uvm;
        public ProgressIndicator progress;

        public FindFriends()
        {
            InitializeComponent();
            uvm = new UserViewModel();
            searchedFriends = new FollowsViewModel();
            DataContext = searchedFriends;
            BuildLocalizedApplicationBar();
            progress = new ProgressIndicator()
            {
                Text = AppResources.Synchroning,
                IsIndeterminate = true,
                IsVisible = false

            };
            SystemTray.SetProgressIndicator(this, progress);
        }

        protected override void OnNavigatedTo(NavigationEventArgs e)
        {
        }

        private void BuildLocalizedApplicationBar()
        {

            // Set the page's ApplicationBar to a new instance of ApplicationBar.
            ApplicationBar = new ApplicationBar();

            ApplicationBar.Mode = ApplicationBarMode.Default;
            
            ApplicationBarMenuItem appBarMenuItemPeople =
                new ApplicationBarMenuItem(AppResources.People);
            appBarMenuItemPeople.Click += appBarMenuItemPeople_Click;
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

        private void appBarMenuItemPeople_Click(object sender, EventArgs e)
        {
            NavigationService.Navigate(new Uri("/People.xaml", UriKind.Relative));
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

            int userId = ((FollowViewModel)findList.SelectedItem).userInfo.idUser;
            
            NavigationService.Navigate(new Uri("/Me.xaml?idUser=" + userId, UriKind.Relative));
        }

        private void SearchBar_Loaded(object sender, RoutedEventArgs e)
        {
            SearchBar.Focus();
        }

        private async void SearchBar_KeyDown(object sender, System.Windows.Input.KeyEventArgs e)
        {
            if (e.Key == Key.Enter && SearchBar.Text.Length > 0)
            {
                progress.IsVisible = true;

                if (findList.Items.Count > 0) findList.ScrollIntoView(findList.Items.First());

                if (App.isInternetAvailable)
                {
                    searchedFriends = await uvm.FindUsersInServer(SearchBar.Text, 0);
                    loadedFromServer = true;
                }
                else
                {
                    searchedFriends = await uvm.FindUsersInLocal(SearchBar.Text);
                    loadedFromServer = false;
                }

                DataContext = searchedFriends;
                findList.ItemsSource = null;
                findList.ItemsSource = searchedFriends.followings;


                if (findList.Items.Count > 0)
                {
                    Focus();
                    endOfList = false;
                    offset = searchedFriends.followings.Count;
                    NoResults.Visibility = System.Windows.Visibility.Collapsed;
                }
                else NoResults.Visibility = System.Windows.Visibility.Visible;

                progress.IsVisible = false;
            }
            else NoResults.Visibility = System.Windows.Visibility.Collapsed;
        }

        private async void findList_MouseEnter(object sender, System.Windows.Input.MouseEventArgs e)
        {
            progress.IsVisible = true;

            ListBoxAutomationPeer svAutomation = (ListBoxAutomationPeer)ScrollViewerAutomationPeer.CreatePeerForElement(findList);
            // not feeling creative with my var names today...
            IScrollProvider scrollInterface = (IScrollProvider)svAutomation.GetPattern(PatternInterface.Scroll);

            if (findList.Items.Count() != 0)
            {
                scrollToChargue = 100 - (15 * 100 / findList.Items.Count());
            }

            if (scrollInterface.VerticalScrollPercent >= scrollToChargue && loadedFromServer)
            {
                if (!endOfList)
                {
                    charge = searchedFriends.followings.Count();
                    foreach (FollowViewModel user in (await uvm.FindUsersInServer(SearchBar.Text, offset)).followings)
                    {
                        searchedFriends.followings.Add(user);
                    }

                    DataContext = null;
                    DataContext = searchedFriends;

                    offset += Constants.SERCOM_PARAM_TIME_LINE_OFFSET_PAG;

                    //if there is no more shots, don't need to charge it again
                    if (findList.Items.Count() - charge < Constants.SERCOM_PARAM_TIME_LINE_OFFSET_PAG)
                    {
                        endOfList = true;
                    }
                }

            }
            progress.IsVisible = false;
        }

        private void PhoneApplicationPage_OrientationChanged(object sender, OrientationChangedEventArgs e)
        {
            findList.ItemsSource = null;
            findList.ItemsSource = searchedFriends.followings;
        }

        private async void Button_Tap(object sender, System.Windows.Input.GestureEventArgs e)
        {
            if (!((FollowViewModel)findList.SelectedItem).isFollowed)
            {
                await searchedFriends.AddAsFollowing(((FollowViewModel)findList.SelectedItem).userInfo);

                ((FollowViewModel)findList.SelectedItem).isFollowed = true;
                ((FollowViewModel)findList.SelectedItem).buttonVisible = Visibility.Visible;
                ((FollowViewModel)findList.SelectedItem).buttonText = AppResources.ProfileButtonFollowing + "  ";
                ((FollowViewModel)findList.SelectedItem).buttonBackgorund = Application.Current.Resources["PhoneAccentBrush"] as SolidColorBrush;
                ((FollowViewModel)findList.SelectedItem).buttonForeground = new System.Windows.Media.SolidColorBrush(Colors.White);
                ((FollowViewModel)findList.SelectedItem).buttonBorderColor = Application.Current.Resources["PhoneAccentBrush"] as SolidColorBrush;
                ((FollowViewModel)findList.SelectedItem).buttonIcon = new System.Windows.Media.Imaging.BitmapImage(new Uri("Resources/icons/appbar.user.added.png", UriKind.RelativeOrAbsolute));
                ((FollowViewModel)findList.SelectedItem).buttonIconVisible = System.Windows.Visibility.Visible;
            }
            else
            {
                await searchedFriends.RemoveFromFollowing(((FollowViewModel)findList.SelectedItem).userInfo);

                ((FollowViewModel)findList.SelectedItem).isFollowed = false;
                ((FollowViewModel)findList.SelectedItem).buttonVisible = Visibility.Visible;
                ((FollowViewModel)findList.SelectedItem).buttonText = AppResources.ProfileButtonFollow + "  ";
                ((FollowViewModel)findList.SelectedItem).buttonBackgorund = Application.Current.Resources["PhoneBackgroundBrush"] as SolidColorBrush;
                ((FollowViewModel)findList.SelectedItem).buttonForeground = Application.Current.Resources["PhoneDisabledBrush"] as SolidColorBrush;
                ((FollowViewModel)findList.SelectedItem).buttonBorderColor = Application.Current.Resources["PhoneDisabledBrush"] as SolidColorBrush;
                ((FollowViewModel)findList.SelectedItem).buttonIcon = new System.Windows.Media.Imaging.BitmapImage(new Uri("Resources/icons/appbar.user.add.png", UriKind.RelativeOrAbsolute));
                ((FollowViewModel)findList.SelectedItem).buttonIconVisible = System.Windows.Visibility.Visible;
            }

            findList.ItemsSource = null;
            findList.ItemsSource = searchedFriends.followings;

        }
    }
}