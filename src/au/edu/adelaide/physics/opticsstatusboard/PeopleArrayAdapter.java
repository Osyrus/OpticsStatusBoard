package au.edu.adelaide.physics.opticsstatusboard;

import java.util.ArrayList;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PeopleArrayAdapter extends ArrayAdapter<Person> {
	private final MainActivity activity;
	private final ArrayList<Person> people;
	
	public PeopleArrayAdapter(MainActivity activity, ArrayList<Person> people) {
		super(activity, android.R.layout.simple_list_item_1, android.R.id.text1, people);
		this.activity = activity;
		this.people = people;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflator = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View personRow = inflator.inflate(R.layout.person_row, parent, false);
		
		TextView nameTextView = (TextView) personRow.findViewById(R.id.personName);
		TextView messageTextView = (TextView) personRow.findViewById(R.id.personMessage);
		RelativeLayout layout = (RelativeLayout) personRow.findViewById(R.id.personRow);
		
		int box = 0;
		
		if (!activity.isNetworking()) {
			Person current = people.get(position);

			if (current.getStatus() == 0) {
				box = R.drawable.row_status_in;
			} else {
				box = R.drawable.row_status_out;
			}
			
			layout.setBackgroundResource(box);
			
			nameTextView.setText(current.getName());
			messageTextView.setText(current.getMessage());
		}
		
		return personRow;
	}
}
