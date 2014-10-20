﻿using System;
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
using System.Windows.Media;

namespace Bagdad
{
    public partial class Following : PhoneApplicationPage
    {
        int idUser = 0;
        int offset = 0;
        private int scrollToChargue = 0;
        private bool endOfList = false;
        private int charge = 0;
        FollowsViewModel followings;
        public ProgressIndicator progress;

        public Following()
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
            progress.IsVisible = true;
            if (this.NavigationContext.QueryString.Count > 0 && !this.NavigationContext.QueryString["idUser"].Equals(""))
            {
                idUser = int.Parse(this.NavigationContext.QueryString["idUser"]);
            }
            else if (idUser == 0)
            {
                idUser = App.ID_USER;
            }

            if (this.NavigationContext.QueryString.Count > 0 && !this.NavigationContext.QueryString["userName"].Equals("") && Title.Text.Equals(""))
            {
                Title.Text = this.NavigationContext.QueryString["userName"];
            }

            if (followings.followings.Count == 0)
            {
                var res = await LoadFollowingsData();
                if (res == -1) NavigationService.GoBack();
            }
            else
            {
                followingList.ItemsSource = followings.followings;
            }

            progress.IsVisible = false;
        }

        private async Task<int> LoadFollowingsData()
        {
            try
            {
                if((App.isInternetAvailable) || (idUser == App.ID_USER))
                {
                    int returned = await followings.LoadData(idUser, offset, Constants.CONST_FOLLOWING);
                    offset += Constants.SERCOM_PARAM_TIME_LINE_OFFSET_PAG;
                    return returned;
                }
                else
                {
                    if (App.IsAirplaneMode()) MessageBox.Show(AppResources.AirplaneMode, AppResources.CanConnect, MessageBoxButton.OK);
                    else MessageBox.Show(AppResources.NoInternetConnection, AppResources.CanConnect, MessageBoxButton.OK);
                    return -1;
                }
            }
            catch (Exception e)
            {
                Debug.WriteLine("E R R O R - Following.xaml.cs - LoadFollowingsData: " + e.Message);
                MessageBox.Show(AppResources.NoInternetConnection, AppResources.CanConnect, MessageBoxButton.OK);
                return -1;
            }
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

            int userId = ((FollowViewModel)followingList.SelectedItem).userInfo.idUser;
            
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
            followingList.ItemsSource = followings.followings;
        }

        private async void Button_Tap(object sender, System.Windows.Input.GestureEventArgs e)
        {
            if (!((FollowViewModel)followingList.SelectedItem).isFollowed)
            {
                await followings.AddAsFollowing(((FollowViewModel)followingList.SelectedItem).userInfo);

                ((FollowViewModel)followingList.SelectedItem).isFollowed = true;
                ((FollowViewModel)followingList.SelectedItem).buttonVisible = Visibility.Visible;
                ((FollowViewModel)followingList.SelectedItem).buttonText = AppResources.ProfileButtonFollowing + "  ";
                ((FollowViewModel)followingList.SelectedItem).buttonBackgorund = Application.Current.Resources["PhoneAccentBrush"] as SolidColorBrush;
                ((FollowViewModel)followingList.SelectedItem).buttonForeground = new System.Windows.Media.SolidColorBrush(Colors.White);
                ((FollowViewModel)followingList.SelectedItem).buttonBorderColor = Application.Current.Resources["PhoneAccentBrush"] as SolidColorBrush;
                ((FollowViewModel)followingList.SelectedItem).buttonIcon = new System.Windows.Media.Imaging.BitmapImage(new Uri("Resources/icons/appbar.user.added.png", UriKind.RelativeOrAbsolute));
                ((FollowViewModel)followingList.SelectedItem).buttonIconVisible = System.Windows.Visibility.Visible;
            }
            else
            {
                await followings.RemoveFromFollowing(((FollowViewModel)followingList.SelectedItem).userInfo);

                ((FollowViewModel)followingList.SelectedItem).isFollowed = false;
                ((FollowViewModel)followingList.SelectedItem).buttonVisible = Visibility.Visible;
                ((FollowViewModel)followingList.SelectedItem).buttonText = AppResources.ProfileButtonFollow + "  ";
                ((FollowViewModel)followingList.SelectedItem).buttonBackgorund = Application.Current.Resources["PhoneBackgroundBrush"] as SolidColorBrush;
                ((FollowViewModel)followingList.SelectedItem).buttonForeground = Application.Current.Resources["PhoneDisabledBrush"] as SolidColorBrush;
                ((FollowViewModel)followingList.SelectedItem).buttonBorderColor = Application.Current.Resources["PhoneDisabledBrush"] as SolidColorBrush;
                ((FollowViewModel)followingList.SelectedItem).buttonIcon = new System.Windows.Media.Imaging.BitmapImage(new Uri("Resources/icons/appbar.user.add.png", UriKind.RelativeOrAbsolute));
                ((FollowViewModel)followingList.SelectedItem).buttonIconVisible = System.Windows.Visibility.Visible;
            }

            followingList.ItemsSource = null;
            followingList.ItemsSource = followings.followings;

        }
    }
}