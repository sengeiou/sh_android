using Bagdad.Models;
using Bagdad.Resources;
using Bagdad.Utils;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Media;
using System.Windows.Media.Imaging;

namespace Bagdad.ViewModels
{
    public class FollowsViewModel : INotifyPropertyChanged
    {
         public ObservableCollection<FollowViewModel> Followings { get; private set; }

        public FollowsViewModel()
        {
            IsDataLoaded = false;
            this.Followings = new ObservableCollection<FollowViewModel>();
        }

        public bool IsDataLoaded
        {
            get;
            private set;
        }

        public async Task<int> LoadData(int idUser, int offset, String type)
        {
            try
            {
                if (idUser == App.ID_USER && !type.Equals(Constants.CONST_FOLLOWERS)) return await LocalDataLoad(idUser, type);
                else return  await ServerDataLoad(idUser, offset, type);
            }
            catch (Exception e)
            {
                throw new Exception("E R R O R : FollowingsViewModel - LoadData: " + e.Message);
            }
        }

        private async Task<int> LocalDataLoad(int idUser, String type)
        {
            int done = 0;

            UserImageManager userImageManager = new UserImageManager();
            Follow follow = new Follow();
            foreach (FollowViewModel following in await follow.GetUserFollowingLocalData(idUser, type))
            {
                //image
                following.userInfo.userImage = userImageManager.GetUserImage(following.userInfo.idUser);
                if (following.userInfo.userImage == null && !String.IsNullOrEmpty(following.userInfo.userURLImage)) following.userInfo.userImage = new System.Windows.Media.Imaging.BitmapImage(new Uri(following.userInfo.userURLImage, UriKind.Absolute));

                //LOCALS ARE ONLY FOLLOWINGS, SO DOESN'T NEED BUTTON
                following.buttonVisible = Visibility.Collapsed;

                Followings.Add(following);
            }

            return done;
        }

        private async Task<int> ServerDataLoad(int idUser, int offset, String type)
        {
            int done = 0;

            UserImageManager userImageManager = new UserImageManager();
            Follow follow = new Follow();

            List<FollowViewModel> follows;

            if (type.Equals(Constants.CONST_FOLLOWING))
                follows = await follow.GetUserFollowingFromServer(idUser, offset);
            else
                follows = await follow.GetUserFollowersFromServer(idUser, offset);

            foreach (FollowViewModel following in follows)
            {
                //image
                following.userInfo.userImage = userImageManager.GetUserImage(following.userInfo.idUser);
                if (following.userInfo.userImage == null && !String.IsNullOrEmpty(following.userInfo.userURLImage)) following.userInfo.userImage = new System.Windows.Media.Imaging.BitmapImage(new Uri(following.userInfo.userURLImage, UriKind.Absolute));

                if (following.userInfo.idUser == App.ID_USER)
                {
                    //Don't Show The Button
                    following.buttonVisible = Visibility.Collapsed;
                }
                else
                {
                    //Default Button
                    if (following.userInfo.isFollowed)
                    {
                        following.buttonVisible = Visibility.Collapsed;
                        
                    }
                    else
                    {
                        following.buttonVisible = Visibility.Visible;
                        following.buttonText = AppResources.ProfileButtonFollow + "  ";
                        following.buttonBackgorund = Application.Current.Resources["PhoneBackgroundBrush"] as SolidColorBrush;
                        following.buttonForeground = Application.Current.Resources["PhoneForegroundBrush"] as SolidColorBrush;
                        following.buttonIcon = new System.Windows.Media.Imaging.BitmapImage(new Uri("Resources/icons/appbar.add.png", UriKind.RelativeOrAbsolute));
                        following.buttonIconVisible = System.Windows.Visibility.Visible;
                    }
                }
                Followings.Add(following);
            }

            return done;
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
