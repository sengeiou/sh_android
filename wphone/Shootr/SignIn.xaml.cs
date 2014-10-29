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
using Bagdad.Models;

namespace Bagdad
{
    public partial class SignIn : PhoneApplicationPage
    {
        Login login;
        Util util;

        public SignIn()
        {
            InitializeComponent();
            login = new Login();
            util = new Util();
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
                            if (await login.LogInByEmail(txbUser.Text, pbPassword.Password))
                            {
                                App.UpdateServices(ServiceCommunication.enumTypeSynchro.ST_DOWNLOAD_ONLY, ServiceCommunication.enumSynchroTables.FULL);
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
                                if (await login.LogInByUserName(txbUser.Text, pbPassword.Password))
                                {
                                    App.UpdateServices(ServiceCommunication.enumTypeSynchro.ST_DOWNLOAD_ONLY, ServiceCommunication.enumSynchroTables.FULL);
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
            catch
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