package com.example.demo;

import com.example.demo.actions.builder.Action;
import com.example.demo.actions.builder.ActionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import exceptions.DataValidationException;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

@SpringBootApplication
public class ProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjectApplication.class, args);
	}
}


	@RestController
	class WorkflowController {

		private final Map<Action, ActionHandler> actionHandlers;

		public WorkflowController(@Autowired List<ActionHandler> actionHandlerList) {
			this.actionHandlers = new HashMap<>(actionHandlerList.size());
			actionHandlerList.forEach(actionHandler -> this.actionHandlers.put(actionHandler.handlingFor(), actionHandler));
		}

		private Logger logger = Logger.getLogger("LANDING CONTROLLER");

		@CrossOrigin
		@PostMapping("/debit/credit")
		public ResponseEntity workflow(@RequestBody Map<String, Object> workflowBody) {
			String actionId = Optional.ofNullable((String) workflowBody.get("id")).orElse("UNKNOWN");
			Map operateOn = Optional.ofNullable((Map) workflowBody.get("operateOn")).orElse(Map.of());

			try {
				ActionHandler selectedHandler = actionHandlers.get(Enum.valueOf(Action.class, actionId));
				Object actionResponse = selectedHandler.executeAction(operateOn);

				Map<String, Object> response = Map.of(
						"timestamp", OffsetDateTime.now().toInstant().toEpochMilli(),
						"status", 200,
						"success", true,
						"data", actionResponse,
						"path", "/v1/workflow/execute"
				);
				return ResponseEntity.ok(response);
			}catch (DataValidationException ex){

				Map<String, Object> response = Map.of(
						"timestamp", OffsetDateTime.now().toInstant().toEpochMilli(),
						"success", false,
						"errors", ex.getLocalizedMessage(),
						"path", "/v1/workflow/execute"
				);
				return ResponseEntity.status(422).body(response);
			}
			catch (Exception ex){
				throw new RuntimeException("No Handler Found", ex);
			}
		}
	}

	@Component
	class CustomErrorAttributes extends DefaultErrorAttributes {

		@Override
		public Map<String, Object> getErrorAttributes(
				WebRequest webRequest, ErrorAttributeOptions options) {
			Map<String, Object> errorAttributes =
					super.getErrorAttributes(webRequest, options);
			errorAttributes.put("success", false);
			errorAttributes.remove("status");
			return errorAttributes;
		}
	}
