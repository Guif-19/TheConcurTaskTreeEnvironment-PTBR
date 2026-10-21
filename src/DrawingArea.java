import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseWheelEvent;
import java.util.Stack;



import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.tree.TreeNode;
//import javax.swing.event.MouseInputListener;

import TaskTree.TaskNode;
import TaskTree.enums.Category;
import TaskTree.enums.Links;


enum MouseState{
	IDLE, SELECT, DRAG, TRANSLATE
}

public class DrawingArea extends JPanel implements MouseMotionListener, MouseListener, MouseWheelListener{
	private TaskNode root, selectedNode;
    private TaskNode clipboard;

    public TaskNode getRoot() { return root; }
    public TaskNode getSelectedNode() { return selectedNode; }
    public void clearSelection() {
        selectedNode = null; state = MouseState.IDLE;
        if (MainFrame.taskInfos != null) MainFrame.taskInfos.setEnabled(false);
        if (MainFrame.taskinfoframe != null) MainFrame.taskinfoframe.setVisible(false);
    }
    public void replaceRoot(TaskNode node) {
        clearSelection(); root = node; viderUndo(); viderRedo(); repaint();
    }
    public void copySelection() {
        if (selectedNode == null) {
            javax.swing.JOptionPane.showMessageDialog(this,"Selecione uma tarefa para copiar."); return;
        }
        clipboard = selectedNode.cloneTree();
    }
    public void cutSelection() {
        if (selectedNode == null || selectedNode == root) {
            javax.swing.JOptionPane.showMessageDialog(this,"Selecione uma tarefa que não seja a raiz para recortar."); return;
        }
        copySelection(); empilerUndo(); viderRedo();
        selectedNode.DeleteNode(); clearSelection(); repaint();
    }
    public void pasteSelection() {
        if (clipboard == null || selectedNode == null) {
            javax.swing.JOptionPane.showMessageDialog(this,"Copie uma tarefa e selecione onde colar como subtarefa."); return;
        }
        empilerUndo(); viderRedo();
        TaskNode copy = clipboard.cloneTree();
        int dx = selectedNode.getTaskPoint().x + selectedNode.getChildCount()*60 - copy.getTaskPoint().x;
        int dy = selectedNode.getTaskPoint().y + 120 - copy.getTaskPoint().y;
        copy.setTaskPoint(new Point(copy.getTaskPoint().x+dx,copy.getTaskPoint().y+dy));
        TranslateTasks(copy,dx,dy);
        selectedNode.add(copy); repaint();
    }

	private int x, y, H, W, scale, x_temp, y_temp;
	private MouseState state;
	private Stack<TaskNode> undo, redo;
	private JLabel info;
	private Point pCour;
	private boolean addingChild;
	private final ImageIcon abstractionTask = new ImageIcon(getClass().getResource("images/abstractionTask.png"));
	private final ImageIcon addAbstractionTask = new ImageIcon(getClass().getResource("images/addAbstractionTask.png"));
	private final ImageIcon addApplicationTask = new ImageIcon(getClass().getResource("images/addApplicationTask.png"));
	private final ImageIcon addChoiceLink = new ImageIcon(getClass().getResource("images/addChoiceLink.png"));
	private final ImageIcon addInteractionTask = new ImageIcon(getClass().getResource("images/addInteractionTask.png"));
	private final ImageIcon addLink = new ImageIcon(getClass().getResource("images/addLink.png"));
	private final ImageIcon addOrderIndependanceLink = new ImageIcon(getClass().getResource("images/addOrderIndependanceLink.png"));
	private final ImageIcon addTask = new ImageIcon(getClass().getResource("images/addTask.png"));
	private final ImageIcon addUserTask = new ImageIcon(getClass().getResource("images/addUserTask.png"));
	private final ImageIcon applicationTask = new ImageIcon(getClass().getResource("images/applicationTask.png"));
	private final ImageIcon editTask = new ImageIcon(getClass().getResource("images/editTask.png"));
	private final ImageIcon interactionTask = new ImageIcon(getClass().getResource("images/interactionTask.png"));
	private final ImageIcon removeLink = new ImageIcon(getClass().getResource("images/removeLink.png"));
	private final ImageIcon removeTask = new ImageIcon(getClass().getResource("images/removeTask.png"));
	private final ImageIcon userTask = new ImageIcon(getClass().getResource("images/userTask.png"));
	private final ImageIcon addChild = new ImageIcon(getClass().getResource("images/addChild.png"));
	private final ImageIcon addSibling = new ImageIcon(getClass().getResource("images/addSibling.png"));
	
	DrawingArea() {
		this.addMouseMotionListener(this);
		this.addMouseListener(this);
		this.addMouseWheelListener(this);
		this.x = 0;
		this.y = 0;
		this.W = 798;
		this.H = 747;
		this.scale = 120;
		this.root = new TaskNode("Raiz", Category.USER, new Point(this.H / 2, 100));
		this.selectedNode = null;
		this.addingChild = true;
		this.info = new JLabel();
		this.add(info);
		state = MouseState.IDLE;
		this.undo = new Stack<TaskNode>();
		this.redo = new Stack<TaskNode>();
	}
	
	private boolean insideArea(TaskNode node) {
		int x = (int) node.getTaskPoint().getX();
		int y = (int) node.getTaskPoint().getY();
		
		if(x >= (this.x - this.scale) && x <= (this.x + this.W + this.scale) 
				&& (y >= this.y  - this.scale) && y <= (this.y + this.H + this.scale))
			return true;
		return false;
	}
	
	private void paintAddTaskButtons(Point p, Graphics2D g2) {
		g2.setColor(Color.GRAY);
		g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.3f));
		g2.fillOval((int) p.getX() - 75, (int) p.getY()  - 75, 
				60, 60);
		g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1.0f));
		g2.drawImage(this.addTask.getImage(), (int) p.getX() - 60,
				(int) p.getY()  - 60, null);
		g2.drawImage(this.addUserTask.getImage(), (int) p.getX() - 70,
				(int) p.getY()  - 90, null);
		g2.drawImage(this.addAbstractionTask.getImage(), (int) p.getX() - 40,
				(int) p.getY()  - 90, null);
		g2.drawImage(this.addApplicationTask.getImage(), (int) p.getX() - 90,
				(int) p.getY()  - 60, null);
		g2.drawImage(this.addInteractionTask.getImage(), (int) p.getX() - 90,
				(int) p.getY()  - 30, null);
		if(this.addingChild)
			g2.drawImage(this.addChild.getImage(), (int) p.getX() - 30,
					(int) p.getY()  - 30, null);
		else
			g2.drawImage(this.addSibling.getImage(), (int) p.getX() - 30,
					(int) p.getY()  - 30, null);
	}
	
	private void paintAddLinkButtons(Point p, Graphics2D g2) {
		g2.setColor(Color.GRAY);
		g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.3f));
		g2.fillOval((int) p.getX() + 15, (int) p.getY()  - 70, 
				50, 50);
		g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1.0f));
		g2.drawImage(this.addLink.getImage(), (int) p.getX() + 25,
				(int) p.getY()  - 60, null);
		g2.drawImage(this.addChoiceLink.getImage(), (int) p.getX() + 50,
				(int) p.getY()  - 70, null);
		g2.drawImage(this.removeLink.getImage(), (int) p.getX() + 10,
				(int) p.getY()  - 80, null);
		g2.drawImage(this.addOrderIndependanceLink.getImage(), (int) p.getX() + 55,
				(int) p.getY()  - 35, null);
	}
	
	private void paintEditButton(Point p, Graphics2D g2) {
		g2.drawImage(this.editTask.getImage(), (int) p.getX() - 60, 
				(int) p.getY()  + 30, null);
	}
	
	private void paintDeleteButton(Point p, Graphics2D g2) {
		g2.drawImage(this.removeTask.getImage(), (int) p.getX() + 30, 
				(int) p.getY()  + 30, null);
	}
	
	private void paintCircle(Point p, Graphics2D g2) {
		g2.setColor(Color.GRAY);
		g2.fillOval((int) p.getX() - 50, (int) p.getY()  - 50, 
				100, 100);
	}
	
	private void paintButtons(Point p, Graphics2D g2) {
		g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.3f));
		paintCircle(p, g2);
		g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1.0f));
		paintAddTaskButtons(p, g2);
		paintAddLinkButtons(p, g2);
		paintEditButton(p, g2);
		paintDeleteButton(p, g2);
	}
	
	private void paintLink(TaskNode node, TaskNode child, Graphics2D g2) {
		g2.drawLine((int) node.getTaskPoint().getX(), (int) node.getTaskPoint().getY(),
				(int) child.getTaskPoint().getX(), (int) child.getTaskPoint().getY());
	}
	
	private void paintBrotherLink(TaskNode node, Graphics2D g2) {
		TaskNode brother = (TaskNode) node.getParent().getChildAfter((TreeNode) node);
		if(node.getLinkD() != null && brother != null) {
			switch(node.getLinkD()) {
				case CHOICE:
					g2.setColor(Color.GREEN);
					break;
				case ORDERINDEPENDANCE:
					g2.setColor(Color.BLUE);
					break;
				default: break;
			}
			paintLink(node, brother, g2);
			g2.setColor(Color.BLACK);
		}
	}
	
	private void paintTitle(TaskNode node, Graphics2D g2) {
		g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1.0f));
		String texte = node.getTaskName();
		int tailleTexte = texte.length();
		// 4 c -> 15
		// 1 c -> 3.75 -> 3.35 un peu plus propre 
		/*g2.drawString(node.getTaskName().substring(0, Math.min(node.getTaskName().length(), 5)),
				(int) node.getTaskPoint().getX() - 15,
				(int) node.getTaskPoint().getY() + 32);*/
		g2.drawString(texte, (int) (node.getTaskPoint().getX() - (3.35*tailleTexte)),
							(int) node.getTaskPoint().getY() + 32);
	}
	
	private void paintTask(TaskNode node, Graphics2D g2) {
		if(insideArea(node)) {
			switch(node.getCategory()) {
				case USER:
					g2.drawImage(this.userTask.getImage(), (int) node.getTaskPoint().getX() - 20, 
							(int) node.getTaskPoint().getY()  - 20, null);
					break;
				case ABSTRACTION:
					g2.drawImage(this.abstractionTask.getImage(), (int) node.getTaskPoint().getX() - 20, 
							(int) node.getTaskPoint().getY()  - 20, null);
					break;
				case APPLICATION:
					g2.drawImage(this.applicationTask.getImage(), (int) node.getTaskPoint().getX() - 20, 
							(int) node.getTaskPoint().getY()  - 20, null);
					break;
				case INTERACTION:
					g2.drawImage(this.interactionTask.getImage(), (int) node.getTaskPoint().getX() - 20, 
							(int) node.getTaskPoint().getY()  - 20, null);
					break;
				default: break;
			}
			paintTitle(node, g2);
		}
	}
	

	
	private void paintTasks(TaskNode node, Graphics2D g2) {		
		for(int i = 0; i < node.getChildCount(); i++) {
			paintLink(node, node.getChildAt(i), g2);
			paintBrotherLink(node.getChildAt(i), g2);
			paintTasks(node.getChildAt(i), g2);
		}
		
		if(node != null)
			paintTask(node, g2);
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		W = getWidth();
		H = getHeight();
		
		Graphics2D g2 = (Graphics2D)g;
		
		// background
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, W, H);
		
		g2.setColor(Color.BLACK);
		paintTasks(this.root, g2);
		
		if(this.selectedNode != null) {
			paintButtons(selectedNode.getTaskPoint(), g2);
			paintTask(this.selectedNode, g2);
		}
	}
	
	private boolean isOnAddUserTaskButton(Point p1, Point p2) {
		// si on a clique sur le bouton add user task
		int x1 = (int) p1.getX();
		int y1 = (int) p1.getY();
		int x2 = (int) p2.getX();
		int y2 = (int) p2.getY();
		if(x2 >= (x1 - 70) && x2 <= (x1 - 50)
				&& y2 >= (y1 - 90) && y2 <= (y1 - 70))
			return true;
		return false;
	}
	
	private boolean isOnAddAbstractionTaskButton(Point p1, Point p2) {
		int x1 = (int) p1.getX();
		int y1 = (int) p1.getY();
		int x2 = (int) p2.getX();
		int y2 = (int) p2.getY();
		if(x2 >= (x1 - 40) && x2 <= (x1 - 20)
				&& y2 >= (y1 - 90) && y2 <= (y1 - 70))
			return true;
		return false;
	}
	
	private boolean isOnAddApplicationTaskButton(Point p1, Point p2) {
		int x1 = (int) p1.getX();
		int y1 = (int) p1.getY();
		int x2 = (int) p2.getX();
		int y2 = (int) p2.getY();
		if(x2 >= (x1 - 90) && x2 <= (x1 - 70)
				&& y2 >= (y1 - 60) && y2 <= (y1 - 40))
			return true;
		return false;
	}
	
	private boolean isOnAddInteractionTaskButton(Point p1, Point p2) {
		int x1 = (int) p1.getX();
		int y1 = (int) p1.getY();
		int x2 = (int) p2.getX();
		int y2 = (int) p2.getY();
		if(x2 >= (x1 - 90) && x2 <= (x1 - 70)
				&& y2 >= (y1 - 30) && y2 <= (y1 - 10))
			return true;
		return false;
	}
	
	private boolean isOnChangeAddingModeTaskButton(Point p1, Point p2) {
		int x1 = (int) p1.getX();
		int y1 = (int) p1.getY();
		int x2 = (int) p2.getX();
		int y2 = (int) p2.getY();
		if(x2 >= (x1 - 30) && x2 <= (x1 - 15)
				&& y2 >= (y1 - 30) && y2 <= (y1 - 15))
			return true;
		return false;
	}
	
	private boolean isOnAddChoiceLinkButton(Point p1, Point p2) {
		int x1 = (int) p1.getX();
		int y1 = (int) p1.getY();
		int x2 = (int) p2.getX();
		int y2 = (int) p2.getY();
		if(x2 >= (x1 + 50) && x2 <= (x1 + 70)
				&& y2 >= (y1 - 70) && y2 <= (y1 - 50))
			return true;
		return false;
	}
	
	private boolean isOnRemoveLinkButton(Point p1, Point p2) {
		int x1 = (int) p1.getX();
		int y1 = (int) p1.getY();
		int x2 = (int) p2.getX();
		int y2 = (int) p2.getY();
		if(x2 >= (x1 + 10) && x2 <= (x1 + 30)
				&& y2 >= (y1 - 80) && y2 <= (y1 - 60))
			return true;
		return false;
	}
	
	private boolean isOnAddOrderLinkButton(Point p1, Point p2) {
		int x1 = (int) p1.getX();
		int y1 = (int) p1.getY();
		int x2 = (int) p2.getX();
		int y2 = (int) p2.getY();
		if(x2 >= (x1 + 55) && x2 <= (x1 + 75)
				&& y2 >= (y1 - 35) && y2 <= (y1 - 15))
			return true;
		return false;
	}

	private boolean isOnRemoveTaskButton(Point p1, Point p2){
		int x1 = (int) p1.getX();
		int y1 = (int) p1.getY();
		int x2 = (int) p2.getX();
		int y2 = (int) p2.getY();
		if(x2 >= (x1 + 30) && x2 <= (x1 + 60)
				&& y2 >= (y1 + 30) && y2 <= (y1 + 60))
			return true;
		return false;
	}
	
	private boolean isOnEditTaskButton(Point p1, Point p2){
		int x1 = (int) p1.getX();
		int y1 = (int) p1.getY();
		int x2 = (int) p2.getX();
		int y2 = (int) p2.getY();
		if(x2 >= (x1 - 60) && x2 <= (x1 - 30)
				&& y2 >= (y1 + 30) && y2 <= (y1 + 60))
			return true;
		return false;
	}
	
	
	
	private boolean isOnTask(Point p1, Point p2) {
		int x1 = (int) p1.getX();
		int y1 = (int) p1.getY();
		int x2 = (int) p2.getX();
		int y2 = (int) p2.getY();
		if(x1 >= (x2 - 20) && x1 <= (x2 + 20) 
				&& y1 >= (y2  - 20) && y1 <= (y2 + 20))
			return true;
		return false;
	}
	
	private TaskNode isOnTask(TaskNode node, Point p) {
		TaskNode temp;
		TaskNode n = null;
		
		if(node != null) {
			if(isOnTask(p, node.getTaskPoint()))
				n = node;
			else {
				for(int i = 0; i < node.getChildCount(); i++) {
					temp = isOnTask(node.getChildAt(i), p);
					if(temp != null) {
						n = temp;
						break;
					}
				}
			}
		}
		return n;
	}
	
	
	private void ajouterFils(TaskNode pere, Category category){
		if(pere.isLeaf()){
			// ajout simple du premier fils
			pere.addChild("Tarefa", category, new Point(
				(int) pere.getTaskPoint().getX(), 
				(int) pere.getTaskPoint().getY() + this.scale));
		}else{
			// ajout d'un nouveau fils a droite des autres
			pere.addChild("Tarefa", category, new Point(
					(int) pere.getTaskPoint().getX(), 
					(int) pere.getTaskPoint().getY() + this.scale));
			alignerFils(pere);
		}
		
	}
	
	private void ajouterFrere(TaskNode node, Category category){
		if(!node.isRoot()){
			node.addBrother("Tarefa", category, new Point(
					(int) node.getTaskPoint().getX() + this.scale, 
					(int) node.getTaskPoint().getY()));
			alignerFils(node.getParent());
		}
		
	}
	
	public void alignerFils(TaskNode pere){
		int nbFils = pere.getChildCount();
		TaskNode curTask;
		int mid = (this.scale*(nbFils-1))/2;
		for(int i=0;i<nbFils;i++){
			curTask=pere.getChildAt(i);
			curTask.setTaskPoint(new Point(
					(int) pere.getTaskPoint().getX()- mid + (this.scale*i),
					(int) pere.getTaskPoint().getY() + this.scale));
			alignerFils(curTask);
		}
	}
	
	private void setLink(TaskNode node, Links link) {
		node.setLinksD(link);
	}
	
	public void supprimerNoeud(TaskNode node){
		node.DeleteNode();
		clearSelection();
	}
	
	public void editerNoeud(TaskNode node){
        MainFrame.taskinfoframe.changeTaskNodeProperties(node);
		MainFrame.taskinfoframe.setVisible(true);
	}
	
	/* undo redo */
	public void empilerUndo(){
		undo.push(root.cloneTree());
		ButtonListComposant.undo.setEnabled(true);
		MainFrame.undo.setEnabled(true);
	}
	public TaskNode depilerUndo(){
		return undo.pop();
	}
	
	public void viderUndo(){
		undo.clear();
		ButtonListComposant.undo.setEnabled(false);
		MainFrame.undo.setEnabled(false);
	}
	
	public void Undo(){
        if (undo.empty()) return;
        clearSelection();
		empilerRedo();
		root = depilerUndo();
		selectedNode=null;
		state = MouseState.IDLE;
		if(undo.empty()){
			ButtonListComposant.undo.setEnabled(false);
			MainFrame.undo.setEnabled(false);
		}
		repaint();
	}
	
	
	public void empilerRedo(){
		redo.push(root.cloneTree());
		ButtonListComposant.redo.setEnabled(true);
		MainFrame.redo.setEnabled(true);
	}
	public TaskNode depilerRedo(){
		return redo.pop();
	}
	
	public void viderRedo(){
		redo.clear();
		ButtonListComposant.redo.setEnabled(false);
		MainFrame.redo.setEnabled(false);
	}
	public void Redo(){
        if (redo.empty()) return;
        clearSelection();
		empilerUndo();
		root = depilerRedo();
		selectedNode=null;
		state = MouseState.IDLE;
		if(redo.empty()){
			ButtonListComposant.redo.setEnabled(false);
			MainFrame.redo.setEnabled(false);
		}
		repaint();
	}
	
	/* fin undo redo */
	
	public void newTaskRoot(){
        clearSelection();
		this.root = new TaskNode("Raiz", Category.USER, new Point(this.H / 2, 100));
		this.selectedNode=null;
		state = MouseState.IDLE;
		this.undo = new Stack<TaskNode>();
		this.redo = new Stack<TaskNode>();
		ButtonListComposant.redo.setEnabled(false);
		ButtonListComposant.undo.setEnabled(false);
		MainFrame.redo.setEnabled(false);
		MainFrame.undo.setEnabled(false);
		repaint();
	}
	
	public void Translate(Point pOld, Point pNew){
		int transX = (int)(0.9*(pNew.getX()-pOld.getX()));
		int transY = (int)(0.9*(pNew.getY()-pOld.getY()));
		root.setTaskPoint(new Point(
				(int) root.getTaskPoint().getX() + transX,
				(int) root.getTaskPoint().getY() + transY));
		TranslateTasks(root,transX,transY);
	}
	
	public void TranslateTasks(TaskNode pere, int transX, int transY){
		int nbFils = pere.getChildCount();
		TaskNode curTask;
		for(int i=0;i<nbFils;i++){
			curTask=pere.getChildAt(i);
			curTask.setTaskPoint(new Point(
					(int) curTask.getTaskPoint().getX() + transX,
					(int) curTask.getTaskPoint().getY() + transY));
			TranslateTasks(curTask,transX,transY);
		}
	}
	
	
	/** Listeners **/
	@Override
	public void mouseClicked(MouseEvent arg0) {}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mousePressed(MouseEvent arg0) {
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		switch(state){
			case IDLE:
				this.selectedNode = isOnTask(this.root, arg0.getPoint());
				if(this.selectedNode!=null){
					// selection d'un noeud
					state=MouseState.SELECT;
					MainFrame.taskinfoframe.changeTaskNodeProperties(selectedNode);
					MainFrame.taskInfos.setEnabled(true);
					this.x_temp = arg0.getX();
					this.y_temp = arg0.getY();
					if(this.selectedNode==root){
						this.addingChild=true;
					}
				}
				pCour=arg0.getPoint();
				break;
			case SELECT:
				if(isOnAddUserTaskButton(this.selectedNode.getTaskPoint(), arg0.getPoint())){
					// ajout d'un fils
					empilerUndo();
					viderRedo();
					if(this.addingChild)
						ajouterFils(this.selectedNode, Category.USER);
					else
						ajouterFrere(this.selectedNode, Category.USER);
				}
				else if(isOnAddAbstractionTaskButton(this.selectedNode.getTaskPoint(), arg0.getPoint())){
					empilerUndo();
					viderRedo();
					if(this.addingChild)
						ajouterFils(this.selectedNode, Category.ABSTRACTION);
					else
						ajouterFrere(this.selectedNode, Category.ABSTRACTION);
				}
				else if(isOnAddApplicationTaskButton(this.selectedNode.getTaskPoint(), arg0.getPoint())){
					empilerUndo();
					viderRedo();
					if(this.addingChild)
						ajouterFils(this.selectedNode, Category.APPLICATION);
					else
						ajouterFrere(this.selectedNode, Category.APPLICATION);
				}
				else if(isOnAddInteractionTaskButton(this.selectedNode.getTaskPoint(), arg0.getPoint())){
					empilerUndo();
					viderRedo();
					if(this.addingChild)
						ajouterFils(this.selectedNode, Category.INTERACTION);
					else
						ajouterFrere(this.selectedNode, Category.INTERACTION);
				}
				else if(isOnChangeAddingModeTaskButton(this.selectedNode.getTaskPoint(), arg0.getPoint())){
					if(this.selectedNode!=root){
						this.addingChild = !this.addingChild;
					}
				}
				else if(isOnRemoveTaskButton(this.selectedNode.getTaskPoint(), arg0.getPoint())){
					empilerUndo();
					viderRedo();
					supprimerNoeud(this.selectedNode);
					state = MouseState.IDLE;
					break;
				}
				else if(isOnAddChoiceLinkButton(this.selectedNode.getTaskPoint(), arg0.getPoint())){
					empilerUndo();
					viderRedo();
					setLink(this.selectedNode, Links.CHOICE);
				}
				else if(isOnAddOrderLinkButton(this.selectedNode.getTaskPoint(), arg0.getPoint())){
					empilerUndo();
					viderRedo();
					setLink(this.selectedNode, Links.ORDERINDEPENDANCE);
				}
				else if(isOnRemoveLinkButton(this.selectedNode.getTaskPoint(), arg0.getPoint())){
					empilerUndo();
					viderRedo();
					this.selectedNode.setLinksD(null);
				}
				else if(isOnEditTaskButton(this.selectedNode.getTaskPoint(), arg0.getPoint())){
					editerNoeud(this.selectedNode);
					break;
				}
				else{
					// on a clique ailleurs
					this.selectedNode = isOnTask(this.root, arg0.getPoint());
					if(this.selectedNode!=null){
						// selection d'un noeud
						MainFrame.taskinfoframe.changeTaskNodeProperties(selectedNode);
						this.x_temp = arg0.getX();
						this.y_temp = arg0.getY();
						if(this.selectedNode==root){
							this.addingChild=true;
						}
						break;
					}
					// si on clique dans le vide, on deselectionne
					pCour=arg0.getPoint();
					state=MouseState.IDLE;
					MainFrame.taskInfos.setEnabled(false);
				}
				break;
			default:
				break;		
		}
		repaint();
		
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		switch(state){
			case DRAG:
				state=MouseState.SELECT;	
				break;
			case TRANSLATE:
				state=MouseState.IDLE;
				break;
			default:
				break;
		}
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		
		switch(state){
			case IDLE:
				empilerUndo();
                viderRedo();
                state=MouseState.TRANSLATE;
				Translate(pCour,arg0.getPoint());
				pCour=arg0.getPoint();
				setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
				repaint();
				break;
			case TRANSLATE:
				Translate(pCour,arg0.getPoint());
				pCour=arg0.getPoint();
				repaint();
				break;
			case SELECT:
				if(arg0.getX() != this.x_temp || arg0.getY() != this.y_temp) {
					empilerUndo();
					viderRedo();
					this.x_temp = arg0.getX();
					this.y_temp = arg0.getY();
					this.selectedNode.setTaskPoint(arg0.getPoint());
					setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
					repaint();
					state=MouseState.DRAG;
				}
				break;
			case DRAG:
				if(arg0.getX() != this.x_temp || arg0.getY() != this.y_temp) {
					this.x_temp = arg0.getX();
					this.y_temp = arg0.getY();
					this.selectedNode.setTaskPoint(arg0.getPoint());
					repaint();
				}
				break;
			default:
				break;
		}
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		this.info.setText("");
		if(this.state.equals(MouseState.SELECT)) {
			if(isOnAddUserTaskButton(this.selectedNode.getTaskPoint(), arg0.getPoint())){
				this.info.setText("Adicionar tarefa do usuário");
			}
			else if(isOnAddAbstractionTaskButton(this.selectedNode.getTaskPoint(), arg0.getPoint())){
				this.info.setText("Adicionar tarefa abstrata");
			}
			else if(isOnAddApplicationTaskButton(this.selectedNode.getTaskPoint(), arg0.getPoint())){
				this.info.setText("Adicionar tarefa da aplicação");
			}
			else if(isOnAddInteractionTaskButton(this.selectedNode.getTaskPoint(), arg0.getPoint())){
				this.info.setText("Adicionar tarefa de interação");
			}
			else if(isOnChangeAddingModeTaskButton(this.selectedNode.getTaskPoint(), arg0.getPoint())){
				if(this.addingChild)
					this.info.setText("Alternar para adicionar tarefa irmã");
				else
					this.info.setText("Alternar para adicionar subtarefa");
			}
			else if(isOnRemoveTaskButton(this.selectedNode.getTaskPoint(), arg0.getPoint())){
				this.info.setText("Remover tarefa");
			}
			else if(isOnAddChoiceLinkButton(this.selectedNode.getTaskPoint(), arg0.getPoint())){
				this.info.setText("Adicionar relação de escolha");
			}
			else if(isOnAddOrderLinkButton(this.selectedNode.getTaskPoint(), arg0.getPoint())){
				this.info.setText("Adicionar relação de ordem independente");
			}
			else if(isOnRemoveLinkButton(this.selectedNode.getTaskPoint(), arg0.getPoint())){
				this.info.setText("Remover relação");
			}
			else if(isOnEditTaskButton(this.selectedNode.getTaskPoint(), arg0.getPoint())){
				this.info.setText("Editar tarefa");
			}
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		empilerUndo();
        viderRedo();
        this.scale -= 4*arg0.getWheelRotation();
		this.scale = Math.max(50, this.scale);
		alignerFils(this.root);
		repaint();
	}
}
