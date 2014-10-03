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
    public class FollowingsViewModel : INotifyPropertyChanged
    {
         public ObservableCollection<FollowingViewModel> Followings { get; private set; }

        public FollowingsViewModel()
        {
            IsDataLoaded = false;
            this.Followings = new ObservableCollection<FollowingViewModel>();
        }

        public bool IsDataLoaded
        {
            get;
            private set;
        }

        public async Task<int> LoadData(int idUser, int offset)
        {
            try
            {
                if (idUser == App.ID_USER) return await LocalDataLoad(idUser);
                else return  await ServerDataLoad(idUser, offset);
            }
            catch (Exception e)
            {
                throw new Exception("E R R O R : FollowingsViewModel - LoadData: " + e.Message);
            }
        }

        private async Task<int> LocalDataLoad(int idUser)
        {
            int done = 0;

            UserImageManager userImageManager = new UserImageManager();
            Follow follow = new Follow();
            foreach (FollowingViewModel following in await follow.GetUserFollowingLocalData(idUser))
            {
                //image
                following.userImage = userImageManager.GetUserImage(following.idUser);
                if (following.userImage == null) following.userImage = new System.Windows.Media.Imaging.BitmapImage(new Uri(following.userImageURL, UriKind.Absolute));

                //Default Button
                following.buttonVisible = Visibility.Visible;
                following.buttonText = AppResources.ProfileButtonFollowing + "  ";
                following.buttonBackgorund = Application.Current.Resources["PhoneBackgroundBrush"] as SolidColorBrush;
                following.buttonForeground = Application.Current.Resources["PhoneForegroundBrush"] as SolidColorBrush;


                Followings.Add(following);
            }

            return done;
        }

        private async Task<int> ServerDataLoad(int idUser, int offset)
        {
            int done = 0;

            UserImageManager userImageManager = new UserImageManager();
            Follow follow = new Follow();
            foreach (FollowingViewModel following in await follow.GetUserFollowingFromServer(idUser, offset))
            {
                //image
                following.userImage = userImageManager.GetUserImage(following.idUser);
                if (following.userImage == null) following.userImage = new System.Windows.Media.Imaging.BitmapImage(new Uri(following.userImageURL, UriKind.Absolute));

                if (following.idUser == App.ID_USER)
                {
                    //Don't Show The Button
                    following.buttonVisible = Visibility.Collapsed;
                }
                else
                {
                    //Default Button
                    if (following.isFollowed)
                    {
                        following.buttonVisible = Visibility.Visible;
                        following.buttonText = AppResources.ProfileButtonFollowing + "  ";
                        following.buttonBackgorund = Application.Current.Resources["PhoneBackgroundBrush"] as SolidColorBrush;
                        following.buttonForeground = Application.Current.Resources["PhoneForegroundBrush"] as SolidColorBrush;
                    }
                    else
                    {
                        following.buttonVisible = Visibility.Visible;
                        following.buttonText = AppResources.ProfileButtonFollow + "  ";
                        following.buttonBackgorund = Application.Current.Resources["PhoneAccentBrush"] as SolidColorBrush;
                        following.buttonForeground = new System.Windows.Media.SolidColorBrush(Colors.White);
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
