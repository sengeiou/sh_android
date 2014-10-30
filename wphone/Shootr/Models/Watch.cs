using Bagdad.Utils;
using System;
using System.Text;
using System.Threading.Tasks;

namespace Bagdad.Models
{
    public partial class Watch : BaseModelJsonConstructor
    {
        public int idMatch { get; set; }
        public int idUser { get; set; }
        public int status { get; set; }
        public Double csys_birth { get; set; }
        public Double csys_modified { get; set; }
        public Double csys_deleted { get; set; }
        public int csys_revision { get; set; }
        public char csys_synchronized { get; set; }
        public Factories.BagdadFactory bagdadFactory { private get; set; }
        
        public Watch(Factories.BagdadFactory _bagdadFactory)
        {
            bagdadFactory = _bagdadFactory; 
        }
        
        public Watch()
        {
            bagdadFactory = new Factories.BagdadFactory();
        }

        protected override String GetEntityName() { return Constants.SERCOM_TB_WATCH; }

        

    }
}
