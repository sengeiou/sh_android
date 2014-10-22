using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using SQLiteWinRT;
using Windows.Storage;
using System.Globalization;
using Bagdad.Utils;

namespace Bagdad.Models
{
    class GenericModel
    {
        public async Task<double> getMaxModificationDateOf(String entity)
        {
            try
            {

                Database database = await DataBaseHelper.GetDatabaseAsync();
                double maxDate = 0;

                string selectQuery = SQLQuerys.getMaxModificationDateOf;

                Statement selectStatement = await database.PrepareStatementAsync(selectQuery);

                selectStatement.BindTextParameterWithName("@Entity", entity);

                if (await selectStatement.StepAsync())
                {
                    maxDate = selectStatement.GetDoubleAt(0);
                    System.Diagnostics.Debug.WriteLine("GenericModel - getMaxModificationDateOf. Fecha recuperada para la tabla " + entity + ": " + maxDate.ToString());
                }

                DataBaseHelper.DBLoaded.Set();

                return maxDate;
            }
            catch (Exception e)
            {
                throw new Exception("GenericModel - getMaxModificationDateOf" + entity + ": " + e.Message, e);
            }
        }

        public async Task<int> updateModificationDateOf(double modificationTime, String entity)
        {
            try
            {
                Database database = await DataBaseHelper.GetDatabaseAsync();

                Statement selectStatement = await database.PrepareStatementAsync(SQLQuerys.updateModificationDateOf);

                selectStatement.BindDoubleParameterWithName("@maxTimestamp", modificationTime);
                selectStatement.BindTextParameterWithName("@Entity", entity);

                await selectStatement.StepAsync();

                DataBaseHelper.DBLoaded.Set();
                System.Diagnostics.Debug.WriteLine("GenericModel - updateModificationDateOf. Fecha establecida para la tabla " + entity + ": " + modificationTime.ToString());
                return 1;
            }
            catch (Exception e)
            {
                throw new Exception("E R R O R : GenericModel - updateModificationDateOf" + entity + ": " + e.Message, e);
            }
        }

        public async Task<List<SynchroTableInfo>> GetSynchronizationTables()
        {
            try
            {
                Database database = await DataBaseHelper.GetDatabaseAsync();
                List<SynchroTableInfo> ListSTI = new List<SynchroTableInfo>();

                Statement selectStatement = await database.PrepareStatementAsync(SQLQuerys.GetSynchronizationTables);

                while (await selectStatement.StepAsync())
                {
                    int _order = selectStatement.GetIntAt(0);

                    String _entity = selectStatement.GetTextAt(1);

                    int _frequency = selectStatement.GetIntAt(2);

                    int _maxRows = selectStatement.GetIntAt(6);


                    int _minRows = selectStatement.GetIntAt(7);

                    string pattern = "yyyy-MM-dd HH':'mm':'ss";
                    DateTime _maxTimestamp;
                    DateTime _minTimestamp;
                    DateTime.TryParseExact(selectStatement.GetTextAt(3), pattern, null, DateTimeStyles.None, out _maxTimestamp);
                    DateTime.TryParseExact(selectStatement.GetTextAt(4), pattern, null, DateTimeStyles.None, out _minTimestamp);

                    String _direction = selectStatement.GetTextAt(5);

                    ListSTI.Add(new SynchroTableInfo() { Order = _order, Entity = _entity, Frequency = _frequency, MaxTimestamp = _maxTimestamp, MinTimestamp = _minTimestamp, Direction = _direction, MaxRows = _maxRows, MinRows = _minRows });
                }

                DataBaseHelper.DBLoaded.Set();

                return ListSTI;
            }
            catch (Exception e)
            {
                throw new Exception("E R R O R : GenericModel - GetSynchronizationTables" + e.Message, e);
            }
        }

        public async Task<int> resetSynchroTables()
        {
            try
            {
                //TODO: resetSynchroTables
                /*Database database = await App.GetDatabaseAsync();

                Statement selectStatement = await database.PrepareStatementAsync(SQLQuerys.resetSynchroTable);

                await selectStatement.StepAsync();

                App.DBLoaded.Set();
                System.Diagnostics.Debug.WriteLine("GenericModel - resetSynchroTables. Restablecidas fechas de tablas de synchro a 1406703600000");*/
                return 1;
            }
            catch (Exception e)
            {
                throw new Exception("E R R O R : GenericModel - resetSynchroTables: " + e.Message, e);
            }
        }

        public async Task<bool> deleteTableToReset(String Entity)
        {
            try
            {
                Database database = await DataBaseHelper.GetDatabaseAsync();

                String sDeleteQuery = "DELETE FROM " + Entity;

                Statement selectStatement = await database.PrepareStatementAsync(sDeleteQuery);

                await selectStatement.StepAsync();

                DataBaseHelper.DBLoaded.Set();
                System.Diagnostics.Debug.WriteLine("GenericModel - deleteTableToReset. Restablecida la tabla " + Entity + " (todos los datos borrados)");
                return true;
            }
            catch (Exception e)
            {
                System.Diagnostics.Debug.WriteLine("E R R O R : GenericModel - deleteTableToReset: " + e.Message, e);
                return false;
            }
        }

        public async Task<bool> deleteShotsOlderThanMax()
        {
            int idShot = -1;
            try
            {
                idShot = await getOlderShotToDelete();

                Database database = await DataBaseHelper.GetDatabaseAsync();
                
                string selectQuery = SQLQuerys.deleteShotsOlderThanMax;

                Statement selectStatement = await database.PrepareStatementAsync(selectQuery);

                selectStatement.BindIntParameterWithName("@idShot", idShot);

                await selectStatement.StepAsync();

                DataBaseHelper.DBLoaded.Set();
                System.Diagnostics.Debug.WriteLine("- - - Borrados los " + Constants.SHOTS_LIMIT.ToString() + " shots inferiores a " + idShot.ToString()  + " correctamente.");
                return true;
            }
            catch (Exception e)
            {
                System.Diagnostics.Debug.WriteLine("E R R O R : GenericModel - deleteShotsOlderThanMax: " + e.Message, e);
                return false;
            }
        }

        private async Task<int> getOlderShotToDelete()
        {
            int retorn = -1;
            try
            {
                Database database = await DataBaseHelper.GetDatabaseAsync();

                string selectQuery = SQLQuerys.getMinIdShotOlderThanMax;

                Statement selectStatement = await database.PrepareStatementAsync(selectQuery);

                selectStatement.BindIntParameterWithName("@limit", Constants.SHOTS_LIMIT);

                if (await selectStatement.StepAsync())
                {
                    retorn = selectStatement.GetIntAt(0);
                }

                DataBaseHelper.DBLoaded.Set();
                return retorn;
            }
            catch (Exception e)
            {
                System.Diagnostics.Debug.WriteLine("E R R O R : GenericModel - getOlderShotToDelete: " + e.Message, e);
                return -1;
            }
        }

    }
}
