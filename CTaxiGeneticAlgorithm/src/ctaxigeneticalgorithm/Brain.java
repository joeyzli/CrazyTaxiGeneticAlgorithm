/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ctaxigeneticalgorithm;
import java.util.*;
/**
 *
 * @author z32115
 */
public class Brain {//neural networking hours
    Layer[] layers;
    public ArrayList<Neuron> myNeurons = new ArrayList<Neuron>();
    
    
    public Brain(int layerCount, int[] lyrDetails){ //lyrDetails gives the length of each layer.
        layers = new Layer[layerCount];
        layers[0] = new Layer(lyrDetails[0], null);//input layer is identified by null as previous.
        for(int i = 1; i < layerCount; i ++){
            layers[i] = new Layer(lyrDetails[i], layers[i-1]);
        }
        getNeurons();       
    }
    
    
    public Brain(Layer[] layers){
        this.layers = layers;      
        getNeurons();
    }
    
    
    public Brain(List<Neuron> neurons, int[] layerDetails){//neurons length is same as sum of the ints in layerDetails
        layers = new Layer[layerDetails.length];
        for(int i = 0;i < layerDetails.length; i++){
            layers[i] = new Layer(neurons.subList(0, layerDetails[i]));
            neurons = neurons.subList(layerDetails[i],neurons.size());
        }
        getNeurons();
    }
    
    private void getNeurons(){
        myNeurons.clear();
        for(Layer l : layers){
            for(Neuron n : l.neurons){
                myNeurons.add(n);
            }
        }
    }
    
   
    public Brain clone(){
        return new Brain(layers.clone());
    }
    
    
    public float[] output(float[] inputs){
        layers[0].setNeurons(inputs);
        float[] nInputs = new float[layers[0].length];
        for(int i = 0; i < layers[0].length; i ++){
            nInputs[i] = layers[0].neurons[i].myValue;
        }
        for(int i = 1; i < layers.length; i ++){
            float[] outputs = new float[layers[i].length];
            for(int j = 0; j < layers[i].length; j++){
                outputs[j] = sigmoid(layers[i].neurons[j].getValue(nInputs));
            }
            nInputs = outputs.clone();
        }
        return nInputs;
    }
    
    public static float sigmoid(float myValue){
        return (float)1/(1+(float)Math.exp(-myValue));
    }
}

