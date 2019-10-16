//
//  LocationListViewController.swift
//  Soluna
//
//  Created by Russell Wolf on 8/1/19.
//  Copyright © 2019 Kotlin/Native. All rights reserved.
//

import UIKit
import Shared

class LocationListViewController: BaseViewController<LocationListViewModel, LocationListViewState>, UINavigationControllerDelegate, UITableViewDelegate, UITableViewDataSource {

    override func initViewModel() -> LocationListViewModel {
        return SwiftKotlinBridgeKt.getLocationListViewModel()
    }

    private var items: [LocationSummary] = []

    @IBOutlet var locationList: UITableView!
    
    var selectedId: Int64 = -1

    override func viewDidLoad() {
        super.viewDidLoad()

//        locationList.register(UINib(nibName: "LocationListItem", bundle: nil), forCellReuseIdentifier: "LocationListItem")

        viewModel.setViewStateListener { (state: LocationListViewState) in
            self.setItems(items: state.locations)
            state.addLocationTrigger.consume { unit in
                self.performSegue(withIdentifier: "AddLocation", sender: self)
            }
            state.locationDetailsTrigger.consume { id in
                self.selectedId = id.int64Value
                self.performSegue(withIdentifier: "LocationDetail", sender: self)
            }
        }
    }
    
    override func viewWillAppear(_ animated: Bool) {
        viewModel.refresh()
    }

    private func setItems(items: [LocationSummary]) {
        self.items = items
        self.locationList.reloadData()
    }

    @IBAction func onAddLocationClick(_ sender: Any) {
        viewModel.navigateToAddLocation()
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return items.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "LocationListItem", for: indexPath) as! LocationListItem
        let index = indexPath.row

        cell.label.text = items[index].label

        return cell
    }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let index = indexPath.row
        viewModel.navigateToLocationDetails(locationSummary: items[index])
    }
    
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        guard let locationDetailViewController = segue.destination as? LocationDetailViewController else {
            return
        }
        locationDetailViewController.id = selectedId
    }
}

class LocationListItem: UITableViewCell {
    @IBOutlet var label: UILabel!
}
