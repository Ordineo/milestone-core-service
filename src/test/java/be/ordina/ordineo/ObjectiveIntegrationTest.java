package be.ordina.ordineo;

import be.ordina.ordineo.model.Objective;
import be.ordina.ordineo.model.ObjectiveType;
import be.ordina.ordineo.repository.ObjectiveRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.hateoas.MediaTypes;
import org.springframework.restdocs.RestDocumentation;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;


import java.time.LocalDate;
import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by PhDa on 11/04/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MilestoneCoreApplication.class)
@WebAppConfiguration
public class ObjectiveIntegrationTest {

    @Autowired
    private ObjectiveRepository objectiveRepository;

    private MockMvc mockMvc;

    private ObjectWriter objectWriter;

    @Autowired
    private ObjectMapper objectMapper;

    @Rule
    public RestDocumentation restDocumentation = new RestDocumentation("target/generated-snippets");
    @Autowired
    private WebApplicationContext wac;
    private RestDocumentationResultHandler document;

    @Before
    public void setup() {
        this.document = document("{method-name}");
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .apply(documentationConfiguration(this.restDocumentation).uris().withScheme("https")).alwaysDo(this.document)
                .build();
        objectWriter = objectMapper.writer();
    }

    @Test
    public void getExistingObjective() throws Exception {
        mockMvc.perform(get("/api/objectives/1")
                .header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJOaXZlayIsInJvbGUiOlt7ImF1dGhvcml0eSI6IlJPTEVfVVNFUiJ9XSwiY3JlYXRlZCI6MTQ2MTkxNzMzMzgyOCwiZXhwIjoxNDYyNTIyMTMzfQ.-hfRSW58Sz6kBE1ZtcGRlfuyqrNvN0nI975iA4bnTBCZIAmj7eJTa2BDsk7JUa5tQtKhLNlFySxLTGbb8MaIIg"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Spring Boot")))
                .andExpect(jsonPath("$.description", is("Lorem Ipsum is slechts een proeftekst uit het drukkerij- en zetterijwezen. Lorem Ipsum is de standaard proeftekst in deze bedrijfstak sinds de 16e eeuw, toen een onbekende drukker een zethaak met letters")))
                .andExpect(jsonPath("$.tags[0]", is("java")))
                .andExpect(jsonPath("$.tags[1]", is("spring")))
                .andExpect(jsonPath("$._links.self.href", endsWith("/objectives/1")))
                .andExpect(jsonPath("$._links.objective.href", endsWith("/objectives/1{?projection}")))
                .andDo(document("{method-name}",responseFields(
                        fieldWithPath("title").description("The objective's unique database identifier"),
                        fieldWithPath("tags").description("The objective's related tags"),
                        fieldWithPath("description").description("The objective's description"),
                        fieldWithPath("objectiveType").description("The objective's type"),
                        fieldWithPath("_links").description("links to other resources")
                )));
    }

    @Test
    public void getExistingObjectiveWithProjection() throws Exception {
        mockMvc.perform(get("/api/objectives/1?projection=objectiveTitleAndTagsView")
                .header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJOaXZlayIsInJvbGUiOlt7ImF1dGhvcml0eSI6IlJPTEVfVVNFUiJ9XSwiY3JlYXRlZCI6MTQ2MTkxNzMzMzgyOCwiZXhwIjoxNDYyNTIyMTMzfQ.-hfRSW58Sz6kBE1ZtcGRlfuyqrNvN0nI975iA4bnTBCZIAmj7eJTa2BDsk7JUa5tQtKhLNlFySxLTGbb8MaIIg"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Spring Boot")))
                .andExpect(jsonPath("$.tags[0]", is("java")))
                .andExpect(jsonPath("$.tags[1]", is("spring")))
                .andExpect(jsonPath("$._links.self.href", endsWith("/objectives/1")))
                .andExpect(jsonPath("$._links.objective.href", endsWith("/objectives/1{?projection}")))
                .andDo(document("{method-name}",responseFields(
                        fieldWithPath("title").description("The objective's unique database identifier"),
                        fieldWithPath("tags").description("The objective's related tags"),
                        fieldWithPath("_links").description("links to other resources")
                )));
    }

    @Test
    public void getNonExistingObjectiveShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/objectives/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void list() throws Exception {
        mockMvc.perform(get("/api/objectives")
                .header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJOaXZlayIsInJvbGUiOlt7ImF1dGhvcml0eSI6IlJPTEVfVVNFUiJ9XSwiY3JlYXRlZCI6MTQ2MTkxNzMzMzgyOCwiZXhwIjoxNDYyNTIyMTMzfQ.-hfRSW58Sz6kBE1ZtcGRlfuyqrNvN0nI975iA4bnTBCZIAmj7eJTa2BDsk7JUa5tQtKhLNlFySxLTGbb8MaIIg"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.objectives", hasSize(3)));
    }


    @Test
    public void findByTitleOrTag() throws Exception{
        mockMvc.perform(get("/api/objectives/search/findByTitleOrTags?text=boot")
                .header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJOaXZlayIsInJvbGUiOlt7ImF1dGhvcml0eSI6IlJPTEVfVVNFUiJ9XSwiY3JlYXRlZCI6MTQ2MTkxNzMzMzgyOCwiZXhwIjoxNDYyNTIyMTMzfQ.-hfRSW58Sz6kBE1ZtcGRlfuyqrNvN0nI975iA4bnTBCZIAmj7eJTa2BDsk7JUa5tQtKhLNlFySxLTGbb8MaIIg"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.objectives", hasSize(1)))
                .andExpect(jsonPath("$._embedded.objectives[0].title",is("Spring Boot")))
                .andExpect(jsonPath("$._embedded.objectives[0].tags", is(Arrays.asList("java","spring"))))
                .andExpect(jsonPath("$._embedded.objectives[0].description", is("Lorem Ipsum is slechts een proeftekst uit het drukkerij- en zetterijwezen. Lorem Ipsum is de standaard proeftekst in deze bedrijfstak sinds de 16e eeuw, toen een onbekende drukker een zethaak met letters")))
                .andExpect(jsonPath("$._embedded.objectives[0].objectiveType", is("TRAINING")))
                .andExpect(jsonPath("$._embedded.objectives[0]._links.self.href", endsWith("/objectives/1")))
                .andExpect(jsonPath("$._embedded.objectives[0]._links.objective.href", endsWith("/objectives/1{?projection}"))
                ).andDo(document("{method-name}", responseFields(
                fieldWithPath("_embedded.objectives[].title").description("The objective's unique database identifier"),
                fieldWithPath("_embedded.objectives[].tags").description("The objective's tag"),
                fieldWithPath("_embedded.objectives[].description").description("The objective's description"),
                fieldWithPath("_embedded.objectives[].objectiveType").optional().description("The objective's type"),
                fieldWithPath("_embedded.objectives[]._links.self").description("The objective's self link"),
                fieldWithPath("_embedded.objectives[]._links.objective").description("The objective's uri link"),
                fieldWithPath("_links.self").description("The search objective self link")


                )));;
    }

    @Test
    public void postObjective() throws Exception {
        Objective objective = new Objective();
        objective.setId(7L);
        objective.setObjectiveType(ObjectiveType.BOOK);
        objective.setTitle("testObjective");
        objective.setDescription("testdescription");
        objective.addTag("Angularjs");
        objective.addTag("Angular material");
        String string = objectWriter.writeValueAsString(objective);

        ConstrainedFields fields = new ConstrainedFields(Objective.class);

        mockMvc.perform(post("/api/objectives").content(string).contentType(MediaTypes.HAL_JSON))
                .andExpect(status().isCreated())
                .andDo(document("{method-name}", requestFields(
                        fields.withPath("title").description("The objectives's unique database identifier"),
                        fields.withPath("objectiveType").description("The type of objective").type(ObjectiveType.class),
                        fields.withPath("description").description("More Information about the objective"),
                        fields.withPath("tags").description("Tags corresponding to this objective")
                )))
                .andReturn().getResponse().getHeader("Location");
    }

    @Test
    public void postObjectiveWithNullShouldReturnBadRequest() throws Exception {
        Objective objective = new Objective();
        objective.setId(7L);
        objective.setObjectiveType(ObjectiveType.BOOK);
        objective.setTitle(null);
        objective.setDescription("testdescription");
        objective.addTag("Angularjs");
        objective.addTag("Angular material");
        String string = objectWriter.writeValueAsString(objective);

        ConstrainedFields fields = new ConstrainedFields(Objective.class);

        mockMvc.perform(post("/api/objectives").content(string).contentType(MediaTypes.HAL_JSON))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getHeader("Location");
    }

    @Test
    public void updateObjective() throws Exception {
        Objective objective = objectiveRepository.findOne(2L);
        objective.setTitle("New Title");
        String string = objectWriter.writeValueAsString(objective);
        objective.addTag("NewTag");

        mockMvc.perform(put("/api/objectives/" +objective.getId()).content(string).contentType(MediaTypes.HAL_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void updateObjectiveWithNullValueShouldReturnBadRequest() throws Exception {
        Objective objective = objectiveRepository.findOne(2L);
        objective.setTitle(null);
        String string = objectWriter.writeValueAsString(objective);

        mockMvc.perform(put("/api/objectives/" +objective.getId()).content(string).contentType(MediaTypes.HAL_JSON))
                .andExpect(status().isBadRequest());
    }

    private static class ConstrainedFields {
        private final ConstraintDescriptions constraintDescriptions;

        ConstrainedFields(Class<?> input) {
            this.constraintDescriptions = new ConstraintDescriptions(input);
        }

        private FieldDescriptor withPath(String path) {
            return fieldWithPath(path).attributes(key("constraints").value(StringUtils
                    .collectionToDelimitedString(this.constraintDescriptions
                            .descriptionsForProperty(path), ". ")));
        }
    }
}
