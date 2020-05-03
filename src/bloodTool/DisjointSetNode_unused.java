package bloodTool;

//Object based version of DJS, Mostly from class notes
//unused

public class DisjointSetNode_unused<T> {

    public DisjointSetNode_unused<?> parent = null;
    public T data;

    public DisjointSetNode_unused(DisjointSetNode_unused<?> parent, T data) {
        this.parent = parent;
        this.data = data;
    }

    public DisjointSetNode_unused(T data) {
        this.data = data;
    }

    public static DisjointSetNode_unused<?> find(DisjointSetNode_unused<?> node) {
        while (node.parent != null) {
            if (node.parent.parent != null) node.parent = node.parent.parent; //Compress path
            node = node.parent;
        }
        return node;
    }

    public static void union(DisjointSetNode_unused<?> p, DisjointSetNode_unused<?> q) {
        find(q).parent = find(p); //The root of q is made reference the root of p
    }

    public DisjointSetNode_unused<?> getParent() {
        return parent;
    }

    public void setParent(DisjointSetNode_unused<?> parent) {
        this.parent = parent;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}