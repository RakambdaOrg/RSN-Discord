package fr.raksrinana.rsndiscord.command.impl.settings.helpers;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.settings.types.CategoryConfiguration;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;
import static java.util.Optional.ofNullable;

public abstract class CategoryConfigurationCommand extends ValueConfigurationCommand<CategoryConfiguration>{
	protected CategoryConfigurationCommand(Command parent){
		super(parent);
	}
	
	@Override
	protected CategoryConfiguration extractValue(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		if(args.isEmpty()){
			throw new IllegalArgumentException("Please mention the role");
		}
		return ofNullable(event.getGuild().getCategoryById(args.pop()))
				.map(CategoryConfiguration::new)
				.orElseThrow(() -> new IllegalArgumentException("Invalid category id"));
	}
	
	@Override
	protected @NotNull String getValueName(){
		return "Category";
	}
}
