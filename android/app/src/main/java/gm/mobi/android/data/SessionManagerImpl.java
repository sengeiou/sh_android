package gm.mobi.android.data;

import gm.mobi.android.data.prefs.CurrentUserId;
import gm.mobi.android.data.prefs.LongPreference;
import gm.mobi.android.data.prefs.SessionToken;
import gm.mobi.android.data.prefs.StringPreference;
import gm.mobi.android.db.objects.UserEntity;
import javax.inject.Inject;

public class SessionManagerImpl implements SessionManager {

    private StringPreference sessionTokenPreference;

    private UserEntity currentUser;

    private LongPreference currentUserIdPreference;

    @Inject public SessionManagerImpl(@SessionToken StringPreference sessionTokenPreference,
      @CurrentUserId LongPreference currentUserIdPreference) {
        this.sessionTokenPreference = sessionTokenPreference;
        this.currentUserIdPreference = currentUserIdPreference;
    }

    @Override
    public UserEntity getCurrentUser() {
        return currentUser;
    }

    @Override
    public void setCurrentUser(UserEntity currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    public String getSessionToken() {
        return sessionTokenPreference.get();
    }

    @Override
    public void setSessionToken(String sessionToken) {
        this.sessionTokenPreference.set(sessionToken);
    }

    @Override
    public long getCurrentUserId() {
        return currentUserIdPreference.get();
    }

    @Override
    public void setCurrentUserId(long currentUserId) {
        this.currentUserIdPreference.set(currentUserId);
    }

    @Override
    public void createSession(long userId, String sessionToken, UserEntity loggedInUser) {
        setCurrentUserId(userId);
        setSessionToken(sessionToken);
        setCurrentUser(loggedInUser);
    }

    @Override
    public void destroySession() {
        currentUserIdPreference.delete();
        sessionTokenPreference.delete();
        currentUser = null;
    }
}
