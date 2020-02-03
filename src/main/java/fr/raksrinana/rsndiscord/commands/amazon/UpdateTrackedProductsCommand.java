package fr.raksrinana.rsndiscord.commands.amazon;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.runners.AmazonPriceCheckerScheduledRunner;
import lombok.NonNull;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;

public class UpdateTrackedProductsCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	UpdateTrackedProductsCommand(final Command parent){
		super(parent);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		new AmazonPriceCheckerScheduledRunner(event.getJDA()).execute();
		return CommandResult.SUCCESS;
	}
	
	@NonNull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Update products";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("u", "update");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Updates the tracked products";
	}
}
