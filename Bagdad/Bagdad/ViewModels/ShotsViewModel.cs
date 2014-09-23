using Bagdad.Utils;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Media.Imaging;

namespace Bagdad.ViewModels
{
    public class ShotsViewModel
    {
        public ObservableCollection<ShotViewModel> Shots { get; private set; }

        public ShotsViewModel()
        {
            IsDataLoaded = false;
            this.Shots = new ObservableCollection<ShotViewModel>();
        }

        public bool IsDataLoaded
        {
            get;
            private set;
        }

        public async Task<int> LoadData(){
            try
            {
                BitmapImage image;
                UserImageManager im = new UserImageManager();

                //im.SaveImageFromURL("http://s3-eu-west-1.amazonaws.com/bagdag/bender.jpg", 1); //Chargue someting
                //im.SaveImageFromURL("http://s3-eu-west-1.amazonaws.com/bagdag/mordisquitos.jpg", 2);
                                
                image = im.GetUserImage(1);
            
                if (image == null) image = new System.Windows.Media.Imaging.BitmapImage(new Uri("http://s3-eu-west-1.amazonaws.com/bagdag/nixon.jpg", UriKind.Absolute));

                //Shots.Add(new ShotViewModel { shotMessage = "", shotTag = "", shotTime = "", shotUserImage = null, shotUserName = "" });

                Shots.Add(new ShotViewModel { shotMessage="Lorem fistrum hasta luego Lucas torpedo diodenoo benemeritaar apetecan ahorarr ahorarr está la cosa muy malar condemor va usté muy cargadoo.", shotTag="Barcelona-Elche 16/12", shotTime="3m", shotUserImage=image, shotUserName="Windows Phone"});
                Shots.Add(new ShotViewModel { shotMessage = "Lorem fistrum hasta luego Lucas torpedo diodenoo benemeritaar apetecantá la cosa muy malar condemor va usté muy cargadoo.", shotTag = "Barcelona-Elche 16/12", shotTime = "3m", shotUserImage = im.GetUserImage(2), shotUserName = "Windows Phone" });
                Shots.Add(new ShotViewModel { shotMessage="Lorem fistrum hasta luego Lucas.", shotTag="Barcelona-Elche 16/12", shotTime="3m", shotUserImage=image, shotUserName="Windows Phone"});
                Shots.Add(new ShotViewModel { shotMessage = "Lorem fistrum hasta luego Lucas torpedo diodenoo benemeritaar apetecan ahorarr ahorarr está la cosa muy malar condemor va usté muy cargadoo.", shotTag = "Barcelona-Elche 16/12", shotTime = "3m", shotUserImage = im.GetUserImage(2), shotUserName = "Windows Phone" });
                Shots.Add(new ShotViewModel { shotMessage="Lorem fistrum hasta luego Lucas.", shotTag="Barcelona-Elche 16/12", shotTime="3m", shotUserImage=image, shotUserName="Windows Phone"});
                Shots.Add(new ShotViewModel { shotMessage = "Lorem fistrum hasta luego Lucas torpedo diodenoo benemeritaar apetecantá la cosa muy malar condemor va usté muy cargadoo.", shotTag = "Barcelona-Elche 16/12", shotTime = "3m", shotUserImage = im.GetUserImage(2), shotUserName = "Windows Phone" });

                IsDataLoaded = true;
                return 1;
            }
            catch (Exception e)
            {
                Debug.WriteLine("E R R O R : ShotsViewModel - LoadData: " + e.Message);
                return 0;
            }
        }
    }
}
