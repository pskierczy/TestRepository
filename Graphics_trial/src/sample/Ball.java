package sample;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.Random;

public class Ball extends Circle {
    private double _speed, _angle, _speedX, _speedY;
    private double _baseX, _baseY, _baseSpeed;
    final private double MAX_SPEED = 600;
    private int _gameMode;
    private double _xBallPaddle1, _xBallPaddle2;
    private boolean _requestUp, _requestDown, _requestLeft, _requestRight, _manual;
    Random r;


    public enum eCollisionTestResult {
        NO_COLLISION,
        P1_HIT,
        P2_HIT,
        P1_SCORES,
        P2_SCORES,
        TOP_WALL_HIT,
        BOTTOM_WALL_HIT,
        OUT_OF_BOUNDS
    }

    public double get_speedX() {
        return _speedX;
    }

    public double get_speedY() {
        return _speedY;
    }

    public Ball(double _speed, double _angle, int _size, int _x, int _y, Color _color) {
        super(_x, _y, _size, _color);
        this._baseX = _x;
        this._baseY = _y;
        this.set_speed(_speed);
        this.set_angle(_angle);
        this._baseSpeed = this._speed;
        this.r = new Random();
    }

    public Ball() {
        super(0, 0, 20, Color.RED);
        this.set_speed(100);
        this.set_angle(45);
        this._baseSpeed = _speed;
        this._baseX = 0;
        this._baseY = 0;
    }

    public void Reset(double _angle) {
        this.setCenterX(this._baseX);
        this.setCenterY(this._baseY);
        this.set_angle(_angle);
        this.set_speed(this._baseSpeed);
    }

    public double get_speed() {
        return _speed;
    }

    public void increaseSpeed() {
        increaseSpeed(1.01);
    }

    public void increaseSpeed(double factor) {
        //double newSpeed = this._speed * factor;
        //this._speed *= factor;
        this.set_speed(this._speed * factor);
    }

    public void speed_FlipX() {
        this._speedX *= -1.0;
        this._angle = Math.toDegrees(Math.atan2(this._speedY, this._speedX));
    }

    public void speed_FlipX_MoveUp() { //paddle moving up
        this._speedX *= -1.0;
        if (this._speedY < 0) { //ball moving up
            this._speedY *= 1.15;
            this._speedY = Math.abs(this._speedY) < this._speed ? this._speedY : Math.signum(this._speedY) * this._speed * 0.95;
        } else {
            this._speedY *= 0.85;
        }
        this._speedX = Math.signum(this._speedX) * Math.sqrt(Math.pow(this._speed, 2) - Math.pow(this._speedY, 2));
        this._angle = Math.toDegrees(Math.atan2(this._speedY, this._speedX));
    }

    public void speed_FlipX_MoveDown() { //paddle moving up
        this._speedX *= -1.0;
        if (this._speedY > 0) { //ball moving down
            this._speedY *= 1.15;
            this._speedY = Math.abs(this._speedY) < this._speed ? this._speedY : Math.signum(this._speedY) * this._speed * 0.95;
        } else {
            this._speedY *= 0.85;
        }
        this._speedX = Math.signum(this._speedX) * Math.sqrt(Math.pow(this._speed, 2) - Math.pow(this._speedY, 2));
        this._angle = Math.toDegrees(Math.atan2(this._speedY, this._speedX));
    }

    public void speed_FlipY() {
        this._speedY *= -1.0;
        this._angle = Math.toDegrees(Math.atan2(this._speedY, this._speedX));
    }

    public int getGameMode() {
        return _gameMode;
    }

    public void setGameMode(int gameMode) {
        _gameMode = gameMode;
    }

    public void set_speed(double _speed) {
        this._speed = _speed < this.MAX_SPEED ? _speed : this.MAX_SPEED;
        this._speedX = this._speed * Math.cos(Math.toRadians(this._angle));
        this._speedY = this._speed * Math.sin(Math.toRadians(this._angle));
    }

    public double get_angle() {
        return _angle;
    }

    public void set_angle(double _angle) {
        this._angle = _angle;
        this._speedX = this._speed * Math.cos(Math.toRadians(this._angle));
        this._speedY = this._speed * Math.sin(Math.toRadians(this._angle));
    }

    double getTop() {
        return this.getCenterY() - this.getRadius();
    }

    double getBottom() {
        return this.getCenterY() + this.getRadius();
    }

    double getLeft() {
        return this.getCenterX() - this.getRadius();
    }

    double getRight() {
        return this.getCenterX() + this.getRadius();
    }

    public void set_manual(boolean _manual) {
        this._manual = _manual;
    }

    public boolean is_manual() {
        return _manual;
    }

    public void set_requestUp(boolean _requestUp) {
        this._requestUp = _requestUp;
    }

    public void set_requestDown(boolean _requestDown) {
        this._requestDown = _requestDown;
    }

    public void set_requestLeft(boolean _requestLeft) {
        this._requestLeft = _requestLeft;
    }

    public void set_requestRight(boolean _requestRight) {
        this._requestRight = _requestRight;
    }

    public double get_xBallPaddle1() {
        return _xBallPaddle1;
    }

    public double get_xBallPaddle2() {
        return _xBallPaddle2;
    }

    public void Update(double timestamp) {
        if (this._manual) {
            if (this._requestRight)
                this.setCenterX(this.getCenterX() + this._speed * timestamp);
            if (this._requestLeft)
                this.setCenterX(this.getCenterX() - this._speed * timestamp);
            if (this._requestDown)
                this.setCenterY(this.getCenterY() + this._speed * timestamp);
            if (this._requestUp)
                this.setCenterY(this.getCenterY() - this._speed * timestamp);
        } else {
            this.setCenterX(this.getCenterX() + this._speedX * timestamp);
            this.setCenterY(this.getCenterY() + this._speedY * timestamp);
        }
    }

    public eCollisionTestResult DetectCollision(Board board, Paddle p1, Paddle p2) {
        final double hitThreshold = 1;
        eCollisionTestResult _currentCollisionResult = eCollisionTestResult.NO_COLLISION;
        _xBallPaddle1 = this.getCenterX() - p1.getX() - p1.getWidth() - this.getRadius();
        _xBallPaddle2 = p2.getX() - this.getCenterX() - this.getRadius();

        //Check if Paddle1 hits ball
        if (_xBallPaddle1 <= 0 && _xBallPaddle1 > -this.getRadius() * hitThreshold)
            if (this.getCenterY() - p1.getY() + this.getRadius() * 0.75 > 0 && this.getCenterY() - p1.getY() - this.getRadius() * 0.75 < p1.getHeight())
                _currentCollisionResult = eCollisionTestResult.P1_HIT;

        //Check if Paddle2 hits ball
        if (_xBallPaddle2 <= 0 && _xBallPaddle2 > -this.getRadius() * hitThreshold)
            if (this.getCenterY() - p2.getY() + this.getRadius() * 0.75 > 0 && this.getCenterY() - p2.getY() - this.getRadius() * 0.75 < p2.getHeight())
                _currentCollisionResult = eCollisionTestResult.P2_HIT;

        //0 - release, 1 - ball demo, 2 - gameplay demo, -1 - debug.
        //if (_gameMode == 1) {
        //Check if ball hits right wall
        if (this.getCenterX() + this.getRadius() > board.get_Right())
            _currentCollisionResult = eCollisionTestResult.P1_SCORES;
        //Check if ball hits left wall
        if (this.getCenterX() - this.getRadius() < board.get_Left())
            _currentCollisionResult = eCollisionTestResult.P2_SCORES;

        //Check intersection with Top Board Border
        if (this.getCenterY() - this.getRadius() < board.get_Top())
            _currentCollisionResult = eCollisionTestResult.TOP_WALL_HIT;
        //Check intersection with Top Board Border
        if (this.getCenterY() + this.getRadius() > board.get_Bottom())
            _currentCollisionResult = eCollisionTestResult.BOTTOM_WALL_HIT;

        //Check if out of bounds
        if (this.getBottom() < board.get_Top() || this.getTop() > board.get_Bottom()
                || this.getLeft() > board.get_Right() || this.getRight() < board.get_Left())
            _currentCollisionResult = eCollisionTestResult.OUT_OF_BOUNDS;

        return _currentCollisionResult;
    }

    public double distanceToX(double value)
    {
        return value-this.getCenterX();
    }

    public double distanceToY(double value)
    {
        return value-this.getCenterY();
    }

}
