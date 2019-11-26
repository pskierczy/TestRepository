package sample;

///////////////POINT CLASS FOR SIMPLIFICATION OF MECHANICS
public class Point {
    protected double _x, _y;

    public double get_x() {
        return _x;
    }

    public void set_x(double _x) {
        this._x = _x;
    }

    public double get_y() {
        return _y;
    }

    public void set_y(double _y) {
        this._y = _y;
    }

    public Point(double x, double y) {
        _x = x;
        _y = y;
    }

    public Point() {
        this(0, 0);
    }

    public void Move(double dx, double dy) {
        this._x += dx;
        this._y += dy;
    }

    public void Move(Point p) {
        Move(p.get_x(), p.get_y());
    }

    public Point CreateAtDistance(double dx, double dy) {
        return (new Point(this._x + dx, this._y + dy));
    }

    public Point CreateAtDistance(Point p) {
        return CreateAtDistance(p.get_x(), p.get_y());
    }

    public boolean equals(Object SecondPoint) {
        if (SecondPoint instanceof Point) {
            return (this._x == ((Point) SecondPoint).get_x() && this._y == ((Point) SecondPoint).get_y());
        }
        return false;
    }

    public boolean isEqual(Point SecondPoint) {
        return (this._x == SecondPoint.get_x() && this._y == SecondPoint.get_y());
    }


}
