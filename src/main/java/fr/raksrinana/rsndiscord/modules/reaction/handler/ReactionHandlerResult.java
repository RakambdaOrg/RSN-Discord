package fr.raksrinana.rsndiscord.modules.reaction.handler;

public enum ReactionHandlerResult{
	FAIL(true),
	PASS(false),
	PROCESSED(true),
	PROCESSED_DELETE(true);
	private final boolean terminal;
	
	ReactionHandlerResult(boolean terminal){this.terminal = terminal;}
	
	public boolean isTerminal(){
		return this.terminal;
	}
}
