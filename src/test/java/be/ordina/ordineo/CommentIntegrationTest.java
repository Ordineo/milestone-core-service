package be.ordina.ordineo;

import be.ordina.ordineo.model.Comment;
import be.ordina.ordineo.model.Objective;
import be.ordina.ordineo.repository.CommentRepository;
import be.ordina.ordineo.util.TestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.restdocs.RestDocumentation;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by PhDa on 4/05/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MilestoneCoreApplication.class)
@WebIntegrationTest({"eureka.client.enabled:false"})
public class CommentIntegrationTest {
    @Autowired
    private CommentRepository commentRepository;

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

    @Before
    public void setup() throws Exception {
        this.document = document("{method-name}");
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .apply(documentationConfiguration(this.restDocumentation).uris().withScheme("https")).alwaysDo(this.document)
                .build();
        objectWriter = objectMapper.writer();

        authToken = TestUtil.getAuthToken();
        TestUtil.setAuthorities();
    }

    @Test
    public void getExistingComment() throws Exception {
        mockMvc.perform(get("/api/comments/1")
                .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("PhDa")))
                .andExpect(jsonPath("$.message", is("Test message")))
                .andExpect(jsonPath("$.createDate", is("2011-07-11")))
                .andExpect(jsonPath("$._links.self.href", endsWith("/comments/1")))
                .andExpect(jsonPath("$._links.milestone.href", endsWith("/milestone")))
                .andExpect(jsonPath("$._embedded.milestone._links.self.href", endsWith("/milestones/1{?projection}")))
                .andDo(document("{method-name}", responseFields(
                        fieldWithPath("_embedded.milestone.username").description("The milestone's unique database identifier"),
                        fieldWithPath("_embedded.milestone.objective").description("The milestone's objective").type(Objective.class),
                        fieldWithPath("_embedded.milestone.createDate").description("When the milestone was created").type(LocalDate.class),
                        fieldWithPath("_embedded.milestone.dueDate").optional().description("When the milestone is due").type(LocalDate.class),
                        fieldWithPath("_embedded.milestone.endDate").description("When the milestone will end").type(LocalDate.class),
                        fieldWithPath("_embedded.milestone.moreInformation").description("More information about the milestone"),
                        fieldWithPath("_embedded.milestone.comments[]").description("The embedded milestones comments"),
                        fieldWithPath("_embedded.milestone._links").description("links to other resources"),
                        fieldWithPath("username").description("The user who posted this comment"),
                        fieldWithPath("message").description("The comment's message"),
                        fieldWithPath("createDate").description("The comment's creation date"),
                        fieldWithPath("_links").description("links to other resources")
                )));
    }

    @Test
    public void getNonExistingCommentShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/comments/999")
                .header("Authorization", authToken))
                .andExpect(status().isNotFound());
    }

    @Test
    public void list() throws Exception {
        mockMvc.perform(get("/api/comments")
                .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.comments", hasSize(1)));
    }


    @Test
    public void postComment() throws Exception {
        String string = "{\n" +
                "  \"username\": \"PhDa\",\n" +
                "  \"createDate\": \"2016-02-01\",\n" +
                "  \"message\": \"test\",\n" +
                "  \"milestone\" : \"http://localhost:8080/api/milestone/1\"\n" +
                "}";

        CommentIntegrationTest.ConstrainedFields fields = new CommentIntegrationTest.ConstrainedFields(Comment.class);

        mockMvc.perform(post("/api/comments").content(string).contentType(MediaTypes.HAL_JSON)
                .header("Authorization", authToken))
                .andExpect(status().isCreated())
                .andDo(document("{method-name}", requestFields(
                        fields.withPath("username").description("The user who posted this comment"),
                        fields.withPath("message").description("The comment's message"),
                        fields.withPath("createDate").description("The comment's creation date"),
                        fields.withPath("milestone").description("The comment's milestone")
                )))
                .andReturn().getResponse().getHeader("Location");
    }

    @Test
    public void postCommentWithNullShouldReturnBadRequest() throws Exception {
        Comment comment = new Comment();
        comment.setId(7L);
        comment.setMilestone(null);
        String string = objectWriter.writeValueAsString(comment);

        CommentIntegrationTest.ConstrainedFields fields = new CommentIntegrationTest.ConstrainedFields(Comment.class);

        mockMvc.perform(post("/api/comments").content(string).contentType(MediaTypes.HAL_JSON)
                .header("Authorization", authToken))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getHeader("Location");
    }

    @Test
    public void updateComment() throws Exception {
        Comment comment = commentRepository.findOne(1L);
        comment.setUsername("PhDa");
        comment.getMilestone();
        String string = objectWriter.writeValueAsString(comment);

        mockMvc.perform(put("/api/comments/" +comment.getId()).content(string).contentType(MediaTypes.HAL_JSON)
                .header("Authorization", authToken))
                .andExpect(status().isNoContent());
    }

    @Test
    public void updateCommentWithNullValueShouldReturnBadRequest() throws Exception {
        Comment comment = commentRepository.findOne(1L);
        comment.setUsername(null);
        String string = objectWriter.writeValueAsString(comment);

        mockMvc.perform(put("/api/comments/" +comment.getId()).content(string).contentType(MediaTypes.HAL_JSON)
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
