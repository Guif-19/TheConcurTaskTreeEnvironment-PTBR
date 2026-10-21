package fr.imag.ufrima.ihm.projet.core.interaction;

import java.awt.Point;

public interface MouseInteraction {

	/** 
	 * Hides the circular menu
	 */
	public void hideCircularMenu();
	
	/**
	 * Shows the circular menu
	 */
	public void showCircularMenu();
	
	/**
	 * Moves a subtree
	 */
	public void moveSubTree(Point clickPoint);
	
	/**
	 * Zooms 
	 */
	public void zoom(double scale);
}
