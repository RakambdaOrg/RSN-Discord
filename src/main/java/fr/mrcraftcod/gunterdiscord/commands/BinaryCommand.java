package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CallableCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-12
 */
@CallableCommand
public class BinaryCommand extends BasicCommand
{
	@Override
	public CommandResult execute(MessageReceivedEvent event, LinkedList<String> args) throws Exception
	{
		super.execute(event, args);
		if(Objects.equals(args.poll(), "t"))
			Actions.reply(event, Arrays.stream(args.stream().collect(Collectors.joining(" ")).replace(" ", "").split("(?<=\\G.{8})")).map(s -> "" + ((char) Integer.parseInt(s, 2))).collect(Collectors.joining()));
		else
			Actions.reply(event, textToBinary(args.stream().collect(Collectors.joining(" "))));
		return CommandResult.SUCCESS;
	}
	
	private static String textToBinary(String input)
	{
		byte[] bytes = input.getBytes();
		StringBuilder binary = new StringBuilder();
		for(byte b : bytes)
		{
			int val = b;
			for(int i = 0; i < 8; i++)
			{
				binary.append((val & 128) == 0 ? 0 : 1);
				val <<= 1;
			}
		}
		return binary.toString();
	}
	
	@Override
	public AccessLevel getAccessLevel()
	{
		return AccessLevel.ADMIN;
	}
	
	@Override
	public int getScope()
	{
		return ChannelType.TEXT.getId();
	}
	
	@Override
	public String getName()
	{
		return "Binary";
	}
	
	@Override
	public List<String> getCommand()
	{
		return List.of("binary");
	}
	
	@Override
	public String getDescription()
	{
		return "Texte en binaire";
	}
}
