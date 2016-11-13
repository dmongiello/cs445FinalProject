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
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
public class Chunk 
{
    static final int CHUNK_SIZE = 30;
    static final int CUBE_LENGTH = 2;
    private Block[][][] Blocks;
    private int VBOVertexHandle;
    private int VBOColorHandle;
    private int StartX, StartY, StartZ;
    private Random r;
    private SimplexNoise noise;
    public void render()
    {
        glPushMatrix();
        glPushMatrix();
        glBindBuffer(GL_ARRAY_BUFFER,
        VBOVertexHandle);
        glVertexPointer(3, GL_FLOAT, 0, 0L);
        glBindBuffer(GL_ARRAY_BUFFER,
        VBOColorHandle);
        glColorPointer(3,GL_FLOAT, 0, 0L);
        glDrawArrays(GL_QUADS, 0,
        CHUNK_SIZE *CHUNK_SIZE*
        CHUNK_SIZE * 24);
        glPopMatrix();
    }
    public void rebuildMesh(float startX, float startY, float startZ) 
    {
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        FloatBuffer VertexPositionData =
         BufferUtils.createFloatBuffer((CHUNK_SIZE*CHUNK_SIZE*CHUNK_SIZE)*6*12);
        
        FloatBuffer VertexColorData =
         BufferUtils.createFloatBuffer((CHUNK_SIZE*CHUNK_SIZE *CHUNK_SIZE)*6*12);
        int height;
        
        for (float x = 0; x < CHUNK_SIZE; x += 1) 
        {
            int i=(int)(startX+x*(15-startX)/4);
            for (float z = 0; z < CHUNK_SIZE; z += 1) 
            {
                height = (int)(CHUNK_SIZE-4 - (noise.getNoise((int)x/2,1,(int)z/2))/2 * CUBE_LENGTH);
                for(float y = 0; y < height && y < CHUNK_SIZE; y++)
                {
                    VertexPositionData.put(createCube((float)(startX+x*CUBE_LENGTH),
                                                      (float)(startY+y*CUBE_LENGTH+
                                                      (int)(CHUNK_SIZE*.8)),
                                                      (float) (startZ + z *
                                                      CUBE_LENGTH)));
                   
                    VertexColorData.put(createCubeVertexCol(getCubeColor(
                                         Blocks[(int) x][(int) y][(int) z])));
                }
            }
        }
        VertexColorData.flip();
        VertexPositionData.flip();
        glBindBuffer(GL_ARRAY_BUFFER,VBOVertexHandle);
        glBufferData(GL_ARRAY_BUFFER,VertexPositionData,GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER,VBOColorHandle);
        glBufferData(GL_ARRAY_BUFFER,VertexColorData,GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
    private float[] createCubeVertexCol(float[] CubeColorArray) 
    {
        float[] cubeColors = new float[CubeColorArray.length * 4 * 6];
        for (int i = 0; i < cubeColors.length; i++) 
        {
            cubeColors[i] = CubeColorArray[i %
            CubeColorArray.length];
        }
        return cubeColors;
    }
    
    public static float[] createCube(float x, float y,float z) 
    {
        int offset = CUBE_LENGTH / 2;
        return new float[] {
        // TOP QUAD
        x + offset, y + offset, z,
        x - offset, y + offset, z,
        x - offset, y + offset, z - CUBE_LENGTH,
        x + offset, y + offset, z - CUBE_LENGTH,
        // BOTTOM QUAD
        x + offset, y - offset, z - CUBE_LENGTH,
        x - offset, y - offset, z - CUBE_LENGTH,
        x - offset, y - offset, z,
        x + offset, y - offset, z,
        // FRONT QUAD
        x + offset, y + offset, z - CUBE_LENGTH,
        x - offset, y + offset, z - CUBE_LENGTH,
        x - offset, y - offset, z - CUBE_LENGTH,
        x + offset, y - offset, z - CUBE_LENGTH,
        // BACK QUAD
        x + offset, y - offset, z,
        x - offset, y - offset, z,
        x - offset, y + offset, z,
        x + offset, y + offset, z,
        // LEFT QUAD
        x - offset, y + offset, z - CUBE_LENGTH,
        x - offset, y + offset, z,
        x - offset, y - offset, z,
        x - offset, y - offset, z - CUBE_LENGTH,
        // RIGHT QUAD
        x + offset, y + offset, z,
        x + offset, y + offset, z - CUBE_LENGTH,
        x + offset, y - offset, z - CUBE_LENGTH,
        x + offset, y - offset, z };
    }
    private float[] getCubeColor(Block block) 
    {
        switch (block.Type.GetID()) 
        {
            case 0:
            return new float[] { 0, 1, 0 };
            case 1:
            return new float[] { 1, 0.5f, 0 };
            case 2:
            return new float[] {0, 0.0f, 1f};
            case 3:
            return new float[] { .2f, .3f, 0f };
        }
        return new float[] { 1, 1, 1 };
    }
    public Chunk(int startX, int startY, int startZ) 
    {
        noise = new SimplexNoise(2,2,1);
        r = new Random();
        Blocks = new
        Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        for (int x = 0; x < CHUNK_SIZE; x++) 
        {
            for (int y = 0; y < CHUNK_SIZE; y++) 
            {
                for (int z = 0; z < CHUNK_SIZE; z++) 
                {
                    if(r.nextFloat()>0.7f)
                    {
                        Blocks[x][y][z] = new
                        Block(Block.BlockType.BlockType_Grass);
                    }
                    else if(r.nextFloat()>0.4f)
                    {
                        Blocks[x][y][z] = new
                        Block(Block.BlockType.BlockType_Dirt);
                    }
                    else if(r.nextFloat()>0.2f)
                    {
                        Blocks[x][y][z] = new
                        Block(Block.BlockType.BlockType_Water);
                    }
                    else
                    {
                        Blocks[x][y][z] = new
                        Block(Block.BlockType.BlockType_Dirt);
                    }
                }
            }
        }
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        StartX = startX;
        StartY = startY;
        StartZ = startZ;
        rebuildMesh(startX, startY, startZ);
    }
}