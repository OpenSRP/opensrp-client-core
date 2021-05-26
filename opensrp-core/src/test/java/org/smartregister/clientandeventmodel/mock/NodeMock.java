package org.smartregister.clientandeventmodel.mock;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;

/**
 * Created by kaderchowdhury on 30/11/17.
 */

public class NodeMock implements Node {

    @Override
    public String getNodeName() {
        return "model/instance/Child_Vaccination_Enrollment/birth_date_known";
    }

    @Override
    public String getNodeValue() throws DOMException {
        return null;
    }

    @Override
    public void setNodeValue(String s) throws DOMException {
        System.out.println();
    }

    @Override
    public short getNodeType() {
        return 1;
    }

    @Override
    public Node getParentNode() {
        return null;
    }

    @Override
    public NodeList getChildNodes() {
        return null;
    }

    @Override
    public Node getFirstChild() {
        return null;
    }

    @Override
    public Node getLastChild() {
        return null;
    }

    @Override
    public Node getPreviousSibling() {
        return null;
    }

    @Override
    public Node getNextSibling() {
        return null;
    }

    @Override
    public NamedNodeMap getAttributes() {
        return null;
    }

    @Override
    public Document getOwnerDocument() {
        return null;
    }

    @Override
    public Node insertBefore(Node node, Node node1) throws DOMException {
        return null;
    }

    @Override
    public Node replaceChild(Node node, Node node1) throws DOMException {
        return null;
    }

    @Override
    public Node removeChild(Node node) throws DOMException {
        return null;
    }

    @Override
    public Node appendChild(Node node) throws DOMException {
        return null;
    }

    @Override
    public boolean hasChildNodes() {
        return false;
    }

    @Override
    public Node cloneNode(boolean b) {
        return null;
    }

    @Override
    public void normalize() {
        System.out.println();
    }

    @Override
    public boolean isSupported(String s, String s1) {
        return false;
    }

    @Override
    public String getNamespaceURI() {
        return null;
    }

    @Override
    public String getPrefix() {
        return null;
    }

    @Override
    public void setPrefix(String s) throws DOMException {
        System.out.println();
    }

    @Override
    public String getLocalName() {
        return null;
    }

    @Override
    public boolean hasAttributes() {
        return false;
    }

    @Override
    public String getBaseURI() {
        return null;
    }

    @Override
    public short compareDocumentPosition(Node node) throws DOMException {
        return 0;
    }

    @Override
    public String getTextContent() throws DOMException {
        return null;
    }

    @Override
    public void setTextContent(String s) throws DOMException {
        System.out.println();
    }

    @Override
    public boolean isSameNode(Node node) {
        return false;
    }

    @Override
    public String lookupPrefix(String s) {
        return null;
    }

    @Override
    public boolean isDefaultNamespace(String s) {
        return false;
    }

    @Override
    public String lookupNamespaceURI(String s) {
        return null;
    }

    @Override
    public boolean isEqualNode(Node node) {
        return false;
    }

    @Override
    public Object getFeature(String s, String s1) {
        return null;
    }

    @Override
    public Object setUserData(String s, Object o, UserDataHandler userDataHandler) {
        return null;
    }

    @Override
    public Object getUserData(String s) {
        return null;
    }
}
