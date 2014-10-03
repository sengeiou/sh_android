using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Media.Imaging;

namespace Bagdad.ViewModels
{
    public class ShotViewModel : INotifyPropertyChanged
    {
        public event PropertyChangedEventHandler PropertyChanged;


        private int _shotId;

        public int shotId
        { 
            get{
            return _shotId;}
            set 
            {
                _shotId = value;
                NotifyPropertyChanged("_shotID");
            }
        }

        public int shotUserId { get; set; }
        public String shotUserName { get; set; }
        public BitmapImage shotUserImage { get; set; }
        public String shotUserImageURL { get; set; }
        public String shotTag { get; set; }
        public String tagVisibility { get; set; }
        public String shotMessage { get; set; }
        public String shotTime { get; set; }

        public ShotViewModel() { }

        // NotifyPropertyChanged will raise the PropertyChanged event, 
        // passing the source property that is being updated.
        public void NotifyPropertyChanged(string propertyName)
        {
            if (PropertyChanged != null)
            {
                PropertyChanged(this,
                    new PropertyChangedEventArgs(propertyName));
            }
        }
    }
}
