using Bagdad.Utils;
using Bagdad.ViewModels;
using Newtonsoft.Json.Linq;
using SQLiteWinRT;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bagdad.Models
{
    public partial class Device : BaseModelJsonConstructor
    {
        public override Task<int> SaveData(List<BaseModelJsonConstructor> models)
        {
            throw new NotImplementedException();
        }

        /// <summary>
        /// Update Object with the current information on the Local DB
        /// </summary>
        /// <returns>true if device exists, false if not</returns>
        public async Task<bool> GetCurrentDeviceInfo()
        {
            bool _return = false;
            try
            {
                Database db = await DataBaseHelper.GetDatabaseAsync();
                Statement st = await db.PrepareStatementAsync(SQLQuerys.GetCurrentDevice);

                if (await st.StepAsync())
                {

                    idDevice = st.GetIntAt(0);
                    idUser = st.GetIntAt(1);
                    token = st.GetTextAt(2);
                    uniqueDeviceID = st.GetTextAt(3);
                    model = st.GetTextAt(4);
                    osVer = st.GetTextAt(5);
                    csys_birth = Util.DateToDouble(DateTime.Parse(st.GetTextAt(6)));
                    csys_modified = Util.DateToDouble(DateTime.Parse(st.GetTextAt(7)));
                    csys_revision = st.GetIntAt(8);

                    App.ID_DEVICE = st.GetIntAt(0);
                    _return = true;
                }
                DataBaseHelper.DBLoaded.Set();
            }
            catch (Exception e)
            {
                throw new Exception("Device - GetCurrentDeviceID: " + e.Message, e);
            }
            return _return;
        }
        
        /// <summary>
        /// Put the current object data in the Local DB
        /// </summary>
        /// <returns>true if works, false if not</returns>
        private async Task<bool> SaveOrUpdateLocalData()
        {
            bool _return = false;
            try
            {
                Database db = await DataBaseHelper.GetDatabaseAsync();
                Statement st = await db.PrepareStatementAsync(SQLQuerys.SaveOrCreateDevice);

                st.BindIntParameterWithName("@idDevice", this.idDevice);
                st.BindIntParameterWithName("@idUser", this.idUser);
                st.BindTextParameterWithName("@token", this.token);
                st.BindTextParameterWithName("@uniqueDeviceID", this.uniqueDeviceID);
                st.BindTextParameterWithName("@model", this.model);
                st.BindTextParameterWithName("@osVer", this.osVer);
                st.BindTextParameterWithName("@csys_birth", Util.FromUnixTime(this.csys_birth.ToString()).ToString("s").Replace('T', ' '));
                st.BindTextParameterWithName("@csys_modified", Util.FromUnixTime(this.csys_modified.ToString()).ToString("s").Replace('T', ' '));
                st.BindTextParameterWithName("@csys_revision", Util.FromUnixTime(this.csys_revision.ToString()).ToString("s").Replace('T', ' '));

                await st.StepAsync();
                _return = true;

                DataBaseHelper.DBLoaded.Set();
            }
            catch (Exception e)
            {
                throw new Exception("Device - GetCurrentDeviceID: " + e.Message, e);
            }
            return _return;
        }
    }
}
