package ure.sys.events;

public class ResolutionChangedEvent {

    public int width;
    public int height;

    public ResolutionChangedEvent(int width, int height) {
        this.width = width;
        this.height = height;
    }
}
