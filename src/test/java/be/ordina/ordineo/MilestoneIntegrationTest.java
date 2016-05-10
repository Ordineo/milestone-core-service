package be.ordina.ordineo;

import be.ordina.ordineo.model.Comment;
import be.ordina.ordineo.model.Milestone;
import be.ordina.ordineo.model.Objective;
import be.ordina.ordineo.repository.MilestoneRepository;
import be.ordina.ordineo.security.JwtFilter;
import be.ordina.ordineo.util.TestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.restdocs.RestDocumentation;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by gide on 11/04/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MilestoneCoreApplication.class)
@WebIntegrationTest({"server.port:0", "eureka.client.enabled:false"})
@ActiveProfiles("test")
public class MilestoneIntegrationTest {

    @Autowired
    private MilestoneRepository milestoneRepository;

    private MockMvc mockMvc;

    private ObjectWriter objectWriter;
    
    private String authToken;

    @Autowired
    private ObjectMapper objectMapper;

    @Rule
    public RestDocumentation restDocumentation = new RestDocumentation("target/generated-snippets");
    @Autowired
    private WebApplicationContext wac;
    private RestDocumentationResultHandler document;


    TestUtil util = new TestUtil();

    @Before
    public void setup() throws Exception{
        this.document = document("{method-name}");
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .apply(documentationConfiguration(this.restDocumentation).uris().withScheme("https")).alwaysDo(this.document)
                .build();

        objectWriter = objectMapper.writer();
        authToken = util.getAuthToken();
        util.setAuthorities();
    }

    @Test
    public void getExistingMilestone() throws Exception {
        mockMvc.perform(get("/api/milestones/1")
                .header("Authorization", authToken))
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
                        fieldWithPath("_links").description("links to resources"),
                        fieldWithPath("_embedded.comments").description("The milestone's comments").type(Comment.class)
                )));
    }

    @Test
    public void getExistingMilestoneWithProjection() throws Exception {
        mockMvc.perform(get("/api/milestones/1?projection=milestoneView")
                .header("Authorization", authToken))
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
                        fieldWithPath("comments[]").description("The milestones comments"),
                        fieldWithPath("_links").description("links to resources")
                )));
    }

    @Test
    public void getNonExistingMilestoneShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/milestones/999")
                .header("Authorization", authToken))
                .andExpect(status().isNotFound());
    }

    @Test
    public void list() throws Exception {
        mockMvc.perform(get("/api/milestones")
                .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.milestones", hasSize(5)));
    }

    @Test
    public void findByUsernameOrderByDate() throws Exception {
        mockMvc.perform(get("/api/milestones/search/findByUsername?username=gide")
                .header("Authorization", authToken))
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
                        fieldWithPath("_embedded.milestones[].comments").description("The milestones comments").type(Comment.class),
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

        mockMvc.perform(post("/api/milestones").content(string).contentType(MediaTypes.HAL_JSON)
                .with(csrf())
                .header("Authorization", authToken))
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

    @Test
    public void postMilestoneWithoutObjectiveShouldReturnBadRequest() throws Exception {
        String string = "{\n" +
                "  \"username\": \"PhDa\",\n" +
                "  \"createDate\": \"2016-02-01\",\n" +
                "  \"dueDate\": \"2016-12-31\",\n" +
                "  \"endDate\": \"2016-03-01\",\n" +
                "  \"moreInformation\": \"test\"\n" +
                "}";
        ConstrainedFields fields = new ConstrainedFields(Milestone.class);

        mockMvc.perform(post("/api/milestones").content(string).contentType(MediaTypes.HAL_JSON)
                .with(csrf())
                .header("Authorization", authToken))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getHeader("Location");
    }

    @Test
    public void updateMilestone() throws Exception {
        Milestone milestone = milestoneRepository.findOne(2L);
        milestone.setEndDate(LocalDate.now());
        String string = objectWriter.writeValueAsString(milestone);

        StringBuilder sb = new StringBuilder(string);
        sb.insert(1,"\n  \"objective\" : \"http://localhost:8080/api/objectives/1\",");
        mockMvc.perform(put("/api/milestones/" +milestone.getId()).content(sb.toString()).contentType(MediaTypes.HAL_JSON)
                .with(csrf())
                .header("Authorization", authToken))
                .andExpect(status().isNoContent());
    }

    @Test
    public void updateMilestoneWithNullValueShouldReturnBadRequest() throws Exception {
        Milestone milestone = milestoneRepository.findOne(2L);
        milestone.setUsername(null);
        String string = objectWriter.writeValueAsString(milestone);

        string = string.substring(0,string.length()-1);
        string+=",\n  \"objective\" : \"http://localhost:8080/api/objectives/1\"\n" +
                "}";
        mockMvc.perform(put("/api/milestones/" +milestone.getId()).content(string).contentType(MediaTypes.HAL_JSON)
                .with(csrf())
                .header("Authorization", authToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateMilestoneWithoutObjectiveShouldReturnBadRequest() throws Exception {
        Milestone milestone = milestoneRepository.findOne(2L);
        String string = objectWriter.writeValueAsString(milestone);

        string = string.substring(0,string.length()-1);
        string+=",\n  \"objective\" : null\n" +
                "}";
        mockMvc.perform(put("/api/milestones/" +milestone.getId()).content(string).contentType(APPLICATION_JSON)
                .with(csrf())
                .header("Authorization", authToken))
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
