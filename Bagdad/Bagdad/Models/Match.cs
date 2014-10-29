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
    public partial class Match : BaseModelJsonConstructor
    {

        public int idMatch { get; set; }
        public Double matchDate { get; set; }
        public int status { get; set; }
        public int idLocalTeam { get; set; }
        public String localTeamName { get; set; }
        public int idVisitorTeam { get; set; }
        public String visitorTeamName { get; set; }
        public Double csys_birth { get; set; }
        public Double csys_modified { get; set; }
        public Double csys_deleted { get; set; }
        public int csys_revision { get; set; }
        public char csys_synchronized { get; set; }
        public Factories.BagdadFactory bagdadFactory { private get; set; }

        private String ops_data = "";

        public Match(Factories.BagdadFactory _bagdadFactory)
        {
            bagdadFactory = _bagdadFactory; 
        }
        public Match()
        {
            bagdadFactory = new Factories.BagdadFactory();
        }

        protected override String GetEntityName() { return Constants.SERCOM_TB_MATCH; }

        protected override String GetOps() { return ops_data; }

        protected override string GetAlias(string operation)
        {
            return "";
        }

        public override async Task<string> ConstructFilter(string conditionDate)
        {
            return "";
        }

       
    }
}
