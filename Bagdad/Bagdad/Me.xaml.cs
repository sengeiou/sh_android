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

namespace Bagdad
{
    public partial class Me : PhoneApplicationPage
    {
        int idUser = 0;
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

            if (uvm.userId != App.ID_USER) ProfileTitle.Text = uvm.userNickName;
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
    }
}