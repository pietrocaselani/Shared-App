//
//  ViewController.m
//  HelloWorld
//
//  Created by Pietro Caselani on 9/7/16.
//  Copyright © 2016 Pietro Caselani. All rights reserved.
//

#import <JavaScriptCore/JavaScriptCore.h>
#import "ViewController.h"

@interface ViewController ()

@property (weak, nonatomic) IBOutlet UITextField *textFieldName;
@property (weak, nonatomic) IBOutlet UILabel *resultLabel;

@end

@implementation ViewController
{
	__strong JSContext *_context;
}

- (void)viewDidLoad
{
	[super viewDidLoad];

	_context = [[JSContext alloc] init];

	NSString *script = [self loadJSFile];
	[_context evaluateScript:script];
}

- (IBAction)helloTouch:(id)sender
{
	JSValue *hello = _context[@"hello"];
    
    JSValue *helloFunction = hello[@"sayHello"];

	JSValue *result = [helloFunction callWithArguments:@[_textFieldName.text]];

	_resultLabel.text = result.toString;
}

- (NSString *)loadJSFile
{
	NSString *path = [[NSBundle mainBundle] pathForResource:@"hello" ofType:@"js"];
	return [[NSString alloc] initWithContentsOfFile:path encoding:NSUTF8StringEncoding error:nil];
}

@end
