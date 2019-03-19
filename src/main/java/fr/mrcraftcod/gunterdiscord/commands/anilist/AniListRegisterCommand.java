package fr.mrcraftcod.gunterdiscord.commands.anilist;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.settings.configs.AniListLastAccessConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.anilist.AniListUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
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
@SuppressWarnings("WeakerAccess")
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
		builder.addField("Code", "API code obtained from: " + AniListUtils.getCodeLink(), false);
	}
	
	@Override
	public CommandResult execute(@NotNull final MessageReceivedEvent event, @NotNull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		if(args.isEmpty()){
			Actions.reply(event, "Please provide your API code");
		}
		else{
			try{
				final var code = args.poll();
				AniListUtils.getAccessToken(event.getMember(), code);
				final var userInfos = AniListUtils.getQuery(event.getMember(), QUERY, new JSONObject());
				final var userInfoConf = new AniListLastAccessConfig(event.getGuild());
				userInfoConf.addValue(event.getAuthor().getIdLong(), "userId", "" + userInfos.getJSONObject("data").getJSONObject("Viewer").getInt("id"));
				Actions.reply(event, "API code saved");
			}
			catch(final Exception e){
				Actions.reply(event, "Error while saving api code");
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
		return "AniList registering";
	}
	
	@Override
	public List<String> getCommand(){
		return List.of("register", "r");
	}
	
	@Override
	public String getDescription(){
		return "Register your account for AniList usages";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
