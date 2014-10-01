using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Navigation;
using Microsoft.Phone.Controls;
using Microsoft.Phone.Shell;
using Bagdad.Utils;
using Bagdad.Resources;
using System.Diagnostics;

namespace Bagdad
{
    public partial class SignIn : PhoneApplicationPage
    {
        Util util = new Util();

        public SignIn()
        {
            InitializeComponent();
        }
        
        private async void SignIn_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                if (App.isInternetAvailable)
                {
                    if (util.isAnEmail(txbUser.Text))
                    {
                        //it's an email
                        if (util.isAValidPassword(pbPassword.Password))
                        {
                            //Call by email
                            if (await util.LogInByEmail(txbUser.Text, pbPassword.Password))
                            {
                                App.UpdateServices(Bagdad.Utils.Constants.ST_DOWNLOAD_ONLY, Utils.ServiceCommunication.enumSynchroTables.FULL);
                                NavigationService.Navigate(new Uri("/TimeLine.xaml", UriKind.Relative));
                            }
                        }
                        else
                        {
                            //invalid password
                            MessageBox.Show(AppResources.GeneralLoginError);
                        }
                    }
                    else
                    {
                        //not an email
                        if (util.isAValidUser(txbUser.Text))
                        {
                            if (util.isAValidPassword(pbPassword.Password))
                            {
                                //Call by userName
                                if (await util.LogInByUserName(txbUser.Text, pbPassword.Password))
                                {
                                    App.UpdateServices(Bagdad.Utils.Constants.ST_DOWNLOAD_ONLY, Utils.ServiceCommunication.enumSynchroTables.FULL);
                                    NavigationService.Navigate(new Uri("/TimeLine.xaml", UriKind.Relative));
                                }
                            }
                            else
                            {
                                //invalid password
                                MessageBox.Show(AppResources.GeneralLoginError);
                            }
                        }
                        else
                        {
                            //invalid mail or user
                            MessageBox.Show(AppResources.GeneralLoginError);
                        }
                    }
                }
                else
                {
                    //No Internet Connection Message.
                    MessageBox.Show(AppResources.NoInternet, AppResources.CanConnect, MessageBoxButton.OK);
                }
            }
            catch (Exception ef)
            {
                //ServerError
                MessageBox.Show(AppResources.GeneralLoginError);
            }
        }

        //Prevents the TextBox of autoChange Background color on got Focus
        private void TextBox_GotFocus(object sender, RoutedEventArgs e)
        {
            System.Windows.Media.SolidColorBrush Brush2 = new System.Windows.Media.SolidColorBrush();
            Brush2.Color = System.Windows.Media.Colors.Transparent;
            txbUser.Background = Brush2;
        }

        //Prevents the PasswordBox of autoChange Background color on got Focus
        private void PasswordBox_GotFocus(object sender, RoutedEventArgs e)
        {
            System.Windows.Media.SolidColorBrush Brush2 = new System.Windows.Media.SolidColorBrush();
            Brush2.Color = System.Windows.Media.Colors.Transparent;
            pbPassword.Background = Brush2;
        }
    }
}