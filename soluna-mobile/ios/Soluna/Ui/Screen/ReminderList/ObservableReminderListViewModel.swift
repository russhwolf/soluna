import SwiftUI
import Shared

class ObservableReminderListViewModel : ObservableViewModel<ReminderListViewModel.State, ReminderListViewModel.Event, ReminderListViewModel.Action> {

    let goBack = BackNavigationTrigger()

    init() {
        super.init(SwiftKotlinBridge().getReminderListViewModel())
        _ = self.objectWillChange.append(super.objectWillChange)
    }
    
    override func onEvent(_ event: ReminderListViewModel.Event) {
        switch event {
        case is ReminderListViewModel.EventExit:
            goBack.navigate()
        default:
            NSLog("Received unknown event \(event)")
        }
    }

    override func reset() {
        goBack.reset()
    }
    
    func onAddReminderClick() {
        performAction(action: ReminderListViewModel.ActionAddReminder(type: ReminderType.sunset, minutesBefore: 15))
    }
    
    func onRemoveReminderClick(_ reminderId: Int64) {
        performAction(action: ReminderListViewModel.ActionRemoveReminder(id: reminderId))
    }
    
    func onUpdateReminder(_ reminderId: Int64, enabled: Bool) {
        performAction(action: ReminderListViewModel.ActionSetReminderEnabled(id: reminderId, enabled: enabled))
    }
    
    func onUpdateReminder(_ reminderId: Int64, minutesBefore: Int32) {
        performAction(action: ReminderListViewModel.ActionSetReminderMinutesBefore(id: reminderId, minutesBefore: minutesBefore))
    }
    
    func onUpdateReminder(_ reminderId: Int64, type: ReminderType) {
        performAction(action: ReminderListViewModel.ActionSetReminderType(id: reminderId, type: type))
    }
}
