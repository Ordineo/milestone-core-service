package be.ordina.ordineo;

import be.ordina.ordineo.model.Comment;
import be.ordina.ordineo.model.Milestone;
import be.ordina.ordineo.repository.MilestoneRepository;
import be.ordina.ordineo.util.TestUtil;
import org.hibernate.validator.HibernateValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
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
@WebIntegrationTest({"eureka.client.enabled:false"})
public class CommentTest {

    private LocalValidatorFactoryBean localValidatorFactory;
    private Set<ConstraintViolation<Comment>> constraintViolations;
    private Comment comment;

    @Autowired
    MilestoneRepository milestoneRepository;

    @Before
    public void setup() throws Exception {
        localValidatorFactory = new LocalValidatorFactoryBean();
        localValidatorFactory.setProviderClass(HibernateValidator.class);
        localValidatorFactory.afterPropertiesSet();

        TestUtil.setAuthorities();

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
    public void messageTooLong() {
        comment.setMessage("In tegenstelling tot wat algemeen aangenomen "+
                "wordt is Lorem Ipsum niet zomaar willekeurige tekst."+
                "het heeft zijn wortels in een stuk klassieke latijnse literatuur uit 45 "+
                "v.Chr. en is dus meer dan 2000 jaar oud. Richard McClintock, een professor "+
                "latijn aan de Hampden-Sydney College in Virginia, heeft één van de meer obscure "+
                "latijnse woorden, consectetur, uit een Lorem Ipsum passage opgezocht, en heeft "+
                "tijdens het zoeken naar het woord in de klassieke literatuur de onverdachte bron ontdekt. "+
                "Lorem Ipsum komt uit de secties 1.10.32 en 1.10.33 van \"de Finibus Bonorum et Malorum\" "+
                "(De uitersten van goed en kwaad) door Cicero, geschreven in 45 v.Chr. Dit boek is een "+
                "verhandeling over de theorie der ethiek, erg populair tijdens de renaissance. "+
                "De eerste regel van Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", "+
                "komt uit een zin in sectie 1.10.32.\n"
                );
        constraintViolations = localValidatorFactory
                .validate(comment);
        assertTrue(constraintViolations.stream().filter(m -> m.getMessage().equals("size must be between 2 and 142")).count() > 0);
    }

    @Test
    public void milestoneIsNull() {
        comment.setMilestone(null);
        constraintViolations = localValidatorFactory
                .validate(comment);
        assertTrue(constraintViolations.stream().filter(m -> m.getMessage().equals("may not be null")).count() > 0);
    }
}
