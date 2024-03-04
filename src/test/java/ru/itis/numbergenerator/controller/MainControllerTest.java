package ru.itis.numbergenerator.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.itis.numbergenerator.constant.Region;
import ru.itis.numbergenerator.controller.MainController;
import ru.itis.numbergenerator.model.CarNumber;
import ru.itis.numbergenerator.service.NumberService;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MainController.class)
@AutoConfigureMockMvc
public class MainControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private NumberService numberService;

    @Test
    void testFirstNextNumberIsNotNull() throws Exception {
        when(numberService.getNext()).thenReturn(new CarNumber(new char[]{'А','А','А'}, 0, Region.TATARSTAN));
        mockMvc.perform(get("/next"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("А000АА 116 RUS"));
    }

    @Test
    void testRandomNumberIsNotNull() throws Exception {
        when(numberService.getRandom()).thenReturn(new CarNumber(new char[]{'Х','В','Н'}, 146, Region.TATARSTAN));
        mockMvc.perform(get("/random"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Х146ВН 116 RUS"));
    }
}
