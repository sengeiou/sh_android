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
using System.Diagnostics;
using System.ComponentModel;
using System.Windows.Media;
using System.Windows.Automation.Peers;
using System.Windows.Automation.Provider;
using Bagdad.ViewModels;
using System.Windows.Threading;
using Bagdad.Utils;

namespace Bagdad
{
    public partial class TimeLine : PhoneApplicationPage
    {

        ApplicationBarIconButton appBarButtonShot;
        private DispatcherTimer timer;
        public ProgressIndicator progress;
        bool endOfLocalList = false;
        bool endOfList = false;
        private int offset = Constants.SERCOM_PARAM_TIME_LINE_FIRST_CHARGE;
        
        
        public TimeLine()
        {      
            InitializeComponent();
            BuildLocalizedApplicationBar();
            DataContext = App.ShotsVM;
            progress = new ProgressIndicator()
            {
                Text = AppResources.Synchroning,
                IsIndeterminate = true,
                IsVisible = false

            };
            SystemTray.SetProgressIndicator(this, progress);
            timer = new DispatcherTimer();
            timer.Tick += timer_Tick;
            timer.Interval = new TimeSpan(0, 0, 0, 10);
        }

        protected async override void OnNavigatedTo(NavigationEventArgs e)
        {
            try
            {
                Util util = new Util();
                var synchroLogin = await util.isUserAlreadyLoged();
                if (synchroLogin) timer.Start();


                if (!App.ShotsVM.IsDataLoaded)
                {
                    App.ShotsVM.Shots.Clear();
                    await App.ShotsVM.LoadData();
                }
                else
                {
                    if (App.ShotsVM.Shots.Count == 0) NoShootsAdvice.Visibility = System.Windows.Visibility.Visible;
                }
            }
            catch(Exception ex)
            {
                Debug.WriteLine("E R R O R : TimeLine - OnNavigatedTo: " + ex.Message);
            }
        }
        
        //When Click BACK on the Main Page (TimeLine) we close the App.
        protected override void OnBackKeyPress(CancelEventArgs e)
        {
            base.OnBackKeyPress(e);            
            Application.Current.Terminate();
        }

        // Build a localized ApplicationBar
        private void BuildLocalizedApplicationBar()
        {

            // Set the page's ApplicationBar to a new instance of ApplicationBar.
            ApplicationBar = new ApplicationBar();

            // Create a new button and set the text value to the localized string from AppResources.
            appBarButtonShot =
                new ApplicationBarIconButton(new
                Uri("/Resources/icons/appbar.message.send.png", UriKind.Relative));
            appBarButtonShot.Text = AppResources.Shoot;
            appBarButtonShot.IsEnabled = false;
            appBarButtonShot.Click += appBarShootButton_Click;
            ApplicationBar.Buttons.Add(appBarButtonShot);

            ApplicationBarIconButton appBarButtonWatching =
                new ApplicationBarIconButton(new
                Uri("/Resources/icons/appbar.eye.png", UriKind.Relative));
            appBarButtonWatching.Text = AppResources.ImWatching;
            appBarButtonWatching.Click += appBarWatchingButton_Click;
            ApplicationBar.Buttons.Add(appBarButtonWatching);

            ApplicationBarIconButton appBarButtonSearch =
                new ApplicationBarIconButton(new
                Uri("/Resources/icons/appbar.magnify.png", UriKind.Relative));
            appBarButtonSearch.Text = AppResources.Search;
            ApplicationBar.Buttons.Add(appBarButtonSearch);

            // Create a new menu item with the localized string from AppResources.
            ApplicationBarMenuItem appBarMenuItemPeople =
                new ApplicationBarMenuItem(AppResources.People);
            ApplicationBar.MenuItems.Add(appBarMenuItemPeople);

            ApplicationBarMenuItem appBarMenuItemTimeLine =
                new ApplicationBarMenuItem(AppResources.TimeLine);
            appBarMenuItemTimeLine.IsEnabled = false;
            ApplicationBar.MenuItems.Add(appBarMenuItemTimeLine);

            ApplicationBarMenuItem appBarMenuItemMe =
                new ApplicationBarMenuItem(AppResources.Me);
            appBarMenuItemMe.Click += appBarMenuItemMe_Click;
            ApplicationBar.MenuItems.Add(appBarMenuItemMe);
        }

        private void SynchronizeShots()
        {
            try
            {
                App.UpdateServices(Bagdad.Utils.Constants.ST_DOWNLOAD_ONLY, Utils.ServiceCommunication.enumSynchroTables.SHOTS);
                timer.Interval = new TimeSpan(0, 0, 0, 1);
                timer.Start();
            }
            catch (Exception e)
            {
                System.Diagnostics.Debug.WriteLine("E R R O R :  Timeline.xaml.cs - RefreshShots: " + e.Message);
            }
        }

        private async void RefreshData()
        {
            try
            {

                App.ShotsVM.Shots.Clear();
                await App.ShotsVM.LoadData();

                if (App.ShotsVM.Shots.Count > 0) NoShootsAdvice.Visibility = System.Windows.Visibility.Collapsed;
                else NoShootsAdvice.Visibility = System.Windows.Visibility.Visible;
                timer.Interval = new TimeSpan(0, 0, 0, 10);
                timer.Start();
            }
            catch (Exception e)
            {
                System.Diagnostics.Debug.WriteLine("E R R O R :  Timeline.xaml.cs - RefreshData: " + e.Message);
            }
        }

        private int extraCharacters()
        {
            int extra = 140; //TODO: GET THIS VAR FROM DB SYNCHRONIZED WITH SERVER

            extra = extra - newShot.Text.Count();

            return extra;
        }

        #region GESTURE EVENTS

        private void appBarWatchingButton_Click(object sender, EventArgs e)
        {
            MessageBox.Show("I'm Watching!");
        }

        private void appBarMenuItemMe_Click(object sender, EventArgs e)
        {
            NavigationService.Navigate(new Uri("/Me.xaml", UriKind.Relative));
        }

        private void appBarShootButton_Click(object sender, EventArgs e)
        {
            timer.Stop();
            extraChars.Visibility = System.Windows.Visibility.Collapsed;
            App.ShotsVM.SendShot(newShot.Text);
            timer.Interval = new TimeSpan(0, 0, 0, 1);
            extraChars.Text = "140";
            newShot.Text = "";
            Focus();

            if (NoShootsAdvice.Visibility == System.Windows.Visibility.Visible)
            {
                NoShootsAdvice.Visibility = System.Windows.Visibility.Collapsed;
            }
            timer.Start();
        }

        private void ChatBubbleTextBox_TextChanged(object sender, TextChangedEventArgs e)
        {
            if (!newShot.Text.Equals(""))
            {
                appBarButtonShot.IsEnabled = true;

                if (extraCharacters() >= 0) extraChars.Text = extraCharacters().ToString();
                else
                {
                    newShot.Text = newShot.Text.Substring(0, 140); //TODO: GET THIS VAR FROM DB SYNCHRONIZED WITH SERVER
                    newShot.Select(140, 0);
                }

            }
            else
            {
                appBarButtonShot.IsEnabled = false;
                extraChars.Text = extraCharacters().ToString();
            }
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
            //TODO: DELETE THIS: JUST TRYING THE IMAGEMANAGER SOURCE
            //UserImageManager im = new UserImageManager();
            //imageToShow.Source = im.GetUserImage(2);
            MessageBox.Show("Info");
        }

        private void Shot_Tap(object sender, System.Windows.Input.GestureEventArgs e)
        {

            int shotId = ((Bagdad.ViewModels.ShotViewModel)MyShots.SelectedItem).shotId;
            MyShots.SelectedIndex = -1;
            MessageBox.Show("Shot #" + shotId);
        }

        private void ShotUserProfile_Tap(object sender, System.Windows.Input.GestureEventArgs e)
        {
            int shotUserId = ((Bagdad.ViewModels.ShotViewModel)MyShots.SelectedItem).shotUserId;
            NavigationService.Navigate(new Uri("/Me.xaml?idUser=" + shotUserId, UriKind.Relative));
        }
        #endregion

        int charge = 0;
        int scrollToChargue = 0;
        ShotsViewModel svm = new ShotsViewModel();

        private async void MyShots_MouseEnter(object sender, System.Windows.Input.MouseEventArgs e)
        {
            progress.IsVisible = true;

            ListBoxAutomationPeer svAutomation = (ListBoxAutomationPeer)ScrollViewerAutomationPeer.CreatePeerForElement(MyShots);
            // not feeling creative with my var names today...
            IScrollProvider scrollInterface = (IScrollProvider)svAutomation.GetPattern(PatternInterface.Scroll);

            if (MyShots.Items.Count() != 0)
            {
                scrollToChargue = 100 - (15 * 100 / MyShots.Items.Count());
            }

            if (scrollInterface.VerticalScrollPercent >= scrollToChargue)
            {
                //Here is the place to call to older Shots
                if (!endOfLocalList)
                {
                    charge = await svm.LoadOtherShots(offset);

                    offset += Constants.SERCOM_PARAM_TIME_LINE_OFFSET_PAG;

                    if (charge == 0)
                    {
                        offset = 0;
                        endOfLocalList = true;
                    }
                }
                else if (!endOfList)
                {
                    charge = await svm.LoadOlderShots(offset);

                    offset += Constants.SERCOM_PARAM_TIME_LINE_OFFSET_PAG;

                    //if there is no more shots, don't need to charge it again
                    if (charge == 0)
                    {
                        endOfList = true;
                    }
                }
                
            }
            progress.IsVisible = false;
        }

        /// <summary>
        /// Evento del timer
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        void timer_Tick(object sender, EventArgs e)
        {
            if (!App.isSynchroRunning())
            {
                timer.Stop();
                if (timer.Interval.Equals(new TimeSpan(0, 0, 0, 10))) SynchronizeShots();
                else progress.IsVisible = false;
                RefreshData();
            }
        }

        private void Rectangle_Tap(object sender, System.Windows.Input.GestureEventArgs e)
        {
            MyShots.ScrollIntoView(MyShots.Items.First());
        }       
        
    }
}