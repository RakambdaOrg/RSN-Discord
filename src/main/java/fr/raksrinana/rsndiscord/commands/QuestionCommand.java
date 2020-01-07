package fr.raksrinana.rsndiscord.commands;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

@BotCommand
public class QuestionCommand extends BasicCommand{
	private static int nextId = 0;
	
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Message", "The question you want to ask", false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		if(args.isEmpty()){
			return CommandResult.BAD_ARGUMENTS;
		}
		else if("message".equalsIgnoreCase(args.peek()) || "question".equalsIgnoreCase(args.peek())){
			Actions.replyPrivate(event.getGuild(), event.getAuthor(), "Please ask a question, and not 'message'", null);
		}
		else{
			final var ID = ++nextId;
			final var builder = new EmbedBuilder();
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			builder.setColor(Color.GREEN);
			builder.setTitle("New question");
			builder.addField("ID", String.valueOf(ID), true);
			builder.addField("User", event.getAuthor().getAsMention(), true);
			builder.addField("Question", String.join(" ", args), false);
			Settings.get(event.getGuild()).getQuestionsConfiguration().getInputChannel().flatMap(ChannelConfiguration::getChannel).ifPresentOrElse(channel -> Actions.reply(event, "", builder.build()).thenAccept(message -> {
				Actions.addReaction(message, BasicEmotes.CHECK_OK.getValue());
				Actions.addReaction(message, BasicEmotes.CROSS_NO.getValue());
				Actions.replyPrivate(event.getGuild(), event.getAuthor(), "Ok, you question have been added to the queue (ID: " + ID + "): " + String.join(" ", args), null);
			}), () -> Actions.replyPrivate(event.getGuild(), event.getAuthor(), "This feature isn't configured yet", null));
		}
		return CommandResult.SUCCESS;
	}
	
	@NonNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <message...>";
	}
	
	@NonNull
	@Override
	public String getName(){
		return "FAQ";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("question", "q");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Ask a question for the FAQ";
	}
}
