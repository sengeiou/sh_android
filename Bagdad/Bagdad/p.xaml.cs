using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Navigation;
using Microsoft.Phone.Controls;
using Microsoft.Phone.Shell;

namespace Bagdad
{
    public partial class p : PhoneApplicationPage
    {
        public p()
        {
            InitializeComponent();
        }

        protected override void OnNavigatedTo(NavigationEventArgs e)
        {
            NavigationService.Navigate(new Uri("/TimeLine.xaml", UriKind.Relative));
        }
    }
}