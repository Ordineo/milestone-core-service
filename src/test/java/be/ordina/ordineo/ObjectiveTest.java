package be.ordina.ordineo;


import be.ordina.ordineo.model.Objective;
import be.ordina.ordineo.model.ObjectiveType;
import be.ordina.ordineo.util.TestUtil;
import org.hibernate.validator.HibernateValidator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import java.util.Set;

import static org.junit.Assert.assertTrue;

@WebIntegrationTest({"server.port:0", "eureka.client.enabled:false"})
public class ObjectiveTest {

    private LocalValidatorFactoryBean localValidatorFactory;
    private Set<ConstraintViolation<Objective>> constraintViolations;
    private Objective objective;

    TestUtil util = new TestUtil();

    @Before
    public void setup() throws Exception {
        localValidatorFactory = new LocalValidatorFactoryBean();
        localValidatorFactory.setProviderClass(HibernateValidator.class);
        localValidatorFactory.afterPropertiesSet();

        util.setAuthorities();

        objective = createObjective();
    }

    private Objective createObjective(){
        Objective objective = new Objective();

        objective.setTitle("Java");
        objective.setDescription("java");
        objective.setObjectiveType(ObjectiveType.BOOK);
        objective.addTag("java");

        return objective;
    }

    @Test
    public void validateObjective() {
        constraintViolations = localValidatorFactory
                .validate(objective);
        assertTrue(constraintViolations.stream().count() == 0);
    }

    @Test
    public void titleIsNull() {
        objective.setTitle(null);
        constraintViolations = localValidatorFactory
                .validate(objective);
        assertTrue(constraintViolations.stream().filter(m -> m.getMessage().equals("may not be null")).count() > 0);
    }

    @Test
    public void ObjectiveTypeIsNull() {
        objective.setObjectiveType(null);
        constraintViolations = localValidatorFactory
                .validate(objective);
        assertTrue(constraintViolations.stream().filter(m -> m.getMessage().equals("may not be null")).count() > 0);
    }

}
