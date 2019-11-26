package sample;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.Arrays;
import java.util.Collections;

public class NeuralNetworkPlotter extends Canvas {
    private Circle _node;
    private Line _weight;

    public NeuralNetworkPlotter() {
        super(700, 400);
    }

    public NeuralNetworkPlotter(int width, int height) {
        super(width, height);
    }

    public void Draw(NeuralNetwork nn) {
        int _nodeCountLayer1, _nodeCountLayer2, _biasColor, _nodeColor, _weightColor, _weightWidth;
        int _dX, _dYlayer1, _dYlayer2, _dY;
        double _minBias, _maxBias, _dBias, _minVal, _maxVal, _dVal, _weight, _bias;
        final int _radius = 10;
        NeuralNetwork.Layer curLayer;

        GraphicsContext graphCont = this.getGraphicsContext2D();

        //draw boundary box
        graphCont.setLineWidth(2);
        graphCont.setStroke(Color.BLACK);
        graphCont.setFill(Color.WHITE);
        graphCont.fillRect(0, 0, this.getWidth(), this.getHeight());
        graphCont.strokeRect(0, 0, this.getWidth(), this.getHeight());

        //draw weights - represented by lines
        //color -> red for negatives, green for positives
        _dX = (int) (this.getWidth() / (nn.getLayersCount()+1));
        for (int _LayerCounter = 1; _LayerCounter < nn.getLayersCount(); _LayerCounter++) {
            _nodeCountLayer1 = nn.get_layer(_LayerCounter - 1).nodesCount();
            _nodeCountLayer2 = nn.get_layer(_LayerCounter).nodesCount();

            _dYlayer1 = (int) (this.getHeight() / (_nodeCountLayer1 + 1));
            _dYlayer2 = (int) (this.getHeight() / (_nodeCountLayer2 + 1));

            for (int _cCurrentNode = 0; _cCurrentNode < _nodeCountLayer2; _cCurrentNode++) {
                for (int _cInputNode = 0; _cInputNode < _nodeCountLayer1; _cInputNode++) {
                    _weight = nn.get_Weight(_LayerCounter, _cCurrentNode, _cInputNode);
                    _weightWidth = LineWidthFromWeight(_weight);
                    graphCont.setLineWidth(_weightWidth);
                    _weightColor = (int) Math.abs(125 * _weight) + 100;
                    graphCont.setStroke(Color.rgb(_weight < 0 ? _weightColor : 0, _weight < 0 ? 0 : _weightColor, 0));
                    //line from (X,Y)_Layer1 to (X,Y)_Layer2
                    if (_weightWidth > 0)
                        graphCont.strokeLine(_dX * _LayerCounter, _dYlayer1 * (_cInputNode + 1), _dX * (_LayerCounter + 1), _dYlayer2 * (_cCurrentNode + 1));
                }
            }
        }

        //draw Nodes - represented by Circles
        //Fill Color -> white to black (min to max value) (255->0)
        //Border Color -> red to green (bias)

        for (int _LayerCounter = 0; _LayerCounter < nn.getLayersCount(); _LayerCounter++) {
            curLayer = nn.get_layer(_LayerCounter);
            _nodeCountLayer1 = curLayer.nodesCount();

            _dY = (int) (this.getHeight() / (_nodeCountLayer1 + 1));

            _minBias = Arrays.stream(curLayer.get_biases()).min().getAsDouble();
            _maxBias = Arrays.stream(curLayer.get_biases()).max().getAsDouble();

            _minVal = Arrays.stream(curLayer.get_nodes()).min().getAsDouble();
            _maxVal = Arrays.stream(curLayer.get_nodes()).max().getAsDouble();
            _dVal = _minVal - _maxVal;


            for (int _cNode = 0; _cNode < curLayer.nodesCount(); _cNode++) {
                graphCont.setLineWidth(2);
                graphCont.setFill(Color.BLUE);
                graphCont.fillOval(_dX * (_LayerCounter + 1) - (_radius + 1), _dY * (_cNode + 1) - (_radius + 1), 2 * (_radius + 1), 2 * (_radius + 1));

                _nodeColor = (int) ((255 / _dVal) * (curLayer.get_nodes()[_cNode] - _maxVal));
                graphCont.setFill(Color.rgb(_nodeColor, _nodeColor, _nodeColor));
                graphCont.fillOval(_dX * (_LayerCounter + 1) - _radius, _dY * (_cNode + 1) - _radius, 2 * _radius, 2 * _radius);

                _bias = curLayer.get_biases()[_cNode];
                _biasColor = (int) (Math.abs(125 / (_bias < 0 ? _minBias : _maxBias) * _bias)) + 100;
                if (_LayerCounter > 0) {
                    graphCont.setStroke(Color.rgb(_bias < 0 ? _biasColor : 0, _bias < 0 ? 0 : _biasColor, 0));
                    graphCont.strokeOval(_dX * (_LayerCounter + 1) - _radius, _dY * (_cNode + 1) - _radius, 2 * _radius, 2 * _radius);
                }
            }

        }
    }

    int LineWidthFromWeight(double value) {
        double _absValue = Math.abs(value);
        if (_absValue < 0.05)
            return 0;
        if (_absValue < 0.25)
            return 1;
        if (_absValue < 0.5)
            return 2;
        if (_absValue < 0.75)
            return 3;
        return 4;
    }
}


