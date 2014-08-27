package gm.mobi.android.task.events;


import com.facebook.model.GraphUser;

public class FacebookProfileEvent {

    private GraphUser graphUser;
    private Exception error;

    public FacebookProfileEvent(GraphUser graphUser) {
        this.graphUser = graphUser;
    }

    public FacebookProfileEvent(Exception error) {
        this.error = error;
    }

    public GraphUser getGraphUser() {
        return graphUser;
    }

    public Exception getError() {
        return error;
    }

    public boolean hasError(){
        return error!=null;
    }

    @Override
    public String toString() {
        return "FacebookProfileEvent{" +
                "graphUser=" + graphUser +
                '}';
    }
}
