using Bagdad.Models;
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
    public class FollowViewModel
    {
        
        public User userInfo { get; set; }
        public bool isFollowed { get; set; }
        public String buttonText { get; set; }
        public BitmapImage userImage { get; set; }
        public SolidColorBrush buttonBackgorund { get; set; }
        public SolidColorBrush buttonForeground { get; set; }
        public SolidColorBrush buttonBorderColor { get; set; }
        public BitmapImage buttonIcon { get; set; }
        public Visibility buttonVisible { get; set; }
        public Visibility buttonIconVisible { get; set; }
        

        public FollowViewModel() { }
    }
}
