using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bagdad.Models
{
        public class SynchroTableInfo
        {
            public int Order { get; set; }
            public String Entity { get; set; }
            public int Frequency { get; set; }
            public DateTime MaxTimestamp { get; set; }
            public DateTime MinTimestamp { get; set; }
            public String Direction { get; set; }
            public int MaxRows { get; set; }
            public int MinRows { get; set; }
        }
}
