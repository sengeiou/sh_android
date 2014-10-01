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

        public const String InsertUserData = "INSERT OR REPLACE INTO User (idUser, idFavouriteTeam, userName, name, photo, csys_birth, csys_modified, csys_revision, csys_deleted, csys_synchronized) VALUES (@idUser, @idFavouriteTeam, @userName, @name, @photo, @csys_birth, @csys_modified, @csys_revision, @csys_deleted, @csys_synchronized)";

        public const String GetSessionToken = "SELECT sessionToken, idUser FROM User WHERE sessionToken IS NOT NULL";

        public const String hasLoggedSQL = "SELECT idUser FROM User WHERE sessionToken IS NOT NULL";

        public const String GetNameAndURL = "SELECT name, photo FROM User WHERE idUser = @idUser";

        #endregion

        #region SHOT

        public const String InsertShotData = "INSERT OR REPLACE INTO Shot (idShot, idUser, comment, csys_birth, csys_modified, csys_revision, csys_deleted, csys_synchronized) VALUES (@idShot, @idUser, @comment, @csys_birth, @csys_modified, @csys_revision, @csys_deleted, @csys_synchronized)";

        public const String GetTimeLineShots = "SELECT s.idShot, s.idUser, s.comment, u.name, u.photo, s.csys_birth FROM Shot s JOIN User u ON s.idUser = u.idUser JOIN Follow f ON s.idUser = f.idUserFollowed OR s.idUser = f.idUser WHERE f.idUser = @idUser GROUP BY s.idShot ORDER BY s.csys_birth DESC LIMIT @limit;";

        public const String GetTimeLineOtherShots = "SELECT s.idShot, s.idUser, s.comment, u.name, u.photo, s.csys_birth FROM Shot s JOIN User u ON s.idUser = u.idUser JOIN Follow f ON s.idUser = f.idUserFollowed OR s.idUser = f.idUser WHERE f.idUser = @idUser GROUP BY s.idShot ORDER BY s.csys_birth DESC LIMIT @offset, @limit;";

        public const String shotsSynchronized = "UPDATE Shot SET csys_synchronized = @csys_synchronized WHERE idShot = @idShot";

        public const String getOlderShotDate = "SELECT MIN(csys_birth) FROM Shot";

        public const String getShotById = "SELECT idShot FROM Shot WHERE idShot = @idShot";

        #endregion

        #region FOLLOW

        public const String InsertFollowData = "INSERT INTO Follow (idUser, idUserFollowed, csys_birth, csys_modified, csys_revision, csys_deleted, csys_synchronized) VALUES (@idUser, @idUserFollowed, @csys_birth, @csys_modified, @csys_revision, @csys_deleted, @csys_synchronized)";

        public const String SelectIdUserFollowing = "SELECT idUserFollowed from Follow where idUser = @idUser";

        #endregion

        #region Generic

        public const String getMaxModificationDateOf = "SELECT maxTimestamp FROM Synchro WHERE Entity = @Entity";

        public const String updateModificationDateOf = "UPDATE Synchro SET maxTimestamp = @maxTimestamp WHERE Entity = @Entity";

        public const String GetSynchronizationTables = "SELECT `order`, entity, frequency, maxTimestamp, minTimestamp, direction, maxRows, minRows  FROM Synchro ORDER BY `order` ASC";

        #endregion
    }
}
