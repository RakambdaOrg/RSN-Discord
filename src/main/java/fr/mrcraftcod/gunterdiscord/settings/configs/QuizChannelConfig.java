package fr.mrcraftcod.gunterdiscord.settings.configs;

import fr.mrcraftcod.gunterdiscord.settings.ValueConfiguration;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-15
 */
public class QuizChannelConfig extends ValueConfiguration
{
	@Override
	public String getName()
	{
		return "quizChannel";
	}
	
	@Override
	public void setValue(Object value)
	{
		try
		{
			super.setValue(Long.parseLong(value.toString()));
		}
		catch(Exception e)
		{
		}
	}
}
