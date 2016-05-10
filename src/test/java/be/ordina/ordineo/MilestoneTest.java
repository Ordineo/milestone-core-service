package be.ordina.ordineo;


import be.ordina.ordineo.model.Milestone;
import be.ordina.ordineo.model.Objective;
import be.ordina.ordineo.repository.MilestoneRepository;
import be.ordina.ordineo.repository.ObjectiveRepository;
import be.ordina.ordineo.util.TestUtil;
import org.hibernate.validator.HibernateValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.Assert.assertTrue;

@SpringApplicationConfiguration(classes=MilestoneCoreApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
//"@WebIntegrationTest({"eureka.client.enabled:false"})
@Transactional
public class MilestoneTest {

    private LocalValidatorFactoryBean localValidatorFactory;
    private Set<ConstraintViolation<Milestone>> constraintViolations;
    private Milestone milestone;

    @Autowired
    ObjectiveRepository objectiveRepository;

    @Autowired
    MilestoneRepository milestoneRepository;

    @Before
    public void setup() throws Exception {
        localValidatorFactory = new LocalValidatorFactoryBean();
        localValidatorFactory.setProviderClass(HibernateValidator.class);
        localValidatorFactory.afterPropertiesSet();

        TestUtil.setAuthorities();

        milestone = createMilestone();
    }

    private Milestone createMilestone(){
        Milestone milestone = milestoneRepository.findByUsernameOrderByDate("gide").get(0);
        Objective objective = objectiveRepository.findByTitleOrTags("scrum").get(0);


        milestone.setObjective(objective);
        milestoneRepository.save(milestone);
        return milestone;
    }

    @Test
    public void validateMilestone() {
        constraintViolations = localValidatorFactory
                .validate(milestone);
        assertTrue(constraintViolations.stream().count() == 0);
    }

    @Test
    public void objectiveIsNull() {
        milestone.setObjective(null);
        constraintViolations = localValidatorFactory
                .validate(milestone);
        assertTrue(constraintViolations.stream().filter(m -> m.getMessage().equals("may not be null")).count() > 0);
    }

    @Test
    public void createDateIsNull() {
        milestone.setCreateDate(null);
        constraintViolations = localValidatorFactory
                .validate(milestone);
        assertTrue(constraintViolations.stream().filter(m -> m.getMessage().equals("may not be null")).count() > 0);
    }
    @Test
    public void dueDateIsNull() {
        milestone.setDueDate(null);
        constraintViolations = localValidatorFactory
                .validate(milestone);
        assertTrue(constraintViolations.stream().filter(m -> m.getMessage().equals("may not be null")).count() > 0);
    }
    @Test
    public void usernameTooLong() {
        milestone.setUsername("1234567890" +
                "1234567890" +
                "1234567890" +
                "1234567890" );
       constraintViolations = localValidatorFactory
                .validate(milestone);
        assertTrue(constraintViolations.stream().filter(m -> m.getMessage().equals("size must be between 2 and 30")).count() > 0);
    }
    @Test
    public void usernameTooShort() {
        milestone.setUsername("1");
        constraintViolations = localValidatorFactory
                .validate(milestone);
        assertTrue(constraintViolations.stream().filter(m -> m.getMessage().equals("size must be between 2 and 30")).count() > 0);
    }
    @Test
    public void usernameIsNull() {
        milestone.setUsername(null);
        constraintViolations = localValidatorFactory
                .validate(milestone);
        assertTrue(constraintViolations.stream().filter(m -> m.getMessage().equals("may not be null")).count() > 0);
    }
    @Test(expected = DataIntegrityViolationException.class)
    public void usernameObjectiveIsNotUnique(){
        Milestone milestone = new Milestone();
        Objective objective = objectiveRepository.findByTitleOrTags("scrum").get(0);

        milestone.setUsername("gide");
        milestone.setObjective(objective);
        milestone.setCreateDate(LocalDate.now());
        milestone.setDueDate(LocalDate.now());
        milestoneRepository.save(milestone);

        System.out.println(milestoneRepository.findByUsernameOrderByDate("gide").contains(milestone));

    }
}
