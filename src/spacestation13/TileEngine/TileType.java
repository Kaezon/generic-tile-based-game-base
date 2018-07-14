package spacestation13.TileEngine;

public enum TileType {
	FLOOR(true,true,"Tile_Solid_White","floors.atlas"),
	WALL(false,true,"","walls.atlas"),
	EMPTY(true,false,"",null);
	
	private boolean passable;
	private boolean gravitized;
	private String defaultTileImage;
	private String atlas;
	
	TileType (boolean passable, boolean gravatized, String defaultTileImage, String atlas) {
		this.passable = passable;
		this.gravitized = gravatized;
		this.defaultTileImage = defaultTileImage;
		this.atlas = atlas;
	}
	
	public boolean passable() { return passable; }
	public boolean gravitized() { return gravitized; }
	public String defaultTileImage() { return defaultTileImage; }
	public String atlas() { return atlas; }

}
