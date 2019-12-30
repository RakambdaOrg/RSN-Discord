package fr.raksrinana.rsndiscord.utils.reaction;

public enum ReactionHandlerResult{
	PROCESSED(true), PROCESSED_DELETE(true), PASS(false);
	private boolean terminal;
	
	ReactionHandlerResult(boolean terminal){this.terminal = terminal;}
	
	public boolean isTerminal(){
		return this.terminal;
	}
}
