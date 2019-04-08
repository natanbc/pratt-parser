package com.github.natanbc.pratt;

/**
 * Represents a history of characters. The size may or may not be
 * limited. When limited, the oldest character is removed when
 * inserting another one while full.
 */
public class CharHistory {
    private final int maxSize;
    private char[] array;
    private int pos;
    private int size;
    
    /**
     * Creates a history with the given maximum size.
     *
     * @param maxSize Maximum size. A value smaller than one
     *                means no limit.
     */
    public CharHistory(int maxSize) {
        this.maxSize = maxSize;
        this.array = maxSize > 0 && maxSize < 16 ? new char[maxSize] : new char[16];
    }
    
    /**
     * @return The size of this history.
     */
    public int size() {
        return size;
    }
    
    /**
     * Inserts a new character into this history.
     *
     * @param c Character to insert.
     */
    public void insert(char c) {
        maybeGrow();
        array[pos] = c;
        pos = (pos + 1) % array.length;
        size = Math.min(size + 1, array.length);
    }
    
    /**
     * Returns the last character inserted and removes it from
     * the history. If no characters have been inserted, this
     * method throws.
     *
     * @return Last inserted character.
     */
    public char remove() {
        if(size == 0) {
            throw new IllegalStateException("Buffer is empty");
        }
        size--;
        return array[pos = dec(pos, array.length)];
    }
    
    private void maybeGrow() {
        if(size == array.length && size != maxSize) {
            //if size is limited, create an array with min(size * 2, maxSize)
            //else size * 2
            char[] next = new char[maxSize <= 0 ? size * 2 : Math.min(maxSize, size * 2)];
            int nextCursor = 0;
            int cursor = pos;
            int remaining = size;
            while(remaining > 0) {
                next[nextCursor++] = array[cursor];
                cursor = inc(cursor, array.length);
                remaining--;
            }
            array = next;
            pos = size;
        }
    }
    
    private static int dec(int i, int modulus) {
        if (--i < 0) i = modulus - 1;
        return i;
    }
    
    private static int inc(int i, int modulus) {
        if(++i >= modulus) i = 0;
        return i;
    }
}
