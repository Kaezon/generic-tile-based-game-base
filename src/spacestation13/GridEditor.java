package spacestation13;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.input.Mouse;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.esotericsoftware.tablelayout.Cell;

import spacestation13.TileEngine.Tile;
import spacestation13.TileEngine.TileBrush;
import spacestation13.TileEngine.TileMap;
import spacestation13.TileEngine.TileType;
import spacestation13.UI.*;

public class GridEditor extends AbstractScreen {
	private static final int CAMERA_SPEED = 4;
	
	private SpriteBatch batch;
	private TileMap editGrid;
	private Tile selectedTile;
	private TileBrush brush;
	private Camera camera;
	private GL10 gl;
	private Vector2 lastClick;
	private Point gridIntersect;
	private ShapeRenderer shapeRenderer;
	private TextureAtlas floorsAtlas;
	private TextureAtlas uiAtlas;
	private BitmapFont uiFont;
	private HashMap<TileType, List<AtlasRegion>> hmTiles;
	//------------------ UI --------------------//
	private Table table;
	private TextButtonStyle buttonStyle;
	private Skin skin;
	private Label debugMouseX;
	private Label debugMouseY;
	private Label debugTileSelectX;
	private Label debugTileSelectY;
	private KeyedSelectionBox<TileType> sbTileTypes;
	private SelectBox sbTiles;
	private ScrollPane spTest;
	//----------------- Input ------------------//
	private InputMultiplexer masterMultiplexer;
	public class gameInputProcessor extends InputAdapter {
		@Override
		public boolean keyDown(int keycode) {
			float moveX = 0;
	    	float moveY = 0;
	    	
			//Keyboard input: Camera
	    	if(keycode == Input.Keys.W) { moveY += CAMERA_SPEED; }
	    	if(keycode == Input.Keys.S) { moveY -= CAMERA_SPEED; }
	    	if(keycode == Input.Keys.A) { moveX -= CAMERA_SPEED; }
	    	if(keycode == Input.Keys.D) { moveX += CAMERA_SPEED; }
	    	game.orthoCam.translate(moveX, moveY, 0f);
	    	
	    	//Keyboard input: Tile Change (Testing only)
	    	if(keycode == Input.Keys.NUM_1 && selectedTile != null) { selectedTile.setTexture(game, "Tile_Solid_White"); }
	    	if(keycode == Input.Keys.NUM_2 && selectedTile != null) { selectedTile.setTexture(game, "Tile_Solid_Blue"); }
	    	if(keycode == Input.Keys.NUM_3 && selectedTile != null) { selectedTile.setTexture(game, "Tile_Solid_Red"); }
	    	if(keycode == Input.Keys.NUM_4 && selectedTile != null) { selectedTile.setTexture(game, "Tile_Solid_Green"); }
			return true;
		}
		
		@Override
		public boolean touchUp(int screenX, int screenY, int pointer, int button)
        {
			lastClick = ScreenToWorld(new Vector2(Gdx.input.getX(),Gdx.input.getY()));
    		gridIntersect = WorldToTileCoord(lastClick);
    		System.out.println("--CLICKED--");
    		return true;
        }
	};
	//------------------------------------------//
	
	public GridEditor(SS13 game) throws Exception {
		super(game);
		
		//------------------------------------------//
		//----------- Asset Instantiation ----------//
		//------------------------------------------//
		batch = getBatch();
		editGrid = new TileMap();
		editGrid.setGlobalCoords(0, 0);
		camera = game.orthoCam;
		gl = Gdx.gl10;
		selectedTile = null;
		lastClick = null;
		shapeRenderer = new ShapeRenderer();
		floorsAtlas = game.getFloorsAtlas();
		uiAtlas = game.getUiAtlas();
		uiFont = game.getAssetManager().get("resources/fonts/default.fnt", BitmapFont.class);
		hmTiles = new HashMap<TileType,List<AtlasRegion>>();
		registerTiles();
		brush = new TileBrush();
		masterMultiplexer = new InputMultiplexer();
		
		//------------------------------------------//
		//------------------ Input -----------------//
		//------------------------------------------//
		masterMultiplexer.addProcessor(stage);
		masterMultiplexer.addProcessor(new gameInputProcessor());
		
		//------------------------------------------//
		//--------------- UI and Skin --------------//
		//------------------------------------------//
		table = super.getTable();
		
		TextureRegionDrawable buttonUp = new TextureRegionDrawable(uiAtlas.findRegion("Button_Up_Blue"));
		TextureRegionDrawable buttonHover = new TextureRegionDrawable(uiAtlas.findRegion("Button_Hover_Blue"));
		TextureRegionDrawable buttonDown = new TextureRegionDrawable(uiAtlas.findRegion("Button_Down_Blue"));
		TextureRegionDrawable tSelectBoxTop = new TextureRegionDrawable(uiAtlas.findRegion("SelectBox_Top"));
		TextureRegionDrawable tSelectBoxSelected = new TextureRegionDrawable(uiAtlas.findRegion("SelectBox_Selected"));
		TextureRegionDrawable tSelectBoxBackground = new TextureRegionDrawable(uiAtlas.findRegion("SelectBox_Background"));
		TextureRegionDrawable tScrollPaneBackground = new TextureRegionDrawable(uiAtlas.findRegion("TextBox_Dark"));
		TextureRegionDrawable tScrollPaneBarV = new TextureRegionDrawable(uiAtlas.findRegion("ScrollBar_Vertical"));
		TextureRegionDrawable tScrollPaneBarH = new TextureRegionDrawable(uiAtlas.findRegion("ScrollBar_Horizontal"));
		TextureRegionDrawable tScrollPaneKnobV = new TextureRegionDrawable(uiAtlas.findRegion("ScrollKnob_Vertical"));
		TextureRegionDrawable tScrollPaneKnobH = new TextureRegionDrawable(uiAtlas.findRegion("ScrollKnob_Horizontal"));
		
		ButtonStyle bStyle = new ButtonStyle();
		TextButtonStyle tButtonStyle = new TextButton.TextButtonStyle(buttonUp, buttonDown, buttonHover, uiFont);
		SelectBoxStyle sBoxStyle = new SelectBoxStyle();
		sBoxStyle.font = uiFont;
		sBoxStyle.fontColor = Color.WHITE;
		sBoxStyle.background = tSelectBoxBackground;
		ScrollPaneStyle spStyle = new ScrollPaneStyle(tScrollPaneBackground, tScrollPaneBarH, tScrollPaneKnobH, tScrollPaneBarV, tScrollPaneKnobV);
		tButtonStyle.font = uiFont;
		tButtonStyle.fontColor = Color.WHITE;
		
		skin = new Skin();
		skin.add("default", new Label.LabelStyle(game.getAssetManager().get("resources/fonts/default.fnt", BitmapFont.class), Color.WHITE));
		skin.add("default", tButtonStyle);
		skin.add("default", sBoxStyle);
		skin.add("default", spStyle);
		skin.add("default", bStyle);
		
        table.setSkin(skin);
        Table spDebug = new Table();
        sbTileTypes = new KeyedSelectionBox<TileType>(TileType.values(),skin);
        sbTiles = new SelectBox(hmTiles.get(TileType.FLOOR).toArray(), skin);
        spTest = new ScrollPane(spDebug, skin);
        
        //-------------- Mouse Debug ---------------//
  		debugMouseX = new Label("X",skin);
  		debugMouseY = new Label("Y",skin);
  		Table debugMouseTable = new Table();
  		debugMouseTable.add(debugMouseX);
  		debugMouseTable.row();
  		debugMouseTable.add(debugMouseY);
  		debugMouseTable.setTouchable(Touchable.disabled);
  		
  		//----------- Tile Select Debug ------------//
  		debugTileSelectX = new Label("X",skin);
  		debugTileSelectY = new Label("Y",skin);
  		Table debugTileSelectTable = new Table();
  		debugTileSelectTable.add(debugTileSelectX);
  		debugTileSelectTable.row();
  		debugTileSelectTable.add(debugTileSelectY);
        
  		//------------- UI Construction ------------//
        table.align(Align.top);
        Table debugTable = new Table();
        debugTable.add(debugMouseTable);
        debugTable.add(debugTileSelectTable);
        table.add(debugTable).expandX().height((float)(game.VIRTUAL_HEIGHT * 0.85));
        table.add(spTest).width((float)(game.VIRTUAL_WIDTH * 0.2)).height((float)(game.VIRTUAL_HEIGHT * 0.85)).bottom();
        table.row();
        table.add(sbTileTypes).align(Align.left).expandY();
        table.add("corner");
        
        spDebug.pad(10).defaults().expandX().space(4);
        for (AtlasRegion aReg : hmTiles.get(TileType.FLOOR)) {
        	spDebug.add(new Button_TilePreview(aReg, skin, sbTileTypes.GetSelectionValue(), this, brush));
        	spDebug.row();
        }
        spDebug.invalidateHierarchy();
        //------------------------------------------//
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		
    	if(game.getAssetManager().update()) {
    		stage.act( delta );
    		
    		//Clear the openGL render area
    		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
    		gl.glLoadIdentity();
    		
    		//Process input BEFORE rendering
        	processInput();
        	
        	game.orthoCam.update();
            game.orthoCam.apply(gl);
            
        	editGrid.render(game, this, camera, gl);
        	renderSelectionBox();

        	Vector2 worldCoords = ScreenToWorld(new Vector2(Mouse.getX(),Mouse.getY()));
        	debugMouseX.setText(Integer.toString((int)worldCoords.x));
        	debugMouseY.setText(Integer.toString((int)worldCoords.y));
        	if (gridIntersect != null) { 
        		debugTileSelectX.setText(Integer.toString(gridIntersect.x));
        		debugTileSelectY.setText(Integer.toString(gridIntersect.y));
        	}
        	
        	stage.draw();
            Table.drawDebug( stage );
        }
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	public void show() {
		super.show();
		Gdx.input.setInputProcessor(masterMultiplexer);
	}

	@Override
	public void hide() {
		super.hide();
		
	}
	
	@Override
	public void dispose() {
		super.dispose();
		shapeRenderer.dispose();
	}
	
	private void processInput() {
    	//Handle user input here
    	float moveX = 0;
    	float moveY = 0;
    	
    	//Keyboard input: Camera
    	//if(Gdx.input.isKeyPressed(Input.Keys.W)) { moveY += CAMERA_SPEED; }
    	//if(Gdx.input.isKeyPressed(Input.Keys.S)) { moveY -= CAMERA_SPEED; }
    	//if(Gdx.input.isKeyPressed(Input.Keys.A)) { moveX -= CAMERA_SPEED; }
    	//if(Gdx.input.isKeyPressed(Input.Keys.D)) { moveX += CAMERA_SPEED; }
    	//game.orthoCam.translate(moveX, moveY, 0f);
    	
    	//Keyboard input: Tile Change (Testing only)
    	//if(Gdx.input.isKeyPressed(Input.Keys.NUM_1) && selectedTile != null) { selectedTile.setTexture(game, "Tile_Solid_White"); }
    	//if(Gdx.input.isKeyPressed(Input.Keys.NUM_2) && selectedTile != null) { selectedTile.setTexture(game, "Tile_Solid_Blue"); }
    	//if(Gdx.input.isKeyPressed(Input.Keys.NUM_3) && selectedTile != null) { selectedTile.setTexture(game, "Tile_Solid_Red"); }
    	//if(Gdx.input.isKeyPressed(Input.Keys.NUM_4) && selectedTile != null) { selectedTile.setTexture(game, "Tile_Solid_Green"); }
    }
	
	private void registerTiles() {
		List<AtlasRegion> list = new ArrayList<AtlasRegion>();
		for (AtlasRegion aReg : floorsAtlas.getRegions()) {
			list.add(aReg);
		}
		hmTiles.put(TileType.FLOOR, list);
	}
	
	private void renderSelectionBox() {
		if(gridIntersect != null) {
			Vector2 position = WorldtoScreen(TileCoordToWorld(gridIntersect));
			if (position.x == 1 || position.y == 1) { System.out.println("It's full of 1's!"); }
			if(position != null) {
				shapeRenderer.begin(ShapeType.Line);
				shapeRenderer.setColor(Color.YELLOW);
				for(int i=0; i<5; i++) {
					shapeRenderer.rect(position.x-i, position.y-i, 32+(i*2), 32+(i*2));
				}
				shapeRenderer.end();
			}
		}
	}
	
	public Vector2 ScreenToWorld(Vector2 screenCoords) {
		return new Vector2(screenCoords.x + CameraOrigen().x, (CameraOrigen().y+camera.viewportHeight) - screenCoords.y);
	}
	
	public Vector2 WorldtoScreen(Vector2 worldCoords) {
		Vector2 v_coordinates = new Vector2(worldCoords.x - CameraOrigen().x, worldCoords.y - CameraOrigen().y);
		if(v_coordinates.x>0 && v_coordinates.x<camera.viewportWidth) {
			if(v_coordinates.y>0 && v_coordinates.y<camera.viewportHeight) {
				return v_coordinates.cpy();
			}
		}
		return null;
	}
	
	public Point WorldToTileCoord(Vector2 worldCoords) {
		Point tileCoord = new Point();
		worldCoords.div(new Vector2(32,32));
		if (worldCoords.x < 0) 	{ tileCoord.x = (int)Math.floor(worldCoords.x); }
		else 					{ tileCoord.x = (int)Math.ceil(worldCoords.x); }
		if (worldCoords.y < 0) 	{ tileCoord.y = (int)Math.floor(worldCoords.y); }
		else 					{ tileCoord.y = (int)Math.ceil(worldCoords.y); }
		return tileCoord;
	}
	
	//Returns the world coordinates of a tile's ORIGEN (bottom left)
	public Vector2 TileCoordToWorld(Point tileCoords) {
		Point tempCoord = (Point) tileCoords.clone();
		//Subtract 1 if positive to return to origen
		if (tileCoords.x > 0) { tempCoord.x -= 1; }
		if (tileCoords.y > 0) { tempCoord.y -= 1; }
		Vector2 worldCoords = new Vector2(tempCoord.x*32, tempCoord.y*32);
		return worldCoords;
	}
	
	public Vector2 CameraOrigen() {
		return new Vector2((camera.position.x-(camera.viewportWidth/2)),(camera.position.y-(camera.viewportHeight/2)));
	}
}
