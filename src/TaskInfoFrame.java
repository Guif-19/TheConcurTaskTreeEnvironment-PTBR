import java.awt.*;
import javax.swing.*;
import TaskTree.TaskNode;
import TaskTree.enums.Category;
public class TaskInfoFrame extends JDialog {
    private final DrawingArea da;
    private TaskNode node;
    private final JTextField name = new JTextField(25);
    private final JComboBox<Category> category = new JComboBox<>(Category.values());
    public TaskInfoFrame(MainFrame owner, DrawingArea da) {
        super(owner,"Propriedades da tarefa",true);
        this.da=da;
        JPanel fields=new JPanel(new GridLayout(2,2,10,10));
        fields.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        fields.add(new JLabel("Nome:")); fields.add(name);
        fields.add(new JLabel("Categoria:")); fields.add(category);
        JButton apply=new JButton("Aplicar");
        apply.addActionListener(e -> {
            if(node==null) return;
            String value=name.getText().trim();
            if(value.isEmpty()) { JOptionPane.showMessageDialog(this,"Digite um nome para a tarefa."); return; }
            if(!value.equals(node.getTaskName()) || category.getSelectedItem()!=node.getCategory()) {
                da.empilerUndo(); da.viderRedo();
                node.setTaskName(value); node.setCategory((Category)category.getSelectedItem()); da.repaint();
            }
            setVisible(false);
        });
        JButton cancel=new JButton("Cancelar");
        cancel.addActionListener(e -> setVisible(false));
        JPanel buttons=new JPanel(); buttons.add(apply); buttons.add(cancel);
        add(fields,BorderLayout.CENTER); add(buttons,BorderLayout.SOUTH);
        getRootPane().setDefaultButton(apply);
        getRootPane().registerKeyboardAction(e -> setVisible(false),KeyStroke.getKeyStroke("ESCAPE"),JComponent.WHEN_IN_FOCUSED_WINDOW);
        setDefaultCloseOperation(HIDE_ON_CLOSE); pack(); setLocationRelativeTo(owner);
    }
    public void changeTaskNodeProperties(TaskNode node) {
        setVisible(false); this.node=node;
        name.setText(node.getTaskName()); category.setSelectedItem(node.getCategory());
    }
}
