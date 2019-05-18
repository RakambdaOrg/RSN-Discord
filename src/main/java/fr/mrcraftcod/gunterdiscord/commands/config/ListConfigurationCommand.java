package fr.mrcraftcod.gunterdiscord.commands.config;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.settings.Configuration;
import fr.mrcraftcod.gunterdiscord.settings.Settings;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.awt.Color;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-05-27
 */
public class ListConfigurationCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	ListConfigurationCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NotNull final Guild guild, @NotNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
	}
	
	@Override
	public CommandResult execute(final GuildMessageReceivedEvent event, @NotNull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		final var builder = new EmbedBuilder();
		builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
		builder.setColor(Color.ORANGE);
		builder.setTitle("Configurations");
		builder.addField("Configurations available", Arrays.stream(Settings.SETTINGS).map(Configuration::getName).sorted().collect(Collectors.joining(", ")), false);
		Actions.reply(event, builder.build());
		return CommandResult.SUCCESS;
	}
	
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ADMIN;
	}
	
	@Override
	public String getName(){
		return "Lists configuration";
	}
	
	@Override
	public List<String> getCommand(){
		return List.of("list", "l");
	}
	
	@Override
	public String getDescription(){
		return "Lists the available configurations";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
