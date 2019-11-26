package sample;

import com.sun.deploy.util.UpdateCheck;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.input.KeyEvent;
import javafx.event.EventHandler;
import javafx.animation.AnimationTimer;

import java.util.List;

public class Main extends Application
        implements EventHandler<KeyEvent> {


    private int rows = 20;
    private int columns = 20;
    private int size = 16;
    private AnimationTimer animationTimer;
    private boolean _AnimationIsRunning;
    private Label lblScore;
    private double frameDelay = 1;//target frames per second
    long t0, dt;
    private int _score;

    private Snake snake;
    private Apple apple;
    private Board MainBoard;

    @Override
    public void start(Stage primaryStage) throws Exception {

        Group root = new Group();
        Initialize(primaryStage, root);

        root.setFocusTraversable(true);
        root.requestFocus();
        root.setOnKeyPressed(this);
        root.setOnKeyReleased(this);

        _AnimationIsRunning = false;

        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                dt = now - t0;
                if (dt * frameDelay * 4 > 1_000_000_000) {
                    Update();
                    Draw();
                    t0 = now;
                }
            }
        };

        t0 = System.currentTimeMillis();
        //animationTimer.start();

    }

    void Initialize(Stage stage, Group root) {

        MainBoard = new Board(columns, rows, size, 0, 50);

        Scene scene = new Scene(root, MainBoard.get_width(), MainBoard.get_height() + 50);
        scene.setFill(Color.BLACK);
        stage.setTitle("SNAKE");
        stage.setScene(scene);
        stage.show();

        lblScore = new Label("SCORE: " + _score);
        lblScore.setLayoutX(10);
        lblScore.setLayoutY(10);
        lblScore.setTextFill(Color.WHITE);
        lblScore.setFont(Font.font(20));
        root.getChildren().add(lblScore);


        snake = new Snake(columns / 2, rows / 2, 4, Color.GREENYELLOW, Snake.SnakeDirection.RIGHT);
        apple = new Apple(0, 0, Color.ORANGE);
        //apple.SetNewFoodPosition(snake, rows, columns);

        root.getChildren().add(MainBoard);

        Draw();
        //DrawAxes(root);
        //root.getChildren().addAll(board);

    }

    private void Draw() {
        MainBoard.Draw(snake, apple);
        lblScore.setText("SCORE: " + _score);
    }

    private void Update() {
        snake.set_color(Color.GREENYELLOW);
        snake.Move();

        if (snake.FoodEatenCheck(apple)) {
            _score++;
            frameDelay = _score / 10 + 1;
            apple.SetNewFoodPosition(snake, MainBoard.get_rows(), MainBoard.get_columns());
        }
        if (snake.SelfHitCheck())
            snake.set_color(Color.RED);
        if (snake.BorderHitCheck(MainBoard))
            snake.set_color(Color.BROWN);

    }


    private void DrawAxes(Group root) {
        Line lx = new Line(MainBoard.get_width() / 2, MainBoard.get_height() / 2,
                MainBoard.get_width() / 2 + 100, MainBoard.get_height() / 2);
        lx.setFill(Color.RED);
        lx.setStrokeWidth(5);
        root.getChildren().add(lx);

        Line ly = new Line(MainBoard.get_width() / 2, MainBoard.get_height() / 2,
                MainBoard.get_width() / 2, MainBoard.get_height() / 2 + 100);
        ly.setStroke(Color.BLUE);
        ly.setStrokeWidth(5);
        root.getChildren().add(ly);


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
        if (key.getCode() == KeyCode.UP && key.getEventType() == KeyEvent.KEY_PRESSED)
            snake.set_requestedDirection(Snake.SnakeDirection.UP);
        if (key.getCode() == KeyCode.DOWN && key.getEventType() == KeyEvent.KEY_PRESSED)
            snake.set_requestedDirection(Snake.SnakeDirection.DOWN);
        if (key.getCode() == KeyCode.LEFT && key.getEventType() == KeyEvent.KEY_PRESSED)
            snake.set_requestedDirection(Snake.SnakeDirection.LEFT);
        if (key.getCode() == KeyCode.RIGHT && key.getEventType() == KeyEvent.KEY_PRESSED)
            snake.set_requestedDirection(Snake.SnakeDirection.RIGHT);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
