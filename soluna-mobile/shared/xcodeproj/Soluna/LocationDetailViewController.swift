//
//  LocationDetailViewController.swift
//  Soluna
//
//  Created by Russell Wolf on 10/14/19.
//  Copyright © 2019 Kotlin/Native. All rights reserved.
//

import UIKit
import Shared

class LocationDetailViewController: BaseViewController<LocationDetailViewModel, LocationDetailViewState> {

    var id: Int64 = -1
    
    @IBOutlet var labelLabel: UILabel!
    
    override func initViewModel() -> LocationDetailViewModel {
        return SwiftKotlinBridgeKt.getLocationDetailViewModel(id: id)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()

        viewModel.setViewStateListener { state in
            self.labelLabel.text = state.location?.label ?? "No Location for id \(self.id)!"
            
            state.exitTrigger.consume { _ in
                self.navigationController?.popViewController(animated: true)
            }
        }
    }
    
    override func viewWillAppear(_ animated: Bool) {
        viewModel.refresh()
    }

    @IBAction func onDeleteClick(_ sender: Any) {
        viewModel.delete()
    }
}
