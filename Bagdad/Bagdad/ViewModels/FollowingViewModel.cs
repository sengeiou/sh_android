using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Media;
using System.Windows.Media.Imaging;

namespace Bagdad.ViewModels
{
    public class FollowingViewModel
    {
        public int idUser { get; set; }
        public bool isFollowed { get; set; }
        public String userName { get; set; }
        public String userNickName { get; set; }
        public BitmapImage userImage { get; set; }
        public String userImageURL { get; set; }
        public String buttonText { get; set; }
        public SolidColorBrush buttonBackgorund { get; set; }
        public SolidColorBrush buttonForeground { get; set; }
        

        public FollowingViewModel() { }
    }
}
