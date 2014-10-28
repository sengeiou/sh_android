package gm.mobi.android.db.objects;

import java.util.Date;

public class MatchEntity extends Synchronized{

    private Long idMatch;
    private Date matchDate;
    private Long idLocalTeam;
    private Long idVisitorTeam;
    private String localTeamName;
    private String visitorTeamName;
    private Long status;

    public Long getIdMatch() {
        return idMatch;
    }

    public void setIdMatch(Long idMatch) {
        this.idMatch = idMatch;
    }

    public Date getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(Date matchDate) {
        this.matchDate = matchDate;
    }

    public Long getIdLocalTeam() {
        return idLocalTeam;
    }

    public void setIdLocalTeam(Long idLocalTeam) {
        this.idLocalTeam = idLocalTeam;
    }

    public Long getIdVisitorTeam() {
        return idVisitorTeam;
    }

    public void setIdVisitorTeam(Long idVisitorTeam) {
        this.idVisitorTeam = idVisitorTeam;
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

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }
}
