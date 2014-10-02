using System;
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

namespace Bagdad
{
    public partial class Me : PhoneApplicationPage
    {
        int idUser = 0;
        bool isFollowing;
        UserImageManager uim;
        UserViewModel uvm;

        public Me()
        {
            InitializeComponent();
            uim = new UserImageManager();
            uvm = new UserViewModel();
        }

        protected async override void OnNavigatedTo(NavigationEventArgs e)
        {
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

        private async void LoadUserData()
        {
            await uvm.GetUserProfileInfo(idUser);

            if (uvm.userId != App.ID_USER)
            {
                ProfileTitle.Text = uvm.userNickName;
                if (await uvm.ImFollowing())
                {
                    headButton.Content = AppResources.ProfileButtonFollowing + "  ";
                    headButton.Background = Resources["PhoneBackgroundBrush"] as SolidColorBrush;
                    headButton.Foreground = Resources["PhoneForegroundBrush"] as SolidColorBrush;
                    isFollowing = true;
                }
                else
                {
                    headButton.Content = AppResources.ProfileButtonFollow + "  ";
                    isFollowing = false;
                }
            }
            points.Text = uvm.points.ToString();
            following.Text = uvm.following.ToString();
            followers.Text = uvm.followers.ToString();
            userName.Text = uvm.userName;
            userBio.Text = uvm.userBio;
            userWebsite.Text = uvm.userWebsite;

            LoadImage();
        }

        private void LoadImage()
        {
            profileImage.Source = uim.GetUserImage(idUser);
            if(profileImage.Source == null) profileImage.Source = new System.Windows.Media.Imaging.BitmapImage(new Uri(uvm.userURLImage, UriKind.Absolute));
        }

        private void userWebsite_Tap(object sender, System.Windows.Input.GestureEventArgs e)
        {
            WebBrowserTask wbt = new WebBrowserTask();
            wbt.Uri = new Uri(uvm.userWebsite, UriKind.Absolute);
            wbt.Show();
        }

        private void followersGrid_Tap(object sender, System.Windows.Input.GestureEventArgs e)
        {
            MessageBox.Show(AppResources.Followers);
        }

        private void followingGrid_Tap(object sender, System.Windows.Input.GestureEventArgs e)
        {
            MessageBox.Show(AppResources.Following);
        }

        private void headButton_Click(object sender, RoutedEventArgs e)
        {
            if (uvm.userId != App.ID_USER)
            {
                updateButton();    
            }
            else
            {
                MessageBox.Show("Edit");
            }
        }

        private void updateButton()
        {
            if (!isFollowing)
            {
                headButton.Content = AppResources.ProfileButtonFollowing + "  ";
                headButton.Background = Resources["PhoneBackgroundBrush"] as SolidColorBrush;
                headButton.Foreground = Resources["PhoneForegroundBrush"] as SolidColorBrush;
                isFollowing = true;
            }
            else
            {
                headButton.Content = AppResources.ProfileButtonFollow + "  ";
                headButton.Background = Resources["PhoneAccentBrush"] as SolidColorBrush;
                headButton.Foreground = new System.Windows.Media.SolidColorBrush(Colors.White);
                isFollowing = false;
            }
        }
    }
}