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

namespace Bagdad.ViewModels
{
    public class ShotsViewModel : INotifyPropertyChanged
    {
        public ObservableCollection<ShotViewModel> shotsList { get; private set; }
        public List<BaseModelJsonConstructor> shotsModel;
        public List<BaseModelJsonConstructor> shotsModelFromScroll;


        public ShotsViewModel()
        {
            IsDataLoaded = false;
            this.shotsList = new ObservableCollection<ShotViewModel>();
            this.shotsModel = new List<BaseModelJsonConstructor>();
            this.shotsModelFromScroll = new List<BaseModelJsonConstructor>();
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

                return ParseShotsForPrinting(await shotModel.getTimeLineShots(), true);
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
                await shotModel.getTimeLineOtherShots(offset);

                return 1;
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

        private string ExtractTime(string shotTime)
        {
            String timeString = "";
            try
            {
                //time
                TimeSpan time = DateTime.Now.AddMilliseconds(App.TIME_LAPSE) - DateTime.Parse(shotTime);

                if (time.Days != 0) timeString = time.Days + "d";
                else if (time.Hours != 0) timeString = time.Hours + "h";
                else if (time.Minutes != 0) timeString = time.Minutes + "m";
                else if (time.Seconds != 0) timeString = time.Seconds + "s";
            }
            catch(Exception e)
            {
                Debug.WriteLine("E R R O R : ShotsViewModel - extractTime: " + e.Message);
            }
            return timeString;
        }

        public int ParseShotsForPrinting(List<ShotViewModel> shots, bool insertAtTale)
        {
            try
            {
                BitmapImage image;

                UserImageManager userImageManager = new UserImageManager();

                if (shots.Count == 0) return 0;
                int done = 0;

                shots.Sort((x, y) => x.shotTime.CompareTo(y.shotTime));
                if (insertAtTale) shots.Reverse();

                foreach (ShotViewModel shot in shots)
                {

                    //time
                    String timeString = ExtractTime(shot.shotTime);

                    //image
                    image = userImageManager.GetUserImage(shot.shotUserId);
                    if (image == null) image = new System.Windows.Media.Imaging.BitmapImage(new Uri(shot.shotUserImageURL, UriKind.Absolute));

                    //Tag
                    String tag = "";        //TODO: Partidos que tendrán asociadas las publicaciones
                    String tagIsVisible = "Visible";
                    if (tag.Equals("")) tagIsVisible = "Collapsed";

                    //Message
                    String message = shot.shotMessage;


                    if (insertAtTale) shotsList.Add(new ShotViewModel { shotId = shot.shotId, shotUserId = shot.shotUserId, shotMessage = message, shotTag = tag, tagVisibility = tagIsVisible, shotTime = timeString, shotUserImage = image, shotUserName = shot.shotUserName });
                    else shotsList.Insert(0, new ShotViewModel { shotId = shot.shotId, shotUserId = shot.shotUserId, shotMessage = message, shotTag = tag, tagVisibility = tagIsVisible, shotTime = timeString, shotUserImage = image, shotUserName = shot.shotUserName });
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
        public async Task<int> SendShot(string text)
        {
            Shot shot = new Shot();
            try
            {
                shot.comment = text.Replace("\r", "\\n");
                shot.idUser = App.ID_USER;
                if (await shot.isShotRepeatedIn24h()) return 0;
                await shot.SynchronizeShot();
                return 1;
            }
            catch (TimeoutException timeExc)
            {
                Debug.WriteLine("E R R O R : ShotsViewModel - SendShot: " + timeExc.Message);
                return 2;
            }
            catch (Exception e)
            {
                Debug.WriteLine("E R R O R : ShotsViewModel - SendShot: " + e.Message);
                return 0;
            }
        }

        public void SetShotsOnScreenToUpdate(List<BaseModelJsonConstructor> _shotsModel, bool fromScroll)
        {
            if(!fromScroll) shotsModel = _shotsModel;
            else shotsModelFromScroll = _shotsModel;
        }

        public async Task<int> UpdateShotsOnScreen()
        {
            int retorn = 0;
            try {
                List<ShotViewModel> shotsViewModel = new List<ShotViewModel>();
                
                foreach (Shot shot in shotsModel)
                {
                    retorn = 1;
                    User user = new User();
                    List<String> userinfo = await user.GetNameAndImageURL(shot.idUser);
                    String _shotUserName = String.Empty, _shotImageURL = String.Empty;
                    if(userinfo.Count > 0) 
                    {
                        _shotUserName = userinfo[0];
                        if (userinfo.Count > 1) _shotImageURL = userinfo[1];
                    }
                    shotsViewModel.Add(new ShotViewModel { shotId = shot.idShot, shotMessage = shot.comment, shotUserId = shot.idUser, shotTime = Util.FromUnixTime(shot.csys_birth.ToString()).ToString("s").Replace('T', ' '), shotUserImageURL = _shotImageURL, shotUserName = _shotUserName });
                }
                ParseShotsForPrinting(shotsViewModel, false);
                shotsModel.Clear();
            }
            catch (Exception e)
            {
                Debug.WriteLine("E R R O R : UpdateShotsOnScreen - SendShot: " + e.Message);
            }
            return retorn;
        }

        public async Task<int> UpdateShotsOnScreenFromScroll()
        {
            int retorn = 0;
            try
            {
                List<ShotViewModel> shotsViewModel = new List<ShotViewModel>();

                foreach (Shot shot in shotsModelFromScroll)
                {
                    retorn = 1;
                    User user = new User();
                    List<String> userinfo = await user.GetNameAndImageURL(shot.idUser);
                    String _shotUserName = String.Empty, _shotImageURL = String.Empty;
                    if (userinfo.Count > 0)
                    {
                        _shotUserName = userinfo[0];
                        if (userinfo.Count > 1) _shotImageURL = userinfo[1];
                    }
                    shotsViewModel.Add(new ShotViewModel { shotId = shot.idShot, shotMessage = shot.comment, shotUserId = shot.idUser, shotTime = Util.FromUnixTime(shot.csys_birth.ToString()).ToString("s").Replace('T', ' '), shotUserImageURL = _shotImageURL, shotUserName = _shotUserName });
                }
                ParseShotsForPrinting(shotsViewModel, true);
                shotsModelFromScroll.Clear();
            }
            catch (Exception e)
            {
                Debug.WriteLine("E R R O R : UpdateShotsOnScreen - SendShot: " + e.Message);
            }
            return retorn;
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
