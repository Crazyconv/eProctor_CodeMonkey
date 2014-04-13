import models.Setting;
import play.Application;
import play.GlobalSettings;
import utils.Global;

public class Configuration extends GlobalSettings {
    public void onStart(Application app) {
        Global.STUDENT = Integer.parseInt(Setting.get("student_domain"));
        Global.INVIGILATOR = Integer.parseInt(Setting.get("invigilator_domain"));
        Global.ADMIN = Integer.parseInt(Setting.get("admin_domain"));
        Global.ADMIN_ACCOUNT = Setting.get("admin_account");
        Global.ADMIN_PASSWORD = Setting.get("admin_password");

        Global.NOTSIGNEDIN = Integer.parseInt(Setting.get("not_signed_in"));
        Global.SIGNEDIN = Integer.parseInt(Setting.get("signed_in"));
        Global.VERIFIED = Integer.parseInt(Setting.get("verified"));
        Global.EXPELLED = Integer.parseInt(Setting.get("expelled"));
        Global.FINISHED = Integer.parseInt(Setting.get("finished"));

        Global.TIME_ADVANCED = Integer.parseInt(Setting.get("time_advanced"));
    }
}
