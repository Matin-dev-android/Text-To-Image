package com.matinApplication.texttoimage;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import dalvik.system.ApplicationRuntime;

public class MainActivity extends AppCompatActivity {


	Button convert;
	EditText editText;
	TextView textView;
	Button importText ;
	private static final int REQUEST_CODE_PERMISSIONS = 101;

	String[] permissions = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE};

	@SuppressLint("MissingInflatedId")
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


		textView = findViewById(R.id.textView2);
		importText = findViewById(R.id.import_txt);
		checkPermissions();


		convert = findViewById(R.id.ConvertBtn);
		editText = findViewById(R.id.EditText);
		convert.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (editText.getText().toString().length() == 0) {
					Toast.makeText(MainActivity.this, "اول یک متن وارد کنید", Toast.LENGTH_SHORT).show();
					return;
				}
				Intent intent = new Intent(MainActivity.this, TextActivity.class);
				String text = editText.getText().toString();
				intent.putExtra("teext", text);
				startActivity(intent);
			}
		});


		importText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
				intent.addCategory(Intent.CATEGORY_OPENABLE);
				intent.setType("text/plain");

				startActivityForResult(intent,20);
			}
		});


	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 20 && resultCode==RESULT_OK && data != null) {
			Uri uri = data.getData();
            try {
                String text = readTextFromUri(uri);
				Intent intent = new Intent(MainActivity.this , TextActivity.class);
				intent.putExtra("teext",text);
				startActivity(intent);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Toast.makeText(this, "...", Toast.LENGTH_SHORT).show();
		}
	}


	private String readTextFromUri(Uri uri) throws IOException {
		StringBuilder stringBuilder = new StringBuilder();
		InputStream inputStream = getContentResolver().openInputStream(uri);

		if (inputStream == null) {
			throw new IOException("Unable to open input stream for URI: " + uri.toString());
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		String line;
		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line).append('\n');
		}

		reader.close();
		inputStream.close();
		return stringBuilder.toString();
	}

	private void checkPermissions() {
		List<String> permissionsToRequest = new ArrayList<>();
		for (String permission : permissions) {
			if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
				permissionsToRequest.add(permission);
			}
		}
		if (!permissionsToRequest.isEmpty()) {
			ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[0]), REQUEST_CODE_PERMISSIONS);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		switch (requestCode) {
			case REQUEST_CODE_PERMISSIONS:
				Map<String, Integer> permissionResults = new HashMap<>();
				for (int i = 0; i < permissions.length; i++) {
					permissionResults.put(permissions[i], grantResults[i]);
				}
				boolean allPermissionsGranted = true;
				for (String permission : permissions) {
					if (permissionResults.get(permission) != PackageManager.PERMISSION_GRANTED) {
						allPermissionsGranted = false;
						break;
					}
				}
				if (allPermissionsGranted) {
					Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show();
				}

		}
	}
}