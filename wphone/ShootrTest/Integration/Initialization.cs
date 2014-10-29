using BagdadTest.Models;
using Microsoft.VisualStudio.TestPlatform.UnitTestFramework;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Bagdad.Utils;
using BagdadTest.Utils;
using Bagdad.Models;

namespace BagdadTest.Integration
{
    [TestClass]
    public class Initialization
    {

        [TestMethod]
        public void CanQueryDatabase()
        {
            DataBaseHelperTest dbTestHelper = new DataBaseHelperTest();
            
            int simpleQueryResult = dbTestHelper.SimpleQuery().Result;
            DataBaseHelper.DBLoaded.Set();

            Assert.AreEqual(1, simpleQueryResult);
        }

        [TestMethod]
        public void ExistingTablesInDataBase()
        {
            DataBaseHelperTest dbTestHelper = new DataBaseHelperTest();
         
            List<String> tableNames = dbTestHelper.GetListOfTables().Result;

            Assert.AreEqual(8, tableNames.Count);
            Assert.IsTrue(tableNames.Contains("User"));
            Assert.IsTrue(tableNames.Contains("Shot"));
            Assert.IsTrue(tableNames.Contains("Follow"));
            Assert.IsTrue(tableNames.Contains("Device"));
            Assert.IsTrue(tableNames.Contains("Synchro"));
            Assert.IsTrue(tableNames.Contains("Team"));
            Assert.IsTrue(tableNames.Contains("Watch"));
            Assert.IsTrue(tableNames.Contains("Matches"));
        }

        [TestMethod]
        public void TestUserNotLogedInAtStartUp()
        {
            DataBaseHelper dataBaseHelper = new DataBaseHelper();
            DataBaseHelperTest dbTestHelper = new DataBaseHelperTest();

            dataBaseHelper.InitializeDB();
            DataBaseHelper.DBLoaded.Set();

            Login login = new Login();

            bool isLogged = login.isUserAlreadyLoged().Result;

            Assert.AreEqual(false, isLogged);
        }

        [TestMethod]
        public void TestUserLogedInAtStartUp()
        {
            DataBaseHelperTest dbTestHelper = new DataBaseHelperTest();

            int simpleQueryResult = dbTestHelper.InsertLogin().Result;

            Login login = new Login();

            bool isLogged = login.isUserAlreadyLoged().Result;

            Assert.AreEqual(true, isLogged);
        }

    }
}
