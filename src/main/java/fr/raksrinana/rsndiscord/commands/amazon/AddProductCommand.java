package fr.raksrinana.rsndiscord.commands.amazon;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.AmazonTrackingConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.amazon.AmazonUtils;
import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class AddProductCommand extends BasicCommand{
	private static final Pattern PRODUCT_PAGE = Pattern.compile("https://www\\.amazon\\.fr/gp/product/.*");
	
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	AddProductCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("url", "The amazon product url", false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		if(args.isEmpty()){
			return CommandResult.BAD_ARGUMENTS;
		}
		final var arg1 = args.pop();
		try{
			if(PRODUCT_PAGE.matcher(arg1).matches()){
				final var url = new URL(arg1);
				AmazonUtils.getProduct(url).ifPresentOrElse(product -> {
					final var tracking = new AmazonTrackingConfiguration(event.getAuthor(), url, event.getChannel(), product.getPrice());
					Settings.get(event.getGuild()).getAmazonTracking().add(tracking);
					AmazonUtils.sendMessage(tracking, product);
				}, () -> Actions.reply(event, "Couldn't access product information. This product won't be tracked.", null));
			}
		}
		catch(final MalformedURLException e){
			Actions.reply(event, "Failed to parse given URL", null);
			Log.getLogger(event.getGuild()).warn("Failed to parse amazon URL: {}", arg1, e);
		}
		return CommandResult.SUCCESS;
	}
	
	@NonNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <url>";
	}
	
	@NonNull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Add product";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("a", "add", "t", "track");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Adds a product from amazon to be tracked";
	}
}
