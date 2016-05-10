package be.ordina.ordineo;

import be.ordina.ordineo.batch.DueDateTasklet;
import be.ordina.ordineo.model.Milestone;
import be.ordina.ordineo.model.Objective;
import be.ordina.ordineo.repository.MilestoneRepository;
import be.ordina.ordineo.repository.ObjectiveRepository;
import be.ordina.ordineo.util.TestUtil;
import org.hamcrest.CoreMatchers;
import org.hibernate.validator.HibernateValidator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;

import static org.junit.Assert.assertThat;

/**
 * Created by PhDa on 9/05/2016.
 */
@SpringApplicationConfiguration(classes=MilestoneCoreApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
@WebIntegrationTest({"eureka.client.enabled:false"})
public class SchedulerTest {

    @Autowired
    private DueDateTasklet dueDateTasklet;

    @Autowired
    ObjectiveRepository objectiveRepository;

    @Autowired
    MilestoneRepository milestoneRepository;

    private LocalValidatorFactoryBean localValidatorFactory;
    private Milestone milestone;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @Before
    public void setup() throws Exception {
        localValidatorFactory = new LocalValidatorFactoryBean();
        localValidatorFactory.setProviderClass(HibernateValidator.class);
        localValidatorFactory.afterPropertiesSet();
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        TestUtil.setAuthorities();

        milestone = createMilestone();
    }

    @After
    public void cleanUpStreams() {
        System.setOut(null);
        System.setErr(null);
    }

    private Milestone createMilestone(){
        Milestone milestone = milestoneRepository.findByUsernameOrderByDate("gide").get(0);
        Objective objective = objectiveRepository.findByTitleOrTags("scrum").get(0);

        milestone.setObjective(objective);
        milestone.setDueDate(LocalDate.now().plusWeeks(1));
        milestoneRepository.save(milestone);
        return milestone;
    }

    @Test
    public void testScheduler() throws Exception {
        dueDateTasklet.execute();
        assertThat(outContent.toString(), CoreMatchers.containsString("Milestone with id: 4 is due in less than 2 weeks"));
    }
}
