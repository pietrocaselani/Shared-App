//
//  DelegateExport.h
//  HTTP Test
//
//  Created by Pietro Caselani on 9/17/16.
//  Copyright © 2016 Pietro Caselani. All rights reserved.
//

#import <JavaScriptCore/JSExport.h>

@protocol DelegateExport <JSExport>

- (void)callback:(NSString *)message;
- (void)onSuccess:(NSDictionary *)json;
- (void)onError:(NSString *)errorMessage;

@end
