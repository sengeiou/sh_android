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

        public async Task<List<String>> GetListOfTables()
        {
            
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

        public async Task<int> InsertLogin()
        {
            DataBaseHelper dataBaseHelper = new DataBaseHelper();
            dataBaseHelper.InitializeDB();
            database = await DataBaseHelper.GetDatabaseAsync();

            String num = Util.FromUnixTime(1414500379574.ToString()).ToString("s").Replace('T', ' ');
            Statement statement = await database.PrepareStatementAsync("INSERT INTO User (idUser, idFavoriteTeam, favoriteTeamName, sessionToken, userName, email, name, photo, bio, website, points, numFollowings, numFollowers, csys_birth, csys_modified, csys_revision, csys_deleted, csys_synchronized) VALUES (1, 0, 'FCB', '125698awe', 'WPUsername', 'microsoft@fav24.com', 'WPname', null, '', '', 0, 1, 1, @num, @num, 1, null, 'S')");
            statement.BindTextParameterWithName("@num", num);
            await statement.StepAsync();
            
            return 1;
        }


    }
}
