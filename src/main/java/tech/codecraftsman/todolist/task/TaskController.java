package tech.codecraftsman.todolist.task;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.codecraftsman.todolist.utils.Utils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
        taskModel.setUserId((UUID) request.getAttribute("userId"));

        var currentDate = LocalDateTime.now();
        if (currentDate.isAfter(taskModel.getStartTime())) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de inicio deve ser maior que a data atual");
        } else if (currentDate.isAfter(taskModel.getEndTime())) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de término deve ser maior que a data atual");
        } else if (taskModel.getStartTime().isAfter(taskModel.getEndTime())) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de inicio deve ser menor que a data de término");
        }
        var task = this.taskRepository.save(taskModel);
        return ResponseEntity.status(200).body(task);
    }


    @GetMapping("/")
    public List<TaskModel> list(HttpServletRequest request) {

        var userId = request.getAttribute("userId");
        var tasks = this.taskRepository.findByUserId((UUID) userId);
        return tasks;
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id) {

        var task = this.taskRepository.findById(id).orElse(null);
        if(task == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tarefa não encontrada");
        }

        var userId = request.getAttribute("userId");

        if(!task.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("O usuário não tem permissão para alterar esta tarefa");
        }

        Utils.copyNonNullProperties(taskModel, task);

        var taskUpdated =  this.taskRepository.save(task);

        return ResponseEntity.ok(this.taskRepository.save(taskUpdated));

    }
}
