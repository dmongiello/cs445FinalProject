/***************************************************************
* file: FinalProject.java
* authors: D. Mongiello
* * Joel Woods
* Erwin Maulas
* class: CS 445 Computer Graphics
*
* assignment: Final Project Checkpoint 1
* date last modified: 10/31/2016 *
* purpose: This program creates a cube in 3d spaces and allows the user 
* to move around in 3d space while viewing the cube. 
* Ideas taken from the lecture slides given by T. Diaz  3D Viewing.
* */
package FinalProject;

/**
 *
 * @author David R. Mongiello
 *         
 */
import org.lwjgl.input.Keyboard; 
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode; 
import static org.lwjgl.opengl.GL11.*; 
import org.lwjgl.util.glu.GLU;

public class FinalProject { 
private FPCameraController fp = new FPCameraController(0f,0f,0f);
private DisplayMode displayMode;
// method: start()
// purpose: This method creates an instance of the class allowing for use of
// object oriented programming. It removes the static of main. And is a virtual 
// instance of main
    public void start()
    {
    try {
        createWindow(); initGL();
        fp.gameLoop();//render(); 
    } catch (Exception e) {
        e.printStackTrace(); }
    }
    
// method: createWindow
// purpose: This method creates a window with openGL and setup the display area. 
    private void createWindow() throws Exception
    {
       Display.setFullscreen(false);
        DisplayMode d[] = Display.getAvailableDisplayModes();
        for (int i = 0; i < d.length; i++) 
        { 
            if (d[i].getWidth() == 640 && d[i].getHeight() == 480
                && d[i].getBitsPerPixel() == 32) 
            { 
                displayMode = d[i];
                break;
            }
        }
        Display.setDisplayMode(displayMode); 
        Display.setTitle("Final Project"); 
        Display.create();
    }

// method: initGL()
// purpose: This method initializes the GL screen. 
    private void initGL() {
        // Background color black
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glMatrixMode(GL_PROJECTION); 
        glLoadIdentity();
        // Clipping Area 
        GLU.gluPerspective(100.0f, (float)displayMode.getWidth()/(float) displayMode.getHeight(), 0.1f, 300.0f);
        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST); 
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        FinalProject p1 = new FinalProject();
        p1.start(); 
        
    }
    
}
