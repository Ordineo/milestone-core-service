package be.ordina.ordineo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Autowired
    private WebApplicationContext wac;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .build();
    }

    @Test
    public void get_existing() throws Exception {
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
                .andExpect(jsonPath("$._links.objective.href", endsWith("/milestones/1/objective")));
    }

    @Test
    public void get_existingWithProjection() throws Exception {
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
                .andExpect(jsonPath("$._links.objective.href", endsWith("/milestones/1/objective")));
    }

    @Test
    public void get_notfound() throws Exception {
        mockMvc.perform(get("/api/milestones/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void list() throws Exception {
        mockMvc.perform(get("/api/milestones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.milestones", hasSize(1)));
    }


}
