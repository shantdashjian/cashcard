package example.cashcard;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class CashCardControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CashCardRepository cashCardRepository;

    @Test
    public void shouldReturnACashCardWhenDataIsSaved() throws Exception {
        when(cashCardRepository.findById(1L))
                .thenReturn(Optional.of(new CashCard(99L, 100.00)));
        mockMvc.perform(get("/cashcards/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(99)))
                .andExpect(jsonPath("$.amount", is(100.00)));
        verify(cashCardRepository, times(1)).findById(1L);
    }
}
