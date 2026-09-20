public class MCSNode {
    public volatile boolean locked = false;
    public volatile MCSNode next = null;
}