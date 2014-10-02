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

        public async Task<int> LoadData(int idUser)
        {
            try
            {
                if (idUser == App.ID_USER) return await LocalDataLoad(idUser);
            }
            catch (Exception e)
            {
                Debug.WriteLine("E R R O R : FollowingsViewModel - LoadData: " + e.Message);
            }
            return 0;
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
                following.isFollowed = true;
                following.buttonText = AppResources.ProfileButtonFollowing + "  ";
                following.buttonBackgorund = Application.Current.Resources["PhoneBackgroundBrush"] as SolidColorBrush;
                following.buttonForeground = Application.Current.Resources["PhoneForegroundBrush"] as SolidColorBrush;


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
