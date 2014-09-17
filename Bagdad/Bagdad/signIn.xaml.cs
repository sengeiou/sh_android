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

namespace Bagdad
{
    public partial class SignIn : PhoneApplicationPage
    {
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
                    Util util = new Util();
                    ServiceCommunication sercom = new ServiceCommunication();

                    if (util.isAnEmail(txbUser.Text))
                    {
                        //it's an email
                        if (util.isAValidPassword(pbPassword.Password))
                        {
                            //Call by email
                            await sercom.doRequest(Constants.SERCOM_OP_RETRIEVE, Constants.SERCOM_TB_LOGIN, "\"key\":{\"email\": \"" + txbUser.Text + "\",\"password\" : \"" + Util.encryptPassword(pbPassword.Password) + "\"}", 0);
                        }
                        else
                        {
                            //invalid password
                            MessageBox.Show("THE PASSWORD NOT MATCH OR IS INVALID!!");
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
                                await sercom.doRequest(Constants.SERCOM_OP_RETRIEVE, Constants.SERCOM_TB_LOGIN, "\"key\":{\"userName\": \"" + txbUser.Text + "\",\"password\" : \"" + Util.encryptPassword(pbPassword.Password) + "\"}", 0);
                            }
                            else
                            {
                                //invalid password
                                MessageBox.Show("THE PASSWORD NOT MATCH OR IS INVALID!!");
                            }
                        }
                        else
                        {
                            //invalid mail or user
                            MessageBox.Show("INVALID USER OR E-MAIL!!");
                        }
                    }
                }
                else
                {
                    //No Internet Connection Message.
                    MessageBox.Show("THERE IS NO INTERNET CONNECTION!!");
                }
            }
            catch (Exception ef)
            {
                MessageBox.Show(ef.Message);
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