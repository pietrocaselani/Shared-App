//
// HTTPExport.h
// HTTP Test
//
// Created by Pietro Caselani on 9/22/16.
// Copyright (c) 2016 Pietro Caselani. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <JavaScriptCore/JavaScriptCore.h>

@protocol HTTPExport <JSExport>

JSExportAs(get,
		- (void)get:(NSString *)url callback:(JSValue *)callback
);

@end