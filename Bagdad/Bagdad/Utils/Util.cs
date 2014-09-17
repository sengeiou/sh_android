using System;
using System.Collections.Generic;
using System.Linq;
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
    }
}
