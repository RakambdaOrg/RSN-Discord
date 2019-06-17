package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.settings.configs.YoutubeChannelConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.YoutubeRoleConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import fr.mrcraftcod.gunterdiscord.utils.log.Log;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-12
 */
public class YoutubeCommand extends BasicCommand{
	@Override
	public void addHelp(@NotNull final Guild guild, @NotNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("User", "The user to get the channel", false);
		builder.addField("URL", "The URL to the user's channel", false);
	}
	
	@Override
	public CommandResult execute(final GuildMessageReceivedEvent event, @NotNull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		if(!event.getMessage().getMentionedMembers().isEmpty()){
			args.poll();
			final var member = event.getMessage().getMentionedMembers().get(0);
			if(Utilities.hasRole(member, new YoutubeRoleConfig(event.getGuild()).getObject())){
				final var strUrl = args.poll();
				if(Objects.nonNull(strUrl)){
					if(Utilities.isAdmin(event.getMember())){
						try{
							final var url = new URL(strUrl);
							new YoutubeChannelConfig(event.getGuild()).addValue(member.getUser().getIdLong(), url.toString());
						}
						catch(final Exception e){
							Log.getLogger(event.getGuild()).warn("Provided YouTube link isn't valid {}", strUrl);
							Actions.reply(event, "Invalid link");
						}
					}
					else{
						Actions.reply(event, "You can't modify the link to a user's channel");
					}
				}
				else{
					Actions.reply(event, "%s's channel is available at %s", member.getAsMention(), new YoutubeChannelConfig(event.getGuild()).getValue(member.getUser().getIdLong()));
				}
			}
			else{
				Actions.reply(event, "This user isn't a content creator");
			}
		}
		else{
			Actions.reply(event, "Please mention a user");
		}
		return CommandResult.SUCCESS;
	}
	
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <@user> [URL]";
	}
	
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Override
	public String getName(){
		return "YouTube";
	}
	
	@Override
	public List<String> getCommandStrings(){
		return List.of("youtube", "yt");
	}
	
	@Override
	public String getDescription(){
		return "Gets the link to a content creator's page";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
