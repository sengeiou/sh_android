using Bagdad.Utils;
using System;
using System.Text;
using System.Threading.Tasks;

namespace Bagdad.Models
{
    public partial class Watch : BaseModelJsonConstructor
    {
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
