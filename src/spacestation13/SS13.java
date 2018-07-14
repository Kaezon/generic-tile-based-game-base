package spacestation13;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Scaling;

public class SS13 extends Game {
	// constant useful for logging
    public static final String LOG = SS13.class.getSimpleName();
    
    // whether we are in development mode
    public static final boolean DEV_MODE = true;
    
	public static final int VIRTUAL_WIDTH = 800;
    public static final int VIRTUAL_HEIGHT = 600;

	
	//Test code for using OrthographicCamera
	//--------------------------------------------------
    private static final float ASPECT_RATIO = (float)VIRTUAL_WIDTH/(float)VIRTUAL_HEIGHT;

    public Camera orthoCam;
    private Rectangle viewport;
    //--------------------------------------------------
    
	
	private AssetManager assetManager;
	
        @Override
        public void create() {
        	Gdx.app.log(SS13.LOG, "Creating SS13 object");
        	assetManager = new AssetManager();
        	loadResources();
        	
        	orthoCam = new OrthographicCamera(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        	Gdx.app.log(SS13.LOG, "Setting screen to Main Menu");
        	setScreen(new MainMenu(this));
        }

        @Override
        public void dispose() {
        	super.dispose();
        	assetManager.dispose();
        }

        @Override
        public void pause() {
                super.pause();
        }

        @Override
        public void render() {
        	super.render();
        	
        	//OrthoCam update code
    		//-------------------------
    		orthoCam.update();
    		orthoCam.apply(Gdx.gl10);
            Gdx.gl.glViewport((int) viewport.x, (int) viewport.y, (int) viewport.width, (int) viewport.height);
            //-------------------------
        }
        	

        @Override
        public void resize(int width, int height) {
        	super.resize(width, height);
        	
        	//Code to constrain the aspect ratio on resize event
        	//-------------------------------------------------------------
        	Vector2 newVirtualRes= new Vector2(0f, 0f);
        	Vector2 crop = new Vector2(width, height);

        	// get new screen size conserving the aspect ratio
        	newVirtualRes.set(Scaling.fit.apply((float)VIRTUAL_WIDTH, (float)VIRTUAL_HEIGHT, (float)width, (float)height));

        	// ensure our scene is centered in screen
        	crop.sub(newVirtualRes);
        	crop.mul(.5f);

        	// build the viewport for further application
        	viewport = new Rectangle(crop.x, crop.y, newVirtualRes.x, newVirtualRes.y);
        	//-------------------------------------------------------------
        }

        @Override
        public void resume() {
                super.resume();
        }
        
        private void loadResources() {
        	Gdx.app.log(SS13.LOG, "Loading resources");
        	//Use asset manager to load images and sounds
        	InternalFileHandleResolver resolver = new InternalFileHandleResolver();
        	assetManager.setLoader(Texture.class, new TextureLoader(resolver));
	    	//assetManager.load("resources/tilesets/standard/floors/Tile_Solid_White.jpg", Texture.class);
	    	//assetManager.load("resources/tilesets/standard/floors/Tile_Solid_Red.jpg", Texture.class);
	    	//assetManager.load("resources/tilesets/standard/floors/Tile_Solid_Green.jpg", Texture.class);
	    	//assetManager.load("resources/tilesets/standard/floors/Tile_Solid_Blue.jpg", Texture.class);
	    	assetManager.load("resources/tilesets/standard/atlases/floors.atlas", TextureAtlas.class);
	    	assetManager.load("resources/images/atlases/ui.atlas", TextureAtlas.class);
	    	assetManager.load("resources/fonts/default.fnt", BitmapFont.class);
	    	//Loop update until it returns false
	    	while(!assetManager.update()){ }
        }
        
        public TextureAtlas getAtlas(String atlas) {
        	return assetManager.get("resources/tilesets/standard/atlases/" + atlas, TextureAtlas.class);
        }
        
        public TextureAtlas getUiAtlas() {
        	return assetManager.get("resources/images/atlases/ui.atlas", TextureAtlas.class);
        }
        
        public TextureAtlas getFloorsAtlas() {
        	return assetManager.get("resources/tilesets/standard/atlases/floors.atlas", TextureAtlas.class);
        }
        
        public AssetManager getAssetManager() {
        	return assetManager;
        }
        
        public Rectangle getScreenSize() {
        	return viewport;
        }
}
