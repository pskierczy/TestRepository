package sample;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Snake {
    public enum SnakeDirection {
        NONE(0, "NONE"),
        UP(1, "UP"),
        RIGHT(2, "RIGHT"),
        DOWN(-1, "DOWN"),
        LEFT(-2, "LEFT");

        private int _value;
        private String _text;

        SnakeDirection(int value, String text) {
            _value = value;
            _text = text;
        }

        public int get_value() {
            return _value;
        }

        @Override
        public String toString() {
            return _text;
        }
    }

    private Color _color;
    private int _length;
    private List<Point> _points;
    private Point _head;
    private SnakeDirection _direction, _requestedDirection;
    private boolean _isAlive;


    public Color get_color() {
        return _color;
    }

    public void set_color(Color _color) {
        this._color = _color;
    }

    public int get_length() {
        return _length;
    }

    public void set_length(int _length) {
        this._length = _length;
    }

    public List<Point> get_points() {
        return _points;
    }

    public SnakeDirection get_direction() {
        return _direction;
    }

    private void set_direction(SnakeDirection direction) {
        System.out.print("Direction:" + _direction.toString() + "  Requested:" + direction.toString());
        if (Math.abs(direction.get_value() + this._direction.get_value()) > 0) {
            this._direction = direction;
            System.out.println("+++ALLOWED+++");
        } else
            System.out.println("---NOT ALLOWED---");
    }

    public void set_requestedDirection(SnakeDirection _requestedDirection) {
        this._requestedDirection = _requestedDirection;
    }

    public Point get_head() {
        return _head;
    }

    public Snake(int xHead, int yHead, int length, Color color, SnakeDirection direction) {
        _points = new ArrayList<>(400);
        _color = color;
        _direction = direction;
        _requestedDirection = direction;
        _isAlive = true;

        int dx = (direction == SnakeDirection.RIGHT ? 1 : (_direction == SnakeDirection.LEFT ? 1 : 0));
        int dy = (direction == SnakeDirection.UP ? 1 : (_direction == SnakeDirection.DOWN ? 1 : 0));
        int x0 = xHead - (length - 1) * dx;
        int y0 = yHead - (length - 1) * dy;

        for (int i = 0; i < length; i++) {
            _points.add(new Point(x0 + dx * i, y0 + dy * i));
        }
        _head = _points.get(length - 1);
    }

    public boolean BorderHitCheck(Board board) {
        return BorderHitCheck(board.get_rows(), board.get_columns());
    }

    public boolean BorderHitCheck(int rows, int columns) {
        return (_head.get_x() < 0 || _head.get_x() > columns - 1
                || _head.get_y() < 0 || _head.get_y() > rows - 1);
    }

    public boolean SelfHitCheck() {
        //boolean hitItself=false;
        for (int i = 0; i < _points.size() - 1; i++)
            if (_points.get(i).equals(_head))
                return true;
        return false;
    }

    public boolean FoodEatenCheck(Point food) {
        if (_head.equals(food))
            return true;
        else {
            _points.remove(0);
            return false;
        }
    }

    public void Move() {
        Point newHeadPosition, movement;
        set_direction(_requestedDirection);
        // coordinate system:
        // |---- X positive right
        // |
        // |
        // Y positive down
        switch (this._direction) {
            case UP:
                movement = new Point(0, -1);
                break;
            case DOWN:
                movement = new Point(0, 1);
                break;
            case LEFT:
                movement = new Point(-1, 0);
                break;
            case RIGHT:
                movement = new Point(1, 0);
                break;
            case NONE:
            default:
                movement = new Point();
                break;
        }
        newHeadPosition = _head.CreateAtDistance(movement);
        _points.add(newHeadPosition);
        //_points.remove(0);
        _head = newHeadPosition;
    }

    ////
    public List<Integer> GetSnakeLocations(int rows) {
        List<Integer> SnakeLocations = new ArrayList<>(get_length());
        for (Point p : _points)
            SnakeLocations.add((int) (p.get_x() + p.get_y() * (rows)));

        return SnakeLocations;
    }


}
