
import javax.swing.JFrame;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Administrator
 */
public class App {
    JFrame jFrame;
    public App(JFrame jFrame){
        this.jFrame = jFrame;
    }
    public void exitApp(){
        jFrame.dispose();
    }
    public void lockApp(){
        jFrame.setAlwaysOnTop(true);
    }
    public void unlockApp(){
        jFrame.setAlwaysOnTop(false);
    }
}
