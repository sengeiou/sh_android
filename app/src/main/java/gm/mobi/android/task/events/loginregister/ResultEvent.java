package gm.mobi.android.task.events.loginregister;

public abstract class ResultEvent<T>  {

    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_INVALID = 2;
    public static final int STATUS_SERVER_FAILURE = 3;
    private int status;
    private Exception error;

    public ResultEvent(int status){
        this.status = status;
    }

    public abstract ResultEvent setSuccessful(T o);

    public abstract  ResultEvent setInvalid();

    public abstract ResultEvent setServerError(Exception e);

    public int getStatus(){
        return status;
    }
    public void setError(Exception error){
        this.error = error;
    }
    public boolean hasError(){
        return error!=null;
    }


}
