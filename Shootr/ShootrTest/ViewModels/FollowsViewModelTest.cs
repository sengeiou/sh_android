using System;
using System.Collections.Generic;
using Microsoft.VisualStudio.TestPlatform.UnitTestFramework;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Bagdad.ViewModels;
using Moq;
using Bagdad.Factories;
using Bagdad.Models;
using Bagdad.Utils;
using System.Windows.Media.Imaging;
using System.Windows;

namespace BagdadTest.ViewModels
{
    [TestClass]
    public class FollowsViewModelTest
    {
        
        [TestMethod]
        public void WhenAddUserToListIsCalledTheListOfFollowingHasOneMoreUser()
        {
            var factory = new Mock<BagdadFactory>();
            var follow = new Mock<Follow>();

            follow.Setup(f => f.getidUserFollowing()).Returns(Task.FromResult(new List<int>()));

            factory.Setup(factorySetup => factorySetup.CreateFollow()).Returns(follow.Object);

            

            var userImageFactory = new Mock<UserImageManager>();
            BitmapImage image = null;

            Deployment.Current.Dispatcher.BeginInvoke(() =>
                {
                     image = new BitmapImage();
                });

            User user = new User(){idUser = 1};
            userImageFactory.Setup(userImageFactorySetup => userImageFactorySetup.GetUserImage(user.idUser)).Returns(image);

            factory.Setup(factorySetup => factorySetup.CreateUserImageManager()).Returns(userImageFactory.Object);

            FollowViewModel followViewModel = new FollowViewModel();
            factory.Setup(factorySetup => factorySetup.CreateNonVisibleFollowViewModel(user,image,It.IsAny<bool>())).Returns(followViewModel);

            FollowsViewModel followsViewModel = new FollowsViewModel(factory.Object);

            Task.FromResult(followsViewModel.AddUserToList(user));
            

            Assert.AreEqual(1, followsViewModel.followings.Count);

        }
    }
}
