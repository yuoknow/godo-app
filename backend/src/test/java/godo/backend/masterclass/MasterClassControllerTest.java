package godo.backend.masterclass;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("MasterClassController Tests")
class MasterClassControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MasterClassRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    // Helper methods
    private MasterClass createValidMasterClass() {
        return new MasterClass(
                "Java для начинающих",
                "Изучите основы Java программирования",
                "Иван Иванов",
                120,
                new BigDecimal("5000.00"));
    }

    private MasterClass createMasterClass(
            String title, String description, String instructor, Integer durationMinutes, BigDecimal price) {
        return new MasterClass(title, description, instructor, durationMinutes, price);
    }

    private String toJson(MasterClass masterClass) throws Exception {
        return objectMapper.writeValueAsString(masterClass);
    }

    @Nested
    @DisplayName("GET /api/master-classes")
    class GetAllMasterClassesTests {

        @Test
        @DisplayName("Should return empty list when no master classes exist")
        void shouldReturnEmptyListWhenNoMasterClasses() throws Exception {
            mockMvc.perform(get("/api/master-classes"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        @DisplayName("Should return all master classes")
        void shouldGetAllMasterClasses() throws Exception {
            MasterClass masterClass1 = createMasterClass(
                    "Java для начинающих",
                    "Изучите основы Java программирования",
                    "Иван Иванов",
                    120,
                    new BigDecimal("5000.00"));
            repository.save(masterClass1);

            MasterClass masterClass2 = createMasterClass(
                    "Spring Boot продвинутый",
                    "Углубленное изучение Spring Boot",
                    "Петр Петров",
                    180,
                    new BigDecimal("8000.00"));
            repository.save(masterClass2);

            mockMvc.perform(get("/api/master-classes"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].title").exists())
                    .andExpect(jsonPath("$[1].title").exists());
        }
    }

    @Nested
    @DisplayName("POST /api/master-classes")
    class CreateMasterClassTests {

        @Test
        @DisplayName("Should create master class successfully")
        void shouldCreateMasterClass() throws Exception {
            MasterClass masterClass = createValidMasterClass();
            String json = toJson(masterClass);

            mockMvc.perform(post("/api/master-classes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.title").value("Java для начинающих"))
                    .andExpect(jsonPath("$.description").value("Изучите основы Java программирования"))
                    .andExpect(jsonPath("$.instructor").value("Иван Иванов"))
                    .andExpect(jsonPath("$.durationMinutes").value(120))
                    .andExpect(jsonPath("$.price").value(5000.00))
                    .andExpect(jsonPath("$.createdAt").exists());
        }

        @Test
        @DisplayName("Should set createdAt automatically")
        void shouldSetCreatedAtAutomatically() throws Exception {
            MasterClass masterClass =
                    createMasterClass("Курс", "Описание", "Инструктор", 120, new BigDecimal("5000.00"));
            String json = toJson(masterClass);

            String response = mockMvc.perform(post("/api/master-classes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            MasterClass created = objectMapper.readValue(response, MasterClass.class);
            assertNotNull(created.getCreatedAt());
        }

        @Test
        @DisplayName("Should create and retrieve master class")
        void shouldCreateAndRetrieveMasterClass() throws Exception {
            MasterClass masterClass = createMasterClass(
                    "React с нуля", "Изучите React с нуля до профи", "Мария Сидорова", 240, new BigDecimal("10000.00"));

            String json = toJson(masterClass);

            String response = mockMvc.perform(post("/api/master-classes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            MasterClass created = objectMapper.readValue(response, MasterClass.class);

            mockMvc.perform(get("/api/master-classes"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(created.getId()))
                    .andExpect(jsonPath("$[0].title").value("React с нуля"));
        }
    }
}
