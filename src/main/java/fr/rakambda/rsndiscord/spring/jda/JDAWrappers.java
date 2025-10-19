package fr.rakambda.rsndiscord.spring.jda;

import fr.rakambda.rsndiscord.spring.jda.wrappers.EditPresenceWrapper;
import fr.rakambda.rsndiscord.spring.jda.wrappers.channel.DeleteChannelWrapper;
import fr.rakambda.rsndiscord.spring.jda.wrappers.channel.EditChannelWrapper;
import fr.rakambda.rsndiscord.spring.jda.wrappers.channel.HistoryChannelWrapper;
import fr.rakambda.rsndiscord.spring.jda.wrappers.interaction.AutoCompleteWrapper;
import fr.rakambda.rsndiscord.spring.jda.wrappers.interaction.EditHookWrapper;
import fr.rakambda.rsndiscord.spring.jda.wrappers.member.AddRoleWrapper;
import fr.rakambda.rsndiscord.spring.jda.wrappers.member.BanWrapper;
import fr.rakambda.rsndiscord.spring.jda.wrappers.member.DeafenWrapper;
import fr.rakambda.rsndiscord.spring.jda.wrappers.member.KickWrapper;
import fr.rakambda.rsndiscord.spring.jda.wrappers.member.ModifyNicknameWrapper;
import fr.rakambda.rsndiscord.spring.jda.wrappers.member.MuteWrapper;
import fr.rakambda.rsndiscord.spring.jda.wrappers.member.RemoveRoleWrapper;
import fr.rakambda.rsndiscord.spring.jda.wrappers.member.UnbanWrapper;
import fr.rakambda.rsndiscord.spring.jda.wrappers.message.AddReactionWrapper;
import fr.rakambda.rsndiscord.spring.jda.wrappers.message.ClearReactionsWrapper;
import fr.rakambda.rsndiscord.spring.jda.wrappers.message.DeleteMessageWrapper;
import fr.rakambda.rsndiscord.spring.jda.wrappers.message.EditMessageWrapper;
import fr.rakambda.rsndiscord.spring.jda.wrappers.message.InteractionEditMessageWrapper;
import fr.rakambda.rsndiscord.spring.jda.wrappers.message.InteractionNewMessageWrapper;
import fr.rakambda.rsndiscord.spring.jda.wrappers.message.InteractionReplyModalWrapper;
import fr.rakambda.rsndiscord.spring.jda.wrappers.message.RemoveAllReactionWrapper;
import fr.rakambda.rsndiscord.spring.jda.wrappers.message.SendMessageWrapper;
import fr.rakambda.rsndiscord.spring.jda.wrappers.message.UnpinMessageWrapper;
import fr.rakambda.rsndiscord.spring.jda.wrappers.role.SetColorWrapper;
import fr.rakambda.rsndiscord.spring.jda.wrappers.thread.AddThreadMemberWrapper;
import fr.rakambda.rsndiscord.spring.jda.wrappers.thread.CreateThreadWrapper;
import fr.rakambda.rsndiscord.spring.jda.wrappers.thread.DeleteThreadWrapper;
import fr.rakambda.rsndiscord.spring.jda.wrappers.thread.EditThreadWrapper;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
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
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.callbacks.IModalCallback;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.modals.Modal;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class JDAWrappers{
	@NonNull
	public static SendMessageWrapper message(@NonNull MessageChannel channel, @NonNull String message){
		return new SendMessageWrapper(channel, message);
	}
	
	@NonNull
	public static SendMessageWrapper message(@NonNull MessageChannel channel, @NonNull MessageTopLevelComponent component, @NonNull MessageTopLevelComponent... components){
		return new SendMessageWrapper(channel, component, components);
	}
	
	@NonNull
	public static SendMessageWrapper message(@NonNull MessageChannel channel, @NonNull MessageEmbed embed){
		return new SendMessageWrapper(channel, embed);
	}
	
	@NonNull
	public static SendMessageWrapper message(@NonNull MessageChannel channel, @NonNull Message message){
		return new SendMessageWrapper(channel, message);
	}
	
	@NonNull
	public static SendMessageWrapper message(@NonNull GenericMessageEvent event, @NonNull String message){
		return message(event.getChannel(), message);
	}
	
	@NonNull
	public static EditMessageWrapper edit(@NonNull Message message, @NonNull String content){
		return new EditMessageWrapper(message, content);
	}
	
	@NonNull
	public static EditMessageWrapper edit(@NonNull Message message, @NonNull MessageEmbed embed){
		return new EditMessageWrapper(message, embed);
	}
	
	@NonNull
	public static EditMessageWrapper editComponents(@NonNull Message message, @NonNull MessageTopLevelComponent... components){
		return new EditMessageWrapper(message).setActionRow(components);
	}
	
	@NonNull
	public static EditMessageWrapper editComponents(@NonNull Message message, @NonNull List<MessageTopLevelComponent> components){
		return new EditMessageWrapper(message).setActionRow(components);
	}
	
	@NonNull
	public static EditHookWrapper editComponents(@NonNull InteractionHook hook, @NonNull MessageTopLevelComponent... components){
		return new EditHookWrapper(hook, components);
	}
	
	@NonNull
	public static SendMessageWrapper reply(@NonNull Message replyToMessage, @NonNull String message){
		return new SendMessageWrapper(replyToMessage.getChannel(), message).replyTo(replyToMessage);
	}
	
	@NonNull
	public static AddReactionWrapper addReaction(@NonNull Message message, @NonNull Emoji emote){
		return new AddReactionWrapper(message, emote);
	}
	
	@NonNull
	public static RemoveAllReactionWrapper removeAllReactions(@NonNull MessageReaction messageReaction){
		return new RemoveAllReactionWrapper(messageReaction);
	}
	
	@NonNull
	public static RemoveAllReactionWrapper removeAllReactions(@NonNull Message message, @NonNull Emoji emoji){
		return new RemoveAllReactionWrapper(message, emoji);
	}
	
	@NonNull
	public static ClearReactionsWrapper clearReactions(@NonNull Message message){
		return new ClearReactionsWrapper(message);
	}
	
	@NonNull
	public static UnpinMessageWrapper unpin(@NonNull Message message){
		return new UnpinMessageWrapper(message);
	}
	
	@NonNull
	public static DeleteMessageWrapper delete(@NonNull Message message){
		return new DeleteMessageWrapper(message);
	}
	
	@NonNull
	public static DeleteMessageWrapper delete(@NonNull MessageChannel channel, long messageId){
		return new DeleteMessageWrapper(channel, messageId);
	}
	
	@NonNull
	public static CreateThreadWrapper createThread(@NonNull Message message, @NonNull String name){
		return new CreateThreadWrapper(message, name);
	}
	
	@NonNull
	public static EditThreadWrapper editThread(@NonNull ThreadChannel threadChannel){
		return new EditThreadWrapper(threadChannel);
	}
	
	@NonNull
	public static AddThreadMemberWrapper addThreadMember(@NonNull ThreadChannel thread, @NonNull User user){
		return new AddThreadMemberWrapper(thread, user);
	}
	
	@NonNull
	public static AddThreadMemberWrapper addThreadMember(@NonNull ThreadChannel thread, @NonNull Member member){
		return new AddThreadMemberWrapper(thread, member);
	}
	
	@NonNull
	public static DeleteThreadWrapper delete(@NonNull ThreadChannel thread){
		return new DeleteThreadWrapper(thread);
	}
	
	@NonNull
	public static InteractionEditMessageWrapper edit(@NonNull IReplyCallback event, @NonNull String message){
		return new InteractionEditMessageWrapper(event.getGuild(), event.getHook(), message);
	}
	
	@NonNull
	public static InteractionEditMessageWrapper edit(@NonNull IReplyCallback event, @NonNull MessageEmbed embed){
		return new InteractionEditMessageWrapper(event.getGuild(), event.getHook(), embed);
	}
	
	@NonNull
	public static InteractionEditMessageWrapper edit(@NonNull IReplyCallback event, @NonNull MessageTopLevelComponent... layouts){
		return new InteractionEditMessageWrapper(event.getGuild(), event.getHook(), layouts);
	}
	
	@NonNull
	public static InteractionNewMessageWrapper reply(@NonNull IReplyCallback event, @NonNull String message){
		return new InteractionNewMessageWrapper(event.getGuild(), event.getHook(), message);
	}
	
	@NonNull
	public static InteractionNewMessageWrapper reply(@NonNull IReplyCallback event, @NonNull MessageEmbed embed){
		return new InteractionNewMessageWrapper(event.getGuild(), event.getHook(), embed);
	}
	
	@NonNull
	public static InteractionReplyModalWrapper reply(@NonNull IModalCallback event, @NonNull Modal modal){
		return new InteractionReplyModalWrapper(event.getGuild(), event, modal);
	}
	
	@NonNull
	public static InteractionEditMessageWrapper removeComponents(@NonNull IReplyCallback event){
		return new InteractionEditMessageWrapper(event.getGuild(), event.getHook()).clearActionRows();
	}
	
	@NonNull
	public static EditChannelWrapper edit(@NonNull TextChannel channel){
		return new EditChannelWrapper(channel);
	}
	
	@NonNull
	public static DeleteChannelWrapper delete(@NonNull TextChannel channel){
		return new DeleteChannelWrapper(channel);
	}
	
	@NonNull
	public static KickWrapper kick(@NonNull Member member){
		return new KickWrapper(member.getGuild(), member);
	}
	
	@NonNull
	public static BanWrapper ban(@NonNull Member target, int deletionDays){
		return new BanWrapper(target.getGuild(), target, deletionDays);
	}
	
	@NonNull
	public static MuteWrapper mute(@NonNull Member member, boolean state){
		return new MuteWrapper(member.getGuild(), member, state);
	}
	
	@NonNull
	public static DeafenWrapper deafen(@NonNull Member member, boolean state){
		return new DeafenWrapper(member.getGuild(), member, state);
	}
	
	@NonNull
	public static RemoveRoleWrapper removeRole(@NonNull Member member, @NonNull Role role){
		return new RemoveRoleWrapper(member.getGuild(), member, role);
	}
	
	@NonNull
	public static AddRoleWrapper addRole(@NonNull Member member, @NonNull Role role){
		return new AddRoleWrapper(member.getGuild(), member, role);
	}
	
	@NonNull
	public static ModifyNicknameWrapper modifyNickname(@NonNull Member target, @Nullable String nickname){
		return new ModifyNicknameWrapper(target.getGuild(), target, nickname);
	}
	
	@NonNull
	public static SetColorWrapper setColor(@NonNull Role role, int color){
		return new SetColorWrapper(role, new Color(color));
	}
	
	@NonNull
	public static SetColorWrapper setColor(@NonNull Role role, @Nullable Color color){
		return new SetColorWrapper(role, color);
	}
	
	@NonNull
	public static UnbanWrapper unban(@NonNull Guild guild, @NonNull UserSnowflake userId){
		return new UnbanWrapper(guild, userId);
	}
	
	@NonNull
	public static EditPresenceWrapper editPresence(@NonNull JDA jda){
		return new EditPresenceWrapper(jda);
	}
	
	@NonNull
	public static AutoCompleteWrapper reply(@NonNull CommandAutoCompleteInteractionEvent event, @NonNull Collection<Command.Choice> choices){
		return new AutoCompleteWrapper(event, choices);
	}
	
	@NonNull
	public static HistoryChannelWrapper history(@NonNull MessageChannel channel){
		return new HistoryChannelWrapper(channel);
	}
	
	@NonNull
	public static Optional<GuildMessageChannel> findChannel(@NonNull JDA jda, long channelId){
		return Optional.ofNullable(jda.getTextChannelById(channelId));
	}
	
	@NonNull
	public static Optional<User> findUser(@NonNull JDA jda, long userId){
		try{
			return Optional.ofNullable(jda.retrieveUserById(userId).complete());
		}
		catch(Exception e){
			return Optional.empty();
		}
	}
	
	@NonNull
	public static Optional<Member> findMember(@NonNull Guild guild, long userId){
		try{
			return Optional.ofNullable(guild.retrieveMemberById(userId).complete());
		}
		catch(Exception e){
			return Optional.empty();
		}
	}
	
	public static boolean isMember(@NonNull Guild guild, long userId){
		if(guild.isLoaded()){
			if(guild.isMember(UserSnowflake.fromId(userId))){
				return true;
			}
		}
		
		return findMember(guild, userId).isPresent();
	}
	
	@NonNull
	public static CompletableFuture<Void> delay(int seconds){
		var delay = CompletableFuture.delayedExecutor(seconds, TimeUnit.SECONDS);
		return CompletableFuture.supplyAsync(() -> null, delay);
	}
}
