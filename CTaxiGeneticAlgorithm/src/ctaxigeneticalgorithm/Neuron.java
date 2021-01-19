/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ctaxigeneticalgorithm;
import java.util.ArrayList;
/**
 *
 * @author z32115
 */
public class Neuron {
    public float[] weights;
    public float myValue;
    
    public Neuron(int ips){
        weights = new float[ips];
        //Randomly initialize weights. No bias.
        randomizeWeights();
    }
    
    private Neuron(float[] weights){
        this.weights = weights;
    }
    public Neuron clone(){
        return new Neuron(weights.clone());
    }
    
    private void randomizeWeights(){
        //sets weights to -1f to 1f
        for(int i = 0;i < weights.length; i ++){
            weights[i] = (float)(Math.random()*2) - 1;
        }
    }
    
    public float getValue(float[] inputs){
        float value = 0;
        for(int i = 0; i < inputs.length; i ++){
            value += inputs[i]*weights[i];
        }
        myValue = value;
        return value;
    }
    

}
