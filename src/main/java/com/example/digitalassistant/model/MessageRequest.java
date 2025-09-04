package com.example.digitalassistant.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * DTO for message requests with validation constraints.
 * I created this to handle user messages sent to assistants.
 */
public class MessageRequest {
    
    @NotBlank(message = "Message is required and cannot be empty")
    @Size(max = 500, message = "Message must not exceed 500 characters")
    private String message;
    
    public MessageRequest() {}
    
    public MessageRequest(String message) {
        this.message = message;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    @Override
    public String toString() {
        return "MessageRequest{" +
                "message='" + message + '\'' +
                '}';
    }
}
