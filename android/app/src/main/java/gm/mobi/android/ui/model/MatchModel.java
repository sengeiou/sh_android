package gm.mobi.android.ui.model;


public class MatchModel {

    private Long idMatch;
    private String localTeamName;
    private String visitorTeamName;
    private String datetime;
    private String title;

    public Long getIdMatch() {
        return idMatch;
    }

    public void setIdMatch(Long idMatch) {
        this.idMatch = idMatch;
    }

    public String getLocalTeamName() {
        return localTeamName;
    }

    public void setLocalTeamName(String localTeamName) {
        this.localTeamName = localTeamName;
    }

    public String getVisitorTeamName() {
        return visitorTeamName;
    }

    public void setVisitorTeamName(String visitorTeamName) {
        this.visitorTeamName = visitorTeamName;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
