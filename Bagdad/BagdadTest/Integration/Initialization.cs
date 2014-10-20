using BagdadTest.Utils;
using Microsoft.VisualStudio.TestPlatform.UnitTestFramework;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Bagdad.Utils;

namespace BagdadTest.Integration
{
    [TestClass]
    public class Initialization
    {

        [TestMethod]
        public void CanQueryDatabase()
        {
            DataBaseHelper dataBaseHelper = new DataBaseHelper();
            Task.FromResult(dataBaseHelper.init()).Wait();
            int simpleQueryResult = dataBaseHelper.SimpleQuery().Result;
            dataBaseHelper.ReleaseDataBase();
            Assert.AreEqual(1, simpleQueryResult);
        }

        [TestMethod]
        public void ExistingTablesInDataBase()
        {
            DataBaseHelper dataBaseHelper = new DataBaseHelper();
            Task.FromResult(dataBaseHelper.init()).Wait();
            List<String> tableNames = dataBaseHelper.GetListOfTables().Result;
            dataBaseHelper.ReleaseDataBase();
            Assert.AreEqual(6, tableNames.Count);
            Assert.IsTrue(tableNames.Contains("User"));
            Assert.IsTrue(tableNames.Contains("Shot"));
            Assert.IsTrue(tableNames.Contains("Follow"));
            Assert.IsTrue(tableNames.Contains("Device"));
            Assert.IsTrue(tableNames.Contains("Synchro"));
            Assert.IsTrue(tableNames.Contains("Team"));
        }

        [TestMethod]
        public void TestUserNotLogedInAtStartUp()
        {
            DataBaseHelper dataBaseHelper = new DataBaseHelper();
            Task.FromResult(dataBaseHelper.init()).Wait();
            Task.FromResult(dataBaseHelper.ResetDataBase()).Wait();
            dataBaseHelper.ReleaseDataBase();

            Util util = new Util();

            bool isLogged = util.isUserAlreadyLoged().Result;

            Assert.AreEqual(false, isLogged);
        }

    }
}
