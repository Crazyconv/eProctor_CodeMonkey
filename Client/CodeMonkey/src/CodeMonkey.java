import java.awt.Frame;
import static javafx.application.Application.launch;
import static javafx.application.Application.launch;
import static javafx.application.Application.launch;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javax.swing.JFrame;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import netscape.javascript.JSObject;
 
public class CodeMonkey extends javafx.application.Application {
    
    JFrame jFrame = new JFrame();
    
    public static void main( String[] args ) {
        launch(args);
    }
 
    @Override
    public void start( final Stage stage ) throws Exception {
        final Scene scene = new Scene(new Browser());
//        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
//            @Override
//            public void handle(KeyEvent t) {
//                KeyCode key = t.getCode();
//                if (key == KeyCode.WINDOWS){
//                    jFrame.dispose();
//                }
//            }
//        });
        
        final JFXPanel fxPanel = new JFXPanel();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                fxPanel.setScene(scene);
            }
        });
        
        jFrame.add(fxPanel);
        jFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
        jFrame.setUndecorated(true);
        //jFrame.setAlwaysOnTop(true);
        jFrame.setVisible(true);
        jFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
}    
    class Browser extends Region {
        final WebView browser = new WebView();
        final WebEngine webEngine = browser.getEngine();
        public Browser() {
            webEngine.getLoadWorker().stateProperty().addListener(
                new ChangeListener<State>() {
                    @Override
                    public void changed(ObservableValue<? extends State> ov,
                        State oldState, State newState) { 
                        if (newState == State.SUCCEEDED) {
                                JSObject win = 
                                    (JSObject) webEngine.executeScript("window");
                               win.setMember("grab", new Grabber());
                        }
                    }
                }
            );
            webEngine.load("http://localhost:9000/");
            getChildren().add(browser);
        }
        @Override 
        protected void layoutChildren() {
            double w = getWidth();
            double h = getHeight();
            layoutInArea(browser,0,0,w,h,0, HPos.CENTER, VPos.CENTER);
        }
    }
    
