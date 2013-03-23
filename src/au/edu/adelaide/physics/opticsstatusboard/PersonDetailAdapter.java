package au.edu.adelaide.physics.opticsstatusboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PersonDetailAdapter extends ArrayAdapter<InfoContainer> {
	Context context;
	InfoContainer[] info;

	public PersonDetailAdapter(Context context, InfoContainer[] info) {
		super(context, android.R.layout.simple_list_item_1, android.R.id.text1, info);
		
		this.context = context;
		this.info = info;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		InfoContainer currentInfo = info[position];
		View personRow;
		TextView titleView;
		TextView contentView;
		
		switch (currentInfo.getInfoType()) {
		case 0:
			personRow = inflator.inflate(R.layout.text_line, parent, false);
			
			titleView = (TextView) personRow.findViewById(R.id.lineTitle);
			contentView = (TextView) personRow.findViewById(R.id.lineContent);
			
			titleView.setText(currentInfo.getTitle());
			contentView.setText(currentInfo.getContents());
			
			return personRow;
		case 1:
			personRow = inflator.inflate(R.layout.phone_number_line, parent, false);
			
			titleView = (TextView) personRow.findViewById(R.id.lineTitle);
			contentView = (TextView) personRow.findViewById(R.id.lineContent);
			
			titleView.setText(currentInfo.getTitle());
			contentView.setText(currentInfo.getContents());
			
			return personRow;
		case 2:
			personRow = inflator.inflate(R.layout.email_line, parent, false);
			
			titleView = (TextView) personRow.findViewById(R.id.lineTitle);
			contentView = (TextView) personRow.findViewById(R.id.lineContent);
			
			titleView.setText(currentInfo.getTitle());
			contentView.setText(currentInfo.getContents());
			
			return personRow;
		default:
			return null;
		}
	}
}
