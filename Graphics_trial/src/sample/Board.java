package sample;

import javafx.scene.paint.*;
import javafx.scene.shape.*;

public class Board extends Rectangle {

    public Board(double _width, double _height) {
        super(_width, _height, Color.WHITE);
        this.setStrokeType(StrokeType.OUTSIDE);
        this.setStroke(Color.BLACK);
        this.setStrokeWidth(1);
    }

    public double get_Left() {
        return this.getX();
    }

    public double get_Right() {
        return this.getX() + this.getWidth();
    }

    public double get_Top() {
        return this.getY();
    }

    public double get_Bottom() {
        return this.getY() + this.getHeight();
    }

    public double get_CenterX() {
        return this.getX() + (this.getWidth()) / 2;
    }

    public double get_CenterY() {
        return this.getY() + (this.getHeight()) / 2;
    }

    public void CenterPosition(int refWidth, int refHeight) {
        this.setX((refWidth - this.getWidth()) / 2);
        this.setY((refHeight - this.getHeight()) / 2);
    }

    public Line GetSplitLine() {
        Line _l = new Line(this.get_CenterX(), this.get_Top(), this.get_CenterX(), this.get_Bottom());
        //_l.getStrokeDashArray().addAll(10d, 10d);
        _l.setStroke(Color.LIGHTGRAY);
        _l.setStrokeWidth(2);
        return _l;
    }

}
