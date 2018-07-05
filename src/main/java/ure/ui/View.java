package ure.ui;

import org.lwjgl.system.CallbackI;
import ure.render.URERenderer;

import java.util.HashSet;
import java.util.Set;

public class View {

    int x, y, width, height;
    // not used yet, but eventually we'll take these into account so our own draw method can use local coordinates

    private Set<View> children = new HashSet<>();

    public Set<View> children() {
        return children;
    }

    public void addChild(View view) {
        children.add(view);
    }

    public void removeChild(View view) {
        children.remove(view);
    }

    public void draw(URERenderer renderer) {
        // Do any drawing required for this view, then draw children
        for (View view : children) {
            view.draw(renderer);
        }
    }

}
