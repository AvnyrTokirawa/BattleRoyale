package es.outlook.adriansrj.battleroyale.game.mode.simple;

import es.outlook.adriansrj.battleroyale.game.mode.BattleRoyaleModeAdapter;
import es.outlook.adriansrj.core.util.Duration;
import es.outlook.adriansrj.core.util.configurable.Configurable;
import es.outlook.adriansrj.core.util.configurable.ConfigurableEntry;
import es.outlook.adriansrj.core.util.configurable.duration.ConfigurableDuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Objects;

/**
 * Represents the battle mode that load its modifiers from a {@link YamlConfiguration}.
 *
 * @author AdrianSR
 */
public class SimpleBattleRoyaleMode extends BattleRoyaleModeAdapter implements Configurable {
	
	public static final Double  DEFAULT_INITIAL_HEALTH        = 20.0;
	public static final Double  DEFAULT_MAX_HEALTH            = 20.0;
	public static final Integer DEFAULT_MAX_KILLS             = 0;
	public static final Integer DEFAULT_MAX_PLAYERS           = 100;
	public static final Integer DEFAULT_MAX_TEAMS             = 0;
	public static final Integer DEFAULT_MAX_PLAYERS_PER_TEAM  = 1;
	public static final Boolean DEFAULT_TEAM_CREATION         = true;
	public static final Boolean DEFAULT_TEAM_SELECTION        = true;
	public static final Boolean DEFAULT_AUTO_FILL             = true;
	public static final Double  DEFAULT_HEALTH_AFTER_REVIVING = 6.0;
	public static final Boolean DEFAULT_RESPAWN_IN_AIR        = true;
	
	public static final ConfigurableDuration DEFAULT_REVIVING_TIME = new ConfigurableDuration ( Duration.ofSeconds ( 10 ) );
	public static final ConfigurableDuration DEFAULT_RESPAWN_TIME  = new ConfigurableDuration ( Duration.ofSeconds ( 5 ) );
	
	/**
	 * {@link SimpleBattleRoyaleMode} builder.
	 *
	 * @author AdrianSR / 02/10/2021 / 01:24 p. m.
	 */
	public static class Builder {
		
		protected double               initial_health       = DEFAULT_INITIAL_HEALTH;
		protected double               maximum_health       = DEFAULT_MAX_HEALTH;
		protected int                  maximum_kills        = DEFAULT_MAX_KILLS;
		protected int                  maximum_teams        = DEFAULT_MAX_TEAMS;
		protected int                  maximum_players_team = DEFAULT_MAX_PLAYERS_PER_TEAM;
		protected boolean              team_creation        = DEFAULT_TEAM_CREATION;
		protected boolean              team_selection       = DEFAULT_TEAM_SELECTION;
		protected boolean              autofill             = DEFAULT_AUTO_FILL;
		protected boolean              reviving;
		protected ConfigurableDuration reviving_time        = DEFAULT_REVIVING_TIME;
		protected double               reviving_health      = DEFAULT_HEALTH_AFTER_REVIVING;
		protected boolean              respawn;
		protected ConfigurableDuration respawn_time         = DEFAULT_RESPAWN_TIME;
		protected boolean              respawn_in_air       = DEFAULT_RESPAWN_IN_AIR;
		protected boolean              redeploy;
		
		public Builder initialHealth ( double initial_health ) {
			this.initial_health = Math.max ( initial_health , 0.5D );
			return this;
		}
		
		public Builder maximumHealth ( double maximum_health ) {
			this.maximum_health = Math.max ( maximum_health , 0.5D );
			return this;
		}
		
		public Builder maximumHealth ( int maximum_kills ) {
			this.maximum_kills = maximum_kills;
			return this;
		}
		
		public Builder maximumTeams ( int maximum_teams ) {
			this.maximum_teams = maximum_teams;
			return this;
		}
		
		public Builder maximumPlayerPerTeam ( int maximum_players_team ) {
			this.maximum_players_team = maximum_players_team;
			return this;
		}
		
		public Builder teamCreation ( boolean team_creation ) {
			this.team_creation = team_creation;
			return this;
		}
		
		public Builder teamSelection ( boolean team_selection ) {
			this.team_selection = team_selection;
			return this;
		}
		
		public Builder autofill ( boolean autofill ) {
			this.autofill = autofill;
			return this;
		}
		
		public Builder reviving ( boolean reviving ) {
			this.reviving = reviving;
			return this;
		}
		
		public Builder revivingTime ( Duration reviving_time ) {
			this.reviving_time = new ConfigurableDuration (
					Objects.requireNonNull ( reviving_time , "reviving time cannot be null" ) );
			return this;
		}
		
		public Builder healthAfterReviving ( double health ) {
			this.reviving_health = Math.max ( health , 0.5D );
			return this;
		}
		
		public Builder respawn ( boolean respawn ) {
			this.respawn = respawn;
			return this;
		}
		
		public Builder respawn ( Duration time ) {
			this.respawn_time = new ConfigurableDuration (
					Objects.requireNonNull ( time , "time cannot be null" ) );
			return this;
		}
		
		public Builder respawnInAir ( boolean flag ) {
			this.respawn_in_air = flag;
			return this;
		}
		
		public Builder redeploy ( boolean redeploy ) {
			this.redeploy = redeploy;
			return this;
		}
		
		public SimpleBattleRoyaleMode build ( ) {
			return new SimpleBattleRoyaleMode (
					initial_health , maximum_health , maximum_kills , maximum_teams ,
					maximum_players_team , autofill , reviving , reviving_time ,
					reviving_health , respawn , respawn_time , respawn_in_air , redeploy );
		}
	}
	
	@ConfigurableEntry ( key = "health.initial", comment = "the health players will have" +
			"\nat the beginning of the match" )
	protected double initial_health = DEFAULT_INITIAL_HEALTH;
	
	@ConfigurableEntry ( key = "health.maximum", comment = "the maximum health players can have" )
	protected double maximum_health = DEFAULT_MAX_HEALTH;
	
	@ConfigurableEntry ( key = "game.kills-limit", comment = "the game will end when this" +
			"\nlimit of kills is reached." +
			"\nset to 0 to disable this option." )
	protected int maximum_kills = DEFAULT_MAX_KILLS;
	
	@ConfigurableEntry ( key = "game.player-limit", comment = "the maximum numbers of players that" +
			"\ncan join the match." +
			"\nset to 0 to disable this option." )
	protected int maximum_players = DEFAULT_MAX_PLAYERS;
	
	@ConfigurableEntry ( key = "team.limit", comment = "the maximum numbers of teams in the match" +
			"\nset to 0 to disable this option." )
	protected int maximum_teams = DEFAULT_MAX_TEAMS;
	
	@ConfigurableEntry ( key = "team.player-limit-per-team", comment = "the maximum numbers of players a team can have" +
			"\nset to 0 to disable this option." )
	protected int maximum_players_team = DEFAULT_MAX_PLAYERS_PER_TEAM;
	
	@ConfigurableEntry ( key = "team.creation", comment = "if disabled, players will not be able to create teams" )
	protected boolean team_creation = DEFAULT_TEAM_CREATION;
	
	@ConfigurableEntry ( key = "team.selection", comment = "if disabled, players will not be able to select a team" )
	protected boolean team_selection = DEFAULT_TEAM_SELECTION;
	
	@ConfigurableEntry ( key = "team.autofill", comment = "if enabled, players who are not on a team will be" +
			"\nautomatically assigned to a team when the match starts" )
	protected boolean autofill = DEFAULT_AUTO_FILL;
	
	@ConfigurableEntry ( key = "reviving.enable", comment = "if enabled, players will be able to revive their teammates" )
	protected boolean reviving;
	
	@ConfigurableEntry ( key = "reviving.time", comment = "how long it will take to revive a teammate" )
	protected ConfigurableDuration reviving_time = DEFAULT_REVIVING_TIME;
	
	@ConfigurableEntry ( key = "reviving.health-after", comment = "health players will have after being revived" )
	protected double reviving_health = DEFAULT_HEALTH_AFTER_REVIVING;
	
	@ConfigurableEntry ( key = "respawning.enable", comment = "if enabled, players will be respawned after dying." +
			"\nnote that the kills limit will determine the end" +
			"\nof the match if this option is enabled" )
	protected boolean respawn;
	
	@ConfigurableEntry ( key = "respawning.time", comment = "how long will players have to wait to respawn" )
	protected ConfigurableDuration respawn_time = DEFAULT_RESPAWN_TIME;
	
	@ConfigurableEntry ( key = "respawning.in-air", comment = "if enabled, players will be respawned in the air." )
	protected boolean respawn_in_air;
	
	@ConfigurableEntry ( key = "parachute.redeploy.enable", comment = "if disabled, players will not be able to open" +
			"\ntheir parachutes after landing" )
	protected boolean redeploy;
	
	public SimpleBattleRoyaleMode ( double initial_health , double maximum_health , int maximum_kills , int maximum_teams ,
			int maximum_players_team , boolean autofill , boolean team_creation , boolean team_selection , boolean reviving ,
			ConfigurableDuration reviving_time , double reviving_health , boolean respawn ,
			ConfigurableDuration respawn_time , boolean respawn_in_air , boolean redeploy ) {
		this.initial_health       = initial_health;
		this.maximum_health       = maximum_health;
		this.maximum_kills        = maximum_kills;
		this.maximum_teams        = maximum_teams;
		this.maximum_players_team = maximum_players_team;
		this.team_creation        = team_creation;
		this.team_selection       = team_selection;
		this.autofill             = autofill;
		this.reviving             = reviving;
		this.reviving_time        = reviving_time;
		this.reviving_health      = reviving_health;
		this.respawn              = respawn;
		this.respawn_time         = respawn_time;
		this.respawn_in_air       = respawn_in_air;
		this.redeploy             = redeploy;
	}
	
	public SimpleBattleRoyaleMode ( double initial_health , double maximum_health , int maximum_kills , int maximum_teams ,
			int maximum_players_team , boolean autofill , boolean reviving ,
			ConfigurableDuration reviving_time , double reviving_health , boolean respawn ,
			ConfigurableDuration respawn_time , boolean respawn_in_air , boolean redeploy ) {
		this ( initial_health , maximum_health , maximum_kills , maximum_teams , maximum_players_team , autofill ,
			   true , true ,
			   reviving , reviving_time , reviving_health , respawn , respawn_time , respawn_in_air , redeploy );
	}
	
	public SimpleBattleRoyaleMode ( ) {
		// to be loaded
	}
	
	@Override
	public boolean initialize ( ) {
		return true;
	}
	
	@Override
	public double getInitialHealth ( ) {
		return initial_health;
	}
	
	@Override
	public double getMaxHealth ( ) {
		return maximum_health;
	}
	
	@Override
	public int getMaxKills ( ) {
		return maximum_kills;
	}
	
	@Override
	public int getMaxPlayers ( ) {
		return maximum_players;
	}
	
	@Override
	public boolean isRedeployEnabled ( ) {
		return redeploy;
	}
	
	@Override
	public int getMaxTeams ( ) {
		return maximum_teams;
	}
	
	@Override
	public int getMaxPlayersPerTeam ( ) {
		return maximum_players_team;
	}
	
	@Override
	public boolean isTeamCreationEnabled ( ) {
		return team_creation;
	}
	
	@Override
	public boolean isTeamSelectionEnabled ( ) {
		return team_selection;
	}
	
	@Override
	public boolean isAutoFillEnabled ( ) {
		return autofill;
	}
	
	@Override
	public final boolean isSolo ( ) {
		return getMaxPlayersPerTeam ( ) <= 1;
	}
	
	@Override
	public boolean isRevivingEnabled ( ) {
		return reviving && reviving_time != null;
	}
	
	@Override
	public Duration getRevivingTime ( ) {
		return reviving_time != null ? reviving_time : DEFAULT_REVIVING_TIME;
	}
	
	@Override
	public double getHealthAfterReviving ( ) {
		return reviving_health;
	}
	
	@Override
	public boolean isRespawnEnabled ( ) {
		return respawn;
	}
	
	@Override
	public Duration getRespawnTime ( ) {
		return respawn_time != null ? respawn_time : DEFAULT_RESPAWN_TIME;
	}
	
	@Override
	public boolean isRespawnInAir ( ) {
		return respawn_in_air;
	}
	
	@Override
	public SimpleBattleRoyaleMode load ( ConfigurationSection section ) {
		loadEntries ( section );
		return this;
	}
	
	@Override
	public int save ( ConfigurationSection section ) {
		return saveEntries ( section );
	}
	
	@Override
	public boolean isValid ( ) {
		return maximum_players_team > 0 && maximum_health > 0.0D && initial_health > 0.0D
				&& ( team_creation || ( isSolo ( ) || maximum_teams > 0 ) );
	}
}