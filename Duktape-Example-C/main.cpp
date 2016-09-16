#include <iostream>
#include "duktape.h"

using namespace std;

void runFile(string fileName);

void runFile3();

void runSumFile();

void runFile4();

void runFile5();

void testTypes();

int main() {
	runFile("js/file1.js"); // Execute file1.js
	runFile("js/file2.js"); // Execute file2.js
	runFile3(); // Invoke a function and get a variable value
	runSumFile(); // Invoke a function with multiple args
	runFile4(); // Insert a variable into a context
	runFile5(); // Change the value of a variable
	testTypes();

	return 0;
}

void testTypes() {
	cout << "Running file: js/types.js" << endl;

	duk_context *context = duk_create_heap_default();
	if (!context) {
		cout << "Out of memory?!" << endl;
		exit(1);
	}

	if (duk_peval_file(context, "js/types.js") != 0) {
		cout << "Error in file js/types.js: " << duk_safe_to_string(context, -1) << endl;
		duk_destroy_heap(context);
		exit(1);
	}

	void (^printType)(string) = ^(string key) {
		duk_get_prop_string(context, -1, key.c_str());
		cout << key << " type = " << duk_get_type(context, -1) << endl;
		duk_pop(context);
	};

	duk_push_global_object(context);

	printType("name");
	printType("age");
	printType("list");
	printType("hey");
	printType("heyy");

	duk_pop(context);

	duk_destroy_heap(context);
}

void runFile5() {
	cout << "Running file: js/file5.js" << endl;

	duk_context *context = duk_create_heap_default();
	if (!context) {
		cout << "Out of memory?!" << endl;
		exit(1);
	}

	if (duk_peval_file(context, "js/file5.js") != 0) {
		cout << "Error in file js/file5.js: " << duk_safe_to_string(context, -1) << endl;
		duk_destroy_heap(context);
		exit(1);
	}

	duk_push_global_object(context);

	duk_get_prop_string(context, -1, "displayName");

	cout << "Invoke the original file" << endl;
	duk_pcall(context, 0);
	duk_pop(context);

	cout << "Invoke with the variable changed" << endl;

	duk_push_string(context, "name");
	duk_push_string(context, "PC");

	duk_idx_t topIndex = duk_get_top_index(context);

	duk_put_prop(context, -topIndex);

	duk_get_prop_string(context, -1, "displayName");

	duk_pcall(context, 0);
	duk_pop(context);

	duk_pop(context);

	duk_destroy_heap(context);
}

void runFile4() {
	cout << "Running file: js/file4.js" << endl;

	duk_context *context = duk_create_heap_default();
	if (!context) {
		cout << "Out of memory?!" << endl;
		exit(1);
	}

	if (duk_peval_file(context, "js/file4.js") != 0) {
		cout << "Error in file js/file4.js: " << duk_safe_to_string(context, -1) << endl;
		duk_destroy_heap(context);
		exit(1);
	}

	duk_push_global_object(context);

	duk_push_string(context, "name");

	duk_push_string(context, "Da onde eu vim?!");

	duk_idx_t topIndex = duk_get_top_index(context);

	duk_put_prop(context, -topIndex);

	duk_get_prop_string(context, -1, "displayOutVarName");

	if (duk_pcall(context, 0) != 0)
		cout << "Error: " << duk_safe_to_string(context, -1) << endl;

	duk_pop(context);

	duk_pop(context);

	duk_destroy_heap(context);

	cout << "\n\n";
}

void runFile3() {
	cout << "Running file: js/file3.js" << endl;

	duk_context *context = duk_create_heap_default();
	if (!context) {
		cout << "Out of memory?!" << endl;
		exit(1);
	}

	if (duk_peval_file(context, "js/file3.js") != 0) {
		cout << "Error in file js/file3.js: " << duk_safe_to_string(context, -1) << endl;
		duk_destroy_heap(context);
		exit(1);
	}

	duk_push_global_object(context);

	duk_get_prop_string(context, -1, "sayHello");
	duk_push_string(context, "PCArg");

	duk_dump_context_stdout(context);

	if (duk_pcall(context, 1) != 0)
		cout << "Error: " << duk_safe_to_string(context, -1) << endl;
	else {
		duk_dump_context_stdout(context);
		cout << "SayHello result: " << duk_get_string(context, -1) << endl;
	}

	duk_dump_context_stdout(context);

	duk_pop(context);

	duk_dump_context_stdout(context);

	if (duk_get_prop_string(context, -1, "myName"))
		cout << "Var myName = " << duk_require_string(context, -1);
	else
		cout << "Var myName is undefined";

	duk_dump_context_stdout(context);

	duk_pop(context);

	duk_dump_context_stdout(context);

	duk_pop(context);

	duk_dump_context_stdout(context);

	duk_destroy_heap(context);

	cout << "\n\n";
}

void runFile(string filePath) {
	cout << "Running file: " << filePath << endl;

	duk_context *context = duk_create_heap_default();
	if (!context) {
		cout << "Out of memory?!" << endl;
		exit(1);
	}

	if (duk_peval_file(context, filePath.c_str()) != 0) {
		cout << "Error in file " << filePath << ": " << duk_safe_to_string(context, -1) << endl;
		duk_destroy_heap(context);
		exit(1);
	}

	duk_pop(context);
	duk_destroy_heap(context);

	cout << "\n\n";
}

void runSumFile() {
	cout << "Running file: js/sum.js" << endl;

	duk_context *context = duk_create_heap_default();
	if (!context) {
		cout << "Out of memory?!" << endl;
		exit(1);
	}

	if (duk_peval_file(context, "js/sum.js") != 0) {
		cout << "Error in file js/sum.js: " << duk_safe_to_string(context, -1) << endl;
		duk_destroy_heap(context);
		exit(1);
	}

	duk_push_global_object(context);

	if (duk_has_prop_string(context, -1, "result") == 1)
		cout << "Tem result!" << endl;
	else
		cout << "Cade o result?! Error: " << duk_safe_to_string(context, -1) << endl;

	duk_get_prop_string(context, -1, "result");

	duk_double_t result = duk_get_number(context, -1);

	cout << "Result = " << result << endl;

	duk_pop(context); // Pop get_prop_string result

	if (duk_get_prop_string(context, -1, "sum") != 1)
		exit(1);

	float n1, n2;

	cout << "Digite um número: ";
	cin >> n1;

	cout << "Digite outro número: ";
	cin >> n2;

	duk_push_number(context, n1);
	duk_push_number(context, n2);

	if (duk_pcall(context, 2) != 0)
		cout << "Error: " << duk_safe_to_string(context, -1);
	else
		cout << "Tudo certo!" << endl;

	duk_pop(context); // Pop the get prop string sum

	duk_get_prop_string(context, -1, "result");

	result = duk_get_number(context, -1);

	cout << "Result agora é = " << result << endl;

	duk_pop(context); // Pop the get prop string result

	duk_pop(context); // Pop the file

	duk_destroy_heap(context);

	cout << "\n\n";
}