package org.smartregister.clientandeventmodel.mock;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Created by kaderchowdhury on 23/11/17.
 */

public class NodeListMock implements NodeList {

    public static NodeList getNodeList() {
        return new NodeListMock();
    }

    @Override
    public Node item(int i) {
        return new NodeMock();
    }

    @Override
    public int getLength() {
        return 1;
    }

}
