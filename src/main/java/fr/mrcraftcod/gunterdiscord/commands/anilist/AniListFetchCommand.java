package fr.mrcraftcod.gunterdiscord.commands.anilist;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.settings.configs.AniListTokenConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-08.
 *
 * @author Thomas Couchoud
 * @since 2018-10-08
 */
public class AniListFetchCommand extends BasicCommand{
	private static final String link = "https://anilist.co/api/v2/oauth/authorize?client_id=1230&response_type=code&redirect_uri=https://www.mrcraftcod.fr/redirect.php";
	
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	AniListFetchCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Code", "Code d'accès à l'api, obtenu en visitant: " + link, false);
	}
	
	@Override
	public CommandResult execute(@NotNull MessageReceivedEvent event, @NotNull LinkedList<String> args) throws Exception{
		super.execute(event, args);
		if(args.size() < 1){
			Actions.reply(event, "Merci de donner votre code d'accès");
		}
		else{
			new AniListTokenConfig(event.getGuild()).addValue(event.getAuthor().getIdLong(), args.poll());
			Actions.reply(event, "Code d'accès enregistré");
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
		return List.of("r", "register");
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
