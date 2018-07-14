package spacestation13.TileEngine;

import spacestation13.SS13;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TileBrush {
	private TileType tileType;
	private AtlasRegion region;
	private boolean passable;
	
	public TileBrush() {
		tileType = tileType.EMPTY;
		region = null;
		passable = false;
	}
	
	public TileBrush(TileType type, AtlasRegion aReg, boolean pass) {
		this.setBrush(type, aReg, pass);
	}
	
	public Tile createTile(SS13 game, TileMap parent) {
		return new Tile(game, tileType, passable, region.name, parent);
	}
	
	public TileType getTileType() {
		return tileType;
	}
	
	public TextureRegion getRegion() {
		return region;
	}
	
	public boolean getPassable() {
		return passable;
	}
	
	public void setTileType(TileType type) {
		tileType = type;
	}
	
	public void setRegion(AtlasRegion aReg) {
		region = aReg;
	}
	
	public void setPassable(boolean pass) {
		passable = pass;
	}
	
	public void setBrush (TileType type, AtlasRegion aReg, boolean pass) {
		tileType = type;
		region = aReg;
		passable = pass;
	}
}
