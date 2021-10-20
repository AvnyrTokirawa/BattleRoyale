package es.outlook.adriansrj.battleroyale.battlefield.setup.tool;

import es.outlook.adriansrj.battleroyale.battlefield.border.BattlefieldBorderSuccessionRandom;
import es.outlook.adriansrj.battleroyale.battlefield.setup.BattlefieldSetupSession;
import es.outlook.adriansrj.battleroyale.enums.EnumInternalLanguage;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.util.StringUtil;
import es.outlook.adriansrj.battleroyale.util.time.TimeUtil;
import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * Tool useful to set the resizing succession of
 * the border of the battlefield.
 *
 * @author AdrianSR / 18/10/2021 / 03:31 p. m.
 */
public class BattlefieldSetupToolBorder extends BattlefieldSetupToolPrompt {
	
	/**
	 * @author AdrianSR / 18/10/2021 / 03:33 p. m.
	 */
	protected static class ResizingSuccessionPrompt extends ValidatingPrompt {
		
		protected static final String[] LEVEL_NAMES = new String[]
				{
						"Minimum Radius" ,
						"Full Time" ,
						"Divisions" ,
						"Minimum Damage" ,
						"Maximum Damage"
				};
		
		protected static final String[] LEVEL_DESCRIPTIONS = new String[]
				{
						"The radius that the border will have at the end of this succession" ,
						"How long does this succession last" ,
						"The number of divisions/shrink points that this succession will have" ,
						"The minimum damage that can be done to players when they are caught outside the safe zone" ,
						"The maximum damage that can be done to players when they are caught outside the safe zone"
				};
		
		protected int level = -1;
		
		protected double min_radius;
		protected long   full_time;
		protected int    divisions;
		protected double min_damage;
		protected double max_damage;
		
		protected final BattlefieldSetupToolBorder tool;
		
		public ResizingSuccessionPrompt ( BattlefieldSetupToolBorder tool ) {
			this.tool = tool;
		}
		
		@NotNull
		@Override
		public String getPromptText ( ConversationContext context ) {
			Conversable whom = context.getForWhom ( );
			
			whom.sendRawMessage ( StringUtil.EMPTY );
			whom.sendRawMessage ( ChatColor.LIGHT_PURPLE + "*------------------" );
			
			if ( level < 0 ) {
				whom.sendRawMessage ( ChatColor.GOLD + "The border resize succession calculator requires some data to work:" );
				level++;
			}
			
			for ( int i = 0 ; i < LEVEL_NAMES.length ; i++ ) {
				boolean current_level = level == i;
				
				whom.sendRawMessage ( ChatColor.GOLD + "- " + ( current_level ? ChatColor.GREEN.toString ( ) :
						ChatColor.GOLD ) + LEVEL_NAMES[ i ] + ( current_level ? ChatColor.RED + " *" : "" ) );
			}
			
			whom.sendRawMessage ( StringUtil.EMPTY );
			
			String request_message = ChatColor.LIGHT_PURPLE + "Please enter: " + LEVEL_DESCRIPTIONS[ level ] + ".";
			
			if ( level == 1 ) {
				return request_message + " ( Format: " + ChatColor.RED + "[" + ChatColor.GREEN + "Time" + ChatColor.RED
						+ "] [" + ChatColor.GREEN + "Unit" + ChatColor.RED + "]. Available units: "
						+ "(seconds, minutes, hours) -> (s, m, h)" + " )";
			} else {
				return request_message;
			}
		}
		
		@NotNull
		@Override
		protected Prompt acceptValidatedInput ( ConversationContext context , String input ) {
			Conversable whom = context.getForWhom ( );
			
			// here we're fixing the input
			input = input.toLowerCase ( ).trim ( );
			input = input.startsWith ( "/" ) ? input.substring ( 1 ) : input;
			
			// here the player wants to cancel/go back.
			if ( input.equalsIgnoreCase ( "cancel" ) || input.equalsIgnoreCase ( "close" )
					|| input.equalsIgnoreCase ( "exit" ) || input.equalsIgnoreCase ( "stop" ) ) {
				return Prompt.END_OF_CONVERSATION;
			} else if ( input.equalsIgnoreCase ( "back" ) || input.equalsIgnoreCase ( "previous" ) ) {
				level--;
				return this;
			}
			
			if ( input.length ( ) > 0 ) {
				String[] args = input.split ( " " );
				
				switch ( level ) {
					// minimum radius
					case 0: {
						setMinimumRadius ( whom , args );
						break;
					}
					
					// full time
					case 1: {
						setFullTime ( whom , args );
						break;
					}
					
					// divisions
					case 2: {
						setDivisions ( whom , args );
						break;
					}
					
					// minimum damage
					case 3: {
						setMinimumDamage ( whom , args );
						break;
					}
					
					// maximum damage
					case 4: {
						if ( setMaximumDamage ( whom , args ) ) {
							proceed ( ); // done!
							return Prompt.END_OF_CONVERSATION;
						} else {
							break;
						}
					}
					
					default: {
						level = 0; // invalid level
						break;
					}
				}
			}
			return this;
		}
		
		protected void setMinimumRadius ( Conversable whom , String[] args ) {
			try {
				if ( args.length > 0 ) {
					double radius = Double.parseDouble ( args[ 0 ] );
					
					if ( radius >= 0.0D ) {
						this.min_radius = radius;
						this.level++;
					} else {
						whom.sendRawMessage ( ChatColor.RED + "This value cannot be negative!" );
					}
				} else {
					throw new NumberFormatException ( );
				}
			} catch ( NumberFormatException ex ) {
				whom.sendRawMessage ( ChatColor.RED + "This is not a valid number!" );
			}
		}
		
		protected void setFullTime ( Conversable whom , String[] args ) {
			try {
				if ( args.length < 1 ) {
					// ups, the time was not specified!
					throw new NumberFormatException ( "You forgot to specify the time!" );
				}
				
				long time = Long.parseLong ( args[ 0 ] );
				
				if ( args.length < 2 ) {
					// ups, the time unit was not specified!
					throw new IllegalArgumentException ( "You forgot to specify the time unit!" );
				}
				
				TimeUnit time_unit = TimeUtil.matchTimeUnit ( args[ 1 ] );
				
				if ( time > 0 && time_unit != null ) {
					this.full_time = time_unit.toMillis ( time );
					this.level++;
				} else {
					if ( time < 0 ) {
						whom.sendRawMessage ( ChatColor.RED + "The time cannot be negative!" );
					}
					
					if ( time_unit == null ) {
						whom.sendRawMessage ( ChatColor.RED + "The time unit must be specified!" );
					}
				}
			} catch ( NumberFormatException ex ) {
				String message = ex.getMessage ( );
				
				if ( message.isEmpty ( ) ) {
					whom.sendRawMessage ( ChatColor.RED + "This is not a valid number!" );
				} else {
					whom.sendRawMessage ( ChatColor.RED + message );
				}
			} catch ( IllegalArgumentException ex_b ) {
				String message = ex_b.getMessage ( );
				
				if ( message.isEmpty ( ) ) {
					whom.sendRawMessage ( ChatColor.RED + "This is not a valid time unit!" );
				} else {
					whom.sendRawMessage ( ChatColor.RED + message );
				}
			}
		}
		
		protected void setDivisions ( Conversable whom , String[] args ) {
			try {
				if ( args.length > 0 ) {
					int divisions = Integer.parseInt ( args[ 0 ] );
					
					if ( divisions >= BattlefieldBorderSuccessionRandom.MINIMUM_DIVISIONS ) {
						this.divisions = divisions;
						this.level++;
					} else {
						whom.sendRawMessage (
								ChatColor.RED + "At least " + ChatColor.GOLD
										+ BattlefieldBorderSuccessionRandom.MINIMUM_DIVISIONS
										+ ChatColor.RED + " divisions are required!" );
					}
				} else {
					throw new NumberFormatException ( );
				}
			} catch ( NumberFormatException ex ) {
				whom.sendRawMessage ( ChatColor.RED + "This is not a valid number!" );
			}
		}
		
		protected void setMinimumDamage ( Conversable whom , String[] args ) {
			try {
				if ( args.length > 0 ) {
					double min_damage = Double.parseDouble ( args[ 0 ] );
					
					if ( min_damage >= 0.0D ) {
						this.min_damage = min_damage;
						this.level++;
					} else {
						whom.sendRawMessage ( ChatColor.RED + "This value cannot be negative!" );
					}
					
				} else {
					throw new NumberFormatException ( );
				}
			} catch ( NumberFormatException ex ) {
				whom.sendRawMessage ( ChatColor.RED + "This is not a valid number!" );
			}
		}
		
		protected boolean setMaximumDamage ( Conversable whom , String[] args ) {
			try {
				if ( args.length == 0 ) {
					throw new NumberFormatException ( );
				}
				
				double max_damage = Double.parseDouble ( args[ 0 ] );
				
				if ( max_damage >= 0.0D ) {
					this.max_damage = max_damage;
					
					whom.sendRawMessage ( StringUtil.EMPTY );
					whom.sendRawMessage ( EnumInternalLanguage.TOOL_PROMPT_BATTLEFIELD_BORDER_SET.toString ( ) );
					return true;
				} else {
					whom.sendRawMessage ( ChatColor.RED + "This value cannot be negative!" );
				}
			} catch ( NumberFormatException ex ) {
				whom.sendRawMessage ( ChatColor.RED + "This is not a valid number!" );
			}
			return false;
		}
		
		protected void proceed ( ) {
			tool.session.setBorderResizeSuccession ( new BattlefieldBorderSuccessionRandom (
					min_radius , full_time , divisions , min_damage , max_damage ) );
			tool.successful = true;
		}
		
		@Override
		protected boolean isInputValid ( ConversationContext context , String input ) {
			return true;
		}
	}
	
	protected final ResizingSuccessionPrompt prompt;
	protected       boolean                  successful;
	
	protected BattlefieldSetupToolBorder ( BattlefieldSetupSession session , Player configurator ) {
		super ( session , configurator );
		
		this.prompt     = new ResizingSuccessionPrompt ( this );
		this.successful = false;
	}
	
	@Override
	public boolean isModal ( ) {
		return true;
	}
	
	@Override
	public boolean isCancellable ( ) {
		return true;
	}
	
	@Override
	protected ValidatingPrompt getPrompt ( ) {
		return prompt;
	}
	
	@Override
	protected boolean isSuccessfully ( ) {
		return successful;
	}
}
