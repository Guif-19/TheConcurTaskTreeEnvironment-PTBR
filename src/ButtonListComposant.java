import javax.swing.*;
import java.awt.*;
public class ButtonListComposant extends JPanel {
    public static JButton undo,redo;
    public ButtonListComposant(DrawingArea da) {
        setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        button(da,"Nova árvore","tree.jpg","new");
        button(da,"Abrir","open.png","open");
        button(da,"Salvar","save.jpg","save");
        button(da,"Salvar como","saveAs.jpg","saveAs");
        button(da,"Recortar tarefa e subtarefas","cut.png","cut");
        button(da,"Copiar tarefa e subtarefas","copy.gif","copy");
        button(da,"Colar como subtarefa da seleção","paste.png","paste");
        undo=button(da,"Desfazer","undo.png","undo"); undo.setEnabled(false);
        redo=button(da,"Refazer","redo.png","redo"); redo.setEnabled(false);
    }
    private JButton button(DrawingArea da,String title,String icon,String command) {
        JButton b=new JButton(new ImageIcon(getClass().getResource("images/"+icon)));
        b.setToolTipText(title); b.getAccessibleContext().setAccessibleName(title);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.addActionListener(e -> ((MainFrame)SwingUtilities.getWindowAncestor(da)).command(command));
        add(b); return b;
    }
}
