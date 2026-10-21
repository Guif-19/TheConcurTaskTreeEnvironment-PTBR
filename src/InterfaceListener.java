import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class InterfaceListener implements ActionListener {
		int num; // 0 = undo, 1 = redo, 2 = new
		DrawingArea da;
		
		
		InterfaceListener(int num, DrawingArea da){
			this.num = num;
			this.da = da;
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			if(num==0){
				// undo
				da.Undo();
			}
			else if (num == 1){
				// redo
				da.Redo();
			}
			else if (num == 2){
				// new drawing area
				da.newTaskRoot();
			}
		}
		
}
