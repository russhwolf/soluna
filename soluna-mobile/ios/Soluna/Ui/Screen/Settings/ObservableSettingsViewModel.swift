import SwiftUI
import Shared

class ObservableSettingsViewModel : ObservableViewModel<SettingsViewModel.State, SettingsViewModel.Event, SettingsViewModel.Action> {

    @Published
    var navigateToLocationList: Bool = false

    @Published
    var navigateToReminderList: Bool = false

    init() {
        super.init(SwiftKotlinBridge().getSettingsViewModel())
        _ = self.objectWillChange.append(super.objectWillChange)
    }
    
    override func onEvent(_ event: SettingsViewModel.Event) {
        switch event {
        case SettingsViewModel.Event.locations:
            navigateToLocationList = true
        case SettingsViewModel.Event.reminders:
            navigateToReminderList = true
        default:
            NSLog("Received unknown event \(event)")
        }
    }
    
    override func reset() {
        navigateToLocationList = false
        navigateToReminderList = false
    }
    
    func onNavigateToLocationList() {
        performAction(action: SettingsViewModel.Action.locations)
    }
    
    func onNavigateToReminderList() {
        performAction(action: SettingsViewModel.Action.reminders)
    }
}
