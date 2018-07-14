package spacestation13;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2.Settings;

public class SpaceStation13_Desktop {
	public static void main (String[] argv) {
		Settings settings = new Settings();
        settings.maxWidth = 1024;
        settings.maxHeight = 1024;
        TexturePacker2.process(settings, "resources/tilesets/standard/floors", "resources/tilesets/standard/atlases", "floors");
        TexturePacker2.process(settings, "resources/images/ui", "resources/images/atlases", "ui");

        new LwjglApplication(new SS13(), "Space Station 13", 800, 600, false);
	}

}
