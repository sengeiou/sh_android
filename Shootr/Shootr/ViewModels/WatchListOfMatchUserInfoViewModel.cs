using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Media;
using System.Windows.Media.Imaging;

namespace Bagdad.ViewModels
{
    public class WatchListOfMatchUserInfoViewModel
    {
        public String viewInfo { get; set; }
        public SolidColorBrush viewInfoForeground { get; set; }
        public Visibility editButtonVisibility { get; set; }
        public UserViewModel user { get; set; }
    }
}
