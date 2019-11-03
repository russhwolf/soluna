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
    
    @IBOutlet var longitudeText: UILabel!
    @IBOutlet var latitudeText: UILabel!
    
    override func initViewModel() -> LocationDetailViewModel {
        SwiftKotlinBridgeKt.getLocationDetailViewModel(id: id)
    }

    override func onUpdateState(state: LocationDetailViewState) {
        super.onUpdateState(state: state)
        if let location = state.location {
            self.title = location.label
            self.longitudeText.text = String(location.longitude)
            self.latitudeText.text = String(location.latitude)
        }
        state.exitTrigger.consume { _ in
            self.navigationController?.popViewController(animated: true)
        }
    }

    @IBAction func onDeleteClick(_ sender: Any) {
        viewModel.delete()
    }
}
