package spacestation13.TileEngine;

import java.math.*;
import java.util.ArrayList;
import java.util.List;

import spacestation13.GridEditor;
import spacestation13.SS13;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class TileMap {
	private Vector2 globalCoordinate;
	
	static enum NodePosition {
		NW,
		NE,
		SW,
		SE;
	}
	
	protected TileMap root;
	protected Tile data;
	
	protected Rectangle dimensions;
	
	protected TileMap childNW;
	protected TileMap childNE;
	protected TileMap childSW;
	protected TileMap childSE;
	
	public TileMap(TileMap rootNode, Vector2 origen) {
		root = rootNode;
		data = null;
		globalCoordinate = null;
		dimensions = new Rectangle(origen.x, origen.y, 1f, 1f);
		childNW = null;
		childNE = null;
		childSW = null;
		childSE = null;
	}
	
	public TileMap() {
		root = this;
		data = null;
		globalCoordinate = null;
		dimensions = null;
		childNW = null;
		childNE = null;
		childSW = null;
		childSE = null;
	}
	
	public Tile element(int x, int y) { return findTarget(new Vector2(x,y)).getData(); }
	public float GlobalX() { return globalCoordinate.x; }
	public float GlobalY() { return globalCoordinate.y; }
	public float sizeX() { return dimensions.width; }
	public float sizeY() { return dimensions.height; }
	
	public void fillGrid(SS13 game, TileType tileType) {
		List<TileMap> leaves = getAllLeaves();
		for(TileMap leaf : leaves)
			leaf.setData(new Tile(game, tileType, leaf));
	}
	
	public List<Tile> elementsInRange(int positionX, int positionY, int width, int height) {
		List<Tile> tileList = new ArrayList<Tile>();
		for (TileMap leaf : this.leavesInRectangle(new Rectangle(positionX,positionY,width,height)))
			tileList.add(leaf.getData());
		return tileList;
	}
	
	public TileMap getRoot() {
		return root;
	}
	
	public Vector2 getWorldCoordinates() {
		Vector2 worldCoords = new Vector2(dimensions.x,dimensions.y);
		worldCoords = worldCoords.sub(1, 1);
		worldCoords.mul(new Matrix3().setToScaling(32, 32));
		worldCoords.add(root.globalCoordinate);
		return worldCoords;
	}
	
	public Rectangle getWorldDimensions() {
		Vector2 worldCoords = getWorldCoordinates();
		return new Rectangle(worldCoords.x, worldCoords.y, dimensions.width*32, dimensions.height*32);
	}
	
	public void Insert (TileMap newNode) throws Exception {
		//Find the best fit (quadrant)
		NodePosition quadrant;
		
		//Test left-to-right, bottom-to-top
		if (newNode.getDimensions().x < this.dimensions.x + (this.dimensions.width/2)) {
			if (newNode.getDimensions().y < this.dimensions.y + (this.dimensions.height/2))
				quadrant = NodePosition.SW;
			else
				quadrant = NodePosition.SE;
		}
		else {
			if (newNode.getDimensions().y < this.dimensions.y + (this.dimensions.height/2))
				quadrant = NodePosition.NW;
			else
				quadrant = NodePosition.NE;
		}
		
		TileMap child = this.getChild(quadrant);
		//Add the tree as a child, push, or recurse
		if (child == null) {
			child = newNode;
			
			//Adjust Dimensions if neccesary
			Vector2 Low = new Vector2(Integer.MIN_VALUE,Integer.MIN_VALUE);
			Vector2 High = new Vector2(Integer.MAX_VALUE, Integer.MAX_VALUE);
			for (TileMap childNode : getAllChildren()) {
				Low.x = Math.min(Low.x, childNode.dimensions.x);
				Low.y = Math.min(Low.y, childNode.dimensions.y);
				High.x = Math.max(High.x, childNode.dimensions.x);
				High.y = Math.max(High.y, childNode.dimensions.y);
			}
			this.dimensions.set(Low.x, Low.y, High.x, High.y);
		}
		else {
			//Store refrence to the old child
			TileMap oldChild = child;
			//Determine new origen
			Vector2 newOrigen = null;
			switch (quadrant) {
			case NW:
				newOrigen = new Vector2(this.dimensions.x, this.dimensions.y + (this.dimensions.height/2));
			case NE:
				newOrigen = new Vector2(this.dimensions.x + (this.dimensions.width/2), this.dimensions.y + (this.dimensions.height/2));
			case SW:
				newOrigen = new Vector2(this.dimensions.x, this.dimensions.y);
			case SE:
				newOrigen = new Vector2(this.dimensions.x + (this.dimensions.width/2), this.dimensions.y);
			}
			
			if (newOrigen == null)
				throw new Exception ("Bad value given for quadrent.");
			
			child = new TileMap(this.root, newOrigen);
			child.Insert(oldChild);
			child.Insert(newNode);
		}
			
	}

	public void render(SS13 game, GridEditor editor, Camera camera, GL10 gl) {
		Vector2 cameraOrigen = editor.CameraOrigen();
		Rectangle cameraView = new Rectangle((int)cameraOrigen.x,(int)cameraOrigen.y,(int)camera.viewportWidth,(int)camera.viewportHeight);
		List<Tile> tiles = new ArrayList<Tile>();
		List<TileMap> leafTest = this.leavesInRectangle(cameraView);
		for (TileMap leaf : leafTest)
			tiles.add(leaf.getData());
		// We wont need indices if we use GL_TRIANGLE_FAN to draw our quad
        // TRIANGLE_FAN will draw the verts in this order: 0, 1, 2; 0, 2, 3
        Mesh quad = new Mesh(true, 4, 0,
                        new VertexAttribute(Usage.Position, 3, "a_position"),
                        new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoords"));
        
        // Set our verts up in a CCW (Counter Clock Wise) order
        quad.setVertices(new float[] {
                        0f, 0f, 0, 0, 0.5f,			// bottom left
                        32f, 0f, 0, 0.5f, 0.5f,		// bottom right
                        32f, 32f, 0, 0.5f, 0,		// top right
                        0f, 32f, 0, 0, 0});			// top left
        
        gl.glColor4f(1f, 1f, 1f, 1); //Clear colors. this command could be used for lighting later.
        
        // Clear the color buffer
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
       
        // Enable texturing
        Gdx.gl10.glEnable(GL10.GL_TEXTURE_2D);

        // Let's save the matrix state, so we can put it back at
        // the end of the render.. remember that glXXX calls are
        // cumulative - even through render calls
        gl.glPushMatrix();
       
        //Translate gl to the grid origin
        gl.glTranslatef(globalCoordinate.x, globalCoordinate.y, 0);
        
        //Set vector for quad position relative to 0,0
        Vector2 baseCoords = new Vector2(globalCoordinate.x,globalCoordinate.y);
        
        for (Tile tile : tiles) {
        	//update coordinates for draw
        	Vector2 translateVector = tile.getWorldCoordinates().sub(baseCoords);
        	baseCoords.add(translateVector);
        	//Translate the difference vector
        	gl.glTranslatef(translateVector.x, translateVector.y, 0);
        	
        	//Get tile UVs
        	Vector2 UV = tile.getUVcoordinates()[0];
    		Vector2 UV2 = tile.getUVcoordinates()[1];
    		
    		//Set our verts up in a CCW (Counter Clock Wise) order
            quad.setVertices(new float[] {
            				0f, 0f, 0, UV.x, UV2.y,      // bottom left
            				32f, 0f, 0, UV2.x, UV2.y,       // bottom right
            				32f, 32f, 0, UV2.x, UV.y,        // top right
            				0f, 32f, 0, UV.x, UV.y});     // top left
    		
    		//Texture shit
    		Texture quadTexture = tile.texture();
    		quadTexture.bind();
    		//Render shit
    		quad.render(GL10.GL_TRIANGLE_FAN);
        }
	}
	
	
	public Tile checkIntersection(Vector2 clickCoords) {
		if (this.isLeaf()) {
			if (this.contains(clickCoords)) {
				return this.data;
			}
		}
		else {
			for (TileMap child : this.getAllChildren()) {
				if (child.contains(clickCoords)) {
					return child.checkIntersection(clickCoords);
				}
			}
		}
		return null;
	}
	
	public boolean contains(Vector2 point) {
		Rectangle thisRect = this.getWorldDimensions();
		if (thisRect.x <= point.x && (thisRect.x + thisRect.width) >= point.x) {
			if (thisRect.y <= point.y && (thisRect.y + thisRect.height) >= point.y) {
				return true;
			}	
		}
		return false;
	}

	public TileMap findTarget(Vector2 target) {
		if (dimensions.contains(target.x, target.y))
			if (dimensions.x == 1 && dimensions.y == 1)
				return this;
			else {
				if (target.x <= dimensions.x/2) {
					if (target.y <= dimensions.y/2)
						return childNW.findTarget(target);
					else
						return childSW.findTarget(target);
				}
				else {
					if (target.y <= dimensions.y/2)
						return childNE.findTarget(target);
					else
						return childSE.findTarget(target);
				}
			}
		else
			return null;
	}

	public Tile getData() {
		return data;
	}

	public List<TileMap> getAllChildren() {
		List<TileMap> children = new ArrayList<TileMap>();
		for (NodePosition pos : NodePosition.values()) {
			TileMap child = this.getChild(pos);
			if (child != null)
				children.add(child);
		}
			return children;
	}

	public List<TileMap> getAllLeaves() {
		List<TileMap> leaves = new ArrayList<TileMap>();
		if (this.isLeaf())
			leaves.add(this);
		else
		{
			for(TileMap element : this.getAllChildren()) {
				leaves.addAll(element.getAllLeaves());
			}
		}
		return leaves;
	}

	public Rectangle getDimensions() {
		return dimensions;
	}

	public boolean isLeaf() {
		if (this.data != null)
			return true;
		return false;
	}

	public List<TileMap> leavesInRectangle(Rectangle rect) {
		List<TileMap> leafList = new ArrayList<TileMap>();
		if(this.isLeaf())
		{
			if (this.intersect(rect)) {
				leafList.add(this);
			}
		}
		else {
			for (TileMap child : this.getAllChildren()) {
				if (child != null)
					if(child.intersect(rect));
						leafList.addAll(child.leavesInRectangle(rect));
			}
		}
		return leafList;
	}

	public boolean intersect(Rectangle otherRect) {
		//checks if two rectangles (world coords) intersect each other
		Rectangle thisRect = this.getWorldDimensions();
		if (thisRect.x <= (otherRect.x + otherRect.width) && (thisRect.x + thisRect.width) >= otherRect.x) {
			if (thisRect.y <= (otherRect.y + otherRect.height) && (thisRect.y + thisRect.height) >= otherRect.y) {
				return true;
			}	
		}
		return false;
	}
	
	public void setChild(NodePosition pos, TileMap tree) {
		switch(pos) {
		case NW:
			childNW = tree;
		case NE:
			childNE = tree;
		case SW:
			childSW = tree;
		case SE:
			childSE = tree;
		}
	}

	public void setData(Tile newData) {
		data = newData;
	}
	
	public void setGlobalCoords(float x, float y) {
		globalCoordinate = new Vector2(x, y);
	}

	public TileMap getChild(NodePosition pos) {
		TileMap child = new TileMap();
		switch(pos) {
		case NW:
			child = childNW;
		case NE:
			child = childNE;
		case SW:
			child = childSW;
		case SE:
			child = childSE;
		}
		return child;
	}
	
	public void printString() {
		printString(0);
	}
	
	protected void printString(int level) {
		Rectangle dims = this.dimensions;
		for (int i = 1; i <= level; i++)
			System.out.print("\t");
		System.out.println("Level: " + level);
		for (int i = 1; i <= level; i++)
			System.out.print("\t");
		System.out.println("Dimensions( x:" + dims.x + " y:" + dims.y + " H:" + dims.height + " W:" + dims.width + ")");
		Rectangle worldDims = this.getWorldDimensions();
		for (int i = 1; i <= level; i++)
			System.out.print("\t");
		System.out.println("World: x:" + worldDims.x + " y:" + worldDims.y);
		if(!this.isLeaf())
			for (TileMap child : this.getAllChildren()) {
					child.printString(level + 1);
			}
	}
}
