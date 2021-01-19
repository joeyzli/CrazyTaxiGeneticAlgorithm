/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ctaxigeneticalgorithm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.Timer;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author z32115
 */ 


//=================================================[CRAZY TAXI AI LEARNS TO CRAZILY PLAY A GAME OF CRAZY TAXI]====================================================//

/*
This project is an attempt to implement a form of machine learning known as the genetic algorithm into the crazy taxi game.
Essentially, it creates brains for players, which contain layers of neurons. The brain structure overall takes inputs of what players can act off of
and uses them to calculate whether or not to take action. There are many players per generation, and the most fit ones(the ones that live the longest)
are selected to reproduce and crossover their genes(neurons in this case) in the production of their children, along with some mutations(re-initialization)
of one neuron per child. The process is repeated with each new generation to emulate natural selection, until eventually(and hopefully) the players converge
to ones that should theoretically never die.

Also, my implementation is essentially completely from scratch and kind of terrible.
Enjoy watching cars die over and over.
*/

//================================================================================================================================================================//


public class TaxiGame extends JFrame{
    int HEIGHT = 800;
    int pubWIDTH = 300;
    int score;
    int velocity = 45;
    int lanewidth = 60;
    boolean deleteFirstRow = false;
    boolean hitThat = false;
    BufferedImage enemyIMG;
    
    Population newgen;
    //Player[] players;
    int numplayers = 512;
    int genNo = 1;
    int maxFit = 0;
    
    Timer timer;
    //Player p;
    ArrayList<Rectangle[]> enemyRows = new ArrayList<Rectangle[]>();
    int frameCounter = 0;
    int moveIteration = 3; //number of move calls per actual lane swap.
    ArrayList<Integer> choices = new ArrayList<Integer>();//will always be maintained as [0,1,2,3,4] by the end of each
    
    TaxiGame(){
        setTitle("Crazy AI Learns to Play Crazy Taxi");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        try{
            enemyIMG = ImageIO.read(new File("EnemyCar.png"));
        }
        catch(IOException e){
            System.out.println("big help");
        }
        
        newgen = new Population(numplayers, 135 ,HEIGHT-150,30,50,velocity,lanewidth);
        
        resetChoices();
        
        JPanel jp = new JPanel();
        jp.setLayout(null);
        jp.setPreferredSize(new Dimension(pubWIDTH+200, HEIGHT));
        jp.setBackground(Color.yellow);
        
        gPanel gp = new gPanel(Color.gray);
        gp.setBounds(100,0,pubWIDTH,HEIGHT);
        
        JLabel jl = new JLabel("Counter");
        JLabel jl1 = new JLabel("MaxFit");
        JLabel jl2 = new JLabel("GenNo");
        JLabel jl3 = new JLabel("GAME END");                                                                                                                                                         
        jl.setBounds(405, 0, 150,20);
        jl1.setBounds(405, 25, 150, 20);
        jl2.setBounds(405, 50, 150, 20);
        jl3.setBounds(405, 75, 150, 20);
        
        
        timer = new Timer(10, new ActionListener(){
            public void actionPerformed(ActionEvent e){
                gp.update();
                jl.setText("Alive: " + newgen.numAlive());
                jl1.setText("MFitness: " + newgen.maxFitness());
                jl2.setText("Generation: " + genNo);
                if(newgen.maxFitness() > maxFit){
                    maxFit = newgen.maxFitness();
                }
                jl3.setText("Highest: " + maxFit);
            }
        });
        timer.start();
        
        
        jp.add(jl);
        jp.add(jl1);
        jp.add(jl2);
        jp.add(jl3);
        jp.add(gp);
        getContentPane().add(jp);
        pack();
    }
    
    
    
    public class gPanel extends JPanel{
        gPanel(Color c){
            super();
            setBackground(c);
            //addKeyListener(new Manual());
            setFocusable(true);
        }
        public void paintComponent(Graphics g){
            g.setColor(Color.red);
            super.paintComponent(g);
            for(Rectangle[] ray : enemyRows){
                for(int i = 0; i < ray.length; i ++){
                    if(ray[i] == null)
                        continue;
                    //g.fillRect(ray[i].x, ray[i].y, ray[i].width, ray[i].height);
                    g.drawImage(enemyIMG, ray[i].x + 1, ray[i].y, null);
                    //System.out.println("DRAWN");
                }
            }
            //g.setColor(Color.green);
            for(Player p : newgen.players) {
                if(p.alive) {
                    //g.fillRect(p.hitbox.x, p.hitbox.y, p.hitbox.width, p.hitbox.height);
                    p.draw(g);
                }
            }
        }
        
        public void update(){
            frameCounter ++;
            
            if(frameCounter % 7 == 0){
                spawnERow();
                frameCounter = 0;
            }
            //updates each row. if one goes off, it is removed(always the first row).
            for(int r = 0; r < enemyRows.size(); r++){
                for(int rI = 0; rI < enemyRows.get(r).length; rI ++){
                    if(enemyRows.get(r)[rI] == null)
                        continue;
                    //System.out.println(enemyRows.get(r).length);                
                    enemyRows.get(r)[rI].y += velocity;
                    if(enemyRows.get(r)[rI].y >= 850){
                        deleteFirstRow = true;
                        break;
                    }
                }
                if(deleteFirstRow){
                    enemyRows.remove(0);
                    deleteFirstRow = false;
                }
            }
            newgen.update(moveIteration, enemyRows, pubWIDTH, hitThat);
            if(newgen.allDead()){
                enemyRows.clear();
                newgen.resetPopulation();
                //System.out.println("Help me please");
                hitThat = true;
                frameCounter = 0;
                genNo++;
            }
            //System.out.println(enemyRows.size());
            repaint();
        }
        
        /*public class Manual extends KeyAdapter{
            public void keyPressed(KeyEvent e){
                //System.out.println("Key Pressed");
                if(p.swapping)
                    return;
                if(e.getKeyCode() == KeyEvent.VK_LEFT){
                    p.swapping = true;
                    p.movingLeft = true;                    
                }
                if(e.getKeyCode() == KeyEvent.VK_RIGHT){
                    p.swapping = true;
                    p.movingRight = true;
                }
            }
            public void keyReleased(KeyEvent e){
                
            }
        }*/
    }
    
    
    public void spawnERow(){
        resetChoices();
        Rectangle[] tRow = new Rectangle[5];
        for(int i = 0; i < 2; i ++){
            int row = selectRandomChoice();
            tRow[row] = new Rectangle(row*60 + 14,-50,32,50);
        }
        enemyRows.add(tRow);
    }
    
    
    public void resetChoices(){
        choices.clear();
        for(int i = 0; i < 5; i ++){
            choices.add(i);
        }
    }
    
    
    public int selectRandomChoice(){
        int i = (int)(Math.random()*choices.size());
        //System.out.println("Chose " + choices.get(i));
        return choices.remove(i);
    }
    
    
    public void display() {
	EventQueue.invokeLater(new Runnable() {
		public void run() {
		setVisible(true);
		}
	});
    }
    
    public static void main(String[] args){
        TaxiGame tg = new TaxiGame();
        tg.display();
    }
}