//
//  AddLocationViewController.swift
//  Soluna
//
//  Created by Russell Wolf on 8/2/19.
//  Copyright © 2019 Kotlin/Native. All rights reserved.
//

import UIKit
import Shared

class AddLocationViewController: BaseViewController<AddLocationViewModel, AddLocationViewState> {
    let viewModelInit = SwiftKotlinBridgeKt.getAddLocationViewModel()

    override var viewModel: AddLocationViewModel! {
        get { return viewModelInit }
        set { super.viewModel = newValue }
    }

    @IBOutlet var labelInput: UITextField!
    @IBOutlet var latitudeInput: UITextField!
    @IBOutlet var longitudeInput: UITextField!
    @IBOutlet var timeZoneInput: UITextField!

    override func viewDidLoad() {
        super.viewDidLoad()
        viewModel.setViewStateListener { (state: AddLocationViewState) in
            state.geocodeTrigger.consume { geocodeResult in
                self.latitudeInput.text = String(geocodeResult.latitude)
                self.longitudeInput.text = String(geocodeResult.longitude)
                self.timeZoneInput.text = geocodeResult.timeZone
            }
            state.exitTrigger.consume { _ in
                self.navigationController?.popViewController(animated: true)
            }
        }
    }

    @IBAction func onSubmitClick(_ sender: Any) {
        viewModel.addLocation(
            label: labelInput.text ?? "",
            latitude: latitudeInput.text ?? "",
            longitude: longitudeInput.text ?? "",
            timeZone: timeZoneInput.text ?? ""
        )
    }

    @IBAction func onGeocodeClick(_ sender: Any) {
        let alert = UIAlertController(title: "Get location from address", message: nil, preferredStyle: .alert)

        var input: UITextField? = nil
        alert.addTextField { field in
            field.text = self.labelInput.text
            field.placeholder = "Address"
            input = field
        }
        alert.addAction(UIAlertAction(title: "Submit", style: .default) { _ in
            let location = input?.text ?? ""
            self.viewModel.geocodeLocation(location: location)
        })
        alert.addAction(UIAlertAction(title: "Cancel", style: .cancel))

        present(alert, animated: true)
    }
}
