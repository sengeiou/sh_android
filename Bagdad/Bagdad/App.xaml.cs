using System;
using System.Diagnostics;
using System.Resources;
using System.Windows;
using System.Windows.Markup;
using System.Windows.Navigation;
using Microsoft.Phone.Controls;
using Microsoft.Phone.Shell;
using Bagdad.Resources;
using System.Collections.Generic;
using Microsoft.Phone.Net.NetworkInformation;
using Windows.Networking.Connectivity;
using System.Threading.Tasks;
using SQLiteWinRT;
using Windows.Storage;
using System.Threading;
using Bagdad.Utils;
using System.ComponentModel;
using Bagdad.ViewModels;
using Windows.Phone.Devices.Notification;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using Coding4Fun.Toolkit.Controls;
using Microsoft.Phone.Notification;
using Bagdad.Models;

namespace Bagdad
{
    public partial class App : Application
    {

        #region VARIABLES

        public static int ID_DEVICE = 0;
        public static int ID_USER = 0;
        public static double TIME_LAPSE = -7167518.431640625;
        public const int PLATFORM_ID = 2;
        public static bool hasDeletedMaxShots = false;

        #endregion

        /// <summary>
        /// Proporcionar acceso sencillo al marco raíz de la aplicación telefónica.
        /// </summary>
        /// <returns>Marco raíz de la aplicación telefónica.</returns>
        public static PhoneApplicationFrame RootFrame { get; private set; }

        /// <summary>
        /// Constructor para el objeto Application.
        /// </summary>
        public App()
        {
            //Initialize the Network Status Control
            NetworkControl();

            // Controlador global para excepciones no detectadas.
            UnhandledException += Application_UnhandledException;

            //Initialize Push Notification Receiver
            setNotificationChannel();

            // Inicialización XAML estándar
            InitializeComponent();

            // Inicialización especifica del teléfono
            InitializePhoneApplication();

            // Inicialización del idioma
            InitializeLanguage();

            // Mostrar información de generación de perfiles gráfica durante la depuración.
            if (Debugger.IsAttached)
            {
                // Mostrar los contadores de velocidad de marcos actual.
                Application.Current.Host.Settings.EnableFrameRateCounter = true;

                // Mostrar las áreas de la aplicación que se están volviendo a dibujar en cada marco.
                //Application.Current.Host.Settings.EnableRedrawRegions = true;

                // Habilitar el modo de visualización de análisis de no producción,
                // que muestra áreas de una página que se entregan a la GPU con una superposición coloreada.
                //Application.Current.Host.Settings.EnableCacheVisualization = true;

                // Impedir que la pantalla se apague mientras se realiza la depuración deshabilitando
                // la detección de inactividad de la aplicación.
                // Precaución: solo debe usarse en modo de depuración. Las aplicaciones que deshabiliten la detección de inactividad del usuario seguirán en ejecución
                // y consumirán energía de la batería cuando el usuario no esté usando el teléfono.
                PhoneApplicationService.Current.UserIdleDetectionMode = IdleDetectionMode.Disabled;
            }

        }


        #region Inicialización de la aplicación telefónica

        // Código para ejecutar cuando la aplicación se inicia (p.ej. a partir de Inicio)
        // Este código no se ejecutará cuando la aplicación se reactive
        private async void Application_Launching(object sender, LaunchingEventArgs e)
        {
            await PrepareDB.initializeDatabase();
            InitializeDB();
            Setup();
        }

        // Código para ejecutar cuando la aplicación se activa (se trae a primer plano)
        // Este código no se ejecutará cuando la aplicación se inicie por primera vez
        private void Application_Activated(object sender, ActivatedEventArgs e)
        {
        }

        // Código para ejecutar cuando la aplicación se desactiva (se envía a segundo plano)
        // Este código no se ejecutará cuando la aplicación se cierre
        private void Application_Deactivated(object sender, DeactivatedEventArgs e)
        {
        }

        // Código para ejecutar cuando la aplicación se cierra (p.ej., al hacer clic en Atrás)
        // Este código no se ejecutará cuando la aplicación se desactive
        private void Application_Closing(object sender, ClosingEventArgs e)
        {
        }

        // Código para ejecutar si hay un error de navegación
        private void RootFrame_NavigationFailed(object sender, NavigationFailedEventArgs e)
        {
            if (Debugger.IsAttached)
            {
                // Ha habido un error de navegación; interrumpir el depurador
                Debugger.Break();
            }
        }

        // Código para ejecutar en excepciones no controladas
        private void Application_UnhandledException(object sender, ApplicationUnhandledExceptionEventArgs e)
        {
            if (Debugger.IsAttached)
            {
                // Se ha producido una excepción no controlada; interrumpir el depurador
                Debugger.Break();
            }
        }

        // Evitar inicialización doble
        private bool phoneApplicationInitialized = false;

        // No agregar ningún código adicional a este método
        private void InitializePhoneApplication()
        {
            if (phoneApplicationInitialized)
                return;

            // Crear el marco pero no establecerlo como RootVisual todavía; esto permite que
            // la pantalla de presentación permanezca activa hasta que la aplicación esté lista para la presentación.
            RootFrame = new TransitionFrame();
            RootFrame.Navigated += CompleteInitializePhoneApplication;

            // Controlar errores de navegación
            RootFrame.NavigationFailed += RootFrame_NavigationFailed;

            // Controlar solicitudes de restablecimiento para borrar la pila de retroceso
            RootFrame.Navigated += CheckForResetNavigation;

            // Asegurarse de que no volvemos a inicializar
            phoneApplicationInitialized = true;
        }

        // No agregar ningún código adicional a este método
        private void CompleteInitializePhoneApplication(object sender, NavigationEventArgs e)
        {
            // Establecer el objeto visual raíz para permitir que la aplicación se presente
            if (RootVisual != RootFrame)
                RootVisual = RootFrame;

            // Quitar este controlador porque ya no es necesario
            RootFrame.Navigated -= CompleteInitializePhoneApplication;
        }

        private void CheckForResetNavigation(object sender, NavigationEventArgs e)
        {
            // Si la aplicación ha recibido una navegación 'reset', tenemos que comprobarlo
            // en la siguiente navegación para ver si se debe restablecer la pila de páginas
            if (e.NavigationMode == NavigationMode.Reset)
            {
                RootFrame.Navigated += ClearBackStackAfterReset;
                //UpdateServices(Constants.ST_FULL_SYNCHRO, ServiceCommunication.enumSynchroTables.FULL);
            }
        }

        private void ClearBackStackAfterReset(object sender, NavigationEventArgs e)
        {
            // Anular registro del evento para que no se vuelva a llamar
            RootFrame.Navigated -= ClearBackStackAfterReset;

            // Borrar solo la pila de navegaciones 'new' (hacia delante) y 'refresh'
            if (e.NavigationMode != NavigationMode.New && e.NavigationMode != NavigationMode.Refresh)
                return;

            // Por coherencia de la IU, borrar toda la pila de páginas
            while (RootFrame.RemoveBackEntry() != null)
            {
                ; // no hacer nada
            }
        }

        private async void Setup()
        {
            try
            {
                Util util = new Util();

                if(isInternetAvailable) TIME_LAPSE = await util.CalculateTimeLapse();

                var synchroLogin = await util.isUserAlreadyLoged();
                if (synchroLogin)
                {
                    UpdateServices(ServiceCommunication.enumTypeSynchro.ST_FULL_SYNCHRO, ServiceCommunication.enumSynchroTables.FULL);
                }
                else
                {
                    (Application.Current.RootVisual as PhoneApplicationFrame).Navigate(new Uri("/Start.xaml", UriKind.Relative));
                }

            }
            catch (Exception e)
            {
                Debug.WriteLine("E R R O R: App.xaml.cs - Setup " + e.Message);
            }
        }

        #endregion
        
        #region CONNECTIVITY_INFO

        /// <summary>
        /// It will have only 2 positions:
        /// 0: Connection Type Name.
        /// 1: If there is connection avalible: True or False
        /// </summary>
        private List<String> Connection;
        
        //Value of current connection status
        public static bool isInternetAvailable;

        //Print status on Console
        private void ShowConnectionInfo()
        {
            Debug.WriteLine("\n Conexión con " + Connection[0] + ": " + Connection[1] + "\n");
        }

        //Prepares the app to get control over network status
        private void NetworkControl()
        {
            isInternetAvailable = NetworkInterface.GetIsNetworkAvailable();

            Connection = new List<String>() { NetworkInterface.NetworkInterfaceType.ToString(), NetworkInterface.GetIsNetworkAvailable().ToString() };

            NetworkInformation.NetworkStatusChanged += NetworkInformation_NetworkStatusChanged;

            ShowConnectionInfo();
        }

        //Update the curren status of the network
        void NetworkInformation_NetworkStatusChanged(object sender)
        {
            Connection = new List<String>() { NetworkInterface.NetworkInterfaceType.ToString(), NetworkInterface.GetIsNetworkAvailable().ToString() };
            if (Connection != null)
                if (Connection[1] != null)
                    if (Connection[1] == "True") isInternetAvailable = true;
                    else if (Connection[1] == "False") isInternetAvailable = false;
            ShowConnectionInfo();
        }


        public static bool IsAirplaneMode()
        {
            bool[] networks = new bool[4] { DeviceNetworkInformation.IsNetworkAvailable, DeviceNetworkInformation.IsCellularDataEnabled, DeviceNetworkInformation.IsCellularDataRoamingEnabled, DeviceNetworkInformation.IsWiFiEnabled };
            if (networks[0] == false && networks[1] == false && networks[3]  == false)
                return true;
            else return false;
        }


        #endregion

        #region SYNCHRO

        //Semaforo para la Synchro
        private static bool isSynchroProcessWorking;
        //Cambios realizados en Sincro
        public static int changesOnSynchro = 0;

        public static bool isSynchroRunning()
        {
            return isSynchroProcessWorking;
        }

        public static void lockSynchro()
        {
            isSynchroProcessWorking = true;
        }

        public static void releaseSynchro()
        {
            isSynchroProcessWorking = false;
        }

        //Llamada a la Synchro
        public static void UpdateServices(ServiceCommunication.enumTypeSynchro _type, ServiceCommunication.enumSynchroTables tablesType)
        {
            ServiceCommunication sc = new ServiceCommunication();
            sc.initTablesToSynchro(tablesType);
            BackgroundWorker bgw = new BackgroundWorker();

            sc.SetSynchroType(_type);

            bgw.DoWork += sc.SynchronizeProcess;
            bgw.RunWorkerAsync();
        }
        #endregion

        #region APP_INFO

        public static string osVersion()
        {
            return Environment.OSVersion.Version.ToString();
        }

        public static string appVersion()
        {
            System.Reflection.Assembly assem = System.Reflection.Assembly.GetExecutingAssembly();
            System.Reflection.AssemblyName assemName = assem.GetName();
            Version vers = assemName.Version;

            return vers.Major.ToString() + "." + vers.Minor.ToString() + "." + vers.Build.ToString() + "." + vers.Revision.ToString();
        }

        public static int appVersionInt()
        {

            System.Reflection.Assembly assem = System.Reflection.Assembly.GetExecutingAssembly();
            System.Reflection.AssemblyName assemName = assem.GetName();
            Version vers = assemName.Version;

            String version = vers.Major.ToString() + "." + vers.Minor.ToString() + "." + vers.Build.ToString();

            String[] array = version.Trim().Split('.');
            Double ver = 0d;
            for (int i = 0; i < array.Length; i++)
            {
                ver += long.Parse(array[i]) * Math.Pow(1000, 2 - i);
            }

            return int.Parse(ver.ToString());

        }

        public static String UDID()
        {
            byte[] myDeviceID = (byte[])Microsoft.Phone.Info.DeviceExtendedProperties.GetValue("DeviceUniqueId");
            return System.Convert.ToBase64String(myDeviceID);
        }

        public static string modelVersion()
        {
            return Microsoft.Phone.Info.DeviceStatus.DeviceManufacturer + " " + Microsoft.Phone.Info.DeviceStatus.DeviceName;
        }

        public static string locale()
        {
            return System.Globalization.CultureInfo.CurrentCulture.Name;
        }

        #endregion

        #region DATA_BASE

        public static Database db;

        public static ManualResetEvent DBLoaded = new ManualResetEvent(false);

        private async void InitializeDB()
        {
            db = new Database(ApplicationData.Current.LocalFolder, "shooter.db");
            await db.OpenAsync();
            DBLoaded.Set();
        }

        public static Task<SQLiteWinRT.Database> GetDatabaseAsync()
        {
            return Task.Run(() =>
            {
                DBLoaded.WaitOne(-1);
                return db;
            });
        }

        #endregion

        #region DATA_VIEW_MODELS

        private static ShotsViewModel shotsViewModel = null;

        public static ShotsViewModel ShotsVM
        {
            get
            {
                if (shotsViewModel == null)
                    shotsViewModel = new ShotsViewModel();

                return shotsViewModel;
            }
        }

        #endregion

        #region SECCIÓN DE PUSHES

        public static String pushToken;

        // Funciones referentes a Notificaciones Toast:
        private void setNotificationChannel()
        {
            try
            {
                /// Holds the push channel that is created or found.
                HttpNotificationChannel pushChannel;

                // The name of our push channel.
                string channelName = "Shootr";

                // Try to find the push channel.
                pushChannel = HttpNotificationChannel.Find(channelName);

                // If the channel was not found, then create a new connection to the push service.
                if (pushChannel == null)
                {
                    //pushChannel = new HttpNotificationChannel(channelName, "wp-gm");
                    pushChannel = new HttpNotificationChannel(channelName);

                    // Register for all the events before attempting to open the channel.
                    pushChannel.ChannelUriUpdated += new EventHandler<NotificationChannelUriEventArgs>(PushChannel_ChannelUriUpdated);
                    pushChannel.ErrorOccurred += new EventHandler<NotificationChannelErrorEventArgs>(PushChannel_ErrorOccurred);

                    // Register for this notification only if you need to receive the notifications while your application is running.
                    pushChannel.ShellToastNotificationReceived += new EventHandler<NotificationEventArgs>(PushChannel_ShellToastNotificationReceived);

                    pushChannel.Open();

                    // Bind this new channel for toast events.
                    pushChannel.BindToShellToast();

                }
                else
                {
                    // The channel was already open, so just register for all the events.
                    pushChannel.ChannelUriUpdated += new EventHandler<NotificationChannelUriEventArgs>(PushChannel_ChannelUriUpdated);
                    pushChannel.ErrorOccurred += new EventHandler<NotificationChannelErrorEventArgs>(PushChannel_ErrorOccurred);

                    // Register for this notification only if you need to receive the notifications while your application is running.
                    pushChannel.ShellToastNotificationReceived += new EventHandler<NotificationEventArgs>(PushChannel_ShellToastNotificationReceived);
                    pushToken = pushChannel.ChannelUri.ToString();
                    // Display the URI for testing purposes. Normally, the URI would be passed back to your web service at this point.
                    if (pushChannel.ChannelUri != null) System.Diagnostics.Debug.WriteLine(" - - - - Nuevo Token: " + pushChannel.ChannelUri.ToString());
                }
            }
            catch (Exception e)
            {
                Debug.WriteLine("E R R O R : setNotificationChannel " + e.Message);
            }
        }

        void PushChannel_ChannelUriUpdated(object sender, NotificationChannelUriEventArgs e)
        {

            Deployment.Current.Dispatcher.BeginInvoke(() =>
            {
                // Display the new URI for testing purposes.   Normally, the URI would be passed back to your web service at this point.
                pushToken = e.ChannelUri.ToString();
                System.Diagnostics.Debug.WriteLine(" - - - - Token: " + pushToken);
                UpdateTokenOnServer();
            });
        }

        void PushChannel_ErrorOccurred(object sender, NotificationChannelErrorEventArgs e)
        {
            // Error handling logic for your particular application would be here.
            Deployment.Current.Dispatcher.BeginInvoke(() =>
                System.Diagnostics.Debug.WriteLine(String.Format("A push notification {0} error occurred.  {1} ({2}) {3}",
                    e.ErrorType, e.Message, e.ErrorCode, e.ErrorAdditionalData))
                    );
        }

        //Mostrar Pushes cuando la App está en marcha.
        void PushChannel_ShellToastNotificationReceived(object sender, NotificationEventArgs e)
        {
            Deployment.Current.Dispatcher.BeginInvoke(() =>
            {
                if (!(Application.Current.RootVisual as PhoneApplicationFrame).CurrentSource.OriginalString.Contains("TimeLine.xaml"))
                {
                    ToastPrompt toast = new ToastPrompt();

                    if (e.Collection.ContainsKey("wp:Text1"))
                        toast.Title = e.Collection["wp:Text1"];
                    else
                        toast.Title = "";

                    if (e.Collection.ContainsKey("wp:Text2"))
                        toast.Message = e.Collection["wp:Text2"];
                    else
                        toast.Message = "";

                    if (e.Collection.ContainsKey("wp:Param"))
                        url = e.Collection["wp:Param"];
                    else
                        url = "";


                    System.Diagnostics.Debug.WriteLine("-------------------PUSH APP ABIERTA RECIBIDO----------------: App.xaml.cs - url: " + url);
                    toast.MillisecondsUntilHidden = 6000;

                    toast.ImageSource = new BitmapImage(new Uri("Assets/Tiles/FlipCycleTileSmall.png", UriKind.RelativeOrAbsolute));

                    toast.Stretch = Stretch.Fill;

                    toast.ImageHeight = 21;
                    toast.ImageWidth = 21;

                    toast.FontSize = 15;
                    toast.Tap += goToDetail;
                    //toast.NavigationUri = new Uri(e.Collection["wp:Param"], UriKind.Relative);

                    System.Windows.Thickness margin = new System.Windows.Thickness();

                    margin.Left = 0;
                    margin.Right = 0;
                    margin.Top = -8;
                    margin.Bottom = 0;

                    toast.Margin = margin;

                    toast.Height = 64;

                    toast.Completed += ShowSystemTray;

                    toast.TextWrapping = TextWrapping.NoWrap;

                    toast.Show();
                    //SystemTray.IsVisible = false;

                    //SystemTray.Opacity = 0;

                    //TODO: "Cargando" de sistema
                    //SystemTray.ProgressIndicator = new ProgressIndicator() { Text = "Cargando...", IsIndeterminate = true, IsVisible = true };

                    //testVibrationDevice.Vibrate(TimeSpan.FromSeconds(0.25));
                }
            });
            // Display a dialog of all the fields in the toast.
            //Deployment.Current.Dispatcher.BeginInvoke(() => MessageBox.Show(message.ToString()));

        }

        private void ShowSystemTray(object sender, EventArgs e)
        {
            SystemTray.Opacity = 1;
        }

        private VibrationDevice testVibrationDevice = VibrationDevice.GetDefault();

        private String url;

        private void goToDetail(object sender, EventArgs e)
        {
            testVibrationDevice.Cancel();
            (Application.Current.RootVisual as PhoneApplicationFrame).Navigate(new Uri(url, UriKind.RelativeOrAbsolute));
        }

        private async void UpdateTokenOnServer()
        {
            //we get the params on Local DB, if there are no device information, the same query will create a new one in server.
            Device device = new Device();
            await device.UpdateDeviceToken();
        }

        #endregion

        // Inicializar la fuente y la dirección de flujo de la aplicación según se define en sus cadenas de recursos traducidas.
        //
        // Para asegurarse de que la fuente de la aplicación está alineada con sus idiomas admitidos y que
        // FlowDirection para todos esos idiomas sigue su dirección tradicional, ResourceLanguage
        // y ResourceFlowDirection se debe inicializar en cada archivo resx para que estos valores coincidan con ese
        // referencia cultural del archivo. Por ejemplo:
        //
        // AppResources.es-ES.resx
        //    El valor de ResourceLanguage debe ser "es-ES"
        //    El valor de ResourceFlowDirection debe ser "LeftToRight"
        //
        // AppResources.ar-SA.resx
        //     El valor de ResourceLanguage debe ser "ar-SA"
        //     El valor de ResourceFlowDirection debe ser "RightToLeft"
        //
        // Para obtener más información sobre cómo traducir aplicaciones para Windows Phone, consulta http://go.microsoft.com/fwlink/?LinkId=262072.
        //
        private void InitializeLanguage()
        {
            try
            {
                // Establecer la fuente para que coincida con el idioma definido por
                // Cadena de recursos ResourceLanguage para cada idioma admitido.
                //
                // Recurrir a la fuente del idioma neutro si el idioma
                // del teléfono no se admite.
                //
                // Si se produce un error del compilador, falta ResourceLanguage
                // el archivo de recursos.
                RootFrame.Language = XmlLanguage.GetLanguage(AppResources.ResourceLanguage);

                // Establecer FlowDirection de todos los elementos del marco raíz según
                // en la cadena de recursos ResourceFlowDirection para cada
                // idioma admitido.
                //
                // Si se produce un error del compilador, falta ResourceFlowDirection
                // el archivo de recursos.
                FlowDirection flow = (FlowDirection)Enum.Parse(typeof(FlowDirection), AppResources.ResourceFlowDirection);
                RootFrame.FlowDirection = flow;
            }
            catch
            {
                // Si se detecta aquí una excepción, lo más probable es que se deba a
                // ResourceLanguage no se ha establecido correctamente en un idioma admitido
                // o ResourceFlowDirection se ha establecido en un valor distinto de LeftToRight
                // o RightToLeft.

                if (Debugger.IsAttached)
                {
                    Debugger.Break();
                }

                throw;
            }
        }
    }
}