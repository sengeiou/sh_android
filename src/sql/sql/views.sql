CREATE OR REPLACE ALGORITHM = MERGE
    VIEW MatchView 
    (
    	idMatch, idTeamLocal, idTeamVisitor, idFixture, DateMatch, ScoreLocal, ScoreVisitor, matchType, 
    	ScoreTeamLocalPenalties, ScoreTeamVisitorPenalties, DateFinal, matchState, idMatchOffer, idPreviousMatch, DateStartOvertime, DateStart2Half, DateStart, 
    	csys_revision, csys_birth, csys_modified, csys_deleted
    )
    AS SELECT m.idMatch, m.idTeamLocal, m.idTeamVisitor, m.idFixture, m.DateMatch, m.ScoreLocal, m.ScoreVisitor, m.matchType,
    me.ScoreTeamLocalPenalties, me.ScoreTeamVisitorPenalties, me.DateFinal, me.matchState, me.idMatchOffer, me.idPreviousMatch, me.DateStartOvertime, me.DateStart2Half, me.DateStart,
    m.csys_revision, m.csys_birth, m.csys_modified, m.csys_deleted
    FROM Matches m INNER JOIN MatchExtension me ON m.idMatch = me.idMatch  
    WITH CASCADED CHECK OPTION;
    
    
CREATE OR REPLACE ALGORITHM = MERGE
    VIEW EventOfMatchView 
    (
		idEventOfMatch, idMatch, idEvent, dateIn, MinuteOfMatch, Status, idPeriod, 				
		IdTeamOfEvent, ScoreLocal, ScoreVisitor, ActorTransmitterName, ActorInTransmitterName, ActorReceptorName, Comments, urlGoalFilm, isOwnGoal, isPenaltyGoal, isPenaltyMissedAtStick, isPenaltyMissedOut, isPenaltyMissedStopped,
		csys_revision, csys_birth, csys_modified, csys_deleted
	)
    AS SELECT em.idEventOfMatch, em.idMatch, em.idEvent, em.dateIn, em.MinuteOfMatch, em.Status, em.idPeriod,
    eme.IdTeamOfEvent, eme.ScoreLocal, eme.ScoreVisitor, eme.ActorTransmitterName, eme.ActorInTransmitterName, eme.ActorReceptorName, eme.Comments, eme.urlGoalFilm, eme.isOwnGoal, eme.isPenaltyGoal, eme.isPenaltyMissedAtStick, eme.isPenaltyMissedOut, eme.isPenaltyMissedStopped,
    em.csys_revision, em.csys_birth, em.csys_modified, em.csys_deleted
    FROM EventOfMatch em INNER JOIN EventOfMatchExtension eme ON em.idEventOfMatch = eme.idEventOfMatch  
    WITH CASCADED CHECK OPTION;