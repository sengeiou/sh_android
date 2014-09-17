using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.ApplicationModel;
using Windows.Storage;

namespace Bagdad.Utils
{
    class PrepareDB
    {
        internal async static Task<int> initializeDatabase()
        {
            await CopyDatabase();
            return 1;
        }

        private static async Task<int> CopyDatabase()
        {
            try
            {
                bool isDatabaseExisting = false;

                try
                {
                    StorageFile storageFile = await ApplicationData.Current.LocalFolder.GetFileAsync("shooter.db");

                    isDatabaseExisting = true;
                }
                catch (Exception ef)
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
    }
}
