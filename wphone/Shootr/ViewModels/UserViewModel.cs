using Bagdad.Models;
using Bagdad.Resources;
using Bagdad.Utils;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Media;
using System.Windows.Media.Imaging;

namespace Bagdad.ViewModels
{
    public class UserViewModel
    {
        public int idUser { get; set; }
        public int idFavoriteTeam { get; set; }
        public int points { get; set; }
        public int following { get; set; }
        public int followers { get; set; }
        public String userNickName { get; set; }
        public String userName { get; set; }
        public String userURLImage { get; set; }
        public String userBio { get; set; }
        public String userWebsite { get; set; }
        public String favoriteTeamName { get; set; }
        public bool isFollowed { get; set; }
        public BitmapImage userImage { get; set; }
        public int revision { get; set; }
        public Double birth { get; set; }
        public Double modified { get; set; }
        public Factories.BagdadFactory bagdadFactory { private get; set; }

        public UserViewModel(Factories.BagdadFactory _bagdadFactory)
        {
            bagdadFactory = _bagdadFactory; 
        }
        public UserViewModel()
        {
            bagdadFactory = new Factories.BagdadFactory();
        }

        public async Task<bool> GetUserProfileInfo(int idUser)
        {
            try
            {
                Follow follow = bagdadFactory.CreateFollow();
                User user = bagdadFactory.CreateUser();
                UserViewModel uvm = null;

                if (await follow.ImFollowing(idUser) || idUser == App.ID_USER)
                {
                    uvm = await user.GetProfileInfo(idUser);
                }

                if (uvm == null || uvm.idUser == 0)
                {
                    if (App.isInternetAvailable) uvm = await user.GetProfilInfoFromServer(idUser);
                }
                if (uvm != null)
                {
                    this.idUser = uvm.idUser;
                    this.points = uvm.points;
                    this.followers = uvm.followers;
                    this.following = uvm.following;
                    this.userNickName = uvm.userNickName;
                    this.userName = uvm.userName;
                    this.userURLImage = uvm.userURLImage;
                    this.userBio = uvm.userBio;
                    this.userWebsite = uvm.userWebsite;
                    this.favoriteTeamName = uvm.favoriteTeamName;
                    this.idFavoriteTeam = uvm.idFavoriteTeam;
                    this.birth = uvm.birth;
                    this.modified = uvm.modified;
                    this.revision = uvm.revision;

                    this.isFollowed = await uvm.ImFollowing();

                    UserImageManager userImageManager = bagdadFactory.CreateUserImageManager();

                    BitmapImage image = userImageManager.GetUserImage(uvm.idUser);

                    if (image == null && !String.IsNullOrEmpty(uvm.userURLImage)) image = new System.Windows.Media.Imaging.BitmapImage(new Uri(uvm.userURLImage, UriKind.Absolute));
                    
                    this.userImage = image;
                }
            }
            catch (Exception e)
            {
                Debug.WriteLine("E R R O R - UserViewModel - GetUserProfileInfo: " + e.Message);
                return false;
            }
            return true;
        }

        public async Task<bool> ImFollowing()
        {
            try
            {
                Follow follow = bagdadFactory.CreateFollow();
                return await follow.ImFollowing(idUser);
            }
            catch (Exception e)
            {
                Debug.WriteLine("E R R O R - UserViewModel - GetUserProfileInfo: " + e.Message);
                return false;
            }
        }

        public async Task<FollowsViewModel> FindUsersInServer(String searchString, int offset)
        {
            User users = bagdadFactory.CreateUser();
            
            List<User> findUsers = await users.FindUsersInServer(searchString, offset);

            FollowsViewModel findedUsers = new FollowsViewModel();

            foreach (User user in findUsers)
            {
                await findedUsers.AddUserToList(user);
            }

            return findedUsers;
        }

        public async Task<FollowsViewModel> FindUsersInLocal(String searchString)
        {
            FollowsViewModel findedUsers = new FollowsViewModel();
            try
            {
                User users = bagdadFactory.CreateUser();

                List<User> findUsers = await users.FindUsersInDB(searchString);
                foreach (User user in findUsers)
                {
                    await findedUsers.AddUserToList(user);
                }
            }
            catch (Exception e)
            {
                Debug.WriteLine("E R R O R - UserViewModel - FindUsersInLocal: " + e.Message);
            }
            return findedUsers;
        }

        public async Task<bool> AddAsFollowing()
        {
            Follow follow = bagdadFactory.CreateFollow();
            return await follow.AddFollowing(AsUser());
        }

        public async Task<bool> RemoveFromFollowing()
        {
            Follow follow = bagdadFactory.CreateFollow();
            return await follow.DelFollowing(AsUser());
        }

        public User AsUser()
        {
            return bagdadFactory.CreateFilledUserWithOutDeleteAndSynchronizedInfo(this.idUser, this.userNickName, this.userName, this.userURLImage, this.followers, this.following, this.points, this.userBio, this.userWebsite, this.favoriteTeamName, this.idFavoriteTeam, this.revision, this.birth, this.modified);
        }
    }
}
