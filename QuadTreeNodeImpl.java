// CIS 121, HW4 QuadTree

public class QuadTreeNodeImpl implements QuadTreeNode {


    QuadTreeNodeImpl topLeft;
    QuadTreeNodeImpl topRight;
    QuadTreeNodeImpl bottomLeft;
    QuadTreeNodeImpl bottomRight;
    int color;
    int size;

    public QuadTreeNodeImpl(int size, int color) { 
        this.color = color;
        this.size = size;
        topLeft = null;
        topRight = null;
        bottomLeft = null;
        bottomRight = null;
    }
    
    public QuadTreeNodeImpl(QuadTreeNodeImpl tl, QuadTreeNodeImpl tr, 
        QuadTreeNodeImpl bl, QuadTreeNodeImpl br, int size) {
        topLeft = tl;
        topRight = tr;
        bottomLeft = bl;
        bottomRight = br;
        this.size = size;
    }
    

    public static QuadTreeNode buildFromIntArray(int[][] image) {
        if (image == null) {
            throw new IllegalArgumentException();
        }
        if (image.length == 0 || image[0].length == 0) {
            throw new IllegalArgumentException();
        }
        
        double x = image.length;
        
        while (x > 1) {
            x /= 2.0;
        }
        
        if (x != 1) {
            throw new IllegalArgumentException();
        }
        if (image.length != image[0].length) {
            throw new IllegalArgumentException();
        }

        return buildHelper(image, image.length, 0, 0);
    }
    
    static QuadTreeNodeImpl buildHelper(int[][] image, int size, int yBounding, int xBounding) {
    
        if (size == 1) { 
            return new QuadTreeNodeImpl(size, image[yBounding][xBounding]);
        }
        
        QuadTreeNodeImpl bl = buildHelper(image, size / 2, yBounding + (size / 2), xBounding);
        QuadTreeNodeImpl br = buildHelper(image, size / 2, yBounding + 
            (size / 2), xBounding + (size / 2));
        QuadTreeNodeImpl tl = buildHelper(image, size / 2, yBounding, xBounding);
        QuadTreeNodeImpl tr = buildHelper(image, size / 2, yBounding, xBounding + (size / 2));

        if (bl.color == br.color && br.color == tr.color &&
            tr.color == tl.color
            && bl.isLeaf()
            && br.isLeaf()
            && tl.isLeaf()
            && tr.isLeaf()) {
            return new QuadTreeNodeImpl(size, tl.color);
        }
        
        return new QuadTreeNodeImpl(tl, tr, bl, br, size);
        
    }

    @Override
    public int getColor(int x, int y) {
        if (x < 0 || y < 0 || x >= size || y >= size) {
            throw new IllegalArgumentException();
        }
        if (this.isLeaf()) {
            return this.color;
        }
        if (y >= size / 2) {
            if (x >= size / 2) {
                return this.bottomRight.getColor(x - (size / 2), y - (size / 2));
            } else {
                return this.bottomLeft.getColor(x, y - (size / 2));
            }
        } else {
            if (x >= size / 2) {
                return this.topRight.getColor(x - (size / 2), y);
            } else {
                return this.topLeft.getColor(x, y);
            }
        }
    }

    @Override
    public void setColor(int x, int y, int c) {
        if (x < 0 || y < 0 || x >= size || y >= size) {
            throw new IllegalArgumentException();
        }
        if (this.isLeaf() && this.size != 1 && this.color != c) {
            this.bottomRight = new QuadTreeNodeImpl(size / 2, this.color);
            this.bottomLeft = new QuadTreeNodeImpl(size / 2, this.color);
            this.topLeft = new QuadTreeNodeImpl(size / 2, this.color);
            this.topRight = new QuadTreeNodeImpl(size / 2, this.color);
            if (y >= size / 2) {
                if (x >= size / 2) {
                    this.bottomRight.setColor(x - (size / 2), y - (size / 2), c); 
                } else {
                    this.bottomLeft.setColor(x, y - (size / 2), c);
                }
            } else {
                if (x >= size / 2) {
                    this.topRight.setColor(x - (size / 2), y, c);
                } else {
                    this.topLeft.setColor(x, y, c);
                }
            }
        } else {
            if (this.size == 1) {
                this.color = c;
            }
            if (!this.isLeaf()) {
                if (y >= size / 2) {
                    if (x >= size / 2) {
                        this.bottomRight.setColor(x - (size / 2), y - (size / 2), c); 
                    } else {
                        this.bottomLeft.setColor(x, y - (size / 2), c);
                    }
                } else {
                    if (x >= size / 2) {
                        this.topRight.setColor(x - (size / 2), y, c);
                    } else {
                        this.topLeft.setColor(x, y, c);
                    }
                }
                if (bottomLeft.color == bottomRight.color && bottomRight.color == topRight.color &&
                    topRight.color == topLeft.color
                    && bottomRight.isLeaf()
                    && bottomLeft.isLeaf()
                    && topLeft.isLeaf()
                    && topRight.isLeaf()) {
                    this.color = bottomLeft.color;
                    this.bottomLeft = null;
                    this.bottomRight = null;
                    this.topLeft = null;
                    this.topRight = null;
                }
      
            }
        }
    }

    @Override
    public QuadTreeNode getQuadrant(QuadName quadrant) {
        switch (quadrant) {
            case TOP_LEFT:
                return topLeft;
            case TOP_RIGHT:
                return topRight;
            case BOTTOM_RIGHT:
                return bottomRight;
            default:
                return bottomLeft;

        }
    }

    @Override
    public int getDimension() {
        return this.size;
    }

    @Override
    public int getSize() {
        if (this.isLeaf()) {
            return 1;
        }
        return 1 + this.bottomLeft.getSize() + this.topLeft.getSize()
            + this.bottomRight.getSize() + this.topRight.getSize();
    }

    @Override
    public boolean isLeaf() {
        return this.bottomLeft == null;
    }

    @Override
    public int[][] decompress() {
        int[][] returnThis = new int[this.size][this.size];
        decompressHelper(returnThis, this.size, 0, 0);
        return returnThis;
    }
    
    void decompressHelper(int[][] newArray, int size, int yBounding, int xBounding) {
        if (this.size == 1) {
            newArray[yBounding][xBounding] = color;
        }
        if (isLeaf()) {
            for (int y = yBounding; y < yBounding + size; y++) {
                for (int x = xBounding; x < xBounding + size; x++) {
                    newArray[y][x] = color;
                }
            }
        } else {
            topLeft.decompressHelper(newArray, size / 2, yBounding, xBounding);
            topRight.decompressHelper(newArray, size / 2, yBounding, xBounding + (size / 2));
            bottomLeft.decompressHelper(newArray, size / 2, yBounding + (size / 2), xBounding);
            bottomRight.decompressHelper(newArray, size / 2, yBounding +
                (size / 2), xBounding + (size / 2));
        }
    
    }

    @Override
    public double getCompressionRatio() {
        return (double) ((double) getSize()) / ((double) getDimension() * getDimension());
    }
}
