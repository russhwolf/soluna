//
//  LocationListViewController.swift
//  Soluna
//
//  Created by Russell Wolf on 8/1/19.
//  Copyright © 2019 Kotlin/Native. All rights reserved.
//

import UIKit
import Shared

class LocationListViewController: UIViewController, UINavigationControllerDelegate, UITableViewDelegate, UITableViewDataSource {
    
    let viewModel = LocationListViewModel(repository: SwiftKotlinBridgeKt.repository, dispatcher: MainThreadKt.mainDispatcher)

    private var items: Array<LocationSummary> = []
    
    @IBOutlet var locationList: UITableView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        locationList.register(UINib(nibName: "LocationListItem", bundle: nil), forCellReuseIdentifier: "LocationListItem")

        viewModel.setViewStateListener { (state: LocationListViewState) in
            self.setItems(items: state.locations)
        }
    }
    
    private func setItems(items: [LocationSummary]) {
        self.items = items
        self.locationList.reloadData()
    }
    
    @IBAction func onAddLocationClick(_ sender: Any) {
        let addLocationViewController = self.storyboard?.instantiateViewController(withIdentifier: "AddLocation")
        navigationController?.pushViewController(addLocationViewController!, animated: true)
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
        viewModel.removeLocation(id: items[index].id)
    }
}

class LocationListItem: UITableViewCell {
    @IBOutlet var label: UILabel!
}
