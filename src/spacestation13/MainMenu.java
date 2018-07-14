package spacestation13;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class MainMenu extends AbstractScreen {
	
	TextButtonStyle buttonStyle;
	Skin skin;
	TextureAtlas uiAtlas;
	
	public MainMenu(SS13 myGame) {
		super(myGame);
		
		uiAtlas = game.getUiAtlas();
		
		TextureRegionDrawable buttonUp = new TextureRegionDrawable(uiAtlas.findRegion("Button_Up_Blue"));
		TextureRegionDrawable buttonHover = new TextureRegionDrawable(uiAtlas.findRegion("Button_Hover_Blue"));
		TextureRegionDrawable buttonDown = new TextureRegionDrawable(uiAtlas.findRegion("Button_Down_Blue"));
		TextButtonStyle tButtonStyle = new TextButton.TextButtonStyle(buttonUp, buttonDown, buttonHover, game.getAssetManager().get("resources/fonts/default.fnt", BitmapFont.class));
		tButtonStyle.font = game.getAssetManager().get("resources/fonts/default.fnt", BitmapFont.class);
		tButtonStyle.fontColor = Color.WHITE;
		
		skin = new Skin();
		skin.add("default", new Label.LabelStyle(game.getAssetManager().get("resources/fonts/default.fnt", BitmapFont.class), Color.WHITE));
		skin.add("default", tButtonStyle);
	}

	@Override
	public void show() {
		super.show();

        // retrieve the default table actor
		Table table = super.getTable();
        table.setSkin(skin);
        table.add( "Nanotransen wecomes you aboard!" ).spaceBottom( 50 );
        table.row();
        
        // register the button "start game"
        TextButton editMapsButton = new TextButton( "Edit Maps", skin );
        editMapsButton.addListener( new ClickListener() {
            @Override
            public void touchUp(
                InputEvent event,
                float x,
                float y,
                int pointer,
                int button )
            {
                super.touchUp( event, x, y, pointer, button );
                try {
					game.setScreen( new GridEditor( game ) );
				} catch (Exception e) { e.printStackTrace(); }
            }
        } );
        table.add( editMapsButton ).size( 256, 64 ).uniform().spaceBottom( 10 );
	}
}