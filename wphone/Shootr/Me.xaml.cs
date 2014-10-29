﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Navigation;
using Microsoft.Phone.Controls;
using Microsoft.Phone.Shell;
using Bagdad.Utils;
using Bagdad.ViewModels;
using Microsoft.Phone.Tasks;
using Bagdad.Resources;
using System.Windows.Media;
using System.Windows.Threading;

namespace Bagdad
{
    public partial class Me : PhoneApplicationPage
    {
        int idUser = 0;
        bool isFollowing;
        UserImageManager uim;
        UserViewModel uvm;
        public ProgressIndicator progress;
        private DispatcherTimer timer;

        public Me()
        {
            InitializeComponent();
            uim = new UserImageManager();
            uvm = new UserViewModel();
            BuildLocalizedApplicationBar();
            progress = new ProgressIndicator()
            {
                Text = AppResources.Synchroning,
                IsIndeterminate = true,
                IsVisible = false

            };
            SystemTray.SetProgressIndicator(this, progress);

            timer = new DispatcherTimer();
            timer.Tick += timer_Tick;
            timer.Interval = new TimeSpan(0, 0, 0, 10);
            timer.Start();
        }

        protected override void OnNavigatedTo(NavigationEventArgs e)
        {
            if (!App.isSynchroRunning()) App.UpdateServices(ServiceCommunication.enumTypeSynchro.ST_FULL_SYNCHRO, ServiceCommunication.enumSynchroTables.FOLLOW);

            if (e.NavigationMode == NavigationMode.Back && PhoneApplicationService.Current.State.ContainsKey("RefreshNeeded") && (bool)PhoneApplicationService.Current.State["RefreshNeeded"] == true)
            {
                uvm.idUser = 0;
            }

            if (this.NavigationContext.QueryString.Count > 0 && !this.NavigationContext.QueryString["idUser"].Equals(""))
            {
                idUser = int.Parse(this.NavigationContext.QueryString["idUser"]);
            }
            else if (idUser == 0)
            {
                idUser = App.ID_USER;
            }

            LoadUserData();
        }

        #region METHODS
        private async void LoadUserData()
        {
            progress.IsVisible = true;

            if (uvm.idUser != idUser ) await uvm.GetUserProfileInfo(idUser);

            if (uvm.idUser == 0)
            {
                if (App.IsAirplaneMode()) MessageBox.Show(AppResources.AirplaneMode, AppResources.CanConnect, MessageBoxButton.OK);
                else MessageBox.Show(AppResources.NoInternetConnection, AppResources.CanConnect, MessageBoxButton.OK);
                NavigationService.GoBack();
            }
            else if (uvm.idUser != App.ID_USER)
            {
                if (uvm.isFollowed)
                {
                    headButtonText.Text = AppResources.ProfileButtonFollowing;
                    headButton.Background = Resources["PhoneAccentBrush"] as SolidColorBrush;
                    headButton.Foreground = new System.Windows.Media.SolidColorBrush(Colors.White);
                    headButtonIcon.ImageSource = new System.Windows.Media.Imaging.BitmapImage(new Uri("Resources/icons/appbar.check.png", UriKind.RelativeOrAbsolute));
                    headButtonIconVisible.Visibility = System.Windows.Visibility.Visible;
                    headButtonIconVisible.Fill = new System.Windows.Media.SolidColorBrush(Colors.White);
                    isFollowing = true;
                }
                else
                {
                    headButtonText.Text = AppResources.ProfileButtonFollow;
                    headButton.Background = Resources["PhoneBackgroundBrush"] as SolidColorBrush;
                    headButton.Foreground = Resources["PhoneForegroundBrush"] as SolidColorBrush;
                    headButtonIcon.ImageSource = new System.Windows.Media.Imaging.BitmapImage(new Uri("Resources/icons/appbar.add.png", UriKind.RelativeOrAbsolute));
                    headButtonIconVisible.Visibility = System.Windows.Visibility.Visible;
                    headButtonIconVisible.Fill = Resources["PhoneForegroundBrush"] as SolidColorBrush;
                    isFollowing = false;
                }
            }
            else
            {
                headButtonText.Text = AppResources.ProfileButtonEdit;
                headButton.Background = Resources["PhoneBackgroundBrush"] as SolidColorBrush;
                headButton.Foreground = Resources["PhoneForegroundBrush"] as SolidColorBrush;
                headButtonIconVisible.Visibility = System.Windows.Visibility.Collapsed;
                isFollowing = false;
            }

            if (uvm.idUser != 0)
            {
                ProfileTitle.Text = uvm.userNickName.ToUpper();
                points.Text = uvm.points.ToString();
                following.Text = uvm.following.ToString();
                followers.Text = uvm.followers.ToString();
                userName.Text = uvm.userName;
                userBio.Text = uvm.favoriteTeamName + " - " + uvm.userBio;
                userWebsite.Text = uvm.userWebsite;

                LoadImage();
            }
            progress.IsVisible = false;
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

        private async void updateButton()
        {
            if (!isFollowing)
            {
                headButtonText.Text = AppResources.ProfileButtonFollowing;
                headButton.Background = Resources["PhoneAccentBrush"] as SolidColorBrush;
                headButton.Foreground = new System.Windows.Media.SolidColorBrush(Colors.White);

                headButtonIcon.ImageSource = new System.Windows.Media.Imaging.BitmapImage(new Uri("Resources/icons/appbar.check.png", UriKind.RelativeOrAbsolute));
                headButtonIconVisible.Visibility = System.Windows.Visibility.Visible;
                headButtonIconVisible.Fill = new System.Windows.Media.SolidColorBrush(Colors.White);

                isFollowing = true;

                await uvm.AddAsFollowing();
            }
            else
            {
                if (MessageBox.Show(AppResources.unFollowQuestion + " " + uvm.userName + "?", uvm.userName, MessageBoxButton.OKCancel) == MessageBoxResult.OK)
                {
                    headButtonText.Text = AppResources.ProfileButtonFollow;
                    headButton.Background = Resources["PhoneBackgroundBrush"] as SolidColorBrush;
                    headButton.Foreground = Resources["PhoneForegroundBrush"] as SolidColorBrush;

                    headButtonIcon.ImageSource = new System.Windows.Media.Imaging.BitmapImage(new Uri("Resources/icons/appbar.add.png", UriKind.RelativeOrAbsolute));
                    headButtonIconVisible.Visibility = System.Windows.Visibility.Visible;
                    headButtonIconVisible.Fill = Resources["PhoneForegroundBrush"] as SolidColorBrush;

                    isFollowing = false;

                    await uvm.RemoveFromFollowing();
                }
            }
            App.UpdateServices(ServiceCommunication.enumTypeSynchro.ST_FULL_SYNCHRO, ServiceCommunication.enumSynchroTables.FOLLOW);
            PhoneApplicationService.Current.State["RefreshNeeded"] = true;
        }

        private void LoadImage()
        {
            profileImage.Source = uim.GetUserImage(idUser);
            if (profileImage.Source == null && !String.IsNullOrEmpty(uvm.userURLImage)) profileImage.Source = new System.Windows.Media.Imaging.BitmapImage(new Uri(uvm.userURLImage, UriKind.Absolute));
        }
        #endregion

        #region EVENTS
        private void userWebsite_Tap(object sender, System.Windows.Input.GestureEventArgs e)
        {
            WebBrowserTask wbt = new WebBrowserTask();
            wbt.Uri = new Uri(uvm.userWebsite, UriKind.Absolute);
            wbt.Show();
        }

        private void followersGrid_Tap(object sender, System.Windows.Input.GestureEventArgs e)
        {
            NavigationService.Navigate(new Uri("/Followers.xaml?idUser=" + idUser + "&userName=" + uvm.userNickName.ToUpper(), UriKind.Relative));
        }

        private void followingGrid_Tap(object sender, System.Windows.Input.GestureEventArgs e)
        {
            NavigationService.Navigate(new Uri("/Following.xaml?idUser=" + idUser + "&userName=" + uvm.userNickName.ToUpper(), UriKind.Relative));
        }

        private void headButton_Click(object sender, RoutedEventArgs e)
        {
            if (uvm.idUser != App.ID_USER)
            {
                updateButton();    
            }
            else
            {
                MessageBox.Show("Edit");
            }
        }

        protected override void OnBackKeyPress(System.ComponentModel.CancelEventArgs e)
        {
            base.OnBackKeyPress(e);
            timer.Stop();
        }

        private void appBarMenuItemTimeLine_Click(object sender, EventArgs e)
        {
            NavigationService.Navigate(new Uri("/TimeLine.xaml", UriKind.Relative));
            timer.Stop();
        }

        private void appBarMenuItemPeople_Click(object sender, EventArgs e)
        {
            NavigationService.Navigate(new Uri("/People.xaml", UriKind.Relative));
        }

        private void appBarMenuItemMe_Click(object sender, EventArgs e)
        {
            NavigationService.Navigate(new Uri("/Me.xaml?idUser=" + App.ID_USER, UriKind.Relative));
            timer.Stop();
        }

        /// <summary>
        /// Evento del timer
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        async void timer_Tick(object sender, EventArgs e)
        {
            ////if (!App.isSynchroRunning())
            ////{
            ////    App.UpdateServices(ServiceCommunication.enumTypeSynchro.ST_FULL_SYNCHRO, ServiceCommunication.enumSynchroTables.FOLLOW);
            ////}
        }

        #endregion

    }
}