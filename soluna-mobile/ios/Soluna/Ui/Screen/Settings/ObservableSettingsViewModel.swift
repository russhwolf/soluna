import SwiftUI
import Shared

class ObservableSettingsViewModel : ObservableViewModel<SettingsViewModel.State, SettingsViewModel.Event, SettingsViewModel.Action> {

    let locationListTrigger = NavigationTrigger<Bool>()
    let reminderListTrigger = NavigationTrigger<Bool>()

    init() {
        super.init(SwiftKotlinBridge().getSettingsViewModel())
        _ = self.objectWillChange.append(super.objectWillChange)
    }
    
    override func onEvent(_ event: SettingsViewModel.Event) {
        switch event {
        case SettingsViewModel.Event.locations:
            locationListTrigger.navigate()
        case SettingsViewModel.Event.reminders:
            reminderListTrigger.navigate()
        default:
            NSLog("Received unknown event \(event)")
        }
    }
    
    override func reset() {
        locationListTrigger.reset()
        reminderListTrigger.reset()
    }
    
    func onNavigateToLocationList() {
        performAction(action: SettingsViewModel.Action.locations)
    }
    
    func onNavigateToReminderList() {
        performAction(action: SettingsViewModel.Action.reminders)
    }
}
