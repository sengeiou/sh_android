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
using System.Windows.Media.Animation;
using Bagdad.Models;

namespace Bagdad
{
    public partial class TimeLine : PhoneApplicationPage
    {

        ApplicationBarIconButton appBarButtonShot;
        private DispatcherTimer timer;
        private DispatcherTimer timerToLoad;
        public ProgressIndicator progress;
        bool endOfLocalList = false;
        bool endOfList = false;
        bool newShotFocused = false;
        private int offset = Constants.SERCOM_PARAM_TIME_LINE_FIRST_CHARGE;
        PageOrientation lastOrientation;
        private double lastScrollPercent = 0;
        private bool synchroWorking = false;
        private int totalShots = 0;
        private int extraShots = 0;

        ListBoxAutomationPeer svAutomation;
        // not feeling creative with my var names today...
        IScrollProvider scrollInterface;
        
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

            timerToLoad = new DispatcherTimer();
            timerToLoad.Tick += timerToLoad_Tick;
            timerToLoad.Interval = new TimeSpan(0, 0, 0, 1);

            lastOrientation = this.Orientation;

        }

        protected async override void OnNavigatedTo(NavigationEventArgs e)
        {
            try
            {
                Login login = new Login();
                var synchroLogin = await login.isUserAlreadyLoged();
                if (synchroLogin)
                {
                    timer.Start();
                    System.Diagnostics.Debug.WriteLine("· · · · · · · · timer start (10 seg) (OnNavigatedTo)");
                }


                if (!App.ShotsVM.IsDataLoaded)
                {
                    App.ShotsVM.shotsList.Clear();
                    await App.ShotsVM.LoadData();
                    if (App.ShotsVM.shotsList.Count > 0) NoShootsAdvice.Visibility = System.Windows.Visibility.Collapsed;
                    else NoShootsAdvice.Visibility = System.Windows.Visibility.Visible;
                }
                else
                {
                    if (App.ShotsVM.shotsList.Count == 0) NoShootsAdvice.Visibility = System.Windows.Visibility.Visible;
                    else
                    {
                        myShots.ItemsSource = null;
                        myShots.ItemsSource = App.ShotsVM.shotsList;
                    }
                }

                totalShots = myShots.Items.Count;
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
            appBarMenuItemPeople.Click += appBarMenuItemPeople_Click;
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
                App.UpdateServices(ServiceCommunication.enumTypeSynchro.ST_DOWNLOAD_ONLY, ServiceCommunication.enumSynchroTables.SHOTS);
            }
            catch (Exception e)
            {
                System.Diagnostics.Debug.WriteLine("E R R O R :  Timeline.xaml.cs - RefreshShots: " + e.Message);
            }
        }

        private async void SendShot()
        {
            try
            {
                myShots.SelectedItem = myShots.Items.First();
                myShots.ScrollIntoView(myShots.Items.First());
                progress.IsVisible = true;
                if (!App.isInternetAvailable)
                {
                    if (App.IsAirplaneMode()) MessageBox.Show(AppResources.AirplaneMode, AppResources.CanConnect, MessageBoxButton.OK);
                    else MessageBox.Show(AppResources.NoInternetConnection, AppResources.CanConnect, MessageBoxButton.OK);
                    progress.IsVisible = false;
                }
                else
                {
                    timer.Stop();
                    System.Diagnostics.Debug.WriteLine("· · · · · · · · timer stop (SendShot)");
                    newShot.IsEnabled = false;
                    appBarButtonShot.IsEnabled = false;
                    extraChars.Visibility = System.Windows.Visibility.Collapsed;
                    var res = await App.ShotsVM.SendShot(newShot.Text);
                    if (res == 2)
                    {
                        MessageBox.Show(AppResources.TimeOut, AppResources.ShotNotPosted, MessageBoxButton.OK);
                        progress.IsVisible = false;
                    }
                    else if (res != 1)
                    {
                        MessageBox.Show(AppResources.ShootRepeated, AppResources.ShotNotPosted, MessageBoxButton.OK);
                        progress.IsVisible = false;
                    }
                    else
                    {
                        if (!timerToLoad.IsEnabled)
                        {
                            timerToLoad.Interval = new TimeSpan(0, 0, 0, 1);
                            timerToLoad.Start();
                        }
                        extraChars.Text = "140";
                        newShot.Text = "";
                        newShot.Hint = AppResources.WhatsUp;
                        Focus();
                        newShotFocused = false;
                        totalShots++;
                    }
                    if (!timer.IsEnabled) timer.Start();
                    newShot.IsEnabled = true;
                    appBarButtonShot.IsEnabled = true;
                    if (NoShootsAdvice.Visibility == System.Windows.Visibility.Visible) NoShootsAdvice.Visibility = System.Windows.Visibility.Collapsed;
                }
            }
            catch (Exception e)
            {
                System.Diagnostics.Debug.WriteLine("E R R O R :  Timeline.xaml.cs - SendShot: " + e.Message);
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
            timer.Stop();
            timerToLoad.Stop();
        }

        private void appBarMenuItemPeople_Click(object sender, EventArgs e)
        {
            NavigationService.Navigate(new Uri("/People.xaml", UriKind.Relative));
            timer.Stop();
            timerToLoad.Stop();
        }

        private void appBarShootButton_Click(object sender, EventArgs e)
        {
            SendShot();
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
            newShot.Hint = "";
            newShotFocused = true;
        }

        private void newShot_LostFocus(object sender, RoutedEventArgs e)
        {
            if (newShot.Text.Equals(""))
            {
                extraChars.Text = "140";
                extraChars.Visibility = System.Windows.Visibility.Collapsed;
                newShot.Hint = AppResources.WhatsUp;
            }
            newShotFocused = false;
        }

        private void StartShooting_Tap(object sender, System.Windows.Input.GestureEventArgs e)
        {
            newShot.Focus();
        }

        private void Info_Tap(object sender, System.Windows.Input.GestureEventArgs e)
        {
            MessageBox.Show("Info");
        }

        private void Shot_Tap(object sender, System.Windows.Input.GestureEventArgs e)
        {
            if (!newShotFocused)
            {
                int shotId = ((Bagdad.ViewModels.ShotViewModel)myShots.SelectedItem).shotId;
                myShots.SelectedIndex = -1;
                MessageBox.Show("Shot #" + shotId);
            }
        }

        private void ShotUserProfile_Tap(object sender, System.Windows.Input.GestureEventArgs e)
        {
            int shotUserId = ((Bagdad.ViewModels.ShotViewModel)myShots.SelectedItem).shotUserId;
            NavigationService.Navigate(new Uri("/Me.xaml?idUser=" + shotUserId, UriKind.Relative));
            timer.Stop();
            timerToLoad.Stop();
        }
        #endregion

        int charge = 0;
        double scrollToChargue = 0;
        ShotsViewModel svm = new ShotsViewModel();

        private void MyShots_MouseEnter(object sender, System.Windows.Input.MouseEventArgs e)
        {
            svAutomation = (ListBoxAutomationPeer)ScrollViewerAutomationPeer.CreatePeerForElement(myShots);
            scrollInterface = (IScrollProvider)svAutomation.GetPattern(PatternInterface.Scroll);

            lastScrollPercent = scrollInterface.VerticalScrollPercent;
        }

        async void timerToLoad_Tick(object sender, EventArgs e)
        {
            var hasNewShot = await App.ShotsVM.UpdateShotsOnScreen();
            if(hasNewShot == 1)
            {
                if (progress.IsVisible) progress.IsVisible = false;
                timerToLoad.Stop();
                timerToLoad.Interval = new TimeSpan(0, 0, 0, 1);
                System.Diagnostics.Debug.WriteLine("· · · · · · · · timerToLoad Stop  (TimerToLoadTick)");
            }
        }

        /// <summary>
        /// Evento del timer
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        async void timer_Tick(object sender, EventArgs e)
        {
            if (!App.isSynchroRunning())
            {
                SynchronizeShots();

                if (!timerToLoad.IsEnabled)
                {
                    System.Diagnostics.Debug.WriteLine("· · · · · · · · timerToLoad start (1 seg) (TimerTick)");
                    timerToLoad.Start();
                }
                if (!timer.IsEnabled)
                {
                    timer.Start();
                    System.Diagnostics.Debug.WriteLine("· · · · · · · · timer start (10 seg) (TimerTick)");
                }
            }

            extraShots = myShots.Items.Count - totalShots;
            svAutomation = (ListBoxAutomationPeer)ScrollViewerAutomationPeer.CreatePeerForElement(myShots);
            scrollInterface = (IScrollProvider)svAutomation.GetPattern(PatternInterface.Scroll);

            //////////if (extraShots > 0 && scrollInterface.VerticalScrollPercent > 0)
            //////////{
            //////////    if (extraShots > 10) GoToTopText.Text = "+10";
            //////////    else GoToTopText.Text = extraShots.ToString();
            //////////    GoToTopText.FontSize = (double)Application.Current.Resources["PhoneFontSizeMedium"];
            //////////    GoToTopText.Foreground = Application.Current.Resources["PhoneAccentBrush"] as SolidColorBrush;
            //////////    GoToTopEllipse.Stroke = Application.Current.Resources["PhoneAccentBrush"] as SolidColorBrush;

            //////////    GoToTopGrid.Visibility = System.Windows.Visibility.Visible;
            //////////}
            //////////else
            //////////{
            //////////    totalShots = myShots.Items.Count;
            //////////}
        }

        private void Rectangle_Tap(object sender, System.Windows.Input.GestureEventArgs e)
        {
            myShots.ScrollIntoView(myShots.Items.First());
        }

        private void phoneApplicationPage_OrientationChanged(object sender, OrientationChangedEventArgs e)
        {
            LayoutRoot.InvalidateMeasure();
            titleGrid.Width = phoneApplicationPage.ActualWidth;

            myShots.ItemsSource = null;
            myShots.ItemsSource = App.ShotsVM.shotsList;
        }

        ////////////////////private void GoToTopGrid_Tap(object sender, System.Windows.Input.GestureEventArgs e)
        ////////////////////{
        ////////////////////    GoToTopButtonAnimation.Begin();
        ////////////////////    GoToTopButtonAnimation.Completed += GoTopAndResetButton;
        ////////////////////}

        ////////////////////private void GoTopAndResetButton(object sender, EventArgs e)
        ////////////////////{
        ////////////////////    myShots.ScrollIntoView(myShots.Items.First());
        ////////////////////    GoToTopText.Text = "↑";
        ////////////////////    GoToTopText.Foreground = Application.Current.Resources["PhoneDisabledBrush"] as SolidColorBrush;
        ////////////////////    GoToTopText.FontSize = (double)Application.Current.Resources["PhoneFontSizeExtraLarge"];
        ////////////////////    GoToTopEllipse.Stroke = Application.Current.Resources["PhoneDisabledBrush"] as SolidColorBrush;
        ////////////////////    extraShots = 0;
        ////////////////////    totalShots = myShots.Items.Count;
        ////////////////////    lastScrollPercent = 0;
        ////////////////////}

        private async void myShots_MouseMove(object sender, System.Windows.Input.MouseEventArgs e)
        {
            progress.IsVisible = true;

            svAutomation = (ListBoxAutomationPeer)ScrollViewerAutomationPeer.CreatePeerForElement(myShots);
            scrollInterface = (IScrollProvider)svAutomation.GetPattern(PatternInterface.Scroll);

            ////////////if (scrollInterface.VerticalScrollPercent != 0 && scrollInterface.VerticalScrollPercent < lastScrollPercent)
            ////////////{
            ////////////    GoToTopGrid.Visibility = System.Windows.Visibility.Visible;
            ////////////}
            ////////////else if (scrollInterface.VerticalScrollPercent != 0 && scrollInterface.VerticalScrollPercent > lastScrollPercent && extraShots == 0)
            ////////////{
            ////////////    GoToTopGrid.Visibility = System.Windows.Visibility.Collapsed;
            ////////////}

            //pagination
            if (myShots.Items.Count() != 0)
            {
                scrollToChargue = 100 - (15 * 100 / myShots.Items.Count());
            }

            //////////if (scrollInterface.VerticalScrollPercent == 0) GoToTopGrid.Visibility = System.Windows.Visibility.Collapsed;

            if (scrollInterface.VerticalScrollPercent != 0 && scrollInterface.VerticalScrollPercent > scrollToChargue && !synchroWorking)
            {
                synchroWorking = true;
                extraShots = myShots.Items.Count - totalShots;

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
                await App.ShotsVM.UpdateShotsOnScreenFromScroll();

                synchroWorking = false;
                totalShots = myShots.Items.Count - extraShots;
            }
            progress.IsVisible = false;
        }
        
    }
}