class DLL {
    static class Node{
        ResearchArticle data;
        Node previous;
        Node next;

        public Node(ResearchArticle data) {
            this.data = data;
        }
    }
    Node head, tail = null;

    public void addNode(ResearchArticle data) {
        Node newNode = new Node(data);

        if(head == null) {
            head = tail = newNode;
            head.previous = null;
            tail.next = null;
        }
        else {
            tail.next = newNode;
            newNode.previous = tail;
            tail = newNode;
            tail.next = null;
        }
    }
}
