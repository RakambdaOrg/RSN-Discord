package fr.raksrinana.rsndiscord.commands.record;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.permission.Permission;
import fr.raksrinana.rsndiscord.utils.permission.SimplePermission;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public class StartCommand extends BasicCommand{
	public StartCommand(Command parent){
		super(parent);
	}
	
	@Override
	public @NonNull Permission getPermission(){
		return new SimplePermission("command.record.start", true);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		if(event.getMessage().getMentionedMembers().isEmpty()){
			return CommandResult.BAD_ARGUMENTS;
		}
		event.getMessage().getMentionedMembers().stream()
				.filter(member -> Objects.nonNull(member.getVoiceState()))
				.filter(member -> member.getVoiceState().inVoiceChannel())
				.findFirst()
				.ifPresentOrElse(member -> {
					var audioManager = event.getGuild().getAudioManager();
					var voiceState = member.getVoiceState();
					var channel = voiceState.getChannel();
					if(audioManager.isConnected() && !Objects.equals(audioManager.getConnectedChannel(), channel)){
						Actions.reply(event, translate(event.getGuild(), "record.open.connected-elsewhere"), null);
						return;
					}
					Receiver.getInstanceOrCreate(event.getGuild()).start();
					audioManager.openAudioConnection(channel);
					Actions.reply(event, translate(event.getGuild(), "record.open.connected"), null);
				}, () -> Actions.reply(event, translate(event.getGuild(), "record.open.member-not-matched"), null));
		return CommandResult.SUCCESS;
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.record.start.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("start");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.record.start.description");
	}
	
	@Override
	public void addHelp(@NonNull Guild guild, @NonNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("user", translate(guild, "command.record.start.help.user"), false);
	}
	
	@Override
	public @NonNull String getCommandUsage(){
		return super.getCommandUsage() + " <@user...>";
	}
}
