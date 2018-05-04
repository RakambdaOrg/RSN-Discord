package fr.mrcraftcod.gunterdiscord.listeners.quiz;

import java.util.HashMap;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-19
 */
public class Question
{
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
	public Question(String question, HashMap<Integer, String> answers, int correctAnswer)
	{
		this.question = question;
		this.answers = answers;
		this.correctAnswer = correctAnswer;
	}
	
	/**
	 * Get all the available answers with their index.
	 *
	 * @return The answers.
	 */
	public HashMap<Integer, String> getAnswers()
	{
		return answers;
	}
	
	/**
	 * Get the index of teh correct answer.
	 *
	 * @return The index of the correct answer.
	 */
	public int getCorrectAnswerIndex()
	{
		return correctAnswer;
	}
	
	/**
	 * Get the question.
	 *
	 * @return The question.
	 */
	public String getQuestion()
	{
		return question;
	}
}
