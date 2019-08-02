//
//  ViewController.swift
//  Soluna
//
//  Created by Russell Wolf on 8/1/19.
//  Copyright © 2019 Kotlin/Native. All rights reserved.
//

import UIKit
import Shared

class ViewController: UIViewController {
    
    let viewModel = LocationListViewModel(repository: SwiftKotlinBridgeKt.repository)

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        
        viewModel.setViewStateListener { (state: LocationListViewState) in
            let locations = state.locations
            NSLog(String(locations.count))
        }
    }
}

