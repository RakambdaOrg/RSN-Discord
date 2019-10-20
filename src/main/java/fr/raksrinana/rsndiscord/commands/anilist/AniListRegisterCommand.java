package fr.raksrinana.rsndiscord.commands.anilist;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.anilist.AniListUtils;
import fr.raksrinana.rsndiscord.utils.log.Log;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-08.
 *
 * @author Thomas Couchoud
 * @since 2018-10-08
 */
public class AniListRegisterCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	AniListRegisterCommand(@Nullable final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@Nonnull final Guild guild, @Nonnull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Code", "API code obtained from: " + AniListUtils.getCodeLink(), false);
	}
	
	@Nonnull
	@Override
	public CommandResult execute(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		if(args.isEmpty()){
			Actions.reply(event, "Please provide your API code from " + AniListUtils.getCodeLink());
		}
		else{
			final var code = args.poll();
			Optional.ofNullable(event.getMember()).ifPresent(member -> {
				try{
					AniListUtils.generateToken(member, code);
					Actions.reply(event, "API code saved");
				}
				catch(final Exception e){
					Actions.reply(event, "Error while saving api code");
					Log.getLogger(event.getGuild()).error("Error getting AniList access token", e);
				}
			});
		}
		return CommandResult.SUCCESS;
	}
	
	@Nonnull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <code>";
	}
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "AniList registering";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("register", "r");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Register your account for AniList usages";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
