import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class Program extends Application {
	public final static String TITLE = "Dungeons and Dragons";
	public final static int WIDTH = 1280;
	public final static int HEIGHT = 720;
	
	public enum STATE{
		TITLE,
		NEWCHAR,
		LOADCHAR,
		SETTINGS,
		CHARSHEET
	}
	
	public STATE state = STATE.TITLE; 
	
	private Parent createContent(){
		Pane root = new Pane();
		root.setPrefSize(WIDTH,HEIGHT);
		
		ImageView img = getBackground();		
		MenuBox vbox = createMenu();
			
		root.getChildren().addAll(img,vbox);
		return root;		
	}
	
	private ImageView getBackground(){
		ImageView img = null;

		if (state == STATE.TITLE){
			try (InputStream background = Files.newInputStream(Paths.get("res/images/DnD_BG.jpg"))) {
				img = new ImageView(new Image(background));
				img.setFitWidth(WIDTH);
				img.setFitHeight(HEIGHT);				
			} catch (IOException e) {
				System.out.println("Couln't load image.");
			}
		}
		
		return img;
	}
	
	private MenuBox createMenu() {
		MenuBox vbox = null;
		
		if (state == STATE.TITLE){			
			MenuItem newChar = new MenuItem("NEW CHARACTER");
			MenuItem loadChar = new MenuItem("LOAD CHARACTER");
			MenuItem settings = new MenuItem("SETTINGS");
			MenuItem itemExit = new MenuItem("EXIT");
			
			newChar.setOnMouseClicked(event -> { state = STATE.NEWCHAR; });
			loadChar.setOnMouseClicked(event -> { state = STATE.LOADCHAR; });
			settings.setOnMouseClicked(event -> { state = STATE.SETTINGS; });
			itemExit.setOnMouseClicked(event -> System.exit(0));
			
			vbox = new MenuBox(
				newChar,
				loadChar,
				settings,
				itemExit
			);
			
			vbox.setTranslateX(100);
			vbox.setTranslateY(250);
		}
		return vbox;
	}

	public void start(Stage primaryStage) throws Exception {
		Scene scene = new Scene(createContent());
		primaryStage.setTitle(TITLE);
		primaryStage.setScene(scene);
		primaryStage.show();
	}	
	
	public static class MenuBox extends VBox {
		public MenuBox(MenuItem... items){
			for (MenuItem item : items){
				getChildren().addAll(item);
			}
		}
	}
	
	public static class MenuItem extends StackPane {
		public MenuItem(String name){		
			Rectangle bg = new Rectangle(500, 80);
			bg.setFill(Color.BLACK);
			bg.setStroke(Color.GOLDENROD);
			bg.setOpacity(0.1);
			
			LinearGradient gradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop[] {
					new Stop(0, Color.KHAKI),
					new Stop(0.1, Color.YELLOW),
					new Stop(0.2, Color.DARKORANGE),
					new Stop(0.8, Color.DARKORANGE),
					new Stop(0.9, Color.YELLOW),
					new Stop(1, Color.KHAKI)
			});
			
			// Creating text style
			Text text = new Text(name);
			text.setFill(Color.RED);
			text.setStroke(Color.BLACK);
			text.setFont(Font.loadFont("file:res/fonts/CROM_V1.TTF", 30));
			
			Blend blend = new Blend();
			blend.setMode(BlendMode.MULTIPLY);

			DropShadow ds = new DropShadow();
			ds.setColor(Color.GOLDENROD);
			ds.setOffsetX(1);
			ds.setOffsetY(1);
			ds.setRadius(5);
			ds.setSpread(0.5);

			blend.setBottomInput(ds);
			text.setEffect(blend);
			// End text style
			
			setAlignment(Pos.CENTER);
			getChildren().addAll(bg,text);
			
			setOnMouseEntered(event -> {
				bg.setFill(gradient);
				bg.setOpacity(0.4);
			});
			
			setOnMouseExited(event -> {
				bg.setFill(Color.BLACK);
				bg.setOpacity(0.1);
			});			
			
			setOnMousePressed(event -> {
				bg.setFill(Color.DARKORANGE);
				text.setFill(Color.KHAKI);
			});
			
			setOnMouseReleased(event -> {
				bg.setFill(gradient);
				bg.setOpacity(0.4);
				text.setFill(Color.RED);			
			});
		}
	}
	
	public static void main(String[] args){	
		launch(args);	
	}
}
