package be.ordina.ordineo;

import be.ordina.ordineo.model.Comment;
import be.ordina.ordineo.model.Milestone;
import be.ordina.ordineo.model.Objective;
import be.ordina.ordineo.repository.MilestoneRepository;
import org.hibernate.validator.HibernateValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.Assert.assertTrue;

/**
 * Created by PhDa on 4/05/2016.
 */
@ContextConfiguration(classes=MilestoneCoreApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class CommentTest {

    private LocalValidatorFactoryBean localValidatorFactory;
    private Set<ConstraintViolation<Comment>> constraintViolations;
    private Comment comment;

    @Autowired
    MilestoneRepository milestoneRepository;

    @Before
    public void setup() {
        localValidatorFactory = new LocalValidatorFactoryBean();
        localValidatorFactory.setProviderClass(HibernateValidator.class);
        localValidatorFactory.afterPropertiesSet();
        comment = createComment();
    }

    private Comment createComment(){
        Comment comment = new Comment();
        Milestone milestone = milestoneRepository.findByUsernameOrderByDate("gide").get(0);

        comment.setUsername("PhDa");
        comment.setCreateDate(LocalDate.now());
        comment.setMessage("New TestMessage");
        comment.setMilestone(milestone);

        return comment;
    }

    @Test
    public void validateObjective() {
        constraintViolations = localValidatorFactory
                .validate(comment);
        assertTrue(constraintViolations.stream().count() == 0);
    }

    @Test
    public void usernameIsNull() {
        comment.setUsername(null);
        constraintViolations = localValidatorFactory
                .validate(comment);
        assertTrue(constraintViolations.stream().filter(m -> m.getMessage().equals("may not be null")).count() > 0);
    }

    @Test
    public void createDateIsNull() {
        comment.setCreateDate(null);
        constraintViolations = localValidatorFactory
                .validate(comment);
        assertTrue(constraintViolations.stream().filter(m -> m.getMessage().equals("may not be null")).count() > 0);
    }

    @Test
    public void messageIsNull() {
        comment.setMessage(null);
        constraintViolations = localValidatorFactory
                .validate(comment);
        assertTrue(constraintViolations.stream().filter(m -> m.getMessage().equals("may not be null")).count() > 0);
    }

    @Test
    public void milestoneIsNull() {
        comment.setMilestone(null);
        constraintViolations = localValidatorFactory
                .validate(comment);
        assertTrue(constraintViolations.stream().filter(m -> m.getMessage().equals("may not be null")).count() > 0);
    }
}
