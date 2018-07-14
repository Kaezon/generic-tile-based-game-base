package spacestation13.TileEngine;

import spacestation13.SS13;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Tile {
	private TileType tileType;
	private boolean passable;
	private Texture texture;
	private TileMap parentGrid;
	private Vector2 UV;
	private Vector2 UV2;
	
	//Constructor takes the default values from its tile type
	public Tile(SS13 game, TileType tileType, TileMap parent) {
		super();
		this.tileType = tileType;
		this.passable = tileType.passable();
		TextureRegion tileRegion = game.getAtlas(tileType.atlas()).findRegion(tileType.defaultTileImage());
		UV = new Vector2(tileRegion.getU(),tileRegion.getV());
		UV2 = new Vector2(tileRegion.getU2(),tileRegion.getV2());
		this.texture = game.getAtlas(tileType.atlas()).findRegion(tileType.defaultTileImage()).getTexture();
		this.parentGrid = parent;
	}
	
	//Constructor with custom defined values
	public Tile(SS13 game, TileType tileType, boolean passible, String textureName, TileMap parent) {
		super();
		this.tileType = tileType;
		this.passable = passible;
		TextureRegion tileRegion = game.getAtlas(tileType.atlas()).findRegion(textureName);
		this.texture = tileRegion.getTexture();
		this.parentGrid = parent;
		UV = new Vector2(tileRegion.getU(),tileRegion.getV());
		UV2 = new Vector2(tileRegion.getU2(),tileRegion.getV2());
	}
	
	public TileType tileType() { return tileType; }
	public boolean passable() { return passable; }
	public Texture texture() { return texture; }
	public Rectangle getWorldDimensions() { return parentGrid.getWorldDimensions(); }
	public Vector2 getWorldCoordinates() { return parentGrid.getWorldCoordinates(); }
	public Vector2[] getUVcoordinates() { return new Vector2[] {UV, UV2}; }
	public TileMap getParent() { return parentGrid; }
	
	public void setType(SS13 game, TileType type) {
		tileType = type;
		setTexture(game, type.defaultTileImage());
	}
	
	public void setTexture(SS13 game, String newTexture) {
		TextureRegion tileRegion = game.getAtlas(tileType.atlas()).findRegion(newTexture);
		UV = new Vector2(tileRegion.getU(),tileRegion.getV());
		UV2 = new Vector2(tileRegion.getU2(),tileRegion.getV2());
		this.texture = tileRegion.getTexture();
	}
		
}
