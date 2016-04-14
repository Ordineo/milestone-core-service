package be.ordina.ordineo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.restdocs.RestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
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
    public void getExistingObjective() throws Exception {
        mockMvc.perform(get("/api/objectives/1"))
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
    public void getNonExistingObjectiveShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/objectives/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void list() throws Exception {
        mockMvc.perform(get("/api/objectives"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.objectives", hasSize(2)));
    }


    @Test
    public void findByTitleOrTag() throws Exception{
        mockMvc.perform(get("/api/objectives/search/findByTitleOrTags?text=boot"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.objectives", hasSize(1)))
                .andExpect(jsonPath("$._embedded.objectives[0].title",is("Spring Boot")))
                .andExpect(jsonPath("$._embedded.objectives[0].tags", is(Arrays.asList("java","spring"))))
                .andExpect(jsonPath("$._embedded.objectives[0].description", is("Lorem Ipsum is slechts een proeftekst uit het drukkerij- en zetterijwezen. Lorem Ipsum is de standaard proeftekst in deze bedrijfstak sinds de 16e eeuw, toen een onbekende drukker een zethaak met letters")))
                .andExpect(jsonPath("$._embedded.objectives[0].objectiveType", is("TRAINING")))
                .andExpect(jsonPath("$._embedded.objectives[0]._links.self.href", endsWith("/objectives/1")))
                .andExpect(jsonPath("$._embedded.objectives[0]._links.objective.href", endsWith("/objectives/1{?projection}")));
    }

}
