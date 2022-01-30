package fr.raksrinana.rsndiscord.interaction.command.slash.impl.external.anilist;

import fr.raksrinana.rsndiscord.api.anilist.query.MediaPagedQuery;
import fr.raksrinana.rsndiscord.interaction.command.CommandResult;
import fr.raksrinana.rsndiscord.interaction.command.slash.base.group.SubSlashCommand;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.List;
import static fr.raksrinana.rsndiscord.interaction.command.CommandResult.FAILED;
import static fr.raksrinana.rsndiscord.interaction.command.CommandResult.HANDLED;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static net.dv8tion.jda.api.interactions.commands.OptionType.INTEGER;

@Log4j2
public class InfoCommand extends SubSlashCommand{
	private static final String MEDIA_OPTION_ID = "media";
	
	@Override
	@NotNull
	public String getId(){
		return "info";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Get info about a media";
	}
	
	@Override
	@NotNull
	protected Collection<? extends OptionData> getOptions(){
		return List.of(new OptionData(INTEGER, MEDIA_OPTION_ID, "Media id").setRequired(true));
	}
	
	@Override
	@NotNull
	public CommandResult executeGuild(@NotNull SlashCommandInteractionEvent event, @NotNull Guild guild, @NotNull Member member){
		var mediaId = getOptionAsInt(event.getOption(MEDIA_OPTION_ID)).orElseThrow();
		
		try{
			var medias = new MediaPagedQuery(mediaId).getResult(member);
			if(!medias.isEmpty()){
				medias.forEach(media -> {
					var builder = new EmbedBuilder();
					media.fillEmbed(guild, builder);
					JDAWrappers.edit(event, builder.build()).submit();
				});
			}
			else{
				JDAWrappers.edit(event, translate(guild, "anilist.media-not-found")).submitAndDelete(5);
			}
		}
		catch(Exception e){
			log.error("Failed to get media with id {}", mediaId, e);
			return FAILED;
		}
		return HANDLED;
	}
}
