//
//  ViewController.m
//  HTTP Test
//
//  Created by Pietro Caselani on 9/17/16.
//  Copyright © 2016 Pietro Caselani. All rights reserved.
//

#import "ViewController.h"

#import "DelegateExport.h"

#import <JavaScriptCore/JavaScriptCore.h>

@interface ViewController () <DelegateExport>

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
    
    [downloadFunction callWithArguments:nil];
}

- (void)callback:(NSString *)message
{
    _labelResult.text = message;
}

@end
