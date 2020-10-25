package fr.raksrinana.rsndiscord.modules.reaction.handler;

public enum ReactionHandlerResult{
	PROCESSED(true), PROCESSED_DELETE(true), PASS(false), FAIL(true);
	private final boolean terminal;
	
	ReactionHandlerResult(boolean terminal){this.terminal = terminal;}
	
	public boolean isTerminal(){
		return this.terminal;
	}
}
