package fr.raksrinana.rsndiscord.command.impl.image;

import fr.raksrinana.rsndiscord.api.dog.DogApi;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command.base.group.SubCommand;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.NotNull;
import static fr.raksrinana.rsndiscord.command.CommandResult.HANDLED;
import static java.awt.Color.GREEN;

public class DogCommand extends SubCommand{
	@Override
	@NotNull
	public String getId(){
		return "dog";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Get a random dog image";
	}
	
	@Override
	public boolean replyEphemeral(){
		return false;
	}
	
	@Override
	@NotNull
	public CommandResult executeGuild(@NotNull SlashCommandEvent event, @NotNull Guild guild, @NotNull Member member){
		return execute(event);
	}
	
	@Override
	@NotNull
	public CommandResult executeUser(@NotNull SlashCommandEvent event){
		return execute(event);
	}
	
	@NotNull
	private CommandResult execute(@NotNull SlashCommandEvent event){
		var author = event.getUser();
		
		var embed = new EmbedBuilder().setAuthor(author.getName(), null, author.getAvatarUrl())
				.setColor(GREEN)
				.setImage(DogApi.getDogPictureURL(event.getGuild()))
				.build();
		
		JDAWrappers.edit(event, embed).submit();
		return HANDLED;
	}
}
