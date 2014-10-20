using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Bagdad.ViewModels;
using Bagdad.Utils;
using Bagdad.Models;
using System.Windows.Media.Imaging;
using Bagdad.Resources;
using System.Windows;
using System.Windows.Media;

namespace Bagdad.Factories
{
    public class BagdadFactory
    {


        public virtual UserImageManager CreateUserImageManager()
        {
            return new UserImageManager();
        }

        public virtual FollowViewModel CreateFollowViewModel()
        {
            return new FollowViewModel();
        }

        public virtual FollowViewModel CreateNonVisibleFollowViewModel(User _user, BitmapImage _image, bool _followed)
        {
            FollowViewModel followVM = new FollowViewModel()
            {
                userInfo = _user,
                userImage = _image,
                isFollowed = _followed,
                buttonVisible = System.Windows.Visibility.Collapsed
            };
            return followVM;
        }

        public virtual FollowViewModel CreateFollowViewModel(User _user, BitmapImage _image, bool _followed)
        {
            FollowViewModel followVM = new FollowViewModel()
            {
                userInfo = _user,
                userImage = _image,
                isFollowed = _followed,
                buttonVisible = Visibility.Visible,
                buttonText = (_followed) ? AppResources.ProfileButtonFollowing + "  " : AppResources.ProfileButtonFollow + "  ",
                buttonBackgorund = (_followed) ? Application.Current.Resources["PhoneAccentBrush"] as SolidColorBrush : Application.Current.Resources["PhoneBackgroundBrush"] as SolidColorBrush,
                buttonForeground = (_followed) ? new System.Windows.Media.SolidColorBrush(Colors.White) : Application.Current.Resources["PhoneDisabledBrush"] as SolidColorBrush,
                buttonBorderColor = (_followed) ? Application.Current.Resources["PhoneAccentBrush"] as SolidColorBrush : Application.Current.Resources["PhoneDisabledBrush"] as SolidColorBrush,
                buttonIcon = (_followed) ? new System.Windows.Media.Imaging.BitmapImage(new Uri("Resources/icons/appbar.user.add.png", UriKind.RelativeOrAbsolute)) : new System.Windows.Media.Imaging.BitmapImage(new Uri("Resources/icons/appbar.user.add.png", UriKind.RelativeOrAbsolute)),
                buttonIconVisible = System.Windows.Visibility.Visible
            };
            return followVM;
        }

        public virtual Follow CreateFollow()
        {
            return new Follow();
        }
    }

}
