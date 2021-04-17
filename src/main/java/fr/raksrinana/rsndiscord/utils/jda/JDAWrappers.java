package fr.raksrinana.rsndiscord.utils.jda;

import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import fr.raksrinana.rsndiscord.utils.jda.wrappers.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.awt.Color;

public class JDAWrappers{
	@NotNull
	public static MessageWrapper message(@NotNull TextChannel channel, @NotNull String message){
		return new MessageWrapper(channel.getGuild(), channel, message);
	}
	
	@NotNull
	public static MessageWrapper message(@NotNull TextChannel channel, @NotNull MessageEmbed embed){
		return new MessageWrapper(channel.getGuild(), channel, embed);
	}
	
	@NotNull
	public static MessageWrapper message(@NotNull PrivateChannel channel, @NotNull String message){
		return new MessageWrapper(channel.getUser(), channel, message);
	}
	
	@NotNull
	public static MessageWrapper message(@NotNull GuildMessageReceivedEvent event, @NotNull String message){
		return message(event.getChannel(), message);
	}
	
	@NotNull
	public static MessageWrapper message(@NotNull GuildMessageReceivedEvent event, @NotNull MessageEmbed embed){
		return message(event.getChannel(), embed);
	}
	
	@NotNull
	public static KickWrapper kick(@NotNull Member member, @Nullable String reason){
		return new KickWrapper(member.getGuild(), member, reason);
	}
	
	@NotNull
	public static BanWrapper ban(@NotNull Member target, int deletionDays, @Nullable String reason){
		return new BanWrapper(target.getGuild(), target, deletionDays, reason);
	}
	
	@NotNull
	public static RemoveRoleWrapper removeRole(@NotNull Member member, @NotNull Role role){
		return new RemoveRoleWrapper(member.getGuild(), member, role);
	}
	
	@NotNull
	public static AddRoleWrapper addRole(@NotNull Member member, @NotNull Role role){
		return new AddRoleWrapper(member.getGuild(), member, role);
	}
	
	@NotNull
	public static MessageWrapper reply(@NotNull Message replyToMessage, @NotNull String message){
		if(replyToMessage.isFromGuild()){
			return new MessageWrapper(replyToMessage.getGuild(), replyToMessage.getChannel(), message).replyTo(replyToMessage);
		}
		if(replyToMessage.isFromType(ChannelType.PRIVATE)){
			var privateChannel = replyToMessage.getPrivateChannel();
			return new MessageWrapper(privateChannel.getUser(), privateChannel, message).replyTo(replyToMessage);
		}
		
		Log.getLogger().warn("Replying to a message with channel type {}", replyToMessage.getChannelType());
		return new MessageWrapper(null, replyToMessage.getChannel(), message).replyTo(replyToMessage);
	}
	
	@NotNull
	public static ModifyNicknameWrapper modifyNickname(@NotNull Member target, @Nullable String nickname){
		return new ModifyNicknameWrapper(target.getGuild(), target, nickname);
	}
	
	@NotNull
	public static AddReactionWrapper addReaction(@NotNull Message message, @NotNull BasicEmotes emote){
		if(message.isFromGuild()){
			return new AddReactionWrapper(message.getGuild(), message, emote.getValue());
		}
		
		return new AddReactionWrapper(null, message, emote.getValue());
	}
	
	@NotNull
	public static DeleteMessageWrapper delete(@NotNull Message message){
		if(message.isFromGuild()){
			return new DeleteMessageWrapper(message.getGuild(), message);
		}
		
		return new DeleteMessageWrapper(null, message);
	}
	
	@NotNull
	public static SetColorWrapper setColor(@NotNull Role role, int color){
		return new SetColorWrapper(role.getGuild(), role, new Color(color));
	}
	
	@NotNull
	public static SetColorWrapper setColor(@NotNull Role role, @Nullable Color color){
		return new SetColorWrapper(role.getGuild(), role, color);
	}
}
