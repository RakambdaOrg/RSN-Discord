package fr.raksrinana.rsndiscord.modules.trombinoscope.command;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.commands.generic.DeleteMode;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.permission.SimplePermission;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.modules.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.modules.settings.types.RoleConfiguration;
import fr.raksrinana.rsndiscord.utils.Utilities;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import static fr.raksrinana.rsndiscord.commands.generic.CommandResult.BAD_ARGUMENTS;
import static fr.raksrinana.rsndiscord.commands.generic.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.commands.generic.DeleteMode.AFTER;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static fr.raksrinana.rsndiscord.utils.Utilities.isModerator;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

class AddCommand extends BasicCommand{
	private static final Path trombinoscopeFolder = Paths.get("trombinoscope");
	
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	AddCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return new SimplePermission("command.trombinoscope.add", true);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		var guild = event.getGuild();
		var author = event.getAuthor();
		var attachments = event.getMessage().getAttachments();
		
		if(attachments.isEmpty()){
			return BAD_ARGUMENTS;
		}
		
		var trombinoscope = Settings.get(guild).getTrombinoscope();
		var target = event.getMessage().getMentionedMembers().stream().findFirst()
				.or(() -> ofNullable(event.getMember()))
				.orElseThrow(() -> new IllegalStateException("Failed to get member from event"));
		if(!Objects.equals(target, event.getMember()) && !isModerator(event.getMember())){
			author.openPrivateChannel().submit()
					.thenAccept(privateChannel -> privateChannel.sendMessage(translate(guild, "trombinoscope.error.add-other")).submit());
			return SUCCESS;
		}
		boolean failed = false;
		try{
			for(Message.Attachment attachment : attachments){
				Log.getLogger(guild).info("Downloading {}", attachment);
				var path = getFilePath(target, attachment);
				var savedFile = attachment.retrieveInputStream()
						.thenApply(is -> {
							try(is){
								Files.copy(is, path);
								return path;
							}
							catch(IOException e){
								Log.getLogger(guild).error("Failed downloading attachment", e);
								Utilities.reportException("Failed to save attachment", e);
							}
							return null;
						})
						.exceptionally(e -> {
							Log.getLogger(guild).error("Failed to save file", e);
							Utilities.reportException("Failed to save trombinoscope file", e);
							return null;
						})
						.get(60, TimeUnit.SECONDS);
				if(checkFile(attachment, savedFile)){
					trombinoscope.registerPicture(target.getUser(), savedFile);
				}
				else{
					failed = true;
					if(nonNull(savedFile) && Files.isRegularFile(savedFile)){
						Files.deleteIfExists(savedFile);
					}
				}
			}
		}
		catch(InterruptedException | ExecutionException | TimeoutException | IOException e){
			failed = true;
			Log.getLogger(guild).error("Failed to save file", e);
			Utilities.reportException("Failed to save trombinoscope file", e);
		}
		if(failed){
			author.openPrivateChannel().submit()
					.thenAccept(privateChannel -> privateChannel.sendMessage(translate(guild, "trombinoscope.error.save-error")).submit());
		}
		else{
			trombinoscope.getPosterRole()
					.flatMap(RoleConfiguration::getRole)
					.ifPresent(role -> guild.addRoleToMember(target, role).submit());
			trombinoscope.getPicturesChannel()
					.flatMap(ChannelConfiguration::getChannel)
					.ifPresent(channel -> channel.sendMessage(translate(guild, "trombinoscope.picture-added", target.getAsMention(), trombinoscope.getPictures(target.getUser()).size())).submit());
		}
		return SUCCESS;
	}
	
	private Path getFilePath(Member member, Message.Attachment attachment) throws IOException{
		var path = trombinoscopeFolder.resolve(member.getId())
				.resolve(String.format("%d-%s", attachment.getIdLong(), attachment.getFileName()));
		Files.createDirectories(path.getParent());
		return path;
	}
	
	private boolean checkFile(Message.Attachment attachment, Path savedFile) throws IOException{
		return nonNull(savedFile) && Files.size(savedFile) == attachment.getSize() && attachment.getSize() != 0;
	}
	
	@Override
	public DeleteMode getDeleteMode(){
		return AFTER;
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.trombinoscope.add.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("add", "a");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.trombinoscope.add.description");
	}
}
