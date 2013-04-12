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
		
		TextView nameTextView = (TextView) personRow.findViewById(R.id.personName);
		TextView messageTextView = (TextView) personRow.findViewById(R.id.personMessage);
		ImageView imageView = (ImageView) personRow.findViewById(R.id.personStatus);
		
		int resId = 0;
		
		if (!activity.isNetworking()) {
			Person current = people.get(position);

			switch (current.getStatus()) {
			case 0:
				resId = R.drawable.in;
//				imageView.setImageResource(R.drawable.in);
				break;
			case 1:
				resId = R.drawable.out;
//				imageView.setImageResource(R.drawable.out);
				break;
			case 2:
				resId = R.drawable.meeting;
//				imageView.setImageResource(R.drawable.meeting);
				break;
			case 3:
				resId = R.drawable.lunch;
//				imageView.setImageResource(R.drawable.lunch);
				break;
			case 4:
				resId = R.drawable.sick;
//				imageView.setImageResource(R.drawable.sick);
				break;
			case 5:
				resId = R.drawable.vacation;
//				imageView.setImageResource(R.drawable.vacation);
				break;
			default:
				resId = R.drawable.out;
//				imageView.setImageResource(R.drawable.out);
				break;
			}
			
			loadBitmap(resId, imageView);

			nameTextView.setText(current.getName());
			messageTextView.setText(current.getMessage());
		}
		
		return personRow;
	}
	
	private void loadBitmap(int resId, ImageView imageView) {
	    if (BitmapWorkerTask.cancelPotentialWork(resId, imageView)) {
	        final BitmapWorkerTask task = new BitmapWorkerTask(imageView, activity);
	        final AsyncDrawable asyncDrawable = new AsyncDrawable(activity.getResources(), null, task);
	        imageView.setImageDrawable(asyncDrawable);
	        task.execute(resId);
	    }
	}
}
