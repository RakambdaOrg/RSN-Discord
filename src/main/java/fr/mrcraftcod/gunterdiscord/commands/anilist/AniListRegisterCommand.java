package fr.mrcraftcod.gunterdiscord.commands.anilist;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.settings.configs.AniListLastAccessConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.anilist.AniListUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-08.
 *
 * @author Thomas Couchoud
 * @since 2018-10-08
 */
public class AniListRegisterCommand extends BasicCommand{
	public static final Logger LOGGER = LoggerFactory.getLogger(AniListRegisterCommand.class);
	private static final String QUERY = "query{Viewer {id name}}";
	
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	AniListRegisterCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NotNull final Guild guild, @NotNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Code", "Code d'accès à l'api, obtenu en visitant: " + AniListUtils.getCodeLink(), false);
	}
	
	@Override
	public CommandResult execute(@NotNull final MessageReceivedEvent event, @NotNull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		if(args.size() < 1){
			Actions.reply(event, "Merci de donner votre code d'accès");
		}
		else{
			try{
				final var code = args.poll();
				AniListUtils.getAccessToken(event.getMember(), code);
				final var userInfos = AniListUtils.getQuery(event.getMember(), QUERY, new JSONObject());
				final var userInfoConf = new AniListLastAccessConfig(event.getGuild());
				userInfoConf.addValue(event.getAuthor().getIdLong(), "userId", "" + userInfos.getJSONObject("data").getJSONObject("Viewer").getInt("id"));
				Actions.reply(event, "Code d'accès enregistré");
			}
			catch(final Exception e){
				Actions.reply(event, "Erreur lors de l'enregistrement de ce token");
				LOGGER.error("Error getting AniList access token", e);
			}
		}
		return CommandResult.SUCCESS;
	}
	
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <code>";
	}
	
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Override
	public String getName(){
		return "AniList enregistrement";
	}
	
	@Override
	public List<String> getCommand(){
		return List.of("register", "r");
	}
	
	@Override
	public String getDescription(){
		return "Enregistre son compte pour le tracker anilist";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
