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
* Credit to Mojang(https://mojang.com/) For texture Terrain.png.
* */

package FinalProject;

import java.nio.FloatBuffer;
import java.util.Random;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Chunk 
{
    static final int CHUNK_SIZE = 30;
    static final int CUBE_LENGTH = 2;
    private Block[][][] Blocks;
    private int VBOVertexHandle;
    private int VBOColorHandle;
    private int StartX, StartY, StartZ;
    private SimplexNoise noise;
    //textures
    private int VBOTextureHandle;
    private Texture texture;
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
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBindTexture(GL_TEXTURE_2D, 1);
        glTexCoordPointer(2,GL_FLOAT,0,0L);
        glDrawArrays(GL_QUADS, 0,
        CHUNK_SIZE *CHUNK_SIZE*
        CHUNK_SIZE * 24);
        glPopMatrix();
    }
    
    //sets block positions and textures
    public void rebuildMesh(float startX, float startY, float startZ) 
    {
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers(); 
        FloatBuffer VertexPositionData =
        BufferUtils.createFloatBuffer((CHUNK_SIZE*CHUNK_SIZE*CHUNK_SIZE)*6*12);
        
        FloatBuffer VertexColorData =
        BufferUtils.createFloatBuffer((CHUNK_SIZE*CHUNK_SIZE *CHUNK_SIZE)*6*12);
        
        FloatBuffer VertexTextureData =
        BufferUtils.createFloatBuffer((CHUNK_SIZE*CHUNK_SIZE *CHUNK_SIZE)*6*12);
        
        int height;
        int r;
        for (float x = 0; x < CHUNK_SIZE; x += 1) 
        {
            for (float z = 0; z < CHUNK_SIZE; z += 1) 
            {
                //randomize height here
                r=(int)((noise.getNoise((double)x/15,(double)z/15))*CUBE_LENGTH);
                height =CHUNK_SIZE-5-r;
                r++;
                for(float y = 0; y < height && y < CHUNK_SIZE; y++)
                {
                    VertexPositionData.put(createCube((float)(startX+x*CUBE_LENGTH),
                                                      (float)(startY+y*CUBE_LENGTH+
                                                      (int)(CHUNK_SIZE*.8)),
                                                      (float) (startZ + z *
                                                      CUBE_LENGTH)));
                    if(y>=(height-2))
                        Blocks[(int)x][(int)y][(int)z].Type = Block.BlockType.BlockType_Dirt;
                    if(y==(height-1))
                        Blocks[(int)x][(int)y][(int)z].Type = Block.BlockType.BlockType_Grass;
                    if(y<=r)
                        Blocks[(int)x][(int)y][(int)z].Type = Block.BlockType.BlockType_Bedrock;
                    VertexColorData.put(createCubeVertexCol(getCubeColor(
                                         Blocks[(int)x][(int)y][(int)z])));
                    VertexTextureData.put(createTexCube((float)0,(float)0,
                                         Blocks[(int)(x)][(int)(y)][(int)(z)]));
                }
            }
        }
        VertexColorData.flip();
        VertexPositionData.flip();
        VertexTextureData.flip();
        
        glBindBuffer(GL_ARRAY_BUFFER,VBOVertexHandle);
        glBufferData(GL_ARRAY_BUFFER,VertexPositionData,GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER,VBOColorHandle);
        glBufferData(GL_ARRAY_BUFFER,VertexColorData,GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexTextureData,GL_STATIC_DRAW);
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
        return new float[] { .5f, .5f, .5f };
    }
    public static float[] createTexCube(float x, float y, Block block) 
    {
        float offset = (1024f/16)/1024f;
        switch (block.Type.GetID()) 
        {
            case 0:
                return new float[] 
                {
                    // topQUAD(DOWN=+Y)
                    x + offset*3, y + offset*10,
                    x + offset*2, y + offset*10,
                    x + offset*2, y + offset*9,
                    x + offset*3, y + offset*9,
                    // borrom!
                    x + offset*3, y + offset*1,
                    x + offset*2, y + offset*1,
                    x + offset*2, y + offset*0,
                    x + offset*3, y + offset*0,
                    // FRONT QUAD
                    x + offset*3, y + offset*0,
                    x + offset*4, y + offset*0,
                    x + offset*4, y + offset*1,
                    x + offset*3, y + offset*1,
                    // BACK QUAD
                    x + offset*4, y + offset*1,
                    x + offset*3, y + offset*1,
                    x + offset*3, y + offset*0,
                    x + offset*4, y + offset*0,
                    // LEFT QUAD
                    x + offset*3, y + offset*0,
                    x + offset*4, y + offset*0,
                    x + offset*4, y + offset*1,
                    x + offset*3, y + offset*1,
                    // RIGHT QUAD
                    x + offset*3, y + offset*0,
                    x + offset*4, y + offset*0,
                    x + offset*4, y + offset*1,
                    x + offset*3, y + offset*1
                };
            case 1:
            return new float[] 
            {
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*3, y + offset*2,
                x + offset*2, y + offset*2,
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                // TOP!
                x + offset*3, y + offset*2,
                x + offset*2, y + offset*2,
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                // FRONT QUAD
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*2,
                x + offset*3, y + offset*2,
                // BACK QUAD
                x + offset*2, y + offset*2,
                x + offset*3, y + offset*2,
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                // LEFT QUAD
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*2,
                x + offset*3, y + offset*2,
                // RIGHT QUAD
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*2,
                x + offset*3, y + offset*2
            };
            case 2:
            return new float[] 
            {
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*15, y + offset*1,
                x + offset*14, y + offset*1,
                x + offset*14, y + offset*0,
                x + offset*15, y + offset*0,
                // TOP!
                x + offset*15, y + offset*1,
                x + offset*14, y + offset*1,
                x + offset*14, y + offset*0,
                x + offset*15, y + offset*0,
                // FRONT QUAD
                x + offset*15, y + offset*0,
                x + offset*14, y + offset*0,
                x + offset*14, y + offset*1,
                x + offset*15, y + offset*1,
                // BACK QUAD
                x + offset*14, y + offset*1,
                x + offset*15, y + offset*1,
                x + offset*15, y + offset*0,
                x + offset*14, y + offset*0,
                // LEFT QUAD
                x + offset*15, y + offset*0,
                x + offset*14, y + offset*0,
                x + offset*14, y + offset*1,
                x + offset*15, y + offset*1,
                // RIGHT QUAD
                x + offset*15, y + offset*0,
                x + offset*14, y + offset*0,
                x + offset*14, y + offset*1,
                x + offset*15, y + offset*1
            };
            case 3:
            return new float[] 
            {
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                // TOP!
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                // FRONT QUAD
                x + offset*3, y + offset*0,
                x + offset*2, y + offset*0,
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                // BACK QUAD
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                x + offset*3, y + offset*0,
                x + offset*2, y + offset*0,
                // LEFT QUAD
                x + offset*3, y + offset*0,
                x + offset*2, y + offset*0,
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                // RIGHT QUAD
                x + offset*3, y + offset*0,
                x + offset*2, y + offset*0,
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1
            };
            case 4:
            return new float[] 
            {
                    // topQUAD(DOWN=+Y)
                    x + offset*2, y + offset*1,
                    x + offset*1, y + offset*1,
                    x + offset*1, y + offset*0,
                    x + offset*2, y + offset*0,
                    // borrom!
                    x + offset*2, y + offset*1,
                    x + offset*1, y + offset*1,
                    x + offset*1, y + offset*0,
                    x + offset*2, y + offset*0,
                    // FRONT QUAD
                    x + offset*1, y + offset*0,
                    x + offset*2, y + offset*0,
                    x + offset*2, y + offset*1,
                    x + offset*1, y + offset*1,
                    // BACK QUAD
                    x + offset*2, y + offset*1,
                    x + offset*1, y + offset*1,
                    x + offset*1, y + offset*0,
                    x + offset*2, y + offset*0,
                    // LEFT QUAD
                    x + offset*1, y + offset*0,
                    x + offset*2, y + offset*0,
                    x + offset*2, y + offset*1,
                    x + offset*1, y + offset*1,
                    // RIGHT QUAD
                    x + offset*1, y + offset*0,
                    x + offset*2, y + offset*0,
                    x + offset*2, y + offset*1,
                    x + offset*1, y + offset*1
                };
            default:
            return new float[] 
            {
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*1, y + offset*10,
                x + offset*2, y + offset*10,
                x + offset*1, y + offset*9,
                x + offset*2, y + offset*9,
                // TOP!
                x + offset*2, y + offset*2,
                x + offset*1, y + offset*2,
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1,
                // FRONT QUAD
                x + offset*2, y + offset*1,
                x + offset*1, y + offset*1,
                x + offset*1, y + offset*2,
                x + offset*2, y + offset*2,
                // BACK QUAD
                x + offset*1, y + offset*2,
                x + offset*2, y + offset*2,
                x + offset*2, y + offset*1,
                x + offset*1, y + offset*1,
                // LEFT QUAD
                x + offset*2, y + offset*1,
                x + offset*1, y + offset*1,
                x + offset*1, y + offset*2,
                x + offset*2, y + offset*2,
                // RIGHT QUAD
                x + offset*2, y + offset*1,
                x + offset*1, y + offset*1,
                x + offset*1, y + offset*2,
                x + offset*2, y + offset*2
            };
        }
        
    }
    public Chunk(int startX, int startY, int startZ) 
    {
        noise = new SimplexNoise(2,1,1);
        double r =0;
        Blocks = new
        Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        for (int x = 0; x < CHUNK_SIZE; x++) 
        {
            for (int y = 0; y < CHUNK_SIZE; y++) 
            {
                for (int z = 0; z < CHUNK_SIZE; z++) 
                {
                    r = Math.abs(noise.getNoise((double)x/30,(double)y/30,(double)z/30));
                    if(r>0.6f)
                    {
                        Blocks[x][y][z] = new
                        Block(Block.BlockType.BlockType_Stone);
                    }
                    else if(r>0.3f)
                    {
                        Blocks[x][y][z] = new
                        Block(Block.BlockType.BlockType_Sand);
                    }
                    else if(r>.2f)
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
        try{
            texture = TextureLoader.getTexture("PNG",
                            ResourceLoader.getResourceAsStream("Terrain.png"));
        }
        catch(Exception e)
        {
            System.out.print("Texture file not found");
        }
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        StartX = startX;
        StartY = startY;
        StartZ = startZ;
  
        rebuildMesh(startX, startY, startZ);
    }
}