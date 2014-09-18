using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bagdad.Utils
{
    class SQLQuerys
    {

        #region USER

        public const String InsertLoginData = "INSERT INTO User (idUser, idFavouriteTeam, sessionToken, userName, email, name, photo, csys_birth, csys_modified, csys_revision, csys_deleted, csys_synchronized) VALUES (@idUser, @idFavouriteTeam, @sessionToken, @userName, @email, @name, @photo, @csys_birth, @csys_modified, @csys_revision, @csys_deleted, @csys_synchronized)";

        public const String GetSessionToken = "SELECT sessionToken FROM User WHERE sessionToken IS NOT NULL";

        #endregion
    }
}
