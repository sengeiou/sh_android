package gm.mobi.android.data;

import gm.mobi.android.db.objects.User;

public interface SessionManager {

    public User getCurrentUser();

    public void setCurrentUser(User currentUser);

    public String getSessionToken();

    public void setSessionToken(String sessionToken);

    public long getCurrentUserId();

    public void setCurrentUserId(long currentUserId);

    public void createSession(long userId, String sessionToken, User loggedInUser);

    public void destroySession();
}
