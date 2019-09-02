//
//  LocationListViewController.swift
//  Soluna
//
//  Created by Russell Wolf on 8/1/19.
//  Copyright © 2019 Kotlin/Native. All rights reserved.
//

import UIKit
import Shared

class LocationListViewController: UITableViewController, UINavigationControllerDelegate {
    
    let viewModel = LocationListViewModel(repository: SwiftKotlinBridgeKt.repository, dispatcher: MainThreadKt.mainDispatcher)
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        
        viewModel.setViewStateListener { (state: LocationListViewState) in
            let locations = state.locations
            NSLog(String(locations.count))
        }
    }
    
    @IBAction func onAddLocationClick(_ sender: Any) {
        let addLocationViewController = self.storyboard?.instantiateViewController(withIdentifier: "AddLocation")
        navigationController?.pushViewController(addLocationViewController!, animated: true)
    }
}

