package sample;

import com.sun.xml.internal.stream.util.BufferAllocator;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.scene.Group;
import sun.font.FontFamily;

import java.util.Random;

public class Main extends Application
        implements EventHandler<KeyEvent> {

    protected final int Width = 1000;
    protected final int Height = 850;
    protected long t0, t1;

    final int GameMode = 0; //0 - release, 1 - ball demo, 2 - gameplay demo, -1 - debug.


    protected Board board;
    protected Paddle paddle1, paddle2;
    protected Ball ball;
    Group grGame, grNN;
    VBox vboxMain;

    private KeyCode kcode = null;
    private Ball.eCollisionTestResult ballCollisionResult;
    private Ball.eCollisionTestResult _lastCollisionResult;
    private int p1_score, p2_score;

    NeuralNetwork nn;
    NeuralNetworkPlotter nnp;

    AnimationTimer anim;
    //    Group root;
    VBox root;
    Circle c1;
    Label lblMain, lblDebug;
    Label lblP1Score, lblP2Score;
    Button b1, b2;

    public static void main(String[] args) {
//        try {
        launch(args);
//        } catch (Exception ex) {
//            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.setTitle("ERROR!");
//            alert.setContentText(ex.toString());
//            alert.show();
//        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
//        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        //InitWindow(primaryStage);
        //InitNeuralNetwork();
        Initialize(primaryStage);

        t0 = System.nanoTime();
        t1 = t0;
        ball.set_manual(true);
        anim = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update((now - t0) / 2.0 / 1_000_000_000.0);
                update((now - t0) / 2.0 / 1_000_000_000.0);
                // render();
                t0 = now;
            }
        };

        anim.start();
    }

    void Initialize(Stage stg) {
        InitWindow(stg);

        root.setFocusTraversable(true);
        root.requestFocus();
        root.setOnKeyPressed(this);
        root.setOnKeyReleased(this);
    }

    void InitWindow(Stage primaryStage) {
        //root = new Group();
        root = new VBox();
        root.setPadding(new Insets(10, 10, 10, 10));
        root.setAlignment(Pos.TOP_CENTER);
        Scene scene = new Scene(root, Width, Height);

        primaryStage.setTitle("Pong");
        primaryStage.setScene(scene);
        primaryStage.show();

        //vboxMain = new VBox();

        //root.getChildren().add(vboxMain);

        grGame = new Group();
        InitGame(grGame);
        root.getChildren().add(grGame);
        // root.getChildren().add(grGame);

        InitNeuralNetwork();
        //grNN = new Group();
        nnp = new NeuralNetworkPlotter();
        root.getChildren().add(nnp);

        UpdateNeuralNetwork();
    }


    void InitGame(Group gr) {
        board = new Board(700, 400);
        //board.CenterPosition(Width, Height);
        gr.getChildren().add(board);
        gr.getChildren().add(board.GetSplitLine());

        ball = new Ball(350, 45, 10, (int) board.get_CenterX(), (int) board.get_CenterY(), Color.RED);
        ball.setGameMode(GameMode);
        gr.getChildren().add(ball);

        paddle1 = new Paddle(10, 50, 250, (int) (board.get_Left() + 30), (int) (board.get_CenterY()), Color.BLUE);
        paddle1.set_limits(board.get_Top(), board.get_Bottom());
        paddle1.set_Computer(true);
        gr.getChildren().add(paddle1);

        paddle2 = new Paddle(10, 50, 250, (int) (board.get_Right() - 30), (int) (board.get_CenterY()), Color.GREEN);
        paddle2.set_limits(board.get_Top(), board.get_Bottom());
        gr.getChildren().add(paddle2);

        lblMain = new Label();
        lblMain.setLayoutX(5);
        lblMain.setLayoutY(5);
        lblMain.setText("0 FPS");
        gr.getChildren().add(lblMain);

        lblDebug = new Label();
        lblDebug.setLayoutX(500);
        lblDebug.setLayoutY(5);
        lblDebug.setText("DEBUG");
        gr.getChildren().add(lblDebug);

        lblP1Score = new Label("0");
        lblP1Score.setFont(new Font("Courier New", 20));
        lblP1Score.setPrefWidth(100);
        lblP1Score.setTextAlignment(TextAlignment.LEFT);
        lblP1Score.setTranslateX(board.get_Left() + 10);
        lblP1Score.setTranslateY(board.get_Top());
        gr.getChildren().add(lblP1Score);

        lblP2Score = new Label("0");
        lblP2Score.setFont(new Font("Courier New", 20));
        lblP2Score.setPrefWidth(100);
        lblP2Score.setTextAlignment(TextAlignment.RIGHT);
        lblP2Score.setTranslateX(board.get_Right() - 30);
        lblP2Score.setTranslateY(board.get_Top());
        gr.getChildren().add(lblP2Score);
    }

    void InitNeuralNetwork() {
        nn = new NeuralNetwork((long) 0);
        nn.addLayer(7, NeuralNetwork.eActivationFunction.Linear);
        nn.addLayer(7, NeuralNetwork.eActivationFunction.Sigmoid);
        nn.addLayer(3, NeuralNetwork.eActivationFunction.SoftMax);
    }

    int UpdateNeuralNetwork() {
        int retVal = 0;
        nn.set_NodeValue(0, 0, ball.get_speedX()); //input node 1 - speed X
        nn.set_NodeValue(0, 1, ball.get_speedY()); //input node 2 - speed Y
        nn.set_NodeValue(0, 2, ball.distanceToY(board.get_Top())); //input node 3 - distance from ball to top edge
        nn.set_NodeValue(0, 3, ball.distanceToY(board.get_Bottom())); //input node 4 - distance from ball to bottom edge
        nn.set_NodeValue(0, 4, ball.distanceToX(paddle1.getLeft())); //input node 5 - distance from ball to paddle (X)
        nn.set_NodeValue(0, 5, ball.distanceToY(paddle1.getTop())); //input node 6 - distance from ball to paddle top (Y)
        nn.set_NodeValue(0, 6, ball.distanceToY(paddle1.getBottom())); //input node 7 - distance from ball to paddle bottom (Y)
        retVal = nn.getActiveOutput();
        nnp.Draw(nn);
        return retVal;
    }

    void DrawAxes(Group gr) {
        Line l;
        Label lbl;
        double dX, dY, l0;
        l0 = 100;
        for (int i = 0; i < 8; i++) {
            dX = l0 * Math.cos(Math.toRadians(45.0 * i));
            dY = l0 * Math.sin(Math.toRadians(45.0 * i));
            l = new Line(board.get_CenterX(), board.get_CenterY(), board.get_CenterX() + dX, board.get_CenterY() + dY);
            l.setStrokeWidth(3);
            l.setStroke(Color.rgb((int) (255.0 * (8 - i) / 8.0), 0, (int) (255.0 * (i / 8.0))));
            gr.getChildren().add(l);

            lbl = new Label(String.format("%4.1f", i * 45.0));
            if (i == 0)
                lbl.setText("X AXIS" + lbl.getText());
            if (i == 2)
                lbl.setText("Y AXIS" + lbl.getText());
            lbl.setLayoutX(board.get_CenterX() + dX * 1.05);
            lbl.setLayoutY(board.get_CenterY() + dY * 1.05);
            gr.getChildren().add(lbl);
        }
    }

    void update(double timestamp) {
        int _action = UpdateNeuralNetwork();

        double ballAngle = ball.get_angle();
        lblMain.setText(String.format("%5.0f FPS\t%.10f s", 1.0 / timestamp, timestamp));
        ball.setFill(Color.RED);
        ballCollisionResult = ball.DetectCollision(board, paddle1, paddle2);

        if (_lastCollisionResult != ballCollisionResult) {
            switch (ballCollisionResult) {
                case P1_HIT:
                    if (GameMode != 1) {
//                        if (paddle1.is_moveUpRequest())
//                            ball.speed_FlipX_MoveUp();
//                        else {
//                            if (paddle1.is_moveDownRequest())
//                                ball.speed_FlipX_MoveDown();
//                            else
                        //ball.speed_FlipX();
                        // }
                        ball.speed_FlipX();
                        ball.increaseSpeed();
                    }
                    ball.setFill(Color.LIGHTSKYBLUE);
                    break;
                case P2_HIT:
                    if (GameMode != 1) {
//                        if (paddle2.is_moveUpRequest())
//                            ball.speed_FlipX_MoveUp();
//                        else {
//                            if (paddle2.is_moveDownRequest())
//                                ball.speed_FlipX_MoveDown();
//                            else
//                                ball.speed_FlipX();
//                        }
                        ball.speed_FlipX();
                        ball.increaseSpeed();
                    }
                    ball.setFill(Color.LIMEGREEN);
                    break;
                case TOP_WALL_HIT:
                case BOTTOM_WALL_HIT:
                    ball.speed_FlipY();
                    //ball.increaseSpeed();
                    break;
                case P1_SCORES:
                    if (GameMode == 1 || ball.is_manual()) {
                        ball.speed_FlipX();
                    } else {
                        paddle1.AddPoint();
                        ball.Reset(45);
                        UpdateScore();
                    }

                    break;
                case P2_SCORES:
                    if (GameMode == 1 || ball.is_manual()) {
                        ball.speed_FlipX();
                        ball.increaseSpeed();
                    } else {
                        paddle2.AddPoint();
                        ball.Reset(-45);
                        UpdateScore();
                    }

                    break;
                case OUT_OF_BOUNDS:
                    ball.Reset(45);
                    _lastCollisionResult = Ball.eCollisionTestResult.NO_COLLISION;
                case NO_COLLISION:
                    break;
            }
            _lastCollisionResult = ballCollisionResult;
        }

        ball.Update(timestamp);
        paddle1.Update(timestamp, _action);
        paddle2.Update(timestamp, _action);

        //nnp.Draw(nn);
        DebugPrint(kcode);
    }

    void render() {
    }

    void UpdateScore() {
        this.lblP1Score.setText(Integer.toString(paddle1.get_score()));
        this.lblP2Score.setText(Integer.toString(paddle2.get_score()));
    }

    @Override
    public void handle(KeyEvent key) {
        if (key.getCode() == KeyCode.UP) {
            if (key.getEventType() == KeyEvent.KEY_PRESSED)
                paddle2.set_MoveUpRequest(true);
            if (key.getEventType() == KeyEvent.KEY_RELEASED)
                paddle2.set_MoveUpRequest(false);
        }
        if (key.getCode() == KeyCode.DOWN) {
            if (key.getEventType() == KeyEvent.KEY_PRESSED)
                paddle2.set_MoveDownRequest(true);
            if (key.getEventType() == KeyEvent.KEY_RELEASED)
                paddle2.set_MoveDownRequest(false);
        }
        if (key.getCode() == KeyCode.W) {
            if (key.getEventType() == KeyEvent.KEY_PRESSED)
                paddle1.set_MoveUpRequest(true);
            if (key.getEventType() == KeyEvent.KEY_RELEASED)
                paddle1.set_MoveUpRequest(false);
        }
        if (key.getCode() == KeyCode.S) {
            if (key.getEventType() == KeyEvent.KEY_PRESSED)
                paddle1.set_MoveDownRequest(true);
            if (key.getEventType() == KeyEvent.KEY_RELEASED)
                paddle1.set_MoveDownRequest(false);
        }

        if (key.getCode() == KeyCode.SPACE)
            if (key.getEventType() == KeyEvent.KEY_PRESSED)
                ball.set_manual(!ball.is_manual());

        if (key.getCode() == KeyCode.NUMPAD8) {
            if (key.getEventType() == KeyEvent.KEY_PRESSED)
                ball.set_requestUp(true);
            if (key.getEventType() == KeyEvent.KEY_RELEASED)
                ball.set_requestUp(false);
        }

        if (key.getCode() == KeyCode.NUMPAD2) {
            if (key.getEventType() == KeyEvent.KEY_PRESSED)
                ball.set_requestDown(true);
            if (key.getEventType() == KeyEvent.KEY_RELEASED)
                ball.set_requestDown(false);
        }

        if (key.getCode() == KeyCode.NUMPAD4) {
            if (key.getEventType() == KeyEvent.KEY_PRESSED)
                ball.set_requestLeft(true);
            if (key.getEventType() == KeyEvent.KEY_RELEASED)
                ball.set_requestLeft(false);
        }

        if (key.getCode() == KeyCode.NUMPAD6) {
            if (key.getEventType() == KeyEvent.KEY_PRESSED)
                ball.set_requestRight(true);
            if (key.getEventType() == KeyEvent.KEY_RELEASED)
                ball.set_requestRight(false);
        }

        if (key.getCode() == KeyCode.NUMPAD7) {
            if (key.getEventType() == KeyEvent.KEY_PRESSED)
                ball.increaseSpeed(0.9);
        }

        if (key.getCode() == KeyCode.NUMPAD9) {
            if (key.getEventType() == KeyEvent.KEY_PRESSED)
                ball.increaseSpeed(1.1);
        }

        //lblDebug.setText("Key pressed:" + key.getCode());
        kcode = key.getCode();
        //DebugPrint(key.getCode());
    }

    private void DebugPrint(KeyCode kc) {
        String debText = "";
        //debText += "Key pressed:" + kc+"\n";
        debText += String.format("Paddle 1 (X:%4.0f Y:%4.0f)\t", paddle1.getX(), paddle1.getY());
        debText += String.format("Paddle 1 (CP:%4.0f CB:%4.0f)\n", paddle1.get_CenterY(), ball.getCenterY());
        debText += String.format("Ball (sX:%4.0f sY:%4.0f A:%4.1f S:%10.1f)\n", ball.get_speedX(), ball.get_speedY(), ball.get_angle(), ball.get_speed());
        debText += String.format("Ball (T:%4.0f B:%4.0f L:%4.0f R:%4.0f)\n", ball.getTop(), ball.getBottom(), ball.getLeft(), ball.getRight());
        debText += String.format("Ball (BP1_X:%4.0f BP2_X:%4.0f)\n", ball.get_xBallPaddle1(), ball.get_xBallPaddle2());
        //debText += String.format("Board\t(B:%4.0f T:%4.0f R:%4.0f L:%4.0f)\n", board.get_Bottom(), board.get_Top(), board.get_Right(), board.get_Left());
        debText += String.format("Collision detection: %s", ballCollisionResult);

        lblDebug.setText(debText);
    }

}
