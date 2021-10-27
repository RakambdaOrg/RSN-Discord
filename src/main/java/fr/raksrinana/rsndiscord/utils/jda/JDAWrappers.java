package fr.raksrinana.rsndiscord.utils.jda;

import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import fr.raksrinana.rsndiscord.utils.jda.wrappers.EditPresenceWrapper;
import fr.raksrinana.rsndiscord.utils.jda.wrappers.channel.CreateTextChannelWrapper;
import fr.raksrinana.rsndiscord.utils.jda.wrappers.channel.DeleteChannelWrapper;
import fr.raksrinana.rsndiscord.utils.jda.wrappers.channel.EditChannelWrapper;
import fr.raksrinana.rsndiscord.utils.jda.wrappers.guild.LeaveGuildWrapper;
import fr.raksrinana.rsndiscord.utils.jda.wrappers.member.*;
import fr.raksrinana.rsndiscord.utils.jda.wrappers.message.*;
import fr.raksrinana.rsndiscord.utils.jda.wrappers.role.SetColorWrapper;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.interactions.components.Component;
import net.dv8tion.jda.api.interactions.components.ComponentLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.awt.Color;

@Log4j2
public class JDAWrappers{
	@NotNull
	public static MessageWrapper message(@NotNull MessageChannel channel, @NotNull String message){
		return new MessageWrapper(channel, message);
	}
	
	@NotNull
	public static MessageWrapper message(@NotNull MessageChannel channel, @NotNull MessageEmbed embed){
		return new MessageWrapper(channel, embed);
	}
	
	@NotNull
	public static MessageWrapper message(@NotNull MessageChannel channel, @NotNull Message message){
		return new MessageWrapper(channel, message);
	}
	
	@NotNull
	public static MessageWrapper message(@NotNull GenericMessageEvent event, @NotNull String message){
		return message(event.getChannel(), message);
	}
	
	@NotNull
	public static EditMessageWrapper edit(@NotNull Message message, @NotNull String content){
		return new EditMessageWrapper(message, content);
	}
	
	@NotNull
	public static EditMessageWrapper edit(@NotNull Message message, @NotNull MessageEmbed embed){
		return new EditMessageWrapper(message, embed);
	}
	
	@NotNull
	public static EditMessageWrapper editComponents(@NotNull Message message, @NotNull Component... components){
		return new EditMessageWrapper(message).addActionRow(components);
	}
	
	@NotNull
	public static EditMessageWrapper removeComponents(@NotNull Message message){
		return new EditMessageWrapper(message).clearActionRows();
	}
	
	@NotNull
	public static MessageWrapper reply(@NotNull Message replyToMessage, @NotNull String message){
		return new MessageWrapper(replyToMessage.getChannel(), message).replyTo(replyToMessage);
	}
	
	@NotNull
	public static AddReactionWrapper addReaction(@NotNull Message message, @NotNull String emote){
		return new AddReactionWrapper(message, emote);
	}
	
	@NotNull
	public static AddReactionWrapper addReaction(@NotNull Message message, @NotNull Emote emote){
		return new AddReactionWrapper(message, emote);
	}
	
	@NotNull
	public static AddReactionWrapper addReaction(@NotNull Message message, @NotNull BasicEmotes emote){
		return addReaction(message, emote.getValue());
	}
	
	@NotNull
	public static RemoveReactionWrapper removeReaction(@NotNull Message message, @NotNull String emote){
		return new RemoveReactionWrapper(message, emote);
	}
	
	@NotNull
	public static RemoveReactionWrapper removeReaction(MessageReaction messageReaction){
		return new RemoveReactionWrapper(messageReaction);
	}
	
	@NotNull
	public static RemoveUserReactionWrapper removeReaction(@NotNull MessageReaction messageReaction, @NotNull User user){
		return new RemoveUserReactionWrapper(messageReaction, user);
	}
	
	@NotNull
	public static ClearReactionsWrapper clearReactions(@NotNull Message message){
		return new ClearReactionsWrapper(message);
	}
	
	@NotNull
	public static PinMessageWrapper pin(@NotNull Message message){
		return new PinMessageWrapper(message);
	}
	
	@NotNull
	public static UnpinMessageWrapper unpin(@NotNull Message message){
		return new UnpinMessageWrapper(message);
	}
	
	@NotNull
	public static DeleteMessageWrapper delete(@NotNull Message message){
		return new DeleteMessageWrapper(message);
	}
	
	@NotNull
	public static DeleteMessageWrapper delete(@NotNull MessageChannel channel, long messageId){
		return new DeleteMessageWrapper(channel, messageId);
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
	
	@NotNull
	public static InteractionEditMessageWrapper removeComponents(@NotNull GenericInteractionCreateEvent event){
		return new InteractionEditMessageWrapper(event.getGuild(), event.getHook()).clearActionRows();
	}
	
	@NotNull
	public static EditChannelWrapper edit(@NotNull TextChannel channel){
		return new EditChannelWrapper(channel.getGuild(), channel);
	}
	
	@NotNull
	public static DeleteChannelWrapper delete(@NotNull TextChannel channel){
		return new DeleteChannelWrapper(channel);
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
	public static MuteWrapper mute(@NotNull Member member, boolean state){
		return new MuteWrapper(member.getGuild(), member, state);
	}
	
	@NotNull
	public static DeafenWrapper deafen(@NotNull Member member, boolean state){
		return new DeafenWrapper(member.getGuild(), member, state);
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
	public static ModifyNicknameWrapper modifyNickname(@NotNull Member target, @Nullable String nickname){
		return new ModifyNicknameWrapper(target.getGuild(), target, nickname);
	}
	
	@NotNull
	public static SetColorWrapper setColor(@NotNull Role role, int color){
		return new SetColorWrapper(role, new Color(color));
	}
	
	@NotNull
	public static SetColorWrapper setColor(@NotNull Role role, @Nullable Color color){
		return new SetColorWrapper(role, color);
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
}
