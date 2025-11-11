package com.user.infrastructure.api.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.application.user.dto.UserRequest;
import com.user.application.user.dto.UserResponse;
import com.user.application.user.usecase.UserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock private UserUseCase userUseCase;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        UserController controller = new UserController(userUseCase);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void register_shouldReturn201AndBody() throws Exception {

        UserRequest req = new UserRequest();
        req.setName("Alessandro");
        req.setEmail("alessandro@gmail.com");
        req.setPassword("password.123");
        req.setPhones(List.of(new UserRequest.PhoneRequest("1234567","1","57")));

        UUID id = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        UserResponse response = new UserResponse(
                id, "Alessandro", "alessandro@gmail.com",
                List.of(new UserResponse.PhoneResponse("1234567","1","57")),
                now, now, now, "jwt", true
        );

        when(userUseCase.register(any(UserRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.email").value("alessandro@gmail.com"))
                .andExpect(jsonPath("$.token").value("jwt"));
    }
}
