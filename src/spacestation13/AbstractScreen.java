package spacestation13;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * The base class for all game screens.
 */
public abstract class AbstractScreen implements Screen {

    protected final SS13 game;
    protected final Stage stage;

    private BitmapFont font;
    private SpriteBatch batch;
    private Skin skin;
    private Table table;

    public AbstractScreen(SS13 game)
    {
        this.game = game;
        this.stage = new Stage(SS13.VIRTUAL_WIDTH, SS13.VIRTUAL_HEIGHT, false);
    }

    protected String getName()
    {
        return getClass().getSimpleName();
    }

    protected boolean isGameScreen()
    {
        return false;
    }

    // Lazily loaded collaborators

    public BitmapFont getFont()
    {
        if( font == null ) {
            font = new BitmapFont();
        }
        return font;
    }

    public SpriteBatch getBatch()
    {
        if( batch == null ) {
            batch = new SpriteBatch();
        }
        return batch;
    }
    
    protected Skin getSkin()
    {
        if( skin == null ) {
            FileHandle skinFile = Gdx.files.internal( "skin/uiskin.json" );
            skin = new Skin( skinFile );
        }
        return skin;
    }

    protected Table getTable()
    {
        if( table == null ) {
        	table = new Table();
            table.setFillParent( true );
            if( SS13.DEV_MODE ) {
                table.debug();
            }
            stage.addActor( table );
        }
        return table;
    }

    // Screen implementation

    @Override
    public void show()
    {
        Gdx.app.log( SS13.LOG, "Showing screen: " + getName() );

        // set the stage as the input processor
        Gdx.input.setInputProcessor( stage );
    }

    @Override
    public void resize(int width, int height)
    {
        Gdx.app.log( SS13.LOG, "Resizing screen: " + getName() + " to: " + width + " x " + height );
    }

    @Override
    public void render(float delta)
    {
        // (1) process the game logic

        // update the actors
        stage.act( delta );

        // (2) draw the result

        // clear the screen with the given RGB color (black)
        Gdx.gl.glClearColor( 0f, 0f, 0f, 1f );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );

        // draw the actors
        stage.draw();

        // draw the table debug lines
        Table.drawDebug( stage );
    }

    @Override
    public void hide()
    {
        Gdx.app.log( SS13.LOG, "Hiding screen: " + getName() );

        // dispose the screen when leaving the screen;
        // note that the dipose() method is not called automatically by the
        // framework, so we must figure out when it's appropriate to call it
        dispose();
    }

    @Override
    public void pause()
    {
        Gdx.app.log( SS13.LOG, "Pausing screen: " + getName() );
    }

    @Override
    public void resume()
    {
        Gdx.app.log( SS13.LOG, "Resuming screen: " + getName() );
    }

    @Override
    public void dispose()
    {
        Gdx.app.log( SS13.LOG, "Disposing screen: " + getName() );

        // the following call disposes the screen's stage, but on my computer it
        // crashes the game so I commented it out; more info can be found at:
        // http://www.badlogicgames.com/forum/viewtopic.php?f=11&t=3624
        // stage.dispose();

        // as the collaborators are lazily loaded, they may be null
        if( font != null ) font.dispose();
        if( batch != null ) batch.dispose();
    }
}