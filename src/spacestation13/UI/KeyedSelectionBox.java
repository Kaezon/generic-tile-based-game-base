package spacestation13.UI;

import java.util.HashMap;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class KeyedSelectionBox<E> extends SelectBox{

	HashMap<String, E> hm = new HashMap<String, E>();
	
	public KeyedSelectionBox(E[] items, Skin skin) {
		super(items, skin);
		for (E o : items) {
			hm.put(o.toString(), o);
		}
	}
	
	public E GetSelectionValue() {
		return hm.get(this.getSelection());
	}

}
