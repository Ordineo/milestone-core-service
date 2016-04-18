package be.ordina.ordineo;

import be.ordina.ordineo.model.Milestone;
import be.ordina.ordineo.model.Objective;
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
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by gide on 11/04/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MilestoneCoreApplication.class)
@WebAppConfiguration
public class MilestoneIntegrationTest {

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
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .apply(documentationConfiguration(this.restDocumentation).uris().withScheme("https"))
                .build();
        objectWriter = objectMapper.writer();
    }

    @Test
    public void getExistingMilestone() throws Exception {
        mockMvc.perform(get("/api/milestones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("gide")))
                .andExpect(jsonPath("$.createDate", is("2016-02-01")))
                .andExpect(jsonPath("$.dueDate", is("2016-12-31")))
                .andExpect(jsonPath("$.endDate", is("2016-03-01")))
                .andExpect(jsonPath("$.moreInformation", is("time to upgrade from java 6 to java 8 certificate.")))
                .andExpect(jsonPath("$._embedded.objective.title", is("Java Certificate")))
                .andExpect(jsonPath("$._embedded.objective.description", is("description certificate")))
                .andExpect(jsonPath("$._embedded.objective.objectiveType", is("CERTIFICATE")))
                .andExpect(jsonPath("$._embedded.objective.tags", is(Arrays.asList("java"))))
                .andExpect(jsonPath("$._embedded.objective._links.self.href", endsWith("/objectives/2{?projection}")))
                .andExpect(jsonPath("$._links.self.href", endsWith("/milestones/1")))
                .andExpect(jsonPath("$._links.milestone.href", endsWith("/milestones/1{?projection}")))
                .andExpect(jsonPath("$._links.objective.href", endsWith("/milestones/1/objective")))
                .andDo(document("{method-name}", responseFields(
                        fieldWithPath("username").description("The milestone's unique database identifier"),
                        fieldWithPath("_embedded.objective").description("The milestone's objective").type(Objective.class),
                        fieldWithPath("createDate").description("When the milestone was created").type(LocalDate.class),
                        fieldWithPath("dueDate").optional().description("When the milestone is due").type(LocalDate.class),
                        fieldWithPath("endDate").description("When the milestone will end").type(LocalDate.class),
                        fieldWithPath("moreInformation").description("More information about the milestone"),
                        fieldWithPath("_links").description("links to resources")
                )));
    }

    @Test
    public void getExistingMilestoneWithProjection() throws Exception {
        mockMvc.perform(get("/api/milestones/1?projection=milestoneView"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("gide")))
                .andExpect(jsonPath("$.createDate", is("2016-02-01")))
                .andExpect(jsonPath("$.dueDate", is("2016-12-31")))
                .andExpect(jsonPath("$.endDate", is("2016-03-01")))
                .andExpect(jsonPath("$.moreInformation", is("time to upgrade from java 6 to java 8 certificate.")))
                .andExpect(jsonPath("$.objective.title", is("Java Certificate")))
                .andExpect(jsonPath("$.objective.description", is("description certificate")))
                .andExpect(jsonPath("$.objective.objectiveType", is("CERTIFICATE")))
                .andExpect(jsonPath("$.objective.tags", is(Arrays.asList("java"))))
                .andExpect(jsonPath("$.objective._links.self.href", endsWith("/objectives/2{?projection}")))
                .andExpect(jsonPath("$._links.self.href", endsWith("/milestones/1")))
                .andExpect(jsonPath("$._links.milestone.href", endsWith("/milestones/1{?projection}")))
                .andExpect(jsonPath("$._links.objective.href", endsWith("/milestones/1/objective")))
                .andDo(document("{method-name}", responseFields(
                        fieldWithPath("username").description("The milestone's unique database identifier"),
                        fieldWithPath("objective").description("The milestone's objective").type(Objective.class),
                        fieldWithPath("createDate").description("When the milestone was created").type(LocalDate.class),
                        fieldWithPath("dueDate").optional().description("When the milestone is due").type(LocalDate.class),
                        fieldWithPath("endDate").description("When the milestone will end").type(LocalDate.class),
                        fieldWithPath("moreInformation").description("More information about the milestone"),
                        fieldWithPath("_links").description("links to resources")
                )));
    }

    @Test
    public void getNonExistingMilestoneShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/milestones/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void list() throws Exception {
        mockMvc.perform(get("/api/milestones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.milestones", hasSize(5)));
    }

    @Test
    public void findByUsernameOrderByDate() throws Exception {
        mockMvc.perform(get("/api/milestones/search/findByUsername?username=gide"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.milestones", hasSize(2)))
                .andExpect(jsonPath("$._embedded.milestones[0]._links.self.href", endsWith("/milestones/2")))
                .andExpect(jsonPath("$._embedded.milestones[1]._links.self.href", endsWith("/milestones/1")))
                .andDo(document("{method-name}", responseFields(
                        fieldWithPath("_embedded.milestones[].username").description("The milestone's unique database identifier"),
                        fieldWithPath("_embedded.milestones[].objective").description("The milestone's objective").type(Objective.class),
                        fieldWithPath("_embedded.milestones[].createDate").description("When the milestone was created").type(LocalDate.class),
                        fieldWithPath("_embedded.milestones[].dueDate").optional().description("When the milestone is due").type(LocalDate.class),
                        fieldWithPath("_embedded.milestones[].endDate").description("When the milestone will end").type(LocalDate.class),
                        fieldWithPath("_embedded.milestones[].moreInformation").description("More information about the milestone"),
                        fieldWithPath("_embedded.milestones[]._links").description("links to other resources"),
                        fieldWithPath("_links").description("links to resources")
                )));
    }

    @Test
    public void postMilestone() throws Exception {
        String string = "{\n" +
                "  \"username\": \"PhDa\",\n" +
                "  \"createDate\": \"2016-02-01\",\n" +
                "  \"dueDate\": \"2016-12-31\",\n" +
                "  \"endDate\": \"2016-03-01\",\n" +
                "  \"moreInformation\": \"test\",\n" +
                "  \"objective\" : \"http://localhost:8080/api/objectives/1\"\n" +
                "}";
        ConstrainedFields fields = new ConstrainedFields(Milestone.class);

        mockMvc.perform(post("/api/milestones").content(string).contentType(MediaTypes.HAL_JSON))
                .andExpect(status().isCreated())
                .andDo(document("{method-name}", requestFields(
                        fields.withPath("username").description("The milestone's unique database identifier"),
                        fields.withPath("objective").description("The milestone's URI"),
                        fields.withPath("createDate").description("When the milestone was created").type(LocalDate.class),
                        fields.withPath("dueDate").description("When the milestone is due").type(LocalDate.class),
                        fields.withPath("endDate").optional().description("When the milestone will end").type(LocalDate.class),
                        fields.withPath("moreInformation").optional().description("More information about the milestone")
                )))
                .andReturn().getResponse().getHeader("Location");
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
