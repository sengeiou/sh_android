using System;
using System.Collections.Generic;
using Microsoft.VisualStudio.TestPlatform.UnitTestFramework;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Moq;
using Newtonsoft.Json.Linq;
using Bagdad.Models;
using Bagdad.Factories;
using Bagdad.Utils;
using System.Windows.Media.Imaging;
using System.Windows;

namespace BagdadTest.Models
{
    [TestClass]
    public class LoginTest
    {
        [TestMethod]
        public void TestParserLogin()
        {
            JObject json = new JObject();
            json = JObject.Parse("{\"alias\":null,\"status\":{\"code\":\"OK\",\"message\":\"Las operaciones se han realizado correctamente.\"},\"req\":[0,0,2,1000000,1413878987417],\"ops\":[{\"metadata\":{\"operation\":\"Retrieve\",\"entity\":\"Login\",\"includeDeleted\":null,\"totalItems\":1,\"offset\":null,\"items\":1,\"key\":{\"password\":\"d93a5def7511da3d0f2d\",\"userName\":\"teo\"},\"filter\":null},\"data\":[{\"numFollowers\":8,\"birth\":1413556061000,\"website\":\"https://www.outlook.com\",\"photo\":\"http://s3-eu-west-1.amazonaws.com/bagdag/mordisquitos.jpg\",\"deleted\":null,\"modified\":1413556061000,\"sessionToken\":\"c81e728d9d4c2f636f067f89cc14862c\",\"revision\":0,\"favoriteTeamName\":\"Arsenal\",\"bio\":\"I'm a shooter lover and I also hate pineapples\",\"email\":\"teo.rodriguez@fav24.com\",\"name\":\"Teófilo\",\"userName\":\"Teo\",\"idUser\":2,\"numFollowings\":7,\"points\":0,\"idFavoriteTeam\":96775}]}]}");
            var factory = new Mock<BagdadFactory>();
            var userImageFactory = new Mock<UserImageManager>();


            BitmapImage image = null;

            Deployment.Current.Dispatcher.BeginInvoke(() =>
            {
                image = new BitmapImage();
            });

            factory.Setup(factorySetup => factorySetup.CreateUserImageManager()).Returns(userImageFactory.Object);

            Login login = new Login(factory.Object);
            
            List<BaseModelJsonConstructor>  listLogin = login.ParseJson(json);

            Assert.AreEqual(1, listLogin.Count);
            login = (Login)listLogin[0];

            Assert.AreEqual(8, login.numFollowers);
            Assert.AreEqual(1413556061000, login.csys_birth);
            Assert.AreEqual("https://www.outlook.com", login.website);
            Assert.AreEqual("http://s3-eu-west-1.amazonaws.com/bagdag/mordisquitos.jpg", login.photo);
            Assert.AreEqual(0, login.csys_deleted); //A null INT it's 0
            Assert.AreEqual(1413556061000, login.csys_modified);
            Assert.AreEqual("c81e728d9d4c2f636f067f89cc14862c", login.sessionToken);
            Assert.AreEqual(0, login.csys_revision);
            Assert.AreEqual("Arsenal", login.favoriteTeamName);
            Assert.AreEqual("I'm a shooter lover and I also hate pineapples", login.bio);
            Assert.AreEqual("teo.rodriguez@fav24.com", login.email);
            Assert.AreEqual("Teófilo", login.name);
            Assert.AreEqual("Teo", login.userName);
            Assert.AreEqual(2, login.idUser);
            Assert.AreEqual(7, login.numFollowing);
            Assert.AreEqual(0, login.points);
            Assert.AreEqual(96775, login.idFavoriteTeam);
        }
    }
}
