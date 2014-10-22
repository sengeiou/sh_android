using Bagdad.Utils;
using Microsoft.VisualStudio.TestPlatform.UnitTestFramework;
using Moq;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Media.Imaging;
using Bagdad.Models;

namespace BagdadTest.Models
{
    [TestClass]
    public class ShotTest
    {
        [TestMethod]
        public void TestParseShot()
        {
            JObject json = new JObject();
            json = JObject.Parse("{\"alias\":null,\"status\":{\"code\":\"OK\",\"message\":\"Las operaciones se han realizado correctamente.\"},\"req\":[163,2,2,1000000,1413964189179],\"ops\":[{\"metadata\":{\"operation\":\"Retrieve\",\"entity\":\"Shot\",\"includeDeleted\":null,\"totalItems\":24,\"offset\":null,\"items\":24,\"key\":null,\"filter\":{\"nexus\":\"and\",\"filterItems\":[],\"filters\":[{\"nexus\":\"or\",\"filterItems\":[{\"comparator\":\"gt\",\"name\":\"modified\",\"value\":1413917953400},{\"comparator\":\"gt\",\"name\":\"deleted\",\"value\":1413917953400}],\"filters\":[]},{\"nexus\":\"or\",\"filterItems\":[{\"comparator\":\"eq\",\"name\":\"idUser\",\"value\":8}],\"filters\":[]}]}},\"data\":[{\"revision\":0,\"birth\":1413962922000,\"idUser\":8,\"idShot\":3571,\"comment\":\"Shot\",\"deleted\":null,\"modified\":1413962922000}]}]}");
            var userImageFactory = new Mock<UserImageManager>();

            Shot shot = new Shot();

            List<BaseModelJsonConstructor> listLogin = shot.ParseJson(json);

            Assert.AreEqual(1, listLogin.Count);
            shot = (Shot)listLogin[0];

            Assert.AreEqual(0, shot.csys_revision);
            Assert.AreEqual(1413962922000, shot.csys_birth);
            Assert.AreEqual(8, shot.idUser);
            Assert.AreEqual(3571, shot.idShot);
            Assert.AreEqual("Shot", shot.comment);
            Assert.AreEqual(1413962922000, shot.csys_modified);
            Assert.AreEqual(0, shot.csys_deleted);
        }
    }
}
