using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bagdad.ViewModels
{
    public class WatchListMatchViewModel
    {
        public String matchName { get; set; }
        public String matchDate { get; set; }
        public Boolean isLive { get; set; }
        public List<WatchListOfMatchUserInfoViewModel> usersViewingMatch { get; set; }
    }
}
