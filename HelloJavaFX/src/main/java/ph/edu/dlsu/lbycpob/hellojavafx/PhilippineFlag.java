package ph.edu.dlsu.lbycpob.hellojavafx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class PhilippineFlag extends Application {

    @Override
    public void start(Stage stage) {

        Canvas canvas = new Canvas(600, 400);
        GraphicsContext graphics = canvas.getGraphicsContext2D();

        // Flag dimensions
        double flagWidth = 500;
        double flagHeight = 300;
        double startX = 50;
        double startY = 50;

        // Blue upper half
        graphics.setFill(Color.BLUE);
        graphics.fillRect(startX, startY, flagWidth, flagHeight / 2);

        // Red lower half
        graphics.setFill(Color.RED);
        graphics.fillRect(startX, startY + flagHeight / 2,
                flagWidth, flagHeight / 2);

        // White triangle
        double[] xPoints = {
                startX,
                startX,
                startX + flagWidth * 0.42
        };

        double[] yPoints = {
                startY,
                startY + flagHeight,
                startY + flagHeight / 2
        };

        graphics.setFill(Color.WHITE);
        graphics.fillPolygon(xPoints, yPoints, 3);

        // Yellow sun
        double sunCenterX = startX + 75;
        double sunCenterY = startY + flagHeight / 2;
        double sunRadius = 25;

        graphics.setFill(Color.GOLD);
        graphics.fillOval(
                sunCenterX - sunRadius,
                sunCenterY - sunRadius,
                sunRadius * 2,
                sunRadius * 2
        );

        // Sun rays
        graphics.setStroke(Color.GOLD);
        graphics.setLineWidth(4);

        for (int i = 0; i < 8; i++) {
            double angle = Math.toRadians(i * 45);

            double innerX = sunCenterX + Math.cos(angle) * 32;
            double innerY = sunCenterY + Math.sin(angle) * 32;

            double outerX = sunCenterX + Math.cos(angle) * 50;
            double outerY = sunCenterY + Math.sin(angle) * 50;

            graphics.strokeLine(innerX, innerY, outerX, outerY);
        }

        // Three stars
        drawStar(graphics, startX + 20, startY + 50, 10);
        drawStar(graphics, startX + 20, startY + flagHeight - 50, 10);
        drawStar(graphics, startX + 150, startY + flagHeight / 2, 10);

        // Display canvas
        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root, 600, 400);

        stage.setTitle("Philippine Flag");
        stage.setScene(scene);
        stage.show();
    }

    private void drawStar(GraphicsContext graphics,
                          double centerX,
                          double centerY,
                          double radius) {

        double[] xPoints = new double[10];
        double[] yPoints = new double[10];

        for (int i = 0; i < 10; i++) {

            double angle = Math.toRadians(-90 + i * 36);

            double currentRadius;

            if (i % 2 == 0) {
                currentRadius = radius;
            } else {
                currentRadius = radius * 0.4;
            }

            xPoints[i] = centerX + Math.cos(angle) * currentRadius;
            yPoints[i] = centerY + Math.sin(angle) * currentRadius;
        }

        graphics.setFill(Color.GOLD);
        graphics.fillPolygon(xPoints, yPoints, 10);
    }

    public static void main(String[] args) {
        launch();
    }
}