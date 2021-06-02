package fr.raksrinana.rsndiscord.command2.impl.external.anilist;

import fr.raksrinana.rsndiscord.api.anilist.query.MediaPagedQuery;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command2.base.group.SubCommand;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.List;
import static fr.raksrinana.rsndiscord.command.CommandResult.FAILED;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static net.dv8tion.jda.api.interactions.commands.OptionType.INTEGER;

public class InfoCommand extends SubCommand{
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
	public CommandResult execute(@NotNull SlashCommandEvent event){
		var guild = event.getGuild();
		var mediaId = getOptionAsInt(event.getOption(MEDIA_OPTION_ID)).orElseThrow();
		
		try{
			var medias = new MediaPagedQuery(mediaId).getResult(event.getMember());
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
			Log.getLogger(event.getGuild()).error("Failed to get media with id {}", mediaId, e);
			return FAILED;
		}
		return SUCCESS;
	}
}
