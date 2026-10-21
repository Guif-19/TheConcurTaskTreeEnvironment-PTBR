import TaskTree.TaskNode;
import TaskTree.enums.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.file.*;
import java.util.*;
import javax.swing.*;

public class RegressionTest {
    static void check(boolean value,String label) { if(!value) throw new AssertionError(label); }
    static TaskNode fixture() {
        TaskNode r=new TaskNode("Gestão agrícola & <plantio>",Category.ABSTRACTION,new Point(350,100));
        TaskNode a=r.addChild("Escolher região",Category.USER,new Point(200,220));
        r.addChild("Consultar previsão",Category.APPLICATION,new Point(400,220));
        r.addChild("Confirmar ação",Category.INTERACTION,new Point(600,220));
        a.addChild("Subtarefa com acentuação",Category.INTERACTION,new Point(150,340));
        a.setLinksD(Links.CHOICE); r.getChildAt(1).setLinksD(Links.ORDERINDEPENDANCE);
        return r;
    }
    static void select(DrawingArea da,TaskNode n) {
        da.clearSelection();
        da.mousePressed(new MouseEvent(da,MouseEvent.MOUSE_PRESSED,System.currentTimeMillis(),0,
            n.getTaskPoint().x,n.getTaskPoint().y,1,false));
        check(da.getSelectedNode()==n,"seleção");
    }
    static <T> T find(Container container,Class<T> type,String text) {
        for(Component c:container.getComponents()) {
            if(type.isInstance(c) && (text==null || c instanceof AbstractButton && text.equals(((AbstractButton)c).getText())))
                return type.cast(c);
            if(c instanceof Container) { T found=find((Container)c,type,text); if(found!=null) return found; }
        }
        return null;
    }
    static void chooseFile(Path path) {
        javax.swing.Timer timer=new javax.swing.Timer(150,null);
        timer.addActionListener(e -> {
            for(Window w:Window.getWindows()) if(w.isVisible() && w instanceof Container) {
                JFileChooser chooser=find((Container)w,JFileChooser.class,null);
                if(chooser!=null) { timer.stop(); chooser.setSelectedFile(path.toFile()); chooser.approveSelection(); return; }
            }
        });
        timer.setRepeats(true); timer.start();
    }
    static void answer(int value) {
        javax.swing.Timer timer=new javax.swing.Timer(150,null);
        timer.addActionListener(e -> {
            for(Window w:Window.getWindows()) if(w.isVisible() && w instanceof Container) {
                JOptionPane option=find((Container)w,JOptionPane.class,null);
                if(option!=null) { timer.stop(); option.setValue(option.getOptions()==null ? value : option.getOptions()[value]); return; }
            }
        });
        timer.start();
    }
    public static void main(String[] args) throws Exception {
        Path dir=Files.createTempDirectory(Paths.get("."),"test-ctt-");
        try {
            TaskNode original=fixture();
            Path file=dir.resolve("árvore de teste.ctt.xml");
            TreeIO.save(file,original);
            check(TreeIO.encode(original).equals(TreeIO.encode(TreeIO.load(file))),"ida e volta XML");
            TaskNode copy=original.cloneTree();
            check(TreeIO.encode(original).equals(TreeIO.encode(copy)),"clone preserva relações");
            copy.getChildAt(0).getTaskPoint().translate(30,50);
            check(original.getChildAt(0).getTaskPoint().equals(new Point(200,220)),"clone independente");
            copy.getChildAt(0).DeleteNode();
            check(copy.getChildCount()==2 && copy.getChildAt(0).getLinkG()==null,"recorte limpa relação");
            check(original.getChildCount()==3,"recorte não altera original");
            Path bad=dir.resolve("bad.xml");
            Files.write(bad,"<ctt-ptbr version='1'><task/></ctt-ptbr>".getBytes("UTF-8"));
            try { TreeIO.load(bad); throw new AssertionError("arquivo inválido aceito"); } catch(java.io.IOException expected) {}
            Files.write(bad,"<!DOCTYPE x [<!ENTITY y SYSTEM 'file:///nao-ler'>]><ctt-ptbr version='1'/>".getBytes("UTF-8"));
            try { TreeIO.load(bad); throw new AssertionError("DOCTYPE aceito"); } catch(org.xml.sax.SAXException expected) {}
            System.out.println("PASS: persistência, acentos, categorias, posições, relações, clone, exclusão e XML inválido.");
            if(args.length>0 && args[0].equals("--ui")) SwingUtilities.invokeAndWait(() -> {
                MainFrame frame=new MainFrame("ConcurTaskTree — Português");
                frame.setVisible(true);
                try {
                    DrawingArea da=frame.da;
                    da.replaceRoot(fixture());
                    String before=TreeIO.encode(da.getRoot());
                    select(da,da.getRoot().getChildAt(0)); frame.command("copy");
                    select(da,da.getRoot()); frame.command("paste");
                    check(da.getRoot().getChildCount()==4,"colar");
                    check(da.getRoot().getChildAt(3).getChildCount()==1,"colar descendentes");
                    ButtonListComposant.undo.doClick();
                    check(before.equals(TreeIO.encode(da.getRoot())),"desfazer colagem");
                    ButtonListComposant.redo.doClick();
                    check(da.getRoot().getChildCount()==4,"refazer colagem");
                    select(da,da.getRoot().getChildAt(3)); frame.command("cut");
                    check(da.getRoot().getChildCount()==3,"recortar");
                    frame.command("undo"); check(da.getRoot().getChildCount()==4,"desfazer recorte");
                    select(da,da.getRoot().getChildAt(0));
                    MainFrame.taskinfoframe.changeTaskNodeProperties(da.getSelectedNode());
                    JTextField field=find(MainFrame.taskinfoframe,JTextField.class,null);
                    field.setText("Novo nome válido");
                    find(MainFrame.taskinfoframe,JButton.class,"Cancelar").doClick();
                    check(da.getSelectedNode().getTaskName().equals("Escolher região"),"cancelar edição");
                    field.setText("Novo nome válido");
                    find(MainFrame.taskinfoframe,JButton.class,"Aplicar").doClick();
                    check(da.getSelectedNode().getTaskName().equals("Novo nome válido"),"aplicar edição");
                    frame.command("undo");
                    check(da.getRoot().getChildAt(0).getTaskName().equals("Escolher região"),"desfazer edição");
                    Path uiFile=dir.resolve("interface.ctt.xml").toAbsolutePath();
                    chooseFile(uiFile); frame.command("saveAs");
                    check(Files.exists(uiFile),"Salvar como conectado");
                    String saved=TreeIO.encode(da.getRoot());
                    frame.command("new"); check(da.getRoot().getChildCount()==0,"Nova conectada");
                    chooseFile(uiFile); frame.command("open");
                    check(saved.equals(TreeIO.encode(da.getRoot())),"Abrir conectado");
                    da.getRoot().setTaskName("Raiz alterada");
                    frame.command("save");
                    check(TreeIO.load(uiFile).getTaskName().equals("Raiz alterada"),"Salvar conectado");
                    da.getRoot().setTaskName("Mudança pendente");
                    answer(2); frame.command("new");
                    check(da.getRoot().getTaskName().equals("Mudança pendente"),"cancelar descarte");
                    answer(1); frame.command("new");
                    check(da.getRoot().getChildCount()==0,"descartar confirmado");
                    // Render the actual layout offscreen to check resources and drawing.
                    da.replaceRoot(fixture());
                    frame.validate();
                    java.awt.image.BufferedImage img=new java.awt.image.BufferedImage(frame.getWidth(),frame.getHeight(),2);
                    frame.paint(img.getGraphics());
                    javax.imageio.ImageIO.write(img,"png",Paths.get("preview-ptbr.png").toFile());
                    System.out.println("PASS: seleção, copiar/colar/recortar, desfazer/refazer, propriedades, menus de arquivo e proteção de alterações.");
                } catch(Exception ex) { throw new RuntimeException(ex); }
                finally { MainFrame.taskinfoframe.dispose(); frame.dispose(); }
            });
        } finally {
            try(java.util.stream.Stream<Path> paths=Files.walk(dir)) {
                paths.sorted(Comparator.reverseOrder()).forEach(p -> { try { Files.delete(p); } catch(Exception e) { throw new RuntimeException(e); } });
            }
        }
        System.out.println("TODOS OS TESTES PASSARAM.");
    }
}
