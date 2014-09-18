package gm.mobi.android.service;

import javax.inject.Inject;

import gm.mobi.android.db.objects.User;
import hugo.weaving.DebugLog;

public class BagdadMockService implements BagdadService {

    private long delay;
    private int variancePercentage;
    private int errorPercentage;

    @Inject public BagdadMockService() {
        //TODO inicializar y usar valores de mockeo mediante preferencias
        //TODO mirar la implementación de AndroidMockValue persistance https://github.com/square/retrofit/blob/master/retrofit-mock/src/main/java/retrofit/android/AndroidMockValuePersistence.java
    }

    @Override
    @DebugLog
    public User login(String id, String password) {
        if (id.equals("rafa.vazsan@gmail.com") || id.equals("sloydev")) {
            User mockUser = new User();
            mockUser.setId(1);
            mockUser.setName("Rafa");
            mockUser.setSessionToken("Nnananananananana");
            mockUser.setEmail("rafa.vazsan@gmail.com");
            mockUser.setUserName("rafavazsan");
            mockUser.setFavouriteTeamId(1);
            mockUser.setPhoto("http://example.com");
            return mockUser;
        }
        return null;
    }


    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public int getVariancePercentage() {
        return variancePercentage;
    }

    public void setVariancePercentage(int variancePercentage) {
        this.variancePercentage = variancePercentage;
    }

    public int getErrorPercentage() {
        return errorPercentage;
    }

    public void setErrorPercentage(int errorPercentage) {
        this.errorPercentage = errorPercentage;
    }
}
