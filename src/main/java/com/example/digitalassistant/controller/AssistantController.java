package com.example.digitalassistant.controller;

import com.example.digitalassistant.model.Assistant;
import com.example.digitalassistant.model.MessageRequest;
import com.example.digitalassistant.model.MessageResponse;
import com.example.digitalassistant.service.AssistantService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for Digital Assistant API
 * 
 * I implemented this controller to handle all HTTP requests for managing
 * digital assistants and processing messages. It provides a complete
 * RESTful API with proper error handling and validation.
 */
@RestController
@RequestMapping("/api/assistants")
@CrossOrigin(origins = "*")
public class AssistantController {
    
    // Service layer for business logic
    @Autowired
    private AssistantService assistantService;
    
    /**
     * Creates a new assistant or updates an existing one
     */
    @PostMapping
    public ResponseEntity<?> createOrUpdateAssistant(@Valid @RequestBody Assistant assistant) {
        try {
            boolean isExistingAssistant = assistantService.assistantExists(assistant.getName());
            
            Assistant savedAssistant = assistantService.createOrUpdateAssistant(
                assistant.getName(), 
                assistant.getResponseText()
            );
            
            HttpStatus status = isExistingAssistant ? HttpStatus.OK : HttpStatus.CREATED;
            String operation = isExistingAssistant ? "updated" : "created";
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Assistant '" + assistant.getName() + "' " + operation + " successfully");
            response.put("operation", operation);
            response.put("assistant", savedAssistant);
            return ResponseEntity.status(status).body(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Error creating/updating assistant");
            errorResponse.put("details", e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Sends a message to an assistant and returns the predefined response
     */
    @PostMapping("/{assistantName}/message")
    public ResponseEntity<?> sendMessage(
            @PathVariable String assistantName,
            @Valid @RequestBody MessageRequest messageRequest) {
        try {
            MessageResponse response = assistantService.sendMessageToAssistant(assistantName, messageRequest);
            return ResponseEntity.ok(response);
            
        } catch (AssistantService.AssistantNotFoundException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Assistant not found");
            errorResponse.put("details", e.getMessage());
            errorResponse.put("assistantName", assistantName);
            errorResponse.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Error processing message");
            errorResponse.put("details", e.getMessage());
            errorResponse.put("assistantName", assistantName);
            errorResponse.put("timestamp", LocalDateTime.now());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Get all digital assistants
     * 
     * HTTP Method: GET
     * Endpoint: /api/assistants
     * 
     * Returns a list of all assistants ordered by creation date (newest first).
     * This endpoint is useful for:
     * - Frontend applications to display available assistants
     * - Administrative purposes to see all configured assistants
     * - Integration testing and verification
     * 
     * @return ResponseEntity containing list of all assistants
     */
    @GetMapping
    public ResponseEntity<List<Assistant>> getAllAssistants() {
        // Retrieve all assistants from the service layer
        List<Assistant> assistants = assistantService.getAllAssistants();
        
        // Return the list with 200 OK status
        return ResponseEntity.ok(assistants);
    }
    
    /**
     * Get a specific assistant by name
     * 
     * HTTP Method: GET
     * Endpoint: /api/assistants/{assistantName}
     * 
     * Path Variable: assistantName - The name of the assistant to retrieve
     * 
     * This endpoint allows clients to:
     * - Check if a specific assistant exists
     * - Retrieve assistant details and configuration
     * - Verify assistant response text before sending messages
     * 
     * @param assistantName The name of the assistant to retrieve
     * @return ResponseEntity with assistant data or error response
     */
    @GetMapping("/{assistantName}")
    public ResponseEntity<?> getAssistant(@PathVariable String assistantName) {
        // Search for the assistant by name
        Optional<Assistant> assistant = assistantService.getAssistantByName(assistantName);
        
        if (assistant.isPresent()) {
            // Assistant found - return assistant data
            return ResponseEntity.ok(assistant.get());
        } else {
            // Assistant not found - return 404 error
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Assistant not found");
            errorResponse.put("details", "Assistant with name '" + assistantName + "' does not exist");
            errorResponse.put("assistantName", assistantName);
            errorResponse.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
    
    /**
     * Delete a digital assistant
     * 
     * HTTP Method: DELETE
     * Endpoint: /api/assistants/{assistantName}
     * 
     * Path Variable: assistantName - The name of the assistant to delete
     * 
     * This endpoint permanently removes an assistant from the system.
     * Use with caution as this operation cannot be undone.
     * 
     * @param assistantName The name of the assistant to delete
     * @return ResponseEntity with success confirmation or error response
     */
    @DeleteMapping("/{assistantName}")
    public ResponseEntity<?> deleteAssistant(@PathVariable String assistantName) {
        try {
            // Attempt to delete the assistant
            assistantService.deleteAssistant(assistantName);
            
            // Return success confirmation
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("success", true);
            successResponse.put("message", "Assistant '" + assistantName + "' deleted successfully");
            successResponse.put("assistantName", assistantName);
            successResponse.put("timestamp", LocalDateTime.now());
            return ResponseEntity.ok(successResponse);
            
        } catch (AssistantService.AssistantNotFoundException e) {
            // Handle case where assistant doesn't exist
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Assistant not found");
            errorResponse.put("details", e.getMessage());
            errorResponse.put("assistantName", assistantName);
            errorResponse.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
    
    /**
     * Health check endpoint for service monitoring
     * 
     * HTTP Method: GET
     * Endpoint: /api/assistants/health
     * 
     * This endpoint provides:
     * - Service status information
     * - Current assistant count
     * - Timestamp for monitoring
     * 
     * Used for:
     * - Load balancer health checks
     * - Monitoring and alerting systems
     * - Development and debugging
     * 
     * @return ResponseEntity with health status information
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        try {
            // Get current assistant count for status information
            long assistantCount = assistantService.getAssistantCount();
            
            // Return comprehensive health information
            Map<String, Object> endpoints = new HashMap<>();
            endpoints.put("createAssistant", "POST /api/assistants");
            endpoints.put("sendMessage", "POST /api/assistants/{name}/message");
            endpoints.put("getAllAssistants", "GET /api/assistants");
            endpoints.put("getAssistant", "GET /api/assistants/{name}");
            endpoints.put("deleteAssistant", "DELETE /api/assistants/{name}");
            
            Map<String, Object> healthResponse = new HashMap<>();
            healthResponse.put("status", "UP");
            healthResponse.put("service", "Digital Assistant Service");
            healthResponse.put("version", "1.0.0");
            healthResponse.put("totalAssistants", assistantCount);
            healthResponse.put("timestamp", LocalDateTime.now());
            healthResponse.put("endpoints", endpoints);
            
            return ResponseEntity.ok(healthResponse);
            
        } catch (Exception e) {
            // Return error status if health check fails
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "DOWN");
            errorResponse.put("error", "Health check failed");
            errorResponse.put("details", e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
        }
    }
    
    /**
     * Global exception handler for validation errors
     * 
     * This method handles validation errors that occur when @Valid annotation
     * finds issues with request data (e.g., missing required fields, size constraints)
     * 
     * @param ex The validation exception
     * @return ResponseEntity with detailed validation error information
     */
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(org.springframework.web.bind.MethodArgumentNotValidException ex) {
        // Extract validation error details
        Map<String, String> errors = new java.util.HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        
        // Return structured error response
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("error", "Validation failed");
        errorResponse.put("validationErrors", errors);
        errorResponse.put("timestamp", LocalDateTime.now());
        return ResponseEntity.badRequest().body(errorResponse);
    }
}
