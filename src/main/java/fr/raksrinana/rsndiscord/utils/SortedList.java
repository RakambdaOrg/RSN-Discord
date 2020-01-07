package fr.raksrinana.rsndiscord.utils;

import java.util.AbstractList;
import java.util.ArrayList;

public class SortedList<E> extends AbstractList<E>{
	private ArrayList<E> internalList = new ArrayList<>();
	
	@Override
	public E get(int i){
		return internalList.get(i);
	}
	
	@Override
	public void add(int position, E e){
		internalList.add(e);
		internalList.sort(null);
	}
	
	@Override
	public int size(){
		return internalList.size();
	}
}
