import javafx.application.*;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

import java.util.List;

public class Main extends Application
{
	private Game game;
	public static void main(String[] args)
	{
		launch(args);
	}
	
	public void start(Stage stage)
	{
		List<String> args = getParameters().getRaw();
		int WORLD_HEIGHT, WORLD_WIDTH, CELL_SIZE, WINDOW_WIDTH, WINDOW_HEIGHT;
		
		if (args.size() == 3)
		{
			WORLD_WIDTH = Integer.parseInt(args.get(0));
			WORLD_HEIGHT = Integer.parseInt(args.get(1));
			CELL_SIZE = Integer.parseInt(args.get(2));
		}
		else
		{
			WORLD_WIDTH = 200;
			WORLD_HEIGHT = 128;
			CELL_SIZE = 5;
		}
		
		WINDOW_WIDTH = CELL_SIZE*WORLD_WIDTH;
		WINDOW_HEIGHT = CELL_SIZE*WORLD_HEIGHT;
		
		Canvas canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
		VBox mainpane = new VBox();
		HBox topbar = new HBox();
		
		Button start = new Button("Start");
		Button clear = new Button("Clear");
		Button reset = new Button("Reset");
		Button save = new Button("Save to save.dat");
		Button load = new Button("Load from save.dat");
		
		Label speed_label = new Label("Ticks/s:");
		
		TextField speed_field = new TextField("4");
		speed_field.setMaxWidth(35);
		
		
		Slider speed = new Slider();
		speed.setMin(1);
		speed.setMax(50);
		speed.setValue(4);
		speed.setShowTickMarks(false);
		speed.setShowTickLabels(false);
		speed.setMajorTickUnit(10);
		speed.setMinorTickCount(1);
		speed.setBlockIncrement(10);
		speed.setSnapToTicks(true);
		
		topbar.getChildren().addAll(start, reset, clear, save, load, speed_label, speed, speed_field);
		topbar.setSpacing(5);
		
		reset.setDisable(true);
		
		mainpane.getChildren().addAll(topbar, canvas);
		
		game = new Game(WORLD_WIDTH, WORLD_HEIGHT, CELL_SIZE, canvas.getGraphicsContext2D());
		
		canvas.setOnMousePressed((MouseEvent e) ->
		{
			boolean alive;
			if (e.getButton() == MouseButton.PRIMARY)
				alive = true;
			else
				alive = false;
				
			game.setCellByClick((int)e.getX(), (int)e.getY(), alive);
		});
		
		canvas.setOnMouseDragged((MouseEvent e) ->
		{
			boolean alive;
			if (e.getButton() == MouseButton.PRIMARY)
				alive = true;
			else
				alive = false;
				
			game.setCellByClick((int)e.getX(), (int)e.getY(), alive);
		});
		
		start.setOnAction((e) ->
		{
			start.setDisable(true);
			reset.setDisable(false);
			new Thread(game).start();
		});
		
		clear.setOnAction((e) ->
		{
			start.setDisable(false);
			reset.setDisable(true);
			game.clear();
		});
		
		reset.setOnAction((e) ->
		{
			start.setDisable(false);
			reset.setDisable(true);
			game.reset();
		});
		
		save.setOnAction((e) ->
		{
			game.toFile();
		});
		
		load.setOnAction((e) ->
		{
			start.setDisable(false);
			reset.setDisable(true);
			game.fromFile();
		});
		
		speed.setOnMouseDragged((e) ->
		{
			speed_field.setText("" +(int)speed.getValue());
			game.setTicksPerSecond((int)speed.getValue());
		});
		
		speed.setOnMousePressed((e) ->
		{
			speed_field.setText("" +(int)speed.getValue());
			game.setTicksPerSecond((int)speed.getValue());
		});
		
		speed_field.setOnAction((e) ->
		{
			int val;
			try
			{
				val = Integer.parseInt(speed_field.getText());
				if (val < 1 || val > 50)
					throw new Exception("Invalid speed value.");
			}
			catch (Exception ex)
			{
				speed_field.setText("" + (int)speed.getValue());
				return;
			}
			
			speed.setValue(val);
			game.setTicksPerSecond(val);
		});
		
		Scene scene = new Scene(mainpane, WINDOW_WIDTH, WINDOW_HEIGHT+30);
		stage.setScene(scene);
		stage.setResizable(false);
		stage.setTitle("Conway's Game of Life");
		stage.show();
		
		
	}
	
	public void stop()
	{
		game.stop();
	}
}
