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
            DataBaseHelperTest dbTestHelper = new DataBaseHelperTest();

            dataBaseHelper.InitializeDB();
            DataBaseHelper.DBLoaded.Set();

            Util util = new Util();

            bool isLogged = util.isUserAlreadyLoged().Result;

            Assert.AreEqual(false, isLogged);
        }

    }
}
