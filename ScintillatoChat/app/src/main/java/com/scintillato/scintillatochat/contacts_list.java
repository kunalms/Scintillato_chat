package com.scintillato.scintillatochat;

import java.util.List;

public class contacts_list {

	String name,number;
	public contacts_list(String name,String number) {
		// TODO Auto-generated constructor stub
		this.name=name;
		this.number=number;
	}
	public List<contacts_list> sort()
	{
		return null;
	}
	public int check(List<contacts_list> arr,contacts_list ele)
	{
		for(int i=0;i<arr.size();i++)
		{
			if(arr.get(i).number.equals(ele.number))
			{
				return 1;
			}
		}
		if(ele.number.length()!=13)
			return 1;
		return 0;
	}
}

