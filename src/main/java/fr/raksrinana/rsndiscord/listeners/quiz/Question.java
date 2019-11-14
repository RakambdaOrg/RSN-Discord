package fr.raksrinana.rsndiscord.listeners.quiz;

import lombok.Getter;
import lombok.NonNull;
import java.util.HashMap;

@Getter
class Question{
	private final String question;
	private final HashMap<Integer, String> answers;
	private final int correctAnswerIndex;
	
	/**
	 * Constructor.
	 *
	 * @param question           The question itself.
	 * @param answers            The answers available.
	 * @param correctAnswerIndex The index of teh correct answer.
	 */
	Question(@NonNull final String question, @NonNull final HashMap<Integer, String> answers, final int correctAnswerIndex){
		this.question = question;
		this.answers = answers;
		this.correctAnswerIndex = correctAnswerIndex;
	}
	
	public String getCorrectAnswer(){
		return this.getAnswers().get(this.getCorrectAnswerIndex());
	}
}
