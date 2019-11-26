package sample;

import java.util.ArrayList;
import java.util.List;

public class NeuralNetwork_bad {
    private String _id;
    private Layer _InputLayer, OutputLayer;
    private List<Layer> _HiddenLayers;


    ///////////////ACTIVATION FUNCTIONS
    private interface ActivationFunction {
        public double GetOutput();
    }

    ///////////////SUMMING FUNCTIONS
    private interface SummingFunction {
        public double GetSum();
    }

    private class Node {
        private String _id;
        private List<Connection> _inputs;
        private double _value;
        private ActivationFunction _activationFunction;
        private SummingFunction _summingFunction;

        public Node() {
            this("", new ArrayList<>());
        }

        public ActivationFunction get_activationFunction() {
            return _activationFunction;
        }

        public void set_activationFunction(ActivationFunction _activationFunction) {
            this._activationFunction = _activationFunction;
        }

        public SummingFunction get_summingFunction() {
            return _summingFunction;
        }

        public void set_summingFunction(SummingFunction _summingFunction) {
            this._summingFunction = _summingFunction;
        }

        public Node(String id, List<Connection> inputs) {
            this._id = id;
            this._inputs = inputs;
            this._value = 0;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public List<Connection> get_inputs() {
            return _inputs;
        }

        public void set_inputs(List<Connection> _inputs) {
            this._inputs = _inputs;
        }

        public double get_value() {
            return _value;
        }

        public void set_value(double _value) {
            this._value = _value;
        }

        public double CalculateOutput() {
            double _retVal = 0;

            for (Connection cn : this._inputs)
                _retVal += cn.get_fromNode().get_activationFunction().GetOutput() * cn.get_weight();

            return _retVal;
        }
    }

    private class Connection {
        private Node _fromNode;
        private double _weight;

        public Connection(Node fromNode, double weight) {
            this._fromNode = fromNode;
            this._weight = weight;
        }

        public Connection(Node fromNode) {
            this(fromNode, Math.random());
        }

        public Connection() {
            this(null, Math.random());
        }

        public Node get_fromNode() {
            return _fromNode;
        }

        public void set_fromNode(Node _fromNode) {
            this._fromNode = _fromNode;
        }

        public double get_weight() {
            return _weight;
        }

        public void set_weight(double _weight) {
            this._weight = _weight;
        }
    }

    private class Layer {

    }
}
