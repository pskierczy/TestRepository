package sample;

import java.util.ArrayList;
import java.util.Random;

public class Generation {
    private ArrayList<NeuralNetwork> _pop;
    private int _popCount;
    private long _seed;
    private Random _rnd;

    Generation(int popCount, long seed) {
        this._seed = seed;
        this._rnd = new Random(this._seed);
        this._popCount = popCount;
        this._pop = new ArrayList<>(this._popCount);
        InitPopulation();
    }

    Generation() {
        //System.currentTimeMillis()
    }

    void InitPopulation() {
        NeuralNetwork nn;
        for (int i = 0; i < this._pop.size() - 1; i++) {
            nn = new NeuralNetwork((long) 0);
            nn.addLayer(7, NeuralNetwork.eActivationFunction.Linear);
            nn.addLayer(7, NeuralNetwork.eActivationFunction.Sigmoid);
            nn.addLayer(3, NeuralNetwork.eActivationFunction.SoftMax);
            this._pop.set(i, nn);

        }
    }

}
