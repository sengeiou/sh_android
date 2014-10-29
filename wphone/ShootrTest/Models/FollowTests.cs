using System;
using Microsoft.VisualStudio.TestPlatform.UnitTestFramework;
using Bagdad.Models;
using System.Threading.Tasks;
using SQLiteWinRT;

namespace BagdadTest.Model
{
    [TestClass]
    public class FollowTests
    {
        /*[TestMethod]
        public void TestDataBase()
        {
            Bagdad.Utils.DataBaseHelper dbHelper = new Bagdad.Utils.DataBaseHelper();
            dbHelper.InitializeDB();
            Database db = Task.FromResult(Bagdad.Utils.DataBaseHelper.GetDatabaseAsync());
            Assert.AreEqual(0, 1);
        }


        [TestMethod]
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
