import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;

import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.awt.*;
import java.util.Map;
import java.util.Spliterator;

import static com.almasb.fxgl.dsl.FXGL.*;

public class PongGame extends GameApplication{

    //  Constants
    private static final int PADDLE_WIDTH = 30;
    private static final int PADDLE_HEIGHT = 100;
    private static final int BALL_SIZE= 20;
    private static final int PADDLE_SPEED = 5;
    private static final int BALL_SPEED = 5;

    //  Game objects (FXGL object is called entity)
    private Entity paddle1;
    private Entity paddle2;
    private Entity ball;


    //  Game settings
    @Override
    protected void initSettings(GameSettings gameSettings) {

        //  Set the title of the application
        gameSettings.setTitle("Pong Game");
    }


    //  Initialize game input
    @Override
    protected void initInput() {


        /*
        *   Player 1 Movements
        */
        //  Move paddle up for player 1 using the key "w"
        getInput().addAction(new UserAction("Up 1") {
            @Override
            protected void onAction() {
                paddle1.translateY(-PADDLE_SPEED);
            }
        }, KeyCode.W);

        //  Move paddle down for player 1 using the key "s"
        getInput().addAction(new UserAction("Down 1") {
            @Override
            protected void onAction() {
                paddle1.translateY(PADDLE_SPEED);
            }
        }, KeyCode.S);



        /*
         *   Player 2 Movements
         */
        //  Move paddle up for player 2 using the key UP directional key
        getInput().addAction(new UserAction("Up 2") {
            @Override
            protected void onAction() {
                paddle2.translateY(-PADDLE_SPEED);
            }
        }, KeyCode.UP);

        //  Move paddle up for player 2 using the key DOWN directional key
        getInput().addAction(new UserAction("Down 2") {
            @Override
            protected void onAction() {
                paddle2.translateY(PADDLE_SPEED);
            }
        }, KeyCode.DOWN);
    }


    //  Initialize score for game
    @Override
    protected void initGameVars(Map<String, Object> vars) {
        //  Set both scores to 0
        //  FXGL variables inferred by given input, so "0" infers int
        vars.put("score1", 0);
        vars.put("score2", 0);
    }


    //  Initialize game paddles and ball
    @Override
    protected void initGame() {
        paddle1 = spawnBat(0, getAppHeight() / 2 - PADDLE_HEIGHT / 2);
        paddle2 = spawnBat(getAppWidth() - PADDLE_WIDTH, getAppHeight() / 2 - PADDLE_HEIGHT / 2);

        ball = spawnBall(getAppWidth() / 2 - BALL_SIZE / 2, getAppHeight() / 2 - BALL_SIZE / 2);
    }

    //  Build paddles
    private Entity spawnBat(double x, double y) {
        /* 1. Create new entity at x,y
        *  2. use the view that we provide
        *  3. generate bounds for box from the view
        *  4. add the entity to the world
        *  **5. for the ball we add a velocity property */

        return entityBuilder()
                .at(x, y)
                .viewWithBBox(new Rectangle(PADDLE_WIDTH, PADDLE_HEIGHT))
                .buildAndAttach();
    }

    //  Build ball
    private Entity spawnBall(double x, double y) {
        return entityBuilder()
                .at(x, y)
                .viewWithBBox(new Rectangle(BALL_SIZE, BALL_SIZE))
                .with("velocity", new Point2D(BALL_SPEED, BALL_SPEED))
                .buildAndAttach();
    }


    //  Initialize UI


    @Override
    protected void initUI() {
        Text textScore1 = getUIFactoryService().newText("", Color.BLACK, 22);
        Text textScore2 = getUIFactoryService().newText("", Color.BLACK, 22);

        textScore1.setTranslateX(10);
        textScore1.setTranslateY(50);

        textScore2.setTranslateX(getAppWidth() - 30);
        textScore2.setTranslateY(50);

        textScore1.textProperty().bind(getWorldProperties().intProperty("score1").asString());
        textScore2.textProperty().bind(getWorldProperties().intProperty("score2").asString());

        getGameScene().addUINodes(textScore1, textScore2);
    }

    @Override
    protected void onUpdate(double tpf) {
        Point2D velocity = ball.getObject("velocity");
        ball.translate(velocity);

        //  If ball hits paddle1, reverse x direction
        if (ball.getX() == paddle1.getRightX()
            && ball.getY() < paddle1.getBottomY()
            && ball.getBottomY() > paddle1.getY())  {
            ball.setProperty("velocity", new Point2D(-velocity.getX(), velocity.getY()));
        }

        //  If ball hits paddle 2, reverse x direction
        if (ball.getRightX() == paddle2.getX()
            && ball.getY() < paddle2.getBottomY()
            && ball.getBottomY() > paddle2.getY())  {
            ball.setProperty("velocity", new Point2D(-velocity.getX(), velocity.getY()));
        }

        //  if the ball scores against paddle 1 (left side), add 1 to player 2 score
        if (ball.getX() <= 0){
            getWorldProperties().increment("score2", +1);
            resetBall();
        }

        //  if the ball scores against paddle 2 (right side), add 1 to player 1 score
        if (ball.getRightX() >= getAppWidth()){
            getWorldProperties().increment("score1", +1);
            resetBall();
        }

        //  if the ball hits the bottom of the screen, reverse y direction
        if (ball.getY() <= 0){
            ball.setY(0);
            ball.setProperty("velocity", new Point2D(velocity.getX(), -velocity.getY()));
        }

        //  if the ball hits the top of the screen, reverse y direction
        if (ball.getBottomY() >= getAppHeight()){
            ball.setY(getAppHeight() - BALL_SIZE);
            ball.setProperty("velocity", new Point2D(velocity.getX(), -velocity.getY()));
        }



    }

    private void resetBall() {
        ball.setPosition(getAppWidth() / 2 - BALL_SIZE / 2, getAppHeight() / 2 - BALL_SIZE / 2);
        ball.setProperty("velocity", new Point2D(BALL_SPEED, BALL_SPEED));
    }

    public static void main(String[] args) {
        launch(args);
    }
}

