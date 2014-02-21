
package org.bml.util.tree.node;

import java.util.Map;

/**
 * @author Brian M. Lima
 */
public class ByteNode {
    int numChildren;
    boolean isLeaf;
    Map<Byte,ByteNode> children;
}
