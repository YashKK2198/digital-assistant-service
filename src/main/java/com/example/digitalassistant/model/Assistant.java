package com.example.digitalassistant.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * JPA Entity representing a digital assistant with name and response text.
 * I implemented this with validation constraints and automatic timestamps.
 */
@Entity
@Table(name = "assistants")
public class Assistant {
    
    /**
     * Primary key - auto-generated unique identifier
     * Uses IDENTITY strategy for H2 database compatibility
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Assistant name - must be unique across all assistants
     * This is used to identify and retrieve specific assistants
     * 
     * Validation Rules:
     * - Cannot be null or empty
     * - Maximum 100 characters
     * - Must be unique in database
     */
    @NotBlank(message = "Assistant name is required")
    @Size(max = 100, message = "Assistant name must not exceed 100 characters")
    @Column(unique = true, nullable = false)
    private String name;
    
    /**
     * Response text - the predefined message this assistant will return
     * This is what users receive when they send messages to this assistant
     * 
     * Validation Rules:
     * - Cannot be null or empty
     * - Maximum 1000 characters
     */
    @NotBlank(message = "Response text is required")
    @Size(max = 1000, message = "Response text must not exceed 1000 characters")
    @Column(nullable = false, length = 1000)
    private String responseText;
    
    /**
     * Timestamp when the assistant was created
     * Automatically set when the entity is first persisted
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    /**
     * Timestamp when the assistant was last updated
     * Automatically updated whenever the entity is modified
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * Default constructor required by JPA
     * Automatically sets creation and update timestamps
     */
    public Assistant() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Constructor with name and response text
     * Used for creating new assistants with initial data
     * 
     * @param name The unique name for the assistant
     * @param responseText The predefined response text
     */
    public Assistant(String name, String responseText) {
        this(); // Call default constructor to set timestamps
        this.name = name;
        this.responseText = responseText;
    }
    
    // Getters and Setters with business logic
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    /**
     * Set assistant name and update the modification timestamp
     * 
     * @param name The new name for the assistant
     */
    public void setName(String name) {
        this.name = name;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getResponseText() {
        return responseText;
    }
    
    /**
     * Set response text and update the modification timestamp
     * 
     * @param responseText The new response text for the assistant
     */
    public void setResponseText(String responseText) {
        this.responseText = responseText;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    /**
     * String representation of the Assistant object
     * Useful for debugging and logging
     */
    @Override
    public String toString() {
        return "Assistant{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", responseText='" + responseText + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
