//
//  SearchManager.swift
//  Shootr
//
//  Created by Christian Cabarrocas on 08/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

import UIKit

class SearchManager: NSObject {
   
    class var singleton : SearchManager {
        
    struct Static {
        static let instance : SearchManager = SearchManager()
        }
        
        return Static.instance
    }
    
    func searchPeopleInLocalDatabase () {
        
    }
}
