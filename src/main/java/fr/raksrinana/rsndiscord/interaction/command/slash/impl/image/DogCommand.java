package fr.raksrinana.rsndiscord.interaction.command.slash.impl.image;

import fr.raksrinana.rsndiscord.api.dog.DogApi;
import fr.raksrinana.rsndiscord.interaction.command.CommandResult;
import fr.raksrinana.rsndiscord.interaction.command.slash.base.group.SubSlashCommand;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.jetbrains.annotations.NotNull;
import static fr.raksrinana.rsndiscord.interaction.command.CommandResult.HANDLED;
import static java.awt.Color.GREEN;

public class DogCommand extends SubSlashCommand{
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
	public CommandResult executeGuild(@NotNull SlashCommandInteraction event, @NotNull Guild guild, @NotNull Member member){
		return execute(event);
	}
	
	@Override
	@NotNull
	public CommandResult executeUser(@NotNull SlashCommandInteraction event){
		return execute(event);
	}
	
	@NotNull
	private CommandResult execute(@NotNull SlashCommandInteraction event){
		var author = event.getUser();
		
		var embed = new EmbedBuilder().setAuthor(author.getName(), null, author.getAvatarUrl())
				.setColor(GREEN)
				.setImage(DogApi.getDogPictureURL(event.getGuild()))
				.build();
		
		JDAWrappers.edit(event, embed).submit();
		return HANDLED;
	}
}
