package com.example.hereiam;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class EditAliasFragment extends DialogFragment {

	private TextView messageView;
	private EditText input;
	SharedPreferences.Editor editor;

	public EditAliasFragment(SharedPreferences.Editor editor) {
		this.editor = editor;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();

		View view = inflater.inflate(R.layout.edit_alias_request_fragment, null);

		messageView = (TextView) view.findViewById(R.id.edit_alias_title);
		messageView.setText("Set Alias");

		input = (EditText) view.findViewById(R.id.edit_alias);

		builder.setView(view);

		return builder.create();
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		editor.putString(getString(R.string.alias), input.getText().toString());
		MainActivity.setAlias(getString(R.string.alias));
		Log.e("set alias", "set preference");
		editor.apply();
		super.onDismiss(dialog);
	}
}