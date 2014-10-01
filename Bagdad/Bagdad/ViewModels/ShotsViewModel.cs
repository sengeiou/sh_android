using Bagdad.Models;
using Bagdad.Utils;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Controls;
using System.Windows.Media.Imaging;
using System.ComponentModel;

namespace Bagdad.ViewModels
{
    public class ShotsViewModel : INotifyPropertyChanged
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
                Shot shotModel = new Shot();

                return ParseShotsForPrinting(await shotModel.getTimeLineShots());               
            }
            catch (Exception e)
            {
                Debug.WriteLine("E R R O R : ShotsViewModel - LoadData: " + e.Message);
                return 0;
            }
        }

        public async Task<int> LoadOtherShots(int offset)
        {
            try
            {
                Shot shotModel = new Shot();

                return ParseShotsForPrinting(await shotModel.getTimeLineOtherShots(offset));
            }
            catch (Exception e)
            {
                Debug.WriteLine("E R R O R : ShotsViewModel - LoadData: " + e.Message);
                return 0;
            }
        }

        /// <summary>
        /// Adds to the Shot List the next X shots from the server.
        /// </summary>
        /// <returns>The number of charged items</returns>
        public async Task<int> LoadOlderShots(int offset)
        {
            try
            {
                ServiceCommunication sc = new ServiceCommunication();

                return await sc.GetOlderShots(offset);
            }
            catch (Exception e)
            {
                Debug.WriteLine("E R R O R : ShotsViewModel - LoadOlderShots: " + e.Message);
                return 0;
            }
        }

        public int ParseShotsForPrinting(List<ShotModel> shots)
        {
            try
            {
                BitmapImage image;

                UserImageManager userImageManager = new UserImageManager();

                if (shots.Count == 0) return 0;
                int done = 0;

                foreach (ShotModel shot in shots)
                {

                    //time
                    String timeString = "";
                    TimeSpan time = DateTime.Now.AddMilliseconds(App.TIME_LAPSE) - DateTime.Parse(shot.shotTime);
                    
                    if (time.Days != 0) timeString = time.Days + "d";
                    else if (time.Hours != 0) timeString = time.Hours + "h";
                    else if (time.Minutes != 0) timeString = time.Minutes + "m";
                    else if (time.Seconds != 0) timeString = time.Seconds + "s";

                    //image
                    image = userImageManager.GetUserImage(shot.shotUserId);
                    if (image == null) image = new System.Windows.Media.Imaging.BitmapImage(new Uri(shot.shotUserImageURL, UriKind.Absolute));

                    //Tag
                    String tag = "";
                    String tagIsVisible = "Visible";
                    if (tag.Equals("")) tagIsVisible = "Collapsed";

                    //Message
                    String message = shot.shotMessage;

                    App.ShotsVM.Shots.Add(new ShotViewModel { shotId = shot.shotId, shotUserId = shot.shotUserId, shotMessage = message, shotTag = tag, tagVisibility = tagIsVisible, shotTime = timeString, shotUserImage = image, shotUserName = shot.shotUserName });
                    done++;
                }

                IsDataLoaded = true;

                return done;
            }
            catch (Exception e)
            {
                Debug.WriteLine("E R R O R : ShotsViewModel - ParseShotsForPrinting: " + e.Message);
                return 0;
            }
        }


        /// <summary>
        /// Envía un shot al servidor
        /// </summary>
        /// <param name="text"></param>
        public async void SendShot(string text)
        {
            Shot shot = new Shot();
            try
            {
                shot.comment = text;
                shot.idUser = App.ID_USER;
                shot.csys_revision = 0;
                shot.csys_synchronized = 'N';
                await shot.SynchronizeShot();
                List<ShotModel> oneShot = new List<ShotModel>();
                UserImageManager userImage = new UserImageManager();
                oneShot.Add(new ShotModel { shotId = shot.idShot, shotMessage = shot.comment, shotUserId = shot.idUser, shotTime = shot.csys_birth.ToShortDateString(), shotUserImageURL = userImage.GetUserImagename(shot.idUser) });
                ParseShotsForPrinting(oneShot);
                NotifyPropertyChanged("Shots");
            }
            catch (Exception e)
            {
                Debug.WriteLine("E R R O R : ShotsViewModel - SendShot: " + e.Message);
            }
        }

        public event PropertyChangedEventHandler PropertyChanged;

        private void NotifyPropertyChanged(String propertyName)
        {
            PropertyChangedEventHandler handler = PropertyChanged;
            if (null != handler)
            {
                handler(this, new PropertyChangedEventArgs(propertyName));
            }
        }

    }
}
