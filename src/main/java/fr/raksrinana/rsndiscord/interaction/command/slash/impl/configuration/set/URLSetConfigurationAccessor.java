package fr.raksrinana.rsndiscord.interaction.command.slash.impl.configuration.set;

import fr.raksrinana.rsndiscord.interaction.command.slash.impl.configuration.SetConfigurationAccessor;
import fr.raksrinana.rsndiscord.settings.impl.GuildConfiguration;
import org.jetbrains.annotations.NotNull;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class URLSetConfigurationAccessor extends SetConfigurationAccessor<URL>{
	public URLSetConfigurationAccessor(String name, Function<GuildConfiguration, Set<URL>> getter){
		super(name, getter);
	}
	
	public URLSetConfigurationAccessor(String name, Function<GuildConfiguration, Set<URL>> getter, BiConsumer<GuildConfiguration, URL> setter){
		super(name, getter, setter);
	}
	
	@Override
	protected URL fromString(@NotNull String value){
		try{
			return URI.create(value).toURL();
		}
		catch(MalformedURLException e){
			throw new IllegalArgumentException(e);
		}
	}
}
