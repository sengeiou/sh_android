using Bagdad.Utils;
using System;
using System.Text;
using System.Threading.Tasks;

namespace Bagdad.Models
{
    public partial class User : BaseModelJsonConstructor
    {
        public int idUser { get; set; }
        public int idFavoriteTeam{ get; set; }
        public string favoriteTeamName { get; set; }
        public string userName{ get; set; }
        public string name{ get; set; }
        public string photo{ get; set; }
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

        private String ops_data = "\"idUser\": null,\"idFavoriteTeam\": null,\"favoriteTeamName\": null,\"userName\": null,\"name\": null,\"photo\": null,\"bio\": null,\"website\": null,\"points\": null,\"numFollowings\": null,\"numFollowers\": null,\"revision\": null,\"birth\": null,\"modified\": null,\"deleted\": null";

        public User(Factories.BagdadFactory _bagdadFactory)
        {
            bagdadFactory = _bagdadFactory; 
        }
        
        public User()
        {
            bagdadFactory = new Factories.BagdadFactory();
        }

        protected override String GetEntityName() { return Constants.SERCOM_TB_USER; }
        
        protected override String GetOps() { return ops_data; }

        protected override string GetAlias(string operation)
        {
            return "\"GET_USERS\",";
        }

        public String GetUserOps() { return ops_data; }

        public override async Task<string> ConstructFilter(string conditionDate)
        {
            StringBuilder sbFilterIdUser = new StringBuilder();
            try
            {
                Follow follow = new Follow();
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
                 sbFilterIdUser.Append(",{\"comparator\":\"eq\",\"name\":\"idUser\",\"value\":" + App.ID_USER + "}");
            }
            catch (Exception e)
            {
                throw new Exception("E R R O R - User - constructFilterFollow: " + e.Message);
            }
            return "\"filterItems\":[], \"filters\":[" + conditionDate + ",{\"filterItems\":[ " + sbFilterIdUser.ToString() + "],\"filters\":[],\"nexus\":\"or\"}],\"nexus\":\"and\"";
        }

        

        

    }
}
