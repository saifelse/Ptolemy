package edu.mit.pt.data;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;



@SuppressWarnings("serial")
public class LinkedList<E> implements List<E> {
	Node<E> head;
	Node<E> tail;
	int size;
	
	public boolean add(E object) {
		if(tail == null){
			head = new Node<E>(object);
			tail = head;
		}else {
			Node<E> newTail = new Node<E>(object);
			tail.setNextNode(newTail);
			tail = newTail;
		}
		size++;
		return true;
	}

	public void add(int location, E object) {
		throw new java.lang.UnsupportedOperationException();
	}

	public boolean addAll(Collection<? extends E> arg0) {
		throw new java.lang.UnsupportedOperationException();
	}

	public boolean addAll(int arg0, Collection<? extends E> arg1) {
		throw new java.lang.UnsupportedOperationException();
	}
	public boolean addAll(LinkedList<E> list){
		if(this.isEmpty()){
			head = list.getHead();
			tail = list.getTail();
			size = list.size();
		}else if(list.isEmpty()){
			// do nothing.
		}else {
			tail.setNextNode(list.getHead());
			size += list.size();
		}
		return true;
	}
	
	public void clear() {
		head = null;
		tail = null;
		size = 0;
	}

	public boolean contains(Object object) {
		throw new java.lang.UnsupportedOperationException();
	}

	public boolean containsAll(Collection<?> arg0) {
		throw new java.lang.UnsupportedOperationException();
	}

	public E get(int location) {
		throw new java.lang.UnsupportedOperationException();
	}

	public int indexOf(Object object) {
		throw new java.lang.UnsupportedOperationException();
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public Iterator<E> iterator() {
		return new LinkedListIterator();
	}

	public int lastIndexOf(Object object) {
		throw new java.lang.UnsupportedOperationException();
	}

	public ListIterator<E> listIterator() {
		throw new java.lang.UnsupportedOperationException();
	}

	public ListIterator<E> listIterator(int location) {
		throw new java.lang.UnsupportedOperationException();
	}

	public E remove(int location) {
		throw new java.lang.UnsupportedOperationException();
	}

	public boolean remove(Object object) {
		throw new java.lang.UnsupportedOperationException();
	}

	public boolean removeAll(Collection<?> arg0) {
		throw new java.lang.UnsupportedOperationException();
	}

	public boolean retainAll(Collection<?> arg0) {
		throw new java.lang.UnsupportedOperationException();
	}

	public E set(int location, E object) {
		throw new java.lang.UnsupportedOperationException();
	}

	public int size() {
		return size;
	}

	public List<E> subList(int start, int end) {
		throw new java.lang.UnsupportedOperationException();
	}

	public Object[] toArray() {
		throw new java.lang.UnsupportedOperationException();
	}

	public <T> T[] toArray(T[] array) {
		throw new java.lang.UnsupportedOperationException();
	}
	public Node<E> getHead(){
		return head;
	}
	public Node<E> getTail(){
		return tail;
	}

	private class Node<E> {
		private E item;
		private Node<E> nextNode;
		public Node(E object) {
			item = object;
		}
		public void setNextNode(Node<E> node){
			nextNode = node;
		}
		public Node<E> getNextNode(){
			return nextNode;
		}
		public E getValue(){
			return item;
		}
	}
	private class LinkedListIterator implements Iterator<E> {
		Node<E> currentNode;
		public LinkedListIterator(){
			currentNode = new Node<E>(null);
			currentNode.setNextNode(LinkedList.this.getHead());
		}
		public boolean hasNext() {
			return currentNode.getNextNode() != null;
		}

		public E next() {
			currentNode = currentNode.getNextNode();
			return currentNode.getValue();
		}

		public void remove() {
			throw new java.lang.UnsupportedOperationException();
		}
		
	}

}


