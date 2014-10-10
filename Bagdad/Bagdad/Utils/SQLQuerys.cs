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

        public const String InsertLoginData = "INSERT INTO User (idUser, idFavouriteTeam, sessionToken, userName, email, name, photo, bio, website, points, numFollowings, numFollowers, csys_birth, csys_modified, csys_revision, csys_deleted, csys_synchronized) VALUES (@idUser, @idFavouriteTeam, @sessionToken, @userName, @email, @name, @photo, @bio, @website, @points, @numFollowings, @numFollowers, @csys_birth, @csys_modified, @csys_revision, @csys_deleted, @csys_synchronized)";

        public const String InsertUserData = "INSERT OR REPLACE INTO User (idUser, idFavouriteTeam, userName, name, photo, bio, website, points, numFollowings, numFollowers, csys_birth, csys_modified, csys_revision, csys_deleted, csys_synchronized) VALUES (@idUser, @idFavouriteTeam, @userName, @name, @photo, @bio, @website, @points, @numFollowings, @numFollowers, @csys_birth, @csys_modified, @csys_revision, @csys_deleted, @csys_synchronized)";

        public const String UpdateUserData = "UPDATE User SET idFavouriteTeam = @idFavouriteTeam, userName = @userName, name = @name, photo = @photo, bio = @bio, website = @website, points= @points, numFollowings = @numFollowings, numFollowers = @numFollowers, csys_modified = @csys_modified, csys_revision =  @csys_revision, csys_synchronized = @csys_synchronized WHERE idUser = @idUser";

        public const String DeleteUserData = "DELETE FROM User WHERE idUser = @idUser";

        public const String getUserById = "SELECT idUser FROM User WHERE idUser = @idUser";

        public const String GetSessionToken = "SELECT sessionToken, idUser FROM User WHERE sessionToken IS NOT NULL";

        public const String hasLoggedSQL = "SELECT idUser FROM User WHERE sessionToken IS NOT NULL";

        public const String GetNameAndURL = "SELECT name, photo FROM User WHERE idUser = @idUser";

        public const String GetUserProfileInfo = "SELECT idUser, userName, name, photo, bio, points, numFollowings, numFollowers, website FROM User WHERE idUser = @idUser";

        public const String GetUsersByUserAndNick = "SELECT idUser, userName, name, photo, bio, points, numFollowings, numFollowers, website FROM User WHERE usernName like @userName OR name LIKE @name";

        #endregion

        #region SHOT

        public const String InsertShotData = "INSERT INTO Shot (idShot, idUser, comment, csys_birth, csys_modified, csys_revision, csys_deleted, csys_synchronized) VALUES (@idShot, @idUser, @comment, @csys_birth, @csys_modified, @csys_revision, @csys_deleted, @csys_synchronized)";

        public const String UpdateShotData = "UPDATE Shot SET comment = @comment, csys_birth = @csys_birth, csys_modified = @csys_modified, csys_revision = @csys_revision, csys_synchronized = @csys_synchronized WHERE idShot = @idShot";

        public const String DeleteShotData = "DELETE FROM Shot WHERE idShot = @idShot";
        
        public const String GetTimeLineShots = "SELECT s.idShot, s.idUser, s.comment, u.name, u.photo, s.csys_birth FROM Shot s JOIN User u ON s.idUser = u.idUser JOIN Follow f ON s.idUser = f.idUserFollowed OR s.idUser = f.idUser WHERE f.idUser = @idUser GROUP BY s.idShot ORDER BY s.csys_birth DESC LIMIT @limit;";

        public const String GetTimeLineOtherShots = "SELECT s.idShot, s.idUser, s.comment, u.name, u.photo, s.csys_birth FROM Shot s JOIN User u ON s.idUser = u.idUser JOIN Follow f ON s.idUser = f.idUserFollowed OR s.idUser = f.idUser WHERE f.idUser = @idUser GROUP BY s.idShot ORDER BY s.csys_birth DESC LIMIT @offset, @limit;";

        public const String shotsSynchronized = "UPDATE Shot SET csys_synchronized = @csys_synchronized WHERE idShot = @idShot";

        public const String getOlderShotDate = "SELECT MIN(csys_birth) FROM Shot";

        public const String getShotById = "SELECT idShot FROM Shot WHERE idShot = @idShot";

        public const String getShotByComment24Hours = "SELECT idShot FROM Shot WHERE comment = @comment and idUser = @idUser and csys_modified > @yesterday";

        public const String getMinIdShotOlderThanMax = "SELECT min(idShot) FROM (Select idshot from Shot order by csys_modified DESC LIMIT @limit)";

        public const String deleteShotsOlderThanMax = "DELETE FROM Shot where idShot < @idShot";

        #endregion

        #region FOLLOW

        public const String InsertFollowData = "INSERT INTO Follow (idUser, idUserFollowed, csys_birth, csys_modified, csys_revision, csys_deleted, csys_synchronized) VALUES (@idUser, @idUserFollowed, @csys_birth, @csys_modified, @csys_revision, @csys_deleted, @csys_synchronized)";

        public const String UpdateFollowData = "UPDATE Follow SET csys_modified =  @csys_modified, csys_revision = @csys_revision,csys_synchronized = @csys_synchronized WHERE idUser = @idUser AND idUserFollowed = @idUserFollowed";

        public const String DeleteFollowData = "DELETE FROM Follow WHERE idUser = @idUser AND idUserFollowed = @idUserFollowed";

        public const String SelectIdUserFollowing = "SELECT idUserFollowed from Follow where idUser = @idUser";

        public const String GetFollowByIdUserAndIdUserFollowed = "SELECT idUser, idUserFollowed FROM Follow WHERE idUser = @idUser AND idUserFollowed = @idUserFollowed";

        public const String GetAllInfoFromFollowings = "SELECT u.idUser, u.userName, u.name, u.photo FROM User u JOIN Follow f ON u.idUser = f.idUserFollowed WHERE f.idUser = @idUser ORDER BY f.csys_modified DESC";

        public const String GetAllInfoFromPeople = "SELECT u.idUser, u.userName, u.name, u.photo FROM User u JOIN Follow f ON u.idUser = f.idUserFollowed WHERE f.idUser = @idUser ORDER BY u.name ASC, u.userName ASC";

        #endregion

        #region Generic

        public const String getMaxModificationDateOf = "SELECT maxTimestamp FROM Synchro WHERE Entity = @Entity";

        public const String updateModificationDateOf = "UPDATE Synchro SET maxTimestamp = @maxTimestamp WHERE Entity = @Entity";

        public const String GetSynchronizationTables = "SELECT `order`, entity, frequency, maxTimestamp, minTimestamp, direction, maxRows, minRows  FROM Synchro ORDER BY `order` ASC";

        #endregion
    }
}
