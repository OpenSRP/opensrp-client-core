package org.smartregister.domain.jsonmapping.util;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by Vincent Karuri on 03/06/2020
 */
public class TreeNodeTest {

    private TreeNode<String, String> parentTreeNode;

    @Before
    public void setUp() {
        parentTreeNode = new TreeNode<String, String>("id", "label", "node", null);
    }

    @Test
    public void testTreeNodeOperations() {
        TreeNode<String, String> expectedChildNode = new TreeNode<>("id", "label", "node", "parent");
        parentTreeNode.addChild(expectedChildNode);

        TreeNode actualChildNode = parentTreeNode.findChild("id");
        assertEquals("id", actualChildNode.getId());
        assertEquals("label", actualChildNode.getLabel());
        assertEquals("node", actualChildNode.getNode());
        assertEquals("parent", actualChildNode.getParent());
        assertNull(actualChildNode.getChildren());

        actualChildNode.setLabel("new_label");
        actualChildNode.setNode("new_node");
        actualChildNode.setParent("new_parent");

        actualChildNode = parentTreeNode.findChild("id");
        assertEquals("id", actualChildNode.getId());
        assertEquals("new_label", actualChildNode.getLabel());
        assertEquals("new_node", actualChildNode.getNode());
        assertEquals("new_parent", actualChildNode.getParent());
        assertNull(actualChildNode.getChildren());

        actualChildNode.removeChild("id");
        assertNull(actualChildNode.findChild("id"));
    }
}
