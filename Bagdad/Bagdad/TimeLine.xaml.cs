using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Navigation;
using Microsoft.Phone.Controls;
using Microsoft.Phone.Shell;
using Bagdad.Resources;
using Bagdad.Utils;
using System.Diagnostics;
using System.ComponentModel;

namespace Bagdad
{
    public partial class TimeLine : PhoneApplicationPage
    {

        ApplicationBarIconButton appBarButton;
        Util util = new Util();
        
        public TimeLine()
        {
            IsRedirectionNeeded();
            InitializeComponent();
            BuildLocalizedApplicationBar();
        }

        //When Click BACK on the Main Page (TimeLine) we close the App.
        protected override void OnBackKeyPress(CancelEventArgs e)
        {
            base.OnBackKeyPress(e);            
            Application.Current.Terminate();
        }

        private async void IsRedirectionNeeded()
        {
            try
            {
                //IF The User isn't Loged --> GO TO: Registration (Now goes to SignIn because Registration is not yet implemented)
                if (!await util.isUserAlreadyLoged()) NavigationService.Navigate(new Uri("/SignIn.xaml", UriKind.Relative));
            }
            catch (Exception e)
            {
                Debug.WriteLine("E R R O R : IsRedirectionNeeded: " + e.Message);
            }
        }

        // Build a localized ApplicationBar
        private void BuildLocalizedApplicationBar()
        {

            // Set the page's ApplicationBar to a new instance of ApplicationBar.
            ApplicationBar = new ApplicationBar();

            // Create a new button and set the text value to the localized string from AppResources.
            appBarButton =
                new ApplicationBarIconButton(new
                Uri("/Resources/icons/appbar.message.send.png", UriKind.Relative));
            appBarButton.Text = AppResources.Shoot;
            appBarButton.IsEnabled = false;
            appBarButton.Click += appBarShootButton_Click;
            ApplicationBar.Buttons.Add(appBarButton);

            ApplicationBarIconButton appBarButton2 =
                new ApplicationBarIconButton(new
                Uri("/Resources/icons/appbar.refresh.png", UriKind.Relative));
            appBarButton2.Text = AppResources.Refresh;
            ApplicationBar.Buttons.Add(appBarButton2);

            ApplicationBarIconButton appBarButton3 =
                new ApplicationBarIconButton(new
                Uri("/Resources/icons/appbar.magnify.png", UriKind.Relative));
            appBarButton3.Text = AppResources.Search;
            ApplicationBar.Buttons.Add(appBarButton3);

            // Create a new menu item with the localized string from AppResources.
            ApplicationBarMenuItem appBarMenuItem =
                new ApplicationBarMenuItem(AppResources.People);
            ApplicationBar.MenuItems.Add(appBarMenuItem);

            ApplicationBarMenuItem appBarMenuItem2 =
                new ApplicationBarMenuItem(AppResources.TimeLine);
            appBarMenuItem2.IsEnabled = false;
            ApplicationBar.MenuItems.Add(appBarMenuItem2);

            ApplicationBarMenuItem appBarMenuItem3 =
                new ApplicationBarMenuItem(AppResources.Me);
            ApplicationBar.MenuItems.Add(appBarMenuItem3);
        }

        private void appBarShootButton_Click(object sender, EventArgs e)
        {
            extraChars.Visibility = System.Windows.Visibility.Collapsed;
            extraChars.Text = "140";
            newShot.Text = "";
            Focus();
            if (NoShootsAdvice.Visibility == System.Windows.Visibility.Visible)
            {
                NoShootsAdvice.Visibility = System.Windows.Visibility.Collapsed;
            }
        }

        private void ChatBubbleTextBox_TextChanged(object sender, TextChangedEventArgs e)
        {
            if (!newShot.Text.Equals(""))
            {
                appBarButton.IsEnabled = true;

                if (extraCharacters() >= 0) extraChars.Text = extraCharacters().ToString();
                else
                {
                    newShot.Text = newShot.Text.Substring(0, 140); //TODO: GET THIS VAR FROM DB SYNCHRONIZED WITH SERVER
                    newShot.Select(140, 0);
                }

            }
            else
            {
                appBarButton.IsEnabled = false;
                extraChars.Text = extraCharacters().ToString();
            }
        }

        private int extraCharacters()
        {
            int extra = 140; //TODO: GET THIS VAR FROM DB SYNCHRONIZED WITH SERVER

            extra = extra - newShot.Text.Count();

            return extra;
        }

        private void newShot_GotFocus(object sender, RoutedEventArgs e)
        {
            extraChars.Visibility = System.Windows.Visibility.Visible;
        }

        private void newShot_LostFocus(object sender, RoutedEventArgs e)
        {
            if (newShot.Text.Equals(""))
            {
                extraChars.Text = "140";
                extraChars.Visibility = System.Windows.Visibility.Collapsed;
            }
        }

        private void StartShooting_Tap(object sender, System.Windows.Input.GestureEventArgs e)
        {
            newShot.Focus();
        }

        private void Info_Tap(object sender, System.Windows.Input.GestureEventArgs e)
        {

        }

        private void ImWatching_Tap(object sender, System.Windows.Input.GestureEventArgs e)
        {

        }
    }
}