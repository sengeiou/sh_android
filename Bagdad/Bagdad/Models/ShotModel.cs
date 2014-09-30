using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bagdad.Models
{
    public class ShotModel
    {
        public int shotId { get; set; }
        public int shotUserId { get; set; }
        public String shotMessage { get; set; }
        public String shotUserName { get; set; }
        public String shotUserImageURL { get; set; }
        public String shotTime { get; set; }
    }
}
