using Bagdad.Factories;
using Bagdad.Models;
using Bagdad.Utils;
using Microsoft.VisualStudio.TestPlatform.UnitTestFramework;
using Moq;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Media.Imaging;

namespace BagdadTest.Models
{
    [TestClass]
    public class UserTest
    {
        [TestMethod]
        public void TestParseUser()
        {
            JObject json = new JObject();
            json = JObject.Parse("{\"alias\":null,\"status\":{\"code\":\"OK\",\"message\":\"Las operaciones se han realizado correctamente.\"},\"req\":[0,0,2,1000000,1413878987417],\"ops\":[{\"metadata\":{\"operation\":\"Retrieve\",\"entity\":\"Login\",\"includeDeleted\":null,\"totalItems\":1,\"offset\":null,\"items\":1,\"key\":{\"password\":\"d93a5def7511da3d0f2d\",\"userName\":\"teo\"},\"filter\":null},\"data\":[{\"numFollowers\":8,\"birth\":1413556061000,\"website\":\"https://www.outlook.com\",\"photo\":\"http://s3-eu-west-1.amazonaws.com/bagdag/mordisquitos.jpg\",\"deleted\":null,\"modified\":1413556061000,\"sessionToken\":\"c81e728d9d4c2f636f067f89cc14862c\",\"revision\":0,\"favoriteTeamName\":\"Arsenal\",\"bio\":\"I'm a shooter lover and I also hate pineapples\",\"email\":\"teo.rodriguez@fav24.com\",\"name\":\"Teófilo\",\"userName\":\"Teo\",\"idUser\":2,\"numFollowings\":7,\"points\":0,\"idFavoriteTeam\":96775}]}]}");
            var userImageFactory = new Mock<UserImageManager>();


            BitmapImage image = null;

            Deployment.Current.Dispatcher.BeginInvoke(() =>
            {
                image = new BitmapImage();
            });
            
            User user = new User();

            List<BaseModelJsonConstructor> listLogin = user.ParseJson(json);

            Assert.AreEqual(1, listLogin.Count);
            user = (User)listLogin[0];

            Assert.AreEqual(8, user.numFollowers);
            Assert.AreEqual(1413556061000, user.csys_birth);
            Assert.AreEqual("https://www.outlook.com", user.website);
            Assert.AreEqual("http://s3-eu-west-1.amazonaws.com/bagdag/mordisquitos.jpg", user.photo);
            Assert.AreEqual(0, user.csys_deleted); //A null INT it's 0
            Assert.AreEqual(1413556061000, user.csys_modified);            
            Assert.AreEqual(0, user.csys_revision);
            Assert.AreEqual("Arsenal", user.favoriteTeamName);
            Assert.AreEqual("I'm a shooter lover and I also hate pineapples", user.bio);
            Assert.AreEqual("Teófilo", user.name);
            Assert.AreEqual("Teo", user.userName);
            Assert.AreEqual(2, user.idUser);
            Assert.AreEqual(7, user.numFollowing);
            Assert.AreEqual(0, user.points);
            Assert.AreEqual(96775, user.idFavoriteTeam);
        }

        [TestMethod]
        public void TestFindFriendsInServer()
        {
            User user = new User();
            List<User> listOfFindedUsers = user.FindUsersInServer("a", 0).Result;
            Assert.AreNotEqual(0, listOfFindedUsers.Count);
            foreach (User userInfo in listOfFindedUsers)
                Assert.AreNotEqual(0, userInfo.idUser);
        }
    }
}
