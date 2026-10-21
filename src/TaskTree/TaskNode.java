package TaskTree;
import java.awt.Point;
import javax.swing.tree.DefaultMutableTreeNode;
import TaskTree.enums.Category;
import TaskTree.enums.Links;
public class TaskNode extends DefaultMutableTreeNode {
    public TaskNode(String name, Category c, Point p) { super(new Task(name,c,p)); }
    private Task task() { return (Task)getUserObject(); }
    public Point getTaskPoint() { return task().getPoint(); }
    public String getTaskName() { return task().getName(); }
    public Category getCategory() { return task().getCategory(); }
    public Links getLinkG() { return task().getLinkG(); }
    public Links getLinkD() { return task().getLinkD(); }
    public TaskNode getParent() { return (TaskNode)super.getParent(); }
    public TaskNode getChildAt(int index) { return (TaskNode)super.getChildAt(index); }
    public void setTaskName(String name) { task().setName(name); }
    public void setCategory(Category c) { task().setCategory(c); }
    public void setTaskPoint(Point p) { task().setPoint(p); }
    public void setLinksG(Links link) {
        TaskNode previous = (TaskNode)getPreviousSibling();
        task().setLinkG(previous == null ? null : link);
        if (previous != null) previous.task().setLinkD(link);
    }
    public void setLinksD(Links link) {
        TaskNode next = (TaskNode)getNextSibling();
        task().setLinkD(next == null ? null : link);
        if (next != null) next.task().setLinkG(link);
    }
    public TaskNode addChild(String name, Category c, Point p) {
        TaskNode child = new TaskNode(name,c,p); add(child); return child;
    }
    public TaskNode addBrother(String name, Category c, Point p) {
        TaskNode parent = getParent();
        if (parent == null) return null;
        TaskNode child = new TaskNode(name,c,p);
        setLinksG(null);
        parent.insert(child,parent.getIndex(this)); return child;
    }
    public void DeleteNode() {
        setLinksG(null); setLinksD(null);
        if (getParent() != null) removeFromParent(); else removeAllChildren();
    }
    public TaskNode cloneTree() {
        TaskNode copy = new TaskNode(getTaskName(),getCategory(),new Point(getTaskPoint()));
        for (int i=0;i<getChildCount();i++) copy.add(getChildAt(i).cloneTree());
        for (int i=0;i<getChildCount()-1;i++) copy.getChildAt(i).setLinksD(getChildAt(i).getLinkD());
        return copy;
    }
}
