//
//  ViewController.m
//  HTTP Test
//
//  Created by Pietro Caselani on 9/17/16.
//  Copyright © 2016 Pietro Caselani. All rights reserved.
//

#import "ViewController.h"

#import "DelegateExport.h"
#import "HTTPExport.h"

#import <JavaScriptCore/JavaScriptCore.h>

@interface ViewController () <DelegateExport, HTTPExport>

@property (weak, nonatomic) IBOutlet UITextField *textFieldName;
@property (weak, nonatomic) IBOutlet UILabel *labelResult;

@property (nonatomic, strong) JSContext *context;

@end

@implementation ViewController

- (void)viewDidLoad
{
	[super viewDidLoad];

	_context = [[JSContext alloc] init];

	NSString *path = [[NSBundle mainBundle] pathForResource:@"downloader" ofType:@"js"];
	NSString *script = [[NSString alloc] initWithContentsOfFile:path encoding:NSUTF8StringEncoding error:nil];

	[_context evaluateScript:script];

	_context[@"delegate"] = self;
	_context[@"http"] = self;

	JSValue *console = [JSValue valueWithNewObjectInContext:self.context];
	console[@"log"] = ^void(NSString *string) {
		NSLog(@"js: %@", string);
	};
	_context[@"console"] = console;
}

- (IBAction)executeTest:(id)sender
{
	NSString *name = _textFieldName.text;

	JSValue *downloader = _context[@"downloader"];
	JSValue *downloadFunction = downloader[@"executeTest"];

	[downloadFunction callWithArguments:@[name]];
}

- (IBAction)download:(id)sender
{
	JSValue *downloader = _context[@"downloader"];
	JSValue *downloadFunction = downloader[@"download"];

	[downloadFunction callWithArguments:@[@"https://raw.githubusercontent.com/pietrocaselani/JSON-test/master/contatos.json"]];
}

- (void)get:(NSString *)link callback:(JSValue *)callback
{
	NSURL *url = [NSURL URLWithString:link];

	NSURLSession *session = [NSURLSession sharedSession];
	NSURLSessionDataTask *task = [session dataTaskWithURL:url completionHandler:^(NSData *data, NSURLResponse *response, NSError *error) {
		NSHTTPURLResponse *httpurlResponse = (NSHTTPURLResponse *) response;

		NSString *jsonString = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];

		NSDictionary *callbackResponse = @{
				@"statusCode" : @(httpurlResponse.statusCode),
				@"body" : jsonString
		};

		dispatch_async(dispatch_get_main_queue(), ^{
			[callback callWithArguments:@[callbackResponse]];
		});
	}];

	[task resume];
}

- (void)callback:(NSString *)message
{
	[self setResultText:message];
}

- (void)onSuccess:(NSDictionary *)json
{
	NSArray *results = json[@"result"];

	NSMutableString *text = [[NSMutableString alloc] init];

	for (NSDictionary *object in results)
	{
		for (NSString *key in object.allKeys)
		{
			[text appendFormat:@"%@ = %@\n", key, object[key]];
		}

		[text appendString:@"\n\n"];
	}


	[self setResultText:[NSString stringWithString:text]];
}

- (void)onError:(NSString *)errorMessage
{
	[self setResultText:errorMessage];
}

- (void)setResultText:(NSString *)text
{
	CGSize maximumLabelSize = CGSizeMake(296, FLT_MAX);

	CGSize expectedLabelSize = [text sizeWithFont:_labelResult.font constrainedToSize:maximumLabelSize lineBreakMode:_labelResult.lineBreakMode];

	CGRect newFrame = _labelResult.frame;
	newFrame.size.height = expectedLabelSize.height;
	_labelResult.frame = newFrame;

	_labelResult.text = text;
}

@end
