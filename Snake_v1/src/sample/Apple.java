package sample;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Apple extends Point {
    private Color _color;

    public Apple(double x, double y, Color color) {
        super(x, y);
        _color = color;
    }

    public Apple() {
        super();
    }

    public Apple(double x, double y) {
        super(x, y);
    }

    public Color get_color() {
        return _color;
    }

    public void set_color(Color _color) {
        this._color = _color;
    }


    public void SetNewFoodPosition(Snake snake, int rows, int columns) {
        Random r = new Random();

        List<Integer> availableLocations = GetAvailableLocations(rows, columns, snake.GetSnakeLocations(rows));
        int nextFoodID = availableLocations.get(r.nextInt(availableLocations.size()));

        this._y = nextFoodID / rows;
        this._x = nextFoodID % rows;
    }

    private List<Integer> GetAvailableLocations(int rows, int columns, List<Integer> snakeLocations) {
        List<Integer> aviLocs = new ArrayList<>(rows * columns);

        for (int r = 0; r < rows; r++)
            for (int c = 0; c < columns; c++)
                aviLocs.add(r + c * (rows));

        aviLocs.removeAll(snakeLocations);

        return aviLocs;
    }
}
