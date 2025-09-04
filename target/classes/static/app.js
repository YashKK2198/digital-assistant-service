/**
 * Digital Assistant Service - Frontend JavaScript Application
 * 
 * This JavaScript file provides the frontend functionality for interacting
 * with the Digital Assistant Service REST API.
 * 
 * Key Features:
 * 1. Create and update digital assistants
 * 2. Send messages to assistants and display responses
 * 3. List and manage existing assistants
 * 4. Real-time UI updates and error handling
 * 
 * API Integration:
 * - Uses modern Fetch API for HTTP requests
 * - Handles JSON request/response format
 * - Implements proper error handling and user feedback
 * 
 * @author Digital Assistant Team
 * @version 1.0.0
 */

// ===================================================================
// CONFIGURATION AND CONSTANTS
// ===================================================================

/**
 * Base URL for the Digital Assistant API
 * Change this if deploying to a different host/port
 */
const API_BASE_URL = 'http://localhost:8080/api/assistants';

/**
 * HTTP request headers for JSON communication
 */
const JSON_HEADERS = {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
};

// ===================================================================
// APPLICATION INITIALIZATION
// ===================================================================

/**
 * Initialize the application when the DOM is fully loaded
 * 
 * This function sets up:
 * - Event listeners for forms
 * - Initial data loading
 * - UI state management
 */
document.addEventListener('DOMContentLoaded', function() {
    console.log('Digital Assistant Frontend Initialized');
    
    // Set up form event listeners
    setupEventListeners();
    
    // Load initial data
    loadAssistants();
    
    // Check API health
    checkApiHealth();
});

/**
 * Set up all event listeners for the application
 * 
 * This centralizes event handling and makes the code more maintainable
 */
function setupEventListeners() {
    // Assistant creation form
    document.getElementById('createForm').addEventListener('submit', handleCreateAssistant);
    
    // Message sending form
    document.getElementById('messageForm').addEventListener('submit', handleSendMessage);
    
    console.log('Event listeners configured');
}

// ===================================================================
// ASSISTANT MANAGEMENT FUNCTIONS
// ===================================================================

/**
 * Handle assistant creation/update form submission
 * 
 * Process Flow:
 * 1. Prevent default form submission
 * 2. Extract form data
 * 3. Validate input data
 * 4. Send API request
 * 5. Handle response and update UI
 * 
 * @param {Event} event The form submission event
 */
async function handleCreateAssistant(event) {
    // Prevent default form submission behavior
    event.preventDefault();
    
    console.log('Creating/updating assistant...');
    
    // Extract form data
    const name = document.getElementById('name').value.trim();
    const responseText = document.getElementById('response').value.trim();
    
    // Client-side validation
    if (!name || !responseText) {
        showResponse('createResponse', 'Please fill in all fields', 'error');
        return;
    }
    
    try {
        // Prepare request data
        const assistantData = {
            name: name,
            responseText: responseText
        };
        
        // Send POST request to create/update assistant
        const response = await fetch(API_BASE_URL, {
            method: 'POST',
            headers: JSON_HEADERS,
            body: JSON.stringify(assistantData)
        });
        
        // Parse JSON response
        const result = await response.json();
        
        if (response.ok) {
            // Success - show confirmation and update UI
            showResponse('createResponse', 
                `${result.message}\n\nAssistant Details:\n${JSON.stringify(result.assistant, null, 2)}`, 
                'success'
            );
            
            // Clear form
            document.getElementById('createForm').reset();
            
            // Refresh assistants list and dropdown
            loadAssistants();
            
            console.log('Assistant created/updated successfully:', result.assistant);
            
        } else {
            // Error - show error message
            showResponse('createResponse', 
                `Error: ${result.error}\nDetails: ${result.details || 'Unknown error'}`, 
                'error'
            );
            
            console.error('Failed to create/update assistant:', result);
        }
        
    } catch (error) {
        // Handle network or parsing errors
        showResponse('createResponse', 
            `Network Error: ${error.message}\nPlease check if the server is running.`, 
            'error'
        );
        
        console.error('Network error:', error);
    }
}

/**
 * Handle message sending form submission
 * 
 * Process Flow:
 * 1. Prevent default form submission
 * 2. Extract form data
 * 3. Validate assistant selection and message
 * 4. Send message to selected assistant
 * 5. Display assistant's response
 * 
 * @param {Event} event The form submission event
 */
async function handleSendMessage(event) {
    // Prevent default form submission behavior
    event.preventDefault();
    
    console.log('Sending message to assistant...');
    
    // Extract form data
    const assistantName = document.getElementById('assistantSelect').value;
    const message = document.getElementById('message').value.trim();
    
    // Client-side validation
    if (!assistantName) {
        showResponse('messageResponse', 'Please select an assistant', 'error');
        return;
    }
    
    if (!message) {
        showResponse('messageResponse', 'Please enter a message', 'error');
        return;
    }
    
    try {
        // Prepare message data
        const messageData = {
            message: message
        };
        
        // Send POST request to assistant's message endpoint
        const response = await fetch(`${API_BASE_URL}/${encodeURIComponent(assistantName)}/message`, {
            method: 'POST',
            headers: JSON_HEADERS,
            body: JSON.stringify(messageData)
        });
        
        // Parse JSON response
        const result = await response.json();
        
        if (response.ok) {
            // Success - display assistant's response
            const formattedResponse = `
Assistant: ${result.assistantName}
Your Message: "${result.originalMessage}"
Assistant Response: "${result.response}"
Timestamp: ${new Date(result.timestamp).toLocaleString()}
            `.trim();
            
            showResponse('messageResponse', formattedResponse, 'success');
            
            // Clear message input but keep assistant selection
            document.getElementById('message').value = '';
            
            console.log('Message processed successfully:', result);
            
        } else {
            // Error - show error message
            showResponse('messageResponse', 
                `Error: ${result.error}\nDetails: ${result.details || 'Unknown error'}`, 
                'error'
            );
            
            console.error('Failed to send message:', result);
        }
        
    } catch (error) {
        // Handle network or parsing errors
        showResponse('messageResponse', 
            `Network Error: ${error.message}\nPlease check if the server is running.`, 
            'error'
        );
        
        console.error('Network error:', error);
    }
}

// ===================================================================
// DATA LOADING FUNCTIONS
// ===================================================================

/**
 * Load all assistants from the API and update the UI
 * 
 * This function:
 * 1. Fetches all assistants from the REST API
 * 2. Updates the assistants list display
 * 3. Updates the assistant selection dropdown
 * 4. Handles loading states and errors
 */
async function loadAssistants() {
    console.log('Loading assistants...');
    
    try {
        // Show loading state
        const assistantsList = document.getElementById('assistantsList');
        const targetAssistant = document.getElementById('targetAssistant');
        
        assistantsList.innerHTML = '<p>Loading assistants...</p>';
        
        // Fetch assistants from API
        const response = await fetch(API_BASE_URL, {
            method: 'GET',
            headers: JSON_HEADERS
        });
        
        if (response.ok) {
            // Parse response data
            const assistants = await response.json();
            
            // Update assistants list display
            updateAssistantsList(assistants);
            
            // Update assistant selection dropdown
            updateAssistantDropdown(assistants);
            
            console.log(`Loaded ${assistants.length} assistants`);
            
        } else {
            // Handle API error
            assistantsList.innerHTML = '<p class="error">Failed to load assistants</p>';
            console.error('Failed to load assistants:', response.status);
        }
        
    } catch (error) {
        // Handle network errors
        document.getElementById('assistantsList').innerHTML = 
            '<p class="error">Network error: Could not connect to server</p>';
        
        console.error('Network error loading assistants:', error);
    }
}

/**
 * Update the assistants list display with current data
 * 
 * @param {Array} assistants Array of assistant objects from the API
 */
function updateAssistantsList(assistants) {
    const assistantsList = document.getElementById('assistantsList');
    
    if (assistants.length === 0) {
        // No assistants found
        assistantsList.innerHTML = '<p>No assistants created yet. Create your first assistant above!</p>';
        return;
    }
    
    // Build HTML for assistants list
    let html = `<h3>Total Assistants: ${assistants.length}</h3>`;
    
    assistants.forEach(assistant => {
        html += `
            <div class="assistant-card">
                <div class="assistant-name">${assistant.name}</div>
                <div class="assistant-response">"${assistant.responseText}"</div>
                <small>Created: ${new Date(assistant.createdAt).toLocaleString()}</small>
                <button onclick="deleteAssistant('${assistant.name}')" style="background: #dc3545; margin-top: 10px; width: auto; padding: 5px 10px;">Delete</button>
            </div>
        `;
    });
    
    assistantsList.innerHTML = html;
}

/**
 * Update the assistant selection dropdown with current assistants
 * 
 * @param {Array} assistants Array of assistant objects from the API
 */
function updateAssistantDropdown(assistants) {
    const dropdown = document.getElementById('assistantSelect');
    
    // Clear existing options (except the default one)
    dropdown.innerHTML = '<option value="">-- Choose an assistant --</option>';
    
    // Add option for each assistant
    assistants.forEach(assistant => {
        const option = document.createElement('option');
        option.value = assistant.name;
        option.textContent = `${assistant.name} - "${assistant.responseText.substring(0, 50)}${assistant.responseText.length > 50 ? '...' : ''}"`;
        dropdown.appendChild(option);
    });
}

// ===================================================================
// ASSISTANT MANAGEMENT FUNCTIONS
// ===================================================================

/**
 * Delete a specific assistant
 * 
 * @param {string} assistantName The name of the assistant to delete
 */
async function deleteAssistant(assistantName) {
    // Confirm deletion with user
    if (!confirm(`Are you sure you want to delete assistant "${assistantName}"?`)) {
        return;
    }
    
    console.log(`Deleting assistant: ${assistantName}`);
    
    try {
        // Send DELETE request
        const response = await fetch(`${API_BASE_URL}/${encodeURIComponent(assistantName)}`, {
            method: 'DELETE',
            headers: JSON_HEADERS
        });
        
        const result = await response.json();
        
        if (response.ok) {
            // Success - refresh the UI
            alert(`${result.message}`);
            loadAssistants();
            
            console.log('Assistant deleted successfully');
            
        } else {
            // Error - show error message
            alert(`Error: ${result.error}\n${result.details}`);
            console.error('Failed to delete assistant:', result);
        }
        
    } catch (error) {
        // Handle network errors
        alert(`Network Error: ${error.message}`);
        console.error('Network error deleting assistant:', error);
    }
}

// ===================================================================
// UTILITY FUNCTIONS
// ===================================================================

/**
 * Display response message in the UI
 * 
 * @param {string} elementId The ID of the element to show the response in
 * @param {string} message The message to display
 * @param {string} type The type of message ('success' or 'error')
 */
function showResponse(elementId, message, type) {
    const element = document.getElementById(elementId);
    element.textContent = message;
    element.className = `response ${type}`;
    element.style.display = 'block';
    
    // Auto-hide success messages after 5 seconds
    if (type === 'success') {
        setTimeout(() => {
            element.style.display = 'none';
        }, 5000);
    }
}

/**
 * Check API health status
 * 
 * This function verifies that the backend service is running
 * and provides feedback to the user about service status
 */
async function checkApiHealth() {
    try {
        console.log('Checking API health...');
        
        // Call health endpoint
        const response = await fetch(`${API_BASE_URL}/health`, {
            method: 'GET',
            headers: JSON_HEADERS
        });
        
        if (response.ok) {
            const healthData = await response.json();
            console.log('API Health Check Passed:', healthData);
            
            // Show success indicator in console
            console.log(`Service Status: ${healthData.status}`);
            console.log(`Total Assistants: ${healthData.totalAssistants}`);
            
        } else {
            console.warn('API Health Check Failed:', response.status);
        }
        
    } catch (error) {
        console.error('API Health Check Error:', error.message);
        
        // Show user-friendly error message
        const assistantsList = document.getElementById('assistantsList');
        assistantsList.innerHTML = `
            <div class="error" style="padding: 20px; text-align: center;">
                <h3>Service Unavailable</h3>
                <p>Cannot connect to the Digital Assistant Service.</p>
                <p>Please ensure the Spring Boot application is running on port 8080.</p>
                <button onclick="location.reload()">Retry Connection</button>
            </div>
        `;
    }
}

/**
 * Format timestamp for display
 * 
 * @param {string} timestamp ISO timestamp string
 * @return {string} Formatted date and time
 */
function formatTimestamp(timestamp) {
    return new Date(timestamp).toLocaleString();
}

/**
 * Escape HTML to prevent XSS attacks
 * 
 * @param {string} text Text to escape
 * @return {string} HTML-escaped text
 */
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// ===================================================================
// API COMMUNICATION FUNCTIONS
// ===================================================================

/**
 * Generic API request function with error handling
 * 
 * @param {string} url The API endpoint URL
 * @param {Object} options Fetch options (method, headers, body, etc.)
 * @return {Promise} Promise that resolves to the API response
 */
async function apiRequest(url, options = {}) {
    try {
        // Add default headers
        options.headers = { ...JSON_HEADERS, ...options.headers };
        
        // Make the request
        const response = await fetch(url, options);
        
        // Parse JSON response
        const data = await response.json();
        
        // Return response with status information
        return {
            ok: response.ok,
            status: response.status,
            data: data
        };
        
    } catch (error) {
        // Handle network errors
        console.error('API Request Error:', error);
        throw new Error(`Network error: ${error.message}`);
    }
}

// ===================================================================
// DEMO AND TESTING FUNCTIONS
// ===================================================================

/**
 * Create demo assistants for testing
 * 
 * This function creates sample assistants to demonstrate the service
 * Useful for development and demonstration purposes
 */
async function createDemoAssistants() {
    const demoAssistants = [
        {
            name: "Yash-SmartBot",
            responseText: "Hello! I'm Yash-SmartBot, your intelligent digital assistant. How can I help you today?"
        },
        {
            name: "SupportBot",
            responseText: "Hi there! I'm SupportBot. I'm here to help you with any technical issues or questions you might have."
        },
        {
            name: "InfoBot",
            responseText: "Greetings! I'm InfoBot, your information specialist. I can provide you with helpful facts and guidance."
        }
    ];
    
    console.log('Creating demo assistants...');
    
    for (const assistant of demoAssistants) {
        try {
            const response = await fetch(API_BASE_URL, {
                method: 'POST',
                headers: JSON_HEADERS,
                body: JSON.stringify(assistant)
            });
            
            if (response.ok) {
                console.log(`Created demo assistant: ${assistant.name}`);
            }
        } catch (error) {
            console.error(`Failed to create demo assistant ${assistant.name}:`, error);
        }
    }
    
    // Refresh the UI
    loadAssistants();
}

/**
 * Test all API endpoints
 * 
 * This function runs a comprehensive test of all API functionality
 * Useful for development and verification
 */
async function runApiTests() {
    console.log('Running API Tests...');
    
    try {
        // Test 1: Health Check
        console.log('Test 1: Health Check');
        await checkApiHealth();
        
        // Test 2: Create Assistant
        console.log('Test 2: Create Assistant');
        const testAssistant = {
            name: "Yash-SmartBot",
            responseText: "This is a test response from Yash-SmartBot"
        };
        
        const createResponse = await fetch(API_BASE_URL, {
            method: 'POST',
            headers: JSON_HEADERS,
            body: JSON.stringify(testAssistant)
        });
        
        console.log('Create Assistant Result:', await createResponse.json());
        
        // Test 3: Send Message
        console.log('Test 3: Send Message');
        const messageResponse = await fetch(`${API_BASE_URL}/Yash-SmartBot/message`, {
            method: 'POST',
            headers: JSON_HEADERS,
            body: JSON.stringify({ message: "Hello Yash-SmartBot!" })
        });
        
        console.log('Send Message Result:', await messageResponse.json());
        
        // Test 4: Get All Assistants
        console.log('Test 4: Get All Assistants');
        const allAssistantsResponse = await fetch(API_BASE_URL);
        console.log('All Assistants:', await allAssistantsResponse.json());
        
        console.log('All API tests completed');
        
    } catch (error) {
        console.error('API Test Error:', error);
    }
}

// ===================================================================
// CONSOLE HELPERS FOR DEVELOPMENT
// ===================================================================

// Make functions available in browser console for testing
window.digitalAssistant = {
    createDemoAssistants,
    runApiTests,
    loadAssistants,
    checkApiHealth,
    API_BASE_URL
};

console.log('Development helpers available via window.digitalAssistant');
console.log('Try: digitalAssistant.createDemoAssistants()');
console.log('Try: digitalAssistant.runApiTests()');
