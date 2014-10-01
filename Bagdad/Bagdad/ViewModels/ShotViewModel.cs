using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Media.Imaging;

namespace Bagdad.ViewModels
{
    public class ShotViewModel
    {
        public int shotId { get; set; }
        public int shotUserId { get; set; }
        public String shotUserName { get; set; }
        public BitmapImage shotUserImage { get; set; }
        public String shotTag { get; set; }
        public String tagVisibility { get; set; }
        public String shotMessage { get; set; }
        public String shotTime { get; set; }

        public ShotViewModel() { }
    }
}
