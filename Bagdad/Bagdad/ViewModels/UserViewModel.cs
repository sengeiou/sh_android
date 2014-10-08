﻿using Bagdad.Models;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Media.Imaging;

namespace Bagdad.ViewModels
{
    public class UserViewModel
    {
        public int idUser { get; set; }
        public int points { get; set; }
        public int following { get; set; }
        public int followers { get; set; }
        public String userNickName { get; set; }
        public String userName { get; set; }
        public String userURLImage { get; set; }
        public String userBio { get; set; }
        public String userWebsite { get; set; }
        public bool isFollowed { get; set; }
        public BitmapImage userImage { get; set; }
        
        public UserViewModel() { }

        public async Task<bool> GetUserProfileInfo(int idUser)
        {
            try
            {

                Follow follow = new Follow();
                User user = new User();
                UserViewModel uvm;

                if (await follow.ImFollowing(idUser) || idUser == App.ID_USER)
                { 
                    uvm = await user.GetProfileInfo(idUser);
                }
                else
                {
                    uvm = await user.GetProfilInfoFromServer(idUser);
                }

                this.idUser = uvm.idUser;
                this.points = uvm.points;
                this.followers = uvm.followers;
                this.following = uvm.following;
                this.userNickName = uvm.userNickName;
                this.userName = uvm.userName;
                this.userURLImage = uvm.userURLImage;
                this.userBio = uvm.userBio;
                this.userWebsite = uvm.userWebsite;

                this.isFollowed = await uvm.ImFollowing();
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
                Follow follow = new Follow();
                return await follow.ImFollowing(idUser);
            }
            catch (Exception e)
            {
                Debug.WriteLine("E R R O R - UserViewModel - GetUserProfileInfo: " + e.Message);
                return false;
            }
            return true;
        }
    }
}
