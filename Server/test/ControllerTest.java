import controllers.routes;
import org.junit.Test;
import play.mvc.Result;
import views.html.*;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

// public class ControllerTest {
//     @Test
//     public void AdminControllerTest() {
//         Result result = callAction(
//                 routes.ref.AdminController.assignInvigilator()
//         );
//         assertThat(status(result)).isEqualTo(OK);
//         assertThat(contentType(result)).isEqualTo("text/html");
//         assertThat(charset(result)).isEqualTo("utf-8");
//         assertThat(contentAsString(result)).contains("Access");
//     }
// }