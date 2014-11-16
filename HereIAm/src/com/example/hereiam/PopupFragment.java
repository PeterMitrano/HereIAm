package com.example.hereiam;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class PopupFragment extends DialogFragment {

	private String message;
	private TextView messageView;
	private OnDismissListener listener;

	public PopupFragment(String message, OnDismissListener listener) {
		this.message = message;
		this.listener = listener;

	}

	public PopupFragment(String message) {
		this.message = message;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();

		View view = inflater.inflate(R.layout.request_fragment, null);

		messageView = (TextView) view.findViewById(R.id.request_fragment_message);
		messageView.setText(message);

		builder.setView(view);

		return builder.create();
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		if (listener != null) {
			listener.onDismiss(dialog);
		}
		super.onDismiss(dialog);
	}
}