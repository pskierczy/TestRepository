package sample;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

public class Board_Full extends StackPane {
    private Rectangle _board;
    private Line _split;
    private Label _lblP1Score, _lblP2Score;
    private int _p1Score, _p2Score;

    public Board_Full(double _width, double _height) {
        this.setPrefSize(_width, _height);//, Color.WHITE);
        this.setPadding(new Insets(0, 0, 0, 0));
        this._board = new Rectangle(_width, _height, Color.WHITE);
        this._board.setStrokeType(StrokeType.OUTSIDE);
        this._board.setStroke(Color.BLACK);
        this._board.setStrokeWidth(1);
        this._split = new Line();
        this._split.setStroke(Color.GRAY);
        this._split.getStrokeDashArray().addAll(20d, 20d);
        UpdateLine();
    }

    private void UpdateLine() {
        this._split.setStartY(this.get_CenterX());
        this._split.setStartY(this.get_Top());
        this._split.setEndX(this.get_CenterX());
        this._split.setEndY(this.get_Bottom());
    }

    public double get_Left() {
        return this._board.getX();
    }

    public double get_Right() {
        return this._board.getX() + this.getWidth();
    }

    public double get_Top() {
        return this._board.getY();
    }

    public double get_Bottom() {
        return this._board.getY() + this.getHeight();
    }

    public double get_CenterX() {
        return this._board.getX() + (this.getWidth()) / 2;
    }

    public double get_CenterY() {
        return this._board.getY() + (this.getHeight()) / 2;
    }

    public void CenterPosition(int refWidth, int refHeight) {
        this._board.setX((refWidth - this.getWidth()) / 2);
        this._board.setY((refHeight - this.getHeight()) / 2);
        UpdateLine();
    }


}
