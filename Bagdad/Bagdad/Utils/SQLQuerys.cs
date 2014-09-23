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

        public const String InsertUserData = "INSERT INTO User (idUser, idFavouriteTeam, userName, name, photo, csys_birth, csys_modified, csys_revision, csys_deleted, csys_synchronized) VALUES (@idUser, @idFavouriteTeam, @userName, @name, @photo, @csys_birth, @csys_modified, @csys_revision, @csys_deleted, @csys_synchronized)";

        public const String GetSessionToken = "SELECT sessionToken, idUser FROM User WHERE sessionToken IS NOT NULL";

        public const String hasLoggedSQL = "SELECT idUser FROM User WHERE sessionToken IS NOT NULL";

        #endregion

        #region SHOT

        public const String InsertShotData = "INSERT INTO Shot (idShot, idUser, comment, csys_birth, csys_modified, csys_revision, csys_deleted, csys_synchronized) VALUES (@idShot, @idUser, @comment, @csys_birth, @csys_modified, @csys_revision, @csys_deleted, @csys_synchronized)";

        #endregion

        #region FOLLOW

        public const String InsertFollowData = "INSERT INTO Follow (idUser, idUserFollowed, csys_birth, csys_modified, csys_revision, csys_deleted, csys_synchronized) VALUES (@idUser, @idUserFollowed, @csys_birth, @csys_modified, @csys_revision, @csys_deleted, @csys_synchronized)";
            

        #endregion

        #region Generic

        public const String getMaxModificationDateOf = "SELECT maxTimestamp FROM Synchro WHERE Entity = @Entity";

        public const String updateModificationDateOf = "UPDATE Synchro SET LastModified = @maxTimestamp WHERE Entity = @Entity";

        public const String GetSynchronizationTables = "SELECT `order`, entity, frequency, maxTimestamp, minTimestamp, direction, maxRows, minRows  FROM Synchro ORDER BY `order` ASC";

        #endregion
    }
}
