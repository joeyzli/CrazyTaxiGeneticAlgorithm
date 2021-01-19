/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ctaxigeneticalgorithm;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author z32115
 */
public class Layer {
    Neuron[] neurons;
    public int length;
    public Layer(int neuronCount, Layer prevLayer){
        neurons = new Neuron[neuronCount];
        length = neuronCount;
        for(int i = 0; i < neuronCount; i ++){
            if(prevLayer != null)
                neurons[i] = new Neuron(prevLayer.length);
            else
                neurons[i] = new Neuron(0);
        }
    }
    
    public Layer(List<Neuron> neurons){//creates layer from existing list of neurons
        length = neurons.size();
        this.neurons = new Neuron[length];
        for(int i = 0; i < length; i ++){
            this.neurons[i] = neurons.get(i).clone();
        }
    }
    
    public void setNeurons(float[] inputs){//only used for the input layer.
        for(int i = 0; i < length; i ++){
            neurons[i].myValue = inputs[i];
        }
    }
}