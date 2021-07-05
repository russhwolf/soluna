import SwiftUI
import Shared

class ObservableHomeViewModel: ObservableViewModel<HomeViewModel.State, HomeViewModel.Event, HomeViewModel.Action> {

    @Published
    var navigateToLocationList: Bool = false

    @Published
    var navigateToReminderList: Bool = false

    init() {
        super.init(SwiftKotlinBridge().getHomeViewModel())
        _ = self.objectWillChange.append(super.objectWillChange)
    }
    
    override func onEvent(_ event: HomeViewModel.Event) {
        switch event {
        case is HomeViewModel.EventLocations:
            navigateToLocationList = true
        case is HomeViewModel.EventReminders:
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
        performAction(action: HomeViewModel.ActionLocations())
    }
    
    func onNavigateToReminderList() {
        performAction(action: HomeViewModel.ActionReminders())
    }
}
