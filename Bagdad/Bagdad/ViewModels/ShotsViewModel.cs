using Bagdad.Models;
using Bagdad.Utils;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Controls;
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
                Shot shotModel = new Shot();
                
                foreach (ShotModel shot in await shotModel.getTimeLineShots()){

                    //time
                    String timeString = "";
                    TimeSpan time = DateTime.Now - DateTime.Parse(shot.shotTime);
                    if (time.Days != 0) timeString = time.Days + "d";
                    else if (time.Hours != 0) timeString = time.Hours + "h";
                    else if (time.Minutes != 0) timeString = time.Minutes + "m";
                    else if (time.Seconds != 0) timeString = time.Seconds + "s";

                    //image
                    image = App.UIM.GetUserImage(shot.shotUserId);
                    if (image == null) image = new System.Windows.Media.Imaging.BitmapImage(new Uri(shot.shotUserImageURL, UriKind.Absolute));

                    //Tag
                    String tag = "";
                    String tagIsVisible = "Visible";
                    if (tag.Equals("")) tagIsVisible = "Collapsed";

                    //Message
                    String message = shot.shotMessage;

                    Shots.Add(new ShotViewModel { shotId = shot.shotId, shotUserId = shot.shotUserId,  shotMessage = message, shotTag = tag, tagVisibility = tagIsVisible, shotTime = timeString, shotUserImage = image, shotUserName = shot.shotUserName });
                }
                
                IsDataLoaded = true;
                return 1;
            }
            catch (Exception e)
            {
                Debug.WriteLine("E R R O R : ShotsViewModel - LoadData: " + e.Message);
                return 0;
            }
        }

        /// <summary>
        /// Envía un shot al servidor
        /// </summary>
        /// <param name="text"></param>
        public void SendShot(string text)
        {
            try
            {
                Shot shot = new Shot();
                shot.comment = text;
                shot.idUser = App.ID_USER;
                shot.csys_revision = 0;
                shot.csys_synchronized = 'N';
                shot.synchronizeShot();

            }
            catch (Exception e)
            {
                Debug.WriteLine("E R R O R : ShotsViewModel - SendShot: " + e.Message);
            }
        }

    }
}
