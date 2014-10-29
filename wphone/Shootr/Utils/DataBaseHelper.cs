using SQLiteWinRT;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using Windows.ApplicationModel;
using Windows.Storage;

namespace Bagdad.Utils
{
    public class DataBaseHelper
    {
        #region DATA_BASE

        public static Database db;

        public static ManualResetEvent DBLoaded = new ManualResetEvent(false);

        public async void InitializeDB()
        {            
            db = new Database(ApplicationData.Current.LocalFolder, "shooter.db");
            await db.OpenAsync();
            DBLoaded.Set();
        }

        public static Task<SQLiteWinRT.Database> GetDatabaseAsync()
        {
            return Task.Run(() =>
            {
                DBLoaded.WaitOne(-1);
                return db;
            });
        }

        internal async static Task<int> initializeDatabase()
        {
            await CopyDatabase();
            return 1;
        }

        public static async Task<int> CopyDatabase()
        {
            try
            {
                bool isDatabaseExisting = false;

                try
                {
                    StorageFile storageFile = await ApplicationData.Current.LocalFolder.GetFileAsync("shooter.db");

                    isDatabaseExisting = true;
                }
                catch
                {
                    isDatabaseExisting = false;
                }

                if (!isDatabaseExisting)
                {
                    StorageFile databaseFile = await Package.Current.InstalledLocation.GetFileAsync("shooter.db");
                    await databaseFile.CopyAsync(ApplicationData.Current.LocalFolder);
                }
                return 1;
            }
            catch (Exception e)
            {
                System.Diagnostics.Debug.WriteLine("PrepareDB - CopyDatabase: " + e.Message);
                return 0;
            }
        }
        #endregion
    }
}
