package au.edu.adelaide.physics.opticsstatusboard;

import java.util.ArrayList;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
		
		TextView textView = (TextView) personRow.findViewById(R.id.personName);
		ImageView imageView = (ImageView) personRow.findViewById(R.id.personStatus);
		
		if (!activity.isNetworking()) {
			Person current = people.get(position);

			switch (current.getStatus()) {
			case 0:
				imageView.setImageResource(R.drawable.in);
				break;
			case 1:
				imageView.setImageResource(R.drawable.out);
				break;
			case 2:
				imageView.setImageResource(R.drawable.meeting);
				break;
			case 3:
				imageView.setImageResource(R.drawable.lunch);
				break;
			case 4:
				imageView.setImageResource(R.drawable.sick);
				break;
			case 5:
				imageView.setImageResource(R.drawable.vacation);
				break;
			default:
				imageView.setImageResource(R.drawable.out);
				break;
			}

			textView.setText(current.getName());
		}
		
		return personRow;
	}
}
