package com.example.digitalassistant.repository;

import com.example.digitalassistant.model.Assistant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Assistant Repository Interface
 * 
 * This interface extends JpaRepository to provide database operations for Assistant entities.
 * Spring Data JPA automatically implements this interface at runtime.
 * 
 * Purpose:
 * - Provide CRUD operations for Assistant entities
 * - Define custom query methods for specific business needs
 * - Abstract database operations from the service layer
 * 
 * Key Features:
 * - Automatic implementation by Spring Data JPA
 * - Type-safe database operations
 * - Custom query methods using method naming conventions
 * - Support for custom JPQL queries
 * 
 * @author Digital Assistant Team
 */
@Repository
public interface AssistantRepository extends JpaRepository<Assistant, Long> {
    
    /**
     * Find an assistant by their unique name
     * 
     * Spring Data JPA automatically implements this method based on the method name:
     * - "findBy" indicates a query operation
     * - "Name" refers to the 'name' field in the Assistant entity
     * 
     * @param name The unique name of the assistant to find
     * @return Optional<Assistant> - contains the assistant if found, empty if not found
     */
    Optional<Assistant> findByName(String name);
    
    /**
     * Check if an assistant exists with the given name
     * 
     * This is more efficient than findByName when you only need to check existence
     * Spring Data JPA implements this as a COUNT query
     * 
     * @param name The name to check for existence
     * @return true if an assistant with this name exists, false otherwise
     */
    boolean existsByName(String name);
    
    /**
     * Find all assistants ordered by creation date (newest first)
     * 
     * Custom JPQL query to retrieve assistants in a specific order
     * This provides a better user experience by showing recent assistants first
     * 
     * @return List of all assistants ordered by creation date descending
     */
    @Query("SELECT a FROM Assistant a ORDER BY a.createdAt DESC")
    List<Assistant> findAllOrderByCreatedAtDesc();
    
    /**
     * Delete an assistant by their name
     * 
     * Spring Data JPA automatically implements this method
     * Note: This method should be used within a @Transactional context
     * 
     * @param name The name of the assistant to delete
     */
    void deleteByName(String name);
    
    /**
     * Count total number of assistants
     * 
     * Inherited from JpaRepository, useful for statistics and monitoring
     * 
     * @return Total count of assistants in the database
     */
    // count() method is inherited from JpaRepository<Assistant, Long>
    
    /**
     * Find assistants by name containing a specific string (case-insensitive)
     * 
     * This method would be useful for search functionality
     * Currently commented out but can be enabled if needed
     * 
     * @param nameFragment Part of the name to search for
     * @return List of assistants whose names contain the fragment
     */
    // List<Assistant> findByNameContainingIgnoreCase(String nameFragment);
}
