package sample;

import javafx.scene.shape.*;
import javafx.scene.paint.*;

public class Paddle extends Rectangle {
    private double _speed;
    private double _ymin, _ymax;
    private boolean _moveUpRequest, _moveDownRequest;
    private int _score;
    private boolean _computer;

    public enum ePaddleState {
        STOP,
        MOVE_UP,
        MOVE_DOWN
    }

    public Paddle() {
        super(10, 50, Color.BLACK);
        this._speed = 1;
        _ymin = 0;
        _ymax = 100;
        _score = 0;
    }

    public Paddle(int _width, int _height, double _speed, int _xCenter, int _yCenter, Color _color) {
        super(_width, _height, _color);
        this._speed = _speed;
        this.setX(_xCenter - _width / 2);
        this.setY(_yCenter - _height / 2);
        this._ymin = 0;
        this._ymax = 100;
        this._score = 0;
    }

    public boolean is_moveUpRequest() {
        return _moveUpRequest;
    }

    public boolean is_moveDownRequest() {
        return _moveDownRequest;
    }

    public boolean is_Computer() {
        return _computer;
    }

    public void set_Computer(boolean _computer) {
        this._computer = _computer;
    }

    public Paddle(int _width, int _height, double _speed, int _xCenter, int _yCenter, Color _color, double _ymin, double _ymax) {
        super(_width, _height, _color);
        this._speed = _speed;
        this.setX(_xCenter - _width / 2);
        this.setY(_yCenter - _height / 2);
        this._ymin = _ymin;
        this._ymax = _ymax;
        this._score = 0;
    }

    public double get_speed() {
        return _speed;
    }

    public void set_speed(double _speed) {
        this._speed = _speed;
    }

    public void set_limits(double _min, double _max) {
        this._ymin = _min;
        this._ymax = _max;
    }

    public void set_MoveUpRequest(boolean value) {
        this._moveUpRequest = value;
    }

    public void set_MoveDownRequest(boolean value) {
        this._moveDownRequest = value;
    }

    public void Update(double timestamp, Ball ball) {
        final double _oppositeAct = 0.01, _correctAct = 0.025;
        if (this._computer) {
            double r = Math.random();
            if (ball.get_speedX()<0) {
                if (this.get_CenterY() > ball.getCenterY()) //ball above paddle, move up
                {
                    if (r < _oppositeAct)
                        this.MoveDown(timestamp);
                    else if (r > _correctAct)
                        this.MoveUp(timestamp);
                } else {
                    if (this.get_CenterY() < ball.getCenterY()) //ball above paddle, move down
                    {
                        if (r < _oppositeAct)
                            this.MoveUp(timestamp);
                        else if (r > _correctAct)
                            this.MoveDown(timestamp);
                    }
                }
            }
        } else Update(timestamp);
    }

    public void Update(double timestamp, int _action) {
        if (this._computer) {
            switch (_action)
            {
                case 0:
                    this.MoveDown(timestamp);
                    break;
                case 1:
                    this.MoveUp(timestamp);
                    break;
                case 2:
                    break;
            }
        } else Update(timestamp);
    }

    public void Update(double timestamp) {
        if (this._moveUpRequest)
            MoveUp(timestamp);

        if (this._moveDownRequest)
            MoveDown(timestamp);
    }


    public ePaddleState getState() {
        if (this._moveUpRequest)
            return ePaddleState.MOVE_UP;
        else if (this._moveDownRequest)
            return ePaddleState.MOVE_DOWN;
        else
            return ePaddleState.STOP;
    }

    public void AddPoint() {
        this._score++;
    }

    public int get_score() {
        return _score;
    }

    public void set_score(int _score) {
        this._score = _score;
    }

    private void MoveUp(double timestamp) {
        double newY = this.getY() - this._speed * timestamp;
        if (newY < _ymin)
            this.setY(this._ymin);
        else
            this.setY(newY);
    }

    private void MoveDown(double timestamp) {
        double newY = this.getY() + this._speed * timestamp;
        if (newY + this.getHeight() > _ymax)
            this.setY(this._ymax - this.getHeight());
        else
            this.setY(newY);
    }

    public double get_CenterY() {
        return this.getY() + this.getHeight() / 2.0;
    }

    public double getTop()
    {
        return  this.getY();
    }

    public double getBottom()
    {
        return this.getTop()+this.getHeight();
    }

    public double getLeft()
    {
        return this.getX();
    }

    public double getRight()
    {
        return  this.getLeft()+this.getWidth();
    }
}
