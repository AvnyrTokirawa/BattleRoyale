package es.outlook.adriansrj.battleroyale.scoreboard.handle;

/**
 * @author AdrianSR / 06/01/2022 / 01:05 p. m.
 */
public interface ScoreboardHandle {
	
	boolean isVisible ( );
	
	void setVisible ( boolean visible );
	
	void update ( );
	
	void destroy ( );
}
