package fr.rakambda.rsndiscord.spring.web;

import fr.rakambda.rsndiscord.spring.schedule.TriggerTaskProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/task")
public class TaskController{
	private final TaskScheduler taskScheduler;
	private final Collection<TriggerTaskProvider> tasks;
	
	@Autowired
	public TaskController(TaskScheduler taskScheduler, Collection<TriggerTaskProvider> tasks){
		this.taskScheduler = taskScheduler;
		this.tasks = tasks;
	}
	
	@GetMapping("/")
	public List<String> getTasks(){
		return tasks.stream().map(TriggerTaskProvider::getId).toList();
	}
	
	@PostMapping("/{taskId}/trigger")
	public void triggerTask(@PathVariable("taskId") String taskId){
		var task = tasks.stream()
				.filter(t -> Objects.equals(t.getId(), taskId))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Invalid task id"));
		taskScheduler.schedule(task, new Date());
	}
}
