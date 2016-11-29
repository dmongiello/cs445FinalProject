/***************************************************************
* file: FPCameraController.java
* authors: D. Mongiello
* * Joel Woods
* Erwin Maulas
* class: CS 445 Computer Graphics
*
* assignment: Final Project Checkpoint 1
* date last modified: 10/31/2016 *
* purpose: To handle the calls to control the camera. 
* Ideas taken from the lecture slides given by T. Diaz  3D Viewing.
* */

package FinalProject;
import java.nio.FloatBuffer;
import java.util.Random;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display; 
import static org.lwjgl.opengl.GL11.*; 
import org.lwjgl.Sys;
/**
 *
 * @author davidmongiello
 */
public class FPCameraController {
    //3d vector to store the camera's position in 
    private Vector3f position = null;
    private Vector3f lPosition = null;
    //the rotation around the Y axis of the camera 
    private float yaw = 0.0f;
    //the rotation around the X axis of the camera 
    private float pitch = 0.0f;
    private Vector3Float me;
    //private float ylist[];
    private Chunk chunk;
    public FPCameraController(float x, float y, float z) {
        //instantiate position Vector3f to the x y z params. 
        position = new Vector3f(x, y-30, z);
        lPosition = new Vector3f(x,y,z);
        lPosition.x = 0f;
        lPosition.y = 15f;
        lPosition.z = 0f;
        chunk = new Chunk(-30,-30,-30);
    }
     /** method: gameLoop()
     * purpose: This method creates an instance of CamraController starting at
     * origin and updates the user view as the user uses input controls to navigate
     * throughout the world. 
     */
    public void gameLoop() 
    {
        FPCameraController camera = new FPCameraController(0, 0, 0);
        float dx = 0.0f;
        float dy = 0.0f;
        // Length of frame
        float dt = 0.0f; 
        float lastTime = 0.0f; // when the last frame was
        long time = 0;
        float mouseSensitivity = 0.09f; 
        float movementSpeed = .35f; 
        //hide the mouse 
        Mouse.setGrabbed(true);
        // keep looping till the display window is closed the ESC key is down 
        while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
        {
            time = Sys.getTime();
            lastTime = time;
            //distance in mouse movement //from the last getDX() call.
            dx = Mouse.getDX();
            //distance in mouse movement //from the last getDY() call.
            dy = Mouse.getDY();
            //controll camera yaw from x movement fromt the mouse
            camera.yaw(dx * mouseSensitivity);
            //controll camera pitch from y movement from the mouse 
            camera.pitch(dy * mouseSensitivity);
            //when passing in the distance to move
            //we times the movementSpeed with dt this is a time scale
            //so if its a slow frame u move more then a fast frame
            //so on a slow computer you move just as fast as on a fast computer 
            if (Keyboard.isKeyDown(Keyboard.KEY_W))//move forward
            {
                camera.walkForward(movementSpeed);   
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_S))//move backwards 
            {
                camera.walkBackwards(movementSpeed); 
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_A))//strafe left 
            {
                camera.strafeLeft(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_D))//strafe right
            {
                camera.strafeRight(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_SPACE))//move up
            {
                camera.moveUp(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
            {
                camera.moveDown(movementSpeed); 
            }
            // Added feature to make the ground shake. 
            if (Keyboard.isKeyDown(Keyboard.KEY_Q))
            {
             quake(camera);
            }

            //set the modelview matrix back to the identity 
            glLoadIdentity();
            //look through the camera before you draw anything 
            camera.lookThrough();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); //you would draw your scene here.
            // render the chunk that we create. 
            chunk.render();
            //draw the buffer to the screen 
            Display.update();
            Display.sync(60);
        }
        Display.destroy();    
    }
    
    //increment the camera's current yaw rotation 
    public void yaw(float amount)
    {
        //increment the yaw by the amount param
        yaw += amount;
    }
    //increment the camera's current yaw rotation 
    public void pitch(float amount)
    {
        //increment the pitch by the amount param
        
            System.out.println( pitch);
        if(pitch-amount <=90)//stops you from turning upside down
        {
            if(pitch-amount >= -90)
                pitch -= amount;
            else
                pitch=-89.9f;
        }
        else
          pitch=89.9f;
    }
     //moves the camera forward relative to its current rotation (yaw) 
    public void walkForward(float distance)
    {
       
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw)); 
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw));
        position.x -= xOffset;
        position.z += zOffset;
        
    }
    //moves the camera backward relative to its current rotation (yaw)
    public void walkBackwards(float distance)
    {
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw)); 
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw));
        position.x += xOffset;position.z -= zOffset;
        
    }
    //strafes the camera left relative to its current rotation (yaw) 
    public void strafeLeft(float distance)
    {
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw-90));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw-90));
        position.x -= xOffset;
        position.z += zOffset;
    }
    //strafes the camera right relative to its current rotation (yaw)
    public void strafeRight(float distance)
    {
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw+90));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw+90));
        position.x -= xOffset;
        position.z += zOffset;
    }
    //moves the camera up relative to its current rotation (yaw) 
    public void moveUp(float distance)
    {
        position.y -= distance; 
    }
    //moves the camera down
    public void moveDown(float distance) 
    {
        position.y += distance;
    }
    
    // This method changes the camera at random using noise to simulate an earthquake. 
    // The idea of using noise was taking from
    // https://www.opengl.org/discussion_boards/showthread.php/154055-Shaking-the-screen
    // The user Spark suggested this.

    public void quake(FPCameraController camera)
    {
      Random rand = new Random();
      // Save the camera postion.
      Vector3f tempVec = new Vector3f(camera.position.x,camera.position.y,camera.position.z);
      // Set the frequency of the quake
      int f = 40;
      // Set the amplitude of the quake.
      int amp = 1; 
      // used to get the noise values. 
      double r = 0;
      // Set the noise 
      SimplexNoise noise = new SimplexNoise(2, 1, rand.nextInt());
      // Get noise for x position. 
      r = Math.abs(noise.getNoise((double) camera.position.x/f, (double) camera.position.y/f, (double) camera.position.z/f));
      camera.position.x += r *amp;
      // get noise for y position. 
      r = Math.abs(noise.getNoise((double) camera.position.x/f, (double) camera.position.y/f + 100, (double) camera.position.z/f));
      camera.position.y += r * amp;
      // Get noise for z position. 
      r = Math.abs(noise.getNoise((double) camera.position.x/f, (double) camera.position.y/f, (double) camera.position.z/f + 100));
      camera.position.z += r *amp;
      glLoadIdentity();
      //look through the camera before you draw anything 
      camera.lookThrough();
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); //you would draw your scene here.
      // render the chunk that we create. 
      chunk.render();
      //draw the buffer to the screen 
      Display.update();
      Display.sync(60);
      // Set the camera position back to where it started from. 
      camera.position = tempVec; 
     
      
    }
    /**
     * translates and rotate the matrix so that it looks through the camera 
     * this does basically what gluLookAt() does
     */
    public void lookThrough()
    {
        //rotate the pitch around the X axis 
        glRotatef(pitch, 1.0f, 0.0f, 0.0f);
        //roatate the yaw around the Y axis 
        glRotatef(yaw, 0.0f, 1.0f, 0.0f);
        //translate to the position vector's location 
        glTranslatef(position.x, position.y, position.z);
        //stops light from moving
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lPosition.x).put(lPosition.y).
                      put(lPosition.z).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }
}
        

