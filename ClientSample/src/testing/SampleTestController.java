/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testing;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 *
 * @author Administrator
 */
public class SampleTestController implements Initializable {
    
    @FXML
    private Label label;
    
    @FXML
    private WebView webView;
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        label.setText("Hello World!");
        WebEngine webEngine = webView.getEngine();
        webEngine.load("http://172.22.79.156:9000");
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }  
}
