//
//  CuotasManager.m
//  Goles Messenger
//
//  Created by Christian Cabarrocas on 05/02/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "CuotasManager.h"
#import "CoreDataManager.h"
#import "CoreDataParsing.h"
#import "MatchBetType.h"
#import "BetType.h"
#import "BetTypeOdd.h"
#import "Utils.h"

@implementation CuotasManager

#pragma mark - SINGLETON CREATION
//------------------------------------------------------------------------------
+ (CuotasManager *)singleton
{
    static CuotasManager *cuotas = nil;
    static dispatch_once_t predicate;
    dispatch_once(&predicate, ^{
        cuotas = [[CuotasManager alloc] init];
    });
    return cuotas;
    
}

//------------------------------------------------------------------------------
+ (CuotasManager *)sharedInstance
{
    return [self singleton];
}


#pragma mark - PUBLIC METHODS

//------------------------------------------------------------------------------
/**
 @brief Retrieve BetTypeOdds with BetType uniqueKey = 1X2.
 
 @discussion Get the 1X2 bettypeodds from coredata and compose an NSString to show in Cuotas tableviewcell
 in the DetailMatchViewController.

 @params match Match entity to retrieve odds from.
 
 @returns NSString formatted with three odds.
 */
//------------------------------------------------------------------------------
- (NSString *)get1X2OddsForMatch:(Match *)match {
    
    NSPredicate *predicate1 = [NSPredicate predicateWithFormat:@"match.idMatch = %@",match.idMatch];
    NSPredicate *predicate2 = [NSPredicate predicateWithFormat:@"betType.uniqueKey = %@",@"1X2"];
    NSPredicate *finalPred = [NSCompoundPredicate andPredicateWithSubpredicates:@[predicate1,predicate2]];
    NSArray *matchBetType = [[CoreDataManager singleton] getAllEntities:[MatchBetType class] withPredicate:finalPred];
    
    MatchBetType *mbt = [matchBetType firstObject];
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:[NSString stringWithFormat:@"matchBetType.idMatchBetType = %@",mbt.idMatchBetType]];
    NSPredicate *deleteFilter = [NSPredicate predicateWithFormat:@"readyToDelete = NO"];
    NSPredicate *compundPredicate = [NSCompoundPredicate andPredicateWithSubpredicates:@[predicate, deleteFilter]];

    NSArray *odds = [[CoreDataManager singleton] getAllEntities:[BetTypeOdd class] orderedByKey:@"idBetTypeOdd" ascending:YES withPredicate:compundPredicate];
    
    if ([odds count] == 3){
        NSMutableArray *oddsValues = [[NSMutableArray alloc] initWithCapacity:3];
        for (BetTypeOdd *odd in odds)
            [oddsValues addObject:odd.value];
        NSString *valueString = [NSString stringWithFormat:@"%@, %@, %@",
                                 [Utils setTwoDecimalsToBetOdd:[[oddsValues objectAtIndex:0]floatValue]],
                                 [Utils setTwoDecimalsToBetOdd:[[oddsValues objectAtIndex:1]floatValue]],
                                 [Utils setTwoDecimalsToBetOdd:[[oddsValues objectAtIndex:2]floatValue]]];
        return valueString;
    }
    return nil;
}

//------------------------------------------------------------------------------
/**
 @brief Gets the provider for a given match.
 
 @discussion Get the provider for a match from coredata. This app version is not multi-provider ready,
 so this method returns the provider for the first matchbettype.
 
 @params match Match entity to retrieve provider from.
 
 @returns Provider complete entity
 */
//------------------------------------------------------------------------------
- (Provider *)getProviderForCurrentOddsForMatch:(Match *)match {
    
    NSArray *matchBetTypes = [match.matchBetType allObjects];
    if ([matchBetTypes count] > 0) {
        MatchBetType *mBT = [matchBetTypes firstObject];
        return mBT.provider;
    }
    
    return nil;
}

//------------------------------------------------------------------------------
/**
 @brief Main method for turn Cuotas cell ON/OFF
 
 @discussion Checks for any Provider in CoreData with "visible" property set to YES. If any provider is active,
 Cuotas cell in DetailMatchViewController is visible.
 
 @returns BOOL YES = Cuotas cell is active - NO = Cuotas cell is not shown.
 */
//------------------------------------------------------------------------------
- (BOOL)isProviderActive {
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:[NSString stringWithFormat:@"visibleIOS = YES"]];
    NSArray *providers = [[CoreDataManager singleton] getAllEntities:[Provider class] withPredicate:predicate];
    if ([providers count] > 0)
        return YES;
    return NO;
}

//------------------------------------------------------------------------------
/**
 @brief Get all cuotas(BetTypeOdds) for a given match and his Bettypes
 
 @params match Match entity to retrieve if cuotas is available.
 
 @returns BOOL YES = Cuotas cell is active
 @returns BOOL NO = Cuotas cell is not shown.
 */
//------------------------------------------------------------------------------
- (NSArray *)getCompleteOddsForMatch:(Match *)match {
    
    NSMutableArray *betsTypesArray = [[NSMutableArray alloc] init];
    
    NSSet *odds = match.matchBetType;
    for (MatchBetType *mbt in odds) {
        NSPredicate *predicate = [NSPredicate predicateWithFormat:@"matchBetType.idMatchBetType = %@", mbt.idMatchBetType];
        NSPredicate *deleteFilter = [NSPredicate predicateWithFormat:@"readyToDelete = NO"];
        NSPredicate *compundPredicate = [NSCompoundPredicate andPredicateWithSubpredicates:@[predicate, deleteFilter]];
        NSArray *odds = [[CoreDataManager singleton] getAllEntities:[BetTypeOdd class] orderedByKey:@"idBetTypeOdd" ascending:YES withPredicate:compundPredicate];

        NSMutableDictionary *bet = [[NSMutableDictionary alloc] init];
        if ([mbt.betType.alwaysVisible boolValue] == YES || [odds count] > 0){
            [bet addEntriesFromDictionary:@{@"betType": mbt.betType,@"odds":odds}];
            [betsTypesArray addObject:bet];
        }
    }
    
    if ([betsTypesArray count] > 0) {
        //Sort the resulting betTypes by weight
        NSSortDescriptor *descriptor = [NSSortDescriptor sortDescriptorWithKey:@"betType.weight" ascending:YES];
        NSArray *sortedBets = [betsTypesArray sortedArrayUsingDescriptors:@[descriptor]];
        return sortedBets;
    }

    return nil;
}

//------------------------------------------------------------------------------
/**
 @brief Finds for any valid BetTypeOdd for a given match.
 
 @params match Match entity to retrieve if cuotas is available.
 
 @returns BOOL YES = At least one odd ready 
 @returns BOOL NO = No odds for this match
 */
//------------------------------------------------------------------------------
- (BOOL)isThereAnyOddAvailableForMatch:(Match *)match {
    
    NSSet *odds = match.matchBetType;
    for (MatchBetType *mbt in odds) {
        NSPredicate *predicate = [NSPredicate predicateWithFormat:[NSString stringWithFormat:@"matchBetType.idMatchBetType = %@", mbt.idMatchBetType]];
        NSArray *odds = [[CoreDataManager singleton] getAllEntities:[BetTypeOdd class] withPredicate:predicate];
        if ([odds count] > 0)
            return YES;
    }
    return NO;
}

//------------------------------------------------------------------------------
/**
 Deletes all cuotas in match every time we call the webservice and webservice response is OK.
 We don't update cuotas to ensure that when cuota is deleted from server we also delete it
 */
//------------------------------------------------------------------------------
- (void)deleteAllCuotasForMatch:(Match *)match {
    
    NSSet *odds = match.matchBetType;
    for (MatchBetType *mbt in odds) {
        NSArray *itemToDelete = [NSArray arrayWithObject:mbt];
        [[CoreDataManager singleton] deleteEntitiesIn:itemToDelete];
    }
    [[CoreDataManager singleton] saveContext];
}

//------------------------------------------------------------------------------
- (void)deleteAllCuotasInArray:(NSArray *)cuotasArray {
    
    if (cuotasArray && [cuotasArray count] > 0) {
        [[CoreDataManager singleton] deleteEntitiesIn:cuotasArray];
        [[CoreDataManager singleton] saveContext];
    }
}

//------------------------------------------------------------------------------
- (void)deleteOldCuotas {

    NSPredicate *matchPredicate = [NSPredicate predicateWithFormat:@"%K = 3",kJSON_MATCH_STATE];
    NSArray *matches = [[CoreDataManager singleton] getAllEntities:[Match class] withPredicate:matchPredicate];
    NSMutableArray *matchBetTypes = [[NSMutableArray alloc] initWithCapacity:[matches count]];
    for (Match *match in matches) {
        [matchBetTypes addObjectsFromArray:[match.matchBetType allObjects]];
    }
    if ([matchBetTypes count] > 0) {
        [[CoreDataManager singleton] deleteEntitiesIn:[matchBetTypes copy]];
        [[CoreDataManager singleton] saveContext];
    }
}

//------------------------------------------------------------------------------
- (void)markAllCuotasForMatchToDelete:(Match *)match {
    
    NSSet *odds = match.matchBetType;
    for (MatchBetType *mbt in odds) {
        for (BetTypeOdd *odd in mbt.betTypeOdds) {
            [odd setReadyToDeleteValue:YES];
        }
    }
//    [[CoreDataManager singleton] saveContext];
}

#pragma mark - Singleton overwritten methods

//------------------------------------------------------------------------------
- (id)init {
	self = [super init];
	if (self != nil) {

	}
	return self;
}

//------------------------------------------------------------------------------
- (id)copyWithZone:(NSZone *)zone
{
    return self;
}

@end
