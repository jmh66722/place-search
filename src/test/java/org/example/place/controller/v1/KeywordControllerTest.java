package org.example.place.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureTestDatabase
@Sql({"classpath:/schema.sql", "classpath:/data.sql"})
@SpringBootTest
@AutoConfigureMockMvc
class KeywordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final static Class HandlerClass = KeywordController.class;
    private final static String BASE_API_PATH = "/v1";

    private ObjectMapper objectMapper = new ObjectMapper();


    @Test
    void getPopularKeywords() throws Exception {
        ResultActions resultActions = mockMvc.perform(
                        get(BASE_API_PATH+"/keywords/popular")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                );
                resultActions
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(handler().handlerType(HandlerClass))
                        .andExpect(handler().methodName("getPopularKeywords"))
                        .andExpect(jsonPath("$.result").value(true))
                        .andExpect(jsonPath("$.status").value(200))
                        .andExpect(jsonPath("$.data").isArray())
                        .andExpect(jsonPath("$.data[*].keyword").exists())
                        .andExpect(jsonPath("$.data[*].totalCount").exists())
                ;
    }
}