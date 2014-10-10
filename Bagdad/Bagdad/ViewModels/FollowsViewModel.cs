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
            
            Follow follow = new Follow();

            foreach (User following in await follow.GetUserFollowingLocalData(idUser, type))
            {
                done += await AddUserToList(following);
            }

            return done;
        }

        private async Task<int> ServerDataLoad(int idUser, int offset, String type)
        {
            int done = 0;

            Follow follow = new Follow();

            List<User> follows;

            if (type.Equals(Constants.CONST_FOLLOWING))
                follows = await follow.GetUserFollowingFromServer(idUser, offset);
            else
                follows = await follow.GetUserFollowersFromServer(idUser, offset);

            foreach (User following in follows)
            {
                done += await AddUserToList(following);
            }

            return done;
        }

        public async Task<int> AddUserToList(User user)
        {
            //image
            UserImageManager userImageManager = new UserImageManager();

            BitmapImage image = userImageManager.GetUserImage(user.idUser);

            if (image == null && !String.IsNullOrEmpty(user.photo)) image = new System.Windows.Media.Imaging.BitmapImage(new Uri(user.photo, UriKind.Absolute));

            //isFollowed
            Follow follow = new Follow();
            List<int> myFollowings = await follow.getidUserFollowing();

            bool followed = false;

            if (myFollowings.Contains(user.idUser)) followed = true;

            //add user with button data

            if (user.idUser == App.ID_USER)
            {
                //Don't Show The Button
                Followings.Add(new FollowViewModel()
                {
                    userInfo = user,
                    userImage = image,
                    isFollowed = followed,
                    buttonVisible = Visibility.Collapsed
                });
            }
            else
            {
                //Default Button
                if (followed)
                {
                    Followings.Add(new FollowViewModel()
                    {
                        userInfo = user,
                        userImage = image,
                        isFollowed = followed,
                        buttonVisible = Visibility.Visible,
                        buttonText = AppResources.ProfileButtonFollowing + "  ",
                        buttonBackgorund = Application.Current.Resources["PhoneAccentBrush"] as SolidColorBrush,
                        buttonForeground = new System.Windows.Media.SolidColorBrush(Colors.White),
                        buttonBorderColor = Application.Current.Resources["PhoneAccentBrush"] as SolidColorBrush,
                        buttonIcon = new System.Windows.Media.Imaging.BitmapImage(new Uri("Resources/icons/appbar.user.added.png", UriKind.RelativeOrAbsolute)),
                        buttonIconVisible = System.Windows.Visibility.Visible
                    });

                }
                else
                {
                    Followings.Add(new FollowViewModel()
                    {
                        userInfo = user,
                        userImage = image,
                        isFollowed = followed,
                        buttonVisible = Visibility.Visible,
                        buttonText = AppResources.ProfileButtonFollow + "  ",
                        buttonBackgorund = Application.Current.Resources["PhoneBackgroundBrush"] as SolidColorBrush,
                        buttonForeground = Application.Current.Resources["PhoneDisabledBrush"] as SolidColorBrush,
                        buttonBorderColor = Application.Current.Resources["PhoneDisabledBrush"] as SolidColorBrush,
                        buttonIcon = new System.Windows.Media.Imaging.BitmapImage(new Uri("Resources/icons/appbar.user.add.png", UriKind.RelativeOrAbsolute)),
                        buttonIconVisible = System.Windows.Visibility.Visible
                    });
                   
                }
            }

            return 1;
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
