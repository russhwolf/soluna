import SwiftUI
import Shared

class ObservableHomeViewModel: ObservableViewModel<HomeViewModel.State, HomeViewModel.Event, HomeViewModel.Action> {

    let settingsTrigger = NavigationTrigger<Bool>()

    init() {
        super.init(SwiftKotlinBridge().getHomeViewModel())
        _ = self.objectWillChange.append(super.objectWillChange)
    }
    
    override func onEvent(_ event: HomeViewModel.Event) {
        switch event {
        case is HomeViewModel.EventSettings:
            settingsTrigger.navigate()
        default:
            NSLog("Received unknown event \(event)")
        }
    }
    
    override func reset() {
        settingsTrigger.reset()
    }
    
    func onNavigateToSettings() {
        performAction(action: HomeViewModel.ActionSettings())
    }
}
