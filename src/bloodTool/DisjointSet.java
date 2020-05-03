package bloodTool;

//With help from: https://www.geeksforgeeks.org/find-the-number-of-islands-set-2-using-disjoint-set/

//Implementation of disjoint set data structure (works with integers)

public class DisjointSet {

    //References parent of node, ie. parent[node] is the parent of 'node'
    int[] parent;

    int[] setSizes;

    int size;

    //constructor
    public DisjointSet(int size) {
        parent = new int[size];
        setSizes = new int[size];
        this.size = size;
        CreateSet();
    }

    //before union, all nodes are their own set
    void CreateSet() {
        for (int i = 0; i < size; i++) {
            parent[i] = i;
            //Each set starts unlinked, so has size of 1
            setSizes[i] = 1;
        }
    }

    boolean AreInSameSet(int i, int j) {
        return Find(i) == Find(j);
    }

    int GetSetSize(int i) {
        int temp = Find(i);
        return setSizes[temp];
    }

    //Returns parent of referenced node
    int Find(int i) {
        while (parent[i] != i) {
            //Compress path
            parent[i] = parent[parent[i]];
            i = parent[i];
        }
        return i;
    }

    //Quick union with elements i and j
    //I call this a "blind union" as it doesn't
    //care which tree is deeper or wider at all
    //and just blindly unions
    void BlindUnion(int i, int j) {
        //Connect roots
        parent[Find(j)] = Find(i);
    }

    void Union(int i, int j) {
        //Get roots of inputted ints
        int r1 = Find(i);
        int r2 = Find(j);
        //Union the smaller set with the bigger one (less expensive than bigger to smaller)
        if (r1 != r2) {
            if (setSizes[r1] >= setSizes[r2]) {
                setSizes[r1] += setSizes[r2];
                parent[r2] = r1;
            } else {
                setSizes[r2] += setSizes[r1];
                parent[r1] = r2;
            }
        }
    }
}
