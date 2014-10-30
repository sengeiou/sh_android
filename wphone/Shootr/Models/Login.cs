using Bagdad.Utils;
using Newtonsoft.Json.Linq;
using SQLiteWinRT;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bagdad.Models
{
    public partial class Login : BaseModelJsonConstructor
    {
        public int idUser { get; set; }
        public int idFavoriteTeam { get; set; }
        public string favoriteTeamName { get; set; }
        public string userName { get; set; }
        public string name { get; set; }
        public string sessionToken { get; set; }
        public string email { get; set; }
        public string photo { get; set; }
        public string bio { get; set; }
        public string website { get; set; }
        public int numFollowers { get; set; }
        public int numFollowing { get; set; }
        public int points { get; set; }
        public Double csys_birth { get; set; }
        public Double csys_modified { get; set; }
        public Double csys_deleted { get; set; }
        public int csys_revision { get; set; }
        public char csys_synchronized { get; set; }
        public Factories.BagdadFactory bagdadFactory { private get; set; }


        public Login(Factories.BagdadFactory _bagdadFactory)
        {
            bagdadFactory = _bagdadFactory; 
        }
        public Login()
        {
            bagdadFactory = new Factories.BagdadFactory();
        }
        
        protected override String GetEntityName() { return Constants.SERCOM_TB_LOGIN; }

        public async Task<bool> isUserAlreadyLoged()
        {
            try
            {
                User u = new User();
                String sessionToken = await u.getSessionToken();

                if (sessionToken.Equals("")) return false;
                else return true;
            }
            catch (System.Security.SecurityException e)
            {
                System.Diagnostics.Debug.WriteLine("E R R O R :  isUserAlreadyLoged: " + e.Message);
                throw e;
            }
        }

       
    }
}
