import java.util.concurrent.atomic.AtomicReference;

public class MCS implements Lock {
    private final AtomicReference<MCSNode> tail = new AtomicReference<>(null);
    private final ThreadLocal<MCSNode> myNode = ThreadLocal.withInitial(MCSNode::new);

    public void lock() {
        MCSNode node = myNode.get();
        node.next = null;
        node.locked = true;

        MCSNode pred = tail.getAndSet(node);
        
        if (pred != null) 
        {
            pred.next = node;
            while (node.locked) 
            {
                Thread.yield();
            }
        }
    }

    public void unlock() {
        MCSNode node = myNode.get();
        
        if (node.next == null) 
        {
            if (tail.compareAndSet(node, null)) 
            {
                return; 
            }
            while (node.next == null) {
                Thread.yield();
            }
        }
        
        node.next.locked = false;
        node.next = null; 
    }
}