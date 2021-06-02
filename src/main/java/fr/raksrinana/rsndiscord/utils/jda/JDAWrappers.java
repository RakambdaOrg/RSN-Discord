package fr.raksrinana.rsndiscord.utils.jda;

import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import fr.raksrinana.rsndiscord.utils.jda.wrappers.EditPresenceWrapper;
import fr.raksrinana.rsndiscord.utils.jda.wrappers.channel.CreateTextChannelWrapper;
import fr.raksrinana.rsndiscord.utils.jda.wrappers.channel.DeleteChannelWrapper;
import fr.raksrinana.rsndiscord.utils.jda.wrappers.channel.EditChannelWrapper;
import fr.raksrinana.rsndiscord.utils.jda.wrappers.guild.LeaveGuildWrapper;
import fr.raksrinana.rsndiscord.utils.jda.wrappers.member.*;
import fr.raksrinana.rsndiscord.utils.jda.wrappers.message.*;
import fr.raksrinana.rsndiscord.utils.jda.wrappers.role.SetColorWrapper;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.ComponentLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.awt.Color;
import java.util.Objects;
import static net.dv8tion.jda.api.entities.ChannelType.TEXT;

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
	public static MessageWrapper message(@NotNull GuildMessageReactionAddEvent event, @NotNull String message){
		return message(event.getChannel(), message);
	}
	
	@NotNull
	public static MessageWrapper message(@NotNull PrivateMessageReceivedEvent event, @NotNull String message){
		return message(event.getChannel(), message);
	}
	
	@NotNull
	public static MessageWrapper message(@NotNull TextChannel channel, @NotNull Message message){
		return new MessageWrapper(channel.getGuild(), channel, message);
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
	public static AddReactionWrapper addReaction(@NotNull Message message, @NotNull String emote){
		if(message.isFromGuild()){
			return new AddReactionWrapper(message.getGuild(), message, emote);
		}
		
		return new AddReactionWrapper(null, message, emote);
	}
	
	@NotNull
	public static AddReactionWrapper addReaction(@NotNull Message message, @NotNull BasicEmotes emote){
		return addReaction(message, emote.getValue());
	}
	
	@NotNull
	public static AddReactionWrapper addReaction(@NotNull Message message, @NotNull Emote emote){
		if(message.isFromGuild()){
			return new AddReactionWrapper(message.getGuild(), message, emote);
		}
		
		return new AddReactionWrapper(null, message, emote);
	}
	
	@NotNull
	public static DeleteMessageWrapper delete(@NotNull Message message){
		if(message.isFromGuild()){
			return new DeleteMessageWrapper(message.getGuild(), message);
		}
		
		return new DeleteMessageWrapper(null, message);
	}
	
	@NotNull
	public static DeleteChannelWrapper delete(@NotNull TextChannel channel){
		return new DeleteChannelWrapper(channel.getGuild(), channel);
	}
	
	@NotNull
	public static DeleteMessageWrapper delete(@NotNull TextChannel channel, long messageId){
		return new DeleteMessageWrapper(channel.getGuild(), channel, messageId);
	}
	
	@NotNull
	public static SetColorWrapper setColor(@NotNull Role role, int color){
		return new SetColorWrapper(role.getGuild(), role, new Color(color));
	}
	
	@NotNull
	public static SetColorWrapper setColor(@NotNull Role role, @Nullable Color color){
		return new SetColorWrapper(role.getGuild(), role, color);
	}
	
	@NotNull
	public static MuteWrapper mute(@NotNull Member member, boolean state){
		return new MuteWrapper(member.getGuild(), member, state);
	}
	
	@NotNull
	public static DeafenWrapper deafen(@NotNull Member member, boolean state){
		return new DeafenWrapper(member.getGuild(), member, state);
	}
	
	@NotNull
	public static EditChannelWrapper edit(@NotNull TextChannel channel){
		return new EditChannelWrapper(channel.getGuild(), channel);
	}
	
	@NotNull
	public static RemoveReactionWrapper removeReaction(@NotNull Message message, @NotNull String emote){
		if(message.isFromGuild()){
			return new RemoveReactionWrapper(message.getGuild(), message, emote);
		}
		
		return new RemoveReactionWrapper(null, message, emote);
	}
	
	@NotNull
	public static RemoveReactionWrapper removeReaction(MessageReaction messageReaction){
		if(Objects.equals(messageReaction.getChannelType(), TEXT)){
			return new RemoveReactionWrapper(messageReaction.getGuild(), messageReaction);
		}
		
		return new RemoveReactionWrapper(null, messageReaction);
	}
	
	@NotNull
	public static RemoveUserReactionWrapper removeReaction(@NotNull MessageReaction messageReaction, @NotNull User user){
		if(Objects.equals(messageReaction.getChannelType(), TEXT)){
			return new RemoveUserReactionWrapper(messageReaction.getGuild(), messageReaction, user);
		}
		
		return new RemoveUserReactionWrapper(null, messageReaction, user);
	}
	
	@NotNull
	public static PinMessageWrapper pin(@NotNull Message message){
		if(message.isFromGuild()){
			return new PinMessageWrapper(message.getGuild(), message);
		}
		
		return new PinMessageWrapper(null, message);
	}
	
	@NotNull
	public static UnpinMessageWrapper unpin(@NotNull Message message){
		if(message.isFromGuild()){
			return new UnpinMessageWrapper(message.getGuild(), message);
		}
		
		return new UnpinMessageWrapper(null, message);
	}
	
	@NotNull
	public static EditMessageWrapper edit(@NotNull Message message, @NotNull String content){
		if(message.isFromGuild()){
			return new EditMessageWrapper(message.getGuild(), message, content);
		}
		
		return new EditMessageWrapper(null, message, content);
	}
	
	@NotNull
	public static EditMessageWrapper edit(@NotNull Message message, @NotNull MessageEmbed embed){
		if(message.isFromGuild()){
			return new EditMessageWrapper(message.getGuild(), message, embed);
		}
		
		return new EditMessageWrapper(null, message, embed);
	}
	
	@NotNull
	public static ClearReactionsWrapper clearReactions(@NotNull Message message){
		if(message.isFromGuild()){
			return new ClearReactionsWrapper(message.getGuild(), message);
		}
		
		return new ClearReactionsWrapper(null, message);
	}
	
	@NotNull
	public static CreateTextChannelWrapper createTextChannel(@NotNull Guild guild, @NotNull String name){
		return new CreateTextChannelWrapper(guild, name);
	}
	
	@NotNull
	public static UnbanWrapper unban(@NotNull Guild guild, @NotNull String userId){
		return new UnbanWrapper(guild, userId);
	}
	
	@NotNull
	public static LeaveGuildWrapper leave(@NotNull Guild guild){
		return new LeaveGuildWrapper(guild);
	}
	
	@NotNull
	public static EditPresenceWrapper editPresence(){
		return new EditPresenceWrapper();
	}
	
	@NotNull
	public static InteractionEditMessageWrapper edit(@NotNull GenericInteractionCreateEvent event, @NotNull String messsage){
		return new InteractionEditMessageWrapper(event.getGuild(), event.getHook(), messsage);
	}
	
	@NotNull
	public static InteractionEditMessageWrapper edit(@NotNull GenericInteractionCreateEvent event, @NotNull MessageEmbed embed){
		return new InteractionEditMessageWrapper(event.getGuild(), event.getHook(), embed);
	}
	
	@NotNull
	public static InteractionEditMessageWrapper edit(@NotNull GenericInteractionCreateEvent event, @NotNull ComponentLayout... layouts){
		return new InteractionEditMessageWrapper(event.getGuild(), event.getHook(), layouts);
	}
	
	@NotNull
	public static InteractionNewMessageWrapper reply(@NotNull GenericInteractionCreateEvent event, @NotNull String messsage){
		return new InteractionNewMessageWrapper(event.getGuild(), event.getHook(), messsage);
	}
	
	@NotNull
	public static InteractionNewMessageWrapper reply(@NotNull GenericInteractionCreateEvent event, @NotNull MessageEmbed embed){
		return new InteractionNewMessageWrapper(event.getGuild(), event.getHook(), embed);
	}
}
