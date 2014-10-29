package gm.mobi.android.db.objects;

import android.support.annotation.NonNull;
import java.util.Date;

public class MatchEntity extends Synchronized implements Comparable<MatchEntity> {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof MatchEntity)) return false;

        MatchEntity that = (MatchEntity) o;

        if (idMatch != null ? !idMatch.equals(that.idMatch) : that.idMatch != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return idMatch != null ? idMatch.hashCode() : 0;
    }

    @Override public int compareTo(@NonNull MatchEntity another) {
        return this.getMatchDate().compareTo(another.getMatchDate());
    }
}
