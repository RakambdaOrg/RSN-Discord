package fr.mrcraftcod.gunterdiscord.listeners.quiz;

import javax.annotation.Nonnull;
import java.util.HashMap;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-19
 */
class Question{
	private final String question;
	private final HashMap<Integer, String> answers;
	private final int correctAnswer;
	
	/**
	 * Constructor.
	 *
	 * @param question      The question itself.
	 * @param answers       The answers available.
	 * @param correctAnswer The index of teh correct answer.
	 */
	Question(@Nonnull final String question, @Nonnull final HashMap<Integer, String> answers, final int correctAnswer){
		this.question = question;
		this.answers = answers;
		this.correctAnswer = correctAnswer;
	}
	
	/**
	 * Get all the available answers with their index.
	 *
	 * @return The answers.
	 */
	@Nonnull
	HashMap<Integer, String> getAnswers(){
		return this.answers;
	}
	
	/**
	 * Get the index of teh correct answer.
	 *
	 * @return The index of the correct answer.
	 */
	int getCorrectAnswerIndex(){
		return this.correctAnswer;
	}
	
	/**
	 * Get the question.
	 *
	 * @return The question.
	 */
	@Nonnull
	String getQuestion(){
		return this.question;
	}
}
