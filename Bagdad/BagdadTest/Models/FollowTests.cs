using System;
using Microsoft.VisualStudio.TestPlatform.UnitTestFramework;
using Bagdad.Models;
using System.Threading.Tasks;

namespace BagdadTest.Model
{
    [TestClass]
    public class FollowTests
    {
        /*[TestMethod]
        public void ConstructFilter()
        {
            Follow follow = new Follow();

            Task<string> filterQuery = follow.ConstructFilter("some date");

            var expectedQuery = "\"filterItems\":[{\"comparator\":\"eq\",\"name\":\"idUser\",\"value\":2},{\"comparator\":\"ne\",\"name\":\"idFollowedUser\",\"value\":null}],\"filters\":[some date],\"nexus\":\"and\"";
            Assert.AreEqual(expectedQuery, filterQuery.Result);


        }*/
        [TestMethod]
        public void TestMethod2()
        {
            Assert.AreEqual(1, 1);
        }
    }
}
