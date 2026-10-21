package TaskTree;

import java.awt.Point;

import TaskTree.enums.Category;
import TaskTree.enums.Links;


class Task  {
	
	private Category c;
	private Links linkG, linkD;
	private String TaskName;
	private Point p;
	
	
	Task(String TaskName, Category c, Point p){
		this.TaskName=TaskName;
		this.c=c;
		this.p=p;
		this.linkD = null;
		this.linkG = null;
	}
	
	/** Getters **/
	Point getPoint(){
		return p;
	}
	
	String getName(){
		return TaskName;
	}
	
	Links getLinkG(){
		return linkG;
	}
	
	Links getLinkD(){
		return linkD;
	}
	
	Category getCategory(){
		return c;
	}
	
	/** Setters **/
	void setPoint(Point p){
		this.p=p;
	}
	
	void setName(String TaskName){
		this.TaskName=TaskName;
	}
	
	void setLinkG(Links linkG){
		this.linkG=linkG;
	}
	
	void setLinkD(Links linkD){
		this.linkD=linkD;
	}
	
	void setCategory(Category c){
		this.c=c;
	}
}
