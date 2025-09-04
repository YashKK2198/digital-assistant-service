package com.example.digitalassistant.service;

import com.example.digitalassistant.model.Assistant;
import com.example.digitalassistant.model.MessageRequest;
import com.example.digitalassistant.model.MessageResponse;
import com.example.digitalassistant.repository.AssistantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service layer containing business logic for digital assistant operations.
 * I implemented this to handle assistant creation, message processing, and data validation.
 */
@Service
@Transactional
public class AssistantService {
    
    // Repository for database operations
    @Autowired
    private AssistantRepository assistantRepository;
    
    /**
     * Creates or updates an assistant with the given name and response text
     */
    public Assistant createOrUpdateAssistant(String name, String responseText) {
        Optional<Assistant> existingAssistant = assistantRepository.findByName(name);
        
        if (existingAssistant.isPresent()) {
            Assistant assistant = existingAssistant.get();
            assistant.setResponseText(responseText);
            return assistantRepository.save(assistant);
        } else {
            Assistant newAssistant = new Assistant(name, responseText);
            return assistantRepository.save(newAssistant);
        }
    }
    
    /**
     * Processes a message and returns the assistant's predefined response
     */
    public MessageResponse sendMessageToAssistant(String assistantName, MessageRequest messageRequest) {
        // Find the assistant by name
        Optional<Assistant> assistant = assistantRepository.findByName(assistantName);
        
        if (assistant.isPresent()) {
            // Assistant found - create and return response
            return new MessageResponse(
                assistantName,                          // Which assistant responded
                assistant.get().getResponseText(),      // Assistant's predefined response
                messageRequest.getMessage()             // User's original message
            );
        } else {
            // Assistant not found - throw custom exception
            throw new AssistantNotFoundException(
                "Assistant with name '" + assistantName + "' not found. " +
                "Please create the assistant first or check the name spelling."
            );
        }
    }
    
    /**
     * Retrieve all assistants from the database
     * 
     * Returns assistants ordered by creation date (newest first) to provide
     * a better user experience when browsing available assistants.
     * 
     * @return List of all assistants ordered by creation date (descending)
     */
    @Transactional(readOnly = true)
    public List<Assistant> getAllAssistants() {
        return assistantRepository.findAllOrderByCreatedAtDesc();
    }
    
    /**
     * Find a specific assistant by their name
     * 
     * This method is useful for:
     * - Checking if an assistant exists before operations
     * - Retrieving assistant details for display
     * - Validation in other business operations
     * 
     * @param name The unique name of the assistant to find
     * @return Optional<Assistant> containing the assistant if found
     */
    @Transactional(readOnly = true)
    public Optional<Assistant> getAssistantByName(String name) {
        return assistantRepository.findByName(name);
    }
    
    /**
     * Delete an assistant by their name
     * 
     * Business Logic:
     * 1. Check if the assistant exists
     * 2. If exists: Delete the assistant
     * 3. If not exists: Throw custom exception
     * 
     * This ensures we provide meaningful error messages when trying to delete
     * non-existent assistants.
     * 
     * @param name The name of the assistant to delete
     * @throws AssistantNotFoundException if the assistant doesn't exist
     */
    public void deleteAssistant(String name) {
        // Check if assistant exists before attempting deletion
        if (!assistantRepository.existsByName(name)) {
            throw new AssistantNotFoundException(
                "Cannot delete assistant '" + name + "' because it doesn't exist. " +
                "Please check the name spelling."
            );
        }
        
        // Delete the assistant
        assistantRepository.deleteByName(name);
    }
    
    /**
     * Check if an assistant exists with the given name
     * 
     * This is a utility method that's more efficient than retrieving the full entity
     * when you only need to check existence.
     * 
     * @param name The name to check
     * @return true if an assistant with this name exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean assistantExists(String name) {
        return assistantRepository.existsByName(name);
    }
    
    /**
     * Get the total count of assistants
     * 
     * Useful for statistics, monitoring, and API responses
     * 
     * @return The total number of assistants in the system
     */
    @Transactional(readOnly = true)
    public long getAssistantCount() {
        return assistantRepository.count();
    }
    
    /**
     * Custom Exception for Assistant Not Found scenarios
     * 
     * This custom exception provides more specific error handling
     * and allows for better error messages to API consumers.
     * 
     * Usage:
     * - Thrown when trying to send messages to non-existent assistants
     * - Thrown when trying to delete non-existent assistants
     * - Caught by the controller layer for proper HTTP error responses
     */
    public static class AssistantNotFoundException extends RuntimeException {
        
        /**
         * Constructor with custom error message
         * 
         * @param message Descriptive error message explaining what went wrong
         */
        public AssistantNotFoundException(String message) {
            super(message);
        }
        
        /**
         * Constructor with message and cause
         * 
         * @param message Descriptive error message
         * @param cause The underlying cause of the exception
         */
        public AssistantNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
