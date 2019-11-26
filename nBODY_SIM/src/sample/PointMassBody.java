package sample;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public class PointMassBody extends Circle {
    double _mass;
    double _Vx, _Vy, _Ax, _Ay;
    boolean _Alive;

    public PointMassBody(double x, double y, double mass, double Vx, double Vy, double Ax, double Ay) {
        super(x, y, mass, Color.BLACK);
        set_x(x);
        set_y(y);
        set_mass(mass);
        set_Vx(Vx);
        set_Vy(Vy);
        set_Ax(Ax);
        set_Ay(Ay);
        _Alive = true;
    }

    public PointMassBody(double x, double y) {
        this(x, y, 1, 0, 0, 0, 0);
    }

    public double get_x() {
        return getCenterX();
    }

    public void set_x(double _x) {
        setCenterX(_x);
    }

    public double get_y() {
        return getCenterY();
    }

    public void set_y(double _y) {
        setCenterY(_y);
    }

    public double get_mass() {
        return _mass;
    }

    public void set_mass(double _mass) {

        if (_mass <= 0) {
            //set_mass(0);
            this._mass = 0;
            _Alive = false;
            this.setVisible(false);
        } else {
            this._mass = _mass;
            //this.setRadius(_mass);
        }
    }

    void setRadiusAfterMerge(double radius2) {
        setRadius(Math.sqrt(Math.pow(getRadius(), 2) + Math.pow(radius2, 2)));
    }

    public double get_Vx() {
        return _Vx;
    }

    public void set_Vx(double _Vx) {
        this._Vx = _Vx;
    }

    public double get_Vy() {
        return _Vy;
    }

    public void set_Vy(double _Vy) {
        this._Vy = _Vy;
    }

    public double get_Ax() {
        return _Ax;
    }

    public void set_Ax(double _Ax) {
        this._Ax = _Ax;
    }

    public double get_Ay() {
        return _Ay;
    }

    public void set_Ay(double _Ay) {
        this._Ay = _Ay;
    }

    public boolean is_Alive() {
        return _Alive;
    }

    public boolean CollisionCheck(PointMassBody SecondObject) {
        if (Math.sqrt(Math.pow(get_x() - SecondObject.get_x(), 2) + Math.pow(get_y() - SecondObject.get_y(), 2)) < 0.75 * (getRadius() + SecondObject.getRadius())) {
            Merge(SecondObject);
            return true;
        } else return false;
    }

    public void Merge(PointMassBody SecondObject) {
        double _New_Mass = get_mass() + SecondObject.get_mass();
        double _New_Vx = (get_Vx() * get_mass() + SecondObject.get_Vx() * SecondObject.get_mass()) / _New_Mass;
        double _New_Vy = (get_Vy() * get_mass() + SecondObject.get_Vy() * SecondObject.get_mass()) / _New_Mass;

        set_x((get_x() * get_mass() + SecondObject.get_x() * SecondObject.get_mass()) / _New_Mass);
        set_y((get_y() * get_mass() + SecondObject.get_y() * SecondObject.get_mass()) / _New_Mass);

        setRadiusAfterMerge(SecondObject.getRadius());

        set_mass(_New_Mass);
        set_Vx(_New_Vx);
        set_Vy(_New_Vy);

        set_Ax(0);
        set_Ay(0);

        SecondObject.set_mass(-1);
    }

    public void CalculateEffectOf(PointMassBody SecondObject, double G) {
        double dx = SecondObject.get_x() - get_x();
        double dy = SecondObject.get_y() - get_y();
        double r = Math.sqrt(dx * dx + dy * dy);

        double a = SecondObject.get_mass() / r;

        _Ax += a * dx / r;
        _Ay += a * dy / r;

        //set_Ax(get_Ax() + SecondObject.get_mass() * (SecondObject.get_x() - get_x()) / Math.pow(Math.abs(SecondObject.get_x() - get_x()), 3));
        //set_Ay(get_Ay() + SecondObject.get_mass() * (SecondObject.get_y() - get_y()) / Math.pow(Math.abs(SecondObject.get_y() - get_y()), 3));
    }

    private void UpdateAccelerations(double TimeInterval) {
        //set_Ax(get_Ax() / get_mass());
        //set_Ay(get_Ay() / get_mass());
    }

    private void UpdateVelocity(double TimeInterval, double G) {
        set_Vx(get_Vx() + get_Ax() * TimeInterval * G);
        set_Vy(get_Vy() + get_Ay() * TimeInterval * G);
    }

    public void UpdatePosition(double TimeInterval, double G) {
        //UpdateAccelerations(TimeInterval);
        UpdateVelocity(TimeInterval, G);
        set_x(get_x() + get_Vx() * TimeInterval);
        set_y(get_y() + get_Vy() * TimeInterval);
    }

    private double SolveRK4(double h, double x, double x0) //for F'(t,x)=xt+x0
    {
        double k1 = 0, k2 = 0, k3 = 0, k4 = 0;
        double t = 0;
        k1 = h * f(t, x, x0);
        k2 = h * f(t + h / 2.0, x + k1 / 2.0, x0);
        k3 = h * f(t + h / 2.0, x + k2 / 2.0, x0);
        k4 = h * f(t + h, x + k3, x0);

        return (k1 + 2 * k2 + 2 * k3 + k4) / 6.0;
    }

    private double f(double t, double x, double x0) {
        return 0 * t * x + x0;
    }
}
