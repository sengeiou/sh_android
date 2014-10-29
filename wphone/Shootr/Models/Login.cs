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

        private String ops_data = "\"idUser\": null,\"idFavoriteTeam\": null,\"favoriteTeamName\": null,\"sessionToken\": null,\"userName\": null,\"email\": null,\"name\": null,\"photo\": null,\"bio\": null,\"website\": null,\"points\": null,\"numFollowings\": null,\"numFollowers\": null,\"revision\": null,\"birth\": null,\"modified\": null,\"deleted\": null";

        public Login(Factories.BagdadFactory _bagdadFactory)
        {
            bagdadFactory = _bagdadFactory; 
        }
        public Login()
        {
            bagdadFactory = new Factories.BagdadFactory();
        }
        
        protected override String GetEntityName() { return Constants.SERCOM_TB_LOGIN; }

        protected override String GetOps() { return ops_data; }

        protected override string GetAlias(string operation)
        {
            return "\"USER_LOGIN\",";
        }

        public override string ConstructOperation(String operation, String searchParams, int offset, int nItems)
        {
            nItems = 1;
            return "\"ops\":[{\"data\":[{" + GetOps() + "}],\"metadata\":{\"items\": " + nItems + ((offset != 0) ? ",\"offset\":" + offset : "") + ",\"TotalItems\":null,\"operation\":\"" + operation + "\"," + searchParams + ",\"entity\":\"" + GetEntityName() + "\"}}]";
        }

        /// <summary>
        /// Construct the filter
        /// </summary>
        /// <param name="conditionDate"></param>
        /// <returns></returns>
        public override async Task<string> ConstructFilter(string conditionDate)
        {
            StringBuilder sbFilterIdUser = new StringBuilder();
            try
            {
                Follow follow = bagdadFactory.CreateFollow();
                var followList = await follow.getidUserFollowing();
                bool isFirst = true;
                foreach (int idUser in followList)
                {
                    if (!isFirst)
                    {
                        sbFilterIdUser.Append(",");
                    }
                    sbFilterIdUser.Append("{\"comparator\":\"eq\",\"name\":\"idUser\",\"value\":" + idUser + "}");
                    isFirst = false;
                }
            }
            catch (Exception e)
            {
                throw new Exception("E R R O R - Login - constructFilterFollow: " + e.Message);
            }
            return "\"filterItems\":[], \"filters\":[" + conditionDate + ",{\"filterItems\":[ " + sbFilterIdUser.ToString() + "],\"filters\":[],\"nexus\":\"or\"}],\"nexus\":\"and\"";
        }

        

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
