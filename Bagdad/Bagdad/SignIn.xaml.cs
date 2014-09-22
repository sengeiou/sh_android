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

        private void PhoneApplicationPage_OrientationChanged(object sender, OrientationChangedEventArgs e)
        {
            // Switch the placement of the buttons based on an orientation change.
            if ((e.Orientation & PageOrientation.Portrait) == (PageOrientation.Portrait))
            {
                Grid.SetRow(signInFacebook, 1);
                Grid.SetColumn(signInFacebook, 0);
                signInFacebook.HorizontalAlignment = System.Windows.HorizontalAlignment.Left;
                ContentPanel.Margin = new Thickness(12, 12, 12, 0);
            }
            // If not in portrait, move buttonList content to visible row and column.
            else
            {
                Grid.SetRow(signInFacebook, 0);
                Grid.SetColumn(signInFacebook, 1);
                signInFacebook.HorizontalAlignment = System.Windows.HorizontalAlignment.Right;
                ContentPanel.Margin = new Thickness(12,0,12,0);
            }
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
                            if(await util.LogInByEmail(txbUser.Text, pbPassword.Password)) NavigationService.Navigate(new Uri("/TimeLine.xaml", UriKind.Relative));
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
                                if (await util.LogInByUserName(txbUser.Text, pbPassword.Password)) NavigationService.Navigate(new Uri("/TimeLine.xaml", UriKind.Relative));
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
                    MessageBox.Show(AppResources.GeneralLoginError);
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