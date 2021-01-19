/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ctaxigeneticalgorithm;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
/**
 *
 * @author z32115
 */
public class Population {
    public List<Player> players;
    int x, y, width, height, velocity, lanewidth;
    public Population(int numPlayers,int x, int y, int width, int height, int velocity, int lanewidth){
        players = new ArrayList<Player>();
        for(int i = 0; i < numPlayers; i ++){
            players.add(new Player(x,y,width,height,velocity,lanewidth));
        }
        this.x = x; this.y  = y; this.width = width; this.height = height; this.velocity = velocity; this.lanewidth = lanewidth;
        //System.out.println(y);
    }
    
    public void resetPopulation(){
       //takes the best members of the previous population by fitness and then generate new members based on that. mutate random one
       
       List<Player> nPlayers = new ArrayList<Player>();
       List<Brain> bAdd = new ArrayList<Brain>();
       List<Brain> bs = bestBrains();
       
       for(Brain b : bs){
           nPlayers.add(new Player(x, y, width, height, velocity, lanewidth, b.clone()));
           //System.out.println("Fit brain added.");
       }
       
       for(int i = 0;i < bs.size(); i ++){
           Brain currBrain = bs.remove(0);
           for(Brain mother : bs){
               for(int j = 0; j < 9; j++){
                   bAdd.add(mutate(crossOver(currBrain, mother)));
               }
           }
           bs.add(currBrain);
       }
       
       for(Brain b : bAdd){
           nPlayers.add(new Player(x,y,width,height,velocity,lanewidth, b));
       }
       
       players = nPlayers;
       
       /*
       players.clear();
       for(int i = 0; i < 512; i ++){
           players.add(new Player(x,y,width,height,velocity,lanewidth));
       }*/
       
       System.out.println("Population restored.");
       //System.out.println(players.get(0).hitbox.y);
    }
    
    public class SortbyFitness implements Comparator<Player>{

        @Override
        public int compare(Player o1, Player o2) {
            return o1.fitness - o2.fitness;
        }
        
    }
    
    public Brain crossOver(Brain father, Brain mother){
        int cutoff = father.myNeurons.size()/2;
        ArrayList<Neuron> temp = new ArrayList<Neuron>();
        for(int i = 0; i < cutoff; i ++){
            temp.add(father.myNeurons.get(i).clone());
        }
        for(int i = cutoff; i < father.myNeurons.size();i ++){
            temp.add(mother.myNeurons.get(i).clone());
        }
        int[] details = {5,9,2};
        return new Brain(temp, details);
    } 
    
    public Brain mutate(Brain b){
        int index = (int)(Math.random()*b.myNeurons.size());
        b.myNeurons.set(index, new Neuron(b.myNeurons.get(index).weights.length));
        int[] details = {5,9,2};
        b = new Brain((ArrayList)b.myNeurons.clone(), details);
        return b;
    }
    
    public List<Brain> bestBrains(){
        Collections.sort(players, new SortbyFitness());
        Collections.reverse(players);
        List<Brain> tumorMode = new ArrayList<Brain>();
        for(Player p : players.subList(0,8)){
            tumorMode.add(p.getBrain());
        }
        return tumorMode;
    }

    public void update(int mI, ArrayList<Rectangle[]> eRs, int pW, boolean hitThat){
        for(Player p : players){
            if(!p.alive)
                continue;

            if(p.movingLeft){
                p.moveLeft(mI);
                p.moveCounter++;
                if(p.moveCounter == mI)
                    p.stop();
            }
            else if(p.movingRight){
                p.moveRight(mI);
                p.moveCounter++;
                if(p.moveCounter == mI)
                    p.stop();
            }
            if(!p.swapping && !eRs.isEmpty()){
                //System.out.println("Now moving.");
                p.update(pW, eRs.get(0), mI);
            }
            if(p.hitbox.x < 0 || p.hitbox.x + 30 > pW){
                //System.out.println("You died, lmao");
                //System.out.println(p.hitbox.x + " " + pubWIDTH);
                p.alive = false;
            }
            for(Rectangle[] row : eRs){
                for(Rectangle r : row){
                    if(r == null)
                        continue;
                    //System.out.println("Rectangle: (" + r.x + ", " + r.y + ")");
                    //System.out.println("HBox: (" + p.hitbox.x + ", " + p.hitbox.y + ")");
                    /*if(p.hitbox.intersects(r)){
                        p.alive = false;
                    }*/
                    if(overlaps(p.hitbox, r)){
                        //System.out.println("Pog");
                        //System.out.println("HBox: (" + p.hitbox.x + ", " + p.hitbox.y + ") / (" + p.hitbox.width + ", " + p.hitbox.height + ")");
                        p.alive = false;
                    }
                }

            }
            if(p.alive)
                p.fitness ++;
            }
    }
    
    public int maxFitness(){
        Collections.sort(players, new SortbyFitness());
        return players.get(players.size()-1).fitness;
    }
    
    public int numAlive(){
        int counter = 0;
        for(Player p : players){
            if(p.alive)
                counter++;
        }
        return counter;
    }
    
    public boolean allDead(){//still faster.
        for(Player p : players){
            if(p.alive)
                return false;
        }
        return true;
    }
    
    public boolean overlaps (Rectangle r1, Rectangle r2) {
        return r1.x < r2.x + r2.width && r1.x + r1.width > r2.x && r1.y < r2.y + r2.height && r1.y + r1.height > r2.y;
    }
}
