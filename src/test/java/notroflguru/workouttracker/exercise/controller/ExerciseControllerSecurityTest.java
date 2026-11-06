package notroflguru.workouttracker.exercise.controller;

import notroflguru.workouttracker.exercise.service.ExerciseService;
import notroflguru.workouttracker.user.security.JwtCore;
import notroflguru.workouttracker.user.service.UserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExerciseController.class)
@ActiveProfiles("test")
public class ExerciseControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExerciseService exerciseService;

    @MockitoBean
    private JwtCore jwtCore;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void shouldAllowAccessForEveryone() throws Exception {
        mockMvc.perform(get("/api/exercise/all"))
                .andExpect(status().isOk());
    }
}
