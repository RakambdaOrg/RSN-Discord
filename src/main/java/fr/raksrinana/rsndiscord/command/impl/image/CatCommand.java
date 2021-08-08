package fr.raksrinana.rsndiscord.command.impl.image;

import fr.raksrinana.rsndiscord.api.thecatapi.TheCatApi;
import fr.raksrinana.rsndiscord.api.thecatapi.data.Breed;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command.base.group.SubCommand;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.NotNull;
import java.util.stream.Collectors;
import static fr.raksrinana.rsndiscord.command.CommandResult.HANDLED;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.awt.Color.GREEN;

public class CatCommand extends SubCommand{
	@Override
	public @NotNull String getId(){
		return "cat";
	}
	
	@Override
	public @NotNull String getShortDescription(){
		return "Get a random cat image";
	}
	
	@Override
	public boolean replyEphemeral(){
		return false;
	}
	
	@Override
	@NotNull
	public CommandResult execute(@NotNull SlashCommandEvent event){
		var guild = event.getGuild();
		var author = event.getUser();
		
		TheCatApi.getRandomCat().ifPresentOrElse(
				cat -> {
					var embed = new EmbedBuilder()
							.setAuthor(author.getName(), null, author.getAvatarUrl())
							.setColor(GREEN);
					
					if(!cat.getBreeds().isEmpty()){
						var breeds = cat.getBreeds().stream()
								.map(Breed::getName)
								.collect(Collectors.joining(" "));
						embed.setDescription(breeds);
					}
					
					embed.setImage(cat.getUrl().toString())
							.setFooter(cat.getId());
					
					JDAWrappers.edit(event, embed.build()).submit();
				},
				() -> JDAWrappers.edit(event, translate(guild, "image.cat.error")).submitAndDelete(5));
		
		return HANDLED;
	}
}
