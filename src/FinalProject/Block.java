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

public class Block 
{
    private boolean IsActive;
    public BlockType Type;
    private float x,y,z;
    public enum BlockType
    {
        BlockType_Grass(0),
        BlockType_Sand(1),
        BlockType_Water(2),
        BlockType_Dirt(3),
        BlockType_Stone(4),
        BlockType_Bedrock(5);
    
        private int BlockID;
        BlockType(int i) {
            BlockID=i;
        }
        public int GetID(){
            return BlockID;
        }
        public void SetID(int i){
            BlockID = i;
        }
    }
    public Block(BlockType type)
    {
        Type= type;
    }
    public void setCoords(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public boolean IsActive() 
    {
        return IsActive;
    }
}