using Bagdad.Utils;
using SQLiteWinRT;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.Storage;

namespace BagdadTest.Utils
{
    class DataBaseHelperTest
    {
        public Database database;


        ////////public async void InitializeDB()
        ////////{
        ////////    //database = new Database(await StorageFolder.GetFolderFromPathAsync("C:\\Data\\Users\\DefApps\\AppData\\{BBF2A5F0-FEA1-4D3F-B9A3-B12397BEE02F}\\local"), "shooter.db");
        ////////    database = new Database(ApplicationData.Current.LocalFolder, "shooter.db");
        ////////    await database.OpenAsync();

        ////////}
        public async Task ResetDataBase()
        {
            await TruncateDeviceTable();
            await TruncateShotTable();
            await TruncateFollowTable();
            await TruncateUserTable();
            await TruncateTeamTable();

        }

        public async Task TruncateDeviceTable()
        {
            await DeleteContentFromTable("Device");

        }

        public async Task TruncateShotTable()
        {
            await DeleteContentFromTable("Shot");

        }

        public async Task TruncateFollowTable()
        {
            await DeleteContentFromTable("Follow");

        }

        public async Task TruncateUserTable()
        {
            await DeleteContentFromTable("User");

        }

        public async Task TruncateTeamTable()
        {
            await DeleteContentFromTable("Team");

        }
        private async Task DeleteContentFromTable(String table)
        {
            DataBaseHelper dataBaseHelper = new DataBaseHelper();
            dataBaseHelper.InitializeDB();
            database = await DataBaseHelper.GetDatabaseAsync();

            Statement statement = await database.PrepareStatementAsync("DELETE FROM " + table);

            await statement.StepAsync();
        }

        public async Task<List<String>> GetListOfTables(){
            //////DataBaseHelper dataBaseHelper = new DataBaseHelper();
            //////await DataBaseHelper.CopyDatabase(); 
            //////dataBaseHelper.InitializeDB();
            //////DataBaseHelper.DBLoaded.Set();
            database = await DataBaseHelper.GetDatabaseAsync();

            Statement statement = await database.PrepareStatementAsync("SELECT DISTINCT tbl_name FROM sqlite_master");
            List<String> tableNames = new List<String>();
            while (await statement.StepAsync())
            {
                tableNames.Add(statement.GetTextAt(0));
            }
            return tableNames;
        }

        public async Task<int> SimpleQuery()
        {
            DataBaseHelper dataBaseHelper = new DataBaseHelper();
            dataBaseHelper.InitializeDB();
            database = await DataBaseHelper.GetDatabaseAsync();

            Statement statement = await database.PrepareStatementAsync("SELECT 1");
            if (await statement.StepAsync())
            {
                return statement.GetIntAt(0);
            }
            else return -1;
        }

    }
}
