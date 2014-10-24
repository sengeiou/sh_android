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
        public ObservableCollection<FollowViewModel> followings { get; private set; }

        public Factories.BagdadFactory bagdadFactory { private get;  set; }

        public FollowsViewModel(Factories.BagdadFactory _bagdadFactory)
        {
            bagdadFactory = _bagdadFactory;
            IsDataLoaded = false;
            this.followings = new ObservableCollection<FollowViewModel>();
        }

        public FollowsViewModel()
        {
            bagdadFactory = new Factories.BagdadFactory();
            IsDataLoaded = false;
            this.followings = new ObservableCollection<FollowViewModel>();
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

            UserImageManager userImageManager = bagdadFactory.CreateUserImageManager();

            BitmapImage image = userImageManager.GetUserImage(user.idUser);

            if (image == null && !String.IsNullOrEmpty(user.photo)) image = new System.Windows.Media.Imaging.BitmapImage(new Uri(user.photo, UriKind.Absolute));

            //isFollowed
            Follow follow = bagdadFactory.CreateFollow();
            List<int> myFollowings = await follow.getidUserFollowing();

            bool followed = false;

            if (myFollowings.Contains(user.idUser)) followed = true;

            //add user with button data

            if (user.idUser == App.ID_USER)
            {
                //Don't Show The Button
                followings.Add(bagdadFactory.CreateNonVisibleFollowViewModel(user, image, followed));
            }
            else
            {
                //Default Button
                followings.Add(bagdadFactory.CreateFollowViewModel(user, image, followed));
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

        public async Task<bool> AddAsFollowing(User user)
        {
            Follow follow = new Follow();
            bool retorn = await follow.AddFollowing(user);
            App.UpdateServices(ServiceCommunication.enumTypeSynchro.ST_FULL_SYNCHRO, ServiceCommunication.enumSynchroTables.FOLLOW);
            return retorn;
        }

        public async Task<bool> RemoveFromFollowing(User user)
        {
            Follow follow = new Follow();
            bool retorn = await follow.DelFollowing(user);
            App.UpdateServices(ServiceCommunication.enumTypeSynchro.ST_FULL_SYNCHRO, ServiceCommunication.enumSynchroTables.FOLLOW);
            return retorn;
        }
    }
}
