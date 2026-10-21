import TaskTree.TaskNode;
import TaskTree.enums.*;
import java.awt.Point;
import java.io.*;
import java.nio.file.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
public final class TreeIO {
    public static String encode(TaskNode root) throws Exception {
        Document doc=DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element model=doc.createElement("ctt-ptbr");
        model.setAttribute("version","1"); doc.appendChild(model);
        model.appendChild(encodeNode(doc,root));
        Transformer t=TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
        StringWriter out=new StringWriter();
        t.transform(new DOMSource(doc),new StreamResult(out)); return out.toString();
    }
    private static Element encodeNode(Document doc,TaskNode node) {
        Element e=doc.createElement("task");
        e.setAttribute("name",node.getTaskName()); e.setAttribute("category",node.getCategory().name());
        e.setAttribute("x",Integer.toString(node.getTaskPoint().x)); e.setAttribute("y",Integer.toString(node.getTaskPoint().y));
        if(node.getLinkD()!=null) e.setAttribute("right",node.getLinkD().name());
        for(int i=0;i<node.getChildCount();i++) e.appendChild(encodeNode(doc,node.getChildAt(i)));
        return e;
    }
    public static void save(Path path,TaskNode root) throws Exception {
        byte[] data=encode(root).getBytes("UTF-8");
        Path target=path.toAbsolutePath(), temp=Files.createTempFile(target.getParent(),".ctt-",".tmp");
        try {
            Files.write(temp,data);
            // Validate the complete output before replacing an existing file.
            load(temp);
            try { Files.move(temp,target,StandardCopyOption.ATOMIC_MOVE,StandardCopyOption.REPLACE_EXISTING); }
            catch(AtomicMoveNotSupportedException ex) { Files.move(temp,target,StandardCopyOption.REPLACE_EXISTING); }
        } finally { Files.deleteIfExists(temp); }
    }
    public static TaskNode load(Path path) throws Exception {
        if(Files.size(path)>5*1024*1024) throw new IOException("Arquivo maior que 5 MB.");
        DocumentBuilderFactory f=DocumentBuilderFactory.newInstance();
        f.setFeature("http://apache.org/xml/features/disallow-doctype-decl",true);
        f.setFeature("http://xml.org/sax/features/external-general-entities",false);
        f.setFeature("http://xml.org/sax/features/external-parameter-entities",false);
        f.setXIncludeAware(false); f.setExpandEntityReferences(false);
        DocumentBuilder builder=f.newDocumentBuilder();
        builder.setErrorHandler(new org.xml.sax.helpers.DefaultHandler() {
            public void error(org.xml.sax.SAXParseException e) throws SAXException { throw e; }
            public void fatalError(org.xml.sax.SAXParseException e) throws SAXException { throw e; }
        });
        Document doc=builder.parse(path.toFile());
        Element model=doc.getDocumentElement();
        if(!"ctt-ptbr".equals(model.getTagName()) || !"1".equals(model.getAttribute("version")))
            throw new IOException("Formato incompatível. Selecione uma árvore salva nesta versão PT-BR.");
        java.util.List<Element> roots=children(model);
        if(roots.size()!=1) throw new IOException("O arquivo deve conter uma única raiz.");
        TaskNode root=decodeNode(roots.get(0),0,new int[]{0});
        if(roots.get(0).hasAttribute("right")) throw new IOException("A raiz não pode ter relação lateral.");
        return root;
    }
    private static java.util.List<Element> children(Element e) throws IOException {
        java.util.List<Element> result=new java.util.ArrayList<>();
        NodeList list=e.getChildNodes();
        for(int i=0;i<list.getLength();i++) if(list.item(i) instanceof Element) {
            Element child=(Element)list.item(i);
            if(!"task".equals(child.getTagName())) throw new IOException("Elemento desconhecido.");
            result.add(child);
        }
        return result;
    }
    private static TaskNode decodeNode(Element e,int depth,int[] count) throws Exception {
        if(depth>100 || ++count[0]>5000) throw new IOException("Árvore muito grande.");
        String name=e.getAttribute("name");
        if(name.trim().isEmpty()) throw new IOException("Tarefa sem nome.");
        TaskNode node=new TaskNode(name,Category.valueOf(e.getAttribute("category")),
            new Point(Integer.parseInt(e.getAttribute("x")),Integer.parseInt(e.getAttribute("y"))));
        java.util.List<Element> children=children(e);
        for(Element child:children) node.add(decodeNode(child,depth+1,count));
        for(int i=0;i<children.size();i++) {
            String link=children.get(i).getAttribute("right");
            if(!link.isEmpty()) {
                if(i==children.size()-1) throw new IOException("Relação lateral sem tarefa vizinha.");
                node.getChildAt(i).setLinksD(Links.valueOf(link));
            }
        }
        return node;
    }
}
