package fr.raksrinana.rsndiscord.commands.action;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.giphy.GiphyUtils;
import fr.raksrinana.rsndiscord.utils.giphy.requests.RandomGifRequest;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public abstract class GenericActionCommand extends BasicCommand{
	protected GenericActionCommand(Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NonNull Guild guild, @NonNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("user", translate(guild, "command.action.help.user"), true);
	}
	
	@Override
	public @NonNull CommandResult execute(@NonNull GuildMessageReceivedEvent event, @NonNull LinkedList<String> args) throws RuntimeException{
		super.execute(event, args);
		if(event.getMessage().getMentionedUsers().isEmpty()){
			return CommandResult.BAD_ARGUMENTS;
		}
		var target = event.getMessage().getMentionedUsers().stream().findFirst().orElseThrow();
		GiphyUtils.getRequest(getRequest()).ifPresentOrElse(response -> {
			var url = response.getData().getImages().getOriginal().getUrl();
			Actions.reply(event, translate(event.getGuild(), "actions.slap", event.getAuthor().getAsMention(), target.getAsMention()) + "\n" + url, null);
		}, () -> Actions.reply(event, translate(event.getGuild(), "actions.request-failed"), null));
		return CommandResult.SUCCESS;
	}
	
	protected abstract RandomGifRequest getRequest();
	
	protected abstract Action getAction();
	
	@Override
	public @NonNull String getCommandUsage(){
		return super.getCommandUsage();
	}
}
