package spacestation13.UI;

import spacestation13.GridEditor;
import spacestation13.TileEngine.TileBrush;
import spacestation13.TileEngine.TileType;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Button_TilePreview extends Button{
	private TileType tileType;
	private AtlasRegion atlasRegion;
	private GridEditor editor;
	private TileBrush brush;
	
	public Button_TilePreview(AtlasRegion aReg, Skin skin, TileType tType, GridEditor edit, TileBrush tBrush) {
		super(skin.get(ButtonStyle.class));
		atlasRegion = aReg;
		tileType = tType;
		editor = edit;
		brush = tBrush;
		Table table = new Table();
		table.add(new Image(new TextureRegionDrawable((TextureRegion)aReg)));
		this.add(table);
		this.addListener( new ClickListener() {
            @Override
            public void touchUp(
                InputEvent event,
                float x,
                float y,
                int pointer,
                int button )
            {
                super.touchUp( event, x, y, pointer, button );
                brush.setBrush(tileType, atlasRegion, true); //make pass variable later
            }
        } );
	}
}