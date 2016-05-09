package be.ordina.ordineo;

import be.ordina.ordineo.batch.DueDateTasklet;
import be.ordina.ordineo.model.Milestone;
import be.ordina.ordineo.model.Objective;
import be.ordina.ordineo.repository.MilestoneRepository;
import be.ordina.ordineo.repository.ObjectiveRepository;
import org.hamcrest.CoreMatchers;
import org.hibernate.validator.HibernateValidator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by PhDa on 9/05/2016.
 */
@ContextConfiguration(classes=MilestoneCoreApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class SchedulerTest {

    @Autowired
    private DueDateTasklet dueDateTasklet;

    @Autowired
    ObjectiveRepository objectiveRepository;

    @Autowired
    MilestoneRepository milestoneRepository;


    private LocalValidatorFactoryBean localValidatorFactory;
    private Set<ConstraintViolation<Milestone>> constraintViolations;
    private Milestone milestone;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @Before
    public void setup() {
        localValidatorFactory = new LocalValidatorFactoryBean();
        localValidatorFactory.setProviderClass(HibernateValidator.class);
        localValidatorFactory.afterPropertiesSet();
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
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
