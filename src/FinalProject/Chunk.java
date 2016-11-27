/***************************************************************
* file: Chunk.java
* authors: D. Mongiello
* * Joel Woods
* Erwin Maulas
* class: CS 445 Computer Graphics
*
* assignment: Final Project Checkpoint 1
* date last modified: 10/31/2016 *
* purpose: to create chunks and render them. 
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

public class Chunk {

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

  public void render() {
    glPushMatrix();

    glBindBuffer(GL_ARRAY_BUFFER,
            VBOVertexHandle);
    glVertexPointer(3, GL_FLOAT, 0, 0L);
    glBindBuffer(GL_ARRAY_BUFFER,
            VBOColorHandle);
    glColorPointer(3, GL_FLOAT, 0, 0L);
    glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
    glBindTexture(GL_TEXTURE_2D, 1);

    glTexCoordPointer(2, GL_FLOAT, 0, 0L);

    glDrawArrays(GL_QUADS, 0, CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 24);
    glPopMatrix();
  }

  //sets block positions and textures
  public void rebuildMesh(float startX, float startY, float startZ) {
    VBOColorHandle = glGenBuffers();
    VBOVertexHandle = glGenBuffers();
    VBOTextureHandle = glGenBuffers();
    FloatBuffer VertexPositionData
            = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);

    FloatBuffer VertexColorData
            = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);

    FloatBuffer VertexTextureData
            = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);

    int height;
    int r;
    for (float x = 0; x < CHUNK_SIZE; x += 1) {
      for (float z = 0; z < CHUNK_SIZE; z += 1) {
        //randomize height here
        r = (int) ((noise.getNoise((double) x / 15, (double) z / 15)) * CUBE_LENGTH);
        height = CHUNK_SIZE - r - 1;
        r++;

        for (float y = 0; y < CHUNK_SIZE; y++) {
          if (y < height && Blocks[(int) x][(int) y][(int) z].IsActive()) {//if below randomized height and active make block
            VertexPositionData.put(createCube((float) (startX + x * CUBE_LENGTH),
                    (float) (startY + y * CUBE_LENGTH),
                    (float) (startZ + z
                    * CUBE_LENGTH)));

            if (Blocks[(int) x][(int) y][(int) z].Type.GetID() > 2)//if not sand or water make grass
            {
              if (y >= (height - 2))//below grass make dirt
              {
                Blocks[(int) x][(int) y][(int) z].Type = Block.BlockType.BlockType_Dirt;
              }
              if (y == (height - 1)) {
                Blocks[(int) x][(int) y][(int) z].Type = Block.BlockType.BlockType_Grass;
              }
            }
            if (y <= r)//bottom level shaped bedrock
            {
              Blocks[(int) x][(int) y][(int) z].Type = Block.BlockType.BlockType_Bedrock;
            }
            if (y < 2 && z < 2 && x == 0) {
              Blocks[(int) x][(int) y][(int) z].Type = Block.BlockType.BlockType_Grass;
            }

            VertexColorData.put(createCubeVertexCol(getCubeColor(
                    Blocks[(int) x][(int) y][(int) z])));
            VertexTextureData.put(createTexCube((float) 0, (float) 0,
                    Blocks[(int) (x)][(int) (y)][(int) (z)]));
          } else//if above the height for that x,z set inactive
          {
            Blocks[(int) x][(int) y][(int) z].setActive(false);
          }
        }
      }
    }
    VertexColorData.flip();
    VertexPositionData.flip();
    VertexTextureData.flip();

    glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
    glBufferData(GL_ARRAY_BUFFER, VertexPositionData, GL_STATIC_DRAW);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
    glBufferData(GL_ARRAY_BUFFER, VertexColorData, GL_STATIC_DRAW);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
    glBufferData(GL_ARRAY_BUFFER, VertexTextureData, GL_STATIC_DRAW);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
  }

  private float[] createCubeVertexCol(float[] CubeColorArray) {
    float[] cubeColors = new float[CubeColorArray.length * 4 * 6];
    for (int i = 0; i < cubeColors.length; i++) {
      cubeColors[i] = CubeColorArray[i
              % CubeColorArray.length];
    }
    return cubeColors;
  }

  public static float[] createCube(float x, float y, float z) {
    int offset = CUBE_LENGTH / 2;
    return new float[]{
      // Top 
      x + offset, y + offset, z,
      x - offset, y + offset, z,
      x - offset, y + offset, z - CUBE_LENGTH,
      x + offset, y + offset, z - CUBE_LENGTH,
      // bottom 
      x + offset, y - offset, z - CUBE_LENGTH,
      x - offset, y - offset, z - CUBE_LENGTH,
      x - offset, y - offset, z,
      x + offset, y - offset, z,
      // front 
      x + offset, y + offset, z - CUBE_LENGTH,
      x - offset, y + offset, z - CUBE_LENGTH,
      x - offset, y - offset, z - CUBE_LENGTH,
      x + offset, y - offset, z - CUBE_LENGTH,
      // back 
      x + offset, y - offset, z,
      x - offset, y - offset, z,
      x - offset, y + offset, z,
      x + offset, y + offset, z,
      // LEFT 
      x - offset, y + offset, z - CUBE_LENGTH,
      x - offset, y + offset, z,
      x - offset, y - offset, z,
      x - offset, y - offset, z - CUBE_LENGTH,
      // RIGHT 
      x + offset, y + offset, z,
      x + offset, y + offset, z - CUBE_LENGTH,
      x + offset, y - offset, z - CUBE_LENGTH,
      x + offset, y - offset, z};
  }

  private float[] getCubeColor(Block block) {
    switch (block.GetID()) {
    case 1:
      return new float[] { 0, 1, 0 };
      case 2:
      return new float[] { 1, 0.5f, 0 };
      case 3:
      return new float[] { 0, 0f, 1f };
      }
    return new float[] { 1, 1, 1 };
  }

  public static float[] createTexCube(float x, float y, Block block) {
    float offset = (1024f / 16) / 1024f;
    switch (block.Type.GetID()) {
      case 0://grass
        return new float[]{
          // top
          x + offset * 3, y + offset * 10,
          x + offset * 2, y + offset * 10,
          x + offset * 2, y + offset * 9,
          x + offset * 3, y + offset * 9,
          // bottom!
          x + offset * 3, y + offset * 1,
          x + offset * 2, y + offset * 1,
          x + offset * 2, y + offset * 0,
          x + offset * 3, y + offset * 0,
          // front 
          x + offset * 3, y + offset * 0,
          x + offset * 4, y + offset * 0,
          x + offset * 4, y + offset * 1,
          x + offset * 3, y + offset * 1,
          // back 
          x + offset * 4, y + offset * 1,
          x + offset * 3, y + offset * 1,
          x + offset * 3, y + offset * 0,
          x + offset * 4, y + offset * 0,
          // LEFT 
          x + offset * 3, y + offset * 0,
          x + offset * 4, y + offset * 0,
          x + offset * 4, y + offset * 1,
          x + offset * 3, y + offset * 1,
          // RIGHT 
          x + offset * 3, y + offset * 0,
          x + offset * 4, y + offset * 0,
          x + offset * 4, y + offset * 1,
          x + offset * 3, y + offset * 1
        };
      case 1://sand
        return new float[]{
          // bottom 
          x + offset * 3, y + offset * 2,
          x + offset * 2, y + offset * 2,
          x + offset * 2, y + offset * 1,
          x + offset * 3, y + offset * 1,
          // Top!
          x + offset * 3, y + offset * 2,
          x + offset * 2, y + offset * 2,
          x + offset * 2, y + offset * 1,
          x + offset * 3, y + offset * 1,
          // front 
          x + offset * 3, y + offset * 1,
          x + offset * 2, y + offset * 1,
          x + offset * 2, y + offset * 2,
          x + offset * 3, y + offset * 2,
          // back 
          x + offset * 2, y + offset * 2,
          x + offset * 3, y + offset * 2,
          x + offset * 3, y + offset * 1,
          x + offset * 2, y + offset * 1,
          // LEFT 
          x + offset * 3, y + offset * 1,
          x + offset * 2, y + offset * 1,
          x + offset * 2, y + offset * 2,
          x + offset * 3, y + offset * 2,
          // RIGHT 
          x + offset * 3, y + offset * 1,
          x + offset * 2, y + offset * 1,
          x + offset * 2, y + offset * 2,
          x + offset * 3, y + offset * 2
        };
      case 2://water
        return new float[]{
          // bottom 
          x + offset * 16, y + offset * 13,
          x + offset * 15, y + offset * 13,
          x + offset * 15, y + offset * 12,
          x + offset * 16, y + offset * 12,
          // Top
          x + offset * 16, y + offset * 13,
          x + offset * 15, y + offset * 13,
          x + offset * 15, y + offset * 12,
          x + offset * 16, y + offset * 12,
          // front 
          x + offset * 16, y + offset * 12,
          x + offset * 15, y + offset * 12,
          x + offset * 15, y + offset * 13,
          x + offset * 16, y + offset * 13,
          // back 
          x + offset * 15, y + offset * 13,
          x + offset * 16, y + offset * 13,
          x + offset * 16, y + offset * 12,
          x + offset * 15, y + offset * 12,
          // LEFT 
          x + offset * 16, y + offset * 12,
          x + offset * 15, y + offset * 12,
          x + offset * 15, y + offset * 13,
          x + offset * 16, y + offset * 13,
          // RIGHT 
          x + offset * 16, y + offset * 12,
          x + offset * 15, y + offset * 12,
          x + offset * 15, y + offset * 13,
          x + offset * 16, y + offset * 13
        };
      case 3://dirt
        return new float[]{
          // bottom 
          x + offset * 3, y + offset * 1,
          x + offset * 2, y + offset * 1,
          x + offset * 2, y + offset * 0,
          x + offset * 3, y + offset * 0,
          // Top!
          x + offset * 3, y + offset * 1,
          x + offset * 2, y + offset * 1,
          x + offset * 2, y + offset * 0,
          x + offset * 3, y + offset * 0,
          // front 
          x + offset * 3, y + offset * 0,
          x + offset * 2, y + offset * 0,
          x + offset * 2, y + offset * 1,
          x + offset * 3, y + offset * 1,
          // back 
          x + offset * 2, y + offset * 1,
          x + offset * 3, y + offset * 1,
          x + offset * 3, y + offset * 0,
          x + offset * 2, y + offset * 0,
          // LEFT 
          x + offset * 3, y + offset * 0,
          x + offset * 2, y + offset * 0,
          x + offset * 2, y + offset * 1,
          x + offset * 3, y + offset * 1,
          // RIGHT 
          x + offset * 3, y + offset * 0,
          x + offset * 2, y + offset * 0,
          x + offset * 2, y + offset * 1,
          x + offset * 3, y + offset * 1
        };
      case 4://stone
        return new float[]{
          // top
          x + offset * 2, y + offset * 1,
          x + offset * 1, y + offset * 1,
          x + offset * 1, y + offset * 0,
          x + offset * 2, y + offset * 0,
          // borrom!
          x + offset * 2, y + offset * 1,
          x + offset * 1, y + offset * 1,
          x + offset * 1, y + offset * 0,
          x + offset * 2, y + offset * 0,
          // front 
          x + offset * 1, y + offset * 0,
          x + offset * 2, y + offset * 0,
          x + offset * 2, y + offset * 1,
          x + offset * 1, y + offset * 1,
          // back 
          x + offset * 2, y + offset * 1,
          x + offset * 1, y + offset * 1,
          x + offset * 1, y + offset * 0,
          x + offset * 2, y + offset * 0,
          // LEFT 
          x + offset * 1, y + offset * 0,
          x + offset * 2, y + offset * 0,
          x + offset * 2, y + offset * 1,
          x + offset * 1, y + offset * 1,
          // RIGHT 
          x + offset * 1, y + offset * 0,
          x + offset * 2, y + offset * 0,
          x + offset * 2, y + offset * 1,
          x + offset * 1, y + offset * 1
        };
      default://bedrock
        return new float[]{
          // bottom 
          x + offset * 1, y + offset * 10,
          x + offset * 2, y + offset * 10,
          x + offset * 1, y + offset * 9,
          x + offset * 2, y + offset * 9,
          // Top!
          x + offset * 2, y + offset * 2,
          x + offset * 1, y + offset * 2,
          x + offset * 1, y + offset * 1,
          x + offset * 2, y + offset * 1,
          // front 
          x + offset * 2, y + offset * 1,
          x + offset * 1, y + offset * 1,
          x + offset * 1, y + offset * 2,
          x + offset * 2, y + offset * 2,
          // back 
          x + offset * 1, y + offset * 2,
          x + offset * 2, y + offset * 2,
          x + offset * 2, y + offset * 1,
          x + offset * 1, y + offset * 1,
          // LEFT 
          x + offset * 2, y + offset * 1,
          x + offset * 1, y + offset * 1,
          x + offset * 1, y + offset * 2,
          x + offset * 2, y + offset * 2,
          // RIGHT 
          x + offset * 2, y + offset * 1,
          x + offset * 1, y + offset * 1,
          x + offset * 1, y + offset * 2,
          x + offset * 2, y + offset * 2
        };
    }

  }

  public Chunk(int startX, int startY, int startZ) {
    Random rand = new Random();
    noise = new SimplexNoise(2, 1, rand.nextInt());
    double r = 0;
    Blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
    for (int x = 0; x < CHUNK_SIZE; x++) {
      for (int y = 0; y < CHUNK_SIZE; y++) {
        for (int z = 0; z < CHUNK_SIZE; z++) {

          r = Math.abs(noise.getNoise((double) x / 30, (double) y / 30, (double) z / 30));
          if (y > CHUNK_SIZE - 4) {//loads sand  and water in upper levels
            if (r > 0.7f) {
              Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Water);
            } else if (r > 0.5f) {
              Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Sand);
            } else {
              Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Dirt);
            }
          } else if (r > .3f) {
            Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Dirt);
          } else {
            Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Stone);
          }
        }
      }
    }
    try {//load block textures
      texture = TextureLoader.getTexture("PNG",
              ResourceLoader.getResourceAsStream("Terrain.png"));
    } catch (Exception e) {
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
