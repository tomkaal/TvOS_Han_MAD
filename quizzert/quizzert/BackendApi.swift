//
//  BackendApi.swift
//  quizzert
//
//  Created by HAN IVS on 07-12-16.
//  Copyright © 2016 HAN IVS. All rights reserved.
//

import Foundation
import SwiftyJSON

class BackendApi {
    
    private struct Api {
        static let Url = "http://localhost:3000/api"
        static let beaconId = "243sad-343fg-t5t5r-3eg6"
    }
    
    func getGroups() -> [Group] {
        var groups = [Group]()
        if let url = URL(string: Api.Url + "/group") {
            if let data = try? Data(contentsOf: url) {
                let json = JSON(data: data)
                
                let jsonGroups = json["doc"].arrayValue
                
                for group in jsonGroups {
                    groups.append(
                        Group(_id: group["_id"].stringValue,
                              name: group["name"].stringValue,
                              owner: User(_id: group["owner"].stringValue)
                        )
                    )
                }
            }
        }
        
        return groups
    }
    
    func getQuestion() -> Question? {
        
        return nil
    }
    
    func getTvDescription() -> String? {
        var description: String? = nil
        
        if let url = URL(string: Api.Url + "/tv/" + Api.beaconId) {
            if let data = try? Data(contentsOf: url) {
                let json = JSON(data: data)
                
                description = json["doc"].arrayValue[0]["description"].stringValue
            }
        }
        
        return description
    }
    
}
