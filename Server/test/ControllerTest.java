import controllers.routes;
import com.avaje.ebean.Ebean;
import com.google.common.collect.ImmutableMap;
import org.junit.*;

import play.libs.Yaml;
import play.mvc.*;
import play.test.*;

import java.util.List;

import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;

public class ControllerTest {
    
	@Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase(), fakeGlobal()));
        Ebean.save((List) Yaml.load("test-data.yml"));
    }
	@Test
    public void AdminControllerTest() {
        Result result = callAction(
                controllers.routes.ref.Application.index(),
                fakeRequest().withSession("cmuser", "0").withSession("domain", "2")
        );
        assertThat(status(result)).isEqualTo(OK);
        assertThat(contentType(result)).isEqualTo("text/html");
        assertThat(charset(result)).isEqualTo("utf-8");
        assertThat(contentAsString(result)).contains("Admin");
    }
}