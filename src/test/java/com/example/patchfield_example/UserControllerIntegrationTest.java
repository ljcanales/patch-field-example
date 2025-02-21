package com.example.patchfield_example;

import com.example.patchfield_example.data.entities.User;
import com.example.patchfield_example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        // Clear the repository before each test
        userRepository.deleteAll();
    }

    @Test
    void testCreateUser() throws Exception {
        String jsonRequest = """
            {
                "name": "John Doe",
                "email": "johndoe@example.com"
            }
            """;

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("johndoe@example.com"));
    }

    @Test
    void testPatchUserName() throws Exception {
        // Add a user to the repository
        User user = new User();
        user.setName("Original Name");
        user.setEmail("original@example.com");
        user = userRepository.save(user);

        String jsonPatchRequest = """
            {
                "name": "Updated Name"
            }
            """;

        // Perform the PATCH request
        mockMvc.perform(patch("/users/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPatchRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("original@example.com")); // Email remains unchanged
    }

    @Test
    void testPatchRemoveUserEmail() throws Exception {
        // Add a user to the repository
        User user = new User();
        user.setName("Original Name");
        user.setEmail("original@example.com");
        user = userRepository.save(user);

        String jsonPatchRequest = """
            {
                "email": null
            }
            """;

        // Perform the PATCH request
        mockMvc.perform(patch("/users/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPatchRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value("Original Name")) // Name remains unchanged
                .andExpect(jsonPath("$.email").isEmpty());
    }

}
