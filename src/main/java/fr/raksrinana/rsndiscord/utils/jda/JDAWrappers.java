package fr.raksrinana.rsndiscord.utils.jda;

import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import fr.raksrinana.rsndiscord.utils.jda.wrappers.EditPresenceWrapper;
import fr.raksrinana.rsndiscord.utils.jda.wrappers.channel.DeleteChannelWrapper;
import fr.raksrinana.rsndiscord.utils.jda.wrappers.channel.EditChannelWrapper;
import fr.raksrinana.rsndiscord.utils.jda.wrappers.channel.HistoryChannelWrapper;
import fr.raksrinana.rsndiscord.utils.jda.wrappers.interaction.AutoCompleteWrapper;
import fr.raksrinana.rsndiscord.utils.jda.wrappers.member.AddRoleWrapper;
import fr.raksrinana.rsndiscord.utils.jda.wrappers.member.BanWrapper;
import fr.raksrinana.rsndiscord.utils.jda.wrappers.member.DeafenWrapper;
import fr.raksrinana.rsndiscord.utils.jda.wrappers.member.KickWrapper;
import fr.raksrinana.rsndiscord.utils.jda.wrappers.member.ModifyNicknameWrapper;
import fr.raksrinana.rsndiscord.utils.jda.wrappers.member.MuteWrapper;
import fr.raksrinana.rsndiscord.utils.jda.wrappers.member.RemoveRoleWrapper;
import fr.raksrinana.rsndiscord.utils.jda.wrappers.member.UnbanWrapper;
import fr.raksrinana.rsndiscord.utils.jda.wrappers.message.AddReactionWrapper;
import fr.raksrinana.rsndiscord.utils.jda.wrappers.message.ClearReactionsWrapper;
import fr.raksrinana.rsndiscord.utils.jda.wrappers.message.DeleteMessageWrapper;
import fr.raksrinana.rsndiscord.utils.jda.wrappers.message.EditMessageWrapper;
import fr.raksrinana.rsndiscord.utils.jda.wrappers.message.InteractionEditMessageWrapper;
import fr.raksrinana.rsndiscord.utils.jda.wrappers.message.InteractionNewMessageWrapper;
import fr.raksrinana.rsndiscord.utils.jda.wrappers.message.InteractionReplyModalWrapper;
import fr.raksrinana.rsndiscord.utils.jda.wrappers.message.MessageWrapper;
import fr.raksrinana.rsndiscord.utils.jda.wrappers.message.RemoveAllReactionWrapper;
import fr.raksrinana.rsndiscord.utils.jda.wrappers.message.UnpinMessageWrapper;
import fr.raksrinana.rsndiscord.utils.jda.wrappers.role.SetColorWrapper;
import fr.raksrinana.rsndiscord.utils.jda.wrappers.thread.AddThreadMemberWrapper;
import fr.raksrinana.rsndiscord.utils.jda.wrappers.thread.CreateThreadWrapper;
import fr.raksrinana.rsndiscord.utils.jda.wrappers.thread.DeleteThreadWrapper;
import fr.raksrinana.rsndiscord.utils.jda.wrappers.thread.EditThreadWrapper;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.interactions.callbacks.IModalCallback;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.Modal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.awt.Color;
import java.util.Collection;
import java.util.List;

@Log4j2
public class JDAWrappers{
	@NotNull
	public static MessageWrapper message(@NotNull MessageChannel channel, @NotNull String message){
		return new MessageWrapper(channel, message);
	}
	
	@NotNull
	public static MessageWrapper message(@NotNull MessageChannel channel, @NotNull LayoutComponent component, @NotNull LayoutComponent... components){
		return new MessageWrapper(channel, component, components);
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
	public static EditMessageWrapper editComponents(@NotNull Message message, @NotNull ItemComponent... components){
		return new EditMessageWrapper(message).setActionRow(components);
	}
	
	@NotNull
	public static EditMessageWrapper editComponents(@NotNull Message message, @NotNull List<ItemComponent> components){
		return new EditMessageWrapper(message).setActionRow(components);
	}
	
	@NotNull
	public static MessageWrapper reply(@NotNull Message replyToMessage, @NotNull String message){
		return new MessageWrapper(replyToMessage.getChannel(), message).replyTo(replyToMessage);
	}
	
	@NotNull
	public static AddReactionWrapper addReaction(@NotNull Message message, @NotNull Emoji emote){
		return new AddReactionWrapper(message, emote);
	}
	
	@NotNull
	public static AddReactionWrapper addReaction(@NotNull Message message, @NotNull BasicEmotes emote){
		return addReaction(message, emote.asEmoji());
	}
	
	@NotNull
	public static RemoveAllReactionWrapper removeAllReactions(@NotNull MessageReaction messageReaction){
		return new RemoveAllReactionWrapper(messageReaction);
	}
	
	@NotNull
	public static RemoveAllReactionWrapper removeAllReactions(@NotNull Message message, @NotNull Emoji emoji){
		return new RemoveAllReactionWrapper(message, emoji);
	}
	
	@NotNull
	public static ClearReactionsWrapper clearReactions(@NotNull Message message){
		return new ClearReactionsWrapper(message);
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
	public static CreateThreadWrapper createThread(@NotNull Message message, @NotNull String name){
		return new CreateThreadWrapper(message, name);
	}
	
	@NotNull
	public static EditThreadWrapper editThread(@NotNull ThreadChannel threadChannel){
		return new EditThreadWrapper(threadChannel);
	}
	
	@NotNull
	public static AddThreadMemberWrapper addThreadMember(@NotNull ThreadChannel thread, @NotNull User user){
		return new AddThreadMemberWrapper(thread, user);
	}
	
	@NotNull
	public static AddThreadMemberWrapper addThreadMember(@NotNull ThreadChannel thread, @NotNull Member member){
		return new AddThreadMemberWrapper(thread, member);
	}
	
	@NotNull
	public static DeleteThreadWrapper delete(@NotNull ThreadChannel thread){
		return new DeleteThreadWrapper(thread);
	}
	
	@NotNull
	public static InteractionEditMessageWrapper edit(@NotNull IReplyCallback event, @NotNull String messsage){
		return new InteractionEditMessageWrapper(event.getGuild(), event.getHook(), messsage);
	}
	
	@NotNull
	public static InteractionEditMessageWrapper edit(@NotNull IReplyCallback event, @NotNull MessageEmbed embed){
		return new InteractionEditMessageWrapper(event.getGuild(), event.getHook(), embed);
	}
	
	@NotNull
	public static InteractionEditMessageWrapper edit(@NotNull IReplyCallback event, @NotNull LayoutComponent... layouts){
		return new InteractionEditMessageWrapper(event.getGuild(), event.getHook(), layouts);
	}
	
	@NotNull
	public static InteractionNewMessageWrapper reply(@NotNull IReplyCallback event, @NotNull String messsage){
		return new InteractionNewMessageWrapper(event.getGuild(), event.getHook(), messsage);
	}
	
	@NotNull
	public static InteractionNewMessageWrapper reply(@NotNull IReplyCallback event, @NotNull MessageEmbed embed){
		return new InteractionNewMessageWrapper(event.getGuild(), event.getHook(), embed);
	}
	
	@NotNull
	public static InteractionReplyModalWrapper reply(@NotNull IModalCallback event, @NotNull Modal modal){
		return new InteractionReplyModalWrapper(event.getGuild(), event, modal);
	}
	
	@NotNull
	public static InteractionEditMessageWrapper removeComponents(@NotNull IReplyCallback event){
		return new InteractionEditMessageWrapper(event.getGuild(), event.getHook()).clearActionRows();
	}
	
	@NotNull
	public static EditChannelWrapper edit(@NotNull TextChannel channel){
		return new EditChannelWrapper(channel);
	}
	
	@NotNull
	public static DeleteChannelWrapper delete(@NotNull TextChannel channel){
		return new DeleteChannelWrapper(channel);
	}
	
	@NotNull
	public static KickWrapper kick(@NotNull Member member){
		return new KickWrapper(member.getGuild(), member);
	}
	
	@NotNull
	public static BanWrapper ban(@NotNull Member target, int deletionDays){
		return new BanWrapper(target.getGuild(), target, deletionDays);
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
	public static UnbanWrapper unban(@NotNull Guild guild, @NotNull UserSnowflake userId){
		return new UnbanWrapper(guild, userId);
	}
	
	@NotNull
	public static EditPresenceWrapper editPresence(){
		return new EditPresenceWrapper();
	}
	
	@NotNull
	public static AutoCompleteWrapper reply(@NotNull CommandAutoCompleteInteractionEvent event, @NotNull Collection<Command.Choice> choices){
		return new AutoCompleteWrapper(event, choices);
	}
	
	@NotNull
	public static HistoryChannelWrapper history(@NotNull MessageChannel channel){
		return new HistoryChannelWrapper(channel);
	}
}
