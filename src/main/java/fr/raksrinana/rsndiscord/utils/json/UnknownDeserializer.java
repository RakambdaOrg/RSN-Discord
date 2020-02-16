package fr.raksrinana.rsndiscord.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.NonNull;
import java.io.IOException;

public class UnknownDeserializer extends JsonDeserializer<Object>{
	@Override
	public Object deserialize(@NonNull final JsonParser jsonParser, @NonNull final DeserializationContext deserializationContext) throws IOException{
		if(jsonParser.isNaN()){
			return null;
		}
		Actions.sendPrivateMessage(Utilities.RAKSRINANA_ACCOUNT, "Parsing error: Unknown field isn't null: " + jsonParser.getCurrentName(), null);
		Log.getLogger(null).error("Unknown field isn't null: " + jsonParser.getCurrentName());
		return getNullValue(deserializationContext);
	}
}
