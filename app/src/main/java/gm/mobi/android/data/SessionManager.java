package gm.mobi.android.data;

import gm.mobi.android.db.objects.User;

public class SessionManager {

    private User currentUser;

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}
