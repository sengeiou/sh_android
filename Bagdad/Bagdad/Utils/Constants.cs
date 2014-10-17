using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bagdad.Utils
{
    class Constants
    {

        #region SERVICE_COMMUNICATION
        //LIMIT SHOTS TO STORE IN DB
        public const int SHOTS_LIMIT = 1000;

        //OPERATIONS
        public const String SERCOM_OP_MANUAL_JSON_REQUEST = "#OMJR";
        public const String SERCOM_OP_RETRIEVE = "retrieve";
        public const String SERCOM_OP_RETRIEVE_NO_AUTO_OFFSET = "retrieveNoAutoOffset";
        public const String SERCOM_OP_UPDATECREATE = "CreateUpdate";
        public const String SERCOM_OP_CREATE = "Create";
        public const String SERCOM_OP_DELETE = "Delete";

        //TABLES
        public const String SERCOM_TB_LOGIN = "Login";
        public const String SERCOM_TB_USER = "User";
        public const String SERCOM_TB_DEVICE = "Device";
        public const String SERCOM_TB_FOLLOW = "Follow";
        public const String SERCOM_TB_SHOT = "Shot";
        public const String SERCOM_TB_OLD_SHOTS = "OldShots";

        //PARAMS
        public const int SERCOM_PARAM_OFFSET_PAG = 100;
        public const int SERCOM_PARAM_TIME_LINE_OFFSET_PAG = 20;
        public const int SERCOM_PARAM_TIME_LINE_FIRST_CHARGE = 50;

        #endregion

        public const String CONST_FOLLOWING = "following";
        public const String CONST_FOLLOWERS = "followers";
        public const String CONST_PEOPLE = "people";
    }
}
