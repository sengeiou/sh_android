package gm.mobi.android.task.jobs;


import com.path.android.jobqueue.Params;

import gm.mobi.android.task.BusProvider;
import gm.mobi.android.task.events.LoginResultEvent;

public class LoginUserJob extends CancellableJob {

    private static final int PRIORITY = 8; //TODO definir valores estáticos para determinados casos
    private static final int RETRY_ATTEMTS = 3;
    private String usernameEmail;
    private String password;


    public LoginUserJob(String usernameEmail, String password) {
        super(new Params(PRIORITY)
                .delayInMs(1000)//debug
        );
        this.usernameEmail = usernameEmail;
        this.password = password;
    }


    @Override
    public void onAdded() {
        /* no-op */
    }

    @Override
    public void onRun() throws Throwable {
        //TODO enviar información de login al servidor y comunicar el resultado
        if(isCancelled()) return;
        // Mock login:
        if (usernameEmail.equals("rafa.vazsan@gmail.com") || usernameEmail.equals("sloydev")) {
            
            BusProvider.getInstance().post(LoginResultEvent.successful());
        } else {
            BusProvider.getInstance().post(LoginResultEvent.invalid());
        }
    }

    @Override
    protected void onCancel() {
        /* no-op */
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return true;
    }

    @Override
    protected int getRetryLimit() {
        return RETRY_ATTEMTS;
    }
}
