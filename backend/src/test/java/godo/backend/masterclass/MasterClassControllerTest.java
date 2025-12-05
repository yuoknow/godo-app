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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
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

    @Nested
    @DisplayName("POST /api/master-classes - Validation Tests")
    class ValidationTests {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        @DisplayName("Should return bad request when title is blank or whitespace")
        void shouldReturnBadRequestWhenTitleIsInvalid(String invalidTitle) throws Exception {
            String titleJson = invalidTitle == null
                    ? "null"
                    : "\""
                            + invalidTitle
                                    .replace("\"", "\\\"")
                                    .replace("\n", "\\n")
                                    .replace("\t", "\\t") + "\"";
            String json = String.format(
                    "{\"title\":%s,\"description\":\"Описание\",\"instructor\":\"Инструктор\",\"durationMinutes\":120,\"price\":5000.00}",
                    titleJson);

            mockMvc.perform(post("/api/master-classes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Название мастер-класса обязательно"));
        }

        @Test
        @DisplayName("Should return bad request when title is missing")
        void shouldReturnBadRequestWhenTitleIsMissing() throws Exception {
            String json =
                    "{\"description\":\"Описание\",\"instructor\":\"Инструктор\",\"durationMinutes\":120,\"price\":5000.00}";

            mockMvc.perform(post("/api/master-classes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Название мастер-класса обязательно"));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        @DisplayName("Should return bad request when description is invalid")
        void shouldReturnBadRequestWhenDescriptionIsInvalid(String invalidDescription) throws Exception {
            String json = String.format(
                    "{\"title\":\"Курс\",\"description\":%s,\"instructor\":\"Инструктор\",\"durationMinutes\":120,\"price\":5000.00}",
                    invalidDescription == null ? "null" : "\"" + invalidDescription + "\"");

            mockMvc.perform(post("/api/master-classes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.description").value("Описание обязательно"));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        @DisplayName("Should return bad request when instructor is invalid")
        void shouldReturnBadRequestWhenInstructorIsInvalid(String invalidInstructor) throws Exception {
            String json = String.format(
                    "{\"title\":\"Курс\",\"description\":\"Описание\",\"instructor\":%s,\"durationMinutes\":120,\"price\":5000.00}",
                    invalidInstructor == null ? "null" : "\"" + invalidInstructor + "\"");

            mockMvc.perform(post("/api/master-classes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.instructor").value("Имя инструктора обязательно"));
        }

        @Test
        @DisplayName("Should return bad request when duration is negative")
        void shouldReturnBadRequestWhenDurationIsNegative() throws Exception {
            String json =
                    "{\"title\":\"Курс\",\"description\":\"Описание\",\"instructor\":\"Инструктор\",\"durationMinutes\":-120,\"price\":5000.00}";

            mockMvc.perform(post("/api/master-classes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.durationMinutes").value("Длительность должна быть положительным числом"));
        }

        @Test
        @DisplayName("Should return bad request when duration is zero")
        void shouldReturnBadRequestWhenDurationIsZero() throws Exception {
            String json =
                    "{\"title\":\"Курс\",\"description\":\"Описание\",\"instructor\":\"Инструктор\",\"durationMinutes\":0,\"price\":5000.00}";

            mockMvc.perform(post("/api/master-classes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.durationMinutes").value("Длительность должна быть положительным числом"));
        }

        @Test
        @DisplayName("Should return bad request when duration is null")
        void shouldReturnBadRequestWhenDurationIsNull() throws Exception {
            String json =
                    "{\"title\":\"Курс\",\"description\":\"Описание\",\"instructor\":\"Инструктор\",\"durationMinutes\":null,\"price\":5000.00}";

            mockMvc.perform(post("/api/master-classes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.durationMinutes").value("Длительность обязательна"));
        }

        @Test
        @DisplayName("Should return bad request when price is negative")
        void shouldReturnBadRequestWhenPriceIsNegative() throws Exception {
            String json =
                    "{\"title\":\"Курс\",\"description\":\"Описание\",\"instructor\":\"Инструктор\",\"durationMinutes\":120,\"price\":-1000.00}";

            mockMvc.perform(post("/api/master-classes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.price").value("Цена должна быть положительным числом"));
        }

        @Test
        @DisplayName("Should return bad request when price is zero")
        void shouldReturnBadRequestWhenPriceIsZero() throws Exception {
            String json =
                    "{\"title\":\"Курс\",\"description\":\"Описание\",\"instructor\":\"Инструктор\",\"durationMinutes\":120,\"price\":0.00}";

            mockMvc.perform(post("/api/master-classes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.price").value("Цена должна быть положительным числом"));
        }

        @Test
        @DisplayName("Should return bad request when price is null")
        void shouldReturnBadRequestWhenPriceIsNull() throws Exception {
            String json =
                    "{\"title\":\"Курс\",\"description\":\"Описание\",\"instructor\":\"Инструктор\",\"durationMinutes\":120,\"price\":null}";

            mockMvc.perform(post("/api/master-classes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.price").value("Цена обязательна"));
        }

        @Test
        @DisplayName("Should return bad request when multiple required fields are missing")
        void shouldReturnBadRequestWhenRequiredFieldsAreMissing() throws Exception {
            String json = "{\"title\":\"Курс\"}";

            mockMvc.perform(post("/api/master-classes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.description").value("Описание обязательно"))
                    .andExpect(jsonPath("$.instructor").value("Имя инструктора обязательно"))
                    .andExpect(jsonPath("$.durationMinutes").value("Длительность обязательна"))
                    .andExpect(jsonPath("$.price").value("Цена обязательна"));
        }

        @Test
        @DisplayName("Should return bad request when JSON is invalid")
        void shouldReturnBadRequestWhenJsonIsInvalid() throws Exception {
            String invalidJson = "{invalid json}";

            mockMvc.perform(post("/api/master-classes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson))
                    .andExpect(status().isBadRequest());
        }
    }
}
