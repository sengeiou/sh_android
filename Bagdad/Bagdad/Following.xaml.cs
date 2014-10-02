using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Navigation;
using Microsoft.Phone.Controls;
using Microsoft.Phone.Shell;
using System.Threading.Tasks;
using Bagdad.ViewModels;

namespace Bagdad
{
    public partial class Following : PhoneApplicationPage
    {
        int idUser = 0;
        FollowingsViewModel followings;
        public Following()
        {
            InitializeComponent();
            followings = new FollowingsViewModel();
            DataContext = followings;
        }

        protected async override void OnNavigatedTo(NavigationEventArgs e)
        {
            if (this.NavigationContext.QueryString.Count > 0 && !this.NavigationContext.QueryString["idUser"].Equals(""))
            {
                idUser = int.Parse(this.NavigationContext.QueryString["idUser"]);
            }
            else if (idUser == 0)
            {
                idUser = App.ID_USER;
            }

            await LoadFollowingsData();
        }

        private async Task<int> LoadFollowingsData(){

            await followings.LoadData(idUser);

            return 1;
        }

        private void goToProfile_Tap(object sender, System.Windows.Input.GestureEventArgs e)
        {
            int userId = ((FollowingViewModel)followingList.SelectedItem).idUser;
            NavigationService.Navigate(new Uri("/Me.xaml?idUser=" + userId, UriKind.Relative));
        }
    }
}