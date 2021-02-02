package fr.raksrinana.rsndiscord.utils;

import org.jetbrains.annotations.Nullable;
import java.util.AbstractList;
import java.util.ArrayList;

public class SortedList<E> extends AbstractList<E>{
	private final ArrayList<E> internalList = new ArrayList<>();
	
	@Override
	@Nullable
	public E get(int i){
		return internalList.get(i);
	}
	
	@Override
	public void add(int position, @Nullable E e){
		internalList.add(e);
		internalList.sort(null);
	}
	
	@Override
	public int size(){
		return internalList.size();
	}
}
