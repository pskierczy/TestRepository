package sample;

import java.util.*;

public class NeuralNetwork {
    private ArrayList<Layer> _layers;
    private long _seed;
    private Random _rng;

    protected enum eActivationFunction {
        Linear,
        Step,
        ReLu,
        LeakyReLu,
        Sigmoid,
        SoftMax
    }

    public NeuralNetwork() {
        this(System.currentTimeMillis());
    }

    public NeuralNetwork(long seed) {
        this._layers = new ArrayList<>();
        this._seed = seed;
        this._rng = new Random(this._seed);
    }

    public NeuralNetwork(int inputNodesCount) {
        this();
        this._layers.add(new Layer(inputNodesCount));
    }

    public NeuralNetwork(int inputNodesCount, long seed) {
        this(seed);
        this._layers.add(new Layer(inputNodesCount, seed));
    }

    public ArrayList<Layer> get_layers() {
        return _layers;
    }

    public Layer get_layer(int i) {
        return this._layers.get(i);
    }

    public void set_layers(ArrayList<Layer> _layers) {
        this._layers = _layers;
    }

    public void addLayer(int noOfNodes, int noOfInputs, eActivationFunction activationFunction, long seed) {
        this._layers.add(new Layer(noOfNodes, noOfInputs, activationFunction, seed));
    }

    public void addLayer(int noOfNodes, int noOfInputs, eActivationFunction activationFunction) {
        this.addLayer(noOfNodes, noOfInputs, activationFunction, this._rng.nextInt(Integer.MAX_VALUE));
    }

    public void addLayer(int noOfNodes, eActivationFunction activationFunction) {
        if (this._layers.isEmpty())
            this.addLayer(noOfNodes, 1, activationFunction);
        else
            this.addLayer(noOfNodes, this._layers.get(this._layers.size() - 1).nodesCount(), activationFunction);
    }

    public int getActiveOutput() {
        //return this._layers.get(this._layers.size() - 1).RecalculateLayer(this._layers.get(this._layers.size() - 1).get_nodes());
        int retVal = 0;
        retVal = this.RecalcualteLayer(this.getLayersCount() - 1);
        return retVal;
    }


    public Integer RecalcualteLayer(int layerID) {
//        int retVal = 0;
        if (layerID < 1)
            return null;
        if (layerID > 1)
            this.RecalcualteLayer(layerID - 1);

        return this._layers.get(layerID).RecalculateLayer(this._layers.get(layerID - 1).get_nodes());
    }


    public int getLayersCount() {
        return this._layers.size();
    }

    public void set_NodeValue(int layerID, int nodeID, double value) {
        this._layers.get(layerID).set_NodeValue(nodeID, value);
    }

    public double get_NodeValue(int layerID, int nodeID) {
        return this._layers.get(layerID).get_nodes()[nodeID];
    }

    public void set_Weight(int layerID, int nodeID, int inputID, double value) {
        this._layers.get(layerID).set_Weight(nodeID, inputID, value);
    }

    public double get_Weight(int layerID, int nodeID, int inputID) {
        return this._layers.get(layerID).get_weights()[nodeID][inputID];
    }

    public void set_BiasValue(int layerID, int nodeID, double value) {
        this._layers.get(layerID).set_Bias(nodeID, value);
    }

    public double get_BiasValue(int layerID, int nodeID) {
        return this._layers.get(layerID).get_biases()[nodeID];
    }

    protected class Layer {
        private double[] _nodes;
        private double[] _biases;
        private double[][] _weights;
        private eActivationFunction _activationFunction;
        private boolean _inputLayer, _outputLayer;
        private long _seed;
        private Random _rnd;

        private Layer(double[] nodes, double[] biases, double[][] weights, eActivationFunction activationFunction) {
            this._nodes = nodes;
            this._biases = biases;
            this._weights = weights;
            this._activationFunction = activationFunction;
            this._inputLayer = false;
            this._outputLayer = false;
        }

        Layer(int noOfNodes, int noOfInputs, eActivationFunction activationFunction) {
            this(new double[noOfNodes], new double[noOfNodes], new double[noOfNodes][noOfInputs], activationFunction);
            this._seed = System.currentTimeMillis();
            this._rnd = new Random(this._seed);
            if (noOfInputs != 0) {
                RandomizeBiases();
                RandomizeWeights();
            }
        }

        Layer(int noOfNodes, int noOfInputs, eActivationFunction activationFunction, long seed) {
            this(new double[noOfNodes], new double[noOfNodes], new double[noOfNodes][noOfInputs], activationFunction);
            this._seed = seed;
            this._rnd = new Random(this._seed);
            if (noOfInputs != 0) {
                RandomizeBiases();
                RandomizeWeights();
            }
        }

        //constructor for input layer
        Layer(int noOfNodes) {
            this(noOfNodes, 1, eActivationFunction.Linear);
            this._inputLayer = true;
        }

        Layer(int noOfNodes, long seed) {
            this(noOfNodes, 1, eActivationFunction.Linear, seed);
        }

        private void InitNodes() {
            for (double n : this._nodes)
                n = 0.0;
        }

        public int InputsCount() {
            return this._nodes.length;
        }

        public int nodesCount() {
            return this._nodes.length;
        }

        public double[] get_nodes() {
            return this._nodes;
        }

        public double[] get_biases() {
            return this._biases;
        }

        public double[][] get_weights() {
            return this._weights;
        }

        public void set_NodeValue(int nodeID, double value) {
            this._nodes[nodeID] = value;
        }

        public void set_Weight(int nodeNo, int inputNode, double weight) {
            this._weights[nodeNo][inputNode] = Math.abs(weight) <= 1.0 ? weight : Math.signum(weight) * 1.0;
        }

        public void set_Bias(int nodeID, double value) {
            this._biases[nodeID] = value;
        }

        public eActivationFunction get_activationFunction() {
            return this._activationFunction;
        }

        public void set_activationFunction(eActivationFunction activationFunction) {
            this._activationFunction = activationFunction;
        }


        public int RecalculateLayer(double[] inputs) {
            switch (this.get_activationFunction()) {
                case ReLu:
                    this.CalculateOutput_ReLu(inputs);
                    break;
                case LeakyReLu:
                    this.CalculateOutput_LeakyReLu(inputs);
                    break;
                case Step:
                    this.CalculateOutput_Step(inputs);
                    break;
                case Linear:
                    this.CalculateOutput_Linear(inputs);
                    break;
                case Sigmoid:
                    this.CalculateOutput_Sigmoid(inputs);
                    break;
                case SoftMax:
                    return this.CalculateOutput_SoftMax(inputs);
                //break;
            }
            return -1;
        }


        private void RandomizeBiases() {
            for (int i = 0; i < this._biases.length; i++)
                this._biases[i] = _rnd.nextDouble() * 2.0 - 1.0;
        }

        private void RandomizeWeights() {
            for (int i = 0; i < this._weights.length; i++)
                for (int j = 0; j < this._weights[0].length; j++)
                    this._weights[i][j] = _rnd.nextDouble() * 2.0 - 1.0;
        }

//        public void CalculateWeightedSums(double[] inputValues) {
//            for (double n : this._nodes) {
//                n = 0.0;
//                for (int i = 0; i < inputValues.length - 1; i++)
//                    _nodes[i] += inputValues[i] * _biases[i];
//            }
//        }

        private void CalculateWeightedSum(double[] inputValues) {
            for (int i = 0; i < _nodes.length - 1; i++) {
                _nodes[i] = 0.0;
                for (int j = 0; j < inputValues.length - 1; j++)
                    _nodes[i] += inputValues[j] * _weights[i][j];
                _nodes[i] += _biases[i];
            }
        }

        public void CalculateOutput_Linear(double[] inputValues) {
            CalculateWeightedSum(inputValues);
        }

        public void CalculateOutput_ReLu(double[] inputValues) {
            CalculateWeightedSum(inputValues);
            for (int i = 0; i < _nodes.length - 1; i++)
                _nodes[i] = Math.max(0, _nodes[i]);
        }

        public void CalculateOutput_LeakyReLu(double[] inputValues, double negSteep) {
            CalculateWeightedSum(inputValues);
            for (int i = 0; i < _nodes.length - 1; i++)
                _nodes[i] *= _nodes[i] < 0 ? negSteep : 1.0;
        }

        public void CalculateOutput_LeakyReLu(double[] inputValues) {
            this.CalculateOutput_LeakyReLu(inputValues, 0.005);
        }

        public void CalculateOutput_Step(double[] inputValues, double bottom, double top, double treshold) {
            CalculateWeightedSum(inputValues);
            for (int i = 0; i < _nodes.length - 1; i++)
                _nodes[i] = _nodes[i] > treshold ? top : bottom;
        }

        public void CalculateOutput_Step(double[] inputValues) {
            CalculateOutput_Step(inputValues, 0.0, 1.0, 0.0);
        }

        public void CalculateOutput_Sigmoid(double[] inputValues, double slope) {
            CalculateWeightedSum(inputValues);
            for (int i = 0; i < _nodes.length - 1; i++)
                _nodes[i] = 1.0 / (1 + Math.exp(-slope * _nodes[i]));
        }

        public void CalculateOutput_Sigmoid(double[] inputValues) {
            CalculateOutput_Sigmoid(inputValues, -1.0);
        }

        public int CalculateOutput_SoftMax(double[] inputValues) {
            int _idxOfMax = 0;

            CalculateWeightedSum(inputValues);
            double _sumOfAll = 0.0;
            for (int i = 0; i < _nodes.length - 1; i++)
                _sumOfAll += _nodes[i] = Math.exp(_nodes[i]);

            for (int i = 0; i < _nodes.length - 1; i++) {
                _nodes[i] = _nodes[i] / _sumOfAll;
                if (_nodes[i] > _nodes[_idxOfMax])
                    _idxOfMax = i;
            }

            return _idxOfMax;
        }
    }
}
