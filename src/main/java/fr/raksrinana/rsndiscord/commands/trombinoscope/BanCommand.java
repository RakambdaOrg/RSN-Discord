package fr.raksrinana.rsndiscord.commands.trombinoscope;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;

import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

class BanCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	BanCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NonNull Guild guild, @NonNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("user", translate(guild, "command.trombinoscope.ban.help.user"), true);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		if(event.getMessage().getMentionedMembers().isEmpty()){
			return CommandResult.BAD_ARGUMENTS;
		}
		var trombinoscope = Settings.get(event.getGuild()).getTrombinoscope();
		var target = event.getMessage().getMentionedMembers().get(0);
		trombinoscope.banUser(target.getUser());
		trombinoscope.removeUser(target.getUser());
		trombinoscope.getPosterRole()
				.flatMap(RoleConfiguration::getRole)
				.ifPresent(role -> Actions.removeRole(target, role));
		Actions.reply(event, translate(event.getGuild(), "trombinoscope.banned", target.getAsMention()), null);
		return CommandResult.SUCCESS;
	}
	
	@Override
	public @NonNull String getCommandUsage(){
		return super.getCommandUsage() + " <user>";
	}
	
	@Override
	public @NonNull AccessLevel getAccessLevel(){
		return AccessLevel.MODERATOR;
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.trombinoscope.ban.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("ban", "b");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.trombinoscope.ban.description");
	}
}
