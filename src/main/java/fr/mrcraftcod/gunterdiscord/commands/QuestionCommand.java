package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.settings.configs.QuestionsChannelConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.BasicEmotes;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-12
 */
public class QuestionCommand extends BasicCommand{
	private static int nextId = 0;
	
	@Override
	public void addHelp(@NotNull final Guild guild, @NotNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Message", "La question que vous souhaitez poser", false);
	}
	
	@Override
	public CommandResult execute(@NotNull final MessageReceivedEvent event, @NotNull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		
		if(args.size() == 0){
			Actions.replyPrivate(event.getGuild(), event.getAuthor(), "Merci de poser une question");
		}
		else if(args.peek().equalsIgnoreCase("message") || args.peek().equalsIgnoreCase("question")){
			Actions.replyPrivate(event.getGuild(), event.getAuthor(), "Merci de poser une question, et pas 'message'");
		}
		else{
			final var ID = ++nextId;
			final var builder = new EmbedBuilder();
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			builder.setColor(Color.GREEN);
			builder.setTitle("Nouvelle question");
			builder.addField("ID", "" + ID, true);
			builder.addField("Utilisateur", event.getAuthor().getAsMention(), true);
			builder.addField("Question", String.join(" ", args), false);
			
			final var m = Actions.getMessage(new QuestionsChannelConfig(event.getGuild()).getObject(), builder.build());
			m.addReaction(BasicEmotes.CHECK_OK.getValue()).queue();
			m.addReaction(BasicEmotes.CROSS_NO.getValue()).queue();
			Actions.replyPrivate(event.getGuild(), event.getAuthor(), "Ok, ta question a été mise en file d'attente (ID: " + ID + "): " + String.join(" ", args));
		}
		return CommandResult.SUCCESS;
	}
	
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <message...>";
	}
	
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Override
	public String getName(){
		return "FAQ";
	}
	
	@Override
	public List<String> getCommand(){
		return List.of("question", "q");
	}
	
	@Override
	public String getDescription(){
		return "Pose une question pour la FAQ";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
