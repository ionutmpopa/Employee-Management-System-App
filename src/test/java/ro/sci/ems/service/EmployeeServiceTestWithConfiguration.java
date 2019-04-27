//package ro.sci.ems.service;
//
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import ro.sci.ems.ApplicationConfiguration;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = {ApplicationConfiguration.class})
//public class EmployeeServiceTestWithConfiguration extends BaseEmployeeServiceTest {
//
//	@Configuration
//	@ComponentScan("ro.sci")
//	public static class SpringConfig {
//
//	}
//	@Autowired
//	private EmployeeService service;
//
//	@Override
//	protected EmployeeService getEmployeeService() {
//		return service;
//	}
//
//
//
//}
