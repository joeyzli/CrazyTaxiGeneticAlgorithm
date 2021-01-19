/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ctaxigeneticalgorithm;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javafx.scene.input.KeyCode;
import javax.imageio.ImageIO;
import java.util.*;
/**
 *
 * @author z32115
 */
public class Player {
    
    public Brain brain;//Neural Network
    public int LaneIn;
    //public int velocity;
    private int lanewidth;//how much this moves left or right.
    public Rectangle hitbox;
    private BufferedImage img;
    public boolean alive;
    public boolean movingLeft = false;
    public boolean movingRight = false;
    public boolean swapping = false;
    private int width;
    public int moveCounter = 0;
    public int fitness;
    
    public Player(int x, int y, int width, int height, int velocity, int lanewidth){
        setup(x, y, width, height, velocity, lanewidth);
        int[] layerDetails = {5,9,2};
        brain = new Brain(3,layerDetails);
        fitness = 0;
        alive = true;
    }
    public Player(int x, int y, int width, int height, int velocity, int lanewidth, Brain b){
        setup(x, y, width, height, velocity, lanewidth);
        brain = b;
        fitness = 0;
        alive = true;
    }
    public void setup(int x, int y, int width, int height, int velocity, int lanewidth){
        try{
            img = ImageIO.read(new File("TempCar.png"));
        }
        catch(IOException e){
            System.out.println("need help");
        }
        hitbox = new Rectangle(x,y,width,height);
        this.width = width;
        //this.velocity = velocity;
        LaneIn = 3;
        this.lanewidth = lanewidth;

    }
    
    public Brain getBrain(){
        return brain.clone();
    }
    
    public boolean gotHit(Rectangle other){
        return other.intersects(hitbox);
    }
    
    public void draw(Graphics g){
        g.drawImage(img, hitbox.x, hitbox.y, null);
    }
    
    public void stop(){
        swapping = false;
        movingLeft = false;
        movingRight = false;
        moveCounter = 0;
    }
    //The main game will have a timer. Note that lane-swapping and brain reading will be disabled when moving to prevent overlays.
    public void moveRight(int ticks){
        hitbox.x += (int)(lanewidth/ticks);
    }
    
    public void moveLeft(int ticks){
        hitbox.x -= (int)(lanewidth/ticks);
    }
    
    public void update(int panelW, Rectangle[] row, int ticks){
        int myX = hitbox.x + width/2;
        int myLane = hitbox.x / lanewidth;
        int rowY = 0;
        float OnLeftEnd = -1000f;//1 or -1
        float OnRightEnd = -1000f;// 1 or -1
        float horiFromLeftFree = 0;//temporary values for these two.
        float horiFromRightFree = 0;
        float vertFromRow;
        
        if(myLane == 0)
            OnLeftEnd = 1f;
        else if(myLane == 4)
            OnRightEnd = 1f;
            
        boolean[] lanes = new boolean[row.length];
        for(int i = 0;i < row.length; i ++){
            if(row[i] != null){
                lanes[i] = true;
                rowY = row[i].y;
            }
            else
                lanes[i] = false;
        }
        
        //Getting horiFromLeftFree
        boolean offscreen = true;
        for(int i = myLane; i >=0; i --){
            //Temporary mechanism w/out death
            if(i >= row.length){
                horiFromLeftFree = -10000;
                break;
            }
            
            if(!lanes[i]){
                horiFromLeftFree = lanewidth * (myLane - i);
                offscreen = false;
                break;
            }
        }
        if(offscreen)
            horiFromLeftFree = -10000;
        
        //Getting horiFromRightFree
        offscreen = true;
        for(int i = myLane; i < row.length; i ++){
            //tempo measure, again
            if(i < 0){
                horiFromRightFree = 10000;
                break;
            }
            
            if(!lanes[i]){
                horiFromRightFree = lanewidth * (i - myLane);
                offscreen = false;
                break;
            }
        }
        if(offscreen)
            horiFromRightFree = 10000;
        
        //Getting vertFromRow
        vertFromRow = hitbox.y - (rowY + 50);
        
        float[] inputs = {OnLeftEnd/*(float)panelW*/, OnRightEnd/*(float)panelW*/, horiFromLeftFree/*(float)panelW*/, horiFromRightFree/*(float)panelW*/, vertFromRow/*(float)panelW*/};
        /* for(int i = 0; i < inputs.length; i ++){
            System.out.print(inputs[i] + " ");
        } */
        //System.out.println();
        //System.out.println(inputs);
        float[] results = brain.output(inputs);
        //System.out.println("Move Left value: " + results[0] + ", Move Right value: " + results[1]);
        if(results[0] > .5f && results[1] <= .5f){
            movingLeft = true;
            swapping = true;
        }
        else if(results[1] > .5f && results[0] <= .5f){
            movingRight = true;
            swapping = true;
        }
    }
}
