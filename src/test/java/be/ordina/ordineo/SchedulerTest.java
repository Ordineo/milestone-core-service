//package be.ordina.ordineo;
//
//import be.ordina.ordineo.batch.DueDateTasklet;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.MockitoAnnotations;
//import org.mockito.Spy;
//import org.mockito.runners.MockitoJUnitRunner;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.SpringApplicationConfiguration;
//import org.springframework.boot.test.WebIntegrationTest;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import static org.mockito.Matchers.anyString;
//import static org.mockito.Mockito.spy;
//import static org.mockito.Mockito.verify;
//
///**
// * Created by PhDa on 9/05/2016.
// */
//@SpringApplicationConfiguration(classes=MilestoneCoreApplication.class)
//@RunWith(SpringJUnit4ClassRunner.class)
//@WebIntegrationTest({"eureka.client.enabled:false"})
//public class SchedulerTest {
//
//    @Autowired
//    private DueDateTasklet dueDateTasklet;
//
//    @Before
//    public void setup() throws Exception {
//    }
//
//    @Test
//    public void testScheduler() throws Exception {
//        DueDateTasklet spytasklet = spy(dueDateTasklet);
//        spytasklet.execute();
//        verify(spytasklet).execute();
//        verify(spytasklet).publishMessage(anyString(),anyString());
//    }
//}
