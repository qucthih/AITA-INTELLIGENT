public class SinglyLinkedList {

    static class Node {
        int data;
        Node next;

        Node(int data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node head;

    public Node insertAtHead(int value) {
        Node newNode = new Node(value)
        newNode.next = head;
        head = newNode;
        return newNode;
    }

    public int size() {
        int count = 0;
        Node current = head;
        while (current != null) {
            count++;
            current = current.next;
        }
        return count;
    }

    public void printList() {
        Node current = head;
        while (current != null) {
            System.out.print(current.data + " ");
            current = current.next;
        }
        System.out.println();
    }

    public static void main(String[] args) {
        SinglyLinkedList list = new SinglyLinkedList();
        list.insertAtHead(3);
        list.insertAtHead(2);
        list.insertAtHead(1);
        list.printList();
        System.out.println("Size: " + list.size());
    }
}
