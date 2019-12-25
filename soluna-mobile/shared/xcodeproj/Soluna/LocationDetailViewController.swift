//
//  LocationDetailViewController.swift
//  Soluna
//
//  Created by Russell Wolf on 10/14/19.
//  Copyright © 2019 Kotlin/Native. All rights reserved.
//

import UIKit
import Shared

class LocationDetailViewController: BaseViewController<LocationDetailViewModel, LocationDetailViewState>, UITableViewDelegate, UITableViewDataSource {

    var id: Int64 = -1

    @IBOutlet var longitudeText: UILabel!
    @IBOutlet var latitudeText: UILabel!
    @IBOutlet var titleView: UITextField!

    private var reminders: [Reminder] = []
    @IBOutlet var reminderList: UITableView!

    override func initViewModel() -> LocationDetailViewModel {
        SwiftKotlinBridgeKt.getLocationDetailViewModel(id: id)
    }

    override func onUpdateState(state: LocationDetailViewState) {
        super.onUpdateState(state: state)
        if let location = state.location {
            self.titleView.text = location.label
            self.longitudeText.text = String(location.longitude)
            self.latitudeText.text = String(location.latitude)
        }
        self.setReminders(reminders: state.reminders)
        state.exitTrigger.consume { _ in
            self.navigationController?.popViewController(animated: true)
        }
    }

    @IBAction func onDeleteClick(_ sender: Any) {
        viewModel.delete()
    }

    @IBAction func onTitleEditingChanged(_ sender: Any) {
        if let text = titleView.text {
            viewModel.setLabel(label: text)
        }
    }

    @IBAction func onAddReminderClick(_ sender: Any) {
        let alert = UIAlertController(title: "Add Reminder", message: nil, preferredStyle: .alert)

        var minutesInput: UITextField? = nil
        alert.addTextField { field in
            field.placeholder = "Minutes Before"
            field.keyboardType = UIKeyboardType.numberPad
            minutesInput = field
        }
        let reminderTypes: [ReminderType] = ReminderType.Companion().values

        reminderTypes.forEach { (type: ReminderType) in
            alert.addAction(.init(title: type.uiString(), style: .default) { action in
                self.handleAddReminder(type: type, minutesInput: minutesInput)
            })
        }

        alert.addAction(UIAlertAction(title: "Cancel", style: .cancel))

        present(alert, animated: true)
    }

    private func handleAddReminder(type: ReminderType, minutesInput: UITextField?) {
        let minutes = Int32(minutesInput?.text ?? "0") ?? 0
        viewModel.addReminder(type: type, minutesBefore: minutes)
    }

    private func setReminders(reminders: [Reminder]) {
        self.reminders = reminders
        reminderList.reloadData()
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        reminders.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "ReminderListItem", for: indexPath) as! ReminderListItem
        let index = indexPath.row
        cell.viewModel = viewModel
        cell.reminder = reminders[index]
        return cell
    }

    func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCell.EditingStyle, forRowAt indexPath: IndexPath) {
        if (editingStyle == .delete) {
            let index = indexPath.row
            viewModel.deleteReminder(reminderId: reminders[index].id)
        }
    }
}

class ReminderListItem: UITableViewCell {
    private var _reminder: Reminder? = nil
    var reminder: Reminder? {
        get {
            self._reminder
        }
        set {
            self.typeLabel.text = newValue?.type.uiString()
            self.minutesLabel.text = "\(newValue?.minutesBefore ?? 0) minutes before"
            self._reminder = newValue
        }
    }

    var viewModel: LocationDetailViewModel? = nil

    @IBOutlet var typeLabel: UILabel!
    @IBOutlet var minutesLabel: UILabel!

    @IBAction func onEnableSwitchChanged(_ sender: UISwitch) {
        if let viewModel = self.viewModel, let reminder = self.reminder {
            viewModel.setReminderEnabled(reminderId: reminder.id, enabled: sender.isOn)
        }
    }

}

extension ReminderType {
    func uiString() -> String {
        switch (self) {
        case .sunrise:
            return "Sunrise"
        case .sunset:
            return "Sunset"
        case .moonrise:
            return "Moonrise"
        case .moonset:
            return "Moonset"
        default:
            return "???"
        }
    }
}
