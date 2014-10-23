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
    public partial class Shot : BaseModelJsonConstructor
    {
        public int idShot { get; set; }
        public int idUser { get; set; }
        public string comment  { get; set; }
        public Double csys_birth { get; set; }
        public Double csys_modified { get; set; }
        public Double csys_deleted { get; set; }
        public int csys_revision { get; set; }
        public char csys_synchronized { get; set; }
        public Factories.BagdadFactory bagdadFactory { private get; set; }
        private String ops_data = "\"idShot\": null,\"idUser\": null,\"comment\": null,\"revision\": null,\"birth\": null,\"modified\": null,\"deleted\": null";

        public Shot(Factories.BagdadFactory _bagdadFactory)
        {
            bagdadFactory = _bagdadFactory; 
        }
        public Shot()
        {
            bagdadFactory = new Factories.BagdadFactory();
        }

        public async Task<int> AddOlderShotsToTimeLine(JObject job)
        {
            int done = 0;
            
            bool add;
            int idShot = 0;
            int idUser = 0;
            string comment = "";
            string shotDate = "";
            User user = new User();
            List<String> userData = null;
            List<ShotViewModel> OldShots = new List<ShotViewModel>();

            try
            {
                if (job["status"]["code"].ToString().Equals("OK") && !job["ops"][0]["metadata"]["items"].ToString().Equals("0"))
                {
                    foreach (JToken shot in job["ops"][0]["data"])
                    {
                        add = true;

                        if (shot["idShot"] == null || String.IsNullOrEmpty(shot["idShot"].ToString()))
                            add = false;
                        else
                           idShot = int.Parse(shot["idShot"].ToString());

                        if (shot["idUser"] == null || String.IsNullOrEmpty(shot["idUser"].ToString()))
                            add = false;
                        else
                        {
                            idUser = int.Parse(shot["idUser"].ToString());
                            //get Name and URL By idUser
                            userData = await user.GetNameAndImageURL(idUser);
                            if(userData == null) add = false;
                        }

                        if (shot["comment"] == null || String.IsNullOrEmpty(shot["comment"].ToString()))
                            add = false;
                        else
                            comment = shot["comment"].ToString();

                        if (shot["birth"] == null || String.IsNullOrEmpty(shot["birth"].ToString()))
                            add = false;
                        else
                            shotDate = Util.FromUnixTime(shot["birth"].ToString()).ToString("s").Replace('T', ' ');


                        if (add)
                        {
                            OldShots.Add(bagdadFactory.CreateShotViewModel(idShot, comment, shotDate, idUser, userData[1], userData[0]));
                            done++;
                        }

                    }
                    //OldShots.Sort((x, y) => x.shotTime.CompareTo(y.shotTime));
                    //OldShots.Reverse();
                    App.ShotsVM.ParseShotsForPrinting(OldShots, true);

                }
            }
            catch (Exception e)
            {
                throw new Exception("E R R O R - Shot - AddOlderShotsToTimeLine: " + e.Message);
            }
            return done;
        }

        protected override String GetEntityName() { return Constants.SERCOM_TB_SHOT; }

        protected override String GetOps() { return ops_data; }

        protected override string GetAlias(string operation)
        {
            return "";
        }

        public override async Task<string> ConstructFilter(string conditionDate)
        {

            StringBuilder sbFilterIdUser = new StringBuilder();
            try
            {
                Follow follow = bagdadFactory.CreateFollow();
                var followList = await follow.getidUserFollowing();
                foreach (int idUser in followList)
                {
                    sbFilterIdUser.Append(",");
                    sbFilterIdUser.Append("{\"comparator\":\"eq\",\"name\":\"idUser\",\"value\":" + idUser + "}");
                }
            }
            catch (Exception e)
            {
                throw new Exception("E R R O R - User - constructFilterFollow: " + e.Message);
            }
            return "\"filterItems\":[], \"filters\":[" + conditionDate + ",{\"filterItems\":[ {\"comparator\":\"eq\",\"name\":\"idUser\",\"value\":" + App.ID_USER + "}"  + sbFilterIdUser.ToString() + "],\"filters\":[],\"nexus\":\"or\"}],\"nexus\":\"and\"";
        }
    }
}
