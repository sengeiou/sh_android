using SQLiteWinRT;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BagdadTest.Utils
{
    class DataBaseHelper
    {

        Database database;

        public async Task init()
        {
            database = await Bagdad.App.GetDatabaseAsync();   
        }

        public void ReleaseDataBase()
        {
            Bagdad.App.DBLoaded.Set();
        }

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
            Statement statement = await database.PrepareStatementAsync("DELETE FROM " + table);

            await statement.StepAsync();
        }

        public async Task<List<String>> GetListOfTables(){

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

            Statement statement = await database.PrepareStatementAsync("SELECT 1");
            if (await statement.StepAsync())
            {
                return statement.GetIntAt(0);
            }
            else return -1;
        }

    }
}
