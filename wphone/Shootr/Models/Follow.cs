using Bagdad.Utils;
using Bagdad.ViewModels;
using Newtonsoft.Json.Linq;
using SQLiteWinRT;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bagdad.Models
{
    public partial class Follow : BaseModelJsonConstructor
    {

        public int idUser { get; set; }
        public int idUserFollowed { get; set; }
        public Double csys_birth { get; set; }
        public Double csys_modified { get; set; }
        public Double csys_deleted { get; set; }
        public int csys_revision { get; set; }
        public char csys_synchronized { get; set; }
        public Factories.BagdadFactory bagdadFactory { private get; set; }

        

        public Follow(Factories.BagdadFactory _bagdadFactory)
        {
            bagdadFactory = _bagdadFactory; 
        }
        public Follow()
        {
            bagdadFactory = new Factories.BagdadFactory();
        }

        protected override String GetEntityName() { return Constants.SERCOM_TB_FOLLOW; }       

        #region FOLLOW/UNFOLLOW

        private async Task<bool> UpdateNumOfFollowings(int _change, User user)
        {
            bool _result = false;

            int actualNumOfFollowings = await GetActualNumOfFollowings() + _change;
            
            if (_change > 0) _result = await user.AddCurrentUserToLocalDB();
            else _result = await user.RemoveCurrentUserFromLocalDB();

            if(_result) _result = await EditNumOfFollowings(actualNumOfFollowings);

            return _result;
        }


        #endregion
    }
}
