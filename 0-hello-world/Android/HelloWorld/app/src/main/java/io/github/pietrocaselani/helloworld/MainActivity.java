package io.github.pietrocaselani.helloworld;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class MainActivity extends AppCompatActivity {

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final Context context = Context.enter();
		context.setOptimizationLevel(-1);
		final ScriptableObject scope = context.initStandardObjects();

		try {
			context.evaluateReader(scope, loadJSFile(), "hello", 1, null);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		final Scriptable helloObject = (Scriptable) scope.get("hello");
		final Function helloFunction = (Function) helloObject.get("sayHello", helloObject);

		findViewById(R.id.activity_main_button_hello).setOnClickListener(new OnClickListener() {
			@Override public void onClick(final View v) {
				final String name = ((TextView) findViewById(R.id.activity_main_edittext_name)).getText().toString();

				final String result = (String) helloFunction.call(context, helloObject, helloObject, new Object[]{name});

				((TextView) findViewById(R.id.activity_main_textview_result)).setText(result);
			}
		});
	}

	@Override protected void onDestroy() {
		if (isFinishing())
			Context.exit();

		super.onDestroy();
	}

	private Reader loadJSFile() throws IOException {
		return new InputStreamReader(getAssets().open("hello.js"));
	}
}
