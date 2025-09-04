package com.example.digitalassistant.model;

import java.time.LocalDateTime;

/**
 * DTO for assistant responses containing the reply, original message, and timestamp.
 * I designed this to provide complete context for each assistant interaction.
 */
public class MessageResponse {
    
    private String assistantName;
    private String response;
    private String originalMessage;
    private LocalDateTime timestamp;
    
    public MessageResponse() {
        this.timestamp = LocalDateTime.now();
    }
    
    public MessageResponse(String assistantName, String response, String originalMessage) {
        this();
        this.assistantName = assistantName;
        this.response = response;
        this.originalMessage = originalMessage;
    }
    
    public String getAssistantName() {
        return assistantName;
    }
    
    public void setAssistantName(String assistantName) {
        this.assistantName = assistantName;
    }
    
    public String getResponse() {
        return response;
    }
    
    public void setResponse(String response) {
        this.response = response;
    }
    
    public String getOriginalMessage() {
        return originalMessage;
    }
    
    public void setOriginalMessage(String originalMessage) {
        this.originalMessage = originalMessage;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return "MessageResponse{" +
                "assistantName='" + assistantName + '\'' +
                ", response='" + response + '\'' +
                ", originalMessage='" + originalMessage + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
