import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import TaskTree.TaskNode;
public class MainFrame extends JFrame {
    public static TaskInfoFrame taskinfoframe;
    public static JMenuItem undo,redo,taskInfos;
    DrawingArea da;
    private File currentFile;
    private String savedState;
    public MainFrame(String title) {
        super(title);
        da=new DrawingArea();
        add(da,BorderLayout.CENTER);
        add(new LeftComposant(this,da),BorderLayout.WEST);
        taskinfoframe=new TaskInfoFrame(this,da);
        JMenuBar bar=new JMenuBar();
        JMenu file=new JMenu("Arquivo"), edit=new JMenu("Editar"), view=new JMenu("Exibir"), help=new JMenu("Ajuda");
        item(file,"Nova árvore","control N","new");
        item(file,"Abrir...","control O","open");
        item(file,"Salvar","control S","save");
        item(file,"Salvar como...","control shift S","saveAs");
        item(file,"Sair","control Q","quit");
        undo=item(edit,"Desfazer","control Z","undo"); undo.setEnabled(false);
        redo=item(edit,"Refazer","control Y","redo"); redo.setEnabled(false);
        item(edit,"Recortar","control X","cut"); item(edit,"Copiar","control C","copy");
        item(edit,"Colar como subtarefa","control V","paste");
        taskInfos=item(view,"Propriedades da tarefa","control I","properties"); taskInfos.setEnabled(false);
        item(help,"Como usar",null,"help");
        bar.add(file); bar.add(edit); bar.add(view); bar.add(help); setJMenuBar(bar);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() { public void windowClosing(WindowEvent e) { command("quit"); } });
        Dimension screen=Toolkit.getDefaultToolkit().getScreenSize();
        setSize(Math.min(1000,screen.width-60),Math.min(780,screen.height-80));
        setLocationRelativeTo(null);
        savedState=snapshot();
    }
    private JMenuItem item(JMenu menu,String name,String key,String command) {
        JMenuItem item=new JMenuItem(name);
        if(key!=null) item.setAccelerator(KeyStroke.getKeyStroke(key));
        item.addActionListener(e -> command(command)); menu.add(item); return item;
    }
    private String snapshot() {
        try { return TreeIO.encode(da.getRoot()); }
        catch(Exception ex) { throw new IllegalStateException(ex); }
    }
    private void title() { setTitle("ConcurTaskTree — Português"+(currentFile==null?"":" — "+currentFile.getName())); }
    public void command(String command) {
        try {
            switch(command) {
                case "new":
                    if(!confirmDiscard()) return;
                    da.newTaskRoot(); currentFile=null; savedState=snapshot(); title(); break;
                case "open": open(); break;
                case "save": save(false); break;
                case "saveAs": save(true); break;
                case "cut": da.cutSelection(); break;
                case "copy": da.copySelection(); break;
                case "paste": da.pasteSelection(); break;
                case "undo": da.Undo(); break;
                case "redo": da.Redo(); break;
                case "properties":
                    if(da.getSelectedNode()!=null) {
                        taskinfoframe.changeTaskNodeProperties(da.getSelectedNode());
                        taskinfoframe.setVisible(true);
                    } break;
                case "quit":
                    if(confirmDiscard()) { taskinfoframe.dispose(); dispose(); } break;
                case "help":
                    JOptionPane.showMessageDialog(this,
                        "Clique em uma tarefa para selecionar e exibir os ícones de edição.\n"+
                        "Use os ícones ao redor dela para adicionar tarefas e relações.\n"+
                        "Arraste uma tarefa para movê-la; arraste o fundo para mover a árvore.\n"+
                        "A roda do mouse reorganiza o espaçamento.\n"+
                        "Copiar/recortar inclui todas as subtarefas. Colar adiciona à seleção.\n"+
                        "Os arquivos .ctt.xml são próprios desta versão PT-BR.\n"+
                        "Salvar e Abrir preservam nomes, categorias, posições e relações.",
                        "Como usar",JOptionPane.INFORMATION_MESSAGE); break;
            }
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(this,"Não foi possível concluir a operação.\n"+
                (ex.getMessage()==null?ex.getClass().getSimpleName():ex.getMessage()),"Erro",JOptionPane.ERROR_MESSAGE);
        }
    }
    private JFileChooser chooser() {
        JFileChooser c=new JFileChooser(currentFile==null?new File("."):currentFile.getAbsoluteFile().getParentFile());
        c.setFileFilter(new FileNameExtensionFilter("Árvore ConcurTaskTree PT-BR (*.ctt.xml)","xml"));
        c.setAcceptAllFileFilterUsed(false);
        if(currentFile!=null) c.setSelectedFile(currentFile);
        return c;
    }
    private void open() throws Exception {
        JFileChooser c=chooser(); c.setDialogTitle("Abrir árvore");
        if(c.showOpenDialog(this)!=JFileChooser.APPROVE_OPTION) return;
        TaskNode loaded=TreeIO.load(c.getSelectedFile().toPath());
        if(!confirmDiscard()) return;
        da.replaceRoot(loaded); currentFile=c.getSelectedFile(); savedState=snapshot(); title();
    }
    private boolean save(boolean choose) throws Exception {
        File target=currentFile;
        if(choose || target==null) {
            JFileChooser c=chooser(); c.setDialogTitle("Salvar árvore");
            if(c.showSaveDialog(this)!=JFileChooser.APPROVE_OPTION) return false;
            target=c.getSelectedFile();
            if(!target.getName().toLowerCase(java.util.Locale.ROOT).endsWith(".ctt.xml"))
                target=new File(target.getPath()+".ctt.xml");
            if(target.exists() && JOptionPane.showConfirmDialog(this,
                "Substituir o arquivo "+target.getName()+"?","Confirmar substituição",
                JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION) return false;
        }
        TreeIO.save(target.toPath(),da.getRoot());
        currentFile=target; savedState=snapshot(); title(); return true;
    }
    private boolean confirmDiscard() throws Exception {
        if(savedState.equals(snapshot())) return true;
        Object[] choices={"Salvar","Descartar alterações","Cancelar"};
        int answer=JOptionPane.showOptionDialog(this,"Há alterações não salvas. O que deseja fazer?",
            "Alterações não salvas",JOptionPane.DEFAULT_OPTION,JOptionPane.QUESTION_MESSAGE,null,choices,choices[0]);
        if(answer==0) return save(false);
        return answer==1;
    }
    public static void main(String[] args) {
        java.util.Locale.setDefault(new java.util.Locale("pt","BR"));
        JComponent.setDefaultLocale(java.util.Locale.getDefault());
        SwingUtilities.invokeLater(() -> {
            UIManager.put("OptionPane.yesButtonText","Sim"); UIManager.put("OptionPane.noButtonText","Não");
            UIManager.put("OptionPane.cancelButtonText","Cancelar");
            UIManager.put("FileChooser.openButtonText","Abrir"); UIManager.put("FileChooser.saveButtonText","Salvar");
            UIManager.put("FileChooser.cancelButtonText","Cancelar");
            new MainFrame("ConcurTaskTree — Português").setVisible(true);
        });
    }
}
