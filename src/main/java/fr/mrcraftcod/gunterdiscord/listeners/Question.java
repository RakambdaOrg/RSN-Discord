package fr.mrcraftcod.gunterdiscord.listeners;

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
	
	public Question(String question, HashMap<Integer, String> answers, int correctAnswer)
	{
		this.question = question;
		this.answers = answers;
		this.correctAnswer = correctAnswer;
	}
	
	public HashMap<Integer, String> getAnswers()
	{
		return answers;
	}
	
	public int getCorrectAnswerIndex()
	{
		return correctAnswer;
	}
	
	public String getQuestion()
	{
		return question;
	}
}
