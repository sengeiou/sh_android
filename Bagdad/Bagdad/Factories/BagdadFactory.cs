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

        public virtual UserViewModel CreateUserViewModel()
        {
            return new UserViewModel();
        }

        public virtual User CreateUser()
        {
            return new User();
        }

        public virtual User CreateFilledUserWithOutDeleteAndSynchronizedInfo(int _idUser, String _userName, String _name, String _photo, int _numFollowers, int _numFollowing, int _points, String _bio, String _website, String _favoriteTeamName, int _idFavoriteTeam, int _csys_revision, double _csys_birth, double _csys_modified)
        {
            return new User() 
            { 
                idUser = _idUser, 
                userName = _userName, 
                name = _name, 
                photo = _photo, 
                numFollowers = _numFollowers, 
                numFollowing = _numFollowing, 
                points = _points, 
                bio = _bio, 
                website = _website, 
                favoriteTeamName = _favoriteTeamName, 
                idFavoriteTeam = _idFavoriteTeam, 
                csys_revision = _csys_revision, 
                csys_birth = _csys_birth, 
                csys_modified = _csys_modified 
            };
        }

        public virtual User CreateFilledUserWithDeleteAndSynchronizedInfo(int _idUser, String _userName, String _name, String _photo, int _numFollowers, int _numFollowing, int _points, String _bio, String _website, String _favoriteTeamName, int _idFavoriteTeam, double _csys_birth, double _csys_modified, Double _csys_deleted, int _csys_revision, Char _csys_synchronized)
        {
            return new User()
            {
                idUser = _idUser,
                userName = _userName,
                name = _name,
                photo = _photo,
                numFollowers = _numFollowers,
                numFollowing = _numFollowing,
                points = _points,
                bio = _bio,
                website = _website,
                favoriteTeamName = _favoriteTeamName,
                idFavoriteTeam = _idFavoriteTeam,
                csys_birth = _csys_birth,
                csys_modified = _csys_modified,
                csys_deleted = _csys_deleted, 
                csys_revision = _csys_revision, 
                csys_synchronized = _csys_synchronized 
            };
        }

        public virtual Login CreateLogin()
        {
            return new Login();
        }

        public virtual Login CreateFullFilledLogin(int _idUser, String _sessionToken, String _email, int _idFavoriteTeamName, String _favoriteTeamName, String _userName, String _name, String _photo, String _bio, String _website, int _points, int _numFollowing, int _numFollowers, Double _csys_birth, Double _csys_modified, Double _csys_deleted, int _csys_revision, Char _csys_synchronized)
        {
            Login login = new Login() 
            {
                idUser = _idUser, 
                sessionToken = _sessionToken, 
                email = _email, 
                idFavoriteTeam = _idFavoriteTeamName, 
                favoriteTeamName = _favoriteTeamName, 
                userName = _userName, 
                name = _name, 
                photo = _photo, 
                bio = _bio, 
                website = _website, 
                points = _points, 
                numFollowing = _numFollowing, 
                numFollowers = _numFollowers, 
                csys_birth = _csys_birth, 
                csys_modified = _csys_modified, 
                csys_deleted = _csys_deleted, 
                csys_revision = _csys_revision, 
                csys_synchronized = _csys_synchronized 
            };
            return login;
        }

        public virtual ServiceCommunication CreateServiceCommunication()
        {
            return new ServiceCommunication();
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
                buttonIcon = (_followed) ? new System.Windows.Media.Imaging.BitmapImage(new Uri("Resources/icons/appbar.user.added.png", UriKind.RelativeOrAbsolute)) : new System.Windows.Media.Imaging.BitmapImage(new Uri("Resources/icons/appbar.user.add.png", UriKind.RelativeOrAbsolute)),
                buttonIconVisible = System.Windows.Visibility.Visible
            };
            return followVM;
        }

        public virtual Follow CreateFollow()
        {
            return new Follow();
        }

        public virtual Follow CreateFollowParseJson(int _idUser, int _idUserFollowed, Double _csys_birth, Double _csys_modified, Double _csys_deleted, int _csys_revision, Char _csys_synchronized)
        {
            return new Follow()
            {
                idUser = _idUser,
                idUserFollowed = _idUserFollowed,
                csys_birth = _csys_birth,
                csys_modified = _csys_modified,
                csys_deleted = _csys_deleted,
                csys_revision = _csys_revision,
                csys_synchronized = _csys_synchronized
            };
        }

        public virtual Follow CreateFollowToUpdate(int _idUser, int _idUserFollowed, Double _csys_birth, Double _csys_modified, Double _csys_deleted, Char _csys_synchronized, int _csys_revision)
        {
            return CreateFollowParseJson(
                _idUser,
                _idUserFollowed,
                _csys_birth,
                _csys_modified,
                _csys_deleted,
                _csys_revision,
                _csys_synchronized
            );
        }

        public virtual User CreateFollowingUserBasicInfo(int _idUser, String _userName, String _name, String _photo, String _favoriteTeamName)
        {
            return new User() { idUser = _idUser, userName = _userName, name = _name, photo = _photo, favoriteTeamName = _favoriteTeamName };
        }

        public virtual Shot CreateShot()
        {
            return new Shot();
        }

        public virtual Shot CreateShotForTimeLineOtherShots(int _idShot, int _idUser, String _comment, Double _csys_birth)
        {
            return new Shot() { idShot = _idShot, idUser = _idUser, comment = _comment, csys_birth = _csys_birth };
        }

        public virtual Shot CreateShotForParseJson(int _idShot, int _idUser, String _comment, Double _csys_birth, Double _csys_modified, Double _csys_deleted, int _csys_revision, Char _csys_synchronized)
        {
            return new Shot(){
                idShot = _idShot,
                idUser = _idUser,
                comment = _comment,
                csys_birth = _csys_birth,
                csys_modified = _csys_modified,
                csys_deleted = _csys_deleted,
                csys_revision = _csys_revision,
                csys_synchronized = _csys_synchronized
            };
        }

        public virtual ShotViewModel CreateShotViewModel(int _shotId, String _shotMessage, String _shotTime, int _shotUserId, String _shotUserImageURL, String _shotUserName)
        {
            return new ShotViewModel() { shotId = _shotId, shotMessage = _shotMessage, shotTime = _shotTime, shotUserId = _shotUserId, shotUserImageURL = _shotUserImageURL, shotUserName = _shotUserName };
        }

        public virtual SynchroTableInfo CreateGenericModelGetSynchronizationTables(int _order, String _entity, int _frequency, DateTime _maxTimestamp, DateTime _minTimestamp, String _direction, int _maxRows, int _minRows)
        {
            return new SynchroTableInfo() { Order = _order, Entity = _entity, Frequency = _frequency, MaxTimestamp = _maxTimestamp, MinTimestamp = _minTimestamp, Direction = _direction, MaxRows = _maxRows, MinRows = _minRows };
        }
    }

}
