package com.example.hereiam;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class RequestFragment extends DialogFragment {

	private String message;
	private TextView messageView;

	public RequestFragment(String message) {
		this.message = message;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();

		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout

		View view = inflater.inflate(R.layout.request_fragment, null);

		messageView = (TextView) view.findViewById(R.id.request_fragment_message);
		messageView.setText(message);

		builder.setView(view);

		return builder.create();
	}
}