package ro.sci.ems.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ro.sci.ems.ApplicationConfiguration;
import ro.sci.ems.dao.EmployeeDAO;
import ro.sci.ems.domain.Employee;
import ro.sci.ems.domain.Gender;
import ro.sci.ems.exception.ValidationException;

import java.util.Arrays;
import java.util.Date;

import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationConfiguration.class})
public class EmployeeServiceTestWithConfigurationAndMocking extends BaseEmployeeServiceTest {

	@InjectMocks
	private EmployeeService service;

	@Mock
	private EmployeeDAO employeeDAO;

	@Override
	protected EmployeeService getEmployeeService() {
		return service;
	}

	@Before
	public void setup() {

	}


	@Test
	public void test_add_valid_employee() throws ValidationException {

		Employee em = new Employee();
		em.setBirthDate(new Date(70, 1, 1));
		em.setEmploymentDate(new Date());
		em.setGender(Gender.FEMALE);
		em.setJobTitle("JUNIOR");
		em.setFirstName("Babanan");
		em.setLastName("Gogu");
		em.setId(1l);

		when(getEmployeeService().listAll()).thenReturn(Arrays.asList(em));

		getEmployeeService().save(em);
		Assert.assertEquals(1, getEmployeeService().listAll().size());
		Employee fromDB = getEmployeeService().listAll().iterator().next();
		Assert.assertNotNull(fromDB);
		Assert.assertTrue(fromDB.getId() > 0);
		em.setId(fromDB.getId());
		Assert.assertEquals(em, fromDB);
	}

	@Test
	public void test_search_by_name_multiple_results() throws ValidationException {
		Employee em = new Employee();
		em.setBirthDate(new Date(70, 1, 1));
		em.setEmploymentDate(new Date());
		em.setGender(Gender.FEMALE);
		em.setJobTitle("JUNIOR");
		em.setFirstName("Mariana");
		em.setLastName("Gogu");
		getEmployeeService().save(em);

		em = new Employee();
		em.setBirthDate(new Date(70, 1, 1));
		em.setEmploymentDate(new Date());
		em.setGender(Gender.FEMALE);
		em.setJobTitle("JUNIOR");
		em.setFirstName("Cucu");
		em.setLastName("Gogu");
		getEmployeeService().save(em);
		Assert.assertEquals(2, getEmployeeService().search("Gogu").size());

	}

}
