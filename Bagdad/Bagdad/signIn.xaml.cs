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
    public partial class SignIn : PhoneApplicationPage
    {
        public SignIn()
        {
            InitializeComponent();
        }

        private void SignIn_Click(object sender, RoutedEventArgs e)
        {
            if (App.isInternetAvailable)
            {
                //TODO: All the functions needed to Signing In
                MessageBox.Show("THERE IS INTERNET CONNECTION!!");
            }
            else
            {
                MessageBox.Show("THERE IS NO INTERNET CONNECTION!!");
            }
        }

        //Prevents the TextBox of autoChange Background color on got Focus
        private void TextBox_GotFocus(object sender, RoutedEventArgs e)
        {
            System.Windows.Media.SolidColorBrush Brush2 = new System.Windows.Media.SolidColorBrush();
            Brush2.Color = System.Windows.Media.Colors.Transparent;
            ((TextBox)sender).Background = Brush2;
        }

        //Prevents the PasswordBox of autoChange Background color on got Focus
        private void PasswordBox_GotFocus(object sender, RoutedEventArgs e)
        {
            System.Windows.Media.SolidColorBrush Brush2 = new System.Windows.Media.SolidColorBrush();
            Brush2.Color = System.Windows.Media.Colors.Transparent;
            ((PasswordBox)sender).Background = Brush2;
        }
    }
}