package be.ordina.ordineo;

import be.ordina.ordineo.model.Milestone;
import org.hibernate.validator.HibernateValidator;
import org.junit.Before;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import java.util.Set;

/**
 * Created by PhDa on 14/04/2016.
 */
public class MilestoneTest {


    private LocalValidatorFactoryBean localValidatorFactory;
    private Set<ConstraintViolation<Milestone>> constraintViolations;
    private Milestone milestone;

    @Before
    public void setup() {
        localValidatorFactory = new LocalValidatorFactoryBean();
        localValidatorFactory.setProviderClass(HibernateValidator.class);
        localValidatorFactory.afterPropertiesSet();
        //employee = createEmployee();
    }
}
