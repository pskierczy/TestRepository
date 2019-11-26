package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.scene.input.KeyEvent;
import javafx.event.EventHandler;
import javafx.animation.AnimationTimer;

import java.util.*;

public class Main extends Application
        implements EventHandler<KeyEvent> {

    ArrayList<PointMassBody> _data;
    protected int WIDTH = 800;
    protected int HEIGHT = 800;
    protected double G = 6.67408E-11;
    protected int NUMBER_OF_BODIES = 250;

    AnimationTimer animationTimer;
    Label lbl;
    double t0;
    boolean _AnimationIsRunning;
    int idx = 0;
    PointMassBody TracedBody;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        double t1;
        t0 = 0;
        Group root = new Group();
        Initialize(primaryStage, root);

        root.setFocusTraversable(true);
        root.requestFocus();
        root.setOnKeyPressed(this);
        root.setOnKeyReleased(this);

        _AnimationIsRunning = false;
        TracedBody = _data.get(idx);

        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                for (int i = 0; i < 100; i++) {
                    //Update(10);
                    Update_Parallel(10);
                }
                //Update(10);
                root.getChildren().add(new Circle(TracedBody.getCenterX(), TracedBody.getCenterY(), 0.5, Color.GREEN));
            }
        };

        //animationTimer.start();
    }

    void Initialize(Stage stage, Group root) {
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        stage.setTitle("n-Body Simulation");
        stage.setScene(scene);
        stage.show();

        //DrawAxes(root);

        InitBodies(NUMBER_OF_BODIES, root);
        lbl = new Label();
        lbl.setLayoutX(5);
        lbl.setLayoutY(5);
        lbl.setText("Simulation Time: 0:0000");

        root.getChildren().add(lbl);
    }

    void DrawAxes(Group root) {
        Line Xaxis, Yaxis;
        Xaxis = new Line(250, 250, 300, 250);
        Xaxis.setStroke(Color.BLUE);
        root.getChildren().add(Xaxis);

        Yaxis = new Line(250, 250, 250, 300);
        Yaxis.setStroke(Color.GREEN);
        root.getChildren().add(Yaxis);

    }

    void InitBodies(int NumberOfBodies, Group root) {
        //Random rnd = new Random(System.currentTimeMillis());
        Random rnd = new Random(1987);
        _data = new ArrayList<>(NumberOfBodies);
        PointMassBody _pmb;
        /* _pmb = new PointMassBody(WIDTH / 2, HEIGHT / 2);
        _pmb.set_mass(20);
        _data.add(_pmb);
        root.getChildren().add(_data.get(0));*/

        for (int i = 0; i < NumberOfBodies; i++) {
            _pmb = new PointMassBody(WIDTH * (0.75 * rnd.nextDouble() + 0.15), HEIGHT * (0.75 * rnd.nextDouble() + 0.15));
            if (i == idx)
                _pmb.setFill(Color.RED);
            _data.add(_pmb);
            root.getChildren().add(_data.get(i));
        }

    }

    @Override
    public void handle(KeyEvent key) {
        if (key.getCode() == KeyCode.SPACE && key.getEventType() == KeyEvent.KEY_PRESSED) {
            if (_AnimationIsRunning) {
                _AnimationIsRunning = false;
                animationTimer.stop();
            } else {
                _AnimationIsRunning = true;
                animationTimer.start();
            }
        }
    }

    void Update(double TimeInterval) {
        //PointMassBody body1;
        String text = "Simulation Time: ";
        t0 += TimeInterval;
        int _NumberOfBodies = _data.size();
        for (int i = 0; i < _NumberOfBodies; i++) {
            if (_data.get(i).is_Alive()) {
                _data.get(i).set_Ax(0.0);
                _data.get(i).set_Ay(0.0);
                for (int j = 0; j < _NumberOfBodies; j++) {
                    if (_data.get(j).is_Alive())
                        if (j != i)
                            if (!_data.get(i).CollisionCheck(_data.get(j)))
                                _data.get(i).CalculateEffectOf(_data.get(j), G);
                }
            }
        }
        for (int i = 0; i < _NumberOfBodies; i++) {
            if (_data.get(i).is_Alive()) {
                _data.get(i).UpdatePosition(TimeInterval, G);
            }
        }


        lbl.setText("Simulation Time: " + t0 +
                "\nALIVE(" + idx + "):" + _data.get(idx).is_Alive() +
                " MASS(" + idx + "):" + _data.get(idx).get_mass() +
                "\nX(" + idx + "):" + String.format("%10.6f", _data.get(idx).get_x()) +
                " Y(" + idx + "):" + String.format("%10.6f", _data.get(idx).get_y()) +
                "\nVx(" + idx + "):" + String.format("%10.5e", _data.get(idx).get_Vx()) +
                " Vy(" + idx + "):" + String.format("%10.5e", _data.get(idx).get_Vy()) +
                "\nAx(" + idx + "):" + String.format("%10.5e", _data.get(idx).get_Ax()) +
                " Ay(" + idx + "):" + String.format("%10.5e", _data.get(idx).get_Ay())
        );
    }

    void Update_Parallel(double TimeInterval) {
        //PointMassBody body1;
        String text = "Simulation Time: ";
        t0 += TimeInterval;
        int _NumberOfBodies = _data.size();
        //for (int i = 0; i < _NumberOfBodies; i++)
        _data.parallelStream().forEach(massptBody ->
                {
                    int i = _data.indexOf(massptBody);
                    if (_data.get(i).is_Alive()) {
                        _data.get(i).set_Ax(0.0);
                        _data.get(i).set_Ay(0.0);
                        for (int j = 0; j < _NumberOfBodies; j++) {
                            if (_data.get(j).is_Alive())
                                if (j != i)
                                    if (!_data.get(i).CollisionCheck(_data.get(j)))
                                        _data.get(i).CalculateEffectOf(_data.get(j), G);
                        }
                    }
                }
        );

        for (int i = 0; i < _NumberOfBodies; i++) {
            if (_data.get(i).is_Alive()) {
                _data.get(i).UpdatePosition(TimeInterval, G);
            }
        }


        lbl.setText("Simulation Time: " + t0 +
                "\nALIVE(" + idx + "):" + _data.get(idx).is_Alive() +
                " MASS(" + idx + "):" + _data.get(idx).get_mass() +
                "\nX(" + idx + "):" + String.format("%10.6f", _data.get(idx).get_x()) +
                " Y(" + idx + "):" + String.format("%10.6f", _data.get(idx).get_y()) +
                "\nVx(" + idx + "):" + String.format("%10.5e", _data.get(idx).get_Vx()) +
                " Vy(" + idx + "):" + String.format("%10.5e", _data.get(idx).get_Vy()) +
                "\nAx(" + idx + "):" + String.format("%10.5e", _data.get(idx).get_Ax()) +
                " Ay(" + idx + "):" + String.format("%10.5e", _data.get(idx).get_Ay())
        );
    }


    void UpdateWithRemoval(double TimeInterval) {
        //PointMassBody body1;
        String text = "Simulation Time: ";
        t0 += TimeInterval;
        //PointMassBody pbmMain, pbmSecond;
        int _NumberOfBodies = _data.size();
        for (PointMassBody pbmMain : _data) {
            pbmMain.set_Ax(0.0);
            pbmMain.set_Ay(0.0);
            for (PointMassBody pbmSecond : _data) {
                if (!pbmSecond.equals(pbmMain))
                    if (pbmMain.CollisionCheck(pbmSecond))
                        _data.remove(pbmSecond);
                    else
                        pbmMain.CalculateEffectOf(pbmSecond, G);
            }
        }
        for (PointMassBody pbmMain : _data)
            pbmMain.UpdatePosition(TimeInterval, G);
    }

}
