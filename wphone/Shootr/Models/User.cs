using Bagdad.Utils;
using System;
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

        public User(Factories.BagdadFactory _bagdadFactory)
        {
            bagdadFactory = _bagdadFactory; 
        }
        
        public User()
        {
            bagdadFactory = new Factories.BagdadFactory();
        }

        protected override String GetEntityName() { return Constants.SERCOM_TB_USER; }
        
        

        

        

        

    }
}
