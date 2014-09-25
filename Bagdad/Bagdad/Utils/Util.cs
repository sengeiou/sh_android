using Bagdad.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;

namespace Bagdad.Utils
{
    class Util
    {
        public static DateTime FromUnixTime(String unixTime)
        {
            var epoch = new DateTime(1970, 1, 1, 0, 0, 0, DateTimeKind.Utc);
            return epoch.AddMilliseconds(Convert.ToDouble(unixTime));
        }

        public bool isAnEmail(String email)
        {
            // Return true if email is in valid e-mail format.
            return Regex.IsMatch(email, @"^([0-9a-zA-Z]([-\.\w]*[0-9a-zA-Z])*@([0-9a-zA-Z][-\w]*[0-9a-zA-Z]\.)+[a-zA-Z]{2,9})$");
        }

        public bool isAValidUser(String user)
        {
            // Return true if is a valid userName.
            return Regex.IsMatch(user, @"^([a-zA-Z0-9]{3,20})$");
        }

        public bool isAValidPassword(String password)
        {
            // Return true if is a valid password.
            return Regex.IsMatch(password, @"^([a-zA-Z0-9._@$%!]{6,20})$");
        }

        public static string encryptPassword(string pwd)
        {
            try
            {
            string passwordEncrypted = MD5.GetMd5String(CalculateSHA1(pwd));
            return passwordEncrypted.Substring(0, 20);
            }
            catch (System.Security.SecurityException e)
            {
                System.Diagnostics.Debug.WriteLine("E R R O R :  encryptPassword: " + e.Message);
                throw e;
            }
        }

        private static string CalculateSHA1(string text)
        {
            try
            {
                SHA1Managed s = new SHA1Managed();
                UTF8Encoding enc = new UTF8Encoding();
                s.ComputeHash(enc.GetBytes(text.ToCharArray()));
                return BitConverter.ToString(s.Hash).Replace("-", "").ToLowerInvariant();
            }
            catch (System.Security.SecurityException e)
            {
                System.Diagnostics.Debug.WriteLine("E R R O R :  CalculateSHA1: " + e.Message);
                throw e;
            }
        }

        public async Task<bool> isUserAlreadyLoged()
        {
            try
            {
                User u = new User();
                String sessionToken = await u.getSessionToken();

                if (sessionToken.Equals("")) return false;
                else return true;
            }
            catch (System.Security.SecurityException e)
            {
                System.Diagnostics.Debug.WriteLine("E R R O R :  isUserAlreadyLoged: " + e.Message);
                throw e;
            }
        }

        public async Task<bool> LogInByEmail(String email, String password)
        {
            try
            {
                ServiceCommunication sercom = new ServiceCommunication();
                await sercom.doRequest(Constants.SERCOM_OP_RETRIEVE, Constants.SERCOM_TB_LOGIN, "\"key\":{\"email\": \"" + email + "\",\"password\" : \"" + encryptPassword(password) + "\"}", 0);
                return true;
            }
            catch (Exception e)
            {
                System.Diagnostics.Debug.WriteLine("E R R O R :  isUserAlreadyLoged: " + e.Message);
                throw e;
            }
        }

        public async Task<bool> LogInByUserName(String userName, String password)
        {
            try
            {
                ServiceCommunication sercom = new ServiceCommunication();
                await sercom.doRequest(Constants.SERCOM_OP_RETRIEVE, Constants.SERCOM_TB_LOGIN, "\"key\":{\"userName\": \"" + userName + "\",\"password\" : \"" + encryptPassword(password) + "\"}", 0);
                return true;
            }
            catch (Exception e)
            {
                System.Diagnostics.Debug.WriteLine("E R R O R :  isUserAlreadyLoged: " + e.Message);
                throw e;
            }
        }

    }
}
