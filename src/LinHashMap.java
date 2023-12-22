
/************************************************************************************
 * @file LinHashMap.java
 *
 * @author  John Miller
 */

import java.io.*;
import java.lang.reflect.Array;
import static java.lang.System.out;
import java.util.*;    
import java.io.Serializable;                                                                                                   

/************************************************************************************
 * This class provides hash maps that use the Linear Hashing algorithm.
 * A hash table is created that is an expandable array-list of buckets.
 */

public class LinHashMap <K, V>
       extends AbstractMap <K, V>
       implements Serializable, Cloneable, Map <K, V>
{
    /** The debug flag
     */
    private static final boolean DEBUG = true;

    /** The number of slots (for key-value pairs) per bucket.
     */
    private static final int SLOTS = 10000;

    /** The threshold/upper bound on the load factor
     */
    private static final double THRESHOLD = 1.1;

    /** The class for type K.
     */
    private final Class <K> classK;

    /** The class for type V.
     */
    private final Class <V> classV;

    /********************************************************************************
     * This inner class defines buckets that are stored in the hash table.
     */

    private class Bucket implements Serializable
    {
        int    nKeys;
        K []   key;
        V []   value;
        Bucket next;
 
        @SuppressWarnings("unchecked")

        Bucket ()
        {
            nKeys = 0;
            key   = (K []) Array.newInstance (classK, SLOTS);
            value = (V []) Array.newInstance (classV, SLOTS);
            next  = null;
        } // constructor

        V find (K k)
        {
            for (var j = 0; j < nKeys; j++) if (key[j].equals (k)) return value[j];
            return null;
        } // find

        void add (K k, V v)
        {
            key[nKeys]   = k;
            value[nKeys] = v;
            nKeys++;
        } // add

        void print ()
        {
            out.print ("[ " );
            for (var j = 0; j < nKeys; j++) out.print (key[j] + " . ");
            out.println ("]" );
        } // print

    } // Bucket inner class

    /** The list of buckets making up the hash table.
     */
    private final List <Bucket> hTable;

    /** The modulus for low resolution hashing
     */
    private int mod1;

    /** The modulus for high resolution hashing
     */
    private int mod2;

    /** The index of the next bucket to split.
     */
    private int isplit = 0;

    /** Counter for the number buckets accessed (for performance testing).
     */
    private int count = 0;

    /** The counter for the total number of keys in the LinHash Map
     */
    private int keyCount = 0;

    /********************************************************************************
     * Construct a hash table that uses Linear Hashing.
     * @param classK  the class for keys (K)
     * @param classV  the class for values (V)
     */
    public LinHashMap (Class <K> _classK, Class <V> _classV)
    {
        classK = _classK;
        classV = _classV;
        mod1   = 4;                                                          // initial size
        mod2   = 2 * mod1;
        hTable = new ArrayList <> ();
        for (var i = 0; i < mod1; i++) hTable.add (new Bucket ());
    } // constructor

    /********************************************************************************
     * Return a set containing all the entries as pairs of keys and values.
     * @return  the set view of the map
     */
    public Set <Map.Entry <K, V>> entrySet ()
    {
        var enSet = new HashSet <Map.Entry <K, V>> ();
        //  T O   B E   I M P L E M E N T E D

        // Getting the size of the hash table to be iterated
        int hashTable_Size = hTable.size();
        int current_ind = 0;

         // Iterating each and every bucket in the hash table
        while( current_ind < hashTable_Size) 
        {
            // Fetching the bucket which is at the current index
        
            Bucket current_bucket = hTable.get(current_ind);
            // Iterate through the bucket chain at this position in the hash table
            while (current_bucket != null) 
            {
                // Iterating through the key-value pairs stored in the current bucket
                for (int temp_counter = 0; temp_counter < current_bucket.nKeys; temp_counter++) 
                {
            
                    // Retrieving the key and value from the current bucket's keyvalue pair
                    K keyToAdd = current_bucket.key[temp_counter];
                    V valueToAdd = current_bucket.value[temp_counter];
                    // Creating a new entry using the key-value pair  
                    Map.Entry<K, V> entryToAdd = new AbstractMap.SimpleEntry<>(keyToAdd, valueToAdd);
                    // Adding the entry to the set
                    enSet.add(entryToAdd);
                }
                // Move to the next bucket in the chain
                current_bucket = current_bucket.next;
                current_ind++;
            }
        }
        // Return the set of entries
        return enSet;
    } 
// entrySet


    /********************************************************************************
     * Given the key, look up the value in the hash table.
     * @param key  the key used for look up
     * @return  the value associated with the key
     */
    @SuppressWarnings("unchecked")
    public V get (Object key)
    {
        var i = h (key);
        return find ((K) key, hTable.get (i), true);
    } // get

    /********************************************************************************
     * Put the key-value pair in the hash table.  Split the 'isplit' bucket chain
     * when the load factor is exceeded.
     * @param key    the key to insert
     * @param value  the value to insert
     * @return  the old/previous value, null if none
     */
    public V put (K key, V value)
    {
        var i = Math.abs(h(key));                                                // hash to i-th bucket chain
        var bh   = hTable.get (i);                                           // start with home bucket
        var oldV = find (key, bh, false);                                    // find old value associated with key
        out.println ("LinearHashMap.put: key = " + key + ", h() = " + i + ", value = " + value);

        keyCount++;                                                          // increment the key count
        var lf = loadFactor ();                                              // compute the load factor
        if (DEBUG) out.println ("put: load factor = " + lf);
        if (lf > THRESHOLD) split ();                                        // split beyond THRESHOLD

        var b = bh;
        while (true) {
            if (b.nKeys < SLOTS) { b.add (key, value); return oldV; }
            if (b.next != null) b = b.next; else break;
        } // while

        var bn = new Bucket ();
        bn.add (key, value);
        b.next = bn;                                                         // add new bucket at end of chain
        return oldV;
    } // put

    /********************************************************************************
     * Print the hash table.
     */
    public void print ()
    {
        out.println ("LinHashMap");
        out.println ("-------------------------------------------");

        for (var i = 0; i < hTable.size (); i++) {
            out.print ("Bucket [ " + i + " ] = ");
            var j = 0;
            for (var b = hTable.get (i); b != null; b = b.next) {
                if (j > 0) out.print (" \t\t --> ");
                b.print ();
                j++;
            } // for
        } // for

        out.println ("-------------------------------------------");
    } // print
 
    /********************************************************************************
     * Return the size (SLOTS * number of home buckets) of the hash table. 
     * @return  the size of the hash table
     */
    public int size ()
    {
        return SLOTS * (mod1 + isplit);
    } // size

    /********************************************************************************
     * Split bucket chain 'isplit' by creating a new bucket chain at the end of the
     * hash table and redistributing the keys according to the high-resolution hash
     * function 'h2'. Increment 'isplit'. If the current split phase is complete,
     * reset 'isplit' to zero, and update the hash functions.
     */
    private void split() {
        out.println("split: bucket chain " + isplit);

        //  T O   B E   I M P L E M E N T E D

        // Create a new bucket chain at the end of the hash table
        hTable.add(new Bucket());

        // Creating new bucket for redistributing the keys
        Bucket bucketKeysNew = new Bucket();

         // Retrieving the buckets to be split
         Bucket splitBucketChain = hTable.get(isplit);

        // The next of the split bucket chain is assigned to the new buckets created
        splitBucketChain.next = bucketKeysNew;

        //Incrementing the index of isplit 
        isplit++;

        // Iterating through all the keys of the split bucket chain
        int i=0;
        while (i < splitBucketChain.nKeys) 
        {
            K split_key = splitBucketChain.key[i];
            V split_key_value = splitBucketChain.value[i];

            // Computing the high-resolution hash value using the 'h2' defined with the key obtained
            int hash_value = h2(split_key);

            // Determine whether to keep the key in the split bucket chain or move it to the new bucket chain
            if (hash_value != isplit) {
                // Move the key to the new bucket chain
                bucketKeysNew.add(split_key, split_key_value);

                // Remove the key from the split bucket chain
                splitBucketChain.key[i] = null;
                splitBucketChain.value[i] = null;
                splitBucketChain.nKeys--;
            }
            i++;
        }

         // Resetting the 'isplit' to zero and updating the hash functions after the current split phase is completed 
         if (isplit == mod1) 
         {
             //isplit set to zero
             
             isplit = 0;
             //updating the hash function
 
             mod1 *= 2;
             mod2 = 2 * mod1;
         }
    } // split


    /********************************************************************************
     * Return the load factor for the hash table.
     * @return  the load factor
     */
    private double loadFactor ()
    {
        return keyCount / (double) size ();
    } // loadFactor

    /********************************************************************************
     * Find the key in the bucket chain that starts with home bucket bh.
     * @param key     the key to find
     * @param bh      the given home bucket
     * @param by_get  whether 'find' is called from 'get' (performance monitored)
     * @return  the current value stored stored for the key
     */
    private V find (K key, Bucket bh, boolean by_get)
    {
        for (var b = bh; b != null; b = b.next) {
            if (by_get) count++;
            V result = b.find (key);
            if (result != null) return result;
        } // for
        return null;
    } // find

    /********************************************************************************
     * Hash the key using the low resolution hash function.
     * @param key  the key to hash
     * @return  the location of the bucket chain containing the key-value pair
     */
    private int h (Object key)
    {
        return key.hashCode () % mod1;
    } // h

    /********************************************************************************
     * Hash the key using the high resolution hash function.
     * @param key  the key to hash
     * @return  the location of the bucket chain containing the key-value pair
     */
    private int h2 (Object key)
    {
        return key.hashCode () % mod2;
    } // h2

    /********************************************************************************
     * The main method used for testing.
     * @param  the command-line arguments (args [0] gives number of keys to insert)
     */
    public static void main (String [] args)
    {
        var totalKeys = 40;
        var RANDOMLY  = false;

        LinHashMap <Integer, Integer> ht = new LinHashMap <> (Integer.class, Integer.class);
        if (args.length == 1) totalKeys = Integer.valueOf (args [0]);

        if (RANDOMLY) {
            var rng = new Random ();
            for (var i = 1; i <= totalKeys; i += 2) ht.put (rng.nextInt (2 * totalKeys), i * i);
        } else {
            for (var i = 1; i <= totalKeys; i += 2) ht.put (i, i * i);
        } // if

        ht.print ();
        for (var i = 0; i <= totalKeys; i++) {
            out.println ("key = " + i + " value = " + ht.get (i));
        } // for
        out.println ("-------------------------------------------");
        out.println ("Average number of buckets accessed = " + ht.count / (double) totalKeys);
    } // main

} // LinHashMap class

