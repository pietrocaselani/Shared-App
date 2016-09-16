package io.github.pietrocaselani.helloworld;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.squareup.duktape.Duktape;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

	private Duktape mDuktape;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mDuktape = Duktape.create();

		final String script = loadJSFile();
		mDuktape.evaluate(script);

		final JSFunction jsFunction = mDuktape.proxy("hello", JSFunction.class);

		findViewById(R.id.activity_main_button_hello).setOnClickListener(new OnClickListener() {
			@Override public void onClick(final View v) {
				final String name = ((TextView) findViewById(R.id.activity_main_edittext_name)).getText().toString();

				final String result = jsFunction.sayHello(name);

				((TextView) findViewById(R.id.activity_main_textview_result)).setText(result);
			}
		});
	}

	@Override protected void onDestroy() {
		if (isFinishing())
			mDuktape.close();

		super.onDestroy();
	}

	private String loadJSFile() {
		return loadFileFromAssets("hello.js");
	}

	private String loadFileFromAssets(String fileName) {
		try {
			final InputStream inputStream = getAssets().open(fileName);

			final BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
			final StringBuilder total = new StringBuilder();
			String line;
			while ((line = r.readLine()) != null)
				total.append(line);

			return total.toString();

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	interface JSFunction {
		String sayHello(String name);
	}
}
