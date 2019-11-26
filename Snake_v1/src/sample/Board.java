package sample;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

import java.util.Random;

public class Board extends Group {

    private int _columns, _rows, _size, _x0, _y0;

    public Board(int columns, int rows, int size, int x0, int y0) {
        super();
        _columns = columns;
        _rows = rows;
        _size = size;
        _x0 = x0 + _size;
        _y0 = y0 + _size;
    }

    public Board(int columns, int rows, int size) {
        this(columns, rows, size, 0, 0);
    }


    public void Reset() {
        this.getChildren().clear();
    }

    public int get_columns() {
        return _columns;
    }

    public void set_columns(int _columns) {
        this._columns = _columns;
    }

    public int get_rows() {
        return _rows;
    }

    public void set_rows(int _rows) {
        this._rows = _rows;
    }

    public int get_size() {
        return _size;
    }

    public void set_size(int _size) {
        this._size = _size;
    }

    public int get_width() {
        return (_columns + 1) * _size;
    }

    public int get_height() {
        return (_rows + 1) * _size;
    }


    public void Draw(Snake snake, Apple apple) {
        this.Reset();

        Rectangle border = new Rectangle(_x0 - _size / 2.0, _y0 - _size / 2.0, _columns * _size, _rows * _size);
        border.setFill(Color.GRAY);
        this.getChildren().add(border);

        DrawFood(apple);
        DrawSnake(snake);
    }

    private void DrawFood(Apple apple) {
        Rectangle rectApple = new Rectangle(_x0 + (apple.get_x() - 0.5) * _size, _y0 + (apple.get_y() - 0.5) * _size, _size, _size);
        //Rectangle rectApple = new Rectangle(_x0 + (apple.get_x() - 0.0) * _size, _y0 + (apple.get_y() - 0.0) * _size, _size, _size);
        rectApple.setFill(apple.get_color());
        this.getChildren().add(rectApple);
    }

    public void DrawSnake(Snake snake) {
        Rectangle rectSnakeSegment;
        for (Point p : snake.get_points()
        ) {
            rectSnakeSegment = new Rectangle(_x0 + (p.get_x() - 0.5) * _size, _y0 + (p.get_y() - 0.5) * _size, _size, _size);
            //rectSnakeSegment = new Rectangle(_x0 + (p.get_x() - 0.0) * _size, _y0 + (p.get_y() - 0.0) * _size, _size, _size);
            rectSnakeSegment.setFill(snake.get_color());
            rectSnakeSegment.setStrokeWidth(1);
            rectSnakeSegment.setStroke(Color.BLACK);

            this.getChildren().add(rectSnakeSegment);
        }
        DrawSnakeHead(snake);
    }

    private void DrawSnakeHead(Snake snake) {

        //LEFT/RIGHT  TOP/BOTTOM
        //|------|    |------|
        //|   1  |    |      |
        //|   |\3|    |      |
        //|   |/ |    | 2__1 |
        //|   2  |    |  \/  |
        //|------|    |---3--|

        Polygon polyHead = new Polygon();
        polyHead.setFill(Color.BLACK);
        Point snakeHead = snake.get_head();


        switch (snake.get_direction()) {
            case UP:
            case DOWN:
                polyHead.getPoints().addAll(new Double[]{
                        _x0 + (snakeHead.get_x() - 0.25) * _size, _y0 + snakeHead.get_y() * _size,
                        _x0 + (snakeHead.get_x() + 0.25) * _size, _y0 + snakeHead.get_y() * _size,
                        _x0 + snakeHead.get_x() * _size,
                        _y0 + (snakeHead.get_y() + (snake.get_direction() == Snake.SnakeDirection.UP ? -0.25 : 0.25)) * _size
                });
                this.getChildren().add(polyHead);
                break;
            case LEFT:
            case RIGHT:
                polyHead.getPoints().addAll(new Double[]{
                        _x0 + snakeHead.get_x() * _size, _y0 + (snakeHead.get_y() - 0.25) * _size,
                        _x0 + snakeHead.get_x() * _size, _y0 + (snakeHead.get_y() + 0.25) * _size,
                        _x0 + (snakeHead.get_x() + (snake.get_direction() == Snake.SnakeDirection.LEFT ? -0.25 : 0.25)) * _size,
                        _y0 + snakeHead.get_y() * _size
                });
                this.getChildren().add(polyHead);
                break;
            default:
                break;
        }


    }
}
